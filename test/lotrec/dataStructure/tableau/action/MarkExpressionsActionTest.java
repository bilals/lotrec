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
 * Tests for MarkExpressionsAction execution.
 * Verifies that all matching expressions in a node are marked.
 */
@DisplayName("MarkExpressionsAction")
class MarkExpressionsActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should mark expression matching constant form")
    void shouldMarkExpressionMatchingConstantForm() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkExpressionsAction action = new MarkExpressionsAction(nodeScheme, expr, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        MarkedExpression me = findMarkedExpression(node, expr);
        assertThat(me).isNotNull();
        assertThat(me.isMarked(marker)).isTrue();
    }

    @Test
    @DisplayName("should mark all expressions matching variable form")
    void shouldMarkAllExpressionsMatchingVariableForm() {
        // Arrange - add multiple expressions
        ConstantExpression expr1 = TableauTestFixtures.constant("P");
        ConstantExpression expr2 = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, expr1, expr2);
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        // Variable form matches any expression
        VariableExpression form = TableauTestFixtures.exprScheme("A");
        MarkExpressionsAction action = new MarkExpressionsAction(nodeScheme, form, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert - all expressions should be marked
        MarkedExpression me1 = findMarkedExpression(node, expr1);
        MarkedExpression me2 = findMarkedExpression(node, expr2);
        assertThat(me1.isMarked(marker)).isTrue();
        assertThat(me2.isMarked(marker)).isTrue();
    }

    @Test
    @DisplayName("should only mark expressions matching specific form")
    void shouldOnlyMarkExpressionsMatchingSpecificForm() {
        // Arrange
        ConstantExpression expr1 = TableauTestFixtures.constant("P");
        ConstantExpression expr2 = TableauTestFixtures.constant("Q");
        TableauNode node = TableauTestFixtures.createNodeWithExpressions(tableau, expr1, expr2);
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        // Only match expr1
        MarkExpressionsAction action = new MarkExpressionsAction(nodeScheme, expr1, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        MarkedExpression me1 = findMarkedExpression(node, expr1);
        MarkedExpression me2 = findMarkedExpression(node, expr2);
        assertThat(me1.isMarked(marker)).isTrue();
        assertThat(me2.isMarked(marker)).isFalse();
    }

    @Test
    @DisplayName("should not duplicate mark on already marked expression")
    void shouldNotDuplicateMarkOnAlreadyMarked() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);
        String marker = "processed";

        // Pre-mark the expression
        MarkedExpression me = findMarkedExpression(node, expr);
        me.mark(marker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkExpressionsAction action = new MarkExpressionsAction(nodeScheme, expr, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert - should still be marked, just once
        assertThat(me.isMarked(marker)).isTrue();
    }

    @Test
    @DisplayName("should resolve scheme variable marker from instance set")
    void shouldResolveSchemeVariableMarker() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);
        ConstantExpression concreteMarker = TableauTestFixtures.constant("ResolvedMarker");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("M");
        MarkExpressionsAction action = new MarkExpressionsAction(nodeScheme, expr, markerScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("M", concreteMarker));

        // Act
        action.apply(em, instanceSet);

        // Assert
        MarkedExpression me = findMarkedExpression(node, expr);
        assertThat(me.isMarked(concreteMarker)).isTrue();
    }

    @Test
    @DisplayName("should throw ProcessException when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkExpressionsAction action = new MarkExpressionsAction(nodeScheme, expr, marker);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for node");
    }

    @Test
    @DisplayName("should return unchanged instance set")
    void shouldReturnUnchangedInstanceSet() {
        // Arrange
        ConstantExpression expr = TableauTestFixtures.constant("P");
        TableauNode node = TableauTestFixtures.createNodeWithExpression(tableau, expr);
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkExpressionsAction action = new MarkExpressionsAction(nodeScheme, expr, marker);

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
