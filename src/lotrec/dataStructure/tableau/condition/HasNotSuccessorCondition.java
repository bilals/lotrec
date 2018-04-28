package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the node creation reaction : the activator (no restriction is created).
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see LinkActivator
@see LinkMatch
@author David Fauthoux
 */
public class HasNotSuccessorCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;
    private Expression relation;

    /**
    Creates a node created condition, ready to deliver knowledge about the corresponding activator
    @param nodeScheme the scheme representing the node
     */
    @ParametersTypes(types = {"node", "relation"})
    @ParametersDescriptions(descriptions = {"The node to be tested if has no successor-nodes linked to it by the \"relation\" parameter",
        "Relation's expression to be teseted as giving no successor-nodes to the first \"node\" parameter"
    })
    public HasNotSuccessorCondition(SchemeVariable nodeScheme, Expression relation) {
        super();
        this.nodeScheme = nodeScheme;
        this.relation = relation;
    }

    public BasicActivator createActivator() {
        return null;
    }

    /**
     * Should return null since this condition could not have an activator
     * @return
     */
    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
        return v;
    }

    public Restriction createRestriction() {
        return new HasNotSuccessorConstraint(nodeScheme, relation);
    }

    public Vector updateSchemes(Vector entry) {
        return entry;
    }
}
