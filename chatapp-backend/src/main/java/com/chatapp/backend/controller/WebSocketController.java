package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.SendMessageRequest;
import com.chatapp.backend.dto.websocket.*;
import com.chatapp.backend.model.User;
import com.chatapp.backend.service.ChatService;
import com.chatapp.backend.service.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /** Client sends a message → server delivers to recipient(s) */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        User sender = (User) ((org.springframework.security.core.Authentication) principal).getPrincipal();
        chatService.sendMessage(sender, request);
    }

    /** Client marks chat as read → server updates DB */
    @MessageMapping("/chat.read")
    public void markRead(@Payload ReadReceiptPayload payload, Principal principal) {
        User reader = (User) ((org.springframework.security.core.Authentication) principal).getPrincipal();
        chatService.markRead(reader, payload.getChatId());
    }

    /** Client sends typing indicator → server broadcasts to chat */
    @MessageMapping("/chat.typing")
    public void typing(@Payload TypingPayload payload, Principal principal) {
        User user = (User) ((org.springframework.security.core.Authentication) principal).getPrincipal();
        payload.setUserId(user.getId());
        payload.setUserName(user.getName() != null ? user.getName() : user.getPhone());

        // Broadcast to group topic or direct queue
        messagingTemplate.convertAndSend("/topic/group/" + payload.getChatId() + "/typing", payload);
    }

    /** Heartbeat — keeps presence TTL alive every 30s */
    @MessageMapping("/heartbeat")
    public void heartbeat(Principal principal) {
        User user = (User) ((org.springframework.security.core.Authentication) principal).getPrincipal();
        presenceService.heartbeat(user.getId());
    }
}
