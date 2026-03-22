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

    // ================= DASHBOARD =================
    @GetMapping("/dashboard/{username}")
    public String dashboard(
        @PathVariable String username,
        @RequestParam(required = false) String success,
        Model model,
        HttpSession session
    ) {
        session.setAttribute("username", username);

        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null) cart = new ArrayList<>();

        session.setAttribute("cart", cart);

        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        List<Order> orders = orderRepository.findByCustomerName(username);

        double totalSpent = 0;
        for (Order o : orders) {
            totalSpent += o.getPrice();
        }

        model.addAttribute("totalSpent", totalSpent);

        Map<String, Integer> sellerCount = new HashMap<>();

        for (Order o : orders) {
            sellerCount.put(
                o.getSellerName(),
                sellerCount.getOrDefault(o.getSellerName(), 0) + 1
            );
        }

        List<String> topSellers = sellerCount
            .entrySet()
            .stream()
            .sorted((a, b) -> b.getValue() - a.getValue())
            .limit(3)
            .map(Map.Entry::getKey)
            .toList();

        model.addAttribute("topSellers", topSellers);

        if (success != null) {
            model.addAttribute("successMessage", "Order placed successfully!");
        }

        model.addAttribute("username", username);
        model.addAttribute("cartSize", cart.size());

        return "buyer-dashboard";
    }

    // ================= PRODUCTS =================
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

    // ================= CART =================
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

    // ================= ADD TO CART =================
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

    // ================= REMOVE FROM CART =================
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

    // ================= ORDER HISTORY =================
    @GetMapping("/orders/{username}")
    public String orderHistory(@PathVariable String username, Model model) {
        List<Order> orders = orderRepository.findByCustomerName(username);

        model.addAttribute("orders", orders);
        model.addAttribute("username", username);

        return "buyer-dashboard";
    }

    // ================= CHECK COUPON =================
    @GetMapping("/check-coupon")
    @ResponseBody
    public Map<String, Object> checkCoupon(@RequestParam String code) {
        Map<String, Object> response = new HashMap<>();

        Coupon coupon = couponRepository.findByCode(code);

        if (coupon != null) {
            response.put("valid", true);
            response.put("discount", coupon.getDiscountPercentage());
        } else {
            response.put("valid", false);
        }

        return response;
    }

    // ================= CHECKOUT =================
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
            redirectAttributes.addFlashAttribute("error", "Cart is empty");
            return "redirect:/buyer/cart/" + username;
        }

        double discount = 0;

        if (couponCode != null && !couponCode.isEmpty()) {
            Coupon coupon = couponRepository.findByCode(couponCode);

            if (coupon != null) discount = coupon.getDiscountPercentage();
        }

        for (Product p : cart) {
            double finalPrice =
                p.getPrice() - ((p.getPrice() * discount) / 100);

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

            orderRepository.save(order);
        }

        session.removeAttribute("cart");

        redirectAttributes.addFlashAttribute("success", "Order placed successfully!");
        return "redirect:/buyer/dashboard/" + username + "?success=true";
    }
}
