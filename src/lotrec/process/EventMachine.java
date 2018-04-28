package lotrec.process;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import java.util.Vector;
import java.util.Enumeration;
import java.util.LinkedList;
import lotrec.dataStructure.expression.InstanceSet;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.action.DuplicateAction;

/**
 * This class is the meeting of the work and the event processing. It is able to synchronize the event processing.
 * <p>
 * <p>It listens event and works on them.
 * <p>To work on event, the event machine calls any event eaters service.
 * When an event is received, the machine finds the eaters capable to eat this type of event and calls the eat method.
 * <p>The eaters query any actions, putting them in an <code>ActionStocking</code>, delivered by the machine.
 * <p>
 * <p>How does the machine work ?
 * <p>It listens event but does not manage them immediatly. The machine keeps the received events in a <code>Queue</code> in order to be able to manage them when necessary.
 * <p>When the machine's user calls its work method, the machine empties the queue, sending the event to the capable eaters,
 * and finally, the machine empties the bag that contains the actions, calling <code>ActionStocking#unstockAll</code>.
 * <p>This way of work ensures that the event processing is consistent. All the events are fairly managed. All the capable eaters are called. All the actions are executed.
 * <p>
 * <p>Why synchronisation ?
 * <p>The machine does not manage any event until the machine's user calls the <code>work</code> method.
 * So, the user can ensure that the work made by the eaters is done only when it is required.
 * <p>
 * <p>Definition here of <i>one step</i> work :
 * <p>To ensure fair managing of the events, it is necessary to manage <i>all</i> the received events when the user calls the work method.
 * <p>This implies the work is done step by step : until the machine's work is called, the machine keeps the event. Then the machine works on all the event received, returns, and continue keeping the received events until another work call.
 * <p>This way of managing enables users to fit parts of its strategy,
 * to order their machines however they wants, keeping in mind the strict specification of the event machines.
 */
public final class EventMachine extends AbstractWorker implements ProcessListener {

    //transient
    private ActionStocking actionStocking;
    //transient 
    private Queue queue;
    private Vector<SingleEventEater> eaters;
//    public boolean commutative;
    private boolean applyOnOneOccurence = false;
    private boolean currentEventHasFinished = false;
    private boolean currentEaterHasFinished = false;
    // allEventsInCurrentQueueHadFinished is equivalent to (currentQueue.isEmpty() && currentEvent == null)
    private boolean allEventsInCurrentQueueHadFinished = false;
    private Queue currentQueue;
    private ProcessEvent currentEvent;
    private SingleEventEater currentEater;
    private ArrayList<InstanceSet> currentEaterModifiersList = new ArrayList<InstanceSet>();
    private ArrayList<InstanceSet> currentQueueModifiersList = new ArrayList<InstanceSet>();
    private LinkedList<SingleEventEater> currentEatersList;
    // TO Be REPLACED!!!!
    private int level;
    private Rule relatedRule;

    /**
     * Creates an empty machine, ready to manage the listened events, ready to manage capable eaters.
     */
    public EventMachine() {//boolean commutative) {
        actionStocking = new ActionStocking();
        queue = new Queue();
        eaters = new Vector();
//        this.commutative = commutative;
        applyOnOneOccurence = false;
    }

    //Added by Bilal
    public EventMachine(String ruleName) {//, boolean commitative) {
        actionStocking = new ActionStocking();
        queue = new Queue();
        eaters = new Vector();
//        this.commutative = commitative;
        this.setWorkerName(ruleName);
        applyOnOneOccurence = false;
    }

    //Added by Bilal: the current used one
    public EventMachine(Rule rule) {
        relatedRule = rule;
        actionStocking = new ActionStocking();
        queue = new Queue();
        eaters = new Vector();
//        this.commutative = commitative;
        this.setWorkerName(rule.getName());
        applyOnOneOccurence = false;
    }

    /**
     * Adds the specified eater to the machine. The eater will receive event of the required type. The machine finds the required type using the <code>SingleEventEater#getEventId</code> method.
     * @param eater the eater to add
     */
    public void add(SingleEventEater eater) {
        //Added by bilal
        //System.out.println("An eater has been added to te EM of rule: " + this.ruleName);
        eaters.add(eater);
    }

