package com.chatapp.android.ui.screen.auth

import androidx.compose.animation.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.ui.theme.*

@Composable
fun PhoneScreen(
    onOtpSent: (String) -> Unit,
    viewModel: PhoneViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToOtp) {
        uiState.navigateToOtp?.let { phone -> onOtpSent(phone); viewModel.onNavigated() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("💬", style = MaterialTheme.typography.headlineLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(56f, androidx.compose.ui.unit.TextUnitType.Sp)))
            Text("Enter your phone number", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("We'll send you a verification code", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            OutlinedTextField(
                value         = uiState.phone,
                onValueChange = viewModel::onPhoneChanged,
                label         = { Text("Phone number") },
                placeholder   = { Text("+91 9876543210") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = ChatGreen,
                    focusedLabelColor    = ChatGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            uiState.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(
                onClick  = viewModel::sendOtp,
                enabled  = !uiState.isLoading && uiState.phone.length >= 10,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ChatGreen)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Continue", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
