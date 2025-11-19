# Java 21 Modernization Guide for ModernRepo

## Executive Summary

This document provides a comprehensive migration plan for upgrading the ModernRepo project from its current technology stack (Spring Boot 2.5.5, Java 11, Hibernate 5.x, javax.* packages) to a modern Java 21 ecosystem (Spring Boot 3.3.x+, Java 21, Hibernate 6.x, jakarta.* packages). The migration will be performed as a side-by-side implementation, creating a new Java-21 module alongside the existing Modern-Backend to minimize risk and allow for gradual transition.

### Key Benefits

- **Long-term Support**: Java 21 is an LTS release with support until 2028
- **Performance**: Virtual threads, enhanced garbage collection, and optimized JVM performance
- **Modern APIs**: Pattern matching, records, sealed classes, and other language enhancements
- **Security**: Latest security patches and vulnerability fixes
- **Framework Updates**: Spring Boot 3.x with improved observability, native compilation support, and better performance

### Scope

This migration covers:
- Migration from Java 11 to Java 21
- Upgrade from Spring Boot 2.5.5 to Spring Boot 3.3.x+
- Transition from javax.* to jakarta.* namespace
- Upgrade from Hibernate 5.x to Hibernate 6.x
- Migration from springdoc-openapi 1.5.12 to springdoc-openapi v2.x
- Build system updates (Maven and Gradle)
- Containerization with Java 21 base images
- Database configuration modernization
- Development and production environment strategies

**Current Module**: `/home/kavia/workspace/code-generation/ModernRepo/Modern-Backend` (Port 3001)  
**New Module**: `/home/kavia/workspace/code-generation/ModernRepo/Java-21` (Port 3002 recommended)

---

## 1. Target Runtime and Framework Matrix

| Component | Current Version | Target Version | Notes |
|-----------|----------------|----------------|-------|
| **Java** | 11 | 21 (LTS) | Use Eclipse Temurin 21 for containers |
| **Spring Boot** | 2.5.5 | 3.3.x+ (latest 3.3.x or 3.4.x) | Major version upgrade |
| **Spring Framework** | 5.x (transitive) | 6.x+ | Comes with Spring Boot 3.x |
| **Hibernate** | 5.x (transitive) | 6.x+ | Bundled with Spring Boot 3.x |
| **Jakarta EE** | javax.* (Java EE 8) | jakarta.* (Jakarta EE 10+) | Namespace change required |
| **springdoc-openapi** | 1.5.12 | 2.x (e.g., 2.6.0) | Major API changes |
| **PostgreSQL Driver** | 42.x (transitive) | 42.7.x+ | Compatible with both versions |
| **H2 Database** | 1.x (transitive) | 2.x+ | URL syntax and mode changes |
| **Maven** | Any 3.6+ | 3.8.1+ recommended | For Java 21 support |
| **Gradle** | Any 7.x+ | 8.5+ recommended | For Java 21 support |

---

## 2. Dependency Upgrade Map

### 2.1 Current Dependencies (Modern-Backend)

Current `pom.xml` dependencies:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.5.5</version>
</parent>

<properties>
    <java.version>11</java.version>
</properties>

<dependencies>
    <!-- Spring Boot Starters -->
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
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Database Drivers -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- OpenAPI/Swagger -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-ui</artifactId>
        <version>1.5.12</version>
    </dependency>
</dependencies>
```

### 2.2 Target Dependencies (Java-21 Module)

Updated dependencies for Spring Boot 3.3.x:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version> <!-- Or latest 3.3.x/3.4.x -->
</parent>

<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>

<dependencies>
    <!-- Spring Boot Starters (no version needed, managed by parent) -->
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
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Database Drivers -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version> <!-- Explicit version for compatibility -->
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.224</version> <!-- H2 2.x required for Spring Boot 3 -->
        <scope>runtime</scope>
    </dependency>
    
    <!-- OpenAPI/Swagger v2 -->
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>2.6.0</version> <!-- Note: artifact name changed -->
    </dependency>
</dependencies>
```

### 2.3 Dependency Mapping Table

| Component | Current Artifact | Target Artifact | Comments |
|-----------|-----------------|-----------------|----------|
| **Spring Boot Parent** | spring-boot-starter-parent:2.5.5 | spring-boot-starter-parent:3.3.5 | Brings Spring 6, Hibernate 6 |
| **JPA Starter** | spring-boot-starter-data-jpa | spring-boot-starter-data-jpa | No artifact change, javax→jakarta internally |
| **Web Starter** | spring-boot-starter-web | spring-boot-starter-web | No artifact change |
| **Validation Starter** | spring-boot-starter-validation | spring-boot-starter-validation | No artifact change, javax→jakarta |
| **Test Starter** | spring-boot-starter-test | spring-boot-starter-test | No artifact change |
| **PostgreSQL Driver** | postgresql (managed) | postgresql:42.7.3 | Specify version for compatibility |
| **H2 Database** | h2:1.x (managed) | h2:2.2.224+ | **URL syntax changes required** |
| **springdoc OpenAPI** | springdoc-openapi-ui:1.5.12 | springdoc-openapi-starter-webmvc-ui:2.6.0 | **Artifact name changed** |

### 2.4 Gradle Dependency Changes

For Gradle users, the equivalent changes in `build.gradle`:

