package com.chatapp.backend.repository;

import com.chatapp.backend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    // Paginated message history (newest first, stable sort using id as tiebreaker)
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdAt DESC, m.id DESC")
    Page<Message> findByChatIdOrderByCreatedAtDesc(UUID chatId, Pageable pageable);

    // Mark all unread messages in a chat as READ for a specific sender's messages
    @Modifying
    @Transactional
    @Query("""
        UPDATE Message m SET m.status = 'READ'
        WHERE m.chat.id = :chatId
        AND m.sender.id != :readerId
        AND m.status != 'READ'
    """)
    int markAllAsRead(UUID chatId, UUID readerId);

    // Mark single message as DELIVERED
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.status = 'DELIVERED' WHERE m.id = :messageId AND m.status = 'SENT'")
    int markAsDelivered(UUID messageId);

    // Count unread messages in a chat for a user
    @Query("""
        SELECT COUNT(m) FROM Message m
        WHERE m.chat.id = :chatId
        AND m.sender.id != :userId
        AND m.status != 'READ'
    """)
    long countUnread(UUID chatId, UUID userId);
}
