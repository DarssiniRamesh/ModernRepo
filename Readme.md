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

The application is set up to connect to a PostgreSQL container running on `localhost` port `3020`, which matches the `postgres-db` container. By default, it uses these environment variables and safe defaults (see `.env.example`):

```
DB_HOST=localhost
DB_PORT=3020
DB_NAME=modernrepo
DB_USERNAME=postgres
DB_PASSWORD=postgres
DB_URL=
```

You can set these variables in your environment or a shell profile. If you wish to override the database host, port, or credentials, set them appropriately. The container listens on port 3020.

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