```groovy
// Current (Spring Boot 2.x)
plugins {
    id 'org.springframework.boot' version '2.5.5'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
    id 'java'
}

java {
    sourceCompatibility = '11'
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springdoc:springdoc-openapi-ui:1.5.12'
    runtimeOnly 'org.postgresql:postgresql'
    runtimeOnly 'com.h2database:h2'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

// Target (Spring Boot 3.x)
plugins {
    id 'org.springframework.boot' version '3.3.5'
    id 'io.spring.dependency-management' version '1.1.6'
    id 'java'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
    runtimeOnly 'org.postgresql:postgresql:42.7.3'
    runtimeOnly 'com.h2database:h2:2.2.224'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

---

## 3. Code Migration Guide: javax → jakarta Namespace

### 3.1 Overview

The transition from Java EE (javax.*) to Jakarta EE (jakarta.*) is the most pervasive code change. Spring Boot 3.x requires Jakarta EE 9+, which uses the `jakarta.*` namespace.

### 3.2 Package Mapping

| javax Package | jakarta Package | Used In |
|---------------|-----------------|---------|
| `javax.persistence.*` | `jakarta.persistence.*` | Entity classes, JPA annotations |
| `javax.validation.*` | `jakarta.validation.*` | Validation annotations (@NotBlank, @Size, etc.) |
| `javax.servlet.*` | `jakarta.servlet.*` | Servlet APIs (rarely used directly in Spring) |
| `javax.transaction.*` | `jakarta.transaction.*` | Transaction management |

### 3.3 Automated Migration

You can use automated tools for bulk replacement:

**IDE Find/Replace (IntelliJ IDEA, VS Code):**
1. Open "Replace in Files"
2. Enable "Regex" mode
3. Find: `import javax\.(persistence|validation|servlet|transaction)\.`
4. Replace: `import jakarta.$1.`

**Command-line (Linux/Mac):**
```bash
find src/main/java -name "*.java" -type f -exec sed -i 's/import javax\.persistence\./import jakarta.persistence./g' {} +
find src/main/java -name "*.java" -type f -exec sed -i 's/import javax\.validation\./import jakarta.validation./g' {} +
find src/main/java -name "*.java" -type f -exec sed -i 's/import javax\.servlet\./import jakarta.servlet./g' {} +
find src/main/java -name "*.java" -type f -exec sed -i 's/import javax\.transaction\./import jakarta.transaction./g' {} +
```

**Windows PowerShell:**
```powershell
Get-ChildItem -Path src\main\java -Filter *.java -Recurse | ForEach-Object {
    (Get-Content $_.FullName) -replace 'import javax\.persistence\.','import jakarta.persistence.' |
    Set-Content $_.FullName
}
# Repeat for validation, servlet, transaction packages
```

### 3.4 Manual Code Changes for ModernRepo

Based on the current codebase, the following files need javax→jakarta updates:

#### Question.java

**Before:**
```java
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
    // ... getters/setters
}
```

**After:**
```java
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
    // ... getters/setters
}
```

#### Answer.java

**Before:**
```java
import javax.persistence.*;
```

**After:**
```java
import jakarta.persistence.*;
```

#### AuditModel.java

**Before:**
```java
import javax.persistence.*;
```

**After:**
```java
import jakarta.persistence.*;
```

#### QuestionController.java

**Before:**
```java
import javax.validation.Valid;
```

**After:**
```java
import jakarta.validation.Valid;
```

#### AnswerController.java

Similar changes for `@Valid` annotations.

### 3.5 No Changes Required

The following remain unchanged:
- Spring Framework annotations (`@RestController`, `@Service`, `@Autowired`, etc.)
- Hibernate annotations (`@OnDelete`, etc.) - these are in `org.hibernate.*` package
- Jackson annotations (`@JsonIgnore`, etc.)

---

## 4. Spring Boot 2.x → 3.x Migration Notes

### 4.1 Actuator Path Changes

If you add Spring Boot Actuator in the future:

**Spring Boot 2.x:**
```properties
management.endpoints.web.base-path=/actuator
```

**Spring Boot 3.x:**
```properties
management.endpoints.web.base-path=/actuator
# Default remains /actuator, but health endpoint behavior changed
management.endpoint.health.show-details=when-authorized
```

### 4.2 Path Matching Strategy

**Spring Boot 2.x:** Default path matching was `ANT_PATH_MATCHER`.

**Spring Boot 3.x:** Default is `PATH_PATTERN_PARSER` (more strict).

If you encounter path matching issues:
```properties
# Fallback to old behavior if needed (not recommended)
spring.mvc.pathmatch.matching-strategy=ant_path_matcher
```

**Recommendation:** Stick with the new default (`PATH_PATTERN_PARSER`) and fix any incompatible paths.

### 4.3 Security Configuration (If Applicable)

If you add Spring Security later:

**Spring Boot 2.x:**
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/public/**").permitAll()
            .anyRequest().authenticated();
    }
}
```

**Spring Boot 3.x:**
```java
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

Key changes:
- `WebSecurityConfigurerAdapter` is deprecated/removed
- Use `SecurityFilterChain` bean
- `authorizeRequests()` → `authorizeHttpRequests()`
- `antMatchers()` → `requestMatchers()`

### 4.4 Exception Handling

No significant changes for `@RestControllerAdvice` and `@ExceptionHandler` patterns used in the current codebase. The existing `ResourceNotFoundException` handling will work as-is.

### 4.5 @ConfigurationProperties

**Spring Boot 2.x:**
```java
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String name;
    // getters/setters
}
```

**Spring Boot 3.x:** Same syntax, but ensure `@EnableConfigurationProperties` is used or `@ConfigurationPropertiesScan` is enabled. The `@Component` approach still works.

**No changes required for ModernRepo** as it doesn't currently use custom configuration properties.

### 4.6 @EnableWebMvc Impacts

The current codebase does **not** use `@EnableWebMvc`. If you add it:

- `@EnableWebMvc` disables Spring Boot auto-configuration for Web MVC
- Generally not needed for Spring Boot applications
- Use `WebMvcConfigurer` (as already done in `OpenApiConfig`) for customizations

**Recommendation:** Continue using `WebMvcConfigurer` without `@EnableWebMvc`.

### 4.7 Deprecated Properties

Check `application.properties` for deprecated properties. Run:
```bash
mvn spring-boot:run
# or
gradle bootRun
```

Spring Boot 3.x will log warnings for deprecated properties. Replace them accordingly.

---

## 5. OpenAPI/Swagger Migration (springdoc 1.x → 2.x)

### 5.1 Dependency Change

**Before (springdoc-openapi 1.x):**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-ui</artifactId>
    <version>1.5.12</version>
</dependency>
```

