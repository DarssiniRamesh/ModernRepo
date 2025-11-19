# Java 22 Migration Guide for ModernRepo

## Overview and Goals

This guide provides a comprehensive step-by-step migration path for upgrading the ModernRepo Spring Boot backend from:

- **Current State**: Spring Boot 2.5.5 with Java 11
- **Target State**: Spring Boot 3.3.x with Java 22

### Migration Objectives

1. Upgrade to Java 22 LTS for long-term support and performance improvements
2. Migrate to Spring Boot 3.3.x for latest features and security patches
3. Adopt Jakarta EE 9+ namespace (`javax.*` → `jakarta.*`)
4. Upgrade Hibernate to 6.4+ via Spring Boot dependency management
5. Migrate OpenAPI/Swagger from springdoc 1.6.x to 2.x
6. Ensure backward compatibility for existing REST API endpoints
7. Maintain database connectivity on `localhost:5000` and server port `3001`
8. Preserve pagination, sorting, validation, and error handling behavior

### Expected Benefits

- **Performance**: Java 22 virtual threads, improved garbage collection, and enhanced startup time
- **Security**: Latest CVE patches in Spring Boot 3.3.x and Hibernate 6.x
- **Developer Experience**: Modern API features, improved observability via Micrometer
- **Future-proofing**: Alignment with Jakarta EE standards ensures compatibility with future frameworks

---

## Compatibility Matrix and Rationale

| Component | Current Version | Target Version | Rationale |
|-----------|----------------|----------------|-----------|
| **Java** | 11 | 22 | Latest LTS version with virtual threads, pattern matching, and performance improvements |
| **Spring Boot** | 2.5.5 | 3.3.5 | Latest stable release with Jakarta EE 9+ support and Spring Framework 6.x |
| **Spring Framework** | 5.3.x (implicit) | 6.1.x (implicit) | Required for Spring Boot 3.3.x |
| **Hibernate** | 5.4.x (implicit) | 6.4.x (implicit) | Managed by Spring Boot BOM, supports Jakarta Persistence API 3.1 |
| **Jakarta Persistence API** | 2.2 (javax.*) | 3.1 (jakarta.*) | Namespace change from javax to jakarta |
| **Jakarta Validation** | 2.0 (javax.*) | 3.0 (jakarta.*) | Namespace change from javax to jakarta |
| **Jakarta Servlet** | 4.0 (javax.*) | 6.0 (jakarta.*) | Required for Tomcat 10+ embedded in Spring Boot 3 |
| **springdoc-openapi** | 1.6.15 | 2.5.0 | Compatible with Spring Boot 3.x and Jakarta namespace |
| **PostgreSQL Driver** | Latest (runtime) | Latest (runtime) | No breaking changes expected |
| **Maven** | 3.6+ | 3.9+ | Required for Java 22 support |

### Why Spring Boot 3.3.5?

- **Stable Release**: 3.3.5 is a production-ready patch version with critical bug fixes
- **Java 22 Support**: Full compatibility with Java 22 features
- **Hibernate 6.4**: Includes query improvements and better N+1 detection
- **Observability**: Enhanced Micrometer integration for metrics and tracing

---

## Prerequisites and Environment Setup

### 1. Install Java 22

**Download and Install JDK 22**:

```bash
# Verify current Java version
java -version

# Download OpenJDK 22 from https://adoptium.net/ or use SDK manager
# For Linux/macOS with SDKMAN:
sdk install java 22-tem
sdk use java 22-tem

# Verify installation
java -version
# Expected output: openjdk version "22" or "22.0.x"
```

**Set JAVA_HOME**:

```bash
# Linux/macOS
export JAVA_HOME=/path/to/jdk-22
export PATH=$JAVA_HOME/bin:$PATH

# Windows
set JAVA_HOME=C:\path\to\jdk-22
set PATH=%JAVA_HOME%\bin;%PATH%
```

### 2. Upgrade Maven

**Check Maven Version**:

```bash
mvn -version
# Required: Apache Maven 3.9.0 or higher
```

**Upgrade Maven (if needed)**:

```bash
# Download from https://maven.apache.org/download.cgi
# Or use package manager:
# macOS: brew install maven
# Linux: sudo apt install maven (may need to add repository for 3.9+)
```

### 3. Configure Maven Toolchain (Optional but Recommended)

Create or update `~/.m2/toolchains.xml` to explicitly set JDK 22:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<toolchains>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>22</version>
            <vendor>openjdk</vendor>
        </provides>
        <configuration>
            <jdkHome>/path/to/jdk-22</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
```

### 4. Verify Database Connectivity

Ensure PostgreSQL is running and accessible:

```bash
# Test connection (ensure DB is running on localhost:5000)
psql -h localhost -p 5000 -U appuser -d myapp -c "SELECT version();"
```

### 5. Create Migration Branch

```bash
cd ModernRepo
git checkout -b feature/java22-spring-boot-3-migration
git add .
git commit -m "Pre-migration checkpoint: Spring Boot 2.5.5 + Java 11"
```

---

## Dependency and Plugin Upgrades

### Step 1: Update `pom.xml` - Parent Version

**Current**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.5</version>
    <relativePath/>
</parent>
```

**Updated**:
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
    <relativePath/>
</parent>
```

### Step 2: Update Java Version in Properties

**Current**:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>11</java.version>
</properties>
```

**Updated**:
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>22</java.version>
    <maven.compiler.source>22</maven.compiler.source>
    <maven.compiler.target>22</maven.compiler.target>
</properties>
```

### Step 3: Update springdoc-openapi Dependency

**Current**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.6.15</version>
</dependency>
```

**Updated**:
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

**Key Changes**:
- Artifact ID changed from `springdoc-openapi-ui` to `springdoc-openapi-starter-webmvc-ui`
- Version 2.x supports Spring Boot 3.x and Jakarta namespace
- Auto-configuration remains the same

### Step 4: Verify Other Dependencies

All other Spring Boot starters will be automatically upgraded via the parent BOM:

