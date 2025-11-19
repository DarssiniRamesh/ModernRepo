package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PUBLIC_INTERFACE
 * OpenAPI configuration for Swagger UI documentation.
 * Configures the API metadata with relative URLs for compatibility across all environments.
 * Relies on Springdoc's automatic server URL resolution through application properties.
 */
@Configuration
public class OpenApiConfig {

    /**
     * PUBLIC_INTERFACE
     * Configures the OpenAPI specification with application metadata.
     * Uses relative URLs and lets the framework handle server URL resolution
     * based on the actual request context and X-Forwarded headers.
     * 
     * @return OpenAPI configuration with title, description, version, and contact info
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ModernRepo REST API")
                        .version("1.0.0")
                        .description("Spring Boot REST API for managing questions and answers. " +
                                "This API provides CRUD operations for questions and their associated answers, " +
                                "with pagination support and validation.")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@modernrepo.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
        // No explicit servers - Springdoc will automatically resolve based on the actual request
        // and X-Forwarded headers configured in application.properties
    }
}
