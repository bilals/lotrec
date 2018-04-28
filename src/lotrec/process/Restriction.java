package lotrec.process;

/**
Tests the modifier object used in the process, completes it if necessary and passes the modifier in its next restriction (the restrictions can be chained).
If the restriction is at the end of the chain (no next), the restriction will put an <code>ActionPack</code> with the action and the current modifier in the <code>ActionStocking</code>.
<p><b>It is serializable because its state must not influence its work.</b>
@see BasicActivator
 */
public abstract class Restriction implements java.io.Serializable {

    private Restriction chained;

    /**
    Creates a restriction ready to be put in a <code>BasicActivator</code>, without any next restriction (end of the chain).
     */
    public Restriction() {
        chained = null;
    }

    /**
    Continues the chain with the specify restriction. If this restriction is not at the end, the specified restriction is chained to the next restriction of this.
    @param r the restriction to chain
     */
    public void chain(Restriction r) {
        if (chained == null) {
            chained = r;
        } else {
            chained.chain(r);
        }
    }

    /**
    The user of this class must use this method to continue the process :
    in the <i>attemptToApply</i> method, if the modifier passes this restriction,
    the user must send the completed modifier to the chain using <i>continueAttemptToApply(action, completedModifier, actionStocking)</i>.
    The user doesn't need to manage action or actionStocking.
    @param action the action that will be put in the bag (actionStocking) if the modifier passes the restriction chain
    @param modifier the modifier object used in the process, it is initialized by an <code>BasicActivator</code>, it must be tested and, if necessary, completed
    @param actionStocking the bag where the actions are temporarily put in order to be executed later    
     */
    public void continueAttemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) {
        if (chained == null) {
            if (!eventMachine.isApplyOnOneOccurence()) {
                actionStocking.stock(new ActionPack(action, modifier));
            } else {
//                System.out.println("Modifier is: " + modifier);
//                System.out.println("Eater Modifiers List is: " + eventMachine.getCurrentEaterModifiersList() +
//                        ", contains m: " + eventMachine.getCurrentEaterModifiersList().contains(modifier));
                if (!eventMachine.getCurrentEaterModifiersList().contains(modifier)) {
//                    System.out.println("Queue Modifiers List is: " + eventMachine.getCurrentQueueModifiersList() +
//                            ", contains m: " + eventMachine.getCurrentQueueModifiersList().contains(modifier));
                    if (!eventMachine.getCurrentQueueModifiersList().contains(modifier)) {
                        actionStocking.stock(new ActionPack(action, modifier));
                    } else {
                        //do nothing
                    }
                } else {
                    //nothing to do
                }
            }
//            if ((!eventMachine.isApplyOnOneOccurence()) ||
//                    (!eventMachine.getCurrentEaterModifiersList().contains(modifier))) {
//                actionStocking.stock(new ActionPack(action, modifier));
//            }
            return;
        }
        chained.attemptToApply(action, modifier, actionStocking,eventMachine);
    }

    /**
    Entry-point used by a <code>BasicActivator</code> if this restriction begins the restriction chain, or by the last restriction in the chain.
    Tests the modifier, if it passes this restriction specification, it is recursively passed thru the chain with the <i>continueAttemptToApply</i> method.
    @param action the action passed thru the restriction chain, the user should not care of this
    @param modifier <b>the object passed and completed thru the restriction chain, it keeps in mind the process, memorising all the needed history</b>
    @param actionStocking the bag where the action will be put in order to be execeuted later, the user should not care of this
     */
    public abstract void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine);

}
