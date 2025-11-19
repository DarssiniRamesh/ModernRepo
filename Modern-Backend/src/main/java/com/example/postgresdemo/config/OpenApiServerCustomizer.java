package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Customizes OpenAPI server configuration at runtime based on incoming request headers.
 * This component intercepts OpenAPI document generation and dynamically sets the server URL
 * based on X-Forwarded headers, ensuring Swagger UI "Try it out" uses the external preview URL.
 * 
 * This approach guarantees that every OpenAPI request (including /v3/api-docs and /swagger-config)
 * receives the correct server URL based on the actual request context.
 */
@Component
public class OpenApiServerCustomizer implements OpenApiCustomiser {

    /**
     * PUBLIC_INTERFACE
     * Customizes the OpenAPI object by setting the server URL based on forwarded headers.
     * Called automatically by Springdoc when generating the OpenAPI specification.
     * 
     * @param openApi The OpenAPI object to customize
     */
    @Override
    public void customise(OpenAPI openApi) {
        String serverUrl = deriveServerUrlFromForwardedHeaders();
        
        if (serverUrl != null) {
            // Set the server URL derived from forwarded headers
            Server server = new Server();
            server.setUrl(serverUrl);
            server.setDescription("Server URL (auto-detected from request headers)");
            
            List<Server> servers = new ArrayList<>();
            servers.add(server);
            openApi.setServers(servers);
        }
        // If no forwarded headers, leave servers as-is (relative paths will be used)
    }

    /**
     * Derives the external server URL from X-Forwarded headers in the current request.
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
                        try {
                            int port = Integer.parseInt(forwardedPort);
                            boolean isStandardPort = 
                                ("http".equals(forwardedProto) && port == 80) ||
                                ("https".equals(forwardedProto) && port == 443);
                            
                            if (!isStandardPort) {
                                serverUrl.append(":").append(port);
                            }
                        } catch (NumberFormatException e) {
                            // If port is not a valid number, skip it
                            System.err.println("Invalid X-Forwarded-Port value: " + forwardedPort);
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
