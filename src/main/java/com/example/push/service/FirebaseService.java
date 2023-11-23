package com.example.push.service;

import com.example.push.domain.Token;
import com.example.push.domain.TokenRepository;
import com.example.push.dto.MessageRequest;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

@Service
public class FirebaseService {

    private final int BATCH_SIZE = 500;

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
        // 모든 토큰을 가져옴
        List<Token> tokenList = tokenRepository.findAll();

        // BATCH_SIZE만큼의 토큰 리스트를 리스트로 가지는 batches를 생성
        List<List<String>> batches = IntStream.range(0, (tokenList.size() + BATCH_SIZE - 1) / BATCH_SIZE)
            .mapToObj(i -> tokenList.subList(i * BATCH_SIZE, Math.min((i + 1) * BATCH_SIZE, tokenList.size()))
                    .stream()
                    .map(Token::getValue)
                    .toList())
            .toList();

        // BATCH_SIZE만큼의 토큰 리스트로 빌드한 멀티캐스트메세지의 리스트를 생성
        List<MulticastMessage> multicastMessageList = batches.stream()
                .map(batch -> MulticastMessage.builder()
                        .putData("title", messageRequest.getTitle())
                        .putData("content", messageRequest.getContent())
                        .addAllTokens(batch)
                        .build())
                .toList();

        // send를 비동기처리하기 위한 future 리스트 생성
        List<CompletableFuture<BatchResponse>> sendFutures = multicastMessageList.stream()
                .map(message -> CompletableFuture.supplyAsync(() -> sendFirebaseMessage(message)))
                .toList();

        // future를 조합하여 대기
        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                sendFutures.toArray(new CompletableFuture[0]));

        // 비동기 작업이 완료될 때까지 대기
        try {
            allOf.get();
            System.out.println("All messages sent successfully.");
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }

    }

    private BatchResponse sendFirebaseMessage(MulticastMessage message) {
        try {
            return FirebaseMessaging.getInstance().sendEachForMulticast(message);
        } catch (FirebaseMessagingException e) {
            System.out.println(e.getMessage());
            return null; // 또는 예외 처리에 따라 적절한 값을 반환
        }
    }

}
