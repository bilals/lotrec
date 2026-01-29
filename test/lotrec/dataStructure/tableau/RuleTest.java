package lotrec.dataStructure.tableau;

import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.expression.ConstantExpression;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.ExpressionWithSubExpressions;
import lotrec.dataStructure.expression.StringSchemeVariable;
import lotrec.dataStructure.tableau.action.AddExpressionAction;
import lotrec.dataStructure.tableau.action.MarkAction;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.dataStructure.tableau.condition.ExpressionCondition;
import lotrec.dataStructure.tableau.condition.MarkCondition;
import lotrec.process.AbstractAction;
import lotrec.process.EventMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Enumeration;
import java.util.Vector;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for the Rule class which keeps conditions and actions,
 * and can create an event machine to manage rule application.
 */
@DisplayName("Rule")
class RuleTest {

    private Rule rule;

    @BeforeEach
    void setUp() {
        rule = new Rule();
    }

    @Nested
    @DisplayName("Constructor and Naming")
    class ConstructorAndNaming {

        @Test
        @DisplayName("should create rule with default constructor")
        void shouldCreateWithDefaultConstructor() {
            Rule newRule = new Rule();

            assertThat(newRule.getName()).isEqualTo("NewRule");
            assertThat(newRule.getConditions()).isEmpty();
            assertThat(newRule.getActions()).isEmpty();
        }

        @Test
        @DisplayName("should create rule with name and commutative flag")
        void shouldCreateWithName() {
            Rule namedRule = new Rule("TestRule", false);

            assertThat(namedRule.getName()).isEqualTo("TestRule");
            assertThat(namedRule.getConditions()).isEmpty();
        }

        @Test
        @DisplayName("should create rule with commutative flag using default name")
        void shouldCreateWithCommutativeFlag() {
            Rule commutativeRule = new Rule(true);

            assertThat(commutativeRule.getName()).startsWith("Rule");
        }

        @Test
        @DisplayName("should set and get name")
        void shouldSetAndGetName() {
            rule.setName("RenamedRule");

            assertThat(rule.getName()).isEqualTo("RenamedRule");
        }

        @Test
        @DisplayName("toString should return rule name")
        void toStringShouldReturnName() {
            rule.setName("MyRule");

            assertThat(rule.toString()).isEqualTo("MyRule");
        }

        @Test
        @DisplayName("should increment default name counter for successive rules")
        void shouldIncrementDefaultNameCounter() {
            Rule rule1 = new Rule(false);
            Rule rule2 = new Rule(false);

            // Both should have "Rule" prefix with different counters
            assertThat(rule1.getName()).startsWith("Rule");
            assertThat(rule2.getName()).startsWith("Rule");
            // Names should be different due to counter increment
            assertThat(rule1.getName()).isNotEqualTo(rule2.getName());
        }
    }

    @Nested
    @DisplayName("Condition Management")
    class ConditionManagement {

        @Test
        @DisplayName("should add single condition")
        void shouldAddConditions() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");
            ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);

            rule.addCondition(condition);

