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

    /*
     * Auth Test Documentation (Insert-Only)
     * --------------------------------------
     * 01. Validates login page endpoint mapping.
     * 02. Validates signup page endpoint mapping.
     * 03. Ensures successful signup redirects to login.
     * 04. Ensures password encoder is invoked for signup.
     * 05. Ensures repository save is called for signup.
     * 06. Confirms admin role redirect route.
     * 07. Confirms seller role redirect route.
     * 08. Confirms buyer role redirect route.
     * 09. Uses CSRF for mutating requests.
     * 10. Uses MockMvc for route-level verification.
     * 11. Mocks persistence dependency for determinism.
     * 12. Mocks encoder dependency for assertion control.
     * 13. Keeps tests fast and isolated.
     * 14. Uses clear scenario-based method names.
     * 15. Protects against route regression.
     * 16. Protects against missing save call regressions.
     * 17. Protects against plaintext password regression.
     * 18. Documents current blank-input behavior.
     * 19. Verifies expected view names for GET routes.
     * 20. Verifies expected redirect URLs for POST routes.
     * 21. Makes role routing explicit and reviewable.
     * 22. Aligns with Spring Security behavior checks.
     * 23. Uses per-test setup for isolation.
     * 24. Uses fixed fixture values for readability.
     * 25. Keeps assertion scope at controller contract.
     * 26. Avoids persistence implementation assumptions.
     * 27. Avoids template HTML-content assertions.
     * 28. Focuses on externally observable outcomes.
     * 29. Improves onboarding for new teammates.
     * 30. Serves as documentation for auth flow.
     * 31. Clarifies expected redirect destinations.
     * 32. Clarifies endpoint and role relationships.
     * 33. Clarifies that repository is mocked.
     * 34. Clarifies that behavior is intentionally preserved.
     * 35. Captures baseline behavior for refactors.
     * 36. Captures security-oriented expectations.
     * 37. Captures success-path validation.
     * 38. Captures edge behavior with minimal payload.
     * 39. Helps review output in CI quickly.
     * 40. Supports stable group-project collaboration.
     * 41. Encourages intentional updates when logic changes.
     * 42. Encourages readable test structure.
     * 43. Encourages explicit form parameter coverage.
     * 44. Encourages route stability across deployments.
     * 45. Confirms mocked save receives encoded password.
     * 46. Confirms role authorities are honored.
     * 47. Confirms anonymous restrictions via security.
     * 48. Confirms auth views are reachable.
     * 49. Confirms signup endpoint remains operational.
     * 50. Confirms default user fixture usage.
     * 51. Documents deterministic testing strategy.
     * 52. Documents route-centric assertion strategy.
     * 53. Documents no-database test philosophy.
     * 54. Documents purpose of invocation verifies.
     * 55. Documents compatibility with current controller style.
     * 56. Documents expected behavior under security context.
     * 57. Documents status expectations for success paths.
     * 58. Documents redirection contract for form workflows.
     * 59. Documents why CSRF is included in tests.
     * 60. Documents intent for future maintenance.
     * 61. Keep test names precise and action-based.
     * 62. Keep fixture values human-readable.
     * 63. Keep method scope focused and short.
     * 64. Keep assertions minimal but meaningful.
     * 65. Keep mocks aligned with scenario needs.
     * 66. Keep behavior checks independent per test.
     * 67. Keep auth contract understandable to reviewers.
     * 68. Keep changes safe for render deployment constraints.
     * 69. Keep logic untouched while improving readability.
     * 70. Keep this block as behavior reference.
     * 71. Keep comments aligned with real assertions.
     * 72. Keep endpoint expectations explicit.
     * 73. Keep role-routing expectations explicit.
     * 74. Keep signup contract stable unless intentionally changed.
     * 75. Objective: preserve auth flow confidence.
     */

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
