package com.chatapp.backend.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FCMService {

    /**
     * Send a collapsed "Tickle" to a single offline device (direct chat).
     *
     * Instead of pushing the actual message text, we push a minimal
     * {"action": "SYNC_REQUIRED", "chatId": "..."} payload.
     * The collapse_key ensures Firebase replaces any previously queued tickle
     * for the same chat with this new one — so 500 messages in the same chat
     * result in exactly ONE queued payload, never overflowing the 100-message limit.
     *
     * When the device reconnects, it receives the single tickle and performs
     * a REST API sync to download all missing messages from the database.
     */
    public void sendSyncTickle(String fcmToken, UUID chatId) {
        if (fcmToken == null || fcmToken.isBlank()) return;

        try {
            Message message = Message.builder()
                .setToken(fcmToken)
                .putData("action", "SYNC_REQUIRED")
                .putData("chatId", chatId.toString())
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setCollapseKey(chatId.toString())   // collapse_key = chatId
                    .build())
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM sync tickle sent for chat {}: {}", chatId, response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM tickle send failed for chat {}: {}", chatId, e.getMessage());
        }
    }

    /**
     * Send push notification to multiple users (group chat).
     * Group chat still uses the full payload for now.
     * Uses batch send for efficiency.
     */
    public void sendGroupMessageNotification(List<String> fcmTokens, UUID chatId,
                                              String groupName, String senderName,
                                              String messagePreview, String messageType) {
        if (fcmTokens == null || fcmTokens.isEmpty()) return;

        List<Message> messages = new ArrayList<>();
        for (String token : fcmTokens) {
            if (token == null || token.isBlank()) continue;
            messages.add(Message.builder()
                .setToken(token)
                .putData("type", "MESSAGE")
                .putData("chatId", chatId.toString())
                .putData("senderName", senderName)
                .putData("messagePreview", messageType.equals("IMAGE") ? "📷 Photo" : messagePreview)
                .putData("isGroup", "true")
                .putData("groupName", groupName)
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .build())
                .build());
        }

        if (messages.isEmpty()) return;

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);
            log.info("Group FCM batch: {}/{} successful for chat {}",
                response.getSuccessCount(), messages.size(), chatId);
        } catch (FirebaseMessagingException e) {
            log.error("FCM batch send failed: {}", e.getMessage());
        }
    }
}
