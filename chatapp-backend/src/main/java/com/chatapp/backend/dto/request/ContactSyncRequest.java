package com.chatapp.backend.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ContactSyncRequest {

    @NotEmpty(message = "Phone list cannot be empty")
    private List<String> phones; // E.164 format list from Android contacts
}
