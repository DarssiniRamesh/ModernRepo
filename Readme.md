## Spring Boot, PostgreSQL, JPA, Hibernate REST API Demo

## Tutorial

Check out the complete tutorial on the CalliCoder blog -

[Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/)

## Steps to Setup

**1. Clone the repository**

If your environment requires it, make the wrapper script executable:

```bash
chmod +x mvnw
```

```bash
git clone https://github.com/callicoder/spring-boot-postgresql-jpa-hibernate-rest-api-demo.git
```

**2. Configure PostgreSQL**

First, create a database named `postgres_demo`. Then, open `src/main/resources/application.properties` file and change the spring datasource username and password as per your PostgreSQL installation.

**3. Run the app**

This project includes the Maven Wrapper, so you don't need a global Maven installation. From the project root, run:

```bash
./mvnw spring-boot:run
```

Alternatively, you can package the application as a JAR and then run it:

```bash
./mvnw clean package
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar
```