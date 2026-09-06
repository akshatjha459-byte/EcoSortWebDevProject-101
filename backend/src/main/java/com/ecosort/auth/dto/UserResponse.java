package com.ecosort.auth.dto;

import com.ecosort.auth.model.User;
import java.time.Instant;

public record UserResponse(
    String id,
    String email,
    String name,
    Instant createdAt
) {
    public UserResponse(User user) {
        this(user.id(), user.email(), user.name(), user.createdAt());
    }
}
