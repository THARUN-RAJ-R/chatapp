package com.chatapp.android.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.chatapp.android.MainActivity

object NotificationHelper {

    const val CHANNEL_ID   = "chat_messages"
    const val CHANNEL_NAME = "Messages"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Chat message notifications"
                enableVibration(true)
                enableLights(true)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun showMessageNotification(
        context: Context,
        chatId: String,
        senderName: String,
        preview: String,
        isGroup: Boolean,
        groupName: String?
    ) {
        // Deep link intent: opens ChatScreen with correct chatId
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data   = android.net.Uri.parse("chatapp://chat/$chatId")
            flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, chatId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isGroup && groupName != null) groupName else senderName
        val body  = if (isGroup) "$senderName: $preview" else preview

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email) // replace with your own icon
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Use chatId hashCode as notification ID so messages from same chat group together
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(chatId.hashCode(), notification)
        }
    }
    /**
     * Show a BigTextStyle notification with up to 10 message lines.
     * BigTextStyle is fully supported on Samsung One UI and all Android versions.
     *
     * Expanded view:
     *   +910000000002
     *   [SENDER_2] Msg 241
     *   [SENDER_2] Msg 242
     *   ...
     *   [SENDER_2] Msg 250
     *   +240 more messages
     */
    fun showInboxNotification(
        context: Context,
        chatId: String,
        senderName: String,
        lines: List<String>,    // up to 10 message texts, oldest → newest
        totalCount: Int         // total new messages
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            data   = android.net.Uri.parse("chatapp://chat/$chatId")
            flags  = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context, chatId.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the big text body: each message on its own line
        val remaining = totalCount - lines.size
        val bigText = buildString {
            lines.forEach { appendLine(it) }
            if (remaining > 0) append("+$remaining more messages")
        }

        // Collapsed single-line preview = last message text
        val collapsedPreview = lines.lastOrNull()
            ?: "$totalCount new messages"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(senderName)
            .setContentText(collapsedPreview)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bigText)
                    .setBigContentTitle(senderName)
                    .setSummaryText("$totalCount new messages")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setNumber(totalCount)           // badge count on app icon
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(chatId.hashCode(), notification)
        }
    }
}
