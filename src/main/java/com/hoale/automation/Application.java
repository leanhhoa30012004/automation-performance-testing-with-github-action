package com.hoale.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Automation Performance Testing Application
 *
 * @author leanhhoa30012004
 * @version 1.0.0
 * @created 2025-07-23 19:11:12 UTC
 */
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Starting Automation Performance Testing Application...");
        System.out.println("Developer: leanhhoa30012004");
        System.out.println("Version: 1.0.0");
        System.out.println("Created: 2025-07-23 19:11:12 UTC");
        System.out.println("Purpose: Performance Testing & CI/CD Integration");
        System.out.println("========================================");

        SpringApplication.run(Application.class, args);

        System.out.println("Automation Performance Testing Application started successfully!");
        System.out.println("Health check: http://localhost:8080/api/health");
        System.out.println("Automation info: http://localhost:8080/api/automation/info");
    }
}