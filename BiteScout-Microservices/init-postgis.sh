#!/bin/bash
set -e

echo "Creating restaurants database and PostGIS extensions"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE restaurants;
EOSQL

echo "Connecting to restaurants database and setting up extensions"
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "restaurants" <<-EOSQL
    CREATE EXTENSION IF NOT EXISTS postgis;
    CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
    GRANT ALL PRIVILEGES ON DATABASE restaurants TO "$POSTGRES_USER";
EOSQL

echo "PostGIS database setup completed successfully"