# Specification: [FEAT-001] JavaFX GUI Migration

> Migrate LoTREC's graphical user interface from Java Swing to JavaFX, including the prerequisite Java 21 upgrade and visual validation infrastructure.

## Metadata

| Field | Value |
|-------|-------|
| **Spec ID** | FEAT-001 |
| **Author** | Claude Code |
| **Created** | 2026-02-11 |
| **Status** | Draft |
| **Priority** | High |
| **Estimated Complexity** | Large |

---

## 1. Summary

LoTREC's current GUI is built on Java Swing, a UI toolkit that has received no significant updates since 2011 and lacks modern styling, layout, and developer experience capabilities. This feature migrates the entire LoTREC user interface to JavaFX, delivering a modernized, maintainable, and visually consistent application while preserving all existing functionality. The migration includes the prerequisite upgrade from Java 8 to Java 21 and the establishment of visual validation infrastructure to ensure quality throughout the incremental migration process.

## 2. Motivation

### Problem Statement

LoTREC's Swing-based GUI presents several challenges:

1. **Aging toolkit**: Swing has not received meaningful updates in over a decade. New Java developers are increasingly unfamiliar with it, making maintenance and contributions harder.
2. **Limited styling**: Swing's look-and-feel system is rigid. Customizing appearance requires cumbersome workarounds rather than clean CSS-based styling.
3. **Developer experience**: Building and modifying Swing UIs involves verbose boilerplate code. JavaFX offers property binding and CSS-based styling, significantly reducing development effort for future features. The migration uses a programmatic-first approach (no FXML) for maximum control over LoTREC's highly dynamic, data-driven panels.
4. **Java 8 lock-in**: The current Java 8 requirement prevents the project from benefiting from 8+ years of language improvements (records, sealed classes, text blocks, pattern matching, improved performance, and modern garbage collectors).
5. **Ecosystem decay**: Libraries and tools increasingly drop Java 8 support. Staying on Java 8 narrows the pool of compatible dependencies and development tools.

### Use Cases

1. **UC-1: Researcher loads and analyzes a logic** — A researcher launches LoTREC, loads a predefined modal logic (e.g., K, S4, S5), enters a formula, runs the tableau proof search, and inspects the resulting proof tree. The entire workflow must function identically in the new JavaFX GUI.

2. **UC-2: Researcher defines a custom logic** — A researcher creates a new logic definition by adding connectors, writing inference rules (conditions and actions), defining strategies, and testing formulas. All editing dialogs and panels must preserve the same editing capabilities.

3. **UC-3: Researcher inspects and exports premodels** — A researcher views constructed tableaux as interactive graphs (nodes, edges, world labels), navigates the proof tree (zoom, pan, select), and exports visualizations to file. The Cytoscape-based graph display continues to function via a compatibility bridge.

4. **UC-4: Developer maintains or extends the GUI** — A developer modifies or adds GUI components using modern JavaFX patterns (CSS styling, property bindings, programmatic layout construction) rather than legacy Swing boilerplate, reducing development time for future enhancements.

### Success Criteria

- [ ] All 38 predefined logics can be loaded, analyzed, and visualized through the new GUI without errors
- [ ] Every user workflow documented in the existing GUI reference (42 screenshot states) is fully functional in the new GUI
- [ ] The new GUI is structurally equivalent to the existing Swing GUI (same layout organization, same controls, same menus, same dialog flows), with functional improvements where modern UX best practices dictate
- [ ] A researcher unfamiliar with the migration can use the new GUI without relearning — all controls, menus, and dialogs are in the same or improved locations following modern UX conventions
- [ ] The application launches and displays the main window within the same time frame as the current Swing version (no perceptible startup delay increase)
- [ ] The old Swing GUI remains functional and launchable throughout the migration process until the JavaFX version is declared complete
- [ ] The project builds and all existing tests pass on Java 21 after the upgrade
- [ ] Visual validation tooling can capture screenshots and compare Swing vs. JavaFX renderings automatically

