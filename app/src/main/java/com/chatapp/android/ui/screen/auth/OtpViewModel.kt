package com.chatapp.android.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.data.remote.api.AuthApi
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

data class OtpUiState(
    val otp: String        = "",
    val isLoading: Boolean = false,
    val error: String?     = null,
    val verified: Boolean? = null  // null=pending, true=new, false=existing
)

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState.asStateFlow()

    private var phone = ""
    private var verificationId = ""
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun init(phoneNumber: String) {
        phone = phoneNumber
    }

    fun onOtpChanged(otp: String) {
        if (otp.length <= 6) _uiState.update { it.copy(otp = otp, error = null) }
        // Auto-verify when 6 digits entered
        if (otp.length == 6) verify()
    }

    fun verify() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // Sign in with Firebase using phone + OTP
                // For test numbers, this uses the pre-configured test OTP
                val credential = PhoneAuthProvider.getCredential(verificationId.ifBlank { phone }, _uiState.value.otp)
                val firebaseResult = firebaseAuth.signInWithCredential(credential).await()
                val idToken = firebaseResult.user?.getIdToken(false)?.await()?.token
                    ?: throw Exception("Could not get Firebase ID token")

                // Send ID token to our backend
                val response = authApi.verifyOtp(VerifyOtpRequest(phone, idToken))
                if (response.isSuccessful && response.body()?.success == true) {
                    val authData = response.body()!!.data!!
                    tokenManager.accessToken  = authData.accessToken
                    tokenManager.refreshToken = authData.refreshToken
                    tokenManager.userId       = authData.user.id
                    tokenManager.userPhone    = authData.user.phone
                    _uiState.update { it.copy(isLoading = false, verified = authData.isNewUser) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Verification failed") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Verification failed") }
            }
        }
    }

    fun setVerificationId(id: String) { verificationId = id }

    fun onNavigated() = _uiState.update { it.copy(verified = null) }
}
