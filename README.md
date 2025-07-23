# Automation Performance Testing with GitHub Action

Simple Java automation application with integrated performance testing.

**Developer:** leanhhoa30012004  
**Created:** 2025-07-23 19:24:30 UTC  
**Repository:** automation-performance-testing-with-github-action

## Features

- 🚀 Simple Java HTTP Server (no external dependencies conflicts)
- 📊 Performance testing framework
- 🔄 GitHub Actions CI/CD integration
- 📈 Load testing capabilities

## Quick Start

```bash
# Build
mvn clean package

# Run application
java -jar target/automation-performance-testing-with-github-action-1.0.0-jar-with-dependencies.jar

# Test (in another terminal)
curl http://localhost:8080/api/health

# Run performance test
java -cp "target/classes:target/lib/*" com.hoale.automation.performance.Main performance-config/api-performance.properties
```

## Endpoints

- `GET /api/health` - Health check
- `GET /api/info` - Application information
- `GET /api/users/{id}` - Get user by ID

## Performance Testing

```bash
# Quick test
./run-tests.sh

# Custom test
java -cp "target/classes:target/lib/*" com.hoale.automation.performance.Main your-config.properties
```

## Configuration

Edit files in `performance-config/` directory:
- `api-performance.properties` - Basic API test
- `load-test.properties` - Load testing

## CI/CD

The project includes GitHub Actions workflow that:
1. Builds the application
2. Starts the server
3. Runs performance tests
4. Reports results

## Developer

**leanhhoa30012004**  
Created: 2025-07-23 19:24:30 UTC

---

*Simple, reliable, no dependencies conflicts.*