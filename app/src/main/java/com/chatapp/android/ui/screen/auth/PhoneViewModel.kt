package com.chatapp.android.ui.screen.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.remote.api.AuthApi
import com.chatapp.android.data.remote.dto.SendOtpRequest
import com.chatapp.android.data.remote.dto.VerifyOtpRequest
import com.chatapp.android.util.TokenManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class PhoneUiState(
    val phone: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val navigateToOtp: String? = null
)

@HiltViewModel
class PhoneViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState: StateFlow<PhoneUiState> = _uiState.asStateFlow()

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, error = null) }
    }

    fun sendOtp() {
        val phone = _uiState.value.phone.trim()
        if (phone.isBlank()) {
            _uiState.update { it.copy(error = "Enter a valid phone number") }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            runCatching { authApi.sendOtp(SendOtpRequest(phone)) }
                .onSuccess { _uiState.update { it.copy(isLoading = false, navigateToOtp = phone) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun onNavigated() = _uiState.update { it.copy(navigateToOtp = null) }
}
