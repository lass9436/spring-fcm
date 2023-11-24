package com.example.producer.dto;

public class MessageRequest {
    private final String title;
    private final String content;

    public MessageRequest(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
