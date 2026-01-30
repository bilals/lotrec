# LoTREC Developer Skills

## Skill Overview

This skill empowers Claude Code as a **senior development engineer** for the **LoTREC** (Logic Tableaux Research and Educational Companion) project. With this skill, Claude Code will:

- Master all tech stacks and architectural design of this Java-based theorem prover
- Follow project coding conventions and development habits established since 2007
- Prioritize code reuse, avoid reinventing the wheel
- Maintain forward compatibility when extending functionality
- Refactor on-demand, avoiding unnecessary changes

## Key Skills Checklist

### 1. Tech Stack Mastery
- **Primary Language**: Java 1.8
- **Core Frameworks**: Swing GUI, Cytoscape (graph visualization), JTopas (tokenizer)
- **Details**: See [references/tech-stack.md](references/tech-stack.md)

### 2. Architecture Understanding
- **Architecture Style**: Layered Architecture with Event-Driven Processing
- **Layering Structure**: GUI -> Engine -> DataStructure -> Parser
- **Details**: See [references/architecture.md](references/architecture.md)

### 3. Coding Conventions
- **Naming Conventions**: PascalCase (classes), camelCase (methods/variables), UPPER_SNAKE_CASE (constants)
- **Code Style**: Egyptian braces, 4-space indentation, JavaDoc documentation
- **Details**: See [references/coding-conventions.md](references/coding-conventions.md)

### 4. Module Structure
- **Project Type**: Monolith with clear package separation
- **Module Count**: 12 main packages
- **Details**: See [references/module-structure.md](references/module-structure.md)

### 5. Development Patterns
- **Design Patterns**: Factory, Observer, Strategy, Command, Prototype (Duplicateable)
- **Best Practices**: Event-driven architecture, XML-based logic definitions
- **Details**: See [references/development-patterns.md](references/development-patterns.md)

## Brownfield Development Principles

### Principle 1: Respect Existing Architecture
> **Don't try to "improve" existing architecture; understand and follow it accurately**

- New code must be consistent with the layered architecture (GUI -> Engine -> DataStructure -> Parser)
- Use the existing event system (`ProcessEvent`, `ProcessListener`) for state changes
- Follow the Duplicateable pattern for any new data structures that need cloning
- Architecture changes require sufficient justification

### Principle 2: Code Reuse First
> **Before creating new code, search for existing implementations**

Execution order:
1. Search for similar functionality in `lotrec.util` and `lotrec.dataStructure`
2. Check existing Action and Condition implementations in `lotrec.dataStructure.tableau.action` and `lotrec.dataStructure.tableau.condition`
3. Reuse parser utilities from `lotrec.parser`
4. Create new code only after confirming no reusable code exists

### Principle 3: Forward Compatibility
> **New features must not break existing functionality**

- Add rather than modify (Open/Closed Principle)
- Maintain backward compatibility of existing Logic XML format
- New connectors, rules, or strategies should follow existing patterns
- Thorough regression testing with predefined logics

### Principle 4: Refactor On-Demand
> **Only refactor when necessary, avoid over-engineering**

Refactoring triggers:
- Impacts new feature implementation
- Obvious code smells exist
- User explicitly requests

### Principle 5: Compatibility First
> **All generated code must be consistent with existing code style**

- Use the same naming conventions (PascalCase classes, camelCase methods)
- Use the same code format (Egyptian braces, 4-space indent)
- Use the same directory structure (packages mirror functionality)
- Use Vector for collections (legacy but consistent)

## Usage Guide

### When Developing New Features

1. **Read relevant reference files** first to understand project conventions
2. **Search existing implementations** to confirm reusable code (Actions, Conditions, etc.)
3. **Follow module structure** to place code in correct packages
4. **Follow coding conventions** to maintain style consistency
5. **Write tests** using main() method testing pattern

### When Adding New Tableau Actions

1. Create class in `lotrec.dataStructure.tableau.action`
2. Extend `AbstractAction`
3. Register keyword in `AbstractAction.CLASSES_KEYWORDS`
4. Implement `apply()` method following existing patterns
5. Update `LogicXMLParser` if needed

### When Adding New Tableau Conditions

1. Create class in `lotrec.dataStructure.tableau.condition`
2. Extend `AbstractCondition`
3. Register keyword in `AbstractCondition.CLASSES_KEYWORDS`
4. Implement condition evaluation following existing patterns

### When Modifying Existing Code

1. **Understand impact scope**: Confirm which packages are affected
2. **Maintain compatibility**: Ensure existing Logic XML files still work
3. **Test thoroughly**: Run with predefined logics to confirm no regressions

## Reference Files

| File | Description |
|------|-------------|
| [architecture.md](references/architecture.md) | Project architecture and layering |
| [tech-stack.md](references/tech-stack.md) | Tech stack details and constraints |
| [coding-conventions.md](references/coding-conventions.md) | Coding standards and style |
| [module-structure.md](references/module-structure.md) | Module structure and responsibilities |
| [development-patterns.md](references/development-patterns.md) | Development patterns and practices |

---

**Version**: 1.0.0 | **Generated**: 2026-01-27 | **Source**: Brownfield Developer Skills Generator
