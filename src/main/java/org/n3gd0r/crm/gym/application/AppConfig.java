package org.n3gd0r.crm.gym.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class AppConfig {
    @Bean("PasswordRandomizer")
    Random random() {
        return new Random(System.currentTimeMillis());
    }
}
