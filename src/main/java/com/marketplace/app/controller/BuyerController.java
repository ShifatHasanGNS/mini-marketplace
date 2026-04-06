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

    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;

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
     * Displays buyer dashboard: stats, products, and order summary.
     * Cart/checkout is NOT shown here – the buyer must navigate to /buyer/cart to checkout.
     */
    @GetMapping("/dashboard/{username}")
    public String dashboard(
            @PathVariable String username,
            @RequestParam(required = false) String success,
            Model model,
            HttpSession session
    ) {
        session.setAttribute("username", username);

        // Cart size for sidebar badge only – do NOT add cart list to model here
        // so the empty checkout form does not appear on the main dashboard
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }

        // Products
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        // Order stats
        List<Order> orders = orderRepository.findByCustomerName(username);
        double totalSpent = orders.stream().mapToDouble(Order::getPrice).sum();
        model.addAttribute("totalSpent", totalSpent);

        Map<String, Integer> sellerCount = new HashMap<>();
        for (Order o : orders) {
            sellerCount.put(o.getSellerName(), sellerCount.getOrDefault(o.getSellerName(), 0) + 1);
        }
        List<String> topSellers = sellerCount.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();
        model.addAttribute("topSellers", topSellers);

        if (success != null) {
            model.addAttribute("successMessage", "Order placed successfully! Check your order history.");
        }

        model.addAttribute("username", username);
        model.addAttribute("cart", cart);
        model.addAttribute("cartSize", cart.size());

        return "buyer-dashboard";
    }

    /**
     * Displays all products for browsing.
     */
    @GetMapping("/products/{username}")
    public String products(
            @PathVariable String username,
            Model model,
            HttpSession session
    ) {
        List<Product> products = productRepository.findAll();

        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        model.addAttribute("products", products);
        model.addAttribute("cartSize", cart.size());
        model.addAttribute("username", username);

        return "buyer-dashboard";
    }

    /**
     * Displays the shopping cart and checkout form.
     */
    @GetMapping("/cart/{username}")
    public String cart(
            @PathVariable String username,
            Model model,
            HttpSession session
    ) {
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        model.addAttribute("cart", cart);
        model.addAttribute("username", username);
        model.addAttribute("cartSize", cart.size());

        return "buyer-dashboard";
    }

    /** Adds a product to the cart and redirects to cart page. */
    @GetMapping("/add-to-cart/{id}/{username}")
    public String addToCart(
            @PathVariable Long id,
            @PathVariable String username,
            HttpSession session
    ) {
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            List<Product> cart = (List<Product>) session.getAttribute("cart");
            if (cart == null) cart = new ArrayList<>();
            cart.add(product);
            session.setAttribute("cart", cart);
        }
        return "redirect:/buyer/cart/" + username;
    }

    /** Removes a product from the cart and redirects to cart page. */
    @GetMapping("/remove-from-cart/{id}/{username}")
    public String removeFromCart(
            @PathVariable Long id,
            @PathVariable String username,
            HttpSession session
    ) {
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(p -> p.getId().equals(id));
            session.setAttribute("cart", cart);
        }
        return "redirect:/buyer/cart/" + username;
    }

    /**
     * Displays order history.
     * Sets cartSize so the sidebar badge works correctly.
     */
    @GetMapping("/orders/{username}")
    public String orderHistory(
            @PathVariable String username,
            Model model,
            HttpSession session
    ) {
        List<Order> orders = orderRepository.findByCustomerName(username);

        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        model.addAttribute("orders", orders);
        model.addAttribute("username", username);
        model.addAttribute("cartSize", cart.size());   // ← was missing

        return "buyer-dashboard";
    }

    /**
     * Validates a coupon code and returns a JSON response.
     * Also checks if the coupon has expired.
     */
    @GetMapping("/check-coupon")
    @ResponseBody
    public Map<String, Object> checkCoupon(@RequestParam String code) {
        Map<String, Object> response = new HashMap<>();
        Coupon coupon = couponRepository.findByCode(code);

        if (coupon != null) {
            // Treat expired coupons as invalid
            if (coupon.getValidUntil() != null && coupon.getValidUntil().isBefore(LocalDate.now())) {
                response.put("valid", false);
                response.put("message", "This coupon has expired.");
            } else {
                response.put("valid", true);
                response.put("discount", coupon.getDiscountPercentage());
            }
        } else {
            response.put("valid", false);
            response.put("message", "Invalid coupon code.");
        }
        return response;
    }

    /**
     * Processes checkout: saves orders, clears cart, redirects to dashboard.
     * After this, the seller's and admin's dashboards will reflect the new orders
     * automatically on their next page load (data is persisted to DB immediately).
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
        List<Product> cart = (List<Product>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/buyer/cart/" + username;
        }

        // Resolve discount
        double discount = 0;
        String appliedCoupon = null;
        if (couponCode != null && !couponCode.isBlank()) {
            Coupon coupon = couponRepository.findByCode(couponCode);
            if (coupon != null &&
                    (coupon.getValidUntil() == null || !coupon.getValidUntil().isBefore(LocalDate.now()))) {
                discount = coupon.getDiscountPercentage();
                appliedCoupon = couponCode;
            }
        }

        // Persist one Order row per cart item
        for (Product p : cart) {
            double finalPrice = p.getPrice() - ((p.getPrice() * discount) / 100);

            Order order = new Order();
            order.setCustomerName(username);
            order.setDeliveryAddress(address);
            order.setMobileNumber(mobile);
            order.setProductName(p.getName());
            order.setOrigin(p.getOrigin());
            order.setPrice(finalPrice);
            order.setSellerName(p.getSellerName());
            order.setDiscountPercentage(discount);
            order.setCouponCode(appliedCoupon);
            order.setOrderDate(LocalDate.now());

            orderRepository.save(order);
        }

        // Clear cart
        session.removeAttribute("cart");

        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
        return "redirect:/buyer/dashboard/" + username + "?success=true";
    }
}
