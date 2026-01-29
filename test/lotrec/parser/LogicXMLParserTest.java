package lotrec.parser;

import lotrec.TestFixtures;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.AbstractAction;
import lotrec.process.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Vector;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for LogicXMLParser.
 * Tests parsing of logic definition XML files including connectors, rules, strategies, and testing formulas.
 */
@DisplayName("LogicXMLParser")
class LogicXMLParserTest {

    private LogicXMLParser parser;

    @BeforeEach
    void setUp() {
        parser = new LogicXMLParser();
    }

    @Nested
    @DisplayName("Connector Parsing")
    class ConnectorParsing {

        @Test
        @DisplayName("should parse all connector properties from Monomodal-K")
        void shouldParseConnectorProperties() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Connector notConnector = logic.getConnector("not");

            assertThat(notConnector).isNotNull();
            assertThat(notConnector.getName()).isEqualTo("not");
            assertThat(notConnector.getArity()).isEqualTo(1);
            assertThat(notConnector.getOutString()).isEqualTo("~ _");
            assertThat(notConnector.getPriority()).isEqualTo(5);
            assertThat(notConnector.isAssociative()).isFalse();
        }

        @Test
        @DisplayName("should parse connector arity correctly for different arities")
        void shouldParseConnectorArity() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            // Unary connectors
            assertThat(logic.getConnector("not").getArity()).isEqualTo(1);
            assertThat(logic.getConnector("nec").getArity()).isEqualTo(1);
            assertThat(logic.getConnector("pos").getArity()).isEqualTo(1);

