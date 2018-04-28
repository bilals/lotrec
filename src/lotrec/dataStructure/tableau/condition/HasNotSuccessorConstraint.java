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
public class HasNotSuccessorConstraint extends Restriction {

    private SchemeVariable nodeScheme;
    private Expression relation;

    /**
    Creates an expression match, ready to be included in a restriction chain, or to begin a chain.
    @param nodeScheme the scheme representing the node containing the expression
    @param expressionScheme the scheme representing the contained expression
    @see TableauNode
     */
    public HasNotSuccessorConstraint(SchemeVariable nodeScheme, Expression relation) {
        this.nodeScheme = nodeScheme;
        this.relation = relation;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {

        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);

        if (n == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for node");
        }
        Enumeration enumr = n.getAllSuccessors(relation, instanceSet);
        if (enumr.hasMoreElements()) {
            return;//This signifies that there is a successor
        }
        continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
    }
}
