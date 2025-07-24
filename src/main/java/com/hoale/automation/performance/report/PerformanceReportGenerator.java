package com.hoale.automation.performance.report;

import com.hoale.automation.performance.model.TestResult;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Performance Report Generator
 * Generates HTML, CSV, and JSON reports
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:52:31 UTC
 */
public class PerformanceReportGenerator {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public ReportFiles generateReports(TestResult testResult, String testName, String author) {
        try {
            // Create reports directory
            Path reportsDir = Paths.get("reports");
            Files.createDirectories(reportsDir);

            String timestamp = LocalDateTime.now().format(formatter);
            String baseFileName = String.format("%s_%s_%s", testName.replaceAll("\\s+", "_"), author, timestamp);

            // Generate different report formats
            String htmlFile = generateHtmlReport(testResult, testName, author, baseFileName);
            String csvFile = generateCsvReport(testResult, testName, author, baseFileName);
            String jsonFile = generateJsonReport(testResult, testName, author, baseFileName);
            String txtFile = generateTextReport(testResult, testName, author, baseFileName);

            System.out.println("📊 Reports generated:");
            System.out.println("   - HTML: " + htmlFile);
            System.out.println("   - CSV: " + csvFile);
            System.out.println("   - JSON: " + jsonFile);
            System.out.println("   - TXT: " + txtFile);

            return new ReportFiles(htmlFile, csvFile, jsonFile, txtFile);

        } catch (Exception e) {
            System.err.println("❌ Failed to generate reports: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private String generateHtmlReport(TestResult testResult, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".html";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<title>Performance Test Report - ").append(testName).append("</title>\n");
        html.append("<style>\n");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }\n");
        html.append(".container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        html.append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; border-radius: 10px; margin-bottom: 20px; }\n");
        html.append(".metrics { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 20px 0; }\n");
        html.append(".metric-card { background: #f8f9fa; border: 1px solid #dee2e6; border-radius: 8px; padding: 15px; text-align: center; }\n");
        html.append(".metric-value { font-size: 2em; font-weight: bold; color: #495057; }\n");
        html.append(".metric-label { color: #6c757d; margin-top: 5px; }\n");
        html.append(".success { color: #28a745; }\n");
        html.append(".warning { color: #ffc107; }\n");
        html.append(".danger { color: #dc3545; }\n");
        html.append(".info-section { background: #e9ecef; padding: 15px; border-radius: 8px; margin: 20px 0; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<div class='container'>\n");
        html.append("<div class='header'>\n");
        html.append("<h1>🚀 Automation Performance Test Report</h1>\n");
        html.append("<h2>").append(testName).append("</h2>\n");
        html.append("<p>Generated on: ").append(LocalDateTime.now()).append("</p>\n");
        html.append("</div>\n");

        // Test Information
        html.append("<div class='info-section'>\n");
        html.append("<h3>📋 Test Information</h3>\n");
        html.append("<p><strong>Test Name:</strong> ").append(testName).append("</p>\n");
        html.append("<p><strong>Test Author:</strong> ").append(author).append("</p>\n");
        html.append("<p><strong>Start Time:</strong> ").append(testResult.getStartTime()).append("</p>\n");
        html.append("<p><strong>End Time:</strong> ").append(testResult.getEndTime()).append("</p>\n");
        html.append("<p><strong>Framework:</strong> Automation Performance Testing v1.0</p>\n");
        html.append("<p><strong>Repository:</strong> automation-performance-testing-with-github-action</p>\n");
        html.append("</div>\n");

        // Metrics Cards
        html.append("<div class='metrics'>\n");

        html.append("<div class='metric-card'>\n");
        html.append("<div class='metric-value'>").append(testResult.getTotalRequests()).append("</div>\n");
        html.append("<div class='metric-label'>Total Requests</div>\n");
        html.append("</div>\n");

        html.append("<div class='metric-card'>\n");
        html.append("<div class='metric-value'>").append(testResult.getSuccessfulRequests()).append("</div>\n");
        html.append("<div class='metric-label'>Successful Requests</div>\n");
        html.append("</div>\n");

        String successRateClass = testResult.getSuccessRate() >= 95 ? "success" : testResult.getSuccessRate() >= 80 ? "warning" : "danger";
        html.append("<div class='metric-card'>\n");
        html.append("<div class='metric-value ").append(successRateClass).append("'>").append(String.format("%.2f%%", testResult.getSuccessRate())).append("</div>\n");
        html.append("<div class='metric-label'>Success Rate</div>\n");
        html.append("</div>\n");

        html.append("<div class='metric-card'>\n");
        html.append("<div class='metric-value'>").append(String.format("%.2f ms", testResult.getAverageResponseTime())).append("</div>\n");
        html.append("<div class='metric-label'>Average Response Time</div>\n");
        html.append("</div>\n");

        html.append("<div class='metric-card'>\n");
        html.append("<div class='metric-value'>").append(String.format("%.2f", testResult.getThroughput())).append("</div>\n");
        html.append("<div class='metric-label'>Throughput (req/sec)</div>\n");
        html.append("</div>\n");

        html.append("<div class='metric-card'>\n");
        html.append("<div class='metric-value'>").append(testResult.getFailedRequests()).append("</div>\n");
        html.append("<div class='metric-label'>Failed Requests</div>\n");
        html.append("</div>\n");

        html.append("</div>\n");

        // Test Status
        html.append("<div class='info-section'>\n");
        html.append("<h3>🎯 Test Result</h3>\n");
        if (testResult.getSuccessRate() >= 95.0) {
            html.append("<p class='success'><strong>✅ PASSED</strong> - Performance test completed successfully!</p>\n");
        } else {
            html.append("<p class='danger'><strong>❌ FAILED</strong> - Performance test did not meet success criteria!</p>\n");
        }
        html.append("</div>\n");

