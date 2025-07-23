#!/bin/bash
# Simple Test Runner
# Developer: leanhhoa30012004
# Created: 2025-07-23 19:24:30 UTC

echo "🚀 Simple Automation Performance Testing"
echo "Developer: leanhhoa30012004"
echo "Created: 2025-07-23 19:24:30 UTC"
echo "Repository: automation-performance-testing-with-github-action"
echo "======================================"

# Build
echo "📦 Building..."
mvn clean package -DskipTests

# Check JAR
JAR_FILE="target/automation-performance-testing-with-github-action-1.0.0-jar-with-dependencies.jar"
if [ ! -f "$JAR_FILE" ]; then
    echo "❌ JAR file not found!"
    exit 1
fi

echo "✅ JAR file created: $(ls -lh $JAR_FILE | awk '{print $5}')"

# Start app
echo "🚀 Starting application..."
nohup java -jar "$JAR_FILE" > app.log 2>&1 &
APP_PID=$!
echo $APP_PID > app.pid

# Wait
sleep 8

# Test
if curl -s http://localhost:8080/api/health > /dev/null; then
    echo "✅ Application is working!"

    echo "🔍 Testing endpoints:"
    curl -s http://localhost:8080/api/health | head -2
    echo ""
    curl -s http://localhost:8080/api/info | head -2

    echo -e "\n⚡ Running performance test..."
    java -cp "target/classes:target/lib/*" com.hoale.automation.performance.Main performance-config/api-performance.properties

else
    echo "❌ Application failed to start"
    cat app.log
fi

# Cleanup
kill $APP_PID 2>/dev/null
rm -f app.pid

echo "🎉 Test completed by leanhhoa30012004!"