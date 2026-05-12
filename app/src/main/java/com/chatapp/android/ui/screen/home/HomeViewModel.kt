package com.chatapp.android.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.local.dao.ChatDao
import com.chatapp.android.data.local.entity.ChatEntity
import com.chatapp.android.data.remote.api.ChatApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatDao: ChatDao,
    private val chatApi: ChatApi
) : ViewModel() {

    // Reactive flow from Room — updates automatically when new messages arrive
    val chats: Flow<List<ChatEntity>> = chatDao.getAllChats()

    init { refreshChats() }

    private fun refreshChats() {
        viewModelScope.launch {
            runCatching { chatApi.getChats() }.onSuccess { response ->
                val dtos = response.body()?.data ?: return@onSuccess
                val entities = dtos.map { dto ->
                    ChatEntity(
                        id              = dto.id,
                        type            = dto.type,
                        name            = dto.groupName ?: "Direct Chat",
                        avatarUrl       = dto.groupAvatar,
                        lastMessage     = null,
                        lastMessageTime = null,
                        unreadCount     = 0,
                        isGroup         = dto.type == "GROUP"
                    )
                }
                chatDao.upsertAll(entities)
            }
        }
    }
}
