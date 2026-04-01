package com.marketplace.app.service;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * AdminService
 * 
 * Provides administrative business operations for the Mini Marketplace.
 * Manages user account administration and system-level operations.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Service
public class AdminService {

    /**
     * Repository for user account operations
     */
    private final LoginRepository loginRepository;

    /**
     * Constructor with dependency injection
     * 
     * @param loginRepository the LoginRepository
     */
    public AdminService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    /**
     * Retrieves all non-admin users (buyers and sellers)
     * 
     * @return list of all buyers and sellers
     */
    public List<Login> getAllBuyersAndSellers() {
        return loginRepository.findByRoleNot("ADMIN");
    }

    /**
     * Deletes a user account by ID
     * 
     * @param id the user ID to delete
     */
    public void deleteUser(Long id) {
        loginRepository.deleteById(id);
    }
}
