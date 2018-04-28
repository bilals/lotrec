package lotrec.process;

import java.util.Vector;
import java.util.Enumeration;
import lotrec.dataStructure.tableau.Tableau;

/**
Contains and can execute many actions.
<p>In many cases, only one action can be used (in a restriction chain for example), but it is not restrictive.
Thanks to this class, the user can manage more than one actions in any process. Use an action container and add whatever action to it : it will execute them when its execution will be required.
@see BasicActivator
@see Restriction
 */
public class ActionContainer implements Action {

    private Vector actions;

    /**
    Creates an empty action container, ready to receive and execute actions.
     */
    public ActionContainer() {
        actions = new Vector();
    }

    /**
    Adds the specified action to this container. The action will be executed (applied) when this container will be executed (applied)
    @param action the action to add
     */
    public void add(Action action) {
        actions.add(action);
    }

    public void setElementAt(Action action, int index) {
        actions.setElementAt(action, index);
    }

    /**
    Remove the action from this container
    @param action the action to remove
     */
    public void remove(Action action) {
        actions.remove(action);
    }

    /**
    Returns the contained actions
    @return the contained actions
     */
    public Enumeration elements() {
        return actions.elements();
    }

    @Override
    public Object apply(EventMachine em, Object modifier) {
        Object m = modifier;
        for (Enumeration enumr = actions.elements(); enumr.hasMoreElements();) {
            m = ((Action) enumr.nextElement()).apply(em, m);
        }
        return m;
    }
}
