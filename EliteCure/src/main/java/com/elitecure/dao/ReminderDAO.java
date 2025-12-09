package com.elitecure.dao;

import com.elitecure.model.Reminder;
import com.elitecure.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for Reminder entity
 * Handles all database operations for medicine reminders
 */
public class ReminderDAO {

    /**
     * Create a new reminder
     */
    public Reminder create(Reminder reminder) throws SQLException {
        String sql = "INSERT INTO reminders (user_id, medicine_name, dosage, frequency, start_date, " +
                     "end_date, time_of_day, notes, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, reminder.getUserId());
            stmt.setString(2, reminder.getMedicineName());
            stmt.setString(3, reminder.getDosage());
            stmt.setString(4, reminder.getFrequency());
            stmt.setDate(5, Date.valueOf(reminder.getStartDate()));
            stmt.setDate(6, reminder.getEndDate() != null ? Date.valueOf(reminder.getEndDate()) : null);
            stmt.setTime(7, Time.valueOf(reminder.getTimeOfDay()));
            stmt.setString(8, reminder.getNotes());
            stmt.setString(9, reminder.getStatus() != null ? reminder.getStatus() : "ACTIVE");
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating reminder failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reminder.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating reminder failed, no ID obtained.");
                }
            }
        }
        
        return reminder;
    }

    /**
     * Find all reminders for a specific user
     */
    public List<Reminder> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM reminders WHERE user_id = ? ORDER BY time_of_day ASC";
        List<Reminder> reminders = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reminders.add(mapResultSetToReminder(rs));
                }
            }
        }
        
        return reminders;
    }

    /**
     * Find today's active reminders for a user
     */
    public List<Reminder> findTodayReminders(Long userId) throws SQLException {
        LocalDate today = LocalDate.now();
        String sql = "SELECT * FROM reminders WHERE user_id = ? AND status = 'ACTIVE' " +
                     "AND start_date <= ? AND (end_date IS NULL OR end_date >= ?) " +
                     "ORDER BY time_of_day ASC";
        List<Reminder> reminders = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(today));
            stmt.setDate(3, Date.valueOf(today));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reminders.add(mapResultSetToReminder(rs));
                }
            }
        }
        
        return reminders;
    }

    /**
     * Find reminder by ID
     */
    public Optional<Reminder> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM reminders WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToReminder(rs));
                }
            }
        }
        
        return Optional.empty();
    }

    /**
     * Update a reminder
     */
    public boolean update(Reminder reminder) throws SQLException {
        String sql = "UPDATE reminders SET medicine_name = ?, dosage = ?, frequency = ?, " +
                     "start_date = ?, end_date = ?, time_of_day = ?, notes = ?, status = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reminder.getMedicineName());
            stmt.setString(2, reminder.getDosage());
            stmt.setString(3, reminder.getFrequency());
            stmt.setDate(4, Date.valueOf(reminder.getStartDate()));
            stmt.setDate(5, reminder.getEndDate() != null ? Date.valueOf(reminder.getEndDate()) : null);
            stmt.setTime(6, Time.valueOf(reminder.getTimeOfDay()));
            stmt.setString(7, reminder.getNotes());
            stmt.setString(8, reminder.getStatus());
            stmt.setLong(9, reminder.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Delete a reminder
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM reminders WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Map ResultSet to Reminder object
     */
    private Reminder mapResultSetToReminder(ResultSet rs) throws SQLException {
        Reminder reminder = new Reminder();
        reminder.setId(rs.getLong("id"));
        reminder.setUserId(rs.getLong("user_id"));
        reminder.setMedicineName(rs.getString("medicine_name"));
        reminder.setDosage(rs.getString("dosage"));
        reminder.setFrequency(rs.getString("frequency"));
        reminder.setStartDate(rs.getDate("start_date").toLocalDate());
        
        Date endDate = rs.getDate("end_date");
        if (endDate != null) {
            reminder.setEndDate(endDate.toLocalDate());
        }
        
        reminder.setTimeOfDay(rs.getTime("time_of_day").toLocalTime());
        reminder.setNotes(rs.getString("notes"));
        reminder.setStatus(rs.getString("status"));
        reminder.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        return reminder;
    }
}
