# Research: [FEAT-001] JavaFX GUI Migration

> Technical research consolidating all decisions, rationale, and alternatives evaluated for the JavaFX GUI migration.

## Metadata

| Field | Value |
|-------|-------|
| **Spec Reference** | [FEAT-001](spec.md) |
| **Author** | Claude Code |
| **Created** | 2026-02-11 |
| **Status** | Complete |

---

## R1: Java Target Version

**Decision**: Java 21 (latest LTS)

**Rationale**: Java 21 is the current Long-Term Support release (September 2023), offering 8+ years of language improvements over Java 8. It provides records, sealed classes, text blocks, pattern matching, virtual threads, and modern garbage collectors. LTS guarantees extended vendor support.

**Alternatives Considered**:
- **Java 17 (previous LTS)**: Still maintained, but already one LTS behind. Upgrading to 17 now would require another upgrade to 21 relatively soon. Java 21 provides all Java 17 features plus sequenced collections, record patterns, and virtual threads.
- **Java 23 (latest non-LTS)**: Bleeding edge, 6-month support cycle. Not appropriate for a research tool requiring stability.

**Impact on Codebase**:
- Source/target compatibility in `build.gradle.kts` changes from `VERSION_1_8` to `VERSION_21`
- 389 `javax.xml.bind` imports must migrate to `jakarta.xml.bind` (JAXB removed in Java 11)
- `javax.annotation` imports may need updates (removed in Java 11)
- Gradle wrapper should target Gradle 8.5+ (Java 21 compatible)
- All 40 existing tests must pass after upgrade

---

## R2: JAXB Migration Strategy (javax.xml.bind → jakarta.xml.bind)

**Decision**: Add Jakarta JAXB dependencies and perform bulk import replacement

**Rationale**: JAXB was deprecated in Java 9 (JEP 320), removed in Java 11. The Jakarta EE fork (`jakarta.xml.bind`) is the official successor with identical API surface. A bulk find-and-replace of import statements is the most efficient migration path given that the API is functionally identical.

**Alternatives Considered**:
- **Re-generate JAXB classes**: The 130 files in `cytoscape/generated/` and `cytoscape/generated2/` were originally generated from XSD schemas. Re-generating with Jakarta-compatible tooling would produce cleaner output but introduces risk of behavioral changes. Deferred in favor of import replacement.
- **Replace JAXB with Jackson XML**: Would require significant refactoring of all marshal/unmarshal code. Not justified for a bridged component (Cytoscape) that will eventually be replaced entirely.
- **Add JAXB as external JAR (Java 8 compatible)**: Would allow staying on `javax.xml.bind` namespace but adds a deprecated dependency. Not recommended.

**Implementation Steps**:
1. Add to `build.gradle.kts`:
   ```kotlin
   implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
   runtimeOnly("com.sun.xml.bind:jaxb-impl:4.0.5")
   ```
2. Bulk replace across all 389 import sites:
   - `javax.xml.bind` → `jakarta.xml.bind`
   - `javax.xml.bind.annotation` → `jakarta.xml.bind.annotation`
3. Update any `JAXBContext.newInstance()` calls if package scanning changed
4. Run full test suite to validate

**Files Affected**: ~135 files across `src/cytoscape/generated/`, `src/cytoscape/generated2/`, `src/cytoscape/bookmarks/`, `src/cytoscape/data/readers/`, `src/cytoscape/data/writers/`

---

## R3: JavaFX Distribution Strategy

**Decision**: Use OpenJFX Maven dependencies (modular, classpath-compatible)

**Rationale**: Since Java 11, JavaFX is distributed separately from the JDK via OpenJFX. Maven Central provides platform-specific artifacts that work on the classpath without requiring JPMS. This aligns with the spec's decision to stay on classpath (no `module-info.java`).

**Alternatives Considered**:
- **Bundled JDK with JavaFX (Liberica Full, Azul Zulu FX)**: Simplifies distribution but locks users into a specific JDK vendor. Not appropriate for an open-source research tool.
- **JPMS modules**: Would provide better encapsulation but Cytoscape's 512 source files and 30+ library JARs are not modularized. JPMS adoption deferred per spec decision.
- **jlink custom runtime image**: Overkill for development phase; consider for future distribution packaging.

**Implementation**:
```kotlin
// build.gradle.kts
val javafxVersion = "21.0.5"
val javafxPlatform = when {
    org.gradle.internal.os.OperatingSystem.current().isWindows -> "win"
    org.gradle.internal.os.OperatingSystem.current().isMacOsX -> "mac"
    else -> "linux"
}

implementation("org.openjfx:javafx-base:$javafxVersion:$javafxPlatform")
implementation("org.openjfx:javafx-controls:$javafxVersion:$javafxPlatform")
implementation("org.openjfx:javafx-graphics:$javafxVersion:$javafxPlatform")
implementation("org.openjfx:javafx-swing:$javafxVersion:$javafxPlatform")  // For SwingNode
```

