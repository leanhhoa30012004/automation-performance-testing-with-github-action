package com.hoale.automation.performance.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Enhanced Test Result Model with additional metrics
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:52:31 UTC
 */
public class TestResult {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxResponseTime = new AtomicLong(0);

    public void recordRequest(boolean success, long responseTime) {
        totalRequests.incrementAndGet();
        if (success) {
            successfulRequests.incrementAndGet();
        }
        totalResponseTime.addAndGet(responseTime);

        // Update min/max response times
        updateMin(responseTime);
        updateMax(responseTime);
    }

    private void updateMin(long responseTime) {
        long currentMin = minResponseTime.get();
        while (responseTime < currentMin && !minResponseTime.compareAndSet(currentMin, responseTime)) {
            currentMin = minResponseTime.get();
        }
    }

    private void updateMax(long responseTime) {
        long currentMax = maxResponseTime.get();
        while (responseTime > currentMax && !maxResponseTime.compareAndSet(currentMax, responseTime)) {
            currentMax = maxResponseTime.get();
        }
    }

    public double getSuccessRate() {
        int total = totalRequests.get();
        return total > 0 ? (successfulRequests.get() * 100.0) / total : 0.0;
    }

    public double getAverageResponseTime() {
        int total = totalRequests.get();
        return total > 0 ? (double) totalResponseTime.get() / total : 0.0;
    }

    public int getFailedRequests() {
        return totalRequests.get() - successfulRequests.get();
    }

    public double getThroughput() {
        if (startTime == null || endTime == null) {
            return 0.0;
        }
        Duration duration = Duration.between(startTime, endTime);
        double seconds = duration.toMillis() / 1000.0;
        return seconds > 0 ? totalRequests.get() / seconds : 0.0;
    }

    public long getMinResponseTime() {
        long min = minResponseTime.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }

    public long getMaxResponseTime() {
        return maxResponseTime.get();
    }

    // Getters and Setters
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public int getTotalRequests() { return totalRequests.get(); }
    public int getSuccessfulRequests() { return successfulRequests.get(); }
}