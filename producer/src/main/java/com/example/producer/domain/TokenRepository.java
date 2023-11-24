package com.example.producer.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
    boolean existsByValue(String value);
}
