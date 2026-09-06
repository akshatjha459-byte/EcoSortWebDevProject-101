package com.ecosort.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class StartupHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        return Health.up()
            .withDetail("app", "EcoSort")
            .withDetail("timestamp", java.time.Instant.now().toString())
            .build();
    }
}
