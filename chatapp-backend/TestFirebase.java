import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;

public class TestFirebase {
    public static void main(String[] args) {
        try {
            FileInputStream serviceAccount = new FileInputStream("firebase-service-account.json");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            System.out.println("✅ FIREBASE ADMIN SDK INITIALIZED SUCCESSFULLY!");
        } catch (Exception e) {
            System.err.println("❌ FIREBASE INIT FAILED:");
            e.printStackTrace();
        }
    }
}
