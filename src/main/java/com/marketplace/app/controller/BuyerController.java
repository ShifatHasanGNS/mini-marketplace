package com.marketplace.app.controller;

import com.marketplace.app.entity.Coupon;
import com.marketplace.app.entity.Order;
import com.marketplace.app.entity.Product;
import com.marketplace.app.repository.CouponRepository;
import com.marketplace.app.repository.OrderRepository;
import com.marketplace.app.repository.ProductRepository;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * BuyerController
 * 
 * Handles all buyer-side operations in the Mini Marketplace system.
 * Manages buyer dashboard, product browsing, shopping cart, order placement, and coupon application.
 * 
 * Base route: /buyer
 * 
 * @author Mini Marketplace Team
 * @version 1.0
 */
@Controller
@RequestMapping("/buyer")
public class BuyerController {

    /**
     * Repository for product operations
     */
    private final ProductRepository productRepository;

    /**
     * Repository for coupon validation and lookup
     */
    private final CouponRepository couponRepository;

    /**
     * Repository for order management
     */
    private final OrderRepository orderRepository;

    /**
     * Constructor with constructor-based dependency injection
     * 
     * @param productRepository the ProductRepository
     * @param couponRepository  the CouponRepository
     * @param orderRepository   the OrderRepository
     */
    public BuyerController(
            ProductRepository productRepository,
            CouponRepository couponRepository,
            OrderRepository orderRepository
    ) {
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Displays buyer dashboard with available products and order information
     * Shows cart items, order history, total spending, and favorite sellers
     * 
     * @param username the buyer's username from URL path
     * @param success  optional success message parameter
     * @param model    the Spring model for view attributes
     * @param session  the HttpSession for storing cart data
     * @return buyer dashboard template
     */
    @GetMapping("/dashboard/{username}")
    public String dashboard(
            @PathVariable String username,
            @RequestParam(required = false) String success,
            Model model,
            HttpSession session
    ) {
        // Store username in session for template access
        session.setAttribute("username", username);
        
        // Retrieve or initialize shopping cart
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        session.setAttribute("cart", cart);
        model.addAttribute("cart", cart);
        
        // Fetch all available products for display
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        
        // Fetch buyer's order history
        List<Order> orders = orderRepository.findByCustomerName(username);
        
        // Calculate total amount spent by buyer
        double totalSpent = orders.stream().mapToDouble(Order::getPrice).sum();
        model.addAttribute("totalSpent", totalSpent);
        
        // Calculate top 3 sellers by purchase frequency
        Map<String, Integer> sellerCount = new HashMap<>();
        for (Order o : orders) {
            sellerCount.put(o.getSellerName(), sellerCount.getOrDefault(o.getSellerName(), 0) + 1);
        }
        List<String> topSellers = sellerCount.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
        model.addAttribute("topSellers", topSellers);
        
        // Add success message if order was placed
        if (success != null) {
            model.addAttribute("successMessage", "Order placed successfully!");
        }
        
        // Add common attributes to model
        model.addAttribute("username", username);
        model.addAttribute("cartSize", cart.size());
        
        return "buyer-dashboard";
    }

    /**
     * Displays product list for browsing
     * Shows all available products in the marketplace
     * 
     * @param username the buyer's username from URL path
     * @param model    the Spring model for view attributes
     * @param session  the HttpSession containing cart information
     * @return buyer dashboard with products section active
     */
    @GetMapping("/products/{username}")
    public String products(
            @PathVariable String username,
            Model model,
            HttpSession session
    ) {
        // Fetch all available products
        List<Product> products = productRepository.findAll();
        
        // Retrieve cart from session
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        // Add attributes to model
        model.addAttribute("products", products);
        model.addAttribute("cartSize", cart.size());
        model.addAttribute("username", username);
        
        return "buyer-dashboard";
    }

    /**
     * Displays the buyer's shopping cart
     * Shows all items currently in the cart
     * 
     * @param username the buyer's username from URL path
     * @param model    the Spring model for view attributes
     * @param session  the HttpSession containing cart data
     * @return buyer dashboard with cart section active
     */
    @GetMapping("/cart/{username}")
    public String cart(
            @PathVariable String username,
            Model model,
            HttpSession session
    ) {
        // Retrieve cart from session
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }
        
        // Add attributes to model
        model.addAttribute("cart", cart);
        model.addAttribute("username", username);
        model.addAttribute("cartSize", cart.size());
        
        return "buyer-dashboard";
    }

