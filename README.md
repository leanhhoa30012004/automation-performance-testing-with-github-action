# Hoale Automation Performance Testing Application

Java automation application with integrated performance testing framework.

**Developer:** leanhhoa30012004  
**Created:** 2025-07-23 18:43:23 UTC  
**Version:** 1.0.0

## Features

- 🚀 Spring Boot REST API application
- 📊 Integrated performance testing framework
- 🔄 CI/CD integration with GitHub Actions
- 📧 Automated email reports
- 📈 Multiple report formats (HTML, CSV, JSON, TXT, Markdown)
- ⚡ Concurrent load testing
- 🎯 Configurable test scenarios

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+

### Build and Run

```bash
# Clone the repository
git clone <your-repo-url>
cd hoale-automation-app

# Build the project
mvn clean package

# Start the application
java -jar target/hoale-automation-app-1.0.0.jar

# Or use the automated test runner
chmod +x run-tests.sh
./run-tests.sh
```

### API Endpoints

- `GET /api/health` - Health check
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create user
- `GET /api/automation/info` - System information
- `GET /api/automation/test/{testType}` - Run test by type
- `GET /api/automation/stress` - CPU stress test

### Performance Testing

```bash
# Run specific performance test
java -cp "target/classes:target/lib/*" com.hoale.automation.Main performance-config/api-performance.properties

# Available test configurations:
# - api-performance.properties: Basic API health check test
# - load-test.properties: High concurrency load test
# - stress-test.properties: System stress test
```

## Configuration

### Application Properties
```properties
server.port=8080
spring.application.name=hoale-automation-app
info.app.developer=leanhhoa30012004
info.app.created=2025-07-23 18:43:23 UTC
```

### Performance Test Configuration
```properties
targetUrl=http://localhost:8080/api/health
httpMethod=GET
threadCount=10
requestsPerThread=50
testDuration=300
testAuthor=leanhhoa30012004
```

## CI/CD Integration

The project includes GitHub Actions workflow for automated testing:

1. **Build** - Compiles and packages the application
2. **Start App** - Runs the application in background
3. **Performance Test** - Executes all performance test suites
4. **Generate Reports** - Creates comprehensive performance reports
5. **Send Email** - Emails results to developers and stakeholders

### Setup GitHub Secrets

```
SMTP_USERNAME: your-email@gmail.com
SMTP_PASSWORD: your-app-password
```

## Reports

Performance test reports are generated in multiple formats:

- **HTML**: Comprehensive visual report with charts and analysis
- **CSV**: Raw data for spreadsheet analysis
- **JSON**: Machine-readable format for API integration
- **TXT**: Plain text summary
- **Markdown**: Documentation-friendly format

Reports include:
- 📊 Success rate and failure analysis
- ⏱️ Response time statistics (min, max, average)
- 🚀 Throughput metrics
- 💡 Performance recommendations
- 📈 Historical trend data

## Directory Structure

```
hoale-automation-app/
├── src/main/java/com/hoale/automation/     # Application source
├── src/test/java/com/hoale/automation/     # Performance testing framework
├── performance-config/                     # Test configurations
├── .github/workflows/                      # CI/CD pipeline
├── reports/                               # Generated reports (auto-created)
└── logs/                                  # Application logs (auto-created)
```

## Development

### Adding New Endpoints

1. Create controller method in `UserController.java`
2. Add business logic in `UserService.java`
3. Update performance test configuration if needed

### Customizing Performance Tests

1. Modify existing config files in `performance-config/`
2. Create new test scenarios by copying existing configs
3. Adjust thresholds in `PerformanceTestEngine.java`

### Extending Reports

1. Update `PerformanceReportGenerator.java`
2. Add new report formats or metrics
3. Customize email templates in GitHub Actions

## Troubleshooting

### Common Issues

1. **Application won't start**
   ```bash
   # Check port availability
   lsof -i :8080
   # Use different port
   java -jar target/hoale-automation-app-1.0.0.jar --server.port=8081
   ```

2. **Performance tests fail**
   ```bash
   # Check application health
   curl http://localhost:8080/api/health
   # Review logs
   tail -f logs/hoale-automation-app.log
   ```

3. **Reports not generated**
   ```bash
   # Check permissions
   ls -la reports/
   # Create directory manually
   mkdir -p reports
   ```

## Contributing

1. Fork the repository
2. Create feature branch: `git checkout -b feature/my-feature`
3. Commit changes: `git commit -am 'Add my feature'`
4. Push to branch: `git push origin feature/my-feature`
5. Submit pull request

## License

This project is developed by leanhhoa30012004 and is available for educational and testing purposes.

## Contact

**Developer:** leanhhoa30012004  
**Email:** leanhhoa30012004@gmail.com  
**Created:** 2025-07-23 18:43:23 UTC

---

*Hoale Automation Performance Testing Framework v1.0*