package com.chatapp.backend.dto.response;

import com.chatapp.backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private String phone;
    private String name;
    private String avatarUrl;
    private boolean isOnline;
    private LocalDateTime lastSeen;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .isOnline(Boolean.TRUE.equals(user.getIsOnline()))
                .lastSeen(user.getLastSeen())
                .build();
    }
}
