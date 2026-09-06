package com.ecosort.auth.model;

import java.time.Instant;
import java.util.UUID;

public record User(
    String id,
    String email,
    String passwordHash,
    String name,
    Instant createdAt
) {
    public static User fromRegistration(String email, String passwordHash, String name) {
        return new User(UUID.randomUUID().toString(), email, passwordHash, name, Instant.now());
    }
}
