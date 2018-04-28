package lotrec.dataStructure.tableau.condition;

/* ADDED 00/12/21 */
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;
import lotrec.util.Marked;

/**
This activator for a restriction chain sends a modifier in the chain when it receives a <code>MarkEvent.UNMARK</code> event.
<p>It builds the modifier with the received event and with its initializers (schemes) : it joins up the concrete bound object and its scheme, and it matches the expression with the concrete expression (received in the event).
 */
public class NotMarkActivator extends BasicActivator {

    private SchemeVariable scheme;
    private Object marker;

    /**
    Creates a mark activator. It will receive event of <code>MarkEvent.UNMARK</code> type (builds a <code>BasicActivator</code> with this event).
    @param scheme the scheme representing the marked object where the mark is added
    @param marker the marker
    @see TableauNode
     */
    public NotMarkActivator(SchemeVariable scheme, Object marker) {
        super(MarkEvent.UNMARK);
        this.scheme = scheme;
        this.marker = marker;
    }

    public Object[] createModifiers(ProcessEvent event) {
        InstanceSet s = new InstanceSet();
        MarkEvent me = (MarkEvent) event;

        Marked source = (Marked) me.getSource();

        if (source.isMarked(me.mark)) {
            return null;
        }
        s.put(scheme, me.getSource());
        if (marker instanceof SchemeVariable) {
            s.put((SchemeVariable) marker, me.mark);
        } else if (!marker.equals(me.mark)) {
        //Bilal: Added 22 october 2009
        // very important to check wether the "mark" of the event
        // matches with the "marker" of the condition, or not..
            return null;
        }
        return new Object[]{s};
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + "["+ scheme + ", " + marker+"]";
    }
}
