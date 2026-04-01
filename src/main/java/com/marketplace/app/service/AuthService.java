package com.marketplace.app.service;

import com.marketplace.app.dto.LoginRequest;
import com.marketplace.app.dto.RegisterRequest;
import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *  AuthService
 * 
 * Handles authentication and user registration operations.
 * Manages user account creation and login credential validation with password encryption.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Service
public class AuthService {

    /**
     * Repository for user account operations
     */
    private final LoginRepository repo;

    /**
     * Password encoder for secure password storage
     */
    private final PasswordEncoder encoder;

    /**
     * Constructor with dependency injection
     * 
     * @param repo    the LoginRepository
     * @param encoder the PasswordEncoder
     */
    public AuthService(LoginRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    /**
     * Registers a new user account
     * Encodes password and saves user with assigned role
     * 
     * @param request the RegisterRequest containing user details
     */
    public void register(RegisterRequest request) {
        // Create new Login entity
        Login user = new Login();
        
        // Set user credentials
        user.setName(request.getName());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        
        // Save user to database
        repo.save(user);
    }

    /**
     * Authenticates user with provided credentials
     * Validates username exists and password is correct
     * 
     * @param request the LoginRequest containing credentials
     * @return the authenticated Login entity
     * @throws RuntimeException if user not found or password is invalid
     */
    public Login login(LoginRequest request) {
        // Find user by username
        Login user = repo
                .findByName(request.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Validate password matches stored encrypted password
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        // Return authenticated user
        return user;
    }
}
