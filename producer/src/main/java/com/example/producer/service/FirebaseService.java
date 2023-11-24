package com.example.producer.service;

import com.example.producer.domain.Token;
import com.example.producer.domain.TokenRepository;
import com.example.producer.dto.MessageObject;
import com.example.producer.dto.MessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class FirebaseService {

    private final int BATCH_SIZE = 500;

    @Autowired
    private final ChannelTopic channelTopic;
    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private final TokenRepository tokenRepository;

    @Autowired
    private final ObjectMapper objectMapper;

    public FirebaseService(
            ChannelTopic channelTopic, RedisTemplate<String, Object> redisTemplate, TokenRepository tokenRepository, ObjectMapper objectMapper) {
        this.channelTopic = channelTopic;
        this.redisTemplate = redisTemplate;
        this.tokenRepository = tokenRepository;
        this.objectMapper = objectMapper;
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

        batches.stream().forEach((batch)->{
            MessageObject messageObject = new MessageObject(messageRequest.getTitle(), messageRequest.getContent(), batch);
            redisTemplate.convertAndSend(channelTopic.getTopic(), messageObject);
        });
    }

}
