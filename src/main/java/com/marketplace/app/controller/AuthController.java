package com.marketplace.app.controller;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class AuthController {

    /**
     * Repository for user login operations
     */
    @Autowired
    private LoginRepository loginRepository;

    /**
     * Password encoder for secure password storage
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Displays the homepage
     *
     * @return the index page template name
     */
    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    /**
     * Displays the login page
     *
     * @return the login page template name
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Displays the signup/registration page
     * 
     * @return the signup page template name
     */
    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    /**
     * Processes user signup form submission
     * Encodes password and saves new user to database
     * 
     * @param login the Login entity containing user credentials and role
     * @return redirect to login page for authentication
     */
    @PostMapping("/signup")
    public String signup(Login login) {
        // Encode the password for secure storage
        login.setPassword(passwordEncoder.encode(login.getPassword()));
        
        // Save the new user to the database
        loginRepository.save(login);
        
        // Redirect to login page for user to authenticate
        return "redirect:/login";
    }

    /**
     * Redirects authenticated user to appropriate dashboard based on role
     * Stores username in session for later use
     * 
     * @param auth    the Authentication object containing user information
     * @param session the HttpSession for storing user session data
     * @return redirect to role-specific dashboard (admin/seller/buyer)
     */
    @GetMapping("/redirect-dashboard")
    public String redirectDashboard(Authentication auth, HttpSession session) {
        // Extract username from authentication
        String username = auth.getName();
        
        // Store username in session for template access
        session.setAttribute("username", username);
        
        // Extract user role from authentication authorities
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        // Redirect based on user role
        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin";
        }
        
        if (role.equals("ROLE_SELLER")) {
            return "redirect:/seller/dashboard/" + username;
        }
        
        if (role.equals("ROLE_BUYER")) {
            return "redirect:/buyer/dashboard/" + username;
        }
        
        // Fallback to login if role is not recognized
        return "redirect:/login";
    }
}
