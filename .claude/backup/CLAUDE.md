# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

This is a NetBeans/Ant Java project. Run commands from the `lotrec/` directory:

```bash
ant clean        # Clean build artifacts
ant compile      # Compile source code
ant jar          # Build executable JAR (dist/LoTREC.jar)
ant test         # Run JUnit tests
ant run          # Run the application
ant javadoc      # Generate API documentation
```

The `-post-jar` target automatically creates `LoTREC-distribution.zip` containing the JAR, `run.bat`, and `README.TXT`.

### Running a Single Test

```bash
ant test -Dtest.includes=**/YourTestClass.java
```

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
| `lotrec.engine` | Proof search orchestrator (Engine.java extends Thread) |
| `lotrec.process` | Rule execution strategies, EventMachine, Worker/Activator patterns |
| `lotrec.dataStructure` | Logic, Tableau, Expression, Graph structures |
| `lotrec.dataStructure.tableau.action` | Tableau decomposition actions (18 classes) |
| `lotrec.dataStructure.tableau.condition` | Rule condition predicates (29 classes) |
| `lotrec.parser` | LogicXMLParser, GraphXMLParser, formula tokenization |
| `lotrec.gui` | Swing UI with MainFrame, CyTableauDisplayer (Cytoscape integration) |
| `lotrec.logics` | 38 predefined logic definitions (XML files) |

### Entry Points

- **`lotrec.Launcher`** - Main class, initializes GUI and Cytoscape
- **`lotrec.Lotrec`** - Static configuration (paths, run modes)
- **`lotrec.gui.MainFrame`** - Primary application window
- **`lotrec.engine.Engine`** - Proof search thread

## Current Project State

### Test Coverage

**Status: Minimal** - Only 1 actual JUnit test exists (`test/lotrec/SampleJUnitTest.java`).

| Area | Coverage | Priority for Testing |
|------|----------|---------------------|
| Engine (proof search) | None | High |
| Expression parsing | None | High |
| LogicXMLParser | None | High |
| Graph operations | None | Medium |
| Tableau operations | None | Medium |
| GUI components | None | Low |

### Known Issues & TODOs

**117+ TODO/FIXME comments** exist across the codebase:

| File | Issue | Lines |
|------|-------|-------|
| `gui/TableauxPanel.java` | Event handler stubs not implemented | 1741, 1745, 1749, 1953, 1957 |
| `gui/logicspane/RulesTabPanel.java` | 6 incomplete event handlers | Multiple |
| `gui/logicspane/ConnTabPanel.java` | Incomplete implementation | 1059 |
| `util/CompleteDuplicateable.java` | Null field exception risk | 30 |
| `dataStructure/tableau/action/LinkIfNotExistAction.java` | Old modification with XXX marker | 74 |

**Cytoscape inherited TODOs**: 70+ in `src/cytoscape/` - mostly auto-generated catch blocks and "TODO Remove" comments.

### Deprecated Code

- `AbstractWorker.passTheDeal()` - marked @Deprecated but still present
- 15 total deprecated instances (mostly safe to remove)

## Key Class Relationships

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

Tableau (extends Graph)
├── Vector<TableauNode>    # Worlds/states
├── Vector<TableauEdge>    # Accessibility relations
└── Strategy               # Associated strategy

EventMachine
├── Queue                  # Pending events
├── ActionStocking         # Actions to execute
└── Vector<SingleEventEater> # Condition matchers
```

## Design Patterns

Understanding these patterns is essential for extending LoTREC correctly.

### Strategy Pattern (Core)

Controls how rules are applied during proof search.

```
Strategy (extends AllRules)
├── repeat ... end    → Loop until no rule applies
├── firstRule ... end → Apply first matching rule only
└── allRules ... end  → Apply all matching rules
```

**Key files:** `process/Strategy.java`, `process/AllRules.java`, `process/FirstRule.java`

### Observer/Event Pattern

EventMachine coordinates rule condition checking and action execution.

```
Event Generated → Queue → EventMachine → SingleEventEater → Match/Activator
                                                          ↓
                                                    Action Execution