- `spring-boot-starter-data-jpa` → will pull Hibernate 6.4.x
- `spring-boot-starter-web` → will use Jakarta Servlet 6.0
- `spring-boot-starter-validation` → will use Jakarta Validation 3.0
- `spring-boot-starter-actuator` → upgraded to 3.3.5
- `spring-boot-starter-test` → upgraded with JUnit 5 improvements

**No changes required** for:
- `postgresql` driver (remains at runtime scope)
- Spring Boot starter dependencies (managed by parent)

### Step 5: Update Build Plugins (Optional)

Add explicit compiler plugin configuration if not using toolchains:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.13.0</version>
            <configuration>
                <source>22</source>
                <target>22</target>
                <release>22</release>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.5</version>
        </plugin>
    </plugins>
</build>
```

### Complete Updated `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>postgres-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>postgres-demo</name>
    <description>Demo project for Spring Boot with Java 22</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>22</java.version>
        <maven.compiler.source>22</maven.compiler.source>
        <maven.compiler.target>22</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- Springdoc OpenAPI (Swagger) dependency - Spring Boot 3.x compatible -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.5.0</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>22</source>
                    <target>22</target>
                    <release>22</release>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## Jakarta EE Namespace Migration (javax → jakarta)

Spring Boot 3.x requires Jakarta EE 9+, which uses the `jakarta.*` namespace instead of `javax.*`. All imports must be updated.

### Package Mapping Reference

| Old Namespace (javax.*) | New Namespace (jakarta.*) | Affected Files |
|-------------------------|---------------------------|----------------|
| `javax.persistence.*` | `jakarta.persistence.*` | Entity models, repositories |
| `javax.validation.*` | `jakarta.validation.*` | Validation annotations |
| `javax.servlet.*` | `jakarta.servlet.*` | (Not directly used in ModernRepo) |

### Step 1: Update Entity Models

**File**: `src/main/java/com/example/postgresdemo/model/Question.java`

**Before**:
```java
package com.example.postgresdemo.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "questions")
public class Question extends AuditModel {
    @Id
    @GeneratedValue(generator = "question_generator")
    @SequenceGenerator(
            name = "question_generator",
            sequenceName = "question_sequence",
            initialValue = 1000
    )
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    // Getters and setters...
}
```

**After**:
```java
package com.example.postgresdemo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "questions")
public class Question extends AuditModel {
    @Id
    @GeneratedValue(generator = "question_generator")
    @SequenceGenerator(
            name = "question_generator",
            sequenceName = "question_sequence",
            initialValue = 1000
    )
    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    // Getters and setters remain unchanged...
}
```

**Changes**:
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.validation.constraints.*` → `jakarta.validation.constraints.*`

---

**File**: `src/main/java/com/example/postgresdemo/model/AuditModel.java`

**Before**:
```java
package com.example.postgresdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
public abstract class AuditModel implements Serializable {
    // Fields and methods...
}
```

**After**:
```java
package com.example.postgresdemo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt"},
        allowGetters = true
)
public abstract class AuditModel implements Serializable {
    // Fields and methods remain unchanged...
}
```

**Changes**:
- `javax.persistence.*` → `jakarta.persistence.*`

---

**File**: `src/main/java/com/example/postgresdemo/model/Answer.java`

Apply the same pattern:
- Replace `javax.persistence.*` with `jakarta.persistence.*`
- Replace `javax.validation.constraints.*` with `jakarta.validation.constraints.*`

### Step 2: Update Controllers

**File**: `src/main/java/com/example/postgresdemo/controller/QuestionController.java`

**Before**:
```java
package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Question;
import com.example.postgresdemo.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@Tag(name = "Questions", description = "API endpoints for managing questions")
public class QuestionController {
    // Controller methods...
}
```

**After**:
```java
package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ResourceNotFoundException;
import com.example.postgresdemo.model.Question;
import com.example.postgresdemo.repository.QuestionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Questions", description = "API endpoints for managing questions")
public class QuestionController {
    // Controller methods remain unchanged...
}
```

**Changes**:
- `javax.validation.Valid` → `jakarta.validation.Valid`

Apply the same change to `AnswerController.java`.

### Step 3: Automated Find-and-Replace

Use IDE or command-line tools to perform bulk replacements:

```bash
# Using find and sed (Linux/macOS)
find src/main/java -name "*.java" -type f -exec sed -i '' \
  's/import javax\.persistence\./import jakarta.persistence./g' {} +

find src/main/java -name "*.java" -type f -exec sed -i '' \
  's/import javax\.validation\./import jakarta.validation./g' {} +

find src/test/java -name "*.java" -type f -exec sed -i '' \
  's/import javax\.persistence\./import jakarta.persistence./g' {} +

find src/test/java -name "*.java" -type f -exec sed -i '' \
  's/import javax\.validation\./import jakarta.validation./g' {} +
