package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>How it completes the modifier when it passes :
<p>The modifier must contain the instance of the node scheme. The modifier will be completed within the matching process between the concrete expression (found in the concrete node) and the expression scheme in the pattern.
<p>In all cases of success (for all the matching found expressions), the completed modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class ExpressionMatch extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates an expression match, ready to be included in a restriction chain, or to begin a chain.
    @param nodeScheme the scheme representing the node containing the expression
    @param expressionScheme the scheme representing the contained expression
    @see TableauNode
     */
    public ExpressionMatch(SchemeVariable nodeScheme, Expression expressionScheme) {
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {

        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);

        if (n == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for node");
        }

        for (Enumeration enumr = n.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
            Expression e = ((MarkedExpression) enumr.nextElement()).expression;
            InstanceSet newInstanceSet = expressionScheme.matchWith(e, instanceSet);
            if (newInstanceSet != null) {
                continueAttemptToApply(action, newInstanceSet, actionStocking,eventMachine);
                if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                    return;
                }
            }
        }
        return;
    }
}
