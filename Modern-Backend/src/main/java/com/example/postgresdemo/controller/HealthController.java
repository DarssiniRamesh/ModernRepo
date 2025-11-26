package com.example.postgresdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * PUBLIC_INTERFACE
 * HealthController exposes health endpoints, including a PostgreSQL connectivity check.
 *
 * Endpoints:
 * - GET /health/db
 *   Performs a lightweight query (SELECT 1) using the configured DataSource.
 *   Returns JSON with connection status and optional error details.
 */
@RestController
@RequestMapping("/health")
@Tag(name = "Health", description = "Operational and database connectivity checks")
public class HealthController {

    private final DataSource dataSource;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // PUBLIC_INTERFACE
    @GetMapping(value = "/db", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Database health check",
            description = "Pings the configured PostgreSQL database with a lightweight validation query. " +
                    "Returns connection status and basic timing information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Database reachable",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = HealthResponse.class))),
                    @ApiResponse(responseCode = "503", description = "Database not reachable",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = HealthResponse.class)))
            },
            tags = { "Health" }
    )
    public ResponseEntity<Map<String, Object>> dbHealth() {
        long started = System.currentTimeMillis();
        Map<String, Object> body = new HashMap<>();
        body.put("component", "postgres");
        body.put("profile", activeProfile);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {

            boolean ok = rs.next() && rs.getInt(1) == 1;
            long tookMs = System.currentTimeMillis() - started;

            body.put("status", ok ? "UP" : "DEGRADED");
            body.put("tookMs", tookMs);
            return ResponseEntity.ok(body);
        } catch (Exception ex) {
            long tookMs = System.currentTimeMillis() - started;
            body.put("status", "DOWN");
            body.put("tookMs", tookMs);
            body.put("error", ex.getClass().getSimpleName());
            body.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
        }
    }

    /**
     * Simple DTO schema definition for OpenAPI docs.
     * Note: Response is actually built as a Map, but this class documents the shape.
     */
    static class HealthResponse {
        public String component;
        public String profile;
        public String status;
        public Long tookMs;
        public String error;
        public String message;
    }
}
