package com.marketplace.app.controller;

import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.service.SellerService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/seller")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    // ================= DASHBOARD =================
    @GetMapping("/dashboard/{username}")
    public String dashboard(@PathVariable String username, Model model) {
        populateModel(username, model);
        model.addAttribute("activeSection", "dashboard");

        return "seller-dashboard";
    }

    // ================= PRODUCTS =================
    @GetMapping("/products/{username}")
    public String products(@PathVariable String username, Model model) {
        populateModel(username, model);
        model.addAttribute("activeSection", "products");

        return "seller-dashboard";
    }

    // ================= ORDERS =================
    @GetMapping("/orders/{username}")
    public String orders(@PathVariable String username, Model model) {
        populateModel(username, model);
        model.addAttribute("activeSection", "orders");

        return "seller-dashboard";
    }

    // ================= ADD PRODUCT =================
    @PostMapping("/products")
    public String addProduct(
        @ModelAttribute Product product,
        HttpSession session
    ) {
        String sellerName = (String) session.getAttribute("username");
        product.setSellerName(sellerName);

        sellerService.addProduct(product);

        return "redirect:/seller/products/" + sellerName;
    }

    // ================= UPDATE PRODUCT =================
    @PutMapping("/products/{id}")
    public String editProduct(
        @PathVariable Long id,
        @ModelAttribute Product product,
        HttpSession session
    ) {
        String sellerName = (String) session.getAttribute("username");

        product.setId(id);
        product.setSellerName(sellerName);

        sellerService.updateProduct(product);

        return "redirect:/seller/products/" + sellerName;
    }

    // ================= DELETE PRODUCT =================
    @DeleteMapping("/products/{id}")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        String sellerName = (String) session.getAttribute("username");

        sellerService.deleteProduct(id);

        return "redirect:/seller/products/" + sellerName;
    }

    // ================= HELPER =================
    private void populateModel(String username, Model model) {
        List<Product> products = sellerService.getProductsBySellerName(
            username
        );
        model.addAttribute("products", products);

        List<Order> myOrders = sellerService.getOrdersBySellerName(username);
        model.addAttribute("myOrders", myOrders);

        double totalSelling = myOrders
            .stream()
            .mapToDouble(Order::getPrice)
            .sum();
        model.addAttribute("totalSelling", totalSelling);

        model.addAttribute("sellerName", username);
        model.addAttribute("product", new Product());
    }
}
