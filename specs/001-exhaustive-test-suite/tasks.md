# Task Tracking: [FEAT-001] Exhaustive Test Suite for LoTREC

> Task checklist for implementing ~285 tests organized into 7 tiers.

## Metadata

| Field | Value |
|-------|-------|
| **Tasks ID** | TASKS-001 |
| **Spec Reference** | [FEAT-001](./spec.md) |
| **Plan Reference** | [PLAN-001](./plan.md) |
| **Author** | Claude Code |
| **Created** | 2026-01-28 |
| **Last Updated** | 2026-01-28 |

---

## Progress Summary

| Phase | Total | Done | Remaining | Status |
|-------|-------|------|-----------|--------|
| Phase 1: Setup | 3 | 0 | 3 | Not Started |
| Phase 2: Foundational | 1 | 0 | 1 | Not Started |
| Phase 3: Tier 1-2 Tests | 8 | 0 | 8 | Not Started |
| Phase 4: Tier 3 Tests | 2 | 0 | 2 | Not Started |
| Phase 5: Tier 4-5 Tests | 3 | 0 | 3 | Not Started |
| Phase 6: Tier 6 Tests | 4 | 0 | 4 | Not Started |
| Phase 7: Tier 7 Tests | 2 | 0 | 2 | Not Started |
| Phase 8: Verification | 3 | 0 | 3 | Not Started |
| **TOTAL** | **26** | **0** | **26** | **0%** |

---

## Dependencies

```
Phase 1 (Setup) ─────────────────────┐
                                     │
Phase 2 (Foundational) ◄─────────────┘
         │
         ├──► Phase 3 (Tier 1-2: Expression & Data Structure)
         │            │
         │            ▼
         ├──► Phase 4 (Tier 3: Registry)
         │            │
         │            ▼
         ├──► Phase 5 (Tier 4-5: XML Loading)
         │            │
         │            ▼
         ├──► Phase 6 (Tier 6: Strategy)
         │            │
         │            ▼
         └──► Phase 7 (Tier 7: Engine)
                     │
                     ▼
              Phase 8 (Verification)
```

**Note**: Phases 3-7 can be executed in parallel after Phase 2, but are recommended in order for incremental coverage growth.

---

## Phase 1: Setup

> Prerequisites and environment setup.

- [ ] T001 Verify existing tests pass with `./gradlew test` in project root
- [ ] T002 Verify JaCoCo report generation with `./gradlew jacocoTestReport`
- [ ] T003 Create test directory structure for new test files:
  - `test/lotrec/dataStructure/expression/`
  - `test/lotrec/dataStructure/tableau/`
  - `test/lotrec/dataStructure/tableau/condition/`
  - `test/lotrec/dataStructure/tableau/action/`
  - `test/lotrec/process/`
  - `test/lotrec/engine/`
  - `test/lotrec/logics/`

---

## Phase 2: Foundational

> Shared test utilities required by all tiers.

- [ ] T004 Create TestFixtures utility class in `test/lotrec/TestFixtures.java` with:
  - `createMinimalLogic()` - creates Logic with basic connectors (not, and, or, imp, nec, pos)
  - `createTokenizer(Logic)` - creates initialized OldiesTokenizer
  - `logicPath(String)` - returns path to predefined logic XML file
  - `loadLogic(String)` - loads and returns a predefined logic by name
  - `benchmarkArgs(String logicName, String formulaInfix, boolean stopAtFirstOpen)` - creates args for Launcher.treatArgsForBenchmark()
  - `toInfixCode(Expression)` - converts Expression to infix using getCodeString()
  - `ALL_LOGIC_NAMES` - String array of all 38 predefined logic names for parameterized tests

---

## Phase 3: Tier 1-2 Tests (Expression Parsing & Data Structures)

> Unit tests for expression types, connectors, and core data structures (~100 tests).

### 3.1 Expression Data Structure Tests

