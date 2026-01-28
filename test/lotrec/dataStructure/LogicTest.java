package lotrec.dataStructure;

import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.tableau.Rule;
import lotrec.process.Strategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Characterization tests for Logic data structure.
 * These tests capture the current behavior of the Logic class.
 */
@DisplayName("Logic")
class LogicTest {

    private Logic logic;

    @BeforeEach
    void setUp() {
        logic = new Logic();
    }

    /**
     * Helper method to create a Rule with a given name.
     * Rule class constructor requires (String, boolean) signature.
     */
    private Rule createRule(String name) {
        Rule rule = new Rule(name, false);
        return rule;
    }

    @Nested
    @DisplayName("Initialization")
    class Initialization {

        @Test
        @DisplayName("has default name")
        void hasDefaultName() {
            assertThat(logic.getName()).isEqualTo("Untitled Logic");
        }

        @Test
        @DisplayName("has empty connectors list")
        void hasEmptyConnectorsList() {
            assertThat(logic.getConnectors()).isNotNull();
            assertThat(logic.getConnectors()).isEmpty();
        }

        @Test
        @DisplayName("has empty rules list")
        void hasEmptyRulesList() {
            assertThat(logic.getRules()).isNotNull();
            assertThat(logic.getRules()).isEmpty();
        }

        @Test
        @DisplayName("has empty strategies list")
        void hasEmptyStrategiesList() {
            assertThat(logic.getStrategies()).isNotNull();
            assertThat(logic.getStrategies()).isEmpty();
        }

        @Test
        @DisplayName("getNewEmptyLogic creates logic with default strategy")
        void getNewEmptyLogicCreatesDefault() {
            Logic newLogic = Logic.getNewEmptyLogic();

            assertThat(newLogic.getName()).isEqualTo("New Logic");
            assertThat(newLogic.getStrategies()).hasSize(1);
            assertThat(newLogic.getMainStrategyName()).isEqualTo("DefaultStrategy");
        }
    }

    @Nested
    @DisplayName("Connector Management")
    class ConnectorManagement {

        @Test
        @DisplayName("addConnector adds to list")
        void addConnectorAddsToList() {
            Connector conn = new Connector("not", 1, "~_");

            logic.addConnector(conn);

            assertThat(logic.getConnectors()).containsExactly(conn);
        }

        @Test
        @DisplayName("removeConnector removes from list")
        void removeConnectorRemovesFromList() {
            Connector conn = new Connector("not", 1, "~_");
            logic.addConnector(conn);

            logic.removeConnector(conn);

            assertThat(logic.getConnectors()).isEmpty();
        }

        @Test
        @DisplayName("getConnector finds by name")
        void getConnectorFindsByName() {
            Connector conn = new Connector("not", 1, "~_");
            logic.addConnector(conn);

            Connector found = logic.getConnector("not");

            assertThat(found).isSameAs(conn);
        }

        @Test
        @DisplayName("getConnector returns null for unknown name")
        void getConnectorReturnsNullForUnknown() {
            Connector found = logic.getConnector("unknown");

            assertThat(found).isNull();
        }
    }

    @Nested
    @DisplayName("Rule Management")
    class RuleManagement {

        @Test
        @DisplayName("addRule adds to list")
        void addRuleAddsToList() {
            Rule rule = createRule("TestRule");

            logic.addRule(rule);

            assertThat(logic.getRules()).containsExactly(rule);
        }

        @Test
        @DisplayName("addRule at index inserts at position")
        void addRuleAtIndexInsertsAtPosition() {
            Rule rule1 = createRule("Rule1");
            Rule rule2 = createRule("Rule2");
            Rule rule3 = createRule("Rule3");
            logic.addRule(rule1);
            logic.addRule(rule3);

            logic.addRule(1, rule2);

            assertThat(logic.getRules()).containsExactly(rule1, rule2, rule3);
        }

        @Test
        @DisplayName("removeRule removes from list")
        void removeRuleRemovesFromList() {
            Rule rule = createRule("TestRule");
            logic.addRule(rule);

            logic.removeRule(rule);

            assertThat(logic.getRules()).isEmpty();
        }

        @Test
        @DisplayName("getRule finds by name")
        void getRuleFindsByName() {
            Rule rule = createRule("TestRule");
            logic.addRule(rule);

            Rule found = logic.getRule("TestRule");

            assertThat(found).isSameAs(rule);
        }

