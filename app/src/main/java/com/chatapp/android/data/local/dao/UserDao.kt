package com.chatapp.android.data.local.dao

import androidx.room.*
import com.chatapp.android.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getByPhone(phone: String): UserEntity?
}
