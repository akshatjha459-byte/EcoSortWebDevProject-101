package com.ecosort.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TestController {

    @GetMapping("/test-error")
    public String error() {
        throw new IllegalArgumentException("test");
    }
}
