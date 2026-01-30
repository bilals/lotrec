package lotrec.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;
import static lotrec.integration.RuleTestHelper.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.dataStructure.tableau.action.*;
import lotrec.dataStructure.tableau.condition.*;
import lotrec.engine.TestableEngine;
import lotrec.process.*;

import java.util.List;

/**
 * Integration tests for complete rule application.
 * Tests the full workflow: conditions match -> actions execute.
 */
@DisplayName("Rule Application Integration")
class RuleApplicationIntegrationTest {

    private Tableau tableau;
    private TestableEngine engine;

    // Standard scheme variables
    private StringSchemeVariable N0;
    private VariableExpression varA;
    private VariableExpression varB;

    // Standard connectors
    private Connector andConnector;
    private Connector notConnector;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        engine = TestableEngine.create();

        N0 = nodeScheme("N0");
        varA = exprVar("A");
        varB = exprVar("B");

        // Connector(name, arity, outputFormat)
        andConnector = new Connector("and", 2, "(_ & _)");
        notConnector = new Connector("not", 1, "~_");
    }

    @Nested
    @DisplayName("Simple Rule Application")
    class SimpleRuleApplication {

        @Test
        @DisplayName("should apply rule when condition matches")
        void shouldApplyRuleWhenConditionMatches() {
            // Arrange: Node with expression P
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 P -> add N0 Q
            ConstantExpression Q = constant("Q");
            Rule rule = createSimpleRule("AddQ",
                    hasElement(N0, P),
                    addExpression(N0, Q));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
            assertThat(expressionCount(node)).isEqualTo(2);
        }

        @Test
        @DisplayName("should not apply rule when condition does not match")
        void shouldNotApplyRuleWhenConditionDoesNotMatch() {
            // Arrange: Node with expression P
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 R -> add N0 Q (R not present)
            ConstantExpression R = constant("R");
            ConstantExpression Q = constant("Q");
            Rule rule = createSimpleRule("AddQ",
                    hasElement(N0, R),
                    addExpression(N0, Q));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(nodeContains(node, Q)).isFalse();
            assertThat(expressionCount(node)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("AND Rule Decomposition")
    class AndRuleDecomposition {

        @Test
        @DisplayName("should decompose AND expression into components")
        void shouldDecomposeAndExpression() {
            // Arrange: Node with "and P Q"
            ConstantExpression P = constant("P");
            ConstantExpression Q = constant("Q");
            Expression andPQ = compound(andConnector, P, Q);
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, andPQ);

            // Rule: hasElement N0 (and ?A ?B) -> add N0 ?A, add N0 ?B
            Expression andPattern = compound(andConnector, varA, varB);
            Rule rule = createRule("R-AND",
                    List.of(hasElement(N0, andPattern)),
                    List.of(addExpression(N0, varA), addExpression(N0, varB)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, P)).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
            assertThat(expressionCount(node)).isEqualTo(3); // andPQ, P, Q
        }

        @Test
        @DisplayName("should not apply AND rule when no AND expression present")
        void shouldNotApplyAndRuleWhenNoAndExpression() {
            // Arrange: Node with just P (not an AND expression)
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 (and ?A ?B) -> add N0 ?A, add N0 ?B
            Expression andPattern = compound(andConnector, varA, varB);
            Rule rule = createRule("R-AND",
                    List.of(hasElement(N0, andPattern)),
                    List.of(addExpression(N0, varA), addExpression(N0, varB)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(expressionCount(node)).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Variable Binding")
    class VariableBinding {

        @Test
        @DisplayName("should bind variable in condition and use in action")
        void shouldBindVariableInConditionAndUseInAction() {
            // Arrange: Node with P
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 ?A -> add N0 (not ?A)
            Expression notVar = compound(notConnector, varA);
            Rule rule = createSimpleRule("AddNot",
                    hasElement(N0, varA),
                    addExpression(N0, notVar));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            Expression notP = compound(notConnector, P);
            assertThat(nodeContains(node, notP)).isTrue();
        }

        @Test
        @DisplayName("should bind same variable across multiple conditions")
        void shouldBindSameVariableAcrossConditions() {
            // Arrange: Node with P but no (not P)
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 ?A AND hasNotElement N0 (not ?A) -> add N0 Q
            Expression notVar = compound(notConnector, varA);
            ConstantExpression Q = constant("Q");
            Rule rule = createRule("AddQIfNoNegation",
                    List.of(hasElement(N0, varA), hasNotElement(N0, notVar)),
                    List.of(addExpression(N0, Q)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
        }
    }

    @Nested
    @DisplayName("Multiple Actions")
    class MultipleActions {

        @Test
        @DisplayName("should execute all actions from a rule")
        void shouldExecuteAllActions() {
            // Arrange
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 P -> add N0 Q, add N0 R, add N0 S
            ConstantExpression Q = constant("Q");
            ConstantExpression R = constant("R");
            ConstantExpression S = constant("S");
            Rule rule = createRule("AddMultiple",
                    List.of(hasElement(N0, P)),
                    List.of(
                            addExpression(N0, Q),
                            addExpression(N0, R),
                            addExpression(N0, S)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
            assertThat(nodeContains(node, R)).isTrue();
            assertThat(nodeContains(node, S)).isTrue();
            assertThat(expressionCount(node)).isEqualTo(4);
        }

        @Test
        @DisplayName("should add expression and mark source")
        void shouldAddExpressionAndMarkSource() {
            // Arrange
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);
            String markName = "PROCESSED";

            // Rule: hasElement N0 P -> add N0 Q, mark N0 P PROCESSED
            ConstantExpression Q = constant("Q");
            Rule rule = createRule("AddAndMark",
                    List.of(hasElement(N0, P)),
                    List.of(
                            addExpression(N0, Q),
                            mark(N0, P, markName)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
            // Verify the expression is marked
            MarkedExpression markedP = findMarkedExpression(node, P);
            assertThat(markedP).isNotNull();
            assertThat(markedP.isMarked(markName)).isTrue();
        }
    }

    @Nested
    @DisplayName("Duplicate Handling")
    class DuplicateHandling {

        @Test
        @DisplayName("should not add duplicate expression")
        void shouldNotAddDuplicateExpression() {
            // Arrange: Node already has both P and Q
            ConstantExpression P = constant("P");
            ConstantExpression Q = constant("Q");
            TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, P, Q);

            // Rule: hasElement N0 P -> add N0 Q (Q already exists)
            Rule rule = createSimpleRule("AddQ",
                    hasElement(N0, P),
                    addExpression(N0, Q));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert - rule should fire but Q should not be duplicated
            assertThat(applied).isTrue();
            assertThat(expressionCount(node)).isEqualTo(2); // Still 2, not 3
        }
    }

    // ========== Helper Methods ==========

    private MarkedExpression findMarkedExpression(TableauNode node, Expression expression) {
        for (java.util.Enumeration<?> e = node.getMarkedExpressionsEnum(); e.hasMoreElements();) {
            MarkedExpression me = (MarkedExpression) e.nextElement();
            if (me.expression.equals(expression)) {
                return me;
            }
        }
        return null;
    }
}
