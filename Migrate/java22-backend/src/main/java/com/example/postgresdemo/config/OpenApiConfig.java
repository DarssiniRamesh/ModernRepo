package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI/Swagger documentation.
 * This class configures the API documentation metadata that will be displayed
 * in the Swagger UI interface.
 */
@Configuration
public class OpenApiConfig {

    /**
     * PUBLIC_INTERFACE
     * Creates and configures the OpenAPI bean with API metadata.
     * 
     * @return OpenAPI object with configured API information
     */
    // PUBLIC_INTERFACE
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PostgreSQL Questions & Answers API")
                        .version("1.0.0")
                        .description("RESTful API for managing questions and answers with PostgreSQL, JPA, and Hibernate. " +
                                     "This API provides CRUD operations for questions and their associated answers, " +
                                     "with pagination support and validation.")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
