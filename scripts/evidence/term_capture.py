#!/usr/bin/env python3
"""
Terminal Output Capture Utility for Evidence Collection.
Executes a shell command, logs full output, renders a dark terminal-style PNG,
and records an entry in docs/evidence/MANIFEST.md.
"""

import argparse
import os
import re
import subprocess
import sys
from datetime import datetime
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont


def get_repo_root() -> Path:
    # This script is at scripts/evidence/term_capture.py
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


def load_font(size: int = 18):
    """Attempt to load a crisp monospace font, falling back gracefully."""
    font_candidates = [
        os.path.join(os.environ.get("WINDIR", "C:\\Windows"), "Fonts", "consola.ttf"),
        os.path.join(os.environ.get("WINDIR", "C:\\Windows"), "Fonts", "cour.ttf"),
        os.path.join(os.environ.get("WINDIR", "C:\\Windows"), "Fonts", "lucon.ttf"),
        "/usr/share/fonts/truetype/dejavu/DejaVuSansMono.ttf",
        "/usr/share/fonts/TTF/DejaVuSansMono.ttf",
        "/System/Library/Fonts/Monaco.ttf",
        "consola.ttf",
        "DejaVuSansMono.ttf",
    ]
    for path in font_candidates:
        if os.path.exists(path):
            try:
                return ImageFont.truetype(path, size=size)
            except Exception:
                continue
    try:
        return ImageFont.truetype("Courier", size=size)
    except Exception:
        return ImageFont.load_default()