        // Footer
        html.append("<div class='info-section'>\n");
        html.append("<h3>👨‍💻 Developer Information</h3>\n");
        html.append("<p><strong>Developer:</strong> ").append(author).append("</p>\n");
        html.append("<p><strong>Framework:</strong> Automation Performance Testing with GitHub Action</p>\n");
        html.append("<p><strong>Version:</strong> 1.0.0</p>\n");
        html.append("<p><strong>Generated:</strong> ").append(LocalDateTime.now()).append(" UTC</p>\n");
        html.append("</div>\n");

        html.append("</div>\n</body>\n</html>");

        Files.write(Paths.get(fileName), html.toString().getBytes());
        return fileName;
    }

    private String generateCsvReport(TestResult testResult, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".csv";

        StringBuilder csv = new StringBuilder();
        csv.append("Metric,Value,Unit\n");
        csv.append("Test Name,").append(testName).append(",\n");
        csv.append("Test Author,").append(author).append(",\n");
        csv.append("Start Time,").append(testResult.getStartTime()).append(",\n");
        csv.append("End Time,").append(testResult.getEndTime()).append(",\n");
        csv.append("Total Requests,").append(testResult.getTotalRequests()).append(",count\n");
        csv.append("Successful Requests,").append(testResult.getSuccessfulRequests()).append(",count\n");
        csv.append("Failed Requests,").append(testResult.getFailedRequests()).append(",count\n");
        csv.append("Success Rate,").append(String.format("%.2f", testResult.getSuccessRate())).append(",percent\n");
        csv.append("Average Response Time,").append(String.format("%.2f", testResult.getAverageResponseTime())).append(",ms\n");
        csv.append("Throughput,").append(String.format("%.2f", testResult.getThroughput())).append(",req/sec\n");
        csv.append("Framework,Automation Performance Testing v1.0,\n");
        csv.append("Repository,automation-performance-testing-with-github-action,\n");
        csv.append("Generated,").append(LocalDateTime.now()).append(",UTC\n");

        Files.write(Paths.get(fileName), csv.toString().getBytes());
        return fileName;
    }

    private String generateJsonReport(TestResult testResult, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".json";

        Map<String, Object> report = new HashMap<>();
        report.put("testName", testName);
        report.put("testAuthor", author);
        report.put("startTime", testResult.getStartTime().toString());
        report.put("endTime", testResult.getEndTime().toString());
        report.put("totalRequests", testResult.getTotalRequests());
        report.put("successfulRequests", testResult.getSuccessfulRequests());
        report.put("failedRequests", testResult.getFailedRequests());
        report.put("successRate", testResult.getSuccessRate());
        report.put("averageResponseTime", testResult.getAverageResponseTime());
        report.put("throughput", testResult.getThroughput());
        report.put("framework", "Automation Performance Testing v1.0");
        report.put("repository", "automation-performance-testing-with-github-action");
        report.put("developer", author);
        report.put("generated", LocalDateTime.now().toString());
        report.put("status", testResult.getSuccessRate() >= 95.0 ? "PASSED" : "FAILED");

        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
        Files.write(Paths.get(fileName), json.getBytes());
        return fileName;
    }

    private String generateTextReport(TestResult testResult, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".txt";

        StringBuilder txt = new StringBuilder();
        txt.append("========================================\n");
        txt.append("Automation Performance Test Report\n");
        txt.append("========================================\n\n");

        txt.append("Test Information:\n");
        txt.append("-----------------\n");
        txt.append("Test Name: ").append(testName).append("\n");
        txt.append("Test Author: ").append(author).append("\n");
        txt.append("Start Time: ").append(testResult.getStartTime()).append("\n");
        txt.append("End Time: ").append(testResult.getEndTime()).append("\n");
        txt.append("Framework: Automation Performance Testing v1.0\n");
        txt.append("Repository: automation-performance-testing-with-github-action\n\n");

        txt.append("Test Results:\n");
        txt.append("-------------\n");
        txt.append("Total Requests: ").append(testResult.getTotalRequests()).append("\n");
        txt.append("Successful Requests: ").append(testResult.getSuccessfulRequests()).append("\n");
        txt.append("Failed Requests: ").append(testResult.getFailedRequests()).append("\n");
        txt.append("Success Rate: ").append(String.format("%.2f%%", testResult.getSuccessRate())).append("\n");
        txt.append("Average Response Time: ").append(String.format("%.2f ms", testResult.getAverageResponseTime())).append("\n");
        txt.append("Throughput: ").append(String.format("%.2f req/sec", testResult.getThroughput())).append("\n\n");

        txt.append("Test Status:\n");
        txt.append("------------\n");
        if (testResult.getSuccessRate() >= 95.0) {
            txt.append("✅ PASSED - Performance test completed successfully!\n");
        } else {
            txt.append("❌ FAILED - Performance test did not meet success criteria!\n");
        }

        txt.append("\nGenerated by: ").append(author).append("\n");
        txt.append("Generated on: ").append(LocalDateTime.now()).append(" UTC\n");

        Files.write(Paths.get(fileName), txt.toString().getBytes());
        return fileName;
    }

    public static class ReportFiles {
        private final String htmlFile;
        private final String csvFile;
        private final String jsonFile;
        private final String txtFile;

        public ReportFiles(String htmlFile, String csvFile, String jsonFile, String txtFile) {
            this.htmlFile = htmlFile;
            this.csvFile = csvFile;
            this.jsonFile = jsonFile;
            this.txtFile = txtFile;
        }

        // Getters
        public String getHtmlFile() { return htmlFile; }
        public String getCsvFile() { return csvFile; }
        public String getJsonFile() { return jsonFile; }
        public String getTxtFile() { return txtFile; }
    }
}