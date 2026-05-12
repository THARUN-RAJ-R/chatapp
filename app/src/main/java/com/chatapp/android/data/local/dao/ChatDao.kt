package com.chatapp.android.data.local.dao

import androidx.room.*
import com.chatapp.android.data.local.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Upsert
    suspend fun upsertAll(chats: List<ChatEntity>)

    @Upsert
    suspend fun upsert(chat: ChatEntity)

    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    fun getAllChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats WHERE id = :chatId")
    suspend fun getById(chatId: String): ChatEntity?

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun clearUnread(chatId: String)
}
