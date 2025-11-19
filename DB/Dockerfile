# Dockerfile for running PostgreSQL in the DB container

FROM postgres:13-alpine

# Set environment variables for default DB setup
ENV POSTGRES_DB=postgres_demo
ENV POSTGRES_USER=postgres
ENV POSTGRES_PASSWORD=postgres

EXPOSE 3002

# Change default Postgres port from 5432 to 3002 for the container
# Use envsubst to replace the port in postgresql.conf after container starts
CMD ["postgres", "-p", "3002"]
