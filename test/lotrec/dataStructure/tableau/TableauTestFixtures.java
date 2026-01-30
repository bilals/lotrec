package lotrec.dataStructure.tableau;

import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.graph.Wallet;

/**
 * Factory methods for creating test fixtures for Tableau-related tests.
 * Provides real instances of Tableau, TableauNode, and helper methods for
 * constructing InstanceSet bindings used in action/condition execution tests.
 */
public final class TableauTestFixtures {

    private TableauTestFixtures() {
        // Utility class - prevent instantiation
    }

    // ========== Tableau Creation ==========

    /**
     * Creates a Tableau with a Wallet (required for some operations).
     * @param name the tableau name
     * @return configured Tableau ready for testing
     */
    public static Tableau createTableau(String name) {
        Tableau tableau = new Tableau(name);
        Wallet wallet = new Wallet("test-wallet");
        wallet.add(tableau);
        return tableau;
    }

    /**
     * Creates a Tableau with default name "test-tableau".
     * @return configured Tableau ready for testing
     */
    public static Tableau createTableau() {
        return createTableau("test-tableau");
    }

    // ========== Node Creation ==========

    /**
     * Creates a TableauNode and adds it to the given tableau.
     * @param tableau the tableau to add the node to
     * @param name the node name
     * @return the created node
     */
    public static TableauNode createNode(Tableau tableau, String name) {
        TableauNode node = new TableauNode(name);
        tableau.add(node);
        return node;
    }

    /**
     * Creates a TableauNode with default naming and adds it to the tableau.
     * @param tableau the tableau to add the node to
     * @return the created node
     */
    public static TableauNode createNode(Tableau tableau) {
        TableauNode node = new TableauNode();
        tableau.add(node);
        return node;
    }

    /**
     * Creates a TableauNode containing the given expression.
     * @param tableau the tableau to add the node to
     * @param expression the expression to add to the node
     * @return the created node with expression
     */
    public static TableauNode createNodeWithExpression(Tableau tableau, Expression expression) {
        TableauNode node = new TableauNode();
        tableau.add(node);
        node.add(new MarkedExpression(expression));
        return node;
    }

    /**
     * Creates a TableauNode containing multiple expressions.
     * @param tableau the tableau to add the node to
     * @param expressions the expressions to add to the node
     * @return the created node with expressions
     */
    public static TableauNode createNodeWithExpressions(Tableau tableau, Expression... expressions) {
        TableauNode node = new TableauNode();
        tableau.add(node);
        for (Expression expr : expressions) {
            node.add(new MarkedExpression(expr));
        }
        return node;
    }

    // ========== Edge/Link Creation ==========

    /**
     * Creates two linked nodes in the tableau.
     * @param tableau the tableau to add nodes to
     * @param relation the relation expression for the edge
     * @return array of [fromNode, toNode]
     */
    public static TableauNode[] createLinkedNodes(Tableau tableau, Expression relation) {
        TableauNode fromNode = new TableauNode();
        TableauNode toNode = new TableauNode();
        tableau.add(fromNode);
        tableau.add(toNode);

        TableauEdge edge = new TableauEdge(fromNode, toNode, relation);
        fromNode.link(edge);

        return new TableauNode[] { fromNode, toNode };
    }

    /**
     * Links two existing nodes with the given relation.
     * @param fromNode source node
     * @param toNode destination node
     * @param relation the relation expression for the edge
     * @return the created edge
     */
    public static TableauEdge linkNodes(TableauNode fromNode, TableauNode toNode, Expression relation) {
        TableauEdge edge = new TableauEdge(fromNode, toNode, relation);
        fromNode.link(edge);
        return edge;
    }

    // ========== InstanceSet Creation ==========

    /**
     * Creates an InstanceSet with the given bindings.
     * @param bindings array of SchemeBinding objects
     * @return configured InstanceSet
     */
    public static InstanceSet createInstanceSetWith(SchemeBinding... bindings) {
        InstanceSet instanceSet = new InstanceSet();
        for (SchemeBinding binding : bindings) {
            instanceSet.put(binding.scheme, binding.instance);
        }
        return instanceSet;
    }

    /**
     * Creates an empty InstanceSet.
     * @return new empty InstanceSet
     */
    public static InstanceSet createEmptyInstanceSet() {
        return new InstanceSet();
    }