- [ ] T005 [P] [US1] Create ConnectorTest in `test/lotrec/dataStructure/expression/ConnectorTest.java` (~12 tests):
  - `shouldCreateWithNameArityOutput()`
  - `shouldAssignPriorityByArity()` - unary=2, binary=1
  - `shouldEqualByNameOnly()`
  - `shouldSetAssociativity()`
  - `shouldSetSpecialCharacter()`
  - `shouldSetOutputFormat()`
  - `shouldHandleNullName()`
  - `shouldCreateUnaryConnector()`
  - `shouldCreateBinaryConnector()`
  - `shouldCreateTernaryConnector()`
  - `shouldCloneConnector()`
  - `shouldCompareByPriority()`

- [ ] T006 [P] [US1] Create ConstantExpressionTest in `test/lotrec/dataStructure/expression/ConstantExpressionTest.java` (~10 tests):
  - `shouldReturnFalsumConstant()`
  - `shouldMatchWithSameConstant()`
  - `shouldNotMatchWithDifferentConstant()`
  - `shouldEqualCaseInsensitive()`
  - `shouldReturnThisFromGetInstance()`
  - `shouldReturnNameAsToString()`
  - `shouldMatchWithVariableExpression()`
  - `shouldNotMatchWithCompositeExpression()`
  - `shouldCreateFromString()`
  - `shouldHandleEmptyInstanceSet()`

- [ ] T007 [P] [US1] Create VariableExpressionTest in `test/lotrec/dataStructure/expression/VariableExpressionTest.java` (~10 tests):
  - `shouldBindOnFirstMatch()`
  - `shouldVerifyExistingBinding()`
  - `shouldRejectInconsistentBinding()`
  - `shouldReturnBoundFromGetInstance()`
  - `shouldEqualCaseSensitive()`
  - `shouldMatchAnyExpression()`
  - `shouldReturnNullForUnbound()`
  - `shouldCreateSchemeVariable()`
  - `shouldIdentifyByName()`
  - `shouldCloneVariable()`

- [ ] T008 [P] [US1] Create ExpressionMatchingTest in `test/lotrec/dataStructure/expression/ExpressionMatchingTest.java` (~12 tests):
  - `shouldMatchStructurally()`
  - `shouldRejectDifferentConnector()`
  - `shouldMatchRecursivelyWithVariables()`
  - `shouldInstantiateWithBindings()`
  - `shouldMatchNestedExpressions()`
  - `shouldRejectArityMismatch()`
  - `shouldMatchWithMultipleVariables()`
  - `shouldPreserveBindingsAcrossMatch()`
  - `shouldHandleDeepNesting()`
  - `shouldMatchModalExpressions()`
  - `shouldReturnNullOnMismatch()`
  - `shouldCreateNewInstanceSet()`

- [ ] T009 [P] [US1] Create MarkedExpressionTest in `test/lotrec/dataStructure/expression/MarkedExpressionTest.java` (~8 tests):
  - `shouldWrapExpression()`
  - `shouldAddMark()`
  - `shouldRemoveMark()`
  - `shouldCheckMarkPresence()`
  - `shouldReturnWrappedExpression()`
  - `shouldCloneWithMarks()`
  - `shouldEqualWithSameMarks()`
  - `shouldNotEqualWithDifferentMarks()`

### 3.2 Tableau Data Structure Tests

- [ ] T010 [P] [US1] Create RuleTest in `test/lotrec/dataStructure/tableau/RuleTest.java` (~8 tests):
  - `shouldCreateWithName()`
  - `shouldAddConditions()`
  - `shouldAddActions()`
  - `shouldSetCommutative()`
  - `shouldGetConditionsAsElements()`
  - `shouldGetActionsAsElements()`
  - `shouldDuplicateRule()`
  - `shouldCreateEventMachine()`

### 3.3 Parser Tests

