# Configuration Specification: Rainwater Harvesting Maintenance Tracker Node

This specification defines the configuration state managed by Ansible for the Containerized Rainwater Harvesting (RWH) Maintenance Tracker application node on Ubuntu 24.04 LTS (WSL2).

---

## 1. System Packages

| Package Name | Purpose | Managed By |
| :--- | :--- | :--- |
| `curl` | HTTP client for health checks and API requests | APT (`base_packages`) |
| `git` | Version control tooling for node maintenance | APT (`base_packages`) |
| `ca-certificates` | Trusted TLS certificate authorities for secure container registry / HTTPS access | APT (`base_packages`) |
| `docker.io` | Docker container runtime engine (installed when `manage_docker_engine: true`) | APT |

---

## 2. Users and Groups

| Entity | Type | Shell / Home | Supplementary Groups | Purpose |
| :--- | :--- | :--- | :--- | :--- |
| `rwh` | System Group | N/A | N/A | Dedicated security group for application processes and configuration ownership |
| `rwh` | System User | `/usr/sbin/nologin`, `/opt/rwh-tracker` | `docker` | Non-privileged service account running container tasks and owning config files |

---

## 3. Directory Structure

| Path | Owner | Group | Mode | Purpose |
| :--- | :--- | :--- | :--- | :--- |
| `/opt/rwh-tracker` | `rwh` | `rwh` | `0755` | Application home directory |
| `/opt/rwh-tracker/config` | `rwh` | `rwh` | `0750` | Restricted configuration directory storing environment files |
| `/var/log/rwh-tracker` | `rwh` | `rwh` | `0755` | Persistent application logging directory |

---

## 4. Managed Configuration Files

| Path | Purpose | Owner | Group | Mode |
| :--- | :--- | :--- | :--- | :--- |
| `/opt/rwh-tracker/config/rwh.env` | Spring Boot environment variables (Datasource URL, user, and vaulted DB password) | `rwh` | `rwh` | `0640` |
| `/etc/systemd/system/rwh-tracker.service` | Systemd service unit governing the Docker container lifecycle | `root` | `root` | `0644` |

---

## 5. Network Ports

| Port | Direction | Protocol | Purpose |
| :--- | :--- | :--- | :--- |
| `8081` | Inbound | TCP | Host application port mapped to container port 8080 |
| `8080` | Container Internal | TCP | Embedded Tomcat Spring MVC HTTP service port |
| `5432` | Outbound | TCP | Connection to external PostgreSQL database (`host.docker.internal:5432`) |

---

## 6. System Services

| Service Name | Enabled | Target State | Description |
| :--- | :--- | :--- | :--- |
| `docker` | Yes | Started (`started`) | Container engine daemon (managed when `manage_docker_engine: true`) |
| `rwh-tracker` | Yes | Inactive (`stopped`) | Application systemd service unit; enabled for boot but not started until Week 14 |

---

## 7. External Dependencies

| Dependency | Location | Description |
| :--- | :--- | :--- |
| PostgreSQL Database | `host.docker.internal:5432/rwh_tracker` | External relational database hosted on Windows host machine |
| Docker Registry | Docker Hub (`vedantkulkarni12/rwh-tracker:latest`) | Remote container registry storing application images |

---

## 8. Ansible Variables

| Variable Name | Default Value | Description |
| :--- | :--- | :--- |
| `app_user` | `rwh` | System username for application execution |
| `app_group` | `rwh` | System group for application execution |
| `app_home` | `/opt/rwh-tracker` | Application base directory |
| `config_dir` | `/opt/rwh-tracker/config` | Path where environment and config files reside |
| `log_dir` | `/var/log/rwh-tracker` | Path where application logs are stored |
| `app_port` | `8081` | External host listening port |
| `container_port` | `8080` | Container internal listening port |
| `image_name` | `vedantkulkarni12/rwh-tracker` | Container image repository |
| `image_tag` | `latest` | Container image tag |
| `db_host` | `host.docker.internal` | PostgreSQL hostname reachable from container |
| `db_port` | `5432` | PostgreSQL port |
| `db_name` | `rwh_tracker` | PostgreSQL database name |
| `db_user` | `postgres` | PostgreSQL username |
| `base_packages` | `[curl, git, ca-certificates]` | Base utilities installed via APT |
| `manage_docker_engine` | `true` / `false` | Controls whether Ansible manages `docker.io` installation |
| `manage_firewall` | `false` | Controls whether UFW rules are applied |

---

## 9. Secrets Handling (Ansible Vault)

Sensitive credentials (the PostgreSQL database password) are secured with **Ansible Vault**:
- Encrypted file: `ansible/group_vars/all/vault.yml` containing `vault_db_password`.
- Password file: `~/.rwh-vault-pass` on the target control machine (mode `0600`).
- Templating tasks masking: All tasks rendering `vault_db_password` specify `no_log: true` and `diff: false` to guarantee secrets are never printed in console logs, diffs, or reports.
- `ansible/group_vars/all/vault.yml` is excluded from git tracking via `.gitignore`. An unencrypted template is provided as `vault.yml.example`.

---

## 10. How to Run

Execute all commands from the repository root inside the target WSL2 node:

1. **Syntax Check:**
   ```bash
   ANSIBLE_CONFIG=ansible/ansible.cfg ansible-playbook ansible/site.yml --syntax-check
   ```

2. **Dry Run (Check Mode):**
   ```bash
   ANSIBLE_CONFIG=ansible/ansible.cfg ansible-playbook ansible/site.yml --check
   ```

3. **Production Idempotent Run:**
   ```bash
   ANSIBLE_CONFIG=ansible/ansible.cfg ansible-playbook ansible/site.yml
   ```
