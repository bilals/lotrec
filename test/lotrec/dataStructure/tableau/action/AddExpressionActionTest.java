package lotrec.dataStructure.tableau.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.EventMachine;
import lotrec.process.TestableEventMachine;
import lotrec.process.ProcessException;

/**
 * Tests for AddExpressionAction execution.
 * Verifies that expressions are correctly added to nodes and duplicate
 * expressions are skipped.
 */
@DisplayName("AddExpressionAction")
class AddExpressionActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should add constant expression to node")
    void shouldAddConstantExpressionToNode() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression exprToAdd = TableauTestFixtures.constant("Q");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddExpressionAction action = new AddExpressionAction(nodeScheme, exprToAdd);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.contains(exprToAdd)).isTrue();
        assertThat(TableauTestFixtures.expressionCount(node)).isEqualTo(1);
    }

    @Test
    @DisplayName("should add expression to node already containing other expressions")
    void shouldAddExpressionToNodeWithExistingExpressions() {
        // Arrange
        ConstantExpression existingExpr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, existingExpr);
        ConstantExpression newExpr = TableauTestFixtures.constant("Q");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddExpressionAction action = new AddExpressionAction(nodeScheme, newExpr);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.contains(existingExpr)).isTrue();
        assertThat(node.contains(newExpr)).isTrue();
        assertThat(TableauTestFixtures.expressionCount(node)).isEqualTo(2);
    }

    @Test
    @DisplayName("should skip duplicate expression (not add twice)")
    void shouldSkipDuplicateExpression() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        // Try to add the same expression again
        AddExpressionAction action = new AddExpressionAction(nodeScheme, expr);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert - should still have only 1 expression
        assertThat(node.contains(expr)).isTrue();
        assertThat(TableauTestFixtures.expressionCount(node)).isEqualTo(1);
    }

    @Test
    @DisplayName("should close node when FALSUM is added")
    void shouldCloseNodeWhenFalsumAdded() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression falsum = TableauTestFixtures.falsum();

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddExpressionAction action = new AddExpressionAction(nodeScheme, falsum);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Verify node is initially open
        assertThat(node.isClosed()).isFalse();

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.contains(falsum)).isTrue();
        assertThat(node.isClosed()).isTrue();
    }

    @Test
    @DisplayName("should instantiate variable expression from instance set")
    void shouldInstantiateVariableExpression() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression concreteExpr = TableauTestFixtures.constant("ConcreteValue");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression exprScheme = TableauTestFixtures.exprScheme("A");
        AddExpressionAction action = new AddExpressionAction(nodeScheme, exprScheme);

        // InstanceSet with variable A bound to ConcreteValue
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("A", concreteExpr));

        // Act
        action.apply(em, instanceSet);

        // Assert - the instantiated (concrete) expression should be added
        assertThat(node.contains(concreteExpr)).isTrue();
    }

    @Test
    @DisplayName("should throw ProcessException when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        ConstantExpression expr = TableauTestFixtures.constant("P");
        AddExpressionAction action = new AddExpressionAction(nodeScheme, expr);

        // Empty instance set - no node binding
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
        // Variable expression without binding in instance set
        VariableExpression unboundExpr = TableauTestFixtures.exprScheme("Unbound");
        AddExpressionAction action = new AddExpressionAction(nodeScheme, unboundExpr);

        // Instance set with node but without the expression variable
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
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression expr = TableauTestFixtures.constant("P");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddExpressionAction action = new AddExpressionAction(nodeScheme, expr);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        Object result = action.apply(em, instanceSet);

        // Assert
        assertThat(result).isSameAs(instanceSet);
    }
}
