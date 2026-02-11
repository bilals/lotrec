# Implementation Plan: Wire JavaFX GUI Panels & Complete Phase 11

## Problem

`MainFrameFX.java` uses placeholder `Label` nodes ("Logic panels pending migration", etc.)
instead of the actual panel classes that already exist. Additionally, 20 menu handlers are
unimplemented, and the engine startup sequence is not wired.

## Scope

All 5 remaining tasks from `tasks.md` (T066, T067, T070, T071, T075), plus 3 new tasks
that were implicitly expected but never explicitly tracked:

- **T-A**: Wire panels into MainFrameFX layout
- **T-B**: Wire menu handlers to panel/dialog actions
- **T-C**: Wire engine startup sequence (Build/Step/Stop/Pause)

## Step 1: Wire panels into MainFrameFX

**File:** `src/lotrec/guifx/MainFrameFX.java`

Replace placeholder labels with actual panel instances. Changes:

1. Add fields + getters for `LoadedLogicsPane` and `PremodelSettingsPane`
2. In constructor, replace:
   - `leftPane` placeholder → `LoadedLogicsPane` instance
   - `controlsPlaceholder` placeholder → `VBox` containing `PremodelSettingsPane` (top) + `ControlsPane` (bottom)
   - `rightPane` placeholder → `TableauxPane` instance
3. Store references via existing `setControlsPane()` / `setTableauxPane()` setters
4. Change `leftPane` and `rightPane` field types from `StackPane` to their actual types
   (or keep StackPane and just replace children)

**Layout structure (matches Swing):**
```
MainFrame (BorderPane)
├── TOP: MenuBar
└── CENTER: mainSplitPane (Horizontal)
    ├── LEFT: leftSplitPane (Vertical, 70/30)
    │   ├── TOP: loadedLogicsPane (LoadedLogicsPane / TabPane)
    │   └── BOTTOM: controlsArea (VBox)
    │       ├── premodelSettingsPane (formula input + build/step/sat)
    │       └── controlsPane (pause/stop + status labels)
    └── RIGHT: tableauxPane (TableauxPane)
```

**Approach:** Keep the SplitPane structure, just replace children. Minimal changes.

## Step 2: Wire critical menu handlers

**File:** `src/lotrec/guifx/MainFrameFX.java` (in `createMenuBar()`)

Need references to panels, so menu handler wiring must happen after panel creation.
Refactor: create menus in constructor body after panels exist, or pass panels to a
`wireMenuHandlers()` method.

### Priority 1 — Core workflow menus:
| Menu Item | Handler |
|-----------|---------|
| Logic > Predefined Logics... | Show `PredefinedLogicsDialog` → parse XML → `loadedLogicsPane.addLogic()` |
| Logic > Open... | `FileDialogs.openLogic()` → parse XML → `loadedLogicsPane.addLogic()` |
| Logic > Save... | `FileDialogs.saveLogic()` with current logic |
| Logic > Save As... | `FileDialogs.saveLogicAs()` with current logic |
| Logic > Close | `loadedLogicsPane.removeLogic(selectedLogic)` |
| Logic > New... | Create empty `Logic` → `loadedLogicsPane.addLogic()` |
| Logic > Logic Description... | Show `LogicDescriptionDialog` for selected logic |

### Priority 2 — Panel visibility:
| Menu Item | Handler |
|-----------|---------|
| Control > Show/Hide > Logics | Toggle `loadedLogicsPane` visibility in leftSplitPane |
| Control > Show/Hide > Controls | Toggle controlsArea visibility in leftSplitPane |
| Control > Show/Hide > Tableaux | Toggle `tableauxPane` visibility in mainSplitPane |

