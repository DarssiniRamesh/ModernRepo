# CORS Configuration Guide

## Overview

This document explains the Cross-Origin Resource Sharing (CORS) configuration in the ModernRepo application and how to customize it for different environments.

## What is CORS?

CORS is a security mechanism that controls how web pages from one domain can access resources from another domain. Without proper CORS configuration, Swagger UI and other web clients may encounter "Failed to fetch" errors when trying to access the API.

## Current Configuration

### Global CORS Filter

The application uses a global CORS filter defined in `CorsConfig.java` that applies to all endpoints (`/**`).

**Location:** `src/main/java/com/example/postgresdemo/config/CorsConfig.java`

**Default Settings:**
- **Allowed Origin Patterns:** `*` (all origins)
- **Allowed Methods:** GET, POST, PUT, PATCH, DELETE, OPTIONS
- **Allowed Headers:** Content-Type, Authorization, X-Requested-With, Accept, Origin, Access-Control-Request-Method, Access-Control-Request-Headers
- **Exposed Headers:** Access-Control-Allow-Origin, Access-Control-Allow-Credentials
- **Allow Credentials:** false (recommended for public APIs)
- **Max Age:** 3600 seconds (1 hour preflight cache)

### Why This Configuration Works for Swagger UI

1. **AllowedOriginPattern vs AllowedOrigin:** Using `addAllowedOriginPattern("*")` instead of `addAllowedOrigin("*")` is important when `allowCredentials` is set. Pattern matching provides more flexibility.

2. **OPTIONS Method:** The OPTIONS method is explicitly allowed to handle CORS preflight requests that browsers send before actual API calls.

3. **Content-Type Header:** Swagger UI sends JSON payloads, so Content-Type must be in the allowed headers list.

4. **Authorization Header:** Included for future authentication support.

## Production Security Recommendations

### 1. Restrict Allowed Origins

For production deployments, **never use wildcard (`*`) for allowed origins**. Instead, specify exact domains:

```java
// Option 1: Hardcode specific domains
config.addAllowedOrigin("https://api.yourdomain.com");
config.addAllowedOrigin("https://app.yourdomain.com");
config.addAllowedOrigin("https://yourdomain.com");

// Option 2: Use environment variable
@Value("${cors.allowed.origins:http://localhost:3001}")
private String allowedOrigins;

Arrays.stream(allowedOrigins.split(","))
      .map(String::trim)
      .forEach(config::addAllowedOrigin);
```

### 2. Environment Variable Configuration

Add to your `.env` file or deployment configuration:

```bash
# Development
CORS_ALLOWED_ORIGINS=http://localhost:3001,http://localhost:3000

# Production
CORS_ALLOWED_ORIGINS=https://api.yourdomain.com,https://app.yourdomain.com
```

Then update `CorsConfig.java`:

```java
@Value("${cors.allowed.origins:*}")
private String allowedOriginsConfig;

@Bean
public CorsFilter corsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(false);
    
    // Parse comma-separated origins
    if ("*".equals(allowedOriginsConfig)) {
        config.addAllowedOriginPattern("*");
    } else {
        Arrays.stream(allowedOriginsConfig.split(","))
              .map(String::trim)
              .forEach(config::addAllowedOrigin);
    }
    
    // ... rest of configuration
}
```

### 3. Restrict Methods (Optional)

If your API only needs certain methods, restrict them:

```java
// Instead of all methods:
config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));

// Remove PATCH if not used
```

### 4. Enable Credentials (If Needed)

If your API uses cookies or authentication:

```java
config.setAllowCredentials(true);

// IMPORTANT: When credentials are true, you CANNOT use "*" for origins
// You must specify exact origins
config.addAllowedOrigin("https://app.yourdomain.com");
```

## Proxy and Preview Environment Support

The application includes configuration for reverse proxies and preview environments:

### Forward Headers Strategy

**File:** `application.properties`

```properties
server.forward-headers-strategy=framework
```

This tells Spring Boot to trust and use forwarded headers (X-Forwarded-For, X-Forwarded-Proto, X-Forwarded-Host).

### Tomcat Remote IP Configuration

```properties
server.tomcat.remoteip.remote-ip-header=x-forwarded-for
server.tomcat.remoteip.protocol-header=x-forwarded-proto
```

These settings ensure:
- The correct client IP is logged (not the proxy IP)
- HTTPS detection works behind SSL-terminating proxies
- Swagger UI generates correct URLs when accessed through a proxy

### OpenAPI Server URL

The `OpenApiConfig.java` dynamically sets the server URL:

