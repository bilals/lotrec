package lotrec.process;

import lotrec.dataStructure.tableau.Tableau;
import lotrec.engine.Engine;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the AllRules strategy class.
 *
 * AllRules is a routine that executes all its workers in sequence,
 * applying every matching rule (unlike FirstRule which stops after the first match).
 * All non-quiet workers are executed in order during a single work() call.
 */
@DisplayName("AllRules")
class AllRulesTest {

    private AllRules allRules;
    private TestableEngine testEngine;
    private Tableau testTableau;

    @BeforeEach
    void setUp() {
        allRules = new AllRules();
        testEngine = new TestableEngine();
        testTableau = new Tableau("test-tableau");

        allRules.setEngine(testEngine);
        allRules.setRelatedTableau(testTableau);
    }

    @Nested
    @DisplayName("Apply All Matching Rules")
    class ApplyAllMatchingRules {

        @Test
        @DisplayName("should apply all matching rules when multiple workers can work")
        void shouldApplyAllMatchingRules() {
            MockWorker worker1 = createWorker(1, true);
            MockWorker worker2 = createWorker(1, true);
            MockWorker worker3 = createWorker(1, true);

            allRules.add(worker1, null);
            allRules.add(worker2, null);
            allRules.add(worker3, null);

            allRules.work();

            // All workers should have been called
            assertThat(worker1.getWorkCallCount()).isEqualTo(1);
            assertThat(worker2.getWorkCallCount()).isEqualTo(1);
            assertThat(worker3.getWorkCallCount()).isEqualTo(1);
            assertThat(allRules.hasWorked()).isTrue();
        }

        @Test
        @DisplayName("should apply multiple matching rules in single work call")
        void shouldApplyMultipleMatchingRules() {
            // Two workers that can work, one that is quiet
            MockWorker workingWorker1 = createWorker(1, true);
            MockWorker workingWorker2 = createWorker(1, true);
            MockWorker quietWorker = createWorker(0, false);

            allRules.add(workingWorker1, null);
            allRules.add(quietWorker, null);
            allRules.add(workingWorker2, null);

            allRules.work();

            // Both working workers should have been called
            assertThat(workingWorker1.getWorkCallCount()).isEqualTo(1);
            assertThat(workingWorker2.getWorkCallCount()).isEqualTo(1);
            // Quiet worker should be skipped
            assertThat(quietWorker.getWorkCallCount()).isZero();
        }
    }

    @Nested
    @DisplayName("No Matching Rules")
    class NoMatchingRules {

        @Test
        @DisplayName("should handle no matching rules gracefully")
        void shouldHandleNoMatchingRules() {
            MockWorker quietWorker1 = createWorker(0, false);
            MockWorker quietWorker2 = createWorker(0, false);

            allRules.add(quietWorker1, null);
            allRules.add(quietWorker2, null);

            assertThatCode(() -> allRules.work()).doesNotThrowAnyException();

            // No workers should have been called (all are quiet)
            assertThat(quietWorker1.getWorkCallCount()).isZero();
            assertThat(quietWorker2.getWorkCallCount()).isZero();
            assertThat(allRules.hasWorked()).isFalse();
        }
    }

    @Nested
    @DisplayName("Order Preservation")
    class OrderPreservation {

        @Test
        @DisplayName("should apply rules in order they were added")
        void shouldApplyRulesInOrder() {
            List<String> executionOrder = new ArrayList<>();

            OrderTrackingWorker worker1 = new OrderTrackingWorker("first", executionOrder);
            OrderTrackingWorker worker2 = new OrderTrackingWorker("second", executionOrder);
            OrderTrackingWorker worker3 = new OrderTrackingWorker("third", executionOrder);

            worker1.setEngine(testEngine);
            worker1.setRelatedTableau(testTableau);
            worker2.setEngine(testEngine);
            worker2.setRelatedTableau(testTableau);
            worker3.setEngine(testEngine);
            worker3.setRelatedTableau(testTableau);

            allRules.add(worker1, null);
            allRules.add(worker2, null);
            allRules.add(worker3, null);

            allRules.work();

            assertThat(executionOrder).containsExactly("first", "second", "third");
        }
    }

    @Nested
    @DisplayName("Empty Rule List")
    class EmptyRuleList {

        @Test
        @DisplayName("should handle empty rule list gracefully")
        void shouldHandleEmptyRuleList() {
            // No workers added

            assertThatCode(() -> allRules.work()).doesNotThrowAnyException();
            assertThat(allRules.isQuiet()).isTrue();
            assertThat(allRules.hasWorked()).isFalse();
        }

        @Test
        @DisplayName("should return immediately when empty")
        void shouldReturnImmediatelyWhenEmpty() {
            allRules.work();

            assertThat(allRules.hasWorked()).isFalse();
        }
    }

    @Nested
    @DisplayName("Single Rule")
    class SingleRule {

        @Test
        @DisplayName("should handle single rule correctly")
        void shouldHandleSingleRule() {
            MockWorker worker = createWorker(1, true);

            allRules.add(worker, null);

            allRules.work();

            assertThat(worker.getWorkCallCount()).isEqualTo(1);
            assertThat(allRules.hasWorked()).isTrue();
        }

        @Test
        @DisplayName("should handle single quiet rule correctly")
        void shouldHandleSingleQuietRule() {
            MockWorker worker = createWorker(0, false);

            allRules.add(worker, null);

            allRules.work();

            assertThat(worker.getWorkCallCount()).isZero();
            assertThat(allRules.hasWorked()).isFalse();
        }
    }

