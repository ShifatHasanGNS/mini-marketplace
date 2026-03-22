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

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(Login login) {
        login.setPassword(passwordEncoder.encode(login.getPassword()));
        loginRepository.save(login);

        return "redirect:/login";
    }

    @GetMapping("/redirect-dashboard")
    public String redirectDashboard(Authentication auth, HttpSession session) {
        String username = auth.getName();
        session.setAttribute("username", username);

        String role = auth.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_ADMIN")) return "redirect:/admin";

        if (role.equals("ROLE_SELLER")) return (
            "redirect:/seller/dashboard/" + username
        );

        if (role.equals("ROLE_BUYER")) return (
            "redirect:/buyer/dashboard/" + username
        );

        return "redirect:/login";
    }
}
