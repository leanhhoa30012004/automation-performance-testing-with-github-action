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
 * Health Check Handler
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class HealthServlet implements HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("📋 Health check called - " + LocalDateTime.now());

        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "UP");
            response.put("timestamp", LocalDateTime.now().toString());
            response.put("application", "automation-performance-testing-with-github-action");
            response.put("developer", "leanhhoa30012004");
            response.put("version", "1.0.0");
            response.put("created", "2025-07-23 19:24:30 UTC");
            response.put("server", "Java Built-in HTTP Server");

            String jsonResponse = objectMapper.writeValueAsString(response);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, -1); // Method not allowed
        }
    }
}