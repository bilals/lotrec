# JavaFX Migration — Speckit Prompt

> Copy the text below (between the `---` markers) and paste it as the argument to `/speckit.specify` in a new Claude Code conversation.

---

Specify the migration of LoTREC's GUI from Java Swing to JavaFX. This is a multi-step project with the following scope and constraints:

## Step 0: Java 17+ Upgrade (prerequisite)

Upgrade the project from Java 8 to Java 17+.

Key findings from codebase analysis:
- LoTREC core code (src/lotrec/) has NO compatibility issues — no removed APIs, no sun.* imports, no setAccessible, no finalize, no SecurityManager.
- Cytoscape source code (src/cytoscape/) has 87 files using javax.xml.bind.* (JAXB) — removed in Java 11. Fix: add Jakarta JAXB dependency (jakarta.xml.bind:jakarta.xml.bind-api + org.glassfish.jaxb:jaxb-runtime) to build.gradle.kts.
- lib/Cytoscape_lib/ contains old jaxb-api.jar and jaxb-impl.jar that may need replacing.
- lib/Cytoscape_lib/activation.jar may need update to Jakarta Activation.
- Update build.gradle.kts: change sourceCompatibility and targetCompatibility from VERSION_1_8 to VERSION_17.
- Run full build and test suite after upgrade to catch any remaining issues.

## Step 0.5: Visual Validation Infrastructure (before migration begins)

Set up automated GUI screenshot capture and AI-driven visual comparison so that every migrated component can be validated against the existing Swing GUI throughout Step 1. This infrastructure combines Java-side programmatic screenshot capture with a Claude Code skill for visual analysis.

### Why before Step 1

The migration constraint says "each migrated component must be visually equivalent to its Swing counterpart." To enforce this incrementally (not just at the end), the validation tooling must be in place before the first panel is migrated.

### Component A: Automated Swing baseline capture

Create a Gradle task or test class that launches the current Swing GUI, navigates to each key state, and saves screenshots to `build/screenshots/swing-baseline/`. Use `java.awt.Robot` for screen capture. This produces a reproducible, programmatic baseline alongside the 42 manual screenshots already in `.specify/memory/GUI-V2/`.

Key states to capture (matching the existing manual screenshots):
- Main frame with loaded logic (Connectors, Rules, Strategies, Formulas tabs)
- Each dialog (Predefined Logics, Logic Description, New Connector, New Rule, etc.)
- Premodels construction with built tableaux and graph display
- Controls panel states (running, paused, stopped)

### Component B: TestFX dependency for JavaFX screenshot capture

Add TestFX (org.testfx:testfx-core + org.testfx:testfx-junit5) as test dependencies in build.gradle.kts. TestFX can launch JavaFX apps headlessly, interact with controls, and capture screenshots. During Step 1, each migrated JavaFX component gets a corresponding TestFX test that captures its screenshot to `build/screenshots/javafx-current/`.

### Component C: MCP screenshot server for ad-hoc development checks

Install a desktop screenshot MCP server (e.g., windows-screenshot-mcp-server or screenshot_mcp_server) in the project's MCP configuration. This lets Claude Code capture the LoTREC window on demand during interactive development sessions without running the full test suite.

MCP server options (Windows):
- windows-screenshot-mcp-server (Go, targets windows by title/class/PID, multiple formats)
- screenshot_mcp_server by codingthefuturewithai (Python, general screen capture)
- mcp-desktop-automation by tanob (RobotJS, adds mouse/keyboard control)

### Component D: Claude Code visual-check skill

Create a Claude Code skill at `.claude/commands/visual-check.md` that orchestrates the full validation loop:
1. Run the Gradle screenshot task to capture current state (Swing baseline and/or JavaFX current)
2. Read the output PNGs via Claude Code's image reading capability
3. Compare each JavaFX screenshot against the corresponding Swing baseline
4. Report visual differences (layout shifts, missing elements, alignment issues, wrong sizing)

This skill is invoked as `/visual-check` during development to validate migrated components.

### Files to create/modify

| File | Purpose |
|------|---------|
| build.gradle.kts | Add TestFX dependencies, add screenshot capture Gradle task |
| test/lotrec/gui/SwingBaselineCaptureTest.java | Automated Swing screenshot capture |
| .mcp.json (or equivalent) | MCP screenshot server configuration |
| .claude/commands/visual-check.md | Claude Code skill for visual comparison |

## Step 1: JavaFX GUI Migration

### Architecture Decisions (already made)

1. **New package**: Create `src/lotrec/guifx/` as a parallel package to `src/lotrec/gui/`. The old Swing GUI remains runnable until the JavaFX version is complete, then gets deleted.

2. **Graph visualization strategy — "Defer + minimal SwingNode"**: Build all panels, dialogs, and menus in pure JavaFX. For the Premodels Views panel (Cytoscape graph display) and Zoom panel (BirdsEyeView), use a minimal SwingNode wrapper around the existing Cytoscape components. This SwingNode bridge has an explicit timeline for replacement (Phase 4 of the roadmap — see .specify/memory/roadmap.md).

3. **Skip Swing code cleanup**: Do NOT refactor or clean up existing Swing code before migration — it will be discarded. Phase 2 (Code Quality) applies only to backend packages (lotrec.engine, lotrec.process, lotrec.dataStructure, lotrec.parser) and can be done independently later.

