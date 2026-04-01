package com.marketplace.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MiniMarketplaceApplication
 * 
 * Main entry point for the Mini Marketplace Spring Boot application.
 * Initializes and starts the application with Spring Boot auto-configuration.
 * 
 * The Mini Marketplace is an e-commerce platform that supports:
 * - User authentication (Admin, Seller, Buyer roles)
 * - Product catalog management by sellers
 * - Shopping cart and order placement by buyers
 * - Discount coupon system
 * - Sales analytics and reporting
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@SpringBootApplication
public class MiniMarketplaceApplication {

    /**
     * Main method to start the Spring Boot application
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(MiniMarketplaceApplication.class, args);
    }
}
