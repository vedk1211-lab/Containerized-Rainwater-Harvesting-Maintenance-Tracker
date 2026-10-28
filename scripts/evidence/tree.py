#!/usr/bin/env python3
"""Print a clean 2-level directory tree excluding .git, target, and cache."""
from pathlib import Path
import sys

def print_tree(dir_path: Path, prefix: str = "", depth: int = 0, max_depth: int = 2):
    if depth >= max_depth:
        return
    ignored = {".git", "target", "__pycache__", ".pytest_cache", ".idea", ".vscode"}
    entries = sorted(
        [p for p in dir_path.iterdir() if p.name not in ignored],
        key=lambda p: (not p.is_dir(), p.name.lower()),
    )
    for i, p in enumerate(entries):
        is_last = (i == len(entries) - 1)
        connector = "└── " if is_last else "├── "
        print(f"{prefix}{connector}{p.name}")
        if p.is_dir() and depth + 1 < max_depth:
            extension = "    " if is_last else "│   "
            print_tree(p, prefix + extension, depth + 1, max_depth)

if __name__ == "__main__":
    if sys.stdout.encoding.lower() != "utf-8":
        sys.stdout.reconfigure(encoding="utf-8")
    print(".")
    print_tree(Path("."))