            assertThat(rule.getConditions()).hasSize(1);
            assertThat(rule.getConditions()).contains(condition);
        }

        @Test
        @DisplayName("should add multiple conditions")
        void shouldAddMultipleConditions() {
            StringSchemeVariable nodeScheme1 = new StringSchemeVariable("n1");
            StringSchemeVariable nodeScheme2 = new StringSchemeVariable("n2");
            Expression expr = new ConstantExpression("P");

            ExpressionCondition condition1 = new ExpressionCondition(nodeScheme1, expr);
            MarkCondition condition2 = new MarkCondition(nodeScheme2, "applied");

            rule.addCondition(condition1);
            rule.addCondition(condition2);

            assertThat(rule.getConditions()).hasSize(2);
        }

        @Test
        @DisplayName("should remove condition")
        void shouldRemoveCondition() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");
            ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);

            rule.addCondition(condition);
            rule.removeCondition(condition);

            assertThat(rule.getConditions()).isEmpty();
        }

        @Test
        @DisplayName("should get conditions as enumeration")
        void shouldGetConditionsAsElements() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");
            ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);

            rule.addCondition(condition);

            Enumeration<?> elements = rule.getConditionsAsElements();

            assertThat(elements.hasMoreElements()).isTrue();
            assertThat(elements.nextElement()).isEqualTo(condition);
            assertThat(elements.hasMoreElements()).isFalse();
        }

        @Test
        @DisplayName("should set conditions vector")
        void shouldSetConditions() {
            Vector<AbstractCondition> newConditions = new Vector<>();
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("Q");
            newConditions.add(new ExpressionCondition(nodeScheme, expr));

            rule.setConditions(newConditions);

            assertThat(rule.getConditions()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Action Management")
    class ActionManagement {

        @Test
        @DisplayName("should add single action")
        void shouldAddActions() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");
            AddExpressionAction action = new AddExpressionAction(nodeScheme, expr);

            rule.addAction(action);

            assertThat(rule.getActions()).hasSize(1);
            assertThat(rule.getActions()).contains(action);
        }

        @Test
        @DisplayName("should add multiple actions")
        void shouldAddMultipleActions() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");

            AddExpressionAction action1 = new AddExpressionAction(nodeScheme, expr);
            MarkAction action2 = new MarkAction(nodeScheme, "processed");

            rule.addAction(action1);
            rule.addAction(action2);

            assertThat(rule.getActions()).hasSize(2);
        }

        @Test
        @DisplayName("should remove action")
        void shouldRemoveAction() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");
            AddExpressionAction action = new AddExpressionAction(nodeScheme, expr);

            rule.addAction(action);
            rule.removeAction(action);

            assertThat(rule.getActions()).isEmpty();
        }

        @Test
        @DisplayName("should get actions as enumeration via action container")
        void shouldGetActionsAsElements() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");
            AddExpressionAction action = new AddExpressionAction(nodeScheme, expr);

            rule.addAction(action);

            Enumeration<?> elements = rule.getActionsAsElements();

            assertThat(elements.hasMoreElements()).isTrue();
            assertThat(elements.nextElement()).isEqualTo(action);
        }

        @Test
        @DisplayName("should get abstract actions as enumeration")
        void shouldGetAbstractActionsAsElements() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");
            AddExpressionAction action = new AddExpressionAction(nodeScheme, expr);

            rule.addAction(action);

            Enumeration<?> elements = rule.getAbstractActionsAsElements();

            assertThat(elements.hasMoreElements()).isTrue();
            assertThat(elements.nextElement()).isEqualTo(action);
        }

        @Test
        @DisplayName("should set actions vector")
        void shouldSetActions() {
            Vector<AbstractAction> newActions = new Vector<>();
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            newActions.add(new MarkAction(nodeScheme, "mark1"));

            rule.setActions(newActions);

            assertThat(rule.getActions()).hasSize(1);
        }

        @Test
        @DisplayName("should provide action container")
        void shouldProvideActionContainer() {
            assertThat(rule.getActionContainer()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Event Machine Creation")
    class EventMachineCreation {

        @Test
        @DisplayName("should create event machine for rule with conditions")
        void shouldCreateEventMachine() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");

            ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);
            AddExpressionAction action = new AddExpressionAction(nodeScheme, new ConstantExpression("Q"));

            rule.setName("TestMachineRule");
            rule.addCondition(condition);
            rule.addAction(action);

            EventMachine machine = rule.createMachine();

            assertThat(machine).isNotNull();
            assertThat(machine.getWorkerName()).isEqualTo("TestMachineRule");
        }

        @Test
        @DisplayName("should create event machine for rule without conditions")
        void shouldCreateEventMachineWithoutConditions() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            AddExpressionAction action = new AddExpressionAction(nodeScheme, new ConstantExpression("Q"));

            rule.setName("NoConditionRule");
            rule.addAction(action);

            // Rules without conditions should still create a machine
            EventMachine machine = rule.createMachine();

            assertThat(machine).isNotNull();
        }
    }

    @Nested
    @DisplayName("Comment Handling")
    class CommentHandling {

        @Test
        @DisplayName("should set and get comment")
        void shouldSetAndGetComment() {
            String comment = "This rule handles conjunction decomposition";

            rule.setComment(comment);

            assertThat(rule.getComment()).isEqualTo(comment);
        }

        @Test
        @DisplayName("comment should be null by default")
        void commentShouldBeNullByDefault() {
            assertThat(rule.getComment()).isNull();
        }
    }

    @Nested
    @DisplayName("Code Generation")
    class CodeGeneration {

        @Test
        @DisplayName("should generate code for empty rule")
        void shouldGenerateCodeForEmptyRule() {
            rule.setName("EmptyRule");

            String code = rule.getCode();

            assertThat(code).startsWith("Rule EmptyRule");
            assertThat(code).endsWith("End");
        }

        @Test
        @DisplayName("should generate code with conditions and actions")
        void shouldGenerateCodeWithConditionsAndActions() {
            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            Expression expr = new ConstantExpression("P");

            ExpressionCondition condition = new ExpressionCondition(nodeScheme, expr);
            condition.setName("hasElement");

            AddExpressionAction action = new AddExpressionAction(nodeScheme, new ConstantExpression("Q"));
            action.setName("add");

            rule.setName("CodeRule");
            rule.addCondition(condition);
            rule.addAction(action);

            String code = rule.getCode();

            assertThat(code).contains("Rule CodeRule");
            assertThat(code).contains("hasElement");
            assertThat(code).contains("add");
            assertThat(code).endsWith("End");
        }
    }

    @Nested
    @DisplayName("Connector Usage Detection")
    class ConnectorUsageDetection {

        @Test
        @DisplayName("should return false when rule has no conditions or actions")
        void shouldReturnFalseForEmptyRule() {
            Connector connector = new Connector("not", 1, "~_");

            boolean isUsed = rule.isUsed(connector);

            assertThat(isUsed).isFalse();
        }

        @Test
        @DisplayName("should return false when connector is not used in conditions or actions")
        void shouldReturnFalseWhenConnectorNotUsed() {
            Connector notConnector = new Connector("not", 1, "~_");
            Connector andConnector = new Connector("and", 2, "_&_");

            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            // Use simple constant expression without connectors
            Expression simpleExpr = new ConstantExpression("P");

            ExpressionCondition condition = new ExpressionCondition(nodeScheme, simpleExpr);
            condition.setName("hasElement");
            // Add parameters to the condition so isUsed can inspect them
            condition.addParameter(new Parameter(ParameterType.NODE, nodeScheme));
            condition.addParameter(new Parameter(ParameterType.FORMULA, simpleExpr));
            rule.addCondition(condition);

            // Check for a connector that is not used
            assertThat(rule.isUsed(notConnector)).isFalse();
            assertThat(rule.isUsed(andConnector)).isFalse();
        }

        @Test
        @DisplayName("should return true when connector is used in condition expression")
        void shouldReturnTrueWhenConnectorUsedInCondition() {
            Connector notConnector = new Connector("not", 1, "~_");

            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            // Create expression using the connector: not P
            Expression innerExpr = new ConstantExpression("P");
            ExpressionWithSubExpressions notExpr = new ExpressionWithSubExpressions(notConnector);
            notExpr.setExpression(innerExpr, 0);

            ExpressionCondition condition = new ExpressionCondition(nodeScheme, notExpr);
            condition.setName("hasElement");
            // Add parameters to the condition so isUsed can inspect them
            condition.addParameter(new Parameter(ParameterType.NODE, nodeScheme));
            condition.addParameter(new Parameter(ParameterType.FORMULA, notExpr));
            rule.addCondition(condition);

            assertThat(rule.isUsed(notConnector)).isTrue();
        }

        @Test
        @DisplayName("should return true when connector is used in action expression")
        void shouldReturnTrueWhenConnectorUsedInAction() {
            Connector notConnector = new Connector("not", 1, "~_");

            StringSchemeVariable nodeScheme = new StringSchemeVariable("n");
            // Create expression using the connector: not Q
            Expression innerExpr = new ConstantExpression("Q");
            ExpressionWithSubExpressions notExpr = new ExpressionWithSubExpressions(notConnector);
            notExpr.setExpression(innerExpr, 0);

            AddExpressionAction action = new AddExpressionAction(nodeScheme, notExpr);
            action.setName("add");
            // Add parameters to the action so isUsed can inspect them
            action.addParameter(new Parameter(ParameterType.NODE, nodeScheme));
            action.addParameter(new Parameter(ParameterType.FORMULA, notExpr));
            rule.addAction(action);

            assertThat(rule.isUsed(notConnector)).isTrue();
        }
    }

    @Nested
    @DisplayName("Serialization")
    class Serialization {

        @Test
        @DisplayName("Rule should be serializable")
        void ruleShouldBeSerializable() {
            // Rule implements java.io.Serializable
            assertThat(rule).isInstanceOf(java.io.Serializable.class);
        }
    }
}
