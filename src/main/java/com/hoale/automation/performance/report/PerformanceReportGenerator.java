package com.hoale.automation.performance.report;

import com.hoale.automation.performance.model.TestResult;
import com.hoale.automation.performance.model.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PerformanceReportGenerator {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public ReportFiles generateReports(TestResult testResult, TestConfig testConfig, String testName, String author) {
        try {
            java.nio.file.Path reportsDir = Paths.get("reports");
            Files.createDirectories(reportsDir);

            String timestamp = LocalDateTime.now().format(formatter);
            String baseFileName = String.format("%s_%s_%s", testName.replaceAll("\\s+", "_"), author, timestamp);

            String htmlFile = generateHtmlReport(testResult, testConfig, testName, author, baseFileName);
            String csvFile = generateCsvReport(testResult, testConfig, testName, author, baseFileName);
            String jsonFile = generateJsonReport(testResult, testConfig, testName, author, baseFileName);
            String txtFile = generateTextReport(testResult, testConfig, testName, author, baseFileName);

            System.out.println("Reports generated:");
            System.out.println("   - HTML: " + htmlFile);
            System.out.println("   - CSV: " + csvFile);
            System.out.println("   - JSON: " + jsonFile);
            System.out.println("   - TXT: " + txtFile);

            return new ReportFiles(htmlFile, csvFile, jsonFile, txtFile);

        } catch (Exception e) {
            System.err.println("Failed to generate reports: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Dashboard summary card
    private String dashboardCard(String title, String value, String gradient) {
        return "<div class='dashboard-card' style='background:linear-gradient(90deg," + gradient + ");'>"
                + "<div class='dashboard-title'>" + title + "</div>"
                + "<div class='dashboard-value'>" + value + "</div>"
                + "</div>";
    }

    // Helper: status label for HTML
    private String statusText(Object value, String metric) {
        switch (metric) {
            case "Success Rate": {
                double v = (double) value;
                if (v >= 99.0) return "<span class='excellent'>EXCELLENT</span>";
                if (v >= 95.0) return "<span class='ok'>OK</span>";
                if (v >= 80.0) return "<span class='warning'>WARNING</span>";
                return "<span class='danger'>LOW</span>";
            }
            case "Average Response Time": {
                double v = (double) value;
                if (v < 300) return "<span class='excellent'>EXCELLENT</span>";
                if (v < 800) return "<span class='acceptable'>ACCEPTABLE</span>";
                if (v < 2000) return "<span class='warning'>WARNING</span>";
                return "<span class='danger'>LOW</span>";
            }
            case "Throughput": {
                double v = (double) value;
                if (v >= 50) return "<span class='excellent'>EXCELLENT</span>";
                if (v >= 10) return "<span class='ok'>OK</span>";
                if (v >= 5) return "<span class='warning'>WARNING</span>";
                return "<span class='danger'>LOW</span>";
            }
            case "Failed Requests": {
                int v = (int) value;
                if (v == 0) return "<span class='ok'>OK</span>";
                if (v < 5) return "<span class='warning'>WARNING</span>";
                return "<span class='danger'>HIGH</span>";
            }
            case "Max Response Time": {
                double v = (double) value;
                if (v < 800) return "<span class='ok'>OK</span>";
                if (v < 2000) return "<span class='warning'>WARNING</span>";
                return "<span class='danger'>CRITICAL</span>";
            }
            default:
                return "<span class='ok'>OK</span>";
        }
    }

    // Helper: plain status label for CSV/TXT
    private String plainStatus(Object value, String metric) {
        switch (metric) {
            case "Success Rate": {
                double v = (double) value;
                if (v >= 99.0) return "EXCELLENT";
                if (v >= 95.0) return "OK";
                if (v >= 80.0) return "WARNING";
                return "LOW";
            }
            case "Average Response Time": {
                double v = (double) value;
                if (v < 300) return "EXCELLENT";
                if (v < 800) return "ACCEPTABLE";
                if (v < 2000) return "WARNING";
                return "LOW";
            }
            case "Throughput": {
                double v = (double) value;
                if (v >= 50) return "EXCELLENT";
                if (v >= 10) return "OK";
                if (v >= 5) return "WARNING";
                return "LOW";
            }
            case "Failed Requests": {
                int v = (int) value;
                if (v == 0) return "OK";
                if (v < 5) return "WARNING";
                return "HIGH";
            }
            case "Max Response Time": {
                double v = (double) value;
                if (v < 800) return "OK";
                if (v < 2000) return "WARNING";
                return "CRITICAL";
            }
            default:
                return "OK";
        }
    }

    private String overallLevel(TestResult t) {
        double successRate = t.getSuccessRate();
        double avgResp = t.getAverageResponseTime();
        double throughput = t.getThroughput();
        if (successRate >= 99 && avgResp < 300 && throughput >= 50) return "EXCELLENT";
        else if (successRate >= 95 && avgResp < 800 && throughput >= 10) return "ACCEPTABLE";
        else if (successRate >= 80 && avgResp < 2000 && throughput >= 5) return "WARNING";
        else return "LOW";
    }

    // Giải thích từng khía cạnh
    private String reliabilityComment(TestResult t) {
        if (t.getSuccessRate() >= 99) return "Excellent - System handles load very well.";
        if (t.getSuccessRate() >= 95) return "Good - Minor errors detected.";
        return "Poor - High error rate, check system stability.";
    }
    private String performanceComment(TestResult t) {
        if (t.getAverageResponseTime() < 300) return "Fast response times.";
        if (t.getAverageResponseTime() < 800) return "Acceptable, but may need optimization.";
        return "Slow response times - optimization needed.";
    }
    private String scalabilityComment(TestResult t) {
        if (t.getThroughput() >= 50) return "High throughput.";
        if (t.getThroughput() >= 10) return "Moderate throughput.";
        return "Low throughput - scaling issues detected.";
    }
    private String recommendation(TestResult t) {
        StringBuilder r = new StringBuilder();
        if (t.getSuccessRate() < 95) r.append("• Check system reliability, investigate error logs. ");
        if (t.getAverageResponseTime() > 800) r.append("• Optimize application/database to reduce response time. ");
        if (t.getThroughput() < 10) r.append("• Consider scaling infrastructure or optimizing concurrency. ");
        if (r.length() == 0) r.append("• System is performing well. Maintain current configuration.");
        return r.toString();
    }

    private String generateHtmlReport(TestResult testResult, TestConfig testConfig, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".html";
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='utf-8'>");
        html.append("<title>Performance Test Report - ").append(testName).append("</title>");
        html.append("<style>");
        html.append("body{font-family:Segoe UI,Arial,sans-serif;background:#f5f5f5;margin:0;padding:0;}");
        html.append(".container{max-width:950px;margin:30px auto;background:#fff;padding:32px 40px;border-radius:12px;box-shadow:0 4px 24px rgba(0,0,0,0.08);}");
        html.append(".dashboard{display:flex;gap:22px;margin-bottom:32px;}");
        html.append(".dashboard-card{flex:1;padding:22px 0 18px 0;border-radius:10px;color:#fff;box-shadow:0 2px 10px rgba(0,0,0,0.04);text-align:center;}");
        html.append(".dashboard-title{font-size:1.05em;opacity:0.90;letter-spacing:0.8px;}");
        html.append(".dashboard-value{font-size:2.3em;font-weight:bold;margin-top:7px;}");
        html.append("h1{color:#3a3985;}h2{color:#6641b0;}");
        html.append(".section-title{margin-top:30px;margin-bottom:10px;color:#2d3a52;border-left:5px solid #6a82fb;padding-left:10px;}");
        html.append("table{width:100%;border-collapse:collapse;margin-top:6px;margin-bottom:24px;}");
        html.append("th,td{border:1px solid #e2e2e2;padding:10px 16px;text-align:left;}");
        html.append("th{background:#f0f0fd;color:#3a3985;font-weight:bold;}");
        html.append("td.status-col{font-weight:bold;}");
        html.append(".ok{color:#28a745;} .excellent{color:#1cb0f6;} .acceptable{color:#ffc107;} .warning{color:#ff9800;} .danger{color:#dc3545;} .critical{color:#c41818;}");
        html.append(".overall{background:#eafaf1;padding:18px 24px;border-radius:8px;margin-top:28px;font-size:1.1em;}");
        html.append(".rec{margin-top:12px;padding:12px 20px;background:#fff3cd;border-left:5px solid #ff9800;border-radius:6px;color:#856404;}");
        html.append(".summary-list{margin-top:10px;margin-bottom:0;}");
        html.append("</style></head><body>");
        html.append("<div class='container'>");

        html.append("<h1>Performance Test Report</h1>");
        html.append("<h2>").append(testName).append("</h2>");
        html.append("<div><b>Generated:</b> ").append(LocalDateTime.now()).append("</div>");

        // Dashboard summary cards
        html.append("<div class='dashboard'>");
        html.append(dashboardCard("Total Requests", testResult.getTotalRequests() + "", "#6a82fb,#fc5c7d"));
        html.append(dashboardCard("Success Rate", String.format("%.2f%%", testResult.getSuccessRate()), "#43cea2,#185a9d"));
        html.append(dashboardCard("Avg Response Time", String.format("%.0f ms", testResult.getAverageResponseTime()), "#f7971e,#ffd200"));
        html.append(dashboardCard("Throughput", String.format("%.1f req/s", testResult.getThroughput()), "#fd746c,#ff9068"));
        html.append("</div>");

        // Test Configuration Table
        html.append("<h3 class='section-title'>Test Configuration</h3><table>");
        html.append("<tr><th>Parameter</th><th>Value</th></tr>");
        html.append("<tr><td>Target URL</td><td>").append(testConfig.getTargetUrl()).append("</td></tr>");
        html.append("<tr><td>Number of Threads</td><td>").append(testConfig.getNumberOfThreads()).append("</td></tr>");
        html.append("<tr><td>Requests per Thread</td><td>").append(testConfig.getRequestsPerThread()).append("</td></tr>");
        html.append("<tr><td>HTTP Method</td><td>").append(testConfig.getHttpMethod()).append("</td></tr>");
        html.append("<tr><td>Test Duration</td><td>").append(testConfig.getTestDurationSec()).append(" seconds</td></tr>");
        html.append("<tr><td>Connection Timeout</td><td>").append(testConfig.getConnectionTimeoutMs()).append(" ms</td></tr>");
        html.append("<tr><td>Response Timeout</td><td>").append(testConfig.getResponseTimeoutMs()).append(" ms</td></tr>");
        html.append("</table>");

        // Test Results Table
        html.append("<h3 class='section-title'>Test Results</h3><table>");
        html.append("<tr><th>Metric</th><th>Value</th><th>Status</th></tr>");
        html.append("<tr><td>Start Time</td><td>").append(testResult.getStartTime()).append("</td><td class='status-col'><span class='ok'>OK</span></td></tr>");
        html.append("<tr><td>End Time</td><td>").append(testResult.getEndTime()).append("</td><td class='status-col'><span class='ok'>OK</span></td></tr>");
        html.append("<tr><td>Duration</td><td>").append(String.format("%.2f seconds", testResult.getDuration())).append("</td><td class='status-col'><span class='ok'>OK</span></td></tr>");
        html.append("<tr><td>Total Requests</td><td>").append(testResult.getTotalRequests()).append("</td><td class='status-col'><span class='ok'>OK</span></td></tr>");
        html.append("<tr><td>Successful Requests</td><td>").append(testResult.getSuccessfulRequests()).append("</td><td class='status-col'><span class='ok'>OK</span></td></tr>");
        html.append("<tr><td>Failed Requests</td><td>").append(testResult.getFailedRequests()).append("</td><td class='status-col'>")
                .append(statusText(testResult.getFailedRequests(), "Failed Requests")).append("</td></tr>");
        html.append("<tr><td>Success Rate</td><td>").append(String.format("%.2f%%", testResult.getSuccessRate())).append("</td><td class='status-col'>")
                .append(statusText(testResult.getSuccessRate(), "Success Rate")).append("</td></tr>");
        html.append("<tr><td>Average Response Time</td><td>").append(String.format("%.2f ms", testResult.getAverageResponseTime())).append("</td><td class='status-col'>")
                .append(statusText(testResult.getAverageResponseTime(), "Average Response Time")).append("</td></tr>");
        html.append("<tr><td>Min Response Time</td><td>").append(String.format("%.0f ms", testResult.getMinResponseTime())).append("</td><td class='status-col'><span class='ok'>OK</span></td></tr>");
        html.append("<tr><td>Max Response Time</td><td>").append(String.format("%.0f ms", testResult.getMaxResponseTime())).append("</td><td class='status-col'>")
                .append(statusText(testResult.getMaxResponseTime(), "Max Response Time")).append("</td></tr>");
        html.append("<tr><td>Throughput</td><td>").append(String.format("%.2f req/s", testResult.getThroughput())).append("</td><td class='status-col'>")
                .append(statusText(testResult.getThroughput(), "Throughput")).append("</td></tr>");
        html.append("</table>");

        // Overall Assessment + Test Summary + Recommendations
        String level = overallLevel(testResult);
        html.append("<div class='overall'>");
        html.append("<b>Overall Assessment: ").append(level).append("</b><br>");
        html.append("Performance Level: <b>").append(level).append("</b>");
        html.append("<div class='summary-list'><b>Test Summary:</b><ul>");
        html.append("<li><b>Reliability:</b> ").append(reliabilityComment(testResult)).append("</li>");
        html.append("<li><b>Performance:</b> ").append(performanceComment(testResult)).append("</li>");
        html.append("<li><b>Scalability:</b> ").append(scalabilityComment(testResult)).append("</li>");
        html.append("</ul></div>");
        html.append("<div class='rec'><b>Recommendations:</b> ").append(recommendation(testResult)).append("</div>");
        html.append("</div>");

        // Footer
        html.append("<div style='margin-top:34px;font-size:0.95em;color:#888;'>");
        html.append("Author: ").append(author).append(" | Repository: automation-performance-testing-with-github-action | Generated: ").append(LocalDateTime.now()).append(" UTC");
        html.append("</div>");

        html.append("</div></body></html>");

        Files.write(Paths.get(fileName), html.toString().getBytes());
        return fileName;
    }

    // CSV, JSON, TXT phần này bạn tùy biến tương tự như trên
    private String generateCsvReport(TestResult testResult, TestConfig testConfig, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".csv";
        StringBuilder csv = new StringBuilder();
        csv.append("Section,Metric,Value,Status\n");
        // Test Configuration
        csv.append("Test Configuration,Target URL,").append(testConfig.getTargetUrl()).append(",\n");
        csv.append("Test Configuration,Number of Threads,").append(testConfig.getNumberOfThreads()).append(",\n");
        csv.append("Test Configuration,Requests per Thread,").append(testConfig.getRequestsPerThread()).append(",\n");
        csv.append("Test Configuration,HTTP Method,").append(testConfig.getHttpMethod()).append(",\n");
        csv.append("Test Configuration,Test Duration,").append(testConfig.getTestDurationSec()).append(" seconds,\n");
        csv.append("Test Configuration,Connection Timeout,").append(testConfig.getConnectionTimeoutMs()).append(" ms,\n");
        csv.append("Test Configuration,Response Timeout,").append(testConfig.getResponseTimeoutMs()).append(" ms,\n");
        // Test Results
        csv.append("Test Results,Start Time,").append(testResult.getStartTime()).append(",OK\n");
        csv.append("Test Results,End Time,").append(testResult.getEndTime()).append(",OK\n");
        csv.append("Test Results,Duration,").append(String.format("%.2f seconds", testResult.getDuration())).append(",OK\n");
        csv.append("Test Results,Total Requests,").append(testResult.getTotalRequests()).append(",OK\n");
        csv.append("Test Results,Successful Requests,").append(testResult.getSuccessfulRequests()).append(",OK\n");
        csv.append("Test Results,Failed Requests,").append(testResult.getFailedRequests()).append(",").append(plainStatus(testResult.getFailedRequests(), "Failed Requests")).append("\n");
        csv.append("Test Results,Success Rate,").append(String.format("%.2f%%", testResult.getSuccessRate())).append(",").append(plainStatus(testResult.getSuccessRate(), "Success Rate")).append("\n");
        csv.append("Test Results,Average Response Time,").append(String.format("%.2f ms", testResult.getAverageResponseTime())).append(",").append(plainStatus(testResult.getAverageResponseTime(), "Average Response Time")).append("\n");
        csv.append("Test Results,Min Response Time,").append(String.format("%.0f ms", testResult.getMinResponseTime())).append(",OK\n");
        csv.append("Test Results,Max Response Time,").append(String.format("%.0f ms", testResult.getMaxResponseTime())).append(",").append(plainStatus(testResult.getMaxResponseTime(), "Max Response Time")).append("\n");
        csv.append("Test Results,Throughput,").append(String.format("%.2f req/s", testResult.getThroughput())).append(",").append(plainStatus(testResult.getThroughput(), "Throughput")).append("\n");
        // Overall
        csv.append("Overall Assessment,Overall Level,").append(overallLevel(testResult)).append(",\n");
        csv.append("Overall Assessment,Reliability,").append(reliabilityComment(testResult)).append(",\n");
        csv.append("Overall Assessment,Performance,").append(performanceComment(testResult)).append(",\n");
        csv.append("Overall Assessment,Scalability,").append(scalabilityComment(testResult)).append(",\n");
        csv.append("Overall Assessment,Recommendations,").append(recommendation(testResult)).append(",\n");
        csv.append("Meta,Generated,").append(LocalDateTime.now()).append(",UTC\n");
        Files.write(Paths.get(fileName), csv.toString().getBytes());
        return fileName;
    }

    private String generateJsonReport(TestResult testResult, TestConfig testConfig, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".json";
        Map<String, Object> report = new HashMap<>();
        Map<String, Object> config = new HashMap<>();
        config.put("targetUrl", testConfig.getTargetUrl());
        config.put("numberOfThreads", testConfig.getNumberOfThreads());
        config.put("requestsPerThread", testConfig.getRequestsPerThread());
        config.put("httpMethod", testConfig.getHttpMethod());
        config.put("testDurationSec", testConfig.getTestDurationSec());
        config.put("connectionTimeoutMs", testConfig.getConnectionTimeoutMs());
        config.put("responseTimeoutMs", testConfig.getResponseTimeoutMs());
        report.put("testConfig", config);
        Map<String, Object> result = new HashMap<>();
        result.put("startTime", testResult.getStartTime().toString());
        result.put("endTime", testResult.getEndTime().toString());
        result.put("duration", testResult.getDuration());
        result.put("totalRequests", testResult.getTotalRequests());
        result.put("successfulRequests", testResult.getSuccessfulRequests());
        result.put("failedRequests", testResult.getFailedRequests());
        result.put("successRate", testResult.getSuccessRate());
        result.put("averageResponseTime", testResult.getAverageResponseTime());
        result.put("minResponseTime", testResult.getMinResponseTime());
        result.put("maxResponseTime", testResult.getMaxResponseTime());
        result.put("throughput", testResult.getThroughput());
        report.put("testResult", result);
        report.put("overallAssessment", overallLevel(testResult));
        report.put("reliability", reliabilityComment(testResult));
        report.put("performance", performanceComment(testResult));
        report.put("scalability", scalabilityComment(testResult));
        report.put("recommendations", recommendation(testResult));
        report.put("generated", LocalDateTime.now().toString());
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(report);
        Files.write(Paths.get(fileName), json.getBytes());
        return fileName;
    }

    private String generateTextReport(TestResult testResult, TestConfig testConfig, String testName, String author, String baseFileName) throws IOException {
        String fileName = "reports/" + baseFileName + ".txt";
        StringBuilder txt = new StringBuilder();
        txt.append("========================================\n");
        txt.append("Performance Test Report\n");
        txt.append("========================================\n\n");
        txt.append("Test Name: ").append(testName).append("\n");
        txt.append("Test Author: ").append(author).append("\n\n");
        txt.append("Test Configuration:\n");
        txt.append("  Target URL: ").append(testConfig.getTargetUrl()).append("\n");
        txt.append("  Number of Threads: ").append(testConfig.getNumberOfThreads()).append("\n");
        txt.append("  Requests per Thread: ").append(testConfig.getRequestsPerThread()).append("\n");
        txt.append("  HTTP Method: ").append(testConfig.getHttpMethod()).append("\n");
        txt.append("  Test Duration: ").append(testConfig.getTestDurationSec()).append(" seconds\n");
        txt.append("  Connection Timeout: ").append(testConfig.getConnectionTimeoutMs()).append(" ms\n");
        txt.append("  Response Timeout: ").append(testConfig.getResponseTimeoutMs()).append(" ms\n\n");
        txt.append("Test Results:\n");
        txt.append("  Start Time: ").append(testResult.getStartTime()).append("\n");
        txt.append("  End Time: ").append(testResult.getEndTime()).append("\n");
        txt.append("  Duration: ").append(String.format("%.2f seconds", testResult.getDuration())).append("\n");
        txt.append("  Total Requests: ").append(testResult.getTotalRequests()).append("\n");
        txt.append("  Successful Requests: ").append(testResult.getSuccessfulRequests()).append("\n");
        txt.append("  Failed Requests: ").append(testResult.getFailedRequests()).append(" (").append(plainStatus(testResult.getFailedRequests(), "Failed Requests")).append(")\n");
        txt.append("  Success Rate: ").append(String.format("%.2f%%", testResult.getSuccessRate())).append(" (").append(plainStatus(testResult.getSuccessRate(), "Success Rate")).append(")\n");
        txt.append("  Average Response Time: ").append(String.format("%.2f ms", testResult.getAverageResponseTime())).append(" (").append(plainStatus(testResult.getAverageResponseTime(), "Average Response Time")).append(")\n");
        txt.append("  Min Response Time: ").append(String.format("%.0f ms", testResult.getMinResponseTime())).append("\n");
        txt.append("  Max Response Time: ").append(String.format("%.0f ms", testResult.getMaxResponseTime())).append(" (").append(plainStatus(testResult.getMaxResponseTime(), "Max Response Time")).append(")\n");
        txt.append("  Throughput: ").append(String.format("%.2f req/s", testResult.getThroughput())).append(" (").append(plainStatus(testResult.getThroughput(), "Throughput")).append(")\n\n");
        txt.append("Overall Assessment: ").append(overallLevel(testResult)).append("\n");
        txt.append("Reliability: ").append(reliabilityComment(testResult)).append("\n");
        txt.append("Performance: ").append(performanceComment(testResult)).append("\n");
        txt.append("Scalability: ").append(scalabilityComment(testResult)).append("\n");
        txt.append("Recommendations: ").append(recommendation(testResult)).append("\n");
        txt.append("Generated by: ").append(author).append("\n");
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

        public String getHtmlFile() { return htmlFile; }
        public String getCsvFile() { return csvFile; }
        public String getJsonFile() { return jsonFile; }
        public String getTxtFile() { return txtFile; }
    }
}