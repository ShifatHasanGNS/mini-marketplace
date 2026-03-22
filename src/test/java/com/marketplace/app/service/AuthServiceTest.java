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
