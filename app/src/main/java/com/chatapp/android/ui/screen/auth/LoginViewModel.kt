package com.chatapp.android.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.remote.api.AuthApi
import com.chatapp.android.data.remote.dto.LoginRequest
import com.chatapp.android.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }

    fun login() {
        val phone = _uiState.value.phone.replace(" ", "").trim()
        if (!phone.startsWith("+") || phone.length < 10) {
            _uiState.update { it.copy(error = "Enter a valid phone number with country code e.g. +919876543210") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching { authApi.login(LoginRequest(phone)) }
                .onSuccess { response ->
                    if (response.isSuccessful && response.body()?.success == true) {
                        val user = response.body()!!.data!!
                        tokenManager.userId    = user.id
                        tokenManager.userPhone = user.phone
                        tokenManager.userName  = user.name
                        _uiState.update { it.copy(isLoading = false, loggedIn = true) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Login failed. Try again.") }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Cannot reach server: ${e.message}") }
                }
        }
    }

    fun onNavigated() = _uiState.update { it.copy(loggedIn = false) }
}
