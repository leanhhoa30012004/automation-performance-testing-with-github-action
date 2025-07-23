package com.hoale.automation.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User Service for Hoale Automation Application
 * Business logic layer
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 18:17:05 UTC
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public String getApplicationInfo() {
        logger.info("Getting automation application info");
        return "Hoale Automation Performance Test Application - Developed by leanhhoa30012004 on 2025-07-23 18:17:05 UTC";
    }

    public String getAutomationFrameworkInfo() {
        return "Hoale Automation Framework v1.0 - Performance Testing & CI/CD Integration";
    }

    public boolean validateAutomationTest(String testType) {
        logger.debug("Validating automation test type: {}", testType);
        if (testType == null || testType.trim().isEmpty()) {
            return false;
        }

        return switch (testType.toLowerCase()) {
            case "unit", "integration", "performance", "load", "stress" -> true;
            default -> false;
        };
    }

    public String getDeveloperInfo() {
        return "leanhhoa30012004 - Hoale Automation Performance Test Engineer";
    }

    public String getSystemInfo() {
        return String.format("System: %s %s, Java: %s, Processors: %d",
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("java.version"),
                Runtime.getRuntime().availableProcessors()
        );
    }
}