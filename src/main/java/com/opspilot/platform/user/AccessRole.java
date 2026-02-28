package com.opspilot.platform.user;

/**
 * Enum representing the different access roles in the OpsPilot platform.
 * Defines the hierarchy of permissions for users in the system.
 */
public enum AccessRole {
    /**
     * Administrator with full system access and privileges
     */
    ADMIN,

    /**
     * Operator with ability to manage and execute work items
     */
    OPERATOR,

    /**
     * Viewer with read-only access to the platform
     */
    VIEWER
}

