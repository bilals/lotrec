#!/usr/bin/env python3
"""
diagnostics-filter.py - Filter and summarize VS Code diagnostics (SonarQube/Java)

Reads diagnostics JSON from VS Code's getDiagnostics API and produces
filtered, human-readable summaries.

Usage:
    python tools/diagnostics-filter.py <diagnostics.json> [options]

Options:
    --path <path>        Filter by file path substring (e.g., src/lotrec/parser)
    --severity <level>   Filter by severity: Error, Warning, Information, Hint
                         (case-insensitive, comma-separated for multiple)
    --source <source>    Filter by source: Java, sonarqube (default: all)
    --code <code>        Filter by diagnostic code (e.g., java:S120, 268435844)
    --top <n>            Show top N diagnostic categories (default: 20)
    --files              Show per-file breakdown
    --verbose            Show individual diagnostic messages
    --export <file>      Export filtered results to JSON file

Examples:
    # All issues in src/lotrec at Warning level
    python tools/diagnostics-filter.py diag.json --path src/lotrec --severity Warning

    # Errors only across entire project
    python tools/diagnostics-filter.py diag.json --severity Error

    # Detailed view of parser package issues
    python tools/diagnostics-filter.py diag.json --path src/lotrec/parser --verbose

    # Multiple severities
    python tools/diagnostics-filter.py diag.json --severity Error,Warning

    # Export filtered results
    python tools/diagnostics-filter.py diag.json --path src/lotrec --export filtered.json
"""

import argparse
import json
import sys
import os
from collections import Counter, defaultdict
from pathlib import PurePosixPath


# Well-known Java compiler diagnostic codes mapped to short descriptions
JAVA_CODE_DESCRIPTIONS = {
    "16777788": "Raw type usage (missing generics)",
    "16777747": "Type safety: raw type method",
    "268435844": "Unused import",
    "67110266": "Deprecated constructor (marked for removal)",
    "536871362": "TODO comment",
    "16777748": "Unchecked conversion",
    "536870973": "Unused local variable",
    "67108967": "Deprecated method usage",
    "570425421": "Unused field",
    "16777221": "Deprecated type usage",
    "603979893": "Static method accessed non-statically",
    "16777746": "Type safety: raw type constructor",
    "570425420": "Static field accessed non-statically",
    "134217861": "Deprecated constructor usage",
    "603979894": "Unused private method",
    "16778649": "Member in deprecated type not marked deprecated",
    "16777786": "Unchecked invocation",
    "16777547": "Redundant superinterface",
    "33554505": "Deprecated field usage",
    "16777761": "Unchecked cast",
}


def load_diagnostics(filepath):
    """Load diagnostics JSON from file.

    Handles two formats:
    - Direct array of {uri, diagnostics} objects
    - Wrapped format: [{text: "<json string>"}] from getDiagnostics tool output
    """
    with open(filepath, "r", encoding="utf-8") as f:
        raw = json.load(f)

    # Detect wrapped format from getDiagnostics tool
    if (
        isinstance(raw, list)
        and len(raw) > 0
        and isinstance(raw[0], dict)
        and "text" in raw[0]
    ):
        data = json.loads(raw[0]["text"])
    else:
        data = raw

    return data


def uri_to_relpath(uri, project_root=None):
    """Convert file:// URI to a relative path for display."""
    path = uri
    if path.startswith("file:///"):
        path = path[len("file:///") :]
    elif path.startswith("file://"):
        path = path[len("file://") :]

    # Normalize separators
    path = path.replace("\\", "/")

    # If project_root given, make relative
    if project_root:
        root = project_root.replace("\\", "/").rstrip("/")
        # Case-insensitive match for Windows
        if path.lower().startswith(root.lower()):
            path = path[len(root) :].lstrip("/")

    return path


