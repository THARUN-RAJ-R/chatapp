package com.chatapp.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chatapp.android.data.local.dao.ChatDao
import com.chatapp.android.data.local.dao.MessageDao
import com.chatapp.android.data.local.dao.UserDao
import com.chatapp.android.data.local.entity.ChatEntity
import com.chatapp.android.data.local.entity.MessageEntity
import com.chatapp.android.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ChatEntity::class, MessageEntity::class],
    version  = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
