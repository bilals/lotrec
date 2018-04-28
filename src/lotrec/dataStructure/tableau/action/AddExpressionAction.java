package lotrec.dataStructure.tableau.action;

import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.AbstractAction;
import lotrec.process.ProcessException;
import lotrec.dataStructure.tableau.*;
import lotrec.process.EventMachine;

/**
When applied, adds an expression into a node if this node does not yet contain this expression
@author David Fauthoux
 */
public class AddExpressionAction extends AbstractAction {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates an action which will add the specified expression after having instanced it
    @param nodeScheme the scheme representing the node to add the expression
    @param expressionScheme the scheme representing the expression to instance and add
     */
    @ParametersTypes(types = {"node", "formula"})
    @ParametersDescriptions(descriptions = {"The node where \"formula\" parameter will be addded. It should be already instanciated by other conditions or created by other actions",
        "The new formula's expression to be added to the instanciated \"node\" parameter. Existing formulae are not duplicated"
    })
    public AddExpressionAction(SchemeVariable nodeScheme, Expression expressionScheme) {
        super();
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
    }

    /**
    Instances the scheme representing the expression to add with the modifier (must be an <code>InstanceSet</code>), and adds it to the concrete node represented in the <code>InstanceSet</code> by the scheme specified in the constructor.
    @param modifier the instance set used in the restriction process
    @return the unchanged modifier
     */
    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode n = (TableauNode) instanceSet.get(nodeScheme);
        if (n == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply action without instance for node with identifier: " + nodeScheme);
        }
        Expression e = expressionScheme.getInstance(instanceSet);
        /* ADDED 00/12/10 */
        if (e == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply action, cannot instanciate expression: " + expressionScheme);
        }
        if (!n.contains(e)) {
            n.add(new MarkedExpression(e));
            if (e.equals(ConstantExpression.FALSUM)) { // see ConstantExpression for equals() implementation and FALSUM definition
                n.setClosed(true);
            }
        }
        //System.out.println("Expression " + e + " had been added to node " + n.getName()+" of "+((Tableau)n.getGraph()).getName());
        return instanceSet;
    }
}
