#!/bin/sh
# Start ModernRepo Spring Boot backend in dev mode by default

set -euo pipefail

SCRIPT="$(realpath "$0" 2>/dev/null || readlink -f "$0" 2>/dev/null || echo "$0")"
if [ ! -x "$SCRIPT" ]; then
  chmod +x "$SCRIPT" 2>/dev/null || true
fi

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

if [ -x "./mvnw" ]; then
  exec ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE"
elif command -v mvn >/dev/null 2>&1; then
  exec mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE"
elif ls target/*.jar >/dev/null 2>&1; then
  JAR=$(ls target/*.jar | head -n1)
  exec java -jar "$JAR" --spring.profiles.active="$SPRING_PROFILES_ACTIVE"
else
  echo "No mvnw, mvn, or jar found to start Spring Boot" >&2
  exit 127
fi
