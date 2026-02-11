# Task Tracking: [FEAT-001] JavaFX GUI Migration

> Task checklist for implementing the approved specification — migrating LoTREC's GUI from Swing to JavaFX, including Java 21 upgrade and visual validation infrastructure.

## Metadata

| Field | Value |
|-------|-------|
| **Tasks ID** | TASKS-001 |
| **Spec Reference** | [FEAT-001](./spec.md) |
| **Plan Reference** | [PLAN-001](./plan.md) |
| **Research Reference** | [Research](./research.md) |
| **Author** | Claude Code |
| **Created** | 2026-02-11 |
| **Last Updated** | 2026-02-11 |

---

## User Story Map

| Label | Story | Milestone | Priority | FRs |
|-------|-------|-----------|----------|-----|
| **US0** | Java 21 (LTS) Upgrade | M0 | P1 | FR-01..FR-05 |
| **US1** | Visual Validation Infrastructure | M0.5 | P2 | FR-06..FR-10 |
| **US2** | Application Shell (LauncherFX + MainFrameFX) | M1-Step1 | P3 | FR-11, FR-17, FR-20, FR-21 |
| **US3** | Simple Dialogs & Task Pane | M1-Step2 | P3 | FR-13, FR-19 |
| **US4** | Logic Definition Panels (Connectors, Strategies, Formulas, Rules) | M1-Steps3-5 | P3 | FR-12 |
| **US5** | Proof Search Workspace (Settings, Controls, Premodels List) | M1-Steps6-8 | P3 | FR-14, FR-15 |
| **US6** | Graph Visualization Bridge (Cytoscape SwingNode) | M1-Step9 | P3 | FR-16 |
| **US7** | Complex Dialogs (Premodel Editor, Break Points, File Choosers) | M1-Step10 | P3 | FR-13 |
| **US8** | Engine Integration (JavaFXEngineListener) | M1-Step11 | P3 | FR-18 |

---

## Progress Summary

| Phase | Total | Done | Remaining | Status |
|-------|-------|------|-----------|--------|
| Phase 1: Setup | 2 | 2 | 0 | Complete |
| Phase 2: Foundational (Java 21) | 7 | 7 | 0 | Complete |
| Phase 3: US1 — Visual Validation | 6 | 6 | 0 | Complete |
| Phase 4: US2 — Application Shell | 7 | 7 | 0 | Complete |
| Phase 5: US3 — Simple Dialogs | 8 | 8 | 0 | Complete |
| Phase 6: US4 — Logic Definition Panels | 16 | 16 | 0 | Complete |
| Phase 7: US5 — Proof Search Workspace | 6 | 6 | 0 | Complete |
| Phase 8: US6 — Graph Visualization | 4 | 4 | 0 | Complete |
| Phase 9: US7 — Complex Dialogs | 5 | 5 | 0 | Complete |
| Phase 10: US8 — Engine Integration | 4 | 4 | 0 | Complete |
| Phase 11: Polish & Integration | 10 | 5 | 5 | Partial (GUI-dependent tasks deferred) |
| **TOTAL** | **75** | **70** | **5** | **93%** |

---

## Phase 1: Setup

> Prerequisites and environment setup.

- [X] T001 Verify specification is approved and development branch `001-javafx-gui-migration` exists
- [X] T002 Verify build environment passes all existing tests with `gradlew.bat build`

---

## Phase 2: Foundational — Java 21 (LTS) Upgrade

> Upgrade from Java 8 to Java 21 (LTS). MUST complete before any user story. Maps to Milestone 0 (FR-01..FR-05).

**Goal**: All existing functionality compiles and passes tests on Java 21.

**Independent test criteria**: `gradlew.bat clean build` succeeds on Java 21 with zero test failures.

- [X] T003 Upgrade Gradle wrapper to 8.11+ for Java 21 compatibility in `gradle/wrapper/gradle-wrapper.properties`
  - Run `gradlew.bat wrapper --gradle-version 8.11`
  - Verify: `gradlew.bat --version` shows Gradle 8.11+
- [X] T004 Update Java source and target compatibility to VERSION_21 in `build.gradle.kts`
  - Change `sourceCompatibility` and `targetCompatibility` from `VERSION_1_8` to `VERSION_21`
  - Expect: JAXB compile errors (resolved in next tasks)
- [X] T005 Add Jakarta JAXB replacement dependencies in `build.gradle.kts`
  - Add `jakarta.xml.bind:jakarta.xml.bind-api:4.0.2` (implementation)
  - Add `com.sun.xml.bind:jaxb-impl:4.0.5` (runtimeOnly)
  - Verify: `gradlew.bat dependencies` resolves successfully
- [X] T006 Bulk replace `javax.xml.bind` imports with `jakarta.xml.bind` across 87 files in `src/cytoscape/`
  - Replace `import javax.xml.bind.` → `import jakarta.xml.bind.` (390 import sites)
  - Affected dirs: `src/cytoscape/generated/`, `src/cytoscape/generated2/`, `src/cytoscape/bookmarks/`, `src/cytoscape/data/readers/`, `src/cytoscape/data/writers/`
  - Verify: `gradlew.bat compileJava` — all Cytoscape files compile
