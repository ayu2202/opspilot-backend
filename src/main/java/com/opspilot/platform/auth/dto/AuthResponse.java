package com.opspilot.platform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response containing JWT token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String email;
    private String fullName;
    private String role;

    public AuthResponse(String token, String email, String fullName, String role) {
        this.token = token;
        this.type = "Bearer";
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }
}

