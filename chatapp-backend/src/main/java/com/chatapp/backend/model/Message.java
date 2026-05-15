package com.chatapp.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;

    // Global auto-increment sequence — assigned by PostgreSQL at INSERT time.
    // This is the WhatsApp-style sequence number: strict, never duplicated, never null.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq_number", insertable = false, updatable = false)
    private Long seqNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private MessageType type = MessageType.TEXT;

    @Column(name = "media_url")
    private String mediaUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    @Builder.Default
    private MessageStatus status = MessageStatus.SENT;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum MessageType  { TEXT, IMAGE }
    public enum MessageStatus { SENT, DELIVERED, READ }
}
