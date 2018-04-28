package lotrec.dataStructure.tableau.action;

import lotrec.process.AbstractAction;
import lotrec.process.ProcessException;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.tableau.*;
import lotrec.process.EventMachine;

/**
When applied, removes from memory a tableau containing a specified node.
@author David Fauthoux
 */
public class KillAction extends AbstractAction {

    private SchemeVariable sourceNodeScheme;

    /**
    Creates an action which will remove from memory the tableau containing the specified node.
    User only specifies the scheme representing this node and this class will find it in the instance set (modifier), when <i>apply</i> method is called.
    @param sourceNodeScheme the scheme representing the node to get the tableau to remove
    @param strategy the global strategy from where the tableau strategy will be remove
     */
    @ParametersTypes(types = {"node"})
    @ParametersDescriptions(descriptions = {"The node that its tableau should be removed from the memory. It should be already instanciated by other conditions or created by other actions"})    
    public KillAction(SchemeVariable sourceNodeScheme) {
        super();
        this.sourceNodeScheme = sourceNodeScheme;
    }

    /**
    Finds the concrete node in the modifier, represented by sourceNodeScheme in the constructor, to get its tableau; and removes the tableau.
    @param modifier the instance set used in the restriction process
    @return the instance set completed with the destination schemes
     */
    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode n = (TableauNode) instanceSet.get(sourceNodeScheme);
        if (n == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply action without instance for node");
        }

        Tableau tableau = (Tableau) n.getGraph();
        (tableau.getWallet()).remove(tableau);
        // Should be treated wisely!!!
        tableau.getStrategy().getEngine().remove(tableau.getStrategy());
        //tableau.getStrategy().forceStop(true);
        return instanceSet;
    }
}
