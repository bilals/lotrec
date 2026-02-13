# FX GUI Enhancements Plan

## Context

The JavaFX GUI migration left several functional gaps compared to the Swing reference:
- No auto-selection of first items in lists
- Missing Edit buttons and Comment fields across all tabs
- Rules tab shows condition/action object IDs instead of readable code
- Formula tab shows prefix code instead of display names
- Strategy tab doesn't surface the main strategy
- "Formula Code" area in PremodelSettingsPane doesn't show infix format

This plan addresses all these gaps in a single implementation pass, since the changes follow repetitive patterns across the 4 tab panes.

---

## Shared Component: `ExpandableCommentField`

**New file:** `src/lotrec/guifx/components/ExpandableCommentField.java`

A reusable VBox containing:
- A 1-row `TextArea` (read-only by default, editable when needed)
- A small toggle `Button` ("Comment ▼" / "Comment ▲") that switches between 1 row and 4 rows
- Default state: collapsed (1 row)
- API: `setText(String)`, `getText()`, `setEditable(boolean)`, `clear()`

This component will be used in all 4 tab panes (Connectors, Rules, Strategies, Formulas).

---

## File Changes

### 1. `src/lotrec/guifx/logicspane/ConnTabPane.java`

**Auto-select first connector:**
- After `refreshList()`, call `connectorList.getSelectionModel().selectFirst()` if list is non-empty

**Inline labels:**
- Change detail layout from stacked `VBox(Label, Field, Label, Field, ...)` to `GridPane` with labels in column 0 and fields in column 1:
  ```
  Name:           [________]
  Arity:          [________]
  Output Format:  [________]
  Priority:       [________]
  ```

**Add "Edit" button:**
- Add `editBtn` between `addBtn` and `removeBtn` in the button bar: `HBox(addBtn, editBtn, removeBtn)`
- `editBtn.setOnAction(e -> editConnector())`
- `editConnector()` opens `NewConnectorDialog` pre-populated with selected connector's data, then updates the connector in the logic on OK
- This requires adding an `editMode` constructor or `setConnector(Connector)` method to `NewConnectorDialog`

**Add Comment field:**
- Add `ExpandableCommentField` below the detail fields
- Populate from `conn.getComment()` on selection change
- Read-only in the details view; editable in the Edit dialog

---

### 2. `src/lotrec/guifx/logicspane/RulesTabPane.java`

**Auto-select first rule:**
- After `refreshList()`, call `rulesList.getSelectionModel().selectFirst()` if list is non-empty

**Fix condition/action display (object IDs -> readable code):**
- In `showRuleDetails()`, change:
  - `cond.toString()` → `((AbstractCondition) cond).getCode()` (line 100)
  - `act.toString()` → `((AbstractAction) act).getCode()` (line 109)
- This will display e.g. `hasElement n0 and P Q` instead of `lotrec...ExpressionCondition@1a2b`

**Add "Edit" button for rules:**
- Add `editRuleBtn` between `addRuleBtn` and `removeRuleBtn`
- Opens `NewRuleDialog` pre-populated with selected rule's name and comment
- Requires adding edit mode support to `NewRuleDialog`

**Add Comment field:**
- Add `ExpandableCommentField` below the rule list/buttons area
- Populate from `rule.getComment()` on selection change

**Add "Add/Edit/Delete" buttons for Conditions:**
- Replace current layout to have a button bar under the conditions tree: `HBox(addCondBtn, editCondBtn, deleteCondBtn)`
- `editCondBtn` opens `ConditionDialog` pre-populated with selected condition (requires edit mode on `ConditionDialog`)
- `deleteCondBtn` removes selected condition from the rule and refreshes

**Add "Add/Edit/Delete" buttons for Actions:**
- Same pattern: `HBox(addActBtn, editActBtn, deleteActBtn)`
- `editActBtn` opens `ActionDialog` pre-populated with selected action
- `deleteActBtn` removes selected action from the rule and refreshes

---

### 3. `src/lotrec/guifx/logicspane/StratTabPane.java`

**Auto-select and display main strategy:**
- After `refreshList()`, select the strategy whose name matches `logic.getMainStrategyName()`
- If no match, select first strategy

**Allow selecting main strategy:**
- Add a `ComboBox<String>` labeled "Main Strategy:" above the strategies list (or a "Set as Main" button)
- Populated from `strategyNames`
- Pre-selected to `logic.getMainStrategyName()`
- On change: `logic.setMainStrategyName(selected)`