---

## Clarifications

### Session 2026-02-11

- Q: Should the JavaFX GUI be built using FXML layouts or programmatic Java code? → A: Programmatic-first with CSS. GUI built in Java code, styled via external CSS. Best for LoTREC's dynamic/data-driven UIs.
- Q: What level of accessibility support should the new JavaFX GUI provide? → A: Match existing Swing level. Keyboard shortcuts preserved, no additional accessibility work. Full accessibility is a separate future enhancement.
- Q: Should the JavaFX GUI ship with a single default theme or support multiple themes? → A: Single default theme. One CSS stylesheet replicating the current Swing look. Dark mode and theme switching are out of scope.
- Q: How should the application behave if the Cytoscape SwingNode bridge fails to render? → A: Error placeholder. Display an error message in the graph panel area; rest of application remains functional.
- Q: Should the JavaFX migration adopt JPMS (module-info.java) or stay on the classpath? → A: Classpath only. No module-info.java. Legacy Cytoscape and backend code are not modular; JPMS adoption deferred to a future phase.
- Q: How should users/developers switch between the Swing and JavaFX GUIs during the migration period? → A: Separate Gradle tasks: `gradlew run` (JavaFX, default), `gradlew runSwing` (Swing, on demand).
- Q: Which OpenJFX version should be used as the JavaFX SDK dependency? → A: OpenJFX 21 (LTS), matching the Java 21 target for long-term support alignment.

---

## 3. Domain Context

> LoTREC-specific concepts relevant to this feature.

### Logical Concepts

This feature does not change any theorem proving logic. However, the GUI must faithfully present the following domain concepts to users:

| Term | Definition |
|------|------------|
| **Tableau** | A proof tree structure where nodes represent possible worlds and edges represent accessibility relations between worlds |
| **Premodel** | A partially constructed model/tableau during or after the proof search process |
| **Connector** | A logical operator (e.g., not, and, or, nec, pos) with defined arity, display format, and priority |
| **Rule** | An inference rule consisting of conditions (when it applies) and actions (what it does) |
| **Strategy** | A control structure defining the order in which rules are applied (repeat, firstRule, allRules) |
| **Marked Expression** | A formula annotated with marks used to track rule application state |

### Related Logic Components

- **Connectors involved**: All user-defined connectors displayed in the Connectors tab
- **Expression types**: All expression types displayed in formula input fields and tableau nodes
- **Tableaux elements**: Nodes, edges, world labels, and expressions displayed in the graph visualization panel

---

## 4. Requirements

### 4.1 Functional Requirements

#### Milestone 0: Java 21 (LTS) Upgrade

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-01 | The application shall compile and run on Java 21 (LTS) | Must |
| FR-02 | All existing unit and integration tests shall pass on Java 21 without modification to test logic | Must |
| FR-03 | The Cytoscape source code shall be updated to replace removed JAXB APIs with their Jakarta equivalents | Must |
| FR-04 | The project constitution shall be updated to reflect the new Java version baseline | Must |
| FR-05 | The build configuration shall target Java 21 as the minimum supported version | Must |

#### Milestone 0.5: Visual Validation Infrastructure

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-06 | The system shall provide an automated mechanism to capture screenshots of the current Swing GUI in all key states | Must |
| FR-07 | The system shall provide an automated mechanism to capture screenshots of JavaFX GUI components as they are migrated | Must |
| FR-08 | A development-time visual comparison tool shall be available that compares JavaFX screenshots against Swing baselines and reports differences | Must |
| FR-09 | The screenshot capture mechanism shall cover all key application states: main frame, each tab, each dialog, tableau display, and control panel states. The complete list of 42 states is defined by the reference screenshots in `.specify/memory/GUI-V2/` | Should |
| FR-10 | Screenshots shall be captured programmatically (not manually) to ensure reproducibility | Should |

