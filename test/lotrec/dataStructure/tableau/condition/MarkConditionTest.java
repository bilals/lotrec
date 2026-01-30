package lotrec.dataStructure.tableau.condition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.*;

/**
 * Tests for MarkCondition and MarkConstraint restriction.
 * Verifies that the condition correctly checks for marks on nodes.
 */
@DisplayName("MarkCondition")
class MarkConditionTest {

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
    @DisplayName("should match when node has specified mark")
    void shouldMatchWhenNodeHasSpecifiedMark() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        node.mark(marker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match when node lacks specified mark")
    void shouldNotMatchWhenNodeLacksSpecifiedMark() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        // Note: node is NOT marked

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should not match when node has different mark")
    void shouldNotMatchWhenNodeHasDifferentMark() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String actualMark = "mark1";
        String searchMark = "mark2";
        node.mark(actualMark);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, searchMark);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should match with expression marker")
    void shouldMatchWithExpressionMarker() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression marker = TableauTestFixtures.constant("ExprMarker");
        node.mark(marker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should resolve scheme variable marker from instance set")
    void shouldResolveSchemeVariableMarkerFromInstanceSet() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression concreteMarker = TableauTestFixtures.constant("ResolvedMarker");
        node.mark(concreteMarker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("M");
        MarkCondition condition = new MarkCondition(nodeScheme, markerScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("M", concreteMarker));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should use scheme variable itself when not bound")
    void shouldUseSchemeVariableItselfWhenNotBound() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("UnboundM");
        node.mark(markerScheme); // Mark with the scheme variable itself

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, markerScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));
        // Note: markerScheme is NOT bound

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should throw when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> restriction.attemptToApply(actionContainer, instanceSet, stocking, em))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for marked");
    }

    @Test
    @DisplayName("should match when node has multiple marks including target")
    void shouldMatchWhenNodeHasMultipleMarksIncludingTarget() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String mark1 = "mark1";
        String mark2 = "mark2";
        node.mark(mark1);
        node.mark(mark2);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, mark2);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not modify instance set (acts as constraint)")
    void shouldNotModifyInstanceSet() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        node.mark(marker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        MarkCondition condition = new MarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - the modifier in the pack should be the same instance set
        ActionPack pack = stocking.getPacks().getFirst();
        assertThat(pack.getModifier()).isSameAs(instanceSet);
    }
}
