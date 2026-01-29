# Implementation Plan: Exhaustive Test Suite for LoTREC

## Summary

Create a comprehensive test suite (~285 tests) organized into 7 tiers, from basic unit tests to integration tests. Tests capture current behavior as characterization tests to enable safe future refactoring.

## Branch & Specification

- **Branch**: `001-exhaustive-test-suite`
- **Spec**: `specs/001-exhaustive-test-suite/spec.md`

## Constitution Compliance

| Constraint | Status |
|------------|--------|
| Java 1.8 | ✅ No Java 9+ features |
| Gradle Build | ✅ `./gradlew test` |
| JUnit 5 + AssertJ | ✅ Already configured |
| No production code changes | ✅ Test-only |

---

## Test Files to Create (21 files)

### Tier 1: Expression Parsing (~40 tests)

| File | Path | Tests |
|------|------|-------|
| ExpressionParserTest.java | `test/lotrec/parser/` | ~25 |
| StrategyParserTest.java | `test/lotrec/parser/` | ~15 |

### Tier 2: Data Structure (~60 tests)

| File | Path | Tests |
|------|------|-------|
| ConnectorTest.java | `test/lotrec/dataStructure/expression/` | ~12 |
| ConstantExpressionTest.java | `test/lotrec/dataStructure/expression/` | ~10 |
| VariableExpressionTest.java | `test/lotrec/dataStructure/expression/` | ~10 |
| ExpressionMatchingTest.java | `test/lotrec/dataStructure/expression/` | ~12 |
| MarkedExpressionTest.java | `test/lotrec/dataStructure/expression/` | ~8 |
| RuleTest.java | `test/lotrec/dataStructure/tableau/` | ~8 |

### Tier 3: Condition/Action Registry (~50 tests)

| File | Path | Tests |
|------|------|-------|
| ConditionRegistryTest.java | `test/lotrec/dataStructure/tableau/condition/` | ~30 |
| ActionRegistryTest.java | `test/lotrec/dataStructure/tableau/action/` | ~20 |

### Tier 4: Logic XML Loading (~45 tests)

| File | Path | Tests |
|------|------|-------|
| LogicXMLParserTest.java | `test/lotrec/parser/` | ~30 |
| PredefinedLogicsLoadTest.java | `test/lotrec/logics/` | ~15 |

### Tier 5: Logic Save/Reload (~20 tests)

| File | Path | Tests |
|------|------|-------|
| PredefinedLogicsSaveTest.java | `test/lotrec/logics/` | ~20 |

### Tier 6: Strategy Execution (~30 tests)

| File | Path | Tests |
|------|------|-------|
| StrategyTest.java | `test/lotrec/process/` | ~10 |
| RepeatTest.java | `test/lotrec/process/` | ~8 |
| FirstRuleTest.java | `test/lotrec/process/` | ~6 |
| AllRulesTest.java | `test/lotrec/process/` | ~6 |

### Tier 7: Engine Execution (~40 tests)

| File | Path | Tests |
|------|------|-------|
| EngineTest.java | `test/lotrec/engine/` | ~25 |
| SatisfiabilityTest.java | `test/lotrec/logics/` | ~15 |

### Utilities

| File | Path | Purpose |
|------|------|---------|
| TestFixtures.java | `test/lotrec/` | Shared test fixtures |

---

## Implementation Order

### Phase 1: Foundation (Tier 1-2)

