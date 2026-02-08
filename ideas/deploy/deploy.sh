#!/bin/bash

# Stop on error
set -e

echo "Starting Deployment..."

# 1. Start Services
echo "Bringing up Docker services..."
docker compose up -d

# 2. Wait for Kong
echo "Waiting for Kong Admin API (localhost:8001)..."
until curl -s http://localhost:8001 > /dev/null; do
    echo "  Kong not ready yet..."
    sleep 5
done
echo "Kong is UP!"

# 3. Configure Kong
echo "Configuring Kong..."
node scripts/config-kong.js

# 4. Restart Data Populator
# The populator might have failed or needs restart to see the routes
echo "Restarting Data Generator..."
docker compose restart Generate-APIs-Data

echo "Deployment Finished!"
echo "Check: http://localhost:4200"
