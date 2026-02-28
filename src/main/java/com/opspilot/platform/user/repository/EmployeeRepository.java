package com.opspilot.platform.user.repository;

import com.opspilot.platform.user.AccessRole;
import com.opspilot.platform.user.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Employee entity operations.
 * Provides data access methods for employee management.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    /**
     * Find an employee by their email address.
     *
     * @param email the email address to search for
     * @return Optional containing the employee if found
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Check if an employee exists with the given email.
     *
     * @param email the email address to check
     * @return true if an employee with the email exists, false otherwise
     */
    Boolean existsByEmail(String email);

    /**
     * Find employees by their role.
     *
     * @param role the role to search for
     * @return List of employees with the given role
     */
    List<Employee> findByRole(AccessRole role);
}
