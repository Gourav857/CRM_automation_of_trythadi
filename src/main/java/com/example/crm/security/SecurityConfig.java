package com.example.crm.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

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
                // 1. ✅ YAHAN CORS CONFIGURATION APPLY KI HAI
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Static resources aur HTML pages ko permit karne ke liye
                        .requestMatchers("/", "/index.html", "/dashboard.html", "/css/**", "/js/**").permitAll()

                        // Public Webhook Route & Lead submission routes (Website integration bina token ke chalne ke liye)
                        // Note: Agar aapka website form data send kar raha hai, toh use permitAll hona chahiye
                        .requestMatchers("/api/v1/webhook/new-lead", "/api/v1/leads/submit", "/api/v1/leads/create").permitAll()

                        // Auth Routes: Login aur Register ke liye public access
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Admin Only Routes: Leads delete karne ka access sirf ADMIN ko ho
                        .requestMatchers("/api/v1/leads/delete/**").hasRole("ADMIN")

                        // Shared Secure Routes: Leads dekhne ka access ADMIN aur SALES dono ko ho
                        .requestMatchers("/api/v1/leads/all", "/api/v1/leads/stats").hasAnyRole("ADMIN", "SALES")

                        // Baaki saari requests authenticated honi chahiye
                        .anyRequest().authenticated()
                );

        // Custom filter attach karna security framework ke upar
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 2. ✅ CORS BEAN: Jo browser ko batayega ki frontend domain trusted hai
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Apne Frontend Domain ko explicitly allow karein
        configuration.setAllowedOrigins(List.of("https://trythadi-1.onrender.com")); 
        
        // Sabhi Methods ko allow karein (Aksar preflight OPTIONS method hi fail hota hai)
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Sabhi Headers ko allow karein (Jaise Authorization, Content-Type)
        configuration.setAllowedHeaders(List.of("*"));
        
        // Credentials (Cookies, Auth Headers) allow karne ke liye
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
