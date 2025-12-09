package com.elitecure.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection utility for H2 database
 * Manages database connection and initialization
 */
public class DatabaseUtil {
    private static final String DB_URL = "jdbc:h2:./data/elitecure;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    static {
        try {
            // Load H2 driver
            Class.forName("org.h2.Driver");
            // Initialize database schema
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load H2 driver", e);
        }
    }

    /**
     * Get a database connection
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Initialize database schema from SQL file
     */
    private static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Read and execute schema.sql
            InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream("schema.sql");
            if (is != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sql = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    // Skip comments and empty lines
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--")) {
                        continue;
                    }
                    sql.append(line).append(" ");
                    
                    // Execute when we hit a semicolon
                    if (line.endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql = new StringBuilder();
                    }
                }
                
                reader.close();
                System.out.println("Database initialized successfully!");
            }
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Close database connection safely
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
