# ModernRepo Dependencies and Runtime Documentation

## Overview

The ModernRepo is a Spring Boot-based RESTful API application that provides CRUD operations for managing questions and answers. It uses PostgreSQL as the database backend, Spring Data JPA for ORM, and Springdoc OpenAPI for API documentation.

**Project Coordinates:**
- **Group ID:** com.example
- **Artifact ID:** postgres-demo
- **Version:** 0.0.1-SNAPSHOT
- **Packaging:** JAR

---

## Java Runtime

### Java Version

**Configured Version:** Java 11

The project is configured to use Java 11 as specified in the `pom.xml`:
```xml
<java.version>11</java.version>
```

**Runtime Compatibility:**
- **Minimum Required:** Java 11
- **Tested With:** OpenJDK 17 (as evidenced by the verification reports)
- **Recommended:** Java 11, Java 17, or Java 21 (LTS versions)

**Note:** While the project is configured for Java 11, it has been verified to work correctly with Java 17. Spring Boot 2.5.5 is compatible with Java 8 through Java 17. If upgrading to Java 17+, ensure all dependencies support the target version.

---

## Build Tool

### Apache Maven

**Build Tool:** Apache Maven 3.x

**Maven Wrapper:** Included (mvnw, mvnw.cmd)
- Use `./mvnw` on Linux/macOS
- Use `mvnw.cmd` on Windows

**Maven Configuration:**
- **Model Version:** 4.0.0
- **Encoding:** UTF-8
- **Reporting Encoding:** UTF-8

**Build Commands:**
```bash
# Compile and run tests
./mvnw clean test

# Package as JAR
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Run packaged JAR
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar
```

---

## Spring Boot Platform

### Spring Boot Version: 2.5.5

The project uses Spring Boot 2.5.5 as the parent POM, which provides dependency management for all Spring Boot starters and commonly used libraries.

**Parent POM:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.5</version>
</parent>
```

**Spring Boot 2.5.5 Details:**
- **Release Date:** September 2021
- **Spring Framework Version:** 5.3.10
- **Minimum Java Version:** Java 8
- **Maximum Java Version:** Java 17
- **Support Status:** Extended support (commercial)

---

## Key Dependencies

### Summary Table

| Dependency | Version | Scope | Purpose |
|------------|---------|-------|---------|
| spring-boot-starter-data-jpa | 2.5.5 | compile | JPA/Hibernate for database persistence |
| spring-boot-starter-web | 2.5.5 | compile | Web framework for REST APIs |
| spring-boot-starter-validation | 2.5.5 | compile | Bean validation with Hibernate Validator |
| spring-boot-starter-actuator | 2.5.5 | compile | Production monitoring and management |
| postgresql | 42.2.23 | runtime | PostgreSQL JDBC driver |
| spring-boot-starter-test | 2.5.5 | test | Testing framework with JUnit 5 |
| springdoc-openapi-ui | 1.6.15 | compile | OpenAPI 3.0/Swagger UI documentation |

---

### Web Framework

#### spring-boot-starter-web (Managed by Spring Boot 2.5.5)

**Purpose:** Provides the core web framework for building RESTful APIs using Spring MVC.

**Included Libraries:**
- **Spring Web MVC:** 5.3.10 - Web framework for building REST endpoints
- **Spring Boot Embedded Tomcat:** 9.0.53 - Embedded servlet container
- **Jackson:** 2.12.5 - JSON serialization/deserialization
- **Hibernate Validator:** 6.2.0.Final - Bean validation implementation

**Usage in Project:**
- REST controller implementations (`@RestController`)
- Request mapping and routing (`@GetMapping`, `@PostMapping`, etc.)
- HTTP request/response handling
- Exception handling with `@RestControllerAdvice`

---

### Data Access

#### spring-boot-starter-data-jpa (Managed by Spring Boot 2.5.5)

**Purpose:** Provides Spring Data JPA for database access with repository pattern.

**Included Libraries:**
- **Spring Data JPA:** 2.5.5 - Repository abstraction layer
- **Hibernate Core:** 5.4.32.Final - JPA implementation and ORM framework
- **Hibernate EntityManager:** 5.4.32.Final - JPA entity management
- **Spring ORM:** 5.3.10 - Integration layer between Spring and ORM frameworks
- **Spring Transaction:** 5.3.10 - Transaction management
- **HikariCP:** 4.0.3 - High-performance JDBC connection pool

**Usage in Project:**
- Repository interfaces (`QuestionRepository`, `AnswerRepository`)
- JPA entities (`Question`, `Answer`, `AuditModel`)
- Pagination support with `Pageable`
- Transactional operations
- Hibernate DDL auto-update for schema management

**Hibernate Configuration:**
```properties
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