#### Milestone 1: JavaFX GUI Migration

| ID | Requirement | Priority |
|----|-------------|----------|
| FR-11 | The system shall provide a new JavaFX-based main application window with the same layout structure as the current Swing main frame (menu bar, split panes, panel arrangement) | Must |
| FR-12 | The system shall provide a JavaFX Loaded Logics Panel with tabs for Connectors, Rules, Strategies, and Predefined Formulas, replicating all editing capabilities of the current Swing version | Must |
| FR-13 | The system shall provide JavaFX dialogs for all existing Swing dialogs: Predefined Logics, Logic Description, New Connector, New Rule, Satisfiability Check Options, Filter, Run Info, Break Points, Premodel Editor, and file dialogs (Open, Save As, Export) | Must |
| FR-14 | The system shall provide a JavaFX Controls Panel with step, pause, stop, and run controls that communicate with the proof Engine identically to the current Swing version | Must |
| FR-15 | The system shall provide a JavaFX Premodels Construction Settings Panel with formula input and action buttons matching current functionality | Must |
| FR-16 | The system shall display Cytoscape graph visualizations within the JavaFX interface using a compatibility bridge until a native replacement is available (Phase 4 of roadmap). If the bridge fails to render, an error placeholder shall be displayed in the graph panel area while the rest of the application remains functional. | Must |
| FR-17 | The system shall provide a new JavaFX application entry point (`LauncherFX`) as the default Gradle `run` task. The existing Swing entry point shall be available via a separate `runSwing` Gradle task. | Must |
| FR-18 | The Engine shall communicate UI updates to the JavaFX GUI using JavaFX's threading model instead of Swing's threading model | Must |
| FR-19 | The system shall provide a Task Pane (startup dialog) with the same quick-access options as the current version: load predefined logics, open existing file, create new logic | Must |
| FR-20 | The system shall provide a splash screen displayed during application initialization | Should |
| FR-21 | The system shall support external CSS stylesheets for visual customization of the JavaFX GUI | Should |
| FR-22 | The old Swing GUI shall remain functional and launchable via the `gradlew runSwing` task throughout the migration | Must |
| FR-23 | Upon completion and validation of the full JavaFX GUI, the old Swing GUI source code shall be removed | Should |

### 4.2 Non-Functional Requirements

| ID | Requirement | Metric |
|----|-------------|--------|
| NFR-01 | The application startup time shall not regress | New GUI displays main window within 5 seconds of launch |
| NFR-02 | The application shall remain responsive during proof search operations | UI controls respond within 200ms while Engine thread runs |
| NFR-03 | The new GUI shall preserve the same keyboard shortcuts and accelerators | All existing Ctrl+key and menu accelerators function identically |
| NFR-04 | The new GUI shall be structurally equivalent to the existing GUI, with functional improvements where modern UX best practices dictate | Layout organization, control placement, and navigation flow match reference screenshots; deviations are permitted where they demonstrably improve usability (e.g., modern dialog patterns, improved spacing, accessible color contrast). Each deviation must be documented in the visual validation report with rationale and approved during review. |
| NFR-05 | The migration shall be incremental and non-destructive | Each migrated component is independently testable; the Swing GUI remains functional until completion |
| NFR-06 | The new GUI source code shall be organized in a clearly separated package | New code is isolated from old Swing code to prevent cross-contamination |
| NFR-07 | Accessibility support shall match the current Swing GUI level | Existing keyboard shortcuts and accelerators preserved; no additional screen reader, WCAG, or ARIA work required. Full accessibility is out of scope for this migration. |

---

## 5. Tech Stack Constraints Checklist

> Updated constraints reflecting this feature's approved technology changes.

