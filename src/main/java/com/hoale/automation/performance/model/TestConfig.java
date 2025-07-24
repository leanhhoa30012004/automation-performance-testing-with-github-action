package com.hoale.automation.performance.model;

public class TestConfig {
    private String targetUrl;
    private int numberOfThreads;
    private int requestsPerThread;
    private String httpMethod;
    private int testDurationSec;
    private int connectionTimeoutMs;
    private int responseTimeoutMs;

    public TestConfig(String targetUrl, int numberOfThreads, int requestsPerThread, String httpMethod,
                      int testDurationSec, int connectionTimeoutMs, int responseTimeoutMs) {
        this.targetUrl = targetUrl;
        this.numberOfThreads = numberOfThreads;
        this.requestsPerThread = requestsPerThread;
        this.httpMethod = httpMethod;
        this.testDurationSec = testDurationSec;
        this.connectionTimeoutMs = connectionTimeoutMs;
        this.responseTimeoutMs = responseTimeoutMs;
    }

    public String getTargetUrl() { return targetUrl; }
    public int getNumberOfThreads() { return numberOfThreads; }
    public int getRequestsPerThread() { return requestsPerThread; }
    public String getHttpMethod() { return httpMethod; }
    public int getTestDurationSec() { return testDurationSec; }
    public int getConnectionTimeoutMs() { return connectionTimeoutMs; }
    public int getResponseTimeoutMs() { return responseTimeoutMs; }
}