package com.chatapp.android.ui.screen.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.remote.api.ChatApi
import com.chatapp.android.data.remote.dto.CreateGroupRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CreateGroupUiState(val groupName: String = "", val isLoading: Boolean = false, val error: String? = null, val createdChatId: String? = null)

@HiltViewModel
class CreateGroupViewModel @Inject constructor(private val chatApi: ChatApi) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) = _uiState.update { it.copy(groupName = name, error = null) }

    fun create() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching { chatApi.createGroup(CreateGroupRequest(_uiState.value.groupName, emptyList())) }
                .onSuccess { response -> response.body()?.data?.let { chat -> _uiState.update { it.copy(isLoading = false, createdChatId = chat.id) } } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
