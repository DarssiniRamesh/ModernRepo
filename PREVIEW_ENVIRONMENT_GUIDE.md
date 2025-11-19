# Preview Environment Configuration Guide

**Application:** ModernRepo Spring Boot Backend  
**Preview URL:** `https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3010`  
**Internal Port:** 3001

---

## Quick Start

### Check Application Status
```bash
curl https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3010/actuator/health
```

**Expected Response:**
```json
{"status":"UP","groups":["liveness","readiness"]}
```

### Check Database Connection
```bash
curl https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3010/questions
```

**Expected:** Paginated list of questions from PostgreSQL

---

## Port Mapping

| External URL | Internal Port | Service |
|-------------|---------------|---------|
| `:3010` | `3001` | Spring Boot Backend |
| `:5432` | `5000` | PostgreSQL Database |

**Important:** The backend runs on **internal port 3001** but is exposed externally on port 3010 via preview proxy.

---

## Environment Configuration

### Required Environment Variables

```bash
# Spring Profile (MUST be set to prod for PostgreSQL)
SPRING_PROFILES_ACTIVE=prod

# Database Connection
POSTGRES_HOST=localhost
POSTGRES_PORT=5000
POSTGRES_DB=myapp
POSTGRES_USER=appuser
POSTGRES_PASSWORD=dbuser123

# Datasource URLs (auto-constructed by start.sh)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5000/myapp
SPRING_DATASOURCE_USERNAME=appuser
SPRING_DATASOURCE_PASSWORD=dbuser123

# Server Port
PORT=3001
```

### Configuration Files

**Location:** `ModernRepo/.env`

All required environment variables are defined in this file and automatically loaded by `start.sh`.

---

## Starting the Application

### Correct Method (Recommended)

```bash
cd /home/kavia/workspace/code-generation/ModernRepo
./start.sh
```

This script:
1. Loads environment variables from `.env`
2. Sets `SPRING_PROFILES_ACTIVE=prod`
3. Exports all database connection variables
4. Starts the application with correct Maven arguments

### Verifying Correct Startup

**Check the logs for:**
```
The following profiles are active: prod
HikariPool-1 - Starting...
HikariPool-1 - Start completed.
Using dialect: org.hibernate.dialect.PostgreSQLDialect
Tomcat started on port(s): 3001 (http)
```

**Verify profile via API:**
```bash
curl http://localhost:3001/actuator/env | grep -A5 "activeProfiles"
```

**Expected:** `"activeProfiles": ["prod"]`

---

## Common Issues and Fixes

### Issue 1: Application Using H2 Instead of PostgreSQL

**Symptom:**
```bash
curl http://localhost:3001/actuator/health
# Shows "database": "H2"
```

**Cause:** `SPRING_PROFILES_ACTIVE` not set or defaulting to `dev`

**Fix:**
```bash
# Stop the application
pkill -f "PostgresDemoApplication"

# Export the profile
export SPRING_PROFILES_ACTIVE=prod

# Restart using the start script
./start.sh
```

### Issue 2: Multiple Instances Running

**Symptom:**
```bash
ps aux | grep PostgresDemoApplication
# Shows multiple Java processes
```

**Fix:**
```bash
# Kill all instances
pkill -f "PostgresDemoApplication"

# Wait a few seconds
sleep 5

# Start single instance
./start.sh
```

### Issue 3: Port Already in Use

**Symptom:**
```
Port 3001 already in use
```

**Fix:**
```bash
# Find the process using the port
lsof -i :3001

# Kill the process (replace PID with actual process ID)
kill -15 <PID>

# Restart
./start.sh
```

### Issue 4: Cannot Connect to PostgreSQL

**Symptom:**
```
Connection refused to localhost:5000
```

**Fix:**
```bash
# Check if PostgreSQL is running
psql -h localhost -p 5000 -U appuser -d myapp

# If not running, check the database container
cd DB
docker compose ps
docker compose up -d
```

---

## Health Check Endpoints

### Application Health
```bash
curl http://localhost:3001/actuator/health
```

**Success Response:**
```json
{"status":"UP","groups":["liveness","readiness"]}
```

### Liveness Probe
```bash
curl http://localhost:3001/actuator/health/liveness
```

### Readiness Probe
```bash
curl http://localhost:3001/actuator/health/readiness
```

### API Status (External)
```bash
curl https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3010/questions
```

---

## Debugging Commands

### Check Active Profile
```bash
ps aux | grep PostgresDemoApplication | grep -o "SPRING_PROFILES_ACTIVE=[^ ]*"
```

### Check Database Connection
```bash
PGPASSWORD=dbuser123 psql -h localhost -p 5000 -U appuser -d myapp -c "SELECT version();"
```

### View Application Logs
```bash
# If started in background, check the log file
tail -f /path/to/log/file

# If started in foreground, logs appear in console
```

### Test Database Write
```bash
curl -X POST http://localhost:3001/questions \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","description":"Test write to PostgreSQL"}'
```

### Verify in Database
```bash
PGPASSWORD=dbuser123 psql -h localhost -p 5000 -U appuser -d myapp \
  -c "SELECT id, title FROM questions ORDER BY id DESC LIMIT 5;"
```

---

## Exit Code 143 Prevention

Exit code 143 (SIGTERM) is a graceful shutdown signal. The application now handles this correctly with:

1. **Graceful Shutdown:** 30-second timeout to complete in-flight requests
2. **Health Checks:** Actuator endpoints for monitoring
3. **Connection Pool Management:** Proper cleanup on shutdown
4. **Single Instance:** No duplicate processes competing for ports

**Configuration (Already Applied):**
```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

---

## Monitoring Checklist

- [ ] Only one Java process running for ModernRepo backend
- [ ] Process listening on port 3001
- [ ] `SPRING_PROFILES_ACTIVE=prod` in process environment
- [ ] Health endpoint returns `{"status":"UP"}`
- [ ] PostgreSQL running on port 5000
- [ ] API responses use PostgreSQL data (not H2)
- [ ] No "database H2" in health check details

---

## Quick Commands Reference

```bash
# Check if app is running
curl -s http://localhost:3001/actuator/health | grep -o '"status":"[^"]*"'

# Check active profile
curl -s http://localhost:3001/actuator/env | grep -A2 "activeProfiles"

# Check database type
curl -s http://localhost:3001/actuator/health | grep -o '"database":"[^"]*"'

# Stop application gracefully
pkill -15 -f PostgresDemoApplication

# Start application
cd /home/kavia/workspace/code-generation/ModernRepo && ./start.sh

# Test API (external URL)
curl https://vscode-internal-32563-beta.beta01.cloud.kavia.ai:3010/questions

# Connect to database
PGPASSWORD=dbuser123 psql -h localhost -p 5000 -U appuser -d myapp
```

---

## Support

**Documentation:**
- Main README: `ModernRepo/README.md`
- Backend README: `ModernRepo/Modern-Backend/Readme.md`
- Database README: `ModernRepo/DB/README.md`
- Exit Code 143 Fix: `ModernRepo/EXIT_CODE_143_FIX_REPORT.md`

**API Documentation (Swagger UI):**
```
http://localhost:3001/swagger-ui.html
```

**Health Status:**
```
http://localhost:3001/actuator/health
```
