package com.example.crm.security;

import com.example.crm.model.User;
import com.example.crm.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 🚀 FIXED: Ab yeh database me pehle "Gourav" ko hi dhoondhega, taaki duplicate insert na ho
        if (userRepository.findByUsername("Gourav").isEmpty()) {
            User defaultAdmin = new User();
            defaultAdmin.setUsername("Gourav");
            defaultAdmin.setPassword(passwordEncoder.encode("Gourav123")); // 🔥 Aapka secure password
            defaultAdmin.setRole("ROLE_ADMIN");

            userRepository.save(defaultAdmin);
            System.out.println("🚀 [DATA INITIALIZER] Admin account created successfully for Gourav!");
        } else {
            System.out.println("ℹ️ [DATA INITIALIZER] Admin account 'Gourav' already exists. Skipping insertion.");
        }
    }
}