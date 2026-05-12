package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.CreateGroupRequest;
import com.chatapp.backend.dto.response.ApiResponse;
import com.chatapp.backend.model.Chat;
import com.chatapp.backend.model.ChatMember;
import com.chatapp.backend.model.User;
import com.chatapp.backend.service.ChatService;
import com.chatapp.backend.service.GroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    /** Get all chats for the logged-in user */
    @GetMapping("/chats")
    public ResponseEntity<ApiResponse<List<Chat>>> getChats(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getUserChats(currentUser)));
    }

    /** Start a direct chat with another user */
    @PostMapping("/chats/direct")
    public ResponseEntity<ApiResponse<Chat>> startDirectChat(
            @AuthenticationPrincipal User currentUser,
            @RequestBody Map<String, String> body) {
        UUID targetId = UUID.fromString(body.get("targetUserId"));
        User target = new User();
        target.setId(targetId);
        Chat chat = chatService.getOrCreateDirectChat(currentUser, target);
        return ResponseEntity.ok(ApiResponse.ok(chat));
    }

    /** Get paginated message history */
    @GetMapping("/chats/{chatId}/messages")
    public ResponseEntity<ApiResponse<?>> getMessages(
            @PathVariable UUID chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {
        return ResponseEntity.ok(ApiResponse.ok(chatService.getMessages(chatId, page, size)));
    }

    /** Create a group */
    @PostMapping("/groups")
    public ResponseEntity<ApiResponse<Chat>> createGroup(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateGroupRequest request) {
        Chat group = groupService.createGroup(currentUser, request);
        return ResponseEntity.ok(ApiResponse.ok("Group created", group));
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
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID chatId,
            @RequestBody Map<String, List<UUID>> body) {
        groupService.addMembers(chatId, body.get("userIds"), currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Members added", null));
    }

    /** Remove member from group */
    @DeleteMapping("/groups/{chatId}/members/{userId}")
    public ResponseEntity<ApiResponse<Void>> removeMember(
            @AuthenticationPrincipal User currentUser,
            @PathVariable UUID chatId,
            @PathVariable UUID userId) {
        groupService.removeMember(chatId, userId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Member removed", null));
    }
}