- [x] **Java 21**: Project upgraded from Java 8 (constitution GUI framework status: **Unlocked**)
- [x] **Gradle (Kotlin DSL)**: Build system unchanged
- [ ] **OpenJFX 21 (LTS)**: New GUI framework replacing Swing, added as explicit Gradle dependency (constitution-approved migration path)
- [ ] **Cytoscape 2.x bridge**: Graph visualization retained via compatibility bridge until Phase 4
- [x] **JUnit 5 + AssertJ**: Testing framework unchanged
- [x] **JaCoCo**: Coverage reporting unchanged
- [ ] **TestFX**: New test dependency for JavaFX UI testing
- [ ] **Jakarta JAXB**: Replacement for removed javax.xml.bind APIs (Java 11+ compatibility)
- [x] **Classpath execution**: No JPMS module-info.java; all code runs on classpath (legacy Cytoscape incompatible with module system)

---

## 6. Architecture Constraints

### Target Layers

- [x] GUI (`lotrec.guifx.*`) — New JavaFX GUI package
- [x] Process Engine (`lotrec.engine`) — New JavaFXEngineListener
- [ ] Visualization (`cytoscape.*`) — SwingNode bridge only, no changes to Cytoscape itself

### Target Packages

| New/Modified Class | Package | Justification |
|--------------------|---------|---------------|
| MainFrameFX | `lotrec.guifx` | New JavaFX main application window |
| LauncherFX | `lotrec.guifx` | New JavaFX application entry point |
| JavaFXEngineListener | `lotrec.engine` | Engine-to-JavaFX UI update bridge |
| LoadedLogicsPane | `lotrec.guifx` | JavaFX equivalent of LoadedLogicsPanel |
| ControlsPane | `lotrec.guifx` | JavaFX equivalent of ControlsPanel |
| TableauxPane | `lotrec.guifx` | JavaFX equivalent of TableauxPanel |
| CytoscapeSwingBridge | `lotrec.guifx.graph` | SwingNode wrapper for Cytoscape display |
| LogicDefTab | `lotrec.guifx.logicspane` | JavaFX logic definition tab container |
| ConnTabPane | `lotrec.guifx.logicspane` | JavaFX connectors editing tab |
| RulesTabPane | `lotrec.guifx.logicspane` | JavaFX rules editing tab |
| StratTabPane | `lotrec.guifx.logicspane` | JavaFX strategies editing tab |
| TestingFormulaePane | `lotrec.guifx.logicspane` | JavaFX predefined formulas tab |
| DialogsFactory | `lotrec.guifx` | JavaFX dialog creation factory |
| PremodelSettingsPane | `lotrec.guifx` | JavaFX premodel construction settings (formula input + build action); the premodel settings area from `TableauxPanel` extracted as a standalone pane |
| FormulaTransformerPane | `lotrec.guifx` | JavaFX formula infix/prefix conversion utility (mirrors standalone Swing `FormulaTransformerGUI` JPanel) |
| ConditionDialog, ActionDialog | `lotrec.guifx.dialogs` | JavaFX dialogs for rule condition/action editing |
| CSS stylesheets | `lotrec.guifx.styles` | External stylesheets for visual customization |

### UI Construction Approach

All JavaFX GUI components shall be built **programmatically in Java code** (no FXML files). Visual styling shall be applied via external CSS stylesheets. This approach is chosen because LoTREC's panels are highly dynamic — tabs, trees, and lists are populated from logic definitions at runtime — making programmatic construction more maintainable than FXML controller binding.

### Dependency Analysis

- **Will import from**: `lotrec.dataStructure.*`, `lotrec.engine`, `lotrec.process`, `lotrec.parser`, `lotrec.resources`, `cytoscape.*` (bridge only)
- **Will be imported by**: None — leaf layer (GUI is top of dependency chain)
- **Dependency direction valid?**: [x] Yes

---

## 7. Existing Code to Reuse

> Search before creating. What existing code can be leveraged?

### Similar Implementations

