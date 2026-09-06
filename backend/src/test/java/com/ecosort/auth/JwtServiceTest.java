package com.ecosort.auth;

import com.ecosort.auth.config.JwtProperties;
import com.ecosort.auth.model.User;
import com.ecosort.auth.service.JwtService;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService createService(String secret) {
        JwtProperties props = new JwtProperties();
        props.setSecret(secret);
        props.setExpirationMs(3600000);
        return new JwtService(props);
    }

    @Test
    void generatesValidToken() {
        JwtService service = createService("test-secret-for-unit-tests-only-1234567890");
        User user = new User("user-1", "user@example.com", "hash", "User", Instant.now());

        String token = service.generateToken(user);
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(service.isTokenValid(token)).isTrue();
        assertThat(service.extractUserId(token)).isEqualTo("user-1");
        assertThat(service.extractEmail(token)).isEqualTo("user@example.com");
    }

    @Test
    void rejectsInvalidToken() {
        JwtService service = createService("test-secret-for-unit-tests-only-1234567890");
        assertThat(service.isTokenValid("invalid-token")).isFalse();
    }

    @Test
    void rejectsEmptySecret() {
        assertThatThrownBy(() -> createService(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT secret must be configured");

        assertThatThrownBy(() -> createService(""))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("JWT secret must be configured");
    }
}
