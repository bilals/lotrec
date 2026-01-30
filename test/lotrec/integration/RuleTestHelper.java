package lotrec.integration;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.graph.Edge;
import lotrec.dataStructure.graph.LinkEvent;
import lotrec.dataStructure.tableau.*;
import lotrec.dataStructure.tableau.action.*;
import lotrec.dataStructure.tableau.condition.*;
import lotrec.engine.Engine;
import lotrec.engine.TestableEngine;
import lotrec.process.*;

import java.util.List;

/**
 * Test utilities for integration testing of rule application.
 * Provides methods to create rules, trigger events, and apply rules to tableaux.
 */
public final class RuleTestHelper {

    private RuleTestHelper() {
        // Utility class - prevent instantiation
    }

    // ========== Rule Creation ==========

    /**
     * Creates a Rule with the given conditions and actions.
     * @param name the rule name
     * @param conditions the conditions to add
     * @param actions the actions to add
     * @return configured Rule
     */
    public static Rule createRule(String name,
            List<AbstractCondition> conditions,
            List<AbstractAction> actions) {
        // Use the empty constructor which initializes all fields including 'actions' vector
        Rule rule = new Rule();
        rule.setName(name);
        for (AbstractCondition condition : conditions) {
            rule.addCondition(condition);
        }
        for (AbstractAction action : actions) {
            rule.addAction(action);
        }
        return rule;
    }

    /**
     * Creates a simple rule with one condition and one action.
     * @param name the rule name
     * @param condition the condition
     * @param action the action
     * @return configured Rule
     */
    public static Rule createSimpleRule(String name,
            AbstractCondition condition,
            AbstractAction action) {
        return createRule(name, List.of(condition), List.of(action));
    }

    // ========== Rule Application ==========

    /**
     * Applies a rule to a tableau by creating an EventMachine and processing events.
     * This is the main integration test method that tests the full rule application workflow.
     *
     * @param rule the rule to apply
     * @param tableau the tableau to apply the rule on
     * @param engine the engine (can be TestableEngine)
     * @return true if the rule was applied (actions executed)
     */
    public static boolean applyRule(Rule rule, Tableau tableau, Engine engine) {
        EventMachine em = rule.createMachine();
        em.setRelatedTableau(tableau);
        em.setEngine(engine);

        // Register the EventMachine as listener on the tableau (which is a Graph)
        tableau.addProcessListener(em);

        // The EventMachine is now registered. Events fired by nodes will be queued.
        // We need to trigger events for existing expressions in nodes.
        triggerExistingExpressionEvents(tableau, em);

        // Process the queued events
        em.work();

        return em.hasWorked();
    }

    /**
     * Applies a rule with a fresh TestableEngine.
     * @param rule the rule to apply
     * @param tableau the tableau
     * @return true if the rule was applied
     */
    public static boolean applyRule(Rule rule, Tableau tableau) {
        return applyRule(rule, tableau, TestableEngine.create());
    }

    /**
     * Triggers expression events for all existing expressions in all nodes of the tableau.
     * This simulates the events that would have been fired when expressions were added.
     */
    public static void triggerExistingExpressionEvents(Tableau tableau, EventMachine em) {
        for (Object nodeObj : tableau.getNodes()) {
            if (nodeObj instanceof TableauNode) {
                TableauNode node = (TableauNode) nodeObj;
                for (java.util.Enumeration<?> e = node.getMarkedExpressionsEnum(); e.hasMoreElements();) {
                    MarkedExpression me = (MarkedExpression) e.nextElement();
                    ExpressionEvent event = new ExpressionEvent(node, ExpressionEvent.EXPRESSION_ADDED, me);
                    em.process(event);
                }
            }
        }
    }

    /**
     * Triggers link events for all existing edges in all nodes of the tableau.
     */
    public static void triggerExistingLinkEvents(Tableau tableau, EventMachine em) {
        for (Object nodeObj : tableau.getNodes()) {
            if (nodeObj instanceof TableauNode) {
                TableauNode node = (TableauNode) nodeObj;
                for (java.util.Enumeration<?> e = node.getNextEdgesEnum(); e.hasMoreElements();) {
                    Edge edge = (Edge) e.nextElement();
                    LinkEvent event = new LinkEvent(node, LinkEvent.LINKED, edge);
                    em.process(event);
                }
            }
        }
    }

    /**
     * Triggers all existing events (expressions and links) in the tableau.
     */
    public static void triggerAllExistingEvents(Tableau tableau, EventMachine em) {
        triggerExistingExpressionEvents(tableau, em);
        triggerExistingLinkEvents(tableau, em);
    }

    // ========== Condition Factories ==========

    /**
     * Creates an ExpressionCondition (hasElement).
     * @param nodeScheme the node scheme variable
     * @param expression the expression to match
     * @return ExpressionCondition
     */
    public static ExpressionCondition hasElement(StringSchemeVariable nodeScheme, Expression expression) {
        return new ExpressionCondition(nodeScheme, expression);
    }

    /**
     * Creates a NotExpressionCondition (hasNotElement).
     * @param nodeScheme the node scheme variable
     * @param expression the expression that should not be present
     * @return NotExpressionCondition
     */
    public static NotExpressionCondition hasNotElement(StringSchemeVariable nodeScheme, Expression expression) {
        return new NotExpressionCondition(nodeScheme, expression);
    }

