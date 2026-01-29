package lotrec.logics;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.ExpressionWithSubExpressions;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for formula parsing and satisfiability testing setup.
 *
 * These tests focus on formula parsing and logic loading without running the
 * full tableau engine. Full engine execution requires GUI/Cytoscape initialization
 * which is not suitable for unit tests.
 *
 * <p><b>Note on Engine execution:</b> Tests that require full tableau satisfiability
 * checking would need to initialize the GUI framework (Cytoscape, Swing MainFrame).
 * Such tests should be marked as integration tests and run separately.</p>
 *
 * @see lotrec.engine.Engine
 * @see lotrec.Launcher#benchmark(String, String, boolean, boolean)
 */
@DisplayName("Satisfiability Tests")
class SatisfiabilityTest {

    /**
     * Provides all predefined logic names with their testing formulas for
     * parameterized testing. This method is at the class level to avoid
     * Java 8 restrictions on static methods in inner classes.
     */
    static Stream<Arguments> allLogicsWithTestingFormulas() {
        List<Arguments> args = new ArrayList<>();

        for (String logicName : TestFixtures.ALL_LOGIC_NAMES) {
            try {
                Logic logic = TestFixtures.loadLogic(logicName);
                Vector<TestingFormula> formulas = logic.getTestingFormulae();

                if (formulas != null && !formulas.isEmpty()) {
                    for (int i = 0; i < formulas.size(); i++) {
                        TestingFormula tf = formulas.get(i);
                        args.add(Arguments.of(logicName, i, tf.getCode()));
                    }
                }
            } catch (Exception e) {
                // Skip logics that fail to load
                System.err.println("Warning: Could not load logic " + logicName + ": " + e.getMessage());
            }
        }

        return args.stream();
    }

    @Nested
    @DisplayName("Propositional Logic Formula Parsing")
    class PropositionalLogic {

        private Logic logic;
        private OldiesTokenizer tokenizer;

        @BeforeEach
        void setUp() throws ParseException {
            logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            tokenizer = TestFixtures.createTokenizer(logic);
        }

        @Test
        @DisplayName("should load Classical-Propositional-Logic successfully")
        void shouldLoadLogic() {
            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("Classical-Propositional-Logic");
            assertThat(logic.getConnectors()).isNotEmpty();
        }

        @Test
        @DisplayName("should parse simple propositional formula (P)")
        void shouldParsePropositionalFormula() throws ParseException {
            Expression expr = tokenizer.parseExpression("P");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isNotNull();
            assertThat(expr.toString()).isEqualTo("P");
        }

        @Test
        @DisplayName("should parse contradiction (P & ~P)")
        void shouldParseContradiction() throws ParseException {
            // In prefix notation: and P not P
            Expression expr = tokenizer.parseExpression("and P not P");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
            // Verify code string representation
            assertThat(expr.getCodeString()).isEqualTo("and P not P");
        }

        @Test
        @DisplayName("should parse tautology (P | ~P)")
        void shouldParseTautology() throws ParseException {
            // In prefix notation: or P not P
            Expression expr = tokenizer.parseExpression("or P not P");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("or");
        }

        @Test
        @DisplayName("should parse conjunction (P & Q)")
        void shouldParseConjunction() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P Q");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("and");
            assertThat(compound.getCodeString()).isEqualTo("and P Q");
        }

        @Test
        @DisplayName("should parse implication (P -> P)")
        void shouldParseImplication() throws ParseException {
            Expression expr = tokenizer.parseExpression("imp P P");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("imp");
        }

