package lotrec.engine;

import gi.transformers.TransformerGUI;
import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.*;
import lotrec.parser.LogicXMLParser;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.Strategy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for benchmark-related functionality.
 *
 * Note: The Launcher.treatArgsForBenchmark() method requires full GUI/Cytoscape
 * initialization which does not work in a headless test environment. These tests
 * focus on components that can run without GUI:
 * - Benchmark arguments creation and validation
 * - Logic loading via LogicXMLParser
 * - Formula parsing via OldiesTokenizer
 * - Logic-specific formula processing
 *
 * @see lotrec.Launcher#treatArgsForBenchmark(String[])
 * @see lotrec.TestFixtures#benchmarkArgs(String, String, boolean, boolean)
 */
@DisplayName("Launcher Benchmark Tests")
class LauncherBenchmarkTest {

    @Nested
    @DisplayName("Benchmark Args Creation")
    class BenchmarkArgsCreation {

        @Test
        @DisplayName("should create args array with four elements")
        void shouldCreateArgsArrayWithFourElements() {
            String[] args = TestFixtures.benchmarkArgs("Monomodal-K", "P & Q", true, false);

            assertThat(args).hasSize(4);
        }

        @Test
        @DisplayName("should place logic name in first position")
        void shouldPlaceLogicNameInFirstPosition() {
            String[] args = TestFixtures.benchmarkArgs("Classical-Propositional-Logic", "P", false, false);

            assertThat(args[0]).isEqualTo("Classical-Propositional-Logic");
        }

        @Test
        @DisplayName("should place formula in second position")
        void shouldPlaceFormulaInSecondPosition() {
            String formula = "(P & Q) -> R";
            String[] args = TestFixtures.benchmarkArgs("Monomodal-K", formula, true, false);

            assertThat(args[1]).isEqualTo(formula);
        }

        @Test
        @DisplayName("should place SAT flag as string in third position")
        void shouldPlaceSatFlagInThirdPosition() {
            String[] argsTrue = TestFixtures.benchmarkArgs("Monomodal-K", "P", true, false);
            String[] argsFalse = TestFixtures.benchmarkArgs("Monomodal-K", "P", false, false);

            assertThat(argsTrue[2]).isEqualTo("true");
            assertThat(argsFalse[2]).isEqualTo("false");
        }

        @Test
        @DisplayName("should place NEG flag as string in fourth position")
        void shouldPlaceNegFlagInFourthPosition() {
            String[] argsTrue = TestFixtures.benchmarkArgs("Monomodal-K", "P", false, true);
            String[] argsFalse = TestFixtures.benchmarkArgs("Monomodal-K", "P", false, false);

            assertThat(argsTrue[3]).isEqualTo("true");
            assertThat(argsFalse[3]).isEqualTo("false");
        }

        @Test
        @DisplayName("should handle complex formula with special characters")
        void shouldHandleComplexFormulaWithSpecialCharacters() {
            String formula = "(([] P) & (<> Q)) -> (~R)";
            String[] args = TestFixtures.benchmarkArgs("Monomodal-K", formula, true, true);

            assertThat(args[1]).isEqualTo(formula);
        }

        @Test
        @DisplayName("should preserve exact formula string")
        void shouldPreserveExactFormulaString() {
            String formula = "  P   &   Q  ";  // with extra spaces
            String[] args = TestFixtures.benchmarkArgs("Monomodal-K", formula, false, false);

            assertThat(args[1]).isEqualTo(formula);
        }
    }

    @Nested
    @DisplayName("Logic Loading")
    class LogicLoading {

        private LogicXMLParser parser;

        @BeforeEach
        void setUp() {
            parser = new LogicXMLParser();
        }

        @ParameterizedTest(name = "should load logic: {0}")
        @ValueSource(strings = {
            "Classical-Propositional-Logic",
            "Monomodal-K",
            "S4-Explicit-R",
            "S4-with-history",
            "S4Optimal",
            "S4Minimal",
            "S5-explicit-edges",
            "S5-implicit-edges",
            "KD",
            "KD45",
            "KD45Optimal",
            "KT-explicit-edges",
            "KT-implicit-edges",
            "KB-explicit-edges",
            "KB-implicit-edges",
            "K4-explicit-edges",
            "K4-implicit-edges",
            "K4Confluence",
            "K45Optimal",
            "K5",
            "KB5",
            "KBT",
            "KBD",
            "Kalt1",
            "KMinimal",
            "K+Universal",
            "K2-with-Inclusion",
            "Multimodal-Kn",
            "KConfluence",
            "Intuitionistic-Logic-Lj",
            "Hybrid-Logic-H-at",
            "LTL",
            "PDL",
            "Model-Checking-Monomodal",
            "Model-Checking-Multimodal",
            "Multi-S5-PAL",
            "xstit",
            "LJminimal"
        })
        @DisplayName("should accept all predefined logic names")
        void shouldAcceptAllPredefinedLogicNames(String logicName) throws ParseException {
            String path = TestFixtures.logicPath(logicName);
            File file = new File(path);

            // Verify file exists
            assertThat(file).exists();

            // Parse the logic
            Logic logic = parser.parseLogic(path);

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo(logicName);
        }

