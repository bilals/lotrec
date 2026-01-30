# Enhancement-002: Tableau Mock Infrastructure for Action/Condition Execution Testing

## Problem Statement

The LoTREC test suite has significant coverage gaps:
- **Action coverage: 6%** - Actions require `Tableau`/`TableauNode` context to execute
- **Condition coverage: 26%** - Conditions require the same context plus `InstanceSet` bindings

Current tests only verify parsing, not actual execution behavior. This enhancement adds test infrastructure to enable comprehensive action and condition execution testing.

## Design Approach

### Key Insight: Real Objects, Not Mocks

The Tableau classes have **simple constructors** - we don't need complex mocks:
- `Tableau(String name)` - creates empty tableau
- `TableauNode(String name)` - creates empty node
- `TableauEdge(Node begin, Node end, Expression relation)` - creates edge
- `MarkedExpression(Expression expr)` - wraps expression

We create **real instances** with **test fixture helpers** instead of mocks.

### Action Execution Pattern
```java
public Object apply(EventMachine em, Object modifier) {
    InstanceSet instanceSet = (InstanceSet) modifier;
    TableauNode n = (TableauNode) instanceSet.get(nodeScheme);
    // Modify tableau state
    return instanceSet;
}
```

### Condition Evaluation Pattern
```java
public void attemptToApply(Action action, Object modifier,
                           ActionStocking actionStocking, EventMachine em) {
    InstanceSet instanceSet = (InstanceSet) modifier;
    TableauNode n = (TableauNode) instanceSet.get(nodeScheme);
    // Check condition, enrich instanceSet, continue chain
    continueAttemptToApply(action, newInstanceSet, actionStocking, em);
}
```

---

## Implementation Plan

### Phase 1: Test Infrastructure

#### 1.1 Create TableauTestFixtures.java
**Location:** `test/lotrec/dataStructure/tableau/TableauTestFixtures.java`

Factory methods:
- `createTableau(String name)` - Tableau with Wallet
- `createNodeWithExpression(Tableau, Expression)` - Node with formula
- `createLinkedNodes(Tableau, Expression relation)` - Two linked nodes
- `createInstanceSetWith(SchemeBinding...)` - InstanceSet with bindings
- `nodeBinding(String name, TableauNode)` - Helper for bindings
- `exprBinding(String name, Expression)` - Helper for bindings
- `nodeScheme(String name)` - Creates StringSchemeVariable
- `exprScheme(String name)` - Creates VariableExpression
- `constant(String name)` - Creates ConstantExpression

#### 1.2 Create TestableEventMachine.java
**Location:** `test/lotrec/process/TestableEventMachine.java`

Minimal EventMachine subclass for unit testing:
- `getWorkerName()` - Returns configurable rule name
- `getRelatedTableau()` - Returns test tableau
- `getEngine()` - Returns test engine
- `isApplyOnOneOccurence()` - Controls matching behavior

Factory methods:
- `forTableau(Tableau)` - Quick setup
- `configured(Tableau, Engine)` - Full setup

#### 1.3 Create TestableEngine.java
**Location:** `test/lotrec/engine/TestableEngine.java`

Minimal Engine for actions that need engine interaction:
- Captures `stopTableau()`, `add()`, `remove()` calls
- Provides verification methods for tests

---

### Phase 2: Action Execution Tests (21 actions)

#### 2.1 Core Actions (High Priority)
| Test File | Actions Tested |
|-----------|---------------|
| `AddExpressionActionTest.java` | Add expression, skip duplicate, FALSUM closes node |
| `AddNodeActionTest.java` | Create node, bind to InstanceSet |
| `LinkActionTest.java` | Create edge between nodes |
| `MarkActionTest.java` | Add mark to node |

#### 2.2 Remaining Actions
| Test File | Actions Tested |
|-----------|---------------|
| `AddOneNodeActionTest.java` | Create single node |
| `AddOneSuccessorActionTest.java` | Create/find successor node |
| `AddOneParentActionTest.java` | Create/find parent node |
| `UnlinkActionTest.java` | Remove edge |
| `LinkIfNotExistActionTest.java` | Conditional link |
| `UnmarkActionTest.java` | Remove mark |
| `MarkExpressionsActionTest.java` | Bulk mark formulas |
| `UnmarkExpressionsActionTest.java` | Bulk unmark formulas |
| `HideActionTest.java` | Hide expression |
| `StopStrategyActionTest.java` | Stop proof search |
| `KillActionTest.java` | Remove tableau |
| `DuplicateActionTest.java` | Clone tableau |
| `BindActionTest.java` | Bind variable |
| `CreateNewConstantActionTest.java` | Create constant |
| `MergeNodeInNodeActionTest.java` | Merge nodes |
| `OracleActionTest.java` | Oracle call |
| `ProcessActionTest.java` | Custom processing |

---

### Phase 3: Condition Evaluation Tests

#### 3.1 Core Conditions (High Priority)
| Test File | Conditions Tested |
|-----------|------------------|
| `ExpressionConditionTest.java` | Match formula in node |
| `NotExpressionConditionTest.java` | No matching formula |
| `LinkConditionTest.java` | Edge exists between nodes |
| `NotLinkConditionTest.java` | No edge exists |

