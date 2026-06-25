package com.chatapp.backend.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    // avatar is handled as multipart separately
}
