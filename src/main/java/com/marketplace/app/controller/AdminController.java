package com.marketplace.app.controller;

import com.marketplace.app.entity.Coupon;
import com.marketplace.app.entity.Login;
import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.repository.CouponRepository;
import com.marketplace.app.repository.LoginRepository;
import com.marketplace.app.repository.OrderRepository;
import com.marketplace.app.repository.ProductRepository;
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
 *
 * Base route: /admin
 *
 * @author Mini Marketplace Team
 * @version 1.0
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

    @Autowired
    private ProductRepository productRepository;

    /** Redirects base admin route to dashboard page */
    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    /** Displays main admin dashboard with system overview */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "dashboard");
        return "admin-dashboard";
    }

    /** Displays user management section */
    @GetMapping("/users")
    public String viewUsers(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "users");
        return "admin-dashboard";
    }

    /**
     * Deletes a user account by ID.
     * Redirects back to the Users section so the admin stays in context.
     */
    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        loginRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    /** Displays coupon management section */
    @GetMapping("/coupons")
    public String viewCoupons(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "coupon");
        return "admin-dashboard";
    }

    /**
     * Creates and saves a new coupon.
     * Redirects back to the Coupons section.
     */
    @PostMapping("/coupons")
    public String createCoupon(@ModelAttribute Coupon coupon) {
        couponRepository.save(coupon);
        return "redirect:/admin/dashboard";
    }

    /** Loads the coupon edit form */
    @GetMapping("/coupons/{id}")
    public String editCoupon(@PathVariable Long id, Model model) {
        populateModel(model);
        Coupon coupon = couponRepository.findById(id).orElse(new Coupon());
        model.addAttribute("coupon", coupon);
        model.addAttribute("openCoupon", true);
        model.addAttribute("activeSection", "coupon");
        return "admin-dashboard";
    }

    /**
     * Updates an existing coupon.
     * Redirects back to the Coupons section.
     */
    @PutMapping("/coupons/{id}")
    public String updateCoupon(@PathVariable Long id, @ModelAttribute Coupon coupon) {
        coupon.setId(id);
        couponRepository.save(coupon);
        return "redirect:/admin/dashboard";
    }

    /**
     * Deletes a coupon by ID.
     * Redirects back to the Coupons section.
     */
    @DeleteMapping("/coupons/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    /** Displays sales and analytics section */
    @GetMapping("/sales")
    public String viewSales(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "sell");
        return "admin-dashboard";
    }

    /** Displays all products section */
    @GetMapping("/products")
    public String viewProducts(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "products");
        return "admin-dashboard";
    }

    /** Creates a new product (admin can assign to any seller) */
    @PostMapping("/products")
    public String createProduct(@ModelAttribute Product product) {
        productRepository.save(product);
        return "redirect:/admin/dashboard";
    }

    /** Loads the product edit form */
    @GetMapping("/products/{id}")
    public String editProduct(@PathVariable Long id, Model model) {
        populateModel(model);
        Product product = productRepository.findById(id).orElse(new Product());
        model.addAttribute("editProduct", product);
        model.addAttribute("openProduct", true);
        model.addAttribute("activeSection", "products");
        return "admin-dashboard";
    }

    /** Updates an existing product */
    @PutMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute Product product) {
        product.setId(id);
        productRepository.save(product);
        return "redirect:/admin/dashboard";
    }

    /** Deletes a product by ID */
    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    /**
     * Helper – populates the model with all dashboard data.
     */
    private void populateModel(Model model) {
        List<Login> users = loginRepository.findByRoleNot("ADMIN");
        List<Coupon> coupons = couponRepository.findAll();
        List<Order> orders = orderRepository.findAll();

        double totalSelling = orders.stream().mapToDouble(Order::getPrice).sum();

        Map<String, Double> sellerMap = new HashMap<>();
        for (Order order : orders) {
            sellerMap.put(
                    order.getSellerName(),
                    sellerMap.getOrDefault(order.getSellerName(), 0.0) + order.getPrice()
            );
        }

        List<Map.Entry<String, Double>> topSellers = sellerMap.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        model.addAttribute("users", users);
        model.addAttribute("coupons", coupons);
        model.addAttribute("orders", orders);
        model.addAttribute("totalSelling", totalSelling);
        model.addAttribute("topSellers", topSellers);
        model.addAttribute("coupon", new Coupon());
        model.addAttribute("openCoupon", false);
        model.addAttribute("allProducts", productRepository.findAll());
        model.addAttribute("editProduct", new Product());
        model.addAttribute("openProduct", false);
    }
}
