# LoTREC Project Overview

> **Reference document for AI agents working on this codebase**

## What is LoTREC?

**LoTREC** (Logic Tableau Reasoner for Education and Computational use) is an automated theorem prover for modal and description logics written in Java.

## Purpose

- **Educational**: Teach logic concepts through interactive visualization
- **Research**: Test new/exotic logics and their properties
- **Verification**: Check if logical formulas are satisfiable or valid
- **Model Checking**: Verify properties of models against logical specifications

## Key Features

- Define custom logics with Kripke semantics via XML
- Automatic proof search using the tableau method
- Interactive step-by-step proof exploration with breakpoints
- Graph visualization of proof trees (using Cytoscape)
- Export proofs to PDF, PNG, PostScript
- 38+ pre-defined logics included

## Technologies

| Component | Technology |
|-----------|------------|
| Language | Java |
| GUI Framework | Swing (AWT/Swing) |
| Build System | Ant (NetBeans project) |
| Graph Visualization | Cytoscape |
| Export Formats | PDF, PNG, PostScript (via FreeHEP) |
| Configuration | XML-based logic definitions |
| Testing | JUnit |

## Project Structure

```
lotrec/
├── src/lotrec/
│   ├── bind/                    # Variable binding
│   ├── dataStructure/           # Core data structures
│   │   ├── expression/          # Logical formulas
│   │   ├── graph/               # Graph representation
│   │   └── tableau/             # Proof tableaux
│   │       ├── action/          # Tableau actions
│   │       └── condition/       # Rule conditions
│   ├── dom/                     # XML DOM handling
│   ├── engine/                  # Proof search engine
│   ├── gui/                     # User interface
│   │   └── dialogs/             # File and option dialogs
│   ├── logics/                  # 38+ predefined logic definitions (XML)
│   ├── parser/                  # XML and expression parsing
│   ├── process/                 # Rule execution strategies
│   ├── resources/               # UI properties, images
│   └── util/                    # Utility classes
├── test/                        # Unit tests
├── lib/                         # Dependencies (Cytoscape, FreeHEP, etc.)
├── build.xml                    # Ant build configuration
└── README.md                    # Original project documentation
```

## Core Components

### 1. Data Structures (`src/lotrec/dataStructure/`)

- **Logic**: Represents a logical system with connectors, rules, and strategies
- **Tableau**: Represents the proof structure with nodes and edges
- **Expression**: Represents logical formulas (marked expressions, connectors, variables)
- **Graph**: Underlying graph structure with event-driven updates

### 2. Reasoning Engine (`src/lotrec/engine/`)

- **Engine**: Main proof-search orchestrator (extends Thread)
  - Manages formula decomposition
  - Applies inference rules
  - Supports step-by-step execution and breakpoints

### 3. Rule Processing (`src/lotrec/process/`)

- **Strategy**: Defines proof search strategy
- **EventMachine**: Manages rule condition checking and action execution
- **Worker/Activator**: Different execution models for rules
- 52+ Java classes for complex proof search mechanisms

### 4. Pre-defined Logics (`src/lotrec/logics/`)

| Category | Logics |
|----------|--------|
| Classical Modal | K, KT, K4, KD, S4, S5, KD45 |
| Hybrid | H@ |
| Intuitionistic | Lj |
| Temporal | LTL |
| Program | PDL |
| Epistemic | S5-PAL |

### 5. Parser (`src/lotrec/parser/`)

- **LogicXMLParser**: Parses and saves logic definitions from XML
- **GraphXMLParser**: Handles tableau/proof graph persistence
- **StringTokenizer**: Expression tokenization and parsing

### 6. GUI (`src/lotrec/gui/`)

- **MainFrame**: Primary application window
- **CyTableauDisplayer**: Graph visualization using Cytoscape
- **ControlsPanel**: User controls for tableau operations
- Export dialogs for PDF, PNG, PS

## How It Works

1. User loads or defines a logic (connectors, rules, strategies)
2. User enters a formula to analyze
3. Engine constructs a tableau by:
   - Decomposing the formula using inference rules
   - Building proof trees through systematic expansion
   - Marking nodes as satisfied/unsatisfied
   - Creating branches for disjunctions
4. Visualization displays the proof tree interactively
5. Results indicate satisfiability/validity

## Statistics

- **Total Java Classes**: 232
- **Pre-defined Logics**: 38+
- **Main Dependencies**: Cytoscape, FreeHEP, JAXB, Xerces, Commons CLI

## Key Entry Points

- `src/lotrec/gui/MainFrame.java` - Main application window
- `src/lotrec/engine/Engine.java` - Proof search engine
- `src/lotrec/dataStructure/Logic.java` - Logic definition
- `src/lotrec/dataStructure/tableau/Tableau.java` - Tableau structure
- `build.xml` - Ant build configuration

---

*This document was generated for AI agent reference. Last updated: 2026-01-23*
