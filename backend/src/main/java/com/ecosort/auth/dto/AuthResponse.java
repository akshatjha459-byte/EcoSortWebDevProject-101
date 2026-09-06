package com.ecosort.auth.dto;

import com.ecosort.auth.model.User;
import java.time.Instant;

public record AuthResponse(
    String token,
    UserResponse user,
    Instant expiresAt
) {
}
