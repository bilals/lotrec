package lotrec.dataStructure.tableau.condition;

/* DEPRECATED 00/12/10 */
import java.util.Vector;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the not mark constraint construction : how the restriction works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see NotMarkConstraint
@author David Fauthoux
 */
public class NotMarkExpressionCondition extends AbstractCondition {

    private SchemeVariable nodeScheme;
    private Expression markedExpression;
    private Object marker;

    /**
    Creates a not mark constraint condition, ready to deliver knowledge about the corresponding restriction
    @param markedScheme the scheme representing the marked object
    @param marker the marker of the specified object
     */
    @ParametersTypes(types = {"node", "formula", "mark"})
    @ParametersDescriptions(descriptions = {"The node where \"formula\" should be found and should not be marked by \"mark\"",
        "The formula located in \"node\" and not marked by \"mark\"", "The mark to be verified as not annotating \"formula\" in \"node\""
    })
    public NotMarkExpressionCondition(SchemeVariable nodeScheme, Expression markedExpression,
            Object marker) {
        super();
        this.nodeScheme = nodeScheme;
        this.markedExpression = markedExpression;
        this.marker = marker;
    }

    public BasicActivator createActivator() {
        return new NotMarkExpressionActivator(nodeScheme, markedExpression, marker);
    }

    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
        return v;
    }

    public Restriction createRestriction() {
        return new NotMarkExpressionConstraint(nodeScheme, markedExpression, marker);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(nodeScheme)) {
            return entry;
        } else {
            return null;
        }
    }
}