### Validation

#### spring-boot-starter-validation (Managed by Spring Boot 2.5.5)

**Purpose:** Provides Bean Validation (JSR-380) support for validating request payloads.

**Included Libraries:**
- **Hibernate Validator:** 6.2.0.Final - JSR-380 implementation
- **Jakarta Validation API:** 2.0.2 - Validation annotations

**Usage in Project:**
- `@Valid` annotation for request body validation
- `@NotBlank` on Question.title
- `@Size` for string length constraints
- Automatic validation error responses (400 Bad Request)

---

### Monitoring and Management

#### spring-boot-starter-actuator (Managed by Spring Boot 2.5.5)

**Purpose:** Provides production-ready features for monitoring and managing the application.

**Included Libraries:**
- **Spring Boot Actuator:** 2.5.5 - Health checks, metrics, info endpoints
- **Micrometer Core:** 1.7.4 - Metrics collection

**Usage in Project:**
- Health endpoint: `/actuator/health`
- Info endpoint: `/actuator/info`
- Database connectivity monitoring

**Actuator Configuration:**
```properties
management.endpoints.web.exposure.include=health,info
management.endpoints.web.base-path=/actuator
management.endpoint.health.show-details=always
```

---

### Database Driver

#### postgresql (Version: 42.2.23, managed by Spring Boot 2.5.5)

**Group ID:** org.postgresql  
**Artifact ID:** postgresql  
**Version:** 42.2.23  
**Scope:** runtime

**Purpose:** JDBC driver for connecting to PostgreSQL databases.

**Compatibility:**
- **PostgreSQL Versions:** 8.2 and higher
- **JDBC Version:** JDBC 4.2
- **Tested With:** PostgreSQL 16 (as per verification reports)

