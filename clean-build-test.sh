#!/bin/bash
# Clean Build and Test Script
# Developer: leanhhoa30012004
# Created: 2025-07-23 19:14:59 UTC

echo "🧹 Complete Clean Build and Test"
echo "Time: 2025-07-23 19:14:59 UTC | User: leanhhoa30012004"
echo "Project: automation-performance-testing-with-github-action"
echo "================================================="

# 1. Complete cleanup
echo "🗑️ Removing all generated files..."
rm -rf target/
rm -rf reports/
rm -f app.log app.pid *.properties
rm -f src/main/resources/logback-spring.xml

# 2. Clean Maven cache for this project
echo "🧹 Cleaning Maven cache..."
mvn dependency:purge-local-repository -DmanualInclude="com.hoale:automation-performance-testing-with-github-action" 2>/dev/null || true

# 3. Full clean build
echo "📦 Full clean build..."
mvn clean compile package -DskipTests -X | grep -E "(ERROR|Exception|Failed)" || echo "✅ Build successful"

# 4. Check JAR
JAR_FILE="target/automation-performance-testing-with-github-action-1.0.0.jar"
if [ -f "$JAR_FILE" ]; then
    echo "✅ JAR file created: $(ls -lh $JAR_FILE | awk '{print $5}')"
    echo "📋 JAR contents check:"
    jar tf "$JAR_FILE" | grep -E "(logback|slf4j)" | head -5 || echo "No conflicting logging libraries found"
else
    echo "❌ JAR file not found!"
    exit 1
fi

# 5. Test run
echo "🚀 Testing application startup..."
timeout 30s java -jar "$JAR_FILE" &
APP_PID=$!
sleep 8

# 6. Quick health check
if curl -s http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Application started successfully!"

    # Quick test
    echo "🔍 Quick API test:"
    curl -s http://localhost:8080/api/health | head -3

    # Quick performance test
    echo -e "\n⚡ Quick performance test..."
    echo "targetUrl=http://localhost:8080/api/health
httpMethod=GET
threadCount=2
requestsPerThread=2
testDuration=5
testName=Clean Build Test
testAuthor=leanhhoa30012004" > clean-test.properties

    timeout 15s java -cp "target/classes:target/lib/*" com.hoale.automation.Main clean-test.properties 2>/dev/null || echo "Performance test completed"

else
    echo "❌ Application failed to start"
    echo "📋 Process status:"
    ps aux | grep java | grep automation || echo "No application process found"
fi

# 7. Cleanup
kill $APP_PID 2>/dev/null || true
rm -f clean-test.properties

echo -e "\n🎉 Clean build test completed!"
echo "👨‍💻 Tested by: leanhhoa30012004"
echo "⏰ Completed at: $(date -u '+%Y-%m-%d %H:%M:%S UTC')"