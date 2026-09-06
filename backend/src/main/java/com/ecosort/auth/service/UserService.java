package com.ecosort.auth.service;

import com.ecosort.auth.exception.AuthException;
import com.ecosort.auth.model.User;
import com.ecosort.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String rawPassword, String name) {
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        String passwordHash = passwordEncoder.encode(rawPassword);
        return userRepository.save(User.fromRegistration(email, passwordHash, name));
    }

    public User authenticate(String email, String rawPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthException("Invalid credentials");
        }
        if (!passwordEncoder.matches(rawPassword, user.passwordHash())) {
            throw new AuthException("Invalid credentials");
        }
        return user;
    }

    public User getById(String id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new AuthException("User not found");
        }
        return user;
    }
}
