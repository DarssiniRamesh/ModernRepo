package com.example.postgresdemo;

// See DbStartupConnectivityProbe for startup connectivity logging.

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

/**
 * Spring Boot Application entrypoint for ModernRepo.
 * Performs startup checks for PostgreSQL connectivity and secure parameter logging.
 */
@SpringBootApplication
@EnableJpaAuditing
public class PostgresDemoApplication {

    private static final Logger logger = LoggerFactory.getLogger(PostgresDemoApplication.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Autowired
    private DataSource dataSource;

    /**
     * Startup method to log effective datasource configuration,
     * prints JDBC URL with credentials obfuscated, and checks DB connectivity.
     */
    @PostConstruct
    // PUBLIC_INTERFACE
    public void startupConnectivityCheck() {
        String obfuscatedPassword = dbPassword == null ? "(none)" : "******";
        // Log effective JDBC URL, hiding credentials (not present in this config but for future-proof)
        String urlToLog = datasourceUrl;
        if (urlToLog != null && urlToLog.contains("://")) {
            int at = urlToLog.indexOf('@');
            if (at >= 0) {
                int schemeSplit = urlToLog.indexOf("://") + 3;
                int pathIdx = urlToLog.indexOf('/', schemeSplit);
                if (pathIdx > schemeSplit && at < pathIdx) {
                    urlToLog = urlToLog.substring(0, schemeSplit) + "****:****@" + urlToLog.substring(at + 1);
                }
            }
        }
        logger.info(">>> [ModernRepo] EFFECTIVE JDBC URL (no credentials): '{}'", (urlToLog == null ? "(not set)" : urlToLog));
        logger.info(">>> [ModernRepo] EFFECTIVE DB user: '{}'", dbUser);

        // Actual DB connectivity check and retry logic
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            if (rs.next() && rs.getInt(1) == 1) {
                logger.info(">>> [ModernRepo] PostgreSQL connection SUCCESSFUL (SELECT 1 passed).");
            } else {
                logger.error(">>> [ModernRepo] PostgreSQL connection test failed: SELECT 1 did not return 1.");
            }
        } catch (SQLException e) {
            logger.error(">>> [ModernRepo] PostgreSQL connection test FAILED:\n  Message: {} \n  SQLState: {} \n  ErrorCode: {}", e.getMessage(), e.getSQLState(), e.getErrorCode(), e);
            // Retry on protocol/SSL error
            if (e.getSQLState() != null &&
                ("08004".equals(e.getSQLState()) || "08006".equals(e.getSQLState()) || e.getMessage().contains("Protocol error") || e.getMessage().contains("Session setup failed"))) {
                logger.warn(">>> [ModernRepo] Protocol-level or SSL negotiation error detected during SELECT 1. Retrying connection using the same DataSource...");
                try (Connection conn2 = dataSource.getConnection();
                     Statement stmt2 = conn2.createStatement();
                     ResultSet rs2 = stmt2.executeQuery("SELECT 1")) {
                    if (rs2.next() && rs2.getInt(1) == 1) {
                        logger.info(">>> [ModernRepo] Retry succeeded: PostgreSQL connection (SELECT 1) passed on retry.");
                    } else {
                        logger.error(">>> [ModernRepo] Retry also failed: SELECT 1 did not return 1.");
                    }
                } catch (SQLException ex2) {
                    logger.error(">>> [ModernRepo] Retry for PostgreSQL connection also FAILED:\n  Message: {} \n  SQLState: {} \n  ErrorCode: {}", ex2.getMessage(), ex2.getSQLState(), ex2.getErrorCode(), ex2);
                }
            }
            throw new IllegalStateException("Failed to connect to PostgreSQL database using supplied JDBC URL and credentials. See logs above.", e);
        }
    }

    /**
     * Main entrypoint.
     */
    // PUBLIC_INTERFACE
    public static void main(String[] args) {
        SpringApplication.run(PostgresDemoApplication.class, args);
    }
}
