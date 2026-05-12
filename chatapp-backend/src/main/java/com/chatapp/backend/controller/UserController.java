package com.chatapp.backend.controller;

import com.chatapp.backend.dto.request.ContactSyncRequest;
import com.chatapp.backend.dto.request.FcmTokenRequest;
import com.chatapp.backend.dto.request.UpdateProfileRequest;
import com.chatapp.backend.dto.response.ApiResponse;
import com.chatapp.backend.dto.response.UserResponse;
import com.chatapp.backend.model.User;
import com.chatapp.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** Get the current logged-in user's profile */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(userService.getProfile(currentUser)));
    }

    /** Update name and/or avatar */
    @PutMapping(value = "/me", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar)
            throws IOException {
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setName(name);
        UserResponse response = userService.updateProfile(currentUser, req, avatar);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated", response));
    }

    /**
     * Contact sync — Android sends all phone contacts,
     * backend returns which ones are registered in the app.
     */
    @PostMapping("/contacts/sync")
    public ResponseEntity<ApiResponse<List<UserResponse>>> syncContacts(
            @Valid @RequestBody ContactSyncRequest request) {
        List<UserResponse> registered = userService.syncContacts(request);
        return ResponseEntity.ok(ApiResponse.ok(registered));
    }

    /** Register or refresh FCM push token */
    @PutMapping("/fcm-token")
    public ResponseEntity<ApiResponse<Void>> updateFcmToken(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody FcmTokenRequest request) {
        userService.updateFcmToken(currentUser, request.getToken());
        return ResponseEntity.ok(ApiResponse.ok("FCM token updated", null));
    }
}
