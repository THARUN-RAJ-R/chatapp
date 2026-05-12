package com.chatapp.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey val id: String,
    val type: String,           // DIRECT or GROUP
    val name: String?,          // contact name (direct) or group name
    val avatarUrl: String?,
    val lastMessage: String?,
    val lastMessageTime: Long?,
    val unreadCount: Int = 0,
    val isGroup: Boolean = false
)
