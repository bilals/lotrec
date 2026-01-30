# LoTREC Architecture

## Architecture Style

**Primary Architecture Pattern**: Layered Architecture with Event-Driven Processing

**Architecture Diagram**:
```
┌─────────────────────────────────────────────────────────────────┐
│                         GUI LAYER                                │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │  MainFrame  │  │  Dialogs    │  │  LogicsPane Components  │  │
│  └──────┬──────┘  └──────┬──────┘  └────────────┬────────────┘  │
└─────────┼────────────────┼─────────────────────┼────────────────┘
          │                │                     │
          ▼                ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                       ENGINE LAYER                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Engine    │  │  Strategy   │  │     EventMachine        │  │
│  │  (Thread)   │  │  Execution  │  │   (Event Processing)    │  │
│  └──────┬──────┘  └──────┬──────┘  └────────────┬────────────┘  │
└─────────┼────────────────┼─────────────────────┼────────────────┘
          │                │                     │
          ▼                ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                   DATA STRUCTURE LAYER                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────────┐  │
│  │   Logic     │  │   Tableau   │  │   Graph / Node / Edge   │  │
│  │ Definition  │  │   Rules     │  │      Wallet             │  │
│  └──────┬──────┘  └──────┬──────┘  └────────────┬────────────┘  │
│         │                │                      │               │
│  ┌──────┴──────────────────────────────────────┴──────────────┐ │
│  │              Expression System                              │ │
│  │  Expression, Connector, VariableExpression, InstanceSet     │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
          │                │                     │
          ▼                ▼                     ▼
┌─────────────────────────────────────────────────────────────────┐
│                       PARSER LAYER                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  │
│  │ LogicXMLParser  │  │ OldiesTokenizer │  │    Verifier     │  │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────┐
│                    EXTERNAL RESOURCES                            │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │        Logic XML Files (PredefinedLogics/*.xml)             │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Layer Structure

### GUI Layer
- **Responsibility**: User interface, visualization, user interaction
- **Location**: `src/lotrec/gui/`
- **Typical Components**:
  - `MainFrame.java` - Main application window
  - `dialogs/` - Various dialog windows
  - `logicspane/` - Logic definition and editing panels
  - `GraphvizDisplayer.java` - External graph rendering
- **Lines of Code**: ~14,608

### Engine Layer
- **Responsibility**: Theorem proving execution, strategy management, event processing
- **Location**: `src/lotrec/engine/`, `src/lotrec/process/`
- **Typical Components**:
  - `Engine.java` - Main execution thread, manages proof state
  - `Strategy.java`, `Routine.java`, `AllRules.java` - Strategy pattern implementations
  - `EventMachine.java` - Event queue processing
  - `AbstractWorker.java` - Base worker class
  - `Benchmarker.java`, `EngineTimer.java` - Performance monitoring
- **Lines of Code**: ~2,992

### Data Structure Layer
- **Responsibility**: Domain models, logical expressions, tableaux representation
- **Location**: `src/lotrec/dataStructure/`
- **Typical Components**:
  - `Logic.java` - Logic definition container
  - `tableau/` - Tableau nodes, edges, rules, actions, conditions
  - `graph/` - Graph structures (Graph, Node, Edge, Wallet)
  - `expression/` - Expression system (Expression, Connector, InstanceSet)
- **Lines of Code**: ~10,503

### Parser Layer
- **Responsibility**: XML parsing, formula tokenization, validation
- **Location**: `src/lotrec/parser/`
- **Typical Components**:
  - `LogicXMLParser.java` - Logic XML file parser/serializer
  - `OldiesTokenizer.java` - Formula expression tokenizer
  - `Verifier.java` - Logic definition validator
  - `exceptions/` - Parser-specific exceptions
- **Lines of Code**: ~2,858

### Utility Layer
- **Responsibility**: Cross-cutting concerns, helpers
- **Location**: `src/lotrec/util/`, `src/lotrec/bind/`, `src/lotrec/dom/`
- **Typical Components**:
  - `Duplicateable.java`, `Duplicator.java` - Object cloning framework
  - `BasicBinder.java` - Variable binding
  - `wrappers/` - DOM element wrappers

## Dependency Rules

### Allowed Dependencies
- GUI Layer -> Engine Layer -> Data Structure Layer -> Parser Layer
- All Layers -> Utility Layer
- Parser Layer -> External Resources (XML files)

### Forbidden Dependencies
- Parser Layer -> GUI Layer (no UI dependencies in parsing)
- Data Structure Layer -> GUI Layer (pure data models)
- Engine Layer -> GUI Layer (except for callbacks/notifications)
- Circular dependencies between packages

### Cross-Layer Communication
- GUI to Engine: Direct method calls on Engine instance
- Engine to GUI: Callback via `MainFrame` reference (passed in constructor)
- Data changes: Event-driven via `ProcessEvent`/`ProcessListener` system

## Key Architecture Decisions

| Decision | Choice | Rationale |
|----------|--------|-----------|
| UI Framework | Swing | Standard Java desktop GUI, widely supported |
| Graph Visualization | Cytoscape | Powerful network visualization library |
| Logic Definition | XML Files | Human-readable, easily editable, portable |
| Expression Parsing | Custom Tokenizer | Tailored for logical formula syntax |
| Execution Model | Threaded Engine | Non-blocking UI during proof execution |
| Object Cloning | Custom Duplicateable | Fine-grained control over deep copy |
| Event System | Custom Events | Decoupled components, extensible |

## Extension Guidelines

### Adding New Features
1. Identify the appropriate layer for the feature
2. Follow existing patterns in that layer
3. Use the event system for cross-layer communication
4. Update GUI components if user interaction is needed
5. Update XML parser if persistence is required

### Adding New Modules
1. Create new package under `src/lotrec/`
2. Follow existing package naming convention
3. Define clear interfaces for cross-module communication
4. Document dependencies in package-info or README
5. Avoid creating circular dependencies

### Adding New Logic Features
1. Define new Connector types in `dataStructure.expression`
2. Create new Action classes in `dataStructure.tableau.action`
3. Create new Condition classes in `dataStructure.tableau.condition`
4. Register keywords in factory HashMaps
5. Update `LogicXMLParser` for XML serialization
6. Add predefined logic XML files as examples
