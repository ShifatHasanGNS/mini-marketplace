package com.marketplace.app.entity;

import jakarta.persistence.*;

/**
 * Login Entity
 * 
 * Represents user account information in the Mini Marketplace system.
 * Stores user credentials and role information for authentication and authorization.
 * Each user is uniquely identified by their username.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Entity
@Table(name = "login")
public class Login {

    /**
     * Unique identifier for the user account
     * Auto-generated primary key in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username for user login
     * Should be unique across the system
     */
    private String name;

    /**
     * Encrypted password for user authentication
     */
    private String password;

    /**
     * User role in the system (ADMIN, SELLER, BUYER)
     */
    private String role;

    /**
     * Default constructor required by JPA for entity instantiation
     */
    public Login() {
    }

    /**
     * Constructor to initialize user with credentials and role
     * 
     * @param name     the username
     * @param password the user password (should be encrypted before storage)
     * @param role     the user role (ADMIN, SELLER, BUYER)
     */
    public Login(String name, String password, String role) {
        this.name = name;
        this.password = password;
        this.role = role;
    }

    /**
     * Gets the user ID
     * 
     * @return the unique user identifier
     */
    public Long getId() {
        return id;
    }

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
     * @return the encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password
     * 
     * @param password the password to set (should be encrypted)
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
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
}
