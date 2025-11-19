#!/bin/sh
# Entrypoint script for ModernRepo repo root.
# Ensures launch from Modern-Backend, prefers dev profile, and
# selects between ./mvnw, mvn, or built jar, in that order, with proper fallback.
# Sets server.port=3001 for preview environments.

set -euo pipefail

# Ensure script is executable on systems that lose chmod after git checkout.
SCRIPT="$(realpath "$0" 2>/dev/null || readlink -f "$0" 2>/dev/null || echo "$0")"
if [ ! -x "$SCRIPT" ]; then
  chmod +x "$SCRIPT" 2>/dev/null || true
fi

# Ensure Modern-Backend/start.sh gets executable as well, if present.
if [ -f "$(dirname "$0")/Modern-Backend/start.sh" ]; then
  chmod +x "$(dirname "$0")/Modern-Backend/start.sh" 2>/dev/null || true
fi

# Set Spring profile to dev unless already set.
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

# Enter Modern-Backend.
cd "$(dirname "$0")/Modern-Backend"

# Main entrypoint logic.
if [ -x ./mvnw ]; then
  exec ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" -Dspring-boot.run.arguments="--server.port=3001"
elif command -v mvn >/dev/null 2>&1; then
  exec mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" -Dspring-boot.run.arguments="--server.port=3001"
elif ls target/*.jar >/dev/null 2>&1; then
  # Pick first (should only be one)
  JAR=$(ls target/*.jar | head -n1)
  exec java -jar "$JAR" --spring.profiles.active="$SPRING_PROFILES_ACTIVE" --server.port=3001
else
  echo "No mvnw, mvn, or jar found, cannot start the Spring Boot application" >&2
  exit 127
fi
