# Swagger Preview URL Implementation Summary

## Overview

This document describes the implementation that ensures Swagger UI and OpenAPI use the external preview base URL (e.g., `https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001`) instead of localhost when accessed through a proxy or preview environment.

## Implementation Details

### 1. Core Components

#### OpenApiServerCustomizer.java
A Spring component that implements `OpenApiCustomiser` to dynamically set the OpenAPI server URL at runtime:

**Key Features:**
- Runs for every OpenAPI document generation request (`/v3/api-docs`, `/swagger-config`)
- Reads `X-Forwarded-Proto`, `X-Forwarded-Host`, and `X-Forwarded-Port` headers from the current HTTP request
- Constructs the external URL dynamically (e.g., `https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001`)
- Sets the URL as the OpenAPI server so Swagger UI's "Try it out" uses the correct base URL

**Flow:**
1. User accesses Swagger UI at preview URL
2. Swagger UI requests `/v3/api-docs` to load OpenAPI spec
3. `OpenApiServerCustomizer.customise()` is invoked
4. Component reads X-Forwarded headers from the request
5. Component constructs external URL and sets it in OpenAPI.servers
6. Swagger UI receives the OpenAPI spec with the correct server URL
7. "Try it out" calls use the external URL, not localhost

#### OpenApiConfig.java
Base OpenAPI configuration that provides application metadata:

**Key Features:**
- Configures API title, version, description, contact info
- Works in conjunction with `OpenApiServerCustomizer` for server URL resolution
- Also includes fallback logic to derive server URL from headers

### 2. Configuration Properties

#### application.properties
```properties
# Forward headers strategy - enables reading X-Forwarded-* headers
server.forward-headers-strategy=framework

# Tomcat remote IP configuration for X-Forwarded headers
server.tomcat.remoteip.remote-ip-header=x-forwarded-for
server.tomcat.remoteip.protocol-header=x-forwarded-proto

# Springdoc OpenAPI configuration with relative URLs
springdoc.api-docs.enabled=true
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.config-url=/v3/api-docs/swagger-config
springdoc.swagger-ui.url=/v3/api-docs
springdoc.swagger-ui.disable-swagger-default-url=true
```

### 3. How It Works

#### Preview Environment Flow

When accessing from preview URL `https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001`:

1. **Proxy Sets Headers:**
   - `X-Forwarded-Proto: https`
   - `X-Forwarded-Host: vscode-internal-32563-beta.beta01.cloud.kavia.ai`
   - `X-Forwarded-Port: 3001`

2. **Request Flow:**
   ```
   Browser → Proxy → Spring Boot App
                ↓
           X-Forwarded headers attached
   ```

3. **OpenAPI Generation:**
   ```
   /v3/api-docs request
        ↓
   OpenApiServerCustomizer.customise() invoked
        ↓
   Read X-Forwarded-Proto, X-Forwarded-Host, X-Forwarded-Port
        ↓
   Construct: https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001
        ↓
   Set as OpenAPI server URL
        ↓
   Return OpenAPI spec with correct server
   ```

4. **Swagger UI Behavior:**
   ```
   Swagger UI loads OpenAPI spec
        ↓
   Sees server URL: https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001
        ↓
   "Try it out" uses this URL for all API calls
   ```

#### Local Development Flow

When running locally without a proxy:

1. **No Forwarded Headers:** X-Forwarded-* headers are absent
2. **Customizer Behavior:** Returns null, doesn't set server URL
3. **Swagger UI Behavior:** Uses relative paths, works with `http://localhost:3001`

### 4. Key Benefits

✅ **Automatic Detection:** No manual configuration needed - automatically detects preview vs local  
✅ **No Caching Issues:** Server URL computed fresh for every OpenAPI request  
✅ **Environment Agnostic:** Works in local, preview, staging, and production environments  
✅ **Standards-Based:** Uses standard X-Forwarded headers set by proxies  
✅ **Backwards Compatible:** Doesn't break local development workflow  

### 5. Testing Verification

#### Manual Testing Steps:

1. **Access Swagger UI:**
   ```
   https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001/swagger-ui.html
   ```

2. **Check Server Dropdown:**
   - Should show: `https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001`
   - Should NOT show: `http://localhost:3001`

3. **Test "Try it out":**
   - Expand any endpoint (e.g., GET /questions)
   - Click "Try it out"
   - Click "Execute"
   - Verify request URL in browser DevTools uses preview URL, not localhost

4. **Verify OpenAPI Spec:**
   ```
   curl https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001/v3/api-docs
   ```
   
   Should contain:
   ```json
   {
     "servers": [
       {
         "url": "https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001",
         "description": "Server URL (auto-detected from request headers)"
       }
     ]
   }
   ```

### 6. Troubleshooting

#### Issue: Swagger UI still shows localhost

**Possible Causes:**
1. Proxy not setting X-Forwarded headers
2. Browser cache showing old Swagger UI
3. Forward headers strategy not enabled

**Solutions:**
1. Check proxy configuration for X-Forwarded header support
2. Hard refresh browser (Ctrl+Shift+R / Cmd+Shift+R)
3. Verify `server.forward-headers-strategy=framework` in properties

#### Issue: "Try it out" requests fail

**Possible Causes:**
1. CORS configuration issues
2. Mixed content (HTTP/HTTPS) issues
3. Network/firewall blocking requests

**Solutions:**
1. Check CORS configuration in `CorsConfig.java`
2. Ensure consistent HTTPS usage
3. Check browser console for CORS/network errors

### 7. Technical Notes

#### Spring Boot Version Compatibility
- Implementation uses `javax.servlet.http.HttpServletRequest` (Spring Boot 2.x)
- For Spring Boot 3.x, replace with `jakarta.servlet.http.HttpServletRequest`

#### Thread Safety
- `RequestContextHolder` provides thread-safe access to current request
- Each request gets its own `ServletRequestAttributes` instance
- No shared state between requests

#### Performance
- Minimal overhead - header reading and string concatenation only
- Runs once per OpenAPI document request (typically cached by Swagger UI)
- No database or external service calls

## Summary

The implementation successfully ensures that Swagger UI and OpenAPI use the external preview base URL by:

1. ✅ Deriving server URL from X-Forwarded-Proto, X-Forwarded-Host, and X-Forwarded-Port headers
2. ✅ Setting OpenAPI servers dynamically at runtime via `OpenApiServerCustomizer`
3. ✅ Using relative springdoc URLs in application properties
4. ✅ Preferring computed external base URL when X-Forwarded headers are available
5. ✅ Ensuring "Try it out" calls use preview URL instead of localhost

The solution is automatic, requires no manual configuration, and works seamlessly across all environments.
