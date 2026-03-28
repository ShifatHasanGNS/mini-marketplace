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
 *
 * Handles authentication-related operations:
 * - Login page display
 * - Signup page display
 * - Signup processing
 * - Role-based dashboard redirection
 *
 * This controller is part of the Mini Marketplace project.
 * 
 * @author YourName
 * @version 1.0
 */
@Controller
public class AuthController {

    // ===================== REPOSITORIES =====================
    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ===================== LOGIN PAGE =====================
    /**
     * Returns the login page view
     *
     * @return login page template name
     */
    @GetMapping("/login")
    public String loginPage() {

        // Simple return statement
        return "login";
    }

    // ===================== SIGNUP PAGE =====================
    /**
     * Returns the signup page view
     *
     * @return signup page template name
     */
    @GetMapping("/signup")
    public String signupPage() {

        // Return signup template
        return "signup";
    }

    // ===================== SIGNUP PROCESS =====================
    /**
     * Processes signup form submission
     *
     * @param login Login entity from form
     * @return redirect to login page
     */
    @PostMapping("/signup")
    public String signup(Login login) {

        // ===================== PASSWORD ENCODING =====================
        // Encode password before saving
        login.setPassword(passwordEncoder.encode(login.getPassword()));

        // ===================== SAVE USER =====================
        loginRepository.save(login);

        // ===================== REDIRECT =====================
        return "redirect:/login";
    }

    // ===================== ROLE-BASED REDIRECTION =====================
    /**
     * Redirects user to their dashboard based on role
     *
     * @param auth Authentication object
     * @param session HttpSession for storing username
     * @return redirect to appropriate dashboard
     */
    @GetMapping("/redirect-dashboard")
    public String redirectDashboard(Authentication auth, HttpSession session) {

        // ===================== GET USERNAME =====================
        String username = auth.getName();

        // Store username in session
        session.setAttribute("username", username);

        // ===================== GET ROLE =====================
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // ===================== REDIRECTION =====================
        if (role.equals("ROLE_ADMIN")) {
            // Redirect admin
            return "redirect:/admin";
        }

        if (role.equals("ROLE_SELLER")) {
            // Redirect seller
            return "redirect:/seller/dashboard/" + username;
        }

        if (role.equals("ROLE_BUYER")) {
            // Redirect buyer
            return "redirect:/buyer/dashboard/" + username;
        }

        // ===================== FALLBACK =====================
        return "redirect:/login";
    }


}