# Specification: [FEAT-001] Exhaustive Test Suite for LoTREC

> Comprehensive test suite capturing and validating LoTREC's parsing, data structure, logic file handling, and theorem proving engine behavior.

## Metadata

| Field | Value |
|-------|-------|
| **Spec ID** | FEAT-001 |
| **Author** | Claude Code |
| **Created** | 2026-01-28 |
| **Status** | Draft |
| **Priority** | Critical |
| **Estimated Complexity** | Large |

---

## 1. Summary

This specification defines an exhaustive test suite for the LoTREC theorem prover that systematically validates all core components from basic parsing through integration-level engine execution. The test suite will capture current behavior as characterization tests, enabling safe future refactoring while ensuring maximum code coverage. Tests are organized progressively from unit tests of individual components to integration tests verifying complete proof workflows. GUI testing is explicitly excluded and deferred to a separate specification.

## 2. Motivation

### Problem Statement
LoTREC is a research-oriented theorem prover with approximately 771 Java files and 49K lines of code, yet currently has minimal test coverage (4 test classes with 84 tests). Without comprehensive tests, any modernization effort risks breaking the core theorem proving logic, which must be preserved. The existing tests cover only tokenization, infix/prefix transformation, and basic Logic data structure operations, leaving critical areas untested.

### Use Cases
1. **UC-1: Safe Refactoring** - Developers need confidence that refactoring code will not break existing functionality; characterization tests capture current behavior as the "correct" baseline.
2. **UC-2: Regression Prevention** - Any changes to parsing, rule execution, or strategy application must not break existing predefined logics.
3. **UC-3: Logic Definition Validation** - Users defining new logics need assurance that the parser correctly interprets their XML definitions.
4. **UC-4: Satisfiability Verification** - Researchers using LoTREC need confidence that satisfiability/validity results are correct.
5. **UC-5: Continuous Integration** - Enable automated testing in CI/CD pipelines to catch regressions early.

### Success Criteria
- [ ] All 38 predefined logic files load successfully without errors
- [ ] All predefined logic files can be saved and reloaded identically
- [ ] Test coverage reaches 60% or higher for core packages (parser, dataStructure, process, engine)
- [ ] All registered conditions (20+) and actions (15+) have dedicated test cases
- [ ] Testing formulas from predefined logics execute with expected satisfiability results
- [ ] Test execution completes within 5 minutes for the full suite
- [ ] Zero test failures when run against unmodified codebase

---

## 3. Domain Context

> LoTREC-specific concepts relevant to this feature.

### Logical Concepts

| Term | Definition |
|------|------------|
| **Tableau Method** | A proof procedure that builds a tree structure to test satisfiability by decomposing formulas |
| **Satisfiability** | A formula is satisfiable if there exists a model (interpretation) that makes it true |
| **Validity** | A formula is valid if it is true in all models; validity = unsatisfiability of negation |
| **Closed Tableau** | A tableau where every branch contains a contradiction (FALSE); indicates unsatisfiability |
| **Open Tableau** | A tableau with at least one branch without contradictions; indicates satisfiability |
| **Modal Logic** | Logic extended with necessity (box/nec) and possibility (diamond/pos) operators |
| **Strategy** | A meta-level control structure specifying rule application order |
| **Connector** | A logical operator (not, and, or, imp, nec, pos, etc.) |

### Related Logic Components
- **Connectors involved**: All connectors defined in logic XML files (not, and, or, imp, equiv, nec, pos, etc.)
- **Expression types**: ConstantExpression, VariableExpression, VariableNodeExpression, ExpressionWithSubExpressions, MarkedExpression
- **Tableaux elements**: Node, TableauNode, Graph, Tableau, Wallet

---

## 4. Requirements

### 4.1 Functional Requirements

