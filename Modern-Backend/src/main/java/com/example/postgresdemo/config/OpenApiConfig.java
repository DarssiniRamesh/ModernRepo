package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

/**
 * PUBLIC_INTERFACE
 * OpenAPI configuration for Swagger UI documentation.
 * Configures the API metadata dynamically based on the current request context.
 * Uses relative URLs to work correctly behind proxies and preview environments.
 */
@Configuration
public class OpenApiConfig {

    /**
     * PUBLIC_INTERFACE
     * Configures the OpenAPI specification with application metadata.
     * Does not set hardcoded server URLs to allow Swagger UI to use relative paths,
     * which is essential for working correctly behind proxies and preview environments.
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
    }
}
