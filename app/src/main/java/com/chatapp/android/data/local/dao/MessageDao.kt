package com.chatapp.android.data.local.dao

import androidx.room.*
import com.chatapp.android.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Upsert
    suspend fun upsert(message: MessageEntity)

    @Upsert
    suspend fun upsertAll(messages: List<MessageEntity>)

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY seqNumber ASC, timestamp ASC, id ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY seqNumber ASC, timestamp ASC, id ASC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesPaged(chatId: String, limit: Int, offset: Int): List<MessageEntity>

    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId")
    suspend fun getCountForChat(chatId: String): Int

    @Query("UPDATE messages SET status = 'READ' WHERE chatId = :chatId AND isMine = 0")
    suspend fun markAllRead(chatId: String)

    @Query("UPDATE messages SET status = :status WHERE id = :messageId")
    suspend fun updateStatus(messageId: String, status: String)
}
