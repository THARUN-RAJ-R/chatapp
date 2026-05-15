package com.chatapp.android.service

import android.util.Log
import com.chatapp.android.data.local.dao.MessageDao
import com.chatapp.android.data.local.entity.MessageEntity
import com.chatapp.android.data.remote.api.ChatApi
import com.chatapp.android.data.remote.api.UserApi
import com.chatapp.android.data.remote.dto.FcmTokenRequest
import com.chatapp.android.util.TokenManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

@AndroidEntryPoint
class ChatFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        // One Mutex per chatId — prevents multiple concurrent syncs for the same chat
        private val syncLocks = ConcurrentHashMap<String, Mutex>()
    }

    @Inject lateinit var userApi: UserApi
    @Inject lateinit var chatApi: ChatApi
    @Inject lateinit var messageDao: MessageDao
    @Inject lateinit var tokenManager: TokenManager

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }

    /**
     * Called when FCM issues a new token (app reinstall, token refresh).
     * We send it to our backend so future pushes reach this device.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM token refreshed")
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { userApi.updateFcmToken(FcmTokenRequest(token)) }
                .onFailure { Log.e(TAG, "Failed to update FCM token: ${it.message}") }
        }
    }

    /**
     * Called when a push notification arrives (foreground or background).
     *
     * Tickle-to-Sync flow:
     *  1. Backend sends {"action": "SYNC_REQUIRED", "chatId": "..."} — no message text.
     *  2. We acquire a per-chatId mutex so only ONE sync runs at a time per chat.
     *  3. We call the REST API (paginated) to fetch all messages for that chat.
     *  4. We count only messages newer than the last sync timestamp as "new".
     *  5. We save all to Room and show a BigTextStyle notification with real content.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        Log.d(TAG, "FCM received: $data")

        val action = data["action"]

        if (action == "SYNC_REQUIRED") {
            // ── Tickle-to-Sync (direct chat) ──────────────────────────────
            val chatId = data["chatId"] ?: return
            val mutex = syncLocks.getOrPut(chatId) { Mutex() }

            CoroutineScope(Dispatchers.IO).launch {
                // If a sync is already running for this chat, skip this tickle entirely
                if (!mutex.tryLock()) {
                    Log.d(TAG, "Sync already running for chat $chatId — skipping duplicate tickle")
                    return@launch
                }
                try {
                    runCatching {
                        // WhatsApp-Style Sync: Only fetch messages newer than the last seqNumber we saw
                        val prefs = getSharedPreferences("sync_prefs", android.content.Context.MODE_PRIVATE)
                        val lastSeq = prefs.getLong("last_seq_$chatId", 0L)

                        val PAGE_SIZE = 200
                        var currentPage = 0
                        var totalPages = 1          // start at 1 so loop runs at least once
                        val allEntities = mutableListOf<MessageEntity>()
                        // Top 10 newest messages for notification (page 0 is DESC = newest first)
                        var newestMessages: List<MessageEntity> = emptyList()

                        // ── Paginated sync loop ───────────────────────────
                        while (currentPage < totalPages) {
                            val response = chatApi.getMessages(chatId, page = currentPage, size = PAGE_SIZE, afterSeq = if (lastSeq > 0) lastSeq else null)
                            if (!response.isSuccessful) break

                            val body = response.body()?.data ?: break

                            totalPages = (body["totalPages"] as? Double)?.toInt() ?: 1

                            @Suppress("UNCHECKED_CAST")
                            val contentList = body["content"] as? List<Map<String, Any>> ?: break

                            val pageEntities = contentList.mapNotNull { m ->
                                val msgId     = m["messageId"] as? String ?: return@mapNotNull null
                                val senderId  = m["senderId"]  as? String ?: return@mapNotNull null
                                val chatIdStr = m["chatId"]    as? String ?: return@mapNotNull null
                                val seqNumNum = m["seqNumber"] as? Number
                                val seqNum    = seqNumNum?.toLong() ?: 0L
                                
                                MessageEntity(
                                    id         = msgId,
                                    chatId     = chatIdStr,
                                    senderId   = senderId,
                                    senderName = m["senderName"] as? String,
                                    content    = m["content"]   as? String,
                                    type       = m["type"]      as? String ?: "TEXT",
                                    mediaUrl   = m["mediaUrl"]  as? String,
                                    status     = m["status"]    as? String ?: "SENT",
                                    timestamp  = (m["timestamp"] as? String)?.let {
                                        runCatching {
                                            java.time.LocalDateTime.parse(it)
                                                .toInstant(java.time.ZoneOffset.UTC)
                                                .toEpochMilli()
                                        }.getOrNull()
                                    } ?: System.currentTimeMillis(),
                                    isMine = senderId == tokenManager.userId,
                                    seqNumber = seqNum
                                )
                            }

                            // Backend returns DESC (newest first) — reverse to ASC for Room
                            val orderedPage = pageEntities.reversed()
                            allEntities.addAll(orderedPage)

                            // Capture the 10 newest messages from page 0 for notification lines
                            if (currentPage == 0) {
                                newestMessages = pageEntities.take(10).reversed() // oldest→newest
                            }

                            if (orderedPage.isNotEmpty()) messageDao.upsertAll(orderedPage)

                            Log.d(TAG, "Tickle sync: page $currentPage/${totalPages - 1} -> ${pageEntities.size} messages saved")
                            currentPage++
                        }

                        if (allEntities.isEmpty()) return@runCatching

                        // WhatsApp-style count: since we only fetched new messages, the total fetched is the exact new count
                        val newCount = allEntities.size

                        // Update the last seen seqNumber
                        val maxSeqFetched = allEntities.maxOfOrNull { it.seqNumber } ?: lastSeq
                        if (maxSeqFetched > lastSeq) {
                            prefs.edit().putLong("last_seq_$chatId", maxSeqFetched).apply()
                        }

                        Log.d(TAG, "Tickle sync COMPLETE: $newCount new messages (${allEntities.size} total) for chat $chatId")

                        if (newCount == 0) return@runCatching  // nothing new to notify

                        // Build notification with the 10 newest message texts
                        val senderName = newestMessages.lastOrNull()?.senderName
                            ?: allEntities.lastOrNull()?.senderName
                            ?: "New Message"
                        val lines = newestMessages.map { it.content ?: "Photo" }

                        NotificationHelper.showInboxNotification(
                            context    = this@ChatFirebaseMessagingService,
                            chatId     = chatId,
                            senderName = senderName,
                            lines      = lines,
                            totalCount = newCount
                        )
                    }.onFailure {
                        Log.e(TAG, "Tickle sync failed for chat $chatId: ${it.message}")
                    }
                } finally {
                    mutex.unlock()  // always release the lock
                }
            }
        } else {
            // ── Legacy full-payload (group chat) ──────────────────────────
            val chatId     = data["chatId"]         ?: return
            val senderName = data["senderName"]     ?: "Unknown"
            val preview    = data["messagePreview"] ?: ""
            val isGroup    = data["isGroup"]        == "true"
            val groupName  = data["groupName"]

            NotificationHelper.showMessageNotification(
                context    = this,
                chatId     = chatId,
                senderName = senderName,
                preview    = preview,
                isGroup    = isGroup,
                groupName  = groupName
            )
        }
    }
}
