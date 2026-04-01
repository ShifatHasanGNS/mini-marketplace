package com.marketplace.app.repository;

import com.marketplace.app.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * CouponRepository
 * 
 * Data access object for Coupon entity.
 * Provides methods for CRUD operations and coupon lookup queries.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    /**
     * Finds a coupon by its code
     * Used for coupon validation during checkout
     * 
     * @param code the coupon code to search for
     * @return the Coupon entity if found, null otherwise
     */
    Coupon findByCode(String code);
}
