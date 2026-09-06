package com.ecosort.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableConfigurationProperties(AppProperties.class)
class AppPropertiesTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    void bindsDefaultValues() {
        assertThat(appProperties.getName()).isEqualTo("EcoSort");
        assertThat(appProperties.getVersion()).isEqualTo("0.1.0");
    }
}
