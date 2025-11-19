#!/bin/bash

# Build script for Java 22 Migration Backend
# This script compiles the Java 22 migration backend

set -e

echo "======================================"
echo "Building Java 22 Migration Backend"
echo "======================================"

cd "$(dirname "$0")/.."

echo "Cleaning previous builds..."
./mvnw clean

echo "Compiling and packaging application..."
./mvnw package -DskipTests

echo "======================================"
echo "Build completed successfully!"
echo "JAR file location: target/postgres-demo-java22-0.0.1-SNAPSHOT.jar"
echo "======================================"
