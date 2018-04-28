package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of the two nodes schemes. The pass succeeds if the first node (represented by the first scheme) contains the second node (represented by the second scheme).
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class MarkedExpressionInAllChildrenMatch extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression markedExpression;
    private Expression relation;
    private Object marker;

    /**
    Creates a contains constraint, ready to be included in a restriction chain.
    The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
    @param bigNodeScheme the scheme representing the node N0
    @param smallNodeScheme the scheme representing the node N1
     */
    public MarkedExpressionInAllChildrenMatch(SchemeVariable nodeScheme, Expression markedExpression, Expression relation, Object marker) {
        this.nodeScheme = nodeScheme;
        this.markedExpression = markedExpression;
        this.relation = relation;
        this.marker = marker;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking, EventMachine eventMachine) throws ProcessException {
//        boolean allSucc = true;
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode node = (TableauNode) instanceSet.get(nodeScheme);
        if (node == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for marked");
        }
        Object test = marker;
        if (marker instanceof SchemeVariable) {
            test = instanceSet.get((SchemeVariable) marker);
            if (test == null) {
                test = marker;
            }
        }
        for (Enumeration enumra = node.getAllSuccessors(relation, instanceSet); enumra.hasMoreElements();) {
            boolean onSucc = false;
//            if (!allSucc) {
//                return;
//            }
            TableauNode successorNode = (TableauNode) enumra.nextElement();
            for (Enumeration enumr = successorNode.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
                MarkedExpression me = (MarkedExpression) enumr.nextElement();
                InstanceSet newInstanceSet = markedExpression.matchWith(me.expression, instanceSet);
                if (newInstanceSet != null) {
                    if (me.isMarked(test)) {
                        onSucc = true;
                    }
                    break;
                }
            }
            if (!onSucc) {
                return;//at least  one successor has not  the expression
            }
//            onSucc = false;
        }
//        if (allSucc) {
        continueAttemptToApply(action, instanceSet, actionStocking, eventMachine);
//        } else {
//            return;
//        }
    }
}
