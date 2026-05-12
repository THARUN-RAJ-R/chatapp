package com.chatapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    @NotEmpty(message = "At least one member is required")
    private List<UUID> memberIds;

    private String avatarUrl; // optional
}
