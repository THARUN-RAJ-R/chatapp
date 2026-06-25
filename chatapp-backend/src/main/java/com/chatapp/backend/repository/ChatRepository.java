package com.chatapp.backend.repository;

import com.chatapp.backend.model.Chat;
import com.chatapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    // Get all chats a user is a member of
    @Query("""
        SELECT c FROM Chat c
        JOIN ChatMember cm ON cm.chat = c
        WHERE cm.user = :user
        ORDER BY c.createdAt DESC
    """)
    List<Chat> findAllByMember(User user);

    // Find existing DIRECT chat between two users
    @Query("""
        SELECT c FROM Chat c
        WHERE c.type = 'DIRECT'
        AND EXISTS (SELECT cm FROM ChatMember cm WHERE cm.chat = c AND cm.user = :user1)
        AND EXISTS (SELECT cm FROM ChatMember cm WHERE cm.chat = c AND cm.user = :user2)
    """)
    Optional<Chat> findDirectChat(User user1, User user2);
}