    private void getNextEvent() {
        //each time the currentQueue is emptied
        if (currentEvent == null) {
            if (queue != null && !queue.isEmpty()) {
                currentQueue = queue.emptyTheQueue();
                currentEvent = currentQueue.getEvent();
                currentQueueModifiersList = new ArrayList<InstanceSet>();
                allEventsInCurrentQueueHadFinished = false;
            } else {
                allEventsInCurrentQueueHadFinished = true;
            //currentEvent = null;
            }
        } else {
            if (!currentEventHasFinished) { //changed by getNextEater
                //currentEvent = currentEvent;
            } else {
                if (!currentQueue.isEmpty()) {
                    currentEvent = currentQueue.getEvent();
                } else {
                    currentEvent = null;
                }
            }
        }
    }

//    private void getNextEvent() {
//        if (!currentEventHasFinished) { //changed by getNextEater
//            //currentEvent = currentEvent;
//        } else {
//            if (!queue.isEmpty()) {
//                currentEvent = queue.getEvent();
//            //By renewing the Modifiers list we will apply the rule where it has been alreday applied..
//                //And by not changing it at each event, we can change its name to "alreadyIdentifiedModifiers"
////                    currentQueueModifiersList = new ArrayList<InstanceSet>();
//            } else {
//                currentEvent = null;
//            }
//        }
//    }
    private void getNextEater() {
        //It happens each time a new fresh event is starting
        if (currentEater == null) {
            copyEaters();
            currentEater = currentEatersList.removeFirst();
            currentEaterModifiersList = new ArrayList<InstanceSet>();
            currentEventHasFinished = false;
        } else {
            if (!currentEaterHasFinished) { //changed in workOnce loop
                //currentEater = currentEater;
                // no change to currentEaterModifiersList
            } else {
                if (!currentEatersList.isEmpty()) {
                    currentEater = currentEatersList.removeFirst();
                    currentEaterModifiersList = new ArrayList<InstanceSet>();
                } else {
                    currentEventHasFinished = true;
                    currentEater = null;
                }
                currentEaterHasFinished = false;
            }
        }
    }

    private void copyEaters() {
        currentEatersList = new LinkedList<SingleEventEater>();
        for (SingleEventEater eater : eaters) {
            currentEatersList.addLast(eater);
        }
    }

    public void workOnce() {
        //Discativated when benchmarking
        //------------------------------
        treatStopPauseResume();
        //------------------------------
        worked = false;
        if (allEventsInCurrentQueueHadFinished && !queue.isEmpty()) {
            allEventsInCurrentQueueHadFinished = false;
        // allEventsInCurrentQueueHadFinished is equivalent to (currentQueue.isEmpty() && currentEvent == null)
        }
        while (!worked && !allEventsInCurrentQueueHadFinished) {
            getNextEvent();
//            System.out.println("Chosen event is: " + currentEvent);
            if (currentEvent == null) {
//                System.out.println("Queue Modifiers List is: " + this.getCurrentQueueModifiersList());
                continue;//getNextEvent will give it another try with queue, then we will stop because of finished.
            } else {
                while (!worked) {
                    getNextEater();
//                    System.out.println("Chosen eater is: " + currentEater);
                    if (currentEater == null) {
                        break;
                    } else {
                        if (currentEater.getEventId() == currentEvent.getType()) {
                            currentEater.eat(currentEvent, actionStocking, this);
//                           getEngine().getBenchmarker().increaseNbTentativesLoTREC(1);
                        }
                        if (actionStocking.isEmpty()) { //i.e. no change
                            currentEaterHasFinished = true;
                            continue;//let consider the next eater
                        } else {
                            //Add the (matched pattern) modifier to currentEaterModifiersList
                            //There should be only one at a time!!
                            for (ActionPack actionPack : actionStocking.getPacks()) {
                                currentEaterModifiersList.add((InstanceSet) actionPack.getModifier());
                                currentQueueModifiersList.add((InstanceSet) actionPack.getModifier());
                            }
                            //Execute... (will be called outside the loops)
                            worked = true; // Obligatory to stop the loop..
                        }
                    }
                }
            }
        }
//        getEngine().getBenchmarker().increaseNbTentativesClassic(getRelatedTableau().getNbFormulas());
        execute();
    }

