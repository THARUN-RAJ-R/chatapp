package com.chatapp.android.ui.screen.group

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.ui.theme.ChatGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onGroupCreated: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CreateGroupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(uiState.createdChatId) { uiState.createdChatId?.let { onGroupCreated(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                title = { Text("New Group", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Group name", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = uiState.groupName, onValueChange = viewModel::onNameChanged,
                placeholder = { Text("Enter group name") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ChatGreen, focusedLabelColor = ChatGreen)
            )

            uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = viewModel::create, enabled = !uiState.isLoading && uiState.groupName.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ChatGreen)
            ) {
                if (uiState.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Create Group", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
