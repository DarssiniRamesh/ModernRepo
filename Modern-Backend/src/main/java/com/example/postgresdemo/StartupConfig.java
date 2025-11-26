package com.example.postgresdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

/**
 * PUBLIC_INTERFACE
 * StartupConfig logs key runtime configuration once the application is ready.
 * It reports:
 * - Active Spring profiles
 * - Datasource URL target (without credentials)
 * - Hibernate DDL auto mode
 *
 * This helps verify binding to port 3000 and DB connection target (postgres-db:5432 or localhost:5000).
 */
@Configuration
public class StartupConfig {

    private static final Logger log = LoggerFactory.getLogger(StartupConfig.class);

    private final Environment environment;

    @Value("${server.port:3000}")
    private String serverPort;

    @Value("${spring.datasource.url:}")
    private String datasourceUrl;

    @Value("${spring.jpa.hibernate.ddl-auto:}")
    private String ddlAuto;

    public StartupConfig(Environment environment) {
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void logRuntimeConfig() {
        String[] profiles = environment.getActiveProfiles();
        String profilesStr = String.join(",", profiles);
        // Sanitize datasource URL by removing credentials if present (e.g., jdbc:postgresql://user:pass@host:port/db)
        String safeUrl = sanitizeJdbcUrl(datasourceUrl);
        log.info("ModernRepo is up on port {} (server.address=0.0.0.0). Active profiles: [{}]", serverPort, profilesStr);
        log.info("Datasource URL target: {}", safeUrl.isEmpty() ? "(not set; dev profile H2 likely in use)" : safeUrl);
        log.info("Hibernate DDL auto: {}", ddlAuto.isEmpty() ? "(not set; default may apply)" : ddlAuto);
    }

    private String sanitizeJdbcUrl(String url) {
        if (url == null) return "";
        // Basic scrub: jdbc:postgresql://user:pass@host:port/db -> jdbc:postgresql://host:port/db
        // Only handle postgres scheme commonly used here.
        int schemeIdx = url.indexOf("jdbc:postgresql://");
        if (schemeIdx != 0) {
            return url;
        }
        String rest = url.substring("jdbc:postgresql://".length());
        int atIdx = rest.indexOf('@');
        if (atIdx > 0) {
            rest = rest.substring(atIdx + 1);
        }
        return "jdbc:postgresql://" + rest;
    }
}
