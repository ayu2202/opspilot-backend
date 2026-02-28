package com.opspilot.platform.user.controller;

import com.opspilot.platform.user.AccessRole;
import com.opspilot.platform.user.dto.EmployeeResponse;
import com.opspilot.platform.user.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Admin-only controller for employee discovery and visibility.
 * Provides endpoints to list and retrieve employees for assignment and oversight.
 */
@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employees", description = "Admin employee management and discovery APIs")
@SecurityRequirement(name = "bearerAuth")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Get paginated list of all employees.
     *
     * @param page page number (default 0)
     * @param size page size (default 20)
     * @param sortBy field to sort by (default "createdAt")
     * @param direction sort direction (asc/desc, default desc)
     * @return page of EmployeeResponse
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List employees (paginated)", description = "Retrieve paginated list of all employees (ADMIN only)")
    public ResponseEntity<Page<EmployeeResponse>> getEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EmployeeResponse> employees = employeeService.getAllEmployees(pageable);

        log.info("Retrieved page {} of employees, size {}", page, employees.getNumberOfElements());
        return ResponseEntity.ok(employees);
    }

    /**
     * Get list of employees with OPERATOR role.
     *
     * @return list of EmployeeResponse for OPERATOR employees
     */
    @GetMapping("/operators")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List operators", description = "Retrieve all employees with OPERATOR role (ADMIN only)")
    public ResponseEntity<List<EmployeeResponse>> getOperators() {
        List<EmployeeResponse> operators = employeeService.getEmployeesByRole(AccessRole.OPERATOR);
        log.info("Retrieved {} operators", operators.size());
        return ResponseEntity.ok(operators);
    }

    /**
     * Get employee details by ID.
     *
     * @param id employee ID
     * @return EmployeeResponse
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get employee by ID", description = "Retrieve employee details by ID (ADMIN only)")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable UUID id) {
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        log.info("Retrieved employee with ID: {}", id);
        return ResponseEntity.ok(employee);
    }
}

