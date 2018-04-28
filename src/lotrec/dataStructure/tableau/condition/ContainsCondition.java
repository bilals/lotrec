package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the contains constraint construction : how the restriction works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see MarkConstraint
@author David Fauthoux
 */
public class ContainsCondition extends AbstractCondition {

    private SchemeVariable bigNodeScheme;
    private SchemeVariable smallNodeScheme;

    /**
    Creates a contains constraint condition, ready to deliver knowledge about the corresponding restriction
    The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
    @param bigNodeScheme the scheme representing the node N0
    @param smallNodeScheme the scheme representing the node N1
     */
    @ParametersTypes(types = {"node", "node"})
    @ParametersDescriptions(descriptions = {"The (bigger) node that should be verified if it contains the second \"node\"",
        "The (smaller) node that should be verified as contained in the first \"node\""
    })
    public ContainsCondition(SchemeVariable bigNodeScheme, SchemeVariable smallNodeScheme) {
        super();
        this.bigNodeScheme = bigNodeScheme;
        this.smallNodeScheme = smallNodeScheme;
    }

    /**
    Returns null
    @return null
     */
    public BasicActivator createActivator() {
        return null;
    }

    /**
    Returns null
    @return null
     */
    public Vector getActivationSchemes() {
        return null;
    }

    public Restriction createRestriction() {
        return new ContainsMatch(bigNodeScheme, smallNodeScheme);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(bigNodeScheme) && entry.contains(smallNodeScheme)) {
            return entry;
        } else {
            return null;
        }
    }
}



