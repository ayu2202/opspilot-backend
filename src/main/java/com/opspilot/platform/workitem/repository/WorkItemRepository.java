package com.opspilot.platform.workitem.repository;

import com.opspilot.platform.user.Employee;
import com.opspilot.platform.workitem.WorkItem;
import com.opspilot.platform.workitem.WorkItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for WorkItem entity operations.
 * Provides data access methods for work item management.
 */
@Repository
public interface WorkItemRepository extends JpaRepository<WorkItem, UUID> {

    /**
     * Find all work items created by a specific employee.
     *
     * @param employee the employee who created the work items
     * @return list of work items created by the employee
     */
    List<WorkItem> findByCreatedBy(Employee employee);

    /**
     * Find all work items assigned to a specific employee.
     *
     * @param employee the employee to whom work items are assigned
     * @return list of work items assigned to the employee
     */
    List<WorkItem> findByAssignedTo(Employee employee);

    /**
     * Find all work items with a specific status.
     *
     * @param status the status to filter by
     * @return list of work items with the given status
     */
    List<WorkItem> findByStatus(WorkItemStatus status);

    /**
     * Find all work items for an employee (created by or assigned to) with pagination.
     *
     * @param employee the employee
     * @param pageable pagination parameters
     * @return page of work items
     */
    @Query("SELECT w FROM WorkItem w WHERE w.createdBy = :employee OR w.assignedTo = :employee")
    Page<WorkItem> findByCreatedByOrAssignedTo(@Param("employee") Employee employee, Pageable pageable);
}