    /**
     * Empties the event queue, calling the capable eaters on the events, finally executes all the actions required by the eaters.
     */
    @Override
    public void work() {
//        if (this.getWorkerName().equals("Or")) {
//            System.out.println();
//            System.out.println(getEngine().getBenchmarker().getNbTentativesLoTREC() + " " + getEngine().getBenchmarker().getNbTentativesClassic());
//            System.out.println();
//        }
        if(eaters.size() == 0){
            actionStocking.stock(new ActionPack(relatedRule.getActionContainer(), new InstanceSet()));
        }
        if (applyOnOneOccurence) {
            workOnce();
        } else {
            //Discativated when benchmarking
            //------------------------------
            treatStopPauseResume();
            //------------------------------

            //System.out.println("EM of rule : " + this + " is trying to work...");
            worked = false;
            if (queue == null) {
                //System.out.println("--EM of rule : " + this + " has not worked: empty events' queue");
                return;
            }
            //the set of actions to be executed at the end of the rule verification
            //(i.e. after treating all the events queued since the last iteration)
//        if (actionStocking == null) {
//            actionStocking = new ActionStocking();
//        }

//            getEngine().getBenchmarker().increaseNbTentativesClassic(getRelatedTableau().getNbFormulas());
            while (!queue.isEmpty()) {
                //System.out.println("--EM of rule : " + this + " has worked (queue not empty)...");
                ProcessEvent e = queue.getEvent();
//                if (e.getType() == ExpressionEvent.EXPRESSION_ADDED) {
//                    getEngine().getBenchmarker().increaseNbTentativesLoTREC(1);
//                }
//                System.out.println("[Normal] Chosen event is: " + e);
                //System.out.println("--EM of rule : " + this + " is working on the event:" + e);
                for (Enumeration enumr = eaters.elements(); enumr.hasMoreElements();) {
                    SingleEventEater eater = (SingleEventEater) enumr.nextElement();
                    //An eater Ea of type T is called to eat an event Ev of the same type T
                    if (eater.getEventId() == e.getType()) {
//                        getEngine().getBenchmarker().increaseNbTentativesLoTREC(1);
                        int packsLastSize = actionStocking.getPacksSize();
                        //The applyOnce should be treated inside this call!!!
                        //To stop after first succesful pattern, then restablish
                        //the pattern matching process from where it was suspended..
                        eater.eat(e, actionStocking, this);
                        //System.out.println("--EM of rule : " + this + " has worked (queue not empty, matching eater)...");
                        //System.out.println("--Event " + e + " has worked...");
                        /**
                         * If an eater has worked, "actionStocking" size would have been increased
                         * And then we can break the "for(.. each eater ..)" loop
                         * In fact, when an eater (i.e. activator) is activated, if it identifies a succefull instanciated pattern,
                         * that means that all the other eaters (i.e. activators in the same Local Search Plan)
                         * were invited to match, verify and complete the pattern, and that they had succeded.
                         * And that means that starting with another eater of the same type will not give back any other new patterns!!
                         * So giving the same event to such other activators will launch the SAME work all again, and thus,
                         * the for-each-eater loop would be better interrupted..
                         */
                        int packsNewSize = actionStocking.getPacksSize();
                        if (packsLastSize != packsNewSize) {
                            // Could be more apropriate if replaced by
                            // an "iF THE EATER HAS WORKED.."
                            /*
                             * DO NOT BREAK!! The above reson "If an eater has worked ..." is not correct!!
                             * Counter example:
                             * suppose you have a rule: isLinked n1 n2 R, isLinked n2 n3 R, then link n1 n3 R
                             * if you create N1 and N2 & link N1 to N1, N1 to N2, N2 to N2
                             * then call the rule,
                             * then create N3, link N3 to N3 & N2 to N3
                             * then the rule is applicable because of n1=N2 n2=N3 n3=N3
                             * then it breaks!! without considering the event (link N2 to N3)
                             * with the condition (isLinked n2 n3 R) that may lead to n2=N2 n3=N3 & n1=N1
                             *
                             * Bilal: 3 July 2009 bug reported by Marcio Moretto Ribeiro
                             */
                            //break;//To consider another event..

                            /*
                             * The right reason to break is:
                             * when an eater eats an event and yields a partially instantiated pattern
                             * of the form: X1=V1,...,Xi=Vi which is a subset of an already
                             * fully instantiated pattern X1=V1,...,Xn=Vn then we can leave
                             * the rest of the instantiation process.
                             */
                        }
                    }
                }
            //System.out.println("EM of rule : " + this + " has worked on the event:" + e);
            }
            execute();
        //System.out.println("EM of rule : " + this + " end work...");
        }
    }

