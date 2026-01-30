# Defining Custom Logics

This guide explains how to create your own logic definitions in LoTREC.

## Overview

A logic in LoTREC consists of:

1. **Connectors** - The logical operators (and, or, not, nec, pos, etc.)
2. **Rules** - Inference rules that decompose formulas
3. **Strategies** - The order in which rules are applied
4. **Testing Formulas** - Sample formulas for verification

## Creating a New Logic

### Using the GUI

1. Go to **Logic → New Logic**
2. Add connectors in the **Connectors** tab
3. Define rules in the **Rules** tab
4. Create strategies in the **Strategies** tab
5. Add test formulas in the **Predefined Formulas** tab
6. Save with **Logic → Save Logic As**

### Using XML

Logic files are XML documents following the `logic.dtd` schema.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE logic SYSTEM "logic.dtd">
<logic>
    <displayName>My Logic</displayName>
    <description>Description of my logic</description>

    <!-- Connectors -->
    <connector>...</connector>

    <!-- Rules -->
    <rule>...</rule>

    <!-- Strategies -->
    <strategy>...</strategy>
    <main-strategy>StrategyName</main-strategy>

    <!-- Test formulas -->
    <testing-formula>...</testing-formula>
</logic>
```

## Defining Connectors

Connectors define the syntax of your logic.

### XML Structure

```xml
<connector>
    <connector-name>and</connector-name>
    <arity>2</arity>
    <output-format>(_ & _)</output-format>
    <priority>3</priority>
    <associative>true</associative>
</connector>
```

### Fields

| Field | Description | Example |
|-------|-------------|---------|
| `connector-name` | Internal name used in formulas | `and`, `nec`, `box` |
| `arity` | Number of arguments | `1` (unary), `2` (binary) |
| `output-format` | Display format (`_` = argument placeholder) | `~_`, `(_ & _)` |
| `priority` | Parsing precedence (higher binds tighter) | `1` to `10` |
| `associative` | Whether operator is associative | `true`, `false` |

### Common Connectors

| Name | Arity | Format | Priority | Meaning |
|------|-------|--------|----------|---------|
| `not` | 1 | `~_` | 5 | Negation |
| `and` | 2 | `(_ & _)` | 3 | Conjunction |
| `or` | 2 | `(_ \| _)` | 2 | Disjunction |
| `imp` | 2 | `(_ -> _)` | 1 | Implication |
| `nec` | 1 | `[]_` | 5 | Necessity (box) |
| `pos` | 1 | `<>_` | 5 | Possibility (diamond) |

## Defining Rules

Rules specify how formulas are decomposed in the tableau.

### XML Structure

```xml
<rule>
    <rule-name>AndRule</rule-name>
    <condition>
        <condition-name>hasElement</condition-name>
        <condition-param>N0</condition-param>
        <condition-param>and A B</condition-param>
    </condition>
    <action>
        <action-name>add</action-name>
        <action-param>N0</action-param>
        <action-param>A</action-param>
    </action>
    <action>
        <action-name>add</action-name>
        <action-param>N0</action-param>
        <action-param>B</action-param>
    </action>
</rule>
```

### Pattern Variables

Use capital letters as pattern variables that match any formula:
- `A`, `B`, `C` - Match any formula
- `N0`, `N1`, `N2` - Match nodes
- `R` - Match relations

### Available Conditions

| Condition | Parameters | Description |
|-----------|------------|-------------|
| `hasElement` | node, formula | Node contains formula |
| `hasNotElement` | node, formula | Node doesn't contain formula |
| `isAtomic` | formula | Formula is atomic |
| `isNotAtomic` | formula | Formula is not atomic |
| `isLinked` | node1, node2, relation | Nodes are linked by relation |
| `isNotLinked` | node1, node2, relation | Nodes are not linked |
| `hasNoSuccessor` | node, relation | Node has no R-successors |
| `hasNoParents` | node | Node has no parents |
| `isAncestor` | node1, node2 | node1 is ancestor of node2 |
| `areIdentical` | node1, node2 | Nodes are the same |
| `areNotIdentical` | node1, node2 | Nodes are different |
| `contains` | formula1, formula2 | formula1 contains formula2 |
| `isMarked` | node, mark | Node has mark |
| `isNotMarked` | node, mark | Node doesn't have mark |
| `isMarkedExpression` | formula, mark | Formula has mark |
| `isNotMarkedExpression` | formula, mark | Formula doesn't have mark |

### Available Actions

| Action | Parameters | Description |
|--------|------------|-------------|
| `add` | node, formula | Add formula to node |
| `createNewNode` | varName | Create new node, bind to variable |
| `link` | node1, node2, relation | Create link between nodes |
| `unlink` | node1, node2, relation | Remove link between nodes |
| `stop` | message | Stop with "closed" (contradiction found) |
| `mark` | node, mark | Add mark to node |
| `unmark` | node, mark | Remove mark from node |
| `markExpressions` | formula, mark | Add mark to formula |
| `unmarkExpressions` | formula, mark | Remove mark from formula |
| `createOneSuccessor` | node, relation, newNode | Create one successor |
| `createOneParent` | node, relation, newNode | Create one parent |
| `hide` | node | Hide node from display |
| `kill` | node | Remove node completely |
| `duplicate` | node | Duplicate node (branching) |
| `merge` | node1, node2 | Merge two nodes |

## Defining Strategies

Strategies control rule application order.

### XML Structure

```xml
<strategy>
    <strategy-name>Main</strategy-name>
    <strategy-code>
        repeat
            firstRule
                PropositionalRules
                ModalRules
            end
        end
    </strategy-code>
