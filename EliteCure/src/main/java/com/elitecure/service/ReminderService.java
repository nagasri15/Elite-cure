package com.elitecure.service;

import com.elitecure.dao.ReminderDAO;
import com.elitecure.model.Reminder;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Reminder-related business logic
 * Handles CRUD operations and validation for medicine reminders
 */
public class ReminderService {
    private final ReminderDAO reminderDAO;

    public ReminderService() {
        this.reminderDAO = new ReminderDAO();
    }

    /**
     * Create a new reminder with validation
     */
    public Reminder createReminder(Long userId, String medicineName, String dosage, String frequency,
                                   LocalDate startDate, LocalDate endDate, LocalTime timeOfDay, String notes) 
            throws Exception {
        
        // Validation
        if (medicineName == null || medicineName.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name is required");
        }
        
        if (dosage == null || dosage.trim().isEmpty()) {
            throw new IllegalArgumentException("Dosage is required");
        }
        
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new IllegalArgumentException("Frequency is required");
        }
        
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        
        if (timeOfDay == null) {
            throw new IllegalArgumentException("Time of day is required");
        }
        
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        // Create reminder
        Reminder reminder = new Reminder();
        reminder.setUserId(userId);
        reminder.setMedicineName(medicineName.trim());
        reminder.setDosage(dosage.trim());
        reminder.setFrequency(frequency.trim());
        reminder.setStartDate(startDate);
        reminder.setEndDate(endDate);
        reminder.setTimeOfDay(timeOfDay);
        reminder.setNotes(notes != null ? notes.trim() : "");
        reminder.setStatus("ACTIVE");
        
        return reminderDAO.create(reminder);
    }

    /**
     * Get all reminders for a user
     */
    public List<Reminder> getUserReminders(Long userId) throws SQLException {
        return reminderDAO.findByUserId(userId);
    }

    /**
     * Get today's reminders for a user
     */
    public List<Reminder> getTodayReminders(Long userId) throws SQLException {
        return reminderDAO.findTodayReminders(userId);
    }

    /**
     * Get reminder by ID
     */
    public Optional<Reminder> getReminderById(Long id) throws SQLException {
        return reminderDAO.findById(id);
    }

    /**
     * Update an existing reminder
     */
    public boolean updateReminder(Long reminderId, Long userId, String medicineName, String dosage, 
                                  String frequency, LocalDate startDate, LocalDate endDate, 
                                  LocalTime timeOfDay, String notes, String status) throws Exception {
        
        // Check if reminder exists and belongs to user
        Optional<Reminder> existingOpt = reminderDAO.findById(reminderId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Reminder not found");
        }
        
        Reminder existing = existingOpt.get();
        if (!existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to update this reminder");
        }
        
        // Validation
        if (medicineName == null || medicineName.trim().isEmpty()) {
            throw new IllegalArgumentException("Medicine name is required");
        }
        
        if (dosage == null || dosage.trim().isEmpty()) {
            throw new IllegalArgumentException("Dosage is required");
        }
        
        if (frequency == null || frequency.trim().isEmpty()) {
            throw new IllegalArgumentException("Frequency is required");
        }
        
        if (startDate == null) {
            throw new IllegalArgumentException("Start date is required");
        }
        
        if (timeOfDay == null) {
            throw new IllegalArgumentException("Time of day is required");
        }
        
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        
        // Update reminder
        existing.setMedicineName(medicineName.trim());
        existing.setDosage(dosage.trim());
        existing.setFrequency(frequency.trim());
        existing.setStartDate(startDate);
        existing.setEndDate(endDate);
        existing.setTimeOfDay(timeOfDay);
        existing.setNotes(notes != null ? notes.trim() : "");
        existing.setStatus(status != null ? status : "ACTIVE");
        
        return reminderDAO.update(existing);
    }

    /**
     * Delete a reminder
     */
    public boolean deleteReminder(Long reminderId, Long userId) throws Exception {
        // Check if reminder exists and belongs to user
        Optional<Reminder> existingOpt = reminderDAO.findById(reminderId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Reminder not found");
        }
        
        Reminder existing = existingOpt.get();
        if (!existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to delete this reminder");
        }
        
        return reminderDAO.delete(reminderId);
    }

    /**
     * Mark reminder as taken
     */
    public boolean markAsTaken(Long reminderId, Long userId) throws Exception {
        Optional<Reminder> existingOpt = reminderDAO.findById(reminderId);
        if (existingOpt.isEmpty()) {
            throw new IllegalArgumentException("Reminder not found");
        }
        
        Reminder existing = existingOpt.get();
        if (!existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Unauthorized to update this reminder");
        }
        
        existing.setStatus("COMPLETED");
        return reminderDAO.update(existing);
    }
}
