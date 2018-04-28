package cytoscape.visual.ui;

import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Adds NodeView and EdgeView menu listeners to network views as
 * the views are created.
 */
public class LoTRECNetworkListener implements PropertyChangeListener{

    /**
     * Listens for NETWORK_VIEW_CREATED events and if it hears one, it adds
     * node and edge context menu listeners to the view.
     * @param evnt The event we're hearing.
     */
    @Override
    public void propertyChange (PropertyChangeEvent evnt) {
        if (evnt.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_CREATED)) {
            NodeLoTRECMenuListener node_menu_listener=new NodeLoTRECMenuListener();
            Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(node_menu_listener);

            EdgeLoTRECMenuListener edge_menu_listener=new EdgeLoTRECMenuListener();
            Cytoscape.getCurrentNetworkView().addEdgeContextMenuListener(edge_menu_listener);
        }
    }
}
