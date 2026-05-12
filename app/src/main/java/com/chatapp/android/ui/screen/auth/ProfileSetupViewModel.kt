package com.chatapp.android.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.remote.api.UserApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

data class ProfileSetupUiState(val name: String = "", val isLoading: Boolean = false, val error: String? = null, val done: Boolean = false)

@HiltViewModel
class ProfileSetupViewModel @Inject constructor(private val userApi: UserApi) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) = _uiState.update { it.copy(name = name, error = null) }

    fun save() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            runCatching {
                val nameBody = _uiState.value.name.toRequestBody()
                userApi.updateProfile(nameBody, null)
            }
            .onSuccess { _uiState.update { it.copy(isLoading = false, done = true) } }
            .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
