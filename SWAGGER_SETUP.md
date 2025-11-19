# Swagger/OpenAPI Configuration for ModernRepo

## Overview
The ModernRepo Spring Boot application has been configured with Springdoc OpenAPI (Swagger UI) for comprehensive API documentation and testing.

## Dependencies Added
- **springdoc-openapi-ui** version 1.6.15 (compatible with Spring Boot 2.5.5)

## Configuration

### Application Properties
The following properties have been configured in `application.properties`:

```properties
# Server configuration
server.port=3010

# Management endpoints configuration
management.endpoints.web.exposure.include=health,info

# Springdoc OpenAPI configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
```

### OpenAPI Configuration Bean
A configuration class `OpenApiConfig.java` has been created to provide API metadata:
- **Title**: PostgreSQL Questions & Answers API
- **Version**: 1.0.0
- **Description**: RESTful API for managing questions and answers with PostgreSQL, JPA, and Hibernate
- **License**: Apache 2.0

## Available Endpoints

### Swagger UI (Interactive Documentation)
- **URL**: http://localhost:3010/swagger-ui.html
- **Description**: Interactive web interface for exploring and testing the API

### OpenAPI JSON Specification
- **URL**: http://localhost:3010/v3/api-docs
- **Description**: OpenAPI 3.0 specification in JSON format

### Management Endpoints
- **Health Check**: http://localhost:3010/actuator/health
- **Info**: http://localhost:3010/actuator/info

## API Documentation

### Questions API
All question endpoints are documented with:
- Operation summaries and descriptions
- Request/response schemas
- HTTP status codes
- Parameter descriptions

**Endpoints:**
- `GET /questions` - Get all questions (paginated)
- `POST /questions` - Create a new question
- `PUT /questions/{questionId}` - Update a question
- `DELETE /questions/{questionId}` - Delete a question

### Answers API
All answer endpoints are documented with:
- Operation summaries and descriptions
- Request/response schemas
- HTTP status codes
- Parameter descriptions

**Endpoints:**
- `GET /questions/{questionId}/answers` - Get all answers for a question
- `POST /questions/{questionId}/answers` - Add an answer to a question
- `PUT /questions/{questionId}/answers/{answerId}` - Update an answer
- `DELETE /questions/{questionId}/answers/{answerId}` - Delete an answer

## Usage

1. Start the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   Or run the packaged JAR:
   ```bash
   java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar
   ```

2. Access Swagger UI in your browser:
   ```
   http://localhost:3010/swagger-ui.html
   ```

3. Explore the API documentation and test endpoints directly from the Swagger UI interface.

## Features

- **Interactive Testing**: Test API endpoints directly from the Swagger UI
- **Request/Response Validation**: View request and response schemas
- **Authentication Support**: (Can be added if needed)
- **Model Documentation**: View all data models and their properties
- **Error Responses**: See all possible error responses and their formats

## Notes

- The server port has been changed from 3001 to 3010 as per requirements
- All controllers have been annotated with OpenAPI/Swagger annotations
- All public interfaces are properly documented with docstrings
- The application uses Springdoc OpenAPI UI which is compatible with Spring Boot 2.x
