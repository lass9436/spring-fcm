package com.example.consumer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MessageObject {
    public String title;
    public String content;
    public List<String> list;
    @JsonCreator
    public MessageObject(
            @JsonProperty("title") String title,
            @JsonProperty("content") String content,
            @JsonProperty("list") List<String> list) {
        this.title = title;
        this.content = content;
        this.list = list;
    }
}
