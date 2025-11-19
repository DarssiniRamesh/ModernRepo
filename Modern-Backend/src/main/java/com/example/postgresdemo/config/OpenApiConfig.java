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
import java.util.Collections;

/**
 * PUBLIC_INTERFACE
 * OpenAPI configuration for Swagger UI documentation.
 * Dynamically resolves server URL from X-Forwarded headers to support preview/proxy environments.
 * When X-Forwarded-Proto and X-Forwarded-Host are present, constructs the external base URL.
 * Otherwise, omits servers to use relative paths for local development.
 */
@Configuration
public class OpenApiConfig {

    /**
     * PUBLIC_INTERFACE
     * Configures the OpenAPI specification with application metadata and dynamic server URL.
     * The server URL is derived from X-Forwarded-Proto, X-Forwarded-Host, and X-Forwarded-Port headers
     * when available (preview/proxy environments), ensuring Swagger UI "Try it out" uses the correct base URL.
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

        // Try to derive server URL from X-Forwarded headers
        String serverUrl = deriveServerUrlFromForwardedHeaders();
        if (serverUrl != null) {
            Server server = new Server();
            server.setUrl(serverUrl);
            server.setDescription("External server URL (derived from X-Forwarded headers)");
            openAPI.servers(Collections.singletonList(server));
        }
        // If no forwarded headers, omit servers - Springdoc will use relative paths

        return openAPI;
    }

    /**
     * Derives the external server URL from X-Forwarded headers.
     * Checks for X-Forwarded-Proto, X-Forwarded-Host, and optionally X-Forwarded-Port.
     * 
     * @return The constructed server URL (e.g., "https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001")
     *         or null if forwarded headers are not present
     */
    private String deriveServerUrlFromForwardedHeaders() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                String forwardedProto = request.getHeader("X-Forwarded-Proto");
                String forwardedHost = request.getHeader("X-Forwarded-Host");
                String forwardedPort = request.getHeader("X-Forwarded-Port");
                
                // If we have both proto and host, construct the URL
                if (forwardedProto != null && forwardedHost != null) {
                    StringBuilder serverUrl = new StringBuilder();
                    serverUrl.append(forwardedProto).append("://").append(forwardedHost);
                    
                    // Add port if present and not standard (80 for HTTP, 443 for HTTPS)
                    if (forwardedPort != null) {
                        int port = Integer.parseInt(forwardedPort);
                        boolean isStandardPort = 
                            ("http".equals(forwardedProto) && port == 80) ||
                            ("https".equals(forwardedProto) && port == 443);
                        
                        if (!isStandardPort) {
                            serverUrl.append(":").append(port);
                        }
                    }
                    
                    return serverUrl.toString();
                }
            }
        } catch (Exception e) {
            // If we can't derive the URL, return null to use relative paths
            System.err.println("Could not derive server URL from forwarded headers: " + e.getMessage());
        }
        
        return null;
    }
}
