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
public class MarkCondition extends AbstractCondition {

    private SchemeVariable markedScheme;
    private Object marker;

    /**
    Creates a mark constraint condition, ready to deliver knowledge about the corresponding restriction
    @param markedScheme the scheme representing the marked object
    @param marker the marker of the specified object
     */
        @ParametersTypes(types = {"node","mark"})
    @ParametersDescriptions(descriptions = {"The node that should be verified if it is marked by \"mark\"",
    "The mark annotating \"node\""})      
    public MarkCondition(SchemeVariable markedScheme, Object marker) {
        super();
        this.markedScheme = markedScheme;
        this.marker = marker;
    }

    public BasicActivator createActivator() {
        return new MarkActivator(markedScheme, marker);
    }

    public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(markedScheme);
        return v;
    }

    public Restriction createRestriction() {
        return new MarkConstraint(markedScheme, marker);
    }

    public Vector updateSchemes(Vector entry) {
        if (entry.contains(markedScheme)) {
            return entry;
        } else {
            return null;
        }
    }
}