def describe_code(code):
    """Get human-readable description for a diagnostic code."""
    code_str = str(code)
    if code_str in JAVA_CODE_DESCRIPTIONS:
        return JAVA_CODE_DESCRIPTIONS[code_str]
    if isinstance(code, str) and code.startswith("java:"):
        return f"SonarQube rule {code}"
    return ""


def filter_diagnostics(data, path_filter=None, severity_filter=None,
                       source_filter=None, code_filter=None, project_root=None):
    """Filter diagnostics entries and return matching results.

    Returns list of (relpath, diagnostic) tuples.
    """
    results = []
    severity_set = None
    if severity_filter:
        severity_set = {s.strip().lower() for s in severity_filter.split(",")}

    for entry in data:
        uri = entry.get("uri", "")
        diagnostics = entry.get("diagnostics", [])
        if not diagnostics:
            continue

        relpath = uri_to_relpath(uri, project_root)

        # Path filter: substring match (case-insensitive, normalized slashes)
        if path_filter:
            filter_normalized = path_filter.replace("\\", "/").lower()
            if filter_normalized not in relpath.lower():
                continue

        for diag in diagnostics:
            # Severity filter
            if severity_set and diag.get("severity", "").lower() not in severity_set:
                continue

            # Source filter
            if source_filter and diag.get("source", "").lower() != source_filter.lower():
                continue

            # Code filter
            if code_filter and str(diag.get("code", "")) != code_filter:
                continue

            results.append((relpath, diag))

    return results


