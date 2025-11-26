package com.example.postgresdemo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * PUBLIC_INTERFACE
 * OpenAPI configuration for the ModernRepo application.
 * This sets top-level API metadata and ensures Swagger UI is available.
 *
 * Paths:
 * - Swagger UI: /docs
 * - OpenAPI JSON: /openapi.json
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "ModernRepo API",
                version = "1.0.0",
                description = "Spring Boot REST API for managing questions and answers with PostgreSQL connectivity.",
                contact = @Contact(name = "ModernRepo Team", email = "support@modernrepo.local")
        ),
        servers = {
                @Server(url = "/", description = "Default Server")
        },
        tags = {
                @Tag(name = "Questions", description = "CRUD endpoints for Questions"),
                @Tag(name = "Answers", description = "CRUD endpoints for Answers"),
                @Tag(name = "Health", description = "Operational and database connectivity checks")
        }
)
@SecurityScheme(
        name = "none",
        scheme = "none",
        type = io.swagger.v3.oas.annotations.enums.SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // No additional beans required for basic setup using springdoc-openapi
}
