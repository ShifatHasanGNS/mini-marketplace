package com.marketplace.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginRepository loginRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private Login testUser;

    @BeforeEach
    void setUp() {
        testUser = new Login();
        testUser.setName("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole("BUYER");
    }

    @Test
    void loginPage_ShouldReturnLoginView() throws Exception {
        mockMvc
            .perform(get("/login"))
            .andExpect(status().isOk())
            .andExpect(view().name("login"));
    }

    @Test
    void signupPage_ShouldReturnSignupView() throws Exception {
        mockMvc
            .perform(get("/signup"))
            .andExpect(status().isOk())
            .andExpect(view().name("signup"));
    }

    @Test
    void signup_WithValidData_ShouldRedirectToLogin() throws Exception {
        // Arrange
        when(passwordEncoder.encode("password123")).thenReturn(
            "encodedPassword123"
        );
        when(loginRepository.save(any(Login.class))).thenReturn(testUser);

        // Act & Assert
        mockMvc
            .perform(
                post("/signup")
                    .with(csrf())
                    .param("name", "newuser")
                    .param("password", "password123")
                    .param("role", "BUYER")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        verify(passwordEncoder, times(1)).encode("password123");
        verify(loginRepository, times(1)).save(any(Login.class));
    }

    @Test
    @WithMockUser(username = "admin", authorities = { "ROLE_ADMIN" })
    void redirectDashboard_WithAdminRole_ShouldRedirectToAdminDashboard()
        throws Exception {
        mockMvc
            .perform(get("/redirect-dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @WithMockUser(username = "seller", authorities = { "ROLE_SELLER" })
    void redirectDashboard_WithSellerRole_ShouldRedirectToSellerDashboard()
        throws Exception {
        mockMvc
            .perform(get("/redirect-dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/seller"));
    }

    @Test
    @WithMockUser(username = "buyer", authorities = { "ROLE_BUYER" })
    void redirectDashboard_WithBuyerRole_ShouldRedirectToBuyerDashboard()
        throws Exception {
        mockMvc
            .perform(get("/redirect-dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buyer/dashboard/buyer"));
    }

    @Test
    void signup_ShouldEncodePassword() throws Exception {
        // Arrange
        when(passwordEncoder.encode("password123")).thenReturn("encoded123");
        when(loginRepository.save(any(Login.class))).thenAnswer(invocation -> {
            Login savedLogin = invocation.getArgument(0);
            assert savedLogin.getPassword().equals("encoded123");
            return savedLogin;
        });

        // Act & Assert
        mockMvc
            .perform(
                post("/signup")
                    .with(csrf())
                    .param("name", "testuser")
                    .param("password", "password123")
                    .param("role", "BUYER")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        verify(passwordEncoder, times(1)).encode("password123");
        verify(loginRepository, times(1)).save(any(Login.class));
    }

    @Test
    void signup_WithNullValues_ShouldStillRedirect() throws Exception {
        // Act & Assert
        mockMvc
            .perform(post("/signup").with(csrf()).param("name", ""))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));

        verify(loginRepository, times(1)).save(any(Login.class));
    }
}
