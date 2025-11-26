# PostgreSQL Setup via Docker for ModernRepo

This guide provisions a PostgreSQL container and configures ModernRepo to use it.

## 1) Start PostgreSQL in Docker

Container name: modernrepo-postgres  
Credentials:
- POSTGRES_USER=modernuser
- POSTGRES_PASSWORD=modernpass
- POSTGRES_DB=modernrepo

Port mapping: host 5000 -> container 5432

Command:
docker run -d --name modernrepo-postgres \
  -e POSTGRES_USER=modernuser \
  -e POSTGRES_PASSWORD=modernpass \
  -e POSTGRES_DB=modernrepo \
  -p 5000:5432 \
  postgres:16-alpine

Verify it is running:
docker ps --filter "name=modernrepo-postgres"

Optional logs:
docker logs -f modernrepo-postgres

## 2) Connection details (for the application)

- host: localhost
- port: 5000
- database: modernrepo
- user: modernuser
- password: modernpass

Spring JDBC URL (two common options):
- In-network container name (recommended in multi-container envs):
  jdbc:postgresql://postgres-db:5432/modernrepo
- Host-mapped port (if you ran Docker with -p 5000:5432):
  jdbc:postgresql://localhost:5000/modernrepo

## 3) Configure ModernRepo to use this database

Option A: export environment variables in your shell
export SPRING_PROFILES_ACTIVE=prod
# Choose one of the URLs below
export SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-db:5432/modernrepo
# OR:
# export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5000/modernrepo
export SPRING_DATASOURCE_USERNAME=modernuser
export SPRING_DATASOURCE_PASSWORD=modernpass
# Optional
export SPRING_JPA_HIBERNATE_DDL_AUTO=update

Option B: copy .env.postgres-example to .env (if your runner loads it) and adjust as needed:
cp ModernRepo/Modern-Backend/.env.postgres-example ModernRepo/Modern-Backend/.env

## 4) Run ModernRepo

From ModernRepo root:
./start.sh

Or from the Modern-Backend folder:
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

Swagger UI: /docs
OpenAPI JSON: /openapi.json

Verification:
- The app binds on port 3000 by default (override with PORT). Visit:
  - http://localhost:3000/docs (Swagger UI)
  - http://localhost:3000/openapi.json
  - http://localhost:3000/questions
- Logs will print at startup:
  - Active profiles should include "prod"
  - Datasource URL target should be either:
    - jdbc:postgresql://postgres-db:5432/modernrepo
    - jdbc:postgresql://localhost:5000/modernrepo
  - Hibernate DDL auto should be "update" (unless overridden)

If tables do not exist initially, Hibernate auto DDL (update) will create/update schema for entities (questions, answers). Ensure the credentials are correct if connection fails.

## 5) Common Docker commands

Stop container:
docker stop modernrepo-postgres

Start container:
docker start modernrepo-postgres

Remove container (destructive):
docker rm -f modernrepo-postgres
