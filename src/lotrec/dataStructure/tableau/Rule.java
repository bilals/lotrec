package lotrec.dataStructure.tableau;

import java.util.Vector;
import java.util.LinkedList;
import java.util.Enumeration;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.process.*;

/**
 * Keeps conditions and actions, and can create an event machine, able to manage these conditions and actions.
 * This useful class is written to reduce the work of the user, but it is not indispendable.
 *
 * @author David Fauthoux
 */
public class Rule implements java.io.Serializable {

    /**
     * Default name for the rules which are built with the empty constructor.
     */
    public static String defaultName = "Rule";
    private static int forName = 0;
    private String name;
    //At least, at this level, i.e. in this class "Rule",
    //The conditions & the actions will be the abstract ones
    //(I.e. the ones with attrinutes, i.e. the ones with meta infos)
    private Vector<AbstractCondition> conditions;
    private Vector<AbstractAction> actions;
    private ActionContainer actionContainer;
//    private boolean commutative = false;
    //The user definition comments...
    private String comment;

    /**
     * Creates an empty rule, ready to keep conditions and actions.
     * author SAID
     */
    public Rule() {
        setName("NewRule");
        conditions = new Vector();
        actions = new Vector();
        actionContainer = new ActionContainer();
    }

    /**
     * Creates an empty rule, ready to keep conditions and actions.
     */
    public Rule(boolean commutative) {
        this(defaultName + forName, commutative);
        forName++;

    }

    /**
     * Creates an empty rule, ready to keep conditions and actions.
     *
     * @param name the name of this rule
     */
    public Rule(String name, boolean commutative) {
        this.setName(name);
        conditions = new Vector();
        actionContainer = new ActionContainer();
//        this.setCommutative(commutative);
    }

    /**
     * Sets the name of this rule.
     * @param name the name of this rule
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this rule.
     * @return the name of this rule
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    /**
     * Adds the specified action to this rule
     * @param action the action to add
     */
    public void addAction(AbstractAction action) {
        actions.add(action);
        //The rest is for respecting the old way
        //of using the ActionContainer
        actionContainer.add(action);
    }

    /**
     * Removes the specified action from this rule
     * @param action the action to remove
     */
    public void removeAction(AbstractAction action) {
        actions.remove(action);
        //The rest is for respecting the old way
        //of using the ActionContainer
        actionContainer.remove(action);
    }

    /**
     * Adds the specified action to this rule
     * @param action the action to add
     */
    public void add(Action action) {
        actionContainer.add(action);
    }

    /**
     * Removes the specified action from this rule
     * @param action the action to remove
     */
    public void remove(Action action) {
        actionContainer.remove(action);
    }

    public ActionContainer getActionContainer() {
        return actionContainer;
    }

    /**
     * Adds the specified condition to this rule
     * @param condition the condition to add
     */
    public void addCondition(AbstractCondition condition) {
        conditions.add(condition);
    }

    /**
     * Removes the specified condition from this rule
     * @param condition the condition to remove
     */
    public void removeCondition(AbstractCondition condition) {
        conditions.remove(condition);
    }

    /**
     * Returns the conditions of this rule
     *
     * @return the conditions of this rule
     */
    public Enumeration getConditionsAsElements() {
        return conditions.elements();
    }

    /**
     * Returns the actions of this rule
     * @return the actions of this rule
     */
    public Enumeration getActionsAsElements() {
        return actionContainer.elements();
    }

    public Enumeration getAbstractActionsAsElements() {
        return actions.elements();
    }

