package lotrec.dataStructure.tableau.condition;

/* DEPRECATED */
import lotrec.dataStructure.graph.*;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
This activator for a restriction chain sends a modifier in the chain when it receives a <code>LinkEvent.LINKED</code> event.
<p>It builds the modifier with the received event and with its initializers (schemes) : it joins up the concrete nodes and their schemes, and it matches the relation with the concrete relation of the edge (received in the event).
@see lotrec.graph.LinkEvent#LINKED
 */
@Deprecated
public class AncestorActivator extends BasicActivator {

    private SchemeVariable childScheme;
    private SchemeVariable ancestorScheme;
    private Expression relationScheme;

    /**
    Creates an ancestor activator. It will receive event of <code>LinkEvent.LINKED</code> 
    type (builds a <code>BasicActivator</code> with this event).
    @param childScheme the scheme representing the child in the links chain
    @param ancestorScheme the scheme representing the ancestor in the links chain
     */
    public AncestorActivator(SchemeVariable childScheme, SchemeVariable ancestorScheme, Expression relationScheme) {
        super(LinkEvent.LINKED);
        this.childScheme = childScheme;
        this.ancestorScheme = ancestorScheme;
        this.relationScheme = relationScheme;
    }

    @Override
    public Object[] createModifiers(ProcessEvent event) {
        TableauEdge e = (TableauEdge) ((LinkEvent) event).edge;
        InstanceSet s = new InstanceSet();
        s.put(childScheme, e.getEndNode());
        //return s;
        return new Object[]{relationScheme.matchWith(e.getRelation(), s)};
    }
}




