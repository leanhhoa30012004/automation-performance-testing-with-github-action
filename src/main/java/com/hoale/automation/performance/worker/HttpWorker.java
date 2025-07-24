package com.hoale.automation.performance.worker;

import com.hoale.automation.performance.config.PerformanceConfig;
import com.hoale.automation.performance.model.TestResult;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.util.concurrent.Callable;

/**
 * HTTP Worker for Performance Testing
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class HttpWorker implements Callable<Void> {

    private final PerformanceConfig config;
    private final TestResult testResult;

    public HttpWorker(PerformanceConfig config, TestResult testResult) {
        this.config = config;
        this.testResult = testResult;
    }

    @Override
    public Void call() throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            for (int i = 0; i < config.getRequestsPerThread(); i++) {
                executeRequest(httpClient);
                Thread.sleep(10); // Small delay
            }
        }
        return null;
    }

    private void executeRequest(CloseableHttpClient httpClient) {
        long startTime = System.currentTimeMillis();
        boolean success = false;

        try {
            HttpGet request = new HttpGet(config.getTargetUrl());
            request.setHeader("User-Agent", "Automation-Performance-Test/1.0");

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int statusCode = response.getCode();
                success = (statusCode >= 200 && statusCode < 300);
            }
        } catch (Exception e) {
            success = false;
        } finally {
            long responseTime = System.currentTimeMillis() - startTime;
            testResult.recordRequest(success, responseTime);
        }
    }
}