package com.example.pawinder_server.controllers;

import com.example.pawinder_server.config.AppVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** GET /api/version: позволяет по ответу сервера определить версию исходного кода. */
@RestController
@RequestMapping("/api/version")
public class VersionController {

    private final AppVersion appVersion;

    public VersionController(AppVersion appVersion) {
        this.appVersion = appVersion;
    }

    @GetMapping
    public Map<String, Object> version() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("version", appVersion.describe());
        result.put("commit", appVersion.commit());
        result.put("dirty", appVersion.dirty());
        result.put("runMode", appVersion.runMode());
        return result;
    }
}
