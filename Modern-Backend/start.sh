#!/usr/bin/env sh
# Start ModernRepo Spring Boot backend in dev mode by default (POSIX sh compliant).

# Attempt to ensure script is executable (non-fatal if this fails).
if [ ! -x "$0" ]; then
  chmod +x "$0" 2>/dev/null || true
fi

if [ -z "$SPRING_PROFILES_ACTIVE" ]; then
  SPRING_PROFILES_ACTIVE=dev
  export SPRING_PROFILES_ACTIVE
fi

# Try Maven Wrapper, then mvn, then jar.
if [ -x "./mvnw" ] && [ -f ".mvn/wrapper/maven-wrapper.jar" ]; then
  exec ./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE"
elif command -v mvn >/dev/null 2>&1; then
  exec mvn -q -DskipTests spring-boot:run -Dspring-boot.run.profiles="$SPRING_PROFILES_ACTIVE"
elif ls target/*.jar >/dev/null 2>&1; then
  JAR=$(ls target/*.jar | head -n 1)
  exec java -jar "$JAR" --spring.profiles.active="$SPRING_PROFILES_ACTIVE"
else
  echo "No mvnw, mvn, or jar found to start Spring Boot" >&2
  exit 127
fi
