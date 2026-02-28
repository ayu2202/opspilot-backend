package com.opspilot.platform.user.dto;

import com.opspilot.platform.user.AccessRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for employee response.
 * Contains employee information without sensitive data like password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private UUID id;
    private String email;
    private String fullName;
    private AccessRole role;
    private Boolean active;
    private LocalDateTime createdAt;
}

