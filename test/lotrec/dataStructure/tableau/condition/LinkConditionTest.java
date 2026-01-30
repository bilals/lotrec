package lotrec.dataStructure.tableau.condition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.*;

/**
 * Tests for LinkCondition and LinkMatch restriction.
 * Verifies that the condition correctly matches links between nodes.
 */
@DisplayName("LinkCondition")
class LinkConditionTest {

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
    @DisplayName("should match when link exists between two nodes with matching relation")
    void shouldMatchWhenLinkExistsBetweenTwoNodes() {
        // Arrange
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, relation);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, relation);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", nodes[0]),
                TableauTestFixtures.nodeBinding("to", nodes[1]));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match when no link exists between nodes")
    void shouldNotMatchWhenNoLinkExists() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, relation);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode),
                TableauTestFixtures.nodeBinding("to", toNode));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should not match when link has different relation")
    void shouldNotMatchWhenLinkHasDifferentRelation() {
        // Arrange
        ConstantExpression existingRelation = TableauTestFixtures.constant("R1");
        ConstantExpression searchRelation = TableauTestFixtures.constant("R2");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, existingRelation);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, searchRelation);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", nodes[0]),
                TableauTestFixtures.nodeBinding("to", nodes[1]));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should bind variable relation to concrete value")
    void shouldBindVariableRelationToConcreteValue() {
        // Arrange
        ConstantExpression concreteRelation = TableauTestFixtures.constant("ConcreteR");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, concreteRelation);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        VariableExpression relScheme = TableauTestFixtures.exprScheme("R");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, relScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", nodes[0]),
                TableauTestFixtures.nodeBinding("to", nodes[1]));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
        ActionPack pack = stocking.getPacks().getFirst();
        InstanceSet result = (InstanceSet) pack.getModifier();
        assertThat(result.get(relScheme)).isEqualTo(concreteRelation);
    }

    @Test
    @DisplayName("should discover destination node when only from node is bound")
    void shouldDiscoverDestinationNodeWhenOnlyFromBound() {
        // Arrange
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, relation);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, relation);
        Restriction restriction = condition.createRestriction();

        // Only bind the from node
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", nodes[0]));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
        ActionPack pack = stocking.getPacks().getFirst();
        InstanceSet result = (InstanceSet) pack.getModifier();
        // The toScheme should be bound to the actual destination node
        assertThat(result.get(toScheme)).isEqualTo(nodes[1]);
    }

    @Test
    @DisplayName("should discover source node when only to node is bound")
    void shouldDiscoverSourceNodeWhenOnlyToBound() {
        // Arrange
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauNode[] nodes = TableauTestFixtures.createLinkedNodes(tableau, relation);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, relation);
        Restriction restriction = condition.createRestriction();

        // Only bind the to node
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("to", nodes[1]));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
        ActionPack pack = stocking.getPacks().getFirst();
        InstanceSet result = (InstanceSet) pack.getModifier();
        // The fromScheme should be bound to the actual source node
        assertThat(result.get(fromScheme)).isEqualTo(nodes[0]);
    }

    @Test
    @DisplayName("should throw when neither node is bound")
    void shouldThrowWhenNeitherNodeBound() {
        // Arrange
        ConstantExpression relation = TableauTestFixtures.constant("R");

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, relation);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> restriction.attemptToApply(actionContainer, instanceSet, stocking, em))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("nodeFrom")
                .hasMessageContaining("nodeTo");
    }

    @Test
    @DisplayName("should match self-loop")
    void shouldMatchSelfLoop() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauTestFixtures.linkNodes(node, node, relation);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        LinkCondition condition = new LinkCondition(nodeScheme, nodeScheme, relation);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should match multiple links with variable pattern")
    void shouldMatchMultipleLinksWithVariablePattern() {
        // Arrange
        TableauNode fromNode = TableauTestFixtures.createNode(tableau);
        TableauNode toNode1 = TableauTestFixtures.createNode(tableau);
        TableauNode toNode2 = TableauTestFixtures.createNode(tableau);
        ConstantExpression relation = TableauTestFixtures.constant("R");
        TableauTestFixtures.linkNodes(fromNode, toNode1, relation);
        TableauTestFixtures.linkNodes(fromNode, toNode2, relation);

        StringSchemeVariable fromScheme = TableauTestFixtures.nodeScheme("from");
        StringSchemeVariable toScheme = TableauTestFixtures.nodeScheme("to");
        LinkCondition condition = new LinkCondition(fromScheme, toScheme, relation);
        Restriction restriction = condition.createRestriction();

        // Only bind from node to discover all destinations
        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("from", fromNode));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should have two action packs
        assertThat(stocking.getPacks().size()).isEqualTo(2);
    }
}
