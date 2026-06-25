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
     * Send a full text message notification to a single device (direct chat).
     */
    public void sendTextMessageNotification(String fcmToken, UUID chatId,
                                            String senderName, String messagePreview,
                                            UUID messageId, UUID senderId, Long seqNumber,
                                            java.time.LocalDateTime createdAt) {
        if (fcmToken == null || fcmToken.isBlank()) return;

        try {
            Message message = Message.builder()
                .setToken(fcmToken)
                .putData("type", "MESSAGE")
                .putData("chatId", chatId.toString())
                .putData("senderName", senderName)
                .putData("messagePreview", messagePreview)
                .putData("isGroup", "false")
                .putData("messageId", messageId.toString())
                .putData("senderId", senderId.toString())
                .putData("seqNumber", seqNumber != null ? seqNumber.toString() : "0")
                .putData("timestamp", createdAt != null ? createdAt.toString() : java.time.LocalDateTime.now().toString())
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .build())
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("FCM text message notification sent for chat {}: {}", chatId, response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM text message notification failed for chat {}", chatId, e);
            log.error("FCM Error - Code: {}, Message: {}, Cause: {}", 
                e.getCode(), e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "N/A");
        }
    }

    /**
     * Send push notification to multiple users (group chat).
     * Group chat still uses the full payload for now.
     * Uses batch send for efficiency.
     */
    public void sendGroupMessageNotification(List<String> fcmTokens, UUID chatId,
                                              String groupName, String senderName,
                                              String messagePreview, String messageType,
                                              UUID messageId, UUID senderId, Long seqNumber,
                                              java.time.LocalDateTime createdAt) {
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
                .putData("messageId", messageId.toString())
                .putData("senderId", senderId.toString())
                .putData("seqNumber", seqNumber != null ? seqNumber.toString() : "0")
                .putData("timestamp", createdAt != null ? createdAt.toString() : java.time.LocalDateTime.now().toString())
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
            log.error("FCM batch send failed for chat {}", chatId, e);
            log.error("FCM Error - Code: {}, Message: {}, Cause: {}", 
                e.getCode(), e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "N/A");
        }
    }
}
