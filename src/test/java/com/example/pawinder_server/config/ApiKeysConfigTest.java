package com.example.pawinder_server.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Проверка конфигурации: при неверных ключах приложение не должно стартовать.
 * Тест падает, если убрать @NotBlank из CatApiProperties / DogApiProperties.
 */
class ApiKeysConfigTest {

    private final ApplicationContextRunner runner =
            new ApplicationContextRunner().withUserConfiguration(ApiKeysConfig.class);

    @Test
    void validKeys_contextStartsAndValuesAreBound() {
        runner.withPropertyValues("catapi.key=test-cat-key", "dogapi.key=test-dog-key")
                .run(ctx -> {
                    assertThat(ctx).hasNotFailed();
                    assertThat(ctx.getBean(CatApiProperties.class).key()).isEqualTo("test-cat-key");
                    assertThat(ctx.getBean(DogApiProperties.class).key()).isEqualTo("test-dog-key");
                });
    }

    @Test
    void blankCatKey_startupFails() {
        runner.withPropertyValues("catapi.key=", "dogapi.key=test-dog-key")
                .run(ctx -> assertThat(ctx).getFailure().hasStackTraceContaining("catapi.key"));
    }

    @Test
    void blankDogKey_startupFails() {
        runner.withPropertyValues("catapi.key=test-cat-key", "dogapi.key=")
                .run(ctx -> assertThat(ctx).getFailure().hasStackTraceContaining("dogapi.key"));
    }
}
