package com.chatapp.android.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val phone: String,
    val name: String?,
    val avatarUrl: String?,
    val isOnline: Boolean = false,
    val lastSeen: Long?   = null
)
