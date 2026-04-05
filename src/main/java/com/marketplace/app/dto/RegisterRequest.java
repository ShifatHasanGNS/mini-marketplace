package com.marketplace.app.dto;

/**
 * RegisterRequest DTO (Data Transfer Object)
 * 
 * Encapsulates user registration data including username, password, and user role.
 * Facilitates secure transfer of registration information from client to authentication service.
 * 
 * This DTO is used for handling user account creation requests in the Mini Marketplace system.
 * It carries user registration credentials that are validated and processed by the authentication service.
 * The password should be transmitted securely and encrypted on the server side before storage.
 * 
 * Supported roles: ADMIN, SELLER, BUYER
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 * @since 1.0
 */
public class RegisterRequest {

    /**
     * Username for new user account
     * This field represents the desired username/login identifier for the new user.
     * Should be unique across all user accounts in the system.
     */
    private String name;

    /**
     * Password for user account (will be encrypted before storage)
     * Plain text password provided by the user during registration.
     * Must be encrypted using BCrypt or similar before persisting to database.
     * Warning: Never store passwords in plain text format.
     */
    private String password;

    /**
     * User role in the system (ADMIN, SELLER, or BUYER)
     * Determines the user's permissions and access level within the application.
     * Valid values:
     * - ADMIN: Full system access for administrative operations
     * - SELLER: Can manage products and view their orders
     * - BUYER: Can browse products, add to cart, and place orders
     */
    private String role;

    /**
     * Gets the username from the registration request
     * Returns the desired username for the new user account.
     * 
     * @return the username string
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the username for the registration request
     * Assigns the provided username to this registration request.
     * 
     * @param name the username to set for the new account
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the password from the registration request
     * Returns the password provided during user registration.
     * 
     * @return the password string (plain text at this point)
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the registration request
     * Assigns the provided password to this registration request.
     * This password will be encrypted by the authentication service.
     * 
     * @param password the password to set for the new account
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user role from the registration request
     * Returns the role that the new user will be assigned.
     * 
     * @return the user role (ADMIN, SELLER, or BUYER)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user role for the registration request
     * Assigns the provided role to this registration request.
     * Determines the permissions and features available to the user.
     * 
     * @param role the user role to set (must be ADMIN, SELLER, or BUYER)
     */
    public void setRole(String role) {
        this.role = role;
    }
}
