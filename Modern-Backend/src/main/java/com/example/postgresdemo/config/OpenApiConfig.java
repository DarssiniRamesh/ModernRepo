package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * PUBLIC_INTERFACE
 * OpenAPI configuration for Swagger UI documentation.
 * Configures the API metadata dynamically based on the current request context.
 * Uses X-Forwarded headers to construct the correct server URL for preview environments.
 */
@Configuration
public class OpenApiConfig {

    /**
     * PUBLIC_INTERFACE
     * Configures the OpenAPI specification with application metadata.
     * Dynamically resolves server URL from X-Forwarded-Proto and X-Forwarded-Host headers
     * if present (preview/proxy environments), otherwise omits servers to use relative paths.
     * 
     * @return OpenAPI configuration with title, description, version, contact info, and dynamic server URL
     */
    @Bean
    public OpenAPI customOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
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

        // Attempt to dynamically resolve server URL from forwarded headers
        String serverUrl = resolveServerUrl();
        if (serverUrl != null && !serverUrl.isEmpty()) {
            openAPI.addServersItem(new Server()
                    .url(serverUrl)
                    .description("Current environment server (resolved from forwarded headers)"));
        }
        // If no forwarded headers, omit servers to keep relative paths

        return openAPI;
    }

    /**
     * Resolves the server URL from X-Forwarded-Proto and X-Forwarded-Host headers.
     * This is used in preview/proxy environments where the external URL differs from the internal one.
     * 
     * @return the resolved server URL, or null if headers are not present
     */
    private String resolveServerUrl() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                // Check for X-Forwarded headers (set by proxy/preview environments)
                String forwardedProto = request.getHeader("X-Forwarded-Proto");
                String forwardedHost = request.getHeader("X-Forwarded-Host");
                String forwardedPort = request.getHeader("X-Forwarded-Port");
                
                if (forwardedProto != null && forwardedHost != null) {
                    // Build URL from forwarded headers
                    StringBuilder urlBuilder = new StringBuilder();
                    urlBuilder.append(forwardedProto).append("://").append(forwardedHost);
                    
                    // Add port if present and not standard for the protocol
                    if (forwardedPort != null && !forwardedPort.isEmpty()) {
                        int port = Integer.parseInt(forwardedPort);
                        boolean isStandardPort = 
                            ("http".equals(forwardedProto) && port == 80) ||
                            ("https".equals(forwardedProto) && port == 443);
                        
                        if (!isStandardPort) {
                            urlBuilder.append(":").append(port);
                        }
                    }
                    
                    return urlBuilder.toString();
                }
            }
        } catch (Exception e) {
            // Silently handle any errors - will fall back to relative URLs
            System.err.println("Failed to resolve server URL from forwarded headers: " + e.getMessage());
        }
        
        return null;
    }
}
