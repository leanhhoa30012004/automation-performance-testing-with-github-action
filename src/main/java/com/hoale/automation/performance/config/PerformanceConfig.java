package com.hoale.automation.performance.config;

import com.hoale.automation.performance.model.TestConfig;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Enhanced Performance Test Configuration with Test Metadata
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:52:31 UTC
 */
public class PerformanceConfig {

    private String targetUrl;
    private String httpMethod = "GET";
    private int threadCount = 10;
    private int requestsPerThread = 50;
    private int testDuration = 300;
    private int connectionTimeout = 5000;
    private int responseTimeout = 10000;
    private int rampUpTime = 0;

    // Test metadata
    private String testName;
    private String testDescription;
    private String testAuthor;
    private String framework;
    private String createdDate;

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
        config.rampUpTime = Integer.parseInt(props.getProperty("rampUpTime", "0"));

        // Test metadata
        config.testName = props.getProperty("testName", "Performance Test");
        config.testDescription = props.getProperty("testDescription", "");
        config.testAuthor = props.getProperty("testAuthor", "leanhhoa30012004");
        config.framework = props.getProperty("framework", "Automation Performance Testing");
        config.createdDate = props.getProperty("createdDate", "2025-07-23 19:52:31 UTC");

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
    public int getRampUpTime() { return rampUpTime; }
    public String getTestName() { return testName; }
    public String getTestDescription() { return testDescription; }
    public String getTestAuthor() { return testAuthor; }
    public String getFramework() { return framework; }
    public String getCreatedDate() { return createdDate; }

    // Chuyển đổi sang TestConfig
    public TestConfig toTestConfig() {
        return new TestConfig(
                this.targetUrl,
                this.threadCount,
                this.requestsPerThread,
                this.httpMethod,
                this.testDuration,
                this.connectionTimeout,
                this.responseTimeout
        );
    }
}