- [ ] T011 [P] [US1] Create ExpressionParserTest in `test/lotrec/parser/ExpressionParserTest.java` (~25 tests):
  - `shouldParseConstant()`
  - `shouldParseVariable()`
  - `shouldParseNodeVariable()`
  - `shouldParseUnaryConnector()`
  - `shouldParseBinaryConnector()`
  - `shouldParseNestedExpression()`
  - `shouldParseDeeplyNested()`
  - `shouldParseModalOperators()`
  - `shouldHandleKeywordConstants()`
  - `shouldRejectMalformedExpression()`
  - `shouldParseWithWhitespace()`
  - `shouldParseMultiCharacterNames()`
  - `shouldParseEquivalence()`
  - `shouldParseImplication()`
  - `shouldParseConjunction()`
  - `shouldParseDisjunction()`
  - `shouldParseNegation()`
  - `shouldParseNecessity()`
  - `shouldParsePossibility()`
  - `shouldParseComplexFormula()`
  - `shouldPreservePrecedence()`
  - `shouldHandleParentheses()`
  - `shouldParseAtomicProposition()`
  - `shouldParseFalsum()`
  - `shouldParseVerum()`

- [ ] T012 [P] [US1] Create StrategyParserTest in `test/lotrec/parser/StrategyParserTest.java` (~15 tests):
  - `shouldParseRepeatBlock()`
  - `shouldParseFirstRuleBlock()`
  - `shouldParseAllRulesBlock()`
  - `shouldParseNestedBlocks()`
  - `shouldRejectInvalidSyntax()`
  - `shouldParseEmptyRepeat()`
  - `shouldParseMultipleRulesInFirstRule()`
  - `shouldParseMultipleRulesInAllRules()`
  - `shouldParseStrategyReference()`
  - `shouldHandleWhitespace()`
  - `shouldParseDeeplyNestedStrategies()`
  - `shouldParseWithComments()`
  - `shouldHandleMalformedEnd()`
  - `shouldParseComplexStrategy()`
  - `shouldSetWorkerName()`

---

## Phase 4: Tier 3 Tests (Condition/Action Registry)

> Tests verifying all registered conditions and actions (~50 tests).

- [ ] T013 [P] [US2] Create ConditionRegistryTest in `test/lotrec/dataStructure/tableau/condition/ConditionRegistryTest.java` (~30 tests):
  - `shouldHaveAllConditionsRegistered()` - verify 20 conditions in CLASSES_KEYWORDS
  - `shouldParseHasElement()` - "hasElement ?n P"
  - `shouldParseHasNotElement()` - "hasNotElement ?n P"
  - `shouldParseIsAtomic()` - "isAtomic P"
  - `shouldParseIsNotAtomic()` - "isNotAtomic P"
  - `shouldParseIsLinked()` - "isLinked ?n ?m R"
  - `shouldParseIsNotLinked()` - "isNotLinked ?n ?m R"
  - `shouldParseHasNoSuccessor()` - "hasNoSuccessor ?n R"
  - `shouldParseHasNoParents()` - "hasNoParents ?n R"
  - `shouldParseIsAncestor()` - "isAncestor ?n ?m R"
  - `shouldParseAreIdentical()` - "areIdentical ?n ?m"
  - `shouldParseAreNotIdentical()` - "areNotIdentical ?n ?m"
  - `shouldParseAreNotEqual()` - "areNotEqual P Q"
  - `shouldParseContains()` - "contains P Q"
  - `shouldParseHaveSameFormulasSet()` - "haveSameFormulasSet ?n ?m"
  - `shouldParseIsNewNode()` - "isNewNode ?n"
  - `shouldParseIsMarked()` - "isMarked ?n applied"
  - `shouldParseIsNotMarked()` - "isNotMarked ?n applied"
  - `shouldParseIsMarkedExpression()` - "isMarkedExpression ?n P applied"
  - `shouldParseIsNotMarkedExpression()` - "isNotMarkedExpression ?n P applied"
  - `shouldParseIsMarkedExpressionInAllChildren()` - "isMarkedExpressionInAllChildren ?n P R"
  - `shouldInstantiateConditionByKeyword()`
  - `shouldHandleInvalidKeyword()`
  - `shouldParseConditionWithNodeParameter()`
  - `shouldParseConditionWithFormulaParameter()`
  - `shouldParseConditionWithRelationParameter()`
  - `shouldParseConditionWithMarkParameter()`
  - `shouldCloneCondition()`
  - `shouldGetConditionKeywords()`
  - `shouldVerifyConditionParameterTypes()`

