package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the not expression constraint construction : how the restriction works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see IdenticalConstraint
@author David Fauthoux
 */
public class IdenticalCondition extends AbstractCondition {

    private SchemeVariable scheme1;
    private SchemeVariable scheme2;

    /**
    Creates an "identical" constraint condition, ready to deliver knowledge about the corresponding restriction
    @param scheme1 the first scheme
    @param scheme2 the second scheme
     */
    @ParametersTypes(types = {"node", "node"})
    @ParametersDescriptions(descriptions = {"A node to be tested if is identical to (same formulas' set of) the other \"node\" parameter",
        "A node to be tested if is identical to (same formulas' set of) the other \"node\" parameter"
    })
    public IdenticalCondition(SchemeVariable scheme1, SchemeVariable scheme2) {
        super();
        this.scheme1 = scheme1;
        this.scheme2 = scheme2;
    }

    /**
    Returns null
    @return null
     */
    @Override
    public BasicActivator createActivator() {
        return null;
    }

    /**
    Returns null
    @return null
     */
    @Override
    public Vector getActivationSchemes() {
        return null;
    }

    @Override
    public Restriction createRestriction() {
        return new IdenticalConstraint(scheme1, scheme2);
    }

    @Override
    public Vector updateSchemes(Vector entry) {
        if (entry.contains(scheme1) && entry.contains(scheme2)) {
            return entry;
        } else {
            return null;
        }
    }
}



