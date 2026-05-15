package com.chatapp.backend.service;

import com.chatapp.backend.dto.websocket.PresencePayload;
import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final String PRESENCE_KEY = "presence:";
    private static final Duration PRESENCE_TTL = Duration.ofSeconds(15);

    /** Called when user connects via WebSocket */
    public void setOnline(UUID userId) {
        redisTemplate.opsForValue().set(PRESENCE_KEY + userId, "ONLINE", PRESENCE_TTL);
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsOnline(true);
            userRepository.save(user);
            broadcastPresence(user, "ONLINE");
        });
        log.debug("User ONLINE: {}", userId);
    }

    /** Called every 30s heartbeat to refresh TTL */
    public void heartbeat(UUID userId) {
        redisTemplate.expire(PRESENCE_KEY + userId, PRESENCE_TTL);
    }

    /** Called when user disconnects */
    public void setOffline(UUID userId) {
        redisTemplate.delete(PRESENCE_KEY + userId);
        userRepository.findById(userId).ifPresent(user -> {
            user.setIsOnline(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            broadcastPresence(user, "OFFLINE");
        });
        log.debug("User OFFLINE: {}", userId);
    }

    /** Check if a user is currently online */
    public boolean isOnline(UUID userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PRESENCE_KEY + userId));
    }

    /** Broadcast presence update to user's own queue (Android subscribes to this) */
    private void broadcastPresence(User user, String status) {
        PresencePayload payload = PresencePayload.builder()
                .userId(user.getId())
                .status(status)
                .lastSeen(user.getLastSeen())
                .build();
        // Each user subscribes to /user/queue/presence on Android side
        messagingTemplate.convertAndSendToUser(
            user.getId().toString(),
            "/queue/presence",
            payload
        );
    }
}
