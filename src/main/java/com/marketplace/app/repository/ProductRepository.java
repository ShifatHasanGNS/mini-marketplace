package com.marketplace.app.repository;

import com.marketplace.app.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ProductRepository
 * 
 * Data access object for Product entity.
 * Provides methods for CRUD operations and custom queries on Product data.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products by seller name
     * 
     * @param sellerName the seller's username
     * @return list of products from this seller
     */
    List<Product> findBySellerName(String sellerName);
}
