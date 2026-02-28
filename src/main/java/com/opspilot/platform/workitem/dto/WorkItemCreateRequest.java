package com.opspilot.platform.workitem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO for creating a new work item.
 * Contains client-provided fields; creator is derived from authenticated user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkItemCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    // Assignee is optional; when provided must correspond to an OPERATOR
    private UUID assignedToId;
}