| ID | Requirement | Priority |
|----|-------------|----------|
| **FR-1** | The test suite shall validate parsing of all expression types (constants, variables, node variables, composite expressions with connectors) | Must |
| **FR-2** | The test suite shall validate parsing of all registered conditions (20+ condition types) | Must |
| **FR-3** | The test suite shall validate parsing of all registered actions (15+ action types) | Must |
| **FR-4** | The test suite shall validate parsing of all strategy constructs (repeat, firstRule, allRules, nested strategies) | Must |
| **FR-5** | The test suite shall verify all 38 predefined logic XML files load without errors | Must |
| **FR-6** | The test suite shall verify logic files can be saved and reloaded with identical structure | Must |
| **FR-7** | The test suite shall validate Connector data structure operations (creation, arity, priority, output format) | Must |
| **FR-8** | The test suite shall validate Rule data structure operations (conditions, actions, execution) | Must |
| **FR-9** | The test suite shall validate Strategy data structure operations (code parsing, worker creation) | Must |
| **FR-10** | The test suite shall validate Expression matching and instantiation mechanisms | Must |
| **FR-11** | The test suite shall verify engine execution produces correct closure/open results for known formulas | Must |
| **FR-12** | The test suite shall validate the EventMachine rule activation and action execution pipeline | Should |
| **FR-13** | The test suite shall test Repeat, FirstRule, and AllRules strategy execution patterns | Should |
| **FR-14** | The test suite shall validate parameter type handling (NODE, FORMULA, RELATION, MARK) | Should |
| **FR-15** | The test suite shall test error handling for malformed XML, invalid expressions, and missing components | Should |
| **FR-16** | The test suite shall enable creation and validation of new user-defined logics | Could |

### 4.2 Non-Functional Requirements

| ID | Requirement | Metric |
|----|-------------|--------|
| **NFR-1** | Test suite execution shall complete within reasonable time | < 5 minutes for full suite |
| **NFR-2** | Tests shall be deterministic and repeatable | 100% consistent results across runs |
| **NFR-3** | Tests shall not require external resources or network access | Offline execution |
| **NFR-4** | Tests shall not modify the production codebase | Read-only verification |
| **NFR-5** | Test reports shall clearly indicate pass/fail status and coverage metrics | JaCoCo HTML report |

---

## 5. Tech Stack Constraints Checklist

> Verify compliance with constitution before proceeding.

- [x] **Java 1.8**: No Java 9+ features used in tests
- [x] **Gradle Build**: Tests run via `./gradlew test`
- [x] **Swing/AWT**: GUI testing excluded from this specification
- [x] **Cytoscape 2.x**: Visualization testing excluded from this specification
- [x] **JUnit 5**: Tests use JUnit 5 Jupiter annotations (@Test, @BeforeEach, @Nested, @DisplayName)
- [x] **AssertJ**: Fluent assertions using AssertJ library
- [x] **No New Dependencies**: Uses existing test dependencies (JUnit 5, AssertJ, JaCoCo)

---

## 6. Architecture Constraints

### Target Layer
- [x] Core Logic (`lotrec.dataStructure.*`) - Primary test target
- [x] Parser (`lotrec.parser`) - Primary test target
- [x] Process Engine (`lotrec.process`) - Primary test target
- [x] GUI (`lotrec.gui.*`) - Excluded from this specification
- [ ] Visualization (`cytoscape.*`) - Excluded from this specification
- [x] Resources (`lotrec.resources`) - Test data source

### Target Packages
| New/Modified Class | Package | Justification |
|--------------------|---------|---------------|
| LogicXMLParserTest | `test/lotrec/parser/` | Tests XML parsing of logic files |
| ConnectorTest | `test/lotrec/dataStructure/expression/` | Tests Connector data structure |
| ExpressionTest | `test/lotrec/dataStructure/expression/` | Tests Expression hierarchy |
| RuleTest | `test/lotrec/dataStructure/tableau/` | Tests Rule data structure |
| StrategyTest | `test/lotrec/process/` | Tests Strategy execution |
| ConditionTest | `test/lotrec/dataStructure/tableau/condition/` | Tests condition parsing/matching |
| ActionTest | `test/lotrec/dataStructure/tableau/action/` | Tests action execution |
| EngineTest | `test/lotrec/engine/` | Integration tests for proof execution |
| PredefinedLogicsTest | `test/lotrec/logics/` | Integration tests for all logic files |

### Dependency Analysis
- **Will import from**: `lotrec.parser`, `lotrec.dataStructure.*`, `lotrec.process`, `lotrec.engine`
- **Will be imported by**: None - test code only
- **Dependency direction valid?**: [x] Yes - tests are leaf components

---

## 7. Existing Code to Reuse

