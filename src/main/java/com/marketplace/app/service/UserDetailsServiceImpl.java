package com.marketplace.app.service;

import com.marketplace.app.entity.Login;
import com.marketplace.app.repository.LoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private LoginRepository loginRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        Login user = loginRepository
            .findByName(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
            .username(user.getName())
            .password(user.getPassword())
            .roles(user.getRole())
            .build();
    }
}
