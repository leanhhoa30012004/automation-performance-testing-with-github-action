package com.hoale.automation.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application Info Handler
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class InfoServlet implements HttpHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("📋 Info endpoint called - " + LocalDateTime.now());

        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, Object> info = new HashMap<>();
            info.put("application", "Automation Performance Testing with GitHub Action");
            info.put("repository", "automation-performance-testing-with-github-action");
            info.put("developer", "leanhhoa30012004");
            info.put("version", "1.0.0");
            info.put("created", "2025-07-23 19:24:30 UTC");
            info.put("timestamp", LocalDateTime.now().toString());
            info.put("server", "Java Built-in HTTP Server");
            info.put("java_version", System.getProperty("java.version"));
            info.put("os_name", System.getProperty("os.name"));
            info.put("available_processors", Runtime.getRuntime().availableProcessors());
            info.put("max_memory", Runtime.getRuntime().maxMemory());
            info.put("endpoints", List.of("/api/health", "/api/info", "/api/users/{id}"));

            String jsonResponse = objectMapper.writeValueAsString(info);

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(jsonResponse.getBytes());
            }
        } else {
            exchange.sendResponseHeaders(405, -1);
        }
    }
}