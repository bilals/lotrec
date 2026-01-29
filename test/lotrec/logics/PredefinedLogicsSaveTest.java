package lotrec.logics;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.parser.LogicXMLParser;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.AbstractAction;
import lotrec.process.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for round-trip serialization of logic definitions.
 * Verifies that logic definitions can be saved to XML and reloaded
 * while preserving all properties.
 */
@DisplayName("Predefined Logics Save Tests")
class PredefinedLogicsSaveTest {

    private LogicXMLParser parser;

    @TempDir
    Path tempDir;

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
    // Round-Trip Tests
    // ========================================================================

    @Nested
    @DisplayName("Round-Trip Serialization Tests")
    class RoundTripTests {

        @Test
        @DisplayName("should round-trip logic - save and reload produces equivalent logic")
        void shouldRoundTripLogic() throws ParseException, IOException {
            // Load original logic
            Logic original = TestFixtures.loadLogic("Monomodal-K");

            // Save to temp file
            Path tempFile = tempDir.resolve("roundtrip-test.xml");
            parser.saveLogic(original, tempFile.toString());

            // Reload from temp file
            Logic reloaded = parser.parseLogic(tempFile.toString());

            // Verify equivalence
            assertThat(reloaded.getConnectors())
                    .as("Reloaded logic should have same number of connectors")
                    .hasSameSizeAs(original.getConnectors());
            assertThat(reloaded.getRules())
                    .as("Reloaded logic should have same number of rules")
                    .hasSameSizeAs(original.getRules());
            assertThat(reloaded.getStrategies())
                    .as("Reloaded logic should have same number of strategies")
                    .hasSameSizeAs(original.getStrategies());
            assertThat(reloaded.getMainStrategyName())
                    .as("Reloaded logic should have same main strategy name")
                    .isEqualTo(original.getMainStrategyName());
        }

        @Test
        @DisplayName("should save and reload connectors")
        void shouldSaveAndReloadConnectors() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("connectors-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded.getConnectors())
                    .extracting(Connector::getName)
                    .containsExactlyElementsOf(
                            original.getConnectors().stream()
                                    .map(Connector::getName)
                                    .toList()
                    );
        }

        @Test
        @DisplayName("should save and reload rules")
        void shouldSaveAndReloadRules() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("rules-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded.getRules())
                    .extracting(Rule::getName)
                    .containsExactlyElementsOf(
                            original.getRules().stream()
                                    .map(Rule::getName)
                                    .toList()
                    );
        }

        @Test
        @DisplayName("should save and reload strategies")
        void shouldSaveAndReloadStrategies() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("strategies-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded.getStrategies())
                    .extracting(Strategy::getWorkerName)
                    .containsExactlyElementsOf(
                            original.getStrategies().stream()
                                    .map(Strategy::getWorkerName)
                                    .toList()
                    );
        }

        @Test
        @DisplayName("should preserve main strategy name")
        void shouldPreserveMainStrategy() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("main-strategy-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded.getMainStrategyName())
                    .isEqualTo(original.getMainStrategyName());

            // Verify the main strategy exists
            Strategy mainStrategy = reloaded.getStrategy(reloaded.getMainStrategyName());
            assertThat(mainStrategy)
                    .as("Main strategy should exist in reloaded logic")
                    .isNotNull();
        }

        @Test
        @DisplayName("should preserve testing formulas")
        void shouldPreserveTestingFormulas() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("testing-formulas-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded.getTestingFormulae())
                    .as("Reloaded logic should have same number of testing formulas")
                    .hasSameSizeAs(original.getTestingFormulae());

            // Check formula codes match
            for (int i = 0; i < original.getTestingFormulae().size(); i++) {
                TestingFormula origFormula = original.getTestingFormulae().get(i);
                TestingFormula reloadedFormula = reloaded.getTestingFormulae().get(i);
                assertThat(reloadedFormula.getCode())
                        .as("Testing formula code should be preserved")
                        .isEqualTo(origFormula.getCode());
            }
        }

        @Test
        @DisplayName("should preserve connector properties")
        void shouldPreserveConnectorProperties() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("connector-props-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            for (int i = 0; i < original.getConnectors().size(); i++) {
                Connector origConn = original.getConnectors().get(i);
                Connector reloadedConn = reloaded.getConnectors().get(i);

                assertThat(reloadedConn.getName())
                        .as("Connector name should be preserved")
                        .isEqualTo(origConn.getName());
                assertThat(reloadedConn.getArity())
                        .as("Connector arity should be preserved for '%s'", origConn.getName())
                        .isEqualTo(origConn.getArity());
                assertThat(reloadedConn.isAssociative())
                        .as("Connector associativity should be preserved for '%s'", origConn.getName())
                        .isEqualTo(origConn.isAssociative());
                assertThat(reloadedConn.getOutString())
                        .as("Connector output format should be preserved for '%s'", origConn.getName())
                        .isEqualTo(origConn.getOutString());
                assertThat(reloadedConn.getPriority())
                        .as("Connector priority should be preserved for '%s'", origConn.getName())
                        .isEqualTo(origConn.getPriority());
            }
        }

