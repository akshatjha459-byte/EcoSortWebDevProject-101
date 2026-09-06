package com.ecosort;

import com.ecosort.config.AppProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnableConfigurationProperties(AppProperties.class)
class EcosortApplicationTests {

    @Autowired
    private AppProperties appProperties;

    @Test
    void contextLoads() {
        assertThat(appProperties).isNotNull();
        assertThat(appProperties.getName()).isEqualTo("EcoSort");
        assertThat(appProperties.getVersion()).isEqualTo("0.1.0");
    }
}