**After (springdoc-openapi 2.x):**
```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

**Key Change:** Artifact ID changed from `springdoc-openapi-ui` to `springdoc-openapi-starter-webmvc-ui`.

### 5.2 Configuration Changes

#### application.properties

**Before (springdoc 1.x):**
```properties
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.disable-swagger-default-url=true
```

**After (springdoc 2.x):**
```properties
# OpenAPI documentation path
springdoc.api-docs.path=/v3/api-docs

# Swagger UI path (default changed to /swagger-ui/index.html in v2)
springdoc.swagger-ui.path=/swagger-ui.html

# UI sorting options
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.tagsSorter=alpha

# Disable default Swagger Petstore URL
springdoc.swagger-ui.disable-swagger-default-url=true

# Try it out enabled by default (explicit configuration)
springdoc.swagger-ui.tryItOutEnabled=true
```

**Default Swagger UI Path Change:**
- springdoc 1.x: `/swagger-ui.html` (if configured)
- springdoc 2.x: Default is `/swagger-ui/index.html`, but can be customized to `/swagger-ui.html` via properties

**Recommendation:** Keep `/swagger-ui.html` for consistency with the current setup.

### 5.3 OpenAPI Configuration Bean

**Current OpenApiConfig.java (Spring Boot 2.x, springdoc 1.x):**
```java
package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server relativeServer = new Server();
        relativeServer.setUrl("/");
        relativeServer.setDescription("Same origin (inherits current host:port)");
        
        Server previewServer = new Server();
        previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001");
        previewServer.setDescription("Preview environment with port 3001");
        
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("ModernRepo Question & Answer API")
                .version("1.0.0")
                .description("RESTful API for managing questions and answers."))
            .servers(Arrays.asList(relativeServer, previewServer));
        
        return openAPI;
    }
}
```

**Target OpenApiConfig.java (Spring Boot 3.x, springdoc 2.x):**

The OpenAPI 3.0 spec and `io.swagger.v3.oas.models.*` API remain the same. **No code changes required** in the OpenAPI configuration bean itself.

However, update the port and URL for the new Java-21 module:

```java
package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Server 1: Relative path - inherits current host:port automatically
        Server relativeServer = new Server();
        relativeServer.setUrl("/");
        relativeServer.setDescription("Same origin (inherits current host:port)");
        
        // Server 2: Explicit preview URL with port 3002 for Java-21 module
        Server previewServer = new Server();
        previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3002");
        previewServer.setDescription("Preview environment with port 3002 (Java-21 module)");
        
        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("ModernRepo Question & Answer API - Java 21")
                .version("2.0.0")
                .description("RESTful API for managing questions and answers using Spring Boot 3, Java 21, and Hibernate 6."))
            .servers(Arrays.asList(relativeServer, previewServer));
        
        return openAPI;
    }
}
```

**Key Points:**
- Prefer relative path `"/"` as the first server entry (inherits current host:port)
- Explicit server URL should include the port number (`:3002` for Java-21 module)
- No package or API changes in `io.swagger.v3.oas.models.*`

### 5.4 Swagger UI Endpoints

| Version | OpenAPI JSON | Swagger UI |
|---------|--------------|------------|
| springdoc 1.x | `/v3/api-docs` | `/swagger-ui.html` |
| springdoc 2.x | `/v3/api-docs` | `/swagger-ui/index.html` (default) or `/swagger-ui.html` (configured) |

**For Java-21 module on port 3002:**
- OpenAPI JSON: `http://localhost:3002/v3/api-docs`
- Swagger UI: `http://localhost:3002/swagger-ui.html`

---

## 6. Persistence and Hibernate Changes

### 6.1 Hibernate 5.x → 6.x

Spring Boot 3.x includes Hibernate 6.x, which brings:
- Performance improvements
- Better Jakarta Persistence API support
- Updated SQL generation

**Most code will work without changes**, but be aware of:
- Deprecated APIs removed (if you use Hibernate-specific APIs directly)
- Query behavior changes (rare, but test thoroughly)

### 6.2 H2 Database Configuration

**Current (H2 1.x):**
```properties
# application-dev.properties
spring.datasource.url=jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

**Target (H2 2.x for Spring Boot 3):**
```properties
# application-dev.properties
# H2 2.x URL syntax with MODE=PostgreSQL for compatibility
spring.datasource.url=jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Dialect: Let Hibernate auto-detect or specify H2Dialect
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# DDL auto: create-drop for dev (recreates schema on each restart)
spring.jpa.hibernate.ddl-auto=create-drop

# Enable H2 console for debugging
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

**Key Changes:**
- **MODE=PostgreSQL**: H2 2.x can emulate PostgreSQL syntax, reducing differences between dev (H2) and prod (PostgreSQL). Highly recommended.
- **DB_CLOSE_ON_EXIT removed**: H2 2.x behavior changed; `DB_CLOSE_DELAY=-1` is usually sufficient.
- Consider adding `CASE_INSENSITIVE_IDENTIFIERS=TRUE` if you encounter case sensitivity issues.

**Alternative H2 URL for PostgreSQL Compatibility:**
```properties
spring.datasource.url=jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE
```

### 6.3 PostgreSQL Configuration

**Current (application-prod.properties):**
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/postgres_demo}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
```

**Target (no changes required, but can improve):**
```properties
# PostgreSQL connection (environment variable driven)
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/postgres_demo}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate dialect: Auto-detection works, but explicit is fine
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
# OR let Hibernate auto-detect (recommended for Hibernate 6.x):
# (remove the dialect line)

