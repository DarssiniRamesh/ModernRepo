#!/bin/bash

# Run script for Java 22 Migration Backend
# This script starts the Java 22 migration backend on port 3002

set -e

echo "======================================"
echo "Starting Java 22 Migration Backend"
echo "======================================"

cd "$(dirname "$0")/.."

# Check if JAR exists
if [ ! -f "target/postgres-demo-java22-0.0.1-SNAPSHOT.jar" ]; then
    echo "JAR file not found. Building application..."
    ./scripts/build.sh
fi

echo "Starting application on port 3002..."
echo "Swagger UI: http://localhost:3002/swagger-ui.html"
echo "API Docs: http://localhost:3002/v3/api-docs"
echo "Health Check: http://localhost:3002/actuator/health"
echo "======================================"

java -jar target/postgres-demo-java22-0.0.1-SNAPSHOT.jar
