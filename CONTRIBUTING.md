# Contributing to RWH Maintenance Tracker

Thank you for your interest in contributing to the Containerized Rainwater Harvesting Maintenance Tracker! Please read and adhere to our development and contribution guidelines.

---

## 🌿 Branch Naming Conventions

All development must occur on dedicated branches cut from `develop`. Use one of the following prefixes based on the nature of the change:

- `feature/<short-description>`: New features, enhancements, or UI components (e.g. `feature/export-csv-reports`).
- `fix/<short-description>`: Bug fixes or patches for issues (e.g. `fix/due-date-boundary-calculation`).
- `chore/<short-description>`: Build configurations, dependency bumps, or tool maintenance (e.g. `chore/upgrade-playwright`).
- `docs/<short-description>`: Documentation additions, guides, or diagram updates (e.g. `docs/api-specification`).

Never push directly to `main` or `develop`.

---

## 📝 Commit Conventions

We follow Conventional Commits formatting. Each commit message should be structured as follows:

```text
<type>: <short imperative summary>

[optional longer description]
```

### Commit Types
- `feat`: A new user-facing or system feature.
- `fix`: A bug fix.
- `docs`: Documentation-only changes.
- `style`: Formatting, missing semicolons, white-space changes with no code effect.
- `refactor`: Code restructuring without bug fixes or new features.
- `test`: Adding missing tests or correcting existing tests.
- `chore`: Maintenance tasks, build tool adjustments, dependency updates.

### Rules
- Use imperative mood in the subject line (e.g., "add", "fix", "update", not "added" or "adds").
- Keep the subject line under 72 characters.
- Never include secrets, tokens, or plaintext credentials in commit messages or code.

---

## 🔀 Pull Request (PR) Workflow

1. **Branch Off `develop`:**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/your-feature-name
   ```
2. **Implement & Test:**
   - Make small, atomic, logical commits.
   - Run local validation:
     ```bash
     mvn clean test
     ```
3. **Push to Remote:**
   ```bash
   git push -u origin feature/your-feature-name
   ```
4. **Open a Pull Request into `develop`:**
   - Fill in the PR template completely with description, checklist, and related issues.
   - Address peer review feedback promptly.
5. **Merge:**
   - Once approved by reviewers and CI checks pass on `rwh-tracker-pipeline`, merge into `develop`.
   - Releases from `develop` into `main` are handled via tagged release PRs.