```

**IntelliJ IDEA**:
1. `Ctrl+Shift+R` (Replace in Path)
2. Find: `import javax.persistence.`
3. Replace: `import jakarta.persistence.`
4. Scope: Project Files
5. Repeat for `javax.validation.`

**VS Code**:
1. `Ctrl+Shift+H` (Replace in Files)
2. Find: `import javax\.persistence\.`
3. Replace: `import jakarta.persistence.`
4. Use regex mode
5. Repeat for `javax\.validation\.`

---

## Spring Boot 3.x Breaking Changes and Code Updates

### Key Changes in Spring Boot 3.x

1. **No Breaking API Changes for Controllers**: Spring Web annotations (`@RestController`, `@GetMapping`, `@PostMapping`, etc.) remain unchanged.
2. **Actuator Endpoints**: Default exposure changed; update `application.properties` if needed.
3. **Observability**: Micrometer is now the default for metrics (replaces legacy metrics).
4. **Spring Data**: Pagination and sorting APIs unchanged; `Pageable` works as before.
5. **Exception Handling**: `@RestControllerAdvice` and `@ExceptionHandler` remain compatible.

### Code Changes Required

#### 1. Main Application Class

**File**: `src/main/java/com/example/postgresdemo/PostgresDemoApplication.java`

**No changes required**. The current implementation is fully compatible:

```java
package com.example.postgresdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PostgresDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(PostgresDemoApplication.class, args);
    }
}
```

#### 2. Exception Handler

**File**: `src/main/java/com/example/postgresdemo/exception/GlobalExceptionHandler.java`

**No code changes required**, but verify imports after Jakarta migration:

```java
package com.example.postgresdemo.exception;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // All existing exception handlers remain unchanged
}
```

**Verification**: Ensure no `javax.*` imports remain. All Spring Framework exceptions are unaffected by Jakarta migration.

#### 3. CORS Configuration

**File**: `src/main/java/com/example/postgresdemo/config/CorsConfig.java`

**No changes required**. The CORS filter configuration is fully compatible with Spring Boot 3.x:

```java
package com.example.postgresdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        // Existing implementation remains unchanged
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(Arrays.asList(
                "https://vscode-internal-36492-beta.beta01.cloud.kavia.ai:*",
                "http://localhost:*"
        ));
        config.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        config.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type", "Accept", "Origin",
                "X-Requested-With", "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"
        ));
        config.setMaxAge(3600L);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

**Note**: `setAllowedOriginPatterns()` is the correct method in Spring Boot 3.x when using `setAllowCredentials(true)`. No changes needed.

#### 4. OpenAPI Configuration

**File**: `src/main/java/com/example/postgresdemo/config/OpenApiConfig.java`

**No changes required**. The OpenAPI configuration is compatible with springdoc 2.x:

```java
package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PostgreSQL Questions & Answers API")
                        .version("1.0.0")
                        .description("RESTful API for managing questions and answers with PostgreSQL, JPA, and Hibernate. " +
                                     "This API provides CRUD operations for questions and their associated answers, " +
                                     "with pagination support and validation.")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
```

**Verification**: Swagger UI annotations (`@Tag`, `@Operation`, `@ApiResponse`) from `io.swagger.v3.oas.annotations.*` are unchanged in springdoc 2.x.

---

## Validation, JPA, and Web Layer Changes

### Validation Layer

**No behavioral changes**. Jakarta Validation 3.0 maintains the same API contracts:

- `@NotBlank`, `@Size`, `@NotNull`, `@Valid` work identically
- Validation error messages remain the same
- Spring Boot auto-configures `LocalValidatorFactoryBean`

**Test Example**:
```bash
# POST with invalid data should still return 400
curl -X POST http://localhost:3001/questions \
  -H "Content-Type: application/json" \
  -d '{"title":"ab","description":"test"}'

# Expected: 400 Bad Request with validation errors
```

### JPA and Hibernate 6.x

**Key Changes in Hibernate 6.4**:

1. **Query Language**: HQL syntax slightly improved (backward compatible)
2. **Criteria API**: Enhanced type safety
3. **N+1 Detection**: Built-in warnings for N+1 queries (logged at WARN level)
4. **Performance**: Improved query planning and batch fetching

**Impacts on ModernRepo**:

- **No code changes required** for simple CRUD operations
- JpaRepository methods (`findAll()`, `findById()`, `save()`, `delete()`) remain unchanged
- `@Query` annotations (if used) require review for deprecated HQL syntax (none in current code)

**Potential Issue**: If using native queries with Hibernate-specific types, review for compatibility.

**Verification**:
```bash
# Test pagination and sorting
curl "http://localhost:3001/questions?page=0&size=10&sort=createdAt,desc"
```

### Web Layer

**No changes required**. Spring Web MVC APIs are fully backward compatible:

- `@RestController`, `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping` unchanged
- `ResponseEntity`, `HttpStatus` unchanged
- `@RequestBody`, `@PathVariable`, `@RequestParam` unchanged
- Pagination via `Pageable` works identically

---

## OpenAPI/Swagger Migration (springdoc 1.x → 2.x)

### Changes in springdoc-openapi 2.x

1. **Artifact ID Change**: `springdoc-openapi-ui` → `springdoc-openapi-starter-webmvc-ui`
2. **Group ID Unchanged**: `org.springdoc`
3. **OpenAPI Spec Version**: Supports OpenAPI 3.1.0
4. **Swagger UI**: Updated to latest version (5.x)

### Code Changes

**No Java code changes required**. The following remain compatible:

- `@Tag`, `@Operation`, `@Parameter`, `@ApiResponse`, `@ApiResponses` annotations
- `OpenAPI` bean configuration
- Custom `Info`, `Contact`, `License` objects

### Configuration Changes

**File**: `src/main/resources/application.properties`

**Current Configuration** (already compatible):
```properties
# Springdoc OpenAPI configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs

# Swagger UI configuration - use relative paths to avoid mixed-content/CORS issues
springdoc.swagger-ui.config-url=/v3/api-docs/swagger-config
springdoc.swagger-ui.url=/v3/api-docs

# Ensure Swagger UI uses relative URLs
springdoc.swagger-ui.disable-swagger-default-url=true
```

**No changes required**. These properties work identically in springdoc 2.x.

### Verification Steps

After migration, test the following:

1. **Access Swagger UI**:
   ```
   https://vscode-internal-36492-beta.beta01.cloud.kavia.ai:3010/swagger-ui.html
   ```

2. **Access OpenAPI JSON**:
   ```
   https://vscode-internal-36492-beta.beta01.cloud.kavia.ai:3010/v3/api-docs
   ```

3. **Test API via Swagger UI**:
   - Expand "Questions" tag
   - Try "GET /questions" with pagination parameters
   - Try "POST /questions" with sample data

---

## Configuration & Properties Changes

### Spring Boot 2.x → 3.x Property Mappings

Most properties in ModernRepo are already compatible with Spring Boot 3.x. Review and update as needed:

**File**: `src/main/resources/application.properties`

#### Current Configuration (Spring Boot 2.5.5)

