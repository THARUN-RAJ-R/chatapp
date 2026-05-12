package com.chatapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Firebase ID token is required")
    private String idToken; // Firebase ID token from Android after OTP success
}
