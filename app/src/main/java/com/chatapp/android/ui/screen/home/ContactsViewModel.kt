package com.chatapp.android.ui.screen.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.remote.api.ChatApi
import com.chatapp.android.data.remote.api.UserApi
import com.chatapp.android.data.remote.dto.ContactSyncRequest
import com.chatapp.android.data.remote.dto.StartDirectChatRequest
import com.chatapp.android.data.remote.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userApi: UserApi,
    private val chatApi: ChatApi
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<UserDto>>(emptyList())
    val contacts: StateFlow<List<UserDto>> = _contacts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init { syncContacts() }

    private fun syncContacts() {
        _isLoading.value = true
        viewModelScope.launch {
            runCatching {
                // Read phone contacts from device
                val phones = readPhoneContacts(context)
                userApi.syncContacts(ContactSyncRequest(phones))
            }.onSuccess { response ->
                _contacts.value = response.body()?.data ?: emptyList()
            }.also { _isLoading.value = false }
        }
    }

    fun startChat(targetUserId: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            runCatching { chatApi.startDirectChat(StartDirectChatRequest(targetUserId)) }
                .onSuccess { response -> response.body()?.data?.let { onResult(it.id) } }
        }
    }

    private fun readPhoneContacts(context: Context): List<String> {
        val phones = mutableListOf<String>()
        val cursor = context.contentResolver.query(
            android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER),
            null, null, null
        )
        cursor?.use {
            while (it.moveToNext()) {
                val phone = it.getString(0)?.replace("[\\s\\-()]".toRegex(), "") ?: continue
                if (phone.startsWith("+")) phones.add(phone)
            }
        }
        return phones.distinct()
    }
}
