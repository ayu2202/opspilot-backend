# OpsPilot — Operations Management Platform

OpsPilot is a **role-based operations management platform** that helps teams **assign, execute, and track internal work in real time**.

It’s designed for operational clarity: admins assign work, operators execute it, and stakeholders can follow progress through a consistent work-item lifecycle.

---

## Project Overview

OpsPilot centralizes day-to-day operational tasks (“work items”) so teams can:
- assign ownership clearly,
- track status changes over time,
- and understand what’s happening right now across the organization.

---

## Key Features

- **Admin task assignment** — admins can assign work items to operators
- **Operator execution workflow** — operators work tasks and update status as progress changes
- **Viewer visibility** — viewers can observe progress (read-only)
- **Status lifecycle tracking** — work moves through defined states (`OPEN`, `IN_PROGRESS`, `COMPLETED`, `REJECTED`)
- **Secure authentication** — JWT-based login with role-based access control

---

## Roles

- **Admin** → Assigns work
- **Operator** → Executes work
- **Viewer** → Observes progress

---

## Workflow

**Admin assigns → Operator executes → Status updates → Dashboard reflects**

1. Admin creates/assigns a work item to an operator
2. Operator works on the task and updates status as it progresses
3. Status changes are reflected across the platform (including dashboard metrics)

---

## Tech Stack

- **Spring Boot** (Java)
- **JWT Security** (Spring Security + stateless tokens)
- **H2 Database** (in-memory for local/demo use)

---

## Demo Credentials

All demo users use the password: **`Password123`**

- **Admin**
  - Email: `admin1@opspilot.com`
  - Password: `Password123`

- **Operator**
  - Email: `operator1@opspilot.com`
  - Password: `Password123`

- **Viewer**
  - Email: `viewer1@opspilot.com`
  - Password: `Password123`

---

## Run Locally

```bash
./mvnw spring-boot:run
```

Server starts at: **http://localhost:${PORT:-8080}**

- Swagger UI: `http://localhost:${PORT:-8080}/swagger-ui.html`
- Health check: `http://localhost:${PORT:-8080}/api/health`
