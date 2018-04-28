/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.dataStructure.expression;

import java.util.ArrayList;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.process.ProcessException;

/**
 *
 * @author said
 */
public class VariableNodeExpression implements Expression {

    private SchemeVariable nodeScheme;

    public VariableNodeExpression(String nodeScheme) {
        this.nodeScheme = new StringSchemeVariable(nodeScheme);
    }

    @Override
    public Expression getInstance(InstanceSet instanceSet) {
        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);
        if (n == null) {
            throw new ProcessException(toString() + " : cannot apply action without instance for node ");
        }
        Expression f = new ConstantNodeExpression(n);
        return f;
    }

    @Override
    public boolean equals(Object e) {
        if (e instanceof VariableNodeExpression) {
            return ((VariableNodeExpression) e).nodeScheme.equals(nodeScheme);
        }
        return false;
    }

    @Override
    public String toString() {
        return nodeScheme.toString();
    }

    @Override
    public String getCodeString() {
        return "nodeVariable " + nodeScheme.toString();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public InstanceSet matchWith(Expression e, InstanceSet current) {
        TableauNode node = (TableauNode) current.get(nodeScheme);
        if (node != null &&
                e instanceof ConstantNodeExpression &&
                node.equals(((ConstantNodeExpression) e).getTableauNode())) {
            return current;
        }
        return null;
    }

    @Override
    public String toMSPASS() {
        return nodeScheme.toString().toLowerCase();
    }

    @Override
    public ArrayList<Expression> getVariableExpressions() {
                ArrayList<Expression> result = new ArrayList<Expression>();
        result.add(this);
        return result;
    }

    @Override
    public boolean isUsed(Connector c) {
        return false;
    }

    @Override
    public ArrayList<Connector> getUsedConnectors() {
        return new ArrayList<Connector>();
    }
}
