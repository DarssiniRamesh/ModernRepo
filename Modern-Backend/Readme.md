## Spring Boot, PostgreSQL, JPA, Hibernate REST API Demo

## Tutorial

Check out the complete tutorial on the CalliCoder blog -

[Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/)

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://github.com/callicoder/spring-boot-postgresql-jpa-hibernate-rest-api-demo.git
```

**2. Database Configuration (PostgreSQL or H2)**

This project supports two database profiles:

### Development Profile (H2 In-Memory Database)

- **Profile name:** `dev` (default)
- **Description:** Uses H2 in-memory database for local development
- **Setup:** No additional setup needed—just start the app
- **H2 Console:** Available at `/h2-console` during runtime for debugging
- **Use case:** Quick local development and testing without external database

### Production Profile (PostgreSQL)

- **Profile name:** `prod`
- **Description:** Connects to a PostgreSQL database (recommended for production)
- **Database:** Requires PostgreSQL server (local or remote)

#### PostgreSQL Configuration Options

**Option 1: Using Environment Variables (Recommended)**

Set the following environment variables:

```bash
export SPRING_PROFILES_ACTIVE=prod
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=myapp
export POSTGRES_USER=appuser
export POSTGRES_PASSWORD=dbuser123
```

Or set the complete datasource URL:

```bash
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/myapp
export SPRING_DATASOURCE_USERNAME=appuser
export SPRING_DATASOURCE_PASSWORD=dbuser123
```

**Option 2: Using .env File**

Copy the `.env.example` file to `.env` at the project root and update values as needed:

```bash
cp ../.env.example ../.env
# Edit .env file with your database credentials
```

**Default Values (if not overridden):**
- `POSTGRES_HOST`: localhost
- `POSTGRES_PORT`: 5432
- `POSTGRES_DB`: myapp
- `POSTGRES_USER`: appuser
- `POSTGRES_PASSWORD`: dbuser123

**3. Run the app**

### Running with Maven Wrapper (Recommended)

**Development mode (H2 database):**
```bash
./mvnw spring-boot:run
# or explicitly
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production mode (PostgreSQL):**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Running with Maven

**Development mode:**
```bash
mvn spring-boot:run
# or explicitly
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Production mode:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Running as JAR

**Build the JAR:**
```bash
./mvnw clean package
```

**Run with dev profile (H2):**
```bash
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

**Run with prod profile (PostgreSQL):**
```bash
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Using the start.sh Script

The `start.sh` script automatically detects the `SPRING_PROFILES_ACTIVE` environment variable:

```bash
# For dev profile (default)
./start.sh

# For prod profile
export SPRING_PROFILES_ACTIVE=prod
./start.sh
```

---

## API Endpoints

Once the application is running, you can access the following REST endpoints:

### Swagger UI Documentation

**🎉 Interactive API Documentation is now available!**

Access the Swagger UI to explore and test all API endpoints:

```
http://localhost:3001/swagger-ui.html
```

Or view the raw OpenAPI specification (JSON format):

```
http://localhost:3001/v3/api-docs
```

**Swagger UI Features:**
- 📖 Complete API documentation with descriptions
- 🧪 Test endpoints directly from the browser ("Try it out" button)
- 📋 View request/response schemas and examples
- ✅ See all validation requirements
- 🏷️ Endpoints organized by tags (Questions, Answers)

**CORS Configuration:**
The application includes global CORS configuration to allow Swagger UI to function correctly. 

- **Preview/Production Origins:** Explicitly allowed origins are configured in `src/main/java/com/example/postgresdemo/config/CorsConfig.java`
- **To add more origins:** Edit the `setAllowedOrigins()` list in the `CorsConfig.corsFilter()` method
- **Local Development:** Wildcard origin patterns (`*`) are enabled for localhost and development testing
- **Security Note:** For production deployments, see [CORS_CONFIGURATION.md](CORS_CONFIGURATION.md) for best practices

Example of adding a new origin:
```java
config.setAllowedOrigins(Arrays.asList(
    "https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3001",
    "https://your-production-domain.com"  // Add your domain here
));
```

### Questions API
- `GET /questions` - Get all questions (with pagination)
- `POST /questions` - Create a new question
- `PUT /questions/{questionId}` - Update a question
- `DELETE /questions/{questionId}` - Delete a question

### Answers API
- `GET /questions/{questionId}/answers` - Get all answers for a question
- `POST /questions/{questionId}/answers` - Add an answer to a question
- `PUT /questions/{questionId}/answers/{answerId}` - Update an answer
- `DELETE /questions/{questionId}/answers/{answerId}` - Delete an answer

### Example API Usage

**Create a question using curl:**
```bash
curl -X POST http://localhost:3001/questions \
  -H "Content-Type: application/json" \
  -d '{"title":"What is Spring Boot?","description":"I want to learn about Spring Boot framework"}'
