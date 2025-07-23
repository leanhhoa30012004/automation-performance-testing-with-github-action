package com.hoale.automation.performance;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;
import com.hoale.automation.performance.worker.HttpWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Performance Test Engine
 * Core engine for executing performance tests
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 18:43:23 UTC
 */
public class PerformanceTestEngine {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestEngine.class);

    private final PerformanceConfig config;
    private final TestResult testResult;

    public PerformanceTestEngine(PerformanceConfig config) {
        this.config = config;
        this.testResult = new TestResult();
    }

    public TestResult runTest() {
        logger.info("Starting Hoale Automation Performance Test...");
        logger.info("Developer: leanhhoa30012004");
        logger.info("Test Date: 2025-07-23 18:43:23 UTC");
        logger.info("Target URL: {}", config.getTargetUrl());
        logger.info("Number of threads: {}", config.getThreadCount());
        logger.info("Requests per thread: {}", config.getRequestsPerThread());
        logger.info("HTTP Method: {}", config.getHttpMethod());
        logger.info("Test duration: {} seconds", config.getTestDuration());

        testResult.setStartTime(LocalDateTime.now());

        ExecutorService executor = Executors.newFixedThreadPool(config.getThreadCount());
        List<Future<Void>> futures = new ArrayList<>();

        try {
            // Create and submit worker threads
            for (int i = 0; i < config.getThreadCount(); i++) {
                HttpWorker worker = new HttpWorker(config, testResult);
                futures.add(executor.submit(worker));

                // Ramp-up delay
                if (config.getRampUpTime() > 0) {
                    Thread.sleep(config.getRampUpTime() * 1000L / config.getThreadCount());
                }
            }

            // Wait for all threads to complete or timeout
            executor.shutdown();
            boolean finished = executor.awaitTermination(config.getTestDuration(), TimeUnit.SECONDS);

            if (!finished) {
                logger.warn("Test timeout after {} seconds, forcing shutdown...", config.getTestDuration());
                executor.shutdownNow();
            }

        } catch (InterruptedException e) {
            logger.error("Test interrupted", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        testResult.setEndTime(LocalDateTime.now());

        printResults();

        return testResult;
    }

    private void printResults() {
        String separator = "=".repeat(80);

        logger.info("\n{}", separator);
        logger.info("HOALE AUTOMATION PERFORMANCE TEST RESULTS");
        logger.info("Developer: leanhhoa30012004 | Created: 2025-07-23 18:43:23 UTC");
        logger.info("{}", separator);
        logger.info("Start time: {}", testResult.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("End time: {}", testResult.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        logger.info("Total requests: {}", testResult.getTotalRequests());
        logger.info("Successful requests: {}", testResult.getSuccessfulRequests());
        logger.info("Failed requests: {}", testResult.getFailedRequests());
        logger.info("Success rate: {} %", String.format("%.2f", testResult.getSuccessRate()));
        logger.info("Average response time: {} ms", String.format("%.2f", testResult.getAverageResponseTime()));
        logger.info("Min response time: {} ms", testResult.getMinResponseTime());
        logger.info("Max response time: {} ms", testResult.getMaxResponseTime());
        logger.info("Throughput: {} requests/second", String.format("%.2f", testResult.getThroughput()));
        printPerformanceAnalysis();

        logger.info("{}", separator);
    }

    private void printPerformanceAnalysis() {
        logger.info("\nHOALE AUTOMATION PERFORMANCE ANALYSIS:");

        double successRate = testResult.getSuccessRate();
        double avgResponseTime = testResult.getAverageResponseTime();
        double throughput = testResult.getThroughput();

        if (successRate >= 99.0) {
            logger.info("✅ Success Rate: EXCELLENT ({}%)", String.format("%.2f", successRate));
        } else if (successRate >= 95.0) {
            logger.info("🟡 Success Rate: GOOD ({}%)", String.format("%.2f", successRate));
        } else {
            logger.info("❌ Success Rate: POOR ({}%) - Needs investigation", String.format("%.2f", successRate));
        }

        if (avgResponseTime <= 200) {
            logger.info("⚡ Response Time: EXCELLENT ({} ms)", String.format("%.2f", avgResponseTime));
        } else if (avgResponseTime <= 500) {
            logger.info("✅ Response Time: ACCEPTABLE ({} ms)", String.format("%.2f", avgResponseTime));
        } else if (avgResponseTime <= 1000) {
            logger.info("🟡 Response Time: SLOW ({} ms)", String.format("%.2f", avgResponseTime));
        } else {
            logger.info("❌ Response Time: VERY SLOW ({} ms) - Performance issue", String.format("%.2f", avgResponseTime));
        }

        if (throughput >= 100) {
            logger.info("🚀 Throughput: HIGH ({} req/s)", String.format("%.2f", throughput));
        } else if (throughput >= 50) {
            logger.info("✅ Throughput: MEDIUM ({} req/s)", String.format("%.2f", throughput));
        } else {
            logger.info("🟡 Throughput: LOW ({} req/s)", String.format("%.2f", throughput));
        }

        logger.info("\n📊 OVERALL ASSESSMENT:");
        if (successRate >= 99.0 && avgResponseTime <= 500 && throughput >= 50) {
            logger.info("🎉 Hoale Automation System performance is EXCELLENT!");
        } else if (successRate >= 95.0 && avgResponseTime <= 1000) {
            logger.info("👍 Hoale Automation System performance is ACCEPTABLE");
        } else {
            logger.info("⚠️ Hoale Automation System performance needs IMPROVEMENT");
        }

        logger.info("🔧 Framework: Hoale Automation Performance Test Engine v1.0");
        logger.info("👨‍💻 Test completed by: leanhhoa30012004 on 2025-07-23 18:43:23 UTC");
    }
}