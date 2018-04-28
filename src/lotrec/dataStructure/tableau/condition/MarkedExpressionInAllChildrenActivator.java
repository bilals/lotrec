package lotrec.dataStructure.tableau.condition;

/* ADDED 00/12/21 */
import java.util.ArrayList;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import java.util.Enumeration;
import lotrec.dataStructure.tableau.*;
import lotrec.util.Marked;

/**
This activator for a restriction chain sends a modifier in the chain when it receives a <code>MarkEvent.MARK</code> event.
<p>It builds the modifier with the received event and with its initializers (schemes) : it joins up the concrete bound object and its scheme, and it matches the expression with the concrete expression (received in the event).
 */
public class MarkedExpressionInAllChildrenActivator extends BasicActivator {

    private SchemeVariable prentNodeScheme;
    private Expression markedExpression;
    private Expression relation;
    private Object marker;

    /**
    Creates a mark activator. It will receive event of <code>MarkEvent.MARK</code> type (builds a <code>BasicActivator</code> with this event).
    @param scheme the scheme representing the marked object where the mark is added
    @param marker the marker
    @see TableauNode
     */
    public MarkedExpressionInAllChildrenActivator(SchemeVariable scheme, Expression markedExpression, Expression relation, Object marker) {
        super(MarkExpressionEvent.MARK_EX);
        this.prentNodeScheme = scheme;
        this.markedExpression = markedExpression;
        this.relation = relation;
        this.marker = marker;
    }

    public Object[] createModifiers(ProcessEvent event) {
        ArrayList modifiersList = new ArrayList();
        MarkExpressionEvent markExpressionEvent = (MarkExpressionEvent) event;
        Marked markedExpressionInstance = (Marked) markExpressionEvent.getSource();
        Object markerInstance = markExpressionEvent.mark;

        if (!markedExpressionInstance.isMarked(markerInstance)) {
            return null;
        }
        if (!(marker instanceof SchemeVariable) && (!marker.equals(markerInstance))) {
            //Bilal: Added 3 mars 2009
            // very important to check wether the "mark" of the event
            // matches with the "marker" of the condition, or not..
            return null;
        }
        TableauNode childNodeInstance = markExpressionEvent.getNode();
        Enumeration inComingEdges = childNodeInstance.getLastEdgesEnum();
        while (inComingEdges.hasMoreElements()) {
            InstanceSet modifier = new InstanceSet();
            if (marker instanceof SchemeVariable) {
                modifier.put((SchemeVariable) marker, markerInstance);
            }
            TableauEdge inComingEdge = (TableauEdge) inComingEdges.nextElement();
            InstanceSet modifierAfterRelation = relation.matchWith(inComingEdge.getRelation(), modifier);
            if (modifierAfterRelation == null) {
                continue;
            }
            TableauNode parentNode = (TableauNode) inComingEdge.getBeginNode();
            modifierAfterRelation.put(prentNodeScheme, parentNode);
            InstanceSet modifierAfterExpressionMatch = markedExpression.matchWith(
                    ((MarkedExpression) markedExpressionInstance).expression, modifierAfterRelation);
            if (modifierAfterExpressionMatch == null) {
                continue;
            }
            boolean markedInAllSuccessors = true;
            for (Enumeration childrenEnum = parentNode.getAllSuccessors(relation, modifierAfterExpressionMatch); childrenEnum.hasMoreElements();) {
                boolean markedInThisSuccessor = false;
                TableauNode childNode = (TableauNode) childrenEnum.nextElement();
                for (Enumeration expressionEnum = childNode.getMarkedExpressionsEnum(); expressionEnum.hasMoreElements();) {
                    MarkedExpression markedExp = (MarkedExpression) expressionEnum.nextElement();
                    InstanceSet newInstanceSet = markedExpression.matchWith(markedExp.expression, modifierAfterExpressionMatch);
                    if (newInstanceSet != null) {
                        if (markedExp.isMarked(markerInstance)) {
                            markedInThisSuccessor = true;
                        }
                        break;
                    }
                }
                if (!markedInThisSuccessor) {
                    markedInAllSuccessors = false;//at least  one successor has not  the expression
                }
            }
            if (markedInAllSuccessors) {
                modifiersList.add(modifierAfterExpressionMatch);
            }
        }
//        InstanceSet modifier = new InstanceSet();
//        if (marker instanceof SchemeVariable) {
//            modifier.put((SchemeVariable) marker, markerInstance);
//        }
//        // This is the bad part :s :s
//        // My tableau should be a tree where for each node there is only one parent node :s :s
//        if (!childNode.getLastEdgesEnum().hasMoreElements()) {
//            return null;
//        }
//        TableauEdge inComingEdge = (TableauEdge) childNode.getLastEdgesEnum().nextElement();
//        modifier = relation.matchWith(inComingEdge.getRelation(), modifier);
//        if (modifier == null) {
//            return null;
//        }
//        TableauNode parentNode = (TableauNode) inComingEdge.getBeginNode();
//        modifier.put(prentNodeScheme, parentNode);
//        modifier = markedExpression.matchWith(((MarkedExpression) markedExpressionInstance).expression, modifier);
//        if (modifier == null) {
//            return null;
//        }
//        for (Enumeration enumra = parentNode.getAllSuccessors(relation, modifier); enumra.hasMoreElements();) {
//            boolean markedInASuccessor = false;
//            TableauNode successorNode = (TableauNode) enumra.nextElement();
//            for (Enumeration enumr = successorNode.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
//                MarkedExpression markedExp = (MarkedExpression) enumr.nextElement();
//                InstanceSet newInstanceSet = markedExpression.matchWith(markedExp.expression, modifier);
//                if (newInstanceSet != null) {
//                    if (markedExp.isMarked(markerInstance)) {
//                        markedInASuccessor = true;
//                    }
//                    break;
//                }
//            }
//            if (!markedInASuccessor) {
//                return null;//at least  one successor has not  the expression
//            }
//        }
//        modifiersList.add(modifier);

        Object[] modifiers = modifiersList.toArray();
        return modifiers;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + prentNodeScheme + ", " + markedExpression + ", " + relation + ", " + marker + "]";
    }
}
