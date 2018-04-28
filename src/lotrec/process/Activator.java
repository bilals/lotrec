package lotrec.process;

/**
This class is a semantic class to explicit how to tie the event process and the action managing. It is the door between the event coming and the event managing.
<p>The event process is managed by a <code>Dispatcher</code> and any <code>ProcessListener</code>.
Usually (<code>BasicActivator</code>), you have to build a chain of <code>Restriction</code>, activated by an <code>Activator</code>. Then, you create a <code>EventMachine</code> to listen and send events to your activator.
<p>The chain will put actions to execute in a bag (an <code>ActionStocking</code>) in order to late their execution.
<p>It is also possible to implement his own special activator without managing any restriction chain (to optimized known processing for example).
To do that, just implement the <i>activate</i> method, with your own event managing and your own action managing.
<p>The class ensure to call the activate method with the correct type of event, specified in the constructor.
@see BasicActivator
@see Restriction
@see RestrictionChain
@see EventMachine
 */
public abstract class Activator implements SingleEventEater, java.io.Serializable {

    private int eventId;

    /**
    Specify in the construction the event type you want to receive.
    @param eventId the event type the class will react on
     */
    public Activator(int eventId) {
        this.eventId = eventId;
    }

    public Activator() {
    }

    /**
    Usually called by an <code>EventMachine</code>. Passes the event to the activate method.
    @param event the event sent in the event process
    @param actionStocking the bag to put action to execute later (according to the action specification)
     */
    @Override
    public void eat(ProcessEvent event, ActionStocking actionStocking, EventMachine eventMachine) {
        if (event.type != eventId) {
            throw new ProcessException(toString() + " : cannot eat " + event);
        }
        activate(event, actionStocking, eventMachine);
    }

    /**
    Implement this method to manage the received event. You should use the actionStocking to put actions you want to ensure correct and consistent execution, according to the extend event model.
    @param event the event to handle
    @param actionStocking the bag where you can put actions to execute
     */
    protected abstract void activate(ProcessEvent event, ActionStocking actionStocking, EventMachine eventMachine);

    /**
    Returns the type of the events this activator handles
    @return the type of the events
     */
    @Override
    public int getEventId() {
        return eventId;
    }

    @Override
    public String toString() {
        return "Activator(" + eventId + ")";
    }
}

