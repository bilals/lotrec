/*
 * CyDisplayer.java
 *
 * Created on 30 janvier 2007, 15:14
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec.gui;

import cytoscape.CyMain;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import giny.model.Edge;
import giny.model.Node;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Wallet;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauEdge;
import lotrec.dataStructure.tableau.TableauNode;
import java.io.File;
import java.util.Properties;
import lotrec.Lotrec;
import org.freehep.graphics2d.VectorGraphics;
import org.freehep.graphicsio.ImageGraphics2D;

/**
 * Should have an executed instance of CyMain
 * Problem to be tackled properly when starting
 * to deal with the engine GUI
 * @author said
 */
@Deprecated
public class CyDisplayer {

    public static CyNetwork lastCurrentNetwork = null;
    public static boolean STEP = false;

    @Deprecated
    /** Creates a new instance of CyDisplayer */
    public CyDisplayer() {
    }

    @Deprecated
    public static void displayTableauInCy(Wallet wallet, MarkedExpression formula) {
        /*Node node_1 = Cytoscape.getCyNode("node 1", true);
        Node node_2 = Cytoscape.getCyNode("node 2", true);
        ArrayList formulas = new ArrayList();
        formulas.add(new String("not not A"));
        formulas.add(new String("pos A"));
        formulas.add(new String("box box B"));
        Cytoscape.getNodeAttributes().setListAttribute(node_1.getIdentifier(),"formulas",formulas);
        //node_1.setIdentifier(getLabelOfFormulas(formulas));
        //node_2.setIdentifier(getLabelOfFormulas(null));
        Cytoscape.getNodeAttributes().setAttribute(node_1.getIdentifier(),"labelOfFormulas",getLabelOfFormulas(formulas));
        Cytoscape.getNodeAttributes().setAttribute(node_1.getIdentifier(),"formulasNumber",formulas.size());
        Double d = new Double(getMaxLengthOfFormulas(formulas));
        Cytoscape.getNodeAttributes().setAttribute(node_1.getIdentifier(),"maxLengthOfFormulas",d*10);
        Edge edge   = Cytoscape.getCyEdge(node_1, node_2, Semantics.INTERACTION, "e", true, true);
        ArrayList relations = new ArrayList();
        relations.add("R2");
        relations.add("R1");
        Cytoscape.getEdgeAttributes().setListAttribute(edge.getIdentifier(),"relations",relations);
        Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),"labelOfRelations",getLabelOfRelations(relations));
        ArrayList nodes = new ArrayList();
        ArrayList edges = new ArrayList();
        nodes.add(node_1);
        nodes.add(node_2);
        edges.add(edge);
        Cytoscape.createNetwork(nodes, edges, "test", Cytoscape.getCurrentNetwork(), true);
         */
        CyNetwork currentNetwork = null;
//        currentNetwork = Cytoscape.createNetwork("kewa");
//        Cytoscape.destroyNetwork(currentNetwork);
        if (STEP) {
            if (lastCurrentNetwork != null) {
                Cytoscape.destroyNetwork(lastCurrentNetwork);
                Cytoscape.destroyNetworkView(lastCurrentNetwork);

//        } else {
                if (wallet.hasOpenTableau()) {
                    currentNetwork = Cytoscape.createNetwork("Tableaux for satisfiable formula: " + formula);
                } else {
                    currentNetwork = Cytoscape.createNetwork("Tableaux for unsatisfiable formula: " + formula);
                }
                lastCurrentNetwork = currentNetwork;
//        }
            }
        } else {
            if (wallet.hasOpenTableau()) {
                currentNetwork = Cytoscape.createNetwork("Tableaux for satisfiable formula: " + formula);
            } else {
                currentNetwork = Cytoscape.createNetwork("Tableaux for unsatisfiable formula: " + formula);
            }
        }

        //System.out.println("Wallet : " + wallet.getName());
        for (Enumeration enumr = wallet.getGraphesEnum(); enumr.hasMoreElements();) {
            Tableau t = (Tableau) enumr.nextElement();
            ArrayList nodes = new ArrayList();
            for (Enumeration enum_ = t.getNodesEnumeration(); enum_.hasMoreElements();) {
                TableauNode n = (TableauNode) enum_.nextElement();
                Node node = Cytoscape.getCyNode(n.getName(), true);
                ArrayList formulas = new ArrayList();
                for (Enumeration enum__ = n.getMarkedExpressionsEnum(); enum__.hasMoreElements();) {
                    MarkedExpression e = (MarkedExpression) enum__.nextElement();
                    formulas.add(e.toString());
                }
                Cytoscape.getNodeAttributes().setListAttribute(node.getIdentifier(), "formulas", formulas);
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "closed", n.isClosed());
                //Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"labelOfFormulas",getLabelOfFormulas(formulas));
                //Double d = new Double(getMaxLengthOfFormulas(formulas));
                //Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"maxLengthOfFormulas",d);
                //d = new Double(formulas.size());
                //Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(),"formulasNumber",d);
                nodes.add(node);
            }
            ArrayList edges = new ArrayList();
            for (Enumeration enum_ = t.getNodesEnumeration(); enum_.hasMoreElements();) {
                TableauNode n = (TableauNode) enum_.nextElement();
                for (Enumeration enum__ = n.getNextEdgesEnum(); enum__.hasMoreElements();) {
                    TableauEdge e = (TableauEdge) enum__.nextElement();
                    Edge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(e.getBeginNode().getName()),
                            Cytoscape.getCyNode(e.getEndNode().getName()),
                            Semantics.INTERACTION, e.getRelation().toString(), true, true);
                    ArrayList relations = new ArrayList();
                    relations.add(e.getRelation().toString());
                    Cytoscape.getEdgeAttributes().setListAttribute(edge.getIdentifier(), "relations", relations);
                    //Cytoscape.getEdgeAttributes().setAttribute(edge.getIdentifier(),"labelOfRelations",getLabelOfRelations(relations));
                    edges.add(edge);
                }
            }
            if (t.isClosed()) {
                Cytoscape.createNetwork(nodes, edges, "Closed Tableau " + t.getName(), currentNetwork, true);
            } else {
                Cytoscape.createNetwork(nodes, edges, "Open Tableau " + t.getName(), currentNetwork, true);
            }
            //
            //Making Hierarchic yFiles layouting for each tableau
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
            JMenuItem yHierarchic = null;
            for (int i = 0; i < submenus.length; i++) {
                yHierarchic = ((JMenuItem) submenus[i]);
                if (yHierarchic.getText().equals("Hierarchic")) {
                    break;
                }
            }
            yHierarchic.doClick();
        }
    }

    @Deprecated
    public static String[] createTableauImagesInCy(Wallet wallet, MarkedExpression formula) {
        int imgNum = 0;
        for (Enumeration enumr1 = wallet.getGraphesEnum(); enumr1.hasMoreElements();) {
            imgNum++;
            enumr1.nextElement();
        }
        String[] imagesNames = new String[imgNum];
        imgNum = 0;
        CyNetwork currentNetwork = Cytoscape.createNetwork("Tableaux for: " + formula);
        for (Enumeration enumr = wallet.getGraphesEnum(); enumr.hasMoreElements();) {
            Tableau t = (Tableau) enumr.nextElement();
            ArrayList nodes = new ArrayList();
            for (Enumeration enum_ = t.getNodesEnumeration(); enum_.hasMoreElements();) {
                TableauNode n = (TableauNode) enum_.nextElement();
                Node node = Cytoscape.getCyNode(n.getName(), true);
                ArrayList formulas = new ArrayList();
                for (Enumeration enum__ = n.getMarkedExpressionsEnum(); enum__.hasMoreElements();) {
                    MarkedExpression e = (MarkedExpression) enum__.nextElement();
                    formulas.add(e.toString());
                }
                Cytoscape.getNodeAttributes().setListAttribute(node.getIdentifier(), "formulas", formulas);
                Cytoscape.getNodeAttributes().setAttribute(node.getIdentifier(), "closed", n.isClosed());
                nodes.add(node);
            }
            ArrayList edges = new ArrayList();
            for (Enumeration enum_ = t.getNodesEnumeration(); enum_.hasMoreElements();) {
                TableauNode n = (TableauNode) enum_.nextElement();
                for (Enumeration enum__ = n.getNextEdgesEnum(); enum__.hasMoreElements();) {
                    TableauEdge e = (TableauEdge) enum__.nextElement();
                    Edge edge = Cytoscape.getCyEdge(Cytoscape.getCyNode(e.getBeginNode().getName()),
                            Cytoscape.getCyNode(e.getEndNode().getName()),
                            Semantics.INTERACTION, e.getRelation().toString(), true, true);
                    ArrayList relations = new ArrayList();
                    relations.add(e.getRelation().toString());
                    Cytoscape.getEdgeAttributes().setListAttribute(edge.getIdentifier(), "relations", relations);
                    edges.add(edge);
                }
            }
            if (t.isClosed()) {
                Cytoscape.createNetwork(nodes, edges, "Closed Tableau " + t.getName(), currentNetwork, true);
            } else {
                Cytoscape.createNetwork(nodes, edges, "Open Tableau " + t.getName(), currentNetwork, true);
            }
            //
            //Making Hierarchic yFiles layouting for each tableau
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
            JMenuItem yHierarchic = null;
            for (int i = 0; i < submenus.length; i++) {
                yHierarchic = ((JMenuItem) submenus[i]);
                if (yHierarchic.getText().equals("Hierarchic")) {
                    break;
                }
            }
            yHierarchic.doClick();
            //
            //Export images of tableaux
            //
            try {
                Properties p = new Properties();
                //p.setProperty("PageSize","A5");
                File imgFile = new File(Lotrec.getWorkingPath() + "tableauxImages/" + t + ".png");
                imagesNames[imgNum] = imgFile.getName();
                imgNum++;
                VectorGraphics g = new ImageGraphics2D(imgFile, Cytoscape.getCurrentNetworkView().getComponent().getSize(), "png");
                g.setProperties(p);
                g.startExport();
                Cytoscape.getCurrentNetworkView().getComponent().print(g);
                g.endExport();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }
        return imagesNames;
    }

    @Deprecated
    public static String getLabelOfFormulas(ArrayList formulas) {
        String strFormulas = new String("No Formulas");
        if (formulas != null && formulas.size() >= 0) {
            strFormulas = (String) formulas.get(0);
            int i = 1;
            while (i < formulas.size()) {
                strFormulas = strFormulas + "\n" + (String) formulas.get(i);
                i++;
            }
        }
        return strFormulas;
    }

    @Deprecated
    public static int getMaxLengthOfFormulas(ArrayList formulas) {
        int max = 0;
        String formula;
        if (formulas != null && formulas.size() >= 0) {
            for (int i = 0; i < formulas.size(); i++) {
                formula = (String) formulas.get(i);
                if (max < formula.length()) {
                    max = formula.length();
                }
            }
        }
        //if(max<formulas.size()) max=formulas.size();
        return max;
    }

    @Deprecated
    public static String getLabelOfRelations(ArrayList relations) {
        String strRelations = new String("No Formulas");
        if (relations != null && relations.size() >= 0) {
            strRelations = (String) relations.get(0);
            int i = 1;
            while (i < relations.size()) {
                strRelations = strRelations + "\n" + (String) relations.get(i);
                i++;
            }
        }
        return strRelations;
    }

    @Deprecated
    public static int getMaxLengthOfRelations(ArrayList relations) {
        int max = 0;
        String relation;
        if (relations != null && relations.size() >= 0) {
            for (int i = 0; i < relations.size(); i++) {
                relation = (String) relations.get(i);
                if (max < relation.length()) {
                    max = relation.length();
                }
            }
        }
        return max;
    }

    @Deprecated
    public static void main(String[] args) throws Exception {
        CyMain.main(args);
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton b = new JButton("display");
        b.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //displayTableauInCy();
            }
        });
        f.getContentPane().add(b);
        f.setVisible(true);
    }
}
