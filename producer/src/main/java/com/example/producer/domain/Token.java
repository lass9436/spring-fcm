package com.example.producer.domain;

import jakarta.persistence.*;

@Entity
public class Token {

    public Token(){ }

    public Token(String value){
        this.value = value;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @Column(name = "`value`")
    private String value;

    public String getValue() {
        return value;
    }
}
