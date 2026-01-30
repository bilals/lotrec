# LoTREC Module Structure

## Project Type

**Structure Type**: Monolith with clear package separation
**Module Count**: 12 main packages

## Module List

| Module | Path | Category | Responsibility |
|--------|------|----------|----------------|
| lotrec (root) | `src/lotrec/` | Core | Main entry points and utilities |
| bind | `src/lotrec/bind/` | Utility | Variable binding framework |
| dataStructure | `src/lotrec/dataStructure/` | Core | Domain models and data types |
| dom | `src/lotrec/dom/` | Utility | XML DOM wrappers |
| engine | `src/lotrec/engine/` | Core | Theorem proving engine |
| gui | `src/lotrec/gui/` | UI | User interface components |
| logics | `src/lotrec/logics/` | Resources | Predefined logic XML files |
| parser | `src/lotrec/parser/` | Core | Parsing and serialization |
| process | `src/lotrec/process/` | Core | Strategy execution framework |
| resources | `src/lotrec/resources/` | Utility | Resource management |
| util | `src/lotrec/util/` | Utility | General utilities |
| images | `src/lotrec/images/` | Resources | Image assets |

## Module Details

### lotrec (Root Package)
- **Path**: `src/lotrec/`
- **Responsibility**: Application entry points, global configuration, core utilities
- **Files**:
  - `Launcher.java` - Main entry point with splash screen
  - `Lotrec.java` - Global configuration and utility methods
  - `FileUtils.java` - File I/O operations
  - `PLLoader.java`, `PredefinedLogicsLoader.java` - Logic file loading
- **Lines of Code**: ~1,200

### lotrec.bind
- **Path**: `src/lotrec/bind/`
- **Responsibility**: Variable binding mechanism for expression evaluation
- **Files**:
  - `BasicBinder.java` - Binding implementation
  - `BindEvent.java` - Binding events
  - `Bound.java` - Binding interface
- **Lines of Code**: ~81
- **Directory Structure**:
  ```
  bind/
  ├── BasicBinder.java
  ├── BindEvent.java
  └── Bound.java
  ```

### lotrec.dataStructure
- **Path**: `src/lotrec/dataStructure/`
- **Responsibility**: Core domain models for logic, expressions, tableaux, and graphs
- **Subpackages**:
  - `expression/` (15 files) - Formula representation
  - `graph/` (11 files) - Graph data structures
  - `tableau/` (98 files) - Tableau system
    - `tableau/action/` (24 files) - Tableau actions
    - `tableau/condition/` (8 files) - Firing conditions
- **Key Files**:
  - `Logic.java` - Logic definition container
  - `TestingFormula.java` - Formula testing structure
  - `Parameters.java`, `ParametersDescriptions.java`, `ParametersTypes.java`
- **Lines of Code**: ~10,503
- **Directory Structure**:
  ```
  dataStructure/
  ├── Logic.java
  ├── TestingFormula.java
  ├── Parameters*.java
  ├── expression/
  │   ├── Expression.java (interface)
  │   ├── Connector.java
  │   ├── VariableExpression.java
  │   ├── ConstantExpression.java
  │   ├── MarkedExpression.java
  │   ├── InstanceSet.java
  │   ├── SchemeVariable.java
  │   └── ...
  ├── graph/
  │   ├── Graph.java
  │   ├── Node.java
  │   ├── Edge.java
  │   ├── Wallet.java
  │   ├── Dispatcher.java
  │   └── ...
  └── tableau/
      ├── Tableau.java
      ├── TableauNode.java
      ├── TableauEdge.java
      ├── Rule.java
      ├── action/
      │   ├── AbstractAction.java
      │   ├── AddExpressionAction.java
      │   ├── MarkAction.java
      │   ├── LinkAction.java
      │   ├── DuplicateAction.java
      │   └── ... (20+ actions)
      └── condition/
          ├── AbstractCondition.java
          ├── Condition.java (interface)
          ├── ExpressionCondition.java
          ├── MarkCondition.java
          ├── LinkCondition.java
          └── ...
  ```

