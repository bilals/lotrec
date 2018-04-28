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
public class HaveAllSuccessorExpressionMatch extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;
    private Expression relation;

    /**
    Creates a contains constraint, ready to be included in a restriction chain.
    The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
    @param bigNodeScheme the scheme representing the node N0
    @param smallNodeScheme the scheme representing the node N1
     */
    public HaveAllSuccessorExpressionMatch(SchemeVariable nodeScheme, Expression expressionScheme, Expression relation) {
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
        this.relation = relation;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking, EventMachine eventMachine) throws ProcessException {
        //boolean allSucc = true;
        InstanceSet instanceSet = (InstanceSet) modifier;

        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);

        if (n == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for node");
        }

        for (Enumeration enumra = n.getAllSuccessors(relation, instanceSet); enumra.hasMoreElements();) {
//            if (!allSucc) {
//                return;
//            }
            boolean onSucc = false;
            TableauNode successorNode = (TableauNode) enumra.nextElement();
            for (Enumeration enumr = successorNode.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
                Expression e = ((MarkedExpression) enumr.nextElement()).expression;
                InstanceSet newInstanceSet = expressionScheme.matchWith(e, instanceSet);
//                try {
                if (instanceSet.equals(newInstanceSet)) {
                    onSucc = true;
                    break;
                }
//                } catch (Exception ex) {
//                    System.out.println("Exception in HaveAllSuccessors: "+ex.getMessage());
//                }
            }
            if (!onSucc) {
//                allSucc = false;
                return;//at least  a successor has not  the expression
            }
//            onSucc = false;
        }
//
//        if (allSucc) {
        //We will reach the following instruction if there are no successors, or if there are one or more but all of them have the expression
        continueAttemptToApply(action, instanceSet, actionStocking, eventMachine);
//        }
    }
}
