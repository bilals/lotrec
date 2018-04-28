package lotrec.dataStructure.tableau.condition;

/* MODIFIED 00/12/21 */
import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the mark constraint construction : how the restriction works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see MarkConstraint
@author David Fauthoux
 */
public class MarkExpressionCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;
    private Expression markedExpression;
    private Object marker;

    /**
    Creates a mark constraint condition, ready to deliver knowledge about the corresponding restriction
    @param markedScheme the scheme representing the marked object
    @param marker the marker of the specified object
     */
    @ParametersTypes(types = {"node", "formula", "mark"})
    @ParametersDescriptions(descriptions = {"The node where \"formula\" is marked by \"mark\"",
        "The formula located in \"node\" and marked by \"mark\"", "The mark annotating \"formula\" in \"node\""
    })
    public MarkExpressionCondition(SchemeVariable nodeScheme, Expression markedExpression, Object marker) {
        super();
        this.nodeScheme = nodeScheme;
        this.markedExpression = markedExpression;
        this.marker = marker;
    }

    public BasicActivator createActivator() {
        return new MarkExpressionActivator(nodeScheme, markedExpression, marker);
    }

    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
//        for (Expression variable : markedExpression.getVariableExpressions()) {
//            v.add(variable);
//        }
        return v;
    }

    public Restriction createRestriction() {
        return new MarkExpressionConstraint(nodeScheme, markedExpression, marker);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(nodeScheme)) {
//            for (Expression variable : markedExpression.getVariableExpressions()) {
//                entry.add(variable);
//            }
            return entry;
        } else {
            return null;
        }
    }
}



