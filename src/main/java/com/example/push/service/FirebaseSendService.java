package com.example.push.service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.stereotype.Service;

@Service
public class FirebaseSendService {
    public BatchResponse sendFirebaseMessage(MulticastMessage message) {
        try {
            return FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
            return null; // 또는 예외 처리에 따라 적절한 값을 반환
        }
    }
}
