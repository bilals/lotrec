

package cytoscape.visual.ui;


import giny.view.NodeView;
import ding.view.NodeContextMenuListener;
import giny.model.Node;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *  implements NodeContextMenuListener
 * When a node is selected it calls bypass andd add 
 */
class NodeLoTRECMenuListener implements NodeContextMenuListener {

    NodeLoTRECMenuListener(){ }

    /**
     * @param nodeView The clicked NodeView
     * @param menu popup menu to add the Bypass menu
     */
    @Override
    public void addNodeContextMenuItems (NodeView nodeView, JPopupMenu menu){
        if(menu==null){
            menu=new JPopupMenu();
        }
        Node n = nodeView.getNode();
        JMenuItem menuItem= new JMenuItem(n.getIdentifier());
        menu.add(menuItem);
    }
}
