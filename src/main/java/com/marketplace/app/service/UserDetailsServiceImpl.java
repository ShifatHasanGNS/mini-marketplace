package com.marketplace.app.service;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * UserDetailsServiceImpl
 * 
 * Implements Spring Security's UserDetailsService interface.
 * Loads user details from database for authentication and authorization.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Repository for user account operations
     */
    @Autowired
    private LoginRepository loginRepository;

    /**
     * Loads user details by username for authentication
     * Retrieves user from database and builds Spring Security UserDetails object
     * 
     * @param username the username to load
     * @return UserDetails object with user information and roles
     * @throws UsernameNotFoundException if user does not exist
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find user by username in repository
        Login user = loginRepository
                .findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Build and return UserDetails object for Spring Security
        return User.builder()
                .username(user.getName())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}