```

**Or use the Swagger UI for a more interactive experience!**

---

## Database Configuration Details

### Connection Pool (Hikari)

The application uses HikariCP as the connection pool (default in Spring Boot 2.x+). Configuration is set in `application-prod.properties`:

- **Connection timeout:** 20 seconds
- **Maximum pool size:** 10 connections
- **Minimum idle:** 5 connections
- **Idle timeout:** 5 minutes
- **Max lifetime:** 20 minutes

### Hibernate DDL Auto

- **Dev profile:** `create-drop` (recreates schema on each startup)
- **Prod profile:** `update` (updates schema incrementally, safe for production)

You can override this with the `SPRING_JPA_HIBERNATE_DDL_AUTO` environment variable.

---

## Troubleshooting

### PostgreSQL Connection Issues

1. **Verify PostgreSQL is running:**
   ```bash
   # Check if PostgreSQL is running on the expected port
   netstat -an | grep 5432
   ```

2. **Verify database exists:**
   ```bash
   psql -h localhost -U appuser -d myapp
   ```

3. **Check environment variables:**
   ```bash
   echo $SPRING_PROFILES_ACTIVE
   echo $SPRING_DATASOURCE_URL
   ```

### H2 Console Not Available

The H2 console is only available in `dev` profile. Make sure you're running with:
```bash
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

Then access: `http://localhost:3001/h2-console`

### Swagger UI "Failed to fetch" Error

If Swagger UI displays "Failed to fetch" when trying API operations:

1. **Check CORS configuration:** The application includes global CORS settings. Verify the `CorsConfig` class exists at:
   ```
   src/main/java/com/example/postgresdemo/config/CorsConfig.java
   ```

2. **Browser Console:** Open browser DevTools (F12) and check the Console tab for specific CORS error messages.

3. **Test with cURL:** Verify the API works outside the browser:
   ```bash
   curl http://localhost:3001/questions
   ```

4. **Clear browser cache:** If you see "Failed to fetch" errors, try a hard refresh:
   - **Windows/Linux:** Press `Ctrl + Shift + R`
   - **Mac:** Press `Cmd + Shift + R`
   - This clears cached JavaScript and CSS that may be causing issues

5. **Behind a proxy or preview environment?** This application is configured to work with relative URLs:
   - All Swagger UI requests use relative paths (`/v3/api-docs`, `/swagger-ui.html`)
   - CORS is configured with `allowedOriginPatterns=[*]` to accept requests from preview proxies
   - Forward headers are handled via `server.forward-headers-strategy=framework`
   - The application automatically adapts to the proxy's base URL
   - **No manual configuration needed** for preview environments

6. **Verify these properties are set** in `application.properties`:
   ```properties
   server.forward-headers-strategy=framework
   server.tomcat.remoteip.remote-ip-header=x-forwarded-for
   server.tomcat.remoteip.protocol-header=x-forwarded-proto
   springdoc.swagger-ui.config-url=/v3/api-docs/swagger-config
   springdoc.swagger-ui.url=/v3/api-docs
   ```

For detailed CORS troubleshooting and production configuration, see [CORS_CONFIGURATION.md](CORS_CONFIGURATION.md).

---

## Container/CI Environment

For container or preview environments, the application can be started with:

```bash
# Default (uses SPRING_PROFILES_ACTIVE from environment or falls back to dev)
./start.sh

# Or using Maven Wrapper directly
./mvnw spring-boot:run
```

Make sure the appropriate environment variables are set in the container environment before starting.
