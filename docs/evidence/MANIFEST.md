# Evidence Manifest

| id | week | kind | caption | source | local date-time | exit code |
|---|---|---|---|---|---|---|
| W01_a | 01 | terminal | Clean 2-level tree listing of repository structure | `python scripts/evidence/tree.py` | 2026-10-29 01:22:30 | 0 |
| W02_a | 02 | browser | GitHub repository docs directory | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/tree/main/docs` | 2026-10-29 01:23:34 | 0 |
| W02_b | 02 | browser | GitHub repository Documents directory | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/tree/main/Documents` | 2026-10-29 01:23:52 | 0 |
| W02_c | 02 | terminal | Git commit history for docs and Documents | `git log --oneline -- docs Documents` | 2026-10-29 01:24:06 | 0 |
| W05_a | 05 | terminal | Git branch topology with feature branch commits | `git log --oneline --graph --decorate -15` | 2026-10-29 01:28:59 | 0 |
| W05_b | 05 | browser | Pull Request 4 opened into develop branch | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/pull/4` | 2026-10-29 01:29:23 | 0 |
| W05_c | 05 | browser | Pull Request 4 with review and discussion comments | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/pull/4` | 2026-10-29 01:29:53 | 0 |
| W05_d | 05 | browser | Pull Request 4 merged into develop | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/pull/4` | 2026-10-29 01:30:22 | 0 |
| W05_e | 05 | terminal | Git graph history on develop following PR merge | `git log --oneline --graph --decorate -15` | 2026-10-29 01:30:51 | 0 |
| W06_e | 06 | terminal | Complete Git commit and merge history across all branches | `git log --oneline --graph --all -20` | 2026-10-29 01:31:50 | 0 |
| W04_a | 04 | browser | GitHub repository home with README and project overview | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker` | 2026-10-29 01:34:50 | 0 |
| W04_b | 04 | browser | GitHub commits history on main branch | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/commits/main` | 2026-10-29 01:35:07 | 0 |
| W04_c | 04 | browser | GitHub repository branches overview | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/branches` | 2026-10-29 01:35:20 | 0 |
| W04_d | 04 | browser | GitHub issues showing delivered retrospective issues | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/issues?q=is%3Aissue` | 2026-10-29 01:35:35 | 0 |
| W04_e | 04 | terminal | Listing of local and remote repository branches | `git branch -a` | 2026-10-29 01:35:48 | 0 |
| W06_a | 06 | terminal | Merge conflict triggered between demo/conflict-a and demo/conflict-b | `git merge demo/conflict-b` | 2026-10-29 01:38:43 | 128 |
| W06_a | 06 | terminal | Merge conflict triggered between demo/conflict-a and demo/conflict-b | `type docs\evidence\conflict-demo.md && echo --- && git status --short` | 2026-10-29 01:39:08 | 0 |
| W06_b | 06 | terminal | Conflict markers in docs/evidence/conflict-demo.md | `type docs\evidence\conflict-demo.md` | 2026-10-29 01:39:16 | 0 |
| W06_c | 06 | terminal | Resolved merge conflict and branch graph | `echo === Resolved file === && type docs\evidence\conflict-demo.md && echo. && echo === Git log graph === && git log --oneline --graph --all -10` | 2026-10-29 01:40:45 | 0 |
| W06_d | 06 | terminal | Annotated tag v0.1.0-mvp and tagged commit details | `git tag -n && echo. && git show v0.1.0-mvp --stat` | 2026-10-29 01:41:37 | 0 |
| W06_f | 06 | browser | GitHub repository tags page showing v0.1.0-mvp | `https://github.com/vedk1211-lab/Containerized-Rainwater-Harvesting-Maintenance-Tracker/tags` | 2026-10-29 01:41:45 | 0 |
