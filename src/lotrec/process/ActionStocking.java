package lotrec.process;

import java.util.LinkedList;
import lotrec.util.Duplicateable;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicator;
import lotrec.dataStructure.expression.InstanceSet;
import lotrec.dataStructure.tableau.Tableau;

/**
The bag to contains later executed actions. Usually used by the <code>EventMachine</code>, this class stores the actions and their modifiers (instance for execution) in <code>ActionPack</code>, so, the user can execute actions whenever he wants, using the <i>unstockAll</i> method.
<p>Important : the duplication process is consistent in this class. When the user calls <i>unstockAll</i>, and, when in the executed actions, a duplication process in runned, the action stocking is duplicated.
And it can be duplicated before having unstocked all its actions. That's because a duplicated action stocking stores its reference in its duplication source : so, the being unstocked action stocking will be able to also unstock its images.
And the actions applying will be complete.
@see EventMachine
 */
public final class ActionStocking implements Duplicateable {

    public boolean callerHasWorked = false;//means the calling EventMachine has really worked
    public static boolean alsoUnpackStage = false;
    private LinkedList<ActionPack> packs;
    private LinkedList alsoUnpack;
    private int totalAppliedRules;
    // currentTableau is always the tableau of the current rule,
    // except when one of the actions of the rule makes a duplication
    // in this case, the currentTableau used in unstockAll()
    // changes after calling alsoUnpack()..
    //It will be the duplicata of the currentTableau
    private Tableau currentTableau;
    private EventMachine callerEM;
//    private boolean applyOnOneOccurence = false;

    /**
    Creates an empty action stocking, ready to store actions and their modifiers
     */
    public ActionStocking() {
        packs = new LinkedList();
        alsoUnpack = new LinkedList();
    }

    /**
    To store an action to be applied later, use this method packing the action and its modifier in an action pack.
    @param actionPack the action pack keeping the action and its modifier (instance of execution)
     */
    public void stock(ActionPack actionPack) {
        packs.add(actionPack);
    }

    /*****/
    public boolean isEmpty() {
        return packs.isEmpty();
    }

    public void trie() {//boolean commitative) {
//        System.out.println("ActionStocking is: " + this);
        Object tab[] = packs.toArray();
        int n = tab.length;//not packs.size();!!!!!!!!!
        for (int i = 0; i < n - 1; i++) {
            for (int j = i + 1; j < n; j++) {
//                if (commitative) {
//                    System.out.println(tab[i]+" and "+tab[j]);
//                    System.out.println(" Are equal commutatively is:" + ((InstanceSet) (((ActionPack) tab[i]).getModifier())).equalsCommutatively(((ActionPack) tab[j]).getModifier()));
//                    System.out.println(" Are equal commutatively V2 is:" + ((InstanceSet) (((ActionPack) tab[i]).getModifier())).equalsCommutativelyV2(((ActionPack) tab[j]).getModifier()));
//                }
                if (//(commitative && ((InstanceSet) (((ActionPack) tab[i]).getModifier())).equalsCommutatively(((ActionPack) tab[j]).getModifier())) ||
                        (((InstanceSet) (((ActionPack) tab[i]).getModifier())).equals(((ActionPack) tab[j]).getModifier()))) {
//                    System.out.println(tab[j] + " will be deleted..");
                    packs.remove(tab[j]);
                }
            }
        }
//        System.out.println("ActionStocking becomes: " + this);
    }

