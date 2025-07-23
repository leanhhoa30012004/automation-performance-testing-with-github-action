package com.hoale.automation.performance.report;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class PerformanceReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceReportGenerator.class);

    private final PerformanceConfig config;
    private final TestResult testResult;
    private final String reportDirectory;

    public PerformanceReportGenerator(PerformanceConfig config, TestResult testResult) {
        this.config = config;
        this.testResult = testResult;
        this.reportDirectory = "reports";
        createReportDirectory();
    }

    private void createReportDirectory() {
        File dir = new File(reportDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public void generateAllReports() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String baseFileName = String.format("performance_test_%s", timestamp);

        try {
            generateHTMLReport(baseFileName + ".html");
            generateCSVReport(baseFileName + ".csv");
            generateJSONReport(baseFileName + ".json");
            generateTextReport(baseFileName + ".txt");
            generateMarkdownReport(baseFileName + ".md");

            logger.info("All reports generated successfully in '{}' directory", reportDirectory);
            logger.info("Report files:");
            logger.info("   HTML Report: {}.html", baseFileName);
            logger.info("   CSV Data: {}.csv", baseFileName);
            logger.info("   JSON Data: {}.json", baseFileName);
            logger.info("   Text Report: {}.txt", baseFileName);
            logger.info("   Markdown Report: {}.md", baseFileName);

        } catch (IOException e) {
            logger.error("Error generating reports", e);
        }
    }

    private void generateHTMLReport(String fileName) throws IOException {
        String filePath = reportDirectory + "/" + fileName;

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(generateHTMLContent());
        }

        logger.info("HTML report generated: {}", filePath);
    }

    private String generateHTMLContent() {
        Duration testDuration = Duration.between(testResult.getStartTime(), testResult.getEndTime());
        double durationSeconds = testDuration.getSeconds() + testDuration.getNano() / 1_000_000_000.0;

        String performanceLevel = getPerformanceLevel();
        String statusColor = getStatusColor(performanceLevel);

        return String.format("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Performance Test Report</title>
                <style>
                    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 20px; background-color: #f5f5f5; }
                    .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                    h1 { color: #2c3e50; text-align: center; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
                    h2 { color: #34495e; border-left: 4px solid #3498db; padding-left: 15px; }
                    .summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 30px 0; }
                    .metric-card { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; border-radius: 10px; text-align: center; }
                    .metric-value { font-size: 2em; font-weight: bold; margin: 10px 0; }
                    .metric-label { font-size: 0.9em; opacity: 0.9; }
                    .status-%s { background: %s; }
                    .config-table, .results-table { width: 100%%; border-collapse: collapse; margin: 20px 0; }
                    .config-table th, .config-table td, .results-table th, .results-table td { 
                        border: 1px solid #ddd; padding: 12px; text-align: left; 
                    }
                    .config-table th, .results-table th { background-color: #f8f9fa; font-weight: bold; }
                    .conclusion { background: #e8f5e8; border: 1px solid #4caf50; border-radius: 5px; padding: 20px; margin: 20px 0; }
                    .recommendation { background: #fff3cd; border: 1px solid #ffc107; border-radius: 5px; padding: 20px; margin: 20px 0; }
                    .chart-placeholder { background: #f8f9fa; border: 2px dashed #dee2e6; height: 200px; display: flex; align-items: center; justify-content: center; color: #6c757d; margin: 20px 0; }
                    .excellent { color: #28a745; font-weight: bold; }
                    .good { color: #ffc107; font-weight: bold; }
                    .acceptable { color: #fd7e14; font-weight: bold; }
                    .poor { color: #dc3545; font-weight: bold; }
                </style>
            </head>
            <body>
                <div class="container">
                    <h1>Performance Test Report</h1>
                    
                    <div class="summary">
                        <div class="metric-card">
                            <div class="metric-label">Total Requests</div>
                            <div class="metric-value">%,d</div>
                        </div>
                        <div class="metric-card">
                            <div class="metric-label">Success Rate</div>
                            <div class="metric-value">%.1f%%</div>
                        </div>
                        <div class="metric-card">
                            <div class="metric-label">Avg Response Time</div>
                            <div class="metric-value">%.0f ms</div>
                        </div>
                        <div class="metric-card">
                            <div class="metric-label">Throughput</div>
                            <div class="metric-value">%.1f req/s</div>
                        </div>
                    </div>
                    
                    <h2>Test Configuration</h2>
                    <table class="config-table">
                        <tr><th>Parameter</th><th>Value</th></tr>
                        <tr><td>Target URL</td><td>%s</td></tr>
                        <tr><td>Number of Threads</td><td>%d</td></tr>
                        <tr><td>Requests per Thread</td><td>%d</td></tr>
                        <tr><td>HTTP Method</td><td>%s</td></tr>
                        <tr><td>Test Duration</td><td>%d seconds</td></tr>
                        <tr><td>Connection Timeout</td><td>%d ms</td></tr>
                        <tr><td>Response Timeout</td><td>%d ms</td></tr>
                    </table>
                    
                    <h2>Test Results</h2>
                    <table class="results-table">
                        <tr><th>Metric</th><th>Value</th><th>Status</th></tr>
                        <tr><td>Start Time</td><td>%s</td><td class="excellent">OK</td></tr>
                        <tr><td>End Time</td><td>%s</td><td class="excellent">OK</td></tr>
                        <tr><td>Duration</td><td>%.2f seconds</td><td class="excellent">OK</td></tr>
                        <tr><td>Total Requests</td><td>%,d</td><td class="excellent">OK</td></tr>
                        <tr><td>Successful Requests</td><td>%,d</td><td class="%s">%s</td></tr>
                        <tr><td>Failed Requests</td><td>%,d</td><td class="%s">%s</td></tr>
                        <tr><td>Success Rate</td><td>%.2f%%</td><td class="%s">%s</td></tr>
                        <tr><td>Average Response Time</td><td>%.2f ms</td><td class="%s">%s</td></tr>
                        <tr><td>Min Response Time</td><td>%d ms</td><td class="excellent">OK</td></tr>
                        <tr><td>Max Response Time</td><td>%d ms</td><td class="%s">%s</td></tr>
                        <tr><td>Throughput</td><td>%.2f req/s</td><td class="%s">%s</td></tr>
                    </table>
                    
                    <div class="conclusion status-%s">
                        <h2>Overall Assessment: %s</h2>
                        <p><strong>Performance Level:</strong> %s</p>
                        %s
                    </div>
                    
                    <div class="recommendation">
                        <h2>Recommendations</h2>
                        %s
                    </div>
                    
                    <div class="chart-placeholder">
                        Response Time Distribution Chart<br>
                        (Chart integration can be added with Chart.js or similar library)
                    </div>
                    
                    <footer style="text-align: center; margin-top: 40px; color: #6c757d; border-top: 1px solid #dee2e6; padding-top: 20px;">
                        <p>Report generated on %s by Performance Test Engine v1.0</p>
                        <p>Tester: %s</p>
                    </footer>
                </div>
            </body>
            </html>
            """,
                // CSS status and color
                performanceLevel.toLowerCase(), statusColor,
                // Metrics
                testResult.getTotalRequests(),
                testResult.getSuccessRate(),
                testResult.getAverageResponseTime(),
                testResult.getThroughput(),
                // Config
                config.getTargetUrl(),
                config.getThreadCount(),
                config.getRequestsPerThread(),
                config.getHttpMethod(),
                config.getTestDuration(),
                config.getConnectionTimeout(),
                config.getResponseTimeout(),
                // Results
                testResult.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                testResult.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                durationSeconds,
                testResult.getTotalRequests(),
                testResult.getSuccessfulRequests(), getSuccessStatusClass(), getSuccessStatusText(),
                testResult.getFailedRequests(), getFailureStatusClass(), getFailureStatusText(),
                testResult.getSuccessRate(), getSuccessRateStatusClass(), getSuccessRateStatusText(),
                testResult.getAverageResponseTime(), getResponseTimeStatusClass(), getResponseTimeStatusText(),
                testResult.getMinResponseTime(),
                testResult.getMaxResponseTime(), getMaxResponseTimeStatusClass(), getMaxResponseTimeStatusText(),
                testResult.getThroughput(), getThroughputStatusClass(), getThroughputStatusText(),
                // Conclusion
                performanceLevel.toLowerCase(), performanceLevel, performanceLevel,
                generateHTMLConclusion(),
                generateHTMLRecommendations(),
                // Footer
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "leanhhoa30012004"
        );
    }

    private String getPerformanceLevel() {
        double successRate = testResult.getSuccessRate();
        double avgResponseTime = testResult.getAverageResponseTime();
        double throughput = testResult.getThroughput();

        if (successRate >= 99.0 && avgResponseTime <= 200 && throughput >= 100) {
            return "EXCELLENT";
        } else if (successRate >= 95.0 && avgResponseTime <= 500 && throughput >= 50) {
            return "GOOD";
        } else if (successRate >= 90.0 && avgResponseTime <= 1000) {
            return "ACCEPTABLE";
        } else {
            return "POOR";
        }
    }

    private String getStatusColor(String level) {
        return switch (level) {
            case "EXCELLENT" -> "linear-gradient(135deg, #2ecc71, #27ae60)";
            case "GOOD" -> "linear-gradient(135deg, #f39c12, #e67e22)";
            case "ACCEPTABLE" -> "linear-gradient(135deg, #f39c12, #d35400)";
            default -> "linear-gradient(135deg, #e74c3c, #c0392b)";
        };
    }

    private String getSuccessStatusClass() {
        return testResult.getSuccessfulRequests() > 0 ? "excellent" : "poor";
    }

    private String getSuccessStatusText() {
        return testResult.getSuccessfulRequests() > 0 ? "OK" : "FAILED";
    }

    private String getFailureStatusClass() {
        return testResult.getFailedRequests() == 0 ? "excellent" : "poor";
    }

    private String getFailureStatusText() {
        return testResult.getFailedRequests() == 0 ? "OK" : "WARNING";
    }

    private String getSuccessRateStatusClass() {
        double rate = testResult.getSuccessRate();
        if (rate >= 99.0) return "excellent";
        if (rate >= 95.0) return "good";
        if (rate >= 90.0) return "acceptable";
        return "poor";
    }

    private String getSuccessRateStatusText() {
        double rate = testResult.getSuccessRate();
        if (rate >= 99.0) return "EXCELLENT";
        if (rate >= 95.0) return "GOOD";
        if (rate >= 90.0) return "ACCEPTABLE";
        return "POOR";
    }

    private String getResponseTimeStatusClass() {
        double time = testResult.getAverageResponseTime();
        if (time <= 200) return "excellent";
        if (time <= 500) return "good";
        if (time <= 1000) return "acceptable";
        return "poor";
    }

    private String getResponseTimeStatusText() {
        double time = testResult.getAverageResponseTime();
        if (time <= 200) return "EXCELLENT";
        if (time <= 500) return "GOOD";
        if (time <= 1000) return "ACCEPTABLE";
        return "POOR";
    }

    private String getMaxResponseTimeStatusClass() {
        long maxTime = testResult.getMaxResponseTime();
        if (maxTime <= 1000) return "excellent";
        if (maxTime <= 3000) return "acceptable";
        return "poor";
    }

    private String getMaxResponseTimeStatusText() {
        long maxTime = testResult.getMaxResponseTime();
        if (maxTime <= 1000) return "OK";
        if (maxTime <= 3000) return "WARNING";
        return "CRITICAL";
    }

    private String getThroughputStatusClass() {
        double throughput = testResult.getThroughput();
        if (throughput >= 100) return "excellent";
        if (throughput >= 50) return "good";
        return "poor";
    }

    private String getThroughputStatusText() {
        double throughput = testResult.getThroughput();
        if (throughput >= 100) return "HIGH";
        if (throughput >= 50) return "MEDIUM";
        return "LOW";
    }

    private String generateHTMLConclusion() {
        StringBuilder conclusion = new StringBuilder();
        double successRate = testResult.getSuccessRate();
        double avgResponseTime = testResult.getAverageResponseTime();
        double throughput = testResult.getThroughput();

        conclusion.append("<p><strong>Test Summary:</strong></p><ul>");

        if (successRate >= 99.0) {
            conclusion.append("<li><strong>Reliability:</strong> Excellent - System handles load very well</li>");
        } else if (successRate >= 95.0) {
            conclusion.append("<li><strong>Reliability:</strong> Good - Minor reliability issues detected</li>");
        } else {
            conclusion.append("<li><strong>Reliability:</strong> Poor - Significant reliability issues need attention</li>");
        }

        if (avgResponseTime <= 200) {
            conclusion.append("<li><strong>Performance:</strong> Excellent response times</li>");
        } else if (avgResponseTime <= 500) {
            conclusion.append("<li><strong>Performance:</strong> Acceptable response times</li>");
        } else {
            conclusion.append("<li><strong>Performance:</strong> Slow response times - optimization needed</li>");
        }

        if (throughput >= 100) {
            conclusion.append("<li><strong>Scalability:</strong> High throughput - system scales well</li>");
        } else if (throughput >= 50) {
            conclusion.append("<li><strong>Scalability:</strong> Medium throughput</li>");
        } else {
            conclusion.append("<li><strong>Scalability:</strong> Low throughput - scaling issues detected</li>");
        }

        conclusion.append("</ul>");
        return conclusion.toString();
    }

    private String generateHTMLRecommendations() {
        StringBuilder recommendations = new StringBuilder();
        double successRate = testResult.getSuccessRate();
        double avgResponseTime = testResult.getAverageResponseTime();
        double throughput = testResult.getThroughput();
        long maxResponseTime = testResult.getMaxResponseTime();

        recommendations.append("<ul>");

        if (successRate < 99.0) {
            recommendations.append("<li><strong>Improve Reliability:</strong> Investigate and fix causes of failed requests</li>");
        }

        if (avgResponseTime > 500) {
            recommendations.append("<li><strong>Optimize Performance:</strong> Consider caching, database optimization, or CDN implementation</li>");
        }

        if (throughput < 50) {
            recommendations.append("<li><strong>Scale System:</strong> Consider horizontal scaling or load balancing</li>");
        }

        if (maxResponseTime > 3000) {
            recommendations.append("<li><strong>Address Outliers:</strong> Investigate causes of very slow responses</li>");
        }

        if (testResult.getFailedRequests() > testResult.getTotalRequests() * 0.01) {
            recommendations.append("<li><strong>Error Handling:</strong> Improve error handling and retry mechanisms</li>");
        }

        recommendations.append("<li><strong>Monitoring:</strong> Implement continuous performance monitoring</li>");
        recommendations.append("<li><strong>Regular Testing:</strong> Schedule regular performance tests</li>");
        recommendations.append("<li><strong>Documentation:</strong> Document performance baselines and improvements</li>");

        recommendations.append("</ul>");
        return recommendations.toString();
    }

    private void generateCSVReport(String fileName) throws IOException {
        String filePath = reportDirectory + "/" + fileName;

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Metric,Value,Unit\n");
            writer.write(String.format("Test Start Time,%s,\n", testResult.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write(String.format("Test End Time,%s,\n", testResult.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write(String.format("Target URL,%s,\n", config.getTargetUrl()));
            writer.write(String.format("HTTP Method,%s,\n", config.getHttpMethod()));
            writer.write(String.format("Number of Threads,%d,threads\n", config.getThreadCount()));
            writer.write(String.format("Requests per Thread,%d,requests\n", config.getRequestsPerThread()));
            writer.write(String.format("Total Requests,%d,requests\n", testResult.getTotalRequests()));
            writer.write(String.format("Successful Requests,%d,requests\n", testResult.getSuccessfulRequests()));
            writer.write(String.format("Failed Requests,%d,requests\n", testResult.getFailedRequests()));
            writer.write(String.format("Success Rate,%.2f,%%\n", testResult.getSuccessRate()));
            writer.write(String.format("Average Response Time,%.2f,ms\n", testResult.getAverageResponseTime()));
            writer.write(String.format("Min Response Time,%d,ms\n", testResult.getMinResponseTime()));
            writer.write(String.format("Max Response Time,%d,ms\n", testResult.getMaxResponseTime()));
            writer.write(String.format("Throughput,%.2f,requests/second\n", testResult.getThroughput()));
            writer.write(String.format("Performance Level,%s,\n", getPerformanceLevel()));
        }

        logger.info("CSV report generated: {}", filePath);
    }

    private void generateJSONReport(String fileName) throws IOException {
        String filePath = reportDirectory + "/" + fileName;

        Map<String, Object> report = new HashMap<>();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("testStartTime", testResult.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        metadata.put("testEndTime", testResult.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        metadata.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        metadata.put("tester", "leanhhoa30012004");
        metadata.put("version", "1.0");
        report.put("metadata", metadata);

        Map<String, Object> configData = new HashMap<>();
        configData.put("targetUrl", config.getTargetUrl());
        configData.put("httpMethod", config.getHttpMethod());
        configData.put("threadCount", config.getThreadCount());
        configData.put("requestsPerThread", config.getRequestsPerThread());
        configData.put("testDuration", config.getTestDuration());
        configData.put("connectionTimeout", config.getConnectionTimeout());
        configData.put("responseTimeout", config.getResponseTimeout());
        report.put("configuration", configData);

        Map<String, Object> results = new HashMap<>();
        results.put("totalRequests", testResult.getTotalRequests());
        results.put("successfulRequests", testResult.getSuccessfulRequests());
        results.put("failedRequests", testResult.getFailedRequests());
        results.put("successRate", testResult.getSuccessRate());
        results.put("averageResponseTime", testResult.getAverageResponseTime());
        results.put("minResponseTime", testResult.getMinResponseTime());
        results.put("maxResponseTime", testResult.getMaxResponseTime());
        results.put("throughput", testResult.getThroughput());
        report.put("results", results);

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("performanceLevel", getPerformanceLevel());
        analysis.put("conclusion", generateTextConclusion());
        analysis.put("recommendations", generateTextRecommendations());
        report.put("analysis", analysis);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), report);

        logger.info("JSON report generated: {}", filePath);
    }

    private void generateTextReport(String fileName) throws IOException {
        String filePath = reportDirectory + "/" + fileName;

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("================================================================================\n");
            writer.write("                        PERFORMANCE TEST REPORT\n");
            writer.write("================================================================================\n\n");

            writer.write("TEST CONFIGURATION:\n");
            writer.write("----------------------------------------\n");
            writer.write(String.format("Target URL: %s\n", config.getTargetUrl()));
            writer.write(String.format("HTTP Method: %s\n", config.getHttpMethod()));
            writer.write(String.format("Number of Threads: %d\n", config.getThreadCount()));
            writer.write(String.format("Requests per Thread: %d\n", config.getRequestsPerThread()));
            writer.write(String.format("Test Duration: %d seconds\n", config.getTestDuration()));
            writer.write(String.format("Connection Timeout: %d ms\n", config.getConnectionTimeout()));
            writer.write(String.format("Response Timeout: %d ms\n\n", config.getResponseTimeout()));

            writer.write("TEST RESULTS:\n");
            writer.write("----------------------------------------\n");
            writer.write(String.format("Test Start: %s\n", testResult.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write(String.format("Test End: %s\n", testResult.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write(String.format("Total Requests: %,d\n", testResult.getTotalRequests()));
            writer.write(String.format("Successful Requests: %,d\n", testResult.getSuccessfulRequests()));
            writer.write(String.format("Failed Requests: %,d\n", testResult.getFailedRequests()));
            writer.write(String.format("Success Rate: %.2f%%\n", testResult.getSuccessRate()));
            writer.write(String.format("Average Response Time: %.2f ms\n", testResult.getAverageResponseTime()));
            writer.write(String.format("Min Response Time: %d ms\n", testResult.getMinResponseTime()));
            writer.write(String.format("Max Response Time: %d ms\n", testResult.getMaxResponseTime()));
            writer.write(String.format("Throughput: %.2f requests/second\n\n", testResult.getThroughput()));

            writer.write("PERFORMANCE ANALYSIS:\n");
            writer.write("----------------------------------------\n");
            writer.write(String.format("Performance Level: %s\n\n", getPerformanceLevel()));

            writer.write("CONCLUSION:\n");
            writer.write(generateTextConclusion() + "\n\n");

            writer.write("RECOMMENDATIONS:\n");
            writer.write(generateTextRecommendations() + "\n\n");

            writer.write("================================================================================\n");
            writer.write(String.format("Report generated on: %s\n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write("Generated by: Performance Test Engine v1.0\n");
            writer.write("Tester: leanhhoa30012004\n");
            writer.write("================================================================================\n");
        }

        logger.info("Text report generated: {}", filePath);
    }

    private void generateMarkdownReport(String fileName) throws IOException {
        String filePath = reportDirectory + "/" + fileName;

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("# Performance Test Report\n\n");

            writer.write("## Executive Summary\n\n");
            writer.write(String.format("- **Performance Level:** %s\n", getPerformanceLevel()));
            writer.write(String.format("- **Total Requests:** %,d\n", testResult.getTotalRequests()));
            writer.write(String.format("- **Success Rate:** %.1f%%\n", testResult.getSuccessRate()));
            writer.write(String.format("- **Average Response Time:** %.0f ms\n", testResult.getAverageResponseTime()));
            writer.write(String.format("- **Throughput:** %.1f req/s\n\n", testResult.getThroughput()));

            writer.write("## Test Configuration\n\n");
            writer.write("| Parameter | Value |\n");
            writer.write("|-----------|-------|\n");
            writer.write(String.format("| Target URL | %s |\n", config.getTargetUrl()));
            writer.write(String.format("| HTTP Method | %s |\n", config.getHttpMethod()));
            writer.write(String.format("| Threads | %d |\n", config.getThreadCount()));
            writer.write(String.format("| Requests/Thread | %d |\n", config.getRequestsPerThread()));
            writer.write(String.format("| Test Duration | %d sec |\n", config.getTestDuration()));
            writer.write(String.format("| Connection Timeout | %d ms |\n", config.getConnectionTimeout()));
            writer.write(String.format("| Response Timeout | %d ms |\n\n", config.getResponseTimeout()));

            writer.write("## Detailed Results\n\n");
            writer.write("| Metric | Value | Status |\n");
            writer.write("|--------|-------|--------|\n");
            writer.write(String.format("| Start Time | %s | OK |\n", testResult.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write(String.format("| End Time | %s | OK |\n", testResult.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write(String.format("| Total Requests | %,d | OK |\n", testResult.getTotalRequests()));
            writer.write(String.format("| Successful | %,d | %s |\n", testResult.getSuccessfulRequests(), getSuccessStatusText()));
            writer.write(String.format("| Failed | %,d | %s |\n", testResult.getFailedRequests(), getFailureStatusText()));
            writer.write(String.format("| Success Rate | %.2f%% | %s |\n", testResult.getSuccessRate(), getSuccessRateStatusText()));
            writer.write(String.format("| Avg Response | %.2f ms | %s |\n", testResult.getAverageResponseTime(), getResponseTimeStatusText()));
            writer.write(String.format("| Min Response | %d ms | OK |\n", testResult.getMinResponseTime()));
            writer.write(String.format("| Max Response | %d ms | %s |\n", testResult.getMaxResponseTime(), getMaxResponseTimeStatusText()));
            writer.write(String.format("| Throughput | %.2f req/s | %s |\n\n", testResult.getThroughput(), getThroughputStatusText()));

            writer.write("## Conclusion\n\n");
            writer.write(generateTextConclusion() + "\n\n");

            writer.write("## Recommendations\n\n");
            writer.write(generateTextRecommendations() + "\n\n");

            writer.write("---\n\n");
            writer.write(String.format("**Report Generated:** %s  \n", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
            writer.write("**Generated By:** Performance Test Engine v1.0  \n");
            writer.write("**Tester:** leanhhoa30012004  \n");
        }

        logger.info("Markdown report generated: {}", filePath);
    }

    private String generateTextConclusion() {
        StringBuilder conclusion = new StringBuilder();
        double successRate = testResult.getSuccessRate();
        double avgResponseTime = testResult.getAverageResponseTime();

        conclusion.append("Based on the performance test results:\n\n");

        if (successRate >= 99.0 && avgResponseTime <= 200) {
            conclusion.append("The system demonstrates EXCELLENT performance with high reliability and fast response times.\n");
            conclusion.append("The application is ready for production use and can handle the tested load efficiently.\n");
        } else if (successRate >= 95.0 && avgResponseTime <= 500) {
            conclusion.append("The system shows GOOD performance with acceptable response times.\n");
            conclusion.append("Minor optimizations may be beneficial but the system is generally stable.\n");
        } else if (successRate >= 90.0 && avgResponseTime <= 1000) {
            conclusion.append("The system performance is ACCEPTABLE but has room for improvement.\n");
            conclusion.append("Consider optimization before handling higher loads.\n");
        } else {
            conclusion.append("The system shows POOR performance with significant issues.\n");
            conclusion.append("Immediate optimization and investigation are required before production deployment.\n");
        }

        return conclusion.toString();
    }

    private String generateTextRecommendations() {
        StringBuilder recommendations = new StringBuilder();
        double successRate = testResult.getSuccessRate();
        double avgResponseTime = testResult.getAverageResponseTime();
        double throughput = testResult.getThroughput();

        if (successRate < 99.0) {
            recommendations.append("- Investigate and fix causes of request failures\n");
            recommendations.append("- Implement better error handling and retry mechanisms\n");
        }

        if (avgResponseTime > 500) {
            recommendations.append("- Optimize database queries and implement caching\n");
            recommendations.append("- Consider using a Content Delivery Network (CDN)\n");
            recommendations.append("- Review and optimize application algorithms\n");
        }

        if (throughput < 50) {
            recommendations.append("- Implement horizontal scaling (load balancing)\n");
            recommendations.append("- Optimize server configuration and resources\n");
            recommendations.append("- Consider asynchronous processing for heavy operations\n");
        }

        recommendations.append("- Implement continuous performance monitoring\n");
        recommendations.append("- Schedule regular performance testing\n");
        recommendations.append("- Document performance baselines and track improvements\n");

        return recommendations.toString();
    }
}