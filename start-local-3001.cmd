@echo off
REM Starts the Spring Boot app on port 3001 using the Maven Wrapper.
REM Requires Java; does not require a global Maven installation.
REM Usage: start-local-3001.cmd

setlocal
cd /d %~dp0
call .\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=3001"
endlocal
