package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.graph.*;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
This activator for a restriction chain sends a modifier in the chain when it receives a <code>NodeEvent.ADDED</code> event.
<p>It builds the modifier with the received event and with its initializer (node scheme) : it joins up the concrete node and its scheme.
@see lotrec.graph.NodeEvent#ADDED
 */
public class NodeCreatedActivator extends BasicActivator {

    private SchemeVariable nodeScheme;

    /**
    Creates a node created activator. It will receive event of <code>NodeEvent.ADDED</code> type (builds a <code>BasicActivator</code> with this event).
    @param nodeScheme the scheme representing the created node
     */
    public NodeCreatedActivator(SchemeVariable nodeScheme) {
        super(NodeEvent.ADDED);
        this.nodeScheme = nodeScheme;
    }

    @Override
    public Object[] createModifiers(ProcessEvent event) {
        InstanceSet s = new InstanceSet();
        s.put(nodeScheme, (TableauNode) ((NodeEvent) event).node);
        return new Object[]{s};
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + nodeScheme + "]";
    }
}




