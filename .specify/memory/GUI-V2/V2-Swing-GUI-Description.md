# LoTREC 2.0 — V2 Swing GUI Component Description

> **Purpose:** Structured reference for the current Java Swing GUI, intended as a migration specification for the JavaFX rewrite. Each section maps screenshots to source files and describes layout, components, and behavior.

---

## Table of Contents

1. [Overview](#1-overview)
2. [Splash Screen](#2-splash-screen)
3. [Task Pane (Startup Dialog)](#3-task-pane-startup-dialog)
4. [Main Frame Layout](#4-main-frame-layout)
5. [Loaded Logics Panel](#5-loaded-logics-panel-top-left)
   - 5.1 [Connectors Tab](#51-connectors-tab)
   - 5.2 [Rules Tab](#52-rules-tab)
   - 5.3 [Strategies Tab](#53-strategies-tab)
   - 5.4 [Predefined Formulas Tab](#54-predefined-formulas-tab)
6. [Premodels Construction Settings Panel](#6-premodels-construction-settings-panel-bottom-left)
7. [Controls Panel](#7-controls-panel-middle-top)
8. [Premodels List Panel](#8-premodels-list-panel-middle)
9. [Zoom Panel](#9-zoom-panel-bottom-middle)
10. [Premodels Views Panel](#10-premodels-views-panel-right)
11. [Menus](#11-menus)
    - 11.1 [Logic Menu](#111-logic-menu)
    - 11.2 [View Menu](#112-view-menu)
    - 11.3 [Premodels Menu](#113-premodels-menu)
    - 11.4 [Help Menu](#114-help-menu)
12. [Dialogs](#12-dialogs)
    - 12.1 [Predefined Logics Dialog](#121-predefined-logics-dialog)
    - 12.2 [Logic Description Dialog](#122-logic-description-dialog)
    - 12.3 [Step By Step — Break Points Dialog](#123-step-by-step--break-points-dialog)
    - 12.4 [Satisfiability Check Options Dialog](#124-satisfiability-check-options-dialog)
    - 12.5 [Filter Displayed Premodels Dialog](#125-filter-displayed-premodels-dialog)
    - 12.6 [Premodels Editor Dialog](#126-premodels-editor-dialog)
    - 12.7 [Run Info Dialog](#127-run-info-dialog)
    - 12.8 [Load a Premodel Dialog](#128-load-a-premodel-dialog)
    - 12.9 [Save Premodel As Dialog](#129-save-premodel-as-dialog)
    - 12.10 [Export Premodel Dialog](#1210-export-premodel-dialog)
13. [Source File Index](#13-source-file-index)

---

## 1. Overview

| Property | Value |
|----------|-------|
| Application name | LoTREC 2.0 |
| Title bar text | `LoTREC 2.0 - Tableaux Theorem Prover` |
| Technology | Java Swing + Cytoscape graph visualization |
| Main class | `lotrec.Launcher` |
| GUI entry point | `lotrec.gui.MainFrame` |

**Startup sequence:**

1. **Splash Screen** — centered on screen, displayed during application loading
2. **Main Frame** — maximized JFrame, becomes the primary application window
3. **Task Pane** — modal dialog centered on Main Frame, prompts user to load a logic

---

## 2. Splash Screen

| | |
|---|---|
| **Screenshot** | `00 Splash Screen.png` |
| **Description** | Centered on screen, displayed while the application loads. Shows "LoTREC 2.0 - Tableau Theorem Prover" branding with the application logo. |
| **Swing type** | JWindow (undecorated) |
| **Source** | `src/lotrec/Launcher.java` |

---

## 3. Task Pane (Startup Dialog)

| | |
|---|---|
| **Screenshot** | `01 Task Pane.png` |
| **Source** | `src/lotrec/gui/MainFrame.java` (`dlgTaskPane`) |
| **Swing type** | JDialog (modal, always-on-top, centered on Main Frame) |

**Layout:**

The Task Pane is a modal startup dialog with clickable sections:

| Section | Behavior |
|---------|----------|
| **Open Predefined Logic** | |
| — K | Loads the K modal logic directly |
| — Model Checking | Loads Model Checking logic directly |
| — Others... | Opens the Predefined Logics Dialog (section 12.1) |
| **Open Existing File** | Opens a file chooser to load a logic XML file |
| **Create Your Own** | Creates a new empty logic definition |
| **Visit our web site** | Opens the LoTREC website in the system browser |

**Interaction:** All labels are clickable with hover effects — blue text color, underline decoration, and hand cursor on hover.

---

## 4. Main Frame Layout

| | |
|---|---|
| **Screenshots** | `02 Main Frame.png`, `04 Main Frame with Loaded Logic - Connector Tab by Default.png`, `21 MainFrame - Build Premodels - Tableaux Built Listed and Graph Displayed - First Tableau Graph Displayed by default.png` |
| **Source** | `src/lotrec/gui/MainFrame.java` |
| **Swing type** | JFrame (maximized, `MAXIMIZED_BOTH`) |

**Title bar:** `LoTREC 2.0 - Tableaux Theorem Prover` — appends the logic filename when a logic is loaded (e.g., `LoTREC 2.0 - Tableaux Theorem Prover - [Monomodal-K.xml]`).

**Menu bar:** Logic | View | Premodels | Help (see section 11)

**Body — Nested Resizable Split Panes:**

```
MainFrame (maximized JFrame)
└─ spltVSplit (JSplitPane HORIZONTAL_SPLIT)
   ├─ LEFT: spltHSplit (JSplitPane VERTICAL_SPLIT)
   │  ├─ TOP: "Loaded Logics" panel        → section 5
   │  └─ BOTTOM: "Premodels Construction
   │              Settings" panel           → section 6
   └─ RIGHT: Tableaux Panel (TableauxPanel)
      ├─ LEFT column (top to bottom):
      │  ├─ "Controls" panel               → section 7
      │  ├─ "Premodels List" panel         → section 8
      │  └─ Zoom panel                     → section 9
      └─ RIGHT: "Premodels Views" panel    → section 10
```

**Resize behavior:** Both `spltVSplit` and `spltHSplit` are user-resizable via draggable dividers. The left column of the Tableaux Panel has a fixed-width layout.

---

## 5. Loaded Logics Panel (top-left)

| | |
|---|---|
| **Screenshots** | `04 Main Frame with Loaded Logic - Connector Tab by Default.png`, `05 Rules Tab.png`, `06 Strategy Tab.png`, `07 Predefined Formulas Tab.png` |
| **Source** | `src/lotrec/gui/LoadedLogicsPanel.java` |
| **Swing type** | JPanel with TitledBorder |

**Titled border:** "Loaded Logics"

**Structure:**

- Contains a **JTabbedPane** (`logicDefTabsPane`) with one tab per loaded logic
- Tab names use the logic filename (e.g., `Monomodal-K.xml`)
- Each tab has a **close button** (x) via `LogicTabComponent` (`src/lotrec/gui/LogicTabComponent.java`)
- Inside each logic tab: a `LogicDefTab` containing a nested **JTabbedPane** (`conRulesTabPane`) with 4 sub-tabs:

| Sub-tab | Panel class | Default? |
|---------|-------------|----------|
| **Connectors** | `ConnTabPanel` | Yes (selected by default) |
| **Rules** | `RulesTabPanel` | |
| **Strategies** | `StratTabPanel` | |
| **Predefined Formulas** | `TestingFormulaePanel` | |

**Source for sub-tabs:** `src/lotrec/gui/logicspane/LogicDefTab.java`

---

### 5.1 Connectors Tab

| | |
|---|---|
| **Screenshots** | `04 Main Frame with Loaded Logic - Connector Tab by Default.png`, `08 New Connector Dialog.png`, `09 New Connector Dialog with Predefined List.png`, `10 Edit Connector Dialog.png` |
| **Source** | `src/lotrec/gui/logicspane/ConnTabPanel.java` |

**Layout — two columns:**

| Left column | Right column |
|-------------|-------------|
| **"Connectors List"** (JList) | **"Selected Connector"** detail panel |
| Buttons: Add, Edit, Delete | Fields (read-only in main view): |
| | — Name (`txfName`) |
| | — Arity (`txfArity`) |
| | — Display (`txfOutputFormat`) — output format string |
| | — Priority (`txfPriority`) |
| | — Associative (`chkbxAssociative`) — checkbox |
| | — Comments (`txaComments`) — text area |

**Dialogs:**

- **New Connector** (`08 New Connector Dialog.png`): Same fields as Selected Connector but editable. Includes an expandable section "Fill in with a predefined connector" showing a scrollable list of predefined connector templates (`09 New Connector Dialog with Predefined List.png`).
- **Edit Connector** (`10 Edit Connector Dialog.png`): Same layout as New Connector, pre-filled with the selected connector's values.

---

### 5.2 Rules Tab

| | |
|---|---|
| **Screenshots** | `05 Rules Tab.png`, `11 Edit Rule.png`, `12 New Rule Dialog.png`, `13 New Condition Dialog.png`, `14 New Action Dialog.png` |
| **Source** | `src/lotrec/gui/logicspane/RulesTabPanel.java` |

**Layout — two columns:**

| Left column | Right column |
|-------------|-------------|
| **"Rules List"** (JList) | **"Selected Rule"** detail panel |
| Buttons: Add, Edit, Delete | — Name |
| Supports drag-and-drop reordering | — Conditions (list) |
| | — Actions (list) |
| | — Comments |

**Dialogs:**

- **New Rule / Edit Rule** (`12 New Rule Dialog.png`, `11 Edit Rule.png`):
  - Name text field
  - Conditions list with Add / Edit / Delete buttons
  - Actions list with Add / Edit / Delete buttons
  - Comments text area

- **New Condition** (`13 New Condition Dialog.png`):
  - Name: JComboBox dropdown of available condition types (e.g., `hasElement`, `isAtomic`, `isLinked`, etc.)
  - Dynamic Parameters fields that change based on the selected condition type (e.g., node, relation, expression fields)
  - Each parameter has an info button (?) showing a tooltip/description

- **New Action** (`14 New Action Dialog.png`):
  - Name: JComboBox dropdown of available action types (e.g., `add`, `createNewNode`, `link`, etc.)
  - Dynamic Parameters fields that change based on the selected action type (e.g., node, formula, mark fields)
  - Each parameter has an info button (?) showing a tooltip/description

**Drag-and-drop:** Rules can be reordered in the Rules List via drag-and-drop (implemented via `RulesListTransferHandler` / `RuleTransferHandler`).

---

### 5.3 Strategies Tab

| | |
|---|---|
| **Screenshot** | `06 Strategy Tab.png` |
| **Source** | `src/lotrec/gui/logicspane/StratTabPanel.java` |

**Layout — two columns:**

| Left column | Right column |
|-------------|-------------|
| **"Strategies List"** (JList, `lstStrategiesList`) | **"Selected Strategy"** detail panel |
| Buttons: Add, Edit, Delete | — Name |
| **"Main Strategy"** dropdown (`cmbxDefaultStrategy`, JComboBox) below the list | — Code (scrollable text area showing strategy structure, e.g., `repeat firstRule Rule1 Rule2 end end`) |
| | — Comments |

The "Main Strategy" dropdown selects which strategy is the entry point for proof execution.

---

### 5.4 Predefined Formulas Tab

| | |
|---|---|
| **Screenshots** | `07 Predefined Formulas Tab.png`, `15 New Formula Dialog.png` |
| **Source** | `src/lotrec/gui/logicspane/TestingFormulaePanel.java` |

**Layout — two columns:**

| Left column | Right column |
|-------------|-------------|
| **"Formulae List"** (JList with horizontal scrollbar, `lstFormulaeList`) | **"Selected Formula"** detail panel |
| Buttons: Add, Edit, Delete | — Name |
| | — Code (prefix notation text area, `txaCodeIn`) |
| | — Comments (`txaCommentsIn`) |

**New Formula Dialog** (`15 New Formula Dialog.png`):
- Code: text area for formula in prefix notation
- Name: radio button choice:
  - `radbtnOutput`: "Use the Code's Output Format" (auto-generate display name from the formula)
  - `radbtnUserName`: "Use this Name" with a text field (`txfNameIn`) for a custom name
- Comments: text area

---

## 6. Premodels Construction Settings Panel (bottom-left)

| | |
|---|---|
| **Screenshots** | `02 Main Frame.png`, `04 Main Frame with Loaded Logic - Connector Tab by Default.png` |
| **Source** | `src/lotrec/gui/ControlsPanel.java` |
| **Swing type** | JPanel with TitledBorder |

**Titled border:** "Premodels Construction Settings"

**Components (top-to-bottom):**

1. **"Select a Formula"** label + **JComboBox** dropdown — populated from the loaded logic's predefined formulas
2. **"Or Compose your own formula"** label + **JTextArea** — multi-line text area for free-form formula input
3. **"infix formula editor..."** clickable link — opens `FormulaTransformerGUI` (`src/lotrec/gui/FormulaTransformerGUI.java`)
4. **Button row:**

| Button | Field name | Action |
|--------|-----------|--------|
| Build Premodels | `btnBuildTableaux` | Runs proof search to completion |
| Step By Step... | `btnSteps` | Opens Break Points dialog (section 12.3) |
| Satisfiability Check... | `btnSatCheck` | Opens Satisfiability Check Options dialog (section 12.4) |

---

## 7. Controls Panel (middle-top)

| | |
|---|---|
| **Screenshot** | `32 Controls Panel.png` |
| **Source** | `src/lotrec/gui/TableauxPanel.java` (`pnlControls`) |
| **Swing type** | JPanel with TitledBorder |

**Titled border:** "Controls"

**Components:**

| Component | Description |
|-----------|-------------|
| **Next** button | Advances one step (step icon) |
| **Pause / Resume** button | Toggle — pauses or resumes execution |
| **Stop** button | Stops the engine |
| **"Next Rule:"** label | Displays the name of the next rule to be applied (e.g., "And") |

**Activation:** This panel is activated during step-by-step execution (after clicking "Start..." in the Break Points dialog). It is disabled/hidden during normal "Build Premodels" execution.

---

## 8. Premodels List Panel (middle)

| | |
|---|---|
| **Screenshots** | `33 Premodels List Panel.png`, `34 Premodels List Panel - Multiple Premodels Selected.png` |
| **Source** | `src/lotrec/gui/TableauxPanel.java` (`lstTableaux`) |
| **Swing type** | JPanel with TitledBorder containing a JList |

**Titled border:** "Premodels List"

**Contents — JList:**

| Item | Description |
|------|-------------|
| **Tableaux Tree** (always first) | Meta-graph showing how premodels derive from each other |
| **premodel.1**, **premodel.2**, ... | Individual premodel names |

**Behavior:**
- Clicking an item displays the corresponding graph in the "Premodels Views" panel (section 10)
- Multi-selection is supported — used for tile/cascade display modes (`34 Premodels List Panel - Multiple Premodels Selected.png`)

---

## 9. Zoom Panel (bottom-middle)

| | |
|---|---|
| **Screenshot** | `35 Zoom Panel.png` |
| **Source** | Cytoscape's built-in `BirdsEyeView` component |

**Description:** A bird's-eye overview miniature of the currently displayed graph. Shows the entire graph at a reduced scale with a viewport rectangle indicating the current visible area in the Premodels Views panel.

**Interaction:** The user can zoom into the Premodels Views panel using right-click + drag; this panel reflects the zoom position and viewport in real time.

---

## 10. Premodels Views Panel (right)

| | |
|---|---|
| **Screenshots** | `21 MainFrame - Build Premodels - Tableaux Built Listed and Graph Displayed - First Tableau Graph Displayed by default.png`, `22 MainFrame - Build Premodels - Tableaux Built Listed and Graph Displayed - Second tableau graph displayed.png`, `23 MainFrame - Build Premodels - Tableaux Built Listed and Graph Displayed - Tableaux Treee Graph displayed.png`, `36 Premodels Views - Tile Sub Windows.png`, `37 Premodels Views - Cascade Sub Windows.png`, `40 Tableaux Tree with 2 Premodels.png` |
| **Source** | `src/lotrec/gui/TableauxPanel.java` (`pnlCyBiModal`), `src/lotrec/gui/CyTableauDisplayer.java` |
| **Swing type** | JPanel with TitledBorder containing a JDesktopPane |

**Titled border:** "Premodels Views"

**Structure:**
- Contains a **JDesktopPane** with **JInternalFrame** sub-windows
- Each internal frame displays a Cytoscape graph visualization of one premodel
- Internal frame features: title bar with premodel name, minimize / maximize / close buttons

**Graph rendering:**
- **Nodes** are rendered as rectangles containing formula text
- **Edges** are labeled arrows with relation names (e.g., `R`)

**Display modes:**

| Mode | Description | Screenshot |
|------|-------------|------------|
| **Single** (default) | One premodel at a time, filling the entire panel | `21...png`, `22...png` |
| **Tile** | All selected premodels arranged side by side | `36 Premodels Views - Tile Sub Windows.png` |
| **Cascade** | Overlapping stacked windows | `37 Premodels Views - Cascade Sub Windows.png` |

**Layout options** (set via View menu):
- **Hierarchic** (default) — top-down tree layout
- **Circular** — circular node arrangement

**Special view — Tableaux Tree:**
- When "Tableaux Tree" is selected in the Premodels List, the view shows a meta-graph where nodes represent premodels and edges represent derivation relationships (`23...png`, `40 Tableaux Tree with 2 Premodels.png`)

---

## 11. Menus

### 11.1 Logic Menu

| | |
|---|---|
| **Screenshot** | `16 Logic Menu.png` |
| **Source** | `src/lotrec/gui/MainFrame.java` (`mnuLogic`) |

| Menu item | Shortcut | Action |
|-----------|----------|--------|
| New... | Ctrl+N | Create a new empty logic |
| Open... | Ctrl+O | Open a logic file from disk (JFileChooser) |
| Predefined Logics... | | Opens the Predefined Logics Dialog (section 12.1) |
| *(separator)* | | |
| Save... | Ctrl+S | Save the current logic |
| Save As... | | Save the current logic to a new file |
| Save All... | | Save all loaded logics |
| *(separator)* | | |
| Close | Ctrl+W | Close the current logic tab |
| Close All | | Close all logic tabs |
| *(separator)* | | |
| Logic Description... | | Opens Logic Description Dialog (section 12.2) |

---

### 11.2 View Menu

| | |
|---|---|
| **Screenshots** | `18 View Menu.png`, `18.1 View Menu - Premodels Display Mode.png`, `38 Tile Premodels Window Menu Item.png`, `39 Cascade Premodels Windows Menu Item.png` |
| **Source** | `src/lotrec/gui/MainFrame.java` (`mnuView`) |

| Menu item | Type | Description |
|-----------|------|-------------|
| Premodels Layout → | Sub-menu | |
| — Hierarchic | Radio (default) | Top-down tree layout |
| — Circular | Radio | Circular node layout |
| Tile premodels window | Toggle item | Tiles all premodel sub-windows side by side (`38...png`). After clicking, the menu item text changes to "Cascade premodels window" and the icon changes (`39...png`). |
| Premodels Display Mode → | Sub-menu (`18.1...png`) | |
| — Only selected one | Radio (default) | Show only the selected premodel |
| — Many selected ones | Radio | Show multiple selected premodels |
| — All premodels | Radio | Show all premodels at once |
| Premodels Filters... | | Opens the Filter Displayed Premodels dialog (section 12.5) |

---

### 11.3 Premodels Menu

| | |
|---|---|
| **Screenshot** | `19 Premodels Menu.png` |
| **Source** | `src/lotrec/gui/MainFrame.java` (`mnuPremodels`) |

| Menu item | Action |
|-----------|--------|
| Load premodel... | Opens JFileChooser to load a premodel XML file (section 12.8) |
| Save selected premodel... | Opens JFileChooser to save the selected premodel (section 12.9) |
| Export Premodel... | Opens export dialog with format selection (section 12.10) |
| *(separator)* | |
| Premodels Editor... | Opens the Premodels Editor dialog (section 12.6) |
| Run Info Window | Opens the Run Info dialog (section 12.7) |

---

### 11.4 Help Menu

| | |
|---|---|
| **Screenshot** | `20 Help Menu.png` |
| **Source** | `src/lotrec/gui/MainFrame.java` |

| Menu item | Action |
|-----------|--------|
| Quick Help... | Shows a quick-start help dialog |
| Tutorial... | Opens tutorial resources |
| Home Page... | Opens the LoTREC website in the system browser |
| About | Shows the About dialog with version and credits |

---

## 12. Dialogs

### 12.1 Predefined Logics Dialog

| | |
|---|---|
| **Screenshot** | `03 Predefined Logics Dialog.png` |
| **Source** | `src/lotrec/gui/MainFrame.java` (`dlgPredefinedLogics`) |
| **Swing type** | JDialog (modal) |

**Components:**
- Scrollable **JList** of predefined logic names (loaded from `src/lotrec/logics/*.xml`)
- Buttons: **Open** | **Cancel**

**Access:** Via Task Pane "Others..." link or menu Logic > Predefined Logics...

---

### 12.2 Logic Description Dialog

| | |
|---|---|
| **Screenshot** | `17 Logic Description Dialog.png` |
| **Source** | `src/lotrec/gui/logicspane/LogicDefTab.java` (`dlgLogicInfo`), `src/lotrec/gui/logicspane/LogicInfoPanel.java` |
| **Swing type** | JDialog (modal) |

**Components:**
- Logic metadata display: description text, author, last update
- Buttons: **Edit** | **Save** | **Cancel**

**Access:** Via menu Logic > Logic Description...

---

### 12.3 Step By Step — Break Points Dialog

| | |
|---|---|
| **Screenshot** | `24 Step By Step - Break Points.png` |
| **Source** | `src/lotrec/gui/ControlsPanel.java` (`dlgSteps`) |
| **Swing type** | JDialog (modal) |

**Components:**
- **JTree** displaying the strategy structure with checkboxes on each rule node
- The user selects which rules to break on during step-by-step execution
- Buttons: **Select All** | **Invert Selection** | **Start...**

**Behavior:** After clicking "Start...", the engine begins execution and pauses at each selected breakpoint rule. The Controls panel (section 7) becomes active for stepping through.

---

### 12.4 Satisfiability Check Options Dialog

| | |
|---|---|
| **Screenshot** | `25 Satisfiability Check Options.png` |
| **Source** | `src/lotrec/gui/ControlsPanel.java` (`dlgSatCheckOptions`) |
| **Swing type** | JDialog (modal) |

**Components:**
- Radio buttons:
  - "Stop after finding a first open premodel, or"
  - "Pause after each found open premodel"
- Button: **Start...**

---

### 12.5 Filter Displayed Premodels Dialog

| | |
|---|---|
| **Screenshot** | `26 Filter Displayed Premodels.png` |
| **Source** | `src/lotrec/gui/TableauxPanel.java` (`dlgFilters`) |
| **Swing type** | JDialog (modal) |

**Components:**
- Checkbox: **"Hide closed premodels"**
- Dropdown: **"Hide nodes marked as:"** with filter options (default: None)

**Access:** Via menu View > Premodels Filters...

---

### 12.6 Premodels Editor Dialog

| | |
|---|---|
| **Screenshot** | `27 Premodels Editor.png` |
| **Source** | `src/lotrec/gui/TableauxPanel.java` (`dlgPremodelsEditor`) |
| **Swing type** | JDialog (modal) |

**Header text:** "Choose a Premodel, an Action to do, fill in its arguments than Apply."

**Components:**

| Component | Description |
|-----------|-------------|
| Premodel selector | JComboBox dropdown to select a premodel |
| "Stop this premodel" | Radio button — marks the selected premodel as stopped |

**Action groups** (radio buttons, mutually exclusive):

| Action | Parameters |
|--------|-----------|
| Create (new) Node | Node Id (text field) |
| Link (2 existing) Nodes | Parent Node (dropdown), Child Node (dropdown), Relation label (text field) |
| Add a Formula to (an existing) Node | Node (dropdown), Formula (text field) |
| Mark a Node | Node (dropdown), Mark (text field) |
| UnMark a Node | Node (dropdown), Mark (text field) |
| Mark Formulas of a Node | Node (dropdown), Formula (dropdown), Mark (text field) |
| UnMark Formulas of a Node | Node (dropdown), Formula (dropdown), Mark (text field) |

**Buttons:** **Apply** | **Cancel**

**Access:** Via menu Premodels > Premodels Editor...

---

### 12.7 Run Info Dialog

| | |
|---|---|
| **Screenshot** | `28 Run Info.png` |
| **Source** | `src/lotrec/gui/TableauxPanel.java` (`dlgRunInfo`) |
| **Swing type** | JDialog |

**Displays engine execution statistics:**

| Field | Example value |
|-------|---------------|
| Status | Running / Completed |
| Tableaux count | 2 |
| Last applied rule | And |
| Current tableau | premodel.1 |
| Total Applied Rules | 15 |
| Elapsed Time | 0.5s |
| Applied Rules | (breakdown list) |

**Access:** Via menu Premodels > Run Info Window

---

### 12.8 Load a Premodel Dialog

| | |
|---|---|
| **Screenshot** | `29 Load a premodel.png` |
| **Source** | `src/lotrec/gui/dialogs/OpenPremodelDialog.java` |
| **Swing type** | JFileChooser (Open mode) |

**File filter:** `*.xml` (XML Files)

**Access:** Via menu Premodels > Load premodel...

---

### 12.9 Save Premodel As Dialog

| | |
|---|---|
| **Screenshot** | `30 Save Premodel as.png` |
| **Source** | `src/lotrec/gui/dialogs/SaveAsPremodelDialog.java` |
| **Swing type** | JFileChooser (Save mode) |

**File filter:** `*.xml` (XML Files)

**Access:** Via menu Premodels > Save selected premodel...

---

### 12.10 Export Premodel Dialog

| | |
|---|---|
| **Screenshot** | `31 Export Premodel.png` |
| **Source** | `src/lotrec/gui/dialogs/ExportPremodelDialog.java` |
| **Swing type** | JFileChooser (Save mode) with format selector dropdown |

**Export formats:**

| Filter | Format |
|--------|--------|
| `*.png` | PNG Files |
| `*.ps` | Post Script Files |
| `*.pdf` | PDF Files |

**Access:** Via menu Premodels > Export Premodel...

---

## 13. Source File Index

| GUI Component | Source File |
|---------------|------------|
| Application entry / Splash | `src/lotrec/Launcher.java` |
| Main Frame & menus | `src/lotrec/gui/MainFrame.java` |
| Loaded Logics Panel | `src/lotrec/gui/LoadedLogicsPanel.java` |
| Logic tab close button | `src/lotrec/gui/LogicTabComponent.java` |
| Logic definition tabs (4 sub-tabs) | `src/lotrec/gui/logicspane/LogicDefTab.java` |
| Connectors tab | `src/lotrec/gui/logicspane/ConnTabPanel.java` |
| Rules tab | `src/lotrec/gui/logicspane/RulesTabPanel.java` |
| Rules drag-and-drop | `src/lotrec/gui/logicspane/RulesListTransferHandler.java`, `src/lotrec/gui/logicspane/RuleTransferHandler.java` |
| Strategies tab | `src/lotrec/gui/logicspane/StratTabPanel.java` |
| Predefined Formulas tab | `src/lotrec/gui/logicspane/TestingFormulaePanel.java` |
| Logic Description dialog | `src/lotrec/gui/logicspane/LogicInfoPanel.java` |
| Premodels Construction Settings | `src/lotrec/gui/ControlsPanel.java` |
| Infix Formula Editor | `src/lotrec/gui/FormulaTransformerGUI.java` |
| Tableaux Panel (Controls, Premodels List, Views) | `src/lotrec/gui/TableauxPanel.java` |
| Cytoscape graph display | `src/lotrec/gui/CyTableauDisplayer.java` |
| Open Premodel dialog | `src/lotrec/gui/dialogs/OpenPremodelDialog.java` |
| Save Premodel dialog | `src/lotrec/gui/dialogs/SaveAsPremodelDialog.java` |
| Export Premodel dialog | `src/lotrec/gui/dialogs/ExportPremodelDialog.java` |
| Dialogs utility | `src/lotrec/gui/DialogsFactory.java` |

---

## Screenshot Reference

All 42 screenshots in this directory and their primary documentation section:

| # | Screenshot filename | Section |
|---|-------------------|---------|
| 00 | `00 Splash Screen.png` | 2 |
| 01 | `01 Task Pane.png` | 3 |
| 02 | `02 Main Frame.png` | 4, 6 |
| 03 | `03 Predefined Logics Dialog.png` | 12.1 |
| 04 | `04 Main Frame with Loaded Logic - Connector Tab by Default.png` | 4, 5, 5.1, 6 |
| 05 | `05 Rules Tab.png` | 5.2 |
| 06 | `06 Strategy Tab.png` | 5.3 |
| 07 | `07 Predefined Formulas Tab.png` | 5.4 |
| 08 | `08 New Connector Dialog.png` | 5.1 |
| 09 | `09 New Connector Dialog with Predefined List.png` | 5.1 |
| 10 | `10 Edit Connector Dialog.png` | 5.1 |
| 11 | `11 Edit Rule.png` | 5.2 |
| 12 | `12 New Rule Dialog.png` | 5.2 |
| 13 | `13 New Condition Dialog.png` | 5.2 |
| 14 | `14 New Action Dialog.png` | 5.2 |
| 15 | `15 New Formula Dialog.png` | 5.4 |
| 16 | `16 Logic Menu.png` | 11.1 |
| 17 | `17 Logic Description Dialog.png` | 12.2 |
| 18 | `18 View Menu.png` | 11.2 |
| 18.1 | `18.1 View Menu - Premodels Display Mode.png` | 11.2 |
| 19 | `19 Premodels Menu.png` | 11.3 |
| 20 | `20 Help Menu.png` | 11.4 |
| 21 | `21 MainFrame - Build Premodels - Tableaux Built Listed and Graph Displayed - First Tableau Graph Displayed by default.png` | 4, 10 |
| 22 | `22 MainFrame - Build Premodels - Tableaux Built Listed and Graph Displayed - Second tableau graph displayed.png` | 10 |
| 23 | `23 MainFrame - Build Premodels - Tableaux Built Listed and Graph Displayed - Tableaux Treee Graph displayed.png` | 10 |
| 24 | `24 Step By Step - Break Points.png` | 12.3 |
| 25 | `25 Satisfiability Check Options.png` | 12.4 |
| 26 | `26 Filter Displayed Premodels.png` | 12.5 |
| 27 | `27 Premodels Editor.png` | 12.6 |
| 28 | `28 Run Info.png` | 12.7 |
| 29 | `29 Load a premodel.png` | 12.8 |
| 30 | `30 Save Premodel as.png` | 12.9 |
| 31 | `31 Export Premodel.png` | 12.10 |
| 32 | `32 Controls Panel.png` | 7 |
| 33 | `33 Premodels List Panel.png` | 8 |
| 34 | `34 Premodels List Panel - Multiple Premodels Selected.png` | 8 |
| 35 | `35 Zoom Panel.png` | 9 |
| 36 | `36 Premodels Views - Tile Sub Windows.png` | 10 |
| 37 | `37 Premodels Views - Cascade Sub Windows.png` | 10 |
| 38 | `38 Tile Premodels Window Menu Item.png` | 11.2 |
| 39 | `39 Cascade Premodels Windows Menu Item.png` | 11.2 |
| 40 | `40 Tableaux Tree with 2 Premodels.png` | 10 |
