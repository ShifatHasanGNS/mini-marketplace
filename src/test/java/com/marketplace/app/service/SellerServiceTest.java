package com.marketplace.app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.repository.OrderRepository;
import com.marketplace.app.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SellerServiceTest {

    /*
     * Seller Service Test Documentation (Insert-Only)
     * ------------------------------------------------
     * 01. Validates product creation through repository save.
     * 02. Validates retrieval of all products.
     * 03. Validates empty product list handling.
     * 04. Validates seller-specific product retrieval.
     * 05. Validates missing seller product retrieval.
     * 06. Validates product lookup by valid id.
     * 07. Validates product lookup by invalid id.
     * 08. Validates product update behavior.
     * 09. Validates product delete delegation.
     * 10. Validates seller-specific order retrieval.
     * 11. Validates empty order list handling.
     * 12. Uses mocked repositories for isolation.
     * 13. Uses realistic fixture data for readability.
     * 14. Uses assertion set for values and size.
     * 15. Uses verify counts for collaboration checks.
     * 16. Protects seller service contract integrity.
     * 17. Protects repository method wiring.
     * 18. Protects optional-return behavior assumptions.
     * 19. Protects list-return behavior assumptions.
     * 20. Documents behavior as stable baseline.
     * 21. Confirms saved product fields remain intact.
     * 22. Confirms updated product reflects new values.
     * 23. Confirms seller filter is applied correctly.
     * 24. Confirms delete uses provided product id.
     * 25. Confirms order filter by seller name.
     * 26. Confirms non-null result contracts.
     * 27. Confirms size expectations where deterministic.
     * 28. Confirms empty collections in no-data paths.
     * 29. Confirms optional presence for valid ids.
     * 30. Confirms optional emptiness for invalid ids.
     * 31. Useful for maintaining seller workflows.
     * 32. Useful for reducing regression risk.
     * 33. Useful for CI diagnostics and intent clarity.
     * 34. Useful for onboarding new project members.
     * 35. Useful when repository API evolves.
     * 36. Useful when product model evolves.
     * 37. Useful when order model evolves.
     * 38. Useful when introducing validation rules.
     * 39. Useful when introducing pagination later.
     * 40. Useful when introducing sorting later.
     * 41. Encourages explicit seller identity checks.
     * 42. Encourages readable fixture naming.
     * 43. Encourages independent scenario tests.
     * 44. Encourages minimal yet strong assertions.
     * 45. Encourages deterministic behavior checks.
     * 46. Encourages clean separation from controller tests.
     * 47. Encourages service-focused validation scope.
     * 48. Encourages stable contracts for callers.
     * 49. Encourages predictable refactor outcomes.
     * 50. Encourages confidence before deployment.
     * 51. Notes test scope excludes transaction semantics.
     * 52. Notes test scope excludes HTTP semantics.
     * 53. Notes repository responses are mock-driven.
     * 54. Notes comments are non-functional additions.
     * 55. Notes logic remains intentionally unchanged.
     * 56. Notes no persistence integration runs here.
     * 57. Notes this suite is fast by design.
     * 58. Notes this suite is deterministic by design.
     * 59. Notes assertions reflect current contract.
     * 60. Notes updates should be intentional if logic changes.
     * 61. Keep service expectations transparent.
     * 62. Keep product flow assumptions explicit.
     * 63. Keep order flow assumptions explicit.
     * 64. Keep repository interactions auditable.
     * 65. Keep test output easy to interpret.
     * 66. Keep maintenance overhead manageable.
     * 67. Keep group collaboration smooth.
     * 68. Keep behavior drift easy to detect.
     * 69. Keep readability improvements non-invasive.
     * 70. Keep deployment constraints respected.
     * 71. Baseline protects product add behavior.
     * 72. Baseline protects product query behavior.
     * 73. Baseline protects product update behavior.
     * 74. Baseline protects order query behavior.
     * 75. Objective: preserve seller service confidence.
     */

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private SellerService sellerService;

    private Product product1;
    private Product product2;
    private Order order1;
    private Order order2;

    @BeforeEach
    void setUp() {
        product1 = new Product();
        product1.setId(1L);
        product1.setName("Laptop");
        product1.setSellerName("tech_seller");
        product1.setPrice(999.99);
        product1.setOrigin("USA");
        product1.setPic("laptop.jpg");

        product2 = new Product();
        product2.setId(2L);
        product2.setName("Mouse");
        product2.setSellerName("tech_seller");
        product2.setPrice(29.99);
        product2.setOrigin("China");
        product2.setPic("mouse.jpg");

        order1 = new Order();
        order1.setId(1L);
        order1.setSellerName("tech_seller");
        order1.setProductName("Laptop");
        order1.setPrice(999.99);
        order1.setCustomerName("John Doe");
        order1.setDeliveryAddress("123 Main St");
        order1.setMobileNumber("555-1234");
        order1.setOrigin("USA");
        order1.setDiscountPercentage(0.0);

        order2 = new Order();
        order2.setId(2L);
        order2.setSellerName("tech_seller");
        order2.setProductName("Mouse");
        order2.setPrice(29.99);
        order2.setCustomerName("Jane Smith");
        order2.setDeliveryAddress("456 Oak Ave");
        order2.setMobileNumber("555-5678");
        order2.setOrigin("China");
        order2.setDiscountPercentage(10.0);
    }

    // Product Tests
    @Test
    void addProduct_WithValidProduct_ShouldReturnSavedProduct() {
        // Arrange
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // Act
        Product result = sellerService.addProduct(product1);

        // Assert
        assertNotNull(result);
        assertEquals(product1.getId(), result.getId());
        assertEquals(product1.getName(), result.getName());
        assertEquals(product1.getSellerName(), result.getSellerName());
        assertEquals(product1.getPrice(), result.getPrice());
        assertEquals(product1.getOrigin(), result.getOrigin());
        assertEquals(product1.getPic(), result.getPic());
        verify(productRepository, times(1)).save(product1);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> result = sellerService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_WhenNoProducts_ShouldReturnEmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<Product> result = sellerService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductsBySellerName_WithValidSeller_ShouldReturnProducts() {
        // Arrange
        String sellerName = "tech_seller";
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findBySellerName(sellerName)).thenReturn(
            expectedProducts
        );

        // Act
        List<Product> result = sellerService.getProductsBySellerName(
            sellerName
        );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(
            result.stream().allMatch(p -> p.getSellerName().equals(sellerName))
        );
        verify(productRepository, times(1)).findBySellerName(sellerName);
    }

    @Test
    void getProductsBySellerName_WithNoProducts_ShouldReturnEmptyList() {
        // Arrange
        String sellerName = "unknown_seller";
        when(productRepository.findBySellerName(sellerName)).thenReturn(
            Arrays.asList()
        );

        // Act
        List<Product> result = sellerService.getProductsBySellerName(
            sellerName
        );

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findBySellerName(sellerName);
    }

    @Test
    void getProductById_WithValidId_ShouldReturnProduct() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(
            Optional.of(product1)
        );

        // Act
        Optional<Product> result = sellerService.getProductById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(product1.getId(), result.get().getId());
        assertEquals(product1.getName(), result.get().getName());
        assertEquals(product1.getPrice(), result.get().getPrice());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProductById_WithInvalidId_ShouldReturnEmptyOptional() {
        // Arrange
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(
            Optional.empty()
        );

        // Act
        Optional<Product> result = sellerService.getProductById(productId);

        // Assert
        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void updateProduct_WithValidProduct_ShouldReturnUpdatedProduct() {
        // Arrange
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Gaming Laptop");
        updatedProduct.setPrice(1299.99);
        updatedProduct.setSellerName("tech_seller");
        updatedProduct.setOrigin("USA");
        updatedProduct.setPic("gaming_laptop.jpg");

        when(productRepository.save(any(Product.class))).thenReturn(
            updatedProduct
        );

        // Act
        Product result = sellerService.updateProduct(updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals(updatedProduct.getId(), result.getId());
        assertEquals("Gaming Laptop", result.getName());
        assertEquals(1299.99, result.getPrice());
        assertEquals("USA", result.getOrigin());
        assertEquals("gaming_laptop.jpg", result.getPic());
        verify(productRepository, times(1)).save(updatedProduct);
    }

    @Test
    void deleteProduct_WithValidId_ShouldCallRepositoryDelete() {
        // Arrange
        Long productId = 1L;
        doNothing().when(productRepository).deleteById(productId);

        // Act
        sellerService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }

    // Order Tests
    @Test
    void getOrdersBySellerName_WithValidSeller_ShouldReturnOrders() {
        // Arrange
        String sellerName = "tech_seller";
        List<Order> expectedOrders = Arrays.asList(order1, order2);
        when(orderRepository.findBySellerName(sellerName)).thenReturn(
            expectedOrders
        );

        // Act
        List<Order> result = sellerService.getOrdersBySellerName(sellerName);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(
            result.stream().allMatch(o -> o.getSellerName().equals(sellerName))
        );
        verify(orderRepository, times(1)).findBySellerName(sellerName);
    }

    @Test
    void getOrdersBySellerName_WithNoOrders_ShouldReturnEmptyList() {
        // Arrange
        String sellerName = "new_seller";
        when(orderRepository.findBySellerName(sellerName)).thenReturn(
            Arrays.asList()
        );

        // Act
        List<Order> result = sellerService.getOrdersBySellerName(sellerName);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository, times(1)).findBySellerName(sellerName);
    }
}
