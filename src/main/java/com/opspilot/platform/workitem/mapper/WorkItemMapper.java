package com.opspilot.platform.workitem.mapper;

import com.opspilot.platform.user.Employee;
import com.opspilot.platform.workitem.WorkItem;
import com.opspilot.platform.workitem.WorkItemStatus;
import com.opspilot.platform.workitem.dto.WorkItemCreateRequest;
import com.opspilot.platform.workitem.dto.WorkItemResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between WorkItem entity and DTOs.
 * Handles transformation of work item data between layers.
 */
@Component
public class WorkItemMapper {

    /**
     * Convert WorkItemCreateRequest DTO to WorkItem entity.
     *
     * @param request the create request DTO
     * @param createdBy the employee creating the work item
     * @param assignedTo the employee assigned to the work item (can be null)
     * @return WorkItem entity ready to be persisted
     */
    public WorkItem toEntity(WorkItemCreateRequest request, Employee createdBy, Employee assignedTo) {
        return WorkItem.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(WorkItemStatus.OPEN)
                .createdBy(createdBy)
                .assignedTo(assignedTo)
                .build();
    }

    /**
     * Convert WorkItem entity to WorkItemResponse DTO.
     *
     * @param workItem the work item entity
     * @return WorkItemResponse DTO
     */
    public WorkItemResponse toResponse(WorkItem workItem) {
        return WorkItemResponse.builder()
                .id(workItem.getId())
                .title(workItem.getTitle())
                .description(workItem.getDescription())
                .status(workItem.getStatus())
                .createdById(workItem.getCreatedBy().getId())
                .createdByName(workItem.getCreatedBy().getFullName())
                .assignedToId(workItem.getAssignedTo() != null ? workItem.getAssignedTo().getId() : null)
                .assignedToName(workItem.getAssignedTo() != null ? workItem.getAssignedTo().getFullName() : null)
                .createdAt(workItem.getCreatedAt())
                .updatedAt(workItem.getUpdatedAt())
                .build();
    }
}

