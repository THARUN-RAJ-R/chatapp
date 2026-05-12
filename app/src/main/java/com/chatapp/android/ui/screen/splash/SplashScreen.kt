package com.chatapp.android.ui.screen.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.ui.theme.ChatGreen

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToPhone: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Pulse animation
    val scale = rememberInfiniteTransition(label = "pulse")
    val scaleVal by scale.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "scale"
    )

    LaunchedEffect(uiState) {
        when (uiState) {
            SplashState.LoggedIn  -> onNavigateToHome()
            SplashState.LoggedOut -> onNavigateToPhone()
            SplashState.Loading   -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = "💬",
                fontSize = 72.sp,
                modifier = Modifier.scale(scaleVal)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text  = "ChatApp",
                style = MaterialTheme.typography.headlineLarge,
                color = ChatGreen,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text  = "Fast. Private. Yours.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
