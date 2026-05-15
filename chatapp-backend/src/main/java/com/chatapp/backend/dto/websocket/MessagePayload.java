package com.chatapp.backend.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagePayload {
    private UUID messageId;
    private UUID chatId;
    private UUID senderId;
    private String senderName;
    private String senderAvatar;
    private String content;
    private String type;       // TEXT or IMAGE
    private String mediaUrl;
    private String status;     // SENT, DELIVERED, READ
    private LocalDateTime timestamp;
    private Long seqNumber;    // WhatsApp-style sequence number for perfect ordering
    private boolean isGroup;
    private String groupName;
}