- [ ] T014 [P] [US2] Create ActionRegistryTest in `test/lotrec/dataStructure/tableau/action/ActionRegistryTest.java` (~20 tests):
  - `shouldHaveAllActionsRegistered()` - verify 15 actions in CLASSES_KEYWORDS
  - `shouldParseAdd()` - "add ?n P"
  - `shouldParseCreateNewNode()` - "createNewNode ?m"
  - `shouldParseLink()` - "link ?n ?m R"
  - `shouldParseUnlink()` - "unlink ?n ?m R"
  - `shouldParseStop()` - "stop ?n"
  - `shouldParseMark()` - "mark ?n applied"
  - `shouldParseUnmark()` - "unmark ?n applied"
  - `shouldParseMarkExpressions()` - "markExpressions ?n P applied"
  - `shouldParseUnmarkExpressions()` - "unmarkExpressions ?n P applied"
  - `shouldParseCreateOneSuccessor()` - "createOneSuccessor ?n ?m R"
  - `shouldParseCreateOneParent()` - "createOneParent ?n ?m R"
  - `shouldParseHide()` - "hide ?n"
  - `shouldParseKill()` - "kill ?n"
  - `shouldParseDuplicate()` - "duplicate ?n"
  - `shouldParseMerge()` - "merge ?n ?m"
  - `shouldInstantiateActionByKeyword()`
  - `shouldHandleInvalidActionKeyword()`
  - `shouldCloneAction()`
  - `shouldVerifyActionParameterTypes()`

---

## Phase 5: Tier 4-5 Tests (XML Loading & Save/Reload)

> Integration tests for logic file parsing and serialization (~65 tests).

- [ ] T015 [P] [US3] Create LogicXMLParserTest in `test/lotrec/parser/LogicXMLParserTest.java` (~30 tests):
  - Nested class `ConnectorParsing`:
    - `shouldParseConnectorProperties()`
    - `shouldParseConnectorArity()`
    - `shouldParseConnectorOutputFormat()`
    - `shouldParseConnectorPriority()`
    - `shouldParseConnectorAssociativity()`
    - `shouldParseMultipleConnectors()`
  - Nested class `RuleParsing`:
    - `shouldParseRuleWithConditionsAndActions()`
    - `shouldParseRuleName()`
    - `shouldParseRuleConditions()`
    - `shouldParseRuleActions()`
    - `shouldParseMultipleRules()`
    - `shouldParseCommutativeRule()`
  - Nested class `StrategyParsing`:
    - `shouldParseStrategyCode()`
    - `shouldParseMainStrategy()`
    - `shouldParseMultipleStrategies()`
    - `shouldParseNestedStrategyCode()`
  - Nested class `TestingFormulaParsing`:
    - `shouldParseTestingFormulas()`
    - `shouldParseFormulaWithExpectedResult()`
  - Nested class `ErrorHandling`:
    - `shouldHandleMissingFile()`
    - `shouldHandleMalformedXML()`
    - `shouldHandleMissingConnector()`
    - `shouldHandleInvalidArity()`
  - Nested class `FullLogicParsing`:
    - `shouldParseCompleteLogic()`
    - `shouldPreserveLogicName()`
    - `shouldPreserveAllComponents()`
    - Additional tests for edge cases (~6 more)

