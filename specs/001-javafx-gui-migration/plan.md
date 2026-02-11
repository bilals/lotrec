# Implementation Plan: [FEAT-001] JavaFX GUI Migration

> Step-by-step implementation plan for migrating LoTREC's GUI from Java Swing to JavaFX, including Java 21 upgrade and visual validation infrastructure.

## Metadata

| Field | Value |
|-------|-------|
| **Plan ID** | PLAN-001 |
| **Spec Reference** | [FEAT-001](spec.md) |
| **Research Reference** | [research.md](research.md) |
| **Author** | Claude Code |
| **Created** | 2026-02-11 |
| **Status** | Draft |

---

## Technical Context

### Current State

| Aspect | Current | Target |
|--------|---------|--------|
| Java version | 1.8 | 21 (LTS) |
| GUI framework | Swing/AWT | JavaFX (OpenJFX 21) |
| GUI entry point | `lotrec.Launcher` → `MainFrame` | `lotrec.guifx.LauncherFX` → `MainFrameFX` |
| Gradle `run` task | Launches Swing (`lotrec.Launcher`) | Launches JavaFX (`lotrec.guifx.LauncherFX`) |
| Swing access | `gradlew run` | `gradlew runSwing` |
| JAXB namespace | `javax.xml.bind` | `jakarta.xml.bind` |
| Graph visualization | Cytoscape 2.x (Swing-native) | Cytoscape 2.x via SwingNode bridge |
| UI construction | Programmatic Swing | Programmatic JavaFX + CSS |
| Module system | Classpath | Classpath (no JPMS) |

### Codebase Inventory

| Component | Files | Lines | Migration Impact |
|-----------|-------|-------|-----------------|
| Swing GUI (`lotrec.gui.*`) | 42 | ~14,608 | Full migration — create JavaFX equivalents |
| Engine (`lotrec.engine`) | 8 | ~1,471 | Add JavaFXEngineListener only |
| Cytoscape (`cytoscape.*`) | 512 | ~10,044 | JAXB migration + SwingNode bridge |
| JAXB imports | 87 files | 390 import sites | `javax.xml.bind` → `jakarta.xml.bind` |
| Backend (parser, process, data) | ~200 | ~25,000 | No changes |

### Key Swing Components (migration targets)

| Component | File | Lines | Complexity |
|-----------|------|-------|------------|
| MainFrame | `lotrec/gui/MainFrame.java` | 1,908 | High — orchestrates all panels |
| TableauxPanel | `lotrec/gui/TableauxPanel.java` | 2,155 | High — premodel display + Cytoscape |
| RulesTabPanel | `lotrec/gui/logicspane/RulesTabPanel.java` | 2,513 | Very High — drag-drop, tree editing |
| ConnTabPanel | `lotrec/gui/logicspane/ConnTabPanel.java` | 1,270 | Medium |
| StratTabPanel | `lotrec/gui/logicspane/StratTabPanel.java` | 1,035 | Medium |
| ControlsPanel | `lotrec/gui/ControlsPanel.java` | 903 | Medium |
| TestingFormulaePanel | `lotrec/gui/logicspane/TestingFormulaePanel.java` | 714 | Medium |
| DialogsFactory | `lotrec/gui/DialogsFactory.java` | 161 | Low |
| LoadedLogicsPanel | `lotrec/gui/LoadedLogicsPanel.java` | 257 | Low |
| FormulaTransformerGUI | `lotrec/gui/FormulaTransformerGUI.java` | 257 | Low |

### Dependencies to Add

```kotlin
// build.gradle.kts additions
val javafxVersion = "21.0.5"
val javafxPlatform = /* win/mac/linux detection */

// JavaFX (OpenJFX 21 LTS)
implementation("org.openjfx:javafx-base:$javafxVersion:$javafxPlatform")
implementation("org.openjfx:javafx-controls:$javafxVersion:$javafxPlatform")
implementation("org.openjfx:javafx-graphics:$javafxVersion:$javafxPlatform")
implementation("org.openjfx:javafx-swing:$javafxVersion:$javafxPlatform")  // SwingNode bridge

// Jakarta JAXB (replaces removed javax.xml.bind)
implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
runtimeOnly("com.sun.xml.bind:jaxb-impl:4.0.5")

// TestFX (JavaFX UI testing)
testImplementation("org.testfx:testfx-core:4.0.18")
testImplementation("org.testfx:testfx-junit5:4.0.18")
testRuntimeOnly("org.testfx:openjfx-monocle:21.0.2")
```