    private void execute() {
        if (!actionStocking.isEmpty()) {
            //Discativated when benchmarking
            //------------------------------
            treatStepPauseResume();
            //------------------------------

            worked = true;

            //Discativated when benchmarking
            //------------------------------
            getEngine().increaseAppliedRules();
            updateEngineInfos();
            System.out.println("  Rule  " + this + "  has worked on  " +
                    this.getRelatedTableau().getName() + " ");
            //------------------------------
            actionStocking.callerHasWorked = true;
        } else {
            worked = false; // could be omitted since "false" is "worked" initial value
            actionStocking.callerHasWorked = false;
        // System.out.println("  Rule  " + this + "  has not worked on  " +
        //         this.getRelatedTableau().getName() + " ");
        }
        DuplicateAction.initialRelatedTableau = this.getRelatedTableau();
        actionStocking.setCallerEM(this);
        actionStocking.setCurrentTableau(this.getRelatedTableau());
        actionStocking.unstockAll();//(commutative); // assure l'atomicite grace au 'alsoUnstock'
        //Discativated when benchmarking
        //------------------------------
        if (actionStocking.getTotalAppliedRules() != 0) {
            getEngine().setTotalAppliedRules(getEngine().getTotalAppliedRules() + actionStocking.getTotalAppliedRules());
            getEngine().updateTotalAppliedRules();
        }
    //------------------------------
    }

    /**
     * Tests whether another call to work is required.
     * @return false if the machine is not quiet, i.e. has received any events and has not yet managed them
     *                                                 or if its actionStocking is not empty yet
     *         true otherwise.
     */
    @Override
    public boolean isQuiet() {
        //This is very worrying according to the documentation!!!
//        if (queue == null) {
//            return true;
//        }
        if(eaters.size() == 0){
            return false;
        }
        if(!actionStocking.isEmpty()){
            return false;
        }
        if (this.isApplyOnOneOccurence()) {
            if (queue.isEmpty() && currentQueue.isEmpty() && currentEvent == null) {
//                System.out.println("--------------------------------------------");
//                System.out.println("Rule " + this + " can NOT work and allEventsHadFinished=" + this.allEventsInCurrentQueueHadFinished +
//                        ", queue.isEmpty= "+queue.isEmpty()+", currentQueue="+(currentQueue!=null ? currentQueue.isEmpty():currentQueue));
//                System.out.println("********************************************");
                return true;
            } else {
//                System.out.println("--------------------------------------------");
//                System.out.println("Rule " + this + " can work and allEventsHadFinished=" + this.allEventsInCurrentQueueHadFinished+
//                        ", queue.isEmpty= "+queue.isEmpty()+", currentQueue="+(currentQueue!=null ? currentQueue.isEmpty():currentQueue));
//                System.out.println("********************************************");
                return false;
            }
        } else {
            return queue.isEmpty();//isEmpty <=> Quiet, in fact: isEmpty => nothing to do..
        }
    }

    /**
     * Listens process events, keeping them in a queue,
     * in order to use them when the machine user will call the
     * <code>work</code> method.
     */
    @Override
    public void process(ProcessEvent event) {
        if (queue == null) {
            queue = new Queue();
        }
        queue.process(event);
    }

