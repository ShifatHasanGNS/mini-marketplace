package com.marketplace.app.repository;

import com.marketplace.app.entity.Order;
import java.util.List;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Existing methods
    List<Order> findBySellerName(String sellerName);
    List<Order> findByCustomerName(String customerName);

    // Total amount spent by a specific buyer
    @Query(
        "SELECT COALESCE(SUM(o.price), 0) FROM Order o WHERE o.customerName = :username"
    )
    Double totalSpentByBuyer(@Param("username") String username);

    // Top sellers for a specific buyer (seller name and total spent)
    @Query(
        "SELECT o.sellerName AS seller, SUM(o.price) AS total " +
            "FROM Order o WHERE o.customerName = :username " +
            "GROUP BY o.sellerName ORDER BY total DESC"
    )
    List<Map<String, Object>> topSellersByBuyer(
        @Param("username") String username
    );
}