# DDL auto: update for most use cases, validate for strict production
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}

# Optional: Enable SQL logging for debugging
# spring.jpa.show-sql=false
# spring.jpa.properties.hibernate.format_sql=true
```

**Dialect Auto-Detection:** Hibernate 6.x is better at auto-detecting dialects. You can remove the explicit `hibernate.dialect` property unless you have a specific reason to override.

### 6.4 Schema Migration Advice

**Flyway or Liquibase:** For production systems, consider using Flyway or Liquibase for version-controlled schema migrations instead of `hibernate.ddl-auto=update`.

**Quick Integration (Flyway):**
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.jpa.hibernate.ddl-auto=validate
```

**Not required for this migration**, but recommended for production environments.

---

## 7. Build System Guidance

### 7.1 Maven Configuration

#### Complete pom.xml for Java-21 Module

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>postgres-demo-java21</artifactId>
    <version>2.0.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>postgres-demo-java21</name>
    <description>ModernRepo migrated to Java 21 and Spring Boot 3</description>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.3.5</version>
        <relativePath/>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <springdoc.version>2.6.0</springdoc.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
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
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- Database Drivers -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <version>42.7.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
            <scope>runtime</scope>
        </dependency>

        <!-- OpenAPI/Swagger UI (springdoc v2) -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            
            <!-- Maven Compiler Plugin (Java 21) -->
            <plugin>
                <groupId>org.apache.maven.compiler.plugin</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <release>21</release>
                </configuration>
            </plugin>
        </plugins>
    </build>

</project>
```

#### Building with Maven

```bash
# Clean and build
mvn clean package

# Run the application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Run with custom port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=3002"
```

### 7.2 Gradle Configuration

#### Complete build.gradle for Java-21 Module

```groovy
plugins {
    id 'org.springframework.boot' version '3.3.5'
    id 'io.spring.dependency-management' version '1.1.6'
    id 'java'
}

group = 'com.example'
version = '2.0.0-SNAPSHOT'
description = 'ModernRepo migrated to Java 21 and Spring Boot 3'

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot Starters
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'

    // Database Drivers
    runtimeOnly 'org.postgresql:postgresql:42.7.3'
    runtimeOnly 'com.h2database:h2:2.2.224'

    // OpenAPI/Swagger UI (springdoc v2)
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
}

tasks.named('test') {
    useJUnitPlatform()
}

// Ensure Java 21 is used for compilation
tasks.withType(JavaCompile) {
    options.release = 21
}
```

#### Building with Gradle

```bash
# Clean and build
./gradlew clean build

# Run the application
./gradlew bootRun

# Run with specific profile
./gradlew bootRun --args='--spring.profiles.active=dev'

# Run with custom port
./gradlew bootRun --args='--server.port=3002'
```

### 7.3 Plugin Updates

| Plugin | Current Version | Target Version | Notes |
|--------|----------------|----------------|-------|
| **maven-compiler-plugin** | 3.8.x (from parent) | 3.11.0+ | Supports Java 21 |
| **spring-boot-maven-plugin** | 2.5.5 (from parent) | 3.3.5 (from parent) | Auto-updated with parent |
| **Gradle Spring Boot Plugin** | 2.5.5 | 3.3.5 | Update in `plugins` block |
| **Gradle Dependency Management** | 1.0.x | 1.1.6 | Update in `plugins` block |

### 7.4 Gradle Wrapper Update

Update Gradle to 8.5+ for Java 21 support:

```bash
cd /home/kavia/workspace/code-generation/ModernRepo/Java-21
./gradlew wrapper --gradle-version 8.5
```

---

## 8. Containerization Plan

### 8.1 Dockerfile for Java 21

#### Multi-Stage Dockerfile (Maven)

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Copy Maven wrapper and pom.xml
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (layer caching)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# Copy application layers from build stage
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=3002
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Expose port
EXPOSE 3002

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:3002/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp app:app/lib/* com.example.postgresdemo.PostgresDemoApplication"]
```

#### Multi-Stage Dockerfile (Gradle)

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /workspace/app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies (layer caching)
RUN ./gradlew dependencies --no-daemon

# Copy source code
COPY src src

# Build the application
RUN ./gradlew bootJar --no-daemon
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
VOLUME /tmp

# Copy application layers from build stage
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=3002
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Expose port
EXPOSE 3002

# Health check (add spring-boot-starter-actuator for /actuator/health)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:3002/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp app:app/lib/* com.example.postgresdemo.PostgresDemoApplication"]
```

#### Simple Dockerfile (JAR-based)

```dockerfile
FROM eclipse-temurin:21-jre-alpine

# Set working directory
WORKDIR /app

