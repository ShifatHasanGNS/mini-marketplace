package com.marketplace.app.dto;

/**
 * LoginRequest DTO (Data Transfer Object)
 * 
 * Handles user authentication request data with username and password credentials.
 * Used to transfer login information from the client to the authentication service.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
public class LoginRequest {


    private String name;


    private String password;

    /**
     * Gets the username
     * 
     * @return the name
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
}
