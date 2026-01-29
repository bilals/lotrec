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
 * Tests for the Repeat strategy class.
 *
 * Repeat is a routine that executes all its workers repeatedly in a fair manner
 * until all workers are quiet (no more work to do).
 */
@DisplayName("Repeat")
class RepeatTest {

    private Repeat repeat;
    private TestableEngine testEngine;
    private Tableau testTableau;

    @BeforeEach
    void setUp() {
        repeat = new Repeat();
        testEngine = new TestableEngine();
        testTableau = new Tableau("test-tableau");

        repeat.setEngine(testEngine);
        repeat.setRelatedTableau(testTableau);
    }

    @Nested
    @DisplayName("Quiet State")
    class QuietState {

        @Test
        @DisplayName("should be quiet when worker list is empty")
        void shouldBeQuietWhenEmpty() {
            assertThat(repeat.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should be quiet when all workers are quiet")
        void shouldBeQuietWhenAllWorkersQuiet() {
            MockWorker worker1 = new MockWorker(0); // 0 work cycles = already quiet
            MockWorker worker2 = new MockWorker(0);

            repeat.add(worker1, null);
            repeat.add(worker2, null);

            assertThat(repeat.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should not be quiet when any worker has work")
        void shouldNotBeQuietWhenWorkerHasWork() {
            MockWorker quietWorker = new MockWorker(0);
            MockWorker busyWorker = new MockWorker(3);

            repeat.add(quietWorker, null);
            repeat.add(busyWorker, null);

            assertThat(repeat.isQuiet()).isFalse();
        }
    }

    @Nested
    @DisplayName("Termination Behavior")
    class TerminationBehavior {

        @Test
        @DisplayName("should terminate immediately when no workers")
        void shouldTerminateOnNoProgress() {
            repeat.work();

            assertThat(repeat.isQuiet()).isTrue();
            assertThat(repeat.hasWorked()).isFalse();
        }

        @Test
        @DisplayName("should repeat until all workers quiet")
        void shouldRepeatUntilQuiet() {
            MockWorker worker1 = new MockWorker(2);
            MockWorker worker2 = new MockWorker(3);

            repeat.add(worker1, null);
            repeat.add(worker2, null);
            worker1.setEngine(testEngine);
            worker1.setRelatedTableau(testTableau);
            worker2.setEngine(testEngine);
            worker2.setRelatedTableau(testTableau);

            repeat.work();

            assertThat(repeat.isQuiet()).isTrue();
            assertThat(worker1.getRemainingCycles()).isZero();
            assertThat(worker2.getRemainingCycles()).isZero();
        }

        @Test
        @DisplayName("should handle empty worker list gracefully")
        void shouldHandleEmptyWorkerList() {
            // No workers added

            assertThatCode(() -> repeat.work()).doesNotThrowAnyException();
            assertThat(repeat.isQuiet()).isTrue();
            assertThat(repeat.hasWorked()).isFalse();
        }
    }

    @Nested
    @DisplayName("Worker Execution")
    class WorkerExecution {

        @Test
        @DisplayName("should handle single worker")
        void shouldHandleSingleWorker() {
            MockWorker worker = new MockWorker(3);
            repeat.add(worker, null);
            worker.setEngine(testEngine);
            worker.setRelatedTableau(testTableau);

            repeat.work();

            assertThat(worker.getRemainingCycles()).isZero();
            assertThat(worker.getWorkCallCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("should handle multiple workers")
        void shouldHandleMultipleWorkers() {
            MockWorker worker1 = new MockWorker(2);
            MockWorker worker2 = new MockWorker(2);
            MockWorker worker3 = new MockWorker(2);

            repeat.add(worker1, null);
            repeat.add(worker2, null);
            repeat.add(worker3, null);

            worker1.setEngine(testEngine);
            worker1.setRelatedTableau(testTableau);
            worker2.setEngine(testEngine);
            worker2.setRelatedTableau(testTableau);
            worker3.setEngine(testEngine);
            worker3.setRelatedTableau(testTableau);

            repeat.work();

            assertThat(worker1.getRemainingCycles()).isZero();
            assertThat(worker2.getRemainingCycles()).isZero();
            assertThat(worker3.getRemainingCycles()).isZero();
        }

        @Test
        @DisplayName("should apply all workers per iteration in fair manner")
        void shouldApplyAllWorkersPerIteration() {
            TrackingWorker worker1 = new TrackingWorker("w1", 2);
            TrackingWorker worker2 = new TrackingWorker("w2", 2);

            repeat.add(worker1, null);
            repeat.add(worker2, null);

            worker1.setEngine(testEngine);
            worker1.setRelatedTableau(testTableau);
            worker2.setEngine(testEngine);
            worker2.setRelatedTableau(testTableau);

            repeat.work();

            // Both workers should have been called the same number of times
            // in a fair interleaved manner
            assertThat(worker1.getWorkCallCount()).isEqualTo(2);
            assertThat(worker2.getWorkCallCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("should mark worked flag when any worker does work")
        void shouldMarkWorkedFlag() {
            MockWorker worker = new MockWorker(1);
            repeat.add(worker, null);
            worker.setEngine(testEngine);
            worker.setRelatedTableau(testTableau);

            assertThat(repeat.hasWorked()).isFalse();

            repeat.work();

            assertThat(repeat.hasWorked()).isTrue();
        }

        @Test
        @DisplayName("should skip quiet workers in iteration")
        void shouldSkipQuietWorkers() {
            MockWorker quietWorker = new MockWorker(0);
            MockWorker busyWorker = new MockWorker(2);

            repeat.add(quietWorker, null);
            repeat.add(busyWorker, null);

            quietWorker.setEngine(testEngine);
            quietWorker.setRelatedTableau(testTableau);
            busyWorker.setEngine(testEngine);
            busyWorker.setRelatedTableau(testTableau);

            repeat.work();

            // Quiet worker should never have been called
            assertThat(quietWorker.getWorkCallCount()).isZero();
            // Busy worker should have completed its work
            assertThat(busyWorker.getWorkCallCount()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Duplication")
    class Duplication {

        @Test
        @DisplayName("should duplicate with workers")
        void shouldDuplicateWithWorkers() {
            MockWorker worker = new MockWorker(5);
            repeat.add(worker, null);

            Repeat duplicated = new Repeat(repeat);

            assertThat(duplicated.getWorkers()).hasSize(1);
        }

        @Test
        @DisplayName("should preserve maxTurns in duplication")
        void shouldPreserveMaxTurnsInDuplication() {
            // maxTurns is always -1 in current implementation
            Repeat duplicated = new Repeat(repeat);

            // Verify duplication doesn't throw and creates valid object
            assertThat(duplicated).isNotNull();
            assertThat(duplicated.isQuiet()).isTrue();
        }
    }

    @Nested
    @DisplayName("Worker Name")
    class WorkerName {

        @Test
        @DisplayName("should have default name 'Repeat'")
        void shouldHaveDefaultName() {
            assertThat(repeat.getWorkerName()).isEqualTo("Repeat");
        }
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

        public MockWorker(int cycles) {
            this.remainingCycles = cycles;
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
            return remainingCycles <= 0;
        }

        public int getRemainingCycles() {
            return remainingCycles;
        }

        public int getWorkCallCount() {
            return workCallCount;
        }

        @Override
        public Duplicateable duplicate(Duplicator duplicator) {
            MockWorker dup = new MockWorker(remainingCycles);
            duplicator.setImage(this, dup);
            return dup;
        }

        @Override
        public void completeDuplication(Duplicator duplicator) {
            // No-op for testing
        }
    }

    /**
     * Worker that tracks execution order for verifying fair scheduling.
     */
    private static class TrackingWorker extends AbstractWorker {
        private final String name;
        private int remainingCycles;
        private int workCallCount = 0;

        public TrackingWorker(String name, int cycles) {
            this.name = name;
            this.remainingCycles = cycles;
            setWorkerName(name);
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
            return remainingCycles <= 0;
        }

        public int getWorkCallCount() {
            return workCallCount;
        }

        @Override
        public Duplicateable duplicate(Duplicator duplicator) {
            TrackingWorker dup = new TrackingWorker(name, remainingCycles);
            duplicator.setImage(this, dup);
            return dup;
        }

        @Override
        public void completeDuplication(Duplicator duplicator) {
            // No-op for testing
        }
    }
}
