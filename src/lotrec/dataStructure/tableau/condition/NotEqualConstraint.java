package lotrec.dataStructure.tableau.condition;

import lotrec.process.*;
import lotrec.dataStructure.expression.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of two schemes. The pass succeeds if the schemes does NOT reference the same object.
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class NotEqualConstraint extends Restriction {

    private Expression scheme1;
    private Expression scheme2;

    /**
    Creates an "not identical" constraint, ready to be included in a restriction chain.
    @param scheme1 the first scheme
    @param scheme2 the second scheme
     */
    public NotEqualConstraint(Expression scheme1, Expression scheme2) {
        this.scheme1 = scheme1;
        this.scheme2 = scheme2;
    }

    @Override
    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {
        InstanceSet instanceSet = (InstanceSet) modifier;
        VariableExpression exp1;
        VariableExpression exp2;
        if (scheme1 instanceof VariableExpression && scheme2 instanceof VariableExpression) {
            exp1 = (VariableExpression) scheme1;
            exp2 = (VariableExpression) scheme2;

            Expression e1 = (Expression)instanceSet.get(exp1);
            Expression e2 = (Expression)instanceSet.get(exp2);

            if ((e1 == null) || (e2 == null)) {
                throw new ProcessException(toString() + " : cannot attempt to apply without instance for the two schemes");
            }
            if (!e1.equals(e2)) {
                continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
            }
        }
        return;
    }
}
