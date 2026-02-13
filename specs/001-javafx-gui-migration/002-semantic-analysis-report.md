# Visual Comparison Results — Semantic Analysis Report

**Generated:** 2026-02-11
**Mode:** `/visual-check all`

## Summary Table

| State | Pixel Diff | Dims Match | Semantic | Notes |
|-------|-----------|------------|----------|-------|
| 02-main-frame | 100.0% | No (839 vs 861h) | Structurally equivalent | Layout reorganized; menu bar added; controls relocated to bottom-left |
| 04-main-frame-connectors-tab | 100.0% | No (839 vs 861h) | Structurally equivalent | Connector properties use vertical form layout; no auto-selection |
| 05-rules-tab | 100.0% | No (839 vs 861h) | Structurally equivalent | Rule editing uses stacked layout; separate Add Condition/Action buttons |
| 06-strategy-tab | 100.0% | No (839 vs 861h) | Minor gap | Missing "Main Strategy" selector dropdown |
| 07-predefined-formulas-tab | 100.0% | No (839 vs 861h) | Structurally equivalent | Formulas shown in prefix notation; adds "Run Test" button |
| 32-controls-panel | 100.0% | No (497x391 vs 507x105) | Different scope | Swing captures formula settings; JavaFX captures runtime controls only |
| 33-loaded-logics-panel | 100.0% | No (497x436 vs 509x490) | Structurally equivalent | Same content, different form layout style |

## Detailed Notes

### 02-main-frame (Empty Main Frame)
- JavaFX adds a full menu bar (Control, Logic, View, Premodels, Help) visible at top — Swing's menu bar is part of the JFrame title bar, not the content pane
- JavaFX consolidates "Testing Formulae" + "Formula Code" + "Runtime Controls" in the bottom-left area, whereas Swing splits them into separate panels (Construction Settings bottom-left, Controls center)
- Right panel: JavaFX shows "Premodels" list + "Filter closed tableaux" checkbox + graph area with "No graph to display"; Swing shows "Controls" + "Premodels List" + "Galss" + "Premodels Views"
- Status bars added in JavaFX: `Idle Time:-- Rules:--` (left) and `Status: Idle Tableaux: 0 Time: --` (right)

### 04-main-frame-connectors-tab (Connectors Tab)
- All 7 connectors present in both (not, and, or, imp, equiv, nec, pos)
- Swing auto-selects "not" and populates detail fields; JavaFX shows empty placeholder fields (Name, Arity, Output Format, Priority)
- Swing groups fields in a "Selected Connector" bordered panel with inline labels; JavaFX uses stacked vertical label+field pairs
- JavaFX: "Add"/"Remove" buttons; Swing: "Add"/"Edit"/"Delete" with icons
- Tab name: "Monomodal-K" (JavaFX) vs "Monomodal-K.xml" (Swing)

### 05-rules-tab
- All rules present in both GUIs (Stop, NotNot, And, NotOr, NotImp, etc.)
- Swing auto-selects "Stop" showing its conditions/actions; JavaFX shows placeholder text
- JavaFX adds dedicated "Add Condition" and "Add Action" buttons below the detail areas
- JavaFX uses "Add Rule"/"Remove Rule"; Swing uses "Add"/"Edit"/"Delete"

### 06-strategy-tab
- Both show CPLStrategy and KStrategy in the list
- Swing auto-selects CPLStrategy and displays its code; JavaFX shows empty placeholder
- **Gap identified:** Swing has a "Main Strategy" dropdown (showing KStrategy) that is not visible in the JavaFX Strategies tab. This is used to designate which strategy is the main/entry strategy.

### 07-predefined-formulas-tab
- All 6 predefined formulas present in both
- Swing displays formulas in **infix** notation in the list (e.g., `[] P & <> Q & <> (R v ~ P)`); JavaFX shows them in **prefix** notation (e.g., `and nec P and pos Q pos or R not P`)
- JavaFX adds a "Run Test" button not present in Swing
- Swing shows selected formula's Name and Code separately; JavaFX has a single "Formula" text field

### 32-controls-panel
- These capture **different components** due to naming differences: Swing's `getControlsPanel()` returns the "Premodels Construction Settings" area (formula dropdown, text area, build buttons); JavaFX's `getControlsPane()` returns only the "Runtime Controls" strip (Next Step, Pause, Stop buttons + status)
- This is a panel reorganization, not a missing feature — the formula construction UI is in JavaFX's `PremodelSettingsPane` instead

### 33-loaded-logics-panel
- Both show the loaded logics tab pane with Monomodal-K and all 4 sub-tabs
- Same connectors present; JavaFX uses vertical form layout vs Swing's horizontal grouped layout

## Overall Assessment

**Structural equivalence: HIGH** — All major UI regions and functional elements are present in both GUIs. The migration successfully reproduces the core application layout with:
- Left panel: loaded logics with tabbed definition editor
- Bottom-left: formula construction + engine controls
- Right panel: tableaux/premodels display area

### Identified Gaps

1. **Main Strategy selector** — The Swing Strategies tab has a "Main Strategy" dropdown to designate the entry strategy. This appears absent from the JavaFX version.
2. **Auto-selection** — JavaFX doesn't auto-select the first item in lists (connectors, rules, strategies, formulas), leaving detail fields empty on initial load. Swing auto-selects and populates.
3. **Formula display format** — Predefined formulas show prefix notation in JavaFX vs infix in Swing list view.

### Informational (styling/layout, not functional)

- JavaFX uses modern flat styling with subtle borders and colored button text
- Vertical stacked form layout (JavaFX) vs compact grouped panels (Swing)
- "Add"/"Remove" naming (JavaFX) vs "Add"/"Edit"/"Delete" with icons (Swing)
- Controls panel captures different component scopes (reorganized architecture)
