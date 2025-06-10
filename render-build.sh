#!/bin/bash
# Render deployment script with robust Java environment setup
set -e

echo "🚀 Starting BlueChat build process..."
echo "========================================"

# Detect and setup Java environment
echo "🔍 Detecting Java environment..."

# Try multiple ways to find Java
if command -v java &> /dev/null; then
    JAVA_PATH=$(which java)
    echo "✅ Java found at: $JAVA_PATH"
    java -version
else
    echo "❌ Java not found in PATH"
    exit 1
fi

# Set JAVA_HOME using multiple detection methods
if [ -z "$JAVA_HOME" ]; then
    echo "🔧 JAVA_HOME not set, attempting to detect..."
    
    # Method 1: Use readlink if available
    if command -v readlink &> /dev/null; then
        JAVA_HOME="$(dirname $(dirname $(readlink -f $(which java))))"
        echo "📍 Method 1 - JAVA_HOME detected: $JAVA_HOME"
    # Method 2: Use Java system property
    elif command -v java &> /dev/null; then
        JAVA_HOME=$(java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $3}' | head -1)
        echo "📍 Method 2 - JAVA_HOME detected: $JAVA_HOME"
    # Method 3: Common Java installation paths
    else
        for java_dir in /usr/lib/jvm/java-* /usr/lib/jvm/default-java /opt/java/* /usr/local/openjdk-*; do
            if [ -d "$java_dir" ] && [ -x "$java_dir/bin/java" ]; then
                JAVA_HOME="$java_dir"
                echo "📍 Method 3 - JAVA_HOME detected: $JAVA_HOME"
                break
            fi
        done
    fi
    
    if [ -n "$JAVA_HOME" ]; then
        export JAVA_HOME
        export PATH="$JAVA_HOME/bin:$PATH"
        echo "✅ JAVA_HOME set to: $JAVA_HOME"
    else
        echo "❌ Could not detect JAVA_HOME automatically"
        echo "📋 Available Java installations:"
        find /usr -name java -type f 2>/dev/null | head -5
        exit 1
    fi
else
    echo "✅ JAVA_HOME already set: $JAVA_HOME"
fi

# Verify Java setup
echo "\n🧪 Verifying Java setup..."
echo "JAVA_HOME: $JAVA_HOME"
echo "Java path: $(which java)"
echo "Java version:"
java -version

# Make Maven wrapper executable
echo "\n🔧 Setting up Maven wrapper..."
chmod +x ./mvnw

# Verify Maven wrapper
echo "📋 Maven wrapper info:"
./mvnw --version

# Build the application
echo "\n🏗️ Building BlueChat application..."
echo "========================================"
./mvnw clean package -DskipTests -B

# Verify build success
echo "\n🔍 Verifying build results..."
if [ -f "target/bluechat-v2-0.0.1-SNAPSHOT.jar" ]; then
    JAR_SIZE=$(du -h target/bluechat-v2-0.0.1-SNAPSHOT.jar | cut -f1)
    echo "✅ Build completed successfully!"
    echo "📦 JAR file created: target/bluechat-v2-0.0.1-SNAPSHOT.jar"
    echo "📏 JAR file size: $JAR_SIZE"
    echo "🎉 BlueChat is ready for deployment!"
else
    echo "❌ Build failed - JAR file not found"
    echo "📂 Contents of target directory:"
    ls -la target/ || echo "Target directory not found"
    exit 1
fi

