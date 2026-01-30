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

import java.util.List;

/**
 * Integration tests for condition chain matching.
 * Tests how multiple conditions work together in a rule.
 */
@DisplayName("Condition Chain Integration")
class ConditionChainIntegrationTest {

    private Tableau tableau;
    private TestableEngine engine;

    // Standard scheme variables
    private StringSchemeVariable N0;
    private StringSchemeVariable N1;
    private VariableExpression varA;

    // Standard connectors
    private Connector notConnector;
    private Connector necConnector;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        engine = TestableEngine.create();

        N0 = nodeScheme("N0");
        N1 = nodeScheme("N1");
        varA = exprVar("A");

        // Connector(name, arity, outputFormat)
        notConnector = new Connector("not", 1, "~_");
        necConnector = new Connector("nec", 1, "[]_");
    }

    @Nested
    @DisplayName("Same Node Conditions")
    class SameNodeConditions {

        @Test
        @DisplayName("should match when both positive conditions hold")
        void shouldMatchWhenBothPositiveConditionsHold() {
            // Arrange: Node with P and Q
            ConstantExpression P = constant("P");
            ConstantExpression Q = constant("Q");
            TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, P, Q);

            // Rule: hasElement N0 P AND hasElement N0 Q -> add N0 R
            ConstantExpression R = constant("R");
            Rule rule = createRule("BothPQ",
                    List.of(hasElement(N0, P), hasElement(N0, Q)),
                    List.of(addExpression(N0, R)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, R)).isTrue();
        }

        @Test
        @DisplayName("should not match when first condition fails")
        void shouldNotMatchWhenFirstConditionFails() {
            // Arrange: Node with Q only (no P)
            ConstantExpression Q = constant("Q");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, Q);

            // Rule: hasElement N0 P AND hasElement N0 Q -> add N0 R
            ConstantExpression P = constant("P");
            ConstantExpression R = constant("R");
            Rule rule = createRule("BothPQ",
                    List.of(hasElement(N0, P), hasElement(N0, Q)),
                    List.of(addExpression(N0, R)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(nodeContains(node, R)).isFalse();
        }

        @Test
        @DisplayName("should not match when second condition fails")
        void shouldNotMatchWhenSecondConditionFails() {
            // Arrange: Node with P only (no Q)
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 P AND hasElement N0 Q -> add N0 R
            ConstantExpression Q = constant("Q");
            ConstantExpression R = constant("R");
            Rule rule = createRule("BothPQ",
                    List.of(hasElement(N0, P), hasElement(N0, Q)),
                    List.of(addExpression(N0, R)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(nodeContains(node, R)).isFalse();
        }
    }

    @Nested
    @DisplayName("Negative Conditions")
    class NegativeConditions {

        @Test
        @DisplayName("should match positive with negative condition")
        void shouldMatchPositiveWithNegative() {
            // Arrange: Node with P but without (not P)
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 ?A AND hasNotElement N0 (not ?A) -> add N0 Q
            Expression notVar = compound(notConnector, varA);
            ConstantExpression Q = constant("Q");
            Rule rule = createRule("AddIfNoNegation",
                    List.of(hasElement(N0, varA), hasNotElement(N0, notVar)),
                    List.of(addExpression(N0, Q)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
        }

        @Test
        @DisplayName("should still match with other bindings when one binding fails")
        void shouldStillMatchWithOtherBindingsWhenOneBindingFails() {
            // Arrange: Node with P AND (not P)
            ConstantExpression P = constant("P");
            Expression notP = compound(notConnector, P);
            TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, P, notP);

            // Rule: hasElement N0 ?A AND hasNotElement N0 (not ?A) -> add N0 Q
            // This tests variable binding behavior:
            // - hasElement can bind ?A to P or to (not P)
            // - If ?A = P, then hasNotElement looks for (not P) which exists -> fail
            // - If ?A = (not P), then hasNotElement looks for (not (not P)) which doesn't exist -> success
            // So the rule still fires with ?A = (not P)
            Expression notVar = compound(notConnector, varA);
            ConstantExpression Q = constant("Q");
            Rule rule = createRule("AddIfNoNegation",
                    List.of(hasElement(N0, varA), hasNotElement(N0, notVar)),
                    List.of(addExpression(N0, Q)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert - Rule fires with ?A bound to (not P) since (not (not P)) doesn't exist
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
        }

        @Test
        @DisplayName("should handle hasNotElement with constant")
        void shouldHandleHasNotElementWithConstant() {
            // Arrange: Node with P but without Q
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule: hasElement N0 P AND hasNotElement N0 Q -> add N0 R
            ConstantExpression Q = constant("Q");
            ConstantExpression R = constant("R");
            Rule rule = createRule("AddIfNoQ",
                    List.of(hasElement(N0, P), hasNotElement(N0, Q)),
                    List.of(addExpression(N0, R)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, R)).isTrue();
        }

        @Test
        @DisplayName("should not apply when hasNotElement fails due to presence")
        void shouldNotApplyWhenHasNotElementFailsDueToPresence() {
            // Arrange: Node with P AND Q
            ConstantExpression P = constant("P");
            ConstantExpression Q = constant("Q");
            TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, P, Q);

            // Rule: hasElement N0 P AND hasNotElement N0 Q -> add N0 R
            ConstantExpression R = constant("R");
            Rule rule = createRule("AddIfNoQ",
                    List.of(hasElement(N0, P), hasNotElement(N0, Q)),
                    List.of(addExpression(N0, R)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(nodeContains(node, R)).isFalse();
        }
    }

    @Nested
    @DisplayName("Mark Conditions")
    class MarkConditions {

        @Test
        @DisplayName("should match isNotMarked condition")
        void shouldMatchIsNotMarkedCondition() {
            // Arrange: Node with P, not marked
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);
            String markName = "PROCESSED";

            // Rule: hasElement N0 P AND isNotMarked N0 PROCESSED -> add N0 Q, mark N0 P PROCESSED
            ConstantExpression Q = constant("Q");
            Rule rule = createRule("ProcessOnce",
                    List.of(hasElement(N0, P), isNotMarked(N0, markName)),
                    List.of(addExpression(N0, Q), mark(N0, P, markName)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
        }

        @Test
        @DisplayName("should not match when node isMarked")
        void shouldNotMatchWhenNodeIsMarked() {
            // Arrange: Node with P, node itself is marked
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);
            String markName = "PROCESSED";
            // Mark the node, not the expression (isNotMarked checks node marks)
            TableauTestFixtures.markNode(node, markName);

            // Rule: hasElement N0 P AND isNotMarked N0 PROCESSED -> add N0 Q
            ConstantExpression Q = constant("Q");
            Rule rule = createRule("ProcessOnce",
                    List.of(hasElement(N0, P), isNotMarked(N0, markName)),
                    List.of(addExpression(N0, Q)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(nodeContains(node, Q)).isFalse();
        }
    }

    @Nested
    @DisplayName("Linked Node Conditions")
    class LinkedNodeConditions {

        @Test
        @DisplayName("should match conditions across linked nodes")
        void shouldMatchConditionsAcrossLinkedNodes() {
            // Arrange: Two linked nodes, N0 -> N1, with "nec P" in N0
            ConstantExpression P = constant("P");
            Expression necP = compound(necConnector, P);
            ConstantExpression R = constant("R");

            TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, R);
            TableauNode fromNode = nodes[0];
            TableauNode toNode = nodes[1];
            fromNode.add(new MarkedExpression(necP));

            // Rule: hasElement N0 (nec ?A) AND isLinked N0 N1 R -> add N1 ?A
            Expression necVar = compound(necConnector, varA);
            Rule rule = createRule("NEC",
                    List.of(hasElement(N0, necVar), isLinked(N0, N1, R)),
                    List.of(addExpression(N1, varA)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isTrue();
            assertThat(nodeContains(toNode, P)).isTrue();
        }

        @Test
        @DisplayName("should not match when link is missing")
        void shouldNotMatchWhenLinkIsMissing() {
            // Arrange: Two unlinked nodes, N0 has "nec P"
            ConstantExpression P = constant("P");
            Expression necP = compound(necConnector, P);
            ConstantExpression R = constant("R");

            TableauNode node0 = TableauTestFixtures.createNodeWithExpression(tableau, necP);
            TableauNode node1 = TableauTestFixtures.createNode(tableau);
            // No link between nodes

            // Rule: hasElement N0 (nec ?A) AND isLinked N0 N1 R -> add N1 ?A
            Expression necVar = compound(necConnector, varA);
            Rule rule = createRule("NEC",
                    List.of(hasElement(N0, necVar), isLinked(N0, N1, R)),
                    List.of(addExpression(N1, varA)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(nodeContains(node1, P)).isFalse();
        }

        @Test
        @DisplayName("should not match when link has wrong relation")
        void shouldNotMatchWhenLinkHasWrongRelation() {
            // Arrange: Two nodes linked with S, but rule expects R
            ConstantExpression P = constant("P");
            Expression necP = compound(necConnector, P);
            ConstantExpression R = constant("R");
            ConstantExpression S = constant("S");

            TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, S); // Linked with S
            TableauNode fromNode = nodes[0];
            TableauNode toNode = nodes[1];
            fromNode.add(new MarkedExpression(necP));

            // Rule expects link with R, but link is with S
            Expression necVar = compound(necConnector, varA);
            Rule rule = createRule("NEC",
                    List.of(hasElement(N0, necVar), isLinked(N0, N1, R)),
                    List.of(addExpression(N1, varA)));

            // Act
            boolean applied = applyRule(rule, tableau, engine);

            // Assert
            assertThat(applied).isFalse();
            assertThat(nodeContains(toNode, P)).isFalse();
        }
    }
}
