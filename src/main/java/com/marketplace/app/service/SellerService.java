package com.marketplace.app.service;

import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.repository.OrderRepository;
import com.marketplace.app.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * SellerService
 * 
 * Handles all seller-related business operations in the Mini Marketplace.
 * Manages product catalog (CRUD operations) and order management for sellers.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Service
public class SellerService {

    /**
     * Repository for product operations
     */
    private final ProductRepository productRepository;

    /**
     * Repository for order operations
     */
    private final OrderRepository orderRepository;

    /**
     * Constructor with dependency injection
     * 
     * @param productRepository the ProductRepository
     * @param orderRepository   the OrderRepository
     */
    public SellerService(
            ProductRepository productRepository,
            OrderRepository orderRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Adds a new product to the marketplace
     * 
     * @param product the Product entity to add
     * @return the saved Product entity
     */
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Retrieves all products from the marketplace
     * 
     * @return list of all products
     */
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Retrieves all products by a specific seller
     * 
     * @param sellerName the seller's username
     * @return list of products from this seller
     */
    public List<Product> getProductsBySellerName(String sellerName) {
        return productRepository.findBySellerName(sellerName);
    }

    /**
     * Retrieves a specific product by ID
     * 
     * @param id the product ID
     * @return Optional containing the product if found
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Updates an existing product
     * 
     * @param product the Product entity with updated information
     * @return the saved updated Product entity
     */
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Deletes a product from the marketplace
     * 
     * @param id the product ID to delete
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    /**
     * Retrieves all orders for a specific seller
     * 
     * @param sellerName the seller's username
     * @return list of orders for this seller
     */
    public List<Order> getOrdersBySellerName(String sellerName) {
        return orderRepository.findBySellerName(sellerName);
    }

    /**
     * Calculates total sales revenue for a seller
     * Sums the price of all orders from this seller
     * 
     * @param sellerName the seller's username
     * @return total sales amount
     */
    public double getTotalSalesBySeller(String sellerName) {
        return orderRepository
                .findBySellerName(sellerName)
                .stream()
                .mapToDouble(Order::getPrice)
                .sum();
    }
}
