package com.chatapp.backend.listener;

import com.chatapp.backend.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        UUID userId = extractUserId(accessor);
        if (userId != null) {
            presenceService.setOnline(userId);
            log.info("WebSocket CONNECTED: {}", userId);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        UUID userId = extractUserId(accessor);
        if (userId != null) {
            presenceService.setOffline(userId);
            log.info("WebSocket DISCONNECTED: {}", userId);
        }
    }

    private UUID extractUserId(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user != null && user.getName() != null) {
            try {
                return UUID.fromString(user.getName());
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }
}
