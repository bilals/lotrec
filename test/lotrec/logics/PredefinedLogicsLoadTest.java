package lotrec.logics;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.tableau.Rule;
import lotrec.parser.LogicXMLParser;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for loading all 38 predefined logic definitions.
 * Verifies that each logic XML file can be parsed successfully and
 * contains valid connectors, rules, and strategies.
 */
@DisplayName("Predefined Logics Load Tests")
class PredefinedLogicsLoadTest {

    private LogicXMLParser parser;

    @BeforeEach
    void setUp() {
        parser = new LogicXMLParser();
    }

    /**
     * Provides all 38 predefined logic names for parameterized tests.
     */
    static Stream<String> allLogicNames() {
        return Arrays.stream(TestFixtures.ALL_LOGIC_NAMES);
    }

    // ========================================================================
    // Main Parameterized Test - All 38 Logics
    // ========================================================================

    @ParameterizedTest(name = "Logic: {0}")
    @MethodSource("allLogicNames")
    @DisplayName("should load all predefined logics without error")
    void shouldLoadAllPredefinedLogicsWithoutError(String logicName) {
        assertThatCode(() -> TestFixtures.loadLogic(logicName))
                .as("Loading logic '%s' should not throw any exception", logicName)
                .doesNotThrowAnyException();
    }

    // ========================================================================
    // Specific Logic Tests
    // ========================================================================

    @Nested
    @DisplayName("Specific Logic Loading Tests")
    class SpecificLogicTests {

        @Test
        @DisplayName("should load Classical Propositional Logic")
        void shouldLoadClassicalPropositionalLogic() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("Classical-Propositional-Logic");
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
            assertThat(logic.getMainStrategyName()).isNotBlank();
        }

