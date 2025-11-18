## Spring Boot, PostgreSQL, JPA, Hibernate REST API Demo

## Tutorial

Check out the complete tutorial on the CalliCoder blog -

[Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/)

## Requirements

- Java 21 (JDK 21)
- Maven Wrapper (included) which downloads Maven 3.9.x automatically
- PostgreSQL database

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://github.com/callicoder/spring-boot-postgresql-jpa-hibernate-rest-api-demo.git
```

**2. Configure PostgreSQL**

First, create a database named `postgres_demo`. Then, open `src/main/resources/application.properties` file and change the spring datasource username and password as per your PostgreSQL installation.

**3. Run the app**

Use the Maven Wrapper so you don't need a globally installed Maven. From the project root:

```bash
./mvnw spring-boot:run
```

To run on a specific port (e.g., 3001) without changing configuration:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001"
```

Alternatively, you can package the application in the form of a JAR file and then run it like so -

```bash
./mvnw clean package
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar --server.port=3001
```

Note:
- This project targets Java 21 and Spring Boot 3.2.x.
- If you have multiple JDKs installed, ensure JAVA_HOME points to JDK 21 before running `./mvnw`.