```properties
## Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
spring.datasource.url=jdbc:postgresql://localhost:5000/myapp
spring.datasource.username=appuser
spring.datasource.password=dbuser123

# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=update

# Server configuration
server.port=3001

# Forward headers strategy - Required for HTTPS proxy/nginx
server.forward-headers-strategy=framework

# Management endpoints configuration
management.endpoints.web.exposure.include=health,info
management.endpoints.web.base-path=/actuator
management.endpoint.health.show-details=always

# Springdoc OpenAPI configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.config-url=/v3/api-docs/swagger-config
springdoc.swagger-ui.url=/v3/api-docs
springdoc.swagger-ui.disable-swagger-default-url=true
```

#### Updated Configuration (Spring Boot 3.3.x)

**No changes required** for the current configuration. All properties are compatible with Spring Boot 3.x.

**Optional Enhancements** (for Spring Boot 3.x features):

```properties
## Spring DATASOURCE (DataSourceAutoConfiguration & DataSourceProperties)
spring.datasource.url=jdbc:postgresql://localhost:5000/myapp
spring.datasource.username=appuser
spring.datasource.password=dbuser123

# The SQL dialect makes Hibernate generate better SQL for the chosen database
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Hibernate ddl auto (create, create-drop, validate, update)
spring.jpa.hibernate.ddl-auto=update

# Server configuration
server.port=3001

# Forward headers strategy - Required for HTTPS proxy/nginx
server.forward-headers-strategy=framework

# Management endpoints configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoints.web.base-path=/actuator
management.endpoint.health.show-details=always

# Micrometer observation (optional, for enhanced observability)
management.tracing.sampling.probability=1.0
management.metrics.export.simple.enabled=true

# Springdoc OpenAPI configuration
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.config-url=/v3/api-docs/swagger-config
springdoc.swagger-ui.url=/v3/api-docs
springdoc.swagger-ui.disable-swagger-default-url=true

# Logging configuration (optional, for debugging Hibernate 6.x)
# logging.level.org.hibernate.SQL=DEBUG
# logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Deprecated Properties

**None found in current configuration**. Common deprecated properties not used in ModernRepo:

| Deprecated (Boot 2.x) | Replacement (Boot 3.x) | Notes |
|-----------------------|------------------------|-------|
| `spring.jpa.hibernate.use-new-id-generator-mappings` | (Removed) | Default behavior changed; no longer configurable |
| `management.metrics.export.prometheus.enabled` | `management.prometheus.metrics.export.enabled` | Only if using Prometheus |
| `spring.mvc.throw-exception-if-no-handler-found` | `spring.mvc.problemdetails.enabled` | For RFC 7807 Problem Details |

---

## Database and Hibernate 6.x Migration Notes

### Hibernate 6.x Key Changes

1. **ID Generation**: Default strategy changed from `native` to `sequence` for databases supporting sequences (including PostgreSQL). **ModernRepo already uses `@SequenceGenerator`**, so no changes needed.

2. **Query Language**: HQL syntax improvements (backward compatible for basic queries).

3. **Type System**: Enhanced handling of `java.time` types (e.g., `LocalDateTime`, `Instant`). ModernRepo uses `java.util.Date`, which remains supported.

4. **N+1 Query Detection**: Hibernate 6.x logs warnings for potential N+1 query issues. Review logs after migration.

5. **Dialect**: `org.hibernate.dialect.PostgreSQLDialect` is still valid but consider using version-specific dialects for optimal performance:
   ```properties
   # PostgreSQL 10+
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL10Dialect
   
   # PostgreSQL 12+ (recommended)
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

### Database Schema Compatibility

**No schema changes expected**. Hibernate 6.x maintains backward compatibility for:

- Table creation via `spring.jpa.hibernate.ddl-auto=update`
- Column definitions
- Sequence generators
- Indexes and constraints

**Verification**:
```bash
# Connect to database and verify schema
psql -h localhost -p 5000 -U appuser -d myapp -c "\d questions"
psql -h localhost -p 5000 -U appuser -d myapp -c "\d answers"
```

### Migration Strategy for Database

**Option 1: In-Place Update** (Recommended for Dev/Test):
1. Update application code (Jakarta migration + Spring Boot 3.x)
2. Start application with `spring.jpa.hibernate.ddl-auto=update`
3. Hibernate 6.x will inspect schema and apply minor updates (if any)

**Option 2: Explicit Schema Validation** (Recommended for Production):
1. Set `spring.jpa.hibernate.ddl-auto=validate` in production
2. Test migration in staging environment first
3. Generate schema diff using Hibernate tools:
   ```bash
   mvn hibernate6-maven-plugin:schema-export
   ```
4. Review and apply changes manually if needed

### Connection Pool

**HikariCP** is the default connection pool in Spring Boot 2.x and 3.x. No changes required unless using custom configuration.

**Optional Tuning** for Java 22 virtual threads:
```properties
# Enable virtual threads for Tomcat (Spring Boot 3.2+)
spring.threads.virtual.enabled=true

# HikariCP settings remain unchanged
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

---

## Build Tooling (Maven) & Java Toolchain

### Maven Wrapper Update

Update Maven wrapper to ensure Java 22 compatibility:

```bash
cd ModernRepo
mvn wrapper:wrapper -Dmaven=3.9.6
```

**Verify**:
```bash
./mvnw -version
# Expected: Apache Maven 3.9.6 or higher
```

### Maven Compiler Plugin

Add or update the compiler plugin in `pom.xml` (already covered in Dependency section):

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.13.0</version>
    <configuration>
        <source>22</source>
        <target>22</target>
        <release>22</release>
    </configuration>
</plugin>
```

**Alternative**: Use Maven Toolchains (see Prerequisites section).

### Maven Surefire Plugin

Ensure JUnit 5 and Java 22 compatibility:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

### Build Commands

**Clean and Compile**:
```bash
./mvnw clean compile
```

**Run Tests**:
```bash
./mvnw test
```

**Package Application**:
```bash
./mvnw clean package -DskipTests
```

**Run Application**:
```bash
./mvnw spring-boot:run
```