---

## Constitution Check

| Constraint | Status | Notes |
|------------|--------|-------|
| Java version | **Unlocked** | Constitution approves migration from 1.8 → 21 |
| GUI framework | **Unlocked** | Constitution approves Swing → JavaFX migration |
| Build system (Gradle) | Locked ✅ | No changes to build system — only adding tasks |
| Testing (JUnit 5 + AssertJ) | Locked ✅ | Existing framework extended with TestFX |
| Coverage (JaCoCo) | Locked ✅ | No changes |
| Parser (JavaCC) | Locked ✅ | No changes |
| Graph visualization | **Unlocked** | Cytoscape retained via SwingNode bridge (Phase 4 replacement) |
| TDD discipline | ✅ | Red-Green-Refactor followed per milestone |
| Layer rules | ✅ | `lotrec.guifx.*` is leaf layer — imports from backend, not imported by anything |
| Module placement | ✅ | All new code follows decision tree in constitution §4 |
| Code reuse | ✅ | EngineListener pattern reused; backend classes called directly |

**Gate check: PASS** — No constitution violations. All technology changes use approved Unlocked paths.

---

## Prerequisites Checklist

- [x] Specification approved (Status: Draft → review pending)
- [x] Research complete (`research.md` — all 10 decisions resolved)
- [x] Tech stack constraints verified (constitution check above)
- [x] Architecture constraints verified (layer rules, dependency direction)
- [x] Existing code to reuse identified (spec §7, research R4/R7)
- [x] Test environment ready (`gradlew test` passes on current Java 8)
- [x] Development branch created (`001-javafx-gui-migration`)

---

## Milestone 0: Java 21 (LTS) Upgrade

> **Goal**: Compile, test, and run the entire application on Java 21. No functional changes.
>
> **Requirements**: FR-01, FR-02, FR-03, FR-04, FR-05

### M0-Step 1: Update Build Configuration

**Files to modify**: `build.gradle.kts`

1. Change `sourceCompatibility` and `targetCompatibility` from `VERSION_1_8` to `VERSION_21`
2. Add Jakarta JAXB dependencies:
   ```kotlin
   implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
   runtimeOnly("com.sun.xml.bind:jaxb-impl:4.0.5")
   ```
3. Verify Gradle wrapper is 8.5+ (Java 21 compatible)

**Verification**:
```bash
gradlew.bat compileJava   # Must compile with Java 21 target
```

### M0-Step 2: Migrate JAXB Imports

**Files affected**: 87 files across `src/cytoscape/` (390 import sites)

1. Bulk replace across all affected files:
   - `javax.xml.bind` → `jakarta.xml.bind`
   - `javax.xml.bind.annotation` → `jakarta.xml.bind.annotation`
2. Verify no `javax.annotation` imports exist (confirmed: 0 files affected)
3. Check `JAXBContext.newInstance()` calls still resolve correctly

**Breakdown by directory** (verified counts):
| Directory | Files | Type |
|-----------|-------|------|
| `cytoscape/generated/` | 27 | Auto-generated JAXB classes |
| `cytoscape/generated2/` | 38 | Auto-generated JAXB classes |
| `cytoscape/bookmarks/` | 5 | Auto-generated bookmark classes |
| `cytoscape/data/readers/` | 4 | Hand-written XML readers |
| `cytoscape/data/writers/` | 2 | Hand-written XML writers |
| Other `cytoscape/` | 11 | Dialogs, actions, servers, util, Cytoscape.java |
| **Total** | **87** | |

**Verification**:
```bash
gradlew.bat compileJava   # Zero javax.xml.bind errors
```

### M0-Step 3: Fix Any Java 21 Incompatibilities

1. Compile full project and address any deprecation/removal errors beyond JAXB
2. Run full test suite:
   ```bash
   gradlew.bat test         # All existing tests pass
   ```
3. Manual smoke test: launch application via `gradlew.bat run`, load K.xml, run tableau

