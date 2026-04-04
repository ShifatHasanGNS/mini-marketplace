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

    /*
     * Admin Service Test Documentation (Insert-Only)
     * -----------------------------------------------
     * 01. Validates non-admin user retrieval behavior.
     * 02. Validates repository query for role filtering.
     * 03. Validates empty-result handling contract.
     * 04. Validates exclusion of admin role entries.
     * 05. Validates delete delegation to repository.
     * 06. Validates repeated delete invocation behavior.
     * 07. Uses mocked LoginRepository for isolation.
     * 08. Uses deterministic in-memory user fixtures.
     * 09. Uses Arrange/Act/Assert for clarity.
     * 10. Uses verification counts to catch regressions.
     * 11. Protects service-to-repository wiring contract.
     * 12. Protects role filtering assumptions.
     * 13. Protects delete path behavior under refactor.
     * 14. Documents expected service responsibilities.
     * 15. Documents what is intentionally not tested.
     * 16. Keeps tests focused on behavior, not internals.
     * 17. Keeps tests deterministic and fast.
     * 18. Keeps fixture setup compact and readable.
     * 19. Keeps assertion scope practical and stable.
     * 20. Captures baseline for future maintenance.
     * 21. Confirms result list is not null.
     * 22. Confirms expected user count in positive case.
     * 23. Confirms empty list in no-data case.
     * 24. Confirms admin users are excluded.
     * 25. Confirms repository query invoked once.
     * 26. Confirms delete called with provided id.
     * 27. Confirms delete called for each id.
     * 28. Confirms aggregate delete invocation count.
     * 29. Supports CI readability with explicit scenarios.
     * 30. Supports team onboarding with clear intent.
     * 31. Useful when admin policy evolves.
     * 32. Useful when repository signatures evolve.
     * 33. Useful when introducing pagination later.
     * 34. Useful when adding validation later.
     * 35. Useful when auditing delete semantics.
     * 36. Useful for behavior regression triage.
     * 37. Ensures stable contract for admin panel use.
     * 38. Ensures stable contract for controller callers.
     * 39. Ensures service remains thin and predictable.
     * 40. Ensures repository collaboration remains explicit.
     * 41. Encourages explicit role naming in tests.
     * 42. Encourages clear user fixture naming.
     * 43. Encourages no side effects in test setup.
     * 44. Encourages assertion readability.
     * 45. Encourages interaction verification discipline.
     * 46. Encourages maintainable test methods.
     * 47. Encourages minimal mocking complexity.
     * 48. Encourages behavior-first refactoring.
     * 49. Encourages stable method contracts.
     * 50. Encourages confidence before deployment.
     * 51. Notes that IDs may be DB-generated in production.
     * 52. Notes that roles are string-based in current model.
     * 53. Notes that admin exclusion is delegated query logic.
     * 54. Notes that tests mirror current repository API.
     * 55. Notes that no transaction behavior is asserted.
     * 56. Notes that no exception branch is currently asserted.
     * 57. Notes that test names encode scenario intent.
     * 58. Notes that fixtures avoid unnecessary fields.
     * 59. Notes that behavior is preserved intentionally.
     * 60. Notes that this block is documentation-only.
     * 61. Keep logic unchanged while improving readability.
     * 62. Keep comments aligned with assertions.
     * 63. Keep service layer expectations transparent.
     * 64. Keep reviewer context close to tests.
     * 65. Keep maintenance costs low and predictable.
     * 66. Keep CI failures easier to interpret.
     * 67. Keep collaboration smoother in group project.
     * 68. Keep assumptions explicit for future changes.
     * 69. Keep behavior contracts easy to diff.
     * 70. Keep production constraints respected.
     * 71. Baseline protects read path behavior.
     * 72. Baseline protects delete path behavior.
     * 73. Baseline protects role exclusion behavior.
     * 74. Baseline protects repository invocation behavior.
     * 75. Objective: preserve admin service confidence.
     */

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
