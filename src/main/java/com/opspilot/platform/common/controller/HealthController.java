package com.opspilot.platform.common.controller;

import com.opspilot.platform.common.dto.HealthResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health monitoring controller for checking application status.
 * Provides basic health check endpoint accessible without authentication.
 */
@RestController
@RequestMapping("/api/health")
@Slf4j
@Tag(name = "Health", description = "Health monitoring endpoints")
public class HealthController {

    /**
     * Health check endpoint.
     *
     * @return health status response
     */
    @GetMapping
    @Operation(summary = "Health check", description = "Check if the service is running")
    public ResponseEntity<HealthResponse> health() {
        log.debug("Health check requested");

        HealthResponse response = HealthResponse.builder()
                .status("UP")
                .service("OpsPilot Operations Core")
                .build();

        return ResponseEntity.ok(response);
    }
}

