package lotrec.process;

import lotrec.dataStructure.tableau.Tableau;
import lotrec.engine.Engine;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for FirstRule strategy behavior.
 * FirstRule applies the first matching (non-quiet) rule from its list and stops.
 */
@DisplayName("FirstRule")
class FirstRuleTest {

    private FirstRule firstRule;
    private Engine mockEngine;
    private Tableau tableau;

    @BeforeEach
    void setUp() {
        firstRule = new FirstRule();
        mockEngine = new TestEngine();
        tableau = new Tableau("TestTableau");

        firstRule.setEngine(mockEngine);
        firstRule.setRelatedTableau(tableau);
    }

    @Nested
    @DisplayName("Applying Rules")
    class ApplyingRules {

        @Test
        @DisplayName("should apply first matching rule")
        void shouldApplyFirstMatchingRule() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", false, true);  // not quiet, will work
            MockWorker worker2 = new MockWorker("Rule2", false, true);  // not quiet, would work
            MockWorker worker3 = new MockWorker("Rule3", false, true);  // not quiet, would work

            firstRule.add(worker1, null);
            firstRule.add(worker2, null);
            firstRule.add(worker3, null);

            // Act
            firstRule.work();

            // Assert
            assertThat(worker1.wasWorkCalled()).isTrue();
            assertThat(worker2.wasWorkCalled()).isFalse();
            assertThat(worker3.wasWorkCalled()).isFalse();
            assertThat(firstRule.hasWorked()).isTrue();
        }

        @Test
        @DisplayName("should return after first match without checking remaining rules")
        void shouldReturnAfterFirstMatch() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", true, false);   // quiet, skip
            MockWorker worker2 = new MockWorker("Rule2", false, true);   // not quiet, will work
            MockWorker worker3 = new MockWorker("Rule3", false, true);   // not quiet, should not be reached

            firstRule.add(worker1, null);
            firstRule.add(worker2, null);
            firstRule.add(worker3, null);

            // Act
            firstRule.work();

