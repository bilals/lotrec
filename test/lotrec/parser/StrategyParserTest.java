package lotrec.parser;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.condition.ExpressionCondition;
import lotrec.dataStructure.expression.StringSchemeVariable;
import lotrec.dataStructure.expression.ConstantExpression;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive tests for strategy parsing in OldiesTokenizer.
 * Tests cover the parseStrategy() method and recognizeRoutine() method behavior.
 */
@DisplayName("Strategy Parser")
class StrategyParserTest {

    private Logic logic;
    private OldiesTokenizer tokenizer;

    @BeforeEach
    void setUp() {
        logic = new Logic();
        logic.setName("TestLogic");

        // Add standard modal logic connectors
        logic.addConnector(new Connector("not", 1, "~_"));
        logic.addConnector(new Connector("and", 2, "_&_"));
        logic.addConnector(new Connector("or", 2, "_|_"));
        logic.addConnector(new Connector("nec", 1, "[]_"));
        logic.addConnector(new Connector("pos", 1, "<>_"));

        // Add test rules to the logic
        addTestRule("Rule1");
        addTestRule("Rule2");
        addTestRule("Rule3");
        addTestRule("Stop");
        addTestRule("And");
        addTestRule("Or");
        addTestRule("NotNot");
        addTestRule("Pos");
        addTestRule("Nec");

        tokenizer = new OldiesTokenizer(logic);
        tokenizer.initializeTokenizerAndProps();
    }

    /**
     * Helper method to add a simple test rule to the logic.
     */
    private void addTestRule(String ruleName) {
        Rule rule = new Rule();
        rule.setName(ruleName);
        // Add a simple condition so the rule can create an EventMachine
        ExpressionCondition condition = new ExpressionCondition(
                new StringSchemeVariable("w"),
                new ConstantExpression("P")
        );
        rule.addCondition(condition);
        logic.addRule(rule);
    }

    /**
     * Helper method to add a strategy to the logic.
     */
    private void addTestStrategy(String strategyName, String strategyCode) {
        Strategy strategy = new Strategy();
        strategy.setWorkerName(strategyName);
        strategy.setCode(strategyCode);
        logic.addStrategy(strategy);
    }

    @Nested
    @DisplayName("Repeat Block Parsing")
    class RepeatBlockParsing {

