package com.chatapp.backend.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PresencePayload {
    private UUID userId;
    private String status;       // ONLINE or OFFLINE
    private LocalDateTime lastSeen;
}
