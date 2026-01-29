package lotrec.process;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.engine.Engine;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.CommonDuplicator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Vector;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for Strategy data structure.
 * Strategy extends AllRules and provides named strategy definitions
 * with code and comment properties.
 */
@DisplayName("Strategy")
class StrategyTest {

    private Strategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new Strategy();
    }

    @Nested
    @DisplayName("Code Property")
    class CodeProperty {

        @Test
        @DisplayName("should set and get code")
        void shouldSetAndGetCode() {
            // Arrange
            String code = "repeat firstRule Rule1 Rule2 end end";

            // Act
            strategy.setCode(code);

            // Assert
            assertThat(strategy.getCode()).isEqualTo(code);
        }

        @Test
        @DisplayName("should parse simple code")
        void shouldParseSimpleCode() {
            // Arrange
            String simpleCode = "allRules";

            // Act
            strategy.setCode(simpleCode);

            // Assert
            assertThat(strategy.getCode()).isEqualTo(simpleCode);
            assertThat(strategy.getCode()).doesNotContain("repeat");
            assertThat(strategy.getCode()).doesNotContain("end");
        }

        @Test
        @DisplayName("should parse complex nested code")
        void shouldParseComplexCode() {
            // Arrange - complex nested strategy code
            String complexCode = "repeat firstRule RuleA RuleB end allRules RuleC RuleD end end";

            // Act
            strategy.setCode(complexCode);

            // Assert
            assertThat(strategy.getCode()).isEqualTo(complexCode);
            assertThat(strategy.getCode()).contains("repeat");
            assertThat(strategy.getCode()).contains("firstRule");
            assertThat(strategy.getCode()).contains("allRules");
            assertThat(strategy.getCode()).contains("end");
        }

        @Test
        @DisplayName("should handle empty code")
        void shouldHandleEmptyCode() {
            // Arrange
            String emptyCode = "";

            // Act
            strategy.setCode(emptyCode);

            // Assert
            assertThat(strategy.getCode()).isEmpty();
        }

        @Test
        @DisplayName("should handle null code")
        void shouldHandleNullCode() {
            // Act
            strategy.setCode(null);

            // Assert
            assertThat(strategy.getCode()).isNull();
        }
    }

    @Nested
    @DisplayName("Worker Name Property")
    class WorkerNameProperty {

        @Test
        @DisplayName("should set and get worker name")
        void shouldSetAndGetWorkerName() {
            // Arrange
            String name = "MainStrategy";

            // Act
            strategy.setWorkerName(name);

            // Assert
            assertThat(strategy.getWorkerName()).isEqualTo(name);
        }

        @Test
        @DisplayName("should inherit AllRules default worker name initially")
        void shouldHaveAllRulesDefaultName() {
            // Assert - Strategy inherits from AllRules which sets default name
            assertThat(strategy.getWorkerName()).isEqualTo("AllRules");
        }

        @Test
        @DisplayName("should override inherited worker name")
        void shouldOverrideInheritedWorkerName() {
            // Arrange
            String customName = "CustomStrategy";

            // Act
            strategy.setWorkerName(customName);

            // Assert
            assertThat(strategy.getWorkerName()).isEqualTo(customName);
            assertThat(strategy.getWorkerName()).isNotEqualTo("AllRules");
        }
    }

    @Nested
    @DisplayName("Name Property (via workerName)")
    class NameProperty {

        @Test
        @DisplayName("should set and get name via setWorkerName/getWorkerName")
        void shouldSetAndGetName() {
            // Arrange
            String name = "TestStrategy";

            // Act
            strategy.setWorkerName(name);

            // Assert
            assertThat(strategy.getWorkerName()).isEqualTo(name);
            assertThat(strategy.toString()).contains(name);
        }

        @Test
        @DisplayName("should use name in toString representation")
        void shouldUseNameInToString() {
            // Arrange
            String name = "NamedStrategy";
            strategy.setWorkerName(name);

            // Act
            String representation = strategy.toString();

            // Assert
            assertThat(representation).contains(name);
        }
    }

    @Nested
    @DisplayName("Comment Property")
    class CommentProperty {

        @Test
        @DisplayName("should set and get comment")
        void shouldSetAndGetComment() {
            // Arrange
            String comment = "This strategy applies modal rules exhaustively";

            // Act
            strategy.setComment(comment);

            // Assert
            assertThat(strategy.getComment()).isEqualTo(comment);
        }

        @Test
        @DisplayName("should handle empty comment")
        void shouldHandleEmptyComment() {
            // Act
            strategy.setComment("");

            // Assert
            assertThat(strategy.getComment()).isEmpty();
        }

        @Test
        @DisplayName("should handle null comment")
        void shouldHandleNullComment() {
            // Act
            strategy.setComment(null);

            // Assert
            assertThat(strategy.getComment()).isNull();
        }
    }

    @Nested
    @DisplayName("Duplication")
    class DuplicationTests {

        @Test
        @DisplayName("should duplicate strategy with same code")
        void shouldDuplicateStrategy() {
            // Arrange
            strategy.setWorkerName("OriginalStrategy");
            strategy.setCode("repeat firstRule Rule1 end end");
            strategy.setComment("Test comment");

            // Act
            Duplicator duplicator = new TestDuplicator();
            Strategy duplicated = (Strategy) strategy.duplicate(duplicator);

            // Assert
            assertThat(duplicated).isNotNull();
            assertThat(duplicated).isNotSameAs(strategy);
            assertThat(duplicated.getCode()).isEqualTo(strategy.getCode());
            assertThat(duplicated.getComment()).isEqualTo(strategy.getComment());
        }

        @Test
        @DisplayName("should preserve worker name during duplication")
        void shouldPreserveWorkerNameDuringDuplication() {
            // Arrange
            strategy.setWorkerName("NamedStrategy");

            // Act
            Duplicator duplicator = new TestDuplicator();
            Strategy duplicated = (Strategy) strategy.duplicate(duplicator);

            // Assert
            assertThat(duplicated.getWorkerName()).isEqualTo("NamedStrategy");
        }

        @Test
        @DisplayName("should clone strategy via copy constructor")
        void shouldCloneStrategy() {
            // Arrange
            strategy.setWorkerName("OriginalStrategy");
            strategy.setCode("allRules Rule1 Rule2 end");
            strategy.setComment("A comment");

            // Act - use copy constructor directly
            Strategy cloned = new Strategy(strategy);

            // Assert
            assertThat(cloned).isNotSameAs(strategy);
            assertThat(cloned.getCode()).isEqualTo(strategy.getCode());
            assertThat(cloned.getComment()).isEqualTo(strategy.getComment());
            assertThat(cloned.getWorkerName()).isEqualTo(strategy.getWorkerName());
        }

        @Test
        @DisplayName("should register duplicate in duplicator")
        void shouldRegisterDuplicateInDuplicator() {
            // Arrange
            strategy.setWorkerName("TestStrategy");
            CommonDuplicator duplicator = new CommonDuplicator();

            // Act
            Strategy duplicated = (Strategy) strategy.duplicate(duplicator);

            // Assert
            assertThat(duplicator.hasImage(strategy)).isTrue();
            assertThatCode(() -> duplicator.getImage(strategy)).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Worker Management (inherited from Routine)")
    class WorkerManagement {

        @Test
        @DisplayName("should add workers to strategy")
        void shouldAddWorkersToStrategy() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1");
            MockWorker worker2 = new MockWorker("Rule2");

            // Act
            strategy.add(worker1, null);
            strategy.add(worker2, null);

            // Assert
            Vector<AbstractWorker> workers = strategy.getWorkers();
            assertThat(workers).hasSize(2);
            assertThat(workers.get(0).getWorkerName()).isEqualTo("Rule1");
            assertThat(workers.get(1).getWorkerName()).isEqualTo("Rule2");
        }

        @Test
        @DisplayName("should check if calling rule by name")
        void shouldCheckIfCallingRule() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1");
            MockWorker worker2 = new MockWorker("Rule2");
            strategy.add(worker1, null);
            strategy.add(worker2, null);

            // Assert
            assertThat(strategy.isCallingRule("Rule1")).isTrue();
            assertThat(strategy.isCallingRule("Rule2")).isTrue();
            assertThat(strategy.isCallingRule("Rule3")).isFalse();
        }

        @Test
        @DisplayName("should check if calling another strategy")
        void shouldCheckIfCallingStrategy() {
            // Arrange
            Strategy innerStrategy = new Strategy();
            innerStrategy.setWorkerName("InnerStrategy");
            strategy.add(innerStrategy, null);

            // Assert
            assertThat(strategy.isCallingStrategy(innerStrategy)).isTrue();
        }

        @Test
        @DisplayName("should remove workers from strategy")
        void shouldRemoveWorkersFromStrategy() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1");
            MockWorker worker2 = new MockWorker("Rule2");
            strategy.add(worker1, null);
            strategy.add(worker2, null);

            // Act
            strategy.remove(worker1);

            // Assert
            Vector<AbstractWorker> workers = strategy.getWorkers();
            assertThat(workers).hasSize(1);
            assertThat(workers.get(0).getWorkerName()).isEqualTo("Rule2");
        }
    }

    @Nested
    @DisplayName("isQuiet Behavior")
    class IsQuietBehavior {

        @Test
        @DisplayName("should be quiet when empty")
        void shouldBeQuietWhenEmpty() {
            // Assert
            assertThat(strategy.isQuiet()).isTrue();
        }

        @Test
        @DisplayName("should not be quiet when any worker is not quiet")
        void shouldNotBeQuietWhenAnyWorkerNotQuiet() {
            // Arrange
            MockWorker quietWorker = new MockWorker("Quiet", true);
            MockWorker activeWorker = new MockWorker("Active", false);

            strategy.add(quietWorker, null);
            strategy.add(activeWorker, null);

            // Assert
            assertThat(strategy.isQuiet()).isFalse();
        }

        @Test
        @DisplayName("should be quiet when all workers are quiet")
        void shouldBeQuietWhenAllWorkersQuiet() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", true);
            MockWorker worker2 = new MockWorker("Rule2", true);

            strategy.add(worker1, null);
            strategy.add(worker2, null);

            // Assert
            assertThat(strategy.isQuiet()).isTrue();
        }
    }

    @Nested
    @DisplayName("Strategy Execution (inherited from AllRules)")
    class StrategyExecution {

        private Engine mockEngine;
        private Tableau tableau;

        @BeforeEach
        void setUpExecution() {
            mockEngine = new TestEngine();
            tableau = new Tableau("TestTableau");
            strategy.setEngine(mockEngine);
            strategy.setRelatedTableau(tableau);
        }

        @Test
        @DisplayName("should execute all workers")
        void shouldExecuteAllWorkers() {
            // Arrange
            MockWorker worker1 = new MockWorker("Rule1", false, true);
            MockWorker worker2 = new MockWorker("Rule2", false, true);
            MockWorker worker3 = new MockWorker("Rule3", false, true);

            strategy.add(worker1, null);
            strategy.add(worker2, null);
            strategy.add(worker3, null);

            // Act
            strategy.work();

            // Assert - AllRules executes all workers
            assertThat(worker1.wasWorkCalled()).isTrue();
            assertThat(worker2.wasWorkCalled()).isTrue();
            assertThat(worker3.wasWorkCalled()).isTrue();
            assertThat(strategy.hasWorked()).isTrue();
        }

        @Test
        @DisplayName("should handle empty workers list")
        void shouldHandleEmptyWorkersList() {
            // Act
            strategy.work();

            // Assert
            assertThat(strategy.hasWorked()).isFalse();
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
     * Mock worker for testing Strategy behavior.
     */
    private static class MockWorker extends AbstractWorker {
        private final boolean quiet;
        private final boolean willWork;
        private boolean workCalled = false;

        MockWorker(String name) {
            this(name, false, true);
        }

        MockWorker(String name, boolean quiet) {
            this(name, quiet, true);
        }

        MockWorker(String name, boolean quiet, boolean willWork) {
            this.quiet = quiet;
            this.willWork = willWork;
            setWorkerName(name);
        }

        @Override
        public void work() {
            workCalled = true;
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
    }
}
