package lotrec.dataStructure.expression;

import java.util.ArrayList;
import java.util.Vector;
import java.util.Enumeration;

/**
Defines an expression, built with a <code>Connector</code> which contains sub expressions.
@author David Fauthoux
 */
public class ExpressionWithSubExpressions implements Expression {

    private Vector<Expression> subExpressions;
    private Connector connector;

    /**
    Creates an expression ready to accept as many expression as connector arity.
    The creation must be completed with the <i>setFormula</i> method.
    @param connector the connector of this expression
     */
    public ExpressionWithSubExpressions(Connector connector) {
        subExpressions = new Vector();
        subExpressions.setSize(connector.getArity());
        this.connector = connector;
    }

    /**
    Changes the connector of this expression.
    This method removes expressions when the connector arity decreases (the new connector arity is smaller than the old connector), and adds new null expressions when it increases (in this case, user should use the <i>setFormula</i> method).
    @param connector the new connector
     */
    public void setConnector(Connector connector) {
        this.connector = connector;
        subExpressions.setSize(connector.getArity());
    }

    /**
    Returns the connector of this expression
    @return the connector of this expression
     */
    public Connector getConnector() {
        return connector;
    }

    /**
    Replaces the sub expression of this expression, at the specified index, with the specified expression.
    The index defines the location to set the expression, 0 for the first expression, 1 for the second... a-1 for the last (a is the arity of the connector)
    @param e the expression to set
    @param index where the expression must be set
     */
    public void setExpression(Expression e, int index) {
        subExpressions.setElementAt(e, index);
    }

    /**
    To match, the specified expression must be an <code>ExpressionWithSubExpressions</code> and the connectors must equal.
    Then, this method matches all the sub expressions (one from this expression, one from the specified expression, in the index order) and returns the completed instance set.
    @param e the expression on which the instanciation will run
    @param current the instance set to use and complete
    @return the completed instance set if the matching process succeeds, null if it fails
     */
    public InstanceSet matchWith(Expression e, InstanceSet current) {
        if (e instanceof ExpressionWithSubExpressions) {

            ExpressionWithSubExpressions ee = (ExpressionWithSubExpressions) e;

            if (!connector.equals(ee.connector)) {
                return null;
            }

            if (ee.subExpressions.size() != subExpressions.size()) {
                return null;
            }

            InstanceSet set = current;
            Enumeration thisEnum = subExpressions.elements();
            for (Enumeration enumr = ee.subExpressions.elements(); enumr.hasMoreElements();) {
                set = ((Expression) thisEnum.nextElement()).matchWith((Expression) enumr.nextElement(), set);
                if (set == null) {
                    return null;
                }
            }
            return set;
        }
        return null;
    }