    /**
     * Creates an event machine, built with the contained conditions and actions, ready to be included in a strategy
     *
     * @return an event machine for this rule
     */
    public EventMachine createMachine() {
//        EventMachine eventMachine = new EventMachine(this.getName(), isCommutative());
        EventMachine eventMachine = new EventMachine(this);//.getName());//, false);
//        System.out.println("--------------------------------------------------------------");
//        System.out.println("Rule '" + this.getName() + "' EM creation starting...");
        boolean noConditionActivator = true;
        for (Enumeration currentConditions = conditions.elements(); currentConditions.hasMoreElements();) {
            AbstractCondition currentCondition = (AbstractCondition) currentConditions.nextElement();
//            System.out.println("------------------------------------------");
//            System.out.println("-- Considering condition: '" + currentCondition.getCode() + "' ...");
            BasicActivator currentConditionActivator = currentCondition.createActivator();
            if (currentConditionActivator != null) {
//            currentConditionActivator.setEventMachine(eventMachine);
////                System.out.println("-- Condition activator created...");
                ///////////////We do not need the following until...
//                Restriction currentConditionRestriction = currentCondition.createRestriction();
//////                System.out.println("-- Condition restriction created...");
//                if (currentConditionRestriction != null) {
////                    currentConditionRestriction.setEventMachine(eventMachine);
//                    currentConditionActivator.chain(currentConditionRestriction);
//////                System.out.println("-- Condition restriction chained in its activator...");
//                } else {
//////                            System.out.println("but: NOT ADDED to currentConditionActivator...");
//                }
                ///////////////....until here
                LinkedList<AbstractCondition> otherConditionsList = new LinkedList();
                LinkedList<AbstractCondition> thirdList = new LinkedList();

////                System.out.println("---- Preparing otherConditions...");
                for (Enumeration otherConditions = conditions.elements(); otherConditions.hasMoreElements();) {
                    AbstractCondition otherCondition = (AbstractCondition) otherConditions.nextElement();
                    if (otherCondition != currentCondition) {
                        otherConditionsList.add(otherCondition);
////                        System.out.println("-->> Other condition: '" + otherCondition.getCode() + "' added...");
                    }
                }
////                System.out.println("---- Finish preparing otherConditions...");

                Vector entry = currentCondition.getActivationSchemes();
                entry = currentCondition.updateSchemes(entry);
//                System.out.println("-->> Condition's Activator's Schemes Set initilized to: " + entry);

//                System.out.println("---- Completing Activator's Schemes Set...");
                while (!otherConditionsList.isEmpty()) {
                    AbstractCondition otherCondition = (AbstractCondition) otherConditionsList.removeFirst();
////                    System.out.println("-->> Other condition considered: '" + otherCondition.getCode() + "'...");
                    Vector newEntry = otherCondition.updateSchemes(entry);
////                    System.out.println("-->> Calculated newEntry is: " + newEntry);
                    if (newEntry == null) {
                        thirdList.add(otherCondition);
////                        System.out.print("-->> thirdList updated and becomes: [");
                        for (AbstractCondition c : thirdList) {
////                            System.out.print("cond: '" + c.getCode() + "', ");
                        }
////                        System.out.println("]");
                    } else {
                        entry = newEntry;
                        Restriction otherConditionRestriction = otherCondition.createRestriction();
                        if (otherConditionRestriction != null) { //This test is not needed, since chain is well coded in Restriction & BasicActivator classes
//                            otherConditionRestriction.setEventMachine(eventMachine);
                            currentConditionActivator.chain(otherConditionRestriction);
//                            System.out.println("-->> Other condition "+otherCondition.getCode()+" restriction added");
//                            System.out.println("-->> Activator's Schemes Set becomes: "+entry+"");
////                            System.out.println("and: ADDED to currentConditionActivator...");
                        } else {
////                            System.out.println("but: NOT ADDED to currentConditionActivator...");
                        }
                        while (!thirdList.isEmpty()) {
////                            System.out.println("-->> otherConditions SHOULD BE UPDATED AGAIN by thirdList:");
                            AbstractCondition c = thirdList.removeLast();
                            otherConditionsList.addFirst(c);
////                            System.out.println("  -->> cond: '" + c.getCode() + "' is trasfered..");
                        }
                    }
                }
//                System.out.println("---- Activator's Schemes Set completed....");

                if (!thirdList.isEmpty()) {
                    //on arrive au bout sans avoir inclu certaines restrictions !
                    //ce qui ne doit pas arriver!!
                    StringBuffer sb = new StringBuffer();
                    for (AbstractCondition cond : thirdList) {
                        sb.append(cond.getCode()+"\n");
                    }
                    throw new RuntimeException(
                            "It seems that some variabeles in the following conditions cannot be intantiated\n" +
                            sb.toString()+
                            "\nNotes:\n" +
                            "Conditions in LoTREC should describe a connected pattern.\n" +
                            "For example, it is possible to write: \"isLinked n1 n2 R\" and \"isLinked n2 n3 R\"\n" +
                            "but not to write: \"isLinked n1 n2 R\" and \"isLinked n3 n4 R\".\n\n" +
                            "In addition, to instantiate some variables of a given condition,\n" +
                            "some other BASIC variables have to be already instantiated by other conditions.\n" +
                            "For example, we cannot write \"hasElement n1 variable a\" and \"areEqual variable a variable b\"\n" +
                            "since both \"variable a\" and \"variable b\" have to be already instantiated by other conditions,\n" +
                            "before testing for their equality, whereas in the example only \"variable a\" can be instatiated\n" +
                            "by the other conditions.");
                }
                currentConditionActivator.add(actionContainer);
                eventMachine.add(currentConditionActivator);
                noConditionActivator = false;
            }//End If(currentActivator != null) 
            else {
//                System.out.println("-- Condition activator is NULL!!");
            }
        }
//        System.out.println("------------------------------------------");
        if (noConditionActivator) {
//            System.out.println("Rule '" + this.getName() + "' has no conditions...");
            // We should treat it as a warning only...
            //            throw new RuntimeException(
            //                    "Rule cannot be activated, please add a condition.");
        }
//        System.out.println("Rule '" + this.getName() + "' EM was successfully created...");
//        System.out.println("--------------------------------------------------------------");
        return eventMachine;
    }

//    public boolean isCommutative() {
//        return commutative;
//    }
//
//    public void setCommutative(boolean commutative) {
//        this.commutative = commutative;
//    }
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Vector<AbstractAction> getActions() {
        return actions;
    }

    public void setActions(Vector actions) {
        this.actions = actions;
    }

    public Vector<AbstractCondition> getConditions() {
        return conditions;
    }

    public void setConditions(Vector conditions) {
        this.conditions = conditions;
    }

    public boolean isUsed(Connector c) {
        for (AbstractCondition cond : conditions) {
            for (Parameter param : cond.getParameters()) {
                if ((param.getType().equals(ParameterType.FORMULA) ||
                        param.getType().equals(ParameterType.RELATION)) && ((Expression) param.getValue()).isUsed(c)) {
                    return true;
                }
            }
        }
        for (AbstractAction act : actions) {
            for (Parameter param : act.getParameters()) {
                if ((param.getType().equals(ParameterType.FORMULA) ||
                        param.getType().equals(ParameterType.RELATION)) && ((Expression) param.getValue()).isUsed(c)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getCode() {
        StringBuffer code = new StringBuffer("Rule " + this.getName() + "\n");
        for (AbstractCondition c : conditions) {
            code.append(" " + c.getCode() + "\n");
        }
        if (conditions.size() > 0 && actions.size() > 0) {
            code.append("\n");
        }
        for (AbstractAction a : actions) {
            code.append(" " + a.getCode() + "\n");
        }
        code.append("End");
        return code.toString();
    }
}
