package com.bluechat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * Sets up authentication, authorization, and CSRF protection.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures HTTP security for the application.
     * 
     * @param http the HttpSecurity to configure
     * @return the SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authorize -> authorize
                // Allow access to static resources
                .antMatchers("/webjars/**", "/css/**", "/js/**").permitAll()
                // Allow access to WebSocket endpoint
                .antMatchers("/ws/**").permitAll()
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")  // Custom login page
                .defaultSuccessUrl("/chat/rooms") // Redirect after successful login
                .permitAll()
            )
            .logout(logout -> logout
                .permitAll()
                .logoutSuccessUrl("/login?logout") // Redirect after logout
            )
            // Configure CSRF protection but include WebSocket endpoints
            .csrf(csrf -> csrf
                .ignoringAntMatchers("/ws/**") // Don't apply CSRF for WebSocket connections
            );
        
        return http.build();
    }

    /**
     * Creates in-memory user details service with demo users.
     * 
     * @param passwordEncoder the password encoder to use
     * @return a UserDetailsService with in-memory users
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        // Create demo users (for testing purposes only)
        UserDetails user1 = User.builder()
                .username("user1")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        
        UserDetails user2 = User.builder()
                .username("user2")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build();
        
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN", "USER")
                .build();
        
        return new InMemoryUserDetailsManager(user1, user2, admin);
    }
    
    /**
     * Creates password encoder for secure password storage.
     * 
     * @return a BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

