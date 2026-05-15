package com.chatapp.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val senderName: String?,
    val content: String?,
    val type: String,        // TEXT or IMAGE
    val mediaUrl: String?,
    val status: String,      // SENT, DELIVERED, READ
    val timestamp: Long,
    val isMine: Boolean,     // true if current user sent this
    val seqNumber: Long = 0L // WhatsApp-style sequence number for perfect ordering
)