> Search before creating. What existing code can be leveraged?

### Similar Implementations
| Existing Code | Location | How to Reuse |
|--------------|----------|--------------|
| OldiesTokenizerTest | `test/lotrec/parser/` | Pattern for parser testing with nested groups |
| PriorityInfixToPrefixTest | `test/gi/transformers/` | Pattern for transformation testing |
| LogicTest | `test/lotrec/dataStructure/` | Pattern for data structure testing |
| LogicXMLParser | `src/lotrec/parser/` | Use directly to load test fixtures |
| Predefined logics | `src/lotrec/logics/*.xml` | Use as test fixtures |

### Extension Points
- [x] Uses existing class: LogicXMLParser for loading test fixtures
- [x] Uses existing class: OldiesTokenizer for parsing test expressions
- [x] Uses existing patterns: @Nested groups, @DisplayName annotations

### Registration Requirements
No registration required - tests only validate existing registrations.

---

## 8. Proposed Solution

### 8.1 Overview

The test suite is organized into 7 test tiers, progressing from basic unit tests to full integration tests:

1. **Tier 1 - Expression Parsing (Unit)**: Individual expression type parsing
2. **Tier 2 - Data Structure (Unit)**: Connector, Rule, Strategy, Logic structures
3. **Tier 3 - Condition/Action Parsing (Unit)**: All registered conditions and actions
4. **Tier 4 - Logic XML Loading (Integration)**: Complete logic file parsing
5. **Tier 5 - Logic Save/Reload (Integration)**: Round-trip serialization
6. **Tier 6 - Strategy Execution (Integration)**: Rule application patterns
7. **Tier 7 - Engine Execution (Integration)**: Full proof search with result verification

### 8.2 Test Organization

```
test/
├── lotrec/
│   ├── parser/
│   │   ├── OldiesTokenizerTest.java          [EXISTING - 34 tests]
│   │   ├── ExpressionParserTest.java         [NEW - Tier 1]
│   │   ├── ConditionParserTest.java          [NEW - Tier 3]
│   │   ├── ActionParserTest.java             [NEW - Tier 3]
│   │   ├── StrategyParserTest.java           [NEW - Tier 1]
│   │   └── LogicXMLParserTest.java           [NEW - Tier 4/5]
│   ├── dataStructure/
│   │   ├── LogicTest.java                    [EXISTING - 29 tests]
│   │   ├── expression/
│   │   │   ├── ConnectorTest.java            [NEW - Tier 2]
│   │   │   ├── ConstantExpressionTest.java   [NEW - Tier 2]
│   │   │   ├── VariableExpressionTest.java   [NEW - Tier 2]
│   │   │   ├── ExpressionMatchingTest.java   [NEW - Tier 2]
│   │   │   └── MarkedExpressionTest.java     [NEW - Tier 2]
│   │   └── tableau/
│   │       ├── RuleTest.java                 [NEW - Tier 2]
│   │       ├── ParameterTest.java            [NEW - Tier 2]
│   │       ├── condition/
│   │       │   └── ConditionRegistryTest.java [NEW - Tier 3]
│   │       └── action/
│   │           └── ActionRegistryTest.java   [NEW - Tier 3]
│   ├── process/
│   │   ├── StrategyTest.java                 [NEW - Tier 2]
│   │   ├── RepeatTest.java                   [NEW - Tier 6]
│   │   ├── FirstRuleTest.java                [NEW - Tier 6]
│   │   ├── AllRulesTest.java                 [NEW - Tier 6]
│   │   └── EventMachineTest.java             [NEW - Tier 6]
│   ├── engine/
│   │   └── EngineTest.java                   [NEW - Tier 7]
│   └── logics/
│       ├── PredefinedLogicsLoadTest.java     [NEW - Tier 4]
│       ├── PredefinedLogicsSaveTest.java     [NEW - Tier 5]
│       └── SatisfiabilityTest.java           [NEW - Tier 7]
├── gi/
│   └── transformers/
│       └── PriorityInfixToPrefixTest.java    [EXISTING - 20 tests]
```

### 8.3 Test Tier Details

#### Tier 1: Expression Parsing Tests (~40 tests)