**Add Comment field:**
- Add `ExpandableCommentField` below the code area
- Populate from `strategy.getComment()` on selection change

---

### 4. `src/lotrec/guifx/logicspane/TestingFormulaePane.java`

**Display formula DisplayName (infix) instead of code (prefix):**
- In `refreshList()`, change `formulaNames.add(tf.getCode())` → `formulaNames.add(tf.getDisplayName())`
- Handle duplicate display names (append index like Swing does)

**Auto-select first formula:**
- After `refreshList()`, call `formulaList.getSelectionModel().selectFirst()` if list is non-empty

**Display formula Code in editable/expandable TextArea:**
- Replace `formulaField` (TextField) with a `TextArea` with `prefRowCount(2)`, wrapText enabled
- On selection change, populate with `tf.getCode()` (prefix notation)
- Make it editable so user can modify formula code

**Add Comment field:**
- Add `ExpandableCommentField` below the code area
- Populate from `tf.getComment()` on selection change

---

### 5. `src/lotrec/guifx/PremodelSettingsPane.java`

**Display infix format under "Formula Code":**
- Add a read-only `Label` or `TextField` below the "Formula Code:" label showing the infix format
- When user selects a testing formula from the combo, display its `toString()` (which returns infix via connector output formats) in this new field
- Label it "Display Format:" or "Infix:"
- When the user types custom formula text, attempt to parse and display infix on focus-out (with error handling if parse fails)

---

### 6. Dialog Modifications

**`NewConnectorDialog` - Add edit mode:**
- Add a `setConnector(Connector conn)` method that pre-populates all fields
- Change dialog title to "Edit Connector" when in edit mode
- On OK in edit mode: update existing connector properties instead of creating new one

**`NewRuleDialog` - Add edit mode:**
- Add a `setRule(Rule rule)` method that pre-populates name and comment fields
- Change dialog title to "Edit Rule" when in edit mode

**`ConditionDialog` - Add edit mode:**
- Add method to pre-populate with existing condition's keyword and parameter values
- Used by the Edit button on the conditions tree

**`ActionDialog` - Add edit mode:**
- Add method to pre-populate with existing action's keyword and parameter values
- Used by the Edit button on the actions tree

---

## Key Data Model References

| What | Method | Returns |
|------|--------|---------|
| Condition readable text | `AbstractCondition.getCode()` | `"hasElement n0 and P Q"` |
| Action readable text | `AbstractAction.getCode()` | `"add n0 not P"` |
| Formula display name | `TestingFormula.getDisplayName()` | Infix format or user name |
| Formula code | `TestingFormula.getCode()` | Prefix notation |
| Formula infix | `TestingFormula.toString()` | Infix via connector output formats |
| Main strategy | `Logic.getMainStrategyName()` | Strategy name string |
| Connector comment | `Connector.getComment()` | Comment string |
| Rule comment | `Rule.getComment()` | Comment string |
| Strategy comment | `Strategy.getComment()` | Comment string |
| Formula comment | `TestingFormula.getComment()` | Comment string |

## Files to Create
- `src/lotrec/guifx/components/ExpandableCommentField.java`

## Files to Modify
- `src/lotrec/guifx/logicspane/ConnTabPane.java`
- `src/lotrec/guifx/logicspane/RulesTabPane.java`
- `src/lotrec/guifx/logicspane/StratTabPane.java`
- `src/lotrec/guifx/logicspane/TestingFormulaePane.java`
- `src/lotrec/guifx/PremodelSettingsPane.java`
- `src/lotrec/guifx/dialogs/NewConnectorDialog.java`
- `src/lotrec/guifx/dialogs/NewRuleDialog.java`
- `src/lotrec/guifx/dialogs/ConditionDialog.java`
- `src/lotrec/guifx/dialogs/ActionDialog.java`

## Verification

1. **Build:** `$GW build` - must compile without errors
2. **Run FX GUI:** `$GW run` (background) - open a predefined logic (e.g., K.xml)
3. **Visual checks per tab:**
   - **Connectors:** First connector auto-selected, inline labels visible, Edit button works, Comment field toggles
   - **Rules:** First rule auto-selected, conditions/actions show readable code (not object IDs), Edit works, Comment toggles, condition/action Add/Edit/Delete work
   - **Strategies:** Main strategy auto-selected, main strategy selector works, Comment toggles
   - **Formulas:** List shows display names (infix), first formula auto-selected, Code area shows prefix and is editable, Comment toggles
   - **PremodelSettingsPane:** Selecting a formula shows infix format below "Formula Code"
