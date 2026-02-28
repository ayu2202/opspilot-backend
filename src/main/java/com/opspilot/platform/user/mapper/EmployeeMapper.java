package com.opspilot.platform.user.mapper;

import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.dto.EmployeeRegistrationRequest;
import com.opspilot.platform.user.dto.EmployeeResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between Employee entity and DTOs.
 * Handles transformation of employee data between layers.
 */
@Component
public class EmployeeMapper {

    /**
     * Convert EmployeeRegistrationRequest DTO to Employee entity.
     * Note: Password should be encoded before calling this method.
     *
     * @param request the registration request DTO
     * @param encodedPassword the pre-encoded password
     * @return Employee entity ready to be persisted
     */
    public Employee toEntity(EmployeeRegistrationRequest request, String encodedPassword) {
        return Employee.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .fullName(request.getFullName())
                .role(request.getRole())
                .active(true)
                .build();
    }

    /**
     * Convert Employee entity to EmployeeResponse DTO.
     * Excludes sensitive information like password.
     *
     * @param employee the employee entity
     * @return EmployeeResponse DTO
     */
    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .email(employee.getEmail())
                .fullName(employee.getFullName())
                .role(employee.getRole())
                .active(employee.getActive())
                .createdAt(employee.getCreatedAt())
                .build();
    }
}

