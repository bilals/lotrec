package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;
import lotrec.dataStructure.expression.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of the node scheme. The pass succeeds if the object does NOT contains the specified expression.
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class NotExpressionConstraint extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates a not expression constraint, ready to be included in a restriction chain.
    @param nodeScheme the scheme representing the node
    @param expressionScheme the expression to be tested
     */
    public NotExpressionConstraint(SchemeVariable nodeScheme, Expression expressionScheme) {
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
            //When the formula (which must not be found) is found, return.. so don't try to continue on verifying the rest of the chain of restrictions
            if (newInstanceSet != null) {
                return;
            }
        }
        //When the expression is not found, so continue on attempting applying the chain of constraints..
        continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
    }
}
