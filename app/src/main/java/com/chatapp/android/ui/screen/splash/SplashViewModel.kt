package com.chatapp.android.ui.screen.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.android.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashState { object Loading : SplashState(); object LoggedIn : SplashState(); object LoggedOut : SplashState() }

@HiltViewModel
class SplashViewModel @Inject constructor(private val tokenManager: TokenManager) : ViewModel() {
    private val _uiState = MutableStateFlow<SplashState>(SplashState.Loading)
    val uiState: StateFlow<SplashState> = _uiState

    init {
        viewModelScope.launch {
            delay(1500) // show splash for 1.5 seconds
            _uiState.value = if (tokenManager.isLoggedIn()) SplashState.LoggedIn else SplashState.LoggedOut
        }
    }
}
