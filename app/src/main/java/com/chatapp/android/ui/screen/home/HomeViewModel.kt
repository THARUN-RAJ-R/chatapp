package com.chatapp.android.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.local.dao.ChatDao
import com.chatapp.android.data.local.entity.ChatEntity
import com.chatapp.android.data.remote.api.ChatApi
import com.chatapp.android.data.remote.api.UserApi
import com.chatapp.android.data.remote.dto.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatDao: ChatDao,
    private val chatApi: ChatApi,
    private val userApi: UserApi
) : ViewModel() {

    // Reactive flow from Room — updates automatically when new messages arrive
    val chats: Flow<List<ChatEntity>> = chatDao.getAllChats()

    init { 
        refreshChats() 
        syncFcmToken()
    }

    private fun syncFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                android.util.Log.d("FCM_TOKEN", "FCM Token fetched successfully: " + token)
                viewModelScope.launch {
                    runCatching { userApi.updateFcmToken(FcmTokenRequest(token)) }
                        .onSuccess { android.util.Log.d("FCM_TOKEN", "Token successfully sent to backend!") }
                        .onFailure { android.util.Log.e("FCM_TOKEN", "Failed to send token to backend: " + it.message) }
                }
            }
        }
    }

    private fun refreshChats() {
        viewModelScope.launch {
            runCatching { chatApi.getChats() }.onSuccess { response ->
                val dtos = response.body()?.data ?: return@onSuccess
                val entities = dtos.map { dto ->
                    // Preserve existing local data (lastMessage, unreadCount) — only update name/avatar
                    val existing = chatDao.getById(dto.id)
                    ChatEntity(
                        id              = dto.id,
                        type            = dto.type,
                        name            = dto.groupName ?: existing?.name ?: "Direct Chat",
                        avatarUrl       = dto.groupAvatar ?: existing?.avatarUrl,
                        lastMessage     = existing?.lastMessage,       // keep local value
                        lastMessageTime = existing?.lastMessageTime,   // keep local value
                        unreadCount     = existing?.unreadCount ?: 0,  // keep local value
                        isGroup         = dto.type == "GROUP"
                    )
                }
                chatDao.upsertAll(entities)
            }
        }
    }
}
