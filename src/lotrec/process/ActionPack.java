package lotrec.process;

import lotrec.dataStructure.tableau.Tableau;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;

/**
Keeps the action and the modifier used to apply the action, so the action can be applied later
@see ActionStocking
@see EventMachine
@author David Fauthoux
 */
public final class ActionPack implements Duplicateable {

    private Action action;
    private Object modifier;

    /**
    Creates an action pack, keeping the modifier
    @param action the action that will be applied later
    @param modifier the modifier that will be used to apply the action
     */
    public ActionPack(Action action, Object modifier) {
        this.action = action;
        this.modifier = modifier;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return modifier.toString();
    }

    public Object getModifier() {
        return modifier;
    }

    /**
    Usually called by an <code>ActionStocking</code> when the action needs to be applied. This methods calls action.apply(modifier)
     */
    public void unpack(EventMachine em) {
        action.apply(em, modifier);
    }

    public boolean equals(ActionPack ap) {
        if ((this.action.equals(ap.getAction())) && (this.modifier.equals(ap.getModifier()))) {
            return true;
        }
        return false;

    }

    /**
    Creates a action pack with the toDuplicate's fields.
    <b> The packs will be duplicated and the contained modifier will be translated.</b>
    @param toDuplicate the action stocking to duplicate
     */
    public ActionPack(ActionPack toDuplicate) {
        action = toDuplicate.action;
        modifier = toDuplicate.modifier;
    }

    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        modifier = ((Duplicateable) modifier).duplicate(duplicator);
        ((Duplicateable) modifier).completeDuplication(duplicator);
    }

    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        ((Duplicateable) modifier).translateDuplication(duplicator);
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new ActionPack(this);
        duplicator.setImage(this, d);
        return d;
    }
}
