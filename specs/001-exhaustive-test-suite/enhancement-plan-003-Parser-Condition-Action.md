# Enhancement Plan 003: Implement Missing Parser Methods for Conditions and Actions

> Add OldiesTokenizer parsing support for 6 conditions and 3 actions that are registered but not parseable.

## Issue Summary

The LoTREC project has a **two-level parsing system**:

1. **LogicXMLParser** (`src/lotrec/parser/LogicXMLParser.java`)
   - Uses reflection with `CLASSES_KEYWORDS` registries to instantiate conditions/actions
   - Works for ALL registered items dynamically
   - Used when loading logic definitions from XML files

2. **OldiesTokenizer** (`src/lotrec/parser/OldiesTokenizer.java`)
   - Uses hardcoded if-else chains to parse string-based rule definitions
   - Requires explicit code for each condition/action
   - Used for parsing strategy code and rule definitions from strings

### The Problem

**6 conditions and 3 actions** are properly registered in the `CLASSES_KEYWORDS` maps but are NOT implemented in `OldiesTokenizer.parseCondition()` and `OldiesTokenizer.parseAction()` methods.

This causes `ParseException` when attempting to parse string-based rule definitions containing these keywords.

---

## Missing Components

### Conditions (6 total)

| Keyword | Class | Constructor Parameters | @ParametersTypes |
|---------|-------|----------------------|------------------|
| `isAtomic` | `IsAtomicCondition` | `(Expression formula)` | `{"formula"}` |
| `isNotAtomic` | `IsNotAtomicCondition` | `(Expression formula)` | `{"formula"}` |
| `areNotEqual` | `NotEqualCondition` | `(Expression formula1, Expression formula2)` | `{"formula", "formula"}` |
| `haveSameFormulasSet` | `HaveSameFormulasSetCondition` | `(SchemeVariable node1, SchemeVariable node2)` | `{"node", "node"}` |
| `hasNoParents` | `HasNoParentsCondition` | `(SchemeVariable node)` | `{"node"}` |
| `isMarkedExpressionInAllChildren` | `MarkedExpressionInAllChildrenCondition` | `(SchemeVariable node, Expression formula, Expression relation, Object mark)` | `{"node", "formula", "relation", "mark"}` |

### Actions (3 total)

| Keyword | Class | Constructor Parameters | @ParametersTypes |
|---------|-------|----------------------|------------------|
| `unlink` | `UnlinkAction` | `(SchemeVariable nodeFrom, SchemeVariable nodeTo, Expression relation)` | `{"node", "node", "relation"}` |
| `createOneParent` | `AddOneParentAction` | `(SchemeVariable sourceNode, SchemeVariable newNode, Expression relation)` | `{"node", "node", "relation"}` |
| `merge` | `MergeNodeInNodeAction` | `(SchemeVariable mergedNode, SchemeVariable targetNode)` | `{"node", "node"}` |

---

## Impact

- **XML parsing works** - `LogicXMLParser` uses reflection and works with all registered items
- **String parsing fails** - `OldiesTokenizer` throws `ParseException.UNKOWN_CODITION` or `ParseException.UNKOWN_ACTION` when encountering these keywords

---

## Implementation Plan

### File to Modify

`src/lotrec/parser/OldiesTokenizer.java`

### Step 1: Add Imports (after line ~42)

Add imports for the missing condition classes:

```java
import lotrec.dataStructure.tableau.condition.IsAtomicCondition;
import lotrec.dataStructure.tableau.condition.IsNotAtomicCondition;
import lotrec.dataStructure.tableau.condition.NotEqualCondition;
import lotrec.dataStructure.tableau.condition.HaveSameFormulasSetCondition;
import lotrec.dataStructure.tableau.condition.HasNoParentsCondition;
import lotrec.dataStructure.tableau.condition.MarkedExpressionInAllChildrenCondition;
```

Note: Action classes (`UnlinkAction`, `AddOneParentAction`, `MergeNodeInNodeAction`) are already covered by the wildcard import `import lotrec.dataStructure.tableau.action.*;` at line 26.

