# LoTREC Project Constitution

> Project governance, constraints, and development discipline for the LoTREC theorem prover.

## 1. Project Identity

**LoTREC** (Logical Tableaux Research Engineering Companion) is a generic tableaux theorem prover and model builder for modal and description logics. It enables researchers to define their own logics, test satisfiability/validity, and visualize proof trees through an extensible Java-based platform.

### Mission
Provide a flexible, educational, and research-oriented tool for exploring non-classical logics through tableaux methods.

### Core Values
- **Extensibility**: Users can define new logics via XML without modifying source code
- **Visualization**: Proof trees and models are visually represented for understanding
- **Education**: Suitable for teaching modal logic concepts
- **Research**: Supports experimentation with new tableaux rules

---

## 2. Locked Technology Stack

> Technologies marked **Locked** are frozen. Technologies marked **Unlocked** have approved migration paths.

| Component | Version | Configuration | Status |
|-----------|---------|---------------|--------|
| **Java** | 1.8 | Migration planned (Phase 3)  | **Unlocked** |
| **Build System** | Gradle (Kotlin DSL) | `build.gradle.kts` at project root | Locked |
| **GUI Framework** | Swing/AWT → JavaFX | Migration planned (Phase 3) | **Unlocked** |
| **Graph Visualization** | Cytoscape 2.x → TBD | Migration planned (Phase 4) | **Unlocked** |
| **Testing** | JUnit 5 | With AssertJ for fluent assertions | Locked |
| **Coverage** | JaCoCo | Reports in `build/reports/jacoco/` | Locked |
| **Parser Generator** | JavaCC | For expression parsing | Locked |

### GUI Framework Migration (Phase 3)
The GUI framework is unlocked to allow incremental migration from Swing/AWT to JavaFX:
1. JavaFX-first approach: Build new JavaFX application, bridge Cytoscape via `SwingNode`
2. Migrate dialogs and simpler panels first
3. Migrate complex panels (LogicsPanel, TableauxPanel) last
4. Eventually remove Swing entirely

During the transition period, both Swing and JavaFX code coexist in separate packages (`lotrec.gui` and `lotrec.guifx`). Java 21 (LTS) is the minimum version for JavaFX support.

### Graph Visualization Migration (Phase 4)
Cytoscape 2.x is unlocked to allow migration to a modern graph visualization library:
- Candidate libraries: JGraphX, Cytoscape.js (via embedded browser), GraphStream
- Final choice to be determined after prototyping in Phase 4
- Must preserve: hierarchical layout, auto-adapting node width, pan/zoom, export capabilities

### Build Commands
```bash
./gradlew build           # Build project (compile + test + jar)
./gradlew test            # Run all tests
./gradlew test --tests "ClassName"  # Run single test class
./gradlew run             # Launch application
./gradlew fatJar          # Create fat JAR with all dependencies
./gradlew publishDist     # Create distribution ZIP at project root
./gradlew clean           # Clean build artifacts
./gradlew jacocoTestReport  # Generate test coverage report
```

**Note**: On Windows, use `gradlew.bat` instead of `./gradlew`.

### Why Locked/Unlocked?

**Locked:**
- **Gradle**: Modern build system with better dependency management and IDE support
- **JUnit 5 / JaCoCo**: Standard modern testing infrastructure
- **JavaCC**: Deeply integrated with expression parsing

**Unlocked:**
- **Java**: Unlocked from 1.8 to enable Java 21 (LTS) upgrade; required for JavaFX support and modern language features (8+ years of improvements: records, sealed classes, pattern matching, modern GC)
- **GUI Framework**: Swing/AWT is aging; JavaFX provides modern UI capabilities, better CSS styling, and improved developer experience
- **Graph Visualization**: Cytoscape 2.x is unmaintained; modern alternatives offer better performance and maintainability

---

## 3. Architecture Constraints

> For detailed architecture documentation, see:
> `.claude/skills/brownfield-developer-lotrec/references/architecture.md`

### Layer Rules

