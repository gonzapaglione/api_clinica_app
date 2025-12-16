package com.gonzalo.labo6final.services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmNotificationService {
    private final FirebaseMessaging firebaseMessaging;

    public String sendToToken(
            String token,
            String title,
            String body,
            Map<String, String> data) throws FirebaseMessagingException {
        Message.Builder builder = Message.builder().setToken(token);
        if (data != null && !data.isEmpty()) {
            builder.putAllData(data);
        }
        if (title != null || body != null) {
            builder.setNotification(Notification.builder().setTitle(title).setBody(body).build());
        }
        return firebaseMessaging.send(builder.build());
    }
}
