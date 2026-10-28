#!/usr/bin/env python3
"""
Browser Screenshot Capture Utility for Evidence Collection.
Uses Playwright Chromium (headless) to capture web pages and records
entries in docs/evidence/MANIFEST.md.
"""

import argparse
import os
import re
import sys
from datetime import datetime
from pathlib import Path
from playwright.sync_api import sync_playwright


def get_repo_root() -> Path:
    return Path(__file__).resolve().parent.parent.parent


def mask_secrets(text: str) -> str:
    """Mask values matching password|passwd|token|secret|PASS= with ******."""
    if not text:
        return ""
    patterns = [
        r'(?i)\b(password|passwd|token|secret|pass)(=|:\s*|\s+)([^\s"\'<>]+|"[^"]*"|\'[^\']*\')',
        r'(?i)(PASS=)([^\s"\'<>]+|"[^"]*"|\'[^\']*\')',
    ]
    res = text
    for pat in patterns:
        def repl(m):
            if len(m.groups()) == 3:
                return f"{m.group(1)}{m.group(2)}******"
            elif len(m.groups()) == 2:
                return f"{m.group(1)}******"
            return m.group(0)
        res = re.sub(pat, repl, res)
    return res


def update_manifest(manifest_path: Path, id_: str, week: str, kind: str, caption: str, source: str, dt_str: str, exit_code: str):
    """Appends an evidence row to MANIFEST.md, creating header if needed."""
    manifest_path.parent.mkdir(parents=True, exist_ok=True)
    header = (
        "# Evidence Manifest\n\n"
        "| id | week | kind | caption | source | local date-time | exit code |\n"
        "|---|---|---|---|---|---|---|\n"
    )

    clean_caption = caption.replace("|", "\\|")
    clean_source = source.replace("|", "\\|")
    row = f"| {id_} | {week} | {kind} | {clean_caption} | `{clean_source}` | {dt_str} | {exit_code} |\n"

    if not manifest_path.exists():
        with open(manifest_path, "w", encoding="utf-8") as f:
            f.write(header)
            f.write(row)
    else:
        content = manifest_path.read_text(encoding="utf-8")
        if "| id | week |" not in content:
            with open(manifest_path, "w", encoding="utf-8") as f:
                f.write(header)
                f.write(row)
        else:
            with open(manifest_path, "a", encoding="utf-8") as f:
                f.write(row)


def main():
    parser = argparse.ArgumentParser(description="Capture browser page screenshot with Playwright.")
    parser.add_argument("--id", required=True, help="Evidence ID (e.g. W01_b)")
    parser.add_argument("--week", required=True, help="Week identifier (e.g. 01 or 1)")
    parser.add_argument("--caption", required=True, help="Caption description")
    parser.add_argument("--url", required=True, help="Target URL to capture")
    parser.add_argument("--scroll-bottom", action="store_true", help="Scroll to bottom before capturing")
    parser.add_argument("--full-page", action="store_true", help="Capture full scrollable page")
    parser.add_argument("--wait-ms", type=int, default=1500, help="Wait time in ms after load (default: 1500)")
    parser.add_argument("--width", type=int, default=1600, help="Viewport width (default: 1600)")
    parser.add_argument("--height", type=int, default=1000, help="Viewport height (default: 1000)")
    args = parser.parse_args()

    repo_root = get_repo_root()
    evidence_dir = repo_root / "docs" / "evidence"
    manifest_path = evidence_dir / "MANIFEST.md"

    evidence_dir.mkdir(parents=True, exist_ok=True)
    img_path = evidence_dir / f"{args.id}.png"

    local_dt = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    masked_url = mask_secrets(args.url)

    exit_code = 0
    try:
        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            context = browser.new_context(
                viewport={"width": args.width, "height": args.height},
                device_scale_factor=1,
            )
            page = context.new_page()

            # Navigate to URL
            page.goto(args.url, wait_until="load", timeout=60000)

            # Optional post-load wait
            if args.wait_ms > 0:
                page.wait_for_timeout(args.wait_ms)

            # Optional scroll to bottom
            if args.scroll_bottom:
                page.evaluate("window.scrollTo(0, document.body.scrollHeight)")
                page.wait_for_timeout(500)

            # Capture screenshot
            page.screenshot(path=str(img_path), full_page=args.full_page)
            browser.close()
    except Exception as e:
        print(f"Error capturing browser screenshot: {e}", file=sys.stderr)
        exit_code = 1

    # Record in manifest
    update_manifest(
        manifest_path=manifest_path,
        id_=args.id,
        week=args.week,
        kind="browser",
        caption=args.caption,
        source=masked_url,
        dt_str=local_dt,
        exit_code=str(exit_code),
    )

    if exit_code == 0:
        print(f"Captured {args.id}:")
        print(f"  Image: {img_path}")
        print(f"  URL:   {masked_url}")
    sys.exit(exit_code)


if __name__ == "__main__":
    main()