4. **Run visual-check skill** to compare FX vs Swing screenshots

---

## Implementation Summary

All changes were implemented in a single pass. Build succeeds with all tests passing.

### New File Created

- **`src/lotrec/guifx/components/ExpandableCommentField.java`** — Reusable collapsible comment field (VBox with toggle button + TextArea). Toggle switches between 1-row and 4-row display. Read-only by default.

### Files Modified

#### 1. `ConnTabPane.java`
- Detail fields now use **GridPane** layout (label in col 0, field in col 1) instead of stacked VBox
- Detail fields are **read-only** (display only; editing via dialog)
- **Edit button** added between Add and Remove — opens `NewConnectorDialog` pre-populated with selected connector
- **ExpandableCommentField** added below detail fields, populated from `conn.getComment()`
- **Auto-select**: restores previous selection after refresh, or selects first item

#### 2. `RulesTabPane.java`
- **Conditions/actions display readable code** via `AbstractCondition.getCode()` / `AbstractAction.getCode()` instead of `toString()` (which showed object IDs like `lotrec...ExpressionCondition@1a2b`)
- **Edit Rule button** added — opens `NewRuleDialog` pre-populated with selected rule
- **ExpandableCommentField** for rule comments
- **Add/Edit/Delete buttons for Conditions** — Edit opens `ConditionDialog` pre-populated with keyword + params extracted from `AbstractCondition.getName()` and `Parameter.getValueCode()`; Delete calls `rule.removeCondition()`
- **Add/Edit/Delete buttons for Actions** — same pattern using `ActionDialog` and `rule.removeAction()`
- **Auto-select**: restores previous selection or selects first rule

#### 3. `StratTabPane.java`
- **Main Strategy ComboBox** added at top, populated from strategy names, pre-selected to `logic.getMainStrategyName()`, updates logic on change
- **ExpandableCommentField** for strategy comments
- **Auto-select**: prefers main strategy, falls back to previous selection, then first item

#### 4. `TestingFormulaePane.java`
- List now shows **`getDisplayName()`** (infix/user name) instead of `getCode()` (prefix)
- Duplicate display names handled by appending index suffix
- `TextField` replaced with **`TextArea`** (2 rows, wrap text) showing `tf.getCode()` (prefix notation)
- **ExpandableCommentField** for formula comments
- **Auto-select**: restores previous index or selects first formula
- Parallel `formulaRefs` list maintained for index-based TestingFormula lookup

#### 5. `PremodelSettingsPane.java`
- **"Display Format:" field** added (read-only `TextField`) below "Formula Code:" showing infix notation
- Combo now shows `getDisplayName()` instead of raw `toString()`; selecting populates both code and infix fields
- **Auto-parse on focus-out**: when user edits formula code and leaves the field, it attempts to parse and display infix (shows "(parse error)" on failure)
- Parallel `formulaRefs` list for index-based lookup from combo selection

#### 6. `ConditionDialog.java`
- **Edit mode constructor** added: `ConditionDialog(Stage, ConditionResult)` pre-populates keyword combo and parameter fields from existing condition data
- Title changes to "Edit Condition" in edit mode

#### 7. `ActionDialog.java`
- **Edit mode constructor** added: `ActionDialog(Stage, ActionResult)` pre-populates keyword combo and parameter fields from existing action data
- Title changes to "Edit Action" in edit mode

#### 8. `NewConnectorDialog.java` & `NewRuleDialog.java`
- Already had edit mode support (accepting `existing` parameter) from prior implementation — no changes needed

### Design Notes

- **Selection restoration**: All tab panes attempt to restore the previously selected item after `refreshList()`, falling back to auto-select first. This prevents the selection from jumping on data refresh.
- **ConditionResult/ActionResult reuse**: The existing inner classes in `ConditionDialog` and `ActionDialog` serve double duty — they carry data both from dialog → caller (new) and from caller → dialog (edit mode).
- **Parameter extraction for edit**: Condition/action parameters are extracted via `Parameter.getValueCode()` which returns the code string for each parameter type (node, formula, relation, mark).
