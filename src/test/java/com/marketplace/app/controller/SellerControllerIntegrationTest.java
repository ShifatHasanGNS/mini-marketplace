package com.marketplace.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.service.SellerService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class SellerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;
    private Product testProduct;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("username", "test_seller");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setSellerName("test_seller");
        testProduct.setOrigin("USA");
        testProduct.setPic("test.jpg");

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setSellerName("test_seller");
        testOrder.setProductName("Test Product");
        testOrder.setPrice(99.99);
        testOrder.setCustomerName("buyer1");
        testOrder.setDeliveryAddress("123 Test St");
        testOrder.setMobileNumber("555-1234");
        testOrder.setOrigin("USA");
        testOrder.setOrderDate(LocalDate.now());
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void sellerDashboard_WithValidUsername_ShouldReturnDashboardView()
        throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        List<Order> orders = Arrays.asList(testOrder);

        when(sellerService.getProductsBySellerName("test_seller")).thenReturn(
            products
        );
        when(sellerService.getOrdersBySellerName("test_seller")).thenReturn(
            orders
        );

        // Act & Assert
        mockMvc
            .perform(get("/seller/dashboard/test_seller").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("seller-dashboard"))
            .andExpect(model().attributeExists("products"))
            .andExpect(model().attributeExists("myOrders"))
            .andExpect(model().attributeExists("totalSelling"))
            .andExpect(model().attributeExists("sellerName"))
            .andExpect(model().attribute("sellerName", "test_seller"));
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void addProduct_WithValidProduct_ShouldAddAndRedirect() throws Exception {
        // Arrange
        when(sellerService.addProduct(any(Product.class))).thenReturn(
            testProduct
        );

        // Act & Assert
        mockMvc
            .perform(
                post("/seller/add-product")
                    .with(csrf())
                    .session(session)
                    .param("name", "New Product")
                    .param("price", "49.99")
                    .param("origin", "China")
                    .param("pic", "new.jpg")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/test_seller"));

        verify(sellerService, times(1)).addProduct(any(Product.class));
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void editProduct_WithValidData_ShouldUpdateAndRedirect() throws Exception {
        // Arrange
        when(sellerService.updateProduct(any(Product.class))).thenReturn(
            testProduct
        );

        // Act & Assert
        mockMvc
            .perform(
                post("/seller/edit-product/1")
                    .with(csrf())
                    .session(session)
                    .param("name", "Updated Product")
                    .param("price", "79.99")
                    .param("origin", "USA")
                    .param("pic", "updated.jpg")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/test_seller"));

        verify(sellerService, times(1)).updateProduct(any(Product.class));
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void deleteProduct_WithValidId_ShouldDeleteAndRedirect() throws Exception {
        // Arrange
        doNothing().when(sellerService).deleteProduct(1L);

        // Act & Assert
        mockMvc
            .perform(get("/seller/delete-product/1").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/test_seller"));

        verify(sellerService, times(1)).deleteProduct(1L);
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void sellerDashboard_WithNoProducts_ShouldShowEmptyLists()
        throws Exception {
        // Arrange
        when(sellerService.getProductsBySellerName("test_seller")).thenReturn(
            Arrays.asList()
        );
        when(sellerService.getOrdersBySellerName("test_seller")).thenReturn(
            Arrays.asList()
        );

        // Act & Assert
        mockMvc
            .perform(get("/seller/dashboard/test_seller").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("seller-dashboard"))
            .andExpect(model().attribute("products", Arrays.asList()))
            .andExpect(model().attribute("myOrders", Arrays.asList()))
            .andExpect(model().attribute("totalSelling", 0.0));
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void addProduct_WithMissingFields_ShouldHandleError() throws Exception {
        // Act & Assert - Spring will handle missing fields based on form binding
        // The controller will still redirect even with missing fields
        mockMvc
            .perform(
                post("/seller/add-product")
                    .with(csrf())
                    .session(session)
                    .param("name", "New Product")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/test_seller"));

        // Verify that service was still called (with null values for missing fields)
        verify(sellerService, times(1)).addProduct(any(Product.class));
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void sellerDashboard_WithMultipleOrders_ShouldCalculateTotalCorrectly()
        throws Exception {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setSellerName("test_seller");
        order2.setProductName("Another Product");
        order2.setPrice(49.99);
        order2.setCustomerName("buyer2");
        order2.setDeliveryAddress("456 Oak Ave");
        order2.setMobileNumber("555-5678");
        order2.setOrigin("China");
        order2.setOrderDate(LocalDate.now());

        List<Order> orders = Arrays.asList(testOrder, order2);

        when(sellerService.getProductsBySellerName("test_seller")).thenReturn(
            products
        );
        when(sellerService.getOrdersBySellerName("test_seller")).thenReturn(
            orders
        );

        // Act & Assert
        mockMvc
            .perform(get("/seller/dashboard/test_seller").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("seller-dashboard"))
            .andExpect(model().attribute("totalSelling", 149.98)); // 99.99 + 49.99
    }

    @Test
    void sellerDashboard_WithoutAuthentication_ShouldRedirectToLogin()
        throws Exception {
        // Act & Assert - No @WithMockUser, so should redirect to login
        mockMvc
            .perform(get("/seller/dashboard/test_seller"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void addProduct_WithEmptyPrice_ShouldStillRedirect() throws Exception {
        // Act & Assert
        mockMvc
            .perform(
                post("/seller/add-product")
                    .with(csrf())
                    .session(session)
                    .param("name", "New Product")
                    .param("origin", "China")
                    .param("pic", "new.jpg")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/test_seller"));

        verify(sellerService, times(1)).addProduct(any(Product.class));
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void editProduct_WithPartialData_ShouldStillUpdate() throws Exception {
        // Act & Assert - Only updating name, other fields remain
        mockMvc
            .perform(
                post("/seller/edit-product/1")
                    .with(csrf())
                    .session(session)
                    .param("name", "Updated Name Only")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/test_seller"));

        verify(sellerService, times(1)).updateProduct(any(Product.class));
    }

    @Test
    @WithMockUser(username = "different_seller", roles = { "SELLER" })
    void sellerDashboard_WithDifferentSeller_ShouldShowDifferentData()
        throws Exception {
        // Arrange
        String differentSeller = "different_seller";
        session.setAttribute("username", differentSeller);

        Product differentProduct = new Product();
        differentProduct.setId(2L);
        differentProduct.setName("Different Product");
        differentProduct.setPrice(199.99);
        differentProduct.setSellerName(differentSeller);
        differentProduct.setOrigin("Japan");
        differentProduct.setPic("different.jpg");

        when(sellerService.getProductsBySellerName(differentSeller)).thenReturn(
            Arrays.asList(differentProduct)
        );
        when(sellerService.getOrdersBySellerName(differentSeller)).thenReturn(
            Arrays.asList()
        );

        // Act & Assert
        mockMvc
            .perform(
                get("/seller/dashboard/{username}", differentSeller).session(
                    session
                )
            )
            .andExpect(status().isOk())
            .andExpect(view().name("seller-dashboard"))
            .andExpect(model().attribute("sellerName", differentSeller))
            .andExpect(
                model().attribute("products", Arrays.asList(differentProduct))
            );
    }

    @Test
    @WithMockUser(username = "test_seller", roles = { "SELLER" })
    void deleteProduct_WithInvalidId_ShouldStillRedirect() throws Exception {
        // Arrange
        doNothing().when(sellerService).deleteProduct(999L);

        // Act & Assert
        mockMvc
            .perform(get("/seller/delete-product/999").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/seller/dashboard/test_seller"));

        verify(sellerService, times(1)).deleteProduct(999L);
    }
}
