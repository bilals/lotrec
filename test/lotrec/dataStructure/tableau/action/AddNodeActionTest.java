package lotrec.dataStructure.tableau.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.EventMachine;
import lotrec.process.TestableEventMachine;

/**
 * Tests for AddNodeAction execution.
 * Verifies that nodes are correctly created and added to tableaux,
 * and bound to the instance set.
 */
@DisplayName("AddNodeAction")
class AddNodeActionTest {

    private Tableau tableau;
    private EventMachine em;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        em = TestableEventMachine.forTableau(tableau);
    }

    @Test
    @DisplayName("should create new node and add to tableau")
    void shouldCreateNewNodeAndAddToTableau() {
        // Arrange
        int initialNodeCount = tableau.getNodes().size();

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("newNode");
        AddNodeAction action = new AddNodeAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(tableau.getNodes().size()).isEqualTo(initialNodeCount + 1);
    }

    @Test
    @DisplayName("should bind new node to instance set")
    void shouldBindNewNodeToInstanceSet() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddNodeAction action = new AddNodeAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act
        Object result = action.apply(em, instanceSet);

        // Assert
        assertThat(result).isInstanceOf(InstanceSet.class);
        InstanceSet resultSet = (InstanceSet) result;

        Object boundNode = resultSet.get(nodeScheme);
        assertThat(boundNode).isNotNull();
        assertThat(boundNode).isInstanceOf(TableauNode.class);

        // Verify the bound node is actually in the tableau
        assertThat(tableau.getNodes()).contains((TableauNode) boundNode);
    }

    @Test
    @DisplayName("should create unique nodes for multiple invocations")
    void shouldCreateUniqueNodesForMultipleInvocations() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddNodeAction action = new AddNodeAction(nodeScheme);

        InstanceSet instanceSet1 = TableauTestFixtures.createEmptyInstanceSet();
        InstanceSet instanceSet2 = TableauTestFixtures.createEmptyInstanceSet();

        // Act
        InstanceSet result1 = (InstanceSet) action.apply(em, instanceSet1);
        InstanceSet result2 = (InstanceSet) action.apply(em, instanceSet2);

        // Assert
        TableauNode node1 = (TableauNode) result1.get(nodeScheme);
        TableauNode node2 = (TableauNode) result2.get(nodeScheme);

        assertThat(node1).isNotSameAs(node2);
        assertThat(tableau.getNodes().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("should add node to related tableau from event machine")
    void shouldAddNodeToRelatedTableauFromEventMachine() {
        // Arrange
        Tableau specificTableau = TableauTestFixtures.createTableau("specific");
        EventMachine specificEm = TestableEventMachine.forTableau(specificTableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddNodeAction action = new AddNodeAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act
        InstanceSet result = (InstanceSet) action.apply(specificEm, instanceSet);

        // Assert - node should be in specificTableau, not in default tableau
        TableauNode newNode = (TableauNode) result.get(nodeScheme);
        assertThat(specificTableau.getNodes()).contains(newNode);
        assertThat(tableau.getNodes()).doesNotContain(newNode);
    }

    @Test
    @DisplayName("should create empty node (no expressions)")
    void shouldCreateEmptyNode() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddNodeAction action = new AddNodeAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act
        InstanceSet result = (InstanceSet) action.apply(em, instanceSet);

        // Assert
        TableauNode newNode = (TableauNode) result.get(nodeScheme);
        assertThat(newNode.getMarkedExpressions()).isEmpty();
    }

    @Test
    @DisplayName("should preserve existing bindings in instance set")
    void shouldPreserveExistingBindings() {
        // Arrange
        TableauNode existingNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression existingExpr = TableauTestFixtures.constant("P");

        StringSchemeVariable existingNodeScheme = TableauTestFixtures.nodeScheme("existing");
        VariableExpression existingExprScheme = TableauTestFixtures.exprScheme("A");
        StringSchemeVariable newNodeScheme = TableauTestFixtures.nodeScheme("new");

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("existing", existingNode),
                TableauTestFixtures.exprBinding("A", existingExpr));

        AddNodeAction action = new AddNodeAction(newNodeScheme);

        // Act
        InstanceSet result = (InstanceSet) action.apply(em, instanceSet);

        // Assert - original bindings should still exist
        assertThat(result.get(existingNodeScheme)).isSameAs(existingNode);
        assertThat(result.get(existingExprScheme)).isSameAs(existingExpr);

        // New binding should also exist
        assertThat(result.get(newNodeScheme)).isNotNull();
    }

    @Test
    @DisplayName("should work with empty initial instance set")
    void shouldWorkWithEmptyInstanceSet() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        AddNodeAction action = new AddNodeAction(nodeScheme);

        InstanceSet instanceSet = new InstanceSet();

        // Act
        InstanceSet result = (InstanceSet) action.apply(em, instanceSet);

        // Assert
        assertThat(result.get(nodeScheme)).isNotNull();
        assertThat(result.get(nodeScheme)).isInstanceOf(TableauNode.class);
    }
}
