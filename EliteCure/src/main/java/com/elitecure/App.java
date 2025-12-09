package com.elitecure;

import com.elitecure.controller.AuthController;
import com.elitecure.controller.ReminderController;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

/**
 * Main application class
 * Starts the HTTP server and configures routes
 */
public class App {
    private static final int PORT = 8080;
    private static final String FRONTEND_DIR = "src/main/webapp";

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            
            // Initialize controllers
            AuthController authController = new AuthController();
            ReminderController reminderController = new ReminderController();
            
            // API Routes
            server.createContext("/api/register", authController.handleRegister());
            server.createContext("/api/login", authController.handleLogin());
            server.createContext("/api/logout", authController.handleLogout());
            
            server.createContext("/api/reminders/today", reminderController.handleGetTodayReminders());
            server.createContext("/api/reminders", exchange -> {
                String method = exchange.getRequestMethod();
                String path = exchange.getRequestURI().getPath();
                
                if ("OPTIONS".equals(method)) {
                    handleCORS(exchange);
                } else if ("GET".equals(method) && path.equals("/api/reminders")) {
                    reminderController.handleGetReminders().handle(exchange);
                } else if ("POST".equals(method) && path.equals("/api/reminders")) {
                    reminderController.handleCreateReminder().handle(exchange);
                } else if ("PUT".equals(method) && path.matches("/api/reminders/\\d+")) {
                    reminderController.handleUpdateReminder().handle(exchange);
                } else if ("DELETE".equals(method) && path.matches("/api/reminders/\\d+")) {
                    reminderController.handleDeleteReminder().handle(exchange);
                } else if ("POST".equals(method) && path.matches("/api/reminders/\\d+/taken")) {
                    reminderController.handleMarkAsTaken().handle(exchange);
                } else {
                    exchange.sendResponseHeaders(404, -1);
                }
            });
            
            // Static file serving for frontend
            server.createContext("/", exchange -> {
                String path = exchange.getRequestURI().getPath();
                
                // Default to index.html for root
                if (path.equals("/")) {
                    path = "/index.html";
                }
                
                File file = new File(FRONTEND_DIR + path);
                
                if (file.exists() && !file.isDirectory()) {
                    String contentType = getContentType(path);
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, fileContent.length);
                    
                    OutputStream os = exchange.getResponseBody();
                    os.write(fileContent);
                    os.close();
                } else {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            });
            
            server.setExecutor(null); // Use default executor
            server.start();
            
            System.out.println("╔════════════════════════════════════════════════════════╗");
            System.out.println("║                                                        ║");
            System.out.println("║     Elite Cure - Medicine Reminder Application        ║");
            System.out.println("║                                                        ║");
            System.out.println("╠════════════════════════════════════════════════════════╣");
            System.out.println("║                                                        ║");
            System.out.println("║  Server running on: http://localhost:" + PORT + "            ║");
            System.out.println("║                                                        ║");
            System.out.println("║  Test Credentials:                                     ║");
            System.out.println("║    Email:    test@elitecure.com                        ║");
            System.out.println("║    Password: Test@123                                  ║");
            System.out.println("║                                                        ║");
            System.out.println("╚════════════════════════════════════════════════════════╝");
            
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle CORS preflight requests
     */
    private static void handleCORS(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        exchange.sendResponseHeaders(204, -1);
    }

    /**
     * Get content type based on file extension
     */
    private static String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".json")) return "application/json";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".ico")) return "image/x-icon";
        if (path.endsWith(".mp3")) return "audio/mpeg";
        return "text/plain";
    }
}
