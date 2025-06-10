@echo off
echo Testing BlueChat locally...

REM Build the application
echo Building application...
mvn clean package -DskipTests

REM Check if build was successful
if not exist "target\bluechat-v2-0.0.1-SNAPSHOT.jar" (
    echo Build failed - JAR not found
    pause
    exit /b 1
)

echo Build successful! Starting application...
echo.
echo Application will be available at: http://localhost:8080
echo Press Ctrl+C to stop the application
echo.

REM Start the application
java -jar target\bluechat-v2-0.0.1-SNAPSHOT.jar --server.port=8080

pause

