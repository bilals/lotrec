package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.util.Marked;
import lotrec.dataStructure.tableau.TableauNode;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of the marked object scheme. The pass succeeds if the object is NOT marked as specified.
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
<p>Important note : if the specified marker is an instance of <code>SchemeVariable</code>, then its concrete reference will be search in then modifier.
@author David Fauthoux
 */
public class NotMarkExpressionConstraint extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression markedExpression;
    private Object marker;

    /**
    Creates a not mark constraint, ready to be included in a restriction chain, or to begin a chain.
    @param markedScheme the scheme representing the marked object
    @param marker the marker of the specified object
     */
    public NotMarkExpressionConstraint(SchemeVariable nodeScheme, Expression markedExpression, Object marker) {
        this.nodeScheme = nodeScheme;
        this.markedExpression = markedExpression;
        this.marker = marker;

    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine)
            throws ProcessException {


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
                    if (!me.isMarked(test)) {
                        continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
                        if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                            return;
                        }
                    //Could not be optimized with "return;"!!!
                    //in fact, once we found that there's a "me" that matches with "markedExpression"
                    //and that this "me" is NOT marked by test, then we can say that the condition was fullfilled
                    // cause when we UNmark an expression, we UNmark ALLLL its similar (matching) expressions of the same node...
                    //NEVERTHELESS, finding all the matches is necessary to apply the rules on every match..
                    }
                }
            }
        }
        return;
    }
}
