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

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    /** Displays the seller dashboard */
    @GetMapping("/dashboard/{username}")
    public String dashboard(@PathVariable String username, Model model) {
        populateModel(username, model);
        model.addAttribute("activeSection", "dashboard");
        return "seller-dashboard";
    }

    /** Displays the seller's products view */
    @GetMapping("/products/{username}")
    public String products(@PathVariable String username, Model model) {
        populateModel(username, model);
        model.addAttribute("activeSection", "products");
        return "seller-dashboard";
    }

    /** Displays the seller's orders view */
    @GetMapping("/orders/{username}")
    public String orders(@PathVariable String username, Model model) {
        populateModel(username, model);
        model.addAttribute("activeSection", "orders");
        return "seller-dashboard";
    }

    /**
     * Adds a new product.
     * Redirects to the Products section so the seller can see it immediately.
     */
    @PostMapping("/products")
    public String addProduct(@ModelAttribute Product product, HttpSession session) {
        String sellerName = (String) session.getAttribute("username");
        if (sellerName == null) {
            return "redirect:/login";
        }
        product.setSellerName(sellerName);
        sellerService.addProduct(product);
        return "redirect:/seller/dashboard/" + sellerName;
    }

    /**
     * Updates an existing product.
     * Redirects to the Products section.
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

    /**
     * Deletes a product.
     * Redirects to the Products section.
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

    /** Populates the model with seller dashboard data */
    private void populateModel(String username, Model model) {
        List<Product> products = sellerService.getProductsBySellerName(username);
        model.addAttribute("products", products);

        List<Order> myOrders = sellerService.getOrdersBySellerName(username);
        model.addAttribute("myOrders", myOrders);

        double totalSelling = myOrders.stream().mapToDouble(Order::getPrice).sum();
        model.addAttribute("totalSelling", totalSelling);

        model.addAttribute("sellerName", username);
        model.addAttribute("product", new Product());
    }
}
