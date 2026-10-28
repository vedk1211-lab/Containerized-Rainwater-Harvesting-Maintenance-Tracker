# Containerized Rainwater Harvesting Maintenance Tracker

An enterprise-ready, containerized Spring Boot web application for tracking, scheduling, and maintaining rainwater harvesting (RWH) infrastructure across municipal and residential facilities.

---

## 📌 Project Overview

The **Containerized Rainwater Harvesting (RWH) Maintenance Tracker** provides a centralized system to register rainwater harvesting installations, monitor individual components (filters, storage tanks, first flush diverters, pumps, pipes), log maintenance activities, compute next due dates, and trigger real-time alerts for overdue inspections.

---

## ✨ Features

- **System Registration & Management:** Register and inspect multi-site RWH systems with capacity, location, and owner details.
- **Component Lifecycle Tracking:** Monitor critical components categorized by type (`FILTER`, `STORAGE_TANK`, `FIRST_FLUSH_DIVERTER`, `PUMP`, `PIPES`) with custom inspection intervals.
- **Automated Due Date Calculation:** Automatically schedules and updates the next maintenance due date based on logged service intervals.
- **Overdue & Health Alerts:** Visual status indicators (`HEALTHY`, `DUE_SOON`, `OVERDUE`) and dedicated alerts overview.
- **Interactive Dashboard:** Real-time summary cards, keyword search across components, and per-component maintenance history audit logs.

---

## 🛠️ Technology Stack

| Layer | Technology | Details |
|---|---|---|
| **Backend** | Java 21 / Spring Boot 3.3.4 | Spring MVC, Spring Data JPA, Hibernate, Bean Validation |
| **Frontend** | Thymeleaf, Bootstrap 5.3.3 | Responsive UI with Bootstrap Icons |
| **Database** | PostgreSQL 16 / 18 | Relational persistence with auto-DDL update |
| **Containerization** | Docker & Docker Compose | Multi-stage Dockerfile (`eclipse-temurin:21`), PostgreSQL container |
| **CI/CD** | Jenkins | Two-stage pipeline: `rwh-tracker-pipeline` (CI) and `rwh-tracker-cd` (CD) |
| **Configuration** | Ansible | Automated configuration management and systemd service deployment |
| **Testing** | JUnit 5, H2 Database, Selenium | Automated unit, integration, and end-to-end browser tests |

---

## 📂 Folder Structure

```text
Containerized-Rainwater-Harvesting-Maintenance-Tracker/
├── .github/                       # GitHub issue & pull request templates
├── ansible/                       # Ansible playbooks, inventory, and templates
│   ├── group_vars/all/            # Configuration variables and vault examples
│   ├── templates/                 # systemd service and environment templates
│   ├── ansible.cfg                # Ansible settings
│   ├── inventory.ini              # Deployment target inventory
│   └── site.yml                   # Main provisioning playbook
├── docs/                          # Architecture, CI/CD evidence, and specifications
│   ├── evidence/                  # Generated evidence logs, screenshots, and manifest
│   ├── ci-cd-evidence.md          # CI pipeline execution records
│   ├── configuration-spec.md      # Environment configuration specification
│   ├── docker-command-log.txt     # Docker lifecycle execution log
│   ├── week12-cd-evidence.md      # Continuous deployment verification
│   └── week13-verification.txt    # End-to-end system verification
├── Documents/                     # Project planning and requirements deliverables
├── scripts/                       # Automation scripts and evidence capture tools
│   ├── evidence/                  # Terminal capture and headless browser screenshot tools
│   └── add-jenkins-credentials.ps1# Automation for Jenkins credential setup
├── src/                           # Application source code
│   ├── main/java/com/rwh/tracker/ # Controllers, Models, Repositories, Services
│   ├── main/resources/            # Application configuration and Thymeleaf templates
│   └── test/                      # Unit, integration, and Selenium browser tests
├── docker-compose.yml             # Local multi-container orchestration
├── Dockerfile                     # Multi-stage production container build
├── Jenkinsfile                    # CI pipeline definition (rwh-tracker-pipeline)
├── Jenkinsfile.cd                 # CD pipeline definition (rwh-tracker-cd)
└── pom.xml                        # Maven dependencies and build configuration
```

---

## 🚀 How to Run Locally

### Prerequisites
- **JDK 21** or later
- **Apache Maven 3.9+**
- **PostgreSQL 16+** (running on port `5432` with user `postgres` and password `postgres`)
- *Optional:* **Docker Desktop** (if running via Docker Compose)

### Method 1: Running with Maven
```bash
# 1. Clone the repository
git clone https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker.git
cd Containerized-Rainwater-Harvesting-Maintenance-Tracker

# 2. Compile and run the Spring Boot application
mvn spring-boot:run
```
Access the application at: [http://localhost:8081](http://localhost:8081)

### Method 2: Running with Docker Compose
```bash
# Build images and start containers (app + database)
docker compose up --build
```
Access the application at: [http://localhost:8081](http://localhost:8081)

---

## 🔄 CI/CD Automation Summary

The repository uses a dual-pipeline Jenkins automation setup:
1. **CI Pipeline (`rwh-tracker-pipeline`):**
   - Triggers on commit/push.
   - Cleans and compiles source code with Maven.
   - Runs unit tests and headless Selenium browser tests against an in-memory H2 database.
   - Builds the production WAR package and builds the versioned Docker image.
   - Automatically triggers the downstream CD pipeline upon successful test completion.
2. **CD Pipeline (`rwh-tracker-cd`):**
   - Receives build artifacts from `rwh-tracker-pipeline`.
   - Provisions and verifies target database connectivity.
   - Deploys the containerized application to staging/production environments.
   - Executes smoke tests and health checks.

Detailed logs and execution evidence are available in the [`docs/`](docs/) directory.
