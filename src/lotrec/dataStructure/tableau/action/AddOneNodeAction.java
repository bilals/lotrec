package lotrec.dataStructure.tableau.action;

import lotrec.process.AbstractAction;
import lotrec.process.ProcessException;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.*;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.graph.Edge;
import lotrec.process.EventMachine;

/**
When applied, adds a node in the same tableau of the specfied node.
@author David Fauthoux
 */
public class AddOneNodeAction extends AbstractAction {

    private SchemeVariable sourceNodeScheme;
    private SchemeVariable nodeScheme;
    private Expression relationScheme;

    /**
    Creates an action which will add a node in the same tableau of the specified node.
    User only specifies the scheme representing this node and this class will find it in the instance set (modifier), when <i>apply</i> method is called.
    <p>The new node will be named with the <i>toString</i> method of <i>nodeScheme</i>
    @param sourceNodeScheme the scheme representing the node to get the tableau to add the node
    @param nodeScheme the node which will represents the added node
     */
    @ParametersTypes(types = {"node","node","relation"})
    @ParametersDescriptions(descriptions = {"The node that its tableau is where the other \"node\" will be added. It should be already instanciated by other conditions or created by other actions",
    "The ONLY-ONE new added node to the tableau of the first instanciated \"node\" parameter and linked to it by the \"relation\" parameter",
    "The relation's expression that will lable the link between the two \"node\" parameters"})    
    public AddOneNodeAction(SchemeVariable sourceNodeScheme, SchemeVariable nodeScheme, Expression relationScheme) {
        super();
        this.sourceNodeScheme = sourceNodeScheme;
        this.nodeScheme = nodeScheme;
        this.relationScheme = relationScheme;
    }

    /**
    Finds the concrete node in the modifier, represented by sourceNodeScheme in the constructor, to get its tableau; and adds a new node to this tableau.
    Finally, adds the new node representation (nodeScheme in the constructor) to the instance set (modifier).
    @param modifier the instance set used in the restriction process
    @return the instance set completed with the new node scheme
     */
    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode n = (TableauNode) instanceSet.get(sourceNodeScheme);
        Expression e = relationScheme.getInstance(instanceSet);

        if (n == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply action without instance for node");
        }

        if (e == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply action, cannot instanciate expression: " + relationScheme);
        }
        Edge edge = n.hasSuccessor(e);
        if (edge != null) {
            //System.out.println("\n"+n.toString()+" has succ");
            TableauNode node = (TableauNode) edge.getEndNode();
            return instanceSet.plus(nodeScheme, node);
        }

        TableauNode newNode = new TableauNode();//%%nodeScheme.toString());

        TableauEdge newEdge = new TableauEdge(n, newNode, e);
        n.link(newEdge);

        n.getGraph().add(newNode);

        return instanceSet.plus(nodeScheme, newNode);
    }
}
