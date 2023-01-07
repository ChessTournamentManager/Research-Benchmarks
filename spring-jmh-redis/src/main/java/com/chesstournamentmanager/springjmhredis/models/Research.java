package com.chesstournamentmanager.springjmhredis.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@RedisHash("Research")
public class Research implements Serializable {
    @Serial
    private static final long serialVersionUID = 3364836025963612078L;

    @Id
    private UUID id;
    private String name;
    private int wordLength;
    private LocalDateTime createdAt;

    public Research(String name, int wordLength) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.wordLength = wordLength;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