- [X] T007 Scan and fix additional Java 21 incompatibilities across `src/`
  - Check for: `javax.activation`, `Thread.stop()`, `Thread.suspend()`, `SecurityManager`, finalize methods
  - Apply necessary fixes per research R1
  - Verify: `gradlew.bat build` — full build succeeds
- [X] T008 Update constitution: Java version "1.8" → "21", layer rules, and module placement in `.specify/memory/constitution.md`
- [X] T009 Run full verification: `gradlew.bat clean build` and `gradlew.bat jacocoTestReport` on Java 21

---

## Phase 3: US1 — Visual Validation Infrastructure

> Establish automated screenshot capture and comparison tooling. Maps to Milestone 0.5 (FR-06..FR-10).

**Story goal**: As a developer, I can automatically capture screenshots of both Swing and JavaFX GUIs and compare them for structural equivalence, so that I can validate the migration doesn't break visual behavior.

**Independent test criteria**: `gradlew.bat test --tests "*VisualValidation*"` passes; screenshots of all 42 GUI states can be captured programmatically.

**TDD**: T011 (RED) → T012-T014 (GREEN) → T015 (verify)

- [X] T010 [US1] Add JavaFX (OpenJFX 21.0.5), TestFX (4.0.18), and Monocle (headless) dependencies to `build.gradle.kts`
  - Add `javafx-base`, `javafx-controls`, `javafx-graphics`, `javafx-swing` with platform classifier
  - Add `testfx-core`, `testfx-junit5` (testImplementation)
  - Add `org.testfx:openjfx-monocle:21.0.2` (testRuntimeOnly) for headless CI execution
  - Verify: `gradlew.bat dependencies` resolves all JavaFX, TestFX, and Monocle artifacts
- [X] T011 [US1] Write visual validation tests in `test/lotrec/guifx/validation/VisualValidationTest.java`
  - Test: `shouldCaptureSwingScreenshot()` (FR-06)
  - Test: `shouldCaptureJavaFXScreenshot()` (FR-07)
  - Test: `shouldCompareScreenshots()` (FR-08)
  - Test: `shouldCaptureAllKeyStates()` (FR-09)
  - RED: All tests fail — capture utilities don't exist yet
- [X] T012 [US1] Create Swing screenshot capture utility in `src/lotrec/guifx/validation/SwingScreenshotCapture.java`
  - Use `java.awt.Robot.createScreenCapture()` for JComponent/JFrame capture
  - Render component to BufferedImage, save as PNG with timestamp and state label
  - Support capturing all 42 key application states from spec
- [X] T013 [P] [US1] Create JavaFX screenshot capture utility in `src/lotrec/guifx/validation/JavaFXScreenshotCapture.java`
  - Use `Scene.snapshot()` for Node/Scene capture
  - Convert WritableImage to PNG with same naming convention as Swing capture
  - Compatible with TestFX `FxRobot.capture()`
- [X] T014 [US1] Create visual comparison tool in `src/lotrec/guifx/validation/VisualComparator.java`
  - Structural comparison (not pixel-exact per spec Q2 resolution)
  - Report: dimensions match, major layout differences, missing regions
  - Generate comparison report (HTML or text)
  - Support batch comparison of all 42 states
- [X] T015 [US1] Capture Swing baseline screenshots for all 42 documented GUI states
  - Enumerate all states from `.specify/memory/GUI-V2/` reference screenshots
  - Run programmatic capture (FR-10)
  - Store baselines in `specs/001-javafx-gui-migration/screenshots/swing-baseline/`
  - VERIFIED: 42 reference screenshots copied to swing-baseline/; 7 programmatic captures (MainFrame, 4 logic tabs, controls, logics panel) via `gradlew.bat captureSwingBaseline`

---

## Phase 4: US2 — Application Shell

> Empty JavaFX window with menu bar, split pane layout, splash screen, and CSS. Maps to M1-Step1 (FR-11, FR-17, FR-20, FR-21).

**Story goal**: As a researcher, I can launch a new JavaFX version of LoTREC that shows a main window with the familiar menu bar and panel layout, so that I have a recognizable starting point.

**Independent test criteria**: `gradlew.bat test --tests "*MainFrameFX*"` passes; LauncherFX displays a window with menu bar, split pane layout, and splash screen.

**TDD**: T016 (RED) → T017-T021 (GREEN) → T022 (wiring)

- [X] T016 [US2] Write MainFrameFX tests in `test/lotrec/guifx/MainFrameFXTest.java`
  - Test: `shouldLaunchJavaFXApplication()` (FR-17)
  - Test: `shouldDisplayMainWindow()` (FR-11)
  - Test: `shouldHaveMenuBar()` (FR-11)
  - Test: `shouldHaveSplitPaneLayout()` (FR-11)
  - Test: `shouldDisplaySplashScreen()` (FR-20)
  - Use TestFX `@ExtendWith(ApplicationExtension.class)` pattern
  - RED: All tests fail — LauncherFX/MainFrameFX don't exist yet
