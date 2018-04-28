package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.Rule;
import lotrec.process.*;

/**
Delivers knowledge about the node creation reaction : the activator (no restriction is created).
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see LinkActivator
@see LinkMatch
@author David Fauthoux
 */
public class NodeCreatedCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;

    /**
    Creates a node created condition, ready to deliver knowledge about the corresponding activator
    @param nodeScheme the scheme representing the node
     */
    @ParametersTypes(types = {"node"})
    @ParametersDescriptions(descriptions = {"The node that should be verified if it has been just created recently"})
    public NodeCreatedCondition(SchemeVariable nodeScheme) {
        super();
        this.nodeScheme = nodeScheme;
    }

    @Override
    public BasicActivator createActivator() {
        return new NodeCreatedActivator(nodeScheme);
    }

    @Override
    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
        return v;
    }

    @Override
    public Restriction createRestriction() {
        return null;
    }

    @Override
    public Vector updateSchemes(Vector entry) {
        return entry;
    }
}