**1.1 Create TestFixtures.java**
```java
package lotrec;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.*;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.LogicXMLParser;

public final class TestFixtures {
    public static Logic createMinimalLogic() {
        Logic logic = new Logic();
        logic.setName("TestLogic");
        logic.addConnector(new Connector("not", 1, "~_"));
        logic.addConnector(new Connector("and", 2, "_&_"));
        logic.addConnector(new Connector("or", 2, "_|_"));
        logic.addConnector(new Connector("imp", 2, "_->_"));
        logic.addConnector(new Connector("nec", 1, "[]_"));
        logic.addConnector(new Connector("pos", 1, "<>_"));
        return logic;
    }

    public static OldiesTokenizer createTokenizer(Logic logic) {
        OldiesTokenizer t = new OldiesTokenizer(logic);
        t.initializeTokenizerAndProps();
        return t;
    }

    public static String logicPath(String name) {
        return "src/lotrec/logics/" + name + ".xml";
    }

    public static Logic loadLogic(String name) {
        LogicXMLParser parser = new LogicXMLParser();
        return parser.parseLogic(logicPath(name));
    }

    // ========== Launcher Benchmark Helpers ==========

    /**
     * Creates args array for Launcher.treatArgsForBenchmark()
     * @param logicName One of 38 predefined logic names
     * @param formulaInfix Formula in infix format (use expression.getCodeString())
     * @param stopAtFirstOpen true = SAT mode (stop at first open), false = build all
     */
    public static String[] benchmarkArgs(String logicName, String formulaInfix, boolean stopAtFirstOpen) {
        return new String[] { logicName, formulaInfix, String.valueOf(stopAtFirstOpen) };
    }

    /**
     * Converts an Expression to infix format suitable for benchmark
     */
    public static String toInfixCode(Expression expr) {
        return expr.getCodeString();
    }

    /** Predefined logic names for parameterized tests */
    public static final String[] ALL_LOGIC_NAMES = {
        "Classical-Propositional-Logic", "Monomodal-K", "S4-Explicit-R",
        "S4-with-history", "S4Optimal", "S5-explicit-edges", "S5-implicit-edges",
        "KD", "KD45", "KD45Optimal", "KT-explicit-edges", "KT-implicit-edges",
        "KB-explicit-edges", "KB-implicit-edges", "K4-explicit-edges",
        "K4-implicit-edges", "K4Confluence", "K45Optimal", "K5", "KB5", "KBT",
        "KBD", "Kalt1", "KMinimal", "K+Universal", "K2-with-Inclusion",
        "Multimodal-Kn", "KConfluence", "Intuitionistic-Logic-Lj",
        "Hybrid-Logic-H-at", "LTL", "PDL", "Model-Checking-Monomodal",
        "Model-Checking-Multimodal", "Multi-S5-PAL", "xstit", "LJminimal", "S4Minimal"
    };
}
```

**1.2 ConnectorTest.java** - Test Connector class
- `shouldCreateWithNameArityOutput()`
- `shouldAssignPriorityByArity()` (unary=2, binary=1, ternary=0)
- `shouldEqualByNameOnly()`
- `shouldSetAssociativity()`
- `shouldSetSpecialCharacter()`

**1.3 ConstantExpressionTest.java** - Test ConstantExpression
- `shouldReturnFalsumConstant()` - verify `ConstantExpression.FALSUM`
- `shouldMatchWithSameConstant()` - matchWith returns InstanceSet
- `shouldNotMatchWithDifferentConstant()` - matchWith returns null
- `shouldEqualCaseInsensitive()` - "P".equals("p")
- `shouldReturnThisFromGetInstance()`

**1.4 VariableExpressionTest.java** - Test VariableExpression
- `shouldBindOnFirstMatch()` - matchWith adds to InstanceSet
- `shouldVerifyExistingBinding()` - consistent binding succeeds
- `shouldRejectInconsistentBinding()` - returns null
- `shouldReturnBoundFromGetInstance()`
- `shouldEqualCaseSensitive()` - "A" != "a"

**1.5 ExpressionMatchingTest.java** - Test composite expression matching
- `shouldMatchStructurally()` - same connector + sub-expressions
- `shouldRejectDifferentConnector()`
- `shouldMatchRecursivelyWithVariables()`
- `shouldInstantiateWithBindings()`

**1.6 ExpressionParserTest.java** - Expression parsing tests
- Already covered in OldiesTokenizerTest, extend with edge cases

**1.7 StrategyParserTest.java** - Strategy code parsing
- `shouldParseRepeatBlock()` - "repeat ... end"
- `shouldParseFirstRuleBlock()` - "firstRule R1 R2 end"
- `shouldParseAllRulesBlock()` - "allRules R1 R2 end"
- `shouldParseNestedBlocks()`
- `shouldRejectInvalidSyntax()`

### Phase 2: Registry Tests (Tier 3)

**2.1 ConditionRegistryTest.java**

Registry verification (20 conditions):
```java
@Test
void shouldHaveAllConditionsRegistered() {
    assertThat(AbstractCondition.CLASSES_KEYWORDS).hasSize(20);
    assertThat(AbstractCondition.CLASSES_KEYWORDS).containsKeys(
        "hasElement", "hasNotElement", "isAtomic", "isNotAtomic",
        "isLinked", "isNotLinked", "hasNoSuccessor", "hasNoParents",
        "isAncestor", "areIdentical", "areNotIdentical", "areNotEqual",
        "contains", "haveSameFormulasSet", "isNewNode", "isMarked",
        "isNotMarked", "isMarkedExpression", "isNotMarkedExpression",
        "isMarkedExpressionInAllChildren"
    );
}
```

Parse each condition via OldiesTokenizer:
- `shouldParseHasElement()` - "hasElement ?n P"
- `shouldParseIsLinked()` - "isLinked ?n ?m R"
- `shouldParseIsMarked()` - "isMarked ?n applied"
- ... (one test per condition)

**2.2 ActionRegistryTest.java**

