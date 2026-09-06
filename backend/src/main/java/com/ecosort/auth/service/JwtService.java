package com.ecosort.auth.service;

import com.ecosort.auth.config.JwtProperties;
import com.ecosort.auth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(JwtProperties jwtProperties) {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("JWT secret must be configured via APP_JWT_SECRET");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = jwtProperties.getExpirationMs();
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(user.id())
                .claim("email", user.email())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        Claims claims = parseClaims(token);
        return claims.get("email", String.class);
    }

    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Instant getTokenExpiration() {
        return Instant.now().plusMillis(expirationMs);
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
