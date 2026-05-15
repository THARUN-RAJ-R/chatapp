package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.SendMessageRequest;
import com.chatapp.backend.dto.websocket.*;
import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import com.chatapp.backend.service.ChatService;
import com.chatapp.backend.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final ChatService chatService;
    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    /** Resolve user from WS Principal — principal.getName() is the userId UUID string */
    private User resolveUser(Principal principal) {
        return userRepository.findById(UUID.fromString(principal.getName()))
                .orElseThrow(() -> new RuntimeException("WS user not found: " + principal.getName()));
    }

    /** Client sends a message → server delivers to recipient(s) */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        chatService.sendMessage(resolveUser(principal), request);
    }

    /** Client marks chat as read → server updates DB */
    @MessageMapping("/chat.read")
    public void markRead(@Payload ReadReceiptPayload payload, Principal principal) {
        chatService.markRead(resolveUser(principal), payload.getChatId());
    }


    /** Heartbeat — keeps presence TTL alive every 30s */
    @MessageMapping("/heartbeat")
    public void heartbeat(Principal principal) {
        presenceService.heartbeat(UUID.fromString(principal.getName()));
    }
}
