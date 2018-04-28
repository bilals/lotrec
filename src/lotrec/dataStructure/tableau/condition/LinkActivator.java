package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.graph.*;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
This activator for a restriction chain sends a modifier in the chain when it receives a <code>LinkEvent.LINKED</code> event.
<p>It builds the modifier with the received event and with its initializers (schemes) : it joins up the concrete nodes and their schemes, and it matches the relation with the concrete relation of the edge (received in the event).
@see lotrec.graph.LinkEvent#LINKED
 */
public class LinkActivator extends BasicActivator {

    private SchemeVariable nodeFromScheme;
    private SchemeVariable nodeToScheme;
    private Expression relationScheme;

    /**
    Creates a link activator. It will receive event of <code>LinkEvent.LINKED</code> type (builds a <code>BasicActivator</code> with this event).
    @param nodeFromScheme the scheme representing the source node of the link
    @param nodeToScheme the scheme representing the destination node of the link
    @param relationScheme the scheme representing the relation of the <code>TableauEdge</code>
    @see TableauEdge
     */
    public LinkActivator(SchemeVariable nodeFromScheme, SchemeVariable nodeToScheme,
            Expression relationScheme) {
        super(LinkEvent.LINKED);
        this.nodeFromScheme = nodeFromScheme;
        this.nodeToScheme = nodeToScheme;
        this.relationScheme = relationScheme;
    }

    public Object[] createModifiers(ProcessEvent event) {
        TableauEdge e = (TableauEdge) ((LinkEvent) event).edge;
        InstanceSet s = new InstanceSet();
        s.put(nodeFromScheme, e.getBeginNode());
        s.put(nodeToScheme, e.getEndNode());
        return new Object[]{relationScheme.matchWith(e.getRelation(), s)};
    }

    @Override
    public String toString(){
        return this.getClass().getSimpleName() + "["+ nodeFromScheme + " -> " + nodeToScheme+ " by " + relationScheme + "]";
    }
}




