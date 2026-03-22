package com.marketplace.app.repository;

import com.marketplace.app.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find products by seller name
    List<Product> findBySellerName(String sellerName);
}
