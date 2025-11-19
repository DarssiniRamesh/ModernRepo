# Spring Boot Profile Configuration Guide

## Overview

This Modern-Backend application uses Spring Boot profiles to manage different runtime environments:
- **prod** (Production): Uses PostgreSQL database (recommended for preview/production)
- **dev** (Development): Uses H2 in-memory database (for local development only)

## Preview Environment Behavior

**The preview environment always runs with the `prod` profile on port 3001.**

This is configured in `.kavia-start.json` and the startup scripts to ensure:
- Single instance launch (no duplicate dev instances)
- PostgreSQL database connection
- No unintended H2 (dev profile) launches causing SIGTERM exit 143

### Configuration Files

1. **`.kavia-start.json`**: Sets `SPRING_PROFILES_ACTIVE=prod` and `SERVER_PORT=3001`
2. **`start.sh`** (root and Modern-Backend): Exports environment variables and launches with prod profile
3. **`application.properties`**: NO default profile set - purely environment-driven
4. **`application-prod.properties`**: PostgreSQL configuration via environment variables
5. **`application-dev.properties`**: H2 in-memory database configuration

## How to Switch Profiles Locally

### Running with Production Profile (PostgreSQL)

```bash
# Set environment variable before starting
export SPRING_PROFILES_ACTIVE=prod
export SERVER_PORT=3001

# Ensure PostgreSQL connection variables are set (in .env or export)
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5000
export POSTGRES_DB=myapp
export POSTGRES_USER=appuser
export POSTGRES_PASSWORD=dbuser123

# Start the application
./start.sh
```

Or use the Maven wrapper directly:
```bash
./Modern-Backend/mvnw -DskipTests spring-boot:run \
  -Dspring-boot.run.profiles=prod \
  -Dspring-boot.run.arguments="--server.port=3001"
```

### Running with Development Profile (H2)

For local development with H2 in-memory database:

```bash
# Set dev profile
export SPRING_PROFILES_ACTIVE=dev
export SERVER_PORT=3001

# Start the application
./start.sh
```

Or directly:
```bash
./Modern-Backend/mvnw -DskipTests spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.arguments="--server.port=3001"
```

The H2 console will be available at: `http://localhost:3001/h2-console`

## Environment Variables Reference

### Required for Production Profile (PostgreSQL)

- `SPRING_PROFILES_ACTIVE=prod` - Activates PostgreSQL configuration
- `SERVER_PORT=3001` - Application server port
- `POSTGRES_HOST` - PostgreSQL host (default: localhost)
- `POSTGRES_PORT` - PostgreSQL port (default: 5000)
- `POSTGRES_DB` - Database name (default: myapp)
- `POSTGRES_USER` - Database username (default: appuser)
- `POSTGRES_PASSWORD` - Database password (default: dbuser123)

Alternatively, you can set the full connection string:
- `SPRING_DATASOURCE_URL` - Full JDBC URL
- `SPRING_DATASOURCE_USERNAME` - Database username
- `SPRING_DATASOURCE_PASSWORD` - Database password

### Development Profile (H2)

No additional environment variables required. H2 uses in-memory database with defaults.

## Swagger/OpenAPI Documentation

The API documentation is available at:
- Swagger UI: `http://localhost:3001/swagger-ui.html`
- OpenAPI JSON: `http://localhost:3001/v3/api-docs`

**Note**: Swagger is configured to use relative paths (`/v3/api-docs`) and will NOT fall back to external Petstore examples.

## Troubleshooting

### Issue: Application starts with wrong profile

**Symptom**: Application connects to H2 instead of PostgreSQL, or vice versa.

**Solution**: 
1. Check `SPRING_PROFILES_ACTIVE` environment variable: `echo $SPRING_PROFILES_ACTIVE`
2. Ensure `.env` file has `SPRING_PROFILES_ACTIVE=prod` for preview
3. Check application logs on startup - it will show which profile is active

### Issue: Exit code 143 (SIGTERM) 

**Symptom**: Application receives SIGTERM and exits with code 143.

**Cause**: This was previously caused by dual instance launches (dev + prod profiles simultaneously).

**Solution**: Now fixed. The launcher ensures only ONE instance with prod profile starts.

### Issue: Port conflict

**Symptom**: Address already in use error on port 3001.

**Solution**:
1. Check if another instance is running: `lsof -i :3001`
2. Kill the existing process or use a different port
3. Set `SERVER_PORT=<other_port>` environment variable

## Maven Plugin Configuration

The `pom.xml` has NO auto-run executions with dev profile. The spring-boot-maven-plugin is configured with defaults only, allowing profile selection to be purely environment-driven through command-line arguments.

## Database Connection Validation

To verify your database connection:

1. Check actuator health endpoint: `curl http://localhost:3001/actuator/health`
2. Review startup logs for connection confirmation
3. For PostgreSQL, ensure the database is running and accessible

## Additional Resources

- [Spring Boot Profiles Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [Spring Boot Configuration Properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
