package lotrec.dataStructure.tableau;

import java.util.ArrayList;
import lotrec.dataStructure.graph.Node;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.expression.InstanceSet;
import lotrec.dataStructure.expression.Expression;
import java.util.Enumeration;
import java.util.Vector;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;

/**
Extends basic node to manage expression containing.
@author David Fauthoux
 */
public final class TableauNode extends Node {

    private Vector<MarkedExpression> markedExpressions;

    /**
    Creates an empty node ready to contain expressions.
     *It's called when creating new node...
     */
    public TableauNode() {
        super();
        markedExpressions = new Vector();
    }

    /**
    Creates an empty node ready to contain expressions.
    @param name the name of the node
     *It's called while creating the root node, just once...
     */
    public TableauNode(String name) {
        super(name);
        markedExpressions = new Vector();
    }

    /**
    Adds a marked expression in this node. <p>Sends an <code>ExpressionEvent</code>
    @param e the marked expression to add
     */
    public void add(MarkedExpression e) {
        markedExpressions.add(e);
        sendEvent(new ExpressionEvent(this, ExpressionEvent.EXPRESSION_ADDED, e));
    }

    /**
    Removes a marked expression from this node. <p>Sends an <code>ExpressionEvent</code> if the expression is contained by the node
    @param e the marked expression to remove
    @return true if the expression was contained, false otherwise
     */
    public boolean remove(MarkedExpression e) {
        if (markedExpressions.contains(e)) {
            markedExpressions.remove(e);
            sendEvent(new ExpressionEvent(this, ExpressionEvent.EXPRESSION_REMOVED, e));
            return true;
        } else {
            return false;
        }
    }

    /**
    Returns the marked expressions contained by this node
    @return the marked expressions
     */
    public Enumeration getMarkedExpressionsEnum() {
        return markedExpressions.elements();
    }

    public Vector<MarkedExpression> getMarkedExpressions() {
        return markedExpressions;
    }

//    public ArrayList getMarks() {
//        return new ArrayList(this.marks);
//    }
    /**
    Tests whether this node contains or not the expression (ignores the marks)
    @param e the expression to test
    @return true if this node contains the expression, false otherwise
     */
    public boolean contains(Expression e) {
        for (Enumeration enumr = getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
            Expression ex = ((MarkedExpression) enumr.nextElement()).expression;
            if (ex.equals(e)) {
                return true;
            }
        }
        return false;
    }

    // duplication
    /**
    Creates a tableau node with the toDuplicate's expression.
    <b> Calls the superclass constructor with <i>toDuplicate</i>. The marked expressions are duplicated and translated.</b>
    @param toDuplicate the node to duplicate
     */
    public TableauNode(TableauNode toDuplicate) {
        super(toDuplicate);
        markedExpressions = (Vector) toDuplicate.markedExpressions.clone();
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
        for (int i = 0; i < markedExpressions.size(); i++) {
            MarkedExpression e = (MarkedExpression) ((Duplicateable) markedExpressions.get(i)).duplicate(duplicator);
            e.completeDuplication(duplicator);
            markedExpressions.setElementAt(e, i);
        }
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
        for (int i = 0; i < markedExpressions.size(); i++) {
            ((Duplicateable) markedExpressions.get(i)).translateDuplication(duplicator);
        }
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new TableauNode(this);
        duplicator.setImage(this, d);
        return d;
    }
    //////////////////////////////////////////////

    /* ADDED 00/12/21 */
    @Override
    public void mark(Object o) {
        if (!this.isMarked(o)) {
            super.mark(o);
            sendEvent(new MarkEvent(this, MarkEvent.MARK, o));
        }
    }

    @Override
    public void unmark(Object o) {
        super.unmark(o);
        sendEvent(new MarkEvent(this, MarkEvent.UNMARK, o));
    }

    public void markAllExpressions(InstanceSet instanceSet, Expression form, Object marker) {
        for (Enumeration e = getMarkedExpressionsEnum(); e.hasMoreElements();) {
            MarkedExpression ex = (MarkedExpression) e.nextElement();
            InstanceSet testSet = form.matchWith(ex.expression, instanceSet);
            if (testSet != null) {
                //Proposed 16 february 2009.. A voir
//            if ((testSet != null)&&(testSet.equals(instanceSet))) {
                if (!ex.isMarked(marker)) {
                    ex.mark(marker);
                    sendEvent(new MarkExpressionEvent(this, ex, MarkExpressionEvent.MARK_EX, marker));
                }
            }
//            else{
//                throw new ProcessException(toString() + " : cannot apply action, cannot instanciate expression: " + form);
//            }
        }
    }

    public void unmarkAllExpressions(InstanceSet instanceSet, Expression form, Object marker) {
        for (Enumeration e = getMarkedExpressionsEnum(); e.hasMoreElements();) {
            MarkedExpression ex = (MarkedExpression) e.nextElement();
            InstanceSet testSet = form.matchWith(ex.expression, instanceSet);
            if (testSet != null) {
                if (ex.isMarked(marker)) {
                    ex.unmark(marker);
                    sendEvent(new MarkExpressionEvent(this, ex, MarkExpressionEvent.UNMARK_EX, marker));
                }
            }
        }
    }

    /* */
}

