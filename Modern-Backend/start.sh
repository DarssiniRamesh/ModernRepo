#!/bin/sh
# Start ModernRepo Spring Boot backend in dev mode by default
./mvnw -q -DskipTests spring-boot:run -Dspring-boot.run.profiles=dev
