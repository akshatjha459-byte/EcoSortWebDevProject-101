package com.ecosort.auth.controller;

import com.ecosort.auth.dto.AuthResponse;
import com.ecosort.auth.dto.LoginRequest;
import com.ecosort.auth.dto.RegisterRequest;
import com.ecosort.auth.dto.UserResponse;
import com.ecosort.auth.model.User;
import com.ecosort.auth.service.JwtService;
import com.ecosort.auth.service.UserService;
import com.ecosort.auth.exception.AuthException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.email(), request.password(), request.name());
        String token = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(token, new UserResponse(user), jwtService.getTokenExpiration()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.authenticate(request.email(), request.password());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, new UserResponse(user), jwtService.getTokenExpiration()));
    }
}
