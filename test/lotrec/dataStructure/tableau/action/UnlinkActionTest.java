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
 * Tests for UnlinkAction execution.
 * Verifies that edges are correctly removed between nodes.
 */
@DisplayName("UnlinkAction")
class UnlinkActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should unlink two nodes with matching relation")
    void shouldUnlinkTwoNodesWithMatchingRelation() {
        // Arrange
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, relation);
        TableauNode fromNode = nodes[0];
        TableauNode toNode = nodes[1];

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        UnlinkAction action = new UnlinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        // Verify edge exists before
        assertThat(hasEdgeTo(fromNode, toNode, relation)).isTrue();

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(hasEdgeTo(fromNode, toNode, relation)).isFalse();
    }

    @Test
    @DisplayName("should not affect edges with different relation")
    void shouldNotAffectEdgesWithDifferentRelation() {
        // Arrange
        ConstantExpression relation1 = TableauTestFixtures.constant("R1");
        ConstantExpression relation2 = TableauTestFixtures.constant("R2");
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);

        // Create edges with both relations
        TableauTestFixtures.linkNodes(fromNode, toNode, relation1);
        TableauTestFixtures.linkNodes(fromNode, toNode, relation2);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        // Only unlink relation1
        UnlinkAction action = new UnlinkAction(fromScheme, toScheme, relation1);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        // Act
        action.apply(em, instanceSet);

        // Assert - only relation1 edge should be removed
        assertThat(hasEdgeTo(fromNode, toNode, relation1)).isFalse();
        assertThat(hasEdgeTo(fromNode, toNode, relation2)).isTrue();
    }

    @Test
    @DisplayName("should handle unlink when no matching edge exists")
    void shouldHandleUnlinkWhenNoEdgeExists() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");
        // Note: no edge created

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        UnlinkAction action = new UnlinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        // Act & Assert - should not throw
        assertThatCode(() -> action.apply(em, instanceSet)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("should throw ProcessException when from node missing")
    void shouldThrowWhenFromNodeMissing() {
        // Arrange
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        UnlinkAction action = new UnlinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("to", toNode));

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("nodeFrom");
    }

    @Test
    @DisplayName("should throw ProcessException when to node missing")
    void shouldThrowWhenToNodeMissing() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        UnlinkAction action = new UnlinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode));

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("nodeTo");
    }

    @Test
    @DisplayName("should throw ProcessException when relation cannot be instantiated")
    void shouldThrowWhenRelationUnbound() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        VariableExpression unboundRelation = TableauTestFixtures.exprScheme("Unbound");

        UnlinkAction action = new UnlinkAction(fromScheme, toScheme, unboundRelation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("cannot instanciate expression");
    }

    @Test
    @DisplayName("should return unchanged instance set")
    void shouldReturnUnchangedInstanceSet() {
        // Arrange
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, relation);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        UnlinkAction action = new UnlinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", nodes[0]),
                TableauTestFixtures.nodeBinding("to", nodes[1]));

        // Act
        Object result = action.apply(em, instanceSet);

        // Assert
        assertThat(result).isSameAs(instanceSet);
    }

    // ========== Helper Methods ==========

    private boolean hasEdgeTo(TableauNode fromNode, TableauNode toNode, Expression relation) {
        for (Enumeration<?> e = fromNode.getNextEdgesEnum(); e.hasMoreElements();) {
            TableauEdge edge = (TableauEdge) e.nextElement();
            if (edge.getEndNode().equals(toNode) && edge.getRelation().equals(relation)) {
                return true;
            }
        }
        return false;
    }
}
