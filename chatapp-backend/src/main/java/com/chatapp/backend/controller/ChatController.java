package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.CreateGroupRequest;
import com.chatapp.backend.dto.response.ApiResponse;
import com.chatapp.backend.dto.response.ChatResponse;
import com.chatapp.backend.model.Chat;
import com.chatapp.backend.model.ChatMember;
import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import com.chatapp.backend.service.ChatService;
import com.chatapp.backend.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final GroupService groupService;
    private final UserRepository userRepository;

    /** Helper: resolve user from X-User-Id header */
    private User resolveUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    /** Get all chats for the logged-in user */
    @GetMapping("/chats")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getChats(
            @RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getUserChats(resolveUser(userId))));
    }

    /** Start a direct chat with another user */
    @PostMapping("/chats/direct")
    public ResponseEntity<ApiResponse<ChatResponse>> startDirectChat(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody Map<String, String> body) {
        UUID targetId = UUID.fromString(body.get("targetUserId"));
        User target = userRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Target user not found"));
        ChatResponse chat = chatService.getOrCreateDirectChatResponse(resolveUser(userId), target);
        return ResponseEntity.ok(ApiResponse.ok(chat));
    }

    /** Get paginated message history. Pass afterSeq for WhatsApp-style incremental sync. */
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<ApiResponse<?>> getMessages(
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size,
            @RequestParam(required = false) Long afterSeq) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(chatId, page, size, afterSeq)));
    }

    /** Create a group */
    @PostMapping("/groups")
    public ResponseEntity<ApiResponse<ChatResponse>> createGroup(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateGroupRequest request) {
        Chat group = groupService.createGroup(resolveUser(userId), request);
        return ResponseEntity.ok(ApiResponse.ok("Group created", ChatResponse.from(group)));
    }

    /** Get group members */
    @GetMapping("/groups/{chatId}/members")
    public ResponseEntity<ApiResponse<List<ChatMember>>> getMembers(
            @PathVariable UUID chatId) {
        return ResponseEntity.ok(ApiResponse.ok(groupService.getMembers(chatId)));
    }

    /** Add members to group */
    @PostMapping("/groups/{chatId}/members")
    public ResponseEntity<ApiResponse<Void>> addMembers(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID chatId,
            @RequestBody Map<String, List<UUID>> body) {
        groupService.addMembers(chatId, body.get("userIds"), resolveUser(userId));
        return ResponseEntity.ok(ApiResponse.ok("Members added", null));
    }

    /** Remove member from group */
    @DeleteMapping("/groups/{chatId}/members/{memberId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable UUID chatId,
            @PathVariable UUID memberId) {
        groupService.removeMember(chatId, memberId, resolveUser(userId));
        return ResponseEntity.ok(ApiResponse.ok("Member removed", null));
    }
}