        @Test
        @DisplayName("should have testing formulas defined")
        void shouldHaveTestingFormulas() {
            Vector<TestingFormula> testingFormulas = logic.getTestingFormulae();

            assertThat(testingFormulas).isNotEmpty();
            assertThat(testingFormulas.size()).isGreaterThanOrEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Modal Logic K Formula Parsing")
    class ModalLogicK {

        private Logic logic;
        private OldiesTokenizer tokenizer;

        @BeforeEach
        void setUp() throws ParseException {
            logic = TestFixtures.loadLogic("Monomodal-K");
            tokenizer = TestFixtures.createTokenizer(logic);
        }

        @Test
        @DisplayName("should load Monomodal-K logic successfully")
        void shouldLoadLogic() {
            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("Monomodal-K");
            // Verify modal connectors exist
            assertThat(logic.getConnector("nec")).isNotNull();
            assertThat(logic.getConnector("pos")).isNotNull();
        }

        @Test
        @DisplayName("should parse K axiom: [](P -> Q) -> ([]P -> []Q)")
        void shouldParseKAxiom() throws ParseException {
            // In prefix notation: imp nec imp P Q imp nec P nec Q
            Expression expr = tokenizer.parseExpression("imp nec imp P Q imp nec P nec Q");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("imp");
        }

        @Test
        @DisplayName("should parse necessity formula: []P")
        void shouldParseNecP() throws ParseException {
            Expression expr = tokenizer.parseExpression("nec P");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("nec");
        }

        @Test
        @DisplayName("should parse possibility formula: <>P")
        void shouldParsePosP() throws ParseException {
            Expression expr = tokenizer.parseExpression("pos P");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            ExpressionWithSubExpressions compound = (ExpressionWithSubExpressions) expr;
            assertThat(compound.getConnector().getName()).isEqualTo("pos");
        }

        @Test
        @DisplayName("should parse nested modal formula: []<>P")
        void shouldParseNestedModal() throws ParseException {
            Expression expr = tokenizer.parseExpression("nec pos P");
            tokenizer.verifyCodeEnd();

            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(expr.getCodeString()).isEqualTo("nec pos P");
        }

        @Test
        @DisplayName("should have modal testing formulas")
        void shouldHaveModalTestingFormulas() {
            Vector<TestingFormula> testingFormulas = logic.getTestingFormulae();

            assertThat(testingFormulas).isNotEmpty();
            // Verify at least one formula uses modal operators
            boolean hasModalFormula = testingFormulas.stream()
                    .anyMatch(tf -> tf.getCode().contains("nec") || tf.getCode().contains("pos"));
            assertThat(hasModalFormula).isTrue();
        }
    }

    @Nested
    @DisplayName("Testing Formulas from Logic XML Files")
    class TestingFormulasFromLogics {

        @Test
        @DisplayName("should extract testing formulas as infix code")
        void shouldExtractTestingFormulasAsInfix() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");

            Vector<TestingFormula> testingFormulas = logic.getTestingFormulae();
            assertThat(testingFormulas).isNotEmpty();

            for (TestingFormula tf : testingFormulas) {
                // getCode() returns the prefix code string
                String code = tf.getCode();
                assertThat(code).isNotNull();
                assertThat(code).isNotEmpty();

                // The formula should be parseable
                MarkedExpression formula = tf.getFormula();
                assertThat(formula).isNotNull();
                assertThat(formula.expression).isNotNull();

                // getCodeString() returns the formula in prefix format
                String codeString = formula.expression.getCodeString();
                assertThat(codeString).isEqualTo(code);
            }
        }

        @Test
        @DisplayName("should parse Classical-Propositional-Logic testing formulas")
        void shouldParseClassicalPropositionalTestingFormulas() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Vector<TestingFormula> testingFormulas = logic.getTestingFormulae();

            for (TestingFormula tf : testingFormulas) {
                String code = tf.getCode();

                // Re-parse the formula code to verify it's valid
                Expression parsed = tokenizer.parseExpression(code);
                tokenizer.verifyCodeEnd();

                assertThat(parsed).isNotNull();
                assertThat(parsed.getCodeString()).isEqualTo(code);

                // Re-initialize tokenizer for next formula
                tokenizer = TestFixtures.createTokenizer(logic);
            }
        }

        @Test
        @DisplayName("should parse Monomodal-K testing formulas")
        void shouldParseMonomodalKTestingFormulas() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Vector<TestingFormula> testingFormulas = logic.getTestingFormulae();
            assertThat(testingFormulas).isNotEmpty();

