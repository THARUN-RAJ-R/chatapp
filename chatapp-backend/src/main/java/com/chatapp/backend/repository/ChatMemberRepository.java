package com.chatapp.backend.repository;

import com.chatapp.backend.model.Chat;
import com.chatapp.backend.model.ChatMember;
import com.chatapp.backend.model.ChatMemberId;
import com.chatapp.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {

    List<ChatMember> findByChat(Chat chat);

    Optional<ChatMember> findByChatAndUser(Chat chat, User user);

    boolean existsByChatAndUser(Chat chat, User user);

    // Get all user IDs in a chat (for FCM broadcast)
    @Query("SELECT cm.user.id FROM ChatMember cm WHERE cm.chat.id = :chatId")
    List<UUID> findUserIdsByChatId(UUID chatId);

    // Get all FCM tokens of members in a chat (for group push)
    @Query("SELECT cm.user.fcmToken FROM ChatMember cm WHERE cm.chat.id = :chatId AND cm.user.fcmToken IS NOT NULL AND cm.user.id != :excludeUserId")
    List<String> findFcmTokensByChatIdExcluding(UUID chatId, UUID excludeUserId);
}
