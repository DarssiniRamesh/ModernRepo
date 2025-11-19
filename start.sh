#!/bin/sh
# Entrypoint script for ModernRepo repo root.
# Ensures launch from Modern-Backend, prefers dev profile, and
# selects between ./mvnw and mvn. 
# Sets server.port=3001 for preview environments.

set -e

cd "$(dirname "$0")/Modern-Backend"
export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

if [ -x "./mvnw" ]; then
  exec ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" -Dspring-boot.run.arguments="--server.port=3001"
else
  exec mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE" -Dspring-boot.run.arguments="--server.port=3001"
fi
