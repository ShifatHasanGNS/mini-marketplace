package com.marketplace.app.controller;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController
 * Handles authentication, signup, and role-based redirection
 */
@Controller
public class AuthController {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===================== LOGIN PAGE =====================
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // ===================== SIGNUP PAGE =====================
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    // ===================== SIGNUP PROCESS =====================
    @PostMapping("/signup")
    public String signup(Login login) {

        // Encode password before saving
        login.setPassword(passwordEncoder.encode(login.getPassword()));

        loginRepository.save(login);

        return "redirect:/login";
    }

    // ===================== ROLE-BASED REDIRECTION =====================
    @GetMapping("/redirect-dashboard")
    public String redirectDashboard(Authentication auth, HttpSession session) {

        String username = auth.getName();

        // Store username in session
        session.setAttribute("username", username);

        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Redirect based on role
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin";
        }

        if (role.equals("ROLE_SELLER")) {
            return "redirect:/seller/dashboard/" + username;
        }

        if (role.equals("ROLE_BUYER")) {
            return "redirect:/buyer/dashboard/" + username;
        }

        // Fallback
        return "redirect:/login";
    }
}