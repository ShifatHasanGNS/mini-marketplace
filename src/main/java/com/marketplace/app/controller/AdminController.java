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
 * Controller responsible for handling all administrative operations
 * within the Mini Marketplace application.
 *
 * Features handled:
 * - Admin dashboard
 * - User management
 * - Coupon CRUD operations
 * - Sales overview and analytics
 *
 * Base route: /admin
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderRepository orderRepository;

    // =====================================================
    // BASE REDIRECTION
    // =====================================================

    /**
     * Redirects the base admin route to the dashboard page.
     *
     * @return redirect path to admin dashboard
     */
    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    // =====================================================
    // DASHBOARD ROUTES
    // =====================================================

    /**
     * Loads the main admin dashboard with users, coupons,
     * orders, sales data, and summary statistics.
     *
     * @param model Spring UI model
     * @return dashboard view
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        populateModel(model);
        model.addAttribute("activeSection", "dashboard");

        return "admin-dashboard";
    }

    // =====================================================
    // USER MANAGEMENT
    // =====================================================

    /**
     * Displays all registered non-admin users.
     *
     * @param model Spring UI model
     * @return admin dashboard with user section active
     */
    @GetMapping("/users")
    public String viewUsers(Model model) {

        populateModel(model);
        model.addAttribute("activeSection", "users");

        return "admin-dashboard";
    }

    /**
     * Deletes a user by their unique ID.
     *
     * @param id user ID
     * @return redirect to admin dashboard
     */
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {

        loginRepository.deleteById(id);

        return "redirect:/admin/dashboard";
    }

    // =====================================================
    // COUPON MANAGEMENT
    // =====================================================

    /**
     * Displays all available coupons.
     *
     * @param model Spring UI model
     * @return admin dashboard with coupon section active
     */
    @GetMapping("/coupons")
    public String viewCoupons(Model model) {

        populateModel(model);
        model.addAttribute("activeSection", "coupon");

        return "admin-dashboard";
    }

    /**
     * Creates and stores a new coupon.
     *
     * @param coupon coupon entity from form
     * @return redirect to admin dashboard
     */
    @PostMapping("/coupons")
    public String createCoupon(@ModelAttribute Coupon coupon) {

        couponRepository.save(coupon);

        return "redirect:/admin/dashboard";
    }

    /**
     * Loads coupon edit form for a specific coupon.
     *
     * @param id coupon ID
     * @param model Spring UI model
     * @return dashboard view with edit modal/form open
     */
    @GetMapping("/coupons/{id}")
    public String editCoupon(@PathVariable Long id, Model model) {

        populateModel(model);

        // Fetch coupon if exists, otherwise return empty coupon object
        Coupon coupon = couponRepository.findById(id).orElse(new Coupon());

        model.addAttribute("coupon", coupon);
        model.addAttribute("openCoupon", true);
        model.addAttribute("activeSection", "coupon");

        return "admin-dashboard";
    }

    /**
     * Updates an existing coupon.
     *
     * @param id coupon ID
     * @param coupon updated coupon data
     * @return redirect to admin dashboard
     */
    @PutMapping("/coupons/{id}")
    public String updateCoupon(
            @PathVariable Long id,
            @ModelAttribute Coupon coupon
    ) {

        coupon.setId(id);
        couponRepository.save(coupon);

        return "redirect:/admin/dashboard";
    }

    /**
     * Deletes a coupon by ID.
     *
     * @param id coupon ID
     * @return redirect to admin dashboard
     */
    @DeleteMapping("/coupons/{id}")
    public String deleteCoupon(@PathVariable Long id) {

        couponRepository.deleteById(id);

        return "redirect:/admin/dashboard";
    }

    // =====================================================
    // SALES MANAGEMENT
    // =====================================================

    /**
     * Displays sales summary and analytics section.
     *
     * @param model Spring UI model
     * @return admin dashboard with sales section active
     */
    @GetMapping("/sales")
    public String viewSales(Model model) {

        populateModel(model);
        model.addAttribute("activeSection", "sell");

        return "admin-dashboard";
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Populates the model with all required data for
     * admin dashboard rendering.
     *
     * Includes:
     * - users
     * - coupons
     * - orders
     * - total sales
     * - top sellers
     *
     * @param model Spring UI model
     */
    private void populateModel(Model model) {

        // Fetch all non-admin users
        List<Login> users = loginRepository.findByRoleNot("ADMIN");

        // Fetch all coupons
        List<Coupon> coupons = couponRepository.findAll();

        // Fetch all orders
        List<Order> orders = orderRepository.findAll();

        // Calculate total sales amount
        double totalSelling = orders
                .stream()
                .mapToDouble(Order::getPrice)
                .sum();

        // Aggregate seller-wise sales
        Map<String, Double> sellerMap = new HashMap<>();

        for (Order order : orders) {
            sellerMap.put(
                    order.getSellerName(),
                    sellerMap.getOrDefault(order.getSellerName(), 0.0)
                            + order.getPrice()
            );
        }

        // Sort sellers by total sales and keep top 5
        List<Map.Entry<String, Double>> topSellers = sellerMap
                .entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        // Add all required attributes to view model
        model.addAttribute("users", users);
        model.addAttribute("coupons", coupons);
        model.addAttribute("orders", orders);
        model.addAttribute("totalSelling", totalSelling);
        model.addAttribute("topSellers", topSellers);
        model.addAttribute("coupon", new Coupon());
        model.addAttribute("openCoupon", false);
    }
}