| Existing Code | Location | How to Reuse |
|--------------|----------|--------------|
| EngineListener interface | `lotrec.engine.EngineListener` | Implement for JavaFX (same pattern as SwingEngineListener) |
| SwingEngineListener | `lotrec.engine.SwingEngineListener` | Reference implementation — replicate with Platform.runLater() |
| MainFrame layout structure | `lotrec.gui.MainFrame` | Mirror panel arrangement in JavaFX |
| DialogsFactory patterns | `lotrec.gui.DialogsFactory` | Replicate dialog creation patterns |
| LogicXMLParser | `lotrec.parser.LogicXMLParser` | Call directly — no changes needed |
| All condition/action classes | `lotrec.dataStructure.tableau.*` | Call directly — no changes needed |
| Cytoscape graph display | `lotrec.gui.CyTableauDisplayer` | Wrap in SwingNode bridge |

### Extension Points

- [x] Implements existing interface: `EngineListener` (new JavaFXEngineListener)
- [x] Reuses existing backend: All parser, engine, process, and data structure classes unchanged

---

## 8. Proposed Solution

### 8.1 Overview

The migration follows a three-milestone approach:

1. **Milestone 0 — Java 21 (LTS) Upgrade**: Update the project baseline from Java 8 to Java 21 (latest LTS), replacing removed APIs (JAXB) with Jakarta equivalents, and validating all existing functionality.

2. **Milestone 0.5 — Visual Validation Infrastructure**: Establish automated screenshot capture for both Swing (baseline) and JavaFX (current), plus a comparison tool to validate visual equivalence throughout the migration.

3. **Milestone 1 — JavaFX GUI Migration**: Build a complete JavaFX GUI in a new `lotrec.guifx` package, migrating components incrementally from simplest to most complex. The existing Swing GUI remains functional throughout. A SwingNode bridge wraps the Cytoscape graph display until a native replacement is built in Phase 4 of the roadmap.

### 8.2 Component Design

```
                    LoTREC Application
                    ==================

  Entry Points:
  ┌──────────────────┐     ┌──────────────────┐
  │   Launcher.java  │     │  LauncherFX.java │
  │ (gradlew runSwing│     │  (gradlew run)   │
  │   Swing - old)   │     │  (JavaFX - new)  │
  └────────┬─────────┘     └────────┬─────────┘
           │                        │
           v                        v
  ┌──────────────────┐     ┌──────────────────┐
  │   lotrec.gui.*   │     │  lotrec.guifx.*  │
  │   (Swing panels, │     │  (JavaFX panes,  │
  │    dialogs)      │     │   dialogs, CSS)  │
  └────────┬─────────┘     └────────┬─────────┘
           │                        │
           │    ┌───────────────────┤
           │    │                   │
           v    v                   v
  ┌──────────────────┐     ┌──────────────────┐
  │  lotrec.engine   │     │ lotrec.guifx     │
  │  EngineListener  │     │ .graph           │
  │  (interface)     │     │ SwingNode bridge  │
  └────────┬─────────┘     │ → Cytoscape      │
           │               └──────────────────┘
           v
  ┌──────────────────────────────────────────┐
  │  Backend (unchanged)                     │
  │  lotrec.dataStructure, lotrec.parser,    │
  │  lotrec.process, lotrec.resources        │
  └──────────────────────────────────────────┘
```

### 8.3 Class/Method Specifications

| Component | Type | Responsibility |
|-----------|------|----------------|
| LauncherFX | Application entry | Initializes JavaFX runtime, shows splash, opens main window |
| MainFrameFX | Main window | Top-level Stage with menu bar, split panes, and all panels |
| JavaFXEngineListener | Engine bridge | Receives Engine events, dispatches UI updates on JavaFX thread |
| CytoscapeSwingBridge | Swing-JavaFX bridge | Wraps existing Cytoscape JComponent in a SwingNode for display in JavaFX |
| LoadedLogicsPane | Panel | Displays loaded logics with tabbed editing (Connectors, Rules, Strategies, Formulas) |
| ControlsPane | Panel | Step/pause/stop/run controls for proof search |
| TableauxPane | Panel | Lists premodels and hosts the graph visualization area |
| PremodelSettingsPane | Panel | Formula input and build action for premodel construction (extracted from `TableauxPanel` settings area) |
| FormulaTransformerPane | Utility panel | Standalone infix/prefix formula conversion tool (mirrors `FormulaTransformerGUI`) |
| DialogsFactory | Factory | Creates and shows all application dialogs (Predefined Logics, New Connector, etc.) |
| CSS stylesheets | Styles | External .css files controlling visual appearance |

