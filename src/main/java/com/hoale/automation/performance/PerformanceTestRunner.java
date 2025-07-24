package com.hoale.automation.performance;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;
import com.hoale.automation.performance.model.TestConfig;
import com.hoale.automation.performance.report.PerformanceReportGenerator;

/**
 * Enhanced Performance Test Runner with Report Generation (Dashboard, Config, Suggestions, Charts-ready)
 *
 * @author leanhhoa30012004
 * @created 2025-07-24 10:50:00 UTC
 */
public class PerformanceTestRunner {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java PerformanceTestRunner <config-file>");
            System.exit(1);
        }

        try {
            System.out.println("Starting Enhanced Performance Test with Report Generation");
            System.out.println("Developer: leanhhoa30012004");
            System.out.println("Framework: Automation Performance Testing v1.0");
            System.out.println("Repository: automation-performance-testing-with-github-action");
            System.out.println("Config: " + args[0]);
            System.out.println("Created: 2025-07-24 10:50:00 UTC");
            System.out.println("=========================================================");

            // Load test configuration
            PerformanceConfig config = PerformanceConfig.load(args[0]);
            PerformanceTestEngine engine = new PerformanceTestEngine(config);
            TestResult result = engine.runTest();

            // Convert PerformanceConfig to TestConfig for reporting
            TestConfig testConfig = config.toTestConfig();

            System.out.println();
            System.out.println("Test Results Summary:");
            System.out.println("========================");
            System.out.println("Total requests: " + result.getTotalRequests());
            System.out.println("Successful requests: " + result.getSuccessfulRequests());
            System.out.println("Failed requests: " + result.getFailedRequests());
            System.out.println("Success rate: " + String.format("%.2f%%", result.getSuccessRate()));
            System.out.println("Average response time: " + String.format("%.2f ms", result.getAverageResponseTime()));
            System.out.println("Min response time: " + result.getMinResponseTime() + " ms");
            System.out.println("Max response time: " + result.getMaxResponseTime() + " ms");
            System.out.println("Throughput: " + String.format("%.2f req/sec", result.getThroughput()));

            // Generate Reports (HTML, CSV, JSON, TXT)
            System.out.println();
            System.out.println("Generating Performance Reports...");
            PerformanceReportGenerator reportGenerator = new PerformanceReportGenerator();
            String testName = config.getTestName() != null ? config.getTestName() : "Performance Test";
            String author = config.getTestAuthor() != null ? config.getTestAuthor() : "leanhhoa30012004";

            PerformanceReportGenerator.ReportFiles reports = reportGenerator.generateReports(result, testConfig, testName, author);

            if (reports != null) {
                System.out.println("Reports generated successfully!");
                System.out.println("HTML Report: " + reports.getHtmlFile());
                System.out.println("CSV Report: " + reports.getCsvFile());
                System.out.println("JSON Report: " + reports.getJsonFile());
                System.out.println("Text Report: " + reports.getTxtFile());
            } else {
                System.err.println("Report generation failed!");
            }

            // Final result output
            System.out.println();
            System.out.println("Final Test Result:");
            System.out.println("====================");
            if (result.getSuccessRate() >= 95.0) {
                System.out.println("PASSED - Performance test completed successfully.");
                System.out.println("All performance criteria met.");
                System.exit(0);
            } else {
                System.out.println("FAILED - Performance test did not meet success criteria.");
                System.out.println("Success rate below 95%: " + String.format("%.2f%%", result.getSuccessRate()));
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println("Test execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}