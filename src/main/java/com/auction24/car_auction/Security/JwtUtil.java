package com.auction24.car_auction.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Create the signing key from secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // GENERATE TOKEN — called after successful login
    // Stores userId, email, and role inside the token
    public String generateToken(String userId, String email, String role) {
        return Jwts.builder()
                .subject(userId)
                .claim("email", email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // EXTRACT ALL CLAIMS from token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Get userId from token
    public String extractUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Get email from token
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // Get role from token
    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Validate token — checks if userId matches and not expired
    public boolean validateToken(String token, String userId) {
        return (extractUserId(token).equals(userId) && !isTokenExpired(token));
    }
}