```

**Key files:** `process/EventMachine.java`, `process/ProcessListener.java`

### Registry Pattern

Dynamic lookup of conditions and actions by XML name.

```java
// In AbstractCondition.java (~line 28-76)
CLASSES_KEYWORDS.put("hasElement", ExpressionCondition.class);
CLASSES_KEYWORDS.put("isAtomic", IsAtomicCondition.class);
// ... etc

// In AbstractAction.java (~line 28-56)
CLASSES_KEYWORDS.put("add", AddExpressionAction.class);
CLASSES_KEYWORDS.put("createNewNode", AddNodeAction.class);
// ... etc
```

**To add new condition/action:** Register in the appropriate `CLASSES_KEYWORDS` map.

### Duplicator/Cloning Pattern

Deep copying for tableau branching (disjunction handling).

```java
// Interfaces
Duplicateable           // Basic duplication
CompleteDuplicateable   // Full deep copy

// Implementation
CommonDuplicator.duplicate(object, duplicateMap)
```

**Key files:** `util/Duplicateable.java`, `util/CompleteDuplicateable.java`, `util/CommonDuplicator.java`

**Warning:** `CompleteDuplicateable` line 30 notes exception risk when fields are null.

### Template Method Pattern

Abstract base classes define the structure, subclasses implement specifics.

```
AbstractCondition
├── getMatch()          # Returns matching logic
├── getActivator()      # Returns event trigger
└── Subclasses implement specific condition logic

AbstractAction
├── apply()             # Execute the action
└── Subclasses implement specific action logic
```

### Visitor Pattern (Condition Matching)

Conditions are evaluated through Match and Activator classes.

```
ExpressionCondition → ExpressionMatch (restriction logic)
                   → ExpressionActivator (event-triggered)

LinkCondition → LinkMatch
             → LinkActivator
