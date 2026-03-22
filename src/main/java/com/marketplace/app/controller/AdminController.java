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

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ===================== REDIRECT BASE /admin =====================
    @GetMapping
    public String redirectToDashboard() {
        return "redirect:/admin/dashboard";
    }

    // ===================== DASHBOARD =====================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "dashboard");
        return "admin-dashboard";
    }

    // ===================== USERS =====================
    @GetMapping("/users")
    public String viewUsers(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "users");
        return "admin-dashboard";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {
        loginRepository.deleteById(id);
        return "redirect:/admin/users";
    }

    // ===================== COUPONS =====================
    @GetMapping("/coupons")
    public String viewCoupons(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "coupon");
        return "admin-dashboard";
    }

    @PostMapping("/coupons")
    public String createCoupon(@ModelAttribute Coupon coupon) {
        couponRepository.save(coupon);
        return "redirect:/admin/coupons";
    }

    @GetMapping("/coupons/{id}")
    public String editCoupon(@PathVariable Long id, Model model) {
        populateModel(model);
        Coupon coupon = couponRepository.findById(id).orElse(new Coupon());
        model.addAttribute("coupon", coupon);
        model.addAttribute("openCoupon", true);
        model.addAttribute("activeSection", "coupon");
        return "admin-dashboard";
    }

    @PutMapping("/coupons/{id}")
    public String updateCoupon(
        @PathVariable Long id,
        @ModelAttribute Coupon coupon
    ) {
        coupon.setId(id);
        couponRepository.save(coupon);
        return "redirect:/admin/coupons";
    }

    @DeleteMapping("/coupons/{id}")
    public String deleteCoupon(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return "redirect:/admin/coupons";
    }

    // ===================== SALES =====================
    @GetMapping("/sales")
    public String viewSales(Model model) {
        populateModel(model);
        model.addAttribute("activeSection", "sell");
        return "admin-dashboard";
    }

    // ===================== HELPER METHOD =====================
    private void populateModel(Model model) {
        List<Login> users = loginRepository.findByRoleNot("ADMIN");
        List<Coupon> coupons = couponRepository.findAll();
        List<Order> orders = orderRepository.findAll();

        // Total selling
        double totalSelling = orders
            .stream()
            .mapToDouble(Order::getPrice)
            .sum();

        // Top sellers
        Map<String, Double> sellerMap = new HashMap<>();
        for (Order o : orders) {
            sellerMap.put(
                o.getSellerName(),
                sellerMap.getOrDefault(o.getSellerName(), 0.0) + o.getPrice()
            );
        }
        List<Map.Entry<String, Double>> topSellers = sellerMap
            .entrySet()
            .stream()
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
    }
}
