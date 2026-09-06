package com.ecosort.auth.controller;

import com.ecosort.auth.dto.UserResponse;
import com.ecosort.auth.model.User;
import com.ecosort.security.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class MeController {

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me() {
        User user = CurrentUser.get();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(new UserResponse(user));
    }
}
