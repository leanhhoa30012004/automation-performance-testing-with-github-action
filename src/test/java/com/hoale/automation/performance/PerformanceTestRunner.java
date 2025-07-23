package com.hoale.automation.performance;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;
import com.hoale.automation.performance.report.PerformanceReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performance Test Runner
 * Main entry point for running performance tests
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 18:32:45 UTC
 */
public class PerformanceTestRunner {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestRunner.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            logger.error("Usage: java PerformanceTestRunner <config-file>");
            logger.error("Example: java PerformanceTestRunner performance-config/api-performance.properties");
            System.exit(1);
        }

        String configFile = args[0];
        logger.info("========================================");
        logger.info("Starting Hoale Automation Performance Test");
        logger.info("Developer: leanhhoa30012004");
        logger.info("Created: 2025-07-23 18:32:45 UTC");
        logger.info("Framework: Hoale Automation Performance Test Engine v1.0");
        logger.info("Config file: {}", configFile);
        logger.info("========================================");

        try {
            // Load configuration
            PerformanceConfig config = PerformanceConfig.load(configFile);

            // Run performance test
            PerformanceTestEngine engine = new PerformanceTestEngine(config);
            TestResult result = engine.runTest();

            // Generate reports
            logger.info("Generating performance reports...");
            PerformanceReportGenerator reportGenerator = new PerformanceReportGenerator(config, result);
            reportGenerator.generateAllReports();

            // Exit with appropriate code based on results
            if (result.getSuccessRate() < 95.0) {
                logger.error("Hoale Automation Performance test FAILED: Success rate {} is below threshold", result.getSuccessRate());
                logger.error("Please check the generated reports for detailed analysis");
                System.exit(1);
            } else {
                logger.info("Hoale Automation Performance test PASSED successfully");
                logger.info("Success rate: {}%, Average response time: {}ms",
                        String.format("%.2f", result.getSuccessRate()),
                        String.format("%.2f", result.getAverageResponseTime()));
                System.exit(0);
            }

        } catch (Exception e) {
            logger.error("Hoale Automation Performance test execution failed", e);
            System.exit(1);
        }
    }
}