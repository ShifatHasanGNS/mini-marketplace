package com.marketplace.app.repository;

import com.marketplace.app.entity.Login;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * LoginRepository
 * 
 * Data access object for user account (Login) entity.
 * Provides methods for user authentication and account management operations.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Repository
public interface LoginRepository extends JpaRepository<Login, Long> {

    /**
     * Finds a user account by username
     * 
     * @param name the username to search for
     * @return Optional containing the user if found
     */
    Optional<Login> findByName(String name);

    /**
     * Finds all users with a role different from the specified role
     * Typically used to get all non-admin users
     * 
     * @param role the role to exclude from results
     * @return list of users excluding the specified role
     */
    List<Login> findByRoleNot(String role);
}