- [ ] T016 [P] [US3] Create PredefinedLogicsLoadTest in `test/lotrec/logics/PredefinedLogicsLoadTest.java` (~15 tests):
  - `shouldLoadAllPredefinedLogicsWithoutError()` - parameterized test for all 38 logics
  - `shouldLoadClassicalPropositionalLogic()`
  - `shouldLoadMonomodalK()`
  - `shouldLoadS4ExplicitR()`
  - `shouldLoadS5ExplicitEdges()`
  - `shouldLoadKD()`
  - `shouldLoadKD45()`
  - `shouldLoadIntuitionisticLogic()`
  - `shouldLoadLTL()`
  - `shouldLoadPDL()`
  - `shouldLoadHybridLogic()`
  - `shouldHaveValidConnectors()` - verify all logics have at least one connector
  - `shouldHaveValidRules()` - verify all logics have at least one rule
  - `shouldHaveValidStrategy()` - verify all logics have a main strategy
  - `shouldHaveConsistentStructure()`

- [ ] T017 [P] [US3] Create PredefinedLogicsSaveTest in `test/lotrec/logics/PredefinedLogicsSaveTest.java` (~20 tests):
  - `shouldRoundTripLogic()`
  - `shouldSaveAndReloadConnectors()`
  - `shouldSaveAndReloadRules()`
  - `shouldSaveAndReloadStrategies()`
  - `shouldPreserveMainStrategy()`
  - `shouldPreserveTestingFormulas()`
  - `shouldPreserveConnectorProperties()`
  - `shouldPreserveRuleConditions()`
  - `shouldPreserveRuleActions()`
  - `shouldCreateNewLogicAndSave()`
  - `shouldSaveToTempFile()`
  - `shouldReloadSavedLogic()`
  - `shouldPreserveLogicName()`
  - `shouldHandleSaveErrors()`
  - `shouldPreserveConnectorOrder()`
  - `shouldPreserveRuleOrder()`
  - `shouldPreserveStrategyOrder()`
  - `shouldRoundTripAllPredefinedLogics()` - parameterized for all 38 logics
  - `shouldProduceValidXML()`
  - `shouldPreserveComments()` (if applicable)

---

## Phase 6: Tier 6 Tests (Strategy Execution)

> Integration tests for strategy patterns and rule application (~30 tests).

- [ ] T018 [P] [US4] Create StrategyTest in `test/lotrec/process/StrategyTest.java` (~10 tests):
  - `shouldSetAndGetCode()`
  - `shouldSetAndGetWorkerName()`
  - `shouldDuplicateStrategy()`
  - `shouldCreateWorkerFromCode()`
  - `shouldParseSimpleCode()`
  - `shouldParseComplexCode()`
  - `shouldHandleEmptyCode()`
  - `shouldSetAndGetName()`
  - `shouldCloneStrategy()`
  - `shouldGetAllRuleNames()`

- [ ] T019 [P] [US4] Create RepeatTest in `test/lotrec/process/RepeatTest.java` (~8 tests):
  - `shouldRepeatUntilQuiet()`
  - `shouldApplyAllWorkersPerIteration()`
  - `shouldTerminateOnNoProgress()`
  - `shouldHandleEmptyWorkerList()`
  - `shouldCountIterations()`
  - `shouldRespectMaxIterations()`
  - `shouldHandleSingleWorker()`
  - `shouldHandleMultipleWorkers()`

- [ ] T020 [P] [US4] Create FirstRuleTest in `test/lotrec/process/FirstRuleTest.java` (~6 tests):
  - `shouldApplyFirstMatchingRule()`
  - `shouldReturnAfterFirstMatch()`
  - `shouldHandleNoMatchingRule()`
  - `shouldRespectRuleOrder()`
  - `shouldHandleEmptyRuleList()`
  - `shouldHandleSingleRule()`

- [ ] T021 [P] [US4] Create AllRulesTest in `test/lotrec/process/AllRulesTest.java` (~6 tests):
  - `shouldApplyAllMatchingRules()`
  - `shouldHandleNoMatchingRules()`
  - `shouldApplyRulesInOrder()`
  - `shouldHandleEmptyRuleList()`
  - `shouldHandleSingleRule()`
  - `shouldApplyMultipleMatchingRules()`

