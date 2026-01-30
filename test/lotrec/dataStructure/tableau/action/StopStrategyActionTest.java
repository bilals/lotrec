package lotrec.dataStructure.tableau.action;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.*;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.engine.TestableEngine;
import lotrec.process.EventMachine;
import lotrec.process.Strategy;
import lotrec.process.TestableEventMachine;
import lotrec.process.ProcessException;

/**
 * Tests for StopStrategyAction execution.
 * Verifies that the strategy is stopped via the engine.
 */
@DisplayName("StopStrategyAction")
class StopStrategyActionTest {

    private Tableau tableau;
    private TestableEngine engine;
    private EventMachine em;
    private Strategy strategy;

    @BeforeEach
    void setUp() {
        tableau = TableauTestFixtures.createTableau("test");
        engine = TestableEngine.create();

        // Set up strategy on tableau
        strategy = new Strategy();
        strategy.setWorkerName("TestStrategy");
        strategy.setEngine(engine);
        strategy.setRelatedTableau(tableau);
        tableau.setStrategy(strategy);

        em = TestableEventMachine.configured(tableau, engine);
    }

    @Test
    @DisplayName("should stop tableau via engine")
    void shouldStopTableauViaEngine() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        StopStrategyAction action = new StopStrategyAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(engine.wasTableauStopped(tableau)).isTrue();
    }

    @Test
    @DisplayName("should set shouldStopStrategy flag on tableau")
    void shouldSetShouldStopStrategyFlag() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        StopStrategyAction action = new StopStrategyAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Verify initially not stopped
        assertThat(tableau.shouldStopStrategy()).isFalse();

        // Act
        action.apply(em, instanceSet);

        // Assert
        assertThat(tableau.shouldStopStrategy()).isTrue();
    }

    @Test
    @DisplayName("should throw ProcessException when node not in instance set")
    void shouldThrowWhenNodeNotInInstanceSet() {
        // Arrange
        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        StopStrategyAction action = new StopStrategyAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createEmptyInstanceSet();

        // Act & Assert
        assertThatThrownBy(() -> action.apply(em, instanceSet))
                .isInstanceOf(ProcessException.class)
                .hasMessageContaining("instance for node");
    }

    @Test
    @DisplayName("should return unchanged modifier")
    void shouldReturnUnchangedModifier() {
        // Arrange
        TableauNode node = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        StopStrategyAction action = new StopStrategyAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        Object result = action.apply(em, instanceSet);

        // Assert
        assertThat(result).isSameAs(instanceSet);
    }

    @Test
    @DisplayName("should stop correct tableau when node is from that tableau")
    void shouldStopCorrectTableau() {
        // Arrange
        Tableau otherTableau = TableauTestFixtures.createTableau("other");
        Strategy otherStrategy = new Strategy();
        otherStrategy.setEngine(engine);
        otherStrategy.setRelatedTableau(otherTableau);
        otherTableau.setStrategy(otherStrategy);

        // Create node in the main tableau, not otherTableau
        TableauNode node = TableauTestFixtures.createNode(tableau);

        StringSchemeVariable nodeScheme = TableauTestFixtures.nodeScheme("n");
        StopStrategyAction action = new StopStrategyAction(nodeScheme);

        InstanceSet instanceSet = TableauTestFixtures.createInstanceSetWith(
                TableauTestFixtures.nodeBinding("n", node));

        // Act
        action.apply(em, instanceSet);

        // Assert - only the main tableau should be stopped
        assertThat(engine.wasTableauStopped(tableau)).isTrue();
        assertThat(engine.wasTableauStopped(otherTableau)).isFalse();
    }
}
