package lotrec.process;

import java.util.*;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;

/**
Pushes received events, in order to pop them later. This containing class enables unsimultaneous event processing, late-comer event processing.
@author David Fauthoux
 */
public final class Queue implements ProcessListener, Duplicateable {

    protected LinkedList eventQueue;

    /**
    Creates an empty queue, ready to listen events.
     */
    public Queue() {
        eventQueue = new LinkedList();
    }

    /**
    Handles the received event, pushing it in a temporary bag.
    @param e the event to handle
     */
    public void process(ProcessEvent e) {
        eventQueue.addLast(e);
    }

    /**
    Returns and forgets the later received event
    @return the later event
     */
    public ProcessEvent getEvent() {
        return (ProcessEvent) eventQueue.removeFirst();
    }
    
//    public void reAddEvent(ProcessEvent e){
//        eventQueue.addFirst(e);
//    }

    /**
    Says if any event has been received
    @return true if the event queue is empty, false if any event has been received and stocked
     */
    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }

    public Queue emptyTheQueue(){
        Queue another = new Queue();
        while(!this.isEmpty()){
            another.process(this.getEvent());
        }
        return another;
    }

    public int size() {
        return eventQueue.size();
    }
    //duplication
    /**
    Creates a queue with the toDuplicate's duplicateable events. <b>A call to completeDuplication will duplicate the events. A call to the translateDuplication method will translate the events in order to reference the duplicated objects.</b>
    @param toDuplicate queue to duplicate
     */
    public Queue(Queue toDuplicate) {
        eventQueue = (LinkedList) toDuplicate.eventQueue.clone();
    }

    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        for (int i = 0; i < eventQueue.size(); i++) {
            ProcessEvent event = (ProcessEvent) ((Duplicateable) eventQueue.get(i)).duplicate(duplicator);
            event.completeDuplication(duplicator);
            eventQueue.set(i, event);
        }
    }

    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        for (int i = 0; i < eventQueue.size(); i++) {
            ((ProcessEvent) eventQueue.get(i)).translateDuplication(duplicator);
        }
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new Queue(this);
        duplicator.setImage(this, d);
        return d;
    }
}
