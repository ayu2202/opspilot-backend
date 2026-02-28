package com.opspilot.platform.workitem.service;

import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.repository.EmployeeRepository;
import com.opspilot.platform.workitem.WorkItem;
import com.opspilot.platform.workitem.WorkItemStatus;
import com.opspilot.platform.workitem.dto.DashboardMetrics;
import com.opspilot.platform.workitem.dto.WorkItemCreateRequest;
import com.opspilot.platform.workitem.dto.WorkItemResponse;
import com.opspilot.platform.workitem.dto.WorkItemUpdateRequest;
import com.opspilot.platform.workitem.mapper.WorkItemMapper;
import com.opspilot.platform.workitem.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for work item management operations.
 * Handles business logic for work item creation, assignment, status updates, and retrieval.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WorkItemService {

    private final WorkItemRepository workItemRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkItemMapper workItemMapper;

    /**
     * Create a new work item.
     *
     * @param request   the create request containing work item details
     * @param createdBy the employee creating the work item (from authentication)
     * @return WorkItemResponse with the created work item information
     * @throws IllegalArgumentException if assignee not found
     */
    @Transactional
    public WorkItemResponse createWorkItem(WorkItemCreateRequest request, Employee createdBy) {
        log.info("Creating new work item with title: {} by creator: {}", request.getTitle(), createdBy.getEmail());

        // Fetch assignee if provided
        Employee assignedTo = null;
        if (request.getAssignedToId() != null) {
            assignedTo = employeeRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> {
                        log.warn("Assignee not found with ID: {}", request.getAssignedToId());
                        return new IllegalArgumentException("Assignee not found with ID: " + request.getAssignedToId());
                    });
        }

        // Create work item entity
        WorkItem workItem = workItemMapper.toEntity(request, createdBy, assignedTo);

        // saveAndFlush so Hibernate executes the INSERT immediately and
        // populates @CreationTimestamp / @UpdateTimestamp before we read them
        WorkItem savedWorkItem = workItemRepository.saveAndFlush(workItem);

        log.info("Successfully created work item with ID: {}", savedWorkItem.getId());
        return workItemMapper.toResponse(savedWorkItem);
    }

    /**
     * Assign a work item to an employee.
     *
     * @param workItemId the ID of the work item to assign
     * @param employeeId the ID of the employee to assign to
     * @return WorkItemResponse with updated work item information
     * @throws IllegalArgumentException if work item or employee not found
     */
    @Transactional
    public WorkItemResponse assignWorkItem(UUID workItemId, UUID employeeId) {
        log.info("Assigning work item {} to employee {}", workItemId, employeeId);

        // Fetch work item
        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> {
                    log.warn("Work item not found with ID: {}", workItemId);
                    return new IllegalArgumentException("Work item not found with ID: " + workItemId);
                });

        // Fetch employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", employeeId);
                    return new IllegalArgumentException("Employee not found with ID: " + employeeId);
                });

        // Update assignment
        workItem.setAssignedTo(employee);

        // Update status to IN_PROGRESS if it was OPEN
        if (workItem.getStatus() == WorkItemStatus.OPEN) {
            workItem.setStatus(WorkItemStatus.IN_PROGRESS);
        }

        WorkItem updatedWorkItem = workItemRepository.save(workItem);

        log.info("Successfully assigned work item {} to employee {}", workItemId, employeeId);
        return workItemMapper.toResponse(updatedWorkItem);
    }

    /**
     * Update the status of a work item.
     *
     * @param workItemId the ID of the work item
     * @param status the new status
     * @return WorkItemResponse with updated work item information
     * @throws IllegalArgumentException if work item not found
     */
    @Transactional
    public WorkItemResponse updateStatus(UUID workItemId, WorkItemStatus status) {
        log.info("Updating status of work item {} to {}", workItemId, status);

        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> {
                    log.warn("Work item not found with ID: {}", workItemId);
                    return new IllegalArgumentException("Work item not found with ID: " + workItemId);
                });

        workItem.setStatus(status);
        WorkItem updatedWorkItem = workItemRepository.save(workItem);

        log.info("Successfully updated status of work item {} to {}", workItemId, status);
        return workItemMapper.toResponse(updatedWorkItem);
    }

    /**
     * Update a work item with provided fields.
     *
     * @param workItemId the ID of the work item
     * @param request the update request with fields to update
     * @return WorkItemResponse with updated work item information
     * @throws IllegalArgumentException if work item not found
     */
    @Transactional
    public WorkItemResponse updateWorkItem(UUID workItemId, WorkItemUpdateRequest request) {
        log.info("Updating work item {}", workItemId);

        WorkItem workItem = workItemRepository.findById(workItemId)
                .orElseThrow(() -> {
                    log.warn("Work item not found with ID: {}", workItemId);
                    return new IllegalArgumentException("Work item not found with ID: " + workItemId);
                });

        // Update fields if provided
        if (request.getTitle() != null) {
            workItem.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            workItem.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            workItem.setStatus(request.getStatus());
        }
        if (request.getAssignedToId() != null) {
            Employee assignedTo = employeeRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + request.getAssignedToId()));
            workItem.setAssignedTo(assignedTo);
        }

        WorkItem updatedWorkItem = workItemRepository.saveAndFlush(workItem);

        log.info("Successfully updated work item {}", workItemId);
        return workItemMapper.toResponse(updatedWorkItem);
    }

    /**
     * Fetch all work items for an employee (created by or assigned to).
     *
     * @param employeeId the ID of the employee
     * @return list of WorkItemResponse
     * @throws IllegalArgumentException if employee not found
     */
    public List<WorkItemResponse> fetchWorkItemsForEmployee(UUID employeeId) {
        log.debug("Fetching work items for employee {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", employeeId);
                    return new IllegalArgumentException("Employee not found with ID: " + employeeId);
                });

        // Get items created by employee
        List<WorkItem> createdItems = workItemRepository.findByCreatedBy(employee);

        // Get items assigned to employee
        List<WorkItem> assignedItems = workItemRepository.findByAssignedTo(employee);

        // Combine and convert to response
        List<WorkItem> allItems = new java.util.ArrayList<>(createdItems);
        assignedItems.stream()
                .filter(item -> !allItems.contains(item))
                .forEach(allItems::add);

        return allItems.stream()
                .map(workItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Fetch work items for an employee with pagination.
     *
     * @param employeeId the ID of the employee
     * @param pageable pagination parameters
     * @return page of WorkItemResponse
     * @throws IllegalArgumentException if employee not found
     */
    public Page<WorkItemResponse> fetchWorkItemsForEmployeePaginated(UUID employeeId, Pageable pageable) {
        log.debug("Fetching paginated work items for employee {} - Page: {}, Size: {}",
                  employeeId, pageable.getPageNumber(), pageable.getPageSize());

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", employeeId);
                    return new IllegalArgumentException("Employee not found with ID: " + employeeId);
                });

        Page<WorkItem> workItemsPage = workItemRepository.findByCreatedByOrAssignedTo(employee, pageable);

        return workItemsPage.map(workItemMapper::toResponse);
    }

    /**
     * Fetch all work items with pagination.
     *
     * @param pageable pagination parameters
     * @return page of WorkItemResponse
     */
    public Page<WorkItemResponse> fetchAllWorkItemsPaginated(Pageable pageable) {
        log.debug("Fetching all work items - Page: {}, Size: {}",
                  pageable.getPageNumber(), pageable.getPageSize());

        Page<WorkItem> workItemsPage = workItemRepository.findAll(pageable);
        return workItemsPage.map(workItemMapper::toResponse);
    }

    /**
     * Fetch dashboard metrics for an employee.
     *
     * @param employeeId the ID of the employee
     * @return DashboardMetrics with aggregated statistics
     * @throws IllegalArgumentException if employee not found
     */
    public DashboardMetrics fetchDashboardMetrics(UUID employeeId) {
        log.debug("Fetching dashboard metrics for employee {}", employeeId);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> {
                    log.warn("Employee not found with ID: {}", employeeId);
                    return new IllegalArgumentException("Employee not found with ID: " + employeeId);
                });

        // Get all work items
        List<WorkItem> allWorkItems = workItemRepository.findAll();

        // Get items for this employee
        List<WorkItem> assignedItems = workItemRepository.findByAssignedTo(employee);
        List<WorkItem> createdItems = workItemRepository.findByCreatedBy(employee);

        // Calculate metrics
        long totalWorkItems = allWorkItems.size();
        long openWorkItems = workItemRepository.findByStatus(WorkItemStatus.OPEN).size();
        long inProgressWorkItems = workItemRepository.findByStatus(WorkItemStatus.IN_PROGRESS).size();
        long completedWorkItems = workItemRepository.findByStatus(WorkItemStatus.COMPLETED).size();
        long rejectedWorkItems = workItemRepository.findByStatus(WorkItemStatus.REJECTED).size();

        return DashboardMetrics.builder()
                .totalWorkItems(totalWorkItems)
                .openWorkItems(openWorkItems)
                .inProgressWorkItems(inProgressWorkItems)
                .completedWorkItems(completedWorkItems)
                .rejectedWorkItems(rejectedWorkItems)
                .myAssignedItems((long) assignedItems.size())
                .myCreatedItems((long) createdItems.size())
                .build();
    }
}