| Test Class | Test Method | Description |
|------------|-------------|-------------|
| ExpressionParserTest | shouldParseConstant | Parse uppercase letter as ConstantExpression |
| ExpressionParserTest | shouldParseVariable | Parse `_var` as VariableExpression |
| ExpressionParserTest | shouldParseNodeVariable | Parse `n_var` as VariableNodeExpression |
| ExpressionParserTest | shouldParseUnaryConnector | Parse `not P` as ExpressionWithSubExpressions |
| ExpressionParserTest | shouldParseBinaryConnector | Parse `and P Q` as composite expression |
| ExpressionParserTest | shouldParseNestedExpression | Parse `and P not Q` with proper nesting |
| ExpressionParserTest | shouldParseDeeplyNested | Parse complex nested formulas |
| ExpressionParserTest | shouldParseModalOperators | Parse `nec P` and `pos P` |
| ExpressionParserTest | shouldHandleKeywordConstants | Parse `constant A`, `variable x` |
| ExpressionParserTest | shouldRejectMalformedExpression | Verify ParseException for invalid input |
| StrategyParserTest | shouldParseRepeatBlock | Parse `repeat ... end` |
| StrategyParserTest | shouldParseFirstRuleBlock | Parse `firstRule R1 R2 end` |
| StrategyParserTest | shouldParseAllRulesBlock | Parse `allRules R1 R2 end` |
| StrategyParserTest | shouldParseNestedStrategies | Parse strategies calling other strategies |

#### Tier 2: Data Structure Tests (~60 tests)

| Test Class | Test Method | Description |
|------------|-------------|-------------|
| ConnectorTest | shouldCreateWithArity | Test unary, binary, ternary arities |
| ConnectorTest | shouldSetOutputFormat | Test `~ _`, `_ & _` formats |
| ConnectorTest | shouldSetPriority | Test precedence values |
| ConnectorTest | shouldSetAssociative | Test associativity flag |
| ConstantExpressionTest | shouldMatchIdentical | Test P matches P |
| ConstantExpressionTest | shouldNotMatchDifferent | Test P not matches Q |
| ConstantExpressionTest | shouldHaveFalsumConstant | Test ConstantExpression.FALSUM |
| VariableExpressionTest | shouldMatchAnyExpression | Test pattern matching |
| VariableExpressionTest | shouldBindInInstanceSet | Test variable instantiation |
| ExpressionMatchingTest | shouldMatchCompositeExpressions | Test structural matching |
| ExpressionMatchingTest | shouldGetInstance | Test substitution application |
| MarkedExpressionTest | shouldWrapExpression | Test mark/annotation operations |
| RuleTest | shouldAddConditions | Test condition management |
| RuleTest | shouldAddActions | Test action management |
| RuleTest | shouldCreateEventMachine | Test machine creation |
| ParameterTest | shouldCreateNodeParameter | Test NODE type |
| ParameterTest | shouldCreateFormulaParameter | Test FORMULA type |
| ParameterTest | shouldCreateRelationParameter | Test RELATION type |
| ParameterTest | shouldCreateMarkParameter | Test MARK type |
| StrategyTest | shouldParseCode | Test code parsing |
| StrategyTest | shouldCreateWorkers | Test worker hierarchy |

#### Tier 3: Condition/Action Registry Tests (~50 tests)

| Test Class | Test Method | Description |
|------------|-------------|-------------|
| ConditionRegistryTest | shouldHaveAllConditionsRegistered | Verify 20+ conditions in CLASSES_KEYWORDS |
| ConditionRegistryTest | shouldInstantiateHasElement | Test ExpressionCondition |
| ConditionRegistryTest | shouldInstantiateHasNotElement | Test NotExpressionCondition |
| ConditionRegistryTest | shouldInstantiateIsAtomic | Test IsAtomicCondition |
| ConditionRegistryTest | shouldInstantiateIsLinked | Test LinkCondition |
| ConditionRegistryTest | shouldInstantiateIsMarked | Test MarkCondition |
| ConditionRegistryTest | shouldInstantiateAreIdentical | Test IdenticalCondition |
| ConditionRegistryTest | shouldInstantiateIsAncestor | Test AncestorCondition |
| ConditionRegistryTest | shouldParseConditionWithParameters | Test parameter extraction |
| ActionRegistryTest | shouldHaveAllActionsRegistered | Verify 15+ actions in CLASSES_KEYWORDS |
| ActionRegistryTest | shouldInstantiateAdd | Test AddExpressionAction |
| ActionRegistryTest | shouldInstantiateCreateNewNode | Test AddNodeAction |
| ActionRegistryTest | shouldInstantiateLink | Test LinkAction |
| ActionRegistryTest | shouldInstantiateMark | Test MarkAction |
| ActionRegistryTest | shouldInstantiateStop | Test StopStrategyAction |
| ActionRegistryTest | shouldInstantiateDuplicate | Test DuplicateAction |
| ActionRegistryTest | shouldParseActionWithParameters | Test parameter extraction |

