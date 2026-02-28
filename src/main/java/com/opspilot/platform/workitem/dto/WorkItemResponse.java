package com.opspilot.platform.workitem.dto;

import com.opspilot.platform.workitem.WorkItemStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for work item response.
 * Contains complete work item information including related entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemResponse {

    private UUID id;
    private String title;
    private String description;
    private WorkItemStatus status;
    private UUID createdById;
    private String createdByName;
    private UUID assignedToId;
    private String assignedToName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

