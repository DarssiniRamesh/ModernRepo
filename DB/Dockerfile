# Dockerfile for running PostgreSQL in the DB container

FROM postgres:13-alpine

# PUBLIC_INTERFACE
# Set environment variables for configuring the PostgreSQL database,
# taken from docker-compose.yml or overridden at runtime if needed.
ENV POSTGRES_DB=postgres_demo
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

# Expose the PostgreSQL service on port 3002 for container linkage
EXPOSE 3002

# No references to db_visualizer directory exist. Only start PostgreSQL on port 3002.
CMD ["postgres", "-p", "3002"]