    /**
    Opens this bag : unstock all the action packs, opening them and applying actions with their modifiers.
    This method is consistent and complete with the duplication process : the duplicated action stocking are known and are able to be also unstocked here, without a necessary call to the duplicated action stocking by external object.
    All is done here, the user doesn't know the duplication consistency needs, sequence or laws.
     */
    public void unstockAll() {//boolean commitative) {
        //trie(commitative);
        trie();

//        if (callerHasWorked) {
//            System.out.println("  Before Unpack: " + this);
//            while (!packs.isEmpty()) {
//                ActionPack ap = (ActionPack) packs.removeFirst();
//                ap.unpack();
//
//            }
//            System.out.println("  After Unpack: " + this);
//            while (!alsoUnpack.isEmpty()) {
//                ((ActionStocking) alsoUnpack.removeFirst()).unstockAll(commitative);
//            }
//            System.out.println("  After Also Unpack: " + this);
//        } else {
        totalAppliedRules = 0;
        while (!packs.isEmpty()) {
            ActionPack ap = (ActionPack) packs.removeFirst();
            ap.unpack(this.callerEM);
            totalAppliedRules++;
//            if (this.isApplyOnOneOccurence()) {
//                this.packs.clear();
//                //What about alsoUnpack? Should not be cleared too?!
//                return;
//            }
        }

//        while (!alsoUnpack.isEmpty()) {
//            ActionStocking duplicata = (ActionStocking) alsoUnpack.removeFirst();
//            duplicata.unstockAll();//commitative);
////            totalAppliedRules += duplicata.getTotalAppliedRules();
//        }

//        }
    }

    public Object removeLast() {
        return packs.removeLast();
    }

    public LinkedList<ActionPack> getPacks() {
        return packs;
    }
    //Bill Change

    public int getPacksSize() {
        return packs.size();
    }
    //Bill Change

    //duplication
    /**
    Creates a action stocking with the toDuplicate's packs.
    <b> The packs will be duplicated and the contained modifier will be translated.</b>
    @param toDuplicate the action stocking to duplicate
     */
    public ActionStocking(ActionStocking toDuplicate) {
        packs = (LinkedList) toDuplicate.packs.clone();
//        alsoUnpack = new LinkedList();
//        toDuplicate.alsoUnpack.add(this);
        currentTableau = toDuplicate.currentTableau;
        callerEM = toDuplicate.callerEM;
//        applyOnOneOccurence = toDuplicate.applyOnOneOccurence;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        for (int i = 0; i < packs.size(); i++) {
            ActionPack a = (ActionPack) ((Duplicateable) packs.get(i)).duplicate(duplicator);
            a.completeDuplication(duplicator);
            packs.set(i, a);
        }
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        if (currentTableau != null) {
            currentTableau = (Tableau) duplicator.getImage(currentTableau);
        }
        if(callerEM != null){
            callerEM = (EventMachine) duplicator.getImage(callerEM);
        }
        for (int i = 0; i < packs.size(); i++) {
            ((ActionPack) packs.get(i)).translateDuplication(duplicator);
        }
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new ActionStocking(this);
        duplicator.setImage(this, d);
        return d;
    }

    @Override
    public String toString() {
        String result = "ActionStocking [";
        for (int i = 0; i < packs.size(); i++) {
            ActionPack a = (ActionPack) packs.get(i);
            result = result + a.toString();
        }
        result = result + "]";
        return result;
    }

    //Should be called after unstockAll call to know the number of executed actions
    public int getTotalAppliedRules() {
        return totalAppliedRules;
    }

    /**
     * @param currentTableau the currentTableau to set
     */
    public void setCurrentTableau(Tableau currentTableau) {
        this.currentTableau = currentTableau;
    }

    /**
     * @return the callerEM
     */
    public EventMachine getCallerEM() {
        return callerEM;
    }

    /**
     * @param callerEM the callerEM to set
     */
    public void setCallerEM(EventMachine callerEM) {
        this.callerEM = callerEM;
    }
    /**
     * @return the applyOnOneOccurence
     */
//    public boolean isApplyOnOneOccurence() {
//        return applyOnOneOccurence;
//    }
    /**
     * @param applyOnOneOccurence the applyOnOneOccurence to set
     */
//    public void setApplyOnOneOccurence(boolean applyOnOneOccurence) {
//        this.applyOnOneOccurence = applyOnOneOccurence;
//    }
}