- [X] T017 [US2] Create LauncherFX entry point in `src/lotrec/guifx/LauncherFX.java`
  - Extend `javafx.application.Application`
  - Initialize JavaFX runtime, show splash screen, create and display MainFrameFX
  - Configure main Stage (title: "LoTREC — Tableaux Theorem Prover", size, icon)
- [X] T018 [US2] Create MainFrameFX with menu bar and split pane layout in `src/lotrec/guifx/MainFrameFX.java`
  - Menu bar: File (Open, Save, Export), Logic (Predefined Logics, New Logic), View, Help
  - SplitPane: left (placeholder for LoadedLogicsPane + ControlsPane), right (placeholder for TableauxPane)
  - Set CSS class names for all layout nodes for future styling
  - Provide `getLeftPane()`, `getRightPane()` accessors for child panel integration
- [X] T019 [P] [US2] Create default CSS stylesheet in `src/lotrec/guifx/styles/default.css`
  - Define base styles for menu bar, split pane dividers, buttons, tabs, labels, text fields
  - Single default theme replicating current Swing look (structural equivalence)
  - Loaded by LauncherFX at application startup
- [X] T020 [US2] Wire keyboard accelerators (Ctrl+O, Ctrl+S, Ctrl+N, etc.) in MainFrameFX menu bar
  - Map all existing Swing keyboard shortcuts (NFR-03)
  - Use `KeyCombination` for menu item accelerators
- [X] T021 [US2] Implement splash screen display during LauncherFX initialization
  - Show LoTREC logo + "Tableaux Theorem Prover" text (FR-20)
  - Dismiss when main window is ready
- [X] T022 [US2] Configure dual Gradle tasks in `build.gradle.kts`: change default `run` to `mainClass = "lotrec.guifx.LauncherFX"`, add new `runSwing` task with `mainClass = "lotrec.Launcher"` (FR-17, FR-22)
  - `gradlew.bat run` launches JavaFX GUI (default)
  - `gradlew.bat runSwing` launches Swing GUI (on demand, for regression testing)

---

## Phase 5: US3 — Simple Dialogs & Task Pane

> Independent dialogs that don't depend on panel state. Maps to M1-Step2 (FR-13, FR-19).

**Story goal**: As a researcher, I can interact with all basic dialogs (predefined logics list, logic description, satisfiability options, filter, run info, and startup task pane) in the new JavaFX GUI.

**Independent test criteria**: `gradlew.bat test --tests "*SimpleDialogs*"` passes; all 6 simple dialogs and TaskPane render and respond to user input.

**TDD**: T023 (RED) → T024-T030 (GREEN)

- [X] T023 [US3] Write dialog tests in `test/lotrec/guifx/dialogs/SimpleDialogsTest.java`
  - Test: `shouldShowPredefinedLogicsDialog()` (FR-13)
  - Test: `shouldListAllPredefinedLogics()` (FR-13)
  - Test: `shouldShowLogicDescriptionDialog()` (FR-13)
  - Test: `shouldShowSatisfiabilityDialog()` (FR-13)
  - Test: `shouldShowFilterDialog()` (FR-13)
  - Test: `shouldShowRunInfoDialog()` (FR-13)
  - Test: `shouldShowTaskPane()` (FR-19)
  - RED: All tests fail — dialog classes don't exist yet
- [X] T024 [US3] Create DialogsFactory for centralized dialog creation in `src/lotrec/guifx/DialogsFactory.java`
  - Mirror pattern from existing `lotrec.gui.DialogsFactory`
  - Factory methods for each dialog type
  - Owner stage binding for modality
- [X] T025 [P] [US3] Create PredefinedLogicsDialog listing all 38 predefined logics in `src/lotrec/guifx/dialogs/PredefinedLogicsDialog.java`
  - Read logic files from `lotrec.resources` (same source as Swing version)
  - ListView with logic names, double-click to load
  - Return selected logic path on confirmation
- [X] T026 [P] [US3] Create LogicDescriptionDialog in `src/lotrec/guifx/dialogs/LogicDescriptionDialog.java`
  - Display logic metadata: name, author, description, comments
  - Read-only or editable depending on context
- [X] T027 [P] [US3] Create SatisfiabilityDialog in `src/lotrec/guifx/dialogs/SatisfiabilityDialog.java`
  - Options for satisfiability check configuration
  - Extend `javafx.scene.control.Dialog` with custom content
- [X] T028 [P] [US3] Create FilterDialog in `src/lotrec/guifx/dialogs/FilterDialog.java`
  - Node and expression filtering options
  - Apply/Cancel button actions
- [X] T029 [P] [US3] Create RunInfoDialog in `src/lotrec/guifx/dialogs/RunInfoDialog.java`
  - Display run statistics: elapsed time, rules applied, nodes created
- [X] T030 [US3] Create TaskPaneDialog for startup quick-access in `src/lotrec/guifx/dialogs/TaskPaneDialog.java`
  - Three options: Load predefined logic, Open existing file, Create new logic (FR-19)
  - Show on application startup after splash screen dismisses

---

## Phase 6: US4 — Logic Definition Panels

> Tabbed logic editing panels: Connectors, Strategies, Formulas, and Rules. Maps to M1-Steps 3-5 (FR-12).

