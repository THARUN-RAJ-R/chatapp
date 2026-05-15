package com.chatapp.backend;

import com.chatapp.backend.model.User;
import com.chatapp.backend.repository.UserRepository;
import com.chatapp.backend.service.FCMService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.UUID;

@SpringBootTest
public class FirebaseActualPushTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FCMService fcmService;

    @Test
    public void testActualPush() {
        System.out.println("Looking for a valid FCM token in the database...");
        
        User userWithToken = userRepository.findAll().stream()
                .filter(u -> u.getFcmToken() != null && !u.getFcmToken().isBlank())
                .findFirst()
                .orElse(null);

        if (userWithToken == null) {
            System.out.println("❌ No user with a valid FCM token found in the local database.");
            return;
        }

        System.out.println("Found user: " + userWithToken.getPhone() + " with token: " + userWithToken.getFcmToken());
        System.out.println("Attempting to send real push notification...");

        try {
            UUID testChatId = UUID.randomUUID();
            fcmService.sendSyncTickle(
                    userWithToken.getFcmToken(),
                    testChatId
            );
            System.out.println("✅ SYNC TICKLE DISPATCHED TO FCM SUCCESSFULLY (chatId=" + testChatId + ")");
            
            // Wait 2 seconds for async logging to complete
            Thread.sleep(2000);
        } catch (Exception e) {
            System.out.println("❌ FAILED TO SEND: " + e.getMessage());
        }
    }
}