def print_summary(results, top_n=20):
    """Print a summary of filtered diagnostics."""
    if not results:
        print("No diagnostics found matching the filters.")
        return

    # Overall counts
    severity_counts = Counter()
    source_counts = Counter()
    code_counts = Counter()
    code_messages = defaultdict(set)
    file_counts = Counter()

    for relpath, diag in results:
        severity_counts[diag.get("severity", "unknown")] += 1
        source_counts[diag.get("source", "unknown")] += 1
        code = str(diag.get("code", "unknown"))
        code_counts[code] += 1
        code_messages[code].add(diag["message"][:150])
        file_counts[relpath] += 1

    print(f"\n{'='*70}")
    print(f"  DIAGNOSTICS SUMMARY")
    print(f"{'='*70}")
    print(f"\n  Total issues: {len(results)}")
    print(f"  Files affected: {len(file_counts)}")

    print(f"\n  By Severity:")
    for sev, count in severity_counts.most_common():
        bar = "#" * min(count // 5, 40)
        print(f"    {sev:<15} {count:>5}  {bar}")

    print(f"\n  By Source:")
    for src, count in source_counts.most_common():
        print(f"    {src:<15} {count:>5}")

    print(f"\n  Top {top_n} Issue Categories:")
    print(f"  {'Code':<20} {'Count':>6}  Description")
    print(f"  {'-'*20} {'-'*6}  {'-'*40}")
    for code, count in code_counts.most_common(top_n):
        desc = describe_code(code)
        if not desc:
            # Use first message as description
            msgs = list(code_messages[code])
            desc = msgs[0][:60] + "..." if msgs else ""
        print(f"  {code:<20} {count:>6}  {desc}")


def print_file_breakdown(results, top_n=30):
    """Print per-file issue counts."""
    file_counts = Counter()
    file_severities = defaultdict(Counter)

    for relpath, diag in results:
        file_counts[relpath] += 1
        file_severities[relpath][diag.get("severity", "?")] += 1

    print(f"\n  Top {top_n} Files by Issue Count:")
    print(f"  {'File':<60} {'Total':>6} {'Err':>4} {'Warn':>5} {'Info':>5}")
    print(f"  {'-'*60} {'-'*6} {'-'*4} {'-'*5} {'-'*5}")
    for filepath, count in file_counts.most_common(top_n):
        sevs = file_severities[filepath]
        err = sevs.get("Error", 0)
        warn = sevs.get("Warning", 0)
        info = sevs.get("Information", 0)
        # Truncate long paths from the left
        display_path = filepath
        if len(display_path) > 58:
            display_path = "..." + display_path[-55:]
        print(f"  {display_path:<60} {count:>6} {err:>4} {warn:>5} {info:>5}")


def print_verbose(results):
    """Print individual diagnostic messages grouped by file."""
    by_file = defaultdict(list)
    for relpath, diag in results:
        by_file[relpath].append(diag)

    for filepath in sorted(by_file.keys()):
        diags = by_file[filepath]
        print(f"\n  {filepath} ({len(diags)} issues)")
        print(f"  {'-'*60}")
        for d in sorted(diags, key=lambda x: x.get("range", {}).get("start", {}).get("line", 0)):
            line = d.get("range", {}).get("start", {}).get("line", "?")
            sev = d.get("severity", "?")[0]  # First letter: E/W/I/H
            code = d.get("code", "")
            msg = d["message"]
            print(f"    L{line:<5} [{sev}] {code}: {msg}")


def export_results(results, output_path):
    """Export filtered results to JSON."""
    export_data = []
    for relpath, diag in results:
        export_data.append({
            "file": relpath,
            "line": diag.get("range", {}).get("start", {}).get("line"),
            "severity": diag.get("severity"),
            "source": diag.get("source"),
            "code": diag.get("code"),
            "message": diag["message"],
        })

    with open(output_path, "w", encoding="utf-8") as f:
        json.dump(export_data, f, indent=2, ensure_ascii=False)

    print(f"\n  Exported {len(export_data)} issues to {output_path}")


def detect_project_root(diagnostics_path):
    """Try to detect project root from the diagnostics file path or URIs."""
    # Default: assume script is run from project root
    return os.getcwd()


def main():
    parser = argparse.ArgumentParser(
        description="Filter and summarize VS Code diagnostics (SonarQube/Java)",
        formatter_class=argparse.RawDescriptionHelpFormatter,
        epilog=__doc__.split("Examples:")[1] if "Examples:" in __doc__ else "",
    )
    parser.add_argument("input", help="Path to diagnostics JSON file")
    parser.add_argument("--path", help="Filter by file path substring (e.g., src/lotrec/parser)")
    parser.add_argument(
        "--severity",
        help="Filter by severity: Error, Warning, Information, Hint (comma-separated)",
    )
    parser.add_argument("--source", help="Filter by source: Java, sonarqube")
    parser.add_argument("--code", help="Filter by diagnostic code (e.g., java:S120, 268435844)")
    parser.add_argument("--top", type=int, default=20, help="Show top N categories (default: 20)")
    parser.add_argument("--files", action="store_true", help="Show per-file breakdown")
    parser.add_argument("--verbose", action="store_true", help="Show individual diagnostic messages")
    parser.add_argument("--export", help="Export filtered results to JSON file")
    parser.add_argument("--project-root", help="Project root for relative path display")

    args = parser.parse_args()

    if not os.path.isfile(args.input):
        print(f"Error: File not found: {args.input}", file=sys.stderr)
        sys.exit(1)

    project_root = args.project_root or detect_project_root(args.input)

    print(f"Loading diagnostics from: {args.input}")
    data = load_diagnostics(args.input)
    print(f"Loaded {len(data)} file entries")

    # Show active filters
    filters = []
    if args.path:
        filters.append(f"path contains '{args.path}'")
    if args.severity:
        filters.append(f"severity = {args.severity}")
    if args.source:
        filters.append(f"source = {args.source}")
    if args.code:
        filters.append(f"code = {args.code}")
    if filters:
        print(f"Filters: {', '.join(filters)}")

    results = filter_diagnostics(
        data,
        path_filter=args.path,
        severity_filter=args.severity,
        source_filter=args.source,
        code_filter=args.code,
        project_root=project_root,
    )

    print_summary(results, top_n=args.top)

    if args.files:
        print_file_breakdown(results)

    if args.verbose:
        print_verbose(results)

    if args.export:
        export_results(results, args.export)


if __name__ == "__main__":
    main()
