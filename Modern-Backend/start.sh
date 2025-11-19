#!/usr/bin/env sh
# Start ModernRepo Spring Boot backend in prod mode by default (POSIX sh compliant).

# Attempt to ensure script is executable (non-fatal if this fails).
if [ ! -x "$0" ]; then
  chmod +x "$0" 2>/dev/null || true
fi

# Load environment variables from parent .env file if it exists
PARENT_ENV_FILE="$(dirname "$0")/../.env"
if [ -f "$PARENT_ENV_FILE" ]; then
  # Source the .env file to load variables into current shell
  set -a
  . "$PARENT_ENV_FILE"
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

# Try Maven Wrapper, then mvn, then jar.
if [ -x "./mvnw" ]; then
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
  JAR=$(ls target/*.jar | head -1)
  exec java -jar "$JAR" \
    --spring.profiles.active="$SPRING_PROFILES_ACTIVE" \
    --server.port="${PORT:-3001}"
else
  echo "No mvnw, mvn, or jar found to start Spring Boot" >&2
  exit 127
fi
