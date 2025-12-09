package com.elitecure.service;

import com.elitecure.dao.UserDAO;
import com.elitecure.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Service layer for User-related business logic
 * Handles authentication, registration, and validation
 */
public class UserService {
    private final UserDAO userDAO;
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    
    private static final int MIN_PASSWORD_LENGTH = 8;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Register a new user
     * Validates input and hashes password before storing
     */
    public User register(String fullName, String email, String password, String confirmPassword) 
            throws Exception {
        
        // Validation
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name is required");
        }
        
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long and contain " +
                "at least one uppercase letter, one lowercase letter, one digit, and one special character"
            );
        }
        
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Check if email already exists
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email already registered");
        }
        
        // Hash password
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        
        // Create user
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.toLowerCase().trim());
        user.setPasswordHash(hashedPassword);
        
        return userDAO.create(user);
    }

    /**
     * Authenticate user login
     */
    public User login(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        
        Optional<User> userOpt = userDAO.findByEmail(email.toLowerCase().trim());
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        User user = userOpt.get();
        
        // Verify password
        if (!BCrypt.checkpw(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        return user;
    }

    /**
     * Get user by ID
     */
    public Optional<User> getUserById(Long id) throws SQLException {
        return userDAO.findById(id);
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate password strength
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            return false;
        }
        
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        
        return hasUpperCase && hasLowerCase && hasDigit && hasSpecial;
    }
}
