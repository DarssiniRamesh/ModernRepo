package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * Configuration for global CORS and OpenAPI documentation.
 * Ensures Swagger UI can properly communicate with the API across different environments.
 * Configures server list to use relative path as primary, ensuring correct port usage.
 */
@Configuration
public class OpenApiConfig {

    // PUBLIC_INTERFACE
    /**
     * Configures OpenAPI documentation for the REST API.
     * CRITICAL: Sets server URLs with ONLY two entries:
     * 1. Relative path "/" (inherits current host:port automatically)
     * 2. Explicit preview server URL with :3001 port
     * NO host-only entries without port are permitted.
     * This ensures Swagger UI 'Try it out' always uses the correct port.
     * Accessible at /v3/api-docs and /swagger-ui.html
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Server 1: Relative path - Swagger UI inherits current origin with correct port
        Server relativeServer = new Server();
        relativeServer.setUrl("/");
        relativeServer.setDescription("Same origin (inherits current host:port)");
        
        // Server 2: Explicit preview URL with :3001 port - Never drop the port number
        Server previewServer = new Server();
        previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001");
        previewServer.setDescription("Preview environment with port 3001");
        
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("ModernRepo Question & Answer API")
                .version("1.0.0")
                .description("RESTful API for managing questions and answers using Spring Boot, PostgreSQL, JPA, and Hibernate."))
            .servers(Arrays.asList(relativeServer, previewServer));
        
        return openAPI;
    }

    // PUBLIC_INTERFACE
    /**
     * Configures CORS to allow all origins, methods, and headers for development/testing.
     * Ensures Swagger UI origin can make requests to API endpoints without CORS errors.
     * Includes explicit OPTIONS method support for preflight requests.
     * In production, customize allowedOriginPatterns to restrict access to specific domains.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