    /**
     * Adds a product to the shopping cart
     * Retrieves product by ID and adds it to the session cart
     * 
     * @param id       the product ID to add
     * @param username the buyer's username from URL path
     * @param session  the HttpSession for storing cart data
     * @return redirect to cart page
     */
    @GetMapping("/add-to-cart/{id}/{username}")
    public String addToCart(
            @PathVariable Long id,
            @PathVariable String username,
            HttpSession session
    ) {
        // Retrieve product by ID
        Product product = productRepository.findById(id).orElse(null);
        
        if (product != null) {
            // Get cart from session and add product
            List<Product> cart = (List<Product>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }
            cart.add(product);
            session.setAttribute("cart", cart);
        }
        
        // Redirect to cart page
        return "redirect:/buyer/cart/" + username;
    }

    /**
     * Removes a product from the shopping cart
     * Removes product by ID from session cart
     * 
     * @param id       the product ID to remove
     * @param username the buyer's username from URL path
     * @param session  the HttpSession for storing cart data
     * @return redirect to cart page
     */
    @GetMapping("/remove-from-cart/{id}/{username}")
    public String removeFromCart(
            @PathVariable Long id,
            @PathVariable String username,
            HttpSession session
    ) {
        // Retrieve cart from session
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        
        if (cart != null) {
            // Remove product with matching ID
            cart.removeIf(p -> p.getId().equals(id));
            session.setAttribute("cart", cart);
        }
        
        // Redirect to cart page
        return "redirect:/buyer/cart/" + username;
    }

    /**
     * Displays buyer's order history
     * Shows all orders placed by the buyer
     * 
     * @param username the buyer's username from URL path
     * @param model    the Spring model for view attributes
     * @return buyer dashboard with orders section active
     */
    @GetMapping("/orders/{username}")
    public String orderHistory(@PathVariable String username, Model model) {
        // Fetch all orders for this buyer
        List<Order> orders = orderRepository.findByCustomerName(username);
        
        // Add attributes to model
        model.addAttribute("orders", orders);
        model.addAttribute("username", username);
        
        return "buyer-dashboard";
    }

    /**
     * Validates coupon code and returns discount percentage
     * Checks if coupon exists and is valid
     * 
     * @param code the coupon code to validate
     * @return JSON response with validation status and discount percentage
     */
    @GetMapping("/check-coupon")
    @ResponseBody
    public Map<String, Object> checkCoupon(@RequestParam String code) {
        Map<String, Object> response = new HashMap<>();
        
        // Look up coupon by code
        Coupon coupon = couponRepository.findByCode(code);
        
        if (coupon != null) {
            // Coupon found - return valid status and discount
            response.put("valid", true);
            response.put("discount", coupon.getDiscountPercentage());
        } else {
            // Coupon not found
            response.put("valid", false);
        }
        
        return response;
    }

    /**
     * Processes checkout and order placement
     * Saves all cart items as orders with applied discount
     * Clears cart after successful checkout
     * 
     * @param username   the buyer's username
     * @param address    the delivery address
     * @param mobile     the buyer's mobile number
     * @param couponCode the coupon code (optional)
     * @param session    the HttpSession containing cart data
     * @param redirectAttributes for flash attributes
     * @return redirect to dashboard with success message
     */
    @PostMapping("/checkout")
    public String checkout(
            @RequestParam String username,
            @RequestParam String address,
            @RequestParam String mobile,
            @RequestParam(required = false) String couponCode,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // Retrieve cart from session
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        
        // Validate cart is not empty
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cart is empty");
            return "redirect:/buyer/cart/" + username;
        }
        
        // Calculate discount percentage
        double discount = 0;
        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponRepository.findByCode(couponCode);
            if (coupon != null) {
                discount = coupon.getDiscountPercentage();
            }
        }
        
        // Save order for each product in cart
        for (Product p : cart) {
            // Calculate final price with discount applied
            double finalPrice = p.getPrice() - ((p.getPrice() * discount) / 100);
            
            // Create new order
            Order order = new Order();
            order.setCustomerName(username);
            order.setDeliveryAddress(address);
            order.setMobileNumber(mobile);
            order.setProductName(p.getName());
            order.setOrigin(p.getOrigin());
            order.setPrice(finalPrice);
            order.setSellerName(p.getSellerName());
            order.setDiscountPercentage(discount);
            order.setOrderDate(LocalDate.now());
            
            // Save order to database
            orderRepository.save(order);
        }
        
        // Clear cart after successful checkout
        session.removeAttribute("cart");
        
        // Redirect to dashboard with success message
        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
        return "redirect:/buyer/dashboard/" + username + "?success=true";
    }
}