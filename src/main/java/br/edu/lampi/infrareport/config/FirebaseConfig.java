/*
package br.edu.lampi.infrareport.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseConfig {

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        File tempFile = File.createTempFile("firebase", ".json");

        try(FileOutputStream fos = new FileOutputStream(tempFile)) {
            String json = "{\n" +
                          "  \"type\": \"service_account\",\n" +
                          "  \"project_id\": \"" + System.getenv("FIREBASE_PROJECT_ID") + "\",\n" +
                          "  \"private_key_id\": \"" + System.getenv("FIREBASE_PRIVATE_KEY_ID") + "\",\n" +
                          "  \"private_key\": \"" + System.getenv("FIREBASE_PRIVATE_KEY").replace("\\n", "\n") + "\",\n" +
                          "  \"client_email\": \"" + System.getenv("FIREBASE_CLIENT_EMAIL") + "\",\n" +
                          "  \"client_id\": \"" + System.getenv("FIREBASE_CLIENT_ID") + "\",\n" +
                          "  \"auth_uri\": \"" + System.getenv("FIREBASE_AUTH_URI") + "\",\n" +
                          "  \"token_uri\": \"" + System.getenv("FIREBASE_TOKEN_URI") + "\",\n" +
                          "  \"auth_provider_x509_cert_url\": \"" + System.getenv("FIREBASE_AUTH_PROVIDER_X509_CERT_URL") + "\",\n" +
                          "  \"client_x509_cert_url\": \"" + System.getenv("FIREBASE_CLIENT_X509_CERT_URL") + "\"\n" +
                          "}";

            fos.write(json.getBytes());
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(Files.newInputStream(tempFile.toPath())))
                .setProjectId(System.getenv("FIREBASE_PROJECT_ID"))
                .setStorageBucket(System.getenv("FIREBASE_STORAGE_BUCKET"))
                .build();

        tempFile.delete();
        return FirebaseApp.initializeApp(options);
    }
}
*/