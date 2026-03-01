package com.opspilot.platform.admin.controller;

import com.opspilot.platform.config.DemoDataService;
import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.service.EmployeeService;
import com.opspilot.platform.workitem.dto.DashboardMetrics;
import com.opspilot.platform.workitem.dto.WorkItemResponse;
import com.opspilot.platform.workitem.service.WorkItemService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Admin controller for administrative operations.
 * Available only to ADMIN role.
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Administrative endpoints (ADMIN only)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final WorkItemService workItemService;
    private final EmployeeService employeeService;
    private final DemoDataService demoDataService;

    /**
     * Get paginated list of all work items.
     *
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @param sortBy field to sort by (default "createdAt")
     * @param direction sort direction (asc/desc, default "desc")
     * @return page of WorkItemResponse
     */
    @GetMapping("/workitems")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all work items (paginated)", description = "Retrieve paginated list of all work items (ADMIN only)")
    public ResponseEntity<Page<WorkItemResponse>> getAllWorkItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("Fetch all work items request - Page: {}, Size: {}", page, size);

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WorkItemResponse> workItemsPage = workItemService.fetchAllWorkItemsPaginated(pageable);

        log.info("Retrieved page {} with {} work items", page, workItemsPage.getNumberOfElements());
        return ResponseEntity.ok(workItemsPage);
    }

    /**
     * Assign a work item to an employee.
     *
     * @param id work item ID
     * @param assignmentData map containing employeeId
     * @param authentication current authenticated admin
     * @return updated work item
     */
    @PutMapping("/workitems/{id}/assign")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign work item", description = "Assign a work item to an employee (ADMIN only)")
    public ResponseEntity<WorkItemResponse> assignWorkItem(
            @PathVariable UUID id,
            @RequestBody Map<String, String> assignmentData,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Assign work item request for ID: {} from admin: {}", id, email);

        String employeeIdStr = assignmentData.get("employeeId");
        if (employeeIdStr == null || employeeIdStr.isBlank()) {
            log.warn("Employee ID not provided in request");
            return ResponseEntity.badRequest().build();
        }

        UUID employeeId;
        try {
            employeeId = UUID.fromString(employeeIdStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid employee ID format: {}", employeeIdStr);
            return ResponseEntity.badRequest().build();
        }

        WorkItemResponse response = workItemService.assignWorkItem(id, employeeId);

        log.info("Work item assigned successfully: {} to employee: {}", id, employeeId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get dashboard metrics for the current admin.
     *
     * @param authentication current authenticated admin
     * @return dashboard metrics
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard metrics", description = "Retrieve dashboard statistics (ADMIN only)")
    public ResponseEntity<DashboardMetrics> getDashboardMetrics(Authentication authentication) {
        String email = authentication.getName();
        log.info("Dashboard metrics request from admin: {}", email);

        Employee currentEmployee = employeeService.findEmployeeEntityByEmail(email);
        DashboardMetrics metrics = workItemService.fetchDashboardMetrics(currentEmployee.getId());

        log.info("Dashboard metrics retrieved successfully for admin: {}", email);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Alias endpoint for demo data loading.
     * Some clients may call /api/admin/demo-data instead of /api/admin/demo-data/load.
     */
    @PostMapping("/demo-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Load demo data (alias)", description = "Alias for /api/admin/demo-data/load (ADMIN only)")
    public ResponseEntity<Map<String, Object>> loadDemoDataAlias() {
        return loadDemoData();
    }

    /**
     * Load demo work items into the system.
     * Creates sample work items with varied statuses assigned to operators.
     *
     * @return summary of loaded data
     */
    @PostMapping("/demo-data/load")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Load demo data", description = "Create sample work items with varied statuses (ADMIN only)")
    public ResponseEntity<Map<String, Object>> loadDemoData() {
        log.info("Demo data load request received");

        int count = demoDataService.loadDemoWorkItems();

        Map<String, Object> response = Map.of(
                "message", "Demo data loaded successfully",
                "workItemsCreated", count
        );

        log.info("Demo data loaded successfully: {} work items created", count);
        return ResponseEntity.ok(response);
    }
}
