package com.marketplace.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private AdminService adminService;

    private Login buyer;
    private Login seller;
    private Login admin;

    @BeforeEach
    void setUp() {
        // Create Login objects using constructor or setters that actually exist
        buyer = new Login();
        buyer.setName("john_doe");
        buyer.setRole("BUYER");
        // Note: Not setting ID as it might be auto-generated

        seller = new Login();
        seller.setName("jane_seller");
        seller.setRole("SELLER");

        admin = new Login();
        admin.setName("admin_user");
        admin.setRole("ADMIN");
    }

    @Test
    void getAllBuyersAndSellers_ShouldReturnAllNonAdminUsers() {
        // Arrange
        List<Login> expectedUsers = Arrays.asList(buyer, seller);
        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(expectedUsers);

        // Act
        List<Login> result = adminService.getAllBuyersAndSellers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(
            result.stream().allMatch(user -> !"ADMIN".equals(user.getRole()))
        );
        verify(loginRepository, times(1)).findByRoleNot("ADMIN");
    }

    @Test
    void getAllBuyersAndSellers_WhenNoNonAdminUsers_ShouldReturnEmptyList() {
        // Arrange
        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(
            Arrays.asList()
        );

        // Act
        List<Login> result = adminService.getAllBuyersAndSellers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(loginRepository, times(1)).findByRoleNot("ADMIN");
    }

    @Test
    void getAllBuyersAndSellers_ShouldExcludeAdminUsers() {
        // Arrange
        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(
            Arrays.asList(buyer, seller)
        );

        // Act
        List<Login> result = adminService.getAllBuyersAndSellers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertFalse(
            result.stream().anyMatch(user -> "ADMIN".equals(user.getRole()))
        );
        verify(loginRepository, times(1)).findByRoleNot("ADMIN");
    }

    @Test
    void deleteUser_WithValidId_ShouldCallRepositoryDelete() {
        // Arrange
        Long userId = 1L;
        doNothing().when(loginRepository).deleteById(userId);

        // Act
        adminService.deleteUser(userId);

        // Assert
        verify(loginRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_WithMultipleIds_ShouldDeleteEachUser() {
        // Arrange
        Long userId1 = 1L;
        Long userId2 = 2L;
        doNothing().when(loginRepository).deleteById(anyLong());

        // Act
        adminService.deleteUser(userId1);
        adminService.deleteUser(userId2);

        // Assert
        verify(loginRepository, times(1)).deleteById(userId1);
        verify(loginRepository, times(1)).deleteById(userId2);
        verify(loginRepository, times(2)).deleteById(anyLong());
    }
}
