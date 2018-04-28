/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.gui;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauEdge;
import lotrec.dataStructure.tableau.TableauNode;

/**
 *
 * @author said
 */
public class CyTableauDisplayer {

    public static String Hierarchic = "Hierarchic";
    public static String Circular = "Circular";
//    public static String MirrorX = "MirrorX";
//    public static String MirrorY = "MirrorY";
    public static CyNetwork nullNetwork = null;
    public static Vector<CyNetwork> lastCreatedNetworks = new Vector();

    public static String currentViewTableauName() {
        return Cytoscape.getCurrentNetworkView().getTitle();
    }

    private static void checkUpForParent(Tableau t, Vector<Tableau> tableauxList) {
        if (t.getDuplicationInitialParent() != null &&
                !tableauxList.contains(t.getDuplicationInitialParent())) {
            tableauxList.add(t.getDuplicationInitialParent());
            checkUpForParent(t.getDuplicationInitialParent(), tableauxList);
        }
    }

    private static void displayTableau(Tableau t) {
        ArrayList nodes = new ArrayList();
        for (Enumeration enum_ = t.getNodesEnumeration(); enum_.hasMoreElements();) {
            TableauNode n = (TableauNode) enum_.nextElement();
            if (TableauxPanel.cmbxNodeFilter.getSelectedItem().equals("None") ||
                    !n.getMarks().contains(TableauxPanel.cmbxNodeFilter.getSelectedItem())) {
                CyNode node = Cytoscape.getCyNode(n.getName(), true);
                ArrayList formulas = new ArrayList();
                for (Enumeration enum__ = n.getMarkedExpressionsEnum(); enum__.hasMoreElements();) {
                    MarkedExpression e = (MarkedExpression) enum__.nextElement();
                    formulas.add(e.toString());
                }
                ArrayList marks = new ArrayList(n.getMarks());
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "isTableauTreeNode", false);
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "name", n.getName());
                Cytoscape.getNodeAttributes().setListAttribute(node.getIdentifier(), "formulas", formulas);
                Cytoscape.getNodeAttributes().setListAttribute(node.getIdentifier(), "marks", marks);
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "closed", n.isClosed());
                //Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"labelOfFormulas",getLabelOfFormulas(formulas));
                //Double d = new Double(getMaxLengthOfFormulas(formulas));
                //Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"maxLengthOfFormulas",d);
                //d = new Double(formulas.size());
                //Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"formulasNumber",d);
                nodes.add(node);
            } else {
                //do nothing
            }

        }
        ArrayList edges = new ArrayList();
        for (Enumeration enum_ = t.getNodesEnumeration(); enum_.hasMoreElements();) {
            TableauNode n = (TableauNode) enum_.nextElement();
            if (TableauxPanel.cmbxNodeFilter.getSelectedItem().equals("None") ||
                    !n.getMarks().contains(TableauxPanel.cmbxNodeFilter.getSelectedItem())) {
                for (Enumeration enum__ = n.getNextEdgesEnum(); enum__.hasMoreElements();) {
                    TableauEdge e = (TableauEdge) enum__.nextElement();
                    TableauNode endNode = (TableauNode) e.getEndNode();
                    if (TableauxPanel.cmbxNodeFilter.getSelectedItem().equals("None") ||
                            !endNode.getMarks().contains(TableauxPanel.cmbxNodeFilter.getSelectedItem())) {
                        CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(e.getBeginNode().getName()),
                                Cytoscape.getCyNode(endNode.getName()),
                                Semantics.INTERACTION, e.getRelation().toString(), true, true);
                        ArrayList relations = new ArrayList();
                        relations.add(e.getRelation().toString());
                        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), "isTableauTreeEdge", false);
                        Cytoscape.getEdgeAttributes().setListAttribute(edge.getIdentifier(), "relations", relations);
                        //Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),"labelOfRelations",getLabelOfRelations(relations));
                        edges.add(edge);
                    } else {
                        //do nothing
                    }
                }
            } else {
                // do nothing
            }
        }
        CyNetwork network = Cytoscape.createNetwork(nodes, edges, t.getName(), nullNetwork, true);
        CyContexMenuAssigner.assignContextMenus(network);
        network.addSelectEventListener(TableauxPanel.SELECTION_LISTENER);
        lastCreatedNetworks.add(network);

//        if (t.isClosed()) {
//            lastCreatedNetworks.add(Cytoscape.createNetwork(nodes, edges, "Closed Tableau " + t.getName(), nullNetwork, true));
//        } else {
//            lastCreatedNetworks.add(Cytoscape.createNetwork(nodes, edges, "Open Tableau " + t.getName(), nullNetwork, true));
//        }

