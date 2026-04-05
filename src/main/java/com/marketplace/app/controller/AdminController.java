package com.marketplace.app.controller;

import com.marketplace.app.entity.Coupon;
import com.marketplace.app.entity.Login;
import com.marketplace.app.entity.Order;
import com.marketplace.app.repository.CouponRepository;
import com.marketplace.app.repository.LoginRepository;
import com.marketplace.app.repository.OrderRepository;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * AdminController
 * 
 * Handles all administrative operations in the Mini Marketplace system.
 * Manages user accounts, coupon system, sales analytics, and system overview.
 * Provides comprehensive admin dashboard and data management endpoints.
 * 
 * Base route: /admin
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * Repository for user account operations
     */
    @Autowired
    private LoginRepository loginRepository;

    /**
     * Repository for coupon management
     */
    @Autowired
    private CouponRepository couponRepository;

    /**
     * Repository for order information and analytics
     */
    @Autowired
    private OrderRepository orderRepository;

    /**
     * Redirects base admin route to dashboard page
     * 
     * @return redirect to admin dashboard
     */
    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    /**
     * Displays main admin dashboard with system overview
     * Populates model with users, coupons, orders, and analytics
     * 
     * @param model the Spring model for view attributes
     * @return admin dashboard template
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Populate model with all dashboard data
        populateModel(model);
        
        // Set active section for UI navigation
        model.addAttribute("activeSection", "dashboard");
        
        return "admin-dashboard";
    }

    /**
     * Displays user management section
     * Shows all registered non-admin users
     * 
     * @param model the Spring model for view attributes
     * @return admin dashboard with users section active
     */
    @GetMapping("/users")
    public String viewUsers(Model model) {
        // Populate model with all dashboard data
        populateModel(model);
        
        // Set active section to users
        model.addAttribute("activeSection", "users");
        
        return "admin-dashboard";
    }

    /**
     * Deletes a user account by ID
     * Removes user from the system
     * 
     * @param id the user ID to delete
     * @return redirect to admin dashboard
     */
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        // Delete user from database
        loginRepository.deleteById(id);
        
        // Redirect to dashboard
        return "redirect:/admin/dashboard";
    }

    /**
     * Displays coupon management section
     * Shows all available coupons in the system
     * 
     * @param model the Spring model for view attributes
     * @return admin dashboard with coupon section active
     */
    @GetMapping("/coupons")
    public String viewCoupons(Model model) {
        // Populate model with all dashboard data
        populateModel(model);
        
        // Set active section to coupon
        model.addAttribute("activeSection", "coupon");
        
        return "admin-dashboard";
    }

    /**
     * Creates and saves a new coupon
     * Allows admin to create discount coupons for customers
     * 
     * @param coupon the Coupon entity from form submission
     * @return redirect to admin dashboard
     */
    @PostMapping("/coupons")
    public String createCoupon(@ModelAttribute Coupon coupon) {
        // Save new coupon to database
        couponRepository.save(coupon);
        
        // Redirect to dashboard
        return "redirect:/admin/dashboard";
    }

    /**
     * Loads coupon edit form
     * Retrieves specific coupon for editing
     * 
     * @param id    the coupon ID to edit
     * @param model the Spring model for view attributes
     * @return admin dashboard with coupon edit form open
     */
    @GetMapping("/coupons/{id}")
    public String editCoupon(@PathVariable Long id, Model model) {
        // Populate model with all dashboard data
        populateModel(model);
        
        // Fetch coupon for editing or create empty coupon
        Coupon coupon = couponRepository.findById(id).orElse(new Coupon());
        
        // Add coupon and form state to model
        model.addAttribute("coupon", coupon);
        model.addAttribute("openCoupon", true);
        model.addAttribute("activeSection", "coupon");
        
        return "admin-dashboard";
    }

    /**
     * Updates an existing coupon
     * Saves modified coupon information to database
     * 
     * @param id     the coupon ID to update
     * @param coupon the Coupon entity with updated information
     * @return redirect to admin dashboard
     */
    @PutMapping("/coupons/{id}")
    public String updateCoupon(
            @PathVariable Long id,
            @ModelAttribute Coupon coupon
    ) {
        // Set coupon ID and save updated coupon
        coupon.setId(id);
        couponRepository.save(coupon);
        
        // Redirect to dashboard
        return "redirect:/admin/dashboard";
    }

    /**
     * Deletes a coupon by ID
     * Removes coupon from the discount system
     * 
     * @param id the coupon ID to delete
     * @return redirect to admin dashboard
     */
    @DeleteMapping("/coupons/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        // Delete coupon from database
        couponRepository.deleteById(id);
        
        // Redirect to dashboard
        return "redirect:/admin/dashboard";
    }

    /**
     * Displays sales and analytics section
     * Shows sales summary and performance metrics
     * 
     * @param model the Spring model for view attributes
     * @return admin dashboard with sales section active
     */
    @GetMapping("/sales")
    public String viewSales(Model model) {
        // Populate model with all dashboard data
        populateModel(model);
        
        // Set active section to sales
        model.addAttribute("activeSection", "sell");
        
        return "admin-dashboard";
    }

    /**
     * Helper method to populate model with dashboard data
     * Aggregates all required information for admin dashboard rendering
     * Includes user statistics, coupon information, orders, and sales analytics
     * 
     * @param model the Spring model to populate
     */
    private void populateModel(Model model) {
        // Fetch all non-admin users from database
        List<Login> users = loginRepository.findByRoleNot("ADMIN");
        
        // Fetch all available coupons
        List<Coupon> coupons = couponRepository.findAll();
        
        // Fetch all orders from the system
        List<Order> orders = orderRepository.findAll();
        
        // Calculate total sales amount across all orders
        double totalSelling = orders
                .stream()
                .mapToDouble(Order::getPrice)
                .sum();
        
        // Aggregate sales by seller
        Map<String, Double> sellerMap = new HashMap<>();
        for (Order order : orders) {
            sellerMap.put(
                    order.getSellerName(),
                    sellerMap.getOrDefault(order.getSellerName(), 0.0) + order.getPrice()
            );
        }
        
        // Get top 5 sellers by total sales
        List<Map.Entry<String, Double>> topSellers = sellerMap
                .entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());
        
        // Add all attributes to model for template rendering
        model.addAttribute("users", users);
        model.addAttribute("coupons", coupons);
        model.addAttribute("orders", orders);
        model.addAttribute("totalSelling", totalSelling);
        model.addAttribute("topSellers", topSellers);
        model.addAttribute("coupon", new Coupon());
        model.addAttribute("openCoupon", false);
    }
}