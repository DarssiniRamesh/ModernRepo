## Spring Boot, PostgreSQL, JPA, Hibernate REST API Demo

## Tutorial

Check out the complete tutorial on the CalliCoder blog -

[Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/)

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://github.com/callicoder/spring-boot-postgresql-jpa-hibernate-rest-api-demo.git
```

**2. Database Configuration (postgres or H2)**

This project now supports two database profiles:

- **dev (default):** Uses H2 in-memory for local development. No setup needed, just start the app.
- **prod:** Connects to a PostgreSQL server (recommended for production).

__To use PostgreSQL (prod profile):__
1. Create a database named `postgres_demo` (or change the DB name as desired).
2. Set the following environment variables to override defaults:
    - `SPRING_PROFILES_ACTIVE=prod`
    - `SPRING_DATASOURCE_URL` (e.g., `jdbc:postgresql://localhost:5432/postgres_demo`)
    - `SPRING_DATASOURCE_USERNAME` (e.g., `postgres`)
    - `SPRING_DATASOURCE_PASSWORD` (your PostgreSQL password)

__To use H2 (dev profile, default):__
- No extra setup—runs automatically if PostgreSQL properties are not provided.
- The H2 console will be available at `/h2-console` during runtime for debugging.

You can also manually activate a profile on the command line:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
# or for dev
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```
Or, use env variables:
```bash
export SPRING_PROFILES_ACTIVE=prod
# (or dev)
```

**3. Run the app**

Type the following command from the root directory of the project to run it:

```bash
mvn spring-boot:run
```
Or using Maven Wrapper (recommended for container/CI environments):
```bash
./mvnw spring-boot:run
```

Alternatively, you can package the application in the form of a JAR file and run it directly:

```bash
mvn clean package
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar
```
Or with Maven Wrapper:
```bash
./mvnw clean package
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar
```

---

### Start and Build Commands (for container/preview environments)

**Build the executable jar:**
```bash
./mvnw clean package
```

**Run with dev profile:**
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
# OR
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

**Run with prod profile** (requires PostgreSQL and DB environment variables):
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
# OR
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

**Default Start Command for CI/Preview Containers:**
```bash
./mvnw spring-boot:run
```
or, if the jar is already built:
```bash
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar
```