**Expected Output**:
```
Started PostgresDemoApplication in X.XXX seconds (JVM running for X.XXX)
Tomcat started on port(s): 3001 (http)
```

---

## Testing & Verification Plan

### Phase 1: Build Verification

**Step 1: Clean Build**
```bash
cd ModernRepo
./mvnw clean
```

**Step 2: Compile**
```bash
./mvnw compile
```

**Expected**: No compilation errors. If errors occur:
- Verify all `javax.*` imports replaced with `jakarta.*`
- Check for missing dependencies
- Ensure Java 22 is active: `java -version`

**Step 3: Run Unit Tests**
```bash
./mvnw test
```

**Expected**: All tests pass. Review `PostgresDemoApplicationTests.java` for context loading.

**Step 4: Package**
```bash
./mvnw clean package -DskipTests
```

**Expected**: JAR file created in `target/postgres-demo-0.0.1-SNAPSHOT.jar`

### Phase 2: Runtime Verification

**Step 1: Start Application**
```bash
./mvnw spring-boot:run
```

**Step 2: Verify Startup Logs**

Look for:
```
Tomcat initialized with port(s): 3001 (http)
Starting service [Tomcat]
Starting Servlet engine: [Apache Tomcat/10.1.x]
Initializing Spring embedded WebApplicationContext
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Hibernate: select version()
Started PostgresDemoApplication in X.XXX seconds (JVM running for X.XXX)
```

**Red Flags**:
- `ClassNotFoundException` for `javax.*` classes → Jakarta migration incomplete
- `NoSuchMethodError` → Version mismatch in dependencies
- `UnsupportedClassVersionError` → Java version mismatch

**Step 3: Test Actuator Endpoints**

```bash
# Health check
curl http://localhost:3001/actuator/health

# Expected: {"status":"UP","components":{"db":{"status":"UP","details":{...}}}}

# Info endpoint
curl http://localhost:3001/actuator/info
```

### Phase 3: API Functional Testing

**Test 1: Get All Questions (Pagination)**
```bash
curl "http://localhost:3001/questions?page=0&size=10&sort=createdAt,desc"
```

**Expected**:
- 200 OK
- JSON with `content`, `pageable`, `totalElements`, etc.
- No validation errors

**Test 2: Create Question**
```bash
curl -X POST http://localhost:3001/questions \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Java 22 Migration Test",
    "description": "Testing Spring Boot 3.3.x with Java 22"
  }'
```

**Expected**:
- 200 OK
- JSON with created question including `id`, `createdAt`, `updatedAt`

**Test 3: Validation Error Handling**
```bash
curl -X POST http://localhost:3001/questions \
  -H "Content-Type: application/json" \
  -d '{
    "title": "ab",
    "description": "Too short title"
  }'
```

**Expected**:
- 400 Bad Request
- Validation error message mentioning `@Size(min = 3, max = 100)`

**Test 4: Invalid Sort Property**
```bash
curl "http://localhost:3001/questions?sort=invalidField,asc"
```

**Expected**:
- 400 Bad Request
- Error message: "Invalid sort property: invalidField. Valid properties are: id, title, description, createdAt, updatedAt"

**Test 5: Update Question**
```bash
curl -X PUT http://localhost:3001/questions/1000 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "description": "Updated description"
  }'
```

**Expected**:
- 200 OK with updated question, or
- 404 Not Found if question doesn't exist

**Test 6: Delete Question**
```bash
curl -X DELETE http://localhost:3001/questions/1000
```

**Expected**:
- 200 OK, or
- 404 Not Found if question doesn't exist

### Phase 4: Swagger UI Verification

**Step 1: Access Swagger UI**

Open browser:
```
https://vscode-internal-36492-beta.beta01.cloud.kavia.ai:3010/swagger-ui.html
```

**Expected**:
- Swagger UI loads without errors
- "PostgreSQL Questions & Answers API" title displayed
- "Questions" and "Answers" tags visible

**Step 2: Test API via Swagger UI**

1. **Expand "GET /questions"**
   - Click "Try it out"
   - Set `page=0`, `size=5`, `sort=id,asc`
   - Click "Execute"
   - Verify 200 response with question list

2. **Expand "POST /questions"**
   - Click "Try it out"
   - Enter sample JSON:
     ```json
     {
       "title": "Swagger Test Question",
       "description": "Testing from Swagger UI"
     }
     ```
   - Click "Execute"
   - Verify 200 response with created question

3. **Test Validation**
   - Try POST with invalid data (title too short)
   - Verify 400 response

**Step 3: Verify OpenAPI JSON**

```bash
curl http://localhost:3001/v3/api-docs
```

**Expected**: Valid OpenAPI 3.x JSON with:
- `openapi: "3.0.1"` or `"3.1.0"`
- `info` section with API title
- `paths` section with `/questions`, `/answers` endpoints
- `components.schemas` with `Question`, `Answer` models

### Phase 5: Database Connectivity

**Test Database Operations**:

```bash
# Connect to database
psql -h localhost -p 5000 -U appuser -d myapp

# Check questions table
SELECT * FROM questions ORDER BY id DESC LIMIT 5;

# Check schema for audit fields
\d questions

# Expected columns: id, title, description, created_at, updated_at

# Verify sequence
SELECT last_value FROM question_sequence;
```

**Test CRUD Consistency**:
1. Create question via API
2. Verify in database: `SELECT * FROM questions WHERE title = 'Swagger Test Question';`
3. Update via API
4. Verify update in database
5. Delete via API
6. Verify deletion in database

### Phase 6: Performance and Memory

**Monitor Startup Time**:
```bash
./mvnw spring-boot:run | grep "Started PostgresDemoApplication"
```

**Compare**:
- Spring Boot 2.5.5 + Java 11: ~X.XX seconds
- Spring Boot 3.3.5 + Java 22: Should be similar or faster (target: < +10%)

**Monitor Memory Usage**:
```bash
# During runtime
jcmd <PID> VM.native_memory summary
jcmd <PID> GC.heap_info
```

