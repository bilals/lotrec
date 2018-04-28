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
@see NotEqualConstraint
@author David Fauthoux
 */
public class NotEqualCondition extends AbstractCondition {

    private Expression scheme1;
    private Expression scheme2;

    /**
    Creates an "not identical" constraint condition, ready to deliver knowledge about the corresponding restriction
    @param scheme1 the first scheme
    @param scheme2 the second scheme
     */
    @ParametersTypes(types = {"formula", "formula"})
    @ParametersDescriptions(descriptions = {"A formula",
        "Another formula"
    })
    public NotEqualCondition(Expression scheme1, Expression scheme2) {
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
        return new NotEqualConstraint(scheme1, scheme2);
    }

    @Override
    public Vector updateSchemes(Vector entry) {
        if (entry.contains(scheme1) && entry.contains(scheme2)) {
            return entry;
        } else {
            return null;
        }
//        return entry;
    }
}



