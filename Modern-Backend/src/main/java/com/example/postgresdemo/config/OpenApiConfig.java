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
        
        // CRITICAL: Only two server entries allowed:
        // 1. Relative path "/" - Swagger UI inherits current host and port automatically
        // 2. Explicit preview URL with :3001 port - Never drop the port number
        // NO host-only entries (e.g., http://host without :3001) are permitted
        
        Server relativeServer = new Server();
        relativeServer.setUrl("/");
        relativeServer.setDescription("Same origin");
        servers.add(relativeServer);
        
        Server previewServer = new Server();
        previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001");
        previewServer.setDescription("Preview 3001");
        servers.add(previewServer);
        
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
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("*")
                    .allowCredentials(false)
                    .maxAge(3600);
            }
        };
    }
}
