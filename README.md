# OpsPilot — Operations Management Platform

A role-based operations management backend for tracking, assigning, and executing work items across teams.

Built with **Spring Boot 3**, secured with **JWT authentication**, and designed around a clear **Admin → Operator → Viewer** workflow.

---

## Key Features

- **JWT Authentication** — Stateless, token-based login and registration
- **Role-Based Access Control** — Three distinct roles with scoped permissions
- **Work Item Lifecycle** — Create, assign, track, and resolve operational tasks
- **Admin Dashboard** — Aggregated metrics across all work items
- **Pagination & Sorting** — Efficient data retrieval on all list endpoints
- **Demo Data Seeding** — Pre-loaded employees and work items on startup
- **Interactive API Docs** — Swagger UI via SpringDoc OpenAPI

---

## Role-Based Workflow

| Role | Responsibilities |
|------|-----------------|
| **Admin** | Full access — create work items, assign to operators, view dashboard metrics, manage employees, load demo data |
| **Operator** | Create and manage own work items, update statuses (Open → In Progress → Completed / Rejected) |
| **Viewer** | Read-only visibility into the platform (future scope) |

**Work Item Statuses:** `OPEN` → `IN_PROGRESS` → `COMPLETED` · `REJECTED`

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.5 |
| Language | Java 21 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | H2 in-memory (PostgreSQL-ready) |
| ORM | Hibernate / Spring Data JPA |
| API Docs | SpringDoc OpenAPI 2.7 |
| Build | Maven |
| Utilities | Lombok |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.6+

### Run Locally

```bash
git clone <repository-url>
cd opspilot-backend

./mvnw spring-boot:run
```

The server starts at **http://localhost:8080**.

| Resource | URL |
|----------|-----|
| API Base | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| H2 Console | `http://localhost:8080/h2-console` |

---

## Demo Credentials

The application seeds sample data automatically on startup.

**Default password for all users:** `Password123`

| Role | Email Pattern | Count |
|------|--------------|-------|
| Admin | `admin1@opspilot.com` – `admin5@opspilot.com` | 5 |
| Operator | `operator1@opspilot.com` – `operator10@opspilot.com` | 10 |
| Viewer | `viewer1@opspilot.com` – `viewer5@opspilot.com` | 5 |

Seeded data includes **20 employees** and **50 work items** in varied statuses.

### Quick Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin1@opspilot.com", "password": "Password123"}'
```

Use the returned `token` as `Authorization: Bearer <token>` on all protected endpoints.

---

## API Overview

### Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/health` | Health check |
| `POST` | `/api/auth/register` | Register new employee |
| `POST` | `/api/auth/login` | Login and receive JWT |

### Authenticated — Admin & Operator

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/workitems` | Create a work item |
| `GET` | `/api/workitems/my` | Get my work items |
| `GET` | `/api/workitems/my/paginated` | Get my work items (paginated) |
| `PUT` | `/api/workitems/{id}/status` | Update work item status |

### Admin Only

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/admin/workitems` | All work items (paginated) |
| `PUT` | `/api/admin/workitems/{id}/assign` | Assign work item to employee |
| `GET` | `/api/admin/dashboard` | Dashboard metrics |
| `GET` | `/api/admin/employees` | List employees (paginated) |
| `GET` | `/api/admin/employees/operators` | List operators |
| `GET` | `/api/admin/employees/{id}` | Get employee by ID |
| `POST` | `/api/admin/demo-data/load` | Load additional demo data |

---

## Project Structure

```
com.opspilot.platform/
├── admin/controller/        Admin-only endpoints
├── auth/controller/, dto/   Authentication & login DTOs
├── common/controller/, dto/ Health check
├── config/                  Security, OpenAPI, data seeding
├── exception/               Global error handling
├── security/                JWT filter, token provider, user details
├── user/                    Employee entity, service, repository, DTOs
└── workitem/                WorkItem entity, service, repository, DTOs
```

---

## License

Proprietary — All rights reserved.
