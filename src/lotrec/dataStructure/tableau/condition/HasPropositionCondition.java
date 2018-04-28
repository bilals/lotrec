package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the expression matching construction : the activator, the restriction and how they works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see ExpressionActivator
@see ExpressionMatch
@author David Fauthoux
 */
@Deprecated
/*
 * hasElement + isProposition may replace this hasProposition condition
 */
public class HasPropositionCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates an expression condition, ready to deliver knowledge about the corresponding activator and restriction
    @param nodeScheme the scheme representing the node where the expression is added
    @param expressionScheme the scheme representing the added expression
     */
    @ParametersTypes(types = {"node", "formula"})
    @ParametersDescriptions(descriptions = {"The node where \"formula\" is found and tested if it is a proposition",
        "The formula's expression to be found in \"node\" and tested if it is a proposition"
    })
    public HasPropositionCondition(SchemeVariable nodeScheme, Expression expressionScheme) {
        super();
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
    }

    @Override
    public BasicActivator createActivator() {
        return new HasPropositionActivator(nodeScheme, expressionScheme);
    }

    @Override
    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
        //formula osf
        return v;
    }

    @Override
    public Restriction createRestriction() {
        return new HasPropositionMatch(nodeScheme, expressionScheme);
    }

    @Override
    public Vector updateSchemes(Vector entry) {
        if (entry.contains(nodeScheme)) {
            return entry;
        } else {
            return null;
        }
    }
}
