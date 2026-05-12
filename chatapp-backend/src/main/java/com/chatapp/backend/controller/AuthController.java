package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.SendOtpRequest;
import com.chatapp.backend.dto.request.VerifyOtpRequest;
import com.chatapp.backend.dto.response.ApiResponse;
import com.chatapp.backend.dto.response.AuthResponse;
import com.chatapp.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Step 1: Android calls this to signal intent to send OTP.
     * Actual OTP is sent by Firebase on the Android side.
     * Backend just validates the phone format and returns OK.
     */
    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendOtp(
            @Valid @RequestBody SendOtpRequest request) {
        // Firebase Phone Auth sends OTP directly to the device.
        // Backend only needs to acknowledge the request.
        return ResponseEntity.ok(ApiResponse.ok(
            "OTP initiated via Firebase. Check your phone.",
            Map.of("phone", request.getPhone())
        ));
    }

    /**
     * Step 2: After Firebase OTP success, Android sends the Firebase ID token here.
     * Backend verifies it and issues our own JWT.
     */
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {
        AuthResponse response = authService.verifyFirebaseToken(request);
        return ResponseEntity.ok(ApiResponse.ok("Authentication successful", response));
    }

    /**
     * Refresh expired access token using refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("refreshToken is required"));
        }
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed", response));
    }
}
