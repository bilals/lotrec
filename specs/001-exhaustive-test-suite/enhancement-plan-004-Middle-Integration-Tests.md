# Enhancement Plan 004: Middle Layer Integration Tests

## Summary

**The Gap:** The test suite has unit tests for individual actions/conditions and E2E engine tests, but no middle layer testing `Rule.createMachine()` → EventMachine → condition matching → action execution.

**The Solution:** Create 4 new test files in `test/lotrec/integration/`:

| File | Purpose |
|------|---------|
| `RuleTestHelper.java` | Utilities for creating/applying rules in tests |
| `RuleApplicationIntegrationTest.java` | Test complete rule execution (AND rule, variable binding) |
| `ConditionChainIntegrationTest.java` | Test multiple conditions matching together |
| `TableauStateIntegrationTest.java` | Test combined action effects on tableau |

**Key Technical Insight:** Rules are event-driven. Tests must trigger events (add expression → ExpressionEvent) after setting up the EventMachine to activate condition matching.

**Expected Outcome:** 16-22 new integration tests, improved coverage for condition/action packages, no production code changes required.

**IMPLEMENTATION STATUS: COMPLETED** - 31 integration tests created, all passing.

---

## Objective

Add integration tests that verify:
1. Complete rule application (conditions + actions together)
2. Condition matching with actual tableau nodes
3. Action effects on tableau state

This fills the gap between existing unit tests (single actions/conditions) and end-to-end engine tests.

---

## Current State

**Existing Tests:**
- Unit tests: `AddExpressionActionTest`, `ExpressionConditionTest`, etc. - test individual components
- E2E tests: `EngineHeadlessTest` - full proof search with complete logic

**Gap:** No tests for the middle layer where `Rule.createMachine()` creates an `EventMachine` that orchestrates condition matching → action execution.

---

## Test Organization

```
test/lotrec/integration/
├── RuleApplicationIntegrationTest.java   # Core rule application tests
├── ConditionChainIntegrationTest.java    # Multi-condition matching tests
├── TableauStateIntegrationTest.java      # Action effects on state
└── RuleTestHelper.java                   # Test utilities
```

---

## Implementation

### File 1: `test/lotrec/integration/RuleTestHelper.java`

Utility class providing:

```java
// Create rule programmatically
public static Rule createRule(String name,
    List<AbstractCondition> conditions,
    List<AbstractAction> actions)

// Apply rule to tableau using EventMachine workflow
public static boolean applyRule(Rule rule, Tableau tableau, Engine engine)

// Trigger initial expression event to activate rule matching
public static void triggerExpressionEvent(EventMachine em,
    TableauNode node, Expression expr)

// Parse conditions/actions from logic context
public static AbstractCondition parseCondition(String code, Logic logic)
public static AbstractAction parseAction(String code, Logic logic)
```

**Key insight:** Rules are event-driven. Tests must trigger an `ExpressionEvent` or `LinkEvent` to activate condition matching.

### File 2: `test/lotrec/integration/RuleApplicationIntegrationTest.java`

**Test Cases:**

| Test | Setup | Verify |
|------|-------|--------|
| `shouldApplyAndRule` | Node with `and P Q`, create R-AND rule | Node contains `P`, `Q`, mark applied |
| `shouldNotApplyWhenConditionsNotMet` | Node with `P` only, AND rule | No changes to node |
| `shouldApplyMultipleActionsFromOneRule` | Rule with 3 actions | All 3 effects present |
| `shouldBindVariablesAcrossConditionAndAction` | `hasElement N0 ?A`, `add N0 not ?A` | `not ?A` instantiated correctly |

### File 3: `test/lotrec/integration/ConditionChainIntegrationTest.java`

**Test Cases:**

| Test | Conditions | Verify |
|------|------------|--------|
| `shouldMatchTwoConditionsOnSameNode` | `hasElement N0 ?A`, `hasNotElement N0 not ?A` | Rule fires when P but not not-P |
| `shouldMatchConditionsAcrossLinkedNodes` | `hasElement N0 nec ?A`, `isLinked N0 N1 R` | Variables bound across nodes |
| `shouldRejectWhenAnyConditionFails` | Same but missing link | Rule does not fire |
| `shouldHandleNegativeCondition` | `hasNotElement` | Correct rejection |

### File 4: `test/lotrec/integration/TableauStateIntegrationTest.java`

**Test Cases:**

| Test | Action(s) | Verify |
|------|-----------|--------|
| `shouldAddExpressionAndMarkSource` | `add`, `mark` | Expression added, mark present |
| `shouldCreateNewNodeAndLink` | `createNewNode`, `link` | New node exists, edge created |
| `shouldCloseNodeOnFalsum` | `add FALSUM` | Node closed, tableau updated |
| `shouldPropagateToSuccessors` | NEC rule on linked nodes | Expression in successor |

