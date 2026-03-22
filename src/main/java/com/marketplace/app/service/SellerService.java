package com.marketplace.app.service;

import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.repository.OrderRepository;
import com.marketplace.app.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SellerService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public SellerService(
        ProductRepository productRepository,
        OrderRepository orderRepository
    ) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // ===== Products =====
    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsBySellerName(String sellerName) {
        return productRepository.findBySellerName(sellerName);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // ===== Orders =====
    public List<Order> getOrdersBySellerName(String sellerName) {
        return orderRepository.findBySellerName(sellerName);
    }

    public double getTotalSalesBySeller(String sellerName) {
        return orderRepository
            .findBySellerName(sellerName)
            .stream()
            .mapToDouble(Order::getPrice)
            .sum();
    }
}
