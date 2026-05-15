package com.chatapp.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chatapp.android.data.local.dao.ChatDao
import com.chatapp.android.data.local.dao.MessageDao
import com.chatapp.android.data.local.dao.UserDao
import com.chatapp.android.data.local.entity.ChatEntity
import com.chatapp.android.data.local.entity.MessageEntity
import com.chatapp.android.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ChatEntity::class, MessageEntity::class],
    version  = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao

    companion object {
        // Migration from v1 → v2: add seq_number column (default 0 for old rows)
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE messages ADD COLUMN seqNumber INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