**Note**: `javafx-swing` module is required for the Cytoscape SwingNode bridge.

---

## R4: Cytoscape SwingNode Bridge Pattern

**Decision**: Wrap Cytoscape JComponent in a `javafx.embed.swing.SwingNode` within an isolated error-handling container

**Rationale**: SwingNode is the official JavaFX mechanism for embedding Swing components. It handles EDT/FX thread coordination automatically. Since Cytoscape is a temporary dependency (Phase 4 replacement planned), a simple bridge with error handling is the correct investment level.

**Alternatives Considered**:
- **JFXPanel (embed JavaFX in Swing)**: The spec originally considered a hybrid Swing→JavaFX migration using JFXPanel. However, the spec resolved to build a new JavaFX application with SwingNode for Cytoscape only. This is cleaner — the application is JavaFX-first with one Swing embedding point.
- **Render Cytoscape to image**: Capture Cytoscape output as BufferedImage and display in JavaFX ImageView. Loses interactivity (zoom, pan, click). Not acceptable.
- **Rewrite graph display in JavaFX immediately**: Scope creep — this is Phase 4 of the roadmap.

**Implementation Pattern**:
```java
public class CytoscapeSwingBridge extends StackPane {
    private final SwingNode swingNode = new SwingNode();

    public void displayTableau(Tableau tableau) {
        SwingUtilities.invokeLater(() -> {
            try {
                JComponent cyPanel = createCytoscapeView(tableau);
                swingNode.setContent(cyPanel);
            } catch (Exception e) {
                Platform.runLater(() -> showErrorPlaceholder(e));
            }
        });
    }

    private void showErrorPlaceholder(Exception e) {
        // Display error message in graph panel area
        // Rest of application remains functional (FR-16)
    }
}
```

**Threading Considerations**:
- Cytoscape operations must run on EDT (`SwingUtilities.invokeLater`)
- JavaFX updates must run on FX Application Thread (`Platform.runLater`)
- SwingNode handles most cross-thread coordination automatically
- Error boundary ensures Cytoscape failures don't crash the application

---

## R5: GUI Construction Approach

**Decision**: Programmatic Java code with external CSS stylesheets (no FXML)

**Rationale**: LoTREC's GUI is highly dynamic — tabs, trees, and lists are populated from logic definitions loaded at runtime. Programmatic construction provides direct control over these data-driven layouts. CSS provides clean visual styling separation without the overhead of FXML controller binding.

**Alternatives Considered**:
- **FXML + Controller binding**: Standard JavaFX pattern for static layouts. However, LoTREC's panels are data-driven (connector lists, rule trees, strategy editors) — these are more naturally expressed in code than in FXML markup.
- **FXML for static parts, code for dynamic parts**: Hybrid approach that adds complexity without clear benefit for LoTREC's small UI surface area.

**CSS Strategy**: Single default CSS stylesheet replicating the current Swing look. No dark mode, no theme switching (per spec clarification). File location: `src/lotrec/guifx/styles/default.css`.

---

## R6: TestFX Testing Strategy

**Decision**: TestFX 4.x with JUnit 5, supporting both headed and headless execution

**Rationale**: TestFX is the de facto standard for JavaFX UI testing. Version 4.x supports Java 21 and JUnit 5. Headless mode (via Monocle) enables CI execution without a display server.

**Alternatives Considered**:
- **Manual testing only**: Insufficient for a 42-state GUI with regression risk.
- **Selenium/WebDriver**: Not applicable — JavaFX is not a web technology.
- **Screenshot-only comparison**: Covers visual validation but not behavioral testing (button clicks, dialog flows).

**Implementation**:
```kotlin
// build.gradle.kts
testImplementation("org.testfx:testfx-core:4.0.18")
testImplementation("org.testfx:testfx-junit5:4.0.18")

// For headless testing
testRuntimeOnly("org.testfx:openjfx-monocle:21.0.2")
```

**Test Pattern**:
```java
@ExtendWith(ApplicationExtension.class)
class MainFrameFXTest {

    @Start
    void start(Stage stage) {
        // Initialize JavaFX stage for testing
    }

    @Test
    void shouldDisplayMainWindow(FxRobot robot) {
        // Interact with UI via robot
        // Assert state with TestFX matchers
    }
}
```

---

## R7: JavaFXEngineListener Design

**Decision**: Implement `EngineListener` interface with all callbacks dispatched via `Platform.runLater()`

