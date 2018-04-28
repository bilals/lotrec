package lotrec.dataStructure.tableau.action;

import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.AbstractAction;
import lotrec.process.ProcessException;
import lotrec.dataStructure.tableau.*;
import lotrec.process.EventMachine;

/**
When applied, stops the strategy of the tableau (specified by a node belonging to it)
@author David Fauthoux
 */
public class StopStrategyAction extends AbstractAction {

    private SchemeVariable nodeScheme;

    /**
    Creates an action which will stop the strategy working on the tableau of the specified node
    @param nodeScheme the scheme representing the node in the stopped tableau
     */
    @ParametersTypes(types = {"node"})
    @ParametersDescriptions(descriptions = {"The node that the strategy should stop developping its tableau (mainly cause it becomes closed). It should be already instanciated by other conditions or created by other actions"})    
    public StopStrategyAction(SchemeVariable nodeScheme) {
        super();
        this.nodeScheme = nodeScheme;
    }

    /**
    Finds the concrete nodes in the modifier, gets the tableau and stops the strategy working on it.
    @param modifier the instance set used in the restriction process
    @return the unchanged modifier
     */
    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;

        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);
        if (n == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply without instance for node");
        }

        Tableau tableau = (Tableau) n.getGraph();
//        tableau.getStrategy()
        tableau.getStrategy().getEngine().stopTableau(tableau);
        return modifier;
    }
}
