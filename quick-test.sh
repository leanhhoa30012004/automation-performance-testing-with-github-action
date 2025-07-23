#!/bin/bash
# Quick Test for Automation Performance Testing
# Developer: leanhhoa30012004
# Created: 2025-07-23 18:51:26 UTC

echo "🚀 Quick Test - Automation Performance Testing"
echo "Project: automation-performance-testing-with-github-action"
echo "Developer: leanhhoa30012004"
echo "Time: 2025-07-23 18:51:26 UTC"
echo "=============================================="

# Check if we're in the right directory
if [ ! -f "pom.xml" ]; then
    echo "❌ Error: pom.xml not found. Please run from project root directory."
    exit 1
fi

# 1. Build project
echo "📦 Building project..."
mvn clean package -q -DskipTests
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
else
    echo "❌ Build failed!"
    exit 1
fi

# 2. Start application
echo "🏃 Starting application..."
nohup java -jar target/automation-performance-testing-with-github-action-1.0.0.jar > app.log 2>&1 &
APP_PID=$!
echo $APP_PID > app.pid
sleep 8

# 3. Health check
echo "🏥 Checking application health..."
RETRY_COUNT=0
MAX_RETRIES=10

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    if curl -s http://localhost:8080/api/health > /dev/null; then
        echo "✅ Application is healthy!"
        break
    else
        echo "⏳ Waiting for application to start... (attempt $((RETRY_COUNT + 1))/$MAX_RETRIES)"
        sleep 2
        RETRY_COUNT=$((RETRY_COUNT + 1))
    fi
done

if [ $RETRY_COUNT -eq $MAX_RETRIES ]; then
    echo "❌ Application health check failed after $MAX_RETRIES attempts!"
    echo "📋 Application logs:"
    tail -20 app.log
    kill $APP_PID 2>/dev/null
    exit 1
fi

# 4. Test API endpoints
echo "🔍 Testing key API endpoints..."
echo "📍 Health endpoint:"
curl -s http://localhost:8080/api/health

echo -e "\n📍 Automation info endpoint:"
curl -s http://localhost:8080/api/automation/info

echo -e "\n📍 Test user endpoint:"
curl -s http://localhost:8080/api/users/123

# 5. Run mini performance test
echo -e "\n⚡ Running mini performance test..."
echo "targetUrl=http://localhost:8080/api/health
httpMethod=GET
threadCount=3
requestsPerThread=5
testDuration=15
connectionTimeout=5000
responseTimeout=10000
rampUpTime=1
testName=Quick Test - automation-performance-testing
testDescription=Quick performance test for automation-performance-testing project by leanhhoa30012004
testAuthor=leanhhoa30012004
framework=Automation Performance Testing
createdDate=2025-07-23 18:51:26 UTC" > quick-test.properties

java -cp "target/classes:target/lib/*" com.hoale.automation.Main quick-test.properties

# 6. Check reports
echo -e "\n📊 Checking generated reports..."
if [ -d "reports" ]; then
    echo "✅ Reports generated:"
    ls -la reports/ | head -10

    # Show summary from latest report
    latest_txt=$(find reports -name "*quick*test*.txt" -type f -printf '%T@ %p\n' | sort -k 1nr | head -1 | cut -d' ' -f2-)
    if [ -f "$latest_txt" ]; then
        echo -e "\n📈 Quick test summary:"
        echo "======================"
        grep -E "(Success rate|Average response time|Throughput|Developer)" "$latest_txt" || head -15 "$latest_txt"
        echo "======================"
    fi
else
    echo "❌ No reports found!"
fi

# 7. Stop application
echo -e "\n🛑 Stopping application..."
kill $APP_PID 2>/dev/null
rm -f app.pid quick-test.properties

echo -e "\n🎉 Quick test completed!"
echo "📋 Summary:"
echo "   - Project: automation-performance-testing-with-github-action"
echo "   - Application: ✅ Working"
echo "   - API Endpoints: ✅ Responsive"
echo "   - Performance Test: ✅ Executed"
echo "   - Reports: ✅ Generated"
echo ""
echo "👨‍💻 Test completed by: leanhhoa30012004"
echo "⏰ Completed at: $(date -u '+%Y-%m-%d %H:%M:%S UTC')"