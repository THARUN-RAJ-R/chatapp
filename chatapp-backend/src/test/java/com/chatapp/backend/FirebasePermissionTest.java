package com.chatapp.backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import org.junit.jupiter.api.Test;
import java.io.FileInputStream;

public class FirebasePermissionTest {
    @Test
    public void testPermissions() throws Exception {
        System.out.println("Initializing Firebase...");
        FileInputStream serviceAccount = new FileInputStream("firebase-service-account.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        
        System.out.println("Firebase initialized. Attempting to send test push...");
        Message msg = Message.builder()
            .putData("test", "test")
            .setToken("dummy_token_to_test_permissions")
            .build();
            
        try {
            FirebaseMessaging.getInstance().send(msg);
            System.out.println("✅ SUCCESS: Push sent!");
        } catch (Exception e) {
            System.out.println("RESULT: " + e.getMessage());
        }
    }
}
