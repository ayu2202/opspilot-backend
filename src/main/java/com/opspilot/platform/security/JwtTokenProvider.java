package com.opspilot.platform.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * JWT Token Provider for generating and validating JWT tokens.
 * Handles token creation, validation, and claims extraction.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long jwtExpirationMs;

    public JwtTokenProvider(
            @Value("${jwt.secret:OpsPilotSecretKeyForJWTTokenGenerationMustBeLongEnough256Bits}") String secret,
            @Value("${jwt.expiration:86400000}") long jwtExpirationMs) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.jwtExpirationMs = jwtExpirationMs;
    }

    /**
     * Generate JWT token from Authentication object.
     *
     * @param authentication the authentication object
     * @return JWT token string
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        log.debug("Generated JWT token for user: {}", username);
        return token;
    }

    /**
     * Generate JWT token from username and role.
     *
     * @param email the employee email
     * @param role the employee role
     * @return JWT token string
     */
    public String generateTokenFromEmailAndRole(String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(email)
                .claim("roles", "ROLE_" + role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();

        log.debug("Generated JWT token for email: {} with role: {}", email, role);
        return token;
    }

    /**
     * Extract username (email) from JWT token.
     *
     * @param token the JWT token
     * @return username (email)
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    /**
     * Extract roles from JWT token and convert to GrantedAuthority collection.
     *
     * @param token the JWT token
     * @return collection of GrantedAuthority
     */
    public Collection<? extends GrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String roles = claims.get("roles", String.class);

        if (roles == null || roles.isEmpty()) {
            log.warn("No roles found in JWT token");
            return Arrays.asList(new SimpleGrantedAuthority("ROLE_VIEWER"));
        }

        // Split roles by comma and convert to GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(role -> !role.isEmpty())
                .map(role -> {
                    // Ensure role has ROLE_ prefix
                    if (!role.startsWith("ROLE_")) {
                        return new SimpleGrantedAuthority("ROLE_" + role);
                    }
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toList());

        log.debug("Extracted roles from token: {}", authorities);
        return authorities;
    }

    /**
     * Validate JWT token.
     *
     * @param token the JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }
}