//        try {
//            Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getSelectedFrame().setMaximum(true);
//        } catch (PropertyVetoException ex) {
//            System.out.println("Error while maximizing the frame for premodel: "+t.getName()+"\n"+
//                    ex.getMessage());
//        }

        if (t.getNodes().size() != 0) {
            doYLayout(MainFrame.getSelectedLayout());
        }
    }

    public static void displayTableauInCy(Tableau t) {
        displayTableau(t);
//        lotrec.gui.MainFrame.mnuLayout.setEnabled(true);
    }

    public static void displayTableauxInCy(Vector<Tableau> tableauxList) {
        for (Tableau t : tableauxList) {
            displayTableau(t);
        }
//        lotrec.gui.MainFrame.mnuLayout.setEnabled(true);
    }

    public static void displayTableauxTreeInCy(Vector<Tableau> tableauxList) {
        ArrayList<CyNode> nodes = new ArrayList();
        int i = tableauxList.size() - 1;
        if (i == -1) {
            CyNode node = Cytoscape.getCyNode("no premodels", true);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "isTableauTreeNode", true);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "name", "no premodels");
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "size", 0);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "closed", false);
            nodes.add(node);
        }
        for (; i >= 0; i--) {
            Tableau t = tableauxList.get(i);
            checkUpForParent(t, tableauxList);
        }
        for (Tableau t : tableauxList) {
            if (t.hasDuplicataIn(tableauxList)) { //equivalent to t.getBaseName() != null
                CyNode node = Cytoscape.getCyNode(t.getBaseName(), true);
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "isTableauTreeNode", true);
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "name", t.getBaseName());
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "size", 0);
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "closed", false);
                nodes.add(node);
            }
            CyNode node = Cytoscape.getCyNode(t.getName(), true);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "isTableauTreeNode", true);
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "name", t.getName());
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "size", t.getNodes().size());
            Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "closed", t.isClosed());
            nodes.add(node);
        }
        ArrayList edges = new ArrayList();
        for (Tableau t : tableauxList) {
            // The link with the parent...
            if (t.getDuplicationInitialParent() != null) {
                // has parent and children
                // link its BaseName to its parent BaseName
                if (t.hasDuplicataIn(tableauxList)) {
                    CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(t.getDuplicationInitialParent().getBaseName()),
                            Cytoscape.getCyNode(t.getBaseName()),
                            Semantics.INTERACTION, " ", true, true);
                    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), "isTableauTreeEdge", true);
                    edges.add(edge);
                } else {
                    // has parent but no children
                    // link its Name to its parent BaseName
                    CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(t.getDuplicationInitialParent().getBaseName()),
                            Cytoscape.getCyNode(t.getName()),
                            Semantics.INTERACTION, " ", true, true);
                    Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), "isTableauTreeEdge", true);
                    edges.add(edge);
                }
            }
            // has children
            // link its Name to its BaseName...
            if (t.hasDuplicataIn(tableauxList)) {
                CyEdge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(t.getBaseName()),
                        Cytoscape.getCyNode(t.getName()),
                        Semantics.INTERACTION, " ", true, true);
                Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(), "isTableauTreeEdge", true);
                edges.add(edge);
            }
        // has no parent and no children is not important:
        // NO LINKS, and It should be a single node!!
        }
        CyNetwork network = Cytoscape.createNetwork(nodes, edges, TableauxPanel.TABLEAU_TREE, nullNetwork, true);
        network.addSelectEventListener(TableauxPanel.SELECTION_LISTENER);
        lastCreatedNetworks.add(network);
        doYLayout(MainFrame.getSelectedLayout());
//        lotrec.gui.MainFrame.mnuLayout.setEnabled(true);
    }

    //Flushing by starting a new session delete all the networks at once, 
    //but does not solve the memory problem of Cytoscape!!
//    public static void flush() {
//        Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
//        Cytoscape.createNewSession();
//        Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
//        lastCreatedNetworks.clear();
//    }
    public static void flush() {
        for (Enumeration<CyNetwork> enumr = lastCreatedNetworks.elements(); enumr.hasMoreElements();) {
            CyNetwork aLastNetwork = enumr.nextElement();
//            System.out.println("last network is being destroyed...");
            Cytoscape.destroyNetwork(aLastNetwork);
            aLastNetwork = null;
//            System.out.println("last network is now: " + aLastNetwork);
        }
        Cytoscape.resetNodeAttributes();
        Cytoscape.resetEdgeAttributes();
        lastCreatedNetworks.clear();
//        lotrec.gui.MainFrame.mnuLayout.setEnabled(false);
    }

    //layoutName could be:
    // - "Hierarchic"
    // - "Circular"
    // - "MirrorX"
    // - "MirrorY"
    public static void doYLayout(String layoutName) {
        //
        //Making yFiles layouting by clicking on a button in Cytoscape Menu :D
        //
        JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getLayoutMenu();
        MenuElement[] popup = layoutMenu.getSubElements();
        MenuElement[] submenus = ((JPopupMenu) popup[0]).getSubElements();
        JMenuItem yFiles = null;
        for (int i = 0; i < submenus.length; i++) {
            yFiles = ((JMenuItem) submenus[i]);
            if (yFiles.getText().equals("yFiles")) {
                break;
            }
        }
        popup = yFiles.getSubElements();
        submenus = ((JPopupMenu) popup[0]).getSubElements();
        JMenuItem yLayoutMenuItem = null;
        for (int i = 0; i < submenus.length; i++) {
            yLayoutMenuItem = ((JMenuItem) submenus[i]);
            if (yLayoutMenuItem.getText().equals(layoutName)) {
                break;
            }
        }
        yLayoutMenuItem.doClick();
    }
}
