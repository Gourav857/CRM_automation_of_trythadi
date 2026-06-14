package com.example.crm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Constructor Injection
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 🚀 YAHAN ADD KIYA HAI: Static resources aur HTML pages ko permit karne ke liye
                        .requestMatchers("/", "/index.html", "/dashboard.html", "/css/**", "/js/**").permitAll()

                        // Public Webhook Route: Website integration bina token ke chalne ke liye
                        .requestMatchers("/api/v1/webhook/new-lead").permitAll()

                        // Auth Routes: Login aur Register ke liye public access
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Admin Only Routes: Leads delete karne ka access sirf ADMIN ko ho
                        .requestMatchers("/api/v1/leads/delete/**").hasRole("ADMIN")

                        // Shared Secure Routes: Leads dekhne ka access ADMIN aur SALES dono ko ho
                        .requestMatchers("/api/v1/leads/all", "/api/v1/leads/stats").hasAnyRole("ADMIN", "SALES")

                        .requestMatchers("/api/v1/leads/all", "/api/v1/leads/stats").hasAnyRole("ADMIN", "SALES")

                      

                        // Baaki saari requests authenticated honi chahiye
                        .anyRequest().authenticated()
                );

        // Custom filter attach karna security framework ke upar
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
