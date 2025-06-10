#!/bin/bash
# Render deployment script
set -e

# Make mvnw executable
chmod +x ./mvnw

# Build the application
./mvnw clean package -DskipTests

echo "Build completed successfully!"

