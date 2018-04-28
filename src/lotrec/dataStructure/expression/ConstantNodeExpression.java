/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.dataStructure.expression;

import java.util.ArrayList;
import lotrec.dataStructure.tableau.TableauNode;

/**
 *
 * @author said
 */
public class ConstantNodeExpression implements Expression {

    private TableauNode node;

    public ConstantNodeExpression(TableauNode node) {
        this.node = node;
    }

    public TableauNode getTableauNode() {
        return this.node;
    }

    @Override
    public String toString() {
        return node.getName();
    }

    @Override
    public boolean equals(Object e) {
        if (e instanceof ConstantNodeExpression) {
            return ((ConstantNodeExpression) e).node.equals(node);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.node != null ? this.node.hashCode() : 0);
        return hash;
    }

    @Override
    public InstanceSet matchWith(Expression e, InstanceSet current) {
        if (equals(e)) {
            return current;
        }
        return null;
    }

    @Override
    public Expression getInstance(InstanceSet set) {
        return this;
    }

    @Override
    public String getCodeString() {
        return node.getName();
    }

    @Override
    public String toMSPASS() {
         return node.toString().toLowerCase();
    }

    @Override
    public ArrayList<Expression> getVariableExpressions() {
        return new ArrayList<Expression>();
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
