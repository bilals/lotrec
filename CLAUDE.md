# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

This project uses Gradle with Kotlin DSL. Run commands from the `lotrec/` directory:

```bash
./gradlew build           # Build project (compile + test + jar)
./gradlew test            # Run all tests
./gradlew test --tests "ClassName"  # Run single test class
./gradlew test --tests "**/parser/*"  # Run tests matching pattern
./gradlew run             # Run the application
./gradlew fatJar          # Create fat JAR with all dependencies
./gradlew packageZip      # Create distribution ZIP
./gradlew clean           # Clean build artifacts
./gradlew jacocoTestReport  # Generate test coverage report
```

**Note**: On Windows, use `gradlew.bat` instead of `./gradlew`.

## Testing

### Framework
- **JUnit 5** with JUnit 4 vintage support for legacy tests
- **AssertJ** for fluent assertions
- **JaCoCo** for coverage reporting (reports in `build/reports/jacoco/`)

### Existing Tests (Use as Examples)
| Test File | Tests |
|-----------|-------|
| `test/lotrec/parser/OldiesTokenizerTest.java` | Formula tokenization |
| `test/gi/transformers/PriorityInfixToPrefixTest.java` | Infix/prefix conversion |
| `test/lotrec/dataStructure/LogicTest.java` | Logic data structure |

### Writing Tests

```java
package lotrec.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

class MyComponentTest {

    @BeforeEach
    void setUp() {
        // Initialize test fixtures
    }

    @Test
    void shouldDoSomething() {
        // Arrange
        // Act
        // Assert with AssertJ
        assertThat(actual).isEqualTo(expected);
    }
}
```

### Priority Test Areas
1. Expression parsing (`lotrec/parser/`)
2. Logic XML loading (`lotrec/parser/LogicXMLParser.java`)
3. Tableau operations (`lotrec/dataStructure/tableau/`)
4. Rule engine (`lotrec/engine/Engine.java`)

## Architecture Overview

LoTREC is an automated theorem prover for modal and description logics using the tableau method.

### Core Workflow
1. User loads a logic definition (XML) specifying connectors and inference rules
2. User enters a formula to analyze
3. Engine applies tableau rules to decompose the formula into a proof tree
4. Cytoscape displays the proof tree interactively
5. Result indicates satisfiability/validity

### Key Packages

| Package | Purpose |
|---------|---------|
| `lotrec.engine` | Proof search orchestrator (`Engine.java` extends Thread) |
| `lotrec.process` | Rule execution strategies, EventMachine, Worker/Activator patterns |
| `lotrec.dataStructure` | Logic, Tableau, Expression, Graph structures |
| `lotrec.dataStructure.tableau.action` | Tableau decomposition actions (18 classes) |
| `lotrec.dataStructure.tableau.condition` | Rule condition predicates (29 classes) |
| `lotrec.parser` | LogicXMLParser, GraphXMLParser, formula tokenization |
| `lotrec.gui` | Swing UI with MainFrame, CyTableauDisplayer (Cytoscape integration) |
| `lotrec.logics` | 38 predefined logic definitions (XML files) |

### Entry Points

| Class | Purpose |
|-------|---------|
| `lotrec.Launcher` | Main class, initializes GUI and Cytoscape |
| `lotrec.Lotrec` | Static configuration (paths, run modes) |
| `lotrec.gui.MainFrame` | Primary application window |
| `lotrec.engine.Engine` | Proof search thread |

### Key Class Relationships

```
Logic
├── Vector<Connector>      # Logical operators (not, and, nec, etc.)
├── Vector<Rule>           # Inference rules
│   ├── Vector<Condition>  # When rule applies
│   └── Vector<Action>     # What rule does
├── Vector<Strategy>       # Rule application order
└── Vector<TestingFormula> # Sample formulas

Engine (extends Thread)
├── Logic                  # Current logic definition
├── Strategy               # Current strategy
├── MarkedExpression       # Formula being analyzed
├── Wallet                 # Collection of tableaux
└── EngineTimer            # Performance tracking
```

## Design Patterns

### Registry Pattern (Adding New Conditions/Actions)
Dynamic lookup of conditions and actions by XML name:

