package com.opspilot.platform.config;

import com.opspilot.platform.user.AccessRole;
import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.repository.EmployeeRepository;
import com.opspilot.platform.workitem.WorkItem;
import com.opspilot.platform.workitem.WorkItemStatus;
import com.opspilot.platform.workitem.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/**
 * Service for loading demo/sample data into the system.
 * Used by DataInitializer on startup and by the admin demo-data endpoint.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DemoDataService {

    private final EmployeeRepository employeeRepository;
    private final WorkItemRepository workItemRepository;

    private static final String[] WORK_ITEM_TITLES = {
        "Deploy Production Server",
        "Database Migration",
        "Fix Authentication Bug",
        "Update User Documentation",
        "Implement New Feature",
        "Security Audit",
        "Performance Optimization",
        "Code Review Required",
        "Setup CI/CD Pipeline",
        "Configure Load Balancer",
        "Backup Database",
        "Update SSL Certificates",
        "Monitor System Health",
        "Refactor Legacy Code",
        "API Integration",
        "Mobile App Release",
        "Customer Support Ticket",
        "Infrastructure Upgrade",
        "Data Analysis Report",
        "Team Meeting Preparation"
    };

    private static final String[] DESCRIPTIONS = {
        "This task requires immediate attention and should be completed by end of day.",
        "Please review the requirements carefully before starting this task.",
        "Coordinate with the development team for this implementation.",
        "Ensure all tests pass before marking this as complete.",
        "Documentation must be updated after completion.",
        "This is a high-priority task related to security compliance.",
        "Follow the standard operating procedures for this operation.",
        "Requires approval from the team lead before proceeding.",
        "Please allocate sufficient time for thorough testing.",
        "This task is part of the Q1 roadmap initiatives."
    };

    private final Random random = new Random();

    /**
     * Load demo work items assigned to existing operators with varied statuses.
     * Creates sample work items using existing employees in the system.
     *
     * @return number of work items created
     * @throws IllegalStateException if no operators or employees exist
     */
    @Transactional
    public int loadDemoWorkItems() {
        log.info("Loading demo work items...");

        List<Employee> allEmployees = employeeRepository.findAll();
        if (allEmployees.isEmpty()) {
            throw new IllegalStateException("No employees found. Register employees before loading demo data.");
        }

        List<Employee> operators = employeeRepository.findByRole(AccessRole.OPERATOR);
        if (operators.isEmpty()) {
            throw new IllegalStateException("No operators found. At least one OPERATOR role employee is required.");
        }

        WorkItemStatus[] statuses = WorkItemStatus.values();
        int count = 20;

        for (int i = 0; i < count; i++) {
            Employee creator = allEmployees.get(random.nextInt(allEmployees.size()));
            Employee assignee = operators.get(random.nextInt(operators.size()));
            WorkItemStatus status = statuses[random.nextInt(statuses.length)];

            String title = WORK_ITEM_TITLES[random.nextInt(WORK_ITEM_TITLES.length)] + " #DM-" + (i + 1);
            String description = DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)];

            WorkItem workItem = WorkItem.builder()
                    .title(title)
                    .description(description)
                    .status(status)
                    .createdBy(creator)
                    .assignedTo(assignee)
                    .build();

            workItemRepository.save(workItem);
            log.debug("Created demo WorkItem: {} - Status: {} - Assigned to: {}",
                    title, status, assignee.getEmail());
        }

        log.info("Successfully loaded {} demo work items", count);
        return count;
    }
}

