package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.LoginRequest;
import com.chatapp.backend.dto.response.ApiResponse;
import com.chatapp.backend.dto.response.UserResponse;
import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Simplified auth: no OTP, no Firebase, no JWT.
 * User provides their phone number → registered/found → returned their profile.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserRepository userRepository;

    /**
     * POST /api/auth/login
     * Body: { "phone": "+919876543210" }
     * Finds existing user or creates a new one.
     * Returns the user's id + profile. No tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        String phone = request.getPhone().replace(" ", "");

        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    log.info("New user registered: {}", phone);
                    return userRepository.save(User.builder().phone(phone).build());
                });

        log.info("User logged in: {} (id={})", phone, user.getId());
        return ResponseEntity.ok(ApiResponse.ok("Login successful", UserResponse.from(user)));
    }
}
