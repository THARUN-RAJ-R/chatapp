package com.chatapp.android.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.local.dao.ChatDao
import com.chatapp.android.data.local.dao.MessageDao
import com.chatapp.android.data.local.entity.MessageEntity
import com.chatapp.android.data.remote.dto.SendMessageRequest
import com.chatapp.android.data.remote.dto.WsMessagePayload
import com.chatapp.android.data.remote.dto.WsReadPayload
import com.chatapp.android.data.remote.dto.WsTypingPayload
import com.chatapp.android.data.websocket.StompWebSocketClient
import com.chatapp.android.util.TokenManager
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatUiState(
    val chatName: String      = "",
    val inputText: String     = "",
    val isOtherOnline: Boolean = false,
    val isOtherTyping: Boolean = false
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val stompClient: StompWebSocketClient,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val gson = Gson()
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var chatId = ""
    lateinit var messages: Flow<List<MessageEntity>>

    fun init(id: String) {
        chatId = id
        messages = messageDao.getMessagesForChat(chatId)

        viewModelScope.launch {
            // Load chat name from Room
            chatDao.getById(chatId)?.let { chat ->
                _uiState.update { it.copy(chatName = chat.name ?: "Chat") }
            }

            // Subscribe to WebSocket for this chat
            if (!stompClient.isConnected) stompClient.connect()
            stompClient.subscribe("/user/queue/messages")
            stompClient.subscribe("/user/queue/presence")

            // Mark messages as read
            messageDao.markAllRead(chatId)
            stompClient.send("/app/chat.read", WsReadPayload(chatId))

            // Collect incoming STOMP frames
            stompClient.messageFlow.collect { frame ->
                if (frame.command == "MESSAGE") {
                    runCatching {
                        val payload = gson.fromJson(frame.body, WsMessagePayload::class.java)
                        if (payload.chatId == chatId) {
                            val entity = MessageEntity(
                                id          = payload.messageId,
                                chatId      = payload.chatId,
                                senderId    = payload.senderId,
                                senderName  = payload.senderName,
                                content     = payload.content,
                                type        = payload.type,
                                mediaUrl    = payload.mediaUrl,
                                status      = payload.status,
                                timestamp   = System.currentTimeMillis(),
                                isMine      = payload.senderId == tokenManager.userId
                            )
                            messageDao.upsert(entity)
                        }
                    }
                }
            }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
        // Send typing indicator
        if (text.isNotBlank()) stompClient.send("/app/chat.typing", WsTypingPayload(chatId, true))
    }

    fun sendMessage() {
        val text = _uiState.value.inputText.trim()
        if (text.isBlank()) return
        _uiState.update { it.copy(inputText = "") }
        stompClient.send("/app/chat.send", SendMessageRequest(chatId, text, "TEXT"))
    }
}
