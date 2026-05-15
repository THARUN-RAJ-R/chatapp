package com.chatapp.android.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.remote.api.ChatApi
import com.chatapp.android.data.remote.api.UserApi
import com.chatapp.android.data.remote.dto.StartDirectChatRequest
import com.chatapp.android.data.remote.dto.UserDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContactsUiState(
    val phone: String = "",
    val isSearching: Boolean = false,
    val foundUser: UserDto? = null,
    val notFound: Boolean = false,
    val error: String? = null,
    val isStartingChat: Boolean = false
)

@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val userApi: UserApi,
    private val chatApi: ChatApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, foundUser = null, notFound = false, error = null) }
    }

    fun searchByPhone() {
        val phone = _uiState.value.phone.replace(" ", "").trim()
        if (!phone.startsWith("+") || phone.length < 10) {
            _uiState.update { it.copy(error = "Enter a valid number with country code e.g. +919876543210") }
            return
        }
        _uiState.update { it.copy(isSearching = true, foundUser = null, notFound = false, error = null) }
        viewModelScope.launch {
            runCatching { userApi.findByPhone(phone) }
                .onSuccess { response ->
                    when {
                        response.isSuccessful && response.body()?.data != null ->
                            _uiState.update { it.copy(isSearching = false, foundUser = response.body()!!.data) }
                        response.code() == 404 ->
                            _uiState.update { it.copy(isSearching = false, notFound = true) }
                        else ->
                            _uiState.update { it.copy(isSearching = false, error = "Search failed") }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSearching = false, error = "Cannot reach server: ${e.message}") }
                }
        }
    }

    fun startChat(onResult: (String) -> Unit) {
        val targetUserId = _uiState.value.foundUser?.id ?: return
        _uiState.update { it.copy(isStartingChat = true) }
        viewModelScope.launch {
            runCatching { chatApi.startDirectChat(StartDirectChatRequest(targetUserId)) }
                .onSuccess { response ->
                    _uiState.update { it.copy(isStartingChat = false) }
                    response.body()?.data?.let { onResult(it.id) }
                }
                .onFailure {
                    _uiState.update { it.copy(isStartingChat = false, error = "Could not start chat") }
                }
        }
    }
}
