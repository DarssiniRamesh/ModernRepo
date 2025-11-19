# Java 22 Migration Backend

This is a Java 22 migration of the Spring Boot PostgreSQL demo application, upgraded to Spring Boot 3.3.6 with Jakarta EE namespace.

## Overview

- **Java Version**: 22
- **Spring Boot Version**: 3.3.6
- **Server Port**: 3002
- **Database**: PostgreSQL (localhost:5000, database: myapp)
- **Namespace**: Jakarta EE (migrated from javax)

## Prerequisites

- Java 22 installed and configured
- Maven 3.9+ (or use the included Maven wrapper `./mvnw`)
- PostgreSQL running on localhost:5000 with database `myapp`, user `appuser`, password `dbuser123`

## Building the Application

```bash
cd Migrate/java22-backend
./scripts/build.sh
```

Or manually:
```bash
./mvnw clean package
```

## Running the Application

```bash
./scripts/run.sh
```

Or manually:
```bash
java -jar target/postgres-demo-java22-0.0.1-SNAPSHOT.jar
```

The application will start on **port 3002**.

## Available Endpoints

### Swagger UI
- **URL**: http://localhost:3002/swagger-ui.html
- **API Documentation**: http://localhost:3002/v3/api-docs
- **OpenAPI JSON**: http://localhost:3002/openapi.json

### Health Check
- **URL**: http://localhost:3002/actuator/health
- **Info**: http://localhost:3002/actuator/info

### Question Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/questions` | Get all questions (paginated) |
| POST | `/questions` | Create a new question |
| PUT | `/questions/{questionId}` | Update a question |
| DELETE | `/questions/{questionId}` | Delete a question |

#### Example: Get Questions with Pagination
```bash
curl "http://localhost:3002/questions?page=0&size=10&sort=createdAt,desc"
```

#### Example: Create a Question
```bash
curl -X POST http://localhost:3002/questions \
  -H "Content-Type: application/json" \
  -d '{"title": "What is Java 22?", "description": "Explain new features in Java 22"}'
```

### Answer Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/questions/{questionId}/answers` | Get all answers for a question |
| POST | `/questions/{questionId}/answers` | Add an answer to a question |
| PUT | `/questions/{questionId}/answers/{answerId}` | Update an answer |
| DELETE | `/questions/{questionId}/answers/{answerId}` | Delete an answer |

#### Example: Add an Answer
```bash
curl -X POST http://localhost:3002/questions/1/answers \
  -H "Content-Type: application/json" \
  -d '{"text": "Java 22 introduces virtual threads and other performance improvements"}'
```

## Migration Changes from Original Backend

### 1. Spring Boot Upgrade
- Upgraded from Spring Boot 2.5.5 to 3.3.6

### 2. Java Version
- Upgraded from Java 11 to Java 22
- Configured Maven compiler with release 22
- Added maven-toolchains-plugin for Java 22 toolchain

### 3. Namespace Migration (javax → jakarta)
All `javax.*` imports migrated to `jakarta.*`:
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.validation.*` → `jakarta.validation.*`

Affected files:
- `model/Question.java`
- `model/Answer.java`
- `model/AuditModel.java`
- `controller/QuestionController.java`
- `controller/AnswerController.java`

### 4. Dependencies Updated
- `springdoc-openapi-ui` (1.6.15) → `springdoc-openapi-starter-webmvc-ui` (2.6.0)
- All Spring Boot starters now use Jakarta EE 10

### 5. Port Configuration
- Changed from port 3001 to port 3002 to avoid conflicts

### 6. CORS Configuration
CORS is configured to work with the Java 22 backend on port 3002. Cross-origin requests are supported for development.

## Database Configuration

The application connects to the same PostgreSQL database as the original backend:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5000/myapp
spring.datasource.username=appuser
spring.datasource.password=dbuser123
```

Both backends can run simultaneously and share the same database.

## Verification

After starting the application, verify it's working:

1. **Health Check**:
   ```bash
   curl http://localhost:3002/actuator/health
   ```
   Expected response: `{"status":"UP"}`

2. **Swagger UI**: 
   Open http://localhost:3002/swagger-ui.html in a browser

3. **Test Endpoints**:
   ```bash
   # Get all questions
   curl http://localhost:3002/questions
   
   # Create a question
   curl -X POST http://localhost:3002/questions \
     -H "Content-Type: application/json" \
     -d '{"title":"Test","description":"Test question"}'
   ```

## Troubleshooting

### Java Version Issues
Ensure Java 22 is installed and set as the default:
```bash
java -version  # Should show version 22
```

### Port Already in Use
If port 3002 is already in use, update `server.port` in `src/main/resources/application.properties`.

### Database Connection Issues
Verify PostgreSQL is running on port 5000:
```bash
psql -h localhost -p 5000 -U appuser -d myapp
```

## Development Notes

- The original ModernRepo application remains unchanged and continues to run on port 3001
- This migration backend runs independently on port 3002
- Both can coexist and share the same PostgreSQL database
- All Jakarta namespace migrations are contained within the Migrate/java22-backend directory
