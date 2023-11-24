package com.example.consumer.component;

import com.example.consumer.dto.MessageObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Component
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;

    @Autowired
    public RedisMessageSubscriber(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            System.out.println(message.getBody());
            MessageObject messageObject = objectMapper.readValue(message.getBody(), MessageObject.class);
            MulticastMessage multicastMessage = MulticastMessage.builder()
                .putData("title", messageObject.title)
                .putData("content", messageObject.content)
                .addAllTokens(messageObject.list)
                .build();
            FirebaseMessaging.getInstance().sendEachForMulticast(multicastMessage);
            System.out.println("Received message: " + multicastMessage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}