            // Assert
            assertThat(worker1.wasWorkCalled()).isFalse();  // quiet workers are skipped
            assertThat(worker2.wasWorkCalled()).isTrue();   // first non-quiet that worked
            assertThat(worker3.wasWorkCalled()).isFalse();  // should not be reached
            assertThat(firstRule.hasWorked()).isTrue();
        }

        @Test
        @DisplayName("should handle no matching rule")
        void shouldHandleNoMatchingRule() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", false, false);  // not quiet, but won't work
            MockWorker worker2 = new MockWorker("Rule2", false, false);  // not quiet, but won't work
            MockWorker worker3 = new MockWorker("Rule3", false, false);  // not quiet, but won't work

            firstRule.add(worker1, null);
            firstRule.add(worker2, null);
            firstRule.add(worker3, null);

            // Act
            firstRule.work();

            // Assert - all workers were tried but none worked
            assertThat(worker1.wasWorkCalled()).isTrue();
            assertThat(worker2.wasWorkCalled()).isTrue();
            assertThat(worker3.wasWorkCalled()).isTrue();
            assertThat(firstRule.hasWorked()).isFalse();
        }

        @Test
        @DisplayName("should respect rule order when multiple rules could match")
        void shouldRespectRuleOrder() {
            // Arrange - all workers can work, order matters
            MockWorker worker1 = new MockWorker("Rule1", false, true);
            MockWorker worker2 = new MockWorker("Rule2", false, true);
            MockWorker worker3 = new MockWorker("Rule3", false, true);

            // Add in specific order
            firstRule.add(worker1, null);
            firstRule.add(worker2, null);
            firstRule.add(worker3, null);

            // Act
            firstRule.work();

            // Assert - only first worker should be executed
            assertThat(worker1.wasWorkCalled()).isTrue();
            assertThat(worker2.wasWorkCalled()).isFalse();
            assertThat(worker3.wasWorkCalled()).isFalse();
            assertThat(worker1.getExecutionOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("should skip quiet workers and apply first non-quiet worker")
        void shouldSkipQuietWorkers() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", true, true);   // quiet
            MockWorker worker2 = new MockWorker("Rule2", true, true);   // quiet
            MockWorker worker3 = new MockWorker("Rule3", false, true);  // not quiet, will work

            firstRule.add(worker1, null);
            firstRule.add(worker2, null);
            firstRule.add(worker3, null);

            // Act
            firstRule.work();

            // Assert
            assertThat(worker1.wasWorkCalled()).isFalse();
            assertThat(worker2.wasWorkCalled()).isFalse();
            assertThat(worker3.wasWorkCalled()).isTrue();
            assertThat(firstRule.hasWorked()).isTrue();
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCases {

        @Test
        @DisplayName("should handle empty rule list")
        void shouldHandleEmptyRuleList() {
            // Arrange - no workers added

            // Act
            firstRule.work();

            // Assert
            assertThat(firstRule.hasWorked()).isFalse();
            assertThat(firstRule.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should handle single rule that matches")
        void shouldHandleSingleRuleThatMatches() {
            // Arrange
            MockWorker worker = new MockWorker("OnlyRule", false, true);
            firstRule.add(worker, null);

            // Act
            firstRule.work();

            // Assert
            assertThat(worker.wasWorkCalled()).isTrue();
            assertThat(firstRule.hasWorked()).isTrue();
        }

        @Test
        @DisplayName("should handle single rule that does not match")
        void shouldHandleSingleRuleThatDoesNotMatch() {
            // Arrange
            MockWorker worker = new MockWorker("OnlyRule", false, false);
            firstRule.add(worker, null);

            // Act
            firstRule.work();

            // Assert
            assertThat(worker.wasWorkCalled()).isTrue();
            assertThat(firstRule.hasWorked()).isFalse();
        }

        @Test
        @DisplayName("should handle single quiet rule")
        void shouldHandleSingleQuietRule() {
            // Arrange
            MockWorker worker = new MockWorker("QuietRule", true, true);
            firstRule.add(worker, null);

            // Act
            firstRule.work();

            // Assert
            assertThat(worker.wasWorkCalled()).isFalse();
            assertThat(firstRule.hasWorked()).isFalse();
            assertThat(firstRule.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should handle all quiet rules")
        void shouldHandleAllQuietRules() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", true, true);
            MockWorker worker2 = new MockWorker("Rule2", true, true);
            MockWorker worker3 = new MockWorker("Rule3", true, true);

            firstRule.add(worker1, null);
            firstRule.add(worker2, null);
            firstRule.add(worker3, null);

            // Act
            firstRule.work();

            // Assert
            assertThat(worker1.wasWorkCalled()).isFalse();
            assertThat(worker2.wasWorkCalled()).isFalse();
            assertThat(worker3.wasWorkCalled()).isFalse();
            assertThat(firstRule.hasWorked()).isFalse();
            assertThat(firstRule.isQuiet()).isTrue();
        }
    }

    @Nested
    @DisplayName("isQuiet Behavior")
    class IsQuietBehavior {

        @Test
        @DisplayName("should be quiet when empty")
        void shouldBeQuietWhenEmpty() {
            // Assert
            assertThat(firstRule.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should not be quiet when any worker is not quiet")
        void shouldNotBeQuietWhenAnyWorkerNotQuiet() {
            // Arrange
            MockWorker quietWorker = new MockWorker("Quiet", true, false);
            MockWorker activeWorker = new MockWorker("Active", false, false);

            firstRule.add(quietWorker, null);
            firstRule.add(activeWorker, null);

            // Assert
            assertThat(firstRule.isQuiet()).isFalse();
        }

        @Test
        @DisplayName("should be quiet when all workers are quiet")
        void shouldBeQuietWhenAllWorkersQuiet() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", true, false);
            MockWorker worker2 = new MockWorker("Rule2", true, false);

            firstRule.add(worker1, null);
            firstRule.add(worker2, null);

            // Assert
            assertThat(firstRule.isQuiet()).isTrue();
        }
    }

    @Nested
    @DisplayName("Duplication")
    class Duplication {

        @Test
        @DisplayName("should duplicate with workers")
        void shouldDuplicateWithWorkers() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", false, true);
            MockWorker worker2 = new MockWorker("Rule2", false, true);

            firstRule.add(worker1, null);
            firstRule.add(worker2, null);

            // Act
            Duplicator duplicator = new TestDuplicator();
            FirstRule duplicated = (FirstRule) firstRule.duplicate(duplicator);

            // Assert
            assertThat(duplicated).isNotNull();
            assertThat(duplicated.getWorkerName()).isEqualTo("FirstRule");
            assertThat(duplicated.getWorkers()).hasSize(2);
        }
    }

    // ========== Test Doubles ==========

    /**
     * Simple test engine that provides synchronized access and never stops.
     */
    private static class TestEngine extends Engine {
        @Override
        public synchronized boolean shouldStop() {
            return false;
        }
    }

    /**
     * Test duplicator for duplication tests.
     */
    private static class TestDuplicator implements Duplicator {
        @Override
        public void setImage(Object original, Object image) {
            // No-op for testing
        }

        @Override
        public Object getImage(Object original) {
            return original;
        }

        @Override
        public boolean hasImage(Object original) {
            return false;
        }
    }

    /**
     * Mock worker for testing FirstRule behavior.
     * Allows controlling quiet state and work result.
     */
    private static class MockWorker extends AbstractWorker {
        private final boolean quiet;
        private final boolean willWork;
        private boolean workCalled = false;
        private int executionOrder = 0;
        private static int executionCounter = 0;

        MockWorker(String name, boolean quiet, boolean willWork) {
            this.quiet = quiet;
            this.willWork = willWork;
            setWorkerName(name);
            // Reset counter for each new mock in a test to avoid cross-test pollution
            executionCounter = 0;
        }

        @Override
        public void work() {
            workCalled = true;
            executionOrder = ++executionCounter;
            if (willWork) {
                worked = true;
            }
        }

        @Override
        public boolean isQuiet() {
            return quiet;
        }

        @Override
        public Duplicateable duplicate(Duplicator duplicator) {
            MockWorker copy = new MockWorker(getWorkerName(), quiet, willWork);
            duplicator.setImage(this, copy);
            return copy;
        }

        @Override
        public void completeDuplication(Duplicator duplicator) {
            // No-op for mock
        }

        boolean wasWorkCalled() {
            return workCalled;
        }

        int getExecutionOrder() {
            return executionOrder;
        }
    }
}