### Step 2: Add Condition Cases in `parseCondition()` (before line 207)

Add 6 new `else if` blocks, following the existing pattern in the method:

```java
} else if (condName.equals("isAtomic")) {
    formulaArg = recognizeExpression();
    cond = new IsAtomicCondition(formulaArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    return cond;
} else if (condName.equals("isNotAtomic")) {
    formulaArg = recognizeExpression();
    cond = new IsNotAtomicCondition(formulaArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    return cond;
} else if (condName.equals("areNotEqual")) {
    Expression formula1 = recognizeExpression();
    Expression formula2 = recognizeExpression();
    cond = new NotEqualCondition(formula1, formula2);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.FORMULA, formula1));
    cond.addParameter(new Parameter(ParameterType.FORMULA, formula2));
    return cond;
} else if (condName.equals("haveSameFormulasSet")) {
    nodeArg = new StringSchemeVariable(readStringToken());
    node2Arg = new StringSchemeVariable(readStringToken());
    cond = new HaveSameFormulasSetCondition(nodeArg, node2Arg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    return cond;
} else if (condName.equals("hasNoParents")) {
    nodeArg = new StringSchemeVariable(readStringToken());
    cond = new HasNoParentsCondition(nodeArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    return cond;
} else if (condName.equals("isMarkedExpressionInAllChildren")) {
    nodeArg = new StringSchemeVariable(readStringToken());
    formulaArg = recognizeExpression();
    relationArg = recognizeExpression();
    markArg = readStringToken();
    cond = new MarkedExpressionInAllChildrenCondition(nodeArg, formulaArg, relationArg, markArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    cond.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    cond.addParameter(new Parameter(ParameterType.MARK, markArg));
    return cond;
```

### Step 3: Add Action Cases in `parseAction()` (before line 370)

Add 3 new `else if` blocks, following the existing pattern:

```java
} else if (acName.equals("unlink")) {
    nodeArg = new StringSchemeVariable(readStringToken());
    node2Arg = new StringSchemeVariable(readStringToken());
    relationArg = recognizeExpression();
    ac = new UnlinkAction(nodeArg, node2Arg, relationArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    ac.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    return ac;
} else if (acName.equals("createOneParent")) {
    nodeArg = new StringSchemeVariable(readStringToken());
    node2Arg = new StringSchemeVariable(readStringToken());
    relationArg = recognizeExpression();
    ac = new AddOneParentAction(nodeArg, node2Arg, relationArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    ac.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    return ac;
} else if (acName.equals("merge")) {
    nodeArg = new StringSchemeVariable(readStringToken());
    node2Arg = new StringSchemeVariable(readStringToken());
    ac = new MergeNodeInNodeAction(nodeArg, node2Arg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    return ac;
```

---

## Testing

### 1. Run Existing Tests

```bash
gradlew.bat test
```

All existing tests should continue to pass.

### 2. Add New Parser Tests

Create test file: `test/lotrec/parser/OldiesTokenizerMissingParsersTest.java`

Test cases to add:
- `shouldParseIsAtomicCondition()` - parse `"isAtomic _X"`
- `shouldParseIsNotAtomicCondition()` - parse `"isNotAtomic _Y"`
- `shouldParseAreNotEqualCondition()` - parse `"areNotEqual _X _Y"`
- `shouldParseHaveSameFormulasSetCondition()` - parse `"haveSameFormulasSet node0 node1"`
- `shouldParseHasNoParentsCondition()` - parse `"hasNoParents node0"`
- `shouldParseIsMarkedExpressionInAllChildrenCondition()` - parse `"isMarkedExpressionInAllChildren node0 _X R myMark"`
- `shouldParseUnlinkAction()` - parse `"unlink node0 node1 R"`
- `shouldParseCreateOneParentAction()` - parse `"createOneParent node0 node1 R"`
- `shouldParseMergeAction()` - parse `"merge node0 node1"`

### 3. Verify XML Round-Trip

The existing tests should confirm XML parsing remains unaffected:
- `PredefinedLogicsLoadTest` - all 38 logics load
- `PredefinedLogicsSaveTest` - all logics save and reload correctly

