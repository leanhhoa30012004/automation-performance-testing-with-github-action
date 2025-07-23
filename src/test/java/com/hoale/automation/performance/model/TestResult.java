package com.hoale.automation.performance.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test Result Model
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class TestResult {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);

    public void recordRequest(boolean success, long responseTime) {
        totalRequests.incrementAndGet();
        if (success) {
            successfulRequests.incrementAndGet();
        }
        totalResponseTime.addAndGet(responseTime);
    }

    public double getSuccessRate() {
        int total = totalRequests.get();
        return total > 0 ? (successfulRequests.get() * 100.0) / total : 0.0;
    }

    public double getAverageResponseTime() {
        int total = totalRequests.get();
        return total > 0 ? (double) totalResponseTime.get() / total : 0.0;
    }

    // Getters and Setters
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public int getTotalRequests() { return totalRequests.get(); }
    public int getSuccessfulRequests() { return successfulRequests.get(); }
}