package com.marketplace.app.dto;

/**
 * RegisterRequest DTO (Data Transfer Object)
 * 
 * Encapsulates user registration data including username, password, and user role.
 * Facilitates secure transfer of registration information from client to authentication service.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
public class RegisterRequest {

    /**
     * Username for new user account
     */
    private String name;

    /**
     * Password for user account (will be encrypted)
     */
    private String password;

    /**
     * User role (ADMIN, SELLER, or BUYER)
     */
    private String role;

    /**
     * Gets the username
     * 
     * @return the username
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the username
     * 
     * @param name the username to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the password
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user role
     * 
     * @return the user role (ADMIN, SELLER, BUYER)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user role
     * 
     * @param role the user role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
}
