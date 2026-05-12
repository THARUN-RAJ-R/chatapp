package com.chatapp.android.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.ui.theme.ChatGreen

@Composable
fun ProfileSetupScreen(onComplete: () -> Unit, viewModel: ProfileSetupViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.done) { if (uiState.done) onComplete() }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("👤", style = MaterialTheme.typography.headlineLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(56f, androidx.compose.ui.unit.TextUnitType.Sp)))
            Text("Set up your profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = uiState.name, onValueChange = viewModel::onNameChanged,
                label = { Text("Your name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChatGreen, focusedLabelColor = ChatGreen)
            )

            uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            Button(
                onClick = viewModel::save, enabled = !uiState.isLoading && uiState.name.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChatGreen)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Save & Continue", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
