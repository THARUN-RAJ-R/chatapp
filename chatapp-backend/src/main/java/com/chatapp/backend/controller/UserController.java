package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.FcmTokenRequest;
import com.chatapp.backend.dto.request.UpdateProfileRequest;
import com.chatapp.backend.dto.response.ApiResponse;
import com.chatapp.backend.dto.response.UserResponse;
import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import com.chatapp.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    /** Helper: resolve current user from X-User-Id header */
    private User resolveUser(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    /** Get the current user's profile */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @RequestHeader("X-User-Id") String userId) {
        User user = resolveUser(userId);
        return ResponseEntity.ok(ApiResponse.ok(userService.getProfile(user)));
    }

    /** Update name and/or avatar */
    @PutMapping(value = "/me", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar)
            throws IOException {
        User user = resolveUser(userId);
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setName(name);
        UserResponse response = userService.updateProfile(user, req, avatar);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", response));
    }

    /**
     * Look up a user by phone number.
     * Used by Android "Find Someone" screen.
     * Returns 200 with user info if found, 404 if not registered.
     */
    @GetMapping("/by-phone")
    public ResponseEntity<ApiResponse<UserResponse>> findByPhone(
            @RequestParam String phone) {
        String normalized = phone.replace(" ", "");
        return userRepository.findByPhone(normalized)
                .map(u -> ResponseEntity.ok(ApiResponse.ok(UserResponse.from(u))))
                .orElse(ResponseEntity.status(404)
                        .body(ApiResponse.error("User not found on this app")));
    }

    /** Register or refresh FCM push token */
    @PutMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody FcmTokenRequest request) {
        User user = resolveUser(userId);
        userService.updateFcmToken(user, request.getToken());
        return ResponseEntity.ok(ApiResponse.ok("FCM token updated", null));
    }
}