    /**
    Tests the equality between the connectors and the sub expressions
    @param e the expression to test
    @return true if the specified expression equals this, false otherwise, false if the specified expression if null or is not an <code>ExpressionWithSubExpression</code>
     */
    @Override
    public boolean equals(Object e) {
        if (e instanceof ExpressionWithSubExpressions) {

            ExpressionWithSubExpressions ee = (ExpressionWithSubExpressions) e;

            if (!connector.equals(ee.connector)) {
                return false;
            }

            if (ee.subExpressions.size() != subExpressions.size()) {
                return false;
            }

            Enumeration thisEnum = subExpressions.elements();
            for (Enumeration enumr = ee.subExpressions.elements(); enumr.hasMoreElements();) {
                if (!thisEnum.nextElement().equals(enumr.nextElement())) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (this.subExpressions != null ? this.subExpressions.hashCode() : 0);
        hash = 79 * hash + (this.connector != null ? this.connector.hashCode() : 0);
        return hash;
    }

    /**
    Returns the sub expressions
    @return the sub expressions
     */
    public Enumeration getSubExpressions() {
        return subExpressions.elements();
    }

    /**
    Creates a string representation of this expression.
    It uses its connector fields, specially the <i>specialCharacter</i> and the <i>outString</i>, replacing in the <i>outString</i> all the <i>specialCharacter</i>
    by the sub expressions strings
    @return a string representaion of this expression
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        String outString = connector.getOutString();
        char specialCharacter = connector.getSpecialCharacter();
        Enumeration enumr = getSubExpressions();
        for (int i = 0; i < outString.length(); i++) {
            char c = outString.charAt(i);

            if (c == specialCharacter) {
                if (enumr.hasMoreElements()) {
                    Expression expression = (Expression) enumr.nextElement();
                    boolean parenth = false;
                    if (expression instanceof ExpressionWithSubExpressions) {
                        Connector other = ((ExpressionWithSubExpressions) expression).getConnector();
                        if (other.equals(connector)) {
                            parenth = !connector.isAssociative();
//                              parenth = true;
                        } else {
                            parenth = (other.getPriority() <= connector.getPriority());
                        }
                    }
                    if (parenth) {
                        s.append("(");
                    }
                    s.append(expression);
                    if (parenth) {
                        s.append(")");
                    }
                } else {
                    s.append("?");
                }
            } else {
                s.append(c);
            }
        }
        return s.toString();
    }

    /**
    Creates a string representation of this expression for MSPASS.
    @return a string representaion of this expression
     */
    public String toMSPASS() {


        Enumeration enumr = getSubExpressions();

        String outString = connector.getName();
        if (outString.equals("not")) {
            Expression expression = (Expression) enumr.nextElement();
            return " not(" + expression.toMSPASS() + ") ";
        } else if (outString.equals("nec")) {
            Expression expression = (Expression) enumr.nextElement();
            return " box(r , " + expression.toMSPASS() + ") ";
        } else if (outString.equals("pos")) {
            Expression expression = (Expression) enumr.nextElement();
            return " not(box( r, not(" + expression.toMSPASS() + "))) ";
        } else if (outString.equals("and")) {
            Expression expression = (Expression) enumr.nextElement();
            Expression expression1 = (Expression) enumr.nextElement();
            return " and(" + expression.toMSPASS() + "," + expression1.toMSPASS() + ") ";
        } else if (outString.equals("or")) {
            Expression expression = (Expression) enumr.nextElement();
            Expression expression1 = (Expression) enumr.nextElement();
            return " not(and(not(" + expression.toMSPASS() + "), not(" + expression1.toMSPASS() + "))) ";
        } else {
            return null;
        }

    }

    /**
    Runs the instanciation process on all the sub expressions
    @param set the instance set used in the instanciation
    @return the expression which results from the instanciation
     */
    /* REMOVED 00/12/10
    public Expression getInstance(InstanceSet set) {
    ExpressionWithSubExpressions e = new ExpressionWithSubExpressions(connector);
    for(int i = 0; i < subExpressions.size(); i++) {
    e.subExpressions.set(i, ((Expression)subExpressions.get(i)).getInstance(set));
    }
    return e;
    }
     */
    /* ADDED 00/12/10 */
    public Expression getInstance(InstanceSet set) {
        ExpressionWithSubExpressions e = new ExpressionWithSubExpressions(connector);
        for (int i = 0; i < subExpressions.size(); i++) {
            Expression f = ((Expression) subExpressions.get(i)).getInstance(set);
            if (f == null) {
                return null;
            }
            e.subExpressions.set(i, f);
        }
        return e;
    }

    public String getCodeString() {
        StringBuffer s = new StringBuffer();
        s.append(connector.getName());
        Enumeration enumr = getSubExpressions();
        while (enumr.hasMoreElements()) {
            Expression expression = (Expression) enumr.nextElement();
            s.append(" " + expression.getCodeString());
        }
        return s.toString();
    }

    public boolean isUsed(Connector c) {
        if (connector.getName().equals(c.getName())) {
            return true;
        } else {
            boolean used = false;
            Enumeration enumr = getSubExpressions();
            while (enumr.hasMoreElements()) {
                Expression expression = (Expression) enumr.nextElement();
                if (expression.isUsed(c)) {
                    used = true;
                }
            }
            return used;
        }
    }

    public ArrayList<Expression> getVariableExpressions() {
        ArrayList<Expression> result = new ArrayList<Expression>();
        for (Expression e : subExpressions) {
            for (Expression variableSchme : e.getVariableExpressions()) {
                result.add(variableSchme);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Connector> getUsedConnectors() {
        ArrayList<Connector> usedConnectors = new ArrayList<Connector>();
        usedConnectors.add(connector);
        Enumeration enumr = getSubExpressions();
        while (enumr.hasMoreElements()) {
            Expression expression = (Expression) enumr.nextElement();
            ArrayList<Connector> subExpressionUsedConnectors = expression.getUsedConnectors();
            for (Connector c :  subExpressionUsedConnectors) {
                usedConnectors.add(c);
            }
        }
        return usedConnectors;
    }
}