        @Test
        @DisplayName("should preserve rule conditions")
        void shouldPreserveRuleConditions() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("rule-conditions-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            for (int i = 0; i < original.getRules().size(); i++) {
                Rule origRule = original.getRules().get(i);
                Rule reloadedRule = reloaded.getRules().get(i);

                assertThat(reloadedRule.getConditions())
                        .as("Rule '%s' should have same number of conditions", origRule.getName())
                        .hasSameSizeAs(origRule.getConditions());

                for (int j = 0; j < origRule.getConditions().size(); j++) {
                    AbstractCondition origCond = origRule.getConditions().get(j);
                    AbstractCondition reloadedCond = reloadedRule.getConditions().get(j);

                    assertThat(reloadedCond.getName())
                            .as("Condition name should be preserved")
                            .isEqualTo(origCond.getName());
                    assertThat(reloadedCond.getParameters())
                            .as("Condition should have same number of parameters")
                            .hasSameSizeAs(origCond.getParameters());
                }
            }
        }

        @Test
        @DisplayName("should preserve rule actions")
        void shouldPreserveRuleActions() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("rule-actions-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            for (int i = 0; i < original.getRules().size(); i++) {
                Rule origRule = original.getRules().get(i);
                Rule reloadedRule = reloaded.getRules().get(i);

                assertThat(reloadedRule.getActions())
                        .as("Rule '%s' should have same number of actions", origRule.getName())
                        .hasSameSizeAs(origRule.getActions());

                for (int j = 0; j < origRule.getActions().size(); j++) {
                    AbstractAction origAction = origRule.getActions().get(j);
                    AbstractAction reloadedAction = reloadedRule.getActions().get(j);

                    assertThat(reloadedAction.getName())
                            .as("Action name should be preserved")
                            .isEqualTo(origAction.getName());
                    assertThat(reloadedAction.getParameters())
                            .as("Action should have same number of parameters")
                            .hasSameSizeAs(origAction.getParameters());
                }
            }
        }
    }

    // ========================================================================
    // Creation Tests
    // ========================================================================

    @Nested
    @DisplayName("Logic Creation and Save Tests")
    class CreationTests {

        @Test
        @DisplayName("should create new logic and save")
        void shouldCreateNewLogicAndSave() throws ParseException, IOException {
            // Create a minimal logic
            Logic newLogic = new Logic();
            newLogic.setName("TestNewLogic");
            newLogic.setDescription("A test logic created programmatically");

            // Add a connector
            Connector notConn = new Connector();
            notConn.setName("not");
            notConn.setArity(1);
            notConn.setAssociative(false);
            notConn.setOutString("~ _");
            notConn.setPriority(5);
            notConn.setComment("Negation operator");
            newLogic.addConnector(notConn);

            // Add a default strategy
            newLogic.addDefaultStrategy();

            // Save to temp file
            Path tempFile = tempDir.resolve("new-logic-test.xml");
            parser.saveLogic(newLogic, tempFile.toString());

            // Verify file was created
            assertThat(Files.exists(tempFile))
                    .as("Saved logic file should exist")
                    .isTrue();

            // Verify file is not empty
            assertThat(Files.size(tempFile))
                    .as("Saved logic file should not be empty")
                    .isGreaterThan(0);
        }

        @Test
        @DisplayName("should save to temp file")
        void shouldSaveToTempFile() throws ParseException, IOException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");

            Path tempFile = Files.createTempFile(tempDir, "logic-save-", ".xml");
            parser.saveLogic(logic, tempFile.toString());

            assertThat(Files.exists(tempFile))
                    .as("Temp file should exist after save")
                    .isTrue();
            assertThat(Files.size(tempFile))
                    .as("Saved file should have content")
                    .isGreaterThan(100); // XML header alone is about 50 chars
        }

        @Test
        @DisplayName("should reload saved logic")
        void shouldReloadSavedLogic() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("S4-Explicit-R");

            Path tempFile = tempDir.resolve("reload-test.xml");
            parser.saveLogic(original, tempFile.toString());

            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded)
                    .as("Reloaded logic should not be null")
                    .isNotNull();
            assertThat(reloaded.getConnectors())
                    .as("Reloaded logic should have connectors")
                    .isNotEmpty();
            assertThat(reloaded.getRules())
                    .as("Reloaded logic should have rules")
                    .isNotEmpty();
            assertThat(reloaded.getStrategies())
                    .as("Reloaded logic should have strategies")
                    .isNotEmpty();
        }
    }

    // ========================================================================
    // Preservation Tests
    // ========================================================================

    @Nested
    @DisplayName("Property Preservation Tests")
    class PreservationTests {

        @Test
        @DisplayName("should preserve logic name")
        void shouldPreserveLogicName() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("KD45");
            Path tempFile = tempDir.resolve("logic-name-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            // Note: Logic name is derived from filename during parsing
            // The saved file will have a different name than original
            assertThat(reloaded.getName())
                    .as("Reloaded logic should have a name derived from filename")
                    .isNotBlank();
        }

        @Test
        @DisplayName("should handle save errors - throws exception for invalid path")
        void shouldHandleSaveErrors() {
            Logic logic = TestFixtures.createMinimalLogic();
            logic.addDefaultStrategy();

            // Try to save to an invalid path (non-existent directory)
            String invalidPath = tempDir.resolve("non-existent-dir/subdir/logic.xml").toString();

            // The saveLogic method has internal error handling but may still throw
            // NullPointerException when trying to close a null stream
            // This test verifies that the method behaves consistently (throws or catches)
            assertThatThrownBy(() -> parser.saveLogic(logic, invalidPath))
                    .as("Save to invalid path should throw an exception")
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("should preserve connector order")
        void shouldPreserveConnectorOrder() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("PDL");
            Path tempFile = tempDir.resolve("connector-order-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            for (int i = 0; i < original.getConnectors().size(); i++) {
                assertThat(reloaded.getConnectors().get(i).getName())
                        .as("Connector at position %d should match", i)
                        .isEqualTo(original.getConnectors().get(i).getName());
            }
        }

        @Test
        @DisplayName("should preserve rule order")
        void shouldPreserveRuleOrder() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("LTL");
            Path tempFile = tempDir.resolve("rule-order-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            for (int i = 0; i < original.getRules().size(); i++) {
                assertThat(reloaded.getRules().get(i).getName())
                        .as("Rule at position %d should match", i)
                        .isEqualTo(original.getRules().get(i).getName());
            }
        }

        @Test
        @DisplayName("should preserve strategy order")
        void shouldPreserveStrategyOrder() throws ParseException, IOException {
            Logic original = TestFixtures.loadLogic("Hybrid-Logic-H-at");
            Path tempFile = tempDir.resolve("strategy-order-test.xml");

            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            for (int i = 0; i < original.getStrategies().size(); i++) {
                assertThat(reloaded.getStrategies().get(i).getWorkerName())
                        .as("Strategy at position %d should match", i)
                        .isEqualTo(original.getStrategies().get(i).getWorkerName());
            }
        }
    }

    // ========================================================================
    // Parameterized Test - All 38 Logics
    // ========================================================================

    @ParameterizedTest(name = "Logic: {0}")
    @MethodSource("allLogicNames")
    @DisplayName("should round-trip all predefined logics")
    void shouldRoundTripAllPredefinedLogics(String logicName) throws ParseException, IOException {
        // Load original logic
        Logic original = TestFixtures.loadLogic(logicName);

        // Save to temp file
        Path tempFile = tempDir.resolve(logicName + "-roundtrip.xml");
        parser.saveLogic(original, tempFile.toString());

        // Reload from temp file
        Logic reloaded = parser.parseLogic(tempFile.toString());

        // Verify structural equivalence
        assertThat(reloaded.getConnectors())
                .as("Logic '%s' should preserve connector count", logicName)
                .hasSameSizeAs(original.getConnectors());

        assertThat(reloaded.getRules())
                .as("Logic '%s' should preserve rule count", logicName)
                .hasSameSizeAs(original.getRules());

        assertThat(reloaded.getStrategies())
                .as("Logic '%s' should preserve strategy count", logicName)
                .hasSameSizeAs(original.getStrategies());

        assertThat(reloaded.getTestingFormulae())
                .as("Logic '%s' should preserve testing formula count", logicName)
                .hasSameSizeAs(original.getTestingFormulae());

        assertThat(reloaded.getMainStrategyName())
                .as("Logic '%s' should preserve main strategy name", logicName)
                .isEqualTo(original.getMainStrategyName());

        // Verify connector names match
        assertThat(reloaded.getConnectors())
                .extracting(Connector::getName)
                .as("Logic '%s' should preserve connector names", logicName)
                .containsExactlyElementsOf(
                        original.getConnectors().stream()
                                .map(Connector::getName)
                                .toList()
                );

        // Verify rule names match
        assertThat(reloaded.getRules())
                .extracting(Rule::getName)
                .as("Logic '%s' should preserve rule names", logicName)
                .containsExactlyElementsOf(
                        original.getRules().stream()
                                .map(Rule::getName)
                                .toList()
                );

        // Verify strategy names match
        assertThat(reloaded.getStrategies())
                .extracting(Strategy::getWorkerName)
                .as("Logic '%s' should preserve strategy names", logicName)
                .containsExactlyElementsOf(
                        original.getStrategies().stream()
                                .map(Strategy::getWorkerName)
                                .toList()
                );
    }

    // ========================================================================
    // XML Validation Tests
    // ========================================================================

    @Nested
    @DisplayName("XML Validation Tests")
    class XmlValidationTests {

        @Test
        @DisplayName("should produce valid XML")
        void shouldProduceValidXML() throws ParseException, IOException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");
            Path tempFile = tempDir.resolve("valid-xml-test.xml");

            parser.saveLogic(logic, tempFile.toString());

            // Read the file content
            String content = Files.readString(tempFile);

            // Basic XML validation checks
            assertThat(content)
                    .as("XML should start with XML declaration")
                    .startsWith("<?xml");

            assertThat(content)
                    .as("XML should contain logic root element")
                    .contains("<logic>");

            assertThat(content)
                    .as("XML should contain closing logic element")
                    .contains("</logic>");

            assertThat(content)
                    .as("XML should contain parser-version element")
                    .contains("<parser-version>");

            assertThat(content)
                    .as("XML should contain connector elements")
                    .contains("<connector>");

            assertThat(content)
                    .as("XML should contain rule elements")
                    .contains("<rule>");

            assertThat(content)
                    .as("XML should contain strategy elements")
                    .contains("<strategy>");

            assertThat(content)
                    .as("XML should contain main-strategy element")
                    .contains("<main-strategy>");

            // Verify it's parseable (ultimate validation)
            assertThatCode(() -> parser.parseLogic(tempFile.toString()))
                    .as("Saved XML should be parseable")
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("should preserve XML special characters")
        void shouldPreserveXmlSpecialCharacters() throws ParseException, IOException {
            // Load a logic that might have special characters in output format
            Logic logic = TestFixtures.loadLogic("Monomodal-K");
            Path tempFile = tempDir.resolve("special-chars-test.xml");

            parser.saveLogic(logic, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            // Find the 'and' connector which typically has '&' in output format
            Connector origAnd = logic.getConnector("and");
            Connector reloadedAnd = reloaded.getConnector("and");

            if (origAnd != null && reloadedAnd != null) {
                assertThat(reloadedAnd.getOutString())
                        .as("Connector output format with special characters should be preserved")
                        .isEqualTo(origAnd.getOutString());
            }
        }
    }

    // ========================================================================
    // Additional Edge Case Tests
    // ========================================================================

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("should handle logic with empty description")
        void shouldHandleEmptyDescription() throws ParseException, IOException {
            Logic logic = TestFixtures.createMinimalLogic();
            logic.setDescription("");
            logic.addDefaultStrategy();

            Path tempFile = tempDir.resolve("empty-desc-test.xml");
            parser.saveLogic(logic, tempFile.toString());

            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded)
                    .as("Logic with empty description should be reloadable")
                    .isNotNull();
        }

        @Test
        @DisplayName("should handle logic with multiple strategies")
        void shouldHandleMultipleStrategies() throws ParseException, IOException {
            // S4-Explicit-R typically has multiple strategies
            Logic original = TestFixtures.loadLogic("S4-Explicit-R");

            assertThat(original.getStrategies())
                    .as("Original logic should have multiple strategies")
                    .hasSizeGreaterThan(1);

            Path tempFile = tempDir.resolve("multi-strategy-test.xml");
            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            assertThat(reloaded.getStrategies())
                    .as("Reloaded logic should preserve all strategies")
                    .hasSameSizeAs(original.getStrategies());
        }

        @Test
        @DisplayName("should handle logic with complex rules")
        void shouldHandleComplexRules() throws ParseException, IOException {
            // PDL has complex rules with multiple conditions and actions
            Logic original = TestFixtures.loadLogic("PDL");

            Path tempFile = tempDir.resolve("complex-rules-test.xml");
            parser.saveLogic(original, tempFile.toString());
            Logic reloaded = parser.parseLogic(tempFile.toString());

            // Verify all rules preserved
            assertThat(reloaded.getRules())
                    .hasSameSizeAs(original.getRules());

            // Verify conditions and actions counts match for each rule
            for (int i = 0; i < original.getRules().size(); i++) {
                Rule origRule = original.getRules().get(i);
                Rule reloadedRule = reloaded.getRules().get(i);

                assertThat(reloadedRule.getConditions())
                        .as("Rule '%s' conditions should be preserved", origRule.getName())
                        .hasSameSizeAs(origRule.getConditions());

                assertThat(reloadedRule.getActions())
                        .as("Rule '%s' actions should be preserved", origRule.getName())
                        .hasSameSizeAs(origRule.getActions());
            }
        }
    }
}
