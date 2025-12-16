package com.gonzalo.labo6final.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

@Configuration
public class FirebaseConfig {
    private static final List<String> DEFAULT_SCOPES = List.of("https://www.googleapis.com/auth/cloud-platform");

    @Bean
    GoogleCredentials googleCredentials(
            @Value("${firebase.service-account:}") String serviceAccountLocation,
            ResourceLoader resourceLoader) throws IOException {
        if (serviceAccountLocation != null && !serviceAccountLocation.isBlank()) {
            String location = serviceAccountLocation.trim();
            if (!location.startsWith("classpath:") && !location.startsWith("file:")) {
                location = "file:" + location;
            }

            Resource resource = resourceLoader.getResource(location);
            try (InputStream is = resource.getInputStream()) {
                return GoogleCredentials.fromStream(is).createScoped(DEFAULT_SCOPES);
            }
        }

        return GoogleCredentials.getApplicationDefault().createScoped(DEFAULT_SCOPES);
    }

    @Bean(destroyMethod = "delete")
    FirebaseApp firebaseApp(
            GoogleCredentials credentials,
            @Value("${firebase.project-id:}") String projectId) {
        FirebaseOptions.Builder builder = FirebaseOptions.builder().setCredentials(credentials);
        if (projectId != null && !projectId.isBlank()) {
            builder.setProjectId(projectId.trim());
        }

        FirebaseOptions options = builder.build();
        for (FirebaseApp existingApp : FirebaseApp.getApps()) {
            if (FirebaseApp.DEFAULT_APP_NAME.equals(existingApp.getName())) {
                return existingApp;
            }
        }

        return FirebaseApp.initializeApp(options);
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
