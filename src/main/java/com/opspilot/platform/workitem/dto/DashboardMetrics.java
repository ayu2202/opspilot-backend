package com.opspilot.platform.workitem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for dashboard metrics.
 * Contains aggregated statistics about work items.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetrics {

    private Long totalWorkItems;
    private Long openWorkItems;
    private Long inProgressWorkItems;
    private Long completedWorkItems;
    private Long rejectedWorkItems;
    private Long myAssignedItems;
    private Long myCreatedItems;
}

