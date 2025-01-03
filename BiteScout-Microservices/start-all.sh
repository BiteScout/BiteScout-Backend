#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m'

echo -e "${BLUE}Starting databases and infrastructure...${NC}"
docker-compose up -d

# Wait for databases to be ready
echo -e "${BLUE}Waiting for databases to be healthy...${NC}"

# Wait for PostgreSQL
echo "Waiting for PostgreSQL..."
until docker exec ms_pg_sql pg_isready -U user -d postgres; do
    echo "PostgreSQL is unavailable - sleeping"
    sleep 2
done

# Check if databases were created
echo "Checking databases..."
for db in users authentication rankings reservations notifications; do
    until docker exec ms_pg_sql psql -U user -d "$db" -c '\q' 2>/dev/null; do
        echo "Waiting for database $db to be created..."
        sleep 2
    done
    echo "Database $db is ready"
done
# Wait for PostGIS
echo "Waiting for PostGIS..."
until docker exec postgis-container pg_isready -U user -d postgres; do
    echo "Waiting for PostGIS to be ready..."
    sleep 2
done

echo "PostGIS is up, waiting for restaurants database..."
until docker exec postgis-container psql -U user -d postgres -c '\l' | grep -q restaurants; do
    echo "Waiting for restaurants database to be created..."
    sleep 2
done

echo "PostGIS is ready!"

echo -e "${GREEN}All databases are healthy!${NC}"

# Start services in order
services=(
    "Config-Service"
    "Discovery-Service"
    "Gateway-Service"
    "Authentication-Service"
    "User-Service"
    "Restaurant-Service"
    "Notification-Service"
    "Reservation-Service"
    "Review-Service"
    "Ranking-Service"

)

# Create logs directory
mkdir -p logs

# Build and start each service
for service in "${services[@]}"; do
    echo -e "${BLUE}Starting $service...${NC}"
    cd "$service"
    chmod +x mvnw
    ./mvnw clean package -DskipTests

    # Start the service and capture its PID
    nohup java -jar target/*.jar > "../logs/$service.log" 2>&1 &
    PID=$!

    # Wait for service to start (check log for "Started" message)
    while ! grep -q "Started.*Application" "../logs/$service.log" 2>/dev/null; do
        if ! ps -p $PID > /dev/null; then
            echo -e "${RED}$service failed to start. Check logs for details.${NC}"
            exit 1
        fi
        echo "Waiting for $service to start..."
        sleep 2
    done

    echo -e "${GREEN}$service started successfully!${NC}"
    cd ..
    sleep 5  # Reduced sleep time since we're actively checking
done

echo -e "${GREEN}All services started! Check logs directory for service logs${NC}"