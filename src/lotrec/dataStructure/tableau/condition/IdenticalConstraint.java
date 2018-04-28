package lotrec.dataStructure.tableau.condition;

import lotrec.process.*;
import lotrec.dataStructure.expression.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of two schemes. The pass succeeds if the schemes references the same object.
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class IdenticalConstraint extends Restriction {

    private SchemeVariable scheme1;
    private SchemeVariable scheme2;

    /**
    Creates an "identical" constraint, ready to be included in a restriction chain.
    @param scheme1 the first scheme
    @param scheme2 the second scheme
     */
    public IdenticalConstraint(SchemeVariable scheme1, SchemeVariable scheme2) {
        this.scheme1 = scheme1;
        this.scheme2 = scheme2;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {
        InstanceSet instanceSet = (InstanceSet) modifier;
        Object o1 = instanceSet.get(scheme1);
        Object o2 = instanceSet.get(scheme2);

        if ((o1 == null) || (o2 == null)) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for the two schemes");
        }
        if (o1 == o2) {
            continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
        }
        return;
    }
}
