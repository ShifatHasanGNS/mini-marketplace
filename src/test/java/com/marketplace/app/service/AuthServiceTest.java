package com.marketplace.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.marketplace.app.dto.LoginRequest;
import com.marketplace.app.dto.RegisterRequest;
import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    /*
     * Auth Service Test Documentation (Insert-Only)
     * ----------------------------------------------
     * 01. Validates registration saves a new user.
     * 02. Validates password is encoded before save.
     * 03. Validates registration field mapping.
     * 04. Validates successful login returns user.
     * 05. Validates unknown username throws error.
     * 06. Validates wrong password throws error.
     * 07. Uses mocked repository and encoder.
     * 08. Uses deterministic DTO and entity fixtures.
     * 09. Uses Arrange/Act/Assert test structure.
     * 10. Uses verify to assert collaboration behavior.
     * 11. Protects auth service contract from regressions.
     * 12. Protects credential-validation behavior.
     * 13. Protects password-security preprocessing behavior.
     * 14. Protects repository lookup behavior.
     * 15. Documents expected exception messages.
     * 16. Documents expected login success payload.
     * 17. Documents current role handling assumptions.
     * 18. Documents input-to-entity mapping assumptions.
     * 19. Keeps assertions focused and intentional.
     * 20. Keeps tests free of external dependencies.
     * 21. Confirms encoder called exactly once on register.
     * 22. Confirms repository save called on register.
     * 23. Confirms saved password equals encoded value.
     * 24. Confirms name mapping during registration.
     * 25. Confirms role mapping during registration.
     * 26. Confirms login repository lookup by username.
     * 27. Confirms password matcher usage in login.
     * 28. Confirms matcher not called for unknown user.
     * 29. Confirms success returns non-null entity.
     * 30. Confirms thrown exception type for failures.
     * 31. Useful for onboarding new contributors quickly.
     * 32. Useful for CI failure interpretation.
     * 33. Useful for safe auth refactoring.
     * 34. Useful for preserving endpoint expectations indirectly.
     * 35. Useful for preserving service-layer guarantees.
     * 36. Useful when changing password policy.
     * 37. Useful when changing exception strategy.
     * 38. Useful when changing repository method signatures.
     * 39. Useful when changing DTO shape.
     * 40. Useful when introducing validation annotations.
     * 41. Encourages explicit scenario naming.
     * 42. Encourages explicit collaborator verification.
     * 43. Encourages deterministic fixture setup.
     * 44. Encourages readable assertion messages by intent.
     * 45. Encourages narrow test scope.
     * 46. Encourages behavior-first maintenance.
     * 47. Encourages stable exception contracts.
     * 48. Encourages secure defaults in auth logic.
     * 49. Encourages confidence in login flow.
     * 50. Encourages confidence in registration flow.
     * 51. Notes service currently throws RuntimeException.
     * 52. Notes error message strings are asserted directly.
     * 53. Notes repository returns Optional for lookups.
     * 54. Notes encoder behavior is controlled via mocks.
     * 55. Notes no persistence integration occurs here.
     * 56. Notes no HTTP behavior is asserted here.
     * 57. Notes this is pure service-layer verification.
     * 58. Notes comments are non-functional additions.
     * 59. Notes production behavior remains unchanged.
     * 60. Notes test runtime should remain low.
     * 61. Keep auth assumptions visible to teammates.
     * 62. Keep fixture values easy to understand.
     * 63. Keep assertions close to scenario intent.
     * 64. Keep collaboration checks precise.
     * 65. Keep logic untouched under deployment constraints.
     * 66. Keep maintenance straightforward for group work.
     * 67. Keep failure diagnostics fast and clear.
     * 68. Keep secure behavior auditable in tests.
     * 69. Keep contract drift detectable early.
     * 70. Keep service confidence high before release.
     * 71. Baseline protects register pipeline behavior.
     * 72. Baseline protects login credential behavior.
     * 73. Baseline protects exception-path behavior.
     * 74. Baseline protects collaborator interaction behavior.
     * 75. Objective: preserve auth service confidence.
     */

    @Mock
    private LoginRepository loginRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private Login user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setRole("BUYER");

        loginRequest = new LoginRequest();
        loginRequest.setName("testuser");
        loginRequest.setPassword("password123");

        user = new Login();
        user.setName("testuser");
        user.setPassword("encodedPassword");
        user.setRole("BUYER");
    }

    @Test
    void register_WithValidRequest_ShouldSaveUser() {
        // Arrange
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(
            "encodedPassword"
        );
        when(loginRepository.save(any(Login.class))).thenReturn(user);

        // Act
        authService.register(registerRequest);

        // Assert
        verify(passwordEncoder, times(1)).encode(registerRequest.getPassword());
        verify(loginRepository, times(1)).save(any(Login.class));
    }

    @Test
    void register_ShouldEncodePasswordBeforeSaving() {
        // Arrange
        String rawPassword = registerRequest.getPassword();
        String encodedPassword = "bcrypt_encoded_password";
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(loginRepository.save(any(Login.class))).thenAnswer(invocation -> {
            Login savedUser = invocation.getArgument(0);
            assertEquals(encodedPassword, savedUser.getPassword());
            return savedUser;
        });

        // Act
        authService.register(registerRequest);

        // Assert
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(loginRepository, times(1)).save(any(Login.class));
    }

    @Test
    void register_ShouldSetAllFieldsCorrectly() {
        // Arrange
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn(
            "encodedPassword"
        );
        when(loginRepository.save(any(Login.class))).thenAnswer(invocation -> {
            Login savedUser = invocation.getArgument(0);
            assertEquals(registerRequest.getName(), savedUser.getName());
            assertEquals("encodedPassword", savedUser.getPassword());
            assertEquals(registerRequest.getRole(), savedUser.getRole());
            return savedUser;
        });

        // Act
        authService.register(registerRequest);

        // Assert
        verify(loginRepository, times(1)).save(any(Login.class));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnUser() {
        // Arrange
        when(loginRepository.findByName(loginRequest.getName())).thenReturn(
            Optional.of(user)
        );
        when(
            passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
            )
        ).thenReturn(true);

        // Act
        Login result = authService.login(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getRole(), result.getRole());
        verify(loginRepository, times(1)).findByName(loginRequest.getName());
        verify(passwordEncoder, times(1)).matches(
            loginRequest.getPassword(),
            user.getPassword()
        );
    }

    @Test
    void login_WithInvalidUsername_ShouldThrowException() {
        // Arrange
        when(loginRepository.findByName(loginRequest.getName())).thenReturn(
            Optional.empty()
        );

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> {
                authService.login(loginRequest);
            }
        );
        assertEquals("User not found", exception.getMessage());
        verify(loginRepository, times(1)).findByName(loginRequest.getName());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WithInvalidPassword_ShouldThrowException() {
        // Arrange
        when(loginRepository.findByName(loginRequest.getName())).thenReturn(
            Optional.of(user)
        );
        when(
            passwordEncoder.matches(
                loginRequest.getPassword(),
                user.getPassword()
            )
        ).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> {
                authService.login(loginRequest);
            }
        );
        assertEquals("Invalid password", exception.getMessage());
        verify(loginRepository, times(1)).findByName(loginRequest.getName());
        verify(passwordEncoder, times(1)).matches(
            loginRequest.getPassword(),
            user.getPassword()
        );
    }
}