**Connection Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5000/myapp
spring.datasource.username=appuser
spring.datasource.password=dbuser123
```

---

### API Documentation

#### springdoc-openapi-ui (Version: 1.6.15)

**Group ID:** org.springdoc  
**Artifact ID:** springdoc-openapi-ui  
**Version:** 1.6.15 (explicitly declared)  
**Scope:** compile

**Purpose:** Automatically generates OpenAPI 3.0 specification and provides Swagger UI for interactive API documentation.

**Included Features:**
- OpenAPI 3.0 specification generation
- Swagger UI web interface
- API endpoint documentation
- Request/response schema documentation
- Interactive API testing

**Compatibility:**
- **Spring Boot:** 2.x (2.0.0 - 2.7.x)
- **Java:** 8+
- **OpenAPI Specification:** 3.0.1

**Endpoints Provided:**
- Swagger UI: `/swagger-ui.html` → `/swagger-ui/index.html`
- OpenAPI JSON: `/v3/api-docs`
- Swagger Config: `/v3/api-docs/swagger-config`

**Configuration:**
```properties
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.config-url=/v3/api-docs/swagger-config
springdoc.swagger-ui.url=/v3/api-docs
springdoc.swagger-ui.disable-swagger-default-url=true
```

**Usage in Project:**
- `@Tag` for controller grouping
- `@Operation` for endpoint documentation
- `@Parameter` for parameter descriptions
- `@ApiResponses` for response documentation
- `OpenApiConfig` class for API metadata

---

## Testing Dependencies

### spring-boot-starter-test (Managed by Spring Boot 2.5.5)

**Scope:** test

**Purpose:** Comprehensive testing framework for unit and integration tests.

**Included Libraries:**
- **JUnit Jupiter:** 5.7.2 - Modern testing framework (JUnit 5)
- **Spring Test:** 5.3.10 - Spring application context testing
- **Spring Boot Test:** 2.5.5 - Spring Boot testing utilities
- **AssertJ:** 3.19.0 - Fluent assertion library
- **Hamcrest:** 2.2 - Matcher library
- **Mockito:** 3.9.0 - Mocking framework
- **JSONassert:** 1.5.0 - JSON assertion library
- **JsonPath:** 2.5.0 - JSON path expressions

**Usage in Project:**
- `PostgresDemoApplicationTests` - Application context load test
- `@SpringBootTest` annotation for integration tests
- `@Test` annotation for test methods

---

## Build Plugins

### spring-boot-maven-plugin (Managed by Spring Boot 2.5.5)

**Purpose:** Maven plugin for packaging and running Spring Boot applications.

**Features:**
- Creates executable JAR files with embedded Tomcat
- Packages all dependencies into a single JAR
- Provides `spring-boot:run` goal for development
- Repackages JAR with Spring Boot launcher

**Output:**
- Artifact: `target/postgres-demo-0.0.1-SNAPSHOT.jar`
- Executable: `java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar`

---

## Runtime Configuration

### Application Server

**Embedded Server:** Apache Tomcat 9.0.53  
**Server Port:** 3001  
**Context Path:** `/` (root)

**Configuration:**
```properties
server.port=3001
server.forward-headers-strategy=framework
```

**Forward Headers Strategy:**
The `framework` strategy enables proper handling of `X-Forwarded-*` headers when the application runs behind a reverse proxy (nginx, Apache, etc.). This ensures:
- Correct scheme (https) in generated URLs
- Proper host and port in redirects
- Accurate client IP logging

---

### Database Configuration

**Database:** PostgreSQL  
**Host:** localhost  
**Port:** 5000  
**Database Name:** myapp  
**Username:** appuser  
**Password:** dbuser123 (configured)

**JDBC URL:**
```
jdbc:postgresql://localhost:5000/myapp
```

**Connection Pool:**
- **Provider:** HikariCP 4.0.3
- **Default Maximum Pool Size:** 10 connections
- **Default Minimum Idle:** 10 connections
- **Default Connection Timeout:** 30 seconds

**Schema Management:**
- **Strategy:** Hibernate DDL Auto-Update
- **Dialect:** PostgreSQLDialect
- **Behavior:** Automatically updates database schema on application startup

**Configuration:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5000/myapp
spring.datasource.username=appuser
spring.datasource.password=dbuser123
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
```

---

### CORS Configuration

**Implementation:** Custom `CorsConfig` class with `CorsFilter` bean

**Allowed Origins:**
- `https://vscode-internal-36492-beta.beta01.cloud.kavia.ai:*` (preview domain)
- `http://localhost:*` (local development)

**Allowed Methods:** GET, POST, PUT, PATCH, DELETE, OPTIONS

**Allowed Headers:** Authorization, Content-Type, Accept, Origin, X-Requested-With, Access-Control-Request-Method, Access-Control-Request-Headers

**Credentials:** Enabled

**Max Age:** 3600 seconds (1 hour for preflight cache)

---

## Dependency Version Management

### Spring Boot Dependency Management

Spring Boot 2.5.5 provides automatic version management for most dependencies through the `spring-boot-starter-parent`. This ensures:

1. **Version Compatibility:** All managed dependencies are tested together
2. **Security Updates:** Spring Boot releases include security patches
3. **Simplified POM:** No need to specify versions for managed dependencies
4. **Consistent Versioning:** Transitive dependencies use compatible versions

### Managed Dependency Versions (via Spring Boot 2.5.5)

The following versions are inherited from Spring Boot's dependency management:

- **Spring Framework:** 5.3.10
- **Hibernate:** 5.4.32.Final
- **Jackson:** 2.12.5
- **Tomcat:** 9.0.53
- **HikariCP:** 4.0.3
- **PostgreSQL Driver:** 42.2.23
- **JUnit Jupiter:** 5.7.2
- **Mockito:** 3.9.0
- **AssertJ:** 3.19.0

### Explicitly Declared Versions

Only one dependency has an explicitly declared version:

- **springdoc-openapi-ui:** 1.6.15 (not managed by Spring Boot)

---

## Compatibility Notes

### Java 17 Compatibility

**Current Status:** ✅ Compatible

The application has been verified to run successfully with Java 17 (OpenJDK 17), even though it's configured for Java 11. Spring Boot 2.5.5 fully supports Java 17.

**To Upgrade to Java 17:**
1. Update `java.version` in `pom.xml`:
   ```xml
   <java.version>17</java.version>
   ```
2. Rebuild the application: `./mvnw clean package`
3. No code changes required for basic Java 17 compatibility

**Benefits of Java 17:**
- Long-term support (LTS) version
- Improved performance
- Enhanced language features (records, sealed classes, pattern matching)
- Better garbage collection

---

### Spring Boot 2.5.x Compatibility

**Current Version:** 2.5.5

**Compatibility Matrix:**

| Component | Compatible Versions | Notes |
|-----------|---------------------|-------|
| Java | 8, 11, 17 | Java 17 is the highest supported version |
| PostgreSQL | 8.2+ | JDBC driver 42.2.23 supports all modern versions |
| Hibernate | 5.4.x | Managed version 5.4.32.Final |
| Spring Data JPA | 2.5.x | Managed version 2.5.5 |
| Tomcat | 9.0.x | Embedded version 9.0.53 |

**Known Limitations:**
- Java 18+ is not officially supported by Spring Boot 2.5.x
- Jakarta EE 9+ namespace (jakarta.*) not used (still using javax.*)
- Spring Boot 2.5.x reaches end-of-support, consider upgrading to 2.7.x or 3.x for long-term projects

---

### PostgreSQL Compatibility

**Tested With:** PostgreSQL 16  
**JDBC Driver Version:** 42.2.23

**Supported PostgreSQL Versions:**
- PostgreSQL 8.2 and higher
- Fully tested with PostgreSQL 9.x, 10.x, 11.x, 12.x, 13.x, 14.x, 15.x, 16.x

**Connection Details:**
- **Host:** localhost
- **Port:** 5000 (non-standard port)
- **Database:** myapp
- **User:** appuser

**Note:** The application uses port 5000 instead of PostgreSQL's default port 5432. Ensure the PostgreSQL instance is configured to listen on port 5000.

---

## How to Update Dependencies Safely

### Using Spring Boot Dependency Management

Spring Boot's dependency management ensures compatible versions across all components. Follow these best practices:

#### 1. Upgrading Spring Boot Version

**Recommended Approach:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version> <!-- Latest 2.x version -->
</parent>
```

**Steps:**
1. Check Spring Boot release notes for breaking changes
2. Update the parent version in `pom.xml`
3. Run tests: `./mvnw clean test`
4. Check for deprecation warnings
5. Update any deprecated APIs

**Migration Paths:**
- **2.5.5 → 2.7.x:** Minor updates, minimal breaking changes
- **2.x → 3.x:** Major version upgrade, requires Java 17+, Jakarta namespace migration

#### 2. Updating Individual Dependencies

**For Managed Dependencies:**

Spring Boot manages versions automatically. To override:
```xml
<properties>
    <!-- Override managed version if needed -->
    <postgresql.version>42.7.3</postgresql.version>
</properties>
```

**For Explicitly Versioned Dependencies:**

Update the version directly in the dependency declaration:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.7.0</version> <!-- Updated version -->
</dependency>
```

#### 3. Checking for Updates

**Use Maven Versions Plugin:**
```bash
# Display available updates
./mvnw versions:display-dependency-updates

# Display plugin updates
./mvnw versions:display-plugin-updates

# Display property updates
./mvnw versions:display-property-updates
```

#### 4. Dependency Tree Analysis

**View complete dependency tree:**
```bash
./mvnw dependency:tree
```

**Check for conflicts:**
```bash
./mvnw dependency:tree -Dverbose
```

#### 5. Security Vulnerability Scanning

**Check for known vulnerabilities:**
```bash
# Using Maven Dependency Check Plugin
./mvnw org.owasp:dependency-check-maven:check
```

**Or use online tools:**
- Snyk: https://snyk.io/
- GitHub Dependabot (if hosted on GitHub)
- WhiteSource Bolt

