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
@see MarkConstraint
@author David Fauthoux
 */
public class NotExpressionCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates a not expression constraint condition, ready to deliver knowledge about the corresponding restriction
    @param nodeScheme the scheme representing the node
    @param expressionScheme the expression to be tested
     */
    @ParametersTypes(types = {"node", "formula"})
    @ParametersDescriptions(descriptions = {"Instanciated reference node where should be verified the ABSENCE \"formula\" parameter",
        "Formula's expression that will be verified if it does not belong to the instanciated \"node\" parameter"
    })
    public NotExpressionCondition(SchemeVariable nodeScheme, Expression expressionScheme) {
        super();
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
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
        return new NotExpressionConstraint(nodeScheme, expressionScheme);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(nodeScheme)) {
            return entry;
        } else {
            return null;
        }
    }
}



