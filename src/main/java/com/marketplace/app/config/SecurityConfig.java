package com.marketplace.app.config;

import com.marketplace.app.service.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Service for loading user details from database
     */
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Constructor with dependency injection
     * 
     * @param userDetailsService the UserDetailsService implementation
     */
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Creates password encoder bean
     * Uses BCrypt algorithm for password hashing
     * 
     * @return configured PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates authentication provider bean
     * Configures DAO authentication with custom user details service
     * 
     * @return configured AuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Set custom user details service
        authProvider.setUserDetailsService(userDetailsService);
        
        // Set password encoder
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    /**
     * Configures security filter chain for HTTP requests
     * Sets up URL authorization rules and form login configuration
     * 
     * @param http the HttpSecurity object
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless API endpoints
                .csrf(csrf -> csrf.disable())
                
                // Configure authorization for different URLs
                .authorizeHttpRequests(auth ->
                        auth
                                // Permit unauthenticated access to homepage, login, signup, and error page
                                .requestMatchers("/", "/login", "/signup", "/error")
                                .permitAll()
                                
                                // Restrict admin operations to ADMIN role
                                .requestMatchers("/admin/**")
                                .hasRole("ADMIN")
                                
                                // Restrict seller operations to SELLER role
                                .requestMatchers("/seller/**")
                                .hasRole("SELLER")
                                
                                // Restrict buyer operations to BUYER role
                                .requestMatchers("/buyer/**")
                                .hasRole("BUYER")
                                
                                // Require authentication for all other requests
                                .anyRequest()
                                .authenticated()
                )
                
                // Configure form login page
                .formLogin(form ->
                        form
                                // Custom login page URL
                                .loginPage("/login")
                                
                                // Custom username parameter name
                                .usernameParameter("name")
                                
                                // Custom password parameter name
                                .passwordParameter("password")
                                
                                // Redirect to dashboard after successful login
                                .defaultSuccessUrl("/redirect-dashboard", true)
                                
                                // Redirect to login on authentication failure
                                .failureUrl("/login?error=true")
                                
                                // Permit access to login page
                                .permitAll()
                )
                
                // Configure logout behavior
                .logout(logout ->
                        logout
                                // Logout endpoint URL
                                .logoutUrl("/logout")
                                
                                // Redirect to login after logout
                                .logoutSuccessUrl("/login")
                );

        return http.build();
    }
}
