package com.hoale.automation.performance.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Performance Test Configuration
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 18:17:05 UTC
 */
public class PerformanceConfig {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceConfig.class);

    private String targetUrl;
    private String httpMethod;
    private int threadCount;
    private int requestsPerThread;
    private int testDuration;
    private int connectionTimeout;
    private int responseTimeout;
    private int rampUpTime;
    private String testName;
    private String testDescription;
    private String testAuthor;

    // Constructors
    public PerformanceConfig() {
        // Default values
        this.httpMethod = "GET";
        this.threadCount = 10;
        this.requestsPerThread = 100;
        this.testDuration = 300;
        this.connectionTimeout = 5000;
        this.responseTimeout = 10000;
        this.rampUpTime = 0;
        this.testAuthor = "leanhhoa30012004";
    }

    public static PerformanceConfig load(String configFile) throws IOException {
        logger.info("Loading performance configuration from: {}", configFile);

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(configFile)) {
            props.load(fis);
        }

        PerformanceConfig config = new PerformanceConfig();

        config.targetUrl = props.getProperty("targetUrl");
        config.httpMethod = props.getProperty("httpMethod", "GET");
        config.threadCount = Integer.parseInt(props.getProperty("threadCount", "10"));
        config.requestsPerThread = Integer.parseInt(props.getProperty("requestsPerThread", "100"));
        config.testDuration = Integer.parseInt(props.getProperty("testDuration", "300"));
        config.connectionTimeout = Integer.parseInt(props.getProperty("connectionTimeout", "5000"));
        config.responseTimeout = Integer.parseInt(props.getProperty("responseTimeout", "10000"));
        config.rampUpTime = Integer.parseInt(props.getProperty("rampUpTime", "0"));
        config.testName = props.getProperty("testName", "Hoale Performance Test");
        config.testDescription = props.getProperty("testDescription", "Performance test by leanhhoa30012004");
        config.testAuthor = props.getProperty("testAuthor", "leanhhoa30012004");

        logger.info("Configuration loaded successfully");
        logger.info("Target URL: {}", config.targetUrl);
        logger.info("Threads: {}, Requests per thread: {}", config.threadCount, config.requestsPerThread);

        return config;
    }

    // Getters and Setters
    public String getTargetUrl() { return targetUrl; }
    public void setTargetUrl(String targetUrl) { this.targetUrl = targetUrl; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public int getThreadCount() { return threadCount; }
    public void setThreadCount(int threadCount) { this.threadCount = threadCount; }

    public int getRequestsPerThread() { return requestsPerThread; }
    public void setRequestsPerThread(int requestsPerThread) { this.requestsPerThread = requestsPerThread; }

    public int getTestDuration() { return testDuration; }
    public void setTestDuration(int testDuration) { this.testDuration = testDuration; }

    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) { this.connectionTimeout = connectionTimeout; }

    public int getResponseTimeout() { return responseTimeout; }
    public void setResponseTimeout(int responseTimeout) { this.responseTimeout = responseTimeout; }

    public int getRampUpTime() { return rampUpTime; }
    public void setRampUpTime(int rampUpTime) { this.rampUpTime = rampUpTime; }

    public String getTestName() { return testName; }
    public void setTestName(String testName) { this.testName = testName; }

    public String getTestDescription() { return testDescription; }
    public void setTestDescription(String testDescription) { this.testDescription = testDescription; }

    public String getTestAuthor() { return testAuthor; }
    public void setTestAuthor(String testAuthor) { this.testAuthor = testAuthor; }
}