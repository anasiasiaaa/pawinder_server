package com.example.pawinder_server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.util.Arrays;

/**
 * Диагностическая запись о запуске: версия, режим запуска, профили, хост БД, наличие ключей.
 * Секреты (пароль БД, значения ключей) в журнал не выводятся.
 */
@Component
public class StartupDiagnostics {

    private static final Logger log = LoggerFactory.getLogger(StartupDiagnostics.class);

    private final Environment env;
    private final AppVersion appVersion;

    public StartupDiagnostics(Environment env, AppVersion appVersion) {
        this.env = env;
        this.appVersion = appVersion;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("Pawinder server started: version={}, runMode={}", appVersion.describe(), appVersion.runMode());
        log.info("Java={}, profiles={}", Runtime.version(), Arrays.toString(env.getActiveProfiles()));
        log.info("Database host={}", databaseHost());
        log.info("catapi.key set={}, dogapi.key set={}",
                StringUtils.hasText(env.getProperty("catapi.key")),
                StringUtils.hasText(env.getProperty("dogapi.key")));
    }

    /** Только хост и порт из spring.datasource.url, без логина и пароля. */
    private String databaseHost() {
        String url = env.getProperty("spring.datasource.url");
        if (url == null || !url.startsWith("jdbc:")) {
            return "not configured";
        }
        try {
            URI uri = URI.create(url.substring("jdbc:".length()));
            return uri.getHost() + ":" + uri.getPort();
        } catch (IllegalArgumentException e) {
            return "unparseable";
        }
    }
}