Registry verification (15 actions):
```java
@Test
void shouldHaveAllActionsRegistered() {
    assertThat(AbstractAction.CLASSES_KEYWORDS).hasSize(15);
    assertThat(AbstractAction.CLASSES_KEYWORDS).containsKeys(
        "add", "createNewNode", "link", "stop", "mark", "unmark",
        "markExpressions", "unmarkExpressions", "createOneSuccessor",
        "createOneParent", "hide", "kill", "duplicate", "unlink", "merge"
    );
}
```

Parse each action via OldiesTokenizer:
- `shouldParseAdd()` - "add ?n P"
- `shouldParseLink()` - "link ?n ?m R"
- ... (one test per action)

### Phase 3: XML Loading (Tier 4-5)

**3.1 LogicXMLParserTest.java**
```java
@Nested
class ConnectorParsing {
    @Test
    void shouldParseConnectorProperties() {
        Logic logic = parser.parseLogic(TestFixtures.logicPath("Monomodal-K"));
        Connector not = logic.getConnector("not");
        assertThat(not).isNotNull();
        assertThat(not.getArity()).isEqualTo(1);
    }
}

@Nested
class RuleParsing {
    @Test
    void shouldParseRuleWithConditionsAndActions() {
        Logic logic = parser.parseLogic(TestFixtures.logicPath("Monomodal-K"));
        assertThat(logic.getRules()).isNotEmpty();
        Rule rule = logic.getRules().get(0);
        assertThat(rule.getConditionsAsElements()).isNotNull();
    }
}
```

**3.2 PredefinedLogicsLoadTest.java** - Parameterized test for all 38 logics
```java
@ParameterizedTest
@ValueSource(strings = {
    "Classical-Propositional-Logic", "Monomodal-K", "S4-Explicit-R",
    "S4Minimal", "S5-explicit-edges", "KD", "KD45", "KT-explicit-edges",
    // ... all 38 logic names
})
void shouldLoadLogicWithoutError(String logicName) {
    assertThatCode(() -> parser.parseLogic(TestFixtures.logicPath(logicName)))
        .doesNotThrowAnyException();
}
```

**3.3 PredefinedLogicsSaveTest.java** - Round-trip serialization
```java
@Test
void shouldRoundTripLogic() throws Exception {
    Logic original = parser.parseLogic(TestFixtures.logicPath("Monomodal-K"));
    Path tempFile = Files.createTempFile("test-logic", ".xml");
    parser.saveLogic(original, tempFile.toString());
    Logic reloaded = parser.parseLogic(tempFile.toString());

    assertThat(reloaded.getConnectors()).hasSameSizeAs(original.getConnectors());
    assertThat(reloaded.getRules()).hasSameSizeAs(original.getRules());
}
```

### Phase 4: Strategy Execution (Tier 6)

**4.1 StrategyTest.java**
- `shouldSetAndGetCode()`
- `shouldSetAndGetWorkerName()`
- `shouldDuplicateStrategy()`

**4.2 RepeatTest.java, FirstRuleTest.java, AllRulesTest.java**
- Requires setting up minimal Logic with rules
- Tests strategy patterns in isolation

### Phase 5: Engine Execution (Tier 7)

> **Headless Entry Point**: Use `Launcher.treatArgsForBenchmark(String[] args)` instead of direct Engine instantiation.
> This method initializes MainFrame and Engine conveniently without GUI.
>
> **Arguments**:
> - `args[0]`: logicName - one of the 38 predefined logic names (e.g., "Classical-Propositional-Logic")
> - `args[1]`: formulaInfixCode - formula in infix format using logic's connectors (use `expression.getCodeString()`)
> - `args[2]`: SAT - "true" to stop at first open tableau, "false" to build all tableaux

**5.1 LauncherBenchmarkTest.java** (replaces EngineTest.java)
```java
@Test
void shouldRunBenchmarkWithValidFormula() throws Exception {
    // Use Launcher.treatArgsForBenchmark for headless engine execution
    String[] args = {"Classical-Propositional-Logic", "P & Q", "true"};

    // This initializes MainFrame and Engine, runs the proof search
    Launcher.treatArgsForBenchmark(args);

    // Verify via MainFrame state or Engine result
    assertThat(Lotrec.getMainFrame()).isNotNull();
}

@Test
void shouldBuildAllTableauxWhenSATisFalse() throws Exception {
    String[] args = {"Monomodal-K", "[]P -> P", "false"};
    Launcher.treatArgsForBenchmark(args);
    // Verify all tableaux were built
}
```

