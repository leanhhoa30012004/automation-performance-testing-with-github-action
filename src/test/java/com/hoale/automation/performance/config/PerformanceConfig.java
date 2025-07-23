package com.hoale.automation.performance.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Performance Test Configuration
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class PerformanceConfig {

    private String targetUrl;
    private String httpMethod = "GET";
    private int threadCount = 10;
    private int requestsPerThread = 50;
    private int testDuration = 300;
    private int connectionTimeout = 5000;
    private int responseTimeout = 10000;

    public static PerformanceConfig load(String configFile) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        }

        PerformanceConfig config = new PerformanceConfig();
        config.targetUrl = props.getProperty("targetUrl");
        config.httpMethod = props.getProperty("httpMethod", "GET");
        config.threadCount = Integer.parseInt(props.getProperty("threadCount", "10"));
        config.requestsPerThread = Integer.parseInt(props.getProperty("requestsPerThread", "50"));
        config.testDuration = Integer.parseInt(props.getProperty("testDuration", "300"));
        config.connectionTimeout = Integer.parseInt(props.getProperty("connectionTimeout", "5000"));
        config.responseTimeout = Integer.parseInt(props.getProperty("responseTimeout", "10000"));

        return config;
    }

    // Getters
    public String getTargetUrl() { return targetUrl; }
    public String getHttpMethod() { return httpMethod; }
    public int getThreadCount() { return threadCount; }
    public int getRequestsPerThread() { return requestsPerThread; }
    public int getTestDuration() { return testDuration; }
    public int getConnectionTimeout() { return connectionTimeout; }
    public int getResponseTimeout() { return responseTimeout; }
}