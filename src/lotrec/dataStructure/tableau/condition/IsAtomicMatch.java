package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
 * Tests wether a given formula represents an atomic proposition
 * It should be a VariableExpression or a ConstantExpression 
 * (i.e. not a ExpressionWithSubExpression)
 * @author Bilal Said
 */
public class IsAtomicMatch extends Restriction {

    private Expression expressionScheme;

    /**
    Creates an expression match, ready to be included in a restriction chain, or to begin a chain.
    @param nodeScheme the scheme representing the node containing the expression
    @param expressionScheme the scheme representing the contained expression
    @see TableauNode
     */
    public IsAtomicMatch(Expression expressionScheme) {
        this.expressionScheme = expressionScheme;
    }

    @Override
    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {

        InstanceSet instanceSet = (InstanceSet) modifier;
        Expression e = expressionScheme.getInstance(instanceSet);
        if (e != null) {
            /* 
             * This test is equivalent to:
             * if ((e instanceof VariableExpression) || (e instanceof ConstantExpression) || (e instanceof ConstantNodeExpression))
             */
            if( ! (e instanceof ExpressionWithSubExpressions)) {
//                System.out.println(expressionScheme + " is a proposition cause " + e.getCodeString() + " is so");
                continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
            } else {
//                System.out.println(expressionScheme + " is not a proposition cause " + e.getCodeString() + " is not so");
            }
        } else {
//            System.out.println(expressionScheme + " is null!!");
        }
        return;
    }
}
