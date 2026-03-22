package com.marketplace.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Login user;

    @BeforeEach
    void setUp() {
        user = new Login();
        user.setName("testuser");
        user.setPassword("encodedPassword");
        user.setRole("BUYER");
    }

    @Test
    void loadUserByUsername_WithValidUsername_ShouldReturnUserDetails() {
        // Arrange
        when(loginRepository.findByName("testuser")).thenReturn(
            Optional.of(user)
        );

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(
            "testuser"
        );

        // Assert
        assertNotNull(userDetails);
        assertEquals(user.getName(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(
            userDetails
                .getAuthorities()
                .stream()
                .anyMatch(a ->
                    a.getAuthority().equals("ROLE_" + user.getRole())
                )
        );
        verify(loginRepository, times(1)).findByName("testuser");
    }

    @Test
    void loadUserByUsername_WithInvalidUsername_ShouldThrowException() {
        // Arrange
        when(loginRepository.findByName("unknown")).thenReturn(
            Optional.empty()
        );

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
            UsernameNotFoundException.class,
            () -> {
                userDetailsService.loadUserByUsername("unknown");
            }
        );
        assertEquals("User not found", exception.getMessage());
        verify(loginRepository, times(1)).findByName("unknown");
    }

    @Test
    void loadUserByUsername_WithSellerRole_ShouldReturnCorrectAuthorities() {
        // Arrange
        user.setRole("SELLER");
        when(loginRepository.findByName("seller")).thenReturn(
            Optional.of(user)
        );

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(
            "seller"
        );

        // Assert
        assertNotNull(userDetails);
        assertTrue(
            userDetails
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"))
        );
        assertFalse(
            userDetails
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BUYER"))
        );
    }

    @Test
    void loadUserByUsername_WithAdminRole_ShouldReturnCorrectAuthorities() {
        // Arrange
        user.setRole("ADMIN");
        when(loginRepository.findByName("admin")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(
            "admin"
        );

        // Assert
        assertNotNull(userDetails);
        assertTrue(
            userDetails
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
        );
    }
}
