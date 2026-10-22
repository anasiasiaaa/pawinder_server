package com.example.pawinder_server.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Настройка ключа TheCatAPI (переменная окружения CAT_API_KEY или свойство catapi.key).
 * Если ключ не задан или пустой, приложение не стартует и выводит понятное сообщение.
 */
@Validated
@ConfigurationProperties(prefix = "catapi")
public record CatApiProperties(
        @NotBlank(message = "ключ TheCatAPI не задан: укажите переменную окружения CAT_API_KEY")
        String key) {
}
