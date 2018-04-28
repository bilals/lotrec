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
public class NotMarkExpressionActivator extends BasicActivator {

    private SchemeVariable node;
    private Expression scheme;
    private Object marker;
    private boolean testValidity;

    /**
    Creates a mark activator. It will receive event of <code>MarkEvent.UNMARK</code> type (builds a <code>BasicActivator</code> with this event).
    @param scheme the scheme representing the marked object where the mark is added
    @param marker the marker
    @see TableauNode
     */
    public NotMarkExpressionActivator(SchemeVariable node, Expression scheme, Object marker) {
        super(MarkExpressionEvent.UNMARK_EX);
        this.node = node;
        this.scheme = scheme;
        this.marker = marker;
    }

    @Override
    public Object[] createModifiers(ProcessEvent event) {
        InstanceSet s = new InstanceSet();
        MarkExpressionEvent me = (MarkExpressionEvent) event;
        Marked source = (Marked) me.getSource();

        if (source.isMarked(me.mark)) {
            return null;
        }
        s.put(node, me.getNode());
        if (marker instanceof SchemeVariable) {
            s.put((SchemeVariable) marker, me.mark);
        }else if (!marker.equals(me.mark)) {
        //Bilal: Added 3 mars 2009
        // very important to check wether the "mark" of the event
        // matches with the "marker" of the condition, or not..
            //Without this check, we would have a problem with the following rule for example:
            // hasElemnt n or A B
            // isMarked A NotSAT
            // isMarked B NotSAT
            //Then: mark or A B NotSAT
            //The problem will raise when isMarked A NotSat is activated when A is marked with Sat for example!!!
            return null;
        }
        return new Object[]{scheme.matchWith(((MarkedExpression) me.getSource()).expression, s)};
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + "["+ node + ", " + scheme + ", " + marker + "]";
    }
}
