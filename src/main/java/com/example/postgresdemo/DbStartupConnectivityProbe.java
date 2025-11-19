package com.example.postgresdemo;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Application startup probe to log the effective JDBC URL (with credentials hidden)
 * and run a connectivity check using SELECT 1 on the configured DataSource.
 * Logs success or failure at INFO level. Never aborts application startup.
 */
@Configuration
public class DbStartupConnectivityProbe {

    private static final Logger logger = LoggerFactory.getLogger(DbStartupConnectivityProbe.class);

    @Bean
    // PUBLIC_INTERFACE
    public ApplicationRunner dbStartupProbe(DataSource dataSource) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) {
                String jdbcUrl = null;
                String username = null;
                try {
                    if (dataSource instanceof HikariDataSource) {
                        HikariDataSource hikari = (HikariDataSource) dataSource;
                        jdbcUrl = hikari.getJdbcUrl();
                        username = hikari.getUsername();
                    } else {
                        // Fallback: try DriverManager properties via toString (won't reveal passwords)
                        jdbcUrl = dataSource.toString();
                    }
                } catch (Exception ignored) {
                    jdbcUrl = "(could not resolve)";
                }
                String safeJdbcUrl = maskJdbcUrl(jdbcUrl);
                logger.info(">>> [ModernRepo] DB Connectivity Startup Probe");
                logger.info(">>> [ModernRepo] EFFECTIVE JDBC URL (no credentials): '{}'", safeJdbcUrl);
                if (username != null) logger.info(">>> [ModernRepo] EFFECTIVE DB user: '{}'", username);
                // Connectivity check
                try (Connection conn = dataSource.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT 1")) {
                    if (rs.next() && rs.getInt(1) == 1) {
                        logger.info(">>> [ModernRepo] DB connectivity probe: SUCCESS (SELECT 1 OK)");
                    } else {
                        logger.info(">>> [ModernRepo] DB connectivity probe: FAILED (SELECT 1 did NOT return 1)");
                    }
                } catch (SQLException e) {
                    logger.info(">>> [ModernRepo] DB connectivity probe: FAILED - {}", e.getMessage());
                } catch (Exception ex) {
                    logger.info(">>> [ModernRepo] DB connectivity probe: Failed with non-SQL error: {}", ex.getMessage());
                }
            }
        };
    }

    /**
     * Masks credentials in JDBC URLs for safe logging—leaves host, port, and DB name.
     * Examples:
     *   jdbc:postgresql://user:pass@host:3020/db?sslmode=disable  ==> jdbc:postgresql://****:****@host:3020/db?sslmode=disable
     *   jdbc:postgresql://host:3020/db?sslmode=disable           ==> unchanged (no credentials found)
     */
    // PUBLIC_INTERFACE
    public static String maskJdbcUrl(String url) {
        if (url == null) return "(not set)";
        int protocolIdx = url.indexOf("://");
        if (protocolIdx < 0) return url;
        int atIdx = url.indexOf('@', protocolIdx + 3);
        if (atIdx > 0) {
            int credsStart = protocolIdx + 3;
            // Only mask if there's a ':' between protocol and @ (user:pass@host)
            int colonIdx = url.indexOf(':', credsStart);
            int passEnd = url.indexOf('@');
            if (colonIdx > 0 && colonIdx < passEnd) {
                // Mask "user:pass@"
                return url.substring(0, credsStart) + "****:****" + url.substring(atIdx);
            } else {
                // Mask anything up to '@'
                return url.substring(0, credsStart) + "****@" + url.substring(atIdx + 1);
            }
        }
        return url;
    }
}
