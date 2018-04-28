package lotrec.process;

/**
Extends the activating managing with the restriction chain specification.
<p>An event will be handle an interpreted by the <i>createModifiers</i> method, and the it will be sent it the restriction chain.
<p>The chain will creates (or not) some actions and will put them in the bag (<code>ActionStocking</code>) in order to be consistently executed.
<p>The chain process will begin with the interpretation of the event, this <i>modifier</code> will pass thru the chain, being tested and modified. The modifier keeps in mind the chain process.
<p>The modifier is an unknown object here to keep free to implement the chain process model in different ways.
<p>An example is the matching process : an event activates the matching process with a new edge in the graph, the activator will tie the nodes with syntactic schemes of them. These schemes are able to be interpreted by the chain.
So the chain is able to test the nodes corresponding to the schemes, and is able to add (to the modifier, here a table which tie concret objects to schemes) some new interpretations between concrete objects and interpretable schemes.
 */
public abstract class BasicActivator extends Activator {

    private Restriction chained;
    private ActionContainer actionContainer;

    /**
    Creates an activator which activates a restriction chain on events of the specified type, when the chain is completed, the contained actions will be used.
    <p>By default, the chain is empty; the contained actions are immediately used and put in the bag <code>ActionStocking</code>. Use <i>chain</i> to complete it.
    <p>Initially, the activator does not contain action. Add some with <i>add(Action action)</i>
    @param eventId the type of the events to handle
     */
    public BasicActivator(int eventId) {
        super(eventId);
        chained = null;
        actionContainer = new ActionContainer();
    }

    public BasicActivator() {
    }
    ;

    /**
    Adds the specified action in order to be executed at the end of the process begun by this activator. The specified action is added in the <code>ActionContainer</i> of this activator.
    @param action the action to add
     */
    public void add(Action action) {
        actionContainer.add(action);
    }

    /**
    Chains the specify restriction to this activator. If the chain already exists, the specified restriction is chained to the end of the chain.
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
    The received event is interpreted via the createModifiers method and then sent to the chain
    @param event the event to handle
    @param actionStocking the bag where you can put actions to execute
    @see RestrictionChain#attemptToApply
     */
    @Override
    protected void activate(ProcessEvent event, ActionStocking actionStocking, EventMachine eventMachine) {
        //Identified pattern is: "m"
        Object[] modifiers = createModifiers(event);
        if (modifiers == null) {
            return;
        }
        //No (or No More) constraints (or restrictions) on the applicability of this rule (having "this" condition) on this pattern ("m")
        if (chained == null) {
            for (Object modifier : modifiers) {
                if (modifier != null) {
//            System.out.println("Modifier is: " + m);
//            System.out.println("Eater Modifiers List is: " + eventMachine.getCurrentEaterModifiersList() +
//                    ", contains m: " + eventMachine.getCurrentEaterModifiersList().contains(m));
                    if ((!eventMachine.isApplyOnOneOccurence()) ||
                            (!eventMachine.getCurrentEaterModifiersList().contains(modifier))) {
//                System.out.println("Queue Modifiers List is: " + eventMachine.getCurrentQueueModifiersList() +
//                        ", contains m: " + eventMachine.getCurrentQueueModifiersList().contains(m));
                        if (!eventMachine.getCurrentQueueModifiersList().contains(modifier)) {
                            actionStocking.stock(new ActionPack(actionContainer, modifier));
                        } else {
                            //do nothing
                        }
                    }
                }
            }
//        else: nothing to do
//        "else" means that the eventMachine is applyOnce
//        and its current eater has already detected the same pattern
//        It cannot happen in principle.. But in case where..
            return;
        }
        //There is a (or There is another) constraint (or restriction) on the applicability of this rule (having "this" condition) on this pattern ("m")
        for (Object modifier : modifiers) {
            if (modifier != null) {
                chained.attemptToApply(actionContainer, modifier, actionStocking, eventMachine);
            }
        }
    }

    /**
    Implement this method to specify the interpretation of the event.
    @param event the received event
    @return the object representing the interpretation of the event. This object should be understandable by the chain used in this activator.
     */
    public abstract Object[] createModifiers(ProcessEvent event);

    @Override
    public String toString() {
        return super.toString() + " action:" + actionContainer;
    }
}
