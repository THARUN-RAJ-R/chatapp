package com.chatapp.android.di

import android.content.Context
import androidx.room.Room
import com.chatapp.android.data.local.AppDatabase
import com.chatapp.android.data.local.dao.ChatDao
import com.chatapp.android.data.local.dao.MessageDao
import com.chatapp.android.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "chatapp.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides fun provideUserDao(db: AppDatabase): UserDao       = db.userDao()
    @Provides fun provideChatDao(db: AppDatabase): ChatDao       = db.chatDao()
    @Provides fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()
}
