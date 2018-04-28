package lotrec.dataStructure.tableau.condition;

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
public class HasNoParentsConstraint extends Restriction {

    private SchemeVariable nodeScheme;

    /**
    Creates an expression match, ready to be included in a restriction chain, or to begin a chain.
    @param nodeScheme the scheme representing the node containing the expression
    @param expressionScheme the scheme representing the contained expression
    @see TableauNode
     */
    public HasNoParentsConstraint(SchemeVariable nodeScheme) {
        this.nodeScheme = nodeScheme;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {

        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);

        if (n == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for node");
        }
        if (n.getLastEdgesEnum().hasMoreElements()) {
            return;//This signifies that this node has a parent node
        }//else, this means that this node has no parent nodes
        continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
    }
}