#### Tier 4: Logic XML Loading Tests (~45 tests)

| Test Class | Test Method | Description |
|------------|-------------|-------------|
| LogicXMLParserTest | shouldParseSimpleLogic | Load minimal logic with one connector |
| LogicXMLParserTest | shouldParseConnectors | Verify connector properties |
| LogicXMLParserTest | shouldParseRules | Verify rule conditions and actions |
| LogicXMLParserTest | shouldParseStrategies | Verify strategy code parsing |
| LogicXMLParserTest | shouldParseTestingFormulas | Verify formula extraction |
| LogicXMLParserTest | shouldSetMainStrategy | Verify main strategy reference |
| LogicXMLParserTest | shouldHandleMissingOptionalFields | Test graceful handling |
| LogicXMLParserTest | shouldRejectInvalidXML | Test error handling |
| PredefinedLogicsLoadTest | shouldLoadClassicalPropositional | Load Classical-Propositional-Logic.xml |
| PredefinedLogicsLoadTest | shouldLoadMonomodalK | Load Monomodal-K.xml |
| PredefinedLogicsLoadTest | shouldLoadS4 | Load S4-Explicit-R.xml |
| PredefinedLogicsLoadTest | shouldLoadAllPredefinedLogics | Parameterized test for all 38 logics |

#### Tier 5: Logic Save/Reload Tests (~20 tests)

| Test Class | Test Method | Description |
|------------|-------------|-------------|
| PredefinedLogicsSaveTest | shouldSaveAndReloadConnectors | Verify connector round-trip |
| PredefinedLogicsSaveTest | shouldSaveAndReloadRules | Verify rule round-trip |
| PredefinedLogicsSaveTest | shouldSaveAndReloadStrategies | Verify strategy round-trip |
| PredefinedLogicsSaveTest | shouldPreserveMainStrategy | Verify main strategy preserved |
| PredefinedLogicsSaveTest | shouldPreserveTestingFormulas | Verify formulas preserved |
| PredefinedLogicsSaveTest | shouldSaveNewLogic | Create and save new logic definition |
| PredefinedLogicsSaveTest | shouldReloadSavedLogic | Reload saved logic successfully |

#### Tier 6: Strategy Execution Tests (~30 tests)

| Test Class | Test Method | Description |
|------------|-------------|-------------|
| RepeatTest | shouldRepeatUntilQuiet | Test repeat loop termination |
| RepeatTest | shouldApplyAllWorkersPerIteration | Test fair scheduling |
| FirstRuleTest | shouldApplyFirstMatchingRule | Test depth-first application |
| FirstRuleTest | shouldReturnAfterFirstMatch | Test early termination |
| AllRulesTest | shouldApplyAllMatchingRules | Test parallel application |
| EventMachineTest | shouldQueueEvents | Test event queueing |
| EventMachineTest | shouldMatchConditions | Test condition evaluation |
| EventMachineTest | shouldExecuteActions | Test action execution |

#### Tier 7: Engine Execution Tests (~40 tests)

| Test Class | Test Method | Description |
|------------|-------------|-------------|
| EngineTest | shouldBuildTableauFromFormula | Test initial tableau creation |
| EngineTest | shouldApplyStrategy | Test strategy execution |
| EngineTest | shouldDetectContradiction | Test FALSE detection and closure |
| EngineTest | shouldLeaveOpenTableau | Test satisfiable formula |
| EngineTest | shouldCreateSuccessorNodes | Test pos rule creating nodes |
| EngineTest | shouldPropagateNecessity | Test nec rule propagation |
| SatisfiabilityTest | shouldVerifyKTheorems | Test known K theorems |
| SatisfiabilityTest | shouldVerifyKNonTheorems | Test known K non-theorems |
| SatisfiabilityTest | shouldVerifyS4Theorems | Test S4-specific theorems |
| SatisfiabilityTest | shouldRunAllTestingFormulas | Run all formulas from all logics |

