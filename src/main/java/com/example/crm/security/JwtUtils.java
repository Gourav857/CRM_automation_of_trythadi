package com.example.crm.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // Production me ise application.properties se private key ke roop me uthana chahiye
    private final String JWT_SECRET = "9a4f2c8d3b7a1e5f8g0h2i4j6k8l0m2n1o3p5q7r9s1t3u5v7w9x1y2z3a4b5c6d";
    private final long JWT_EXPIRATION_MS = 86400000; // 24 Hours validity

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    }

    // Token generate karne ke liye authentication flow ke baad call hoga
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Request header se username nikalne ke liye
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Token check karne ke liye filter layer me kaam aayega
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("⚠️ Invalid JWT Token: " + e.getMessage());
        }
        return false;
    }
}