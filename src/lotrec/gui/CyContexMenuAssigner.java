/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.gui;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author said
 */
public class CyContexMenuAssigner {

    /*
    System.out.println("Click is on: " + e.getComponent());
    DingCanvas networkCanvas = (DingCanvas) e.getComponent();
    DingCanvas networkCanvas = ((DGraphView)view).getCanvas(DGraphView.Canvas.NETWORK_CANVAS);     
     */
    public static void assignContextMenus(CyNetwork network) {
        CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
        final DGraphView dView = (DGraphView) view;
        dView.getComponent().addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                maybeShowPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                maybeShowPopup(e);
            }

            private void maybeShowPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    NodeView nView = dView.getPickedNodeView(e.getPoint());
                    EdgeView eView = dView.getPickedEdgeView(e.getPoint());
                    if (nView != null) {
                        assignNodeContextMenu(nView, popupMenu);
                        popupMenu.show(e.getComponent(),
                                e.getX(), e.getY());
                    } else if (eView != null) {
                        assignEdgeContextMenu(eView, popupMenu);
                        popupMenu.show(e.getComponent(),
                                e.getX(), e.getY());
                    } else {
                        assignNetworkContextMenu(dView, popupMenu);
//                        popupMenu.show(e.getComponent(),
//                                e.getX(), e.getY());
                    }
                }
            }
        });
    }

    private static void assignNodeContextMenu(NodeView nView, JPopupMenu popupMenu) {
        Node node = nView.getNode();
//        CyNetwork network = Cytoscape.getNetwork(nView.getGraphView().getIdentifier());
        popupMenu.add(new JMenuItem(node.getIdentifier()));//+" of "+network.getTitle()
//        System.out.println("Picked node is: " + node.getIdentifier());
    }

    private static void assignEdgeContextMenu(EdgeView eView, JPopupMenu popupMenu) {
        Edge edge = eView.getEdge();
//        CyNetwork network = Cytoscape.getNetwork(eView.getGraphView().getIdentifier());
        popupMenu.add(new JMenuItem(edge.getSource().getIdentifier() + " -> " + edge.getTarget().getIdentifier()));//+" of "+network.getTitle()
//        System.out.println("Picked edge is: " + edge.getSource().getIdentifier() + " -> " + edge.getTarget().getIdentifier());
    }

    private static void assignNetworkContextMenu(DGraphView dView, JPopupMenu popupMenu) {
//        CyNetwork network = Cytoscape.getNetwork(dView.getIdentifier());
//        popupMenu.add(new JMenuItem("Add Node"+ " to "+network.getTitle()));
//        popupMenu.add(new JMenuItem("Add Edge"+ " to "+network.getTitle()));
//        System.out.println("Click outside nodes and edges :p :) ");
    }
}
