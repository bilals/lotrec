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

> **NON-NEGOTIABLE**: These technologies are frozen. Do not propose upgrades or alternatives.

| Component | Version | Configuration |
|-----------|---------|---------------|
| **Java** | 1.8 | `javac.source=1.8`, `javac.target=1.8` |
| **Build System** | Apache Ant | `build.xml` at project root |
| **GUI Framework** | Swing/AWT | Standard Java desktop toolkit |
| **Graph Visualization** | Cytoscape 2.x | `cytoscape.jar` in `lib/` |
| **Testing** | JUnit 4.12 | With Hamcrest 1.3 matchers |
| **Parser Generator** | JavaCC | For expression parsing |

### Build Commands
```bash
ant compile    # Compile all sources
ant test       # Run JUnit tests
ant jar        # Build LoTREC.jar
ant clean      # Clean build artifacts
ant run        # Launch application
```

### Why Locked?
- Java 1.8: Maximum compatibility with existing deployments
- Cytoscape 2.x: Deep integration with visualization layer
- Ant: Existing build infrastructure, no Maven/Gradle migration planned

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
| GUI | `lotrec.gui.*` | All layers, Swing/AWT |
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
  YES → Which aspect?
    - Main frames/dialogs → lotrec.gui
    - Tableaux visualization → lotrec.gui.tableau
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

### JUnit 4.12 Test Pattern

```java
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class FeatureNameTest {

    @Before
    public void setUp() {
        // Test fixture setup
    }

    @Test
    public void shouldDoExpectedBehavior() {
        // Given
        // When
        // Then
        assertThat(actual, is(expected));
    }

    @Test(expected = SomeException.class)
    public void shouldThrowWhenInvalidInput() {
        // Test that triggers exception
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

### Swing/AWT Conventions

- Use `SwingUtilities.invokeLater()` for UI updates from background threads
- Prefer `ActionListener` lambdas where Java 8 is available
- Follow existing dialog patterns in `lotrec.gui`

---

## 9. Reference Documentation

| Topic | Location |
|-------|----------|
| Full Architecture | `.claude/skills/brownfield-developer-lotrec/references/architecture.md` |
| Technology Stack | `.claude/skills/brownfield-developer-lotrec/references/tech-stack.md` |
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
6. **Verify**: Run `ant test` and `ant jar`

### Templates Location
- `.specify/templates/spec-template.md`
- `.specify/templates/plan-template.md`
- `.specify/templates/tasks-template.md`

---

*This constitution is the source of truth for LoTREC development. All specifications and implementations must comply with these constraints.*