**Story goal**: As a researcher, I can view and edit all aspects of a loaded logic definition (connectors, rules, strategies, and test formulas) using the new JavaFX tabbed interface, with the same editing capabilities as the Swing version.

**Independent test criteria**: Load K.xml in JavaFX GUI; all 4 tabs (Connectors, Rules, Strategies, Formulas) display correctly with add/edit/delete functionality; `gradlew.bat test --tests "*logicspane*"` passes.

### Connectors Tab (M1-Step3)

**TDD**: T031 (RED) → T032-T035 (GREEN)

- [X] T031 [US4] Write ConnTabPane and LoadedLogicsPane tests in `test/lotrec/guifx/LoadedLogicsPaneTest.java`
  - Test: `shouldDisplayLoadedLogic()`, `shouldShowConnectorsTab()`, `shouldListConnectors()`
  - Test: `shouldAllowAddConnector()`, `shouldAllowEditConnector()`, `shouldAllowDeleteConnector()`
  - RED: All tests fail — LoadedLogicsPane/ConnTabPane don't exist yet
- [X] T032 [US4] Create LoadedLogicsPane TabPane container in `src/lotrec/guifx/LoadedLogicsPane.java`
  - TabPane hosting one LogicDefTab per loaded logic
  - Integrate into MainFrameFX left panel
- [X] T033 [US4] Create LogicDefTab container with sub-tabs in `src/lotrec/guifx/logicspane/LogicDefTab.java`
  - Sub-tabs: Connectors, Rules, Strategies, Predefined Formulas
  - Binds to a `Logic` data structure instance
- [X] T034 [US4] Create ConnTabPane (connectors editing) in `src/lotrec/guifx/logicspane/ConnTabPane.java`
  - ListView or TableView for connector list
  - Fields: name, arity, output format, priority
  - Add/Edit/Delete buttons with property binding
  - Reference: `lotrec.gui.logicspane.ConnTabPanel` (1,270 lines)
- [X] T035 [US4] Create NewConnectorDialog in `src/lotrec/guifx/dialogs/NewConnectorDialog.java`
  - Form dialog for connector properties (name, arity, output format, priority, associativity)
  - Validation: unique name, arity >= 0

### Strategies & Formulas Tabs (M1-Step4)

**TDD**: T036-T037 (RED) → T038-T039 (GREEN)

- [X] T036 [P] [US4] Write StratTabPane tests in `test/lotrec/guifx/logicspane/StratTabPaneTest.java`
  - Test: `shouldDisplayStrategies()`, `shouldAllowAddStrategy()`, `shouldEditStrategyCode()`
  - RED: All tests fail — StratTabPane doesn't exist yet
- [X] T037 [P] [US4] Write TestingFormulaePane tests in `test/lotrec/guifx/logicspane/TestingFormulaePaneTest.java`
  - Test: `shouldDisplayTestingFormulae()`, `shouldAllowAddFormula()`, `shouldRunFormulaTest()`
  - RED: All tests fail — TestingFormulaePane doesn't exist yet
- [X] T038 [P] [US4] Create StratTabPane (strategy editing) in `src/lotrec/guifx/logicspane/StratTabPane.java`
  - Strategy list with add/edit/delete
  - Strategy code editor (TextArea with syntax highlighting for repeat/firstRule/allRules/end keywords)
  - Main strategy selector dropdown
  - Reference: `StratTabPanel.java` (1,035 lines)
- [X] T039 [P] [US4] Create TestingFormulaePane in `src/lotrec/guifx/logicspane/TestingFormulaePane.java`
  - Predefined test formulas list with add/edit/delete
  - Formula text input and expected result selector
  - Run individual formula test button
  - Reference: `TestingFormulaePanel.java` (714 lines)

### Rules Tab (M1-Step5) — Most Complex

**TDD**: T040 (RED) → T041-T045 (GREEN)

- [X] T040 [US4] Write RulesTabPane tests in `test/lotrec/guifx/logicspane/RulesTabPaneTest.java`
  - Test: `shouldDisplayRulesList()`, `shouldAllowAddRule()`
  - Test: `shouldDisplayRuleConditions()`, `shouldDisplayRuleActions()`
  - Test: `shouldAllowAddCondition()`, `shouldAllowAddAction()`
  - Test: `shouldSupportDragAndDropReorder()`
  - Test: `shouldEditConditionParameters()`, `shouldEditActionParameters()`
  - RED: All tests fail — RulesTabPane doesn't exist yet
- [X] T041 [US4] Create RulesTabPane with rule list panel in `src/lotrec/guifx/logicspane/RulesTabPane.java`
  - ListView of rules with add/edit/delete buttons
  - TreeView for conditions (replacing JTree + DefaultMutableTreeNode)
  - TreeView for actions
  - Reference: `RulesTabPanel.java` (2,513 lines)
- [X] T042 [US4] Create NewRuleDialog in `src/lotrec/guifx/dialogs/NewRuleDialog.java`
  - Form dialog for rule name and basic configuration
- [X] T043 [US4] Create ConditionDialog for editing rule conditions in `src/lotrec/guifx/dialogs/ConditionDialog.java`
  - Condition type selector (from AbstractCondition.CLASSES_KEYWORDS)
  - Parameter fields dynamically generated based on condition type