# Copy the built JAR file
COPY target/*.jar app.jar

# Environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=3002
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Expose port
EXPOSE 3002

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:3002/questions || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 8.2 docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:16-alpine
    container_name: modernrepo-postgres
    environment:
      POSTGRES_DB: postgres_demo
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  java21-backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: java21-backend
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SERVER_PORT: 3002
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/postgres_demo
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
      JAVA_OPTS: "-Xmx512m -Xms256m"
    ports:
      - "3002:3002"
    depends_on:
      postgres:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:3002/questions"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

volumes:
  postgres-data:
```

### 8.3 Environment Configuration

**Environment Variables for Container:**

```bash
# Profile selection
SPRING_PROFILES_ACTIVE=prod

# Server port
SERVER_PORT=3002

# Database connection (PostgreSQL)
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres_demo
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# JVM memory settings
JAVA_OPTS=-Xmx512m -Xms256m

# Optional: Enable debug logging
# LOGGING_LEVEL_ROOT=DEBUG
```

### 8.4 Memory Flags

For Java 21 with containerized environments:

```bash
# Recommended JVM flags for containers
JAVA_OPTS="
  -Xmx512m 
  -Xms256m 
  -XX:+UseContainerSupport 
  -XX:MaxRAMPercentage=75.0 
  -XX:+UseZGC
  -XX:+ZGenerational
"
```

**Explanation:**
- `-Xmx512m`: Maximum heap size
- `-Xms256m`: Initial heap size
- `-XX:+UseContainerSupport`: Detect container memory limits
- `-XX:MaxRAMPercentage=75.0`: Use up to 75% of container memory
- `-XX:+UseZGC`: Z Garbage Collector (low latency)
- `-XX:+ZGenerational`: Generational ZGC (Java 21 feature)

---

## 9. CORS and Security Notes

### 9.1 CORS Configuration for Swagger

The current `OpenApiConfig` includes CORS configuration. **No changes required** for Spring Boot 3.x:

```java
@Bean
public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
        }
    };
}
```

**Security Note:** This configuration allows all origins (`allowedOriginPatterns("*")`), which is fine for development/testing. **For production**, restrict to specific domains:

```java
.allowedOriginPatterns(
    "https://yourdomain.com",
    "https://*.yourdomain.com"
)
```

### 9.2 Security Considerations

**Current Setup:** No Spring Security is configured.

**If Adding Security Later:**
- Whitelist Swagger endpoints: `/v3/api-docs`, `/swagger-ui/**`, `/swagger-ui.html`
- Use `requestMatchers()` instead of `antMatchers()` in Spring Boot 3.x

**Example:**
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

---

## 10. Environment and Profiles Strategy

### 10.1 Profile Overview

| Profile | Database | Purpose | Port |
|---------|----------|---------|------|
| **dev** | H2 in-memory | Local development, no external DB required | 3002 (configurable) |
| **prod** | PostgreSQL | Production deployment, external PostgreSQL | 3002 (configurable) |

### 10.2 application.properties (Base)

```properties
# Server configuration
server.port=${PORT:3002}
server.forward-headers-strategy=framework
server.use-forward-headers=true

# Active profile (dev by default)
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

# springdoc-openapi Swagger UI configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.disable-swagger-default-url=true

# JPA/Hibernate settings (profile-specific overrides below)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### 10.3 application-dev.properties

```properties
# Development profile: H2 in-memory database with PostgreSQL mode
spring.datasource.url=jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Hibernate dialect for H2
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# DDL auto: create-drop (recreates schema on restart)
spring.jpa.hibernate.ddl-auto=create-drop

# Enable H2 console for debugging
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Show SQL for development
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**H2 Console Access:** `http://localhost:3002/h2-console`
- JDBC URL: `jdbc:h2:mem:devdb`
- Username: `sa`
- Password: (leave blank)

### 10.4 application-prod.properties

```properties
# Production profile: PostgreSQL (environment variable driven)
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/postgres_demo}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate dialect: Auto-detection (can specify explicitly if needed)
# spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# DDL auto: update (or validate for strict production)
spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}

# Disable SQL logging in production
spring.jpa.show-sql=false

# Optional: Connection pool tuning
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### 10.5 Running with Profiles

**Development (H2):**
```bash
# Maven
mvn spring-boot:run

# Gradle
./gradlew bootRun

# Java JAR
java -jar target/postgres-demo-java21-2.0.0-SNAPSHOT.jar
```

**Production (PostgreSQL):**
```bash
# Maven
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"

# Gradle
./gradlew bootRun --args='--spring.profiles.active=prod'

# Java JAR
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres_demo
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=yourpassword
java -jar target/postgres-demo-java21-2.0.0-SNAPSHOT.jar
```

---

## 11. End-to-End Migration Checklist

### 11.1 Pre-Migration

- [ ] **Backup Current Code**: Ensure Modern-Backend is committed and backed up
- [ ] **Review Current Dependencies**: Document current versions (already done in pom.xml)
- [ ] **Identify Custom Code**: Review for any direct usage of javax.* or Hibernate-specific APIs
- [ ] **Test Modern-Backend**: Ensure current application works as expected on port 3001

### 11.2 Setup Java-21 Module

- [ ] **Create Java-21 Directory**: `/home/kavia/workspace/code-generation/ModernRepo/Java-21`
- [ ] **Initialize Build System**:
  - [ ] Maven: Copy `pom.xml`, update parent to 3.3.5, set Java 21
  - [ ] Gradle: Copy `build.gradle`, update plugins, set toolchain to Java 21
- [ ] **Copy Source Code**: Copy `src/` directory from Modern-Backend to Java-21
- [ ] **Copy Resources**: Copy `application.properties` and profile-specific properties

### 11.3 Dependency Updates

- [ ] **Update Spring Boot Parent**: Change from 2.5.5 to 3.3.5 in `pom.xml` or `build.gradle`
- [ ] **Update Java Version**: Set `java.version=21` in properties
- [ ] **Update springdoc-openapi**:
  - [ ] Remove `springdoc-openapi-ui:1.5.12`
  - [ ] Add `springdoc-openapi-starter-webmvc-ui:2.6.0`
- [ ] **Update H2 Version**: Ensure H2 2.x is used (managed by Spring Boot 3.x parent)
- [ ] **Update PostgreSQL Driver**: Specify version 42.7.3 explicitly

### 11.4 Code Changes

- [ ] **javax → jakarta Migration**:
  - [ ] Update `import javax.persistence.*` → `import jakarta.persistence.*`
  - [ ] Update `import javax.validation.*` → `import jakarta.validation.*`
  - [ ] Update `import javax.servlet.*` → `import jakarta.servlet.*` (if used)
  - [ ] Files to update: `Question.java`, `Answer.java`, `AuditModel.java`, `QuestionController.java`, `AnswerController.java`
- [ ] **OpenApiConfig Updates**:
  - [ ] Update server URL to use port 3002
  - [ ] Update API title/description to indicate Java 21 version
  - [ ] Verify CORS configuration (no changes needed, but review)

### 11.5 Configuration Updates

- [ ] **application.properties**:
  - [ ] Change `server.port=${PORT:3002}`
  - [ ] Verify `springdoc.swagger-ui.path=/swagger-ui.html`
- [ ] **application-dev.properties**:
  - [ ] Update H2 URL: `jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL`
  - [ ] Verify dialect: `org.hibernate.dialect.H2Dialect`
- [ ] **application-prod.properties**:
  - [ ] Review PostgreSQL configuration (no changes needed)
  - [ ] Consider removing explicit dialect (auto-detection)

### 11.6 Build and Compilation

- [ ] **Clean Build**:
  - [ ] Maven: `mvn clean package`
  - [ ] Gradle: `./gradlew clean build`
- [ ] **Resolve Compilation Errors**:
  - [ ] Fix any missing jakarta.* imports
  - [ ] Address deprecated API warnings
- [ ] **Run Tests**:
  - [ ] Maven: `mvn test`
  - [ ] Gradle: `./gradlew test`
  - [ ] Fix any failing tests due to API changes

### 11.7 Runtime Verification (Dev Profile)

- [ ] **Start Application** (dev profile, H2):
  - [ ] Maven: `mvn spring-boot:run`
  - [ ] Gradle: `./gradlew bootRun`
  - [ ] Verify startup logs: No errors, listens on port 3002
- [ ] **Check Endpoints**:
  - [ ] Root: `http://localhost:3002/`
  - [ ] Questions: `http://localhost:3002/questions`
  - [ ] OpenAPI JSON: `http://localhost:3002/v3/api-docs`
  - [ ] Swagger UI: `http://localhost:3002/swagger-ui.html`
- [ ] **H2 Console**: `http://localhost:3002/h2-console` (JDBC URL: `jdbc:h2:mem:devdb`)
- [ ] **Test CRUD Operations**:
  - [ ] Create a question via POST `/questions`
  - [ ] Retrieve questions via GET `/questions`
  - [ ] Update a question via PUT `/questions/{id}`
  - [ ] Delete a question via DELETE `/questions/{id}`
  - [ ] Create answers via POST `/questions/{id}/answers`

### 11.8 Runtime Verification (Prod Profile)

- [ ] **Start PostgreSQL** (Docker or local):
  ```bash
  docker run -d --name postgres -p 5432:5432 \
    -e POSTGRES_DB=postgres_demo \
    -e POSTGRES_USER=postgres \
    -e POSTGRES_PASSWORD=postgres \
    postgres:16-alpine
  ```
- [ ] **Start Application** (prod profile):
  ```bash
  export SPRING_PROFILES_ACTIVE=prod
  export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres_demo
  export SPRING_DATASOURCE_USERNAME=postgres
  export SPRING_DATASOURCE_PASSWORD=postgres
  mvn spring-boot:run
  ```
- [ ] **Verify Database Connection**: Check logs for "HHH000400: Using dialect" with PostgreSQL
- [ ] **Test CRUD Operations**: Same as dev profile tests
- [ ] **Verify Data Persistence**: Restart app, check if data persists

### 11.9 Swagger UI Testing

- [ ] **Access Swagger UI**: `http://localhost:3002/swagger-ui.html`
- [ ] **Server Selection**: Verify two servers listed:
  - [ ] Relative path `/` (default)
  - [ ] Explicit `http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3002`
- [ ] **Try It Out**: Execute sample requests:
  - [ ] GET `/questions` → 200 OK
  - [ ] POST `/questions` → 200/201 OK (with valid JSON body)
  - [ ] PUT `/questions/{id}` → 200 OK
  - [ ] DELETE `/questions/{id}` → 200 OK
- [ ] **Verify CORS**: No CORS errors in browser console

### 11.10 Log Review

- [ ] **Startup Logs**: No ERROR or WARN messages related to:
  - [ ] Missing beans
  - [ ] Deprecated properties
  - [ ] Unsupported configurations
- [ ] **Runtime Logs**: No exceptions during CRUD operations
- [ ] **SQL Logs** (dev profile): Verify SQL statements are generated correctly

### 11.11 Containerization (Optional)

- [ ] **Create Dockerfile**: Use multi-stage build with Eclipse Temurin 21
- [ ] **Build Docker Image**:
  ```bash
  docker build -t java21-backend:latest .
  ```
- [ ] **Run Container** (dev profile):
  ```bash
  docker run -p 3002:3002 -e SPRING_PROFILES_ACTIVE=dev java21-backend:latest
  ```
- [ ] **Run Container** (prod profile with external PostgreSQL):
  ```bash
  docker run -p 3002:3002 \
    -e SPRING_PROFILES_ACTIVE=prod \
    -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres_demo \
    -e SPRING_DATASOURCE_USERNAME=postgres \
    -e SPRING_DATASOURCE_PASSWORD=postgres \
    java21-backend:latest
  ```
- [ ] **docker-compose**: Use provided `docker-compose.yml` to start both app and PostgreSQL

### 11.12 Side-by-Side Operation

- [ ] **Modern-Backend Running**: Port 3001, Spring Boot 2.5.5, Java 11
- [ ] **Java-21 Module Running**: Port 3002, Spring Boot 3.3.5, Java 21
- [ ] **No Port Conflicts**: Verify both can run simultaneously
- [ ] **Compare Responses**: Send identical requests to both, compare output
- [ ] **Performance Comparison**: Measure response times (optional)

### 11.13 Final Validation

- [ ] **Code Review**: Review all changes for correctness
- [ ] **Documentation Update**: Update README.md for Java-21 module
- [ ] **Test Coverage**: Run all tests, achieve same or better coverage
- [ ] **Security Scan**: Run dependency vulnerability checks:
  - [ ] Maven: `mvn dependency-check:check`
  - [ ] Gradle: `./gradlew dependencyCheckAnalyze`
- [ ] **Commit Changes**: Commit Java-21 module to version control

---

## 12. Rollback and Side-by-Side Strategy

### 12.1 Rationale

A side-by-side migration approach minimizes risk by:
- Keeping the current Modern-Backend intact and operational
- Allowing gradual testing and validation of the new Java-21 module
- Enabling easy rollback if issues arise
- Facilitating A/B comparison and performance benchmarking

### 12.2 Directory Structure

```
/home/kavia/workspace/code-generation/ModernRepo/
├── Modern-Backend/           # Existing Spring Boot 2.5.5 + Java 11
│   ├── src/
│   ├── pom.xml
│   └── ... (port 3001)
├── Java-21/                  # New Spring Boot 3.3.x + Java 21
│   ├── src/
│   ├── pom.xml (or build.gradle)
│   └── ... (port 3002)
├── README.md                 # Project overview
└── docker-compose.yml        # (optional) Both modules + PostgreSQL
```

### 12.3 Port Allocation

| Module | Port | Purpose |
|--------|------|---------|
| **Modern-Backend** | 3001 | Current production-like environment |
| **Java-21** | 3002 | New modernized environment (recommended) |

**Rationale for Port 3002:**
- Avoids conflict with Modern-Backend on 3001
- Allows both to run simultaneously for comparison
- Aligns with preview URL strategy: `http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3002`

### 12.4 Configuration Alignment

**Modern-Backend (Port 3001):**
```properties
server.port=${PORT:3001}
```

**Java-21 (Port 3002):**
```properties
server.port=${PORT:3002}
```

**OpenAPI Server Configuration:**

Modern-Backend:
```java
previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3001");
```

Java-21:
```java
previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3002");
```

### 12.5 Database Strategy

**Development (H2):**
- Both modules use in-memory H2, no shared state
- Each module has its own isolated database

**Production (PostgreSQL):**
- **Option 1**: Separate databases
  - Modern-Backend: `postgres_demo` (or `postgres_demo_old`)
  - Java-21: `postgres_demo_java21`
- **Option 2**: Shared database
  - Both connect to `postgres_demo`
  - Use different schema or table prefixes (not recommended)
- **Recommendation**: Use separate databases initially, migrate data after full validation

### 12.6 Running Both Modules Simultaneously

**Terminal 1 (Modern-Backend):**
```bash
cd /home/kavia/workspace/code-generation/ModernRepo/Modern-Backend
mvn spring-boot:run
# Listens on port 3001
```

**Terminal 2 (Java-21):**
```bash
cd /home/kavia/workspace/code-generation/ModernRepo/Java-21
mvn spring-boot:run
# Listens on port 3002
```

**Access Points:**
- Modern-Backend Swagger: `http://localhost:3001/swagger-ui.html`
- Java-21 Swagger: `http://localhost:3002/swagger-ui.html`

### 12.7 Rollback Plan

If issues are discovered in the Java-21 module:

1. **Stop Java-21 Application**:
   ```bash
   # If running via mvn/gradle: Ctrl+C
   # If Docker: docker stop java21-backend
   ```

2. **Continue Using Modern-Backend**: No changes required, already running on port 3001

3. **Fix Issues in Java-21 Module**: Debug, fix code, rebuild

4. **Re-test**: Repeat verification steps from Section 11

5. **Gradual Cutover**: Once fully validated, update documentation and references to use port 3002

### 12.8 Migration Cutover

When ready to fully migrate:

1. **Update External References**: Change all documentation, URLs, load balancers to port 3002
2. **Data Migration**: If using separate databases, migrate data from old to new
3. **Deprecate Modern-Backend**: Mark as deprecated, eventually decommission
4. **Rename Java-21**: Optionally rename to `Modern-Backend-v2` or similar

---

## 13. Risks and Pitfalls

### 13.1 Common Issues

| Risk | Impact | Mitigation |
|------|--------|------------|
| **Missed javax → jakarta imports** | Compilation errors | Use IDE/regex find-replace, test thoroughly |
| **H2 URL syntax incompatibility** | H2 connection failures | Use `MODE=PostgreSQL` in URL, test dev profile |
| **springdoc artifact name change** | Dependency resolution failure | Update to `springdoc-openapi-starter-webmvc-ui` |
| **Hibernate 6 query behavior changes** | Unexpected query results | Review custom JPQL/HQL queries, test extensively |
| **Path matching strictness** | 404 errors for existing endpoints | Test all endpoints, adjust paths if needed |
| **Deprecated properties** | Runtime warnings | Review Spring Boot 3 migration guide, update properties |
| **Java 21 toolchain not installed** | Build failures | Install JDK 21, configure IDE/build tools |
| **PostgreSQL dialect auto-detection issues** | SQL generation errors | Explicitly set dialect if needed |

### 13.2 Testing Gaps

- **Integration Tests**: Ensure tests cover all endpoints and database interactions
- **Lazy Loading**: Test Hibernate lazy loading behavior (Hibernate 6 is stricter)
- **Transaction Management**: Verify `@Transactional` behavior unchanged
- **Pagination**: Test `Pageable` behavior with Hibernate 6

### 13.3 Performance Considerations

- **Startup Time**: Spring Boot 3.x may have faster/slower startup (benchmark)
- **Memory Usage**: Java 21 + Spring 6 may have different memory profiles (monitor)
- **GC Behavior**: Test with different GC algorithms (ZGC, G1GC)

### 13.4 Security Vulnerabilities

- **Dependency Vulnerabilities**: Run `mvn dependency-check:check` to scan for CVEs
- **Outdated Libraries**: Keep Spring Boot and dependencies up-to-date
- **CORS Configuration**: Review and restrict `allowedOriginPatterns` for production

---

## 14. Appendices

### 14.1 Sample pom.xml (Spring Boot 3.3.x)

See Section 7.1 for the complete Maven `pom.xml`.

**Quick Reference:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
</parent>

<properties>
    <java.version>21</java.version>
    <springdoc.version>2.6.0</springdoc.version>
</properties>

<dependencies>
    <!-- Spring Boot starters (no version, managed by parent) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springdoc</groupId>
        <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
        <version>${springdoc.version}</version>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.7.3</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.224</version>
    </dependency>
</dependencies>
```

### 14.2 Sample build.gradle (Spring Boot 3.3.x)

See Section 7.2 for the complete Gradle `build.gradle`.

**Quick Reference:**
```groovy
plugins {
    id 'org.springframework.boot' version '3.3.5'
    id 'io.spring.dependency-management' version '1.1.6'
    id 'java'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0'
    runtimeOnly 'org.postgresql:postgresql:42.7.3'
    runtimeOnly 'com.h2database:h2:2.2.224'
}
```

### 14.3 Sample application.properties (Java-21)

**Base Configuration (application.properties):**
```properties
server.port=${PORT:3002}
server.forward-headers-strategy=framework
server.use-forward-headers=true

spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.disable-swagger-default-url=true

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

**Dev Profile (application-dev.properties):**
```properties
spring.datasource.url=jdbc:h2:mem:devdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

**Prod Profile (application-prod.properties):**
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/postgres_demo}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME:postgres}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=${SPRING_JPA_HIBERNATE_DDL_AUTO:update}
spring.jpa.show-sql=false

spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
```

### 14.4 Sample OpenApiConfig.java (Spring Boot 3.x, springdoc v2)

```java
package com.example.postgresdemo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server relativeServer = new Server();
        relativeServer.setUrl("/");
        relativeServer.setDescription("Same origin (inherits current host:port)");
        
        Server previewServer = new Server();
        previewServer.setUrl("http://vscode-internal-40632-beta.beta01.cloud.kavia.ai:3002");
        previewServer.setDescription("Preview environment with port 3002 (Java-21 module)");
        
        return new OpenAPI()
            .info(new Info()
                .title("ModernRepo Question & Answer API - Java 21")
                .version("2.0.0")
                .description("RESTful API for managing questions and answers using Spring Boot 3, Java 21, and Hibernate 6."))
            .servers(Arrays.asList(relativeServer, previewServer));
    }
}
```

### 14.5 Sample WebConfig.java (CORS)

**If you separate CORS from OpenApiConfig:**

```java
package com.example.postgresdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .exposedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

**Note:** For production, restrict `allowedOriginPatterns` to specific domains.

### 14.6 Sample Dockerfile (Eclipse Temurin 21)

See Section 8.1 for complete Dockerfile examples.

**Quick Reference (Simple JAR-based):**
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=3002
ENV JAVA_OPTS="-Xmx512m -Xms256m"
EXPOSE 3002
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 14.7 Sample docker-compose.yml

See Section 8.2 for the complete `docker-compose.yml`.

**Quick Reference:**
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: postgres_demo
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
  
  java21-backend:
    build: .
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SERVER_PORT: 3002
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/postgres_demo
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: postgres
    ports:
      - "3002:3002"
    depends_on:
      - postgres
```

---

## 15. Quick Reference: Command Summary

### 15.1 Build Commands

```bash
# Maven
mvn clean package
mvn spring-boot:run
mvn test

# Gradle
./gradlew clean build
./gradlew bootRun
./gradlew test
```

### 15.2 Run Commands

```bash
# Development (H2)
mvn spring-boot:run
# OR
./gradlew bootRun
# OR
java -jar target/postgres-demo-java21-2.0.0-SNAPSHOT.jar

# Production (PostgreSQL)
export SPRING_PROFILES_ACTIVE=prod
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres_demo
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
mvn spring-boot:run
```

### 15.3 Docker Commands

```bash
# Build image
docker build -t java21-backend:latest .

# Run container (dev)
docker run -p 3002:3002 -e SPRING_PROFILES_ACTIVE=dev java21-backend:latest

# Run container (prod)
docker run -p 3002:3002 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/postgres_demo \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  java21-backend:latest

# docker-compose
docker-compose up -d
docker-compose down
```

### 15.4 Verification URLs

| Endpoint | URL |
|----------|-----|
| **Root** | `http://localhost:3002/` |
| **Questions** | `http://localhost:3002/questions` |
| **OpenAPI JSON** | `http://localhost:3002/v3/api-docs` |
| **Swagger UI** | `http://localhost:3002/swagger-ui.html` |
| **H2 Console** | `http://localhost:3002/h2-console` (dev profile) |

---

## 16. Conclusion

This modernization guide provides a complete roadmap for migrating the ModernRepo project from Spring Boot 2.5.5 with Java 11 to Spring Boot 3.3.x with Java 21. By following the side-by-side approach, you can minimize risk, validate thoroughly, and ensure a smooth transition to the modern Java ecosystem.

**Next Steps:**

1. Create the Java-21 module directory
2. Copy and update build configuration files (pom.xml or build.gradle)
3. Copy source code and apply javax → jakarta changes
4. Update application properties for port 3002
5. Build and run the application in dev profile
6. Test all endpoints via Swagger UI
7. Validate with PostgreSQL in prod profile
8. Compare side-by-side with Modern-Backend
9. Containerize and deploy

**Success Criteria:**

- All tests pass
- Swagger UI accessible on port 3002
- CRUD operations work in both dev (H2) and prod (PostgreSQL) profiles
- No compilation errors or runtime exceptions
- Clean logs with no warnings
- Side-by-side operation with Modern-Backend on port 3001

**Support:**

- Refer to official Spring Boot 3 Migration Guide: https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-3.0-Migration-Guide
- springdoc-openapi v2 documentation: https://springdoc.org/
- Java 21 documentation: https://openjdk.org/projects/jdk/21/

Good luck with your migration!