---

## Risk Assessment

**Low Risk Implementation:**
- Changes follow established patterns already in the file
- No modification to existing parsing logic - only additions
- XML parsing (LogicXMLParser) remains completely unchanged
- All 38 predefined logic files should continue to work

---

## Summary of Changes

| File | Changes |
|------|---------|
| `src/lotrec/parser/OldiesTokenizer.java` | Add 6 imports, 6 condition cases (~60 lines), 3 action cases (~30 lines) |

### Optional Files to Create

| File | Purpose |
|------|---------|
| `test/lotrec/parser/OldiesTokenizerMissingParsersTest.java` | Test new condition/action parsing |

---

## Implementation Completed (2026-01-30)

### Status: ✅ COMPLETE

All missing parser methods have been successfully implemented and tested.

### Changes Made to `src/lotrec/parser/OldiesTokenizer.java`

**1. Added 6 new imports for condition classes (lines 43-48):**
- `IsAtomicCondition`
- `IsNotAtomicCondition`
- `NotEqualCondition`
- `HaveSameFormulasSetCondition`
- `HasNoParentsCondition`
- `MarkedExpressionInAllChildrenCondition`

**2. Added 6 new condition parsing cases in `parseCondition()` (lines 213-258):**

| Keyword | Class | Parameters |
|---------|-------|------------|
| `isAtomic` | `IsAtomicCondition` | `(Expression formula)` |
| `isNotAtomic` | `IsNotAtomicCondition` | `(Expression formula)` |
| `areNotEqual` | `NotEqualCondition` | `(Expression formula1, Expression formula2)` |
| `haveSameFormulasSet` | `HaveSameFormulasSetCondition` | `(SchemeVariable node1, SchemeVariable node2)` |
| `hasNoParents` | `HasNoParentsCondition` | `(SchemeVariable node)` |
| `isMarkedExpressionInAllChildren` | `MarkedExpressionInAllChildrenCondition` | `(SchemeVariable node, Expression formula, Expression relation, Object mark)` |

**3. Added 3 new action parsing cases in `parseAction()` (lines 411-438):**

| Keyword | Class | Parameters |
|---------|-------|------------|
| `unlink` | `UnlinkAction` | `(SchemeVariable nodeFrom, SchemeVariable nodeTo, Expression relation)` |
| `createOneParent` | `AddOneParentAction` | `(SchemeVariable sourceNode, SchemeVariable newNode, Expression relation)` |
| `merge` | `MergeNodeInNodeAction` | `(SchemeVariable mergedNode, SchemeVariable targetNode)` |

### Test Updates

**1. Updated `test/lotrec/dataStructure/tableau/action/ActionRegistryTest.java`:**
- Changed 3 tests from expecting `ParseException` to verifying successful parsing
- Renamed nested class from `UnimplementedActionParsing` to `LinkMergeActionParsing`
- Tests added: `shouldParseUnlink()`, `shouldParseMerge()`, `shouldParseCreateOneParent()`

**2. Updated `test/lotrec/dataStructure/tableau/condition/ConditionRegistryTest.java`:**
- Added 6 new parsing tests for the newly implemented conditions:
  - `shouldParseIsAtomic()`
  - `shouldParseIsNotAtomic()`
  - `shouldParseAreNotEqual()`
  - `shouldParseHaveSameFormulasSet()`
  - `shouldParseHasNoParents()`
  - `shouldParseIsMarkedExpressionInAllChildren()`

### Test Results

| Metric | Result |
|--------|--------|
| **Total Tests** | 1301 (was 1288) |
| **All Tests Passing** | ✅ Yes |
| **New Parsing Tests** | 9 |

---

## References

- Implementation summary: `specs/001-exhaustive-test-suite/implementation-summary.md`
- AbstractCondition registry: `src/lotrec/dataStructure/tableau/condition/AbstractCondition.java` (lines 28-76)
- AbstractAction registry: `src/lotrec/process/AbstractAction.java` (lines 28-56)
- OldiesTokenizer: `src/lotrec/parser/OldiesTokenizer.java`
