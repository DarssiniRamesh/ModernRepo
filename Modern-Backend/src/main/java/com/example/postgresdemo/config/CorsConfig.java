package com.example.postgresdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

/**
 * PUBLIC_INTERFACE
 * Global CORS configuration for the application.
 * Configures Cross-Origin Resource Sharing (CORS) to allow Swagger UI and other clients
 * to make requests from different origins, including preview proxy environments.
 * 
 * To add additional allowed origins for production deployments, update the
 * setAllowedOrigins list in the corsFilter() method below.
 */
@Configuration
public class CorsConfig {

    /**
     * PUBLIC_INTERFACE
     * Creates a CorsFilter bean with global CORS configuration.
     * This allows Swagger UI and other web clients to make cross-origin requests.
     * 
     * Configuration includes:
     * - Explicit allowed origins for preview/production environments
     * - Wildcard origin patterns for local development
     * - Standard HTTP methods (GET, POST, PUT, PATCH, DELETE, OPTIONS)
     * - Common headers (Authorization, Content-Type, Accept)
     * - Exposed headers (Location, Link) for REST operations
     * - Preflight cache duration of 1 hour (3600 seconds)
     * 
     * @return CorsFilter configured with allowed origins, methods, and headers
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials set to false for broader compatibility with strict browsers
        config.setAllowCredentials(false);
        
        // Explicitly allow preview domain origin for strict browser CORS enforcement
        // Add additional production origins here as needed
        config.setAllowedOrigins(Arrays.asList(
            "https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001"
        ));
        
        // Keep wildcard pattern for local development (localhost variations)
        // This works alongside allowedOrigins for non-strict scenarios
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Allow standard HTTP methods including OPTIONS for preflight requests
        config.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Allow common request headers
        config.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept",
            "Origin", 
            "X-Requested-With", 
            "Access-Control-Request-Method", 
            "Access-Control-Request-Headers"
        ));
        
        // Expose headers that clients may need to access in responses
        // Location: used for created resource URLs
        // Link: used for pagination and HATEOAS
        config.setExposedHeaders(Arrays.asList(
            "Location", 
            "Link",
            "Access-Control-Allow-Origin", 
            "Access-Control-Allow-Credentials",
            "Content-Type",
            "Authorization"
        ));
        
        // Cache preflight response for 1 hour (3600 seconds)
        // This reduces OPTIONS request overhead
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS configuration to all paths including controllers and Swagger routes
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
