package com.example.crm.repository;

import com.example.crm.model.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    // Isme hume khud se code likhne ki zaroorat nahi hai, Spring Data JPA sab handle kar lega
}