**Java 22 Benefits**:
- Virtual threads (if enabled): Better scalability under high concurrency
- Improved GC: Reduced pause times with ZGC/G1GC

---

## Operational Verification (Actuator, Health, Metrics)

### Actuator Endpoints

**Available Endpoints** (based on `application.properties`):

```bash
# Health endpoint (with details)
curl http://localhost:3001/actuator/health | jq

# Info endpoint
curl http://localhost:3001/actuator/info | jq

# Metrics endpoint (if enabled)
curl http://localhost:3001/actuator/metrics | jq
```

### Health Checks

**Database Health**:
```bash
curl http://localhost:3001/actuator/health | jq '.components.db'
```

**Expected**:
```json
{
  "status": "UP",
  "details": {
    "database": "PostgreSQL",
    "validationQuery": "isValid()"
  }
}
```

**Disk Space Health**:
```bash
curl http://localhost:3001/actuator/health | jq '.components.diskSpace'
```

### Metrics (Spring Boot 3.x with Micrometer)

**Enable Metrics** in `application.properties`:
```properties
management.endpoints.web.exposure.include=health,info,metrics
```

**View Available Metrics**:
```bash
curl http://localhost:3001/actuator/metrics | jq '.names'
```

**JVM Memory Metrics**:
```bash
curl http://localhost:3001/actuator/metrics/jvm.memory.used | jq
```

**HTTP Request Metrics**:
```bash
curl http://localhost:3001/actuator/metrics/http.server.requests | jq
```

**Database Connection Pool Metrics**:
```bash
curl http://localhost:3001/actuator/metrics/hikaricp.connections.active | jq
```

### Logging

**Enable DEBUG Logging** for troubleshooting:

```properties
# Hibernate SQL logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Spring Data JPA
logging.level.org.springframework.data.jpa=DEBUG

# Spring Boot
logging.level.org.springframework.boot=INFO
```

**Check for Warnings**:
- N+1 query warnings from Hibernate 6.x
- Deprecated API usage warnings
- ClassLoader issues

---

## Rolling Back and Feature Flags

### Rollback Strategy

#### Option 1: Git-Based Rollback

**Pre-Migration Checkpoint**:
```bash
git checkout -b feature/java22-spring-boot-3-migration
git add .
git commit -m "Pre-migration checkpoint: Spring Boot 2.5.5 + Java 11"
```

**Post-Migration Checkpoint**:
```bash
git add .
git commit -m "Post-migration: Spring Boot 3.3.5 + Java 22"
git tag v1.0.0-springboot3-java22
```

**Rollback Commands**:
```bash
# Revert to previous commit
git revert HEAD

# Or reset to pre-migration state
git reset --hard <pre-migration-commit-hash>

# Or switch back to previous branch
git checkout main
```

#### Option 2: Docker/Container Rollback

**Build Images for Both Versions**:

```dockerfile
# Dockerfile.java11
FROM eclipse-temurin:11-jre
COPY target/postgres-demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

# Dockerfile.java22
FROM eclipse-temurin:22-jre
COPY target/postgres-demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**Deployment**:
```bash
# Deploy Java 22 version
docker run -d -p 3001:3001 modernrepo:java22

# Rollback to Java 11 version
docker stop <java22-container-id>
docker run -d -p 3001:3001 modernrepo:java11
```

#### Option 3: Blue-Green Deployment

**Setup**:
1. Deploy Spring Boot 3.3.5 + Java 22 to new environment (Green)
2. Keep existing Spring Boot 2.5.5 + Java 11 running (Blue)
3. Route traffic gradually using load balancer
4. Monitor metrics and logs
5. Switch fully to Green or rollback to Blue

**Nginx Configuration Example**:
```nginx
upstream backend {
    # Blue (Java 11)
    server localhost:3001 weight=1;
    
    # Green (Java 22)
    server localhost:3002 weight=0;  # Increase weight gradually
}
```

### Feature Flags

**Scenario**: Test Jakarta-migrated code alongside legacy code during phased rollout.

**Implementation** using Spring Profiles:

**1. Create Profiles**:
```properties
# application-java11.properties
spring.profiles.active=java11

# application-java22.properties
spring.profiles.active=java22
```

**2. Conditional Beans**:
```java
@Configuration
@Profile("java22")
public class Java22FeatureConfig {
    @Bean
    public FeatureService featureService() {
        return new Java22FeatureServiceImpl();
    }
}

@Configuration
@Profile("java11")
public class Java11FeatureConfig {
    @Bean
    public FeatureService featureService() {
        return new Java11FeatureServiceImpl();
    }
}
```

**3. Runtime Toggle**:
```bash
# Run with Java 22 profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=java22

