package com.marketplace.app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Order Entity
 * 
 * Represents a customer order in the Mini Marketplace system.
 * Captures order details including customer information, product details, pricing, and delivery information.
 * Supports discount application through coupon codes.
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Entity
@Table(name = "orders")
public class Order {

    /**
     * Unique identifier for the order
     * Auto-generated primary key in the database
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the customer who placed the order
     */
    private String customerName;

    /**
     * Delivery address for the order
     */
    private String deliveryAddress;

    /**
     * Customer's mobile number for contact
     */
    private String mobileNumber;

    /**
     * Name of the product being ordered
     */
    private String productName;

    /**
     * Origin/source of the product
     */
    private String origin;

    /**
     * Price of the product
     */
    private double price;

    /**
     * Name of the seller providing the product
     */
    private String sellerName;

    /**
     * Discount percentage applied to the order through coupon
     */
    private Double discountPercentage;

    /**
     * Coupon code used for discount in the order
     */
    private String couponCode;

    /**
     * Date when the order was placed
     * Defaults to current date at creation
     */
    private LocalDate orderDate = LocalDate.now();

    /**
     * Default constructor required by JPA for entity instantiation
     */
    public Order() {
    }

    /**
     * Gets the order ID
     * 
     * @return the unique order identifier
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the order ID
     * 
     * @param id the order identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the customer name
     * 
     * @return the customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customer name
     * 
     * @param customerName the customer name to set
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the delivery address
     * 
     * @return the delivery address
     */
    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * Sets the delivery address
     * 
     * @param deliveryAddress the delivery address to set
     */
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    /**
     * Gets the customer's mobile number
     * 
     * @return the mobile number
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * Sets the customer's mobile number
     * 
     * @param mobileNumber the mobile number to set
     */
    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    /**
     * Gets the product name
     * 
     * @return the product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the product name
     * 
     * @param productName the product name to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Gets the product origin
     * 
     * @return the product origin
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Sets the product origin
     * 
     * @param origin the product origin to set
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Gets the product price
     * 
     * @return the product price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the product price
     * 
     * @param price the product price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Gets the seller name
     * 
     * @return the seller name
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * Sets the seller name
     * 
     * @param sellerName the seller name to set
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    /**
     * Gets the discount percentage
     * 
     * @return the discount percentage
     */
    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    /**
     * Sets the discount percentage
     * 
     * @param discountPercentage the discount percentage to set
     */
    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    /**
     * Gets the coupon code used in the order
     * 
     * @return the coupon code
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * Sets the coupon code for the order
     * 
     * @param couponCode the coupon code to set
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    /**
     * Gets the order date
     * 
     * @return the date when order was placed
     */
    public LocalDate getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the order date
     * 
     * @param orderDate the order date to set
     */
    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }
}
