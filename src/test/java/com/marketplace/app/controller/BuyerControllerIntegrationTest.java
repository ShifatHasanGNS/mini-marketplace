package com.marketplace.app.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketplace.app.entity.Coupon;
import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.repository.CouponRepository;
import com.marketplace.app.repository.OrderRepository;
import com.marketplace.app.repository.ProductRepository;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class BuyerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private CouponRepository couponRepository;

    @MockBean
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private Coupon testCoupon;
    private Order testOrder;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setPrice(99.99);
        testProduct.setSellerName("test_seller");
        testProduct.setOrigin("USA");
        testProduct.setPic("test.jpg");

        testCoupon = new Coupon();
        testCoupon.setCode("SAVE10");
        testCoupon.setDiscountPercentage(10);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerName("testbuyer");
        testOrder.setProductName("Test Product");
        testOrder.setPrice(89.99);
        testOrder.setSellerName("test_seller");
        testOrder.setDeliveryAddress("123 Test St");
        testOrder.setMobileNumber("555-1234");
        testOrder.setOrigin("USA");
        testOrder.setOrderDate(LocalDate.now());

        session = new MockHttpSession();
        List<Product> cart = new ArrayList<>();
        List<Double> finalPrices = new ArrayList<>();
        session.setAttribute("cart", cart);
        session.setAttribute("finalPrices", finalPrices);
        session.setAttribute("username", "testbuyer");
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void buyerDashboard_WithValidUsername_ShouldReturnDashboardView()
        throws Exception {
        // Arrange
        when(productRepository.findAll()).thenReturn(
            Arrays.asList(testProduct)
        );
        when(orderRepository.findByCustomerName("testbuyer")).thenReturn(
            Arrays.asList(testOrder)
        );

        // Act & Assert
        mockMvc
            .perform(get("/buyer/dashboard/testbuyer").session(session))
            .andExpect(status().isOk())
            .andExpect(view().name("buyer-dashboard"))
            .andExpect(model().attributeExists("products"))
            .andExpect(model().attributeExists("cart"))
            .andExpect(model().attributeExists("cartSize"));
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void addToCart_WithValidProduct_ShouldAddToCartAndRedirect()
        throws Exception {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(
            Optional.of(testProduct)
        );

        // Act & Assert
        mockMvc
            .perform(get("/buyer/add-to-cart/1/testbuyer").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buyer/cart/testbuyer"));

        // Verify cart in session
        MvcResult result = mockMvc
            .perform(
                get("/buyer/dashboard/testbuyer").session(session).with(csrf())
            )
            .andReturn();

        MockHttpSession updatedSession = (MockHttpSession) result
            .getRequest()
            .getSession();
        List<Product> cart = (List<Product>) updatedSession.getAttribute(
            "cart"
        );
        assert cart != null && cart.size() > 0;
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void removeFromCart_WithValidProduct_ShouldRemoveFromCart()
        throws Exception {
        // Arrange
        List<Product> cart = new ArrayList<>();
        cart.add(testProduct);
        session.setAttribute("cart", cart);

        List<Double> finalPrices = new ArrayList<>();
        finalPrices.add(99.99);
        session.setAttribute("finalPrices", finalPrices);

        // Act & Assert
        mockMvc
            .perform(
                get("/buyer/remove-from-cart/1/testbuyer").session(session)
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buyer/cart/testbuyer"));

        // Verify removal
        MvcResult result = mockMvc
            .perform(
                get("/buyer/dashboard/testbuyer").session(session).with(csrf())
            )
            .andReturn();

        MockHttpSession updatedSession = (MockHttpSession) result
            .getRequest()
            .getSession();
        List<Product> updatedCart = (List<Product>) updatedSession.getAttribute(
            "cart"
        );
        assert updatedCart != null && updatedCart.isEmpty();
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void checkCoupon_WithValidCoupon_ShouldReturnValidResponse()
        throws Exception {
        // Arrange
        when(couponRepository.findByCode("SAVE10")).thenReturn(testCoupon);

        // Act & Assert
        mockMvc
            .perform(get("/buyer/check-coupon").param("code", "SAVE10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(true))
            .andExpect(jsonPath("$.discount").value(10));
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void checkCoupon_WithInvalidCoupon_ShouldReturnInvalidResponse()
        throws Exception {
        // Arrange
        when(couponRepository.findByCode("INVALID")).thenReturn(null);

        // Act & Assert
        mockMvc
            .perform(get("/buyer/check-coupon").param("code", "INVALID"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.valid").value(false));
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void finalCheckout_WithValidData_ShouldProcessOrderAndRedirect()
        throws Exception {
        // Arrange
        List<Product> cart = Arrays.asList(testProduct);
        session.setAttribute("cart", cart);

        List<Double> finalPrices = Arrays.asList(99.99);
        session.setAttribute("finalPrices", finalPrices);

        when(couponRepository.findByCode("SAVE10")).thenReturn(testCoupon);
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc
            .perform(
                post("/buyer/checkout")
                    .with(csrf())
                    .session(session)
                    .param("username", "testbuyer")
                    .param("address", "123 Test St")
                    .param("mobile", "555-1234")
                    .param("couponCode", "SAVE10")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buyer/dashboard/testbuyer"))
            .andExpect(flash().attributeExists("success"));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void finalCheckout_WithEmptyCart_ShouldReturnError() throws Exception {
        // Arrange
        session.setAttribute("cart", new ArrayList<>());

        // Act & Assert
        mockMvc
            .perform(
                post("/buyer/checkout")
                    .with(csrf())
                    .session(session)
                    .param("username", "testbuyer")
                    .param("address", "123 Test St")
                    .param("mobile", "555-1234")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buyer/cart/testbuyer"))
            .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void finalCheckout_WithNoCoupon_ShouldProcessOrderWithoutDiscount()
        throws Exception {
        // Arrange
        List<Product> cart = Arrays.asList(testProduct);
        session.setAttribute("cart", cart);

        List<Double> finalPrices = Arrays.asList(99.99);
        session.setAttribute("finalPrices", finalPrices);

        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        // Act & Assert
        mockMvc
            .perform(
                post("/buyer/checkout")
                    .with(csrf())
                    .session(session)
                    .param("username", "testbuyer")
                    .param("address", "123 Test St")
                    .param("mobile", "555-1234")
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buyer/dashboard/testbuyer"))
            .andExpect(flash().attributeExists("success"));

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void buyerDashboard_WithoutAuthentication_ShouldRedirectToLogin()
        throws Exception {
        // Act & Assert - No @WithMockUser, so should redirect to login
        mockMvc
            .perform(get("/buyer/dashboard/testbuyer"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(username = "testbuyer", roles = { "BUYER" })
    void addToCart_WithInvalidProduct_ShouldHandleGracefully()
        throws Exception {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc
            .perform(get("/buyer/add-to-cart/999/testbuyer").session(session))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/buyer/cart/testbuyer"));

        // Cart should remain empty
        MvcResult result = mockMvc
            .perform(
                get("/buyer/dashboard/testbuyer").session(session).with(csrf())
            )
            .andReturn();

        MockHttpSession updatedSession = (MockHttpSession) result
            .getRequest()
            .getSession();
        List<Product> cart = (List<Product>) updatedSession.getAttribute(
            "cart"
        );
        assert cart != null && cart.isEmpty();
    }
}
