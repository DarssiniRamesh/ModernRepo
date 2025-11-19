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

First, create a database named `postgres_demo`. Then, open `src/main/resources/application.properties` file and change the spring datasource username and password as per your PostgreSQL installation.

If running in Docker, the included `docker-compose.yml` in `ModernRepo/DB` will launch a minimal Postgres DB (port 3002) and Adminer at (port 8080). Use these connection settings for backend:

- Host: `localhost` (Spring Boot) or `db` (Adminer in Compose)
- Port: `3002`
- DB Name: `postgres_demo`
- Username: `postgres`
- Password: `postgres`
- JDBC URL: `jdbc:postgresql://localhost:3002/postgres_demo`

To use Adminer for visualizing the DB tables, visit [http://localhost:8080](http://localhost:8080), and log in with these details:
- System: PostgreSQL
- Server: db
- Username: postgres
- Password: postgres
- Database: postgres_demo

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