    //duplication
    /**
     * Creates an event machine with the toDuplicate's fields.
     * <b> Only the bag where the machine stocks the actions and the event queue are duplicated and translated.
     * The eaters are not duplicated, because their states does not influence their work.</b>
     * @param toDuplicate machine to duplicate
     */
    public EventMachine(EventMachine toDuplicate) {
        super(toDuplicate);
        this.relatedRule = toDuplicate.relatedRule;
        eaters = (Vector) toDuplicate.eaters.clone();
        actionStocking = toDuplicate.actionStocking;
        queue = toDuplicate.queue;
//        commutative = toDuplicate.commutative;
        level = toDuplicate.level;
        applyOnOneOccurence = toDuplicate.applyOnOneOccurence;
        this.allEventsInCurrentQueueHadFinished = toDuplicate.allEventsInCurrentQueueHadFinished;
        this.currentEaterHasFinished = toDuplicate.currentEaterHasFinished;
        this.currentEventHasFinished = toDuplicate.currentEventHasFinished;
        this.currentEater = toDuplicate.currentEater;
        this.currentEvent = toDuplicate.currentEvent;
        if (toDuplicate.currentEatersList != null) {
            this.currentEatersList = (LinkedList<SingleEventEater>) toDuplicate.currentEatersList.clone();
        }
        this.currentEaterModifiersList = (ArrayList<InstanceSet>) toDuplicate.currentEaterModifiersList.clone();
        this.currentQueueModifiersList = (ArrayList<InstanceSet>) toDuplicate.currentQueueModifiersList.clone();
        this.currentQueue = toDuplicate.currentQueue;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        actionStocking = (ActionStocking) actionStocking.duplicate(duplicator);
        actionStocking.completeDuplication(duplicator);
        queue = (Queue) queue.duplicate(duplicator);
        queue.completeDuplication(duplicator);
        if (currentQueue != null) {
            currentQueue = (Queue) currentQueue.duplicate(duplicator);
            currentQueue.completeDuplication(duplicator);
        }
        if (currentEvent != null) {
            ProcessEvent event = (ProcessEvent) ((Duplicateable) currentEvent).duplicate(duplicator);
            event.completeDuplication(duplicator);
            currentEvent = event;
        //currentEvent.completeDuplication(duplicator);// Does nothing for the moment..
        }
        for (int i = 0; i < currentEaterModifiersList.size(); i++) {
            InstanceSet is = (InstanceSet) currentEaterModifiersList.get(i).duplicate(duplicator);
            is.completeDuplication(duplicator);
            currentEaterModifiersList.set(i, is);
        }
        for (int i = 0; i < currentQueueModifiersList.size(); i++) {
            InstanceSet is = (InstanceSet) currentQueueModifiersList.get(i).duplicate(duplicator);
            is.completeDuplication(duplicator);
            currentQueueModifiersList.set(i, is);
        }
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        //System.out.println("---------- TRANSLATE RULE WORKING --------" + this + " Of the tebleau: " + ((CommonDuplicator) duplicator).getDuplicatedSource());
        super.translateDuplication(duplicator);
        actionStocking.translateDuplication(duplicator);
        queue.translateDuplication(duplicator);
        if (currentQueue != null) {
            currentQueue.translateDuplication(duplicator);
        }
        if (currentEvent != null) {
            currentEvent.translateDuplication(duplicator);
        }
        for (InstanceSet is : currentEaterModifiersList) {
            is.translateDuplication(duplicator);
        }
        for (InstanceSet is : currentQueueModifiersList) {
            is.translateDuplication(duplicator);
        }
    //System.out.println("---------- TRANSLATE RULE DONE --------" + this + " Of the tebleau: " + ((CommonDuplicator) duplicator).getDuplicatedImage());
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new EventMachine(this);
        duplicator.setImage(this, d);
        return d;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isApplyOnOneOccurence() {
        return applyOnOneOccurence;
    }

    public void setApplyOnOneOccurence(boolean applyOnOneOccurence) {
        this.applyOnOneOccurence = applyOnOneOccurence;
//        this.actionStocking.setApplyOnOneOccurence(applyOnOneOccurence);
    }

    private void treatStopPauseResume() {
        synchronized (getEngine()) {
            if (getEngine().shouldPause()) {
                getEngine().makePause();
                while (getEngine().shouldPause()) {
                    try {
                        getEngine().wait();
                    } catch (InterruptedException ex) {
                        System.err.println(ex.getMessage());
                    }
                }
                getEngine().makeResume();
            }
            if (getEngine().shouldStop()) {
                return;
            }
        //Ativated when benchmarking
        //------------------------------
//            if (getEngine().getEngineTimer().getCurrentElapsedTime() > 100000){
////                    && getEngine().getEngineTimer().getCurrentElapsedTime() < 100100) { //this can be activated during benchmarking to allow to continue after 100 seconds
//                    getEngine().getMainFrame().getTableauxPanel().makePause();
//            }
        //------------------------------
        }
    }

    private void treatStepPauseResume() {
        synchronized (getEngine()) {
            if (getEngine().isRunningBySteps()) {
                if (getEngine().getRulesBreakPoints().indexOf(this.getLevel()) != -1) {
                    getEngine().makeStepPause(this);
                    try {
                        getEngine().wait();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(EventMachine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    getEngine().makeStepResume(this);
                } else {
                    System.out.println("  Engine shouldn't step-pause at rule " + this);
                }
            }
        }
    }

    private void updateEngineInfos() {
        getEngine().updateTableauxCount();
        getEngine().updateLastAppliedRule(getWorkerName(), getRelatedTableau().getName());
        getEngine().updateAppliedRules();
    }

    /**
     * @return the currentEaterModifiersList
     */
    public ArrayList<InstanceSet> getCurrentEaterModifiersList() {
        return currentEaterModifiersList;
    }

    /**
     * @return the currentQueueModifiersList
     */
    public ArrayList<InstanceSet> getCurrentQueueModifiersList() {
        return currentQueueModifiersList;
    }
}