    // ========== Binding Helpers ==========

    /**
     * Helper class to hold scheme-to-instance bindings.
     */
    public static class SchemeBinding {
        final SchemeVariable scheme;
        final Object instance;

        SchemeBinding(SchemeVariable scheme, Object instance) {
            this.scheme = scheme;
            this.instance = instance;
        }
    }

    /**
     * Creates a binding for a node scheme variable to a TableauNode.
     * @param name the scheme variable name
     * @param node the node instance
     * @return SchemeBinding for use with createInstanceSetWith
     */
    public static SchemeBinding nodeBinding(String name, TableauNode node) {
        return new SchemeBinding(new StringSchemeVariable(name), node);
    }

    /**
     * Creates a binding for an expression scheme variable to an Expression.
     * @param name the scheme variable name
     * @param expression the expression instance
     * @return SchemeBinding for use with createInstanceSetWith
     */
    public static SchemeBinding exprBinding(String name, Expression expression) {
        return new SchemeBinding(new VariableExpression(name), expression);
    }

    /**
     * Creates a binding for any scheme variable to any object.
     * @param scheme the scheme variable
     * @param instance the object instance
     * @return SchemeBinding for use with createInstanceSetWith
     */
    public static SchemeBinding binding(SchemeVariable scheme, Object instance) {
        return new SchemeBinding(scheme, instance);
    }

    // ========== Scheme Variable Creation ==========

    /**
     * Creates a StringSchemeVariable for node references.
     * @param name the variable name
     * @return StringSchemeVariable
     */
    public static StringSchemeVariable nodeScheme(String name) {
        return new StringSchemeVariable(name);
    }

    /**
     * Creates a VariableExpression for expression pattern matching.
     * @param name the variable name
     * @return VariableExpression
     */
    public static VariableExpression exprScheme(String name) {
        return new VariableExpression(name);
    }

    // ========== Expression Creation ==========

    /**
     * Creates a ConstantExpression.
     * @param name the constant name
     * @return ConstantExpression
     */
    public static ConstantExpression constant(String name) {
        return new ConstantExpression(name);
    }

    /**
     * Creates a MarkedExpression wrapping the given expression.
     * @param expression the expression to wrap
     * @return MarkedExpression
     */
    public static MarkedExpression marked(Expression expression) {
        return new MarkedExpression(expression);
    }

    /**
     * Creates FALSUM constant expression (represents contradiction/closed branch).
     * @return FALSUM ConstantExpression
     */
    public static ConstantExpression falsum() {
        return ConstantExpression.FALSUM;
    }

    // ========== Convenience Methods ==========

    /**
     * Creates a simple test setup: tableau with one node containing one expression.
     * @param expressionName the expression constant name
     * @return array of [tableau, node, expression]
     */
    public static Object[] createSimpleSetup(String expressionName) {
        Tableau tableau = createTableau();
        ConstantExpression expr = constant(expressionName);
        TableauNode node = createNodeWithExpression(tableau, expr);
        return new Object[] { tableau, node, expr };
    }

    /**
     * Adds a mark to a TableauNode.
     * @param node the node to mark
     * @param mark the mark object
     */
    public static void markNode(TableauNode node, Object mark) {
        node.mark(mark);
    }

    /**
     * Adds a mark to a MarkedExpression within a node.
     * @param node the node containing the expression
     * @param expression the expression to find and mark
     * @param mark the mark object
     * @return true if expression was found and marked
     */
    public static boolean markExpression(TableauNode node, Expression expression, Object mark) {
        for (java.util.Enumeration<?> e = node.getMarkedExpressionsEnum(); e.hasMoreElements();) {
            MarkedExpression me = (MarkedExpression) e.nextElement();
            if (me.expression.equals(expression)) {
                me.mark(mark);
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a node contains an expression (ignoring marks).
     * @param node the node to check
     * @param expression the expression to find
     * @return true if node contains the expression
     */
    public static boolean nodeContains(TableauNode node, Expression expression) {
        return node.contains(expression);
    }

    /**
     * Gets the count of expressions in a node.
     * @param node the node to count
     * @return number of expressions
     */
    public static int expressionCount(TableauNode node) {
        return node.getMarkedExpressions().size();
    }
}
