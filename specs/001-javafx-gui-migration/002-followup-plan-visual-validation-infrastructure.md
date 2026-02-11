# Plan: Visual Validation Infrastructure + `/visual-check` Skill

## Context

The JavaFX GUI migration is 97% complete (76/78 tasks). Task T067 requires a batch visual comparison across all documented GUI states, but the capture-compare-report pipeline has gaps. The core utilities (SwingScreenshotCapture, JavaFXScreenshotCapture, VisualComparator) are built and tested, but there's no JavaFX batch capture, no comparison runner, no Gradle tasks for the full pipeline, and no `/visual-check` Claude Code skill.

This plan fills those gaps and enables T067 completion.

## Constraints (per user)

- All GUI screenshots go to subfolders of `build/` (not `specs/`)
- Do NOT alter manual screenshots in `.specify/memory/GUI-V2/`
- Manual screenshots serve as additional reference baseline
- Programmatic Swing screenshots enable panel-by-panel comparison
- MCP screenshot server (Component C) is unnecessary — Claude Code reads images natively

---

## Files to Create/Modify

| # | File | Action | Description |
|---|------|--------|-------------|
| 1 | `src/lotrec/guifx/validation/SwingBaselineCapture.java` | MODIFY | Change default output path to `build/screenshots/swing-baseline` |
| 2 | `src/lotrec/guifx/validation/JavaFXBaselineCapture.java` | CREATE | JavaFX Application that captures 7 panel states to `build/screenshots/javafx-current/` |
| 3 | `src/lotrec/guifx/validation/ValidationRunner.java` | CREATE | Batch comparison runner generating `build/screenshots/comparison-report.txt` |
| 4 | `build.gradle.kts` | MODIFY | Add `captureJavaFXBaseline` and `compareVisuals` tasks; pass explicit output path arg to `captureSwingBaseline` |
| 5 | `.claude/commands/visual-check.md` | CREATE | Claude Code `/visual-check` skill orchestrating the pipeline |

---

## Implementation Steps

### Step 1: Update SwingBaselineCapture default output path

**File:** `src/lotrec/guifx/validation/SwingBaselineCapture.java` (line 43)

Change default from `specs/001-javafx-gui-migration/screenshots/swing-baseline/programmatic` to `build/screenshots/swing-baseline`. The `args[0]` override continues to work.

### Step 2: Create JavaFXBaselineCapture.java

**File:** `src/lotrec/guifx/validation/JavaFXBaselineCapture.java`

Mirrors `SwingBaselineCapture` but for the JavaFX GUI:
- Extends `javafx.application.Application` (required for FX Application Thread)
- `main()` calls `launch(args)`; `start(Stage)` bootstraps everything
- Initializes `Lotrec.initialize(Lotrec.GUI_RUN_MODE)` (same as LauncherFX)
- Creates `MainFrameFX(primaryStage)` then **overrides** `stage.setMaximized(false)` and sets 1280x900 fixed size (MainFrameFX constructor sets maximized=true on line 92)
- Loads Monomodal-K via `FileUtils.extractPredefinedLogicFile()` + `Lotrec.openLogicFile()` + `loadedLogicsPane.addLogic()`
- Default output: `build/screenshots/javafx-current/` (overridable via args)

**Captures same 7 states with identical filenames:**

| Label | How |
|-------|-----|
| `02-main-frame` | `scene.getRoot()` snapshot before loading logic |
| `04-main-frame-connectors-tab` | Root snapshot after loading Monomodal-K (Connectors is default tab) |
| `05-rules-tab` | Select `logicDefTab.getSelectionModel().select(1)`, root snapshot |
| `06-strategy-tab` | Select index 2, root snapshot |
| `07-predefined-formulas-tab` | Select index 3, root snapshot |
| `32-controls-panel` | `mainFrame.getControlsPane()` node snapshot |
| `33-loaded-logics-panel` | `mainFrame.getLoadedLogicsPane()` node snapshot |

**Thread coordination:** Background daemon thread runs captures sequentially, using `Platform.runLater()` + `CountDownLatch` for each FX-thread step, with `Thread.sleep()` between steps for rendering to settle. Calls `Platform.exit()` when done.