        @Test
        @DisplayName("getRule returns null for unknown name")
        void getRuleReturnsNullForUnknown() {
            Rule found = logic.getRule("unknown");

            assertThat(found).isNull();
        }

        @Test
        @DisplayName("isRuleName returns true for existing rule")
        void isRuleNameReturnsTrueForExisting() {
            Rule rule = createRule("TestRule");
            logic.addRule(rule);

            assertThat(logic.isRuleName("TestRule")).isTrue();
        }

        @Test
        @DisplayName("isRuleName returns false for unknown")
        void isRuleNameReturnsFalseForUnknown() {
            assertThat(logic.isRuleName("unknown")).isFalse();
        }
    }

    @Nested
    @DisplayName("Strategy Management")
    class StrategyManagement {

        @Test
        @DisplayName("addStrategy adds to list")
        void addStrategyAddsToList() {
            Strategy strategy = new Strategy();
            strategy.setWorkerName("TestStrategy");

            logic.addStrategy(strategy);

            assertThat(logic.getStrategies()).containsExactly(strategy);
        }

        @Test
        @DisplayName("removeStrategy removes from list")
        void removeStrategyRemovesFromList() {
            Strategy strategy = new Strategy();
            strategy.setWorkerName("TestStrategy");
            logic.addStrategy(strategy);

            logic.removeStrategy(strategy);

            assertThat(logic.getStrategies()).isEmpty();
        }

        @Test
        @DisplayName("getStrategy finds by name")
        void getStrategyFindsByName() {
            Strategy strategy = new Strategy();
            strategy.setWorkerName("TestStrategy");
            logic.addStrategy(strategy);

            Strategy found = logic.getStrategy("TestStrategy");

            assertThat(found).isSameAs(strategy);
        }

        @Test
        @DisplayName("getStrategy returns null for unknown name")
        void getStrategyReturnsNullForUnknown() {
            Strategy found = logic.getStrategy("unknown");

            assertThat(found).isNull();
        }

        @Test
        @DisplayName("isStrategyName returns true for existing strategy")
        void isStrategyNameReturnsTrueForExisting() {
            Strategy strategy = new Strategy();
            strategy.setWorkerName("TestStrategy");
            logic.addStrategy(strategy);

            assertThat(logic.isStrategyName("TestStrategy")).isTrue();
        }

        @Test
        @DisplayName("isStrategyName returns false for unknown")
        void isStrategyNameReturnsFalseForUnknown() {
            assertThat(logic.isStrategyName("unknown")).isFalse();
        }
    }

    @Nested
    @DisplayName("Routine Names")
    class RoutineNames {

        @Test
        @DisplayName("isRoutineName returns true for allRules")
        void isRoutineNameAllRules() {
            assertThat(logic.isRoutineName("allRules")).isTrue();
        }

        @Test
        @DisplayName("isRoutineName returns true for firstRule")
        void isRoutineNameFirstRule() {
            assertThat(logic.isRoutineName("firstRule")).isTrue();
        }

        @Test
        @DisplayName("isRoutineName returns true for repeat")
        void isRoutineNameRepeat() {
            assertThat(logic.isRoutineName("repeat")).isTrue();
        }

        @Test
        @DisplayName("isRoutineName returns false for unknown")
        void isRoutineNameReturnsFalseForUnknown() {
            assertThat(logic.isRoutineName("unknown")).isFalse();
        }
    }

    @Nested
    @DisplayName("Equality")
    class Equality {

        @Test
        @DisplayName("equals returns true for same name")
        void equalsReturnsTrueForSameName() {
            Logic logic1 = new Logic();
            logic1.setName("TestLogic");

            Logic logic2 = new Logic();
            logic2.setName("TestLogic");

            assertThat(logic1).isEqualTo(logic2);
        }

        @Test
        @DisplayName("equals returns false for different name")
        void equalsReturnsFalseForDifferentName() {
            Logic logic1 = new Logic();
            logic1.setName("Logic1");

            Logic logic2 = new Logic();
            logic2.setName("Logic2");

            assertThat(logic1).isNotEqualTo(logic2);
        }

        @Test
        @DisplayName("hashCode is consistent with equals")
        void hashCodeIsConsistentWithEquals() {
            Logic logic1 = new Logic();
            logic1.setName("TestLogic");

            Logic logic2 = new Logic();
            logic2.setName("TestLogic");

            assertThat(logic1.hashCode()).isEqualTo(logic2.hashCode());
        }
    }
}
