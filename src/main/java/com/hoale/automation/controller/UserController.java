package com.hoale.automation.controller;

import com.hoale.automation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User Controller for Automation Performance Testing Application
 * Provides REST endpoints for performance testing
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:14:59 UTC
 */
@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        System.out.println("📋 Health check endpoint called");

        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        response.put("server", "Automation Performance Testing Server v1.0");
        response.put("developer", "leanhhoa30012004");
        response.put("application", "automation-performance-testing-with-github-action");
        response.put("created", "2025-07-23 19:14:59 UTC");
        response.put("version", "1.0.0");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, Object>> getUser(@PathVariable String id) {
        System.out.println("📋 Get user endpoint called for ID: " + id);

        // Simulate realistic processing time for performance testing
        try {
            Thread.sleep(50 + (long)(Math.random() * 100)); // 50-150ms random delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "AutoUser " + id);
        user.put("email", "autouser" + id + "@automation-performance-testing.com");
        user.put("timestamp", System.currentTimeMillis());
        user.put("server", "automation-performance-testing-api");
        user.put("department", "automation-testing");
        user.put("created_by", "leanhhoa30012004");
        user.put("version", "1.0.0");
        user.put("framework", "automation-performance-testing");

        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<Map<String, String>> createUser(@RequestBody Map<String, String> userData) {
        System.out.println("📋 Create user endpoint called with data: " + userData);

        // Simulate processing time for realistic load testing
        try {
            Thread.sleep(100 + (long)(Math.random() * 200)); // 100-300ms random delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, String> response = new HashMap<>();
        response.put("id", String.valueOf(System.currentTimeMillis()));
        response.put("message", "User created successfully in automation system");
        response.put("status", "created");
        response.put("server", "automation-performance-testing-api");
        response.put("system", "automation-framework");
        response.put("developer", "leanhhoa30012004");
        response.put("timestamp", java.time.LocalDateTime.now().toString());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/automation/info")
    public ResponseEntity<Map<String, Object>> getAutomationInfo() {
        System.out.println("📋 Automation info endpoint called");

        Map<String, Object> info = new HashMap<>();
        info.put("application", "Automation Performance Testing with GitHub Action");
        info.put("version", "1.0.0");
        info.put("developer", "leanhhoa30012004");
        info.put("created_date", "2025-07-23 19:14:59 UTC");
        info.put("timestamp", java.time.LocalDateTime.now().toString());
        info.put("jvmInfo", System.getProperty("java.version"));
        info.put("jvmVendor", System.getProperty("java.vendor"));
        info.put("osName", System.getProperty("os.name"));
        info.put("osVersion", System.getProperty("os.version"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemory", Runtime.getRuntime().maxMemory());
        info.put("freeMemory", Runtime.getRuntime().freeMemory());
        info.put("totalMemory", Runtime.getRuntime().totalMemory());
        info.put("framework", "automation-performance-testing");
        info.put("purpose", "CI/CD Performance Testing Integration");
        info.put("endpoints", java.util.List.of(
                "/api/health",
                "/api/users/{id}",
                "/api/users (POST)",
                "/api/automation/info",
                "/api/automation/test/{testType}",
                "/api/automation/stress"
        ));

        return ResponseEntity.ok(info);
    }

    @GetMapping("/automation/test/{testType}")
    public ResponseEntity<Map<String, Object>> runAutomationTest(@PathVariable String testType) {
        System.out.println("📋 Automation test endpoint called for test type: " + testType);

        // Simulate different test execution times based on test type
        long processingTime = switch (testType.toLowerCase()) {
            case "unit" -> 50 + (long)(Math.random() * 50);      // 50-100ms
            case "integration" -> 200 + (long)(Math.random() * 100); // 200-300ms
            case "performance" -> 500 + (long)(Math.random() * 300); // 500-800ms
            case "load" -> 800 + (long)(Math.random() * 400);        // 800-1200ms
            default -> 100 + (long)(Math.random() * 100);            // 100-200ms
        };

        try {
            Thread.sleep(processingTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Object> testResult = new HashMap<>();
        testResult.put("testType", testType);
        testResult.put("status", "completed");
        testResult.put("duration", processingTime + "ms");
        testResult.put("timestamp", System.currentTimeMillis());
        testResult.put("executor", "automation-performance-testing-engine");
        testResult.put("framework", "automation-performance-test");
        testResult.put("developer", "leanhhoa30012004");
        testResult.put("version", "1.0.0");
        testResult.put("requestId", java.util.UUID.randomUUID().toString());

        return ResponseEntity.ok(testResult);
    }

    @GetMapping("/automation/stress")
    public ResponseEntity<Map<String, Object>> stressTest() {
        System.out.println("📋 Stress test endpoint called");

        // Simulate CPU intensive operation
        long startTime = System.currentTimeMillis();
        double result = 0;
        for (int i = 0; i < 1000000; i++) {
            result += Math.sqrt(i) * Math.sin(i);
        }
        long endTime = System.currentTimeMillis();

        Map<String, Object> response = new HashMap<>();
        response.put("operation", "cpu-intensive-calculation");
        response.put("result", result);
        response.put("duration", (endTime - startTime) + "ms");
        response.put("iterations", 1000000);
        response.put("timestamp", endTime);
        response.put("developer", "leanhhoa30012004");
        response.put("purpose", "stress-testing");
        response.put("created", "2025-07-23 19:14:59 UTC");

        return ResponseEntity.ok(response);
    }
}