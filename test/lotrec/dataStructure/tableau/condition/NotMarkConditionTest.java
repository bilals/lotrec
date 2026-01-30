package lotrec.dataStructure.tableau.condition;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.process.*;

/**
 * Tests for NotMarkCondition and NotMarkConstraint restriction.
 * Verifies that the condition correctly checks for ABSENCE of marks on nodes.
 */
@DisplayName("NotMarkCondition")
class NotMarkConditionTest {

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
    @DisplayName("should match when node does NOT have specified mark")
    void shouldMatchWhenNodeDoesNotHaveMark() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        // Note: node is NOT marked

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotMarkCondition condition = new NotMarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should not match when node has specified mark")
    void shouldNotMatchWhenNodeHasMark() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";
        node.mark(marker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotMarkCondition condition = new NotMarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should match when node has different mark")
    void shouldMatchWhenNodeHasDifferentMark() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String actualMark = "mark1";
        String searchMark = "mark2";
        node.mark(actualMark);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotMarkCondition condition = new NotMarkCondition(nodeScheme, searchMark);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should match because node doesn't have mark2
        assertThat(stocking.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("should resolve scheme variable marker from instance set")
    void shouldResolveSchemeVariableMarkerFromInstanceSet() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression concreteMarker = TableauTestFixtures.constant("ResolvedMarker");
        // Note: node does NOT have this mark

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("M");
        NotMarkCondition condition = new NotMarkCondition(nodeScheme, markerScheme);
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
    @DisplayName("should not match with resolved marker that node has")
    void shouldNotMatchWithResolvedMarkerThatNodeHas() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        ConstantExpression concreteMarker = TableauTestFixtures.constant("ResolvedMarker");
        node.mark(concreteMarker);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        VariableExpression markerScheme = TableauTestFixtures.exprScheme("M");
        NotMarkCondition condition = new NotMarkCondition(nodeScheme, markerScheme);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node),
                TableauTestFixtures.exprBinding("M", concreteMarker));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert - should NOT match because node HAS the resolved marker
        assertThat(stocking.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("should throw when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotMarkCondition condition = new NotMarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> restriction.attemptToApply(actionContainer, instanceSet, stocking, em))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for marked");
    }

    @Test
    @DisplayName("should not modify instance set (acts as constraint)")
    void shouldNotModifyInstanceSet() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);
        String marker = "processed";

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        NotMarkCondition condition = new NotMarkCondition(nodeScheme, marker);
        Restriction restriction = condition.createRestriction();

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        restriction.attemptToApply(actionContainer, instanceSet, stocking, em);

        // Assert
        assertThat(stocking.isEmpty()).isFalse();
        ActionPack pack = stocking.getPacks().getFirst();
        assertThat(pack.getModifier()).isSameAs(instanceSet);
    }
}