**5.2 SatisfiabilityTest.java** - Known theorems/non-theorems via Launcher benchmark
```java
@Nested
class PropositionalLogic {
    @Test
    void shouldFindContradictionUnsatisfiable() {
        // P & ~P is unsatisfiable (closed tableau)
        String[] args = {"Classical-Propositional-Logic", "P & ~P", "true"};
        Launcher.treatArgsForBenchmark(args);
        // Verify closed tableau result
    }

    @Test
    void shouldFindSimpleFormulaSatisfiable() {
        // P is satisfiable (open tableau)
        String[] args = {"Classical-Propositional-Logic", "P", "true"};
        Launcher.treatArgsForBenchmark(args);
        // Verify open tableau result
    }
}

@Nested
class ModalLogicK {
    @Test
    void shouldVerifyKAxiom() {
        // nec(P->Q) -> (nec P -> nec Q) is valid
        // Test unsatisfiability of negation: ~(nec(P->Q) -> (nec P -> nec Q))
        String[] args = {"Monomodal-K", "~([]( P -> Q) -> ([]P -> []Q))", "true"};
        Launcher.treatArgsForBenchmark(args);
        // Verify closed tableau (formula is valid)
    }
}

@Nested
class TestingFormulasFromLogics {
    @ParameterizedTest
    @MethodSource("getTestingFormulasWithExpectedResults")
    void shouldVerifyPredefinedTestingFormulas(String logicName, String formula, boolean expectedSat) {
        String[] args = {logicName, formula, "true"};
        Launcher.treatArgsForBenchmark(args);
        // Verify result matches expected satisfiability
    }

    // Helper to extract testing formulas from logic XML files
    // Use expression.getCodeString() to get infix format
}
```

---

## Directory Structure After Implementation

```
test/
├── lotrec/
│   ├── TestFixtures.java
│   ├── parser/
│   │   ├── OldiesTokenizerTest.java      [EXISTING]
│   │   ├── ExpressionParserTest.java     [NEW]
│   │   ├── StrategyParserTest.java       [NEW]
│   │   └── LogicXMLParserTest.java       [NEW]
│   ├── dataStructure/
│   │   ├── LogicTest.java                [EXISTING]
│   │   ├── expression/
│   │   │   ├── ConnectorTest.java        [NEW]
│   │   │   ├── ConstantExpressionTest.java [NEW]
│   │   │   ├── VariableExpressionTest.java [NEW]
│   │   │   ├── ExpressionMatchingTest.java [NEW]
│   │   │   └── MarkedExpressionTest.java [NEW]
│   │   └── tableau/
│   │       ├── RuleTest.java             [NEW]
│   │       ├── condition/
│   │       │   └── ConditionRegistryTest.java [NEW]
│   │       └── action/
│   │           └── ActionRegistryTest.java [NEW]
│   ├── process/
│   │   ├── StrategyTest.java             [NEW]
│   │   ├── RepeatTest.java               [NEW]
│   │   ├── FirstRuleTest.java            [NEW]
│   │   └── AllRulesTest.java             [NEW]
│   ├── engine/
│   │   └── LauncherBenchmarkTest.java    [NEW] (uses Launcher.treatArgsForBenchmark)
│   └── logics/
│       ├── PredefinedLogicsLoadTest.java [NEW]
│       ├── PredefinedLogicsSaveTest.java [NEW]
│       └── SatisfiabilityTest.java       [NEW]
└── gi/
    └── transformers/
        └── PriorityInfixToPrefixTest.java [EXISTING]
```

---

## Verification Steps

After each tier, run:
```bash
./gradlew test
./gradlew jacocoTestReport
```

### Success Criteria

1. ✅ All 38 predefined logics load without errors
2. ✅ Logic save/reload produces identical structure
3. ✅ 60%+ coverage for: `lotrec.parser`, `lotrec.dataStructure`, `lotrec.process`, `lotrec.engine`
4. ✅ All 20 conditions tested via parsing
5. ✅ All 15 actions tested via parsing
6. ✅ Full suite < 5 minutes
7. ✅ Zero failures on unmodified codebase

### Final Verification
```bash
./gradlew clean test jacocoTestReport
# Check: build/reports/tests/test/index.html
# Check: build/reports/jacoco/test/html/index.html
```

---

## Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| Engine/MainFrame initialization | Use `Launcher.treatArgsForBenchmark()` for headless testing |
| Thread timing in Engine tests | Use timeouts and `join()` |
| XML file paths differ on Windows | Use forward slashes, relative to project root |
| Strategy infinite loops | Set iteration limits in tests |
| Formula format for benchmark | Use `expression.getCodeString()` to get correct infix format |

---

## Estimated Test Count by Phase

| Phase | Tests | Priority |
|-------|-------|----------|
| Phase 1: Foundation | ~60 | Start here |
| Phase 2: Registry | ~50 | High value |
| Phase 3: XML Loading | ~65 | Core functionality |
| Phase 4: Strategy | ~30 | Medium |
| Phase 5: Engine | ~40 | Integration |
| **Total** | **~285** | |