### lotrec.dom
- **Path**: `src/lotrec/dom/`
- **Responsibility**: XML DOM manipulation wrappers
- **Files**:
  - `wrappers/` - DOM element wrapper classes
- **Lines of Code**: ~1,925

### lotrec.engine
- **Path**: `src/lotrec/engine/`
- **Responsibility**: Theorem proving execution engine
- **Files**:
  - `Engine.java` - Main execution thread
  - `EngineStatus.java` - Engine state tracking
  - `EngineTimer.java` - Timing utilities
  - `Benchmarker.java` - Performance measurement
- **Lines of Code**: ~723
- **Directory Structure**:
  ```
  engine/
  ├── Engine.java
  ├── EngineStatus.java
  ├── EngineTimer.java
  └── Benchmarker.java
  ```

### lotrec.gui
- **Path**: `src/lotrec/gui/`
- **Responsibility**: User interface components
- **Subpackages**:
  - `dialogs/` - Dialog windows
  - `logicspane/` - Logic editing panels
- **Key Files**:
  - `MainFrame.java` - Main application window
  - `DialogsFactory.java` - Dialog creation factory
  - `ControlsPanel.java` - Control buttons
  - `GraphvizDisplayer.java` - External graph rendering
- **Lines of Code**: ~14,608
- **Directory Structure**:
  ```
  gui/
  ├── MainFrame.java
  ├── DialogsFactory.java
  ├── ControlsPanel.java
  ├── GraphvizDisplayer.java
  ├── dialogs/
  │   ├── AboutDialog.java
  │   ├── OpenLogicDialog.java
  │   └── ...
  └── logicspane/
      ├── LogicsPanel.java
      ├── ConnectorsPanel.java
      ├── RulesPanel.java
      └── ...
  ```

### lotrec.parser
- **Path**: `src/lotrec/parser/`
- **Responsibility**: Parsing logic definitions and formulas
- **Subpackages**:
  - `exceptions/` - Parser-specific exceptions
- **Key Files**:
  - `LogicXMLParser.java` - XML parsing and serialization
  - `OldiesTokenizer.java` - Formula tokenization
  - `Verifier.java` - Logic validation
- **Lines of Code**: ~2,858
- **Directory Structure**:
  ```
  parser/
  ├── LogicXMLParser.java
  ├── OldiesTokenizer.java
  ├── Verifier.java
  └── exceptions/
      ├── ParseException.java
      ├── GraphXMLParserException.java
      ├── LexicalException.java
      └── ...
  ```

### lotrec.process
- **Path**: `src/lotrec/process/`
- **Responsibility**: Strategy execution and event processing
- **Key Files**:
  - `Strategy.java` - Strategy definition
  - `Routine.java` - Execution routine (abstract)
  - `AllRules.java` - All-rules execution strategy
  - `EventMachine.java` - Event queue processing
  - `AbstractWorker.java` - Base worker class
  - `ProcessEvent.java`, `ProcessListener.java` - Event system
- **Lines of Code**: ~2,269
- **Directory Structure**:
  ```
  process/
  ├── Strategy.java
  ├── Routine.java
  ├── AllRules.java
  ├── EventMachine.java
  ├── AbstractWorker.java
  ├── ProcessEvent.java
  ├── ProcessListener.java
  ├── ActionStocking.java
  └── ...
  ```

### lotrec.logics (Resources)
- **Path**: `src/lotrec/logics/`
- **Responsibility**: Predefined logic XML definitions
- **Files**: 38 XML files defining various modal and description logics
- **Examples**:
  - `Classical-Propositional-Logic.xml`
  - `KD.xml`, `KT.xml`, `K4.xml`, `K5.xml`
  - `KD45.xml`, `KD45Optimal.xml`
  - `Intuitionistic-Logic-Lj.xml`
  - `Hybrid-Logic-H-at.xml`

### lotrec.util
- **Path**: `src/lotrec/util/`
- **Responsibility**: General utility classes
- **Key Files**:
  - `Duplicateable.java` - Cloning interface
  - `Duplicator.java` - Cloning helper interface
  - `CompleteDuplicateable.java` - Complete duplication interface
