package com.ecosort.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.jwt.secret=test-secret-for-unit-tests-only-1234567890")
class SecurityContextTest {

    @Test
    void contextLoads() {
        assertThat(true).isTrue();
    }
}
