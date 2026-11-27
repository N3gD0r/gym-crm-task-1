package org.n3gd0r.crm.gym.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;

@Configuration
public class AppConfig {
    @Bean
    SecureRandom passwordRandomizer() {
        return new SecureRandom();
    }
}