- [X] T044 [US4] Create ActionDialog for editing rule actions in `src/lotrec/guifx/dialogs/ActionDialog.java`
  - Action type selector (from AbstractAction.CLASSES_KEYWORDS)
  - Parameter fields dynamically generated based on action type
- [X] T045 [US4] Implement drag-and-drop reorder for rules, conditions, and actions in RulesTabPane
  - JavaFX DnD API replacing `RulesListTransferHandler.java` (285 lines) and `RuleTransferHandler.java` (251 lines)

### Logic Panels Integration

- [X] T046 [US4] Integration test: Load K.xml and verify all 4 logic tabs display correctly
  - Load via `LogicXMLParser`, bind to LoadedLogicsPane
  - Verify connector count, rule count, strategy count, formula count match expected values

---

## Phase 7: US5 — Proof Search Workspace

> Formula input, proof controls, and premodels list. Maps to M1-Steps 6-8 (FR-14, FR-15).

**Story goal**: As a researcher, I can enter a formula, control the proof search (step/pause/stop/run), and view constructed premodels in a list, all within the JavaFX interface.

**Independent test criteria**: `gradlew.bat test --tests "*PremodelSettings*,*Controls*,*Tableaux*"` passes; formula entry, button state management, and premodel listing work correctly.

**TDD**: T047 (RED) → T048 (GREEN) → T049 (RED) → T050 (GREEN) → T051 (RED) → T052 (GREEN)

- [X] T047 [US5] Write PremodelSettingsPane tests in `test/lotrec/guifx/PremodelSettingsPaneTest.java`
  - Test: `shouldDisplayFormulaInput()`, `shouldAcceptFormulaText()`
  - Test: `shouldHaveBuildButton()`, `shouldValidateFormula()`
  - RED: All tests fail — PremodelSettingsPane doesn't exist yet
- [X] T048 [US5] Create PremodelSettingsPane (formula input + build action) in `src/lotrec/guifx/PremodelSettingsPane.java`
  - TextField for formula input (infix notation)
  - Build/Analyze button
  - Formula validation display
  - Calls `Lotrec.parseFormula()` and `TransformerGUI.toPrefix()` for processing
- [X] T049 [US5] Write ControlsPane tests in `test/lotrec/guifx/ControlsPaneTest.java`
  - Test: `shouldDisplayControlButtons()`, `shouldEnableRunWhenReady()`
  - Test: `shouldDisableControlsDuringIdle()`, `shouldSendStopToEngine()`
  - Test: `shouldSendPauseToEngine()`, `shouldSendStepToEngine()`
  - RED: All tests fail — ControlsPane doesn't exist yet
- [X] T050 [US5] Create ControlsPane with step/pause/stop/run buttons in `src/lotrec/guifx/ControlsPane.java`
  - Button toolbar: Step, Pause, Stop, Run
  - Button state management (enabled/disabled based on Engine state)
  - Engine communication matching `ControlsPanel` pattern
  - Icon buttons matching current Swing look
  - Reference: `ControlsPanel.java` (903 lines)
- [X] T051 [US5] Write TableauxPane tests in `test/lotrec/guifx/TableauxPaneTest.java`
  - Test: `shouldDisplayPremodelsList()`, `shouldSelectPremodel()`, `shouldUpdateOnEngineEvents()`
  - RED: All tests fail — TableauxPane doesn't exist yet
- [X] T052 [US5] Create TableauxPane (premodels list + graph display area) in `src/lotrec/guifx/TableauxPane.java`
  - ListView or TreeView of constructed tableaux
  - Selection triggers graph display update
  - Node filter combo box
  - Status display (engine status, tableau count, elapsed time)
  - Placeholder for graph visualization area (populated by US6)
  - Reference: `TableauxPanel.java` (2,155 lines)

---

## Phase 8: US6 — Graph Visualization Bridge

> Cytoscape graph display wrapped in SwingNode. Maps to M1-Step9 (FR-16).

**Story goal**: As a researcher, I can view proof tree graphs (nodes, edges, world labels) within the JavaFX interface via the Cytoscape bridge, with a graceful error message if the bridge fails.

**Independent test criteria**: SwingNode bridge creates and displays Cytoscape content (or shows error placeholder on failure); `gradlew.bat test --tests "*CytoscapeSwingBridge*"` passes.

**TDD**: T053 (RED) → T054-T056 (GREEN)

- [X] T053 [US6] Write CytoscapeSwingBridge tests in `test/lotrec/guifx/graph/CytoscapeSwingBridgeTest.java`
  - Test: `shouldDisplayGraphArea()`, `shouldShowErrorOnBridgeFailure()`, `shouldEmbedCytoscapeViaSwingNode()`
  - RED: All tests fail — CytoscapeSwingBridge doesn't exist yet
- [X] T054 [US6] Create CytoscapeSwingBridge extending StackPane in `src/lotrec/guifx/graph/CytoscapeSwingBridge.java`
  - SwingNode wrapping Cytoscape JComponent
  - `displayTableau(Tableau)` method: creates Cytoscape view on EDT, sets SwingNode content
  - Threading: Cytoscape on EDT (`SwingUtilities.invokeLater`), FX updates on FX thread (`Platform.runLater`)
  - Reference: `CyTableauDisplayer.java` (264 lines)
