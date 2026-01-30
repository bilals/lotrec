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
 * Tests for UnmarkAction execution.
 * Verifies that marks are correctly removed from nodes.
 */
@DisplayName("UnmarkAction")
class UnmarkActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should unmark node with string marker")
    void shouldUnmarkNodeWithStringMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        node.mark(marker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        UnmarkAction action = new UnmarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Verify initially marked
        assertThat(node.isMarked(marker)).isTrue();

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(marker)).isFalse();
    }

    @Test
    @DisplayName("should handle unmark when node is not marked")
    void shouldHandleUnmarkWhenNotMarked() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        // Note: node is NOT marked

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        UnmarkAction action = new UnmarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act & Assert - should not throw
        assertThatCode(() -> action.apply(em, instanceSet)).doesNotThrowAnyException();
        assertThat(node.isMarked(marker)).isFalse();
    }

    @Test
    @DisplayName("should unmark specific marker leaving others intact")
    void shouldUnmarkSpecificMarkerLeavingOthersIntact() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker1 = "mark1";
        String marker2 = "mark2";
        node.mark(marker1);
        node.mark(marker2);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        UnmarkAction action = new UnmarkAction(nodeScheme, marker1);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(marker1)).isFalse();
        assertThat(node.isMarked(marker2)).isTrue();
    }

    @Test
    @DisplayName("should resolve scheme variable marker from instance set")
    void shouldResolveSchemeVariableMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression concreteMarker = TableauTestFixtures.constant("ResolvedMarker");
        node.mark(concreteMarker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("M");

        UnmarkAction action = new UnmarkAction(nodeScheme, markerScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("M", concreteMarker));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(concreteMarker)).isFalse();
    }

    @Test
    @DisplayName("should use scheme variable itself if not bound")
    void shouldUseSchemeVariableItselfIfNotBound() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("UnboundM");
        node.mark(markerScheme); // Mark with the scheme variable itself

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        UnmarkAction action = new UnmarkAction(nodeScheme, markerScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));
        // Note: markerScheme is NOT bound

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(node.isMarked(markerScheme)).isFalse();
    }

    @Test
    @DisplayName("should throw ProcessException when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        String marker = "processed";
        UnmarkAction action = new UnmarkAction(nodeScheme, marker);

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
        node.mark(marker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        UnmarkAction action = new UnmarkAction(nodeScheme, marker);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        Object result = action.apply(em, instanceSet);

        // Assert
        assertThat(result).isSameAs(instanceSet);
    }
}
