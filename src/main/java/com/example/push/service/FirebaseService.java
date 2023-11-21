package com.example.push.service;

import com.example.push.domain.Token;
import com.example.push.domain.TokenRepository;
import com.example.push.dto.MessageRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FirebaseService {

    @Autowired
    private final TokenRepository tokenRepository;

    public FirebaseService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public void registerToken(String firebaseToken) {
        if(!tokenRepository.existsByValue(firebaseToken)){
            tokenRepository.save(new Token(firebaseToken));
        }
    }

    public void sendMessage(MessageRequest messageRequest) {
        List<Token> tokenList = tokenRepository.findAll();
        tokenList.forEach(token -> {
            String tokenValue = token.getValue();
            Message firebaseMessage = Message.builder()
                    .putData("title", messageRequest.getTitle())
                    .putData("content", messageRequest.getContent())
                    .setToken(tokenValue)
                    .build();
            String response = null;
            try {
                response = FirebaseMessaging.getInstance().send(firebaseMessage);
            } catch (FirebaseMessagingException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Successfully sent message: " + response);
        });
    }
}
