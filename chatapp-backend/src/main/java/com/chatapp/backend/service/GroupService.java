package com.chatapp.backend.service;

import com.chatapp.backend.dto.request.CreateGroupRequest;
import com.chatapp.backend.model.*;
import com.chatapp.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupService {

    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public Chat createGroup(User creator, CreateGroupRequest request) {
        Chat group = chatRepository.save(Chat.builder()
                .type(Chat.ChatType.GROUP)
                .groupName(request.getName())
                .groupAvatar(request.getAvatarUrl())
                .createdBy(creator)
                .build());

        // Add creator as ADMIN
        chatMemberRepository.save(ChatMember.builder()
                .chat(group).user(creator).role(ChatMember.Role.ADMIN).build());

        // Add other members
        for (UUID memberId : request.getMemberIds()) {
            userRepository.findById(memberId).ifPresent(member ->
                chatMemberRepository.save(ChatMember.builder()
                    .chat(group).user(member).role(ChatMember.Role.MEMBER).build())
            );
        }

        log.info("Group '{}' created by {} with {} members",
            request.getName(), creator.getPhone(), request.getMemberIds().size() + 1);
        return group;
    }

    @Transactional
    public void addMembers(UUID chatId, List<UUID> userIds, User requester) {
        Chat group = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        ChatMember requesterMember = chatMemberRepository.findByChatAndUser(group, requester)
                .orElseThrow(() -> new RuntimeException("You are not a member of this group"));

        if (requesterMember.getRole() != ChatMember.Role.ADMIN) {
            throw new RuntimeException("Only admins can add members");
        }

        for (UUID userId : userIds) {
            userRepository.findById(userId).ifPresent(user -> {
                if (!chatMemberRepository.existsByChatAndUser(group, user)) {
                    chatMemberRepository.save(ChatMember.builder()
                        .chat(group).user(user).role(ChatMember.Role.MEMBER).build());
                }
            });
        }
    }

    @Transactional
    public void removeMember(UUID chatId, UUID userId, User requester) {
        Chat group = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        ChatMember requesterMember = chatMemberRepository.findByChatAndUser(group, requester)
                .orElseThrow(() -> new RuntimeException("You are not a member"));

        boolean isSelf = requester.getId().equals(userId);
        if (!isSelf && requesterMember.getRole() != ChatMember.Role.ADMIN) {
            throw new RuntimeException("Only admins can remove members");
        }

        User target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        chatMemberRepository.findByChatAndUser(group, target)
                .ifPresent(chatMemberRepository::delete);
    }

    public List<ChatMember> getMembers(UUID chatId) {
        Chat group = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        return chatMemberRepository.findByChat(group);
    }
}
