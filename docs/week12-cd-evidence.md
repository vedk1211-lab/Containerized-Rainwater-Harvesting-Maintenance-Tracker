# Week 12: CI/CD Pipeline Automation Evidence

## 1. Pipeline Overview & Architecture

Two distinct Jenkins pipelines have been established with a clean separation of concerns:
- **CI Job**: `rwh-tracker-pipeline` handles checkout, build, unit/integration testing, and upon success, automatically triggers the CD job asynchronously.
- **CD Job**: `rwh-tracker-cd` handles Docker diagnostics, multi-stage image building, Docker Hub publishing, container lifecycle deployment with PostgreSQL 15, and automated HTTP health checking.

### Job 1: `rwh-tracker-pipeline` (CI Only)
- **Triggers**: SCM Polling (`H/5 * * * *`)
- **Parameters**: `DEPLOY_ENV` (choices: `dev`, `staging`)
- **Stages**:
  1. `Checkout`
  2. `Build` (`mvn clean compile`)
  3. `Test` (`mvn test -Dspring.profiles.active=test`, JUnit reporting)
- **Post-Action**: On success, triggers `rwh-tracker-cd` (`build job: 'rwh-tracker-cd', wait: false`)

### Job 2: `rwh-tracker-cd` (CD Only)
- **Triggers**: None (triggered downstream by `rwh-tracker-pipeline`)
- **Parameters**: None
- **Stages**:
  1. `Checkout`
  2. `Docker Check` (`where docker`, `docker version`)
  3. `Docker Build` (`docker build -t %IMAGE_NAME%:%IMAGE_TAG% -t %IMAGE_NAME%:latest --label git.commit=%GIT_COMMIT% .`)
  4. `Docker Push` (with local `.docker-push` config, pushing `1.0.<BUILD_NUMBER>` and `latest`)
  5. `Deploy Container` (Network `rwh-ci-net`, PostgreSQL container `rwh-ci-db`, Application container `rwh-tracker`)
  6. `Health Check` (PowerShell polling `http://localhost:8081/systems` every 5s up to 2m)
- **Post-Action**: Echoes deployed image tag on success.

---

## 2. Automated Commit-to-Container Chain Execution

- **Trigger Commit**: `cb77e8a` ("Trigger CI/CD chain test")
- **CI Build Number**: `#14`
  - **Trigger Cause**: `Started by an SCM change`
  - **Status**: `SUCCESS` (6 tests run, 0 failures, 0 errors)
- **CD Build Number**: `#3`
  - **Trigger Cause**: `Started by upstream project "rwh-tracker-pipeline" build number 14`
  - **Status**: `SUCCESS`

---

## 3. Docker Image & Registry Evidence

- **Image Repository**: `vedantkulkarni12/rwh-tracker`
- **Pushed Tags**:
  - `vedantkulkarni12/rwh-tracker:1.0.3` (digest: `sha256:4c7d3f7c12c6f9bf5d0b31573343e2afde1ba38034db407b870420de542e8056`)
  - `vedantkulkarni12/rwh-tracker:latest` (digest: `sha256:4c7d3f7c12c6f9bf5d0b31573343e2afde1ba38034db407b870420de542e8056`)

---

## 4. Container Runtime Status (`docker ps`)

```
CONTAINER ID   IMAGE                                COMMAND                  CREATED          STATUS          PORTS                                         NAMES
f65cf7f8a506   vedantkulkarni12/rwh-tracker:1.0.3   "java -jar app.war -…"   Up 2 minutes    0.0.0.0:8081->8080/tcp, [::]:8081->8080/tcp   rwh-tracker
b709ebb93b35   postgres:15                          "docker-entrypoint.s…"   Up 10 minutes   5432/tcp                                      rwh-ci-db
```

---

## 5. Application Health Verification

- **Endpoint**: `http://localhost:8081/systems`
- **HTTP Status**: `200 OK`
- **Response**: HTML interface for RWH system management successfully rendered.

---

## 6. Git Commits in Chain

- `cb77e8a` - Trigger CI/CD chain test
- `d5b94c9` - CI success now triggers the CD job
- `24e44f7` - Add Jenkinsfile.cd for the CD pipeline
- `c1d6a1a` - Split pipeline: CI job only runs build and tests
- `cdf5610` - Add Ansible configuration spec, playbook and first run log for RWH node

---

## 7. Build Logs
Full console outputs saved to:
- `docs/logs/ci-build-14.log`
- `docs/logs/cd-build-3.log`
