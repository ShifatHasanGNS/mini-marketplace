package com.marketplace.app.controller;

import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.service.SellerService;

import jakarta.servlet.http.HttpSession;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * SellerController
 *
 * Handles all seller-related operations for Mini Marketplace:
 * - Seller dashboard
 * - Products management (add, update, delete)
 * - Orders management
 *
 * Base URL: /seller
 */
@Controller
@RequestMapping("/seller")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // ===================== DASHBOARD =====================

    /**
     * Seller dashboard view
     */
    @GetMapping("/dashboard/{username}")
    public String dashboard(@PathVariable String username, Model model) {

        populateModel(username, model);
        model.addAttribute("activeSection", "dashboard");

        return "seller-dashboard";
    }

    // ===================== PRODUCTS =====================

    /**
     * View seller products
     */
    @GetMapping("/products/{username}")
    public String products(@PathVariable String username, Model model) {

        populateModel(username, model);
        model.addAttribute("activeSection", "products");

        return "seller-dashboard";
    }

    // ===================== ORDERS =====================

    /**
     * View seller orders
     */
    @GetMapping("/orders/{username}")
    public String orders(@PathVariable String username, Model model) {

        populateModel(username, model);
        model.addAttribute("activeSection", "orders");

        return "seller-dashboard";
    }

    // ===================== ADD PRODUCT =====================

    /**
     * Add a new product
     */
    @PostMapping("/products")
    public String addProduct(
            @ModelAttribute Product product,
            HttpSession session
    ) {

        String sellerName = (String) session.getAttribute("username");

        if (sellerName == null) {
            return "redirect:/login";
        }

        product.setSellerName(sellerName);
        sellerService.addProduct(product);

        return "redirect:/seller/dashboard/" + sellerName;
    }

    // ===================== UPDATE PRODUCT =====================

    /**
     * Update an existing product
     */
    @PutMapping("/products/{id}")
    public String editProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            HttpSession session
    ) {

        String sellerName = (String) session.getAttribute("username");

        if (sellerName == null) {
            return "redirect:/login";
        }

        product.setId(id);
        product.setSellerName(sellerName);

        sellerService.updateProduct(product);

        return "redirect:/seller/dashboard/" + sellerName;
    }

    // ===================== DELETE PRODUCT =====================

    /**
     * Delete a product by ID
     */
    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {

        String sellerName = (String) session.getAttribute("username");

        if (sellerName == null) {
            return "redirect:/login";
        }

        sellerService.deleteProduct(id);

        return "redirect:/seller/dashboard/" + sellerName;
    }

    // ===================== HELPER METHOD =====================

    /**
     * Populates common data for seller dashboard
     */
    private void populateModel(String username, Model model) {

        // Fetch seller products
        List<Product> products = sellerService.getProductsBySellerName(username);
        model.addAttribute("products", products);

        // Fetch seller orders
        List<Order> myOrders = sellerService.getOrdersBySellerName(username);
        model.addAttribute("myOrders", myOrders);

        // Calculate total selling amount
        double totalSelling = myOrders
                .stream()
                .mapToDouble(Order::getPrice)
                .sum();
        model.addAttribute("totalSelling", totalSelling);

        // Common attributes
        model.addAttribute("sellerName", username);
        model.addAttribute("product", new Product());
    }
}