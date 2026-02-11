---
description: Run visual comparison between Swing and JavaFX GUIs for migration validation.
---

## Visual Check — Swing vs JavaFX Migration Validation

Perform a visual comparison between the Swing baseline and JavaFX current GUI screenshots.

### User Input

```text
$ARGUMENTS
```

### Instructions

Parse the user argument (default: `all`) to determine what to do:

| Argument | Action |
|----------|--------|
| `all` (or empty) | Capture both GUIs, run comparison, perform semantic analysis |
| `javafx-only` | Capture JavaFX screenshots only |
| `swing-only` | Capture Swing screenshots only |
| `compare-only` | Skip capture, run comparison on existing screenshots |
| `<state-label>` | Deep comparison of a single state (e.g. `05-rules-tab`) |

### Step 1: Capture Screenshots

Set the Gradle wrapper path:
```bash
GW="C:/Users/bsaid/Downloads/LoTREC-Claude/lotrec/gradlew.bat"
```

Based on the argument:

- **`all`**: Run both capture tasks sequentially (each launches a GUI, so they cannot run in parallel):
  ```bash
  $GW captureSwingBaseline
  $GW captureJavaFXBaseline
  ```
- **`swing-only`**: Run `$GW captureSwingBaseline` only
- **`javafx-only`**: Run `$GW captureJavaFXBaseline` only
- **`compare-only`**: Skip this step
- **`<state-label>`**: Run both capture tasks (need both for comparison)

Use `run_in_background: false` for these tasks — they need to complete before comparison.

### Step 2: Run Programmatic Pixel Comparison

Run the comparison runner:
```bash
$GW compareVisuals
```

This generates `build/screenshots/comparison-report.txt` with per-state pixel difference percentages.

### Step 3: Read the Comparison Report

Read the file `build/screenshots/comparison-report.txt` and present the results.

### Step 4: Semantic Visual Analysis

Read the PNG screenshot files from both directories for visual analysis:

- **Swing baselines**: `build/screenshots/swing-baseline/*.png`
- **JavaFX current**: `build/screenshots/javafx-current/*.png`

For each matching pair (or just the specified `<state-label>`), read both PNG files and perform a **semantic visual analysis** comparing:

1. **Layout structure** — Are panels, split panes, and tab structures in equivalent positions?
2. **Element presence** — Are all UI elements (buttons, labels, tables, trees) present in both?
3. **Text content** — Do labels, tab names, button text match?
4. **Alignment & sizing** — Are elements roughly the same relative size and position?
5. **Visual polish** — Note any styling differences (colors, borders, fonts) as informational

If the argument was a specific `<state-label>`, focus the deep analysis on just that state.

### Step 5: Cross-Reference Manual Screenshots

The manual reference screenshots in `.specify/memory/GUI-V2/` cover all 42 GUI states including dialogs, menus, and proof results that cannot be captured programmatically. Reference these for additional context when analyzing the 7 programmatically captured states.

### Step 6: Summary Report

Present findings to the user and **always save the full report** as a markdown file to `build/screenshots/semantic-analysis-report.md` using the Write tool. Overwrite any existing file at that path.

Use this format for the report:

```
# Visual Comparison Results — Semantic Analysis Report

**Generated:** [today's date]
**Mode:** `/visual-check [argument]`

## Summary Table

| State | Pixel Diff | Dims Match | Semantic | Notes |
|-------|-----------|------------|----------|-------|
| 02-main-frame | X.X% | Yes/No | OK/Issues | ... |
| ... | ... | ... | ... | ... |

## Detailed Notes
- [Per-state observations from semantic analysis]

## Overall Assessment
- [Summary: structural equivalence status, any action items]
```

### Available States

The 7 programmatically captured states are:
- `02-main-frame` — Empty main frame before loading logic
- `04-main-frame-connectors-tab` — Main frame with Monomodal-K loaded, Connectors tab
- `05-rules-tab` — Rules tab selected
- `06-strategy-tab` — Strategies tab selected
- `07-predefined-formulas-tab` — Predefined Formulas tab selected
- `32-controls-panel` — Controls panel sub-component
- `33-loaded-logics-panel` — Loaded logics panel sub-component