- **Lines of Code**: ~261

## Module Dependencies

### Dependency Graph
```
┌─────────────┐
│   lotrec    │ (root)
│  Launcher   │
└──────┬──────┘
       │
       ▼
┌─────────────┐      ┌─────────────┐
│    gui      │ ───► │   engine    │
│  MainFrame  │      │   Engine    │
└──────┬──────┘      └──────┬──────┘
       │                    │
       │                    ▼
       │             ┌─────────────┐
       │             │   process   │
       │             │  Strategy   │
       │             └──────┬──────┘
       │                    │
       ▼                    ▼
┌─────────────────────────────────────┐
│           dataStructure             │
│  Logic, Tableau, Expression, Graph  │
└────────────────┬────────────────────┘
                 │
                 ▼
┌─────────────┐      ┌─────────────┐
│   parser    │      │    util     │
│ XMLParser   │      │ Duplicateable│
└─────────────┘      └─────────────┘
       │
       ▼
┌─────────────┐
│   logics    │ (XML resources)
└─────────────┘
```

### Dependency Rules
- **Allowed**: Top layers depend on lower layers
- **Forbidden**: Lower layers cannot depend on higher layers
- **Cross-module**: Via interfaces and events

## Code Placement Decision Tree

### New Feature Code Placement Guide

```
Is it a UI component or dialog?
├── Yes → lotrec.gui/ or lotrec.gui.dialogs/
└── No → Is it theorem proving execution logic?
    ├── Yes → lotrec.engine/ or lotrec.process/
    └── No → Is it a data model (Logic, Expression, Graph)?
        ├── Yes → lotrec.dataStructure/
        │   ├── Expression-related → dataStructure.expression/
        │   ├── Graph-related → dataStructure.graph/
        │   └── Tableau-related → dataStructure.tableau/
        └── No → Is it parsing or serialization?
            ├── Yes → lotrec.parser/
            └── No → Is it a general utility?
                ├── Yes → lotrec.util/
                └── No → lotrec/ (root) or create new package
```

### New Tableau Component Placement

```
Is it an action that modifies tableau state?
├── Yes → dataStructure.tableau.action/
│   └── Extend AbstractAction, register in CLASSES_KEYWORDS
└── No → Is it a condition that checks tableau state?
    ├── Yes → dataStructure.tableau.condition/
    │   └── Extend AbstractCondition, register in CLASSES_KEYWORDS
    └── No → Is it a tableau structure component?
        ├── Yes → dataStructure.tableau/
        └── No → Consider if it belongs in process/ or engine/
```

### Code Type Quick Reference

| Code Type | Target Module | Target Path |
|-----------|---------------|-------------|
| Main application class | lotrec | `src/lotrec/` |
| GUI window/panel | gui | `src/lotrec/gui/` |
| Dialog | gui.dialogs | `src/lotrec/gui/dialogs/` |
| Logic editing panel | gui.logicspane | `src/lotrec/gui/logicspane/` |
| Engine component | engine | `src/lotrec/engine/` |
| Strategy/Routine | process | `src/lotrec/process/` |
| Event class | process | `src/lotrec/process/` |
| Logic definition | dataStructure | `src/lotrec/dataStructure/` |
| Expression type | dataStructure.expression | `src/lotrec/dataStructure/expression/` |
| Graph component | dataStructure.graph | `src/lotrec/dataStructure/graph/` |
| Tableau component | dataStructure.tableau | `src/lotrec/dataStructure/tableau/` |
| Tableau action | dataStructure.tableau.action | `src/lotrec/dataStructure/tableau/action/` |
| Tableau condition | dataStructure.tableau.condition | `src/lotrec/dataStructure/tableau/condition/` |
| XML parser | parser | `src/lotrec/parser/` |
| Parser exception | parser.exceptions | `src/lotrec/parser/exceptions/` |
| Utility class | util | `src/lotrec/util/` |
| Predefined logic | logics | `src/lotrec/logics/` (XML file) |
| Test class | test | `test/lotrec/` |
