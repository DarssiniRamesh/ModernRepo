#!/usr/bin/env sh
# Entrypoint script for ModernRepo repo root (POSIX sh compliant).

# Ensure this script is executable in case permissions were lost (e.g., after a git checkout)
if [ ! -x "$0" ]; then
  chmod +x "$0" 2>/dev/null || true
fi

# Ensure Modern-Backend/start.sh gets executable as well, if present.
if [ -f "$(dirname "$0")/Modern-Backend/start.sh" ]; then
  if [ ! -x "$(dirname "$0")/Modern-Backend/start.sh" ]; then
    chmod +x "$(dirname "$0")/Modern-Backend/start.sh" 2>/dev/null || true
  fi
fi

# Load environment variables from .env file if it exists
ENV_FILE="$(dirname "$0")/.env"
if [ -f "$ENV_FILE" ]; then
  # Source the .env file to load variables into current shell
  set -a
  . "$ENV_FILE"
  set +a
fi

# Ensure Spring profile is set to prod for deployment
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-prod}"

# Export database connection variables explicitly
export POSTGRES_HOST="${POSTGRES_HOST:-localhost}"
export POSTGRES_PORT="${POSTGRES_PORT:-5000}"
export POSTGRES_DB="${POSTGRES_DB:-myapp}"
export POSTGRES_USER="${POSTGRES_USER:-appuser}"
export POSTGRES_PASSWORD="${POSTGRES_PASSWORD:-dbuser123}"
export SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}}"
export SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-${POSTGRES_USER}}"
export SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-${POSTGRES_PASSWORD}}"

# Enter Modern-Backend.
cd "$(dirname "$0")/Modern-Backend" || exit 1

# Try Maven Wrapper, then mvn, then jar.
if [ -x ./mvnw ]; then
  exec ./mvnw -q -DskipTests spring-boot:run \
    -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" \
    -Dspring-boot.run.jvmArguments="-DSPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" \
    -Dspring-boot.run.arguments="--server.port=${PORT:-3001}"
elif command -v mvn >/dev/null 2>&1; then
  exec mvn -q -DskipTests spring-boot:run \
    -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" \
    -Dspring-boot.run.jvmArguments="-DSPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE" \
    -Dspring-boot.run.arguments="--server.port=${PORT:-3001}"
elif ls target/*.jar >/dev/null 2>&1; then
  JAR=$(ls target/*.jar | head -n 1)
  exec java -jar "$JAR" \
    --spring.profiles.active="$SPRING_PROFILES_ACTIVE" \
    --server.port="${PORT:-3001}"
else
  echo "No mvnw, mvn, or jar found, cannot start the Spring Boot application" >&2
  exit 127
fi