```java
@Value("${server.port:3001}")
private String serverPort;

@Value("${openapi.server.url:http://localhost:${server.port}}")
private String serverUrl;
```

This ensures the OpenAPI specification uses the correct base URL. Override with:

```bash
OPENAPI_SERVER_URL=https://api.yourdomain.com
```

## Testing CORS Configuration

### 1. Using cURL

**Preflight Request (OPTIONS):**
```bash
curl -v -H "Origin: http://example.com" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -X OPTIONS http://localhost:3001/questions
```

**Expected Response Headers:**
```
Access-Control-Allow-Origin: http://example.com
Access-Control-Allow-Methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
Access-Control-Allow-Headers: Content-Type, Authorization, ...
Access-Control-Max-Age: 3600
```

**Actual Request:**
```bash
curl -v -H "Origin: http://example.com" \
  -H "Content-Type: application/json" \
  -X GET http://localhost:3001/questions
```

**Expected Response Header:**
```
Access-Control-Allow-Origin: http://example.com
```

### 2. Using Browser DevTools

1. Open Swagger UI: `http://localhost:3001/swagger-ui.html`
2. Open Browser DevTools (F12) → Network tab
3. Try an API operation (e.g., GET /questions)
4. Inspect the request headers:
   - Should see `Origin: http://localhost:3001`
5. Inspect the response headers:
   - Should see `Access-Control-Allow-Origin: http://localhost:3001`

### 3. Using Swagger UI

1. Navigate to `http://localhost:3001/swagger-ui.html`
2. Click "Try it out" on any endpoint
3. Click "Execute"
4. If CORS is working: You'll see the response data
5. If CORS is failing: You'll see "Failed to fetch" in the browser console

## Common CORS Issues and Solutions

### Issue 1: "Failed to fetch" in Swagger UI

**Cause:** Browser blocking the request due to CORS policy.

**Solution:**
- Verify `CorsConfig.java` is present and annotated with `@Configuration`
- Check that `corsFilter()` method is annotated with `@Bean`
- Ensure allowed origins include the Swagger UI origin
- Check browser console for specific CORS error messages

### Issue 2: "allowCredentials=true with wildcard origins"

**Error:** `When allowCredentials is true, allowedOrigins cannot contain the special value "*"`

**Solution:**
```java
// Change from:
config.setAllowCredentials(true);
config.addAllowedOrigin("*");

// To:
config.setAllowCredentials(true);
config.addAllowedOrigin("http://localhost:3001");
config.addAllowedOrigin("https://yourdomain.com");
```

### Issue 3: OPTIONS requests return 403 Forbidden

**Cause:** Spring Security (if added) blocking OPTIONS requests.

**Solution:** Add to SecurityConfig:
```java
http.cors().and()
    .authorizeRequests()
    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
```

### Issue 4: Swagger UI uses wrong scheme (http vs https)

**Cause:** Proxy doesn't forward protocol headers correctly.

**Solution:**
- Ensure `server.forward-headers-strategy=framework` is set
- Verify proxy sends `X-Forwarded-Proto` header
- Set `OPENAPI_SERVER_URL` environment variable to the correct URL

### Issue 5: CORS works in dev but fails in production

**Cause:** Production has different origin (domain/port).

**Solution:**
- Update allowed origins to include production domain
- Use environment variables for origin configuration
- Test with production URL before deploying

## Architecture Notes

### Why Global CORS Filter vs @CrossOrigin Annotations?

**Global CORS Filter (Current Approach):**
- ✅ Single configuration point
- ✅ Applies to all endpoints automatically
- ✅ Easier to manage and audit
- ✅ Works for all Spring MVC controllers and servlets
- ✅ Consistent behavior across the application

**@CrossOrigin Annotations:**
- ❌ Must be added to every controller
- ❌ Easy to forget on new endpoints
- ❌ Inconsistent configuration across controllers
- ✅ Allows per-endpoint customization (if needed)

For most applications, the global CORS filter approach is recommended.

### Integration with Spring Security

If you add Spring Security to the application, the CORS filter must be configured there as well:

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    private CorsFilter corsFilter;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and() // Enable CORS
            .csrf().disable()
            .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .anyRequest().authenticated();
    }
}
```

## References

- [Spring Framework CORS Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-cors)
- [MDN Web Docs: CORS](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
- [Springdoc OpenAPI Documentation](https://springdoc.org/)

## Support

For issues or questions about CORS configuration:
1. Check browser console for specific CORS error messages
2. Review this document's troubleshooting section
3. Test with cURL to isolate browser-specific issues
4. Verify proxy/load balancer configuration if behind one
