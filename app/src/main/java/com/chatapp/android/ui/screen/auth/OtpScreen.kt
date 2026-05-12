package com.chatapp.android.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.ui.theme.ChatGreen

@Composable
fun OtpScreen(
    phone: String,
    onVerified: (Boolean) -> Unit,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(phone) { viewModel.init(phone) }
    LaunchedEffect(uiState.verified) {
        uiState.verified?.let { isNew -> onVerified(isNew); viewModel.onNavigated() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("🔐", style = MaterialTheme.typography.headlineLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(56f, androidx.compose.ui.unit.TextUnitType.Sp)))
            Text("Verify your number", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Code sent to $phone", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)

            OutlinedTextField(
                value         = uiState.otp,
                onValueChange = viewModel::onOtpChanged,
                label         = { Text("6-digit code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChatGreen,
                    focusedLabelColor  = ChatGreen
                )
            )

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick  = viewModel::verify,
                enabled  = !uiState.isLoading && uiState.otp.length == 6,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ChatGreen)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Verify", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
