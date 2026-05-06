package com.lechthemitch.sms.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private SecretKey superSecretKey;

    @PostConstruct
    private void keyInit() {
        this.superSecretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        var allAuthorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        var role = allAuthorities.stream().
                filter(authority -> authority.startsWith("ROLE_")).collect(Collectors.toSet());
        var permissions = allAuthorities.stream().
                filter(authority -> !authority.startsWith("ROLE_")).collect(Collectors.toSet());
        claims.put("role", role);
        claims.put("permissions", permissions);
        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(superSecretKey)
                .compact();
    }

    public String extractUsernameFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String username = extractUsernameFromToken(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(superSecretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