```

**Naming convention:** `*Condition.java` + `*Match.java` + optional `*Activator.java`

## Logic XML Structure

Logic definitions in `src/lotrec/logics/*.xml` follow this structure:

```xml
<logic>
    <parser-version>2.1</parser-version>
    <description>Logic description</description>

    <!-- Connectors define operators -->
    <connector>
        <connector-name>not</connector-name>
        <arity>1</arity>
        <associative>false</associative>
        <output-format>~_</output-format>    <!-- _ is placeholder for operand -->
        <priority>5</priority>                <!-- Higher = binds tighter -->
    </connector>

    <!-- Rules define inference steps -->
    <rule>
        <rule-name>RuleName</rule-name>
        <condition>
            <condition-name>hasElement</condition-name>
            <parameter>node0</parameter>
            <parameter>not variable a</parameter>
        </condition>
        <action>
            <action-name>add</action-name>
            <parameter>node0</parameter>
            <parameter>variable a</parameter>
        </action>
    </rule>

    <!-- Strategies control rule application -->
    <strategy>
        <strategy-name>MainStrategy</strategy-name>
        <strategy-code>repeat firstRule Rule1 Rule2 end end</strategy-code>
    </strategy>

    <main-strategy>MainStrategy</main-strategy>
</logic>
```

### Strategy DSL Keywords

- `repeat ... end` - Loop until no rule applies
- `firstRule ... end` - Apply first matching rule only
- `allRules ... end` - Apply all matching rules

### Available Conditions (XML `condition-name`)

Complete list from `AbstractCondition.CLASSES_KEYWORDS`:

| Name | Class | Description |
|------|-------|-------------|
| `hasElement` | ExpressionCondition | Node contains formula |
| `hasNotElement` | NotExpressionCondition | Node doesn't contain formula |
| `isAtomic` | IsAtomicCondition | Formula is atomic |
| `isNotAtomic` | IsNotAtomicCondition | Formula is not atomic |
| `isLinked` | LinkCondition | Edge exists between nodes |
| `isNotLinked` | NotLinkCondition | No edge between nodes |
| `hasNoSuccessor` | HasNotSuccessorCondition | Node has no outgoing edges |
| `hasNoParents` | HasNoParentsCondition | Node has no incoming edges |
| `isAncestor` | AncestorCondition | Transitive edge relation |
| `areIdentical` | IdenticalCondition | Same node reference |
| `areNotIdentical` | NotIdenticalCondition | Different node references |
| `areNotEqual` | NotEqualCondition | Nodes are not equal |
| `contains` | ContainsCondition | Formula contains subformula |
| `haveSameFormulasSet` | HaveSameFormulasSetCondition | Nodes have identical formulas |
| `isNewNode` | NodeCreatedCondition | Node was just created |
| `isMarked` | MarkCondition | Node has mark |
| `isNotMarked` | NotMarkCondition | Node lacks mark |
| `isMarkedExpression` | MarkExpressionCondition | Formula has mark |
| `isNotMarkedExpression` | NotMarkExpressionCondition | Formula lacks mark |
| `isMarkedExpressionInAllChildren` | MarkedExpressionInAllChildrenCondition | Formula marked in all children |

### Available Actions (XML `action-name`)

Complete list from `AbstractAction.CLASSES_KEYWORDS`:

| Name | Class | Description |
|------|-------|-------------|
| `add` | AddExpressionAction | Add formula to node |
| `createNewNode` | AddNodeAction | Create new node |
| `link` | LinkAction | Create edge between nodes |
| `unlink` | UnlinkAction | Remove edge between nodes |
| `stop` | StopStrategyAction | Close/stop branch |
| `mark` | MarkAction | Mark node |
| `unmark` | UnmarkAction | Unmark node |
| `markExpressions` | MarkExpressionsAction | Mark formula in node |
| `unmarkExpressions` | UnmarkExpressionsAction | Unmark formula in node |
| `createOneSuccessor` | AddOneSuccessorAction | Create single successor node |
| `createOneParent` | AddOneParentAction | Create single parent node |
| `hide` | HideAction | Hide node from display |
| `kill` | KillAction | Remove/delete node |
| `duplicate` | DuplicateAction | Branch/duplicate tableau |
| `merge` | MergeNodeInNodeAction | Merge two nodes |

## Adding New Features

### Adding a New Condition (Complete Example)

This example creates a condition that checks if a node has exactly N formulas.

**Step 1:** Create the condition class in `lotrec/dataStructure/tableau/condition/`:

```java
package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.expression.*;
import lotrec.process.Restriction;

@ParametersTypes(types = {"node", "integer"})
@ParametersDescriptions(descriptions = {
    "The node to check",
    "The expected formula count"
})
public class HasFormulaCountCondition extends AbstractCondition {
    private SchemeVariable nodeScheme;
    private int expectedCount;

    public HasFormulaCountCondition(SchemeVariable nodeScheme, int expectedCount) {
        super();
        this.nodeScheme = nodeScheme;
        this.expectedCount = expectedCount;
    }

    @Override
    public Restriction getRestriction() {
        return new HasFormulaCountMatch(nodeScheme, expectedCount);
    }

    // Getters for GUI/serialization
    public SchemeVariable getNodeScheme() { return nodeScheme; }
    public int getExpectedCount() { return expectedCount; }
}
```

**Step 2:** Create the Match class:

```java
package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.process.Restriction;
import lotrec.bind.InstanceSet;

public class HasFormulaCountMatch implements Restriction {
    private SchemeVariable nodeScheme;
    private int expectedCount;

    public HasFormulaCountMatch(SchemeVariable nodeScheme, int expectedCount) {
        this.nodeScheme = nodeScheme;
        this.expectedCount = expectedCount;
    }

    @Override
    public InstanceSet restriction(InstanceSet instanceSet) {
        // Get the bound node from the instance set
        TableauNode node = (TableauNode) instanceSet.get(nodeScheme);
        if (node == null) {
            return null; // Node not bound yet
        }

        // Check formula count
        int actualCount = node.getMarkedExpressions().size();
        if (actualCount == expectedCount) {
            return instanceSet; // Condition satisfied
        }
        return null; // Condition not satisfied
    }
}
```

**Step 3:** Register in `AbstractCondition.java` (around line 28-76):

```java
static {
    // ... existing entries ...
    CLASSES_KEYWORDS.put("hasFormulaCount", HasFormulaCountCondition.class);
}
```

**Step 4:** Use in XML logic definition:

```xml
<condition>
    <condition-name>hasFormulaCount</condition-name>
    <parameter>node0</parameter>
    <parameter>3</parameter>
</condition>
```

### Adding a New Action (Complete Example)

This example creates an action that prints debug info to console.

**Step 1:** Create the action class in `lotrec/dataStructure/tableau/action/`:

```java
package lotrec.dataStructure.tableau.action;

import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.process.AbstractAction;
import lotrec.process.EventMachine;
import lotrec.bind.InstanceSet;

@ParametersTypes(types = {"node", "formula"})
@ParametersDescriptions(descriptions = {
    "The node to debug",
    "The formula to print"
})
public class DebugPrintAction extends AbstractAction {
    private SchemeVariable nodeScheme;
    private Expression formulaScheme;

    public DebugPrintAction(SchemeVariable nodeScheme, Expression formulaScheme) {
        super();
        this.nodeScheme = nodeScheme;
        this.formulaScheme = formulaScheme;
    }

    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;

        // Get bound values
        TableauNode node = (TableauNode) instanceSet.get(nodeScheme);
        Expression formula = (Expression) instanceSet.get(formulaScheme);

        // Debug output
        System.out.println("[DEBUG] Node: " + node.getName());
        System.out.println("[DEBUG] Formula: " + formula);
        System.out.println("[DEBUG] All formulas in node: " + node.getMarkedExpressions());

        return instanceSet; // Pass through unchanged
    }

    // Getters for GUI/serialization
    public SchemeVariable getNodeScheme() { return nodeScheme; }
    public Expression getFormulaScheme() { return formulaScheme; }
}
```

**Step 2:** Register in `AbstractAction.java` (around line 28-56):

```java
static {
    // ... existing entries ...
    CLASSES_KEYWORDS.put("debugPrint", DebugPrintAction.class);
}
```

**Step 3:** Use in XML logic definition:

```xml
<action>
    <action-name>debugPrint</action-name>
    <parameter>node0</parameter>
    <parameter>variable a</parameter>
</action>
```

### Adding a New Logic Definition

Create a new XML file in `src/lotrec/logics/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<logic>
    <parser-version>2.1</parser-version>
    <description>My Custom Logic - extends K with special rule</description>

    <!-- Define connectors (operators) -->
    <connector>
        <connector-name>myOp</connector-name>
        <arity>1</arity>
        <associative>false</associative>
        <output-format>[_]</output-format>
        <priority>6</priority>
    </connector>

    <!-- Include standard connectors -->
    <connector>
        <connector-name>not</connector-name>
        <arity>1</arity>
        <associative>false</associative>
        <output-format>~_</output-format>
        <priority>5</priority>
    </connector>

    <!-- Define rules -->
    <rule>
        <rule-name>MyRule</rule-name>
        <condition>
            <condition-name>hasElement</condition-name>
            <parameter>node0</parameter>
            <parameter>myOp variable a</parameter>
        </condition>
        <condition>
            <condition-name>isNotMarkedExpression</condition-name>
            <parameter>node0</parameter>
            <parameter>myOp variable a</parameter>
        </condition>
        <action>
            <action-name>add</action-name>
            <parameter>node0</parameter>
            <parameter>variable a</parameter>
        </action>
        <action>
            <action-name>markExpressions</action-name>
            <parameter>node0</parameter>
            <parameter>myOp variable a</parameter>
        </action>
    </rule>

    <!-- Define strategy -->
    <strategy>
        <strategy-name>Main</strategy-name>
        <strategy-code>repeat allRules MyRule end end</strategy-code>
    </strategy>

    <main-strategy>Main</main-strategy>

    <!-- Optional: test formulas -->
    <testing-formula>
        <formula>myOp P</formula>
        <is-marked-formula>true</is-marked-formula>
    </testing-formula>
</logic>
```

## Code Conventions

### Naming Patterns

- `*Condition.java` - Rule condition predicates
- `*Match.java` - Condition matching logic
- `*Activator.java` - Event-triggered condition activation
- `*Action.java` - Rule action implementations
- `*Event.java` - Event types for the EventMachine

### Variable Naming in Rules

- `node0`, `node1`, etc. - Node scheme variables
- `variable a`, `variable b` - Formula scheme variables
- `premodel_copy` - Duplicated tableau reference
- `R` - Default accessibility relation label

### Thread Safety & Concurrency

The `Engine` class runs in its own thread, separate from the Swing EDT (Event Dispatch Thread).

**Architecture:**

```
Main Thread (Swing EDT)          Engine Thread
        │                              │
        │  ←── synchronized ───→       │
        │                              │
   GUI Updates                   Proof Search
   User Input                    Rule Application
   Event Handling                Tableau Construction
```

**Key Synchronization Points:**

| Location | Mechanism | Purpose |
|----------|-----------|---------|
| `Engine` stop/pause/resume | `synchronized` blocks | Control proof execution |
| GUI updates from Engine | `SwingUtilities.invokeLater()` | Thread-safe UI updates |
| Step-by-step execution | `wait()`/`notify()` | Pause between rule applications |
| ConstantExpression IDs | `synchronized` static method | Unique ID generation |
| Binding module | `synchronized` map | Thread-safe variable bindings |

**Rules for Extending:**

1. **Never update GUI directly from Engine thread** - Always use:
   ```java
   SwingUtilities.invokeLater(() -> {
       // GUI update code here
   });
   ```

2. **Synchronize shared state access** - If your new code accesses data shared between Engine and GUI, use synchronized blocks.

3. **Avoid long operations in synchronized blocks** - This can cause UI freezes.

4. **Be careful with static fields** - They're shared across all instances and may need synchronization.

### Formula Representation

- **Prefix notation** internally: `and P Q` not `P and Q`
- **Infix notation** for display via `TransformerGUI.toPrefix()`
- `MarkedExpression` wraps `Expression` with marks/annotations
- `SchemeVariable` for pattern matching in rules
- `ConstantExpression` for concrete values

## Debugging Tips

### Console Output

The engine prints rule application to stdout:
```
The global strategy starts working on tableau premodel ..
  Rule Stop is step paused..
The global strategy stops working on tableau premodel ..
```

### Common Issues

1. **Rule not firing**: Check condition order - earlier conditions must bind variables used by later ones

2. **Infinite loops**: Ensure rules mark processed formulas or add stopping conditions

3. **Memory issues**: Large tableaux with many branches - use `STOP_WHEN_HAVING_OPEN_TABLEAU` mode

4. **Parser errors**: Logic XML must match DTD schema in `src/lotrec/logics/logic.dtd`

### Benchmarking Mode

Uncomment benchmark code in `Launcher.java` for performance testing:
```java
// In main():
treatArgsForBenchmark(args);

// Run with:
java -jar LoTREC.jar "KMinimal" "and P not nec P" true false
```

## Troubleshooting Guide

### Build Issues

**Problem:** `ant compile` fails with "package does not exist"
```
Solution: Ensure all JARs in lib/ are present. Check lib/Cytoscape_lib/ and lib/Cytoscape_plugins/.
```

**Problem:** `ant test` shows "No tests found"
```
Solution: Ensure test classes are in test/lotrec/ with @Test annotations.
Test class names should end with "Test.java".
```

**Problem:** JAR won't run - "Could not find main class"
```
Solution: Rebuild with `ant clean jar`. Check manifest.mf has correct Main-Class.
```

### Runtime Issues

**Problem:** Logic file won't load - XML parsing error
```
Solution: Validate XML against src/lotrec/logics/logic.dtd.
Common issues:
- Missing closing tags
- Invalid connector/rule names
- Incorrect parameter counts
```

**Problem:** Application hangs on startup
```
Solution: Cytoscape initialization issue. Check:
- All Cytoscape JARs present in lib/Cytoscape_lib/
- Sufficient heap memory: java -Xmx512m -jar LoTREC.jar
```

**Problem:** Graph visualization not showing
```
Solution: Cytoscape plugin issue. Ensure lib/Cytoscape_plugins/ has all required JARs.
Try: File → Reset View or restart application.
```

### Logic/Rule Issues

**Problem:** Rule never fires
```
Causes:
1. Condition order wrong - earlier conditions must bind variables used by later ones
2. Scheme variable names don't match between conditions and actions
3. Formula pattern doesn't match (check prefix vs infix notation)

Debug: Add console output in condition's Match class to trace matching.
```

**Problem:** Infinite loop in proof search
```
Causes:
1. Rule adds formula that triggers same rule again
2. Missing stopping condition (no "stop" action)
3. Strategy doesn't have termination condition

Solutions:
- Mark processed formulas with markExpressions action
- Add isNotMarkedExpression condition to prevent re-processing
- Use hasElement/hasNotElement to check for existing formulas
```

**Problem:** Unexpected tableau branching
```
Cause: DuplicateAction creates copies without proper distinction.
Solution: Ensure duplicate action parameters correctly distinguish branches.
Check that each branch adds different formulas.
```

**Problem:** Memory exhaustion on large proofs
```
Solutions:
1. Use STOP_WHEN_HAVING_OPEN_TABLEAU mode for satisfiability checks
2. Add more aggressive stopping conditions
3. Increase heap: java -Xmx1g -jar LoTREC.jar
4. Simplify strategy to reduce branching
```

### Parser Issues

**Problem:** Formula parsing error - "Unknown connector"
```
Solution: Connector must be defined in logic XML before use.
Check connector-name matches exactly (case-sensitive).
```

**Problem:** Precedence/associativity wrong
```
Solution: Adjust priority values in connector definitions.
Higher priority = binds tighter. Check output-format for display issues.
```

**Problem:** Scheme variable not binding
```
Solution: Use correct syntax:
- Nodes: node0, node1, node2...
- Formulas: variable a, variable b, variable c...
Case matters! "variable A" ≠ "variable a"
```

### GUI Issues

**Problem:** UI unresponsive during proof search
```
Cause: Long operation on EDT or synchronization issue.
Solution: Ensure Engine runs in separate thread. Check for missing
SwingUtilities.invokeLater() in code that updates UI from Engine.
```

**Problem:** Export to PDF/PNG fails
```
Solution: FreeHEP library issue. Check lib/ has:
- freehep-graphics2d.jar
- freehep-graphicsio-pdf.jar (for PDF)
- freehep-graphicsio.jar
```

**Problem:** Localization not working
```
Solution: Check src/lotrec/resources/ has property files for locale.
Supported: English (US), French (FR), Arabic (LB).
Set via ResourcesProvider.setCurrentLocale().
```

## Performance Considerations

### Memory Management

**Tableau Branching:**
- Each `duplicate` action creates a full copy of the tableau
- Exponential growth possible with many disjunctions
- Monitor with `-verbose:gc` JVM flag

**Recommendations:**
```bash
# Increase heap for large proofs
java -Xmx1g -Xms256m -jar LoTREC.jar

# Enable GC logging for debugging
java -Xmx1g -verbose:gc -jar LoTREC.jar
```

### Proof Search Optimization

**Strategy Design:**

| Strategy | Use Case | Performance |
|----------|----------|-------------|
| `firstRule` | Find one applicable rule quickly | Fast |
| `allRules` | Apply all applicable rules | Thorough but slower |
| `repeat` | Loop until saturation | Can be slow if not bounded |

**Optimization Tips:**

1. **Order rules by selectivity** - Put most restrictive conditions first
   ```xml
   <!-- Good: specific condition first -->
   <condition>
       <condition-name>isAtomic</condition-name>
       <parameter>variable a</parameter>
   </condition>
   <condition>
       <condition-name>hasElement</condition-name>
       ...
   </condition>
   ```

2. **Mark processed formulas** - Prevent re-processing
   ```xml
   <action>
       <action-name>markExpressions</action-name>
       <parameter>node0</parameter>
       <parameter>variable a</parameter>
   </action>
   ```

3. **Use stopping conditions** - Stop early when result is known
   ```xml
   <!-- Stop on contradiction -->
   <rule>
       <rule-name>Clash</rule-name>
       <condition>
           <condition-name>hasElement</condition-name>
           <parameter>node0</parameter>
           <parameter>variable a</parameter>
       </condition>
       <condition>
           <condition-name>hasElement</condition-name>
           <parameter>node0</parameter>
           <parameter>not variable a</parameter>
       </condition>
       <action>
           <action-name>stop</action-name>
           <parameter>node0</parameter>
       </action>
   </rule>
   ```

4. **Use `STOP_WHEN_HAVING_OPEN_TABLEAU`** - For satisfiability, stop at first open tableau

### Profiling

**Built-in Timer:**
```java
// In Engine.java
EngineTimer timer = engine.getTimer();
// Access timing data after proof search
```

**Benchmark Mode:**
```bash
# Enable in Launcher.java, then run:
java -jar LoTREC.jar "LogicName" "formula" showTableau waitForClose
```

### Codebase Statistics

| Metric | Value |
|--------|-------|
| Total Java classes | 232 (lotrec/) |
| Lines of code | ~34,000 |
| Predefined logics | 38 |
| Condition types | 29 |
| Action types | 18 |

## Testing

Tests are in `test/lotrec/`. Current coverage is minimal (see "Current Project State" above).

### Test Framework

- **JUnit 4.12** (`lib/junit-4.12.jar`)
- **Hamcrest Core 1.3** for assertions (`lib/hamcrest-core-1.3.jar`)

### Running Tests

```bash
ant test                                    # Run all tests
ant test -Dtest.includes=**/MyTest.java     # Run single test class
ant test -Dtest.includes=**/engine/*.java   # Run tests in package
```

### Writing New Tests

**Basic Test Template:**

```java
package lotrec;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;

public class MyComponentTest {

    @Before
    public void setUp() {
        // Initialize test fixtures
    }

    @After
    public void tearDown() {
        // Clean up resources
    }

    @Test
    public void testBasicFunctionality() {
        // Arrange
        // Act
        // Assert
        assertTrue("Description of what should be true", condition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExceptionCase() {
        // Code that should throw
    }
}
```

**Engine Test Template:**

```java
package lotrec.engine;

import org.junit.Test;
import static org.junit.Assert.*;
import lotrec.dataStructure.Logic;
import lotrec.parser.LogicXMLParser;

public class EngineTest {

    @Test
    public void testBasicProofSearch() throws Exception {
        // Load a simple logic
        Logic logic = LogicXMLParser.loadLogicFromFile("src/lotrec/logics/K.xml");
        assertNotNull("Logic should load", logic);

        // Create engine with formula
        // ... test proof search
    }
}
```

**Expression Parser Test Template:**

```java
package lotrec.parser;

import org.junit.Test;
import static org.junit.Assert.*;
import lotrec.dataStructure.expression.Expression;

public class ExpressionParserTest {

    @Test
    public void testParseSimpleFormula() {
        // Test formula parsing
        // Expression expr = parser.parse("and P Q");
        // assertEquals("and", expr.getConnector().getName());
    }
}
```

### Priority Test Areas

1. **Expression parsing** - Verify formulas parse correctly
2. **Logic XML loading** - Verify logic definitions load without errors
3. **Rule application** - Verify conditions match and actions execute
4. **Tableau construction** - Verify nodes/edges created correctly
5. **Strategy execution** - Verify rule ordering works

## Configuration & Localization

### Build Configuration

| File | Purpose |
|------|---------|
| `build.xml` | Ant build script (NetBeans generated) |
| `nbproject/project.properties` | Build settings, JAR references |
| `manifest.mf` | JAR manifest (Main-Class set by build) |

### Runtime Configuration

**Run Modes** (set in `Lotrec.java`):
- `GUI_RUN_MODE` - Normal desktop application
- `WEB_RUN_MODE` - Web deployment mode

**File Paths:**
```
PredefinedLogics/     → Bundled logics (in JAR)
UserdefinedLogics/    → User-created logics (working directory)
```

### Localization (i18n)

**Supported Locales:**
- English (US) - Default
- French (FR)
- Arabic (LB)

**Property Files** (in `src/lotrec/resources/`):

| File Pattern | Purpose |
|--------------|---------|
| `ControlMenu*.properties` | Menu text |
| `HelpMenu*.properties` | Help text |
| `LanguageMenu*.properties` | Language options |
| `LocalInterface*.properties` | UI strings |
| `LogicDefMenu*.properties` | Logic definition UI |
| `MainFrame*.properties` | Main window text |
| `TableauxEnginePanel*.properties` | Engine UI |
| `messages*.properties` | General messages |
| `dialogs/*.properties` | Dialog text |

**Adding a New Language:**

1. Copy each `*_en_US.properties` file to `*_xx_YY.properties`
2. Translate all values (keep keys unchanged)
3. Register new locale in `ResourcesProvider.java`

**Setting Locale Programmatically:**
```java
ResourcesProvider.setCurrentLocale(new Locale("fr", "FR"));
```

## Dependencies

- **Java 1.8** - Required JDK version
- **Cytoscape** - Graph visualization (embedded with 15+ plugins)
- **FreeHEP** - Export to PDF, PNG, PostScript
- **JUnit 4.12** - Testing framework
- **Xerces, JAXB** - XML processing
- **jtopas** - Tokenization library

All dependencies are in `lib/` - no external package manager needed.

## File Locations

| What | Where |
|------|-------|
| Main source | `src/lotrec/` |
| Cytoscape source | `src/cytoscape/` |
| Logic definitions | `src/lotrec/logics/*.xml` |
| UI resources | `src/lotrec/resources/` |
| Images | `src/lotrec/images/` |
| Test source | `test/lotrec/` |
| Libraries | `lib/` |
| Build output | `build/`, `dist/` |

## Quick Reference

### Common Tasks

| Task | Command/Action |
|------|----------------|
| Build JAR | `ant jar` |
| Run application | `ant run` or `java -jar dist/LoTREC.jar` |
| Run all tests | `ant test` |
| Run single test | `ant test -Dtest.includes=**/TestClass.java` |
| Generate Javadoc | `ant javadoc` |
| Clean build | `ant clean` |
| Create distribution | `ant jar` (creates LoTREC-distribution.zip) |

### Key Files to Know

| Purpose | File |
|---------|------|
| Application entry | `src/lotrec/Launcher.java` |
| Main window | `src/lotrec/gui/MainFrame.java` |
| Proof engine | `src/lotrec/engine/Engine.java` |
| Logic definition | `src/lotrec/dataStructure/Logic.java` |
| Add new condition | `src/lotrec/dataStructure/tableau/condition/AbstractCondition.java` |
| Add new action | `src/lotrec/process/AbstractAction.java` |
| Logic DTD schema | `src/lotrec/logics/logic.dtd` |
| Sample logic | `src/lotrec/logics/K.xml` |

### Related Documentation

- `.claude/PROJECT_OVERVIEW.md` - High-level project overview and purpose
- `README.md` - Original project readme
- `src/lotrec/dist/README.TXT` - Distribution runtime instructions