### 8.4 Logic XML Changes

None. This feature does not modify logic XML definitions or the DTD schema.

### 8.5 Migration Order

The GUI components shall be migrated incrementally in this order (simplest → most complex):

1. LauncherFX + MainFrameFX shell (empty window with menu bar and split pane layout)
2. Simple dialogs (Predefined Logics, Logic Description, Satisfiability Check Options, Filter, Run Info)
3. Loaded Logics Panel with Connectors Tab
4. Strategies Tab and Predefined Formulas Tab
5. Rules Tab (most complex panel)
6. Premodels Construction Settings Panel (formula input + buttons)
7. Controls Panel (step/pause/stop)
8. Premodels List Panel
9. Premodels Views Panel with SwingNode bridge for Cytoscape
10. Complex dialogs (Premodels Editor, Break Points, file choosers)
11. Engine integration (JavaFXEngineListener)

---

## 9. Test Strategy

### 9.1 Test Categories

| Category | Count | Description |
|----------|-------|-------------|
| Build Validation | ~5 | Java 21 compilation, JAXB replacement, dependency resolution |
| Visual Validation | ~42 | Screenshot comparison for each key GUI state (matching existing 42 screenshots) |
| UI Functional Tests | ~20 | TestFX tests verifying component behavior (button clicks, tab switches, dialog flows) |
| Integration Tests | ~5 | Engine-to-GUI communication, logic loading, formula analysis end-to-end |
| Regression Tests | Existing | All existing unit and integration tests must continue passing |

### 9.2 Test Cases

| Test ID | Description | Type | Input | Expected Output |
|---------|-------------|------|-------|-----------------|
| TC-01 | Project compiles on Java 21 | Build | `gradlew build` | Successful compilation, no errors |
| TC-02 | All existing tests pass on Java 21 | Regression | `gradlew test` | All tests green |
| TC-03 | JAXB-dependent Cytoscape code compiles | Build | Cytoscape source with Jakarta JAXB | No javax.xml.bind errors |
| TC-04 | Swing baseline screenshots captured | Visual | Screenshot capture task | PNGs for all 42 states saved |
| TC-05 | JavaFX main window launches | UI | Launch LauncherFX | Main window appears with correct layout |
| TC-06 | Predefined logic loads in JavaFX GUI | Integration | Load K.xml via JavaFX GUI | Logic displayed with connectors, rules, strategies |
| TC-07 | Formula analysis completes in JavaFX GUI | Integration | Enter formula, run analysis | Tableau built and displayed in graph view |
| TC-08 | Each migrated panel matches Swing baseline | Visual | Compare screenshots | Structural equivalence confirmed |
| TC-09 | Engine listener dispatches on JavaFX thread | Unit | Engine sends event | JavaFXEngineListener calls Platform.runLater() |
| TC-10 | Swing GUI still launches after migration | Regression | Launch old Launcher | Swing GUI fully functional |

### 9.3 Predefined Logic Testing

All predefined logics should be tested for load-and-display correctness:

- [x] `lotrec/logics/K.xml` — Basic modal logic
- [x] `lotrec/logics/S4.xml` — Reflexive transitive modal logic
- [x] `lotrec/logics/S5.xml` — Equivalence relation modal logic
- [x] All 38 predefined logics — Batch load test to verify no loading errors

---

