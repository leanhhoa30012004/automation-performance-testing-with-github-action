package com.hoale.automation;

import com.hoale.automation.performance.PerformanceTestRunner;

/**
 * Main class for standalone performance test execution
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 18:32:45 UTC
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hoale Automation Performance Test Framework");
        System.out.println("Developer: leanhhoa30012004");
        System.out.println("Created: 2025-07-23 18:32:45 UTC");
        System.out.println("============================================");

        // Delegate to PerformanceTestRunner
        PerformanceTestRunner.main(args);
    }
}