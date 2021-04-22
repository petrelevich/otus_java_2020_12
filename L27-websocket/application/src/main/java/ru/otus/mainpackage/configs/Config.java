package ru.otus.mainpackage.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    MessageConfig messageConfig(@Value("${application.message}") String message) {
        return new MessageConfig(message);
    }
}
