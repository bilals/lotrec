package lotrec.dataStructure.tableau.condition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.*;

/**
 * Tests for ExpressionCondition and ExpressionMatch restriction.
 * Verifies that the condition correctly matches expressions in nodes
 * and enriches the instance set.
 */
@DisplayName("ExpressionCondition")
class ExpressionConditionTest {

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
    @DisplayName("should match when node contains exact constant expression")
    void shouldMatchWhenNodeContainsExactConstantExpression() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should have produced an action pack
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match when node does not contain expression")
    void shouldNotMatchWhenNodeDoesNotContainExpression() {
        // Arrange
        ConstantExpression existingExpr = TableauTestFixtures.constant("P");
        ConstantExpression searchExpr = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, existingExpr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, searchExpr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should NOT have produced an action pack
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should bind variable expression to concrete value")
    void shouldBindVariableExpressionToConcreteValue() {
        // Arrange
        ConstantExpression concreteExpr = TableauTestFixtures.constant("ConcreteP");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, concreteExpr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();

        // Get the resulting instance set from the action pack
        ActionPack pack = stocking.getPacks().getFirst();
        InstanceSet result = (InstanceSet) pack.getModifier();

        // The variable A should be bound to the concrete expression
        assertThat(result.get(exprScheme)).isEqualTo(concreteExpr);
    }

    @Test
    @DisplayName("should match multiple expressions with variable pattern")
    void shouldMatchMultipleExpressionsWithVariablePattern() {
        // Arrange
        ConstantExpression expr1 = TableauTestFixtures.constant("P");
        ConstantExpression expr2 = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, expr1, expr2);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should have two action packs (one for each matching expression)
        assertThat(stocking.getPacks().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("should stop after first match when applyOnOneOccurrence is true")
    void shouldStopAfterFirstMatchWhenApplyOnOneOccurrence() {
        // Arrange
        ConstantExpression expr1 = TableauTestFixtures.constant("P");
        ConstantExpression expr2 = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, expr1, expr2);

        em.setApplyOnOneOccurence(true);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should have only one action pack due to applyOnOneOccurrence
        assertThat(stocking.getPacks().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("should throw when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> restriction.attemptToApply(actionContainer, instanceSet, stocking, em))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for node");
    }

    @Test
    @DisplayName("should not match on empty node")
    void shouldNotMatchOnEmptyNode() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression expr = TableauTestFixtures.constant("P");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should match with already bound variable if values are equal")
    void shouldMatchWithAlreadyBoundVariableIfValuesEqual() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        // Pre-bind A to the same expression
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("A", expr));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should match because bound value equals actual value
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match with already bound variable if values differ")
    void shouldNotMatchWithAlreadyBoundVariableIfValuesDiffer() {
        // Arrange
        ConstantExpression actualExpr = TableauTestFixtures.constant("P");
        ConstantExpression boundExpr = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, actualExpr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        ExpressionCondition condition = new ExpressionCondition(nodeScheme, exprScheme);
        Restriction restriction = condition.createRestriction();

        // Pre-bind A to a different expression
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("A", boundExpr));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should NOT match because bound value differs
        assertThat(stocking.isEmpty()).isTrue();
    }
}
