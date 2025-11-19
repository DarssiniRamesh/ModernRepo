package com.example.postgresdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS).
 * This configuration allows the Swagger UI and other clients from the preview domain
 * to access the API endpoints without CORS errors.
 */
@Configuration
public class CorsConfig {

    /**
     * PUBLIC_INTERFACE
     * Creates a CORS filter bean that handles all CORS requests.
     * This filter will be applied to all endpoints in the application.
     * 
     * @return CorsFilter configured with allowed origins, methods, and headers
     */
    // PUBLIC_INTERFACE
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);
        
        // Allow the preview origin and local development origins
        config.setAllowedOriginPatterns(Arrays.asList(
                "https://vscode-internal-36492-beta.beta01.cloud.kavia.ai:*",
                "http://localhost:*"
        ));
        
        // Allow all common HTTP methods
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // Allow all common headers
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        
        // Expose headers that the client can access
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        
        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);
        
        // Apply CORS configuration to all paths
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