4. **Engine integration**: The Engine already communicates via an abstract `EngineListener` interface (src/lotrec/engine/EngineListener.java). Create a `JavaFXEngineListener` (analogous to existing `SwingEngineListener`) that uses `Platform.runLater()` instead of `SwingUtilities.invokeLater()`.

### Existing Documentation and References

- **GUI component documentation**: `.specify/memory/GUI-V2/V2-Swing-GUI-Description.md` — comprehensive description of every panel, dialog, and menu with screenshots and source file mappings.
- **Screenshots**: `.specify/memory/GUI-V2/` — 42 annotated screenshots (numbered 00-40 plus 18.1) covering every GUI state.
- **Roadmap**: `.specify/memory/roadmap.md` — full modernization roadmap with Phase 3 (UI Modernization) covering this migration.
- **Constitution**: `.specify/memory/constitution.md` — project principles and architecture constraints.

### Backend Coupling (verified clean)

| Layer | Swing imports? | Migration impact |
|-------|---------------|-----------------|
| lotrec.engine | Minimal (deprecated MainFrame ref in Engine.java:20, SwingEngineListener wraps MainFrame) | Create JavaFXEngineListener |
| lotrec.dataStructure | None | No changes needed |
| lotrec.parser | None | No changes needed |
| lotrec.process | None | No changes needed |

### Source Files to Create (under src/lotrec/guifx/)

Map each existing Swing source to a new JavaFX equivalent:

| Swing source (reference only, will be deleted later) | JavaFX target |
|------------------------------------------------------|---------------|
| gui/MainFrame.java | guifx/MainFrameFX.java |
| gui/LoadedLogicsPanel.java | guifx/LoadedLogicsPane.java |
| gui/LogicTabComponent.java | guifx/LogicTabComponent.java |
| gui/ControlsPanel.java | guifx/ControlsPane.java |
| gui/TableauxPanel.java | guifx/TableauxPane.java |
| gui/CyTableauDisplayer.java | guifx/graph/CytoscapeSwingBridge.java (SwingNode wrapper) |
| gui/FormulaTransformerGUI.java | guifx/FormulaTransformerDialog.java |
| gui/DialogsFactory.java | guifx/DialogsFactory.java |
| gui/logicspane/LogicDefTab.java | guifx/logicspane/LogicDefTab.java |
| gui/logicspane/ConnTabPanel.java | guifx/logicspane/ConnTabPane.java |
| gui/logicspane/RulesTabPanel.java | guifx/logicspane/RulesTabPane.java |
| gui/logicspane/StratTabPanel.java | guifx/logicspane/StratTabPane.java |
| gui/logicspane/TestingFormulaePanel.java | guifx/logicspane/TestingFormulaePane.java |
| gui/logicspane/LogicInfoPanel.java | guifx/logicspane/LogicInfoPane.java |
| gui/dialogs/OpenPremodelDialog.java | guifx/dialogs/OpenPremodelDialog.java |
| gui/dialogs/SaveAsPremodelDialog.java | guifx/dialogs/SaveAsPremodelDialog.java |
| gui/dialogs/ExportPremodelDialog.java | guifx/dialogs/ExportPremodelDialog.java |
| engine/SwingEngineListener.java | engine/JavaFXEngineListener.java |
| Launcher.java | LauncherFX.java (extends javafx.application.Application) |

### Swing to JavaFX Component Mapping

| Swing | JavaFX equivalent |
|-------|-------------------|
| JFrame | Stage + Scene |
| JPanel | Pane / VBox / HBox / BorderPane |
| JSplitPane | SplitPane |
| JTabbedPane | TabPane |
| JList | ListView |
| JComboBox | ComboBox |
| JTextField | TextField |
| JTextArea | TextArea |
| JButton | Button |
| JCheckBox | CheckBox |
| JRadioButton | RadioButton |
| JTree | TreeView |
| JMenuBar / JMenu / JMenuItem | MenuBar / Menu / MenuItem |
| JDialog (modal) | Stage (initModality) or Dialog |
| JFileChooser | FileChooser |
| JDesktopPane + JInternalFrame | TabPane or custom tiling layout |
| JScrollPane | ScrollPane |
| TitledBorder | TitledPane or CSS-styled Pane |
| SwingUtilities.invokeLater() | Platform.runLater() |

### Migration Order (incremental, simplest first)

1. LauncherFX + MainFrameFX shell (empty window with menu bar and split pane layout)
2. Simple dialogs (Predefined Logics, Logic Description, Satisfiability Check Options, Filter, Run Info)
3. Loaded Logics Panel with Connectors Tab
4. Strategies Tab and Predefined Formulas Tab
5. Rules Tab (most complex panel — ~36K tokens in Swing version)
6. Premodels Construction Settings Panel (formula input + buttons)
7. Controls Panel (step/pause/stop)
8. Premodels List Panel
9. Premodels Views Panel with SwingNode bridge for Cytoscape
10. Complex dialogs (Premodels Editor, Break Points, file choosers)
11. Engine integration (JavaFXEngineListener)

### Constraints

- Each migrated component must be visually equivalent to its Swing counterpart (use screenshots in .specify/memory/GUI-V2/ as reference, and validate via the `/visual-check` skill from Step 0.5).
- The old Swing GUI (src/lotrec/gui/) must remain functional and runnable via the existing Launcher until the JavaFX version is complete.
- Use FXML for layout where appropriate, but plain Java is acceptable for complex dynamic UIs.
- CSS styling should be in external .css files under src/lotrec/guifx/styles/.
- Follow existing project conventions: package naming, code style (see CLAUDE.md).

---
