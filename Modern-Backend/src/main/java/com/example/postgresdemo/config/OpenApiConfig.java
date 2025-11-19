package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PUBLIC_INTERFACE
 * OpenAPI configuration for Swagger UI documentation.
 * Configures the API metadata and server URLs displayed in Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:3001}")
    private String serverPort;

    @Value("${openapi.server.url:http://localhost:${server.port}}")
    private String serverUrl;

    /**
     * PUBLIC_INTERFACE
     * Configures the OpenAPI specification with application metadata and server URLs.
     * Sets the correct base URL so Swagger UI makes requests to the right endpoint.
     * 
     * @return OpenAPI configuration with title, description, version, contact info, and server URL
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Construct server URL from port if not explicitly set
        String baseUrl = serverUrl;
        if (baseUrl.contains("${server.port}")) {
            baseUrl = "http://localhost:" + serverPort;
        }
        
        Server server = new Server()
                .url(baseUrl)
                .description("ModernRepo API Server");
        
        return new OpenAPI()
                .addServersItem(server)
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
    }
}