# Run with Java 11 profile (fallback)
./mvnw spring-boot:run -Dspring-boot.run.profiles=java11
```

**Note**: For ModernRepo, feature flags are **not necessary** since the migration is straightforward (no behavioral changes).

### Database Rollback

**Schema Compatibility**: Hibernate 6.x doesn't introduce breaking schema changes. If rollback is needed:

1. **Backup Database Before Migration**:
   ```bash
   pg_dump -h localhost -p 5000 -U appuser -d myapp > pre-migration-backup.sql
   ```

2. **Restore on Rollback**:
   ```bash
   psql -h localhost -p 5000 -U appuser -d myapp < pre-migration-backup.sql
   ```

3. **Verify Data Integrity**:
   ```sql
   SELECT COUNT(*) FROM questions;
   SELECT COUNT(*) FROM answers;
   ```

---

## Post-Migration Hardening Checklist

### Security

- [ ] **Update Dependencies**: Run `mvn versions:display-dependency-updates` to check for newer versions
- [ ] **Security Scan**: Run `mvn org.owasp:dependency-check-maven:check` to identify CVEs
- [ ] **Enable HTTPS** in production (already configured for proxy with `server.forward-headers-strategy=framework`)
- [ ] **Review CORS Configuration**: Ensure only trusted origins are allowed
- [ ] **Actuator Security**: Restrict actuator endpoints in production:
  ```properties
  management.endpoints.web.exposure.include=health,info
  management.endpoint.health.show-details=when-authorized
  ```

### Performance

- [ ] **Enable Virtual Threads** (Spring Boot 3.2+):
  ```properties
  spring.threads.virtual.enabled=true
  ```
- [ ] **Optimize HikariCP**:
  ```properties
  spring.datasource.hikari.maximum-pool-size=20
  spring.datasource.hikari.minimum-idle=5
  spring.datasource.hikari.connection-timeout=30000
  ```
- [ ] **Enable HTTP/2** (if behind compatible proxy):
  ```properties
  server.http2.enabled=true
  ```
- [ ] **Add Caching**: Consider Spring Cache for frequently accessed questions
- [ ] **Database Indexing**: Review query performance and add indexes as needed:
  ```sql
  CREATE INDEX idx_questions_created_at ON questions(created_at DESC);
  ```

### Observability

- [ ] **Enable Distributed Tracing** (if using microservices):
  ```xml
  <dependency>
      <groupId>io.micrometer</groupId>
      <artifactId>micrometer-tracing-bridge-brave</artifactId>
  </dependency>
  ```
- [ ] **Configure Logging**: Use structured logging (JSON) for production:
  ```xml
  <dependency>
      <groupId>net.logstash.logback</groupId>
      <artifactId>logstash-logback-encoder</artifactId>
      <version>7.4</version>
  </dependency>
  ```
- [ ] **Monitor GC**: Enable GC logging:
  ```properties
  # JVM args
  -Xlog:gc*:file=/var/log/gc.log:time,uptime:filecount=10,filesize=100M
  ```
- [ ] **Set Up Alerts**: Monitor critical metrics (heap usage, DB connection pool, error rate)

### Testing

- [ ] **Regression Testing**: Execute full test suite including integration tests
- [ ] **Load Testing**: Run Apache JMeter or Gatling tests to compare performance
- [ ] **Canary Deployment**: Deploy to 10% of users first, monitor for errors
- [ ] **Synthetic Monitoring**: Set up automated checks for critical endpoints

### Documentation

- [ ] **Update README**: Document Java 22 and Spring Boot 3.3.x requirements
- [ ] **Update CI/CD**: Ensure pipelines use JDK 22 and Maven 3.9+
- [ ] **API Documentation**: Verify Swagger UI is accessible and accurate
- [ ] **Runbook**: Document rollback procedure and troubleshooting steps

### Compliance

- [ ] **License Review**: Verify no license conflicts with updated dependencies
- [ ] **Data Privacy**: Ensure GDPR/CCPA compliance if handling user data
- [ ] **Audit Logging**: Enable audit logs for sensitive operations

---

## Appendix: Sample Diffs and Snippets

### A. Complete Diff for `Question.java`

```diff
--- a/src/main/java/com/example/postgresdemo/model/Question.java
+++ b/src/main/java/com/example/postgresdemo/model/Question.java
@@ -1,8 +1,8 @@
 package com.example.postgresdemo.model;
 
-import javax.persistence.*;
-import javax.validation.constraints.NotBlank;
-import javax.validation.constraints.Size;
+import jakarta.persistence.*;
+import jakarta.validation.constraints.NotBlank;
+import jakarta.validation.constraints.Size;
 
 @Entity
 @Table(name = "questions")
```

### B. Complete Diff for `QuestionController.java`

```diff
--- a/src/main/java/com/example/postgresdemo/controller/QuestionController.java
+++ b/src/main/java/com/example/postgresdemo/controller/QuestionController.java
@@ -13,7 +13,7 @@ import org.springframework.data.domain.Page;
 import org.springframework.data.domain.Pageable;
 import org.springframework.http.ResponseEntity;
 import org.springframework.web.bind.annotation.*;
-import javax.validation.Valid;
+import jakarta.validation.Valid;
 
 @RestController
 @Tag(name = "Questions", description = "API endpoints for managing questions")
```

### C. Complete `pom.xml` Diff

```diff
--- a/pom.xml
+++ b/pom.xml
@@ -14,7 +14,7 @@
 	<parent>
 		<groupId>org.springframework.boot</groupId>
 		<artifactId>spring-boot-starter-parent</artifactId>
-		<version>2.5.5</version>
+		<version>3.3.5</version>
 		<relativePath/>
 	</parent>
 
 	<properties>
 		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
 		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
-		<java.version>11</java.version>
+		<java.version>22</java.version>
+		<maven.compiler.source>22</maven.compiler.source>
+		<maven.compiler.target>22</maven.compiler.target>
 	</properties>
 
@@ -53,9 +56,9 @@
 			<scope>test</scope>
 		</dependency>
-		<!-- Springdoc OpenAPI (Swagger) dependency -->
+		<!-- Springdoc OpenAPI (Swagger) dependency - Spring Boot 3.x compatible -->
 		<dependency>
 			<groupId>org.springdoc</groupId>
-			<artifactId>springdoc-openapi-ui</artifactId>
-			<version>1.6.15</version>
+			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
+			<version>2.5.0</version>
 		</dependency>
 	</dependencies>
 
 	<build>
 		<plugins>
 			<plugin>
 				<groupId>org.springframework.boot</groupId>
 				<artifactId>spring-boot-maven-plugin</artifactId>
 			</plugin>
+			<plugin>
+				<groupId>org.apache.maven.plugins</groupId>
+				<artifactId>maven-compiler-plugin</artifactId>
+				<version>3.13.0</version>
+				<configuration>
+					<source>22</source>
+					<target>22</target>
+					<release>22</release>
+				</configuration>
+			</plugin>
+			<plugin>
+				<groupId>org.apache.maven.plugins</groupId>
+				<artifactId>maven-surefire-plugin</artifactId>
+				<version>3.2.5</version>
+			</plugin>
 		</plugins>
 	</build>
```

### D. Shell Script for Automated Validation

```bash
#!/bin/bash
# validate-migration.sh - Automated validation script for Java 22 migration

