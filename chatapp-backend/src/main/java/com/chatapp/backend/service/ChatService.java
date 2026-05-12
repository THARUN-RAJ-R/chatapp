package com.chatapp.backend.service;

import com.chatapp.backend.dto.request.SendMessageRequest;
import com.chatapp.backend.dto.websocket.MessagePayload;
import com.chatapp.backend.model.*;
import com.chatapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final PresenceService presenceService;
    private final FCMService fcmService;
    private final SimpMessagingTemplate messagingTemplate;

    // ─── Send Message ────────────────────────────────────────────────────────

    @Transactional
    public MessagePayload sendMessage(User sender, SendMessageRequest request) {
        Chat chat = chatRepository.findById(request.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        // Verify sender is a member
        if (!chatMemberRepository.existsByChatAndUser(chat, sender)) {
            throw new RuntimeException("You are not a member of this chat");
        }

        // Save message to DB
        Message message = messageRepository.save(Message.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .type(Message.MessageType.valueOf(request.getType()))
                .mediaUrl(request.getMediaUrl())
                .status(Message.MessageStatus.SENT)
                .build());

        // Build payload
        MessagePayload payload = toPayload(message, chat);

        // Deliver based on chat type
        if (chat.getType() == Chat.ChatType.DIRECT) {
            deliverDirect(chat, sender, message, payload);
        } else {
            deliverGroup(chat, sender, message, payload);
        }

        return payload;
    }

    // ─── Direct Message Delivery ─────────────────────────────────────────────

    private void deliverDirect(Chat chat, User sender, Message message, MessagePayload payload) {
        // Find the other member
        chatMemberRepository.findByChat(chat).stream()
            .map(ChatMember::getUser)
            .filter(u -> !u.getId().equals(sender.getId()))
            .findFirst()
            .ifPresent(recipient -> {
                if (presenceService.isOnline(recipient.getId())) {
                    // Deliver via WebSocket
                    messagingTemplate.convertAndSendToUser(
                        recipient.getId().toString(),
                        "/queue/messages",
                        payload
                    );
                    // Mark as DELIVERED
                    messageRepository.markAsDelivered(message.getId());
                    payload.setStatus("DELIVERED");
                } else {
                    // Push via FCM
                    fcmService.sendMessageNotification(
                        recipient.getFcmToken(),
                        chat.getId(),
                        sender.getName() != null ? sender.getName() : sender.getPhone(),
                        sender.getAvatarUrl(),
                        message.getContent(),
                        message.getType().name()
                    );
                }
                // Echo back to sender's other devices
                messagingTemplate.convertAndSendToUser(
                    sender.getId().toString(),
                    "/queue/messages",
                    payload
                );
            });
    }

    // ─── Group Message Delivery ──────────────────────────────────────────────

    private void deliverGroup(Chat chat, User sender, Message message, MessagePayload payload) {
        // Broadcast to STOMP topic — all online members receive it
        messagingTemplate.convertAndSend("/topic/group/" + chat.getId(), payload);

        // FCM to offline members
        List<String> offlineTokens = chatMemberRepository
            .findFcmTokensByChatIdExcluding(chat.getId(), sender.getId())
            .stream()
            .filter(token -> token != null && !token.isBlank())
            .filter(token -> {
                // Only push to members not currently subscribed via WS
                UUID memberId = userRepository.findAll().stream()
                    .filter(u -> token.equals(u.getFcmToken()))
                    .map(User::getId)
                    .findFirst().orElse(null);
                return memberId == null || !presenceService.isOnline(memberId);
            })
            .toList();

        if (!offlineTokens.isEmpty()) {
            String senderName = sender.getName() != null ? sender.getName() : sender.getPhone();
            fcmService.sendGroupMessageNotification(
                offlineTokens,
                chat.getId(),
                chat.getGroupName(),
                senderName,
                message.getContent(),
                message.getType().name()
            );
        }
    }

    // ─── Get or Create Direct Chat ───────────────────────────────────────────

    @Transactional
    public Chat getOrCreateDirectChat(User user1, User user2) {
        return chatRepository.findDirectChat(user1, user2).orElseGet(() -> {
            Chat chat = chatRepository.save(Chat.builder()
                    .type(Chat.ChatType.DIRECT)
                    .createdBy(user1)
                    .build());
            chatMemberRepository.save(ChatMember.builder().chat(chat).user(user1).role(ChatMember.Role.ADMIN).build());
            chatMemberRepository.save(ChatMember.builder().chat(chat).user(user2).role(ChatMember.Role.MEMBER).build());
            return chat;
        });
    }

    // ─── Get Chat List ───────────────────────────────────────────────────────

    public List<Chat> getUserChats(User user) {
        return chatRepository.findAllByMember(user);
    }

    // ─── Get Message History (paginated) ─────────────────────────────────────

    public Page<Message> getMessages(UUID chatId, int page, int size) {
        return messageRepository.findByChatIdOrderByCreatedAtDesc(
            chatId, PageRequest.of(page, size)
        );
    }

    // ─── Mark Messages as Read ───────────────────────────────────────────────

    @Transactional
    public void markRead(User reader, UUID chatId) {
        int updated = messageRepository.markAllAsRead(chatId, reader.getId());
        if (updated > 0) {
            log.debug("Marked {} messages as read in chat {} by {}", updated, chatId, reader.getId());
        }
    }

    // ─── Helper: Convert Message to WebSocket Payload ────────────────────────

    public MessagePayload toPayload(Message message, Chat chat) {
        User sender = message.getSender();
        return MessagePayload.builder()
                .messageId(message.getId())
                .chatId(chat.getId())
                .senderId(sender.getId())
                .senderName(sender.getName() != null ? sender.getName() : sender.getPhone())
                .senderAvatar(sender.getAvatarUrl())
                .content(message.getContent())
                .type(message.getType().name())
                .mediaUrl(message.getMediaUrl())
                .status(message.getStatus().name())
                .timestamp(message.getCreatedAt())
                .isGroup(chat.getType() == Chat.ChatType.GROUP)
                .groupName(chat.getGroupName())
                .build();
    }
}
