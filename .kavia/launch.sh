#!/bin/sh
# Defensive launch script for ModernRepo: always run Modern-Backend with correct dev profile, mimics start.sh logic.

set -euo pipefail

cd "$(dirname "$0")/../Modern-Backend"

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-dev}"

if [ -x ./mvnw ]; then
  exec ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE"
elif command -v mvn >/dev/null 2>&1; then
  exec mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE"
else
  echo "Neither mvnw nor mvn available in Modern-Backend!" >&2
  exit 127
fi
