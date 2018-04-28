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
public class NotMarkCondition extends AbstractCondition {

    private SchemeVariable markedScheme;
    private Object marker;

    /**
    Creates a not mark constraint condition, ready to deliver knowledge about the corresponding restriction
    @param markedScheme the scheme representing the marked object
    @param marker the marker of the specified object
     */
    @ParametersTypes(types = {"node", "mark"})
    @ParametersDescriptions(descriptions = {"The node that should be verified if it is not marked by \"mark\"",
        "The mark to be verified as NOT annotating \"node\""
    })
    public NotMarkCondition(SchemeVariable markedScheme, Object marker) {
        super();
        this.markedScheme = markedScheme;
        this.marker = marker;
    }

    public BasicActivator createActivator() {
        return new NotMarkActivator(markedScheme, marker);
    }

    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(markedScheme);
        return v;
    }

    public Restriction createRestriction() {
        return new NotMarkConstraint(markedScheme, marker);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(markedScheme)) {
            return entry;
        } else {
            return null;
        }
    }
}



