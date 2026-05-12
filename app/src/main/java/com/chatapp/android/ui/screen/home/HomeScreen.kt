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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.data.local.entity.ChatEntity
import com.chatapp.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onChatClick: (String) -> Unit,
    onContactsClick: () -> Unit,
    onCreateGroupClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val chats by viewModel.chats.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ChatApp", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = ChatGreen) },
                actions = {
                    IconButton(onClick = onCreateGroupClick) { Icon(Icons.Default.Group, "New group", tint = MaterialTheme.colorScheme.onSurface) }
                    IconButton(onClick = onContactsClick)   { Icon(Icons.Default.PersonAdd, "Contacts", tint = MaterialTheme.colorScheme.onSurface) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onContactsClick, containerColor = ChatGreen) {
                Icon(Icons.Default.Edit, "New chat", tint = MaterialTheme.colorScheme.onPrimary)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (chats.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("💬", style = MaterialTheme.typography.headlineLarge.copy(fontSize = androidx.compose.ui.unit.TextUnit(56f, androidx.compose.ui.unit.TextUnitType.Sp)))
                    Text("No chats yet", style = MaterialTheme.typography.titleMedium)
                    Text("Tap the pencil to start a conversation", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(chats, key = { it.id }) { chat -> ChatListItem(chat = chat, onClick = { onChatClick(chat.id) }) }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: ChatEntity, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Box(Modifier.size(52.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Text(chat.name?.firstOrNull()?.uppercaseChar()?.toString() ?: "?", style = MaterialTheme.typography.titleLarge, color = ChatGreen, fontWeight = FontWeight.Bold)
        }

        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(chat.name ?: "Unknown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text(formatTime(chat.lastMessageTime), style = MaterialTheme.typography.labelSmall, color = if (chat.unreadCount > 0) ChatGreen else MaterialTheme.colorScheme.outline)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(chat.lastMessage ?: "No messages yet", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                if (chat.unreadCount > 0) {
                    Box(Modifier.size(20.dp).clip(CircleShape).background(ChatGreen), contentAlignment = Alignment.Center) {
                        Text(chat.unreadCount.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(start = 80.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
}

private fun formatTime(millis: Long?): String {
    millis ?: return ""
    val diff = System.currentTimeMillis() - millis
    return when {
        diff < 60_000 -> "now"
        diff < 3600_000 -> "${diff / 60_000}m"
        diff < 86400_000 -> "${diff / 3600_000}h"
        else -> "${diff / 86400_000}d"
    }
}
