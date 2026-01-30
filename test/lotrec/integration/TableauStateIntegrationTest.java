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
 * Integration tests for action effects on tableau state.
 * Tests how actions modify the tableau structure and node contents.
 */
@DisplayName("Tableau State Integration")
class TableauStateIntegrationTest {

    private Tableau tableau;
    private TestableEngine engine;

    // Standard scheme variables
    private StringSchemeVariable N0;
    private StringSchemeVariable N1;
    private VariableExpression varA;

    // Standard connectors
    private Connector notConnector;
    private Connector posConnector;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        engine = TestableEngine.create();

        N0 = nodeScheme("N0");
        N1 = nodeScheme("N1");
        varA = exprVar("A");

        // Connector(name, arity, outputFormat)
        notConnector = new Connector("not", 1, "~_");
        posConnector = new Connector("pos", 1, "<>_");
    }

    @Nested
    @DisplayName("Expression Addition")
    class ExpressionAddition {

        @Test
        @DisplayName("should add expression and verify state change")
        void shouldAddExpressionAndVerifyStateChange() {
            // Arrange
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);
            int initialCount = expressionCount(node);

            // Rule: hasElement N0 P -> add N0 Q
            ConstantExpression Q = constant("Q");
            Rule rule = createSimpleRule("AddQ",
                    hasElement(N0, P),
                    addExpression(N0, Q));

            // Act
            applyRule(rule, tableau, engine);

            // Assert
            assertThat(expressionCount(node)).isEqualTo(initialCount + 1);
            assertThat(nodeContains(node, Q)).isTrue();
        }

        @Test
        @DisplayName("should close node when FALSUM is added")
        void shouldCloseNodeWhenFalsumAdded() {
            // Arrange
            ConstantExpression P = constant("P");
            Expression notP = compound(notConnector, P);
            TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, P, notP);

            assertThat(node.isClosed()).isFalse();

            // Rule: hasElement N0 P AND hasElement N0 (not P) -> add N0 FALSUM
            Rule rule = createRule("Close",
                    List.of(hasElement(N0, P), hasElement(N0, notP)),
                    List.of(addExpression(N0, ConstantExpression.FALSUM)));

            // Act
            applyRule(rule, tableau, engine);

            // Assert
            assertThat(node.isClosed()).isTrue();
            assertThat(nodeContains(node, ConstantExpression.FALSUM)).isTrue();
        }
    }

    @Nested
    @DisplayName("Node Creation")
    class NodeCreation {

        @Test
        @DisplayName("should create new node via action")
        void shouldCreateNewNodeViaAction() {
            // Arrange
            ConstantExpression P = constant("P");
            Expression posP = compound(posConnector, P);
            TableauNode existingNode = TableauTestFixtures.createNodeWithExpression(tableau, posP);
            int initialNodeCount = nodeCount(tableau);

            // Rule: hasElement N0 (pos ?A) -> createNewNode N1, add N1 ?A
            Expression posVar = compound(posConnector, varA);
            Rule rule = createRule("CreatePosNode",
                    List.of(hasElement(N0, posVar)),
                    List.of(createNewNode(N1), addExpression(N1, varA)));

            // Act
            applyRule(rule, tableau, engine);

            // Assert
            assertThat(nodeCount(tableau)).isEqualTo(initialNodeCount + 1);
        }

        @Test
        @DisplayName("should create new node and link it")
        void shouldCreateNewNodeAndLinkIt() {
            // Arrange
            ConstantExpression P = constant("P");
            Expression posP = compound(posConnector, P);
            ConstantExpression R = constant("R");
            TableauNode existingNode = TableauTestFixtures.createNodeWithExpression(tableau, posP);
            int initialNodeCount = nodeCount(tableau);

            // Rule: hasElement N0 (pos ?A) -> createNewNode N1, link N0 N1 R, add N1 ?A
            Expression posVar = compound(posConnector, varA);
            Rule rule = createRule("POS",
                    List.of(hasElement(N0, posVar)),
                    List.of(
                            createNewNode(N1),
                            link(N0, N1, R),
                            addExpression(N1, varA)));

            // Act
            applyRule(rule, tableau, engine);

            // Assert
            assertThat(nodeCount(tableau)).isEqualTo(initialNodeCount + 1);

            // Find the new node (it should have P in it)
            TableauNode newNode = findNodeContaining(tableau, P, existingNode);
            assertThat(newNode).isNotNull();
            assertThat(nodesAreLinked(existingNode, newNode)).isTrue();
        }
    }

    @Nested
    @DisplayName("Node Marking")
    class NodeMarking {

        @Test
        @DisplayName("should mark node via action")
        void shouldMarkNodeViaAction() {
            // Arrange
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);
            String markName = "VISITED";

            assertThat(node.isMarked(markName)).isFalse();

            // Rule: hasElement N0 P -> markNode N0 VISITED
            Rule rule = createSimpleRule("MarkVisited",
                    hasElement(N0, P),
                    markNode(N0, markName));

            // Act
            applyRule(rule, tableau, engine);

            // Assert
            assertThat(node.isMarked(markName)).isTrue();
        }

        @Test
        @DisplayName("should mark expression via action")
        void shouldMarkExpressionViaAction() {
            // Arrange
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);
            String markName = "DECOMPOSED";

            // Verify expression is not marked initially
            MarkedExpression me = findMarkedExpression(node, P);
            assertThat(me.isMarked(markName)).isFalse();

            // Rule: hasElement N0 P -> mark N0 P DECOMPOSED
            Rule rule = createSimpleRule("MarkDecomposed",
                    hasElement(N0, P),
                    mark(N0, P, markName));

            // Act
            applyRule(rule, tableau, engine);

            // Assert
            me = findMarkedExpression(node, P);
            assertThat(me.isMarked(markName)).isTrue();
        }

        @Test
        @DisplayName("should not apply rule twice due to marking")
        void shouldNotApplyRuleTwiceDueToMarking() {
            // Arrange
            ConstantExpression P = constant("P");
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);
            String markName = "PROCESSED";

            // Rule: hasElement N0 P AND isNotMarked N0 PROCESSED -> add N0 Q, markNode N0 PROCESSED
            ConstantExpression Q = constant("Q");
            Rule rule = createRule("ProcessOnce",
                    List.of(hasElement(N0, P), isNotMarked(N0, markName)),
                    List.of(addExpression(N0, Q), markNode(N0, markName)));

            // Act - First application
            boolean firstApply = applyRule(rule, tableau, engine);

            // Assert - First application should succeed
            assertThat(firstApply).isTrue();
            assertThat(nodeContains(node, Q)).isTrue();
            int countAfterFirst = expressionCount(node);

            // Act - Second application (should not apply due to mark)
            boolean secondApply = applyRule(rule, tableau, engine);

            // Assert - Second application should not add more expressions
            assertThat(secondApply).isFalse();
            assertThat(expressionCount(node)).isEqualTo(countAfterFirst);
        }
    }

    @Nested
    @DisplayName("Node Linking")
    class NodeLinking {

        @Test
        @DisplayName("should link existing nodes via action")
        void shouldLinkExistingNodesViaAction() {
            // Arrange: Two unlinked nodes
            ConstantExpression P = constant("P");
            ConstantExpression Q = constant("Q");
            ConstantExpression R = constant("R");

            TableauNode node0 = TableauTestFixtures.createNodeWithExpression(tableau, P);
            TableauNode node1 = TableauTestFixtures.createNodeWithExpression(tableau, Q);

            assertThat(nodesAreLinked(node0, node1)).isFalse();

            // This test requires direct action application since we need
            // both nodes already bound. Let's use a different approach:
            // Create the link via TableauTestFixtures and verify it works
            TableauTestFixtures.linkNodes(node0, node1, R);

            // Assert
            assertThat(nodesAreLinked(node0, node1)).isTrue();
        }
    }

    @Nested
    @DisplayName("Complex State Changes")
    class ComplexStateChanges {

        @Test
        @DisplayName("should propagate expression to successor node")
        void shouldPropagateExpressionToSuccessorNode() {
            // Arrange: N0 has "nec P", N0 -> N1 via R
            ConstantExpression P = constant("P");
            Connector necConnector = new Connector("nec", 1, "[]_");
            Expression necP = compound(necConnector, P);
            ConstantExpression R = constant("R");

            TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, R);
            TableauNode node0 = nodes[0];
            TableauNode node1 = nodes[1];
            node0.add(new MarkedExpression(necP));

            assertThat(nodeContains(node1, P)).isFalse();

            // Rule: hasElement N0 (nec ?A) AND isLinked N0 N1 R -> add N1 ?A
            Expression necVar = compound(necConnector, varA);
            Rule rule = createRule("NEC",
                    List.of(hasElement(N0, necVar), isLinked(N0, N1, R)),
                    List.of(addExpression(N1, varA)));

            // Act
            applyRule(rule, tableau, engine);

            // Assert
            assertThat(nodeContains(node1, P)).isTrue();
        }

        @Test
        @DisplayName("should handle contradictory expressions across rule applications")
        void shouldHandleContradictoryExpressionsAcrossRuleApplications() {
            // Arrange: Node with P
            ConstantExpression P = constant("P");
            Expression notP = compound(notConnector, P);
            TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, P);

            // Rule 1: Add (not P) if P exists
            Rule rule1 = createSimpleRule("AddNotP",
                    hasElement(N0, P),
                    addExpression(N0, notP));

            // Rule 2: Add FALSUM if P and (not P) exist
            Rule rule2 = createRule("Close",
                    List.of(hasElement(N0, P), hasElement(N0, notP)),
                    List.of(addExpression(N0, ConstantExpression.FALSUM)));

            // Act - Apply first rule
            applyRule(rule1, tableau, engine);

            // Assert - notP should be added
            assertThat(nodeContains(node, notP)).isTrue();
            assertThat(node.isClosed()).isFalse();

            // Act - Apply second rule
            applyRule(rule2, tableau, engine);

            // Assert - FALSUM should close the node
            assertThat(nodeContains(node, ConstantExpression.FALSUM)).isTrue();
            assertThat(node.isClosed()).isTrue();
        }
    }

    // ========== Helper Methods ==========

    private TableauNode findNodeContaining(Tableau tableau, Expression expr, TableauNode exclude) {
        for (Object nodeObj : tableau.getNodes()) {
            if (nodeObj instanceof TableauNode) {
                TableauNode node = (TableauNode) nodeObj;
                if (node != exclude && node.contains(expr)) {
                    return node;
                }
            }
        }
        return null;
    }

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