**Rationale**: Direct port of the `SwingEngineListener` pattern. The Engine runs in its own thread and calls listener methods. The JavaFX listener wraps each callback in `Platform.runLater()` to marshal updates to the FX Application Thread, exactly as `SwingEngineListener` uses `SwingUtilities.invokeLater()` for the EDT.

**Reference**: `SwingEngineListener.java` (182 lines) — all 16 EngineListener methods wrap GUI calls in `SwingUtilities.invokeLater()`. The JavaFX version is a mechanical translation.

**Existing Pattern** (from `HeadlessEngineListener.java`): The headless listener captures state without GUI updates, demonstrating that the listener pattern is well-decoupled from any specific UI framework.

---

## R8: Visual Validation Infrastructure

**Decision**: Programmatic screenshot capture using TestFX (`FxRobot.capture()`) and JavaFX `Scene.snapshot()`, with image comparison using pixel-diff tooling at structural (not pixel-exact) level

**Rationale**: The spec requires automated screenshot capture (FR-06, FR-07) and comparison (FR-08) covering 42 key application states. TestFX provides built-in screenshot capture. Structural comparison (layout, control presence, sizing) rather than pixel-exact matching accommodates platform rendering differences.

**Alternatives Considered**:
- **Manual screenshot capture**: Not reproducible (FR-10 requires programmatic capture).
- **Pixel-exact comparison**: Too brittle — font rendering, anti-aliasing, and platform differences cause false failures.
- **MCP screenshot server**: Useful as development fallback but not automated enough for CI.

**Implementation Strategy**:
1. **Swing baseline capture**: Use `Robot.createScreenCapture()` on Swing components before migration
2. **JavaFX capture**: Use `Scene.snapshot()` or TestFX `FxRobot.capture()` on new components
3. **Comparison**: Report structural differences (missing controls, layout changes) rather than pixel diffs
4. **Storage**: Reference images in `specs/001-javafx-gui-migration/screenshots/`

---

## R9: Package Organization

**Decision**: New `lotrec.guifx` package hierarchy, completely separate from `lotrec.gui`

**Rationale**: Clean separation prevents cross-contamination during migration (NFR-06). The old `lotrec.gui` package remains functional until migration completion (FR-22). After completion, `lotrec.gui` is removed (FR-23).

**Package Structure**:
```
src/lotrec/guifx/
├── LauncherFX.java          # Application entry point
├── MainFrameFX.java         # Main Stage window
├── LoadedLogicsPane.java    # Logic definition panel
├── ControlsPane.java        # Proof search controls
├── TableauxPane.java        # Premodel list and display
├── DialogsFactory.java      # Dialog creation factory
├── FormulaTransformerDialog.java
├── dialogs/                 # All application dialogs
│   ├── PredefinedLogicsDialog.java
│   ├── LogicDescriptionDialog.java
│   ├── NewConnectorDialog.java
│   ├── NewRuleDialog.java
│   ├── SatisfiabilityDialog.java
│   ├── FilterDialog.java
│   ├── RunInfoDialog.java
│   ├── BreakPointsDialog.java
│   ├── PremodelEditorDialog.java
│   └── FileDialogs.java
├── logicspane/              # Logic definition tabs
│   ├── LogicDefTab.java
│   ├── ConnTabPane.java
│   ├── RulesTabPane.java
│   ├── StratTabPane.java
│   └── TestingFormulaePane.java
├── graph/                   # Graph visualization
│   └── CytoscapeSwingBridge.java
└── styles/                  # CSS stylesheets
    └── default.css
```

---

## R10: Migration Order Validation

**Decision**: 11-step incremental migration order (simplest → most complex) as specified in spec Section 8.5

**Rationale**: Each step produces an independently testable, functional increment. The order is designed to:
1. Establish the application shell first (Step 1)
2. Migrate simple, independent dialogs next (Step 2)
3. Build up panel complexity gradually (Steps 3-8)
4. Tackle the Cytoscape bridge after panels are stable (Step 9)
5. Migrate complex dialogs that depend on panels (Step 10)
6. Complete engine integration last when all UI is ready (Step 11)

**Dependency Validation**:
- Steps 1-2: No inter-panel dependencies
- Steps 3-5: Logic panels can be built independently
- Steps 6-8: Construction/Controls/Premodels depend on step 1 (main frame shell)
- Step 9: SwingNode bridge depends on step 8 (premodels views container)
- Step 10: Complex dialogs depend on steps 3-8 (panels they modify)
- Step 11: Engine listener depends on all panels being ready to receive updates

---

*Research complete. All NEEDS CLARIFICATION items resolved. Ready for implementation planning.*