        @Test
        @DisplayName("should parse repeat block with single rule")
        void shouldParseRepeatBlock() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("repeat Rule1 end");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);
            assertThat(strategy.getWorkers().get(0)).isInstanceOf(Repeat.class);

            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
        }

        @Test
        @DisplayName("should parse empty repeat block")
        void shouldParseEmptyRepeat() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("repeat end");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);
            assertThat(strategy.getWorkers().get(0)).isInstanceOf(Repeat.class);

            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).isEmpty();
        }

        @Test
        @DisplayName("should parse repeat block with multiple rules")
        void shouldParseRepeatWithMultipleRules() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("repeat Rule1 Rule2 Rule3 end");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);

            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("FirstRule Block Parsing")
    class FirstRuleBlockParsing {

        @Test
        @DisplayName("should parse firstRule block with single rule")
        void shouldParseFirstRuleBlock() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("firstRule Rule1 end");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);
            assertThat(strategy.getWorkers().get(0)).isInstanceOf(FirstRule.class);
        }

        @Test
        @DisplayName("should parse firstRule block with multiple rules")
        void shouldParseMultipleRulesInFirstRule() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("firstRule Rule1 Rule2 Rule3 end");

            assertThat(strategy).isNotNull();
            FirstRule firstRule = (FirstRule) strategy.getWorkers().get(0);
            assertThat(firstRule.getWorkers()).hasSize(3);
        }

        @Test
        @DisplayName("should parse empty firstRule block")
        void shouldParseEmptyFirstRule() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("firstRule end");

            assertThat(strategy).isNotNull();
            FirstRule firstRule = (FirstRule) strategy.getWorkers().get(0);
            assertThat(firstRule.getWorkers()).isEmpty();
        }
    }

    @Nested
    @DisplayName("AllRules Block Parsing")
    class AllRulesBlockParsing {

        @Test
        @DisplayName("should parse allRules block with single rule")
        void shouldParseAllRulesBlock() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("allRules Rule1 end");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);
            assertThat(strategy.getWorkers().get(0)).isInstanceOf(AllRules.class);
        }

        @Test
        @DisplayName("should parse allRules block with multiple rules")
        void shouldParseMultipleRulesInAllRules() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("allRules Rule1 Rule2 Rule3 end");

            assertThat(strategy).isNotNull();
            AllRules allRules = (AllRules) strategy.getWorkers().get(0);
            assertThat(allRules.getWorkers()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("Nested Block Parsing")
    class NestedBlockParsing {

        @Test
        @DisplayName("should parse nested blocks - repeat containing firstRule")
        void shouldParseNestedBlocks() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("repeat firstRule Rule1 Rule2 end end");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);

            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
            assertThat(repeat.getWorkers().get(0)).isInstanceOf(FirstRule.class);

            FirstRule firstRule = (FirstRule) repeat.getWorkers().get(0);
            assertThat(firstRule.getWorkers()).hasSize(2);
        }

        @Test
        @DisplayName("should parse deeply nested strategies with three levels")
        void shouldParseDeeplyNestedStrategies() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat allRules firstRule Rule1 end Rule2 end end"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);

            AllRules allRules = (AllRules) repeat.getWorkers().get(0);
            assertThat(allRules.getWorkers()).hasSize(2);
            assertThat(allRules.getWorkers().get(0)).isInstanceOf(FirstRule.class);
        }

        @Test
        @DisplayName("should parse multiple nested blocks at same level")
        void shouldParseMultipleNestedBlocksAtSameLevel() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat firstRule Rule1 end allRules Rule2 end end"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(2);
            assertThat(repeat.getWorkers().get(0)).isInstanceOf(FirstRule.class);
            assertThat(repeat.getWorkers().get(1)).isInstanceOf(AllRules.class);
        }
    }

    @Nested
    @DisplayName("Strategy Reference Parsing")
    class StrategyReferenceParsing {

        @Test
        @DisplayName("should parse strategy reference by name")
        void shouldParseStrategyReference() throws ParseException {
            // Add a referenced strategy
            addTestStrategy("SubStrategy", "firstRule Rule1 end");

            Strategy strategy = tokenizer.parseStrategy("repeat SubStrategy end");

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
            // The strategy reference is expanded into its parsed form
            assertThat(repeat.getWorkers().get(0)).isInstanceOf(Strategy.class);
        }

        @Test
        @DisplayName("should parse strategy reference at top level")
        void shouldParseStrategyReferenceAtTopLevel() throws ParseException {
            addTestStrategy("TopStrategy", "allRules Rule1 Rule2 end");

            Strategy strategy = tokenizer.parseStrategy("TopStrategy");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);
            assertThat(strategy.getWorkers().get(0)).isInstanceOf(Strategy.class);
        }

        @Test
        @DisplayName("should preserve worker name for referenced strategy")
        void shouldPreserveWorkerNameForStrategy() throws ParseException {
            addTestStrategy("NamedStrategy", "firstRule Rule1 end");

            Strategy strategy = tokenizer.parseStrategy("repeat NamedStrategy end");

            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            Strategy referencedStrategy = (Strategy) repeat.getWorkers().get(0);
            assertThat(referencedStrategy.getWorkerName()).isEqualTo("NamedStrategy");
        }
    }

    @Nested
    @DisplayName("Whitespace Handling")
    class WhitespaceHandling {

        @Test
        @DisplayName("should handle extra whitespace between tokens")
        void shouldHandleWhitespace() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat    firstRule   Rule1    end    end"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
        }

        @Test
        @DisplayName("should handle newlines in strategy code")
        void shouldHandleNewlines() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat\n  firstRule\n    Rule1\n  end\nend"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
        }

        @Test
        @DisplayName("should handle tabs in strategy code")
        void shouldHandleTabs() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat\t\tfirstRule\tRule1\tend\tend"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Comment Handling")
    class CommentHandling {

        @Test
        @DisplayName("should parse strategy with line comments")
        void shouldParseWithComments() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat // This is a comment\n Rule1 end"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
        }

        @Test
        @DisplayName("should parse strategy with block comments")
        void shouldParseWithBlockComments() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat /* block comment */ firstRule Rule1 end end"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Error Handling")
    class ErrorHandling {

        @Test
        @DisplayName("should reject missing end keyword")
        void shouldRejectInvalidSyntax() {
            assertThatThrownBy(() -> tokenizer.parseStrategy("repeat Rule1"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should reject unknown identifier")
        void shouldRejectUnknownIdentifier() {
            assertThatThrownBy(() -> tokenizer.parseStrategy("repeat UnknownRule end"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("UnknownRule");
        }

        @Test
        @DisplayName("should reject malformed end - extra end keyword")
        void shouldHandleMalformedEnd() throws ParseException {
            // Extra 'end' at top level is treated as unknown identifier
            assertThatThrownBy(() -> tokenizer.parseStrategy("repeat Rule1 end end"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should reject nested block with missing end")
        void shouldRejectNestedMissingEnd() {
            assertThatThrownBy(() -> tokenizer.parseStrategy("repeat firstRule Rule1 end"))
                    .isInstanceOf(ParseException.class);
        }
    }

    @Nested
    @DisplayName("Complex Strategy Parsing")
    class ComplexStrategyParsing {

        @Test
        @DisplayName("should parse complex strategy from predefined logics")
        void shouldParseComplexStrategy() throws ParseException {
            // Based on Monomodal-K.xml strategy pattern
            Strategy strategy = tokenizer.parseStrategy(
                    "repeat firstRule Stop NotNot And Or end end"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            FirstRule firstRule = (FirstRule) repeat.getWorkers().get(0);
            assertThat(firstRule.getWorkers()).hasSize(4);
        }

        @Test
        @DisplayName("should parse multi-level strategy similar to K logic")
        void shouldParseKStyleStrategy() throws ParseException {
            // Add a sub-strategy like CPLStrategy
            addTestStrategy("CPLStrategy", "repeat firstRule Stop NotNot And Or end end");

            Strategy strategy = tokenizer.parseStrategy(
                    "repeat CPLStrategy Pos Nec end"
            );

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(3);
            // First worker is the expanded CPLStrategy
            assertThat(repeat.getWorkers().get(0)).isInstanceOf(Strategy.class);
        }

        @Test
        @DisplayName("should parse strategy with rules only at top level")
        void shouldParseRulesAtTopLevel() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("Rule1 Rule2 Rule3");

            assertThat(strategy).isNotNull();
            // Rules at top level are added as EventMachines
            assertThat(strategy.getWorkers()).hasSize(3);
            for (AbstractWorker worker : strategy.getWorkers()) {
                assertThat(worker).isInstanceOf(EventMachine.class);
            }
        }
    }

    @Nested
    @DisplayName("Worker Name Setting")
    class WorkerNameSetting {

        @Test
        @DisplayName("should set worker name for Repeat routine")
        void shouldSetWorkerNameForRepeat() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("repeat Rule1 end");

            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkerName()).isEqualTo("Repeat");
        }

        @Test
        @DisplayName("should set worker name for FirstRule routine")
        void shouldSetWorkerNameForFirstRule() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("firstRule Rule1 end");

            FirstRule firstRule = (FirstRule) strategy.getWorkers().get(0);
            assertThat(firstRule.getWorkerName()).isEqualTo("FirstRule");
        }

        @Test
        @DisplayName("should set worker name for AllRules routine")
        void shouldSetWorkerNameForAllRules() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("allRules Rule1 end");

            AllRules allRules = (AllRules) strategy.getWorkers().get(0);
            assertThat(allRules.getWorkerName()).isEqualTo("AllRules");
        }

        @Test
        @DisplayName("should preserve rule name as worker name for EventMachine")
        void shouldPreserveRuleNameAsWorkerName() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("Rule1");

            EventMachine eventMachine = (EventMachine) strategy.getWorkers().get(0);
            assertThat(eventMachine.getWorkerName()).isEqualTo("Rule1");
        }
    }

    @Nested
    @DisplayName("Empty and Edge Cases")
    class EmptyAndEdgeCases {

        @Test
        @DisplayName("should parse empty strategy code")
        void shouldParseEmptyStrategy() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).isEmpty();
        }

        @Test
        @DisplayName("should parse whitespace-only strategy code")
        void shouldParseWhitespaceOnlyStrategy() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("   \n\t  ");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).isEmpty();
        }

        @Test
        @DisplayName("should parse single rule without blocks")
        void shouldParseSingleRule() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("Rule1");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);
            assertThat(strategy.getWorkers().get(0)).isInstanceOf(EventMachine.class);
        }
    }

    @Nested
    @DisplayName("ApplyOnce Parsing")
    class ApplyOnceParsing {

        @Test
        @DisplayName("should parse applyOnce rule reference")
        void shouldParseApplyOnce() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("repeat applyOnce Rule1 end");

            assertThat(strategy).isNotNull();
            Repeat repeat = (Repeat) strategy.getWorkers().get(0);
            assertThat(repeat.getWorkers()).hasSize(1);
            EventMachine machine = (EventMachine) repeat.getWorkers().get(0);
            assertThat(machine.isApplyOnOneOccurence()).isTrue();
        }

        @Test
        @DisplayName("should reject applyOnce without rule name")
        void shouldRejectApplyOnceWithoutRule() {
            assertThatThrownBy(() -> tokenizer.parseStrategy("repeat applyOnce end"))
                    .isInstanceOf(ParseException.class)
                    .hasMessageContaining("Rule name is expected after applyOnce");
        }

        @Test
        @DisplayName("should reject applyOnce with invalid rule name")
        void shouldRejectApplyOnceWithInvalidRule() {
            assertThatThrownBy(() -> tokenizer.parseStrategy("repeat applyOnce UnknownRule end"))
                    .isInstanceOf(ParseException.class);
        }

        @Test
        @DisplayName("should parse applyOnce at top level")
        void shouldParseApplyOnceAtTopLevel() throws ParseException {
            Strategy strategy = tokenizer.parseStrategy("applyOnce Rule1");

            assertThat(strategy).isNotNull();
            assertThat(strategy.getWorkers()).hasSize(1);
            EventMachine machine = (EventMachine) strategy.getWorkers().get(0);
            assertThat(machine.isApplyOnOneOccurence()).isTrue();
        }
    }
}
