package com.chatapp.backend.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

// Sent from client to mark messages as read
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReadReceiptPayload {
    private UUID chatId;
}
