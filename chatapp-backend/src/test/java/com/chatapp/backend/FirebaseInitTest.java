package com.chatapp.backend;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.junit.jupiter.api.Test;
import java.io.FileInputStream;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FirebaseInitTest {

    @Test
    public void testFirebaseInit() throws Exception {
        System.out.println("Testing Firebase Initialization...");
        FileInputStream serviceAccount = new FileInputStream("firebase-service-account.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        
        assertNotNull(FirebaseApp.getInstance());
        System.out.println("✅ FIREBASE ADMIN SDK INITIALIZED SUCCESSFULLY!");
    }
}
