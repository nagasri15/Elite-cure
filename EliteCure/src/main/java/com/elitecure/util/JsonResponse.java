package com.elitecure.util;

import com.google.gson.JsonObject;
import com.google.gson.Gson;

/**
 * Utility class for creating JSON responses
 */
public class JsonResponse {
    
    private static final Gson gson = GsonUtil.getGson();
    
    /**
     * Create a success response with data
     */
    public static String success(Object data) {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.add("data", com.google.gson.JsonParser.parseString(
            gson.toJson(data)));
        return response.toString();
    }
    
    /**
     * Create a success response with message
     */
    public static String success(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("success", true);
        response.addProperty("message", message);
        return response.toString();
    }
    
    /**
     * Create an error response
     */
    public static String error(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("success", false);
        response.addProperty("error", message);
        return response.toString();
    }
    
    /**
     * Create an error response with status code
     */
    public static String error(String message, int statusCode) {
        JsonObject response = new JsonObject();
        response.addProperty("success", false);
        response.addProperty("error", message);
        response.addProperty("statusCode", statusCode);
        return response.toString();
    }
}
