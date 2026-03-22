package com.marketplace.app.service;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final LoginRepository loginRepository;

    public AdminService(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
    }

    // Fetch all users except ADMIN
    public List<Login> getAllBuyersAndSellers() {
        return loginRepository.findByRoleNot("ADMIN");
    }

    public void deleteUser(Long id) {
        loginRepository.deleteById(id);
    }
}