| Layer | Packages | Allowed Dependencies |
|-------|----------|---------------------|
| Core Logic | `lotrec.dataStructure.*` | Java standard library only |
| Parser | `lotrec.parser` | Core Logic, JavaCC runtime |
| Process Engine | `lotrec.process` | Core Logic |
| Engine Orchestrator | `lotrec.engine` | Core Logic, Process Engine; listener implementations may reference GUI layer |
| GUI (Swing) | `lotrec.gui.*` | All layers, Swing/AWT |
| GUI (JavaFX) | `lotrec.guifx.*` | All layers, JavaFX, Swing (bridge only) |
| Visualization | `cytoscape.*` | Cytoscape API, GUI |
| Resources | `lotrec.resources` | None (data only) |

### Dependency Direction
```
GUI/Visualization → Process Engine → Core Logic ← Parser
                                          ↑
                                     Resources
```

**Violations are forbidden**: Lower layers must never import from higher layers.

---

## 4. Module Placement Rules

> For complete package structure, see:
> `.claude/skills/brownfield-developer-lotrec/references/module-structure.md`

### Decision Tree for New Code

```
Is it a data structure (Expression, Connector, Node, etc.)?
  YES → lotrec.dataStructure.expression OR lotrec.dataStructure.graph
  NO ↓

Is it a tableaux rule, action, or condition?
  YES → lotrec.process
  NO ↓

Is it parsing-related?
  YES → lotrec.parser
  NO ↓

Is it GUI-related?
  YES → Which toolkit?
    JavaFX (new) → Which aspect?
      - Main frames/dialogs → lotrec.guifx
      - Tableaux visualization → lotrec.guifx (TableauxPane)
      - Graph display → lotrec.guifx.graph
      - Logic editing → lotrec.guifx.logicspane
      - Dialogs → lotrec.guifx.dialogs
      - Visual validation → lotrec.guifx.validation
      - CSS styles → lotrec.guifx.styles
    Swing (legacy) → Which aspect?
      - Main frames/dialogs → lotrec.gui
      - Graph display → lotrec.gui.graph
      - Logic editing → lotrec.gui.logicspane
  NO ↓

Is it file I/O or resources?
  YES → lotrec.resources
  NO ↓

Is it a utility without clear home?
  → lotrec.util
```

### Key Package Responsibilities

| Package | Responsibility |
|---------|---------------|
| `lotrec.dataStructure.expression` | Logical expressions (propositions, connectors) |
| `lotrec.dataStructure.graph` | Tableaux structure (nodes, edges, worlds) |
| `lotrec.process` | Rule engine, actions, conditions |
| `lotrec.gui` | Main application frames and dialogs |
| `lotrec.parser` | Expression and logic XML parsing |
| `lotrec.resources` | Predefined logics (K.xml, S4.xml, etc.) |

---

## 5. TDD Discipline

> **Red-Green-Refactor is mandatory for all new features and bug fixes.**

### The TDD Cycle

```
┌─────────────────────────────────────────────────────┐
│  1. RED: Write a failing test                       │
│     - Test must fail for the right reason           │
│     - Compile errors don't count as "red"           │
├─────────────────────────────────────────────────────┤
│  2. GREEN: Write minimum code to pass               │
│     - No extra features                             │
│     - No premature optimization                     │
│     - Just make the test pass                       │
├─────────────────────────────────────────────────────┤
│  3. REFACTOR: Clean up while green                  │
│     - Tests must stay passing                       │
│     - Extract methods, rename, simplify             │
│     - No new functionality                          │
└─────────────────────────────────────────────────────┘
```

### JUnit 5 + AssertJ Test Pattern

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

@DisplayName("FeatureName")
class FeatureNameTest {

    @BeforeEach
    void setUp() {
        // Test fixture setup
    }

    @Nested
    @DisplayName("When valid input")
    class WhenValidInput {

