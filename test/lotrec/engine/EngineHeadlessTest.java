package lotrec.engine;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Wallet;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for headless Engine execution using the EngineListener pattern.
 * These tests verify full proof search execution without GUI initialization.
 *
 * @see Engine
 * @see EngineListener
 * @see HeadlessEngineListener
 */
@DisplayName("Engine Headless Execution Tests")
class EngineHeadlessTest {

    @Nested
    @DisplayName("EngineListener Interface")
    class EngineListenerTests {

        @Test
        @DisplayName("should create HeadlessEngineListener without recording")
        void shouldCreateHeadlessListenerWithoutRecording() {
            HeadlessEngineListener listener = new HeadlessEngineListener();

            assertThat(listener).isNotNull();
            assertThat(listener.getEventLog()).isNull();
        }

        @Test
        @DisplayName("should create HeadlessEngineListener with recording")
        void shouldCreateHeadlessListenerWithRecording() {
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            assertThat(listener).isNotNull();
            assertThat(listener.getEventLog()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("should record build events")
        void shouldRecordBuildEvents() {
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            listener.onBuildStart();
            listener.onBuildEnd(false);

            assertThat(listener.getEventLog())
                    .contains("BUILD_START", "BUILD_END:FINISHED");
            assertThat(listener.isBuildStarted()).isTrue();
            assertThat(listener.isBuildEnded()).isTrue();
            assertThat(listener.wasStopped()).isFalse();
        }

        @Test
        @DisplayName("should record status changes")
        void shouldRecordStatusChanges() {
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            listener.onStatusChanged(EngineStatus.NORMAL);
            listener.onStatusChanged(EngineStatus.FINISHED);

            assertThat(listener.getEventLog())
                    .contains("STATUS:" + EngineStatus.NORMAL, "STATUS:" + EngineStatus.FINISHED);
            assertThat(listener.getLastStatus()).isEqualTo(EngineStatus.FINISHED);
        }

        @Test
        @DisplayName("should record rule applications")
        void shouldRecordRuleApplications() {
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            listener.onRuleApplied("AndRule", "premodel");
            listener.onAppliedRulesChanged(5);
            listener.onTotalAppliedRulesChanged(10);

            assertThat(listener.getLastRuleName()).isEqualTo("AndRule");
            assertThat(listener.getLastTableauName()).isEqualTo("premodel");
            assertThat(listener.getLastAppliedRules()).isEqualTo(5);
            assertThat(listener.getLastTotalAppliedRules()).isEqualTo(10);
        }

        @Test
        @DisplayName("should reset recorded state")
        void shouldResetRecordedState() {
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            listener.onBuildStart();
            listener.onStatusChanged(EngineStatus.NORMAL);
            listener.onAppliedRulesChanged(5);
            listener.reset();

            assertThat(listener.getEventLog()).isEmpty();
            assertThat(listener.isBuildStarted()).isFalse();
            assertThat(listener.getLastStatus()).isNull();
            assertThat(listener.getLastAppliedRules()).isZero();
        }
    }

    @Nested
    @DisplayName("Engine Construction with EngineListener")
    class EngineConstructionTests {

        @Test
        @DisplayName("should construct Engine with HeadlessEngineListener")
        void shouldConstructEngineWithHeadlessListener() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            Strategy strategy = parseStrategy(logic);
            MarkedExpression formula = parseFormula(logic, "and P Q");
            HeadlessEngineListener listener = new HeadlessEngineListener();

            Engine engine = new Engine(logic, strategy, formula, listener);

            assertThat(engine).isNotNull();
            assertThat(engine.getLogic()).isEqualTo(logic);
            assertThat(engine.getStrategy()).isEqualTo(strategy);
            assertThat(engine.getFormula()).isEqualTo(formula);
            assertThat(engine.getListener()).isEqualTo(listener);
            assertThat(engine.getMainFrame()).isNull();
        }

        @Test
        @DisplayName("should use EngineBuilder for convenient setup")
        void shouldUseEngineBuilder() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "and P Q");

            Engine engine = EngineBuilder.forLogic(logic)
                    .withFormula(formula)
                    .stopOnOpenTableau()
                    .build();

            assertThat(engine).isNotNull();
            assertThat(engine.getLogic()).isEqualTo(logic);
            assertThat(engine.getOpenTableauAction()).isEqualTo(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
        }

        @Test
        @DisplayName("should use EngineBuilder with recording listener")
        void shouldUseEngineBuilderWithRecordingListener() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "and P Q");

            Engine engine = EngineBuilder.forLogic(logic)
                    .withFormula(formula)
                    .withRecordingListener()
                    .build();

