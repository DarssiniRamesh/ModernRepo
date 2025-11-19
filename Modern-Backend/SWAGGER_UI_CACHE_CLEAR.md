# Swagger UI Cache Clearing Guide

## Why Clear Cache?

If you previously accessed Swagger UI and it was loading the Petstore example API, your browser may have cached the old Swagger configuration. After updating the Springdoc configuration to point to this application's API, you may need to clear your browser cache.

## How to Clear Swagger UI Cache

### Option 1: Hard Refresh (Recommended)

1. Open Swagger UI: `http://localhost:3001/swagger-ui.html`
2. Perform a hard refresh:
   - **Windows/Linux:** `Ctrl + Shift + R` or `Ctrl + F5`
   - **Mac:** `Cmd + Shift + R`

### Option 2: Clear Browser Cache

**Chrome:**
1. Press `F12` to open DevTools
2. Right-click the refresh button
3. Select "Empty Cache and Hard Reload"

**Firefox:**
1. Press `Ctrl + Shift + Delete` (Windows/Linux) or `Cmd + Shift + Delete` (Mac)
2. Select "Cached Web Content"
3. Click "Clear Now"

**Safari:**
1. Go to Safari → Preferences → Advanced
2. Enable "Show Develop menu in menu bar"
3. Go to Develop → Empty Caches

### Option 3: Open in Incognito/Private Mode

Open Swagger UI in an incognito/private browsing window:
- **Chrome:** `Ctrl + Shift + N` (Windows/Linux) or `Cmd + Shift + N` (Mac)
- **Firefox:** `Ctrl + Shift + P` (Windows/Linux) or `Cmd + Shift + P` (Mac)
- **Safari:** `Cmd + Shift + N`

Then navigate to: `http://localhost:3001/swagger-ui.html`

## Verifying the Fix

After clearing cache, you should see:

1. **API Title:** "ModernRepo REST API" (not "Swagger Petstore")
2. **Server URL:** `http://localhost:3001` (not petstore.swagger.io)
3. **Endpoints:** `/questions`, `/questions/{questionId}/answers`, etc.

## Configuration Changes Made

The following properties were added to prevent Petstore fallback:

```properties
# In application.properties, application-dev.properties, and application-prod.properties
springdoc.swagger-ui.url=/v3/api-docs
```

This explicitly tells Swagger UI to load the OpenAPI spec from `/v3/api-docs` instead of falling back to the default Petstore example.

## Troubleshooting

If Swagger UI still shows Petstore after clearing cache:

1. **Verify the application restarted** after configuration changes
2. **Check the OpenAPI spec endpoint** is accessible:
   ```bash
   curl http://localhost:3001/v3/api-docs
   ```
   Should return the ModernRepo API spec, not Petstore

3. **Check the swagger-config endpoint**:
   ```bash
   curl http://localhost:3001/v3/api-docs/swagger-config
   ```
   Should include `"url":"/v3/api-docs"` in the response

4. **Verify no static Swagger UI files** are overriding the configuration (none should exist in the project)

5. **Check browser console** (F12 → Console tab) for any error messages

## Related Documentation

- See `SWAGGER_CORS_FIX_SUMMARY.md` for CORS configuration details
- See `CORS_CONFIGURATION.md` for production CORS setup
- See `Readme.md` for general API documentation and usage
