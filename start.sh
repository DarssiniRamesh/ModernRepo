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

# Set Spring profile to dev unless already set.
if [ -z "$SPRING_PROFILES_ACTIVE" ]; then
  SPRING_PROFILES_ACTIVE=dev
  export SPRING_PROFILES_ACTIVE
fi

# Enter Modern-Backend.
cd "$(dirname "$0")/Modern-Backend" || exit 1

# Try Maven Wrapper, then mvn, then jar.
if [ -x ./mvnw ]; then
  exec ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" -Dspring-boot.run.arguments="--server.port=3001"
elif command -v mvn >/dev/null 2>&1; then
  exec mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" -Dspring-boot.run.arguments="--server.port=3001"
elif ls target/*.jar >/dev/null 2>&1; then
  JAR=$(ls target/*.jar | head -n 1)
  exec java -jar "$JAR" --spring.profiles.active="$SPRING_PROFILES_ACTIVE" --server.port=3001
else
  echo "No mvnw, mvn, or jar found, cannot start the Spring Boot application" >&2
  exit 127
fi
