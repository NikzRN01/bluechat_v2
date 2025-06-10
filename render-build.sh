#!/bin/bash
# Render deployment script
set -e

echo "Starting build process..."

# Check Java version
echo "Java version:"
java -version

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME="$(dirname $(dirname $(readlink -f $(which java))))"
    echo "Set JAVA_HOME to: $JAVA_HOME"
fi

# Make mvnw executable
chmod +x ./mvnw

# Show Maven version
echo "Maven wrapper version:"
./mvnw --version

# Clean and build the application
echo "Building application..."
./mvnw clean package -DskipTests -q

# Verify the JAR was created
if [ -f "target/bluechat-v2-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ Build completed successfully!"
    echo "JAR file size: $(du -h target/bluechat-v2-0.0.1-SNAPSHOT.jar | cut -f1)"
else
    echo "❌ Build failed - JAR file not found"
    exit 1
fi

