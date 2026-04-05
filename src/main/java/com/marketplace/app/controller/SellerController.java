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
 * Handles all seller-related operations in the Mini Marketplace system.
 * Manages seller dashboard, product operations (CRUD), and order management.
 * 
 * Base URL: /seller
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Controller
@RequestMapping("/seller")
public class SellerController {

    /**
     * Service layer for seller operations
     */
    private final SellerService sellerService;

    /**
     * Constructor to initialize SellerController with service dependency
     * 
     * @param sellerService the seller service
     */
    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /**
     * Displays the seller dashboard with all relevant information
     * 
     * @param username the seller's username from the URL path
     * @param model    the Spring model for passing data to the template
     * @return the seller dashboard template
     */
    @GetMapping("/dashboard/{username}")
    public String dashboard(@PathVariable String username, Model model) {
        // Populate model with seller data
        populateModel(username, model);
        
        // Set active section for UI navigation
        model.addAttribute("activeSection", "dashboard");
        
        return "seller-dashboard";
    }

    /**
     * Displays the seller's products view
     * 
     * @param username the seller's username from the URL path
     * @param model    the Spring model for passing data to the template
     * @return the seller dashboard template with products section active
     */
    @GetMapping("/products/{username}")
    public String products(@PathVariable String username, Model model) {
        // Populate model with seller data
        populateModel(username, model);
        
        // Set active section to products
        model.addAttribute("activeSection", "products");
        
        return "seller-dashboard";
    }

    /**
     * Displays the seller's orders view
     * 
     * @param username the seller's username from the URL path
     * @param model    the Spring model for passing data to the template
     * @return the seller dashboard template with orders section active
     */
    @GetMapping("/orders/{username}")
    public String orders(@PathVariable String username, Model model) {
        // Populate model with seller data
        populateModel(username, model);
        
        // Set active section to orders
        model.addAttribute("activeSection", "orders");
        
        return "seller-dashboard";
    }

    /**
     * Handles new product creation
     * Requires authenticated user with seller session
     * 
     * @param product the Product entity from form submission
     * @param session the HttpSession containing seller information
     * @return redirect to seller dashboard or login if not authenticated
     */
    @PostMapping("/products")
    public String addProduct(@ModelAttribute Product product, HttpSession session) {
        // Retrieve seller username from session
        String sellerName = (String) session.getAttribute("username");
        
        // Redirect to login if session is invalid
        if (sellerName == null) {
            return "redirect:/login";
        }
        
        // Set the seller for this product
        product.setSellerName(sellerName);
        
        // Save product through service layer
        sellerService.addProduct(product);
        
        // Redirect to dashboard after successful creation
        return "redirect:/seller/dashboard/" + sellerName;
    }

    /**
     * Handles product update operation
     * Updates existing product with new information
     * 
     * @param id      the product ID to update
     * @param product the Product entity with updated information
     * @param session the HttpSession containing seller information
     * @return redirect to seller dashboard or login if not authenticated
     */
    @PutMapping("/products/{id}")
    public String editProduct(
            @PathVariable Long id,
            @ModelAttribute Product product,
            HttpSession session
    ) {
        // Retrieve seller username from session
        String sellerName = (String) session.getAttribute("username");
        
        // Redirect to login if session is invalid
        if (sellerName == null) {
            return "redirect:/login";
        }
        
        // Set product ID and seller for the update
        product.setId(id);
        product.setSellerName(sellerName);
        
        // Update product through service layer
        sellerService.updateProduct(product);
        
        // Redirect to dashboard after successful update
        return "redirect:/seller/dashboard/" + sellerName;
    }

    /**
     * Handles product deletion
     * Removes product from the marketplace
     * 
     * @param id      the product ID to delete
     * @param session the HttpSession containing seller information
     * @return redirect to seller dashboard or login if not authenticated
     */
    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        // Retrieve seller username from session
        String sellerName = (String) session.getAttribute("username");
        
        // Redirect to login if session is invalid
        if (sellerName == null) {
            return "redirect:/login";
        }
        
        // Delete product through service layer
        sellerService.deleteProduct(id);
        
        // Redirect to dashboard after successful deletion
        return "redirect:/seller/dashboard/" + sellerName;
    }

    /**
     * Helper method to populate model with common seller dashboard data
     * Fetches seller's products, orders, and calculates summary statistics
     * 
     * @param username the seller's username
     * @param model    the Spring model to populate with data
     */
    private void populateModel(String username, Model model) {
        // Fetch all products for this seller
        List<Product> products = sellerService.getProductsBySellerName(username);
        model.addAttribute("products", products);
        
        // Fetch all orders for this seller
        List<Order> myOrders = sellerService.getOrdersBySellerName(username);
        model.addAttribute("myOrders", myOrders);
        
        // Calculate total sales revenue for this seller
        double totalSelling = myOrders
                .stream()
                .mapToDouble(Order::getPrice)
                .sum();
        model.addAttribute("totalSelling", totalSelling);
        
        // Add common attributes for template rendering
        model.addAttribute("sellerName", username);
        model.addAttribute("product", new Product());
    }
}