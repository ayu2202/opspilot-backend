package com.opspilot.platform.workitem;

/**
 * Enum representing the lifecycle status of a work item in the OpsPilot platform.
 * Tracks the progression of work items through the operational workflow.
 */
public enum WorkItemStatus {
    /**
     * Work item has been created but not yet started
     */
    OPEN,

    /**
     * Work item is currently being worked on
     */
    IN_PROGRESS,

    /**
     * Work item has been successfully completed
     */
    COMPLETED,

    /**
     * Work item has been rejected and will not be completed
     */
    REJECTED
}

