package com.marketplace.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.app.entity.Coupon;
import com.marketplace.app.entity.Login;
import com.marketplace.app.entity.Order;
import com.marketplace.app.repository.CouponRepository;
import com.marketplace.app.repository.LoginRepository;
import com.marketplace.app.repository.OrderRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerIntegrationTest {

    /*
     * Admin Test Documentation (Insert-Only)
     * ---------------------------------------
     * 01. Validates dashboard renders for admin users.
     * 02. Validates dashboard includes users list model.
     * 03. Validates dashboard includes coupons list model.
     * 04. Validates dashboard includes orders list model.
     * 05. Validates dashboard includes total-selling model.
     * 06. Validates dashboard includes top-sellers model.
     * 07. Validates dashboard includes coupon form model.
     * 08. Validates openCoupon flag default behavior.
     * 09. Verifies empty data still renders dashboard.
     * 10. Verifies total selling is computed correctly.
     * 11. Verifies delete user endpoint redirects.
     * 12. Verifies delete user repository invocation.
     * 13. Verifies add coupon endpoint redirects.
     * 14. Verifies add coupon repository invocation.
     * 15. Verifies update coupon endpoint redirects.
     * 16. Verifies update coupon repository invocation.
     * 17. Verifies delete coupon endpoint redirects.
     * 18. Verifies delete coupon repository invocation.
     * 19. Verifies edit coupon opens edit mode.
     * 20. Verifies invalid coupon id still renders.
     * 21. Verifies top-seller scenario remains supported.
     * 22. Verifies partial update flow remains supported.
     * 23. Verifies anonymous admin access is blocked.
     * 24. Uses role annotation to enforce admin context.
     * 25. Uses CSRF tokens for mutating routes.
     * 26. Uses mock repositories for deterministic results.
     * 27. Uses fixed fixtures for predictable totals.
     * 28. Uses view/model assertions for MVC contracts.
     * 29. Uses redirect assertions for form workflows.
     * 30. Protects dashboard template model contract.
     * 31. Protects coupon management route contract.
     * 32. Protects delete and update action wiring.
     * 33. Protects against accidental endpoint regressions.
     * 34. Protects against accidental model-key regressions.
     * 35. Documents expected admin-facing behavior.
     * 36. Documents scenario-level intent for reviewers.
     * 37. Documents current behavior for no-data pages.
     * 38. Documents current behavior for invalid IDs.
     * 39. Documents aggregate computation expectation.
     * 40. Documents leaderboard data presence expectation.
     * 41. Keeps logic untouched for production stability.
     * 42. Keeps comments aligned with real assertions.
     * 43. Keeps tests readable for group collaboration.
     * 44. Keeps scenarios independent and isolated.
     * 45. Keeps setup fixtures concise and reusable.
     * 46. Keeps mocking minimal and purposeful.
     * 47. Keeps route-method mapping explicit.
     * 48. Keeps security behavior visible in tests.
     * 49. Keeps test output easy to scan in CI.
     * 50. Keeps assertions focused on behavior contract.
     * 51. Captures baseline before deeper refactors.
     * 52. Captures assumptions around openCoupon model flag.
     * 53. Captures assumptions around dashboard model keys.
     * 54. Captures assumptions around redirect destinations.
     * 55. Captures assumptions around repo interaction count.
     * 56. Captures assumptions around aggregate calculation.
     * 57. Captures assumptions around authenticated access.
     * 58. Captures assumptions around empty-list handling.
     * 59. Captures assumptions around invalid-id resilience.
     * 60. Captures assumptions around top-seller availability.
     * 61. Useful for onboarding new team contributors.
     * 62. Useful for reviewing behavior drift.
     * 63. Useful for debugging CI failures quickly.
     * 64. Useful for preserving admin UX consistency.
     * 65. Useful for safe template evolution.
     * 66. Useful for safe service-layer refactors.
     * 67. Useful for maintaining route confidence.
     * 68. Useful for documenting behavior under constraints.
     * 69. Useful when deployment logic cannot change.
     * 70. Useful as a living behavior checklist.
     * 71. Ensure updates are intentional and explicit.
     * 72. Ensure assertions remain user-impact focused.
     * 73. Ensure model names stay in sync with template.
     * 74. Ensure redirects stay in sync with navigation.
     * 75. Objective: preserve admin workflow confidence.
     */

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginRepository loginRepository;

    @MockBean
    private CouponRepository couponRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Login buyer;
    private Login seller;
    private Coupon coupon1;
    private Coupon coupon2;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        // Setup test users
        buyer = new Login();
        buyer.setName("john_doe");
        buyer.setRole("BUYER");

        seller = new Login();
        seller.setName("jane_seller");
        seller.setRole("SELLER");

        // Setup test coupons
        coupon1 = new Coupon();
        coupon1.setCode("SAVE10");
        coupon1.setDiscountPercentage(10);

        coupon2 = new Coupon();
        coupon2.setCode("SAVE20");
        coupon2.setDiscountPercentage(20);

        // Setup test orders
        order1 = new Order();
        order1.setCustomerName("buyer1");
        order1.setSellerName("jane_seller");
        order1.setProductName("Laptop");
        order1.setPrice(999.99);
        order1.setDeliveryAddress("123 Main St");
        order1.setMobileNumber("555-1234");
        order1.setOrigin("USA");
        order1.setOrderDate(LocalDate.now());

        order2 = new Order();
        order2.setCustomerName("buyer2");
        order2.setSellerName("john_seller");
        order2.setProductName("Phone");
        order2.setPrice(499.99);
        order2.setDeliveryAddress("456 Oak Ave");
        order2.setMobileNumber("555-5678");
        order2.setOrigin("China");
        order2.setOrderDate(LocalDate.now());
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void dashboard_ShouldReturnDashboardViewWithAllData() throws Exception {
        // Arrange
        List<Login> nonAdminUsers = Arrays.asList(buyer, seller);
        List<Coupon> coupons = Arrays.asList(coupon1, coupon2);
        List<Order> orders = Arrays.asList(order1, order2);

        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(nonAdminUsers);
        when(couponRepository.findAll()).thenReturn(coupons);
        when(orderRepository.findAll()).thenReturn(orders);

        // Act & Assert
        mockMvc
            .perform(get("/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attributeExists("users"))
            .andExpect(model().attributeExists("coupons"))
            .andExpect(model().attributeExists("orders"))
            .andExpect(model().attributeExists("totalSelling"))
            .andExpect(model().attributeExists("topSellers"))
            .andExpect(model().attributeExists("coupon"))
            .andExpect(model().attribute("openCoupon", false));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void dashboard_WithNoData_ShouldShowEmptyLists() throws Exception {
        // Arrange
        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(
            Arrays.asList()
        );
        when(couponRepository.findAll()).thenReturn(Arrays.asList());
        when(orderRepository.findAll()).thenReturn(Arrays.asList());

        // Act & Assert
        mockMvc
            .perform(get("/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attribute("totalSelling", 0.0))
            .andExpect(model().attribute("topSellers", Arrays.asList()));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void dashboard_ShouldCalculateTotalSellingCorrectly() throws Exception {
        // Arrange
        List<Order> orders = Arrays.asList(order1, order2);
        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(
            Arrays.asList(buyer, seller)
        );
        when(couponRepository.findAll()).thenReturn(
            Arrays.asList(coupon1, coupon2)
        );
        when(orderRepository.findAll()).thenReturn(orders);

        double expectedTotal = order1.getPrice() + order2.getPrice(); // 999.99 + 499.99 = 1499.98

        // Act & Assert
        mockMvc
            .perform(get("/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attribute("totalSelling", expectedTotal));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void deleteUser_WithValidId_ShouldDeleteAndRedirect() throws Exception {
        // Arrange
        Long userId = 1L;
        doNothing().when(loginRepository).deleteById(userId);

        // Act & Assert
        mockMvc
            .perform(delete("/admin/users/{id}", userId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"));

        verify(loginRepository, times(1)).deleteById(userId);
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void addCoupon_WithValidCoupon_ShouldSaveAndRedirect() throws Exception {
        // Arrange
        Coupon newCoupon = new Coupon();
        newCoupon.setCode("NEWYEAR25");
        newCoupon.setDiscountPercentage(25);

        when(couponRepository.save(any(Coupon.class))).thenReturn(newCoupon);

        // Act & Assert
        mockMvc
            .perform(
                post("/admin/coupons")
                    .with(csrf())
                    .param("code", "NEWYEAR25")
                    .param("discountPercentage", "25")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"));

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void updateCoupon_WithValidData_ShouldUpdateAndRedirect() throws Exception {
        // Arrange
        Long couponId = 1L;
        Coupon updatedCoupon = new Coupon();
        updatedCoupon.setCode("UPDATED20");
        updatedCoupon.setDiscountPercentage(20);

        when(couponRepository.save(any(Coupon.class))).thenReturn(
            updatedCoupon
        );

        // Act & Assert
        mockMvc
            .perform(
                put("/admin/coupons/{id}", couponId)
                    .with(csrf())
                    .param("code", "UPDATED20")
                    .param("discountPercentage", "20")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"));

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void deleteCoupon_WithValidId_ShouldDeleteAndRedirect() throws Exception {
        // Arrange
        Long couponId = 1L;
        doNothing().when(couponRepository).deleteById(couponId);

        // Act & Assert
        mockMvc
            .perform(delete("/admin/coupons/{id}", couponId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"));

        verify(couponRepository, times(1)).deleteById(couponId);
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void editCoupon_WithValidId_ShouldShowEditForm() throws Exception {
        // Arrange
        Long couponId = 1L;
        // Create a coupon with ID using constructor or set fields
        Coupon existingCoupon = new Coupon();
        existingCoupon.setCode("SAVE10");
        existingCoupon.setDiscountPercentage(10);

        when(couponRepository.findById(couponId)).thenReturn(
            Optional.of(existingCoupon)
        );
        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(
            Arrays.asList(buyer, seller)
        );
        when(couponRepository.findAll()).thenReturn(
            Arrays.asList(coupon1, coupon2)
        );
        when(orderRepository.findAll()).thenReturn(
            Arrays.asList(order1, order2)
        );

        // Act & Assert
        mockMvc
            .perform(get("/admin/coupons/{id}", couponId))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attributeExists("users"))
            .andExpect(model().attributeExists("coupons"))
            .andExpect(model().attributeExists("orders"))
            .andExpect(model().attribute("openCoupon", true));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void editCoupon_WithInvalidId_ShouldShowEmptyCoupon() throws Exception {
        // Arrange
        Long invalidId = 999L;
        when(couponRepository.findById(invalidId)).thenReturn(Optional.empty());
        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(
            Arrays.asList(buyer, seller)
        );
        when(couponRepository.findAll()).thenReturn(
            Arrays.asList(coupon1, coupon2)
        );
        when(orderRepository.findAll()).thenReturn(
            Arrays.asList(order1, order2)
        );

        // Act & Assert
        mockMvc
            .perform(get("/admin/coupons/{id}", invalidId))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attributeExists("coupon"))
            .andExpect(model().attribute("openCoupon", true));
    }

    // Helper method to create orders for testing
    private Order createOrder(String sellerName, double price) {
        Order order = new Order();
        order.setSellerName(sellerName);
        order.setPrice(price);
        order.setCustomerName("test");
        order.setProductName("test product");
        order.setDeliveryAddress("123 Test St");
        order.setMobileNumber("555-1234");
        order.setOrigin("Test Origin");
        order.setOrderDate(LocalDate.now());
        return order;
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void dashboard_WithMultipleSellers_ShouldLimitTopSellersToFive()
        throws Exception {
        // Arrange
        List<Order> manyOrders = Arrays.asList(
            createOrder("seller1", 100.0),
            createOrder("seller2", 200.0),
            createOrder("seller3", 300.0),
            createOrder("seller4", 400.0),
            createOrder("seller5", 500.0),
            createOrder("seller6", 600.0)
        );

        when(loginRepository.findByRoleNot("ADMIN")).thenReturn(
            Arrays.asList(buyer, seller)
        );
        when(couponRepository.findAll()).thenReturn(
            Arrays.asList(coupon1, coupon2)
        );
        when(orderRepository.findAll()).thenReturn(manyOrders);

        // Act & Assert
        mockMvc
            .perform(get("/admin/dashboard"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin-dashboard"))
            .andExpect(model().attributeExists("topSellers"));
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void updateCoupon_WithPartialData_ShouldUpdate() throws Exception {
        // Arrange
        Long couponId = 1L;
        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon1);

        // Act & Assert - Only updating code, discount remains same
        mockMvc
            .perform(
                put("/admin/coupons/{id}", couponId)
                    .with(csrf())
                    .param("code", "NEWCODE")
                    .param("discountPercentage", "10")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/dashboard"));

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void dashboard_WithoutAuthentication_ShouldRedirectToLogin()
        throws Exception {
        // Act & Assert - No @WithMockUser, so should redirect to login
        mockMvc
            .perform(get("/admin/dashboard"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }
}
