package com.opspilot.platform.config;

import com.opspilot.platform.user.AccessRole;
import com.opspilot.platform.user.Employee;
import com.opspilot.platform.user.repository.EmployeeRepository;
import com.opspilot.platform.workitem.WorkItem;
import com.opspilot.platform.workitem.WorkItemStatus;
import com.opspilot.platform.workitem.repository.WorkItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Data Initializer for seeding the database with realistic demo data.
 * Runs on application startup and creates employees and work items.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final WorkItemRepository workItemRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "Password123";

    private static final String[] FIRST_NAMES = {
        "James", "Mary", "John", "Patricia", "Robert", "Jennifer", "Michael", "Linda",
        "William", "Elizabeth", "David", "Barbara", "Richard", "Susan", "Joseph", "Jessica",
        "Thomas", "Sarah", "Charles", "Karen", "Christopher", "Nancy", "Daniel", "Lisa"
    };

    private static final String[] LAST_NAMES = {
        "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
        "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
        "Taylor", "Moore", "Jackson", "Martin", "Lee", "Thompson", "White", "Harris"
    };

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

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");

        // Check if data already exists
        if (employeeRepository.count() > 0) {
            log.info("Data already exists. Skipping initialization.");
            return;
        }

        // Create employees
        List<Employee> employees = createEmployees();
        log.info("Created {} employees", employees.size());

        // Create work items
        int workItemCount = createWorkItems(employees);
        log.info("Created {} work items", workItemCount);

        log.info("Data initialization completed successfully!");
        log.info("Default password for all users: {}", DEFAULT_PASSWORD);
        log.info("Sample admin user: admin1@opspilot.com");
        log.info("Sample operator user: operator1@opspilot.com");
    }

    private List<Employee> createEmployees() {
        List<Employee> employees = new ArrayList<>();
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);

        // Create 5 ADMIN users
        for (int i = 1; i <= 5; i++) {
            Employee admin = Employee.builder()
                    .email(String.format("admin%d@opspilot.com", i))
                    .password(encodedPassword)
                    .fullName(generateFullName())
                    .role(AccessRole.ADMIN)
                    .active(true)
                    .build();
            employees.add(employeeRepository.save(admin));
            log.debug("Created ADMIN: {}", admin.getEmail());
        }

        // Create 10 OPERATOR users
        for (int i = 1; i <= 10; i++) {
            Employee operator = Employee.builder()
                    .email(String.format("operator%d@opspilot.com", i))
                    .password(encodedPassword)
                    .fullName(generateFullName())
                    .role(AccessRole.OPERATOR)
                    .active(true)
                    .build();
            employees.add(employeeRepository.save(operator));
            log.debug("Created OPERATOR: {}", operator.getEmail());
        }

        // Create 5 VIEWER users
        for (int i = 1; i <= 5; i++) {
            Employee viewer = Employee.builder()
                    .email(String.format("viewer%d@opspilot.com", i))
                    .password(encodedPassword)
                    .fullName(generateFullName())
                    .role(AccessRole.VIEWER)
                    .active(true)
                    .build();
            employees.add(employeeRepository.save(viewer));
            log.debug("Created VIEWER: {}", viewer.getEmail());
        }

        return employees;
    }

    private int createWorkItems(List<Employee> employees) {
        List<Employee> operators = employees.stream()
                .filter(e -> e.getRole() == AccessRole.OPERATOR)
                .toList();

        WorkItemStatus[] statuses = WorkItemStatus.values();

        for (int i = 0; i < 50; i++) {
            Employee creator = employees.get(random.nextInt(employees.size()));
            Employee assignee = operators.get(random.nextInt(operators.size()));
            WorkItemStatus status = statuses[random.nextInt(statuses.length)];

            String title = WORK_ITEM_TITLES[random.nextInt(WORK_ITEM_TITLES.length)] + " #" + (i + 1);
            String description = DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)];

            WorkItem workItem = WorkItem.builder()
                    .title(title)
                    .description(description)
                    .status(status)
                    .createdBy(creator)
                    .assignedTo(assignee)
                    .build();

            workItemRepository.save(workItem);
            log.debug("Created WorkItem: {} - Status: {}", title, status);
        }

        return 50;
    }

    private String generateFullName() {
        String firstName = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String lastName = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        return firstName + " " + lastName;
    }
}

