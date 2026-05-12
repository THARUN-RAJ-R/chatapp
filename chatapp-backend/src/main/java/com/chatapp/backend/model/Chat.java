package com.chatapp.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "chats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private ChatType type;

    @Column(name = "group_name", length = 100)
    private String groupName;

    @Column(name = "group_avatar")
    private String groupAvatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ChatType { DIRECT, GROUP }
}
