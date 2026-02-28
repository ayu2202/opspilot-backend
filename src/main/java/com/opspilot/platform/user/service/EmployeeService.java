package com.opspilot.platform.user.service;

import com.opspilot.platform.user.AccessRole;
import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.dto.EmployeeRegistrationRequest;
import com.opspilot.platform.user.dto.EmployeeResponse;
import com.opspilot.platform.user.mapper.EmployeeMapper;
import com.opspilot.platform.user.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for employee management operations.
 * Handles business logic for employee registration, authentication, and retrieval.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new employee in the system.
     *
     * @param request the registration request containing employee details
     * @return EmployeeResponse with the created employee information
     * @throws IllegalArgumentException if email already exists
     */
    @Transactional
    public EmployeeResponse registerEmployee(EmployeeRegistrationRequest request) {
        log.info("Attempting to register employee with email: {}", request.getEmail());

        // Check if email already exists
        if (employeeRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Create employee entity
        Employee employee = employeeMapper.toEntity(request, encodedPassword);

        // Save to database
        Employee savedEmployee = employeeRepository.saveAndFlush(employee);

        log.info("Successfully registered employee with ID: {}", savedEmployee.getId());
        return employeeMapper.toResponse(savedEmployee);
    }

    /**
     * Fetch an employee by their email address.
     *
     * @param email the email address to search for
     * @return EmployeeResponse with the employee information
     * @throws IllegalArgumentException if employee not found
     */
    public EmployeeResponse fetchEmployeeByEmail(String email) {
        log.debug("Fetching employee by email: {}", email);

        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Employee not found with email: {}", email);
                    return new IllegalArgumentException("Employee not found with email: " + email);
                });

        return employeeMapper.toResponse(employee);
    }

    /**
     * Find an employee entity by email (internal use).
     *
     * @param email the email address to search for
     * @return Employee entity
     * @throws IllegalArgumentException if employee not found
     */
    public Employee findEmployeeEntityByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found with email: " + email));
    }

    /**
     * Get paginated list of all employees.
     *
     * @param pageable pagination information
     * @return page of EmployeeResponse
     */
    public Page<EmployeeResponse> getAllEmployees(Pageable pageable) {
        log.debug("Fetching paginated employees - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Employee> page = employeeRepository.findAll(pageable);
        return page.map(employeeMapper::toResponse);
    }

    /**
     * Get list of employees by role.
     *
     * @param role access role to filter by
     * @return list of EmployeeResponse
     */
    public List<EmployeeResponse> getEmployeesByRole(AccessRole role) {
        log.debug("Fetching employees by role: {}", role);

        List<Employee> employees = employeeRepository.findByRole(role);
        return employees.stream()
                .map(employeeMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get employee details by ID.
     *
     * @param id employee ID
     * @return EmployeeResponse
     * @throws IllegalArgumentException if employee not found
     */
    public EmployeeResponse getEmployeeById(UUID id) {
        log.debug("Fetching employee by ID: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", id);
                    return new IllegalArgumentException("Employee not found with ID: " + id);
                });

        return employeeMapper.toResponse(employee);
    }
}