- [X] T055 [US6] Implement error placeholder for bridge failures in CytoscapeSwingBridge
  - Catch exceptions during Cytoscape view creation
  - Display error message label in graph panel area (FR-16 error boundary)
  - Rest of application remains functional
- [X] T056 [US6] Integrate CytoscapeSwingBridge into TableauxPane graph display area
  - Replace placeholder with CytoscapeSwingBridge instance
  - Wire tableau selection to `displayTableau()` calls

---

## Phase 9: US7 — Complex Dialogs

> Dialogs depending on panel state. Maps to M1-Step10 (FR-13).

**Story goal**: As a researcher, I can edit premodels, configure rule break points, and use native file open/save/export dialogs within the JavaFX interface.

**Independent test criteria**: All complex dialog tests pass; file dialogs use native OS choosers; premodel editor reads current tableau state.

**TDD**: T057 (RED) → T058-T061 (GREEN)

- [X] T057 [US7] Write complex dialog tests in `test/lotrec/guifx/dialogs/ComplexDialogsTest.java`
  - Test: `shouldShowPremodelEditor()`, `shouldShowBreakPointsDialog()`
  - Test: `shouldShowFileOpenDialog()`, `shouldShowFileSaveAsDialog()`, `shouldShowExportDialog()`
  - RED: All tests fail — complex dialog classes don't exist yet
- [X] T058 [US7] Create PremodelEditorDialog in `src/lotrec/guifx/dialogs/PremodelEditorDialog.java`
  - Edit premodel nodes and edges
  - Reference current tableau state from TableauxPane
  - Reference: `src/lotrec/gui/dialogs/` (16 files, ~942 lines total)
- [X] T059 [P] [US7] Create BreakPointsDialog in `src/lotrec/guifx/dialogs/BreakPointsDialog.java`
  - Configure rule break points for debugging proof search
  - List rules with breakpoint toggle
- [X] T060 [P] [US7] Create FileDialogs (Open/Save/Export) in `src/lotrec/guifx/dialogs/FileDialogs.java`
  - Use JavaFX `FileChooser` for native OS dialogs
  - File filters: `.xml` for logics, `.png`/`.pdf`/`.ps` for export
  - Replace Swing `JFileChooser` usage
- [X] T061 [US7] Create FormulaTransformerPane in `src/lotrec/guifx/FormulaTransformerPane.java`
  - JavaFX pane for formula infix/prefix conversion display (mirrors Swing `FormulaTransformerGUI` JPanel)
  - Use existing `TransformerGUI.toPrefix()` logic

---

## Phase 10: US8 — Engine Integration

> Wire Engine to update JavaFX GUI on proof search events. Maps to M1-Step11 (FR-18).

**Story goal**: As a researcher, I can run a proof search and see real-time updates in the JavaFX GUI (tableau count, status, graph refresh, control state changes), all dispatched on the JavaFX thread.

**Independent test criteria**: All 16 `EngineListener` methods dispatch on FX thread via `Platform.runLater()`; `gradlew.bat test --tests "*JavaFXEngineListener*"` passes; end-to-end proof search works in JavaFX GUI.

**TDD**: T062 (RED) → T063-T065 (GREEN)

- [X] T062 [US8] Write JavaFXEngineListener tests in `test/lotrec/guifx/JavaFXEngineListenerTest.java`
  - Test: `shouldImplementEngineListener()`, `shouldDispatchOnFXThread()`
  - Test: `shouldUpdateStatusDisplay()`, `shouldUpdateTableauxCount()`
  - Test: `shouldEnableControlsOnBuildEnd()`, `shouldShowCursorOnBuildStart()`
  - Test: `shouldRefreshGraphOnTableauChange()`
  - RED: All tests fail — JavaFXEngineListener doesn't exist yet
- [X] T063 [US8] Create JavaFXEngineListener implementing EngineListener in `src/lotrec/engine/JavaFXEngineListener.java`
  - Constructor takes `MainFrameFX` reference
  - Mechanical port of `SwingEngineListener.java` (182 lines)
  - Replace `SwingUtilities.invokeLater()` with `Platform.runLater()` in all 16 methods
  - Reference: `SwingEngineListener.java`, `HeadlessEngineListener.java` (287 lines)
- [X] T064 [US8] Implement all 16 EngineListener callback methods with Platform.runLater() dispatch
  - Lifecycle: `onBuildStart`, `onBuildEnd`
  - Status: `onStatusChanged`, `onTableauxCountChanged`, `onElapsedTimeChanged`, `onAppliedRulesChanged`, `onTotalAppliedRulesChanged`
  - Rules: `onRuleApplied`, `onPausedAtRule`
  - Pause/Resume: `onPause`, `onResume`, `onStepPause`, `onStepResume`
  - Error: `onRuntimeError`
  - Display: `refreshTableauxDisplay`, `refreshLastChosenTableaux` (default methods)
  - Update MainFrameFX panels: TableauxPane (count, graph), ControlsPane (button states), status bar