            // Binary connectors
            assertThat(logic.getConnector("and").getArity()).isEqualTo(2);
            assertThat(logic.getConnector("or").getArity()).isEqualTo(2);
            assertThat(logic.getConnector("imp").getArity()).isEqualTo(2);
            assertThat(logic.getConnector("equiv").getArity()).isEqualTo(2);
        }

        @Test
        @DisplayName("should parse connector output format")
        void shouldParseConnectorOutputFormat() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getConnector("not").getOutString()).isEqualTo("~ _");
            assertThat(logic.getConnector("and").getOutString()).isEqualTo("_ & _");
            assertThat(logic.getConnector("or").getOutString()).isEqualTo("_ v _");
            assertThat(logic.getConnector("imp").getOutString()).isEqualTo("_ -> _");
            assertThat(logic.getConnector("nec").getOutString()).isEqualTo("[] _");
            assertThat(logic.getConnector("pos").getOutString()).isEqualTo("<> _");
        }

        @Test
        @DisplayName("should parse connector priority")
        void shouldParseConnectorPriority() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            // Higher priority means tighter binding
            assertThat(logic.getConnector("not").getPriority()).isEqualTo(5);
            assertThat(logic.getConnector("nec").getPriority()).isEqualTo(5);
            assertThat(logic.getConnector("pos").getPriority()).isEqualTo(5);
            assertThat(logic.getConnector("and").getPriority()).isEqualTo(4);
            assertThat(logic.getConnector("or").getPriority()).isEqualTo(3);
            assertThat(logic.getConnector("imp").getPriority()).isEqualTo(2);
            assertThat(logic.getConnector("equiv").getPriority()).isEqualTo(0);
        }

        @Test
        @DisplayName("should parse connector associativity")
        void shouldParseConnectorAssociativity() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            // Unary connectors are not associative
            assertThat(logic.getConnector("not").isAssociative()).isFalse();
            assertThat(logic.getConnector("nec").isAssociative()).isFalse();

            // Binary connectors: and, or, imp are associative
            assertThat(logic.getConnector("and").isAssociative()).isTrue();
            assertThat(logic.getConnector("or").isAssociative()).isTrue();
            assertThat(logic.getConnector("imp").isAssociative()).isTrue();

            // equiv is not associative in this logic
            assertThat(logic.getConnector("equiv").isAssociative()).isFalse();
        }

        @Test
        @DisplayName("should parse multiple connectors")
        void shouldParseMultipleConnectors() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Vector<Connector> connectors = logic.getConnectors();

            assertThat(connectors).hasSize(7);
            assertThat(connectors).extracting(Connector::getName)
                    .containsExactlyInAnyOrder("not", "and", "or", "imp", "equiv", "nec", "pos");
        }

        @Test
        @DisplayName("should parse logic with minimal connectors")
        void shouldParseLogicWithMinimalConnectors() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4Minimal");

            Vector<Connector> connectors = logic.getConnectors();

            // S4Minimal only has not, and, nec
            assertThat(connectors).hasSize(3);
            assertThat(connectors).extracting(Connector::getName)
                    .containsExactlyInAnyOrder("not", "and", "nec");
        }
    }

    @Nested
    @DisplayName("Rule Parsing")
    class RuleParsing {

        @Test
        @DisplayName("should parse rule with conditions and actions")
        void shouldParseRuleWithConditionsAndActions() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Rule stopRule = logic.getRule("Stop");

            assertThat(stopRule).isNotNull();
            assertThat(stopRule.getConditions()).isNotEmpty();
            assertThat(stopRule.getActions()).isNotEmpty();
        }

        @Test
        @DisplayName("should parse rule name")
        void shouldParseRuleName() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getRule("Stop")).isNotNull();
            assertThat(logic.getRule("Stop").getName()).isEqualTo("Stop");
            assertThat(logic.getRule("NotNot")).isNotNull();
            assertThat(logic.getRule("And")).isNotNull();
            assertThat(logic.getRule("Nec")).isNotNull();
            assertThat(logic.getRule("Pos")).isNotNull();
        }

        @Test
        @DisplayName("should parse rule conditions")
        void shouldParseRuleConditions() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Rule stopRule = logic.getRule("Stop");
            Vector<AbstractCondition> conditions = stopRule.getConditions();

            assertThat(conditions).hasSize(2);
            // Both conditions are hasElement
            assertThat(conditions).extracting(AbstractCondition::getName)
                    .containsOnly("hasElement");
        }

        @Test
        @DisplayName("should parse rule actions")
        void shouldParseRuleActions() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Rule stopRule = logic.getRule("Stop");
            Vector<AbstractAction> actions = stopRule.getActions();

            assertThat(actions).hasSize(2);
            assertThat(actions).extracting(AbstractAction::getName)
                    .containsExactly("add", "stop");
        }

        @Test
        @DisplayName("should parse multiple rules")
        void shouldParseMultipleRules() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Vector<Rule> rules = logic.getRules();

            assertThat(rules).hasSizeGreaterThan(10);
            assertThat(rules).extracting(Rule::getName)
                    .contains("Stop", "NotNot", "And", "Or", "Nec", "Pos", "Imp", "Equiv");
        }

        @Test
        @DisplayName("should parse rule with isLinked condition")
        void shouldParseRuleWithIsLinkedCondition() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Rule necRule = logic.getRule("Nec");
            Vector<AbstractCondition> conditions = necRule.getConditions();

            assertThat(conditions).hasSize(2);
            assertThat(conditions).extracting(AbstractCondition::getName)
                    .contains("hasElement", "isLinked");
        }

        @Test
        @DisplayName("should parse rule with mark condition")
        void shouldParseRuleWithMarkCondition() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4Minimal");

            Rule notAndRule = logic.getRule("NotAnd");
            Vector<AbstractCondition> conditions = notAndRule.getConditions();

            assertThat(conditions).extracting(AbstractCondition::getName)
                    .contains("isNotMarked");
        }

        @Test
        @DisplayName("should parse rule with createNewNode action")
        void shouldParseRuleWithCreateNewNodeAction() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Rule posRule = logic.getRule("Pos");
            Vector<AbstractAction> actions = posRule.getActions();

            assertThat(actions).extracting(AbstractAction::getName)
                    .contains("createNewNode", "link", "add");
        }

        @Test
        @DisplayName("should parse rule with duplicate action")
        void shouldParseRuleWithDuplicateAction() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Rule orRule = logic.getRule("Or");
            Vector<AbstractAction> actions = orRule.getActions();

            assertThat(actions).extracting(AbstractAction::getName)
                    .contains("duplicate", "add");
        }
    }

    @Nested
    @DisplayName("Strategy Parsing")
    class StrategyParsing {

        @Test
        @DisplayName("should parse strategy code")
        void shouldParseStrategyCode() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Strategy kStrategy = logic.getStrategy("KStrategy");

            assertThat(kStrategy).isNotNull();
            assertThat(kStrategy.getCode()).isNotNull();
            assertThat(kStrategy.getCode()).contains("repeat");
            assertThat(kStrategy.getCode()).contains("end");
        }

        @Test
        @DisplayName("should parse main strategy reference")
        void shouldParseMainStrategy() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getMainStrategyName()).isEqualTo("KStrategy");
            assertThat(logic.getStrategy(logic.getMainStrategyName())).isNotNull();
        }

        @Test
        @DisplayName("should parse multiple strategies")
        void shouldParseMultipleStrategies() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Vector<Strategy> strategies = logic.getStrategies();

            assertThat(strategies).hasSizeGreaterThanOrEqualTo(2);
            assertThat(strategies).extracting(Strategy::getWorkerName)
                    .contains("CPLStrategy", "KStrategy");
        }

        @Test
        @DisplayName("should parse nested strategy code with repeat and firstRule")
        void shouldParseNestedStrategyCode() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Strategy cplStrategy = logic.getStrategy("CPLStrategy");

            assertThat(cplStrategy.getCode()).contains("repeat");
            assertThat(cplStrategy.getCode()).contains("firstRule");
            assertThat(cplStrategy.getCode()).contains("end");
        }

        @Test
        @DisplayName("should parse strategy with allRules routine")
        void shouldParseStrategyWithAllRulesRoutine() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4Minimal");

            Strategy s4Strategy = logic.getStrategy("S4Strategy");

            assertThat(s4Strategy.getCode()).contains("allRules");
        }

        @Test
        @DisplayName("should parse strategy calling another strategy")
        void shouldParseStrategyCallingAnotherStrategy() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Strategy kStrategy = logic.getStrategy("KStrategy");

            assertThat(kStrategy.getCode()).contains("CPLStrategy");
        }
    }

    @Nested
    @DisplayName("Testing Formula Parsing")
    class TestingFormulaParsing {

        @Test
        @DisplayName("should parse testing formulas")
        void shouldParseTestingFormulas() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Vector<TestingFormula> testingFormulae = logic.getTestingFormulae();

            assertThat(testingFormulae).isNotEmpty();
            assertThat(testingFormulae).hasSizeGreaterThanOrEqualTo(5);
        }

        @Test
        @DisplayName("should parse testing formula code")
        void shouldParseTestingFormulaCode() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            Vector<TestingFormula> testingFormulae = logic.getTestingFormulae();
            TestingFormula firstFormula = testingFormulae.get(0);

            assertThat(firstFormula.getCode()).isNotNull();
            assertThat(firstFormula.getCode()).isNotEmpty();
            assertThat(firstFormula.getFormula()).isNotNull();
        }

        @Test
        @DisplayName("should parse testing formula with complex expression")
        void shouldParseTestingFormulaWithComplexExpression() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            // Find a formula containing multiple connectors
            Vector<TestingFormula> testingFormulae = logic.getTestingFormulae();

            boolean foundComplexFormula = false;
            for (TestingFormula tf : testingFormulae) {
                String code = tf.getCode();
                if (code.contains("and") && code.contains("nec")) {
                    foundComplexFormula = true;
                    break;
                }
            }
            assertThat(foundComplexFormula).isTrue();
        }

        @Test
        @DisplayName("should parse testing formula with comment")
        void shouldParseTestingFormulaWithComment() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4Minimal");

            Vector<TestingFormula> testingFormulae = logic.getTestingFormulae();

            // The second formula in S4Minimal has a comment
            boolean foundFormulaWithComment = false;
            for (TestingFormula tf : testingFormulae) {
                String comment = tf.getComment();
                if (comment != null && !comment.isEmpty()) {
                    foundFormulaWithComment = true;
                    break;
                }
            }
            assertThat(foundFormulaWithComment).isTrue();
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("should handle missing file")
        void shouldHandleMissingFile() {
            assertThatThrownBy(() -> parser.parseLogic("nonexistent_file.xml"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("FileNotFoundException");
        }

        @Test
        @DisplayName("should throw exception for malformed XML")
        void shouldThrowExceptionForMalformedXML() throws IOException {
            File malformedXml = tempDir.resolve("malformed.xml").toFile();
            try (FileWriter writer = new FileWriter(malformedXml)) {
                writer.write("<?xml version=\"1.0\"?>\n<logic><unclosed>");
            }

            // Malformed XML should throw some exception - either ParseException or a Xerces-related exception
            assertThatThrownBy(() -> parser.parseLogic(malformedXml.getAbsolutePath()))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("should handle missing logic tag")
        void shouldHandleMissingLogicTag() throws IOException {
            File noLogicTag = tempDir.resolve("no_logic.xml").toFile();
            try (FileWriter writer = new FileWriter(noLogicTag)) {
                writer.write("<?xml version=\"1.0\"?>\n<other>content</other>");
            }

            assertThatThrownBy(() -> parser.parseLogic(noLogicTag.getAbsolutePath()))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("No 'logic' tag element");
        }

        @Test
        @DisplayName("should throw exception for invalid arity value")
        void shouldThrowExceptionForInvalidArity() throws IOException {
            File invalidArity = tempDir.resolve("invalid_arity.xml").toFile();
            try (FileWriter writer = new FileWriter(invalidArity)) {
                writer.write("<?xml version=\"1.0\"?>\n" +
                        "<logic>\n" +
                        "  <parser-version>2.1</parser-version>\n" +
                        "  <description>Test</description>\n" +
                        "  <connector>\n" +
                        "    <connector-name>test</connector-name>\n" +
                        "    <arity>notanumber</arity>\n" +
                        "    <associative>false</associative>\n" +
                        "    <output-format>test</output-format>\n" +
                        "    <priority>1</priority>\n" +
                        "    <connector-comment/>\n" +
                        "  </connector>\n" +
                        "  <strategy>\n" +
                        "    <strategy-name>Main</strategy-name>\n" +
                        "    <strategy-code></strategy-code>\n" +
                        "    <strategy-comment/>\n" +
                        "  </strategy>\n" +
                        "  <main-strategy>Main</main-strategy>\n" +
                        "</logic>");
            }

            // Invalid arity should throw ParseException
            assertThatThrownBy(() -> parser.parseLogic(invalidArity.getAbsolutePath()))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should throw exception for missing main strategy tag")
        void shouldThrowExceptionForMissingMainStrategy() throws IOException {
            File noMainStrategy = tempDir.resolve("no_main_strategy.xml").toFile();
            try (FileWriter writer = new FileWriter(noMainStrategy)) {
                writer.write("<?xml version=\"1.0\"?>\n" +
                        "<logic>\n" +
                        "  <strategy>\n" +
                        "    <strategy-name>Test</strategy-name>\n" +
                        "    <strategy-code></strategy-code>\n" +
                        "  </strategy>\n" +
                        "</logic>");
            }

            // Missing main-strategy tag causes NullPointerException when accessing the tag
            assertThatThrownBy(() -> parser.parseLogic(noMainStrategy.getAbsolutePath()))
                    .isInstanceOf(Exception.class);
        }

        @Test
        @DisplayName("should throw exception for incorrect main strategy reference")
        void shouldThrowExceptionForIncorrectMainStrategyReference() throws IOException {
            File incorrectMainStrategy = tempDir.resolve("incorrect_main_strategy.xml").toFile();
            try (FileWriter writer = new FileWriter(incorrectMainStrategy)) {
                writer.write("<?xml version=\"1.0\"?>\n" +
                        "<logic>\n" +
                        "  <parser-version>2.1</parser-version>\n" +
                        "  <description>Test</description>\n" +
                        "  <strategy>\n" +
                        "    <strategy-name>Test</strategy-name>\n" +
                        "    <strategy-code></strategy-code>\n" +
                        "    <strategy-comment/>\n" +
                        "  </strategy>\n" +
                        "  <main-strategy>NonExistent</main-strategy>\n" +
                        "</logic>");
            }

            assertThatThrownBy(() -> parser.parseLogic(incorrectMainStrategy.getAbsolutePath()))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("main strategy");
        }

        @Test
        @DisplayName("should throw exception for duplicated connector names")
        void shouldThrowExceptionForDuplicatedConnectorNames() throws IOException {
            File duplicatedConnector = tempDir.resolve("duplicated_connector.xml").toFile();
            try (FileWriter writer = new FileWriter(duplicatedConnector)) {
                writer.write("<?xml version=\"1.0\"?>\n" +
                        "<logic>\n" +
                        "  <parser-version>2.1</parser-version>\n" +
                        "  <description>Test</description>\n" +
                        "  <connector>\n" +
                        "    <connector-name>not</connector-name>\n" +
                        "    <arity>1</arity>\n" +
                        "    <associative>false</associative>\n" +
                        "    <output-format>~_</output-format>\n" +
                        "    <priority>5</priority>\n" +
                        "    <connector-comment/>\n" +
                        "  </connector>\n" +
                        "  <connector>\n" +
                        "    <connector-name>not</connector-name>\n" +
                        "    <arity>1</arity>\n" +
                        "    <associative>false</associative>\n" +
                        "    <output-format>!_</output-format>\n" +
                        "    <priority>5</priority>\n" +
                        "    <connector-comment/>\n" +
                        "  </connector>\n" +
                        "  <strategy>\n" +
                        "    <strategy-name>Main</strategy-name>\n" +
                        "    <strategy-code></strategy-code>\n" +
                        "    <strategy-comment/>\n" +
                        "  </strategy>\n" +
                        "  <main-strategy>Main</main-strategy>\n" +
                        "</logic>");
            }

            assertThatThrownBy(() -> parser.parseLogic(duplicatedConnector.getAbsolutePath()))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("connector");
        }

        @Test
        @DisplayName("should throw exception for unknown condition name")
        void shouldThrowExceptionForUnknownConditionName() throws IOException {
            File unknownCondition = tempDir.resolve("unknown_condition.xml").toFile();
            try (FileWriter writer = new FileWriter(unknownCondition)) {
                writer.write("<?xml version=\"1.0\"?>\n" +
                        "<logic>\n" +
                        "  <parser-version>2.1</parser-version>\n" +
                        "  <description>Test</description>\n" +
                        "  <rule>\n" +
                        "    <rule-name>TestRule</rule-name>\n" +
                        "    <condition>\n" +
                        "      <condition-name>unknownCondition</condition-name>\n" +
                        "      <parameter>w</parameter>\n" +
                        "    </condition>\n" +
                        "    <rule-comment/>\n" +
                        "  </rule>\n" +
                        "  <strategy>\n" +
                        "    <strategy-name>Main</strategy-name>\n" +
                        "    <strategy-code></strategy-code>\n" +
                        "    <strategy-comment/>\n" +
                        "  </strategy>\n" +
                        "  <main-strategy>Main</main-strategy>\n" +
                        "</logic>");
            }

            assertThatThrownBy(() -> parser.parseLogic(unknownCondition.getAbsolutePath()))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("condition");
        }

        @Test
        @DisplayName("should throw exception for unknown action name")
        void shouldThrowExceptionForUnknownActionName() throws IOException {
            File unknownAction = tempDir.resolve("unknown_action.xml").toFile();
            try (FileWriter writer = new FileWriter(unknownAction)) {
                writer.write("<?xml version=\"1.0\"?>\n" +
                        "<logic>\n" +
                        "  <parser-version>2.1</parser-version>\n" +
                        "  <description>Test</description>\n" +
                        "  <rule>\n" +
                        "    <rule-name>TestRule</rule-name>\n" +
                        "    <action>\n" +
                        "      <action-name>unknownAction</action-name>\n" +
                        "      <parameter>w</parameter>\n" +
                        "    </action>\n" +
                        "    <rule-comment/>\n" +
                        "  </rule>\n" +
                        "  <strategy>\n" +
                        "    <strategy-name>Main</strategy-name>\n" +
                        "    <strategy-code></strategy-code>\n" +
                        "    <strategy-comment/>\n" +
                        "  </strategy>\n" +
                        "  <main-strategy>Main</main-strategy>\n" +
                        "</logic>");
            }

            assertThatThrownBy(() -> parser.parseLogic(unknownAction.getAbsolutePath()))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("action");
        }
    }

    @Nested
    @DisplayName("Full Logic Parsing")
    class FullLogicParsing {

        @Test
        @DisplayName("should parse complete Monomodal-K logic")
        void shouldParseCompleteLogic() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic).isNotNull();
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
            assertThat(logic.getStrategies()).isNotEmpty();
            assertThat(logic.getTestingFormulae()).isNotEmpty();
            assertThat(logic.getMainStrategyName()).isNotNull();
        }

        @Test
        @DisplayName("should preserve logic name from file name")
        void shouldPreserveLogicName() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getName()).isEqualTo("Monomodal-K");
        }

        @Test
        @DisplayName("should preserve logic description")
        void shouldPreserveLogicDescription() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Monomodal-K");

            assertThat(logic.getDescription()).isNotNull();
            assertThat(logic.getDescription()).contains("modal logic K");
        }

        @Test
        @DisplayName("should preserve all components in S4Minimal")
        void shouldPreserveAllComponents() throws ParseException {
            Logic logic = TestFixtures.loadLogic("S4Minimal");

            // Connectors
            assertThat(logic.getConnectors()).hasSize(3);

            // Rules
            assertThat(logic.getRules()).hasSizeGreaterThanOrEqualTo(8);
            assertThat(logic.getRule("Stop")).isNotNull();
            assertThat(logic.getRule("NotNot")).isNotNull();
            assertThat(logic.getRule("And")).isNotNull();
            assertThat(logic.getRule("NotAnd")).isNotNull();
            assertThat(logic.getRule("loopTest")).isNotNull();

            // Strategies
            assertThat(logic.getStrategies()).hasSizeGreaterThanOrEqualTo(2);
            assertThat(logic.getStrategy("CPLStrategy")).isNotNull();
            assertThat(logic.getStrategy("S4Strategy")).isNotNull();

            // Main strategy
            assertThat(logic.getMainStrategyName()).isEqualTo("S4Strategy");

            // Testing formulas
            assertThat(logic.getTestingFormulae()).hasSizeGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("should parse all predefined logics without errors")
        void shouldParseAllPredefinedLogicsWithoutErrors() {
            String[] logicNames = TestFixtures.ALL_LOGIC_NAMES;

            for (String logicName : logicNames) {
                assertThatCode(() -> TestFixtures.loadLogic(logicName))
                        .as("Logic '%s' should parse without errors", logicName)
                        .doesNotThrowAnyException();
            }
        }

        @Test
        @DisplayName("should parse Classical Propositional Logic")
        void shouldParseClassicalPropositionalLogic() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Classical-Propositional-Logic");

            assertThat(logic.getName()).isEqualTo("Classical-Propositional-Logic");
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
            assertThat(logic.getStrategies()).isNotEmpty();
        }

        @Test
        @DisplayName("should parse multimodal logic Multimodal-Kn")
        void shouldParseMultimodalLogic() throws ParseException {
            Logic logic = TestFixtures.loadLogic("Multimodal-Kn");

            assertThat(logic.getName()).isEqualTo("Multimodal-Kn");
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should parse PDL (Propositional Dynamic Logic)")
        void shouldParsePDL() throws ParseException {
            Logic logic = TestFixtures.loadLogic("PDL");

            assertThat(logic.getName()).isEqualTo("PDL");
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
        }

        @Test
        @DisplayName("should parse LTL (Linear Temporal Logic)")
        void shouldParseLTL() throws ParseException {
            Logic logic = TestFixtures.loadLogic("LTL");

            assertThat(logic.getName()).isEqualTo("LTL");
            assertThat(logic.getConnectors()).isNotEmpty();
            assertThat(logic.getRules()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("Save Logic")
    class SaveLogic {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("should save and reload logic preserving connectors")
        void shouldSaveAndReloadLogicPreservingConnectors() throws ParseException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            File savedFile = tempDir.resolve("saved_logic.xml").toFile();

            parser.saveLogic(original, savedFile.getAbsolutePath());
            Logic reloaded = parser.parseLogic(savedFile.getAbsolutePath());

            assertThat(reloaded.getConnectors()).hasSameSizeAs(original.getConnectors());
            for (Connector origConn : original.getConnectors()) {
                Connector reloadedConn = reloaded.getConnector(origConn.getName());
                assertThat(reloadedConn).isNotNull();
                assertThat(reloadedConn.getArity()).isEqualTo(origConn.getArity());
                assertThat(reloadedConn.getPriority()).isEqualTo(origConn.getPriority());
                assertThat(reloadedConn.isAssociative()).isEqualTo(origConn.isAssociative());
            }
        }

        @Test
        @DisplayName("should save and reload logic preserving rules")
        void shouldSaveAndReloadLogicPreservingRules() throws ParseException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            File savedFile = tempDir.resolve("saved_logic.xml").toFile();

            parser.saveLogic(original, savedFile.getAbsolutePath());
            Logic reloaded = parser.parseLogic(savedFile.getAbsolutePath());

            assertThat(reloaded.getRules()).hasSameSizeAs(original.getRules());
            for (Rule origRule : original.getRules()) {
                Rule reloadedRule = reloaded.getRule(origRule.getName());
                assertThat(reloadedRule).isNotNull();
                assertThat(reloadedRule.getConditions()).hasSameSizeAs(origRule.getConditions());
                assertThat(reloadedRule.getActions()).hasSameSizeAs(origRule.getActions());
            }
        }

        @Test
        @DisplayName("should save and reload logic preserving strategies")
        void shouldSaveAndReloadLogicPreservingStrategies() throws ParseException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            File savedFile = tempDir.resolve("saved_logic.xml").toFile();

            parser.saveLogic(original, savedFile.getAbsolutePath());
            Logic reloaded = parser.parseLogic(savedFile.getAbsolutePath());

            assertThat(reloaded.getStrategies()).hasSameSizeAs(original.getStrategies());
            assertThat(reloaded.getMainStrategyName()).isEqualTo(original.getMainStrategyName());
        }

        @Test
        @DisplayName("should save and reload logic preserving testing formulas")
        void shouldSaveAndReloadLogicPreservingTestingFormulas() throws ParseException {
            Logic original = TestFixtures.loadLogic("Monomodal-K");
            File savedFile = tempDir.resolve("saved_logic.xml").toFile();

            parser.saveLogic(original, savedFile.getAbsolutePath());
            Logic reloaded = parser.parseLogic(savedFile.getAbsolutePath());

            assertThat(reloaded.getTestingFormulae()).hasSameSizeAs(original.getTestingFormulae());
        }
    }
}