### M0-Step 4: Update Constitution and Build Metadata

**Files to modify**: `.specify/memory/constitution.md`

1. Update Java version in Locked Technology Stack table: `1.8` → `21`
2. Update any references to Java 8 compatibility

### M0 Checkpoint

- [ ] `gradlew.bat build` succeeds on Java 21
- [ ] All existing tests pass (FR-02)
- [ ] Application launches and runs correctly (FR-01)
- [ ] No `javax.xml.bind` imports remain (FR-03)
- [ ] Constitution updated (FR-04)
- [ ] `build.gradle.kts` targets Java 21 (FR-05)

---

## Milestone 0.5: Visual Validation Infrastructure

> **Goal**: Establish automated screenshot capture and comparison tooling.
>
> **Requirements**: FR-06, FR-07, FR-08, FR-09, FR-10

### M0.5-Step 1: Swing Baseline Screenshot Capture

**New file**: `test/lotrec/gui/SwingScreenshotCapture.java`

1. Create a test utility that programmatically launches the Swing GUI
2. Navigate to each of the 42 key application states (referencing `.specify/memory/GUI-V2/` screenshots)
3. Capture each state using `java.awt.Robot.createScreenCapture()` on the relevant Swing component
4. Save PNGs to `specs/001-javafx-gui-migration/screenshots/swing-baseline/`

