package com.chatapp.android.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.data.local.entity.MessageEntity
import com.chatapp.android.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    onBack: () -> Unit,
    onInfoClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState(initial = emptyList())
    val uiState  by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(chatId)        { viewModel.init(chatId) }
    LaunchedEffect(messages.size) { if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = MaterialTheme.colorScheme.onSurface) }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(38.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
                            Text(uiState.chatName.firstOrNull()?.uppercaseChar()?.toString() ?: "?", color = ChatGreen, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(uiState.chatName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                },
                actions = { IconButton(onClick = onInfoClick) { Icon(Icons.Default.Info, "Info") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            MessageInput(
                text      = uiState.inputText,
                onChanged = viewModel::onInputChanged,
                onSend    = viewModel::sendMessage,
                onImage   = { /* TODO: image picker */ }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            state    = listState,
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { message -> MessageBubble(message) }
        }
    }
}

@Composable
fun MessageBubble(message: MessageEntity) {
    val isMine = message.isMine
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (isMine) 16.dp else 4.dp,
                    bottomEnd = if (isMine) 4.dp else 16.dp
                ))
                .background(if (isMine) SentBubble else ReceivedBubble)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column {
                if (!isMine && message.senderName != null) {
                    Text(message.senderName, style = MaterialTheme.typography.labelSmall, color = ChatGreen, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                }
                Text(message.content ?: "", style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                    Text(formatTimestamp(message.timestamp), style = MaterialTheme.typography.labelSmall, color = TextTimestamp)
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(Modifier.padding(start = 8.dp, top = 4.dp)) {
        Box(Modifier.clip(RoundedCornerShape(12.dp)).background(ReceivedBubble).padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text("• • •", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun MessageInput(text: String, onChanged: (String) -> Unit, onSend: () -> Unit, onImage: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onImage) { Icon(Icons.Default.AttachFile, "Attach", tint = MaterialTheme.colorScheme.onSurfaceVariant) }
        OutlinedTextField(
            value         = text,
            onValueChange = onChanged,
            placeholder   = { Text("Message") },
            modifier      = Modifier.weight(1f),
            maxLines      = 4,
            shape         = RoundedCornerShape(24.dp),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = ChatGreen,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
        FloatingActionButton(
            onClick          = onSend,
            modifier         = Modifier.size(48.dp),
            containerColor   = ChatGreen,
            shape            = CircleShape
        ) { Icon(Icons.Default.Send, "Send", tint = MaterialTheme.colorScheme.onPrimary) }
    }
}

private fun formatTimestamp(millis: Long): String {
    val date = java.util.Date(millis)
    val fmt  = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return fmt.format(date)
}
