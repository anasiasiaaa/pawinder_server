package com.example.pawinder_server.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Настройка ключа TheDogAPI (переменная окружения DOGAPI_KEY или свойство dogapi.key).
 */
@Validated
@ConfigurationProperties(prefix = "dogapi")
public record DogApiProperties(
        @NotBlank(message = "ключ TheDogAPI не задан: укажите переменную окружения DOGAPI_KEY")
        String key) {
}
