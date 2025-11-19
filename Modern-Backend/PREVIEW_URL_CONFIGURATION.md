# Preview URL Configuration

## Overview

This Spring Boot application is configured to work correctly in preview/proxy environments where the external URL differs from the internal server URL. The application uses forwarded headers to detect the external base URL and configure Swagger/OpenAPI accordingly.

## How It Works

### Forwarded Headers Strategy

The application uses Spring Boot's built-in forwarded headers support to detect the external URL:

1. **Header Detection**: When requests pass through a proxy or preview environment, the proxy sets standard `X-Forwarded-*` headers:
   - `X-Forwarded-Proto`: The original protocol (http/https)
   - `X-Forwarded-Host`: The external hostname
   - `X-Forwarded-Port`: The external port (if non-standard)
   - `X-Forwarded-For`: The original client IP

2. **Server Configuration**: The application is configured with:
   ```properties
   server.forward-headers-strategy=framework
   server.tomcat.remoteip.remote-ip-header=x-forwarded-for
   server.tomcat.remoteip.protocol-header=x-forwarded-proto
   ```

3. **Dynamic Server URL Resolution**: The `OpenApiConfig` class dynamically builds the OpenAPI server URL from forwarded headers:
   - If `X-Forwarded-Proto` and `X-Forwarded-Host` are present, it constructs the full external URL
   - If headers are absent (local development), it omits the server URL, allowing Swagger UI to use relative paths

### Preview Environment Example

For a preview environment like:
```
https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001
```

The proxy will set:
- `X-Forwarded-Proto: https`
- `X-Forwarded-Host: vscode-internal-32563-beta.beta01.cloud.kavia.ai`
- `X-Forwarded-Port: 3001`

The application will:
1. Detect these headers via the forwarded headers strategy
2. Build the server URL: `https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001`
3. Configure OpenAPI to use this URL for the "Try it out" feature in Swagger UI

### Swagger UI Configuration

Swagger UI is configured to use relative URLs:
```properties
springdoc.swagger-ui.config-url=/v3/api-docs/swagger-config
springdoc.swagger-ui.url=/v3/api-docs
```

This ensures that:
- API documentation is fetched from the correct base URL
- "Try it out" requests use the external URL (not localhost)
- No hardcoded URLs prevent usage in different environments

## CORS Configuration

The application uses permissive CORS settings to allow Swagger UI to function in preview environments:

```java
config.setAllowedOriginPatterns(Collections.singletonList("*"));
config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
```

This allows cross-origin requests from any preview domain while maintaining security through other mechanisms.

## Local Development

When running locally (without a proxy), the application will:
- Not detect any `X-Forwarded-*` headers
- Omit the server URL in OpenAPI configuration
- Use relative paths for all API requests
- Work correctly at `http://localhost:3001`

## Verification

To verify the configuration is working:

1. **Access Swagger UI**: Navigate to `https://your-preview-url/swagger-ui.html`
2. **Check Server URL**: The Swagger UI should show the correct external URL in the "Servers" dropdown
3. **Test API**: Use "Try it out" on any endpoint - requests should go to the external URL, not localhost

## Troubleshooting

### Issue: Swagger UI shows localhost URLs

**Cause**: Forwarded headers are not being set by the proxy

**Solution**: Verify that the proxy/preview environment is configured to set `X-Forwarded-Proto` and `X-Forwarded-Host` headers

### Issue: Failed to fetch errors

**Cause**: CORS or mixed content issues

**Solution**: 
- Ensure the proxy is using HTTPS consistently
- Check browser console for CORS errors
- Verify `server.forward-headers-strategy=framework` is set in application properties

### Issue: API requests go to wrong URL

**Cause**: Hardcoded server URLs in configuration

**Solution**: Remove any `OPENAPI_SERVER_URL` environment variables or `springdoc.swagger-ui.urls[]` properties - the application should auto-detect from headers

## Configuration Files

Key configuration files for preview URL support:

1. **application.properties**: Base configuration with forwarded headers strategy and Springdoc settings
2. **application-dev.properties**: Development profile (uses forwarded headers)
3. **application-prod.properties**: Production profile (uses forwarded headers)
4. **OpenApiConfig.java**: Base OpenAPI configuration with metadata
5. **OpenApiServerCustomizer.java**: Runtime server URL resolution from X-Forwarded headers
6. **CorsConfig.java**: Permissive CORS for preview environments

### Key Implementation: OpenApiServerCustomizer

The `OpenApiServerCustomizer` class is the core component that ensures Swagger UI uses the external preview URL:

```java
@Component
public class OpenApiServerCustomizer implements OpenApiCustomiser {
    @Override
    public void customise(OpenAPI openApi) {
        String serverUrl = deriveServerUrlFromForwardedHeaders();
        if (serverUrl != null) {
            Server server = new Server();
            server.setUrl(serverUrl);
            openApi.setServers(Collections.singletonList(server));
        }
    }
}
```

This component:
- Runs automatically for every OpenAPI document request
- Reads X-Forwarded-Proto, X-Forwarded-Host, and X-Forwarded-Port from the current request
- Constructs the external URL (e.g., `https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001`)
- Sets it as the OpenAPI server URL so Swagger UI uses it for "Try it out" calls

## Environment Variables

No specific environment variables are required for preview URL support. The application automatically detects the environment from forwarded headers.

Optional environment variables:
- `SPRING_PROFILES_ACTIVE`: Set to `dev` or `prod` (default: prod)
- `SERVER_PORT` or `PORT`: Internal server port (default: 3001)

## Security Considerations

- Forwarded headers are only used for URL construction, not authentication
- CORS is permissive to allow preview environments but should be restricted in production
- Standard security practices (authentication, authorization) should be implemented separately
- The application trusts the proxy to set correct forwarded headers
