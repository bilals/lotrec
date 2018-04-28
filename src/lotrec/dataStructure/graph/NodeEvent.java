package lotrec.dataStructure.graph;

import lotrec.process.ProcessEvent;
import lotrec.util.Duplicator;
import lotrec.util.Duplicateable;
import lotrec.util.DuplicateException;

/**
Event which indicates a node has been added or removed. This event is sent by a <code>Graph</code>. It is sent to ProcessListener objects.
@see Graph#addProcessListener(ProcessListener listener)
@author David Fauthoux
 */
public final class NodeEvent extends ProcessEvent implements Duplicateable {

    /**
    The event thrown indicates that <code>node</code> has been added
     */
    public static int ADDED = 0;
    /**
    The event thrown indicates that <code>node</code> has been removed
     */
    public static int REMOVED = 1;
    /**
    specifies the added or removed node
     */
    public Node node;

    /**
    Class constructor building a specific event
    @param source the source of the event
    @param type the type of the event : <code>ADDED</code> or <code>REMOVED</code>
    @param node the node added or removed
     */
    public NodeEvent(Graph source, int type, Node node) {
        super(source, type);
        this.node = node;
    }

    /**
    Returns the node, added or removed.
    @return the node, added or removed.
     */
    public Node getNode() {
        return node;
    }


    //duplication
    /**
    Creates an event with the toDuplicate's fields. In a duplication process, the event must be translated to reference the duplicated node.
    @param toDuplicate event to duplicate
    @see translateDuplication(Duplicator duplicator)
     */
    public NodeEvent(NodeEvent toDuplicate) {
        super(toDuplicate);
        node = toDuplicate.node;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);

    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
        node = (Node) duplicator.getImage(node);
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new NodeEvent(this);
        duplicator.setImage(this, d);
        return d;
    }
}
