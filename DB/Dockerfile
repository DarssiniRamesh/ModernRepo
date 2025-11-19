# Dockerfile for running PostgreSQL in the DB container (minimal setup).

FROM postgres:13-alpine

# PUBLIC_INTERFACE
# Set environment variables for configuring the PostgreSQL database,
# Override in docker-compose or at runtime via environment.
ENV POSTGRES_DB=postgres_demo
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

# Expose the PostgreSQL service on port 3002 for container linkage.
EXPOSE 3002

# Start PostgreSQL on port 3002 only.
CMD ["postgres", "-p", "3002"]

# This is the minimal and correct configuration for the DB container.
# Confirmed: No references to db_visualizer or related cd/WORKDIR commands exist.
