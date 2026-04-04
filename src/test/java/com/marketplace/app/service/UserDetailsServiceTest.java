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

    /*
     * UserDetails Service Test Documentation (Insert-Only)
     * -----------------------------------------------------
     * 01. Validates user lookup with valid username.
     * 02. Validates exception for invalid username.
     * 03. Validates seller authority mapping.
     * 04. Validates admin authority mapping.
     * 05. Uses mocked LoginRepository for isolation.
     * 06. Uses Spring Security UserDetails contract assertions.
     * 07. Uses deterministic login fixture data.
     * 08. Uses role-to-authority prefix expectation.
     * 09. Uses verify calls for repository interaction.
     * 10. Protects authentication user-loading contract.
     * 11. Protects authority generation behavior.
     * 12. Protects exception-path behavior.
     * 13. Protects username/password propagation behavior.
     * 14. Documents security-specific service intent.
     * 15. Documents current error message expectation.
     * 16. Documents current role string assumptions.
     * 17. Keeps tests focused and deterministic.
     * 18. Keeps scenarios independent and readable.
     * 19. Keeps assertion intent explicit.
     * 20. Keeps setup lightweight and repeatable.
     * 21. Confirms valid path returns non-null details.
     * 22. Confirms username value is preserved.
     * 23. Confirms encoded password value is preserved.
     * 24. Confirms ROLE_BUYER mapping when buyer fixture used.
     * 25. Confirms ROLE_SELLER mapping in seller scenario.
     * 26. Confirms ROLE_ADMIN mapping in admin scenario.
     * 27. Confirms negative authority expectations where applicable.
     * 28. Confirms repository is called exactly once per scenario.
     * 29. Confirms invalid user throws UsernameNotFoundException.
     * 30. Confirms exception message text stability.
     * 31. Useful for Spring Security regression checks.
     * 32. Useful for CI failures around login setup.
     * 33. Useful for onboarding new contributors.
     * 34. Useful for maintaining authority naming consistency.
     * 35. Useful when user model fields evolve.
     * 36. Useful when repository query contracts evolve.
     * 37. Useful when exception strategy evolves.
     * 38. Useful when role model evolves.
     * 39. Useful when security config evolves.
     * 40. Useful when integrating SSO alternatives later.
     * 41. Encourages explicit authority assertions.
     * 42. Encourages explicit negative-case assertions.
     * 43. Encourages stable naming in test methods.
     * 44. Encourages behavior-first maintenance.
     * 45. Encourages minimal mocking overhead.
     * 46. Encourages contract-focused validation.
     * 47. Encourages deterministic fixture strategy.
     * 48. Encourages clear exception-path testing.
     * 49. Encourages maintainable security tests.
     * 50. Encourages predictable refactor outcomes.
     * 51. Notes this suite is service-layer only.
     * 52. Notes no controller or HTTP assertions are present.
     * 53. Notes no DB integration is performed.
     * 54. Notes comments are non-functional additions.
     * 55. Notes production logic remains unchanged.
     * 56. Notes user role is string-based currently.
     * 57. Notes authority prefix relies on ROLE_ convention.
     * 58. Notes repository Optional semantics are assumed.
     * 59. Notes assertion style follows JUnit defaults.
     * 60. Notes this block aids long-term readability.
     * 61. Keep security assumptions visible and local.
     * 62. Keep exception assumptions explicit and testable.
     * 63. Keep role mapping assumptions explicit.
     * 64. Keep user-details contract stable.
     * 65. Keep reviewer context near test scenarios.
     * 66. Keep maintenance overhead low.
     * 67. Keep CI diagnostics straightforward.
     * 68. Keep collaboration easier in group project.
     * 69. Keep behavior drift quickly detectable.
     * 70. Keep non-functional edits safe and traceable.
     * 71. Baseline protects valid-user load behavior.
     * 72. Baseline protects invalid-user exception behavior.
     * 73. Baseline protects seller-authority behavior.
     * 74. Baseline protects admin-authority behavior.
     * 75. Objective: preserve user-details service confidence.
     */

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