</strategy>
<main-strategy>Main</main-strategy>
```

### Strategy Constructs

| Construct | Behavior |
|-----------|----------|
| `repeat ... end` | Loop until no rule in block applies |
| `firstRule ... end` | Apply first matching rule, then exit block |
| `allRules ... end` | Apply all matching rules before continuing |

### Nesting Strategies

Strategies can reference other strategies:

```xml
<strategy>
    <strategy-name>PropositionalRules</strategy-name>
    <strategy-code>firstRule AndRule OrRule NotRule end</strategy-code>
</strategy>

<strategy>
    <strategy-name>ModalRules</strategy-name>
    <strategy-code>firstRule NecRule PosRule end</strategy-code>
</strategy>

<strategy>
    <strategy-name>Main</strategy-name>
    <strategy-code>
        repeat
            PropositionalRules
            ModalRules
        end
    </strategy-code>
</strategy>
```

## Complete Example: Modal Logic K

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE logic SYSTEM "logic.dtd">
<logic>
    <displayName>Modal Logic K</displayName>
    <description>Basic modal logic K with necessity and possibility</description>

    <!-- Connectors -->
    <connector>
        <connector-name>not</connector-name>
        <arity>1</arity>
        <output-format>~_</output-format>
        <priority>5</priority>
    </connector>

    <connector>
        <connector-name>and</connector-name>
        <arity>2</arity>
        <output-format>(_ & _)</output-format>
        <priority>3</priority>
        <associative>true</associative>
    </connector>

    <connector>
        <connector-name>or</connector-name>
        <arity>2</arity>
        <output-format>(_ | _)</output-format>
        <priority>2</priority>
        <associative>true</associative>
    </connector>

    <connector>
        <connector-name>nec</connector-name>
        <arity>1</arity>
        <output-format>[]_</output-format>
        <priority>5</priority>
    </connector>

    <connector>
        <connector-name>pos</connector-name>
        <arity>1</arity>
        <output-format>&lt;&gt;_</output-format>
        <priority>5</priority>
    </connector>

    <!-- Rules -->
    <rule>
        <rule-name>AndRule</rule-name>
        <condition>
            <condition-name>hasElement</condition-name>
            <condition-param>N0</condition-param>
            <condition-param>and A B</condition-param>
        </condition>
        <condition>
            <condition-name>isNotMarkedExpression</condition-name>
            <condition-param>and A B</condition-param>
            <condition-param>treated</condition-param>
        </condition>
        <action>
            <action-name>add</action-name>
            <action-param>N0</action-param>
            <action-param>A</action-param>
        </action>
        <action>
            <action-name>add</action-name>
            <action-param>N0</action-param>
            <action-param>B</action-param>
        </action>
        <action>
            <action-name>markExpressions</action-name>
            <action-param>and A B</action-param>
            <action-param>treated</action-param>
        </action>
    </rule>

    <rule>
        <rule-name>ClashRule</rule-name>
        <condition>
            <condition-name>hasElement</condition-name>
            <condition-param>N0</condition-param>
            <condition-param>A</condition-param>
        </condition>
        <condition>
            <condition-name>hasElement</condition-name>
            <condition-param>N0</condition-param>
            <condition-param>not A</condition-param>
        </condition>
        <condition>
            <condition-name>isAtomic</condition-name>
            <condition-param>A</condition-param>
        </condition>
        <action>
            <action-name>stop</action-name>
            <action-param>closed</action-param>
        </action>
    </rule>

    <!-- Strategy -->
    <strategy>
        <strategy-name>Main</strategy-name>
        <strategy-code>
            repeat
                firstRule
                    ClashRule
                    AndRule
                    OrRule
                    NecRule
                    PosRule
                end
            end
        </strategy-code>
    </strategy>
    <main-strategy>Main</main-strategy>

    <!-- Test formulas -->
    <testing-formula>
        <formula-name>K axiom</formula-name>
        <formula-code>imp nec imp P Q imp nec P nec Q</formula-code>
    </testing-formula>
</logic>
```

## Tips for Logic Design

### 1. Order Rules Carefully

Place rules that detect contradictions (clash rules) first in your strategy.

### 2. Mark Treated Formulas

Use `markExpressions` to avoid applying the same rule repeatedly:

```xml
<condition>
    <condition-name>isNotMarkedExpression</condition-name>
    <condition-param>and A B</condition-param>
    <condition-param>treated</condition-param>
</condition>
```

### 3. Test Incrementally

Add rules one at a time and test with simple formulas before adding complexity.

### 4. Use Predefined Logics as Templates

Study the 38 predefined logics in `src/lotrec/logics/` for patterns and best practices.

### 5. Handle Branching Properly

For rules that require branching (like disjunction), use `duplicate`:

```xml
<action>
    <action-name>duplicate</action-name>
    <action-param>N0</action-param>
</action>
```

## Further Reading

- [Predefined Logics](predefined-logics.md) - Study existing logic definitions
- [User Guide](user-guide.md) - GUI reference for the logic editor
- `src/lotrec/logics/logic.dtd` - Complete XML schema reference
