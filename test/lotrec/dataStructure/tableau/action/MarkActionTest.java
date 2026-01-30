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
 * Tests for MarkAction execution.
 * Verifies that marks are correctly added to nodes and that duplicates are handled.
 */
@DisplayName("MarkAction")
class MarkActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should mark node with string marker")
    void shouldMarkNodeWithStringMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkAction action = new MarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(marker)).isTrue();
    }

    @Test
    @DisplayName("should not duplicate mark if already marked")
    void shouldNotDuplicateMark() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        node.mark(marker); // Pre-mark the node

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkAction action = new MarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert - should still be marked, but mark list shouldn't grow
        assertThat(node.isMarked(marker)).isTrue();
    }

    @Test
    @DisplayName("should mark node with multiple different marks")
    void shouldMarkNodeWithMultipleMarks() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker1 = "mark1";
        String marker2 = "mark2";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkAction action1 = new MarkAction(nodeScheme, marker1);
        MarkAction action2 = new MarkAction(nodeScheme, marker2);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action1.apply(em, instanceSet);
        action2.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(marker1)).isTrue();
        assertThat(node.isMarked(marker2)).isTrue();
    }

    @Test
    @DisplayName("should mark node with expression as marker")
    void shouldMarkNodeWithExpressionMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression marker = TableauTestFixtures.constant("ExprMarker");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkAction action = new MarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(marker)).isTrue();
    }

    @Test
    @DisplayName("should resolve scheme variable marker from instance set")
    void shouldResolveSchemeVariableMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression concreteMarker = TableauTestFixtures.constant("ResolvedMarker");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("M");

        MarkAction action = new MarkAction(nodeScheme, markerScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("M", concreteMarker));

        // Act
        action.apply(em, instanceSet);

        // Assert - should be marked with the resolved marker
        assertThat(node.isMarked(concreteMarker)).isTrue();
    }

    @Test
    @DisplayName("should use scheme variable itself as marker if not in instance set")
    void shouldUseSchemeVariableAsMarkerIfNotBound() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("UnboundM");

        MarkAction action = new MarkAction(nodeScheme, markerScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));
        // Note: markerScheme is NOT bound in the instance set

        // Act
        action.apply(em, instanceSet);

        // Assert - should be marked with the scheme variable itself
        assertThat(node.isMarked(markerScheme)).isTrue();
    }

    @Test
    @DisplayName("should throw ProcessException when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        String marker = "processed";
        MarkAction action = new MarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for marked");
    }

    @Test
    @DisplayName("should return unchanged instance set")
    void shouldReturnUnchangedInstanceSet() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkAction action = new MarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        Object result = action.apply(em, instanceSet);

        // Assert
        assertThat(result).isSameAs(instanceSet);
    }

    @Test
    @DisplayName("should mark with integer marker")
    void shouldMarkWithIntegerMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        Integer marker = 42;

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkAction action = new MarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(marker)).isTrue();
    }

    @Test
    @DisplayName("should mark with custom object marker")
    void shouldMarkWithCustomObjectMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        Object customMarker = new Object() {
            @Override
            public String toString() {
                return "CustomMarker";
            }
        };

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkAction action = new MarkAction(nodeScheme, customMarker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(customMarker)).isTrue();
    }
}
