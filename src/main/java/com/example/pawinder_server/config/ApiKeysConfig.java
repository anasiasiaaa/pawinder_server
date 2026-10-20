package com.example.pawinder_server.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** Включает проверку ключей внешних API при старте приложения. */
@Configuration
@EnableConfigurationProperties({CatApiProperties.class, DogApiProperties.class})
public class ApiKeysConfig {
}