### 8.4 Test Data Strategy

1. **Predefined Logics**: Use existing 38 logic XML files as primary test fixtures
2. **Inline Test Data**: Simple expressions created inline in test methods
3. **Testing Formulas**: Use formulas defined in logic XML files
4. **Minimal Test Logics**: Create minimal logic definitions for isolated unit tests
5. **Expected Results**: Document expected satisfiability for known formulas from literature

---

## 9. Test Strategy

### 9.1 Test Categories

| Category | Count | Description |
|----------|-------|-------------|
| Unit Tests | ~150 | Individual component testing (Tiers 1-3) |
| Integration Tests | ~135 | Component interaction testing (Tiers 4-7) |
| Manual Tests | 0 | None - all tests automated |

### 9.2 JUnit 5 Test Template

```java
package lotrec.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

@DisplayName("LogicXMLParser")
class LogicXMLParserTest {

    private LogicXMLParser parser;

    @BeforeEach
    void setUp() {
        parser = new LogicXMLParser();
    }

    @Nested
    @DisplayName("Connector Parsing")
    class ConnectorParsing {

        @Test
        @DisplayName("should parse connector with all properties")
        void shouldParseConnectorWithAllProperties() {
            // Given
            String logicFile = "src/lotrec/logics/Classical-Propositional-Logic.xml";

            // When
            Logic logic = parser.parseLogic(logicFile);

            // Then
            assertThat(logic.getConnectors()).isNotEmpty();
            Connector notConnector = logic.getConnector("not");
            assertThat(notConnector).isNotNull();
            assertThat(notConnector.getArity()).isEqualTo(1);
        }
    }
}
```

### 9.3 Predefined Logic Testing Matrix

| Logic File | Connectors | Rules | Strategies | Testing Formulas |
|------------|------------|-------|------------|------------------|
| Classical-Propositional-Logic.xml | 5 | ~10 | 1 | 3 |
| Monomodal-K.xml | 7 | ~15 | 2 | 6 |
| S4-Explicit-R.xml | 7 | ~20 | 2 | 12 |
| Intuitionistic-Logic-Lj.xml | 5 | ~12 | 1 | ~5 |
| LTL.xml | ~8 | ~15 | 1 | ~5 |
| PDL.xml | ~10 | ~20 | 1 | ~5 |
| *... (38 total)* | | | | |

### 9.4 Coverage Targets

| Package | Target Coverage | Rationale |
|---------|----------------|-----------|
| `lotrec.parser` | 80% | Critical for all logic loading |
| `lotrec.dataStructure.expression` | 75% | Core formula representation |
| `lotrec.dataStructure.tableau` | 70% | Rule and condition framework |
| `lotrec.process` | 70% | Strategy execution |
| `lotrec.engine` | 60% | Integration point |
| **Overall** | **60%** | Phase 1 target |

---

## 10. Risks and Mitigations

| Risk | Likelihood | Impact | Mitigation |
|------|------------|--------|------------|
| Tests expose bugs in existing code | Medium | Medium | Document as known issues, don't fix in this phase |
| XML parsing variations across files | Medium | Low | Use parameterized tests with all 38 logics |
| Engine execution non-determinism | Low | High | Use deterministic test formulas, add timeouts |
| Reflection-based instantiation failures | Low | Medium | Test all registered conditions/actions explicitly |
| Test execution time too long | Medium | Medium | Organize tests by tier, allow selective execution |

---

## 11. Open Questions

| # | Question | Status | Resolution |
|---|----------|--------|------------|
| Q1 | Should tests modify any predefined logic files? | Resolved | No - use temp files for save tests |
| Q2 | How to handle logic files with known issues? | Resolved | Document issues, test what works |
| Q3 | Which testing formulas have documented expected results? | Resolved | Use comments in XML files (theorem/non-theorem) |

---

