


package cytoscape.visual.ui;


import javax.swing.JPopupMenu;
import giny.view.EdgeView;
import ding.view.EdgeContextMenuListener;
import giny.model.Edge;
import javax.swing.JMenuItem;

/**
 *  implements EdgeContextMenuListener
 * When a node is selected it calls
 */
class EdgeLoTRECMenuListener implements EdgeContextMenuListener {

    EdgeLoTRECMenuListener(){ }

    /**
     * @param nodeView The clicked EdgeView
     * @param menu popup menu to add the LoTREC menu
     */
    @Override
    public void addEdgeContextMenuItems (EdgeView edgeView, JPopupMenu menu){
        if(menu==null){
            menu=new JPopupMenu();
        }
        Edge e = edgeView.getEdge();
        JMenuItem menuItem= new JMenuItem(e.getSource().getIdentifier()+"->" +
                e.getTarget().getIdentifier());
        menu.add(menuItem);
    }
}
