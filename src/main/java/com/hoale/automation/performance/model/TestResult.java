package com.hoale.automation.performance.model;

import java.time.LocalDateTime;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TestResult {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private final AtomicInteger totalRequests = new AtomicInteger(0);
    private final AtomicInteger successfulRequests = new AtomicInteger(0);
    private final AtomicLong totalResponseTime = new AtomicLong(0);
    private final AtomicLong minResponseTime = new AtomicLong(Long.MAX_VALUE);
    private final AtomicLong maxResponseTime = new AtomicLong(0);

    public void recordRequest(boolean success, long responseTimeMillis) {
        totalRequests.incrementAndGet();
        if (success) {
            successfulRequests.incrementAndGet();
        }
        totalResponseTime.addAndGet(responseTimeMillis);
        updateMin(responseTimeMillis);
        updateMax(responseTimeMillis);
    }

    private void updateMin(long responseTime) {
        long prev, next;
        do {
            prev = minResponseTime.get();
            next = Math.min(prev, responseTime);
        } while (!minResponseTime.compareAndSet(prev, next));
    }

    private void updateMax(long responseTime) {
        long prev, next;
        do {
            prev = maxResponseTime.get();
            next = Math.max(prev, responseTime);
        } while (!maxResponseTime.compareAndSet(prev, next));
    }

    public int getTotalRequests() { return totalRequests.get(); }
    public int getSuccessfulRequests() { return successfulRequests.get(); }
    public int getFailedRequests() { return getTotalRequests() - getSuccessfulRequests(); }
    public double getSuccessRate() {
        int total = getTotalRequests();
        return total > 0 ? (getSuccessfulRequests() * 100.0) / total : 0.0;
    }
    public double getAverageResponseTime() {
        int total = getTotalRequests();
        return total > 0 ? (double) totalResponseTime.get() / total : 0.0;
    }
    public double getMinResponseTime() {
        long min = minResponseTime.get();
        return min == Long.MAX_VALUE ? 0 : min;
    }
    public double getMaxResponseTime() {
        return maxResponseTime.get();
    }
    public double getDuration() {
        if (startTime == null || endTime == null) return 0.0;
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMillis() / 1000.0;
    }
    public double getThroughput() {
        double durationSeconds = getDuration();
        return durationSeconds > 0 ? getTotalRequests() / durationSeconds : 0.0;
    }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}