        @Test
        @DisplayName("should handle invalid logic name")
        void shouldHandleInvalidLogicName() {
            String invalidPath = TestFixtures.logicPath("NonExistent-Logic");

            assertThatThrownBy(() -> parser.parseLogic(invalidPath))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should load logic with connectors")
        void shouldLoadLogicWithConnectors() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getConnector("not")).isNotNull();
            assertThat(logic.getConnector("and")).isNotNull();
            assertThat(logic.getConnector("or")).isNotNull();
        }

        @Test
        @DisplayName("should load logic with rules")
        void shouldLoadLogicWithRules() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should load logic with strategies")
        void shouldLoadLogicWithStrategies() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getStrategies()).isNotEmpty();
            assertThat(logic.getMainStrategyName()).isNotNull();
            assertThat(logic.getStrategy(logic.getMainStrategyName())).isNotNull();
        }

        @Test
        @DisplayName("should load logic with testing formulas")
        void shouldLoadLogicWithTestingFormulas() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getTestingFormulae()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Formula Processing")
    class FormulaProcessing {

        private Logic logic;
        private OldiesTokenizer tokenizer;

        @BeforeEach
        void setUp() throws ParseException {
            logic = TestFixtures.loadLogic("Monomodal-K");
            tokenizer = TestFixtures.createTokenizer(logic);
        }

        @Test
        @DisplayName("should accept infix formula via TransformerGUI")
        void shouldAcceptInfixFormula() {
            TransformerGUI transformer = new TransformerGUI();
            String prefixResult = transformer.toPrefix("P & Q");

            assertThat(prefixResult).isNotNull();
            assertThat(prefixResult).contains("and");
        }

        @Test
        @DisplayName("should accept complex nested formula")
        void shouldAcceptComplexNestedFormula() {
            TransformerGUI transformer = new TransformerGUI();
            String prefixResult = transformer.toPrefix("((P & Q) -> (R v S))");

            assertThat(prefixResult).isNotNull();
        }

        @Test
        @DisplayName("should accept modal formula")
        void shouldAcceptModalFormula() {
            TransformerGUI transformer = new TransformerGUI();
            String prefixResult = transformer.toPrefix("box P -> dia Q");

            assertThat(prefixResult).isNotNull();
        }

        @Test
        @DisplayName("should handle formula with all basic connectors")
        void shouldHandleFormulaWithAllConnectors() {
            TransformerGUI transformer = new TransformerGUI();
            String prefixResult = transformer.toPrefix("(~P & Q) v (R -> S)");

            assertThat(prefixResult).isNotNull();
        }

        @Test
        @DisplayName("should parse prefix formula to expression")
        void shouldParsePrefixFormulaToExpression() throws ParseException {
            Expression expr = tokenizer.parseExpression("and P Q");

            assertThat(expr).isNotNull();
            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
        }

        @Test
        @DisplayName("should parse nested prefix formula")
        void shouldParseNestedPrefixFormula() throws ParseException {
            Expression expr = tokenizer.parseExpression("imp and P Q or R S");

            assertThat(expr).isNotNull();
            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
        }

        @Test
        @DisplayName("should reject malformed formula")
        void shouldRejectMalformedFormula() {
            // Unknown connector should throw
            assertThatThrownBy(() -> tokenizer.parseExpression("unknownop P Q"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should parse modal operators")
        void shouldParseModalOperators() throws ParseException {
            Expression necExpr = tokenizer.parseExpression("nec P");
            Expression posExpr = tokenizer.parseExpression("pos Q");

            assertThat(necExpr).isInstanceOf(ExpressionWithSubExpressions.class);
            assertThat(posExpr).isInstanceOf(ExpressionWithSubExpressions.class);
        }
    }

    @Nested
    @DisplayName("Engine Initialization")
    class EngineInitialization {

        /**
         * Note: Full Engine construction requires MainFrame which requires GUI.
         * These tests verify what can be tested without GUI.
         */

        @Test
        @DisplayName("should create Engine with default constructor")
        void shouldCreateEngineWithDefaultConstructor() {
            Engine engine = new Engine();

            assertThat(engine).isNotNull();
        }

        @Test
        @DisplayName("Engine constants should be defined")
        void engineConstantsShouldBeDefined() {
            assertThat(Engine.NOP_WHEN_HAVING_OPEN_TABLEAU).isEqualTo(0);
            assertThat(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU).isEqualTo(1);
            assertThat(Engine.PAUSE_WHEN_HAVING_OPEN_TABLEAU).isEqualTo(2);
        }

        @Test
        @DisplayName("should verify EngineTimer exists")
        void shouldVerifyEngineTimerExists() {
            // EngineTimer is used by Engine for performance tracking
            // This verifies the class can be instantiated
            EngineTimer timer = new EngineTimer();

            assertThat(timer).isNotNull();
        }

        @Test
        @DisplayName("should verify Benchmarker exists")
        void shouldVerifyBenchmarkerExists() {
            // Benchmarker is used by Engine for benchmark statistics
            Benchmarker benchmarker = new Benchmarker();

            assertThat(benchmarker).isNotNull();
        }

        /**
         * Note: Full Engine(Logic, Strategy, MarkedExpression, MainFrame) constructor
         * cannot be tested without GUI. The following test documents this limitation.
         */
        @Test
        @DisplayName("documents: full Engine construction requires MainFrame (GUI)")
        void documentsFullEngineConstructionRequiresMainFrame() throws ParseException {
            // These components can be created without GUI
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Strategy strategy = tokenizer.parseStrategy(
                    logic.getStrategy(logic.getMainStrategyName()).getCode());
            MarkedExpression formula = new MarkedExpression(
                    tokenizer.parseExpression("and P Q"));

            // Verify these are properly created
            assertThat(logic).isNotNull();
            assertThat(strategy).isNotNull();
            assertThat(formula).isNotNull();

            // Note: Engine(logic, strategy, formula, mainFrame) requires MainFrame
            // which requires GUI initialization. This is a documented limitation.
        }
    }

    @Nested
    @DisplayName("Logic-Specific Tests")
    class LogicSpecificTests {

        @Test
        @DisplayName("should parse Classical Propositional Logic formula")
        void shouldParseClassicalPropositionalFormula() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            // CPL formulas: and, or, not, imp, equiv
            Expression expr = tokenizer.parseExpression("and P imp Q not R");

            assertThat(expr).isNotNull();
            assertThat(expr).isInstanceOf(ExpressionWithSubExpressions.class);
        }

        @Test
        @DisplayName("should parse Monomodal K formula")
        void shouldParseMonomodalKFormula() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            // K has modal operators: nec, pos
            Expression expr = tokenizer.parseExpression("and nec P pos Q");

            assertThat(expr).isNotNull();
        }

        @Test
        @DisplayName("should parse S4 formula")
        void shouldParseS4Formula() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4Optimal");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Expression expr = tokenizer.parseExpression("nec nec P");

            assertThat(expr).isNotNull();
        }

        @Test
        @DisplayName("should parse S5 formula")
        void shouldParseS5Formula() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S5-explicit-edges");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Expression expr = tokenizer.parseExpression("pos nec P");

            assertThat(expr).isNotNull();
        }

        @Test
        @DisplayName("should parse KD formula")
        void shouldParseKDFormula() throws ParseException {
            Logic logic = TestFixtures.loadLogic("KD");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Expression expr = tokenizer.parseExpression("imp nec P pos P");

            assertThat(expr).isNotNull();
        }

        @Test
        @DisplayName("should parse Intuitionistic Logic formula")
        void shouldParseIntuitionisticLogicFormula() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Intuitionistic-Logic-Lj");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            // Verify we can parse basic formulas
            Expression expr = tokenizer.parseExpression("imp P P");

            assertThat(expr).isNotNull();
        }

        @Test
        @DisplayName("should load and verify LTL connectors")
        void shouldLoadAndVerifyLtlConnectors() throws ParseException {
            Logic logic = TestFixtures.loadLogic("LTL");

            // LTL should have temporal operators
            assertThat(logic.getConnectors()).isNotEmpty();
        }

        @Test
        @DisplayName("should load and verify PDL connectors")
        void shouldLoadAndVerifyPdlConnectors() throws ParseException {
            Logic logic = TestFixtures.loadLogic("PDL");

            assertThat(logic.getConnectors()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Testing Formulas Validation")
    class TestingFormulasValidation {

        @ParameterizedTest(name = "logic {0} should have parseable testing formulas")
        @ValueSource(strings = {
                "Classical-Propositional-Logic",
                "Monomodal-K",
                "S4Optimal",
                "KD"
        })
        @DisplayName("should have parseable testing formulas")
        void shouldHaveParseableTestingFormulas(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            for (TestingFormula tf : logic.getTestingFormulae()) {
                // Each testing formula should already be parsed
                assertThat(tf.getFormula()).isNotNull();
                // MarkedExpression has a public field 'expression'
                assertThat(tf.getFormula().expression).isNotNull();
            }
        }

        @Test
        @DisplayName("should have valid testing formulas in Monomodal-K")
        void shouldHaveValidTestingFormulasInMonomodalK() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getTestingFormulae()).hasSizeGreaterThan(0);

            for (TestingFormula tf : logic.getTestingFormulae()) {
                MarkedExpression me = tf.getFormula();
                assertThat(me).isNotNull();
                // MarkedExpression has a public field 'expression'
                assertThat(me.expression).isNotNull();
            }
        }
    }

    @Nested
    @DisplayName("Strategy Parsing")
    class StrategyParsing {

        @Test
        @DisplayName("should parse repeat strategy")
        void shouldParseRepeatStrategy() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            String strategyCode = "repeat Stop And end";
            Strategy strategy = tokenizer.parseStrategy(strategyCode);

            assertThat(strategy).isNotNull();
        }

        @Test
        @DisplayName("should parse firstRule strategy")
        void shouldParseFirstRuleStrategy() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            String strategyCode = "firstRule Stop And end";
            Strategy strategy = tokenizer.parseStrategy(strategyCode);

            assertThat(strategy).isNotNull();
        }

        @Test
        @DisplayName("should parse nested strategy")
        void shouldParseNestedStrategy() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            String strategyCode = "repeat firstRule Stop And end end";
            Strategy strategy = tokenizer.parseStrategy(strategyCode);

            assertThat(strategy).isNotNull();
        }

        @Test
        @DisplayName("should parse main strategy from each logic")
        void shouldParseMainStrategyFromEachLogic() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            String mainStrategyName = logic.getMainStrategyName();
            Strategy mainStrategy = logic.getStrategy(mainStrategyName);

            assertThat(mainStrategy).isNotNull();
            assertThat(mainStrategy.getCode()).isNotEmpty();

            // Verify we can re-parse the strategy code
            Strategy reparsed = tokenizer.parseStrategy(mainStrategy.getCode());
            assertThat(reparsed).isNotNull();
        }
    }

    @Nested
    @DisplayName("Expression Code Generation")
    class ExpressionCodeGeneration {

        @Test
        @DisplayName("should generate code string from expression")
        void shouldGenerateCodeStringFromExpression() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Expression expr = tokenizer.parseExpression("and P Q");
            String codeString = TestFixtures.toInfixCode(expr);

            assertThat(codeString).isNotNull();
        }

        @Test
        @DisplayName("should generate constant expression code")
        void shouldGenerateConstantExpressionCode() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Expression expr = tokenizer.parseExpression("P");
            String codeString = expr.getCodeString();

            assertThat(codeString).isEqualTo("P");
        }

        @Test
        @DisplayName("should generate compound expression code")
        void shouldGenerateCompoundExpressionCode() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            OldiesTokenizer tokenizer = TestFixtures.createTokenizer(logic);

            Expression expr = tokenizer.parseExpression("and P Q");
            String codeString = expr.getCodeString();

            assertThat(codeString).contains("and");
            assertThat(codeString).contains("P");
            assertThat(codeString).contains("Q");
        }
    }

    @Nested
    @DisplayName("Connector Properties")
    class ConnectorProperties {

        @Test
        @DisplayName("should verify connector arity")
        void shouldVerifyConnectorArity() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Connector notConn = logic.getConnector("not");
            Connector andConn = logic.getConnector("and");
            Connector necConn = logic.getConnector("nec");

            assertThat(notConn.getArity()).isEqualTo(1);
            assertThat(andConn.getArity()).isEqualTo(2);
            assertThat(necConn.getArity()).isEqualTo(1);
        }

        @Test
        @DisplayName("should verify connector output format")
        void shouldVerifyConnectorOutputFormat() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Connector notConn = logic.getConnector("not");
            Connector andConn = logic.getConnector("and");

            assertThat(notConn.getOutString()).isNotEmpty();
            assertThat(andConn.getOutString()).isNotEmpty();
        }

        @Test
        @DisplayName("should verify connector priority")
        void shouldVerifyConnectorPriority() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Connector notConn = logic.getConnector("not");
            Connector andConn = logic.getConnector("and");
            Connector orConn = logic.getConnector("or");

            // Priority determines parsing precedence
            // Higher priority binds tighter
            assertThat(notConn.getPriority()).isGreaterThanOrEqualTo(andConn.getPriority());
            assertThat(andConn.getPriority()).isGreaterThanOrEqualTo(orConn.getPriority());
        }

        @Test
        @DisplayName("should verify associativity property")
        void shouldVerifyAssociativityProperty() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Connector andConn = logic.getConnector("and");
            Connector notConn = logic.getConnector("not");

            // and is typically associative
            assertThat(andConn.isAssociative()).isTrue();
            // not is not associative (unary)
            assertThat(notConn.isAssociative()).isFalse();
        }
    }
}
