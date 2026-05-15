package com.chatapp.android.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.chatapp.android.ui.theme.ChatGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    onChatStarted: (String) -> Unit,
    viewModel: ContactsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Find Someone") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                "Enter a phone number to find someone on the app",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ─── Search Field ────────────────────────────────────────────────
            OutlinedTextField(
                value         = uiState.phone,
                onValueChange = viewModel::onPhoneChanged,
                label         = { Text("Phone number") },
                placeholder   = { Text("+91 9876543210") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction    = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(onSearch = { viewModel.searchByPhone() }),
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                trailingIcon  = {
                    IconButton(onClick = viewModel::searchByPhone, enabled = !uiState.isSearching) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ChatGreen,
                    focusedLabelColor  = ChatGreen
                )
            )

            Button(
                onClick  = viewModel::searchByPhone,
                enabled  = !uiState.isSearching && uiState.phone.length >= 10,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape    = RoundedCornerShape(12.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = ChatGreen)
            ) {
                if (uiState.isSearching)
                    CircularProgressIndicator(Modifier.size(18.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else
                    Text("Search", fontWeight = FontWeight.SemiBold)
            }

            // ─── Error ───────────────────────────────────────────────────────
            AnimatedVisibility(visible = uiState.error != null, enter = fadeIn(), exit = fadeOut()) {
                uiState.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }

            // ─── Not Found ───────────────────────────────────────────────────
            AnimatedVisibility(visible = uiState.notFound, enter = fadeIn(), exit = fadeOut()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape    = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("😔", fontSize = 32.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "This person isn't on the app yet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ─── Found User ──────────────────────────────────────────────────
            AnimatedVisibility(visible = uiState.foundUser != null, enter = fadeIn(), exit = fadeOut()) {
                uiState.foundUser?.let { user ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Avatar placeholder
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(ChatGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (user.name?.firstOrNull() ?: user.phone.last()).uppercaseChar().toString(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    user.name ?: "No name set",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    user.phone,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = { viewModel.startChat { chatId -> onChatStarted(chatId) } },
                                enabled  = !uiState.isStartingChat,
                                shape    = RoundedCornerShape(10.dp),
                                colors   = ButtonDefaults.buttonColors(containerColor = ChatGreen)
                            ) {
                                if (uiState.isStartingChat)
                                    CircularProgressIndicator(Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                                else
                                    Text("Chat", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }
    }
}