**Key states to capture** (grouped, matching 42 reference screenshots in `.specify/memory/GUI-V2/`):
| Group | States | Count |
|-------|--------|-------|
| Startup | Splash screen (#00), Task pane (#01) | 2 |
| Main frame variants | Empty (#02), with loaded logic (#04), with first tableau (#21), second tableau (#22), tableaux tree (#23) | 5 |
| Connector dialogs | New connector (#08), new connector with predefined list (#09), edit connector (#10) | 3 |
| Rules tab + editing | Rules tab (#05), edit rule (#11), new rule (#12), new condition (#13), new action (#14) | 5 |
| Strategies tab | Strategy tab (#06) | 1 |
| Formulas tab | Predefined formulas (#07), new formula (#15) | 2 |
| Controls panel | Controls panel (#32) | 1 |
| Premodels list | List (#33), multiple selected (#34) | 2 |
| Graph/tableaux views | Zoom panel (#35), tile sub windows (#36), cascade sub windows (#37), tableaux tree with 2 premodels (#40) | 4 |
| Menus | Logic (#16), View (#18), View-Premodels mode (#18.1), Premodels (#19), Help (#20) | 5 |
| Menu actions | Tile menu item (#38), Cascade menu item (#39) | 2 |
| Dialogs | Predefined Logics (#03), Logic Description (#17), Break Points (#24), Satisfiability (#25), Filter (#26), Premodel Editor (#27), Run Info (#28) | 7 |
| File dialogs | Load premodel (#29), Save As (#30), Export (#31) | 3 |
| **Total** | | **42** |

### M0.5-Step 2: JavaFX Screenshot Capture Utility

**New file**: `test/lotrec/guifx/JavaFXScreenshotCapture.java`

1. Create a TestFX-based utility for capturing JavaFX component screenshots
2. Use `Scene.snapshot()` or `FxRobot.capture()` for each migrated component
3. Save PNGs to `specs/001-javafx-gui-migration/screenshots/javafx-current/`

### M0.5-Step 3: Comparison Tool

**New file**: `test/lotrec/guifx/validation/VisualComparisonTool.java`

1. Load Swing baseline and JavaFX current screenshots side-by-side
2. Report structural differences (missing controls, layout mismatches, sizing issues)
3. Generate a comparison report (HTML or Markdown) with side-by-side images
4. Structural comparison, not pixel-exact (per research R8)

### M0.5 Checkpoint

- [ ] Swing baseline screenshots captured for key states (FR-06)
- [ ] JavaFX screenshot capture utility ready (FR-07)
- [ ] Comparison tool generates comparison report (FR-08)
- [ ] Screenshots cover the 42 reference states (FR-09)
- [ ] Capture is programmatic, not manual (FR-10)

---

## Milestone 1: JavaFX GUI Migration

> **Goal**: Build a complete JavaFX GUI in `lotrec.guifx` package, migrating components incrementally.
>
> **Requirements**: FR-11 through FR-23, NFR-01 through NFR-07

### Build Configuration (pre-migration setup)

**File**: `build.gradle.kts`

1. Add OpenJFX 21 dependencies (with platform-specific classifiers):
   ```kotlin
   val javafxVersion = "21.0.5"
   val javafxPlatform = when {
       org.gradle.internal.os.OperatingSystem.current().isWindows -> "win"
       org.gradle.internal.os.OperatingSystem.current().isMacOsX -> "mac"
       else -> "linux"
   }
   implementation("org.openjfx:javafx-base:$javafxVersion:$javafxPlatform")
   implementation("org.openjfx:javafx-controls:$javafxVersion:$javafxPlatform")
   implementation("org.openjfx:javafx-graphics:$javafxVersion:$javafxPlatform")
   implementation("org.openjfx:javafx-swing:$javafxVersion:$javafxPlatform")
   ```
2. Add TestFX dependencies:
   ```kotlin
   testImplementation("org.testfx:testfx-core:4.0.18")
   testImplementation("org.testfx:testfx-junit5:4.0.18")
   testRuntimeOnly("org.testfx:openjfx-monocle:21.0.2")
   ```
3. Configure Gradle tasks for dual GUI:
   - `run` task: `mainClass.set("lotrec.guifx.LauncherFX")` (JavaFX — default)
   - New `runSwing` task: `mainClass.set("lotrec.Launcher")` (Swing — on demand)
4. Add `--add-opens` JVM arguments for classpath JavaFX access (no JPMS)
5. Verify both `gradlew.bat run` and `gradlew.bat runSwing` compile

### Migration Steps (spec §8.5 order)

Each step follows this pattern:
1. **RED**: Write TestFX test(s) for the component (test fails — component doesn't exist)
2. **GREEN**: Implement the JavaFX component to pass the test
3. **Validate**: Run visual comparison against Swing baseline
4. **Verify**: `gradlew.bat test` — all tests pass, Swing GUI still works

---

#### M1-Step 1: LauncherFX + MainFrameFX Shell

**Requirements**: FR-11, FR-17, FR-20

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/LauncherFX.java` | JavaFX `Application` entry point with splash screen |
| `src/lotrec/guifx/MainFrameFX.java` | Top-level `Stage` with menu bar, split panes, empty panel placeholders |
| `src/lotrec/guifx/styles/default.css` | Base CSS stylesheet |
| `test/lotrec/guifx/MainFrameFXTest.java` | TestFX test for main window launch and layout |

**Implementation details**:
- `LauncherFX extends javafx.application.Application`
- `MainFrameFX` creates: `MenuBar`, vertical `SplitPane` (left: logic panel area, right: tableaux area), horizontal `SplitPane` (tableaux display area, controls area)
- Menu bar mirrors `MainFrame` menus: File, Logic, Tableaux, Help
- All panel areas initially show placeholder labels ("Panel pending migration")
- Splash screen shows during initialization (FR-20)
- CSS stylesheet loaded from `lotrec/guifx/styles/default.css`

**Reference**: `lotrec/gui/MainFrame.java` (1,908 lines) — mirror layout structure

**Tests** (RED first):
- `shouldLaunchMainWindow()` — Stage shows with correct title
- `shouldDisplayMenuBar()` — Menu bar has File, Logic, Tableaux, Help menus
- `shouldDisplaySplitPaneLayout()` — Split panes present with correct orientation

**Verification**:
```bash
gradlew.bat test --tests "*MainFrameFXTest"  # New tests pass
gradlew.bat test                              # All existing tests still pass
gradlew.bat runSwing                          # Swing GUI still works
```

---

#### M1-Step 2: Simple Dialogs

**Requirements**: FR-13 (subset)

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/dialogs/PredefinedLogicsDialog.java` | Logic selection dialog |
| `src/lotrec/guifx/dialogs/LogicDescriptionDialog.java` | Logic metadata viewer |
| `src/lotrec/guifx/dialogs/SatisfiabilityDialog.java` | Check options dialog |
| `src/lotrec/guifx/dialogs/FilterDialog.java` | Filter configuration dialog |
| `src/lotrec/guifx/dialogs/RunInfoDialog.java` | Run information display |
| `src/lotrec/guifx/DialogsFactory.java` | Central dialog creation factory |
| `test/lotrec/guifx/dialogs/SimpleDialogsTest.java` | TestFX tests for all simple dialogs |

**Implementation details**:
- Each dialog extends `javafx.scene.control.Dialog` or uses `Stage` for custom layout
- Dialogs use JavaFX controls (`TextField`, `ComboBox`, `Button`, `ListView`) programmatically
- `DialogsFactory` centralizes dialog creation (mirrors `lotrec.gui.DialogsFactory` pattern)
- Predefined Logics dialog lists all 38 logics from `lotrec.resources`

**Reference**: `lotrec/gui/DialogsFactory.java` (161 lines)

**Tests** (RED first):
- `shouldShowPredefinedLogicsDialog()` — Lists all predefined logics
- `shouldShowLogicDescriptionDialog()` — Displays logic metadata
- `shouldShowSatisfiabilityDialog()` — Shows check options

---

#### M1-Step 3: Loaded Logics Panel + Connectors Tab

**Requirements**: FR-12 (partial)

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/LoadedLogicsPane.java` | Logic tab container (replaces `LoadedLogicsPanel`) |
| `src/lotrec/guifx/logicspane/LogicDefTab.java` | Single logic definition tab |
| `src/lotrec/guifx/logicspane/ConnTabPane.java` | Connectors editing tab |
| `test/lotrec/guifx/logicspane/ConnTabPaneTest.java` | TestFX test for connector editing |

**Implementation details**:
- `LoadedLogicsPane` uses JavaFX `TabPane` for loaded logics (each logic is a tab)
- Each `LogicDefTab` contains sub-tabs: Connectors, Rules, Strategies, Formulas
- `ConnTabPane` replicates `ConnTabPanel` (1,270 lines): connector list, name/arity/format/priority editing, add/remove/edit buttons
- Uses JavaFX `TableView` or `ListView` for connector list
- Property bindings for field updates

**Reference**: `lotrec/gui/logicspane/ConnTabPanel.java` (1,270 lines), `LogicDefTab.java` (310 lines), `LoadedLogicsPanel.java` (257 lines)

**Tests** (RED first):
- `shouldDisplayConnectorsList()` — Shows connectors for loaded logic
- `shouldAddNewConnector()` — Add button creates connector
- `shouldEditConnectorProperties()` — Name, arity, format, priority fields work

---

#### M1-Step 4: Strategies Tab + Predefined Formulas Tab

**Requirements**: FR-12 (partial)

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/logicspane/StratTabPane.java` | Strategies editing tab |
| `src/lotrec/guifx/logicspane/TestingFormulaePane.java` | Predefined formulas tab |
| `test/lotrec/guifx/logicspane/StratTabPaneTest.java` | Strategy editing tests |
| `test/lotrec/guifx/logicspane/TestingFormulaePaneTest.java` | Formula editing tests |

**Implementation details**:
- `StratTabPane` replicates `StratTabPanel` (1,035 lines): strategy list, code editor, add/remove
- `TestingFormulaePane` replicates `TestingFormulaePanel` (714 lines): formula list, formula input
- Strategy code editor uses JavaFX `TextArea` with basic syntax feedback

**Reference**: `lotrec/gui/logicspane/StratTabPanel.java` (1,035 lines), `TestingFormulaePanel.java` (714 lines)

---

#### M1-Step 5: Rules Tab

**Requirements**: FR-12 (complete)

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/logicspane/RulesTabPane.java` | Rules editing tab (most complex) |
| `src/lotrec/guifx/dialogs/ConditionDialog.java` | Condition editing dialog |
| `src/lotrec/guifx/dialogs/ActionDialog.java` | Action editing dialog |
| `test/lotrec/guifx/logicspane/RulesTabPaneTest.java` | Rule editing tests |

**Implementation details**:
- Replicates `RulesTabPanel` (2,513 lines) — the most complex single component
- JavaFX `TreeView` replaces Swing `JTree` for rule hierarchy display
- Drag-and-drop support via JavaFX DnD API (replaces `RulesListTransferHandler`, `RuleTransferHandler`)
- Condition/Action dialogs for editing rule components
- Break into sub-components to manage complexity:
  - Rule list view (TreeView)
  - Rule detail editor
  - Condition list editor
  - Action list editor
  - Drag-drop handlers

**Reference**: `lotrec/gui/logicspane/RulesTabPanel.java` (2,513 lines), plus 5 transfer handler files (~690 lines)

**Risk mitigation**: This step is the highest complexity. Break into sub-PRs if needed.

---

#### M1-Step 6: Premodels Construction Settings Panel

**Requirements**: FR-15

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/PremodelSettingsPane.java` | Premodel construction settings (formula input + build action) |
| `test/lotrec/guifx/PremodelSettingsPaneTest.java` | Premodel settings tests |

**Implementation details**:
- Formula input `TextField` with Build/Analyze button
- Formula validation display
- Calls `Lotrec.parseFormula()` and `TransformerGUI.toPrefix()` for processing
- Connected to loaded logic's connectors for parsing context
- This is the premodel construction area currently embedded in `TableauxPanel`, extracted as a standalone pane

**Note**: `FormulaTransformerPane` (the standalone infix/prefix conversion utility mirroring `FormulaTransformerGUI`) is a separate component, created in M1-Step 10 (T061).

**Reference**: `lotrec/gui/TableauxPanel.java` (premodel settings area within 2,155 lines)

---

#### M1-Step 7: Controls Panel

**Requirements**: FR-14

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/ControlsPane.java` | Proof search controls |
| `test/lotrec/guifx/ControlsPaneTest.java` | Controls behavior tests |

**Implementation details**:
- Step, Pause, Stop, Run buttons using JavaFX `Button` with icons
- Button state management (enabled/disabled based on engine state)
- Communicates with `Engine` identically to Swing version
- Rule breakpoint selection (if present in current `ControlsPanel`)

**Reference**: `lotrec/gui/ControlsPanel.java` (903 lines), `NewControlsPanel.java` (160 lines)

---

#### M1-Step 8: Premodels List Panel

**Requirements**: FR-15 (partial)

**New files**:
| File | Purpose |
|------|---------|
| (Integrated into `TableauxPane.java`) | Premodel list within tableaux panel |

**Implementation details**:
- `TableauxPane` hosts the premodel list (left side) and graph view (right side)
- JavaFX `ListView` or `TreeView` for premodel navigation
- Selection changes update the graph visualization area

**Reference**: `lotrec/gui/TableauxPanel.java` (2,155 lines — partial)

---

#### M1-Step 9: Premodels Views Panel + Cytoscape Bridge

**Requirements**: FR-16

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/TableauxPane.java` | Complete tableaux panel (list + graph view) |
| `src/lotrec/guifx/graph/CytoscapeSwingBridge.java` | SwingNode wrapper for Cytoscape |
| `test/lotrec/guifx/graph/CytoscapeSwingBridgeTest.java` | Bridge rendering tests |

**Implementation details**:
- `CytoscapeSwingBridge extends StackPane` containing a `SwingNode`
- Cytoscape operations run on EDT via `SwingUtilities.invokeLater()`
- JavaFX updates run on FX thread via `Platform.runLater()`
- Error boundary: on bridge failure, display error placeholder label (FR-16 / clarification)
- `SwingNode` handles most cross-thread coordination automatically

**Reference**: Research R4 (SwingNode bridge pattern), `lotrec/gui/CyTableauDisplayer.java` (264 lines)

**Threading model**:
```
Engine Thread → JavaFXEngineListener → Platform.runLater() → TableauxPane
                                                              ↓
                                                     CytoscapeSwingBridge
                                                              ↓
                                              SwingUtilities.invokeLater()
                                                              ↓
                                                     Cytoscape JComponent
```

---

#### M1-Step 10: Complex Dialogs

**Requirements**: FR-13 (complete), FR-19

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/guifx/dialogs/PremodelEditorDialog.java` | Premodel editing dialog |
| `src/lotrec/guifx/dialogs/BreakPointsDialog.java` | Rule breakpoints dialog |
| `src/lotrec/guifx/dialogs/FileDialogs.java` | Open/Save/Export file choosers |
| `src/lotrec/guifx/dialogs/TaskPaneDialog.java` | Startup task pane (FR-19) |
| `test/lotrec/guifx/dialogs/ComplexDialogsTest.java` | Complex dialog tests |

**Implementation details**:
- `PremodelEditorDialog`: mirrors existing premodel editor with JavaFX controls
- `BreakPointsDialog`: rule breakpoint selection (tied to ControlsPane)
- `FileDialogs`: uses JavaFX `FileChooser` and `DirectoryChooser` (much simpler than Swing `JFileChooser`)
- `TaskPaneDialog`: startup dialog with quick-access options (load predefined, open file, create new)

**Reference**: `lotrec/gui/dialogs/` (17 files), `lotrec/gui/MainFrame.java` (task pane logic)

---

#### M1-Step 11: Engine Integration

**Requirements**: FR-18

**New files**:
| File | Purpose |
|------|---------|
| `src/lotrec/engine/JavaFXEngineListener.java` | Engine-to-JavaFX bridge |
| `test/lotrec/engine/JavaFXEngineListenerTest.java` | Listener dispatch tests |

**Implementation details**:
- Implements `EngineListener` interface (same as `SwingEngineListener`)
- All 16 listener methods wrap callbacks in `Platform.runLater()`
- Mechanical translation from `SwingUtilities.invokeLater()` to `Platform.runLater()`
- Holds reference to `MainFrameFX` (or its sub-components) for UI updates

**Reference**: `lotrec/engine/SwingEngineListener.java` (182 lines), `HeadlessEngineListener.java` (287 lines), Research R7

**Tests** (RED first):
- `shouldDispatchOnJavaFXThread()` — Verify `Platform.runLater()` is called
- `shouldUpdateUIOnEngineStart()` — Engine start triggers UI state change
- `shouldUpdateUIOnEngineStop()` — Engine stop triggers UI state change

---

### Milestone 1 Final Integration

After all 11 steps:

1. **Wire LauncherFX → full GUI**: Remove placeholder labels, connect all real panels
2. **Wire Gradle tasks**: `run` → `LauncherFX`, `runSwing` → `Launcher`
3. **End-to-end test**: Load K.xml → enter formula → run tableau → view graph
4. **Run all 38 predefined logics** through JavaFX GUI (batch test)
5. **Visual comparison**: Run comparison tool against all 42 Swing baseline screenshots
6. **Keyboard shortcuts**: Verify all accelerators match existing Swing shortcuts (NFR-03)
7. **Startup time**: Measure and verify ≤5s (NFR-01)
8. **Responsiveness**: Verify UI responds within 200ms during proof search (NFR-02)

### M1 Checkpoint

- [ ] All 38 predefined logics load and display correctly (Success Criterion 1)
- [ ] All 42 GUI states are functional (Success Criterion 2)
- [ ] Layout structurally equivalent to Swing (Success Criterion 3, NFR-04)
- [ ] Keyboard shortcuts preserved (NFR-03)
- [ ] Startup time ≤5s (NFR-01)
- [ ] Responsive during proof search (NFR-02)
- [ ] `gradlew.bat run` launches JavaFX GUI (FR-17)
- [ ] `gradlew.bat runSwing` launches Swing GUI (FR-22)
- [ ] All existing tests pass (regression)
- [ ] All new TestFX tests pass
- [ ] Visual comparison report generated with no critical differences

---

## New Package Structure

```
src/lotrec/guifx/
├── LauncherFX.java                    # M1-Step 1
├── MainFrameFX.java                   # M1-Step 1
├── LoadedLogicsPane.java              # M1-Step 3
├── ControlsPane.java                  # M1-Step 7
├── TableauxPane.java                  # M1-Step 8-9
├── DialogsFactory.java                # M1-Step 2
├── PremodelSettingsPane.java          # M1-Step 6
├── FormulaTransformerPane.java        # M1-Step 10
├── dialogs/                           # M1-Steps 2, 10
│   ├── PredefinedLogicsDialog.java
│   ├── LogicDescriptionDialog.java
│   ├── SatisfiabilityDialog.java
│   ├── FilterDialog.java
│   ├── RunInfoDialog.java
│   ├── ConditionDialog.java           # M1-Step 5
│   ├── ActionDialog.java              # M1-Step 5
│   ├── PremodelEditorDialog.java      # M1-Step 10
│   ├── BreakPointsDialog.java         # M1-Step 10
│   ├── FileDialogs.java               # M1-Step 10
│   └── TaskPaneDialog.java            # M1-Step 10
├── logicspane/                        # M1-Steps 3-5
│   ├── LogicDefTab.java
│   ├── ConnTabPane.java
│   ├── RulesTabPane.java
│   ├── StratTabPane.java
│   └── TestingFormulaePane.java
├── graph/                             # M1-Step 9
│   └── CytoscapeSwingBridge.java
└── styles/                            # M1-Step 1
    └── default.css

src/lotrec/engine/
└── JavaFXEngineListener.java          # M1-Step 11 (new file only)

test/lotrec/guifx/                     # TestFX tests mirror src structure
├── MainFrameFXTest.java
├── ControlsPaneTest.java
├── PremodelSettingsPaneTest.java
├── FormulaTransformerPaneTest.java
├── dialogs/
│   ├── SimpleDialogsTest.java
│   └── ComplexDialogsTest.java
├── logicspane/
│   ├── ConnTabPaneTest.java
│   ├── StratTabPaneTest.java
│   ├── TestingFormulaePaneTest.java
│   └── RulesTabPaneTest.java
├── graph/
│   └── CytoscapeSwingBridgeTest.java
└── validation/
    └── VisualComparisonTool.java

test/lotrec/engine/
└── JavaFXEngineListenerTest.java
```

---

## Risk Management

| Risk | Step | Mitigation | Fallback |
|------|------|------------|----------|
| Java 21 incompatibilities | M0-Step 3 | Full test suite + manual smoke test | Pin specific Java 21 patch version |
| JAXB bulk replacement errors | M0-Step 2 | Compile-check after each directory batch | Keep `javax.xml.bind` compat JAR |
| Cytoscape SwingNode threading | M1-Step 9 | Early prototype in isolation; error boundary | Error placeholder per FR-16 |
| RulesTabPanel complexity | M1-Step 5 | Break into sub-components; allocate extra effort | Partial migration (basic editing first) |
| TestFX headless mode issues | M0.5 | Both headed and headless execution support | MCP screenshot server fallback |
| OpenJFX classpath access issues | M1 setup | `--add-opens` JVM arguments | Test with specific JDK vendor (Liberica) |

---

## Build Commands Reference

| Command | Purpose | When to Use |
|---------|---------|-------------|
| `gradlew.bat build` | Full build (compile + test + jar) | After each milestone |
| `gradlew.bat test` | Run all tests | After each step |
| `gradlew.bat test --tests "*MainFrameFXTest"` | Run specific test | During development |
| `gradlew.bat run` | Launch JavaFX GUI (default) | After M1 build config |
| `gradlew.bat runSwing` | Launch Swing GUI | Regression testing |
| `gradlew.bat jacocoTestReport` | Coverage report | After milestone completion |
| `gradlew.bat clean build` | Clean rebuild | Before milestone verification |

---

## Completion Checklist

- [ ] **Milestone 0**: Java 21 upgrade complete, all tests pass
- [ ] **Milestone 0.5**: Visual validation infrastructure operational
- [ ] **Milestone 1, Steps 1-11**: All components migrated
- [ ] **Final integration**: End-to-end workflow verified
- [ ] **Visual validation**: All 42 states compared and approved
- [ ] **Regression**: Swing GUI still functional via `gradlew.bat runSwing`
- [ ] **Performance**: Startup ≤5s, responsiveness ≤200ms
- [ ] **Constitution compliance**: All constraints verified

---

## Notes

- Research decisions are documented in [research.md](research.md) (R1-R10)
- The Swing GUI removal (FR-23, "Should" priority) is deferred until after full JavaFX validation
- Phase 4 (Cytoscape replacement) is a separate future feature, not part of this plan
- The `gradlew run` → JavaFX / `gradlew runSwing` → Swing split takes effect when M1 build configuration is applied (before M1-Step 1)
- OpenJFX 21 LTS was chosen to match the Java 21 LTS target (clarification session 2026-02-11)

---

*Plan version: 1.0 | Based on FEAT-001 spec and LoTREC Constitution TDD Discipline*
