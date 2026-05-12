package com.chatapp.backend.listener;

import com.chatapp.backend.model.User;
import com.chatapp.backend.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final PresenceService presenceService;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = extractUser(accessor);
        if (user != null) {
            presenceService.setOnline(user.getId());
            log.info("WebSocket CONNECTED: {}", user.getPhone());
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        User user = extractUser(accessor);
        if (user != null) {
            presenceService.setOffline(user.getId());
            log.info("WebSocket DISCONNECTED: {}", user.getPhone());
        }
    }

    private User extractUser(StompHeaderAccessor accessor) {
        if (accessor.getUser() instanceof UsernamePasswordAuthenticationToken auth) {
            if (auth.getPrincipal() instanceof User user) {
                return user;
            }
        }
        return null;
    }
}
