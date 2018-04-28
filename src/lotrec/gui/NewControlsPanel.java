/*
 * NewControlsPanel.java
 *
 * Created on March 19, 2009, 11:20 AM
 */
package lotrec.gui;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Wallet;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauNode;

/**
 *
 * @author  Administrator
 */
public class NewControlsPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private JDesktopPane desktopPane;

    /** Creates new form NewControlsPanel */
    public NewControlsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
//        desktopPane = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane();
//        desktopPane.setBackground(java.awt.Color.WHITE);
//        javax.swing.GroupLayout pnlPartialPremodelLayout = new javax.swing.GroupLayout(pnlPartialPremodel);
//        pnlPartialPremodel.setLayout(pnlPartialPremodelLayout);
//        pnlPartialPremodelLayout.setHorizontalGroup(
//                pnlPartialPremodelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 589, Short.MAX_VALUE));
//        pnlPartialPremodelLayout.setVerticalGroup(
//                pnlPartialPremodelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(desktopPane, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE));

//        desktopPane = new JDesktopPane();
//        org.jdesktop.layout.GroupLayout pnlNetworksLayout = new org.jdesktop.layout.GroupLayout(desktopPane);
//        desktopPane.setLayout(pnlNetworksLayout);
//        pnlNetworksLayout.setHorizontalGroup(
//                pnlNetworksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 478, Short.MAX_VALUE));
//        pnlNetworksLayout.setVerticalGroup(
//                pnlNetworksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 350, Short.MAX_VALUE));
//
//        org.jdesktop.layout.GroupLayout pnlCyBiModalLayout = new org.jdesktop.layout.GroupLayout(pnlPartialPremodel);
//        pnlPartialPremodel.setLayout(pnlCyBiModalLayout);
//        pnlCyBiModalLayout.setHorizontalGroup(
//                pnlCyBiModalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(desktopPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
//        pnlCyBiModalLayout.setVerticalGroup(
//                pnlCyBiModalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(desktopPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));        
    }

    public NewControlsPanel() {
        this(null);
    }

    public void buildSatPremodel(MarkedExpression formula) {
        Wallet currentWallet = new Wallet();
        Tableau tableau = new Tableau("premodel");
        currentWallet.add(tableau);
        TableauNode.initialiseForName();
        TableauNode firstNode = new TableauNode();
        tableau.add(firstNode);
        firstNode.add(formula);
        CyTableauDisplayer.displayTableauInCy(tableau);
        JInternalFrame frame = cytoscape.Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getSelectedFrame();
        try {
            frame.setMaximum(true);
        } catch (PropertyVetoException ex) {
            System.out.println("An error occured during frame maximizing\n"+ex.getMessage());
        }
//        desktopPane.add(frame);
//        pnlPartialPremodel.add(cytoscape.Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(
//                cytoscape.Cytoscape.getCurrentNetworkView()));
//        pnlPartialPremodel.removeAll();
//        pnlPartialPremodel.add(frame.getRootPane());
//        pnlPartialPremodel.removeAll();
//        Component view = ((DGraphView)Cytoscape.getCurrentNetworkView()).getComponent();
//        frame.remove(view);
            //How to resize this view according to the resize of its container??
//        pnlPartialPremodel.add(view);
//        mainFrame.EditingFrame.removeAll();
//        mainFrame.EditingFrame.setContentPane(frame.getContentPane());
//        mainFrame.EditingFrame.setLayeredPane(frame.getLayeredPane());
//        mainFrame.EditingFrame.pack();
//        mainFrame.EditingFrame.requestFocus();

//        desktopPane.add(frame);

//        pnlPartialPremodel.add(cytoscape.Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(
//                cytoscape.Cytoscape.getCurrentNetworkView()));

//        pnlPartialPremodel.removeAll();        
//        pnlPartialPremodel.add(frame.getRootPane()); 

//        pnlPartialPremodel.removeAll();
//        Component view = ((DGraphView)Cytoscape.getCurrentNetworkView()).getComponent();
//        frame.remove(view);

    //How to resize this view according to the resize of its container??

//        pnlPartialPremodel.add(view);      

//        mainFrame.EditingFrame.removeAll();
//        mainFrame.EditingFrame.setContentPane(frame.getContentPane());
//        mainFrame.EditingFrame.setLayeredPane(frame.getLayeredPane());        

//        mainFrame.EditingFrame.pack();
//        mainFrame.EditingFrame.requestFocus();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlPartialPremodel = new javax.swing.JPanel();

        pnlPartialPremodel.setToolTipText("Partial Premodel Panel");

        javax.swing.GroupLayout pnlPartialPremodelLayout = new javax.swing.GroupLayout(pnlPartialPremodel);
        pnlPartialPremodel.setLayout(pnlPartialPremodelLayout);
        pnlPartialPremodelLayout.setHorizontalGroup(
            pnlPartialPremodelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 589, Short.MAX_VALUE)
        );
        pnlPartialPremodelLayout.setVerticalGroup(
            pnlPartialPremodelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPartialPremodel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlPartialPremodel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel pnlPartialPremodel;
    // End of variables declaration//GEN-END:variables
}
