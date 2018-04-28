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
public class ExpressionCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates an expression condition, ready to deliver knowledge about the corresponding activator and restriction
    @param nodeScheme the scheme representing the node where the expression is added
    @param expressionScheme the scheme representing the added expression
     */
    @ParametersTypes(types = {"node", "formula"})
    @ParametersDescriptions(descriptions = {"The node that should be verified if it contains the \"formula\" parameter",
        "Formula's expression to be tested if it belongs to the formulas' set of the \"node\" parameter"
    })
    public ExpressionCondition(SchemeVariable nodeScheme, Expression expressionScheme) {
        super();
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
    }

    public BasicActivator createActivator() {
        return new ExpressionActivator(nodeScheme, expressionScheme);
    }

    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
        //formula osf
//        for (Expression variable : expressionScheme.getVariableExpressions()) {
//            v.add(variable);
//        }
//        System.out.println("Activation scheme is: " + v);
        return v;
    }

    public Restriction createRestriction() {
        return new ExpressionMatch(nodeScheme, expressionScheme);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(nodeScheme)) {
            for (Expression variable : expressionScheme.getVariableExpressions()) {
                entry.add(variable);
            }
            return entry;
        } else {
            return null;
        }
    }
}
