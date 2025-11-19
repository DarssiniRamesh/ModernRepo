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
 */
@Configuration
public class CorsConfig {

    /**
     * PUBLIC_INTERFACE
     * Creates a CorsFilter bean with global CORS configuration.
     * This allows Swagger UI and other web clients to make cross-origin requests.
     * Uses allowedOriginPatterns to support preview proxies with varying origins.
     * 
     * @return CorsFilter configured with allowed origins, methods, and headers
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials set to false for broader compatibility
        config.setAllowCredentials(false);
        
        // Allow all origin patterns to support preview proxies
        config.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // Allow all standard HTTP methods including OPTIONS for preflight
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Allow Authorization and Content-Type headers as required
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", 
                                               "Origin", "X-Requested-With", 
                                               "Access-Control-Request-Method", 
                                               "Access-Control-Request-Headers"));
        
        // Expose headers that clients may need to access
        config.setExposedHeaders(Arrays.asList("Access-Control-Allow-Origin", 
                                               "Access-Control-Allow-Credentials",
                                               "Content-Type",
                                               "Authorization"));
        
        // Cache preflight response for 1 hour (3600 seconds)
        config.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply CORS configuration to all paths
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