set -e

echo "=== Java 22 Migration Validation ==="

# Check Java version
echo "1. Checking Java version..."
java -version 2>&1 | grep -q "22" || { echo "ERROR: Java 22 not found"; exit 1; }
echo "✓ Java 22 detected"

# Check Maven version
echo "2. Checking Maven version..."
mvn -version | grep -q "3.9" || { echo "WARNING: Maven 3.9+ recommended"; }
echo "✓ Maven version OK"

# Clean build
echo "3. Running clean build..."
./mvnw clean compile || { echo "ERROR: Compilation failed"; exit 1; }
echo "✓ Compilation successful"

# Run tests
echo "4. Running tests..."
./mvnw test || { echo "ERROR: Tests failed"; exit 1; }
echo "✓ Tests passed"

# Package
echo "5. Packaging application..."
./mvnw package -DskipTests || { echo "ERROR: Packaging failed"; exit 1; }
echo "✓ Packaging successful"

# Start application in background
echo "6. Starting application..."
./mvnw spring-boot:run > /tmp/springboot.log 2>&1 &
APP_PID=$!
sleep 15

# Check if application started
if ! kill -0 $APP_PID 2>/dev/null; then
    echo "ERROR: Application failed to start"
    cat /tmp/springboot.log
    exit 1
fi
echo "✓ Application started (PID: $APP_PID)"

# Test health endpoint
echo "7. Testing health endpoint..."
curl -f http://localhost:3001/actuator/health || { echo "ERROR: Health check failed"; kill $APP_PID; exit 1; }
echo "✓ Health check passed"

# Test API endpoint
echo "8. Testing API endpoint..."
curl -f "http://localhost:3001/questions?page=0&size=5" || { echo "ERROR: API test failed"; kill $APP_PID; exit 1; }
echo "✓ API test passed"

# Test Swagger UI
echo "9. Testing Swagger UI..."
curl -f http://localhost:3001/swagger-ui.html > /dev/null || { echo "ERROR: Swagger UI not accessible"; kill $APP_PID; exit 1; }
echo "✓ Swagger UI accessible"

# Cleanup
echo "10. Stopping application..."
kill $APP_PID
sleep 3
echo "✓ Application stopped"

echo "=== Migration Validation Complete ==="
echo "All checks passed! ✓"
```

**Usage**:
```bash
chmod +x validate-migration.sh
./validate-migration.sh
```

### E. CI/CD Pipeline Example (GitHub Actions)

```yaml
name: Java 22 CI/CD

on:
  push:
    branches: [ feature/java22-spring-boot-3-migration ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v4
    
    - name: Set up JDK 22
      uses: actions/setup-java@v4
      with:
        java-version: '22'
        distribution: 'temurin'
        cache: maven
    
    - name: Set up Maven 3.9+
      run: |
        wget https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
        tar xzf apache-maven-3.9.6-bin.tar.gz
        echo "$PWD/apache-maven-3.9.6/bin" >> $GITHUB_PATH
    
    - name: Build with Maven
      run: mvn clean package -DskipTests
      working-directory: ModernRepo
    
    - name: Run tests
      run: mvn test
      working-directory: ModernRepo
    
    - name: Run integration tests
      run: mvn verify
      working-directory: ModernRepo
      env:
        SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb
        SPRING_DATASOURCE_USERNAME: test
        SPRING_DATASOURCE_PASSWORD: test
    
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
          POSTGRES_DB: testdb
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
```

### F. Quick Reference Commands

```bash
# Build commands
./mvnw clean                          # Clean build artifacts
./mvnw compile                        # Compile only
./mvnw test                           # Run tests
./mvnw package                        # Build JAR
./mvnw spring-boot:run                # Run application

# Database commands
psql -h localhost -p 5000 -U appuser -d myapp -c "SELECT * FROM questions;"
pg_dump -h localhost -p 5000 -U appuser -d myapp > backup.sql
psql -h localhost -p 5000 -U appuser -d myapp < backup.sql

# Testing commands
curl http://localhost:3001/actuator/health
curl "http://localhost:3001/questions?page=0&size=5"
curl -X POST http://localhost:3001/questions -H "Content-Type: application/json" \
  -d '{"title":"Test","description":"Test description"}'

# Git commands
git checkout -b feature/java22-spring-boot-3-migration
git add .
git commit -m "Migrate to Spring Boot 3.3.5 + Java 22"
git push origin feature/java22-spring-boot-3-migration
```

---

## Summary

This migration guide provides a complete roadmap for upgrading ModernRepo from Spring Boot 2.5.5 (Java 11) to Spring Boot 3.3.5 (Java 22). Key takeaways:

1. **Namespace Migration**: Replace all `javax.*` imports with `jakarta.*`
2. **Dependency Updates**: Upgrade Spring Boot parent to 3.3.5 and springdoc to 2.5.0
3. **Java Version**: Update to Java 22 with appropriate Maven configuration
4. **No Breaking Changes**: Spring Web, JPA, and validation APIs remain functionally unchanged
5. **Configuration Compatibility**: Existing `application.properties` work without modification
6. **Testing Required**: Validate all endpoints, Swagger UI, and database operations
7. **Rollback Plan**: Use Git branches or blue-green deployment for safe rollback

**Estimated Migration Time**: 2-4 hours for code changes + testing

**Risk Level**: Low (straightforward upgrade with no architectural changes)

**Next Steps**:
1. Review this guide with the development team
2. Set up a staging environment with Java 22
3. Execute the migration in a feature branch
4. Run the full validation checklist
5. Deploy to production with monitoring

For questions or issues during migration, refer to:
- [Spring Boot 3.x Migration Guide](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide)
- [Hibernate 6.x Migration Guide](https://docs.jboss.org/hibernate/orm/6.4/migration-guide/migration-guide.html)
- [Jakarta EE 9 Namespace Changes](https://jakarta.ee/specifications/platform/9/jakarta-platform-spec-9.html)
