package com.hoale.automation.server;

import com.hoale.automation.servlet.HealthServlet;
import com.hoale.automation.servlet.InfoServlet;
import com.hoale.automation.servlet.UserServlet;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;

/**
 * Simple HTTP Server using Java built-in HttpServer
 *
 * @author leanhhoa30012004
 * @created 2025-07-23 19:24:30 UTC
 */
public class SimpleServer {

    private HttpServer server;
    private static final int PORT = 8080;

    public void start() throws Exception {
        System.out.println("🚀 Starting Simple HTTP Server on port " + PORT);

        server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Register handlers
        server.createContext("/api/health", new HealthServlet());
        server.createContext("/api/info", new InfoServlet());
        server.createContext("/api/users", new UserServlet());

        server.setExecutor(null); // Use default executor
        server.start();

        System.out.println("✅ Simple Server started on http://localhost:" + PORT);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            System.out.println("🛑 Simple Server stopped");
        }
    }
}