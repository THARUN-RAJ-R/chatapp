package com.chatapp.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_members")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ChatMemberId.class)
public class ChatMember {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private Role role = Role.MEMBER;

    @Column(name = "joined_at")
    @Builder.Default
    private LocalDateTime joinedAt = LocalDateTime.now();

    public enum Role { ADMIN, MEMBER }
}
