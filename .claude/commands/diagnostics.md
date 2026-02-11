---
description: Filter and summarize VS Code diagnostics (SonarQube/Java) by package path and severity.
---

## Diagnostics Filter

Analyze VS Code diagnostics for this project, filtered by package and severity.

### User Input

```text
$ARGUMENTS
```

### Instructions

1. **Fetch diagnostics** by calling the `getDiagnostics` VS Code tool (mcp__ide__getDiagnostics with no parameters) to get all current diagnostics.

2. **Save the raw diagnostics** to a temporary JSON file at `tools/.diagnostics-raw.json` so the Python filter script can process it. The getDiagnostics result is an array with one object containing a `text` field. Write the entire result as JSON.

3. **Parse user arguments** from the input above. The format is:
   - `[path] [--severity LEVEL]`
   - Examples:
     - `src/lotrec` → filter by path only (all severities)
     - `src/lotrec --severity Warning` → filter by path and severity
     - `--severity Error` → filter by severity only (all paths)
     - `src/lotrec/parser --severity Warning --verbose` → verbose output
     - (empty) → show all diagnostics summary
   - Supported flags: `--severity`, `--verbose`, `--files`, `--source`, `--code`, `--top`

4. **Run the filter script**:
   ```bash
   python tools/diagnostics-filter.py tools/.diagnostics-raw.json [parsed arguments]
   ```

5. **Present the results** to the user. If the output is clear, show it directly. If the user asked a question about the results, interpret them.

### Important Notes

- The first positional argument (if not starting with `--`) is the path filter.
- Always pass `--files` unless the user explicitly said they don't want file breakdown.
- If no arguments are provided, run with `--files` to give a full project overview.
- The script is at `tools/diagnostics-filter.py` in the project root.
