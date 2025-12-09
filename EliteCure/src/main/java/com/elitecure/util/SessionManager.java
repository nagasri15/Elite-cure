package com.elitecure.util;

import com.elitecure.model.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple session manager for user sessions
 * Stores session information in memory
 */
public class SessionManager {
    private static final Map<String, User> sessions = new ConcurrentHashMap<>();
    
    /**
     * Create a new session for a user
     */
    public static String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }
    
    /**
     * Get user from session
     */
    public static User getUser(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        return sessions.get(sessionId);
    }
    
    /**
     * Invalidate (destroy) a session
     */
    public static void invalidateSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            sessions.remove(sessionId);
        }
    }
    
    /**
     * Check if session is valid
     */
    public static boolean isValidSession(String sessionId) {
        return sessionId != null && sessions.containsKey(sessionId);
    }
}
