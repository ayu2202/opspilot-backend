package com.opspilot.platform.auth.controller;

import com.opspilot.platform.auth.dto.AuthResponse;
import com.opspilot.platform.security.JwtTokenProvider;
import com.opspilot.platform.user.dto.EmployeeRegistrationRequest;
import com.opspilot.platform.user.dto.EmployeeResponse;
import com.opspilot.platform.user.dto.LoginRequest;
import com.opspilot.platform.user.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller for employee registration and login.
 * Handles JWT token generation for authenticated users.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Employee authentication and registration endpoints")
public class AuthController {

    private final EmployeeService employeeService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new employee.
     *
     * @param request employee registration details
     * @return registered employee information
     */
    @PostMapping("/register")
    @Operation(summary = "Register new employee", description = "Create a new employee account in the system")
    public ResponseEntity<EmployeeResponse> register(@Valid @RequestBody EmployeeRegistrationRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());

        EmployeeResponse response = employeeService.registerEmployee(request);

        log.info("Employee registered successfully: {}", response.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate employee and generate JWT token.
     *
     * @param request login credentials
     * @return JWT token and employee details
     */
    @PostMapping("/login")
    @Operation(summary = "Employee login", description = "Authenticate employee and receive JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Get employee details
        EmployeeResponse employee = employeeService.fetchEmployeeByEmail(request.getEmail());

        AuthResponse authResponse = new AuthResponse(
                jwt,
                employee.getEmail(),
                employee.getFullName(),
                employee.getRole().name()
        );

        log.info("Employee logged in successfully: {}", request.getEmail());
        return ResponseEntity.ok(authResponse);
    }
}

