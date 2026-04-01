package com.marketplace.app.repository;

import com.marketplace.app.entity.Order;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * OrderRepository
 * 
 * Data access object for Order entity.
 * Provides methods for CRUD operations and custom queries for order analytics and retrieval.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders by seller name
     * 
     * @param sellerName the seller's username
     * @return list of orders from this seller
     */
    List<Order> findBySellerName(String sellerName);

    /**
     * Finds all orders by customer name
     * 
     * @param customerName the customer's username
     * @return list of orders placed by this customer
     */
    List<Order> findByCustomerName(String customerName);

    /**
     * Calculates total amount spent by a specific buyer
     * Uses JPQL query to sum order prices
     * 
     * @param username the buyer's username
     * @return total amount spent, or 0 if no orders
     */
    @Query(
            "SELECT COALESCE(SUM(o.price), 0) FROM Order o WHERE o.customerName = :username"
    )
    Double totalSpentByBuyer(@Param("username") String username);

    /**
     * Finds top sellers for a specific buyer
     * Returns seller name and total amount spent per seller
     * Ordered by total amount in descending order
     * 
     * @param username the buyer's username
     * @return list of maps with seller name and total spent
     */
    @Query(
            "SELECT o.sellerName AS seller, SUM(o.price) AS total " +
            "FROM Order o WHERE o.customerName = :username " +
            "GROUP BY o.sellerName ORDER BY total DESC"
    )
    List<Map<String, Object>> topSellersByBuyer(
            @Param("username") String username
    );
}
