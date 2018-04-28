package lotrec.dataStructure.graph;

import lotrec.process.ProcessEvent;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;

/**
  Event which indicates a node has been linked or unlinked. This event is sent by a <code>Node</code>. It is sent to ProcessListener objects, passing thru the containing graph dispatcher. So, to receive node events, add a listener to the graph (container of the nodes) using addProcessListener method.
  @see Graph#addProcessListener(ProcessListener listener)
  @author David Fauthoux
 */
public final class LinkEvent extends ProcessEvent implements Duplicateable {

    /**
       The event thrown indicates that two nodes has been linked with <code>edge</code>
     */
    public static int LINKED = 2;
    /**
       The event thrown indicates that two nodes has been unlinked, <code>edge</code> is removed
     */
    public static int UNLINKED = 3;

    /**
       specifies the added or removed edge
     */
    public Edge edge;

    /**
       Class constructor building a specific event
       @param source the source of the event
       @param type the type of the event : <code>LINKED</code> or <code>UNLINKED</code>
       @param edge the edge added or removed
     */
    public LinkEvent(Node source, int type, Edge edge) {
	super(source, type);
	this.edge = edge;
    }

    /**
       Returns the edge added or removed.
       @return the edge added or removed.
     */
    public Edge getEdge() {
	return edge;
    }


    //duplication

    /**
       Creates an event with the toDuplicate's fields. In a duplication process, the event must be translated to reference the duplicated edge and nodes.
       @param toDuplicate event to duplicate
       @see translateDuplication(Duplicator duplicator)
   */
    public LinkEvent(LinkEvent toDuplicate) {
	super(toDuplicate);
	edge = toDuplicate.edge;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
	super.completeDuplication(duplicator);
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
	super.translateDuplication(duplicator);
	edge = (Edge)duplicator.getImage(edge);
    }

    public Duplicateable duplicate(Duplicator duplicator) {
	Duplicateable d = new LinkEvent(this);
	duplicator.setImage(this, d);
	return d;
    }
}
