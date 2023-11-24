package com.example.producer.controller;

import com.example.producer.dto.MessageRequest;
import com.example.producer.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FirebaseController {

    @Autowired
    private final FirebaseService firbaseService;

    public FirebaseController(FirebaseService firebaseService){
        this.firbaseService = firebaseService;
    }

    @PostMapping("/send-message")
    public void sendMessage(@RequestBody MessageRequest messageRequest) {
        firbaseService.sendMessage(messageRequest);
    }

    @PostMapping("/register-token")
    public void registerToken(@RequestBody Map<String, String> token) {
        String firebaseToken = token.get("token");
        firbaseService.registerToken(firebaseToken);
    }
}
