package com.hoale.automation.performance;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;

/**
 * Performance Test Runner
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class PerformanceTestRunner {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage: java PerformanceTestRunner <config-file>");
            System.exit(1);
        }

        try {
            System.out.println("🚀 Starting Performance Test");
            System.out.println("Developer: leanhhoa30012004");
            System.out.println("Config: " + args[0]);

            PerformanceConfig config = PerformanceConfig.load(args[0]);
            PerformanceTestEngine engine = new PerformanceTestEngine(config);
            TestResult result = engine.runTest();

            System.out.println("📊 Test Results:");
            System.out.println("Total requests: " + result.getTotalRequests());
            System.out.println("Success rate: " + String.format("%.2f%%", result.getSuccessRate()));
            System.out.println("Average response time: " + String.format("%.2fms", result.getAverageResponseTime()));

            if (result.getSuccessRate() >= 95.0) {
                System.out.println("✅ Performance test PASSED");
                System.exit(0);
            } else {
                System.out.println("❌ Performance test FAILED");
                System.exit(1);
            }

        } catch (Exception e) {
            System.err.println("❌ Test execution failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}