**LogicDefTab navigation:** `LoadedLogicsPane` stores tabs as `new Tab(name, logicDefTab)`. Access via:
```java
Tab tab = mainFrame.getLoadedLogicsPane().getSelectionModel().getSelectedItem();
LogicDefTab logicDefTab = (LogicDefTab) tab.getContent();
logicDefTab.getSelectionModel().select(index);
```
Tab order: Connectors(0), Rules(1), Strategies(2), Predefined Formulas(3).

### Step 3: Create ValidationRunner.java

**File:** `src/lotrec/guifx/validation/ValidationRunner.java`

Standalone `main()` that:
- Reads all `*.png` files from `build/screenshots/swing-baseline/`
- Finds matching files in `build/screenshots/javafx-current/`
- Runs `VisualComparator.compare()` on each pair
- Generates `build/screenshots/comparison-report.txt` with:
  - Per-state results (dimensions, pixel diff %, pass/fail verdict)
  - Skipped states (Swing file with no JavaFX counterpart)
  - Info about JavaFX-only files
  - Summary: total passed/failed/skipped
- Exits with code 1 if any state has major differences (for CI)
- Also prints report to stdout

### Step 4: Add Gradle tasks

**File:** `build.gradle.kts` — insert after existing `captureSwingBaseline` (line 80)

```kotlin
// Update existing captureSwingBaseline to pass explicit output path
// (add args line to existing task)

tasks.register<JavaExec>("captureJavaFXBaseline") {
    group = "verification"
    description = "Capture JavaFX GUI screenshots for visual comparison"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("lotrec.guifx.validation.JavaFXBaselineCapture")
    args("build/screenshots/javafx-current")
    jvmArgs(
        "--add-opens", "javafx.graphics/com.sun.javafx.application=ALL-UNNAMED",
        "--add-opens", "javafx.graphics/com.sun.glass.ui=ALL-UNNAMED"
    )
}

tasks.register<JavaExec>("compareVisuals") {
    group = "verification"
    description = "Compare Swing and JavaFX screenshots and generate report"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("lotrec.guifx.validation.ValidationRunner")
}
```

### Step 5: Create `/visual-check` Claude Code skill

**File:** `.claude/commands/visual-check.md`

Follows existing command format (YAML frontmatter + markdown instructions). The skill:

1. Accepts arguments: `all` (default), `javafx-only`, `swing-only`, `compare-only`, or `<state-label>`
2. Runs appropriate Gradle capture tasks
3. Runs `compareVisuals` for programmatic pixel comparison
4. Reads the comparison report
5. Reads the PNG files from both `build/screenshots/` directories
6. Performs **semantic visual analysis** using Claude's multimodal capabilities: layout structure, element presence, text content, alignment, sizing
7. Reports findings in a summary table + detailed notes
8. References manual screenshots in `.specify/memory/GUI-V2/` as additional context

---

## How This Enables T067

T067 requires: "Capture JavaFX screenshots → Run VisualComparator against Swing baselines → Document structural equivalence."

**Automated (7 states):** `/visual-check all` captures both GUIs, runs pixel comparison, and performs semantic analysis — covering the 7 programmatically capturable panel states.

**Remaining states (dialogs, menus, proof results):** The 42 manual reference screenshots in `.specify/memory/GUI-V2/` cover all states. For states requiring user interaction (dialogs, proof search), the `/visual-check` skill can read manual screenshots and compare them visually. The Swing GUI was not altered, so the manual screenshots remain valid as baseline.

---

## Verification

1. `$GW build` — confirms new Java files compile
2. `$GW captureSwingBaseline` — 7 PNGs in `build/screenshots/swing-baseline/`
3. `$GW captureJavaFXBaseline` — 7 PNGs in `build/screenshots/javafx-current/`
4. `$GW compareVisuals` — report generated at `build/screenshots/comparison-report.txt`
5. `/visual-check all` — full pipeline executes and produces semantic analysis
6. `/visual-check 05-rules-tab` — single-state deep comparison works
7. `$GW test` — no regressions in existing tests
