package com.example.pawinder_server.config;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Идентификатор версии: версия из pom.xml (META-INF/build-info.properties)
 * и сокращённый SHA коммита (git.properties, создаётся git-commit-id-maven-plugin).
 * Формат: 0.0.1-SNAPSHOT+abc1234 или 0.0.1-SNAPSHOT+abc1234-dirty.
 */
@Component
public class AppVersion {

    private final String version;
    private final String commit;
    private final boolean dirty;
    private final String runMode;

    public AppVersion() {
        Properties git = load("git.properties");
        Properties build = load("META-INF/build-info.properties");
        this.version = build.getProperty("build.version", "unknown");
        this.commit = git.getProperty("git.commit.id.abbrev", "unknown");
        this.dirty = Boolean.parseBoolean(git.getProperty("git.dirty", "false"));
        this.runMode = detectRunMode();
    }

    private static Properties load(String path) {
        Properties props = new Properties();
        try (InputStream in = AppVersion.class.getClassLoader().getResourceAsStream(path)) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException ignored) {
            // файла нет или он не читается: останутся значения «unknown»
        }
        return props;
    }

    /** «jar» при запуске собранного jar-файла, «classes» при запуске из каталога target/classes. */
    private static String detectRunMode() {
        try {
            String location = AppVersion.class.getProtectionDomain().getCodeSource().getLocation().toString();
            return location.contains(".jar") ? "jar" : "classes";
        } catch (RuntimeException e) {
            return "unknown";
        }
    }

    public String version() {
        return version;
    }

    public String commit() {
        return commit;
    }

    public boolean dirty() {
        return dirty;
    }

    public String runMode() {
        return runMode;
    }

    public String describe() {
        return version + "+" + commit + (dirty ? "-dirty" : "");
    }
}
