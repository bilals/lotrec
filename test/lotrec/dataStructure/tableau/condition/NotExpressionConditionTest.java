package lotrec.dataStructure.tableau.condition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.*;

/**
 * Tests for NotExpressionCondition and NotExpressionConstraint restriction.
 * Verifies that the condition correctly checks for ABSENCE of expressions in nodes.
 */
@DisplayName("NotExpressionCondition")
class NotExpressionConditionTest {

    private Tableau tableau;
    private EventMachine em;
    private ActionStocking stocking;
    private ActionContainer actionContainer;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
        stocking = TestableEventMachine.createActionStocking(em, tableau);
        actionContainer = TestableEventMachine.createActionContainer();
    }

    @Test
    @DisplayName("should match when node does NOT contain the expression")
    void shouldMatchWhenNodeDoesNotContainExpression() {
        // Arrange
        ConstantExpression existingExpr = TableauTestFixtures.constant("P");
        ConstantExpression absentExpr = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, existingExpr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, absentExpr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should match because Q is absent
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match when node contains the expression")
    void shouldNotMatchWhenNodeContainsExpression() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, expr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should NOT match because P is present
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should match when node is empty")
    void shouldMatchWhenNodeIsEmpty() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression expr = TableauTestFixtures.constant("P");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, expr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should match because empty node doesn't contain P
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match when variable pattern matches any expression")
    void shouldNotMatchWhenVariablePatternMatchesAnyExpression() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        // Variable expression matches any expression
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should NOT match because variable A matches P
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should match with variable when node is empty")
    void shouldMatchWithVariableWhenNodeIsEmpty() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should match because empty node has no matching expressions
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should match with bound variable when value not in node")
    void shouldMatchWithBoundVariableWhenValueNotInNode() {
        // Arrange
        ConstantExpression existingExpr = TableauTestFixtures.constant("P");
        ConstantExpression absentExpr = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, existingExpr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        // Pre-bind A to Q (which is not in node)
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("A", absentExpr));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should match because Q (bound to A) is not in node
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match with bound variable when value is in node")
    void shouldNotMatchWithBoundVariableWhenValueIsInNode() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        // Pre-bind A to P (which IS in node)
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("A", expr));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should NOT match because P (bound to A) IS in node
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should throw when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, expr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> restriction.attemptToApply(actionContainer, instanceSet, stocking, em))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for node");
    }

    @Test
    @DisplayName("should not modify instance set (acts as constraint)")
    void shouldNotModifyInstanceSet() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression expr = TableauTestFixtures.constant("P");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotExpressionCondition condition = new NotExpressionCondition(nodeScheme, expr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - the modifier in the pack should be the same instance set
        assertThat(stocking.isEmpty()).isFalse();
        ActionPack pack = stocking.getPacks().getFirst();
        assertThat(pack.getModifier()).isSameAs(instanceSet);
    }
}
