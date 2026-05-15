package com.chatapp.android.data.remote.dto

// ─── Auth ──────────────────────────────────────────────────────────────────
data class LoginRequest(val phone: String)

// ─── User ──────────────────────────────────────────────────────────────────
data class FcmTokenRequest(val token: String)

// ─── Chat ──────────────────────────────────────────────────────────────────
data class StartDirectChatRequest(val targetUserId: String)
data class SendMessageRequest(
    val chatId: String,
    val content: String?,
    val type: String,
    val mediaUrl: String? = null
)
data class CreateGroupRequest(
    val name: String,
    val memberIds: List<String>
)
data class AddMembersRequest(val userIds: List<String>)

// ─── Responses ─────────────────────────────────────────────────────────────
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)

data class UserDto(
    val id: String,
    val phone: String,
    val name: String?,
    val avatarUrl: String?,
    val isOnline: Boolean,
    val lastSeen: String?
)

data class ChatDto(
    val id: String,
    val type: String,
    val groupName: String?,
    val groupAvatar: String?,
    val createdBy: UserDto?
)

data class MessageDto(
    val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String?,
    val senderAvatar: String?,
    val content: String?,
    val type: String,
    val mediaUrl: String?,
    val status: String,
    val createdAt: String
)

data class MediaUploadResponse(val mediaUrl: String)

// ─── WebSocket STOMP Payloads ──────────────────────────────────────────────
data class WsMessagePayload(
    val messageId: String,
    val chatId: String,
    val senderId: String,
    val senderName: String?,
    val senderAvatar: String?,
    val content: String?,
    val type: String,
    val mediaUrl: String?,
    val status: String,
    val timestamp: String,
    val seqNumber: Long = 0L,
    val isGroup: Boolean,
    val groupName: String?
)

data class WsReadPayload(val chatId: String)
data class WsTypingPayload(val chatId: String, val isTyping: Boolean)
data class WsHeartbeat(val ping: Boolean = true)