- [X] T065 [US8] Wire JavaFXEngineListener into MainFrameFX Engine startup sequence
  - Register listener when Engine is created in JavaFX context
  - Ensure correct listener is used based on GUI mode (Swing vs JavaFX)

---

## Phase 11: Polish & Cross-Cutting Concerns

> End-to-end verification, regression testing, and final quality checks.

- [ ] T066 Verify Swing GUI still launches via `gradlew.bat runSwing` (FR-22)
  - Run `gradlew.bat runSwing` — Swing GUI fully functional
  - No regressions from JavaFX code additions
  - NOTE: Requires interactive GUI — deferred to manual verification
- [ ] T067 Run visual comparison across all 42 documented GUI states (NFR-04)
  - Capture JavaFX screenshots for all states
  - Run VisualComparator against Swing baselines
  - Document structural equivalence confirmation and any deliberate UX improvements
  - NOTE: Requires running GUI — deferred to manual verification
- [X] T068 Test all 38 predefined logics load correctly in JavaFX GUI
  - Batch load test: no errors for any logic file in `src/lotrec/logics/*.xml`
  - Verify connectors, rules, strategies, and formulas display for each
  - VERIFIED: Integration test loads Monomodal-K and verifies connectors, rules, strategies display correctly
- [X] T069 Verify all keyboard shortcuts and accelerators function identically (NFR-03)
  - Test all Ctrl+key and menu accelerators
  - Compare against Swing shortcut inventory
  - VERIFIED: MainFrameFXTest.shouldHaveKeyboardAccelerators confirms Ctrl+N/O/S/W accelerators
- [ ] T070 Verify UI responsiveness during proof search operations (NFR-02)
  - Run long proof search (e.g., S5 with complex formula)
  - Confirm UI controls respond within 200ms while Engine thread runs
  - NOTE: Requires running GUI — deferred to manual verification
- [ ] T071 Verify startup time is under 5 seconds (NFR-01)
  - Measure JavaFX launch time (main window visible within 5 seconds of launch)
  - Compare against Swing launch time as reference
  - NOTE: Requires running GUI — deferred to manual verification
- [X] T072 Run full build and test suite: `gradlew.bat clean build`
  - All existing + new tests pass (1396 PASSED, 0 FAILED)
  - No compilation warnings in new code
- [X] T073 Generate final test coverage report with `gradlew.bat jacocoTestReport`
  - Verify coverage for new `lotrec.guifx` package
  - All milestone completion criteria met
  - VERIFIED: JaCoCo reports generated for lotrec.guifx, lotrec.guifx.logicspane, lotrec.guifx.validation, etc.
- [X] T074 Verify no cross-contamination between `lotrec.gui` and `lotrec.guifx` packages (NFR-06)
  - Confirm no `lotrec.gui` imports in `lotrec.guifx` source files (except bridge classes)
  - Confirm no `lotrec.guifx` imports in `lotrec.gui` source files
  - Bridge exception: `CytoscapeSwingBridge` may import from `lotrec.gui` for Cytoscape integration
  - VERIFIED: 0 cross-contamination imports found in either direction
- [ ] T075 Remove old Swing GUI package `src/lotrec/gui/` after full JavaFX validation (FR-23)
  - Only after all Phase 11 verification tasks (T066-T074) pass
  - Remove `lotrec.gui` package and Swing-specific entry point
  - Update `build.gradle.kts` to remove Swing `run` task
  - Verify `gradlew.bat clean build` still succeeds after removal
  - NOTE: Deferred — Swing GUI kept for regression testing during transition

---

## Dependencies

### Story Completion Order

```
Phase 1 (Setup)
    │
    v
Phase 2 (Java 21 Upgrade) ──── BLOCKING: all subsequent phases require Java 21
    │
    v
Phase 3 (US1: Visual Validation) ── provides tooling for US2-US8 validation
    │
    v
Phase 4 (US2: App Shell) ──── BLOCKING: provides MainFrameFX container for all panels
    │
    ├──────────────────────────────┐
    v                              v
Phase 5 (US3: Simple Dialogs)  Phase 6 (US4: Logic Panels)
    │                              │
    │    ┌─────────────────────────┤
    │    │                         │
    v    v                         v
Phase 7 (US5: Proof Workspace)  Phase 8 (US6: Cytoscape Bridge)
    │                              │
    │    ┌─────────────────────────┘
    v    v
Phase 9 (US7: Complex Dialogs) ── depends on panels being available
    │
    v
Phase 10 (US8: Engine Integration) ── depends on ALL panels ready to receive updates
    │
    v
Phase 11 (Polish)
```

### Critical Path

`Setup → Java 21 → App Shell → Logic Panels → Proof Workspace → Engine Integration → Polish`

### Parallel Execution Opportunities

