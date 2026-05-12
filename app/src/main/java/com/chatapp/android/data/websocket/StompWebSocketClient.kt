package com.chatapp.android.data.websocket

import android.util.Log
import com.chatapp.android.BuildConfig
import com.chatapp.android.util.TokenManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import okhttp3.*
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Custom lightweight STOMP client over OkHttp WebSocket.
 * Handles CONNECT, SUBSCRIBE, SEND, DISCONNECT frames manually.
 */
@Singleton
class StompWebSocketClient @Inject constructor(
    private val tokenManager: TokenManager,
    private val okHttpClient: OkHttpClient
) {
    private val gson = Gson()
    private var webSocket: WebSocket? = null
    private val subscriptions = mutableMapOf<String, String>() // id -> destination

    private val _messageFlow = MutableSharedFlow<StompFrame>(replay = 0, extraBufferCapacity = 64)
    val messageFlow: SharedFlow<StompFrame> = _messageFlow

    var isConnected = false
        private set

    fun connect() {
        val token = tokenManager.accessToken ?: return
        val request = Request.Builder().url(BuildConfig.WS_BASE_URL).build()
        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(ws: WebSocket, response: Response) {
                // Send STOMP CONNECT frame
                ws.send(buildConnectFrame(token))
                Log.d(TAG, "WebSocket opened, CONNECT sent")
            }

            override fun onMessage(ws: WebSocket, text: String) {
                val frame = parseFrame(text)
                Log.d(TAG, "Received: ${frame.command}")
                if (frame.command == "CONNECTED") {
                    isConnected = true
                    // Resubscribe if reconnecting
                    subscriptions.forEach { (id, dest) -> ws.send(buildSubscribeFrame(id, dest)) }
                }
                _messageFlow.tryEmit(frame)
            }

            override fun onFailure(ws: WebSocket, t: Throwable, response: Response?) {
                isConnected = false
                Log.e(TAG, "WebSocket failure: ${t.message}")
            }

            override fun onClosed(ws: WebSocket, code: Int, reason: String) {
                isConnected = false
                Log.d(TAG, "WebSocket closed: $reason")
            }
        })
    }

    fun subscribe(destination: String): String {
        val id = UUID.randomUUID().toString()
        subscriptions[id] = destination
        webSocket?.send(buildSubscribeFrame(id, destination))
        return id
    }

    fun send(destination: String, body: Any) {
        val json = gson.toJson(body)
        webSocket?.send(buildSendFrame(destination, json))
    }

    fun disconnect() {
        webSocket?.send("DISCONNECT\n\n\u0000")
        webSocket?.close(1000, "User logout")
        subscriptions.clear()
        isConnected = false
    }

    // ─── Frame Builders ──────────────────────────────────────────────────
    private fun buildConnectFrame(token: String) =
        "CONNECT\naccept-version:1.2\nheart-beat:10000,10000\nAuthorization:Bearer $token\n\n\u0000"

    private fun buildSubscribeFrame(id: String, destination: String) =
        "SUBSCRIBE\nid:$id\ndestination:$destination\n\n\u0000"

    private fun buildSendFrame(destination: String, body: String) =
        "SEND\ndestination:$destination\ncontent-type:application/json\n\n$body\u0000"

    // ─── Frame Parser ────────────────────────────────────────────────────
    private fun parseFrame(raw: String): StompFrame {
        val parts   = raw.trimEnd('\u0000').split("\n\n", limit = 2)
        val headers = parts[0].lines()
        val command = headers.firstOrNull() ?: ""
        val body    = if (parts.size > 1) parts[1] else ""
        val headerMap = headers.drop(1)
            .mapNotNull { line -> line.indexOf(':').takeIf { it > 0 }?.let { line.substring(0, it) to line.substring(it + 1) } }
            .toMap()
        return StompFrame(command, headerMap, body)
    }

    companion object { private const val TAG = "StompClient" }
}

data class StompFrame(
    val command: String,
    val headers: Map<String, String>,
    val body: String
)