            for (TestingFormula tf : testingFormulas) {
                String code = tf.getCode();

                Expression parsed = tokenizer.parseExpression(code);
                tokenizer.verifyCodeEnd();

                assertThat(parsed).isNotNull();

                // Re-initialize tokenizer for next formula
                tokenizer = TestFixtures.createTokenizer(logic);
            }
        }

        @Test
        @DisplayName("should parse S4 testing formulas")
        void shouldParseS4TestingFormulas() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4Optimal");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Vector<TestingFormula> testingFormulas = logic.getTestingFormulae();
            assertThat(testingFormulas).isNotEmpty();

            for (TestingFormula tf : testingFormulas) {
                String code = tf.getCode();

                Expression parsed = tokenizer.parseExpression(code);
                tokenizer.verifyCodeEnd();

                assertThat(parsed).isNotNull();

                // Re-initialize tokenizer for next formula
                tokenizer = TestFixtures.createTokenizer(logic);
            }
        }

        @ParameterizedTest(name = "{0} - formula #{1}: {2}")
        @MethodSource("lotrec.logics.SatisfiabilityTest#allLogicsWithTestingFormulas")
        @DisplayName("should parse all predefined testing formulas")
        void shouldParseAllPredefinedTestingFormulas(String logicName, int formulaIndex, String formulaCode)
                throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            // Parse the formula
            Expression parsed = tokenizer.parseExpression(formulaCode);
            tokenizer.verifyCodeEnd();

            assertThat(parsed)
                    .as("Formula '%s' from logic '%s' should parse successfully", formulaCode, logicName)
                    .isNotNull();
        }
    }

    @Nested
    @DisplayName("Logic Loading and Validation")
    class LogicLoadingAndValidation {

        @Test
        @DisplayName("should load all predefined logics successfully")
        void shouldLoadAllPredefinedLogics() {
            List<String> failedLogics = new ArrayList<>();

            for (String logicName : TestFixtures.ALL_LOGIC_NAMES) {
                try {
                    Logic logic = TestFixtures.loadLogic(logicName);
                    assertThat(logic).isNotNull();
                    assertThat(logic.getName()).isNotEmpty();
                } catch (Exception e) {
                    failedLogics.add(logicName + ": " + e.getMessage());
                }
            }

            assertThat(failedLogics)
                    .as("All predefined logics should load successfully")
                    .isEmpty();
        }

        @Test
        @DisplayName("should have connectors defined in each logic")
        void shouldHaveConnectorsInEachLogic() throws ParseException {
            for (String logicName : TestFixtures.ALL_LOGIC_NAMES) {
                Logic logic = TestFixtures.loadLogic(logicName);

                assertThat(logic.getConnectors())
                        .as("Logic '%s' should have connectors", logicName)
                        .isNotEmpty();
            }
        }

        @Test
        @DisplayName("should have a main strategy defined in each logic")
        void shouldHaveMainStrategyInEachLogic() throws ParseException {
            for (String logicName : TestFixtures.ALL_LOGIC_NAMES) {
                Logic logic = TestFixtures.loadLogic(logicName);

                String mainStrategyName = logic.getMainStrategyName();
                assertThat(mainStrategyName)
                        .as("Logic '%s' should have a main strategy name", logicName)
                        .isNotNull()
                        .isNotEmpty();

                // Verify the strategy exists
                assertThat(logic.getStrategy(mainStrategyName))
                        .as("Main strategy '%s' should exist in logic '%s'", mainStrategyName, logicName)
                        .isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("MarkedExpression and Formula Setup")
    class MarkedExpressionSetup {

        @Test
        @DisplayName("should create MarkedExpression from parsed formula")
        void shouldCreateMarkedExpression() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Expression expr = tokenizer.parseExpression("and P pos Q");
            tokenizer.verifyCodeEnd();

            MarkedExpression markedExpr = new MarkedExpression(expr);

            assertThat(markedExpr).isNotNull();
            assertThat(markedExpr.expression).isEqualTo(expr);
            assertThat(markedExpr.getCodeString()).isEqualTo("and P pos Q");
        }

        @Test
        @DisplayName("should access testing formula's MarkedExpression")
        void shouldAccessTestingFormulaMarkedExpression() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");

            Vector<TestingFormula> testingFormulas = logic.getTestingFormulae();
            assertThat(testingFormulas).isNotEmpty();

            TestingFormula tf = testingFormulas.get(0);
            MarkedExpression formula = tf.getFormula();

            assertThat(formula).isNotNull();
            assertThat(formula.expression).isNotNull();
            assertThat(formula.getCodeString()).isEqualTo(tf.getCode());
        }

        @Test
        @DisplayName("should verify formula code roundtrip")
        void shouldVerifyFormulaCodeRoundtrip() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            String originalCode = "and nec P pos or Q not R";
            Expression expr = tokenizer.parseExpression(originalCode);
            tokenizer.verifyCodeEnd();

            String roundtripCode = expr.getCodeString();

            assertThat(roundtripCode).isEqualTo(originalCode);
        }
    }

    /**
     * Note: The following tests would require full Engine execution with GUI initialization.
     * They are documented here but marked as disabled since they require integration testing.
     *
     * To run full satisfiability checks, you would need:
     * 1. Initialize Cytoscape and Swing UI (via Launcher)
     * 2. Create an Engine with Logic, Strategy, and MarkedExpression
     * 3. Call engine.buildTableaux() and engine.start()
     * 4. Wait for engine completion and check results
     *
     * Example (requires GUI):
     * <pre>
     * // This would require GUI initialization
     * Engine engine = new Engine(logic, strategy, formula, mainFrame);
     * engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
     * engine.buildTableaux();
     * engine.start();
     * engine.join();
     * // Check engine.getCurrentWallet() for results
     * </pre>
     *
     * For headless testing, consider:
     * - Using a mock MainFrame
     * - Refactoring Engine to separate tableau logic from GUI updates
     * - Creating a headless mode for the engine
     */
    @Nested
    @DisplayName("Engine Integration Tests (require GUI - documented only)")
    class EngineIntegrationDocumentation {

        @Test
        @DisplayName("documents how to set up engine for satisfiability testing")
        void documentsEngineSetup() throws ParseException {
            // This test documents the setup process without actually running the engine

            // Step 1: Load the logic
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            assertThat(logic).isNotNull();

            // Step 2: Create tokenizer and parse formula
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);
            Expression expr = tokenizer.parseExpression("and P not P"); // contradiction
            tokenizer.verifyCodeEnd();

            // Step 3: Create MarkedExpression
            MarkedExpression formula = new MarkedExpression(expr);
            assertThat(formula).isNotNull();

            // Step 4: Parse the main strategy
            String mainStrategyName = logic.getMainStrategyName();
            assertThat(mainStrategyName).isNotNull();

            // Note: To actually run the engine, you would need:
            // - MainFrame mainFrame (requires GUI initialization)
            // - Strategy newStr = tokenizer.parseStrategy(logic.getStrategy(mainStrategyName).getCode());
            // - Engine engine = new Engine(logic, newStr, formula, mainFrame);
            // - engine.buildTableaux();
            // - engine.start();
            // - engine.join();

            // The result would be available in engine.getCurrentWallet()
            // An open tableau indicates satisfiability
            // All closed tableaux indicate unsatisfiability
        }

        @Test
        @DisplayName("documents benchmark method parameters")
        void documentsBenchmarkParameters() throws ParseException {
            // The Launcher.benchmark() method expects:
            // - logicName: Name of the logic file (without .xml)
            // - formulaInfixCode: Formula in infix notation (e.g., "P & ~P")
            // - SAT: true to stop at first open tableau, false to build all
            // - NEG: true to negate the formula before testing

            // Example benchmark args:
            String[] args = TestFixtures.benchmarkArgs(
                    "Classical-Propositional-Logic",
                    "P & ~P",  // contradiction in infix
                    true,     // SAT mode
                    false     // don't negate
            );

            assertThat(args).hasSize(4);
            assertThat(args[0]).isEqualTo("Classical-Propositional-Logic");
            assertThat(args[1]).isEqualTo("P & ~P");
            assertThat(args[2]).isEqualTo("true");
            assertThat(args[3]).isEqualTo("false");
        }
    }
}
