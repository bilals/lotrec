package lotrec.dataStructure.tableau.action;

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

import java.util.*;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;

/**
When applied, marks an expression in a node with "hide" so that it will not be displayed
@author David Fauthoux
 */
public class HideAction extends AbstractAction {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates an action which will hide the expression in the specified node
    @param nodeScheme the scheme representing the node
    @param expressionScheme the expression to hide
     */
    @ParametersTypes(types = {"node", "formula"})
    @ParametersDescriptions(descriptions = {"The node where \"formula\" parameter should be found and made hidden (i.e. the formula reamins, but will not be displayed). This node should be already instanciated by other conditions or created by other actions",
"Formula's expression to be made hidden (i.e. the formula reamins, but will not be displayed) in \"node\" parameter"
})
    public HideAction(SchemeVariable nodeScheme, Expression expressionScheme) {
        super();
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
    }

    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);
        if (n == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " : cannot apply action without instance for node");
        }

        Expression e = expressionScheme.getInstance(instanceSet);
        /* ADDED 00/12/10 */ if (e == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply action, cannot instanciate expression: " + expressionScheme);
        }

        for (Enumeration enumr = n.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
            MarkedExpression ex = (MarkedExpression) enumr.nextElement();
            if (e.equals(ex.expression)) {
                ex.mark("hide");
//                System.out.println("hide:" + ex.expression);
            }
        }
        return instanceSet;
    }
}
