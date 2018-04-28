/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.dataStructure.tableau.action;

import java.util.Enumeration;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.InstanceSet;
import lotrec.dataStructure.expression.SchemeVariable;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauEdge;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.process.AbstractAction;
import lotrec.process.EventMachine;
import lotrec.process.ProcessException;

/**
 * Removes all the adges, having the as relation the "relationScheme", that exist between "nodeFromScheme" and "nodeToScheme" (see the constructor).
 * @see AbstractAction, Node, Edge, 
 * @author said
 */
public class UnlinkAction extends AbstractAction {

    private SchemeVariable nodeFromScheme;
    private SchemeVariable nodeToScheme;
    private Expression relationScheme;
    
    /**
     * The constructor is better described with its annotations:
     * 1- @ParametersTypes(types = {"node", "node", "relation"})
     *    This annotation determines the type of each parameter : mandatory and used by the parser.
     * 2- @ParametersDescriptions(descriptions = {"The (source) node that should be unlinked with the other (destination) "node" parameter. It should be already instanciated by other conditions or created by other actions",
     *                                           "The (destination) node that will be unlinked from the first (source) "node" parameter. It should be also already instanciated by other conditions or created by other actions",
     *                                           "The relation's expression that should have  as label the links that exist between the two "node" parameters and that are to be deleted."
     *                                          })
     *    This annotation describes each parameter : filling it is facultatif but recommanded,
     *    since descriptions are displayed to the users and help them better understand the action.
     */    
    @ParametersTypes(types = {"node", "node", "relation"})
    @ParametersDescriptions(descriptions = {"The (source) node that should be unlinked with the other (destination) \"node\" parameter. It should be already instanciated by other conditions or created by other actions",
"The (destination) node that will be unlinked from the first (source) \"node\" parameter. It should be also already instanciated by other conditions or created by other actions",
"The relation's expression that should have  as label the links that exist between the two \"node\" parameters and that are to be deleted."
})    
    public UnlinkAction(SchemeVariable nodeFromScheme, SchemeVariable nodeToScheme, Expression relationScheme) {
        this.nodeFromScheme = nodeFromScheme;
        this.nodeToScheme = nodeToScheme;
        this.relationScheme = relationScheme;
    }

    
    /**
     * Finds the concrete instances of the (source and target) nodes and the edge relation's expression.
     * If all these parameters are well instantiated, this method removes all the edges having "relationSchme" as label and linking the two node parameters.
     * @param modifier: an <b>Object</b> of type <b>InstanceSet</b>: the set of schemes (variables) instantiated by the rule conditions verification and former actions application.
     * @return  modifier: the same input InstanceSet.
     */    
    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;

        TableauNode nFrom = (TableauNode) instanceSet.get(nodeFromScheme);
        TableauNode nTo = (TableauNode) instanceSet.get(nodeToScheme);
        Expression e = relationScheme.getInstance(instanceSet);
        if (e == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply action, cannot instanciate expression: " + relationScheme);
        }
        if ((nFrom == null) || (nTo == null)) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply without instance for nodeFrom or for nodeTo");
        }
        for (Enumeration enumr = nFrom.getNextEdgesEnum(); enumr.hasMoreElements();) {
            TableauEdge edge = (TableauEdge) enumr.nextElement();
            if (edge.getEndNode().equals(nTo) && edge.getRelation().equals(e)) {
                nFrom.unlink(edge);
            }
        }
        return instanceSet;
    }
}
