#!/usr/bin/env sh
# Starts the Spring Boot app on port 3001 using the Maven Wrapper.
# Requires Java; does not require a global Maven installation.
# Usage: ./start-local-3001.sh

DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$DIR"

# Run the application on port 3001
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=3001"
