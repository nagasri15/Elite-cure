package com.elitecure.controller;

import com.elitecure.model.User;
import com.elitecure.service.UserService;
import com.elitecure.util.JsonResponse;
import com.elitecure.util.SessionManager;
import com.elitecure.util.GsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Controller for authentication operations
 * Handles registration, login, and logout
 */
public class AuthController {
    private final UserService userService;
    private final Gson gson;

    public AuthController() {
        this.userService = new UserService();
        this.gson = GsonUtil.getGson();
    }

    /**
     * Handle user registration
     */
    public HttpHandler handleRegister() {
        return exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String requestBody = readRequestBody(exchange);
                    JsonObject jsonRequest = gson.fromJson(requestBody, JsonObject.class);
                    
                    String fullName = jsonRequest.get("fullName").getAsString();
                    String email = jsonRequest.get("email").getAsString();
                    String password = jsonRequest.get("password").getAsString();
                    String confirmPassword = jsonRequest.get("confirmPassword").getAsString();
                    
                    User user = userService.register(fullName, email, password, confirmPassword);
                    
                    // Create session
                    String sessionId = SessionManager.createSession(user);
                    
                    // Prepare response
                    JsonObject userData = new JsonObject();
                    userData.addProperty("id", user.getId());
                    userData.addProperty("fullName", user.getFullName());
                    userData.addProperty("email", user.getEmail());
                    userData.addProperty("sessionId", sessionId);
                    
                    sendJsonResponse(exchange, 201, JsonResponse.success(userData));
                    
                } catch (IllegalArgumentException e) {
                    sendJsonResponse(exchange, 400, JsonResponse.error(e.getMessage()));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Registration failed: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Handle user login
     */
    public HttpHandler handleLogin() {
        return exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String requestBody = readRequestBody(exchange);
                    JsonObject jsonRequest = gson.fromJson(requestBody, JsonObject.class);
                    
                    String email = jsonRequest.get("email").getAsString();
                    String password = jsonRequest.get("password").getAsString();
                    
                    User user = userService.login(email, password);
                    
                    // Create session
                    String sessionId = SessionManager.createSession(user);
                    
                    // Prepare response
                    JsonObject userData = new JsonObject();
                    userData.addProperty("id", user.getId());
                    userData.addProperty("fullName", user.getFullName());
                    userData.addProperty("email", user.getEmail());
                    userData.addProperty("sessionId", sessionId);
                    
                    sendJsonResponse(exchange, 200, JsonResponse.success(userData));
                    
                } catch (IllegalArgumentException e) {
                    sendJsonResponse(exchange, 401, JsonResponse.error(e.getMessage()));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Login failed: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Handle user logout
     */
    public HttpHandler handleLogout() {
        return exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    String sessionId = getSessionIdFromHeader(exchange);
                    
                    if (sessionId != null) {
                        SessionManager.invalidateSession(sessionId);
                    }
                    
                    sendJsonResponse(exchange, 200, JsonResponse.success("Logged out successfully"));
                    
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Logout failed: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Get session ID from Authorization header
     */
    private String getSessionIdFromHeader(HttpExchange exchange) {
        String auth = exchange.getRequestHeaders().getFirst("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            return auth.substring(7);
        }
        return null;
    }

    /**
     * Read request body as string
     */
    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Send JSON response
     */
    private void sendJsonResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
