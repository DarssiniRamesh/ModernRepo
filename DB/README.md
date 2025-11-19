# PostgreSQL Database Container

This folder contains the Docker Compose configuration for running a PostgreSQL 16 database for the ModernRepo application.

## Quick Start

### Start the Database

```bash
cd ModernRepo/DB
docker compose up -d
```

This will:
- Pull the PostgreSQL 16 image (if not already present)
- Create a named volume `modernrepo-pgdata` for persistent data storage
- Start the database container in detached mode
- Expose PostgreSQL on port 5432 (configurable via `.env`)

### Stop the Database

```bash
docker compose down
```

To stop and **remove all data**:
```bash
docker compose down -v
```

⚠️ **Warning:** The `-v` flag will delete the persistent volume and all database data.

### Check Database Status

```bash
# Check if container is running
docker compose ps

# View logs
docker compose logs

# Follow logs in real-time
docker compose logs -f

# Check health status
docker compose ps postgres
```

## Configuration

### Environment Variables

The database is configured using the `.env` file in this directory:

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_DB` | `myapp` | Database name |
| `POSTGRES_USER` | `appuser` | Database user |
| `POSTGRES_PASSWORD` | `dbuser123` | Database password |
| `POSTGRES_PORT` | `5432` | Port to expose on host |
| `POSTGRES_HOST` | `localhost` | Host address (used by backend) |

**Important:** Change `POSTGRES_PASSWORD` for production deployments.

### Data Persistence

Database data is persisted using a Docker named volume `modernrepo-pgdata`. This ensures:
- Data survives container restarts
- Data is retained even if the container is removed (unless you use `docker compose down -v`)
- Better performance compared to bind mounts

## Backend Integration

### Connecting the Spring Boot Backend

The backend application (in `Modern-Backend/`) connects to this database when running in **production profile**.

#### Step 1: Ensure Database is Running

```bash
cd ModernRepo/DB
docker compose up -d
```

#### Step 2: Configure Backend Environment Variables

The backend requires these environment variables to connect to PostgreSQL. You can set them:

**Option A: Using .env file in the backend directory**

Create or update `ModernRepo/Modern-Backend/.env`:

```bash
# Spring profile selection
SPRING_PROFILES_ACTIVE=prod

# PostgreSQL connection (individual variables)
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
POSTGRES_DB=myapp
POSTGRES_USER=appuser
POSTGRES_PASSWORD=dbuser123
```

**Option B: Using complete JDBC URL**

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/myapp
SPRING_DATASOURCE_USERNAME=appuser
SPRING_DATASOURCE_PASSWORD=dbuser123
```

**Option C: Export as environment variables**

```bash
export SPRING_PROFILES_ACTIVE=prod
export POSTGRES_HOST=localhost
export POSTGRES_PORT=5432
export POSTGRES_DB=myapp
export POSTGRES_USER=appuser
export POSTGRES_PASSWORD=dbuser123
```

#### Step 3: Start the Backend

```bash
cd ModernRepo/Modern-Backend
./start.sh
```

Or from the repository root:
```bash
cd ModernRepo
./start.sh
```

### Connection String Format

The backend constructs the JDBC URL using this pattern:

```
SPRING_DATASOURCE_URL=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}
```

With default values:
```
jdbc:postgresql://localhost:5432/myapp
```

The username and password are set via:
```
SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
```

These match exactly with the variables defined in `application-prod.properties`:
- `spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:myapp}}`
- `spring.datasource.username=${SPRING_DATASOURCE_USERNAME:${POSTGRES_USER:appuser}}`
- `spring.datasource.password=${SPRING_DATASOURCE_PASSWORD:${POSTGRES_PASSWORD:dbuser123}}`

## Database Profiles

### Development Profile (H2)

When running with `SPRING_PROFILES_ACTIVE=dev` (default), the backend uses an **in-memory H2 database** and does **NOT** connect to this PostgreSQL container.

- No external database needed
- Data is lost on application restart
- H2 console available at `/h2-console`

### Production Profile (PostgreSQL)

When running with `SPRING_PROFILES_ACTIVE=prod`, the backend connects to this PostgreSQL container:

- Persistent data storage
- Production-ready configuration
- Requires this database container to be running

## Manual Database Access

### Using psql Command Line

```bash
# Connect to the database
docker compose exec postgres psql -U appuser -d myapp

# Or from host (if psql is installed)
psql -h localhost -p 5432 -U appuser -d myapp
```

### Using Docker Exec

```bash
docker compose exec postgres bash
psql -U appuser -d myapp
```

## Healthcheck

The container includes a healthcheck that runs every 10 seconds:

```bash
pg_isready -U ${POSTGRES_USER} -d ${POSTGRES_DB}
```

Check health status:
```bash
docker compose ps
```

Healthy container will show `healthy` in the status column.

## Troubleshooting

### Container won't start

1. **Check if port 5432 is already in use:**
   ```bash
   lsof -i :5432
   # or
   netstat -an | grep 5432
   ```

2. **Change the port in `.env`:**
   ```bash
   POSTGRES_PORT=5433
   ```
   Then restart: `docker compose up -d`

### Backend can't connect

1. **Verify database is running:**
   ```bash
   docker compose ps
   ```

2. **Check database logs:**
   ```bash
   docker compose logs postgres
   ```

3. **Verify connection parameters match:**
   - Check `ModernRepo/DB/.env`
   - Check backend environment variables
   - Ensure `SPRING_PROFILES_ACTIVE=prod` is set

4. **Test connection manually:**
   ```bash
   psql -h localhost -p 5432 -U appuser -d myapp
   ```

### Data persistence issues

1. **Check volume exists:**
   ```bash
   docker volume ls | grep modernrepo-pgdata
   ```

2. **Inspect volume:**
   ```bash
   docker volume inspect modernrepo-pgdata
   ```

### Reset database

To completely reset the database and start fresh:

```bash
# Stop and remove container and volume
docker compose down -v

# Start fresh
docker compose up -d
```

## Network Configuration

### Docker Network

By default, the container uses Docker's default bridge network. The backend connects using `localhost:5432` when running on the host.

### Container-to-Container Communication

If running the backend in Docker as well, use the service name:

```bash
POSTGRES_HOST=postgres  # Use service name instead of localhost
```

## Backup and Restore

### Create a Backup

```bash
docker compose exec postgres pg_dump -U appuser myapp > backup.sql
```

### Restore from Backup

```bash
cat backup.sql | docker compose exec -T postgres psql -U appuser -d myapp
```

## Production Considerations

For production deployments:

1. **Change default password** in `.env`
2. **Use secrets management** instead of plain text passwords
3. **Configure regular backups**
4. **Monitor disk space** for the volume
5. **Set up replication** if high availability is needed
6. **Review security settings** and network exposure
7. **Consider using managed PostgreSQL** (AWS RDS, Google Cloud SQL, etc.)

## Additional Resources

- [PostgreSQL Official Documentation](https://www.postgresql.org/docs/)
- [Docker Compose Documentation](https://docs.docker.com/compose/)
- [Spring Boot PostgreSQL Guide](https://spring.io/guides/gs/accessing-data-jpa/)
