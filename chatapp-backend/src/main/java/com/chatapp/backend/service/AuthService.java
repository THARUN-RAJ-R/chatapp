package com.chatapp.backend.service;

import com.chatapp.backend.dto.request.VerifyOtpRequest;
import com.chatapp.backend.dto.response.AuthResponse;
import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import com.chatapp.backend.security.JwtUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    /**
     * Called by Android after Firebase OTP success.
     * Android sends the Firebase ID Token → we verify it server-side.
     * If user doesn't exist, we create them (first-time login).
     */
    public AuthResponse verifyFirebaseToken(VerifyOtpRequest request) {
        try {
            // Verify Firebase ID token using Admin SDK
            FirebaseToken decodedToken = FirebaseAuth.getInstance()
                    .verifyIdToken(request.getIdToken());

            String phoneFromToken = decodedToken.getClaims()
                    .getOrDefault("phone_number", "").toString();

            // Validate phone matches what client sent
            if (!phoneFromToken.equals(request.getPhone())) {
                throw new IllegalArgumentException("Phone number mismatch in token");
            }

            // Find or create user
            Optional<User> existingUser = userRepository.findByPhone(phoneFromToken);
            boolean isNewUser = existingUser.isEmpty();

            User user = existingUser.orElseGet(() -> userRepository.save(
                User.builder()
                    .phone(phoneFromToken)
                    .build()
            ));

            // Issue our own JWT tokens
            String accessToken  = jwtUtil.generateAccessToken(user.getId(), user.getPhone());
            String refreshToken = jwtUtil.generateRefreshToken(user.getId());

            log.info("Auth success for phone: {} (newUser: {})", phoneFromToken, isNewUser);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(com.chatapp.backend.dto.response.UserResponse.from(user))
                    .isNewUser(isNewUser)
                    .build();

        } catch (Exception e) {
            log.error("Firebase token verification failed: {}", e.getMessage());
            throw new RuntimeException("Invalid Firebase token: " + e.getMessage());
        }
    }

    /**
     * Refresh access token using refresh token
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtUtil.isTokenValid(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        UUID userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken  = jwtUtil.generateAccessToken(user.getId(), user.getPhone());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .user(com.chatapp.backend.dto.response.UserResponse.from(user))
                .isNewUser(false)
                .build();
    }
}
