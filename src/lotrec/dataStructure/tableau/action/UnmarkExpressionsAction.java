package lotrec.dataStructure.tableau.action;

import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
When applied, unmarks all expression of the specified form in the node
@author David Fauthoux
 */
public class UnmarkExpressionsAction extends AbstractAction {

    private SchemeVariable nodeScheme;
    private Expression form;
    private Object marker;

    /**
    Creates an action which will unmark the specified object
    @param nodeScheme the scheme representing the node in which expressions will be marked
    @param marker the marker to unmark the specified object
    @param form the form to recognize expressions to mark
     */
    @ParametersTypes(types = {"node", "formula", "mark"})
    @ParametersDescriptions(descriptions = {"The node in which the indicated \"mark\" will be removed from the annotations' list of the \"formula\" parameter. It should be already instanciated by other conditions or created by other actions",
"The formula that will be un-marked. It shoul be already instanciated by other conditions or created by other actions, and should belong to the instanciated \"node\"",
"The annotation to be removed from the annotations' list of the \"formula\" parameter"
})
    public UnmarkExpressionsAction(SchemeVariable nodeScheme, Expression form, Object marker) {
        super();
        this.nodeScheme = nodeScheme;
        this.marker = marker;
        this.form = form;
    }

    /**
    Unmarks the expressions in the concrete node represented in the <code>InstanceSet</code> by the scheme specified in the constructor.
    If the specified marker is an instance of <code>SchemeVariable</code>, then its concrete reference will be search in then modifier.
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

        Object instanceMarker = marker;
        if (marker instanceof SchemeVariable) {
            instanceMarker = instanceSet.get((SchemeVariable) marker);
            if (instanceMarker == null) {
                instanceMarker = marker;
            }
        }

        n.unmarkAllExpressions(instanceSet, form, instanceMarker);
        return instanceSet;
    }
}
