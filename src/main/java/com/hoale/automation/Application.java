package com.hoale.automation;

import com.hoale.automation.server.SimpleServer;

/**
 * Automation Performance Testing Application
 * Using Java built-in HTTP Server - No external dependencies conflicts
 *
 * @author leanhhoa30012004
 * @version 1.0.0
 * @created 2025-07-23 19:24:30 UTC
 */
public class Application {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Automation Performance Testing Application");
        System.out.println("Simple Java Implementation - No Dependencies Conflicts");
        System.out.println("Developer: leanhhoa30012004");
        System.out.println("Version: 1.0.0");
        System.out.println("Created: 2025-07-23 19:24:30 UTC");
        System.out.println("Repository: automation-performance-testing-with-github-action");
        System.out.println("========================================");

        try {
            SimpleServer server = new SimpleServer();
            server.start();

            System.out.println("✅ Application started successfully!");
            System.out.println("🔗 Health check: http://localhost:8080/api/health");
            System.out.println("📊 Info: http://localhost:8080/api/info");
            System.out.println("👤 Users: http://localhost:8080/api/users/123");
            System.out.println("🛑 Press Ctrl+C to stop");

            // Keep running
            Thread.currentThread().join();

        } catch (Exception e) {
            System.err.println("❌ Failed to start: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}