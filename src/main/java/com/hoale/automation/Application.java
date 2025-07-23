package com.hoale.automation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hoale Automation Performance Testing Application
 *
 * @author leanhhoa30012004
 * @version 1.0.0
 * @created 2025-07-23 18:34:45 UTC
 */
@SpringBootApplication
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("Starting Hoale Automation Application...");
        logger.info("Developer: leanhhoa30012004");
        logger.info("Version: 1.0.0");
        logger.info("Created: 2025-07-23 18:34:45 UTC");
        logger.info("Purpose: Performance Testing & CI/CD Integration");
        logger.info("========================================");

        SpringApplication.run(Application.class, args);

        logger.info("Hoale Automation Application started successfully!");
        logger.info("Health check: http://localhost:8080/api/health");
        logger.info("Automation info: http://localhost:8080/api/automation/info");
    }
}