    @Nested
    @DisplayName("Quiet State")
    class QuietState {

        @Test
        @DisplayName("should be quiet when worker list is empty")
        void shouldBeQuietWhenEmpty() {
            assertThat(allRules.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should be quiet when all workers are quiet")
        void shouldBeQuietWhenAllWorkersQuiet() {
            MockWorker worker1 = createWorker(0, false);
            MockWorker worker2 = createWorker(0, false);

            allRules.add(worker1, null);
            allRules.add(worker2, null);

            assertThat(allRules.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should not be quiet when any worker has work")
        void shouldNotBeQuietWhenWorkerHasWork() {
            MockWorker quietWorker = createWorker(0, false);
            MockWorker busyWorker = createWorker(1, true);

            allRules.add(quietWorker, null);
            allRules.add(busyWorker, null);

            assertThat(allRules.isQuiet()).isFalse();
        }
    }

    @Nested
    @DisplayName("Worker Name")
    class WorkerName {

        @Test
        @DisplayName("should have default name 'AllRules'")
        void shouldHaveDefaultName() {
            assertThat(allRules.getWorkerName()).isEqualTo("AllRules");
        }
    }

    @Nested
    @DisplayName("Engine Stop Signal")
    class EngineStopSignal {

        @Test
        @DisplayName("should respect engine stop signal and stop early")
        void shouldRespectEngineStopSignal() {
            // First worker works but triggers stop
            MockWorker worker1 = new MockWorker(1, true) {
                @Override
                public void work() {
                    super.work();
                    testEngine.setShouldStop(true);
                }
            };
            worker1.setEngine(testEngine);
            worker1.setRelatedTableau(testTableau);

            MockWorker worker2 = createWorker(1, true);

            allRules.add(worker1, null);
            allRules.add(worker2, null);

            allRules.work();

            // First worker should have worked
            assertThat(worker1.getWorkCallCount()).isEqualTo(1);
            // Second worker should NOT have been reached due to stop signal
            assertThat(worker2.getWorkCallCount()).isZero();
        }
    }

    @Nested
    @DisplayName("Duplication")
    class Duplication {

        @Test
        @DisplayName("should duplicate with workers")
        void shouldDuplicateWithWorkers() {
            MockWorker worker = createWorker(5, true);
            allRules.add(worker, null);

            AllRules duplicated = new AllRules(allRules);

            assertThat(duplicated.getWorkers()).hasSize(1);
        }

        @Test
        @DisplayName("should create valid duplicate")
        void shouldCreateValidDuplicate() {
            AllRules duplicated = new AllRules(allRules);

            assertThat(duplicated).isNotNull();
            assertThat(duplicated.isQuiet()).isTrue();
            assertThat(duplicated.getWorkerName()).isEqualTo("AllRules");
        }
    }

    // ==================== Helper Methods ====================

    private MockWorker createWorker(int cycles, boolean canWork) {
        MockWorker worker = new MockWorker(cycles, canWork);
        worker.setEngine(testEngine);
        worker.setRelatedTableau(testTableau);
        return worker;
    }

    // ==================== Test Doubles ====================

    /**
     * Minimal Engine implementation for testing that doesn't require GUI.
     */
    private static class TestableEngine extends Engine {
        private boolean shouldStop = false;

        public TestableEngine() {
            super();
        }

        @Override
        public synchronized boolean shouldStop() {
            return shouldStop;
        }

        public void setShouldStop(boolean shouldStop) {
            this.shouldStop = shouldStop;
        }
    }

    /**
     * Mock worker for testing that simulates work cycles.
     * Becomes quiet after the specified number of work() calls.
     */
    private static class MockWorker extends AbstractWorker {
        private int remainingCycles;
        private int workCallCount = 0;
        private final boolean canWorkInitially;

        public MockWorker(int cycles, boolean canWork) {
            this.remainingCycles = cycles;
            this.canWorkInitially = canWork;
        }

        @Override
        public void work() {
            if (remainingCycles > 0) {
                remainingCycles--;
                workCallCount++;
                worked = true;
            } else {
                worked = false;
            }
        }

        @Override
        public boolean isQuiet() {
            return !canWorkInitially || remainingCycles <= 0;
        }

        public int getRemainingCycles() {
            return remainingCycles;
        }

        public int getWorkCallCount() {
            return workCallCount;
        }

        @Override
        public Duplicateable duplicate(Duplicator duplicator) {
            MockWorker dup = new MockWorker(remainingCycles, canWorkInitially);
            duplicator.setImage(this, dup);
            return dup;
        }

        @Override
        public void completeDuplication(Duplicator duplicator) {
            // No-op for testing
        }
    }

    /**
     * Worker that tracks execution order for verifying rule application order.
     */
    private static class OrderTrackingWorker extends AbstractWorker {
        private final String name;
        private final List<String> executionLog;
        private boolean hasExecuted = false;

        public OrderTrackingWorker(String name, List<String> executionLog) {
            this.name = name;
            this.executionLog = executionLog;
            setWorkerName(name);
        }

        @Override
        public void work() {
            executionLog.add(name);
            hasExecuted = true;
            worked = true;
        }

        @Override
        public boolean isQuiet() {
            return hasExecuted;
        }

        @Override
        public Duplicateable duplicate(Duplicator duplicator) {
            OrderTrackingWorker dup = new OrderTrackingWorker(name, executionLog);
            duplicator.setImage(this, dup);
            return dup;
        }

        @Override
        public void completeDuplication(Duplicator duplicator) {
            // No-op for testing
        }
    }
}
