package com.ecosort.health;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class StartupHealthIndicator implements HealthIndicator {

    private final Clock clock;

    public StartupHealthIndicator(ObjectProvider<Clock> clockProvider) {
        this.clock = clockProvider.getIfAvailable(Clock::systemUTC);
    }

    @Override
    public Health health() {
        return Health.up()
            .withDetail("app", "EcoSort")
            .withDetail("timestamp", clock.instant().toString())
            .build();
    }
}
