package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.tableau.TableauNode;
import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.util.Marked;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of the marked object scheme. The pass succeeds if the object is marked as specified.
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
<p>Important note : if the specified marker is an instance of <code>SchemeVariable</code>, then its concrete reference will be search in then modifier.
@author David Fauthoux
 */
public class MarkExpressionConstraint extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression markedExpression;
    private Object marker;

    /**
    Creates a mark constraint, ready to be included in a restriction chain, or to begin a chain.
    @param markedScheme the scheme representing the marked object
    @param marker the marker of the specified object
     */
    public MarkExpressionConstraint(SchemeVariable nodeScheme, Expression markedExpression, Object marker) {
        this.nodeScheme = nodeScheme;
        this.markedExpression = markedExpression;
        this.marker = marker;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {
        InstanceSet instanceSet = (InstanceSet) modifier;

        Marked m = (Marked) instanceSet.get(nodeScheme);

        if (m == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for marked");
        }
        Object test = marker;

        if (marker instanceof SchemeVariable) {
            test = instanceSet.get((SchemeVariable) marker);
            if (test == null) {
                test = marker;
            }
        }

        if (m instanceof TableauNode) {
            for (Enumeration enumr = ((TableauNode) m).getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
                MarkedExpression me = (MarkedExpression) enumr.nextElement();
                InstanceSet newInstanceSet = markedExpression.matchWith(me.expression, instanceSet);
                if (newInstanceSet != null) {
//                if ((newInstanceSet != null)&&(newInstanceSet.equals(instanceSet))) {
                    /*
                     * Attention!!
                     * I should not test if  "newInstanceSet.equals(instanceSet)"
                     * Counter-example: when this conditions matching would instantiate a formula marked with a given mark..
                     *
                     * Rule MarkAnd
                     * isMarkedExpression w variable A Fulfilled
                     * isMarkedExpression w variable B Fulfilled
                     * hasElement w and variable A variable B
                     *
                     * markExpressions w and variable A variable B Fulfilled
                     * End
                     *
                     * Formula: A & A & A with A marked as Fulfilled
                     */
                    if (me.isMarked(test)) {
                        continueAttemptToApply(action, newInstanceSet, actionStocking,eventMachine);
                        /* Corrected vulnerability:
                         * After instantiating the matchable formula, we do not complete the instanceSet
                         * We were sending the instanceSet to the rest of the restriction chain instead of
                         * completing it and sending the newInstanceSet. It is corrected now.
                         */
//                        continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
                        if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                            return;
                        }
                    //Could not be optimized with "return;"!!!
                    //in fact, once we found that there's a "me" that matches with "markedExpression"
                    //and that this "me" is marked by test, then we can say that the condition was fullfilled
                    // cause when we mark an expression, we mark ALLLL its similar (matching) expressions of the same node...
                    //NEVERTHELESS, finding all the matches is necessary to apply the rules on every match..
                    }
                }
            }
        }
        return;
    }
}