#### 3.2 Mark Conditions
| Test File | Conditions Tested |
|-----------|------------------|
| `MarkConditionTest.java` | Node has mark |
| `NotMarkConditionTest.java` | Node lacks mark |
| `MarkExpressionConditionTest.java` | Expression has mark |
| `NotMarkExpressionConditionTest.java` | Expression lacks mark |

#### 3.3 Structure Conditions
| Test File | Conditions Tested |
|-----------|------------------|
| `IsAtomicConditionTest.java` | Expression is atomic |
| `IsNotAtomicConditionTest.java` | Expression is composite |
| `IdenticalConditionTest.java` | Objects are same |
| `NotIdenticalConditionTest.java` | Objects differ |
| `HasNoSuccessorConditionTest.java` | Node has no children |
| `HasNoParentsConditionTest.java` | Node has no parents |
| `AncestorConditionTest.java` | Node ancestry |
| `ContainsConditionTest.java` | Expression containment |

---

### Phase 4: Integration Tests

Create tests that combine conditions and actions:
- Rule application with single condition/action
- Multi-condition rule matching
- Action chaining effects

---

## Files to Create

### Test Infrastructure (Phase 1)
```
test/lotrec/dataStructure/tableau/TableauTestFixtures.java
test/lotrec/process/TestableEventMachine.java
test/lotrec/engine/TestableEngine.java
```

### Action Tests (Phase 2)
```
test/lotrec/dataStructure/tableau/action/AddExpressionActionTest.java
test/lotrec/dataStructure/tableau/action/AddNodeActionTest.java
test/lotrec/dataStructure/tableau/action/LinkActionTest.java
test/lotrec/dataStructure/tableau/action/MarkActionTest.java
test/lotrec/dataStructure/tableau/action/UnlinkActionTest.java
test/lotrec/dataStructure/tableau/action/UnmarkActionTest.java
test/lotrec/dataStructure/tableau/action/MarkExpressionsActionTest.java
test/lotrec/dataStructure/tableau/action/UnmarkExpressionsActionTest.java
test/lotrec/dataStructure/tableau/action/HideActionTest.java
test/lotrec/dataStructure/tableau/action/StopStrategyActionTest.java
test/lotrec/dataStructure/tableau/action/KillActionTest.java
test/lotrec/dataStructure/tableau/action/DuplicateActionTest.java
test/lotrec/dataStructure/tableau/action/AddOneNodeActionTest.java
test/lotrec/dataStructure/tableau/action/AddOneSuccessorActionTest.java
test/lotrec/dataStructure/tableau/action/AddOneParentActionTest.java
test/lotrec/dataStructure/tableau/action/LinkIfNotExistActionTest.java
test/lotrec/dataStructure/tableau/action/BindActionTest.java
test/lotrec/dataStructure/tableau/action/CreateNewConstantActionTest.java
test/lotrec/dataStructure/tableau/action/MergeNodeInNodeActionTest.java
```

### Condition Tests (Phase 3)
```
test/lotrec/dataStructure/tableau/condition/ExpressionConditionTest.java
test/lotrec/dataStructure/tableau/condition/NotExpressionConditionTest.java
test/lotrec/dataStructure/tableau/condition/LinkConditionTest.java
test/lotrec/dataStructure/tableau/condition/NotLinkConditionTest.java
test/lotrec/dataStructure/tableau/condition/MarkConditionTest.java
test/lotrec/dataStructure/tableau/condition/NotMarkConditionTest.java
test/lotrec/dataStructure/tableau/condition/MarkExpressionConditionTest.java
test/lotrec/dataStructure/tableau/condition/NotMarkExpressionConditionTest.java
test/lotrec/dataStructure/tableau/condition/IsAtomicConditionTest.java
test/lotrec/dataStructure/tableau/condition/IdenticalConditionTest.java
test/lotrec/dataStructure/tableau/condition/NotIdenticalConditionTest.java
test/lotrec/dataStructure/tableau/condition/HasNoSuccessorConditionTest.java
test/lotrec/dataStructure/tableau/condition/AncestorConditionTest.java
test/lotrec/dataStructure/tableau/condition/ContainsConditionTest.java
```

---

## Critical Source Files Referenced

| Purpose | File |
|---------|------|
| Action base class | `src/lotrec/process/AbstractAction.java` |
| Example action | `src/lotrec/dataStructure/tableau/action/AddExpressionAction.java` |
| Condition match | `src/lotrec/dataStructure/tableau/condition/ExpressionMatch.java` |
| Action queue | `src/lotrec/process/ActionStocking.java` |
| Tableau structure | `src/lotrec/dataStructure/tableau/Tableau.java` |
| Tableau nodes | `src/lotrec/dataStructure/tableau/TableauNode.java` |
| Instance bindings | `src/lotrec/dataStructure/expression/InstanceSet.java` |
| Existing fixtures | `test/lotrec/TestFixtures.java` |

---

## Expected Coverage Improvements

