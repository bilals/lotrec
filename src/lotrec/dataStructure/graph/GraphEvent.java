package lotrec.dataStructure.graph;

import lotrec.process.ProcessEvent;
import lotrec.util.Duplicator;
import lotrec.util.Duplicateable;
import lotrec.util.DuplicateException;

/**
  Event which indicates a graph has been added or removed. This event is sent by a <code>Wallet</code>. It is sent to ProcessListener objects.
  @see Wallet#addProcessListener(ProcessListener listener)
  @author David Fauthoux
 */
public final class GraphEvent extends ProcessEvent implements Duplicateable {

    /**
       The event thrown indicates that <code>graph</code> has been added
     */
    public static int ADDED = 4;
    /**
       The event thrown indicates that <code>graph</code> has been removed
     */
    public static int REMOVED = 5;

    /**
       specifies the added or removed graph
     */
    public Graph graph;

    /**
       Class constructor building a specific event
       @param source the source of the event
       @param type the type of the event : <code>ADDED</code> or <code>REMOVED</code>
       @param graph the graph added or removed
     */
    public GraphEvent(Wallet source, int type, Graph graph) {
	super(source, type);
	this.graph = graph;
    }

    /**
       Returns the graph added or removed.
       @return the graph added or removed.
     */
    public Graph getGraph() {
	return graph;
    }

    //duplication

    /**
       Creates an event with the toDuplicate's fields. In a duplication process, the event must be translated to reference the duplicated graph.
       @param toDuplicate event to duplicate
       @see translateDuplication(Duplicator duplicator)
   */
    public GraphEvent(GraphEvent toDuplicate) {
	super(toDuplicate);
	graph = toDuplicate.graph;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
	super.completeDuplication(duplicator);
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
	super.translateDuplication(duplicator);
	graph = (Graph)duplicator.getImage(graph);
    }

    public Duplicateable duplicate(Duplicator duplicator) {
	Duplicateable d = new GraphEvent(this);
	duplicator.setImage(this, d);
	return d;
    }
}