        @Test
        @DisplayName("should load Monomodal K")
        void shouldLoadMonomodalK() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("Monomodal-K");
            // Modal logic K should have modal operators
            assertThat(logic.getConnectors())
                    .extracting(Connector::getName)
                    .as("Modal K should contain modal connectors")
                    .isNotEmpty();
        }

        @Test
        @DisplayName("should load S4 Explicit R")
        void shouldLoadS4ExplicitR() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4-Explicit-R");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("S4-Explicit-R");
            // S4 has reflexivity and transitivity
            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should load S5 Explicit Edges")
        void shouldLoadS5ExplicitEdges() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S5-explicit-edges");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("S5-explicit-edges");
            // S5 is reflexive, symmetric, and transitive
            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should load KD")
        void shouldLoadKD() throws ParseException {
            Logic logic = TestFixtures.loadLogic("KD");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("KD");
            // KD has seriality (every world has a successor)
            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should load KD45")
        void shouldLoadKD45() throws ParseException {
            Logic logic = TestFixtures.loadLogic("KD45");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("KD45");
            // KD45 is serial, transitive, and Euclidean
            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should load Intuitionistic Logic")
        void shouldLoadIntuitionisticLogic() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Intuitionistic-Logic-Lj");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("Intuitionistic-Logic-Lj");
            // Intuitionistic logic has different implication rules
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should load LTL")
        void shouldLoadLTL() throws ParseException {
            Logic logic = TestFixtures.loadLogic("LTL");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("LTL");
            // Linear Temporal Logic should have temporal operators
            assertThat(logic.getConnectors()).isNotEmpty();
        }

        @Test
        @DisplayName("should load PDL")
        void shouldLoadPDL() throws ParseException {
            Logic logic = TestFixtures.loadLogic("PDL");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("PDL");
            // Propositional Dynamic Logic has program connectors
            assertThat(logic.getConnectors()).isNotEmpty();
        }

        @Test
        @DisplayName("should load Hybrid Logic")
        void shouldLoadHybridLogic() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Hybrid-Logic-H-at");

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo("Hybrid-Logic-H-at");
            // Hybrid logic has nominals and satisfaction operators
            assertThat(logic.getConnectors()).isNotEmpty();
        }
    }

    // ========================================================================
    // Validation Tests - Structural Integrity
    // ========================================================================

    @Nested
    @DisplayName("Structural Validation Tests")
    class StructuralValidationTests {

        @ParameterizedTest(name = "Logic: {0}")
        @MethodSource("lotrec.logics.PredefinedLogicsLoadTest#allLogicNames")
        @DisplayName("should have valid connectors - at least one connector per logic")
        void shouldHaveValidConnectors(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic.getConnectors())
                    .as("Logic '%s' should have at least one connector", logicName)
                    .isNotEmpty();

            // Each connector should have valid properties
            for (Connector connector : logic.getConnectors()) {
                assertThat(connector.getName())
                        .as("Connector name should not be blank in logic '%s'", logicName)
                        .isNotBlank();
                assertThat(connector.getArity())
                        .as("Connector '%s' arity should be non-negative in logic '%s'",
                            connector.getName(), logicName)
                        .isGreaterThanOrEqualTo(0);
                assertThat(connector.getOutString())
                        .as("Connector '%s' output format should not be blank in logic '%s'",
                            connector.getName(), logicName)
                        .isNotBlank();
            }
        }

        @ParameterizedTest(name = "Logic: {0}")
        @MethodSource("lotrec.logics.PredefinedLogicsLoadTest#allLogicNames")
        @DisplayName("should have valid rules - at least one rule per logic")
        void shouldHaveValidRules(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic.getRules())
                    .as("Logic '%s' should have at least one rule", logicName)
                    .isNotEmpty();

            // Each rule should have a valid name
            for (Rule rule : logic.getRules()) {
                assertThat(rule.getName())
                        .as("Rule name should not be blank in logic '%s'", logicName)
                        .isNotBlank();
            }
        }

        @ParameterizedTest(name = "Logic: {0}")
        @MethodSource("lotrec.logics.PredefinedLogicsLoadTest#allLogicNames")
        @DisplayName("should have valid strategy - main strategy must be defined")
        void shouldHaveValidStrategy(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            // Main strategy name should be set
            assertThat(logic.getMainStrategyName())
                    .as("Logic '%s' should have a main strategy name", logicName)
                    .isNotBlank();

            // Main strategy should exist in strategies list
            Strategy mainStrategy = logic.getStrategy(logic.getMainStrategyName());
            assertThat(mainStrategy)
                    .as("Main strategy '%s' should exist in logic '%s'",
                        logic.getMainStrategyName(), logicName)
                    .isNotNull();

            // All strategies should have valid names
            for (Strategy strategy : logic.getStrategies()) {
                assertThat(strategy.getWorkerName())
                        .as("Strategy name should not be blank in logic '%s'", logicName)
                        .isNotBlank();
            }
        }

        @ParameterizedTest(name = "Logic: {0}")
        @MethodSource("lotrec.logics.PredefinedLogicsLoadTest#allLogicNames")
        @DisplayName("should have consistent structure - all components properly linked")
        void shouldHaveConsistentStructure(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            // Logic should have a name
            assertThat(logic.getName())
                    .as("Logic should have a name")
                    .isNotBlank()
                    .isEqualTo(logicName);

            // Should have connectors
            assertThat(logic.getConnectors())
                    .as("Logic '%s' should have connectors", logicName)
                    .isNotNull()
                    .isNotEmpty();

            // Should have rules
            assertThat(logic.getRules())
                    .as("Logic '%s' should have rules", logicName)
                    .isNotNull()
                    .isNotEmpty();

            // Should have strategies
            assertThat(logic.getStrategies())
                    .as("Logic '%s' should have strategies", logicName)
                    .isNotNull()
                    .isNotEmpty();

            // Main strategy reference should be valid
            String mainStrategyName = logic.getMainStrategyName();
            assertThat(logic.getStrategy(mainStrategyName))
                    .as("Main strategy reference should be valid in logic '%s'", logicName)
                    .isNotNull();
        }
    }

    // ========================================================================
    // Additional Validation Tests
    // ========================================================================

    @Nested
    @DisplayName("Additional Validation Tests")
    class AdditionalValidationTests {

        @ParameterizedTest(name = "Logic: {0}")
        @ValueSource(strings = {
            "S4-with-history",
            "S4Optimal",
            "S4Minimal"
        })
        @DisplayName("should load S4 variant logics")
        void shouldLoadS4Variants(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo(logicName);
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
        }

        @ParameterizedTest(name = "Logic: {0}")
        @ValueSource(strings = {
            "KT-explicit-edges",
            "KT-implicit-edges",
            "KB-explicit-edges",
            "KB-implicit-edges",
            "K4-explicit-edges",
            "K4-implicit-edges"
        })
        @DisplayName("should load K extension logics")
        void shouldLoadKExtensions(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo(logicName);
        }

        @ParameterizedTest(name = "Logic: {0}")
        @ValueSource(strings = {
            "K45Optimal",
            "K5",
            "KB5",
            "KBT",
            "KBD",
            "Kalt1",
            "KMinimal"
        })
        @DisplayName("should load additional K variants")
        void shouldLoadAdditionalKVariants(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo(logicName);
        }

        @ParameterizedTest(name = "Logic: {0}")
        @ValueSource(strings = {
            "K+Universal",
            "K2-with-Inclusion",
            "Multimodal-Kn",
            "KConfluence",
            "K4Confluence"
        })
        @DisplayName("should load multimodal and confluence logics")
        void shouldLoadMultimodalLogics(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo(logicName);
        }

        @ParameterizedTest(name = "Logic: {0}")
        @ValueSource(strings = {
            "Model-Checking-Monomodal",
            "Model-Checking-Multimodal"
        })
        @DisplayName("should load model checking logics")
        void shouldLoadModelCheckingLogics(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo(logicName);
        }

        @ParameterizedTest(name = "Logic: {0}")
        @ValueSource(strings = {
            "Multi-S5-PAL",
            "xstit",
            "LJminimal",
            "KD45Optimal",
            "S5-implicit-edges"
        })
        @DisplayName("should load specialized logics")
        void shouldLoadSpecializedLogics(String logicName) throws ParseException {
            Logic logic = TestFixtures.loadLogic(logicName);

            assertThat(logic).isNotNull();
            assertThat(logic.getName()).isEqualTo(logicName);
        }
    }

    // ========================================================================
    // Error Handling Tests
    // ========================================================================

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("should throw ParseException for non-existent logic file")
        void shouldThrowForNonExistentFile() {
            assertThatThrownBy(() -> TestFixtures.loadLogic("Non-Existent-Logic"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should use correct file path format")
        void shouldUseCorrectFilePathFormat() {
            String path = TestFixtures.logicPath("Monomodal-K");

            assertThat(path)
                    .isEqualTo("src/lotrec/logics/Monomodal-K.xml");
        }

        @Test
        @DisplayName("should have exactly 38 predefined logics")
        void shouldHaveExactly38PredefinedLogics() {
            assertThat(TestFixtures.ALL_LOGIC_NAMES)
                    .as("Should have exactly 38 predefined logics")
                    .hasSize(38);
        }
    }

    // ========================================================================
    // Performance Sanity Tests
    // ========================================================================

    @Nested
    @DisplayName("Performance Sanity Tests")
    class PerformanceSanityTests {

        @Test
        @DisplayName("should load all logics within reasonable time")
        void shouldLoadAllLogicsWithinReasonableTime() {
            long startTime = System.currentTimeMillis();

            for (String logicName : TestFixtures.ALL_LOGIC_NAMES) {
                assertThatCode(() -> TestFixtures.loadLogic(logicName))
                        .doesNotThrowAnyException();
            }

            long duration = System.currentTimeMillis() - startTime;

            // All 38 logics should load in under 30 seconds
            assertThat(duration)
                    .as("Loading all 38 logics should complete within 30 seconds")
                    .isLessThan(30_000);
        }
    }
}