## 10. Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Cytoscape SwingNode bridge has rendering or threading issues | Medium | High | Isolate bridge in its own component; test early in migration order; on failure, display an error placeholder in the graph panel while keeping the rest of the application functional |
| Rules Tab migration is extremely complex (~36K tokens of Swing code) | High | Medium | Migrate last; dedicate focused effort; break into sub-components |
| JAXB removal breaks Cytoscape in unexpected ways | Medium | High | Run full Cytoscape integration tests after JAXB replacement; keep old JARs as fallback reference |
| Java 21 upgrade reveals hidden runtime incompatibilities | Low | High | Run full test suite after upgrade; perform manual smoke test of all GUI states |
| Dual GUI maintenance burden during migration | Medium | Low | Minimize changes to Swing code during migration; migration order is designed for quick incremental progress |
| TestFX headless mode compatibility issues on CI/development machines | Medium | Low | Provide both headless and headed test execution options; MCP screenshot server as fallback for development |

---

## 11. Open Questions

| # | Question | Status | Resolution |
|---|----------|--------|------------|
| Q1 | Should the Java upgrade target exactly Java 17 (oldest current LTS) or Java 21 (latest LTS)? | Resolved | **Java 21** (latest LTS). All references updated to target Java 21 specifically. |
| Q2 | Should visual equivalence validation require exact pixel matching or structural equivalence? | Resolved | **Structural equivalence** aligned with modern UX best practices. Functional equivalence where modernization improves the design. The new GUI may deviate from the Swing original where modern UX conventions produce a better result. |
| Q3 | Should the three milestones be tracked as one combined feature or decomposed into three separate feature branches? | Resolved | **One feature, three milestones in a single branch** (`001-javafx-gui-migration`). All milestones are tracked as sequential phases within this single feature spec. |

---

## 12. Assumptions

1. **Backend stability**: The backend packages (engine, parser, process, data structures) are stable and require no changes beyond the JavaFXEngineListener addition.
2. **Cytoscape bridge is temporary**: The SwingNode bridge for Cytoscape is an interim solution with an explicit replacement timeline (Phase 4 of the roadmap).
3. **No new GUI features**: This migration replicates existing functionality. New features (Rule DSL editor, infix/prefix toggle) are separate roadmap items.
4. **CSS styling targets structural equivalence**: The JavaFX GUI ships with a single default CSS stylesheet replicating the current Swing look and layout structure, with functional improvements where modern UX best practices dictate. Multiple themes (e.g., dark mode) and theme switching are out of scope. A full visual redesign is out of scope, but incremental UX improvements are in scope.
5. **TestFX availability**: TestFX supports Java 21 and JUnit 5, which is confirmed by current library versions.
6. **Constitution will be updated**: The project constitution's Java version lock and GUI framework migration approach will be updated as part of Milestone 0.
7. **No JPMS**: The project runs entirely on the classpath (no `module-info.java`). Legacy Cytoscape source and the existing backend are not modularized. JPMS adoption is deferred to a future modernization phase.

---

## 13. References

- Constitution: `.specify/memory/constitution.md`
- Roadmap: `.specify/memory/roadmap.md`
- GUI Documentation: `.specify/memory/GUI-V2/V2-Swing-GUI-Description.md`
- GUI Screenshots: `.specify/memory/GUI-V2/` (42 annotated screenshots)
- Migration Prompt: `.specify/memory/GUI-V2/javafx-migration-speckit-prompt.md`
- Architecture: `.CLAUDE.md` and `.claude/skills/brownfield-developer-lotrec/references/architecture.md`
- Tech Stack : Up-to-date Version `.CLAUDE.md` - Old Version `.claude/skills/brownfield-developer-lotrec/references/tech-stack.md`

---

## Approval

| Role | Name | Date | Signature |
|------|------|------|-----------|
| Author | Claude Code | 2026-02-11 | |
| Reviewer | | | |
| Approver | | | |

---

*Template version: 1.0 | Based on LoTREC Constitution*
