package lotrec.dataStructure.tableau.condition;

/* ADDED 00/12/21 */
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import java.util.Collection;
import java.util.Vector;
import lotrec.dataStructure.tableau.*;
import lotrec.util.Marked;

/**
This activator for a restriction chain sends a modifier in the chain when it receives a <code>MarkEvent.MARK</code> event.
<p>It builds the modifier with the received event and with its initializers (schemes) : it joins up the concrete bound object and its scheme, and it matches the expression with the concrete expression (received in the event).
 */
public class MarkExpressionActivator extends BasicActivator {

    private SchemeVariable scheme;
    private Expression markedExpression;
    private Object marker;

    /**
    Creates a mark activator. It will receive event of <code>MarkEvent.MARK</code> type (builds a <code>BasicActivator</code> with this event).
    @param scheme the scheme representing the marked object where the mark is added
    @param marker the marker
    @see TableauNode
     */
    public MarkExpressionActivator(SchemeVariable scheme, Expression markedExpression, Object marker) {
        super(MarkExpressionEvent.MARK_EX);
        this.scheme = scheme;
        this.markedExpression = markedExpression;
        this.marker = marker;
    }

    public Object[] createModifiers(ProcessEvent event) {
        InstanceSet s = new InstanceSet();
        MarkExpressionEvent markExpressionEvent = (MarkExpressionEvent) event;
        Marked theSourceMarkedExpression = (Marked) markExpressionEvent.getSource();

        if (!theSourceMarkedExpression.isMarked(markExpressionEvent.mark)) {
            return null;
        }
        s.put(scheme, markExpressionEvent.getNode());
        if (marker instanceof SchemeVariable) {
            s.put((SchemeVariable) marker, markExpressionEvent.mark);
        }else if (!marker.equals(markExpressionEvent.mark)) {
        //Bilal: Added 3 mars 2009
        // very important to check wether the "mark" of the event
        // matches with the "marker" of the condition, or not..
            return null;
        }
        return new Object[]{markedExpression.matchWith(((MarkedExpression) markExpressionEvent.getSource()).expression, s)};
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + scheme + ", " + markedExpression + ", " + marker + "]";
    }
}