```java
// In AbstractCondition.java (~line 28-76)
CLASSES_KEYWORDS.put("hasElement", ExpressionCondition.class);

// In AbstractAction.java (~line 28-56)
CLASSES_KEYWORDS.put("add", AddExpressionAction.class);
```

**To add new condition/action:** Register in the appropriate `CLASSES_KEYWORDS` map.

### Strategy Pattern
Controls how rules are applied during proof search:
- `repeat ... end` - Loop until no rule applies
- `firstRule ... end` - Apply first matching rule only
- `allRules ... end` - Apply all matching rules

### Observer/Event Pattern
`EventMachine` coordinates rule condition checking and action execution.

## Logic XML Structure

Logic definitions in `src/lotrec/logics/*.xml`:

```xml
<logic>
    <connector>
        <connector-name>not</connector-name>
        <arity>1</arity>
        <output-format>~_</output-format>
        <priority>5</priority>
    </connector>

    <rule>
        <rule-name>RuleName</rule-name>
        <condition>...</condition>
        <action>...</action>
    </rule>

    <strategy>
        <strategy-name>Main</strategy-name>
        <strategy-code>repeat firstRule Rule1 Rule2 end end</strategy-code>
    </strategy>
    <main-strategy>Main</main-strategy>
</logic>
```

### Available Conditions
`hasElement`, `hasNotElement`, `isAtomic`, `isNotAtomic`, `isLinked`, `isNotLinked`, `hasNoSuccessor`, `hasNoParents`, `isAncestor`, `areIdentical`, `areNotIdentical`, `contains`, `isMarked`, `isNotMarked`, `isMarkedExpression`, `isNotMarkedExpression`

### Available Actions
`add`, `createNewNode`, `link`, `unlink`, `stop`, `mark`, `unmark`, `markExpressions`, `unmarkExpressions`, `createOneSuccessor`, `createOneParent`, `hide`, `kill`, `duplicate`, `merge`

## Code Conventions

### Naming Patterns
- `*Condition.java` - Rule condition predicates
- `*Match.java` - Condition matching logic
- `*Activator.java` - Event-triggered condition activation
- `*Action.java` - Rule action implementations

### Formula Representation
- **Prefix notation** internally: `and P Q` not `P and Q`
- **Infix notation** for display via `TransformerGUI.toPrefix()`
- `MarkedExpression` wraps `Expression` with marks/annotations
- `SchemeVariable` for pattern matching in rules

### Thread Safety
The `Engine` runs in its own thread. GUI updates from Engine must use:
```java
SwingUtilities.invokeLater(() -> { /* GUI update */ });
```

## File Locations

| What | Where |
|------|-------|
| Main source | `src/lotrec/` |
| Cytoscape source | `src/cytoscape/` |
| Logic definitions | `src/lotrec/logics/*.xml` |
| UI resources | `src/lotrec/resources/` |
| Test source | `test/` |
| Libraries | `lib/` |
| Build output | `build/` |
| Gradle config | `build.gradle.kts` |

## Project Status

See `.specify/memory/roadmap.md` for the full modernization roadmap.

**Current Status:**
- Phase 1.1 (Gradle migration): COMPLETE
- Phase 1.2 (Test infrastructure): IN PROGRESS - JUnit 5 + AssertJ configured, initial tests created
- Phase 2+ (Code quality, UI modernization): PENDING

## Quick Reference

| Task | Command |
|------|---------|
| Build and test | `./gradlew build` |
| Run application | `./gradlew run` |
| Run specific test | `./gradlew test --tests "OldiesTokenizerTest"` |
| Generate coverage | `./gradlew jacocoTestReport` |
| Create distribution | `./gradlew packageZip` |

### Key Files

| Purpose | File |
|---------|------|
| Application entry | `src/lotrec/Launcher.java` |
| Proof engine | `src/lotrec/engine/Engine.java` |
| Add new condition | `src/lotrec/dataStructure/tableau/condition/AbstractCondition.java` |
| Add new action | `src/lotrec/process/AbstractAction.java` |
| Logic DTD schema | `src/lotrec/logics/logic.dtd` |
| Sample logic | `src/lotrec/logics/K.xml` |
