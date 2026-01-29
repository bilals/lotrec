package lotrec.dataStructure.tableau.condition;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.ParameterType;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for the Condition Registry in AbstractCondition.
 * Verifies all registered conditions can be instantiated and parsed correctly.
 */
@DisplayName("ConditionRegistry")
class ConditionRegistryTest {

    private Logic logic;
    private OldiesTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        logic = TestFixtures.createMinimalLogic();
        // Add a relation connector for link-based conditions
        logic.addConnector(new Connector("rel", 0, "R"));
        tokenizer = TestFixtures.createTokenizer(logic);
    }

    @Nested
    @DisplayName("Registry Verification")
    class RegistryVerification {

        @Test
        @DisplayName("should have all 20 conditions registered in CLASSES_KEYWORDS")
        void shouldHaveAllConditionsRegistered() {
            HashMap<String, String> keywords = AbstractCondition.CLASSES_KEYWORDS;

            assertThat(keywords).isNotNull();
            assertThat(keywords).hasSize(20);

            // Verify all expected condition keywords are present
            assertThat(keywords.keySet()).containsExactlyInAnyOrder(
                    "hasElement",
                    "hasNotElement",
                    "isAtomic",
                    "isNotAtomic",
                    "isLinked",
                    "isNotLinked",
                    "hasNoSuccessor",
                    "hasNoParents",
                    "isAncestor",
                    "areIdentical",
                    "areNotIdentical",
                    "areNotEqual",
                    "contains",
                    "haveSameFormulasSet",
                    "isNewNode",
                    "isMarked",
                    "isNotMarked",
                    "isMarkedExpression",
                    "isNotMarkedExpression",
                    "isMarkedExpressionInAllChildren"
            );
        }

        @Test
        @DisplayName("should map keywords to correct class names")
        void shouldMapKeywordsToCorrectClassNames() {
            HashMap<String, String> keywords = AbstractCondition.CLASSES_KEYWORDS;

            assertThat(keywords.get("hasElement")).isEqualTo("ExpressionCondition");
            assertThat(keywords.get("hasNotElement")).isEqualTo("NotExpressionCondition");
            assertThat(keywords.get("isAtomic")).isEqualTo("IsAtomicCondition");
            assertThat(keywords.get("isNotAtomic")).isEqualTo("IsNotAtomicCondition");
            assertThat(keywords.get("isLinked")).isEqualTo("LinkCondition");
            assertThat(keywords.get("isNotLinked")).isEqualTo("NotLinkCondition");
            assertThat(keywords.get("hasNoSuccessor")).isEqualTo("HasNotSuccessorCondition");
            assertThat(keywords.get("hasNoParents")).isEqualTo("HasNoParentsCondition");
            assertThat(keywords.get("isAncestor")).isEqualTo("AncestorCondition");
            assertThat(keywords.get("areIdentical")).isEqualTo("IdenticalCondition");
            assertThat(keywords.get("areNotIdentical")).isEqualTo("NotIdenticalCondition");
            assertThat(keywords.get("areNotEqual")).isEqualTo("NotEqualCondition");
            assertThat(keywords.get("contains")).isEqualTo("ContainsCondition");
            assertThat(keywords.get("haveSameFormulasSet")).isEqualTo("HaveSameFormulasSetCondition");
            assertThat(keywords.get("isNewNode")).isEqualTo("NodeCreatedCondition");
            assertThat(keywords.get("isMarked")).isEqualTo("MarkCondition");
            assertThat(keywords.get("isNotMarked")).isEqualTo("NotMarkCondition");
            assertThat(keywords.get("isMarkedExpression")).isEqualTo("MarkExpressionCondition");
            assertThat(keywords.get("isNotMarkedExpression")).isEqualTo("NotMarkExpressionCondition");
            assertThat(keywords.get("isMarkedExpressionInAllChildren")).isEqualTo("MarkedExpressionInAllChildrenCondition");
        }

        @Test
        @DisplayName("should have CONDITIONS_PACKAGE set correctly")
        void shouldHaveConditionsPackageSetCorrectly() {
            assertThat(AbstractCondition.CONDITIONS_PACKAGE)
                    .isEqualTo("lotrec.dataStructure.tableau.condition.");
        }

        @Test
        @DisplayName("should get all condition keywords")
        void shouldGetConditionKeywords() {
            Set<String> keywords = AbstractCondition.CLASSES_KEYWORDS.keySet();

            assertThat(keywords).hasSize(20);
            assertThat(keywords).contains("hasElement", "isLinked", "isMarked", "isNewNode");
        }
    }

    @Nested
    @DisplayName("Condition Parsing - Node and Formula")
    class ConditionParsingNodeAndFormula {

        @Test
        @DisplayName("should parse hasElement condition")
        void shouldParseHasElement() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n P");

            assertThat(cond).isInstanceOf(ExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasElement");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
        }

        @Test
        @DisplayName("should parse hasNotElement condition")
        void shouldParseHasNotElement() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasNotElement ?n P");

            assertThat(cond).isInstanceOf(NotExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasNotElement");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
        }

        @Test
        @DisplayName("should parse hasElement with complex formula")
        void shouldParseHasElementWithComplexFormula() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n and P Q");

            assertThat(cond).isInstanceOf(ExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasElement");
            assertThat(cond.getParameters()).hasSize(2);
        }

        @Test
        @DisplayName("should parse hasElement with variable formula")
        void shouldParseHasElementWithVariableFormula() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n _A");

            assertThat(cond).isInstanceOf(ExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasElement");
        }
    }

    @Nested
    @DisplayName("Condition Parsing - Atomic Tests")
    class ConditionParsingAtomicTests {

        // Note: isAtomic and isNotAtomic are registered in CLASSES_KEYWORDS
        // but not implemented in OldiesTokenizer.parseCondition()
        // These tests document the expected behavior once implemented

        @Test
        @DisplayName("should have isAtomic registered in CLASSES_KEYWORDS")
        void shouldHaveIsAtomicRegistered() {
            assertThat(AbstractCondition.CLASSES_KEYWORDS).containsKey("isAtomic");
            assertThat(AbstractCondition.CLASSES_KEYWORDS.get("isAtomic"))
                    .isEqualTo("IsAtomicCondition");
        }

        @Test
        @DisplayName("should have isNotAtomic registered in CLASSES_KEYWORDS")
        void shouldHaveIsNotAtomicRegistered() {
            assertThat(AbstractCondition.CLASSES_KEYWORDS).containsKey("isNotAtomic");
            assertThat(AbstractCondition.CLASSES_KEYWORDS.get("isNotAtomic"))
                    .isEqualTo("IsNotAtomicCondition");
        }

        @Test
        @DisplayName("isAtomic condition class exists and can be instantiated")
        void isAtomicConditionClassExists() {
            // Verify the condition class can be instantiated directly
            lotrec.dataStructure.expression.VariableExpression expr =
                    new lotrec.dataStructure.expression.VariableExpression("A");
            IsAtomicCondition cond = new IsAtomicCondition(expr);

            assertThat(cond).isNotNull();
            assertThat(cond.createRestriction()).isNotNull();
        }

        @Test
        @DisplayName("isNotAtomic condition class exists and can be instantiated")
        void isNotAtomicConditionClassExists() {
            lotrec.dataStructure.expression.VariableExpression expr =
                    new lotrec.dataStructure.expression.VariableExpression("A");
            IsNotAtomicCondition cond = new IsNotAtomicCondition(expr);

            assertThat(cond).isNotNull();
            assertThat(cond.createRestriction()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Condition Parsing - Link Conditions")
    class ConditionParsingLinkConditions {

        @Test
        @DisplayName("should parse isLinked condition")
        void shouldParseIsLinked() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isLinked ?n ?m R");

            assertThat(cond).isInstanceOf(LinkCondition.class);
            assertThat(cond.getName()).isEqualTo("isLinked");
            assertThat(cond.getParameters()).hasSize(3);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(2).getType()).isEqualTo(ParameterType.RELATION);
        }

        @Test
        @DisplayName("should parse isNotLinked condition")
        void shouldParseIsNotLinked() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNotLinked ?n ?m R");

            assertThat(cond).isInstanceOf(NotLinkCondition.class);
            assertThat(cond.getName()).isEqualTo("isNotLinked");
            assertThat(cond.getParameters()).hasSize(3);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(2).getType()).isEqualTo(ParameterType.RELATION);
        }

        @Test
        @DisplayName("should parse hasNoSuccessor condition")
        void shouldParseHasNoSuccessor() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasNoSuccessor ?n R");

            assertThat(cond).isInstanceOf(HasNotSuccessorCondition.class);
            assertThat(cond.getName()).isEqualTo("hasNoSuccessor");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.RELATION);
        }

        @Test
        @DisplayName("should have hasNoParents registered in CLASSES_KEYWORDS")
        void shouldHaveHasNoParentsRegistered() {
            assertThat(AbstractCondition.CLASSES_KEYWORDS).containsKey("hasNoParents");
            assertThat(AbstractCondition.CLASSES_KEYWORDS.get("hasNoParents"))
                    .isEqualTo("HasNoParentsCondition");
        }

        @Test
        @DisplayName("hasNoParents condition class exists and can be instantiated")
        void hasNoParentsConditionClassExists() {
            lotrec.dataStructure.expression.StringSchemeVariable node =
                    new lotrec.dataStructure.expression.StringSchemeVariable("n");
            HasNoParentsCondition cond = new HasNoParentsCondition(node);

            assertThat(cond).isNotNull();
            assertThat(cond.createRestriction()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Condition Parsing - Node Identity")
    class ConditionParsingNodeIdentity {

        @Test
        @DisplayName("should parse isAncestor condition")
        void shouldParseIsAncestor() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isAncestor ?n ?m");

            assertThat(cond).isInstanceOf(AncestorCondition.class);
            assertThat(cond.getName()).isEqualTo("isAncestor");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
        }

        @Test
        @DisplayName("should parse areIdentical condition")
        void shouldParseAreIdentical() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("areIdentical ?n ?m");

            assertThat(cond).isInstanceOf(IdenticalCondition.class);
            assertThat(cond.getName()).isEqualTo("areIdentical");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
        }

        @Test
        @DisplayName("should parse areNotIdentical condition")
        void shouldParseAreNotIdentical() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("areNotIdentical ?n ?m");

            assertThat(cond).isInstanceOf(NotIdenticalCondition.class);
            assertThat(cond.getName()).isEqualTo("areNotIdentical");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
        }

        @Test
        @DisplayName("should have areNotEqual registered in CLASSES_KEYWORDS")
        void shouldHaveAreNotEqualRegistered() {
            assertThat(AbstractCondition.CLASSES_KEYWORDS).containsKey("areNotEqual");
            assertThat(AbstractCondition.CLASSES_KEYWORDS.get("areNotEqual"))
                    .isEqualTo("NotEqualCondition");
        }

        @Test
        @DisplayName("areNotEqual condition class exists and can be instantiated")
        void areNotEqualConditionClassExists() {
            lotrec.dataStructure.expression.VariableExpression expr1 =
                    new lotrec.dataStructure.expression.VariableExpression("A");
            lotrec.dataStructure.expression.VariableExpression expr2 =
                    new lotrec.dataStructure.expression.VariableExpression("B");
            NotEqualCondition cond = new NotEqualCondition(expr1, expr2);

            assertThat(cond).isNotNull();
            assertThat(cond.createRestriction()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Condition Parsing - Contains and Set Operations")
    class ConditionParsingContainsAndSets {

        @Test
        @DisplayName("should parse contains condition")
        void shouldParseContains() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("contains ?n ?m");

            assertThat(cond).isInstanceOf(ContainsCondition.class);
            assertThat(cond.getName()).isEqualTo("contains");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.NODE);
        }

        @Test
        @DisplayName("should have haveSameFormulasSet registered in CLASSES_KEYWORDS")
        void shouldHaveHaveSameFormulasSetRegistered() {
            assertThat(AbstractCondition.CLASSES_KEYWORDS).containsKey("haveSameFormulasSet");
            assertThat(AbstractCondition.CLASSES_KEYWORDS.get("haveSameFormulasSet"))
                    .isEqualTo("HaveSameFormulasSetCondition");
        }

        @Test
        @DisplayName("haveSameFormulasSet condition class exists and can be instantiated")
        void haveSameFormulasSetConditionClassExists() {
            lotrec.dataStructure.expression.StringSchemeVariable node1 =
                    new lotrec.dataStructure.expression.StringSchemeVariable("n");
            lotrec.dataStructure.expression.StringSchemeVariable node2 =
                    new lotrec.dataStructure.expression.StringSchemeVariable("m");
            HaveSameFormulasSetCondition cond = new HaveSameFormulasSetCondition(node1, node2);

            assertThat(cond).isNotNull();
            assertThat(cond.createRestriction()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Condition Parsing - Node Creation")
    class ConditionParsingNodeCreation {

        @Test
        @DisplayName("should parse isNewNode condition")
        void shouldParseIsNewNode() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNewNode ?n");

            assertThat(cond).isInstanceOf(NodeCreatedCondition.class);
            assertThat(cond.getName()).isEqualTo("isNewNode");
            assertThat(cond.getParameters()).hasSize(1);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
        }
    }

    @Nested
    @DisplayName("Condition Parsing - Mark Conditions")
    class ConditionParsingMarkConditions {

        @Test
        @DisplayName("should parse isMarked condition")
        void shouldParseIsMarked() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isMarked ?n applied");

            assertThat(cond).isInstanceOf(MarkCondition.class);
            assertThat(cond.getName()).isEqualTo("isMarked");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should parse isNotMarked condition")
        void shouldParseIsNotMarked() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNotMarked ?n applied");

            assertThat(cond).isInstanceOf(NotMarkCondition.class);
            assertThat(cond.getName()).isEqualTo("isNotMarked");
            assertThat(cond.getParameters()).hasSize(2);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should parse isMarkedExpression condition")
        void shouldParseIsMarkedExpression() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isMarkedExpression ?n P applied");

            assertThat(cond).isInstanceOf(MarkExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("isMarkedExpression");
            assertThat(cond.getParameters()).hasSize(3);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
            assertThat(cond.getParameters().get(2).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should parse isNotMarkedExpression condition")
        void shouldParseIsNotMarkedExpression() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNotMarkedExpression ?n P applied");

            assertThat(cond).isInstanceOf(NotMarkExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("isNotMarkedExpression");
            assertThat(cond.getParameters()).hasSize(3);
            assertThat(cond.getParameters().get(0).getType()).isEqualTo(ParameterType.NODE);
            assertThat(cond.getParameters().get(1).getType()).isEqualTo(ParameterType.FORMULA);
            assertThat(cond.getParameters().get(2).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should have isMarkedExpressionInAllChildren registered")
        void shouldHaveIsMarkedExpressionInAllChildrenRegistered() {
            assertThat(AbstractCondition.CLASSES_KEYWORDS).containsKey("isMarkedExpressionInAllChildren");
            assertThat(AbstractCondition.CLASSES_KEYWORDS.get("isMarkedExpressionInAllChildren"))
                    .isEqualTo("MarkedExpressionInAllChildrenCondition");
        }

        @Test
        @DisplayName("isMarkedExpressionInAllChildren condition class exists and can be instantiated")
        void isMarkedExpressionInAllChildrenConditionClassExists() {
            lotrec.dataStructure.expression.StringSchemeVariable node =
                    new lotrec.dataStructure.expression.StringSchemeVariable("n");
            lotrec.dataStructure.expression.VariableExpression formula =
                    new lotrec.dataStructure.expression.VariableExpression("A");
            lotrec.dataStructure.expression.ConstantExpression relation =
                    new lotrec.dataStructure.expression.ConstantExpression("R");
            MarkedExpressionInAllChildrenCondition cond =
                    new MarkedExpressionInAllChildrenCondition(node, formula, relation, "mark");

            assertThat(cond).isNotNull();
            assertThat(cond.createActivator()).isNotNull();
            assertThat(cond.createRestriction()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("should handle invalid keyword by throwing ParseException")
        void shouldHandleInvalidKeyword() {
            assertThatThrownBy(() -> tokenizer.parseCondition("unknownCondition ?n"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("Unkown condition");
        }

        @Test
        @DisplayName("should handle empty condition code by throwing ParseException")
        void shouldHandleEmptyConditionCode() {
            assertThatThrownBy(() -> tokenizer.parseCondition(""))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should handle missing parameters by throwing ParseException")
        void shouldHandleMissingParameters() {
            assertThatThrownBy(() -> tokenizer.parseCondition("hasElement"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should handle whitespace-only input by throwing ParseException")
        void shouldHandleWhitespaceOnlyInput() {
            assertThatThrownBy(() -> tokenizer.parseCondition("   "))
                    .isInstanceOf(ParseException.class);
        }
    }

    @Nested
    @DisplayName("Condition Instantiation")
    class ConditionInstantiation {

        @Test
        @DisplayName("should instantiate condition by keyword for hasElement")
        void shouldInstantiateConditionByKeywordHasElement() throws Exception {
            String className = AbstractCondition.CLASSES_KEYWORDS.get("hasElement");
            String fullClassName = AbstractCondition.CONDITIONS_PACKAGE + className;

            Class<?> conditionClass = Class.forName(fullClassName);

            assertThat(conditionClass).isNotNull();
            assertThat(AbstractCondition.class).isAssignableFrom(conditionClass);
        }

        @Test
        @DisplayName("should instantiate condition by keyword for isLinked")
        void shouldInstantiateConditionByKeywordIsLinked() throws Exception {
            String className = AbstractCondition.CLASSES_KEYWORDS.get("isLinked");
            String fullClassName = AbstractCondition.CONDITIONS_PACKAGE + className;

            Class<?> conditionClass = Class.forName(fullClassName);

            assertThat(conditionClass).isNotNull();
            assertThat(AbstractCondition.class).isAssignableFrom(conditionClass);
        }

        @Test
        @DisplayName("should instantiate all registered condition classes")
        void shouldInstantiateAllRegisteredConditionClasses() {
            for (String keyword : AbstractCondition.CLASSES_KEYWORDS.keySet()) {
                String className = AbstractCondition.CLASSES_KEYWORDS.get(keyword);
                String fullClassName = AbstractCondition.CONDITIONS_PACKAGE + className;

                assertThatCode(() -> {
                    Class<?> conditionClass = Class.forName(fullClassName);
                    assertThat(AbstractCondition.class).isAssignableFrom(conditionClass);
                }).as("Should be able to load class for keyword: " + keyword)
                  .doesNotThrowAnyException();
            }
        }
    }

    @Nested
    @DisplayName("Parameter Type Verification")
    class ParameterTypeVerification {

        @Test
        @DisplayName("should verify condition with NODE parameter type")
        void shouldVerifyConditionWithNodeParameterType() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isNewNode ?n");

            Vector<Parameter> params = cond.getParameters();
            assertThat(params).hasSize(1);
            assertThat(params.get(0).getType()).isEqualTo(ParameterType.NODE);
        }

        @Test
        @DisplayName("should verify condition with FORMULA parameter type")
        void shouldVerifyConditionWithFormulaParameterType() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n P");

            Vector<Parameter> params = cond.getParameters();
            assertThat(params).hasSize(2);
            assertThat(params.get(1).getType()).isEqualTo(ParameterType.FORMULA);
        }

        @Test
        @DisplayName("should verify condition with RELATION parameter type")
        void shouldVerifyConditionWithRelationParameterType() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isLinked ?n ?m R");

            Vector<Parameter> params = cond.getParameters();
            assertThat(params).hasSize(3);
            assertThat(params.get(2).getType()).isEqualTo(ParameterType.RELATION);
        }

        @Test
        @DisplayName("should verify condition with MARK parameter type")
        void shouldVerifyConditionWithMarkParameterType() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isMarked ?n applied");

            Vector<Parameter> params = cond.getParameters();
            assertThat(params).hasSize(2);
            assertThat(params.get(1).getType()).isEqualTo(ParameterType.MARK);
        }

        @Test
        @DisplayName("should verify all four parameter types exist")
        void shouldVerifyAllFourParameterTypesExist() {
            assertThat(ParameterType.values()).containsExactlyInAnyOrder(
                    ParameterType.NODE,
                    ParameterType.FORMULA,
                    ParameterType.RELATION,
                    ParameterType.MARK
            );
        }
    }

    @Nested
    @DisplayName("Condition Code Generation")
    class ConditionCodeGeneration {

        @Test
        @DisplayName("should generate code for hasElement condition")
        void shouldGenerateCodeForHasElement() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n P");

            String code = cond.getCode();

            // Note: StringSchemeVariable stores just the name without "?" prefix
            assertThat(code).startsWith("hasElement");
            assertThat(code).contains("n"); // node variable stored without "?"
            assertThat(code).contains("P");
        }

        @Test
        @DisplayName("should generate code for isLinked condition")
        void shouldGenerateCodeForIsLinked() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isLinked ?n ?m R");

            String code = cond.getCode();

            // Note: StringSchemeVariable stores just the name without "?" prefix
            assertThat(code).startsWith("isLinked");
            assertThat(code).contains("n"); // node variable stored without "?"
            assertThat(code).contains("m"); // node variable stored without "?"
            assertThat(code).contains("R");
        }

        @Test
        @DisplayName("should generate code for isMarkedExpression condition")
        void shouldGenerateCodeForIsMarkedExpression() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isMarkedExpression ?n P applied");

            String code = cond.getCode();

            // Note: StringSchemeVariable stores just the name without "?" prefix
            assertThat(code).startsWith("isMarkedExpression");
            assertThat(code).contains("n"); // node variable stored without "?"
            assertThat(code).contains("P");
            assertThat(code).contains("applied");
        }
    }

    @Nested
    @DisplayName("Condition with Complex Formulas")
    class ConditionWithComplexFormulas {

        @Test
        @DisplayName("should parse condition with nested formula")
        void shouldParseConditionWithNestedFormula() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n imp P not Q");

            assertThat(cond).isInstanceOf(ExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasElement");
        }

        @Test
        @DisplayName("should parse condition with modal formula")
        void shouldParseConditionWithModalFormula() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n nec P");

            assertThat(cond).isInstanceOf(ExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasElement");
        }

        @Test
        @DisplayName("should parse condition with deeply nested formula")
        void shouldParseConditionWithDeeplyNestedFormula() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n and nec P pos Q");

            assertThat(cond).isInstanceOf(ExpressionCondition.class);
            assertThat(cond.getName()).isEqualTo("hasElement");
        }
    }

    @Nested
    @DisplayName("Condition Restriction and Activator")
    class ConditionRestrictionAndActivator {

        @Test
        @DisplayName("ExpressionCondition should create activator")
        void expressionConditionShouldCreateActivator() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n P");

            assertThat(cond.createActivator()).isNotNull();
        }

        @Test
        @DisplayName("ExpressionCondition should create restriction")
        void expressionConditionShouldCreateRestriction() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("hasElement ?n P");

            assertThat(cond.createRestriction()).isNotNull();
        }

        @Test
        @DisplayName("LinkCondition should create activator")
        void linkConditionShouldCreateActivator() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isLinked ?n ?m R");

            assertThat(cond.createActivator()).isNotNull();
        }

        @Test
        @DisplayName("MarkCondition should create activator")
        void markConditionShouldCreateActivator() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("isMarked ?n applied");

            assertThat(cond.createActivator()).isNotNull();
        }

        @Test
        @DisplayName("IdenticalCondition should not create activator")
        void identicalConditionShouldNotCreateActivator() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("areIdentical ?n ?m");

            assertThat(cond.createActivator()).isNull();
        }

        @Test
        @DisplayName("ContainsCondition should not create activator")
        void containsConditionShouldNotCreateActivator() throws ParseException {
            AbstractCondition cond = tokenizer.parseCondition("contains ?n ?m");

            assertThat(cond.createActivator()).isNull();
        }
    }
}
