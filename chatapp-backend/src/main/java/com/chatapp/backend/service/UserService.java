package com.chatapp.backend.service;

import com.chatapp.backend.dto.request.ContactSyncRequest;
import com.chatapp.backend.dto.request.UpdateProfileRequest;
import com.chatapp.backend.dto.response.UserResponse;
import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.media.path}")
    private String mediaPath;

    public UserResponse getProfile(User currentUser) {
        return UserResponse.from(currentUser);
    }

    public UserResponse updateProfile(User currentUser, UpdateProfileRequest request,
                                       MultipartFile avatar) throws IOException {
        if (request.getName() != null && !request.getName().isBlank()) {
            currentUser.setName(request.getName().trim());
        }

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = saveAvatar(currentUser.getId(), avatar);
            currentUser.setAvatarUrl(avatarUrl);
        }

        currentUser = userRepository.save(currentUser);
        return UserResponse.from(currentUser);
    }

    /**
     * Contact sync — receives a list of phone numbers from Android contacts,
     * returns only the ones registered in our app.
     */
    public List<UserResponse> syncContacts(ContactSyncRequest request) {
        List<User> registeredUsers = userRepository.findByPhoneIn(request.getPhones());
        return registeredUsers.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * Update FCM token when Firebase issues a new one.
     */
    public void updateFcmToken(User currentUser, String fcmToken) {
        currentUser.setFcmToken(fcmToken);
        userRepository.save(currentUser);
        log.debug("FCM token updated for user: {}", currentUser.getId());
    }

    /**
     * Update last seen timestamp.
     */
    public void updateLastSeen(UUID userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastSeen(LocalDateTime.now());
            user.setIsOnline(false);
            userRepository.save(user);
        });
    }

    private String saveAvatar(UUID userId, MultipartFile file) throws IOException {
        String ext = getExtension(file.getOriginalFilename());
        String filename = "avatar_" + userId + "." + ext;
        Path dir = Paths.get(mediaPath);
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(filename),
                StandardCopyOption.REPLACE_EXISTING);
        return "/media/" + filename;
    }

    private String getExtension(String filename) {
        if (filename == null) return "jpg";
        int idx = filename.lastIndexOf('.');
        return idx > 0 ? filename.substring(idx + 1).toLowerCase() : "jpg";
    }
}
