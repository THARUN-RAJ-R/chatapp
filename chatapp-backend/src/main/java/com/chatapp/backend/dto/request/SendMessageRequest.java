package com.chatapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class SendMessageRequest {

    @NotNull(message = "chatId is required")
    private UUID chatId;

    private String content;

    @NotBlank(message = "type is required")
    private String type; // TEXT or IMAGE

    private String mediaUrl; // populated if type = IMAGE
}