#### 6. Testing After Updates

**Recommended testing sequence:**
```bash
# 1. Clean build
./mvnw clean compile

# 2. Run all tests
./mvnw test

# 3. Package application
./mvnw package

# 4. Run integration tests (if available)
./mvnw verify

# 5. Run application
./mvnw spring-boot:run

# 6. Verify endpoints
curl http://localhost:3001/actuator/health
curl http://localhost:3001/questions
```

---

### Updating springdoc-openapi-ui

**Current Version:** 1.6.15  
**Latest for Spring Boot 2.x:** 1.8.0

**To update:**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.8.0</version>
</dependency>
```

**Migration Considerations:**
- 1.6.x → 1.7.x: Minimal changes, mostly bug fixes
- 1.7.x → 1.8.x: Minor configuration changes, enhanced features
- Spring Boot 3.x requires springdoc-openapi v2.x

**Compatibility Check:**
- Verify Swagger UI loads: `http://localhost:3001/swagger-ui.html`
- Check OpenAPI spec: `http://localhost:3001/v3/api-docs`
- Test API documentation completeness

---

### PostgreSQL Driver Updates

**Current Version:** 42.2.23 (September 2021)  
**Latest 42.x Version:** 42.7.3 (March 2024)

**To update:**
```xml
<properties>
    <postgresql.version>42.7.3</postgresql.version>
</properties>
```

**Benefits of Updating:**
- Security patches
- Performance improvements
- Support for newer PostgreSQL features
- Bug fixes

**Testing:**
1. Verify database connectivity
2. Test all CRUD operations
3. Check connection pool behavior
4. Monitor performance metrics

---

## Troubleshooting Common Dependency Issues

### Issue 1: Port Already in Use

**Symptom:** `Address already in use: bind`

**Solution:**
```properties
# Change port in application.properties
server.port=8080
```

Or use environment variable:
```bash
SERVER_PORT=8080 ./mvnw spring-boot:run
```

### Issue 2: Database Connection Refused

**Symptom:** `Connection refused: localhost:5000`

**Solutions:**
1. Verify PostgreSQL is running: `pg_isready -h localhost -p 5000`
2. Check connection string in `application.properties`
3. Verify database credentials
4. Ensure PostgreSQL is listening on port 5000

### Issue 3: Hibernate DDL Auto-Update Failures

**Symptom:** Schema update errors on startup

**Solutions:**
```properties
# Switch to validate mode to see what's wrong
spring.jpa.hibernate.ddl-auto=validate

# Or use create-drop for development
spring.jpa.hibernate.ddl-auto=create-drop
```

### Issue 4: Version Conflicts

**Symptom:** `NoSuchMethodError` or `ClassNotFoundException`

**Solution:**
```bash
# Analyze dependency tree
./mvnw dependency:tree

# Look for version conflicts and exclude transitive dependencies if needed
```

---

## Additional Notes

### Environment-Specific Configuration

Use Spring profiles for different environments:

```properties
# application-dev.properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# application-prod.properties
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
```

Activate profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Performance Tuning

**HikariCP Configuration:**
```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### Security Considerations

1. **Database Credentials:** Use environment variables or secret management
2. **CORS Configuration:** Restrict allowed origins in production
3. **Actuator Endpoints:** Secure sensitive endpoints in production
4. **Dependency Scanning:** Regularly scan for vulnerabilities

### Monitoring in Production

Enable additional actuator endpoints:
```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

---

## References

- **Spring Boot Documentation:** https://docs.spring.io/spring-boot/docs/2.5.5/reference/html/
- **Spring Data JPA:** https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/
- **Hibernate ORM:** https://hibernate.org/orm/documentation/5.4/
- **PostgreSQL JDBC Driver:** https://jdbc.postgresql.org/documentation/
- **Springdoc OpenAPI:** https://springdoc.org/
- **HikariCP:** https://github.com/brettwooldridge/HikariCP

---

**Document Version:** 1.0  
**Last Updated:** 2025-01-19  
**Application Version:** 0.0.1-SNAPSHOT  
**Spring Boot Version:** 2.5.5
