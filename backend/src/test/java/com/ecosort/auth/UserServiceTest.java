package com.ecosort.auth;

import com.ecosort.auth.model.User;
import com.ecosort.auth.repository.InMemoryUserRepository;
import com.ecosort.auth.repository.UserRepository;
import com.ecosort.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserServiceTest {

    @Test
    void passwordIsHashed() {
        UserRepository repo = new InMemoryUserRepository();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        UserService service = new UserService(repo, encoder);

        User user = service.register("test@example.com", "password123", "Test User");

        assertThat(user.passwordHash()).isNotEqualTo("password123");
        assertThat(encoder.matches("password123", user.passwordHash())).isTrue();
    }

    @Test
    void rejectsDuplicateEmail() {
        UserRepository repo = new InMemoryUserRepository();
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        UserService service = new UserService(repo, encoder);

        service.register("dup@example.com", "password123", "First");
        assertThatThrownBy(() -> service.register("dup@example.com", "password456", "Second"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }
}
