package com.example.postgresdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * PUBLIC_INTERFACE
 * Global CORS configuration for the application.
 * Configures Cross-Origin Resource Sharing (CORS) to allow Swagger UI and other clients
 * to make requests from different origins.
 */
@Configuration
public class CorsConfig {

    /**
     * PUBLIC_INTERFACE
     * Creates a CorsFilter bean with global CORS configuration.
     * This allows Swagger UI and other web clients to make cross-origin requests.
     * 
     * @return CorsFilter configured with allowed origins, methods, and headers
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (set to false for public APIs)
        config.setAllowCredentials(false);
        
        // Allow all origin patterns (can be restricted in production)
        config.addAllowedOriginPattern("*");
        
        // Allow common HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Allow common headers including Authorization and Content-Type
        config.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization", "X-Requested-With", 
                                               "Accept", "Origin", "Access-Control-Request-Method", 
                                               "Access-Control-Request-Headers"));
        
        // Expose common response headers
        config.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", 
                                               "Access-Control-Allow-Credentials"));
        
        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS configuration to all paths
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
