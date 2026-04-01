package com.marketplace.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Coupon Entity
 * 
 * Represents discount coupons in the Mini Marketplace system.
 * Stores coupon details including discount percentage and validity period.
 * Coupons can be applied to orders to provide discounts to customers.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Entity
@Table(name = "coupon")
public class Coupon {

    /**
     * Unique identifier for the coupon
     * Auto-generated primary key in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Coupon code that customers use to apply the discount
     */
    private String code;

    /**
     * Discount percentage offered by this coupon
     */
    private int discountPercentage;

    /**
     * Date until which the coupon is valid
     */
    private LocalDate validUntil;

    /**
     * Gets the coupon ID
     * 
     * @return the unique coupon identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the coupon ID
     * 
     * @param id the coupon identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the coupon code
     * 
     * @return the coupon code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the coupon code
     * 
     * @param code the coupon code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the discount percentage
     * 
     * @return the discount percentage
     */
    public int getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Sets the discount percentage
     * 
     * @param discountPercentage the discount percentage to set
     */
    public void setDiscountPercentage(int discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    /**
     * Gets the coupon validity date
     * 
     * @return the date until which the coupon is valid
     */
    public LocalDate getValidUntil() {
        return validUntil;
    }

    /**
     * Sets the coupon validity date
     * 
     * @param validUntil the validity date to set
     */
    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }
}
