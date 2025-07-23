#!/bin/bash

# Automation Performance Testing with GitHub Action - Test Runner
# Developer: leanhhoa30012004
# Created: 2025-07-23 18:51:26 UTC

echo "==========================================="
echo "Automation Performance Testing Test Runner"
echo "Project: automation-performance-testing-with-github-action"
echo "Developer: leanhhoa30012004"
echo "Created: 2025-07-23 18:51:26 UTC"
echo "==========================================="

# Build the project
echo "🔨 Building project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Build failed!"
    exit 1
fi

echo "✅ Build completed successfully!"

# Start the application
echo "🚀 Starting Automation Performance Testing application..."
nohup java -jar target/automation-performance-testing-with-github-action-1.0.0.jar > app.log 2>&1 &
echo $! > app.pid

# Wait for application to start
echo "⏳ Waiting for application to start..."
sleep 10

# Check if application is running
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Application is running!"
else
    echo "❌ Application failed to start!"
    echo "📋 Checking logs..."
    tail -20 app.log
    exit 1
fi

# Test API endpoints
echo "🔍 Testing API endpoints..."
echo "📍 Health Check:"
curl -s http://localhost:8080/api/health | jq . 2>/dev/null || curl -s http://localhost:8080/api/health

echo -e "\n📍 Automation Info:"
curl -s http://localhost:8080/api/automation/info | jq . 2>/dev/null || curl -s http://localhost:8080/api/automation/info

# Run performance tests
echo -e "\n⚡ Running API Health Check Performance Test..."
java -cp "target/classes:target/lib/*" com.hoale.automation.Main performance-config/api-performance.properties

echo -e "\n🚀 Running Load Test..."
java -cp "target/classes:target/lib/*" com.hoale.automation.Main performance-config/load-test.properties

echo -e "\n💪 Running Stress Test..."
java -cp "target/classes:target/lib/*" com.hoale.automation.Main performance-config/stress-test.properties

# Check reports
echo -e "\n📊 Checking generated reports..."
if [ -d "reports" ]; then
    echo "✅ Reports generated:"
    ls -la reports/
    echo -e "\n📈 Latest report summary:"
    latest_txt=$(find reports -name "*.txt" -type f -printf '%T@ %p\n' | sort -k 1nr | head -1 | cut -d' ' -f2-)
    if [ -f "$latest_txt" ]; then
        echo "===================="
        head -20 "$latest_txt"
        echo "===================="
    fi
else
    echo "❌ No reports found!"
fi

# Stop the application
echo -e "\n🛑 Stopping application..."
if [ -f app.pid ]; then
    kill $(cat app.pid)
    rm app.pid
fi

echo -e "\n🎉 All tests completed!"
echo "📊 Check the reports/ directory for detailed results"
echo "📝 Application logs are in app.log"
echo ""
echo "Automation Performance Testing with GitHub Action"
echo "Developed by: leanhhoa30012004"
echo "Completed on: $(date -u '+%Y-%m-%d %H:%M:%S UTC')"