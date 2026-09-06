package com.ecosort.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecosort.auth.dto.AuthResponse;
import com.ecosort.auth.dto.LoginRequest;
import com.ecosort.auth.dto.RegisterRequest;
import com.ecosort.auth.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "app.jwt.secret=test-secret-for-unit-tests-only-1234567890")
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String extractToken(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        JsonNode tokenNode = node.get("token");
        if (tokenNode == null || tokenNode.isNull()) {
            throw new AssertionError("Token is missing in response");
        }
        return tokenNode.asText();
    }

    @Test
    void registersNewUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password123\",\"name\":\"Test User\"}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.user.name").value("Test User"));
    }

    @Test
    void registrationRejectsDuplicateEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"dup@example.com\",\"password\":\"password123\",\"name\":\"First\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"dup@example.com\",\"password\":\"password456\",\"name\":\"Second\"}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
            .andExpect(jsonPath("$.message").value(containsString("already registered")));
    }

    @Test
    void loginReturnsTokenForValidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"login@example.com\",\"password\":\"password123\",\"name\":\"Login User\"}"))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"login@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.user.email").value("login@example.com"));
    }

    @Test
    void loginRejectsInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"nonexistent@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.status").value(HttpStatus.UNAUTHORIZED.value()))
            .andExpect(jsonPath("$.message").value(containsString("Invalid credentials")));
    }

    @Test
    void loginRejectsNonExistentUser() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"unknown@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void meEndpointRejectsUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void meEndpointRejectsInvalidToken() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer invalid-token-12345"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void meEndpointReturnsCurrentUserWithValidToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"me@example.com\",\"password\":\"password123\",\"name\":\"Me User\"}"))
            .andExpect(status().isCreated());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"me@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String token = extractToken(loginResponse);

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("me@example.com"))
            .andExpect(jsonPath("$.name").value("Me User"));
    }

    @Test
    void meEndpointReturnsCorrectIdentity() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"alice@example.com\",\"password\":\"password123\",\"name\":\"Alice\"}"))
            .andExpect(status().isCreated());

        String aliceLoginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"alice@example.com\",\"password\":\"password123\"}"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        String token = extractToken(aliceLoginResponse);

        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.name").value("Alice"));
    }
}