---

## Phase 7: Tier 7 Tests (Engine Execution via Launcher Benchmark)

> Full integration tests for proof search and satisfiability (~40 tests).
> **Uses `Launcher.treatArgsForBenchmark(String[] args)` for headless execution.**
>
> **Arguments format**: `{logicName, formulaInfixCode, SAT}`
> - `logicName`: One of 38 predefined logic names
> - `formulaInfixCode`: Formula in infix format (use `expression.getCodeString()`)
> - `SAT`: "true" = stop at first open tableau, "false" = build all tableaux

- [ ] T022 [P] [US5] Create LauncherBenchmarkTest in `test/lotrec/engine/LauncherBenchmarkTest.java` (~25 tests):
  - Nested class `BenchmarkExecution`:
    - `shouldRunBenchmarkWithValidFormula()` - basic execution test
    - `shouldInitializeMainFrame()` - verify Lotrec.getMainFrame() not null
    - `shouldAcceptAllPredefinedLogicNames()` - parameterized for 38 logics
    - `shouldHandleInvalidLogicName()` - error handling
  - Nested class `SATModeControl`:
    - `shouldStopAtFirstOpenTableauWhenSATisTrue()`
    - `shouldBuildAllTableauxWhenSATisFalse()`
    - `shouldHandleBooleanStringVariants()` - "true"/"false" parsing
  - Nested class `FormulaProcessing`:
    - `shouldAcceptInfixFormula()` - e.g., "P & Q"
    - `shouldAcceptComplexNestedFormula()` - e.g., "(P -> Q) & (~Q -> ~P)"
    - `shouldAcceptModalFormula()` - e.g., "[]P -> P"
    - `shouldHandleFormulaWithAllConnectors()` - comprehensive connector test
    - `shouldRejectMalformedFormula()` - error handling
  - Nested class `TableauResults`:
    - `shouldProduceClosedTableauForContradiction()`
    - `shouldProduceOpenTableauForSatisfiable()`
    - `shouldCompleteWithinTimeout()` - performance guard
  - Nested class `LogicSpecificTests`:
    - `shouldRunClassicalPropositionalBenchmark()`
    - `shouldRunMonomodalKBenchmark()`
    - `shouldRunS4Benchmark()`
    - `shouldRunS5Benchmark()`
    - `shouldRunIntuitionisticBenchmark()`
    - `shouldRunLTLBenchmark()`
    - `shouldRunPDLBenchmark()`
    - `shouldRunHybridLogicBenchmark()`

- [ ] T023 [P] [US5] Create SatisfiabilityTest in `test/lotrec/logics/SatisfiabilityTest.java` (~15 tests):
  - **Note**: All tests use `Launcher.treatArgsForBenchmark()` with infix formulas
  - Nested class `PropositionalLogic`:
    - `shouldFindAtomSatisfiable()` - args: {"Classical-Propositional-Logic", "P", "true"}
    - `shouldFindContradictionUnsatisfiable()` - args: {"Classical-Propositional-Logic", "P & ~P", "true"}
    - `shouldFindTautologyValid()` - test ~(P | ~P) is unsatisfiable
    - `shouldFindConjunctionSatisfiable()` - args: {"Classical-Propositional-Logic", "P & Q", "true"}
    - `shouldFindImplicationTautologyValid()` - test ~(P -> P) is unsatisfiable
  - Nested class `ModalLogicK`:
    - `shouldVerifyKAxiom()` - test ~([]( P -> Q) -> ([]P -> []Q)) is unsatisfiable
    - `shouldFindNecPSatisfiable()` - args: {"Monomodal-K", "[]P", "true"}
    - `shouldFindPosPSatisfiable()` - args: {"Monomodal-K", "<>P", "true"}
    - `shouldFindNecFalsumUnsatisfiable()` - args: {"Monomodal-K", "[]FALSE", "true"}
  - Nested class `TestingFormulasFromLogics`:
    - `shouldExtractTestingFormulasAsInfix()` - use expression.getCodeString()
    - `shouldRunClassicalPropositionalTestingFormulas()`
    - `shouldRunMonomodalKTestingFormulas()`
    - `shouldRunS4TestingFormulas()`
    - `shouldRunAllPredefinedTestingFormulas()` - parameterized for all 38 logics
    - `shouldVerifyExpectedSatisfiabilityResults()` - check theorem/non-theorem

