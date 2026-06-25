package com.chatapp.backend.dto.response;

import com.chatapp.backend.model.Chat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ChatResponse {
    private UUID id;
    private String type;
    private String groupName;
    private String groupAvatar;
    private UserResponse createdBy;
    private LocalDateTime createdAt;

    public static ChatResponse from(Chat chat) {
        if (chat == null) return null;
        
        // Note: createdBy is intentionally omitted to avoid LazyInitializationException 
        // since the Android client doesn't use it for Chat rendering anyway.
        return ChatResponse.builder()
                .id(chat.getId())
                .type(chat.getType().name())
                .groupName(chat.getGroupName())
                .groupAvatar(chat.getGroupAvatar())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