### Priority 3 — View & Premodels (defer if time-constrained):
| Menu Item | Handler |
|-----------|---------|
| View > Layout modes | Set CytoscapeSwingBridge layout |
| View > Display modes | Set display mode on TableauxPane |
| View > Filters | Show `FilterDialog` |
| Premodels > Load/Save/Export | File dialogs for premodel I/O |
| Premodels > Editor | Show `PremodelEditorDialog` |
| Premodels > Run Info | Show `RunInfoDialog` |
| Help > Home Page | Open URL in browser |

## Step 3: Wire engine startup sequence

**Files:** `MainFrameFX.java`, `PremodelSettingsPane.java`, `ControlsPane.java`

Wire the button actions that trigger proof search:

1. **Build button** (`premodelSettingsPane.getBuildButton()`):
   - Get selected logic from `loadedLogicsPane.getSelectedLogic()`
   - Parse formula via `premodelSettingsPane.parseFormula()`
   - Create `Engine` with logic + formula
   - Create `JavaFXEngineListener(mainFrameFX)`, register on engine
   - Start engine thread
   - Enable controls on `controlsPane`

2. **Step button** (`premodelSettingsPane.getStepButton()`):
   - Same as Build but in step mode

3. **Stop button** (`controlsPane.getStopButton()`):
   - Call `engine.stopEngine()` (or equivalent)

4. **Pause/Resume button** (`controlsPane.getPauseResumeButton()`):
   - Toggle engine pause state

5. **Next Step button** (`controlsPane.getNextStepButton()`):
   - Advance engine one step

6. **Logic tab selection** → update `premodelSettingsPane.setLogic(logic)` so
   testing formulae dropdown refreshes.

## Step 4: Show TaskPaneDialog on startup

**File:** `src/lotrec/guifx/LauncherFX.java`

After `primaryStage.show()`, show `TaskPaneDialog` (the startup quick-access dialog
with "Load predefined logic", "Open existing", "Create new" options). Wire its
result to the appropriate action (load predefined, open file, or new logic).

## Step 5: Verification tasks (T066, T067, T070, T071)

After Steps 1-4 are complete:

1. **T066** — Run `gradlew.bat runSwing`, confirm Swing GUI still works
2. **T070** — Run `gradlew.bat run`, load a logic (e.g., K.xml), run a proof search,
   confirm UI stays responsive
3. **T071** — Run `gradlew.bat run`, measure time to main window visible (< 5s)
4. **T067** — Capture JavaFX screenshots, run visual comparison against Swing baselines

## Step 6: T075 — Remove Swing GUI (DEFERRED)

Only after user confirms all verification passes. Not part of this implementation round.

## Execution Order

```
Step 1 (Wire panels)           — BLOCKING, must be first
    │
    ├── Step 2 (Menu handlers) — depends on panel references
    │
    └── Step 3 (Engine wiring) — depends on panel references
            │
            └── Step 4 (TaskPaneDialog startup)
                    │
                    └── Step 5 (Manual verification: T066, T067, T070, T071)
```

Steps 2 and 3 can be done in parallel since they modify different parts of the code.

## Files Modified

| File | Changes |
|------|---------|
| `src/lotrec/guifx/MainFrameFX.java` | Replace placeholders, add fields/getters, wire menus |
| `src/lotrec/guifx/LauncherFX.java` | Show TaskPaneDialog after startup |

## Files Read-Only (no changes expected)

All panel classes (`LoadedLogicsPane`, `ControlsPane`, `PremodelSettingsPane`,
`TableauxPane`, `FormulaTransformerPane`), all dialog classes, `JavaFXEngineListener`,
`DialogsFactory` — these are already implemented and should work as-is once wired.

## Risk Assessment

| Risk | Mitigation |
|------|------------|
| Panel constructors may fail at runtime (missing resources, etc.) | Build + test first, then run GUI |
| Menu handler logic may need Swing-specific adaptation | Reference Swing MainFrame handlers |
| Engine wiring may have threading issues | All UI updates via Platform.runLater() already in JavaFXEngineListener |
| CytoscapeSwingBridge may fail in SwingNode | Error boundary already implemented in CytoscapeSwingBridge |
