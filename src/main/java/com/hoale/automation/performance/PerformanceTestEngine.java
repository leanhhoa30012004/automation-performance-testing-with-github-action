package com.hoale.automation.performance;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;
import com.hoale.automation.performance.worker.HttpWorker;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Performance Test Engine
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class PerformanceTestEngine {

    private final PerformanceConfig config;
    private final TestResult testResult;

    public PerformanceTestEngine(PerformanceConfig config) {
        this.config = config;
        this.testResult = new TestResult();
    }

    public TestResult runTest() {
        System.out.println("🎯 Running performance test...");
        System.out.println("Target: " + config.getTargetUrl());
        System.out.println("Threads: " + config.getThreadCount());
        System.out.println("Requests per thread: " + config.getRequestsPerThread());

        testResult.setStartTime(LocalDateTime.now());

        ExecutorService executor = Executors.newFixedThreadPool(config.getThreadCount());
        List<Future<Void>> futures = new ArrayList<>();

        try {
            // Submit worker threads
            for (int i = 0; i < config.getThreadCount(); i++) {
                HttpWorker worker = new HttpWorker(config, testResult);
                futures.add(executor.submit(worker));
            }

            // Wait for completion
            executor.shutdown();
            executor.awaitTermination(config.getTestDuration(), TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            System.err.println("Test interrupted");
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        testResult.setEndTime(LocalDateTime.now());
        return testResult;
    }
}