package lotrec.dataStructure.tableau.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import java.util.Enumeration;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.EventMachine;
import lotrec.process.TestableEventMachine;
import lotrec.process.ProcessException;

/**
 * Tests for HideAction execution.
 * Verifies that expressions are correctly marked as hidden.
 */
@DisplayName("HideAction")
class HideActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should mark expression as hidden")
    void shouldMarkExpressionAsHidden() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        HideAction action = new HideAction(nodeScheme, expr);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        MarkedExpression me = findMarkedExpression(node, expr);
        assertThat(me).isNotNull();
        assertThat(me.isMarked("hide")).isTrue();
    }

    @Test
    @DisplayName("should hide matching expression from multiple expressions")
    void shouldHideMatchingExpressionFromMultiple() {
        // Arrange
        ConstantExpression expr1 = TableauTestFixtures.constant("P");
        ConstantExpression expr2 = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, expr1, expr2);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        HideAction action = new HideAction(nodeScheme, expr1);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        MarkedExpression me1 = findMarkedExpression(node, expr1);
        MarkedExpression me2 = findMarkedExpression(node, expr2);

        assertThat(me1.isMarked("hide")).isTrue();
        assertThat(me2.isMarked("hide")).isFalse();
    }

    @Test
    @DisplayName("should instantiate variable expression from instance set")
    void shouldInstantiateVariableExpression() {
        // Arrange
        ConstantExpression concreteExpr = TableauTestFixtures.constant("Concrete");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, concreteExpr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        HideAction action = new HideAction(nodeScheme, exprScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("A", concreteExpr));

        // Act
        action.apply(em, instanceSet);

        // Assert
        MarkedExpression me = findMarkedExpression(node, concreteExpr);
        assertThat(me).isNotNull();
        assertThat(me.isMarked("hide")).isTrue();
    }

    @Test
    @DisplayName("should handle expression not found in node")
    void shouldHandleExpressionNotFoundInNode() {
        // Arrange
        ConstantExpression existingExpr = TableauTestFixtures.constant("P");
        ConstantExpression nonExistingExpr = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, existingExpr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        HideAction action = new HideAction(nodeScheme, nonExistingExpr);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act & Assert - should not throw, just do nothing
        assertThatCode(() -> action.apply(em, instanceSet)).doesNotThrowAnyException();

        // Existing expression should not be hidden
        MarkedExpression me = findMarkedExpression(node, existingExpr);
        assertThat(me.isMarked("hide")).isFalse();
    }

    @Test
    @DisplayName("should throw ProcessException when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        HideAction action = new HideAction(nodeScheme, expr);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for node");
    }

    @Test
    @DisplayName("should throw ProcessException when expression cannot be instantiated")
    void shouldThrowWhenExpressionCannotBeInstantiated() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression unboundExpr = TableauTestFixtures.exprScheme("Unbound");
        HideAction action = new HideAction(nodeScheme, unboundExpr);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("cannot instanciate expression");
    }

    @Test
    @DisplayName("should return unchanged instance set")
    void shouldReturnUnchangedInstanceSet() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        HideAction action = new HideAction(nodeScheme, expr);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        Object result = action.apply(em, instanceSet);

        // Assert
        assertThat(result).isSameAs(instanceSet);
    }

    // ========== Helper Methods ==========

    private MarkedExpression findMarkedExpression(TableauNode node, Expression expr) {
        for (Enumeration<?> e = node.getMarkedExpressionsEnum(); e.hasMoreElements();) {
            MarkedExpression me = (MarkedExpression) e.nextElement();
            if (me.expression.equals(expr)) {
                return me;
            }
        }
        return null;
    }
}