        @Test
        @DisplayName("should do expected behavior")
        void shouldDoExpectedBehavior() {
            // Given
            // When
            // Then
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    @DisplayName("should throw when invalid input")
    void shouldThrowWhenInvalidInput() {
        assertThatThrownBy(() -> methodThatThrows())
            .isInstanceOf(SomeException.class);
    }
}
```

### Test Location
- Tests go in `test/` directory
- Mirror the package structure of `src/`
- Example: `src/lotrec/process/Action.java` → `test/lotrec/process/ActionTest.java`

---

## 6. Code Reuse Principle

> **Search before creating. Extend before duplicating.**

### Before Writing New Code

1. **Search existing codebase** for similar functionality
2. **Check predefined logics** in `lotrec/resources/*.xml` for patterns
3. **Review existing actions/conditions** in `lotrec.process` for extension points
4. **Consult brownfield skill docs** at `.claude/skills/brownfield-developer-lotrec/`

### Extension Points

| To Add | Extend/Implement |
|--------|------------------|
| New logical connector | `Connector` class hierarchy |
| New tableaux action | `AbstractAction` or existing action |
| New tableaux condition | `AbstractCondition` |
| New expression type | `Expression` class hierarchy |
| New GUI panel | Existing panel patterns in `lotrec.gui` |

### Registration Requirements

New actions and conditions must be registered in `CLASSES_KEYWORDS`:
```java
// In the appropriate registration class
CLASSES_KEYWORDS.put("myNewAction", MyNewAction.class);
```

---

## 7. Directory Contracts

| Path | Purpose | Mutability |
|------|---------|------------|
| `src/` | Production source code | Editable |
| `test/` | JUnit test code | Editable |
| `lib/` | Third-party JARs | Frozen |
| `lotrec/resources/` | Predefined logic XML files | Editable with care |
| `build/` | Compiled classes (generated) | Auto-generated |
| `dist/` | Distribution artifacts | Auto-generated |
| `.specify/` | SDD specifications | Editable |
| `specs/` | Active feature specs | Editable |

---

## 8. Coding Conventions

> For detailed conventions, see:
> `.claude/skills/brownfield-developer-lotrec/references/coding-conventions.md`

### Summary

- **Naming**: CamelCase for classes, camelCase for methods/variables
- **Indentation**: 4 spaces (no tabs)
- **Braces**: K&R style (opening brace on same line)
- **Imports**: No wildcards, organize by package
- **Comments**: Javadoc for public APIs, inline for complex logic
- **Error Handling**: Specific exceptions, meaningful messages

### Swing/AWT Conventions (Legacy)

- Use `SwingUtilities.invokeLater()` for UI updates from background threads
- Prefer `ActionListener` lambdas where Java 8 is available
- Follow existing dialog patterns in `lotrec.gui`

### JavaFX Conventions (New)

- Use `Platform.runLater()` for UI updates from background threads
- Use property binding for reactive UI updates where applicable
- Build UI programmatically (no FXML) — LoTREC's dynamic panels require programmatic construction
- Apply visual styling via external CSS stylesheets in `lotrec.guifx.styles`
- Follow existing pane patterns in `lotrec.guifx`

---

## 9. Reference Documentation

| Topic | Location |
|-------|----------|
| Full Architecture | `.claude/skills/brownfield-developer-lotrec/references/architecture.md` |
| Technology Stack | Updated Version `CLAUDE.md` ~~Old Version `.claude/skills/brownfield-developer-lotrec/references/tech-stack.md`~~ |
| Module Structure | `.claude/skills/brownfield-developer-lotrec/references/module-structure.md` |
| Coding Conventions | `.claude/skills/brownfield-developer-lotrec/references/coding-conventions.md` |
| Development Patterns | `.claude/skills/brownfield-developer-lotrec/references/development-patterns.md` |
| Brownfield Principles | `.claude/skills/brownfield-developer-lotrec/SKILL.md` |

---

## 10. Specification Workflow

### Creating a New Feature

1. **Spec**: Create `specs/FEAT-XXX-feature-name/spec.md` using template
2. **Review**: Validate against this constitution
3. **Plan**: Create `plan.md` with TDD phases
4. **Tasks**: Create `tasks.md` for tracking
5. **Implement**: Follow Red-Green-Refactor
6. **Verify**: Run `./gradlew test` and `./gradlew build`

### Templates Location
- `.specify/templates/spec-template.md`
- `.specify/templates/plan-template.md`
- `.specify/templates/tasks-template.md`

---

*This constitution is the source of truth for LoTREC development. All specifications and implementations must comply with these constraints.*
