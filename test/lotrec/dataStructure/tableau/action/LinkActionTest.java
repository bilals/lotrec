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
 * Tests for LinkAction execution.
 * Verifies that edges are correctly created between nodes with proper relations.
 */
@DisplayName("LinkAction")
class LinkActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should link two nodes with constant relation")
    void shouldLinkTwoNodesWithConstantRelation() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkAction action = new LinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(hasEdgeTo(fromNode, toNode, relation)).isTrue();
    }

    @Test
    @DisplayName("should not create duplicate edge with same relation")
    void shouldNotCreateDuplicateEdge() {
        // Arrange
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, relation);
        TableauNode fromNode = nodes[0];
        TableauNode toNode = nodes[1];

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkAction action = new LinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        int initialEdgeCount = countEdgesFrom(fromNode);

        // Act
        action.apply(em, instanceSet);

        // Assert - edge count should not increase
        assertThat(countEdgesFrom(fromNode)).isEqualTo(initialEdgeCount);
    }

    @Test
    @DisplayName("should create self-loop (node linked to itself)")
    void shouldCreateSelfLoop() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        LinkAction action = new LinkAction(nodeScheme, nodeScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(hasEdgeTo(node, node, relation)).isTrue();
    }

    @Test
    @DisplayName("should instantiate variable relation from instance set")
    void shouldInstantiateVariableRelation() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression concreteRelation = TableauTestFixtures.constant("ConcreteR");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        VariableExpression relationScheme = TableauTestFixtures.exprScheme("R");

        LinkAction action = new LinkAction(fromScheme, toScheme, relationScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode),
                TableauTestFixtures.exprBinding("R", concreteRelation));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(hasEdgeTo(fromNode, toNode, concreteRelation)).isTrue();
    }

    @Test
    @DisplayName("should throw ProcessException when from node not in instance set")
    void shouldThrowWhenFromNodeMissing() {
        // Arrange
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkAction action = new LinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("to", toNode));
        // Missing "from" binding

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("nodeFrom");
    }

    @Test
    @DisplayName("should throw ProcessException when to node not in instance set")
    void shouldThrowWhenToNodeMissing() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkAction action = new LinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode));
        // Missing "to" binding

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("nodeTo");
    }

    @Test
    @DisplayName("should throw ProcessException when relation cannot be instantiated")
    void shouldThrowWhenRelationCannotBeInstantiated() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        VariableExpression unboundRelation = TableauTestFixtures.exprScheme("Unbound");

        LinkAction action = new LinkAction(fromScheme, toScheme, unboundRelation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));
        // Missing relation binding

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("cannot instanciate expression");
    }

    @Test
    @DisplayName("should allow multiple edges between same nodes with different relations")
    void shouldAllowMultipleEdgesWithDifferentRelations() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation1 = TableauTestFixtures.constant("R1");
        ConstantExpression relation2 = TableauTestFixtures.constant("R2");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");

        LinkAction action1 = new LinkAction(fromScheme, toScheme, relation1);
        LinkAction action2 = new LinkAction(fromScheme, toScheme, relation2);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        // Act
        action1.apply(em, instanceSet);
        action2.apply(em, instanceSet);

        // Assert - both edges should exist
        assertThat(hasEdgeTo(fromNode, toNode, relation1)).isTrue();
        assertThat(hasEdgeTo(fromNode, toNode, relation2)).isTrue();
        assertThat(countEdgesFrom(fromNode)).isEqualTo(2);
    }

    @Test
    @DisplayName("should return unchanged instance set")
    void shouldReturnUnchangedInstanceSet() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkAction action = new LinkAction(fromScheme, toScheme, relation);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

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

    private int countEdgesFrom(TableauNode node) {
        int count = 0;
        for (Enumeration<?> e = node.getNextEdgesEnum(); e.hasMoreElements();) {
            e.nextElement();
            count++;
        }
        return count;
    }
}
