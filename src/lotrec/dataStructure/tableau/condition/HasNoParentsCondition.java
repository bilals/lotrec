package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
 * This condition should identify if a node has a parent node. It is equivalent
 * to verify if the getEdgeLast() of the node is empty.
@author Bilal Said
 */
public class HasNoParentsCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;

    /**
    @param nodeScheme the scheme representing the node
     */
    @ParametersTypes(types = {"node"})
    @ParametersDescriptions(descriptions = {"The node to be tested if has no successor-nodes linked to it by the \"relation\" parameter"
    })
    public HasNoParentsCondition(SchemeVariable nodeScheme) {
        super();
        this.nodeScheme = nodeScheme;
    }

    /**
     * Should return null since this condition could not have an activator
     * @return null
     */
    public BasicActivator createActivator() {
        return null;
    }

    /**
     *
     * @return
     */
    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
        return v;
    }

    public Restriction createRestriction() {
        return new HasNoParentsConstraint(nodeScheme);
    }

    public Vector updateSchemes(Vector entry) {
        return entry;
    }
}
