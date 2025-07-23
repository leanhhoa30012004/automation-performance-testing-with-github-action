#!/bin/bash
# Enhanced Test Script with Report Generation
# Developer: leanhhoa30012004
# Created: 2025-07-23 19:52:31 UTC

echo "🚀 Enhanced Automation Performance Testing with Reports"
echo "Developer: leanhhoa30012004"
echo "Created: 2025-07-23 19:52:31 UTC"
echo "Repository: automation-performance-testing-with-github-action"
echo "Framework: Automation Performance Testing v1.0"
echo "============================================================"

# Build
echo "📦 Building application..."
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
sleep 10

# Test application
if curl -s http://localhost:8080/api/health > /dev/null; then
    echo "✅ Application is working!"

    echo "🔍 Testing endpoints:"
    curl -s http://localhost:8080/api/health | head -2
    echo ""
    curl -s http://localhost:8080/api/info | head -2

    echo -e "\n⚡ Running performance tests with report generation..."
    echo "=================================================="

    # Run all performance tests
    echo "🎯 API Health Check Performance Test:"
    java -cp "target/classes:target/lib/*" com.hoale.automation.performance.Main performance-config/api-performance.properties

    echo -e "\n🎯 Application Info Load Test:"
    java -cp "target/classes:target/lib/*" com.hoale.automation.performance.Main performance-config/load-test.properties

    echo -e "\n🎯 User API Stress Test:"
    java -cp "target/classes:target/lib/*" com.hoale.automation.performance.Main performance-config/stress-test.properties

    # Show generated reports
    echo -e "\n📊 Generated Reports:"
    echo "===================="
    if [ -d "reports" ]; then
        ls -la reports/
        echo ""
        echo "📋 Report Summary:"
        find reports -name "*.txt" -exec echo "📝 {}" \; -exec head -20 {} \; -exec echo "---" \;
    else
        echo "No reports found"
    fi

else
    echo "❌ Application failed to start"
    cat app.log
fi

# Cleanup
kill $APP_PID 2>/dev/null
rm -f app.pid

echo -e "\n🎉 Enhanced testing completed!"
echo "👨‍💻 Developer: leanhhoa30012004"
echo "📊 Framework: Automation Performance Testing v1.0"
echo "📝 Reports available in ./reports/ directory"
echo "⏰ Completed at: $(date)"