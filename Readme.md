## Spring Boot, PostgreSQL, JPA, Hibernate REST API Demo

## Tutorial

Check out the complete tutorial on the CalliCoder blog -

[Spring Boot, PostgreSQL, JPA, Hibernate RESTful CRUD API Example](https://www.callicoder.com/spring-boot-jpa-hibernate-postgresql-restful-crud-api-example/)

## Steps to Setup

**1. Clone the repository**

```bash
git clone https://github.com/callicoder/spring-boot-postgresql-jpa-hibernate-rest-api-demo.git
```

**2. Configure PostgreSQL**

The application connects to a local `postgres-db` container running on `localhost:3020` by default.
- **SSL is strictly disabled for local/dev with `?sslmode=disable` and HikariCP `ssl=false`**.
- **DO NOT remove `sslmode=disable` from the JDBC URL for local/dev.** This ensures JDBC and HikariCP do not attempt SSL.
- To force specific SSL settings, use env variables as shown in the provided `.env.example`.

Example environment variable setup (for local/dev):

```
SPRING_DATASOURCE_HOST=localhost
SPRING_DATASOURCE_PORT=3020
SPRING_DATASOURCE_DB=modernrepo
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_SSLMODE=disable           # (MANDATORY for local, do not use SSL locally)
DB_URL=                                     # If you set this, make sure to add `?sslmode=disable`
```

You can set these in your shell profile or as env vars. By default, the database listens on port 3020 and SSL must be off for local containers; for production enable SSL as appropriate (and configure CA certs).

**3. Run the app**

Type the following command from the root directory of the project to run it -

```bash
mvn spring-boot:run
```

Alternatively, you can package the application in the form of a JAR file and then run it like so -

```bash
mvn clean package
java -jar target/postgres-demo-0.0.1-SNAPSHOT.jar
```