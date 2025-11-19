# ModernRepo - Spring Boot REST API Demo

A Spring Boot REST API demo application using PostgreSQL, JPA, and Hibernate for managing questions and answers.

## Project Structure

```
ModernRepo/
├── .env.example          # Environment variables template
├── start.sh              # Start script for the application
├── Modern-Backend/       # Spring Boot application
│   ├── src/
│   ├── pom.xml
│   ├── start.sh
│   └── Readme.md        # Detailed documentation
└── README.md            # This file
```

## Quick Start

### 1. Configure Environment Variables

Copy the `.env.example` file to `.env` and update values as needed:

```bash
cp .env.example .env
```

Edit the `.env` file to set your database credentials and other configuration:

```bash
# Set profile: 'prod' for PostgreSQL, 'dev' for H2
SPRING_PROFILES_ACTIVE=prod

# PostgreSQL connection details
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=myapp
POSTGRES_USER=appuser
POSTGRES_PASSWORD=dbuser123
```

### 2. Run the Application

**From the repository root:**
```bash
./start.sh
```

**From the Modern-Backend directory:**
```bash
cd Modern-Backend
./start.sh
```

**Using Maven directly:**
```bash
cd Modern-Backend
./mvnw spring-boot:run
```

## Database Profiles

### Development (H2)
- **Profile:** `dev` (default if not specified)
- **Database:** H2 in-memory
- **Setup:** None required
- **Use case:** Local development and testing

```bash
export SPRING_PROFILES_ACTIVE=dev
./start.sh
```

### Production (PostgreSQL)
- **Profile:** `prod`
- **Database:** PostgreSQL (local or remote)
- **Setup:** Requires PostgreSQL server and configuration
- **Use case:** Production and staging environments

```bash
export SPRING_PROFILES_ACTIVE=prod
# Set POSTGRES_HOST, POSTGRES_PORT, POSTGRES_DB, POSTGRES_USER, POSTGRES_PASSWORD
./start.sh
```

## PostgreSQL Setup

### Using Docker (Recommended for Local Development)

```bash
docker run -d \
  --name postgres-db \
  -e POSTGRES_DB=myapp \
  -e POSTGRES_USER=appuser \
  -e POSTGRES_PASSWORD=dbuser123 \
  -p 5432:5432 \
  postgres:latest
```

### Using Local PostgreSQL Installation

1. Install PostgreSQL on your system
2. Create a database:
   ```sql
   CREATE DATABASE myapp;
   CREATE USER appuser WITH PASSWORD 'dbuser123';
   GRANT ALL PRIVILEGES ON DATABASE myapp TO appuser;
   ```
3. Update the `.env` file with your connection details

## Environment Variables Reference

### Required for PostgreSQL (prod profile)

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `prod` |
| `POSTGRES_HOST` | PostgreSQL host | `localhost` |
| `POSTGRES_PORT` | PostgreSQL port | `5432` |
| `POSTGRES_DB` | Database name | `myapp` |
| `POSTGRES_USER` | Database user | `appuser` |
| `POSTGRES_PASSWORD` | Database password | `dbuser123` |

### Alternative Configuration

You can also set the complete JDBC URL directly:

| Variable | Example |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/myapp` |
| `SPRING_DATASOURCE_USERNAME` | `appuser` |
| `SPRING_DATASOURCE_PASSWORD` | `dbuser123` |

## API Documentation

The application exposes RESTful APIs for managing questions and answers:

- **Questions:** Create, read, update, delete questions
- **Answers:** Manage answers for each question

### 📚 Swagger UI (Interactive Documentation)

Once the application is running, access the interactive Swagger UI documentation:

```
http://localhost:3001/swagger-ui.html
```

The Swagger UI provides:
- Complete API documentation with examples
- Interactive "Try it out" functionality to test endpoints
- Request/response schemas and validation rules
- OpenAPI 3.0 specification at: `http://localhost:3001/v3/api-docs`

For more details, see [Modern-Backend/Readme.md](Modern-Backend/Readme.md)

## CORS Configuration

The application includes global CORS (Cross-Origin Resource Sharing) configuration to allow Swagger UI and other web clients to make requests from different origins.

### Default CORS Settings

By default, the application is configured with:
- **Allowed Origins:** All origin patterns (`*`)
- **Allowed Methods:** GET, POST, PUT, PATCH, DELETE, OPTIONS
- **Allowed Headers:** Content-Type, Authorization, X-Requested-With, Accept, Origin, and Access-Control headers
- **Credentials:** Disabled (suitable for public APIs)

### Configuring Allowed Origins for Production

For production environments, you should restrict allowed origins for security. You can customize CORS settings by:

1. **Editing the CorsConfig class** (`Modern-Backend/src/main/java/com/example/postgresdemo/config/CorsConfig.java`)
   
   Replace the wildcard pattern with specific domains:
   ```java
   // Instead of:
   config.addAllowedOriginPattern("*");
   
   // Use specific domains:
   config.addAllowedOrigin("https://yourdomain.com");
   config.addAllowedOrigin("https://app.yourdomain.com");
   ```

2. **For dynamic origins**, use environment variables:
   ```java
   @Value("${cors.allowed.origins:*}")
   private String allowedOrigins;
   
   // Then in the bean:
   Arrays.stream(allowedOrigins.split(","))
         .forEach(config::addAllowedOrigin);
   ```

3. **Set the environment variable** in your `.env` file or deployment configuration:
   ```bash
   CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://app.yourdomain.com
   ```

### Swagger UI and Proxy/Preview Environments

The application is configured to work correctly behind proxies and in preview environments:
- **Forward Headers Strategy:** Framework mode (respects X-Forwarded headers)
- **Remote IP Header:** x-forwarded-for
- **Protocol Header:** x-forwarded-proto
- **Swagger UI:** Uses relative URLs to avoid scheme/host mismatches

These settings ensure Swagger UI works correctly even when accessed through a reverse proxy or preview URL.

### Testing CORS

To test CORS configuration, you can use curl:

```bash
# Preflight request
curl -H "Origin: http://example.com" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -X OPTIONS http://localhost:3001/questions

# Actual request
curl -H "Origin: http://example.com" \
  -H "Content-Type: application/json" \
  -X GET http://localhost:3001/questions
```

## Troubleshooting

### Can't connect to PostgreSQL

1. Verify PostgreSQL is running:
   ```bash
   docker ps | grep postgres
   # or for local installation
   sudo systemctl status postgresql
   ```

2. Check connection settings in `.env` file

3. Test connection manually:
   ```bash
   psql -h localhost -U appuser -d myapp
   ```

### Application starts but uses H2 instead of PostgreSQL

Make sure `SPRING_PROFILES_ACTIVE=prod` is set in your environment or `.env` file.

### Port already in use

The application runs on port 3001 by default. Change it by setting:
```bash
export PORT=8080
```

## Development

For detailed development instructions, build commands, and testing information, see [Modern-Backend/Readme.md](Modern-Backend/Readme.md)

## License

This is a demo application. Check the original tutorial for license information.
