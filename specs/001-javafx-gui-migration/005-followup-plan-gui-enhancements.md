# 005 - Followup GUI Enhancements Plan

## Context

Following the JavaFX GUI migration (specs/001-javafx-gui-migration), several UI polish items, layout improvements, and bug fixes are needed. This plan addresses 10 specific enhancements requested as Followup 005.

---

## 1. Main Frame Icon

**File:** `src/lotrec/guifx/MainFrameFX.java`

Add LoTREC icon (`/lotrec/images/lotrecIcon.GIF`) to the Stage after line 92:
```java
stage.getIcons().add(new Image(getClass().getResourceAsStream("/lotrec/images/lotrecIcon.GIF")));
```

Also set on splash stage in `LauncherFX.java`.

---

## 2. Task Pane Dialog

**File:** `src/lotrec/guifx/dialogs/TaskPaneDialog.java`

- Add `setResizable(true)` after `initOwner(owner)`
- Set LoTREC icon on the dialog stage via `setOnShown` listener (since dialog window isn't available until shown)

---

## 3. Premodels Settings Pane - Remove Testing Formulae + Move to Right Side

**Files:** `PremodelSettingsPane.java`, `MainFrameFX.java`, `TableauxPane.java`

### 3a. Remove Testing Formulae combo from PremodelSettingsPane
- Remove `testingFormulaeCombo`, `formulaRefs`, `"Testing Formulae:"` label, and all combo-related code
- Remove the `getTestingFormulaeCombo()` getter
- Simplify `setLogic()` to just store the logic and clear fields
- The user can still edit the "Formula Code" field manually or rely on auto-populate (#4)

### 3b. Restructure MainFrameFX layout (side-by-side at top of right panel)
**Current layout:**
```
[Left: LoadedLogicsPane / (PremodelSettings + Controls)] | [Right: TableauxPane]
```
**New layout:**
```
[Left: LoadedLogicsPane / Controls] | [Right: (PremodelSettings | PremodelsList+Filter) / CytoscapeGraph / StatusBar]
```

The PremodelSettingsPane and Premodels list sit **side-by-side** at the top of the right panel, with the Cytoscape graph area below.

In `MainFrameFX.java`:
- Move `premodelSettingsPane` out of the left VBox
- Create the right side as a VBox: top row is an HBox/SplitPane containing `premodelSettingsPane` (left) and the premodels list+filter (right), below is the Cytoscape graph and status bar

In `TableauxPane.java`:
- Expose the premodels list VBox (list + filter checkbox) as a separate accessible component, OR
- Restructure so the right side in `MainFrameFX` composes `premodelSettingsPane` alongside a premodels list area extracted from `TableauxPane`

---

## 4. Testing Formulae Auto-populate

**File:** `PremodelSettingsPane.java`

In `setLogic(Logic logic)`, after storing the logic, auto-populate formula field from the first testing formula:
```java
if (logic != null && logic.getTestingFormulae() != null && !logic.getTestingFormulae().isEmpty()) {
    TestingFormula tf = (TestingFormula) logic.getTestingFormulae().get(0);
    formulaField.setText(tf.getCode());
    updateInfixFromCode();
}
```

The existing listener in `MainFrameFX` (line 46-51) already calls `setLogic()` on tab selection change, so this will auto-update when switching logic tabs.

---

## 5. Logic Tab Description

**File:** `src/lotrec/guifx/logicspane/LogicDefTab.java`

Change from `extends TabPane` to `extends VBox`:
- Add a collapsible `TitledPane("Description", descriptionTextArea)` above the sub-tabs
- Description text area: read-only, 3 rows, wrapping, populated from `logic.getDescription()`
- Collapsed by default
- The sub-tabs TabPane becomes an internal field

Must verify no code casts `LogicDefTab` to `TabPane` -- checked: `LoadedLogicsPane` stores it as Tab content (any Node), `MainFrameFX` accesses logic through `logicTabs` map. Safe to change.

---

## 6. Rules Tab Cleanup

**File:** `src/lotrec/guifx/logicspane/RulesTabPane.java`

- Remove `new Label("Rules:")` from `getChildren().addAll(...)` at line 98
- Add `conditionsTree.setShowRoot(false)` and `actionsTree.setShowRoot(false)` to hide the collapsible "Conditions"/"Actions" root nodes while keeping the children as a flat list
- Keep the "Conditions:" and "Actions:" labels that already exist above each tree

---

## 7. Condition/Action Dialogs - Resizable + Info Icons

**Files:** `ConditionDialog.java`, `ActionDialog.java`

For both (identical changes):
- Add `setResizable(true)` after `initOwner(owner)`
- Add `sizeToScene()` call at end of `updateParameterFields()` (with null-guards)
- Add info icon buttons: for each parameter field, create an HBox `[label, textfield, infoButton]` instead of just `[label, textfield]`
- Info button uses `/lotrec/images/info.png` as graphic (16x16)
- Tooltip set from `@ParametersDescriptions` annotation values
- Click handler shows `DialogsFactory.infoDialog()` with full description text
- New field: `List<Button> infoButtons`

---

## 8. Predefined Formulas Tab Label Removal

**File:** `src/lotrec/guifx/logicspane/TestingFormulaePane.java`

Remove `new Label("Predefined Formulas:")` from `getChildren().addAll(...)` at line 56 (redundant with tab title).

---

## 9. Step By Step Button Fix

**Files:** `MainFrameFX.java`, `BreakPointsDialog.java`

### Problem
Current step button handler calls `startEngine(true)` which immediately starts the engine without showing the breakpoints dialog. The Swing version shows a dialog with a tree of rules + checkboxes for selecting breakpoints first.

### Fix
- Change step button handler to call new `showStepByStepDialog()` method
- `showStepByStepDialog()`: validates formula/strategy, populates BreakPointsDialog with rule names from the strategy, shows dialog, on OK starts engine with `setRunningBySteps(true)` and selected breakpoints

### BreakPointsDialog Enhancement (full strategy tree view, matching Swing)
- Rewrite to show hierarchical strategy tree (matching Swing's `treStepsTree`)
- Use `CheckBoxTreeItem<String>` with `CheckBoxTreeCell.forTreeView()` for checkbox-enabled tree
- Walk strategy hierarchy: `Routine` nodes are branch nodes (repeat, firstRule, allRules), `EventMachine` nodes are leaf nodes (individual rules with checkboxes)
- Assign levels to leaf EventMachine nodes (matching Swing's `setLevel()` pattern)
- Add "Select All" and "Invert Selection" buttons
- Change confirm button text from "OK" to "Start"
- Add `populateFromStrategy(Strategy)` method
- Return `ArrayList<Integer>` of selected breakpoint levels compatible with `Engine.setRulesBreakPoints()`

Reference: Swing `ControlsPanel.java` lines 100-148 (`displayStepsTree` method)

### Refactoring
Extract duplicated formula/strategy validation from `startEngine()`, `showStepByStepDialog()`, and `showSatCheckDialog()` into a helper method.

---

## 10. Satisfiability Check Button

**Files:** `SatisfiabilityDialog.java` (rewrite), `MainFrameFX.java`

### SatisfiabilityDialog Rewrite
Change from `Dialog<Void>` to `Dialog<Integer>`:
- Two radio buttons (ToggleGroup):
  - "Stop after finding a first open premodel" (default)
  - "Pause after each found open premodel"
- Button: "Start" (instead of OK)
- Result converter returns `Engine.STOP_WHEN_HAVING_OPEN_TABLEAU` or `Engine.PAUSE_WHEN_HAVING_OPEN_TABLEAU`

### MainFrameFX Wiring
- Add handler: `premodelSettingsPane.getSatCheckButton().setOnAction(e -> showSatCheckDialog())`
- `showSatCheckDialog()`: validates formula/strategy, shows SatisfiabilityDialog, on Start creates Engine with `setOpenTableauAction(result)` and starts it

---

## Implementation Order

1. Item 1 - Main Frame Icon (trivial)
2. Item 8 - Remove Predefined Formulas label (one-line)
3. Item 6 - Rules tab cleanup (3 small changes)
4. Item 2 - Task Pane dialog (small, isolated)
5. Item 5 - Logic description collapsible (moderate refactor)
6. Item 7 - Condition/Action dialog enhancements (medium)
7. Item 3 - PremodelSettings layout move (significant restructure)
8. Item 4 - Auto-populate formula (small, depends on #3)
9. Item 10 - Satisfiability Check dialog (new dialog + wiring)
10. Item 9 - Step By Step fix (most complex, breakpoints tree)

---

## Critical Files

| File | Items |
|------|-------|
| `src/lotrec/guifx/MainFrameFX.java` | #1, #3, #9, #10 |
| `src/lotrec/guifx/PremodelSettingsPane.java` | #3, #4 |
| `src/lotrec/guifx/TableauxPane.java` | #3 |
| `src/lotrec/guifx/logicspane/LogicDefTab.java` | #5 |
| `src/lotrec/guifx/logicspane/RulesTabPane.java` | #6 |
| `src/lotrec/guifx/logicspane/TestingFormulaePane.java` | #8 |
| `src/lotrec/guifx/dialogs/ConditionDialog.java` | #7 |
| `src/lotrec/guifx/dialogs/ActionDialog.java` | #7 |
| `src/lotrec/guifx/dialogs/SatisfiabilityDialog.java` | #10 |
| `src/lotrec/guifx/dialogs/BreakPointsDialog.java` | #9 |
| `src/lotrec/guifx/dialogs/TaskPaneDialog.java` | #2 |
| `src/lotrec/guifx/LauncherFX.java` | #1 |

## Verification

After each item, run `$GW run` (in background) and verify:
1. App icon appears in taskbar and title bar
2. Task pane dialog shows icon and is resizable
3. PremodelSettingsPane appears to the left of premodels list in right panel
4. Loading a logic auto-populates formula field; switching tabs updates it
5. Logic description appears as collapsible section above sub-tabs
6. Rules tab shows no redundant label, conditions/actions shown as flat lists
7. Condition/Action dialogs are resizable, resize when type changes, show info icons
8. Predefined Formulas tab has no redundant label
9. "Step By Step..." shows breakpoints dialog, then starts engine
10. "Satisfiability Check..." shows options dialog with Stop/Pause, then starts engine
