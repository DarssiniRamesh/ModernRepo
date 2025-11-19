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
 * Configures the API metadata displayed in Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    /**
     * PUBLIC_INTERFACE
     * Configures the OpenAPI specification with application metadata.
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