    /**
     * Creates a LinkCondition (isLinked).
     * @param fromScheme the source node scheme
     * @param toScheme the target node scheme
     * @param relation the relation expression
     * @return LinkCondition
     */
    public static LinkCondition isLinked(StringSchemeVariable fromScheme,
            StringSchemeVariable toScheme,
            Expression relation) {
        return new LinkCondition(fromScheme, toScheme, relation);
    }

    /**
     * Creates a MarkCondition (isMarked).
     * @param nodeScheme the node scheme
     * @param mark the mark to check
     * @return MarkCondition
     */
    public static MarkCondition isMarked(StringSchemeVariable nodeScheme, Object mark) {
        return new MarkCondition(nodeScheme, mark);
    }

    /**
     * Creates a NotMarkCondition (isNotMarked).
     * @param nodeScheme the node scheme
     * @param mark the mark that should not be present
     * @return NotMarkCondition
     */
    public static NotMarkCondition isNotMarked(StringSchemeVariable nodeScheme, Object mark) {
        return new NotMarkCondition(nodeScheme, mark);
    }

    // ========== Action Factories ==========

    /**
     * Creates an AddExpressionAction (add).
     * @param nodeScheme the node scheme
     * @param expression the expression to add
     * @return AddExpressionAction
     */
    public static AddExpressionAction addExpression(StringSchemeVariable nodeScheme, Expression expression) {
        return new AddExpressionAction(nodeScheme, expression);
    }

    /**
     * Creates a MarkAction for marking a node.
     * @param nodeScheme the node scheme
     * @param mark the mark object
     * @return MarkAction
     */
    public static MarkAction markNode(StringSchemeVariable nodeScheme, Object mark) {
        return new MarkAction(nodeScheme, mark);
    }

    /**
     * Creates a MarkExpressionsAction for marking expressions in a node.
     * @param nodeScheme the node scheme
     * @param expression the expression pattern to mark
     * @param mark the mark object
     * @return MarkExpressionsAction
     */
    public static MarkExpressionsAction mark(StringSchemeVariable nodeScheme, Expression expression, Object mark) {
        return new MarkExpressionsAction(nodeScheme, expression, mark);
    }

    /**
     * Creates an AddNodeAction (createNewNode).
     * @param nodeScheme the scheme for the new node
     * @return AddNodeAction
     */
    public static AddNodeAction createNewNode(StringSchemeVariable nodeScheme) {
        return new AddNodeAction(nodeScheme);
    }

    /**
     * Creates a LinkAction (link).
     * @param fromScheme source node scheme
     * @param toScheme target node scheme
     * @param relation relation expression
     * @return LinkAction
     */
    public static LinkAction link(StringSchemeVariable fromScheme,
            StringSchemeVariable toScheme,
            Expression relation) {
        return new LinkAction(fromScheme, toScheme, relation);
    }

    // ========== Expression Helpers ==========

    /**
     * Creates a scheme variable for nodes.
     * @param name the variable name
     * @return StringSchemeVariable
     */
    public static StringSchemeVariable nodeScheme(String name) {
        return new StringSchemeVariable(name);
    }

    /**
     * Creates a scheme variable for expressions (for pattern matching).
     * @param name the variable name
     * @return VariableExpression
     */
    public static VariableExpression exprVar(String name) {
        return new VariableExpression(name);
    }

    /**
     * Creates a constant expression.
     * @param name the constant name
     * @return ConstantExpression
     */
    public static ConstantExpression constant(String name) {
        return new ConstantExpression(name);
    }

    /**
     * Creates a compound expression using a connector.
     * @param connector the connector
     * @param children the child expressions
     * @return ExpressionWithSubExpressions
     */
    public static ExpressionWithSubExpressions compound(Connector connector, Expression... children) {
        ExpressionWithSubExpressions expr = new ExpressionWithSubExpressions(connector);
        for (int i = 0; i < children.length; i++) {
            expr.setExpression(children[i], i);
        }
        return expr;
    }

    // ========== Assertion Helpers ==========

    /**
     * Checks if a node contains an expression.
     * @param node the node to check
     * @param expression the expression to find
     * @return true if the node contains the expression
     */
    public static boolean nodeContains(TableauNode node, Expression expression) {
        return node.contains(expression);
    }

    /**
     * Gets the count of expressions in a node.
     * @param node the node
     * @return the number of expressions
     */
    public static int expressionCount(TableauNode node) {
        return node.getMarkedExpressions().size();
    }

    /**
     * Checks if a node is marked with a specific mark.
     * @param node the node
     * @param mark the mark to check
     * @return true if the node has the mark
     */
    public static boolean nodeIsMarked(TableauNode node, Object mark) {
        return node.isMarked(mark);
    }

    /**
     * Gets the count of nodes in a tableau.
     * @param tableau the tableau
     * @return number of nodes
     */
    public static int nodeCount(Tableau tableau) {
        return tableau.getNodes().size();
    }

    /**
     * Checks if two nodes are linked.
     * @param fromNode source node
     * @param toNode target node
     * @return true if there's an edge from fromNode to toNode
     */
    public static boolean nodesAreLinked(TableauNode fromNode, TableauNode toNode) {
        for (java.util.Enumeration<?> e = fromNode.getNextEdgesEnum(); e.hasMoreElements();) {
            Edge edge = (Edge) e.nextElement();
            if (edge.getEndNode() == toNode) {
                return true;
            }
        }
        return false;
    }
}
