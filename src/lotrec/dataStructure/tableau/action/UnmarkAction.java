package lotrec.dataStructure.tableau.action;

/* ADDED 00/12/21 */
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.util.Marked;
import lotrec.dataStructure.tableau.*;

/**
When applied, unmarks a specified object
<p>Important note : if the specified marker is an instance of <code>SchemeVariable</code>, then its concrete reference will be search in then modifier.
@author David Fauthoux
 */
public class UnmarkAction extends AbstractAction {

    private SchemeVariable markedScheme;
    private Object marker;

    /**
    Creates an action which will mark the specified object
    @param markedScheme the scheme representing the object to mark
    @param marker the marker to mark the specified object
     */
    @ParametersTypes(types = {"node","mark"})
    @ParametersDescriptions(descriptions = {"The node where the \"mark\" parameter should be removed. It should be already instanciated by other conditions or created by other actions",
    "The mark to be removed from annotations' list of the instanciated \"node\" parameter"})      
    public UnmarkAction(SchemeVariable markedScheme, Object marker) {
        super();
        this.markedScheme = markedScheme;
        this.marker = marker;
    }

    /**
    Marks the concrete object represented in the <code>InstanceSet</code> by the scheme specified in the constructor.
    If the specified marker is an instance of <code>SchemeVariable</code>, then its concrete reference will be search in then modifier.
    @param modifier the instance set used in the restriction process
    @return the unchanged modifier
     */
    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet) modifier;
        Marked m = (Marked) instanceSet.get(markedScheme);
        //TableauNode m = (TableauNode)instanceSet.get(markedScheme);
        if (m == null) {
            throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
                    "cannot apply without instance for marked");
        }
        Object test = marker;
        if (marker instanceof SchemeVariable) {
            test = instanceSet.get((SchemeVariable) marker);
            if (test == null) {
                test = marker;
            }
        }
        if (m.isMarked(test)) {
            m.unmark(test);
            ((TableauNode) m).sendEvent(new MarkEvent(m, MarkEvent.UNMARK, marker));
        }
        return instanceSet;
    }
}