---

## Phase 8: Verification & Polish

> Final verification and coverage analysis.

- [ ] T024 Run full test suite with `./gradlew clean test` and verify zero failures
- [ ] T025 Generate coverage report with `./gradlew jacocoTestReport` and verify 60%+ coverage for:
  - `lotrec.parser`
  - `lotrec.dataStructure`
  - `lotrec.process`
  - `lotrec.engine`
- [ ] T026 Review and document any discovered issues or limitations in `specs/001-exhaustive-test-suite/notes.md`

---

## Parallel Execution Guide

### Independent Tasks (Can Run in Parallel)

After completing Phase 2 (T004 - TestFixtures), the following tasks are independent and can be executed in parallel:

**Batch 1** (Expression/Data Structure - Phase 3):
- T005 (ConnectorTest) | T006 (ConstantExpressionTest) | T007 (VariableExpressionTest)

**Batch 2** (More Expression Tests - Phase 3):
- T008 (ExpressionMatchingTest) | T009 (MarkedExpressionTest) | T010 (RuleTest)

**Batch 3** (Parser Tests - Phase 3):
- T011 (ExpressionParserTest) | T012 (StrategyParserTest)

**Batch 4** (Registry Tests - Phase 4):
- T013 (ConditionRegistryTest) | T014 (ActionRegistryTest)

**Batch 5** (XML Tests - Phase 5):
- T015 (LogicXMLParserTest) | T016 (PredefinedLogicsLoadTest) | T017 (PredefinedLogicsSaveTest)

**Batch 6** (Strategy Tests - Phase 6):
- T018 (StrategyTest) | T019 (RepeatTest) | T020 (FirstRuleTest) | T021 (AllRulesTest)

**Batch 7** (Engine Tests via Launcher Benchmark - Phase 7):
- T022 (LauncherBenchmarkTest) | T023 (SatisfiabilityTest)

---

## Implementation Strategy

### MVP Scope (Recommended First Milestone)

Complete **Phases 1-4** (Tasks T001-T014) to establish:
1. Working test infrastructure
2. Full expression type coverage
3. Registry validation for all conditions/actions
4. ~160 tests with foundational coverage

### Incremental Delivery

| Milestone | Phases | Tasks | Tests | Coverage Target |
|-----------|--------|-------|-------|-----------------|
| MVP | 1-4 | T001-T014 | ~160 | 40% |
| XML | 5 | T015-T017 | ~65 | 55% |
| Strategy | 6 | T018-T021 | ~30 | 60% |
| Full | 7-8 | T022-T026 | ~40 | 65%+ |

---

## Test Count Summary

| User Story / Tier | Task IDs | Estimated Tests |
|-------------------|----------|-----------------|
| US1: Expression & Data Structure (Tier 1-2) | T005-T012 | ~100 |
| US2: Registry (Tier 3) | T013-T014 | ~50 |
| US3: XML Loading (Tier 4-5) | T015-T017 | ~65 |
| US4: Strategy Execution (Tier 6) | T018-T021 | ~30 |
| US5: Engine Execution (Tier 7) | T022-T023 | ~40 |
| **TOTAL** | | **~285** |

---

## Verification Commands

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "ConnectorTest"

# Run tests in specific package
./gradlew test --tests "lotrec.parser.*"

# Generate coverage report
./gradlew jacocoTestReport
# Report at: build/reports/jacoco/test/html/index.html

# Full verification
./gradlew clean test jacocoTestReport
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

*Generated: 2026-01-28 | Based on FEAT-001 Specification and Plan*