def render_terminal_png(
    id_: str,
    caption: str,
    cmd: str,
    output_lines: list[str],
    tail: int,
    total_lines: int,
    output_path: Path,
    width: int = 1400,
):
    """Renders a modern dark terminal-style PNG."""
    font = load_font(size=18)
    small_font = load_font(size=14)

    # Window bar dimensions
    header_height = 42
    padding_x = 28
    padding_top = 18
    padding_bottom = 24
    line_spacing = 6

    # Measure line height
    bbox = font.getbbox("Ay$#M_g10")
    font_height = bbox[3] - bbox[1]
    line_height = font_height + line_spacing

    max_text_width = width - (padding_x * 2)

    # Wrap or prepare lines to render
    prepared_lines = []

    # Prompt + Command line
    prompt_str = f"$ {cmd}"
    prepared_lines.append(("prompt", prompt_str))

    # Truncation note if lines were truncated
    if total_lines > tail:
        k = total_lines - tail
        prepared_lines.append(("truncation", f"... ({k} earlier lines in logs/{id_}.txt) ..."))

    # Output lines with wrap handling
    for raw_line in output_lines:
        line = raw_line.rstrip("\r\n")
        if not line:
            prepared_lines.append(("output", ""))
            continue

        # Wrap long lines if needed
        cur = ""
        for char in line:
            test_line = cur + char
            b = font.getbbox(test_line)
            if (b[2] - b[0]) > max_text_width and cur:
                prepared_lines.append(("output", cur))
                cur = "  " + char  # Indent continuation line
            else:
                cur = test_line
        if cur:
            prepared_lines.append(("output", cur))

    # Calculate image height
    total_content_lines = len(prepared_lines)
    content_height = total_content_lines * line_height
    image_height = header_height + padding_top + content_height + padding_bottom
    image_height = max(image_height, 180)

    # Create image canvas
    bg_color = (24, 24, 37)        # Dark terminal background #181825
    header_bg = (30, 30, 46)       # Title bar #1e1e2e
    border_color = (69, 71, 90)    # Border #45475a

    img = Image.new("RGB", (width, image_height), color=bg_color)
    draw = ImageDraw.Draw(img)

    # Draw header bar
    draw.rectangle([(0, 0), (width, header_height)], fill=header_bg)
    draw.line([(0, header_height), (width, header_height)], fill=border_color, width=1)

    # Window control dots (Mac/Linux terminal style)
    dots = [
        (20, 21, (243, 139, 168)),  # Red
        (38, 21, (249, 226, 175)),  # Yellow
        (56, 21, (166, 227, 161)),  # Green
    ]
    for x, y, col in dots:
        draw.ellipse([(x - 6, y - 6), (x + 6, y + 6)], fill=col)

    # Title in header
    title_text = f"evidence/{id_} — {caption}"
    if len(title_text) > 85:
        title_text = title_text[:82] + "..."
    draw.text((82, 12), title_text, fill=(166, 173, 200), font=small_font)

    # Render lines
    y = header_height + padding_top
    for kind, text in prepared_lines:
        if kind == "prompt":
            # Render $ in green, command in white
            draw.text((padding_x, y), "$ ", fill=(166, 227, 161), font=font)
            prompt_w = font.getbbox("$ ")[2]
            draw.text((padding_x + prompt_w, y), cmd, fill=(245, 245, 245), font=font)
        elif kind == "truncation":
            draw.text((padding_x, y), text, fill=(249, 226, 175), font=font)
        else:
            # Determine color based on line content
            lowered = text.lower()
            if any(err_kw in lowered for err_kw in ["error", "exception", "fatal", "failed", "failure"]):
                text_color = (243, 139, 168)  # Soft red
            elif any(warn_kw in lowered for warn_kw in ["warn", "warning"]):
                text_color = (249, 226, 175)  # Soft yellow
            elif any(succ_kw in lowered for succ_kw in ["success", "build success", "passed"]):
                text_color = (166, 227, 161)  # Soft green
            else:
                text_color = (205, 214, 244)  # Crisp terminal foreground
            draw.text((padding_x, y), text, fill=text_color, font=font)
        y += line_height

    # Outer border
    draw.rectangle([(0, 0), (width - 1, image_height - 1)], outline=border_color, width=1)

    output_path.parent.mkdir(parents=True, exist_ok=True)
    img.save(output_path, "PNG")


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
    parser = argparse.ArgumentParser(description="Capture terminal command execution, log, and render PNG.")
    parser.add_argument("--id", required=True, help="Evidence ID (e.g. W01_a)")
    parser.add_argument("--week", required=True, help="Week identifier (e.g. 01 or 1)")
    parser.add_argument("--caption", required=True, help="Caption description")
    parser.add_argument("--cmd", required=True, help="Command to execute")
    parser.add_argument("--tail", type=int, default=45, help="Number of trailing output lines to display (default: 45)")
    parser.add_argument("--cwd", default=None, help="Working directory (default: repository root)")
    args = parser.parse_args()

    repo_root = get_repo_root()
    cwd = Path(args.cwd).resolve() if args.cwd else repo_root

    evidence_dir = repo_root / "docs" / "evidence"
    logs_dir = evidence_dir / "logs"
    manifest_path = evidence_dir / "MANIFEST.md"

    evidence_dir.mkdir(parents=True, exist_ok=True)
    logs_dir.mkdir(parents=True, exist_ok=True)

    local_dt = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # Run command
    try:
        proc = subprocess.run(
            args.cmd,
            shell=True,
            cwd=str(cwd),
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="utf-8",
            errors="replace",
        )
        raw_output = proc.stdout or ""
        exit_code = proc.returncode
    except Exception as e:
        raw_output = f"Execution error: {e}"
        exit_code = 1

    # Apply secret masking
    masked_cmd = mask_secrets(args.cmd)
    masked_output = mask_secrets(raw_output)

    # Save full raw log
    log_file = logs_dir / f"{args.id}.txt"
    with open(log_file, "w", encoding="utf-8") as f:
        f.write(f"$ {masked_cmd}\n")
        f.write(masked_output)
        if not masked_output.endswith("\n"):
            f.write("\n")
        f.write(f"[Process exited with code {exit_code}]\n")

    # Prepare lines for terminal image
    all_lines = masked_output.splitlines()
    total_lines = len(all_lines)
    tail_lines = all_lines[-args.tail:] if total_lines > args.tail else all_lines

    png_path = evidence_dir / f"{args.id}.png"
    render_terminal_png(
        id_=args.id,
        caption=args.caption,
        cmd=masked_cmd,
        output_lines=tail_lines,
        tail=args.tail,
        total_lines=total_lines,
        output_path=png_path,
        width=1400,
    )

    # Append to MANIFEST.md
    update_manifest(
        manifest_path=manifest_path,
        id_=args.id,
        week=args.week,
        kind="terminal",
        caption=args.caption,
        source=masked_cmd,
        dt_str=local_dt,
        exit_code=str(exit_code),
    )

    print(f"Captured {args.id}:")
    print(f"  Log:   {log_file}")
    print(f"  Image: {png_path}")
    print(f"  Exit code: {exit_code}")
    sys.exit(exit_code)


if __name__ == "__main__":
    main()