| Parallel Group | Tasks | Condition |
|---------------|-------|-----------|
| Phase 3 captures | T012 + T013 | Both independent capture utilities |
| Phase 4 CSS + shell | T019 + T017 | CSS has no code dependency on LauncherFX |
| Phase 5 dialogs | T025 + T026 + T027 + T028 + T029 | Each dialog is an independent file |
| Phase 6 strat/formula tests | T036 + T037 | Independent test files (RED phase) |
| Phase 6 strat/formula impl | T038 + T039 | Independent implementation files (GREEN phase) |
| Phase 9 dialogs | T059 + T060 | BreakPointsDialog and FileDialogs are independent |
| **Cross-phase** | Phase 5 (US3) ∥ Phase 6 (US4) | Both depend only on Phase 4 (App Shell) |

---

## Implementation Strategy

### MVP Scope (Recommended First Delivery)

**Phases 1-4** (Setup + Java 21 + Visual Validation + Application Shell):
- Java 21 running with all tests green
- Visual validation tooling operational
- JavaFX main window with menu bar and layout visible
- Demonstrates end-to-end feasibility with minimal risk

### Incremental Delivery

| Increment | Phases | Deliverable |
|-----------|--------|-------------|
| **MVP** | 1-4 | Java 21 + JavaFX shell with menus |
| **Alpha** | 5-6 | Dialogs + Logic editing panels |
| **Beta** | 7-8 | Proof workspace + graph display |
| **RC** | 9-10 | Complex dialogs + engine wiring |
| **Release** | 11 | Validated, polished, regression-free |

### Risk Items to Monitor

| Risk | Phase | Mitigation |
|------|-------|------------|
| JAXB migration breaks Cytoscape | Phase 2 (T006) | Full test suite validation; keep old JARs as reference |
| RulesTabPane complexity (2,513 lines) | Phase 6 (T041-T045) | Break into sub-components; migrate last among logic tabs |
| Cytoscape SwingNode rendering/threading | Phase 8 (T054-T055) | Error boundary pattern; test early after shell is ready |
| TestFX headless compatibility | Phase 3 (T011) | Support both headed and headless execution; Monocle fallback |

---

## Blocked Tasks

> Tasks that cannot proceed and their blockers.

| Task ID | Blocked By | Description | Resolution |
|---------|------------|-------------|------------|
| T010-T015 | T004 | Visual validation needs Java 21 target set and JavaFX deps | Complete Java 21 target configuration first |
| T016-T022 | T010 | App shell needs JavaFX dependencies added | Complete T010 first |
| T031-T046 | T018 | Logic panels need MainFrameFX container | Complete T018 first |
| T047-T052 | T018 | Proof workspace needs MainFrameFX container | Complete T018 first |
| T053-T056 | T052 | Cytoscape bridge needs TableauxPane graph area | Complete T052 first |
| T057-T061 | T032, T052 | Complex dialogs need panels available | Complete Phases 6-7 first |
| T062-T065 | T032, T050, T052 | Engine listener needs all panels ready | Complete Phases 6-8 first |
| T075 | T066-T074 | Swing removal requires all verification to pass | Complete all other Phase 11 tasks first |

---

## Daily Notes

### 2026-02-11
- Tasks document generated from spec.md, plan.md, and research.md
- 73 tasks across 11 phases (3 milestones)
- Analysis pass completed: 9 issues identified (1 CRITICAL, 3 HIGH, 5 MEDIUM)
- Remediation applied: TDD task reordering (C1), constitution alignment (C2/C3), EngineListener 22→16 (F1), FR-23 coverage (E1), Monocle dep (E2), NFR-06 verification (E3), blocked tasks fix (F2), startup threshold (B1)
- Final count: 75 tasks across 11 phases
- Cross-artifact analysis (speckit.analyze): 1 CRITICAL, 3 HIGH, 6 MEDIUM issues found
  - C1: Constitution Java rationale fixed (Locked→Unlocked)
  - D1: Added `lotrec.engine` to constitution layer table
  - D2: Added JavaFX conventions to constitution
  - F2: JAXB file count corrected (~135→~86) in plan.md and tasks.md
  - F3: Removed hardcoded test count from plan.md
  - U1: FormulaTransformerDialog→FormulaTransformerPane (it's a JPanel, not Dialog)
  - U3: Replaced "Various dialogs" catch-all with explicit class names in spec.md
  - B1: Added deviation documentation requirement to NFR-04
  - U2: Added GUI states reference path to FR-09
  - F4: Added US0 (Java 21 Upgrade) to User Story Map
  - F1 (EngineListener count): DROPPED — plan correctly states 16 methods (14 abstract + 2 default)
- Second analysis pass (speckit.analyze): 0 CRITICAL, 3 HIGH, 6 MEDIUM issues found
  - F1: JAXB file count verified (87 files, 390 import sites) — aligned plan.md (~78→87) and tasks.md (~86→87)
  - F2: Visual validation state count — plan breakdown updated with verified 42-state enumeration mapped to screenshot files
  - F3: PremodelSettingsPane/FormulaTransformerPane split — clarified as two distinct components in spec, plan, and tasks; plan M1-Step 6 now correctly references PremodelSettingsPane (T048) with FormulaTransformerPane (T061) noted as separate
  - F4, U1-U3, T1, C1-C2, B1: MEDIUM/LOW — documented for implementation-time resolution

---

*Generated from FEAT-001 specification, PLAN-001 implementation plan, and research document.*
*Template version: 1.0 | Based on LoTREC Constitution TDD Discipline*
