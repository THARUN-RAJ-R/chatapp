package com.chatapp.android.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.data.remote.dto.UserDto
import com.chatapp.android.ui.theme.ChatGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onChatStarted: (String) -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val contacts by viewModel.contacts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Contact", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = ChatGreen) }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding)) {
                items(contacts) { user ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            viewModel.startChat(user.id) { chatId -> onChatStarted(chatId) }
                        }.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(Modifier.size(48.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                            Text(user.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?", style = MaterialTheme.typography.titleMedium, color = ChatGreen, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(user.name ?: user.phone, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                            Text(user.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(start = 76.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                }
            }
        }
    }
}
