#!/bin/sh
# Entrypoint script for ModernRepo container root.
# Delegates to Modern-Backend/start.sh if present.
# Falls back to running with mvnw or mvn from Modern-Backend as appropriate.
# Defaults to Spring Boot dev profile.

set -e

cd "$(dirname "$0")/Modern-Backend"

if [ -f "./start.sh" ]; then
  chmod +x ./start.sh
  exec ./start.sh
elif [ -f "./mvnw" ]; then
  chmod +x ./mvnw
  exec ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="${SPRING_PROFILES_ACTIVE:-dev}"
else
  exec mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="${SPRING_PROFILES_ACTIVE:-dev}"
fi