| Package | Current | Target |
|---------|---------|--------|
| `lotrec.dataStructure.tableau.action` | 6% | 60%+ |
| `lotrec.dataStructure.tableau.condition` | 26% | 60%+ |

---

## Verification

After implementation, verify with:
```bash
# Run all tests
gradlew.bat test

# Generate coverage report
gradlew.bat jacocoTestReport

# View report at: build/reports/jacoco/test/html/index.html
```

---

## Test Pattern Examples

### Action Test Pattern
```java
@Test
void shouldAddExpressionToNode() {
    // Arrange
    Tableau tableau = TableauTestFixtures.createTableau("test");
    TableauNode node = TableauTestFixtures.createNodeWithExpression(
        tableau, TableauTestFixtures.constant("P"));

    AddExpressionAction action = new AddExpressionAction(
        TableauTestFixtures.nodeScheme("n"),
        TableauTestFixtures.constant("Q"));

    InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
        TableauTestFixtures.nodeBinding("n", node));

    TestableEventMachine em = TestableEventMachine.forTableau(tableau);

    // Act
    action.apply(em, instanceSet);

    // Assert
    assertThat(node.contains(TableauTestFixtures.constant("Q"))).isTrue();
}
```

### Condition Test Pattern
```java
@Test
void shouldMatchWhenNodeContainsExpression() {
    // Arrange
    Tableau tableau = TableauTestFixtures.createTableau("test");
    Expression pattern = TableauTestFixtures.constant("P");
    TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, pattern);

    ExpressionCondition condition = new ExpressionCondition(
        TableauTestFixtures.nodeScheme("n"), pattern);
    Restriction restriction = condition.createRestriction();

    InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
        TableauTestFixtures.nodeBinding("n", node));

    TestableEventMachine em = TestableEventMachine.forTableau(tableau);
    ActionStocking stocking = new ActionStocking();

    // Act
    restriction.attemptToApply(new ActionContainer(), instanceSet, stocking, em);

    // Assert
    assertThat(stocking.isEmpty()).isFalse();
}
```

---

## Status

- **Created:** 2026-01-30
- **Completed:** 2026-01-30
- **Status:** COMPLETED
- **Related:** FEAT-001 Exhaustive Test Suite, Enhancement-001 Headless Engine

---

## Implementation Results

### Coverage Achieved

| Package | Before | After | Improvement |
|---------|--------|-------|-------------|
| `lotrec.dataStructure.tableau.action` | 6% | **42%** | +36% |
| `lotrec.dataStructure.tableau.condition` | 26% | **40%** | +14% |

### Files Created

#### Test Infrastructure (Phase 1)
| File | Purpose |
|------|---------|
| `test/lotrec/dataStructure/tableau/TableauTestFixtures.java` | Factory methods for test objects |
| `test/lotrec/process/TestableEventMachine.java` | Factory for configured EventMachines |
| `test/lotrec/engine/TestableEngine.java` | Engine subclass that captures method calls |

#### Action Tests (Phase 2) - 9 files, 59 tests
| File | Tests |
|------|-------|
| `AddExpressionActionTest.java` | 8 |
| `AddNodeActionTest.java` | 7 |
| `LinkActionTest.java` | 9 |
| `MarkActionTest.java` | 9 |
| `UnlinkActionTest.java` | 7 |
| `UnmarkActionTest.java` | 7 |
| `HideActionTest.java` | 7 |
| `MarkExpressionsActionTest.java` | 7 |
| `StopStrategyActionTest.java` | 5 |

#### Condition Tests (Phase 3) - 5 files, 44 tests
| File | Tests |
|------|-------|
| `ExpressionConditionTest.java` | 10 |
| `LinkConditionTest.java` | 9 |
| `MarkConditionTest.java` | 9 |
| `NotExpressionConditionTest.java` | 9 |
| `NotMarkConditionTest.java` | 7 |

### Key Implementation Details

#### EventMachine is Final
The original plan proposed `TestableEventMachine extends EventMachine`, but `EventMachine` is a **final class**. The solution was to convert `TestableEventMachine` into a **factory class** with static methods:

```java
public final class TestableEventMachine {
    private TestableEventMachine() {} // Factory class only

    public static EventMachine forTableau(Tableau tableau) {
        EventMachine em = new EventMachine("TestRule");
        em.setRelatedTableau(tableau);
        return em;
    }
}
```

#### TableauTestFixtures Pattern
The fixtures use a `SchemeBinding` record for type-safe bindings:

```java
public record SchemeBinding(Object scheme, Object value) {}

public static InstanceSet createInstanceSetWith(SchemeBinding... bindings) {
    InstanceSet instanceSet = new InstanceSet();
    for (SchemeBinding binding : bindings) {
        instanceSet.put(binding.scheme(), binding.value());
    }
    return instanceSet;
}
```

### Remaining Work (Phase 4+)

The following tests are planned for future iterations:
- More action tests (AddOneNode, AddOneSuccessor, Kill, Duplicate, etc.)
- More condition tests (NotLink, MarkExpression, IsAtomic, Ancestor, etc.)
- Integration tests combining conditions and actions