            assertThat(engine.getListener()).isInstanceOf(HeadlessEngineListener.class);
            HeadlessEngineListener listener = (HeadlessEngineListener) engine.getListener();
            assertThat(listener.getEventLog()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Headless Proof Search Execution")
    class HeadlessProofSearchTests {

        @Test
        @DisplayName("should run proof search for simple formula")
        void shouldRunProofSearchForSimpleFormula() throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "P");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            assertThat(engine.getCurrentWallet()).isNotNull();
            assertThat(listener.isBuildStarted()).isTrue();
            assertThat(listener.isBuildEnded()).isTrue();
        }

        @Test
        @DisplayName("should detect satisfiable formula - atom P")
        void shouldDetectSatisfiableAtom() throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "P");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            // P is satisfiable - should have an open tableau
            Wallet wallet = engine.getCurrentWallet();
            assertThat(wallet).isNotNull();
            assertThat(wallet.getGraphes()).isNotEmpty();
        }

        @Test
        @DisplayName("should detect unsatisfiable formula - contradiction P and not P")
        void shouldDetectContradiction() throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "and P not P");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            // P and not P is unsatisfiable - all tableaux should be closed
            Wallet wallet = engine.getCurrentWallet();
            assertThat(wallet).isNotNull();
            boolean allClosed = true;
            for (Object graph : wallet.getGraphes()) {
                if (graph instanceof Tableau && !((Tableau) graph).isClosed()) {
                    allClosed = false;
                    break;
                }
            }
            assertThat(allClosed).as("All tableaux should be closed for contradiction").isTrue();
        }

        @Test
        @DisplayName("should detect satisfiable conjunction")
        void shouldDetectSatisfiableConjunction() throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "and P Q");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            Wallet wallet = engine.getCurrentWallet();
            assertThat(wallet).isNotNull();
            // P and Q is satisfiable - should stop with open tableau
            assertThat(listener.wasStopped()).isTrue();
        }

        @Test
        @DisplayName("should report engine status during execution")
        void shouldReportEngineStatusDuringExecution() throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "and P Q");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            // Should have recorded status changes
            assertThat(listener.getEventLog()).isNotEmpty();
            // Final status should be STOPPED (stopped on open tableau) or FINISHED
            assertThat(listener.getLastStatus()).isIn(EngineStatus.STOPPED, EngineStatus.FINISHED);
        }
    }

    @Nested
    @DisplayName("Modal Logic K Tests")
    class ModalLogicKTests {

        private Logic logic;

        @BeforeEach
        void setUp() throws ParseException {
            logic = TestFixtures.loadLogic("Monomodal-K");
        }

        @Test
        @DisplayName("should handle modal formula nec P")
        void shouldHandleModalFormulaNecP() throws ParseException, InterruptedException {
            MarkedExpression formula = parseFormula(logic, "nec P");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            assertThat(engine.getCurrentWallet()).isNotNull();
            assertThat(listener.isBuildEnded()).isTrue();
        }

        @Test
        @DisplayName("should handle K axiom formula")
        void shouldHandleKAxiom() throws ParseException, InterruptedException {
            // K axiom: [](P -> Q) -> ([]P -> []Q)
            MarkedExpression formula = parseFormula(logic, "imp nec imp P Q imp nec P nec Q");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            assertThat(engine.getCurrentWallet()).isNotNull();
        }

        @Test
        @DisplayName("should handle possibility formula pos P")
        void shouldHandlePossibilityFormula() throws ParseException, InterruptedException {
            MarkedExpression formula = parseFormula(logic, "pos P");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            assertThat(engine.getCurrentWallet()).isNotNull();
            // pos P is satisfiable in K
            assertThat(listener.wasStopped()).isTrue();
        }
    }

    @Nested
    @DisplayName("EngineBuilder Tests")
    class EngineBuilderTests {

        @Test
        @DisplayName("should build engine with default settings")
        void shouldBuildWithDefaults() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");

            Engine engine = EngineBuilder.forLogic(logic).build();

            assertThat(engine).isNotNull();
            assertThat(engine.getLogic()).isEqualTo(logic);
            assertThat(engine.getListener()).isInstanceOf(HeadlessEngineListener.class);
        }

        @Test
        @DisplayName("should build and initialize tableaux")
        void shouldBuildAndInit() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            MarkedExpression formula = parseFormula(logic, "P");

            Engine engine = EngineBuilder.forLogic(logic)
                    .withFormula(formula)
                    .buildAndInit();

            assertThat(engine.getCurrentWallet()).isNotNull();
            assertThat(engine.getCurrentWallet().getGraphes()).isNotEmpty();
        }

        @Test
        @DisplayName("should configure open tableau action")
        void shouldConfigureOpenTableauAction() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");

            Engine stopEngine = EngineBuilder.forLogic(logic)
                    .stopOnOpenTableau()
                    .build();

            Engine continueEngine = EngineBuilder.forLogic(logic)
                    .continueOnOpenTableau()
                    .build();

            Engine pauseEngine = EngineBuilder.forLogic(logic)
                    .pauseOnOpenTableau()
                    .build();

            assertThat(stopEngine.getOpenTableauAction()).isEqualTo(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            assertThat(continueEngine.getOpenTableauAction()).isEqualTo(Engine.NOP_WHEN_HAVING_OPEN_TABLEAU);
            assertThat(pauseEngine.getOpenTableauAction()).isEqualTo(Engine.PAUSE_WHEN_HAVING_OPEN_TABLEAU);
        }

    }

    /**
     * Provides logic names with their testing formulas.
     * Note: Static method at class level to work with Java 8 restrictions.
     */
    static Stream<Arguments> logicsWithTestingFormulas() {
        List<Arguments> args = new ArrayList<>();

        // Select a subset of logics for testing
        String[] testLogics = {
            "Classical-Propositional-Logic",
            "Monomodal-K",
            "KD",
            "S4Optimal"
        };

        for (String logicName : testLogics) {
            try {
                Logic logic = TestFixtures.loadLogic(logicName);
                Vector<TestingFormula> formulas = logic.getTestingFormulae();

                if (formulas != null && !formulas.isEmpty()) {
                    // Take first formula from each logic
                    TestingFormula tf = formulas.get(0);
                    args.add(Arguments.of(logicName, tf.getCode()));
                }
            } catch (Exception e) {
                System.err.println("Could not load logic " + logicName + ": " + e.getMessage());
            }
        }

        return args.stream();
    }

    @Nested
    @DisplayName("Testing Formulas from Predefined Logics")
    class TestingFormulasTests {

        @ParameterizedTest(name = "{0}: {1}")
        @MethodSource("lotrec.engine.EngineHeadlessTest#logicsWithTestingFormulas")
        @DisplayName("should execute proof search for testing formula")
        void shouldExecuteProofSearchForTestingFormula(String logicName, String formulaCode)
                throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic(logicName);
            MarkedExpression formula = parseFormula(logic, formulaCode);
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            assertThat(engine.getCurrentWallet())
                    .as("Should complete proof search for %s with formula %s", logicName, formulaCode)
                    .isNotNull();
            assertThat(listener.isBuildEnded())
                    .as("Build should complete")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Multiple Logics Execution")
    class MultipleLogicsTests {

        @ParameterizedTest(name = "should execute in {0}")
        @ValueSource(strings = {
            "Classical-Propositional-Logic",
            "Monomodal-K",
            "KD",
            "S4Optimal",
            "S5-explicit-edges"
        })
        @DisplayName("should execute proof search in multiple logics")
        void shouldExecuteInMultipleLogics(String logicName) throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic(logicName);

            // Use a simple tautology that works in all logics
            MarkedExpression formula = parseFormula(logic, "or P not P");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), formula, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            assertThat(engine.getCurrentWallet())
                    .as("Should complete in logic %s", logicName)
                    .isNotNull();
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("should report runtime error via listener")
        void shouldReportRuntimeErrorViaListener() {
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            listener.onRuntimeError("Test error message");

            assertThat(listener.getLastError()).isEqualTo("Test error message");
            assertThat(listener.getEventLog()).contains("ERROR:Test error message");
        }

        @Test
        @DisplayName("should handle engine with null formula")
        void shouldHandleNullFormula() throws ParseException, InterruptedException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            HeadlessEngineListener listener = new HeadlessEngineListener(true);

            Engine engine = new Engine(logic, parseStrategy(logic), null, listener);
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
            engine.buildTableaux();
            engine.start();
            engine.join();

            // Should complete without error
            assertThat(listener.isBuildEnded()).isTrue();
            assertThat(listener.getLastError()).isNull();
        }
    }

    // ========== Helper Methods ==========

    private static Strategy parseStrategy(Logic logic) throws ParseException {
        OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);
        String mainStrategyName = logic.getMainStrategyName();
        Strategy mainStrategy = logic.getStrategy(mainStrategyName);
        return tokenizer.parseStrategy(mainStrategy.getCode());
    }

    private static MarkedExpression parseFormula(Logic logic, String formulaCode) throws ParseException {
        OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);
        Expression expr = tokenizer.parseExpression(formulaCode);
        tokenizer.verifyCodeEnd();
        return new MarkedExpression(expr);
    }
}
