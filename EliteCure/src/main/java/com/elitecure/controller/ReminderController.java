package com.elitecure.controller;

import com.elitecure.model.Reminder;
import com.elitecure.model.User;
import com.elitecure.service.ReminderService;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller for reminder operations
 * Handles CRUD operations for medicine reminders
 */
public class ReminderController {
    private final ReminderService reminderService;
    private final Gson gson;

    public ReminderController() {
        this.reminderService = new ReminderService();
        this.gson = GsonUtil.getGson();
    }

    /**
     * Get all reminders for the logged-in user
     */
    public HttpHandler handleGetReminders() {
        return exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    User user = getAuthenticatedUser(exchange);
                    if (user == null) {
                        sendJsonResponse(exchange, 401, JsonResponse.error("Unauthorized"));
                        return;
                    }
                    
                    List<Reminder> reminders = reminderService.getUserReminders(user.getId());
                    
                    // Create success response manually with properly serialized data
                    String jsonResponse = "{\"success\":true,\"data\":" + gson.toJson(reminders) + "}";
                    sendJsonResponse(exchange, 200, jsonResponse);
                    
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Error fetching reminders: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Get today's reminders for the logged-in user
     */
    public HttpHandler handleGetTodayReminders() {
        return exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    User user = getAuthenticatedUser(exchange);
                    if (user == null) {
                        sendJsonResponse(exchange, 401, JsonResponse.error("Unauthorized"));
                        return;
                    }
                    
                    List<Reminder> reminders = reminderService.getTodayReminders(user.getId());
                    
                    // Create success response manually with properly serialized data
                    String jsonResponse = "{\"success\":true,\"data\":" + gson.toJson(reminders) + "}";
                    sendJsonResponse(exchange, 200, jsonResponse);
                    
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Error fetching today's reminders: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Create a new reminder
     */
    public HttpHandler handleCreateReminder() {
        return exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    User user = getAuthenticatedUser(exchange);
                    if (user == null) {
                        sendJsonResponse(exchange, 401, JsonResponse.error("Unauthorized"));
                        return;
                    }
                    
                    String requestBody = readRequestBody(exchange);
                    JsonObject jsonRequest = gson.fromJson(requestBody, JsonObject.class);
                    
                    String medicineName = jsonRequest.get("medicineName").getAsString();
                    String dosage = jsonRequest.get("dosage").getAsString();
                    String frequency = jsonRequest.get("frequency").getAsString();
                    LocalDate startDate = LocalDate.parse(jsonRequest.get("startDate").getAsString());
                    LocalDate endDate = jsonRequest.has("endDate") && !jsonRequest.get("endDate").isJsonNull() 
                        ? LocalDate.parse(jsonRequest.get("endDate").getAsString()) : null;
                    LocalTime timeOfDay = LocalTime.parse(jsonRequest.get("timeOfDay").getAsString());
                    String notes = jsonRequest.has("notes") ? jsonRequest.get("notes").getAsString() : "";
                    
                    Reminder reminder = reminderService.createReminder(
                        user.getId(), medicineName, dosage, frequency, 
                        startDate, endDate, timeOfDay, notes
                    );
                    
                    sendJsonResponse(exchange, 201, JsonResponse.success(reminder));
                    
                } catch (IllegalArgumentException e) {
                    sendJsonResponse(exchange, 400, JsonResponse.error(e.getMessage()));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Error creating reminder: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Update an existing reminder
     */
    public HttpHandler handleUpdateReminder() {
        return exchange -> {
            if ("PUT".equals(exchange.getRequestMethod())) {
                try {
                    User user = getAuthenticatedUser(exchange);
                    if (user == null) {
                        sendJsonResponse(exchange, 401, JsonResponse.error("Unauthorized"));
                        return;
                    }
                    
                    String path = exchange.getRequestURI().getPath();
                    Long reminderId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                    
                    String requestBody = readRequestBody(exchange);
                    JsonObject jsonRequest = gson.fromJson(requestBody, JsonObject.class);
                    
                    String medicineName = jsonRequest.get("medicineName").getAsString();
                    String dosage = jsonRequest.get("dosage").getAsString();
                    String frequency = jsonRequest.get("frequency").getAsString();
                    LocalDate startDate = LocalDate.parse(jsonRequest.get("startDate").getAsString());
                    LocalDate endDate = jsonRequest.has("endDate") && !jsonRequest.get("endDate").isJsonNull()
                        ? LocalDate.parse(jsonRequest.get("endDate").getAsString()) : null;
                    LocalTime timeOfDay = LocalTime.parse(jsonRequest.get("timeOfDay").getAsString());
                    String notes = jsonRequest.has("notes") ? jsonRequest.get("notes").getAsString() : "";
                    String status = jsonRequest.has("status") ? jsonRequest.get("status").getAsString() : "ACTIVE";
                    
                    boolean updated = reminderService.updateReminder(
                        reminderId, user.getId(), medicineName, dosage, frequency,
                        startDate, endDate, timeOfDay, notes, status
                    );
                    
                    if (updated) {
                        sendJsonResponse(exchange, 200, JsonResponse.success("Reminder updated successfully"));
                    } else {
                        sendJsonResponse(exchange, 404, JsonResponse.error("Reminder not found"));
                    }
                    
                } catch (IllegalArgumentException e) {
                    sendJsonResponse(exchange, 400, JsonResponse.error(e.getMessage()));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Error updating reminder: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Delete a reminder
     */
    public HttpHandler handleDeleteReminder() {
        return exchange -> {
            if ("DELETE".equals(exchange.getRequestMethod())) {
                try {
                    User user = getAuthenticatedUser(exchange);
                    if (user == null) {
                        sendJsonResponse(exchange, 401, JsonResponse.error("Unauthorized"));
                        return;
                    }
                    
                    String path = exchange.getRequestURI().getPath();
                    Long reminderId = Long.parseLong(path.substring(path.lastIndexOf('/') + 1));
                    
                    boolean deleted = reminderService.deleteReminder(reminderId, user.getId());
                    
                    if (deleted) {
                        sendJsonResponse(exchange, 200, JsonResponse.success("Reminder deleted successfully"));
                    } else {
                        sendJsonResponse(exchange, 404, JsonResponse.error("Reminder not found"));
                    }
                    
                } catch (IllegalArgumentException e) {
                    sendJsonResponse(exchange, 400, JsonResponse.error(e.getMessage()));
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Error deleting reminder: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Mark reminder as taken
     */
    public HttpHandler handleMarkAsTaken() {
        return exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                try {
                    User user = getAuthenticatedUser(exchange);
                    if (user == null) {
                        sendJsonResponse(exchange, 401, JsonResponse.error("Unauthorized"));
                        return;
                    }
                    
                    String path = exchange.getRequestURI().getPath();
                    String[] parts = path.split("/");
                    Long reminderId = Long.parseLong(parts[parts.length - 2]);
                    
                    boolean updated = reminderService.markAsTaken(reminderId, user.getId());
                    
                    if (updated) {
                        sendJsonResponse(exchange, 200, JsonResponse.success("Reminder marked as taken"));
                    } else {
                        sendJsonResponse(exchange, 404, JsonResponse.error("Reminder not found"));
                    }
                    
                } catch (Exception e) {
                    sendJsonResponse(exchange, 500, JsonResponse.error("Error marking reminder: " + e.getMessage()));
                }
            } else {
                sendJsonResponse(exchange, 405, JsonResponse.error("Method not allowed"));
            }
        };
    }

    /**
     * Get authenticated user from session
     */
    private User getAuthenticatedUser(HttpExchange exchange) {
        String sessionId = getSessionIdFromHeader(exchange);
        return SessionManager.getUser(sessionId);
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
