package com.marketplace.app.service;

import com.marketplace.app.dto.LoginRequest;
import com.marketplace.app.dto.RegisterRequest;
import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final LoginRepository repo;
    private final PasswordEncoder encoder;

    public AuthService(LoginRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public void register(RegisterRequest request) {
        Login user = new Login();
        user.setName(request.getName());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        repo.save(user);
    }

    public Login login(LoginRequest request) {
        Login user = repo
            .findByName(request.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
}
