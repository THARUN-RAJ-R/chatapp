package com.chatapp.android.service

import android.util.Log
import com.chatapp.android.data.remote.api.UserApi
import com.chatapp.android.data.remote.dto.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var userApi: UserApi

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
     * We show it as a notification — Android handles it when app is killed.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val data = message.data
        Log.d(TAG, "FCM received: $data")

        val chatId      = data["chatId"]        ?: return
        val senderName  = data["senderName"]    ?: "Unknown"
        val preview     = data["messagePreview"] ?: ""
        val isGroup     = data["isGroup"]       == "true"
        val groupName   = data["groupName"]

        NotificationHelper.showMessageNotification(
            context    = this,
            chatId     = chatId,
            senderName = senderName,
            preview    = preview,
            isGroup    = isGroup,
            groupName  = groupName
        )
    }

    companion object { private const val TAG = "FCMService" }
}