---

## Technical Approach

### Rule Application Flow (for tests)

```java
// 1. Create rule with conditions and actions
Rule rule = new Rule("TestRule", false);
rule.addCondition(new ExpressionCondition(nodeScheme, formula));
rule.addAction(new AddExpressionAction(nodeScheme, newExpr));

// 2. Create EventMachine from rule
EventMachine em = rule.createMachine();
em.setRelatedTableau(tableau);
em.setEngine(testableEngine);

// 3. Trigger initial event (rules are event-driven)
// Option A: Add expression to node (fires ExpressionEvent)
node.add(new MarkedExpression(triggerExpr));

// Option B: Directly process via work()
em.work();

// 4. Verify results
assertThat(node.contains(expectedExpr)).isTrue();
```

### Event Triggering Challenge

Rules activate via events. Two approaches:
1. **Add expression after EventMachine setup** - the add triggers ExpressionEvent
2. **Use TestableEventMachine methods** - manually queue events

---

## Files to Modify

| File | Change |
|------|--------|
| `test/lotrec/integration/` (new directory) | Create 4 new test files |
| `test/lotrec/dataStructure/tableau/TableauTestFixtures.java` | Add `createTableauWithFormula()` helper (optional) |

---

## Dependencies

```
TestFixtures (existing)
TableauTestFixtures (existing)
TestableEventMachine (existing)
TestableEngine (existing)
    │
    └── RuleTestHelper (new)
            │
            ├── RuleApplicationIntegrationTest
            ├── ConditionChainIntegrationTest
            └── TableauStateIntegrationTest
```

---

## Verification

After implementation:

```bash
# Run integration tests only
gradlew test --tests "**/integration/*"

# Run all tests to ensure no regression
gradlew test

# Check coverage improvement
gradlew jacocoTestReport
```

Expected outcome:
- All new tests pass
- Coverage of `lotrec.dataStructure.tableau.condition` and `lotrec.dataStructure.tableau.action` increases
- No changes to production code required

---

## Estimated Test Count

| File | Tests |
|------|-------|
| RuleApplicationIntegrationTest | 6-8 |
| ConditionChainIntegrationTest | 5-7 |
| TableauStateIntegrationTest | 5-7 |
| **Total** | **16-22** |

---

## Implementation Results

### Files Created

```
test/lotrec/integration/
├── RuleTestHelper.java                    [NEW] - 340 lines
├── RuleApplicationIntegrationTest.java    [NEW] - 290 lines
├── ConditionChainIntegrationTest.java     [NEW] - 360 lines
└── TableauStateIntegrationTest.java       [NEW] - 340 lines
```

### Files Modified

```
test/lotrec/engine/
└── TestableEngine.java                    [MODIFIED] - Added shouldPause(), shouldStop(), isRunningBySteps() overrides
```

### Test Summary

| Test Class | Tests | Status |
|------------|-------|--------|
| RuleApplicationIntegrationTest | 9 | All Pass |
| ConditionChainIntegrationTest | 12 | All Pass |
| TableauStateIntegrationTest | 10 | All Pass |
| **Total** | **31** | **All Pass** |

### Key Test Categories

**RuleApplicationIntegrationTest:**
- Simple rule application (condition match → action execute)
- AND rule decomposition with pattern matching
- Variable binding across conditions and actions
- Multiple actions from single rule
- Duplicate expression handling

**ConditionChainIntegrationTest:**
- Same-node multi-condition matching
- Negative conditions (hasNotElement)
- Mark conditions (isNotMarked, isMarked)
- Linked node conditions across edges
- Variable binding behavior with multiple matches

**TableauStateIntegrationTest:**
- Expression addition and state verification
- FALSUM/node closure
- Node creation and linking
- Node and expression marking
- Complex multi-step state changes

### Utility Class: RuleTestHelper

Provides reusable methods for integration testing:
- `createRule()` - Build rules programmatically
- `applyRule()` - Full rule application workflow
- `triggerExistingExpressionEvents()` - Event triggering
- Condition factories: `hasElement()`, `hasNotElement()`, `isLinked()`, `isMarked()`, `isNotMarked()`
- Action factories: `addExpression()`, `mark()`, `markNode()`, `createNewNode()`, `link()`
- Expression helpers: `nodeScheme()`, `exprVar()`, `constant()`, `compound()`
- Assertion helpers: `nodeContains()`, `expressionCount()`, `nodesAreLinked()`

---

*Document created: 2026-01-30*
*Implementation completed: 2026-01-30*