## 12. References

- Constitution: `.specify/memory/constitution.md`
- Roadmap: `.specify/memory/roadmap.md`
- CLAUDE.md: Project guidance document
- Existing Tests: `test/lotrec/parser/OldiesTokenizerTest.java`, `test/lotrec/dataStructure/LogicTest.java`
- Logic DTD: `src/lotrec/logics/logic.dtd`

---

## Appendix A: Complete List of Predefined Logics

1. Classical-Propositional-Logic.xml
2. Monomodal-K.xml
3. S4-Explicit-R.xml
4. S4-with-history.xml
5. S4Optimal.xml
6. S5-explicit-edges.xml
7. KD.xml
8. KD45.xml
9. KT-explicit-edges.xml
10. KB-explicit-edges.xml
11. K4-explicit-edges.xml
12. K4-implicit-edges.xml
13. K4Confluence.xml
14. KD45Optimal.xml
15. K45Optimal.xml
16. K5.xml
17. KB5.xml
18. KBT.xml
19. KBD.xml
20. Kalt1.xml
21. KMinimal.xml
22. K+Universal.xml
23. K2-with-Inclusion.xml
24. Multimodal-Kn.xml
25. KConfluence.xml
26. Intuitionistic-Logic-Lj.xml
27. Hybrid-Logic-H-at.xml
28. LTL.xml
29. PDL.xml
30. Model-Checking-Monomodal.xml
31. Model-Checking-Multimodal.xml
32. Multi-S5-PAL.xml
33. xstit.xml
34. LJminimal.xml
35-38. (Additional logic files as discovered)

---

## Appendix B: Registered Conditions and Actions

### Conditions (AbstractCondition.CLASSES_KEYWORDS)

| XML Keyword | Class Name | Parameters |
|-------------|------------|------------|
| hasElement | ExpressionCondition | node, formula |
| hasNotElement | NotExpressionCondition | node, formula |
| isAtomic | IsAtomicCondition | formula |
| isNotAtomic | IsNotAtomicCondition | formula |
| isLinked | LinkCondition | node, node, relation |
| isNotLinked | NotLinkCondition | node, node, relation |
| hasNoSuccessor | HasNotSuccessorCondition | node, relation |
| hasNoParents | HasNoParentsCondition | node, relation |
| isAncestor | AncestorCondition | node, node, relation |
| areIdentical | IdenticalCondition | node, node |
| areNotIdentical | NotIdenticalCondition | node, node |
| areNotEqual | NotEqualCondition | expression, expression |
| contains | ContainsCondition | formula, formula |
| haveSameFormulasSet | HaveSameFormulasSetCondition | node, node |
| isNewNode | NodeCreatedCondition | node |
| isMarked | MarkCondition | node, mark |
| isNotMarked | NotMarkCondition | node, mark |
| isMarkedExpression | MarkExpressionCondition | node, formula, mark |
| isNotMarkedExpression | NotMarkExpressionCondition | node, formula, mark |
| isMarkedExpressionInAllChildren | MarkedExpressionInAllChildrenCondition | node, formula, relation |

### Actions (AbstractAction.CLASSES_KEYWORDS)

| XML Keyword | Class Name | Parameters |
|-------------|------------|------------|
| add | AddExpressionAction | node, formula |
| createNewNode | AddNodeAction | nodeName |
| link | LinkAction | source, target, relation |
| unlink | UnlinkAction | source, target, relation |
| stop | StopStrategyAction | node |
| mark | MarkAction | node, mark |
| unmark | UnmarkAction | node, mark |
| markExpressions | MarkExpressionsAction | node, formula, mark |
| unmarkExpressions | UnmarkExpressionsAction | node, formula, mark |
| createOneSuccessor | AddOneSuccessorAction | source, target, relation |
| createOneParent | AddOneParentAction | source, target, relation |
| hide | HideAction | node |
| kill | KillAction | node |
| duplicate | DuplicateAction | node |
| merge | MergeNodeInNodeAction | source, target |

---

## Approval

| Role | Name | Date | Signature |
|------|------|------|-----------|
| Author | Claude Code | 2026-01-28 | |
| Reviewer | | | |
| Approver | | | |

---

*Template version: 1.0 | Based on LoTREC Constitution*
