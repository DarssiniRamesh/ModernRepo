package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

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
     * Sets server URLs with relative path as primary (inherits current host:port)
     * and explicit preview server URL with :3001 port as fallback.
     * This ensures Swagger UI 'Try it out' always uses the correct port.
     * Accessible at /v3/api-docs and /swagger-ui.html
     */
    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("ModernRepo Question & Answer API")
                .version("1.0.0")
                .description("RESTful API for managing questions and answers using Spring Boot, PostgreSQL, JPA, and Hibernate."));
        
        // Configure servers list for Swagger UI
        // Order matters: relative path first ensures Swagger UI uses current origin with correct port
        List<Server> servers = new ArrayList<>();
        
        // Primary server: relative path "/" - Swagger UI will inherit current host and port
        // This resolves issues where Swagger strips port or uses wrong host
        // Example: If accessed via http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001/swagger-ui.html
        // All API calls will automatically use http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001
        Server relativeServer = new Server();
        relativeServer.setUrl("/");
        relativeServer.setDescription("Current origin (auto-detects host:port)");
        servers.add(relativeServer);
        
        // Secondary server: explicit preview environment URL with :3001 port
        // This ensures the port :3001 is never dropped and provides a working fallback
        // IMPORTANT: This URL must include :3001 port explicitly to prevent Swagger from dropping it
        Server previewServer = new Server();
        previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001");
        previewServer.setDescription("Preview environment (explicit :3001)");
        servers.add(previewServer);
        
        // Note: No server entry without :3001 port should exist to prevent Swagger from using wrong URL
        
        openAPI.setServers(servers);
        return openAPI;
    }

    // PUBLIC_INTERFACE
    /**
     * Configures CORS to allow all origins, methods, and headers for development/testing.
     * Ensures Swagger UI origin can make requests to API endpoints without CORS errors.
     * In production, customize allowedOriginPatterns to restrict access to specific domains.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOriginPatterns("*") // Allow all origins for development
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("*") // Expose all response headers
                    .allowCredentials(false) // False to avoid CORS credential conflicts with wildcard origin
                    .maxAge(3600);
            }
        };
    }
}
