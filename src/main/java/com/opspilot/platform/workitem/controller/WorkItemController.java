package com.opspilot.platform.workitem.controller;

import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.service.EmployeeService;
import com.opspilot.platform.workitem.WorkItemStatus;
import com.opspilot.platform.workitem.dto.WorkItemCreateRequest;
import com.opspilot.platform.workitem.dto.WorkItemResponse;
import com.opspilot.platform.workitem.service.WorkItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Work Item controller for managing work items.
 * Available to ADMIN and OPERATOR roles.
 */
@RestController
@RequestMapping("/api/workitems")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Work Items", description = "Work item management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class WorkItemController {

    private final WorkItemService workItemService;
    private final EmployeeService employeeService;

    /**
     * Create a new work item.
     *
     * @param request work item creation details
     * @param authentication current authenticated user
     * @return created work item
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Create work item", description = "Create a new work item (ADMIN/OPERATOR)")
    public ResponseEntity<WorkItemResponse> createWorkItem(
            @Valid @RequestBody WorkItemCreateRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Create work item request from: {}", email);

        // Resolve current employee and pass as creator
        Employee currentEmployee = employeeService.findEmployeeEntityByEmail(email);

        WorkItemResponse response = workItemService.createWorkItem(request, currentEmployee);

        log.info("Work item created successfully: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all work items for the current user.
     *
     * @param authentication current authenticated user
     * @return list of work items
     */
    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get my work items", description = "Fetch all work items created by or assigned to current user")
    public ResponseEntity<List<WorkItemResponse>> getMyWorkItems(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetch my work items request from: {}", email);

        Employee currentEmployee = employeeService.findEmployeeEntityByEmail(email);
        List<WorkItemResponse> workItems = workItemService.fetchWorkItemsForEmployee(currentEmployee.getId());

        log.info("Retrieved {} work items for employee: {}", workItems.size(), email);
        return ResponseEntity.ok(workItems);
    }

    /**
     * Get paginated work items for the current user.
     *
     * @param page page number (default 0)
     * @param size page size (default 10)
     * @param sortBy sort field (default "createdAt")
     * @param direction sort direction (default "desc")
     * @param authentication current authenticated user
     * @return page of work items
     */
    @GetMapping("/my/paginated")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Get my work items (paginated)", description = "Fetch work items with pagination support")
    public ResponseEntity<Page<WorkItemResponse>> getMyWorkItemsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Fetch paginated work items request from: {} - Page: {}, Size: {}", email, page, size);

        Employee currentEmployee = employeeService.findEmployeeEntityByEmail(email);

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<WorkItemResponse> workItemsPage = workItemService.fetchWorkItemsForEmployeePaginated(
                currentEmployee.getId(), pageable);

        log.info("Retrieved page {} with {} items for employee: {}",
                 page, workItemsPage.getNumberOfElements(), email);

        return ResponseEntity.ok(workItemsPage);
    }

    /**
     * Update work item status.
     *
     * @param id work item ID
     * @param statusUpdate map containing new status
     * @param authentication current authenticated user
     * @return updated work item
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    @Operation(summary = "Update work item status", description = "Change the status of a work item")
    public ResponseEntity<WorkItemResponse> updateWorkItemStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> statusUpdate,
            Authentication authentication) {

        String email = authentication.getName();
        log.info("Update work item status request for ID: {} from: {}", id, email);

        String statusStr = statusUpdate.get("status");
        if (statusStr == null || statusStr.isBlank()) {
            log.warn("Status not provided in request");
            return ResponseEntity.badRequest().build();
        }

        WorkItemStatus status;
        try {
            status = WorkItemStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status value: {}", statusStr);
            return ResponseEntity.badRequest().build();
        }

        WorkItemResponse response = workItemService.updateStatus(id, status);

        log.info("Work item status updated successfully: {}", id);
        return ResponseEntity.ok(response);
    }
}
