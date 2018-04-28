package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.util.Marked;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of the marked object scheme. The pass succeeds if the object is NOT marked as specified.
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
<p>Important note : if the specified marker is an instance of <code>SchemeVariable</code>, then its concrete reference will be search in then modifier.
@author David Fauthoux
 */
public class NotMarkConstraint extends Restriction {

    private SchemeVariable markedScheme;
    private Object marker;

    /**
    Creates a not mark constraint, ready to be included in a restriction chain, or to begin a chain.
    @param markedScheme the scheme representing the marked object
    @param marker the marker of the specified object
     */
    public NotMarkConstraint(SchemeVariable markedScheme, Object marker) {
        this.markedScheme = markedScheme;
        this.marker = marker;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine)
            throws ProcessException {
        InstanceSet instanceSet = (InstanceSet) modifier;
        Marked m = (Marked) instanceSet.get(markedScheme);

        if (m == null) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for marked");
        }
        Object test = marker;

        if (marker instanceof SchemeVariable) {
            test = instanceSet.get((SchemeVariable) marker);
        }
        if (!m.isMarked(test)) {
            continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
        }
    }
}
