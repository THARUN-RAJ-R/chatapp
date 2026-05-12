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
     * Send push notification to a single user (1-to-1 chat).
     */
    public void sendMessageNotification(String fcmToken, UUID chatId,
                                         String senderName, String senderAvatar,
                                         String messagePreview, String messageType) {
        if (fcmToken == null || fcmToken.isBlank()) return;

        try {
            Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                    .setTitle(senderName)
                    .setBody(messageType.equals("IMAGE") ? "📷 Photo" : messagePreview)
                    .build())
                .putData("type", "MESSAGE")
                .putData("chatId", chatId.toString())
                .putData("senderName", senderName)
                .putData("senderAvatar", senderAvatar != null ? senderAvatar : "")
                .putData("messagePreview", messageType.equals("IMAGE") ? "📷 Photo" : messagePreview)
                .putData("isGroup", "false")
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                        .setChannelId("chat_messages")
                        .setSound("default")
                        .setClickAction("OPEN_CHAT")
                        .build())
                    .build())
                .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.debug("FCM sent to {}: {}", senderName, response);
        } catch (FirebaseMessagingException e) {
            log.error("FCM send failed for token {}: {}", fcmToken, e.getMessage());
        }
    }

    /**
     * Send push notification to multiple users (group chat).
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
                .setNotification(Notification.builder()
                    .setTitle(groupName)
                    .setBody(senderName + ": " + (messageType.equals("IMAGE") ? "📷 Photo" : messagePreview))
                    .build())
                .putData("type", "MESSAGE")
                .putData("chatId", chatId.toString())
                .putData("senderName", senderName)
                .putData("messagePreview", messageType.equals("IMAGE") ? "📷 Photo" : messagePreview)
                .putData("isGroup", "true")
                .putData("groupName", groupName)
                .setAndroidConfig(AndroidConfig.builder()
                    .setPriority(AndroidConfig.Priority.HIGH)
                    .setNotification(AndroidNotification.builder()
                        .setChannelId("chat_messages")
                        .setSound("default")
                        .setClickAction("OPEN_CHAT")
                        .build())
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
