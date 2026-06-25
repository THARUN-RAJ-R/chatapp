package com.chatapp.backend.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TypingPayload {
    private UUID chatId;
    private UUID userId;
    private String userName;
    private boolean isTyping;
}
