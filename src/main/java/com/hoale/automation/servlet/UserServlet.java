package com.hoale.automation.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * User Management Handler
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class UserServlet implements HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        System.out.println("📋 User " + method + " request: " + path + " - " + LocalDateTime.now());

        if ("GET".equals(method)) {
            handleGetUser(exchange, path);
        } else if ("POST".equals(method)) {
            handleCreateUser(exchange);
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private void handleGetUser(HttpExchange exchange, String path) throws IOException {
        String[] pathParts = path.split("/");
        String userId = pathParts.length > 3 ? pathParts[3] : "unknown";

        // Simulate processing time
        try {
            Thread.sleep(50 + (long)(Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", "AutoUser " + userId);
        user.put("email", "user" + userId + "@automation-test.com");
        user.put("timestamp", System.currentTimeMillis());
        user.put("created_by", "leanhhoa30012004");
        user.put("server", "automation-performance-testing");

        String jsonResponse = objectMapper.writeValueAsString(user);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    private void handleCreateUser(HttpExchange exchange) throws IOException {
        // Simulate processing time
        try {
            Thread.sleep(100 + (long)(Math.random() * 200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Map<String, String> result = new HashMap<>();
        result.put("id", String.valueOf(System.currentTimeMillis()));
        result.put("status", "created");
        result.put("message", "User created successfully");
        result.put("developer", "leanhhoa30012004");
        result.put("timestamp", LocalDateTime.now().toString());

        String jsonResponse = objectMapper.writeValueAsString(result);

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(201, jsonResponse.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }
}