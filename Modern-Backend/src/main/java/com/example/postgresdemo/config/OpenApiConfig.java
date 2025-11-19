package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for global CORS and OpenAPI documentation.
 * Ensures Swagger UI can properly communicate with the API across different environments.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:3001}")
    private String serverPort;

    @Value("${api.base.url:}")
    private String apiBaseUrl;

    // PUBLIC_INTERFACE
    /**
     * Configures OpenAPI documentation for the REST API.
     * Dynamically sets server URLs based on environment configuration.
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
        List<Server> servers = new ArrayList<>();
        
        // If API_BASE_URL is set (preview environment), use it
        if (apiBaseUrl != null && !apiBaseUrl.isEmpty() && !apiBaseUrl.equals("${api.base.url:}")) {
            Server previewServer = new Server();
            previewServer.setUrl(apiBaseUrl);
            previewServer.setDescription("Preview/Production server");
            servers.add(previewServer);
        }
        
        // Always add localhost option for local development
        Server localServer = new Server();
        localServer.setUrl("https://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001");
        localServer.setDescription("Local development server");
        servers.add(localServer);
        
        // Add relative path server (same origin) - this resolves CORS issues in preview environments
        Server relativeServer = new Server();
        relativeServer.setUrl("");
        relativeServer.setDescription("Same origin (current host)");
        servers.add(0, relativeServer); // Add as first option
        
        openAPI.setServers(servers);
        return openAPI;
    }

    // PUBLIC_INTERFACE
    /**
     * Configures CORS to allow all origins, methods, and headers for development/testing.
     * Ensures Swagger UI origin can make requests to API endpoints.
     * In production, customize allowedOrigins to restrict access.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOriginPatterns("*") // Allow all origins with credentials
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .exposedHeaders("*") // Expose all response headers
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
