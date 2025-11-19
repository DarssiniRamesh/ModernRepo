#!/bin/sh
# Start ModernRepo Spring Boot backend in dev mode by default

# Ensure mvnw has executable permission (required for preview/CI environments)
chmod +x ./mvnw

./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
