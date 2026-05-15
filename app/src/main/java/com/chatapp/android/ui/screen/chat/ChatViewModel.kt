package com.chatapp.android.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.local.dao.ChatDao
import com.chatapp.android.data.local.dao.MessageDao
import com.chatapp.android.data.local.entity.MessageEntity
import com.chatapp.android.data.remote.api.ChatApi
import com.chatapp.android.data.remote.dto.SendMessageRequest
import com.chatapp.android.data.remote.dto.WsHeartbeat
import com.chatapp.android.data.remote.dto.WsMessagePayload
import com.chatapp.android.data.remote.dto.WsReadPayload
import com.chatapp.android.data.remote.dto.WsTypingPayload
import com.chatapp.android.data.websocket.StompWebSocketClient
import com.chatapp.android.util.TokenManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val chatName: String       = "",
    val inputText: String      = "",
    val isOtherTyping: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val stompClient: StompWebSocketClient,
    private val tokenManager: TokenManager,
    private val chatApi: ChatApi
) : ViewModel() {

    private val gson = Gson()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var chatId = ""
    private val _messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val messages: StateFlow<List<MessageEntity>> = _messages.asStateFlow()

    // Guard: only call init once per chatId
    private var initializedChatId = ""
    private var typingTimeoutJob: Job? = null

    fun init(id: String) {
        if (id == initializedChatId) return  // Bug fix #1: prevent re-init on recompose
        initializedChatId = id
        chatId = id

        // 1. Load chat name from local DB
        viewModelScope.launch {
            chatDao.getById(chatId)?.let { chat ->
                _uiState.update { it.copy(chatName = chat.name ?: "Chat") }
            }
        }

        // 2. Observe local messages (Room flow)
        viewModelScope.launch {
            messageDao.getMessagesForChat(chatId).collect { msgs ->
                _messages.value = msgs
            }
        }

        // 3. Sync missed messages from backend (in background)
        viewModelScope.launch {
            runCatching {
                val response = chatApi.getMessages(chatId)
                if (response.isSuccessful) {
                    val body = response.body()?.data ?: return@runCatching
                    // Backend returns Page<MessagePayload> — content is a list
                    @Suppress("UNCHECKED_CAST")
                    val contentList = body["content"] as? List<Map<String, Any>> ?: return@runCatching
                    val entities = contentList.mapNotNull { m ->
                        val msgId     = m["messageId"] as? String ?: return@mapNotNull null
                        val senderId  = m["senderId"]  as? String ?: return@mapNotNull null
                        val chatIdStr = m["chatId"]    as? String ?: return@mapNotNull null
                        MessageEntity(
                            id         = msgId,
                            chatId     = chatIdStr,
                            senderId   = senderId,
                            senderName = m["senderName"] as? String,
                            content    = m["content"]   as? String,
                            type       = m["type"]      as? String ?: "TEXT",
                            mediaUrl   = m["mediaUrl"]  as? String,
                            status     = m["status"]    as? String ?: "SENT",
                            timestamp  = (m["timestamp"] as? String)?.let {
                                runCatching {
                                    java.time.LocalDateTime.parse(it).toInstant(java.time.ZoneOffset.UTC).toEpochMilli()
                                }.getOrNull()
                            } ?: System.currentTimeMillis(),
                            isMine     = senderId == tokenManager.userId
                        )
                    }
                    if (entities.isNotEmpty()) messageDao.upsertAll(entities)
                }
            }
        }

        // 4. Connect WebSocket and listen for messages
        viewModelScope.launch {
            if (!stompClient.isConnected) stompClient.connect()
            // Small delay to let CONNECT frame complete
            delay(500)
            stompClient.subscribe("/user/queue/messages")

            // Mark as read immediately
            messageDao.markAllRead(chatId)
            runCatching { stompClient.send("/app/chat.read", WsReadPayload(chatId)) }

            // Heartbeat loop — every 10s to keep server presence alive (TTL=15s)
            launch {
                while (true) {
                    delay(10_000)
                    if (stompClient.isConnected)
                        runCatching { stompClient.send("/app/heartbeat", WsHeartbeat()) }
                }
            }

            // Collect incoming STOMP frames
            stompClient.messageFlow.collect { frame ->
                when (frame.command) {
                    "MESSAGE" -> runCatching {
                        val payload = gson.fromJson(frame.body, WsMessagePayload::class.java)
                        if (payload.chatId == chatId) {
                            val entity = MessageEntity(
                                id         = payload.messageId,
                                chatId     = payload.chatId,
                                senderId   = payload.senderId,
                                senderName = payload.senderName,
                                content    = payload.content,
                                type       = payload.type,
                                mediaUrl   = payload.mediaUrl,
                                status     = payload.status,
                                timestamp  = System.currentTimeMillis(),
                                isMine     = payload.senderId == tokenManager.userId
                            )
                            messageDao.upsert(entity)

                            // Update chat list with last message
                            chatDao.getById(chatId)?.let { chat ->
                                chatDao.upsert(chat.copy(
                                    lastMessage     = payload.content ?: "📷 Photo",
                                    lastMessageTime = System.currentTimeMillis()
                                ))
                            }
                        }
                    }
                    "TYPING" -> runCatching {
                        _uiState.update { it.copy(isOtherTyping = true) }
                        // Bug fix #7: auto-clear typing after 3 seconds
                        typingTimeoutJob?.cancel()
                        typingTimeoutJob = launch {
                            delay(3_000)
                            _uiState.update { it.copy(isOtherTyping = false) }
                        }
                    }
                }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
        if (text.isNotBlank() && stompClient.isConnected) {
            runCatching { stompClient.send("/app/chat.typing", WsTypingPayload(chatId, true)) }
        }
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        _uiState.update { it.copy(inputText = "") }
        if (stompClient.isConnected) {
            stompClient.send("/app/chat.send", SendMessageRequest(chatId, text, "TEXT"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        typingTimeoutJob?.cancel()
    }
}
