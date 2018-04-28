/*
 * TableauxPanel.java
 *
 * Created on 1 novembre 2007, 16:18
 */
package lotrec.gui;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.view.cytopanels.CytoPanelState;
import giny.model.Node;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.graph.Graph;
import lotrec.dataStructure.graph.Wallet;
import lotrec.dataStructure.tableau.MarkEvent;
import lotrec.dataStructure.tableau.MarkExpressionEvent;
import lotrec.dataStructure.tableau.Tableau;
import lotrec.dataStructure.tableau.TableauEdge;
import lotrec.dataStructure.tableau.TableauNode;
import lotrec.parser.OldiesTokenizer;
import lotrec.resources.ResourcesProvider;
import lotrec.util.Marked;

/**
 *
 * @author  said
 */
public class TableauxPanel extends javax.swing.JPanel {

    public static SelectEventListener SELECTION_LISTENER = new SelectEventListener() {

        @Override
        public void onSelectEvent(SelectEvent event) {
            Set<Node> nodes = event.getSource().getSelectedNodes();
            if (nodes.size() == 1) {
//                Node selectedNode = nodes.iterator().next();
                CyNode selectedNode = (CyNode) nodes.iterator().next();
                StringBuffer sb = new StringBuffer(selectedNode.getIdentifier());
                boolean closed = (boolean) Cytoscape.getNodeAttributes().getBooleanAttribute(selectedNode.getIdentifier(), "closed");
                if (closed) {
                    sb.append("; closed");
                }
                ArrayList marks = (ArrayList) Cytoscape.getNodeAttributes().getListAttribute(selectedNode.getIdentifier(), "marks");
                if (marks != null && marks.size() > 0) {

                    sb.append("; it is marked by:[");
                    for (Object m : marks) {
                        sb.append(m.toString() + ", ");
                    }
                    sb.setLength(sb.length() - 2);
                    sb.append("]");

                }
//                lblSelection.setText(sb.toString());
            }
            if (nodes.size() == 0) {
//                lblSelection.setText("-");
            }
            if (nodes.size() > 1) {
//                lblSelection.setText("select only one node..");
            }
        }
    };
    public static String TABLEAU_TREE = "Tableaux Tree";
    private int selectionMode = 0;//stands for one by one..
    private int[] lastSelectedIndices;//should never be changed outside valueChanged
    private ArrayList<String> lastSelectedTabs;
//    private Component navigator;
    private MainFrame mainFrame;
    private ResourceBundle resource;

    /** Creates new form TableauxPanel */
    public TableauxPanel() {
        initComponents();
    }

    public TableauxPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        initializeCyFrame();
        setSelectionModeEnabled(false);
        disableControlsButtons();
    }

    public void displayEngineElapsedTime(String elapsedTime) {
        lblElapsedTime.setText(elapsedTime);
    }

    public void displayEngineAppliedRules(String appliedRules) {
        lblAppliedRules.setText(appliedRules);
    }

    public void displayEngineTotalAppliedRules(String totalppliedRules) {
        lblTotalAppliedRules.setText(totalppliedRules);
    }

    public void displayEngineStatus(String status) {
        lblEngineStatus.setText(status);
    }

    public void displayTableauxCount(int tableauxCount) {
        lblTableauxCount.setText(String.valueOf(tableauxCount));
    }

    public void initializeCyFrame() {
        Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.HIDE);
        Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
        JDesktopPane desktopPane = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane();//new JDesktopPane();//

        desktopPane.setBackground(Color.WHITE);
        org.jdesktop.layout.GroupLayout pnlNetworksLayout = new org.jdesktop.layout.GroupLayout(desktopPane);
        desktopPane.setLayout(pnlNetworksLayout);
        pnlNetworksLayout.setHorizontalGroup(
                pnlNetworksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 478, Short.MAX_VALUE));
        pnlNetworksLayout.setVerticalGroup(
                pnlNetworksLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(0, 350, Short.MAX_VALUE));

        org.jdesktop.layout.GroupLayout pnlCyBiModalLayout = new org.jdesktop.layout.GroupLayout(pnlCyBiModal);
        pnlCyBiModal.setLayout(pnlCyBiModalLayout);
        pnlCyBiModalLayout.setHorizontalGroup(
                pnlCyBiModalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(desktopPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        pnlCyBiModalLayout.setVerticalGroup(
                pnlCyBiModalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(desktopPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    }

    public void setNavigator(Component navigator) {
        if (navigator != null) {
//            JPanel glassPanel = new JPanel();
//            glassPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Galss"));
//            glassPanel.setLayout(new java.awt.FlowLayout());
//            glassPanel.add(navigator);
//            spltLeftBottom.setRightComponent(glassPanel);
            spltLeftBottom.setRightComponent(navigator);
            spltLeftBottom.validate();
        //spltLeft.setRightComponent(navigator);
        //spltLeft.validate();
        }
        System.out.println("Navigator set..");
    }

    public void initializeCyFrameOld() {
        CytoscapeFrame.remove(tolbrCytoToolBar);
//        CytoscapeFrame.setTitle("Tableaux Results");
        try {
            CytoscapeFrame.setMaximum(true);
        } catch (java.beans.PropertyVetoException e1) {
            e1.printStackTrace();
        }
        CytoscapeFrame.setContentPane(Cytoscape.getDesktop().getContentPane());
        Cytoscape.getDesktop().getMain_panel().add(tolbrCytoToolBar, BorderLayout.NORTH);
//        Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setTitle("Tableaux Lists");
        Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.HIDE);
//        Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setTitle("Tableaux Editor");
        Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
        tglbtnFullScreen.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tglbtnFullScreen.isSelected()) {
//                    mainFrame.maximizeTableauxPanel();
//                    java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
//                    screenSize.setSize(screenSize.getWidth(), screenSize.getHeight() - 22);
//                    dlgCyFrame.setSize(screenSize);
                } else {
//                    mainFrame.minimizeTableauxPanel();
                }
                refreshToggleButtons();
            }
        });
        tglbtnShowHideTabList.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tglbtnShowHideTabList.isSelected()) {
                    Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.DOCK);
                } else {
                    Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).setState(CytoPanelState.HIDE);
                }
                refreshToggleButtons();
            }
        });
        tglbtnShowHideTabEditor.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (tglbtnShowHideTabEditor.isSelected()) {
                    Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.DOCK);
                } else {
                    Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH).setState(CytoPanelState.HIDE);
                }
                refreshToggleButtons();
            }
        });
//        System.out.println("Bar Height before: "+((javax.swing.plaf.basic.BasicInternalFrameUI)CytoscapeFrame.getUI()).getNorthPane().getHeight() );
//        ((javax.swing.plaf.basic.BasicInternalFrameUI)CytoscapeFrame.getUI()).getNorthPane().setPreferredSize( new Dimension(0,0) );
//        System.out.println("Bar Height after: "+((javax.swing.plaf.basic.BasicInternalFrameUI)CytoscapeFrame.getUI()).getNorthPane().getHeight() );

        ((javax.swing.plaf.basic.BasicInternalFrameUI) CytoscapeFrame.getUI()).setNorthPane(null);
        dlgCyFrame.pack();
        dlgCyFrame.setVisible(true);
    }

    public void fillTableauxList() {
        Wallet wallet = mainFrame.getEngine().getCurrentWallet();
        DefaultListModel listModel = new DefaultListModel();
        listModel.addElement(TableauxPanel.TABLEAU_TREE);
        for (Graph g : wallet.getGraphes()) {
            if (ckbxFilterClosedTableaux.isSelected() && g.isClosed()) {
                continue;
            }
            listModel.addElement(g.getName());
//            System.out.println("tab name is: '" + g.getName() + "'");
        }
        lstTableaux.setModel(listModel);
        displayTableauxCount(wallet.getGraphes().size());
        fillTableauxInEditor();
    }

    public void enableControlsButtons() {
        btnNextStep.setEnabled(false);
        btnPauseResume.setEnabled(true);
        btnStop.setEnabled(true);
    }

    public void disableControlsButtons() {
        btnNextStep.setEnabled(false);
        btnPauseResume.setEnabled(false);
        btnStop.setEnabled(false);
    }

    public void enableStepControlsButtons() {
        btnNextStep.setEnabled(true);
        btnPauseResume.setEnabled(false);
    }

    public void disableStepControlsButtons() {
        btnNextStep.setEnabled(false);
        btnPauseResume.setEnabled(true);
    }

    public void displayTableaux() {
        Wallet wallet = mainFrame.getEngine().getCurrentWallet();
        CyDisplayer.STEP = true;
        displayTableauxFor(wallet, mainFrame.getEngine().getFormula(), mainFrame.getEngine().getLogic().getName());
        Cytoscape.getDesktop().getMain_panel().add(tolbrCytoToolBar, BorderLayout.NORTH);

//        tileWindows();
//        cascadeTableauxFrames();
//        tileWindows();
    }

//    public void buildDisplayTableaux() {
////        try {
//        Wallet wallet = mainFrame.getEngine().buildTableaux();
////            mainFrame.getTableauxEnginePanel().getEngine().join();
//        displayTableauxFor(wallet, mainFrame.getEngine().getFormula(), mainFrame.getEngine().getLogic().getName());
//        Cytoscape.getDesktop().getMain_panel().add(tolbrCytoToolBar, BorderLayout.NORTH);
////        } catch (InterruptedException ex) {
////            Logger.getLogger(TableauxPanel.class.getName()).log(Level.SEVERE, null, ex);
////        }
////        tileWindows();
////        cascadeTableauxFrames();
////        tileWindows();
//    }
    public void cascadeTableauxFrames() {
        JInternalFrame[] frames = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane().getAllFrames();
        int x = 10;
        int y = 10;
        for (int i = frames.length - 1; i >= 0; i--) {
            frames[i].setLocation(x, y);
            x += 10;
            y += 30;
        }
    }

    public void tileWindows() {
        JDesktopPane desktop = Cytoscape.getDesktop().getNetworkViewManager().getDesktopPane();
        // count frames that aren't iconized
        int frameCount = 0;
        for (JInternalFrame frame : desktop.getAllFrames()) {
            if (!frame.isIcon()) {
                frameCount++;
            }
        }
        if (frameCount == 0) {
            return;
        }

        int rows = (int) Math.sqrt(frameCount);
        int cols = frameCount / rows;
        int extra = frameCount % rows;
        // number of columns with an extra row

        int width = desktop.getWidth() / cols;
        int height = desktop.getHeight() / rows;
        int r = 0;
        int c = 0;
        for (JInternalFrame frame : desktop.getAllFrames()) {
            if (!frame.isIcon()) {
                try {
                    frame.setMaximum(false);
                    frame.reshape(c * width, r * height, width, height);
                    r++;
                    if (r == rows) {
                        r = 0;
                        c++;
                        if (c == cols - extra) {
                            // start adding an extra row
                            rows++;
                            height = desktop.getHeight() / rows;
                        }
                    }
                } catch (PropertyVetoException e) {
                }
            }
        }
    }

    public void displayTableauxFor(Wallet wallet, MarkedExpression formula, String logicName) {
        CyDisplayer.displayTableauInCy(wallet, formula);
    }

    private void displayTableauxTree() {
        Vector<Tableau> currentTableauxList = new Vector();
        for (Graph g : mainFrame.getEngine().getCurrentWallet().getGraphes()) {
            currentTableauxList.add((Tableau) g);
        }
        CyTableauDisplayer.displayTableauxTreeInCy(currentTableauxList);
    }

    private void diplayAllTableaux() {
        mainFrame.showWaitCursor();
        CyTableauDisplayer.flush();
//        displayTableauxTree();
        final Vector<Tableau> selectedTableaux = new Vector();
        for (int tabIndex = 0; tabIndex < lstTableaux.getModel().getSize(); tabIndex++) {
            if (tabIndex == 0) {
                displayTableauxTree();
            } else {
//                selectedTableaux.add((Tableau) mainFrame.getEngine().getCurrentWallet().getGraphes().get(tabIndex - 1));
                selectedTableaux.add((Tableau) mainFrame.getEngine().getCurrentWallet().getGraph((String) lstTableaux.getModel().getElementAt(tabIndex)));
            }
        }
        CyTableauDisplayer.displayTableauxInCy(selectedTableaux);
        if (MainFrame.rdbtnTileCascade.isSelected()) {
            tileWindows();
        } else {
            cascadeTableauxFrames();
        }
        mainFrame.hideWaitCursor();
        lblPercentage.setText("-");
    }

    private void displaySelectedTableaux() {
        mainFrame.showWaitCursor();
        CyTableauDisplayer.flush();
        int[] tabIndices = lstTableaux.getSelectedIndices();
        Vector<Tableau> selectedTableaux = new Vector();
        for (int tabIndex : tabIndices) {
            if (tabIndex == 0) {
                displayTableauxTree(); //We want de display it anyway..
            } else {
//                selectedTableaux.add((Tableau) mainFrame.getEngine().getCurrentWallet().getGraphes().get(tabIndex - 1));
                selectedTableaux.add((Tableau) mainFrame.getEngine().getCurrentWallet().getGraph((String) lstTableaux.getModel().getElementAt(tabIndex)));
            }
        }
        if (selectedTableaux.size() > 0) {
            CyTableauDisplayer.displayTableauxInCy(selectedTableaux);
        } else {
            lstTableaux.setSelectedIndex(0);
            displayTableauxTree();
        }
        if (MainFrame.rdbtnTileCascade.isSelected()) {
            tileWindows();
        } else {
            cascadeTableauxFrames();
        }
        mainFrame.hideWaitCursor();
    }

    public void displaySelectedTableau() {
        CyTableauDisplayer.flush();
        int tabIndex = lstTableaux.getSelectedIndex();
        if (tabIndex == -1) {
            if (lstTableaux.getModel().getSize() > 0) {
                lstTableaux.setSelectedIndex(0);
                displayTableauxTree();
            }
        } else if (tabIndex == 0) {
            displayTableauxTree();
        } else {
//            Tableau t = (Tableau) mainFrame.getEngine().getCurrentWallet().getGraphes().get(tabIndex - 1);
            Tableau t = (Tableau) mainFrame.getEngine().getCurrentWallet().getGraph((String) lstTableaux.getSelectedValue());
            CyTableauDisplayer.displayTableauInCy(t);
        }
    }

    public void fillTabListAndDisplayFirst() {
        fillTableauxList();
        if (lstTableaux.getModel().getSize() > 0) {
            if (lstTableaux.getModel().getSize() == 1) {
                getTableauxList().setSelectedIndex(0);// display the Tableaux Tree
                displaySelectedTableau();
            } else {
                getTableauxList().setSelectedIndex(1);// display the tab in place of Tableaux Tree
                displaySelectedTableau();
            }
        }
//        if (mainFrame.getEngine().getCurrentWallet().getGraphes().size() >= 0) {
//            getTableauxList().setSelectedIndex(1);// display the tab in place of Tableaux Tree
//            displaySelectedTableau();
//        }
        lstTableaux.validate();
//        lstTableaux.updateUI();
    }

    public void fillTabListAndDisplayLastChosenOnes() {
        int[] copyOfLastSelectedIndices = lastSelectedIndices;
//        ArrayList<String> copyOfLastSelectedTabs = lastSelectedTabs;
        fillTableauxList();
        getTableauxList().setSelectedIndices(copyOfLastSelectedIndices);
        lstTableaux.validate();
        if (selectionMode == 2) {
            diplayAllTableaux();
        } else if (ckbxFilterClosedTableaux.isSelected()) {
            displaySelectedTableau();
        } else {
            displayLastChosenOnes();
        }
    }

    public void displayLastChosenOnes() {
        mainFrame.showWaitCursor();
        CyTableauDisplayer.flush();
        Vector<Tableau> selectedTableaux = new Vector();
        for (String tabName : lastSelectedTabs) {
            if (tabName.equals(TableauxPanel.TABLEAU_TREE)) {
                displayTableauxTree();
            } else {
                selectedTableaux.add((Tableau) mainFrame.getEngine().getCurrentWallet().getGraph(tabName));
            }
        }
//
//        for (int i = 0; i < lastSelectedIndices.length; i++) {
//            if (lastSelectedIndices[i] == 0) {
//                displayTableauxTree();
//            } else {
//                selectedTableaux.add((Tableau) mainFrame.getEngine().getCurrentWallet().getGraphes().get(lastSelectedIndices[i] - 1));
//            }
//        }
        if (selectedTableaux.size() > 0) {
            CyTableauDisplayer.displayTableauxInCy(selectedTableaux);
        } else {
            lstTableaux.setSelectedIndex(0);
            displayTableauxTree();
        }
        if (selectedTableaux.size() > 1) {
            if (MainFrame.rdbtnTileCascade.isSelected()) {
                tileWindows();
            } else {
                cascadeTableauxFrames();
            }
        }
        mainFrame.hideWaitCursor();
    }

    public void setSelectionModeEnabled(boolean yesNo) {
        rdbtnDisplayOne.setEnabled(yesNo);
        rdbtnDisplayMultiple.setEnabled(yesNo);
        rdbtnDisplayAll.setEnabled(yesNo);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dlgCyFrame = new javax.swing.JDialog();
        CytoscapeFrame = new javax.swing.JInternalFrame();
        tolbrCytoToolBar = new javax.swing.JToolBar();
        tglbtnFullScreen = new javax.swing.JToggleButton();
        tglbtnCascadeTiles = new javax.swing.JToggleButton();
        tglbtnShowHideTabEditor = new javax.swing.JToggleButton();
        tglbtnShowHideTabList = new javax.swing.JToggleButton();
        btngrpDisplayMode = new javax.swing.ButtonGroup();
        dlgPremodelsEditor = new javax.swing.JDialog();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        rdbtnCreate = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        txfNewNodeId = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        rdbtnLink = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        txfRelation = new javax.swing.JTextField();
        cmbxNodeFrom = new javax.swing.JComboBox();
        cmbxNodeTo = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        rdbtnAdd = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        cmbxNodeAdd = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        txfFormulaAdd = new javax.swing.JTextField();
        jPanel11 = new javax.swing.JPanel();
        cmbxTableauxList = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        rdbtnStop = new javax.swing.JRadioButton();
        jPanel10 = new javax.swing.JPanel();
        rdbtnMark = new javax.swing.JRadioButton();
        jLabel19 = new javax.swing.JLabel();
        cmbxNodeMark = new javax.swing.JComboBox();
        jLabel20 = new javax.swing.JLabel();
        txfMark = new javax.swing.JTextField();
        rdbtnUnMark = new javax.swing.JRadioButton();
        jPanel12 = new javax.swing.JPanel();
        rdbtnMarkFormula = new javax.swing.JRadioButton();
        jLabel21 = new javax.swing.JLabel();
        cmbxFormulaMarkNode = new javax.swing.JComboBox();
        jLabel22 = new javax.swing.JLabel();
        txfFormulaMark = new javax.swing.JTextField();
        rdbtnUnMarkFormula = new javax.swing.JRadioButton();
        jLabel23 = new javax.swing.JLabel();
        cmbxFormulaToMark = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        btnApply = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btngrpActions = new javax.swing.ButtonGroup();
        dlgRunInfo = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblEngineStatus = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblTableauxCount = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblLastAppliedRule = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblOnTableauName = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        lblTotalAppliedRules = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        lblElapsedTime = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        lblAppliedRules = new javax.swing.JLabel();
        dlgOtherOldComponents = new javax.swing.JDialog();
        jPanel3 = new javax.swing.JPanel();
        rdbtnDisplayOne = new javax.swing.JRadioButton();
        rdbtnDisplayMultiple = new javax.swing.JRadioButton();
        btnDisplay = new javax.swing.JButton();
        rdbtnDisplayAll = new javax.swing.JRadioButton();
        lblPercentage = new javax.swing.JLabel();
        dlgFilters = new javax.swing.JDialog();
        jPanel4 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        cmbxNodeFilter = new javax.swing.JComboBox();
        ckbxFilterClosedTableaux = new javax.swing.JCheckBox();
        spltMain = new javax.swing.JSplitPane();
        spltLeft = new javax.swing.JSplitPane();
        pnlControls = new javax.swing.JPanel();
        lblAtRule = new javax.swing.JLabel();
        btnPauseResume = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        btnNextStep = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        spltLeftBottom = new javax.swing.JSplitPane();
        pnlTabList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstTableaux = new javax.swing.JList();
        pnlGlass = new javax.swing.JPanel();
        pnlCyBiModal = new javax.swing.JPanel();

        dlgCyFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        CytoscapeFrame.setBorder(null);
        CytoscapeFrame.setVisible(true);

        tglbtnFullScreen.setText("Full Screen");
        tglbtnFullScreen.setFocusable(false);
        tglbtnFullScreen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tglbtnFullScreen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tolbrCytoToolBar.add(tglbtnFullScreen);

        tglbtnCascadeTiles.setText("Cascade");
        tglbtnCascadeTiles.setFocusable(false);
        tglbtnCascadeTiles.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tglbtnCascadeTiles.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tglbtnCascadeTiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglbtnCascadeTilesActionPerformed(evt);
            }
        });
        tolbrCytoToolBar.add(tglbtnCascadeTiles);

        tglbtnShowHideTabEditor.setText("Hide Tableaux Editor");
        tglbtnShowHideTabEditor.setFocusable(false);
        tglbtnShowHideTabEditor.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tglbtnShowHideTabEditor.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tolbrCytoToolBar.add(tglbtnShowHideTabEditor);

        tglbtnShowHideTabList.setText("Hide Tableaux List");
        tglbtnShowHideTabList.setFocusable(false);
        tglbtnShowHideTabList.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        tglbtnShowHideTabList.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        tolbrCytoToolBar.add(tglbtnShowHideTabList);

        org.jdesktop.layout.GroupLayout CytoscapeFrameLayout = new org.jdesktop.layout.GroupLayout(CytoscapeFrame.getContentPane());
        CytoscapeFrame.getContentPane().setLayout(CytoscapeFrameLayout);
        CytoscapeFrameLayout.setHorizontalGroup(
            CytoscapeFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tolbrCytoToolBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
        );
        CytoscapeFrameLayout.setVerticalGroup(
            CytoscapeFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(CytoscapeFrameLayout.createSequentialGroup()
                .add(tolbrCytoToolBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(462, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout dlgCyFrameLayout = new org.jdesktop.layout.GroupLayout(dlgCyFrame.getContentPane());
        dlgCyFrame.getContentPane().setLayout(dlgCyFrameLayout);
        dlgCyFrameLayout.setHorizontalGroup(
            dlgCyFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(CytoscapeFrame)
        );
        dlgCyFrameLayout.setVerticalGroup(
            dlgCyFrameLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(CytoscapeFrame)
        );

        dlgPremodelsEditor.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dlgPremodelsEditor.setTitle("Premodels Editor");
        dlgPremodelsEditor.setAlwaysOnTop(true);
        dlgPremodelsEditor.setResizable(false);
        dlgPremodelsEditor.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgPremodelsEditorWindowClosing(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/idea.PNG"))); // NOI18N
        jLabel7.setText("Choose a Premodel, an Action to do, fill in its arguments than Apply.");

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btngrpActions.add(rdbtnCreate);
        rdbtnCreate.setText("Create (new) Node:");
        rdbtnCreate.setActionCommand("create");
        rdbtnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        jLabel9.setText("Node Id:");

        txfNewNodeId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfNewNodeIdActionPerformed(evt);
            }
        });

        jLabel10.setText("(empty Id is recommended)");

        jLabel11.setText("It's optional, cause LoTREC choose a default convenient name");

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rdbtnCreate)
                    .add(jPanel7Layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel7Layout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(jLabel11))
                            .add(jPanel7Layout.createSequentialGroup()
                                .add(jLabel9)
                                .add(18, 18, 18)
                                .add(txfNewNodeId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel10)))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(rdbtnCreate)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(txfNewNodeId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel10))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel11))
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btngrpActions.add(rdbtnLink);
        rdbtnLink.setText("Link (2 existing) Nodes:");
        rdbtnLink.setActionCommand("link");
        rdbtnLink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        jLabel12.setText("(From) Parent Node");

        jLabel13.setText("(To) Child Node");

        jLabel14.setText("(Link label) Relation");

        txfRelation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfRelationActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rdbtnLink)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jPanel8Layout.createSequentialGroup()
                                .add(jLabel13)
                                .add(18, 18, 18)
                                .add(cmbxNodeTo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel8Layout.createSequentialGroup()
                                .add(jLabel12)
                                .add(18, 18, 18)
                                .add(cmbxNodeFrom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel8Layout.createSequentialGroup()
                                .add(jLabel14)
                                .add(18, 18, 18)
                                .add(txfRelation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(124, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .add(rdbtnLink)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(cmbxNodeFrom, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(cmbxNodeTo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txfRelation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel14)))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btngrpActions.add(rdbtnAdd);
        rdbtnAdd.setText("Add a Formula to (an existing) Node:");
        rdbtnAdd.setActionCommand("add");
        rdbtnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        jLabel15.setText("Node");

        jLabel16.setText("Formula");

        txfFormulaAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfFormulaAddActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel9Layout = new org.jdesktop.layout.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rdbtnAdd)
                    .add(jPanel9Layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel16)
                            .add(jLabel15))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cmbxNodeAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(txfFormulaAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(120, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9Layout.createSequentialGroup()
                .add(rdbtnAdd)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel15)
                    .add(cmbxNodeAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(txfFormulaAdd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        cmbxTableauxList.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Premodels list>" }));
        cmbxTableauxList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxTableauxListActionPerformed(evt);
            }
        });

        jLabel8.setText("Edit Premodel:");

        btngrpActions.add(rdbtnStop);
        rdbtnStop.setText("Stop this premodel");
        rdbtnStop.setActionCommand("stop");
        rdbtnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel11Layout = new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel8)
                .add(18, 18, 18)
                .add(cmbxTableauxList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(rdbtnStop)
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(cmbxTableauxList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(rdbtnStop))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btngrpActions.add(rdbtnMark);
        rdbtnMark.setText("Mark a Node");
        rdbtnMark.setActionCommand("mark");
        rdbtnMark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        jLabel19.setText("Node");

        jLabel20.setText("Mark");

        txfMark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfMarkActionPerformed(evt);
            }
        });

        btngrpActions.add(rdbtnUnMark);
        rdbtnUnMark.setText("UnMark a Node");
        rdbtnUnMark.setActionCommand("unMark");
        rdbtnUnMark.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        rdbtnUnMark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(rdbtnMark)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 172, Short.MAX_VALUE)
                .add(rdbtnUnMark))
            .add(jPanel10Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel20)
                    .add(jLabel19))
                .add(18, 18, 18)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cmbxNodeMark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txfMark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 131, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(161, 161, 161))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rdbtnMark)
                    .add(rdbtnUnMark))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(cmbxNodeMark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(txfMark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        btngrpActions.add(rdbtnMarkFormula);
        rdbtnMarkFormula.setText("Mark Formulas of a Node");
        rdbtnMarkFormula.setActionCommand("markFormulas");
        rdbtnMarkFormula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        jLabel21.setText("Node");

        cmbxFormulaMarkNode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxFormulaMarkNodeActionPerformed(evt);
            }
        });

        jLabel22.setText("Mark");

        txfFormulaMark.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfFormulaMarkActionPerformed(evt);
            }
        });

        btngrpActions.add(rdbtnUnMarkFormula);
        rdbtnUnMarkFormula.setText("UnMark formulas of a Node");
        rdbtnUnMarkFormula.setActionCommand("unMarkFormulas");
        rdbtnUnMarkFormula.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        rdbtnUnMarkFormula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TableauEditingActionChooser(evt);
            }
        });

        jLabel23.setText("Formula");

        cmbxFormulaToMark.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<No Formulas in this node>" }));

        org.jdesktop.layout.GroupLayout jPanel12Layout = new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(rdbtnMarkFormula)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 56, Short.MAX_VALUE)
                .add(rdbtnUnMarkFormula))
            .add(jPanel12Layout.createSequentialGroup()
                .add(21, 21, 21)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel12Layout.createSequentialGroup()
                        .add(jLabel22)
                        .add(18, 18, 18)
                        .add(txfFormulaMark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 131, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel12Layout.createSequentialGroup()
                        .add(jLabel23)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbxFormulaToMark, 0, 293, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(jPanel12Layout.createSequentialGroup()
                        .add(jLabel21)
                        .add(18, 18, 18)
                        .add(cmbxFormulaMarkNode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel12Layout.createSequentialGroup()
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rdbtnMarkFormula)
                    .add(rdbtnUnMarkFormula))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel21)
                    .add(cmbxFormulaMarkNode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel23)
                    .add(cmbxFormulaToMark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(txfFormulaMark, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel12, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jLabel7, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel11, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setLayout(new java.awt.GridBagLayout());

        btnApply.setText("Apply");
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel6.add(btnApply, gridBagConstraints);

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel6.add(btnCancel, gridBagConstraints);

        org.jdesktop.layout.GroupLayout dlgPremodelsEditorLayout = new org.jdesktop.layout.GroupLayout(dlgPremodelsEditor.getContentPane());
        dlgPremodelsEditor.getContentPane().setLayout(dlgPremodelsEditorLayout);
        dlgPremodelsEditorLayout.setHorizontalGroup(
            dlgPremodelsEditorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
            .add(jPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgPremodelsEditorLayout.setVerticalGroup(
            dlgPremodelsEditorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, dlgPremodelsEditorLayout.createSequentialGroup()
                .add(jPanel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        dlgRunInfo.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dlgRunInfo.setTitle("Run Info");
        dlgRunInfo.setAlwaysOnTop(true);
        dlgRunInfo.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgRunInfoWindowClosing(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel2.setLayout(new java.awt.GridLayout(4, 0));

        jLabel2.setText("Status:");
        jPanel2.add(jLabel2);

        lblEngineStatus.setText("-");
        jPanel2.add(lblEngineStatus);

        jLabel1.setText("Tableaux count:");
        jPanel2.add(jLabel1);

        lblTableauxCount.setText("0");
        jPanel2.add(lblTableauxCount);

        jLabel4.setText("Last applied rule:");
        jPanel2.add(jLabel4);

        lblLastAppliedRule.setText("-");
        jPanel2.add(lblLastAppliedRule);

        jLabel5.setText("Current tableau:");
        jPanel2.add(jLabel5);

        lblOnTableauName.setText("-");
        jPanel2.add(lblOnTableauName);

        jLabel25.setText("Total Applied Rules:");
        jPanel2.add(jLabel25);

        lblTotalAppliedRules.setText("0");
        jPanel2.add(lblTotalAppliedRules);

        jLabel24.setText("Elapsed Time:");
        jPanel2.add(jLabel24);

        lblElapsedTime.setText("0 ms");
        jPanel2.add(lblElapsedTime);

        jLabel17.setText("Applied Rules:");
        jPanel2.add(jLabel17);

        lblAppliedRules.setText("0");
        jPanel2.add(lblAppliedRules);

        org.jdesktop.layout.GroupLayout dlgRunInfoLayout = new org.jdesktop.layout.GroupLayout(dlgRunInfo.getContentPane());
        dlgRunInfo.getContentPane().setLayout(dlgRunInfoLayout);
        dlgRunInfoLayout.setHorizontalGroup(
            dlgRunInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgRunInfoLayout.setVerticalGroup(
            dlgRunInfoLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, 0, 108, Short.MAX_VALUE)
        );

        dlgOtherOldComponents.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dlgOtherOldComponents.setTitle("Glass");
        dlgOtherOldComponents.setAlwaysOnTop(true);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Display Mode"));

        btngrpDisplayMode.add(rdbtnDisplayOne);
        rdbtnDisplayOne.setSelected(true);
        rdbtnDisplayOne.setText("Only Selected Tableau");
        rdbtnDisplayOne.setActionCommand("DisplayOne");

        btngrpDisplayMode.add(rdbtnDisplayMultiple);
        rdbtnDisplayMultiple.setText("Multiple Selection");
        rdbtnDisplayMultiple.setActionCommand("DisplayMultiple");

        btnDisplay.setText("Display");
        btnDisplay.setEnabled(false);

        btngrpDisplayMode.add(rdbtnDisplayAll);
        rdbtnDisplayAll.setText("Display All");
        rdbtnDisplayAll.setActionCommand("DisplayAll");

        lblPercentage.setText("-");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rdbtnDisplayOne)
                    .add(rdbtnDisplayMultiple)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(btnDisplay))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(rdbtnDisplayAll)
                        .add(18, 18, 18)
                        .add(lblPercentage)))
                .addContainerGap(42, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(rdbtnDisplayOne)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rdbtnDisplayMultiple)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(btnDisplay)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rdbtnDisplayAll)
                    .add(lblPercentage)))
        );

        org.jdesktop.layout.GroupLayout dlgOtherOldComponentsLayout = new org.jdesktop.layout.GroupLayout(dlgOtherOldComponents.getContentPane());
        dlgOtherOldComponents.getContentPane().setLayout(dlgOtherOldComponentsLayout);
        dlgOtherOldComponentsLayout.setHorizontalGroup(
            dlgOtherOldComponentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 497, Short.MAX_VALUE)
            .add(dlgOtherOldComponentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(dlgOtherOldComponentsLayout.createSequentialGroup()
                    .add(16, 16, 16)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(16, 16, 16)))
        );
        dlgOtherOldComponentsLayout.setVerticalGroup(
            dlgOtherOldComponentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 398, Short.MAX_VALUE)
            .add(dlgOtherOldComponentsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(dlgOtherOldComponentsLayout.createSequentialGroup()
                    .add(16, 16, 16)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(252, Short.MAX_VALUE)))
        );

        dlgFilters.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dlgFilters.setTitle("Filter Displayed Premodels");
        dlgFilters.setAlwaysOnTop(true);
        dlgFilters.setResizable(false);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        jLabel26.setText("Hide nodes marked as:");

        cmbxNodeFilter.setEditable(true);
        cmbxNodeFilter.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "hide", "Hist" }));
        cmbxNodeFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxNodeFilterActionPerformed(evt);
            }
        });

        ckbxFilterClosedTableaux.setText("Hide closed premodels");
        ckbxFilterClosedTableaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ckbxFilterClosedTableauxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(ckbxFilterClosedTableaux)
                    .add(jLabel26, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbxNodeFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(ckbxFilterClosedTableaux)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel26)
                    .add(cmbxNodeFilter, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout dlgFiltersLayout = new org.jdesktop.layout.GroupLayout(dlgFilters.getContentPane());
        dlgFilters.getContentPane().setLayout(dlgFiltersLayout);
        dlgFiltersLayout.setHorizontalGroup(
            dlgFiltersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgFiltersLayout.setVerticalGroup(
            dlgFiltersLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        spltMain.setDividerLocation(210);
        spltMain.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                spltMainComponentResized(evt);
            }
        });

        spltLeft.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlControls.setBorder(javax.swing.BorderFactory.createTitledBorder("Controls"));

        lblAtRule.setText("-");

        btnPauseResume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/pause.png"))); // NOI18N
        btnPauseResume.setText("Pause");
        btnPauseResume.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnPauseResume.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPauseResumeActionPerformed(evt);
            }
        });

        btnStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/stop.png"))); // NOI18N
        btnStop.setText("Stop");
        btnStop.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        btnNextStep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/next-step.png"))); // NOI18N
        btnNextStep.setText("Next");
        btnNextStep.setMargin(new java.awt.Insets(2, 4, 2, 4));
        btnNextStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextStepActionPerformed(evt);
            }
        });

        jLabel6.setText("Next Rule:");

        org.jdesktop.layout.GroupLayout pnlControlsLayout = new org.jdesktop.layout.GroupLayout(pnlControls);
        pnlControls.setLayout(pnlControlsLayout);
        pnlControlsLayout.setHorizontalGroup(
            pnlControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlControlsLayout.createSequentialGroup()
                .add(pnlControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlControlsLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(pnlControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlControlsLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(lblAtRule))
                            .add(jLabel6)))
                    .add(pnlControlsLayout.createSequentialGroup()
                        .add(btnNextStep, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(btnPauseResume, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 56, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(btnStop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 61, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlControlsLayout.setVerticalGroup(
            pnlControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlControlsLayout.createSequentialGroup()
                .add(pnlControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(btnNextStep)
                    .add(btnPauseResume)
                    .add(btnStop))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jLabel6)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblAtRule)
                .addContainerGap())
        );

        spltLeft.setTopComponent(pnlControls);

        spltLeftBottom.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        spltLeftBottom.setResizeWeight(0.7);

        pnlTabList.setBorder(javax.swing.BorderFactory.createTitledBorder("Premodels List"));

        lstTableaux.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstTableaux.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstTableauxMouseClicked(evt);
            }
        });
        lstTableaux.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstTableauxValueChanged(evt);
            }
        });
        lstTableaux.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lstTableauxKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(lstTableaux);

        org.jdesktop.layout.GroupLayout pnlTabListLayout = new org.jdesktop.layout.GroupLayout(pnlTabList);
        pnlTabList.setLayout(pnlTabListLayout);
        pnlTabListLayout.setHorizontalGroup(
            pnlTabListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 198, Short.MAX_VALUE)
        );
        pnlTabListLayout.setVerticalGroup(
            pnlTabListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE)
        );

        spltLeftBottom.setTopComponent(pnlTabList);

        pnlGlass.setBorder(javax.swing.BorderFactory.createTitledBorder("Galss"));

        org.jdesktop.layout.GroupLayout pnlGlassLayout = new org.jdesktop.layout.GroupLayout(pnlGlass);
        pnlGlass.setLayout(pnlGlassLayout);
        pnlGlassLayout.setHorizontalGroup(
            pnlGlassLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 198, Short.MAX_VALUE)
        );
        pnlGlassLayout.setVerticalGroup(
            pnlGlassLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 0, Short.MAX_VALUE)
        );

        spltLeftBottom.setRightComponent(pnlGlass);

        spltLeft.setRightComponent(spltLeftBottom);

        spltMain.setLeftComponent(spltLeft);

        pnlCyBiModal.setBorder(javax.swing.BorderFactory.createTitledBorder("Premodels Views"));

        org.jdesktop.layout.GroupLayout pnlCyBiModalLayout = new org.jdesktop.layout.GroupLayout(pnlCyBiModal);
        pnlCyBiModal.setLayout(pnlCyBiModalLayout);
        pnlCyBiModalLayout.setHorizontalGroup(
            pnlCyBiModalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 459, Short.MAX_VALUE)
        );
        pnlCyBiModalLayout.setVerticalGroup(
            pnlCyBiModalLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 623, Short.MAX_VALUE)
        );

        spltMain.setRightComponent(pnlCyBiModal);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spltMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 700, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(spltMain, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    private void tglbtnCascadeTilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglbtnCascadeTilesActionPerformed
        if (tglbtnCascadeTiles.getText().equals("Cascade")) {
            cascadeTableauxFrames();
            tglbtnCascadeTiles.setText("Tile");
        } else {
            tileWindows();
            tglbtnCascadeTiles.setText("Cascade");
        }
}//GEN-LAST:event_tglbtnCascadeTilesActionPerformed

    private void lstTableauxValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstTableauxValueChanged
        lastSelectedIndices = lstTableaux.getSelectedIndices();
        lastSelectedTabs = new ArrayList<String>();
        for (Object value : lstTableaux.getSelectedValues()) {
            lastSelectedTabs.add((String) value);
        }
//        System.out.print("Last chosen tableaux: [");
//        for (int index : lastSelectedIndices) {
//            System.out.print(index + ", ");
//        }
//        System.out.println("]");
//        System.out.print("Last chosen tableaux: [");
//        for (String tabname : lastSelectedTabs) {
//            System.out.print(tabname + ", ");
//        }
//        System.out.println("]");
    }//GEN-LAST:event_lstTableauxValueChanged

    private void lstTableauxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstTableauxMouseClicked
        if (selectionMode == 0) {
            displaySelectedTableau();
        }
    }//GEN-LAST:event_lstTableauxMouseClicked

    public void resetSelectionMode() {
        rdbtnDisplayOne.setSelected(true);
        //To do list to be updated when displayModeSelection is updated too
        btnDisplay.setEnabled(false);
        selectionMode = 0;
        lstTableaux.setEnabled(true);
        lstTableaux.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstTableaux.clearSelection();
    //Shouldn't be initialized!!
    //initializeView();
    }
    public static String SINGLE_DISPLAY_MODE = "Only selected one";
    public static String MULTIPLE_DISPLAY_MODE = "Many setected ones";
    public static String ALL_DISPLAY_MODE = "All premodels";

    public void displayModeChanged(String Mode) {
        if (Mode.equals(SINGLE_DISPLAY_MODE)) {
            btnDisplay.setEnabled(false);
            selectionMode = 0;
            lstTableaux.setEnabled(true);
            lstTableaux.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            lstTableaux.clearSelection();
            fillTabListAndDisplayFirst();
        }
        if (Mode.equals(MULTIPLE_DISPLAY_MODE)) {
            btnDisplay.setEnabled(true);
            selectionMode = 1;
            lstTableaux.setEnabled(true);
            lstTableaux.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            lstTableaux.clearSelection();
        }
        if (Mode.equals(ALL_DISPLAY_MODE)) {
            btnDisplay.setEnabled(false);
            selectionMode = 2;
            lstTableaux.setEnabled(false);
            lstTableaux.clearSelection();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    diplayAllTableaux();
                }
            });
        }
    }

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        mainFrame.getEngine().stopWork();
        //No need to test if paused at a specific place or StepByStep
        //Whatever is the type of "pause", 
        //just resumeWork by puting "shoulPause" to false
        //and awake the engine... it will stop automatically after that..
        if (mainFrame.getEngine().shouldPause()) {
            makeResume();//will make it correctly with changing Pause/Resume button text etc...
        }
        if (mainFrame.getEngine().isRunningBySteps()) {
            mainFrame.getEngine().resumeWorkToNextStep();
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private void btnPauseResumeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPauseResumeActionPerformed
        if (btnPauseResume.getText().equals("Pause")) {
            makePause();
        } else {
            makeResume();
        }
    }//GEN-LAST:event_btnPauseResumeActionPerformed

    private void btnNextStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextStepActionPerformed
        if (mainFrame.getEngine().isRunningBySteps()) {
            mainFrame.getEngine().resumeWorkToNextStep();
        }
    }//GEN-LAST:event_btnNextStepActionPerformed

    public void hideRunInfo() {
        dlgRunInfo.dispose();
    }

    public void showRunInfo() {
        dlgRunInfo.pack();
        Point p = mainFrame.getLocation();
        p.translate(mainFrame.getWidth() / 2 - dlgRunInfo.getWidth() / 2, mainFrame.getHeight() / 2 - dlgRunInfo.getHeight() / 2);
        dlgRunInfo.setLocation(p);
        dlgRunInfo.setVisible(true);
    }

    public void hidePremodelFilters() {
        dlgFilters.dispose();
    }

    public void showPremodelFilters() {
        dlgFilters.pack();
        Point p = mainFrame.getLocation();
        p.translate(mainFrame.getWidth() / 2 - dlgFilters.getWidth() / 2, mainFrame.getHeight() / 2 - dlgFilters.getHeight() / 2);
        dlgFilters.setLocation(p);
        dlgFilters.setVisible(true);
    }

    public void hidePremodelsEditor() {
        dlgPremodelsEditor.dispose();
    }

    public void showPremodelEditor() {
        if (mainFrame.getEngine() == null ||
                mainFrame.getEngine().getCurrentWallet() == null ||
                mainFrame.getEngine().getCurrentWallet().getGraphes().size() == 0) {
            DialogsFactory.PremodelEditingActionWarning(this.mainFrame, "There is no premodel to be edited..");
            return;
        }
//        fillNodesListsInEditor();
        dlgPremodelsEditor.pack();
        Point p = mainFrame.getLocation();
        p.translate(mainFrame.getWidth() / 2 - dlgPremodelsEditor.getWidth() / 2, mainFrame.getHeight() / 2 - dlgPremodelsEditor.getHeight() / 2);
        dlgPremodelsEditor.setLocation(p);
        dlgPremodelsEditor.setVisible(true);
    }

    private void fillNodesListsInEditor() {
        String selectedTableauName = (String) cmbxTableauxList.getSelectedItem();
        Tableau selectedTableau = (Tableau) mainFrame.getEngine().getCurrentWallet().getGraph(selectedTableauName);
        javax.swing.DefaultComboBoxModel comboModelFrom = new javax.swing.DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel comboModelTo = new javax.swing.DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel comboModelAdd = new javax.swing.DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel comboModelMark = new javax.swing.DefaultComboBoxModel();
        javax.swing.DefaultComboBoxModel comboModelFormulaMark = new javax.swing.DefaultComboBoxModel();
        if (selectedTableau != null) {
            for (int i = 0; i < selectedTableau.getNodes().size(); i++) {
                String nodeName = selectedTableau.getNodes().get(i).getName();
                comboModelFrom.addElement(nodeName);
                comboModelTo.addElement(nodeName);
                comboModelAdd.addElement(nodeName);
                comboModelMark.addElement(nodeName);
                comboModelFormulaMark.addElement(nodeName);
            }
        } else {
            String msg = "Error with '" + selectedTableauName + "'";
            comboModelFrom.addElement(msg);
            comboModelTo.addElement(msg);
            comboModelAdd.addElement(msg);
            comboModelMark.addElement(msg);
            comboModelFormulaMark.addElement(msg);
        }
        int lastFrom = cmbxNodeFrom.getSelectedIndex();
        int lastTo = cmbxNodeTo.getSelectedIndex();
        int lastAdd = cmbxNodeAdd.getSelectedIndex();
        int lastNodeMark = cmbxNodeMark.getSelectedIndex();
        int lastFromulaMarkNode = cmbxFormulaMarkNode.getSelectedIndex();
        cmbxNodeFrom.setModel(comboModelFrom);
        cmbxNodeTo.setModel(comboModelTo);
        cmbxNodeAdd.setModel(comboModelAdd);
        cmbxNodeMark.setModel(comboModelMark);
        cmbxFormulaMarkNode.setModel(comboModelFormulaMark);
        if (lastFrom < cmbxNodeFrom.getItemCount()) {
            cmbxNodeFrom.setSelectedIndex(lastFrom);
        } else if(cmbxNodeFrom.getItemCount()>0){
            cmbxNodeFrom.setSelectedIndex(0);
        }else{
            //nothing?
        }
        if (lastTo < cmbxNodeTo.getItemCount()) {
            cmbxNodeTo.setSelectedIndex(lastTo);
        } else if(cmbxNodeTo.getItemCount()>0){
            cmbxNodeTo.setSelectedIndex(0);
        }else{
            //
        }
        if (lastAdd < cmbxNodeAdd.getItemCount()) {
            cmbxNodeAdd.setSelectedIndex(lastAdd);
        } else {
            cmbxNodeAdd.setSelectedIndex(0);
        }
        if (lastNodeMark < cmbxNodeMark.getItemCount()) {
            cmbxNodeMark.setSelectedIndex(lastNodeMark);
        } else {
            cmbxNodeMark.setSelectedIndex(0);
        }
        if (lastFromulaMarkNode < cmbxFormulaMarkNode.getItemCount()) {
            cmbxFormulaMarkNode.setSelectedIndex(lastFromulaMarkNode);
        } else {
            cmbxFormulaMarkNode.setSelectedIndex(0);
        }
        fillFormulasToBeMarked();
    }

    private void fillTableauxInEditor() {
        int lastSelected = cmbxTableauxList.getSelectedIndex();
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        for (int i = 0; i < mainFrame.getEngine().getCurrentWallet().getGraphes().size(); i++) {
            comboModel.addElement(mainFrame.getEngine().getCurrentWallet().getGraphes().get(i).getName());
        }
        cmbxTableauxList.setModel(comboModel);
        if(cmbxTableauxList.getItemCount() == 0){
            //nothing to do
        }else if (lastSelected < cmbxTableauxList.getItemCount()) {
            cmbxTableauxList.setSelectedIndex(lastSelected);
        } else {
            cmbxTableauxList.setSelectedIndex(0);
        }
        fillNodesListsInEditor();
    }

    private void fillFormulasToBeMarked() {
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        String selectedTableauName = (String) cmbxTableauxList.getSelectedItem();
        Tableau selectedTableau = (Tableau) mainFrame.getEngine().getCurrentWallet().getGraph(selectedTableauName);
        if (selectedTableau == null) {
            comboModel.addElement("<No Formulas in this node>");
        } else {
            String formulaMarkNodeName = (String) cmbxFormulaMarkNode.getSelectedItem();
            TableauNode formulaMarkNode = (TableauNode) selectedTableau.getNode(formulaMarkNodeName);
            if (formulaMarkNode == null) {
                comboModel.addElement("<No Formulas in this node>");
            } else {
                if (formulaMarkNode.getMarkedExpressions().size() == 0) {
                    comboModel.addElement("<No Formulas in this node>");
                } else {
                    for (MarkedExpression formula : formulaMarkNode.getMarkedExpressions()) {
                        comboModel.addElement(formula.toString());
                    }
                }
            }
        }
        cmbxFormulaToMark.setModel(comboModel);
    }

    private void clearEditingActionSelection() {
        rdbtnCreate.setSelected(false);
        rdbtnLink.setSelected(false);
        rdbtnAdd.setSelected(false);
    }

    private void dlgPremodelsEditorWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgPremodelsEditorWindowClosing
        hidePremodelsEditor();
}//GEN-LAST:event_dlgPremodelsEditorWindowClosing

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        hidePremodelsEditor();
}//GEN-LAST:event_btnCancelActionPerformed

    private void txfNewNodeIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfNewNodeIdActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfNewNodeIdActionPerformed

    private void txfRelationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfRelationActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfRelationActionPerformed

    private void txfFormulaAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfFormulaAddActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfFormulaAddActionPerformed

    private void TableauEditingActionChooser(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TableauEditingActionChooser
        if (evt.getActionCommand().equals(rdbtnCreate.getActionCommand())) {
        } else if (evt.getActionCommand().equals(rdbtnLink.getActionCommand())) {
        } else if (evt.getActionCommand().equals(rdbtnAdd.getActionCommand())) {
        }
    }//GEN-LAST:event_TableauEditingActionChooser

    private void cmbxTableauxListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxTableauxListActionPerformed
        fillNodesListsInEditor();
    }//GEN-LAST:event_cmbxTableauxListActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        String selectedTableauName = (String) cmbxTableauxList.getSelectedItem();
        Tableau selectedTableau = (Tableau) mainFrame.getEngine().getCurrentWallet().getGraph(selectedTableauName);
        if (selectedTableau == null) {
            System.out.println("Verify the chosen tableau.. It is null!");
            DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "Verify the chosen tableau.. It is null!");
            return;
        } else {
            if (rdbtnCreate.isSelected()) {
                String newNodeName = txfNewNodeId.getText();
                if (newNodeName.equals("")) {
                    // CREATE WITH THE DEFAULT NAME
                    TableauNode newNode = new TableauNode();
                    selectedTableau.add(newNode);
                } else {
                    if (selectedTableau.getNode(newNodeName) != null) {
                        System.out.println("The given new node name '" + newNodeName + "' is already used\n" +
                                "to designate another existing node in tableau '" + selectedTableauName + "'.\n" +
                                "Please choose a diffrent name.");
                        DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The given new node name '" + newNodeName + "' is already used\n" +
                                " to designate another existing node in tableau '" + selectedTableauName + "'.\n" +
                                "Please choose a diffrent name.");
                        return;
                    } else {
                        // CREATE WITH THE SPECIFIED NAME
                        TableauNode newNode = new TableauNode(newNodeName);
                        selectedTableau.add(newNode);
                    }
                }
            } else if (rdbtnLink.isSelected()) {
                String nodeFromId = (String) cmbxNodeFrom.getSelectedItem();
                String nodeToId = (String) cmbxNodeTo.getSelectedItem();
                String relationLabel = txfRelation.getText();
                TableauNode nodeFrom = (TableauNode) selectedTableau.getNode(nodeFromId);
                TableauNode nodeTo = (TableauNode) selectedTableau.getNode(nodeToId);
                if (nodeFrom == null) {
                    System.out.println("The given (From) parent node '" + nodeFromId + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The given (From) parent node '" + nodeFromId + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    return;
                } else if (nodeTo == null) {
                    System.out.println("The given (To) child node '" + nodeToId + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The given (To) child node '" + nodeToId + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    return;
                } else {
                    OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(mainFrame.getEngine().getLogic());
                    oldiesTokenizer.initializeTokenizerAndProps();
                    Expression relation = null;
                    try {
                        relation = oldiesTokenizer.parseExpression(relationLabel);
                        oldiesTokenizer.verifyCodeEnd();
                    } catch (lotrec.parser.exceptions.ParseException ex) {
                        DialogsFactory.syntaxErrorMessage(dlgPremodelsEditor, "The given relation code raised the following parser exception:\n\n" +
                                ex.getMessage());
                        return;
                    }
                    //LINK ACTION
                    /*   
                    If the link is already existant we do nothing
                     */
                    for (Enumeration enumr = nodeFrom.getNextEdgesEnum(); enumr.hasMoreElements();) {
                        TableauEdge edge = (TableauEdge) enumr.nextElement();
                        if (edge.getEndNode().equals(nodeTo)) {
                            if (edge.getRelation().equals(relation)) {
                                System.out.println("The relation specified already exists between the two given nodes");
                                DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The relation specified already exists between the two given nodes");
                                return;
                            }
                        }
                    }
                    // WE SHOULD TEST IF THE RELATION IS AMONG CONSTANTS..
                    TableauEdge newEdge = new TableauEdge(nodeFrom, nodeTo, relation);
                    nodeFrom.link(newEdge);
                }
            } else if (rdbtnAdd.isSelected()) {
                String nodeAddId = (String) cmbxNodeAdd.getSelectedItem();
                String formulaAddCode = txfFormulaAdd.getText();
                TableauNode nodeAdd = (TableauNode) selectedTableau.getNode(nodeAddId);
                if (nodeAdd == null) {
                    System.out.println("The given (add to it) node '" + nodeAddId + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The given (add to it) node '" + nodeAddId + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    return;
                } else {
                    OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(mainFrame.getEngine().getLogic());
                    oldiesTokenizer.initializeTokenizerAndProps();
                    Expression formulaAdd = null;
                    try {
                        formulaAdd = oldiesTokenizer.parseExpression(formulaAddCode);
                        oldiesTokenizer.verifyCodeEnd();
                    } catch (lotrec.parser.exceptions.ParseException ex) {
                        DialogsFactory.syntaxErrorMessage(dlgPremodelsEditor, "The given formula code raised the following parser exception:\n\n" +
                                ex.getMessage());
                        return;
                    }
                    //ADD EXPRESSION ACTION
                    if (!nodeAdd.contains(formulaAdd)) {
                        nodeAdd.add(new MarkedExpression(formulaAdd));
                    }
                }
            } else if (rdbtnMark.isSelected()) {
                //MARK NODE                
                String nodeMarkName = (String) cmbxNodeMark.getSelectedItem();
                String mark = txfMark.getText();
                TableauNode nodeToBeMarked = (TableauNode) selectedTableau.getNode(nodeMarkName);
                if (nodeToBeMarked == null) {
                    System.out.println("The node to be marked '" + nodeToBeMarked + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The node to be marked '" + nodeToBeMarked + "' could not be found\n" +
                            "in the specified tableau '" + selectedTableauName + "'");
                    return;
                }
                if (mark.equals("")) {
                    System.out.println("Please give a mark to add it to node '" + nodeToBeMarked + "'");
                    DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "Please give a mark to add it to node '" + nodeToBeMarked + "'");
                    return;
                }
                //We should pass by Marked class to enable the duplication of the marks correctly etc...
                Marked m = (Marked) nodeToBeMarked;
                if (!m.isMarked(mark)) {
                    m.mark(mark);
                    //the object calling sendEvent should be the nodeToBeMarked
                    //the marked paramerter of MarEvent constructor should be a Marked object                    
                    nodeToBeMarked.sendEvent(new MarkEvent(m, MarkEvent.MARK, mark));
                } else {
                    System.out.println("The node '" + nodeToBeMarked + "' is already marked by the given mark" + mark + "'");
                    DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The node '" + nodeToBeMarked + "' is already marked by the given mark" + mark + "'");
                    return;
                }
            } else if (rdbtnUnMark.isSelected()) {
                //UNMARK NODE
                String nodeMarkName = (String) cmbxNodeMark.getSelectedItem();
                String mark = txfMark.getText();
                TableauNode nodeToBeMarked = (TableauNode) selectedTableau.getNode(nodeMarkName);
                Marked m = (Marked) nodeToBeMarked;
                if (m.isMarked(mark)) {
                    m.unmark(mark);
                    nodeToBeMarked.sendEvent(new MarkEvent(m, MarkEvent.UNMARK, mark));
                } else {
                    System.out.println("The node '" + nodeToBeMarked + "' is not marked by the given mark" + mark + "'");
                    DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "The node '" + nodeToBeMarked + "' is not marked by the given mark" + mark + "'");
                    return;
                }
            } else if (rdbtnMarkFormula.isSelected()) {
                //MARK Formula
                String formulaMarkNodeName = (String) cmbxFormulaMarkNode.getSelectedItem();
                TableauNode formulaMarkNode = (TableauNode) selectedTableau.getNode(formulaMarkNodeName);
                if (formulaMarkNode != null && cmbxFormulaToMark.getSelectedIndex() >= 0 && formulaMarkNode.getMarkedExpressions().size() > 0) {
                    MarkedExpression formula = formulaMarkNode.getMarkedExpressions().get(cmbxFormulaToMark.getSelectedIndex());
                    String formulaMark = txfFormulaMark.getText();
                    if (!formula.isMarked(formulaMark)) {
                        formula.mark(formulaMark);
                        formulaMarkNode.sendEvent(new MarkExpressionEvent(
                                formulaMarkNode, formula, MarkExpressionEvent.MARK_EX, formulaMark));
                    }
                }
            } else if (rdbtnUnMarkFormula.isSelected()) {
                //UNMARK FOMULA                
                String formulaMarkNodeName = (String) cmbxFormulaMarkNode.getSelectedItem();
                TableauNode formulaMarkNode = (TableauNode) selectedTableau.getNode(formulaMarkNodeName);
                if (formulaMarkNode != null && cmbxFormulaToMark.getSelectedIndex() >= 0 && formulaMarkNode.getMarkedExpressions().size() > 0) {
                    MarkedExpression formula = formulaMarkNode.getMarkedExpressions().get(cmbxFormulaToMark.getSelectedIndex());
                    String formulaMark = txfFormulaMark.getText();
                    if (formula.isMarked(formulaMark)) {
                        formula.unmark(formulaMark);
                        formulaMarkNode.sendEvent(new MarkExpressionEvent(
                                formulaMarkNode, formula, MarkExpressionEvent.UNMARK_EX, formulaMark));
                    }
                }
            } else if (rdbtnStop.isSelected()) {
                selectedTableau.getStrategy().getEngine().stopTableau(selectedTableau);
            } else {
                System.out.println("Choose an editing action to apply..");
                DialogsFactory.PremodelEditingActionWarning(dlgPremodelsEditor, "Make sure to choose an editing action to apply it..");
                return;
            }

            displayLastChosenOnes();

            fillNodesListsInEditor();

            clearEditingActionSelection();
        }
    }//GEN-LAST:event_btnApplyActionPerformed

    private void txfMarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfMarkActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfMarkActionPerformed

    private void txfFormulaMarkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfFormulaMarkActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfFormulaMarkActionPerformed

    private void cmbxFormulaMarkNodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxFormulaMarkNodeActionPerformed
        fillFormulasToBeMarked();
    }//GEN-LAST:event_cmbxFormulaMarkNodeActionPerformed

    private void cmbxNodeFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxNodeFilterActionPerformed
        fillTabListAndDisplayLastChosenOnes();
    }//GEN-LAST:event_cmbxNodeFilterActionPerformed

    private void spltMainComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_spltMainComponentResized
//        double loc = spltMain.getBounds().getHeight() * 0.8;
//        spltLeft.setDividerLocation(0.8);
//        spltRight.setDividerLocation(0.8);
//        spltLeftBottom.setDividerLocation(0.7);
    }//GEN-LAST:event_spltMainComponentResized

    private void dlgRunInfoWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgRunInfoWindowClosing
        hideRunInfo();
    }//GEN-LAST:event_dlgRunInfoWindowClosing

    private void lstTableauxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lstTableauxKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    displaySelectedTableaux();
                }
            });
        }
    }//GEN-LAST:event_lstTableauxKeyPressed

    private void ckbxFilterClosedTableauxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ckbxFilterClosedTableauxActionPerformed
        fillTabListAndDisplayFirst();
}//GEN-LAST:event_ckbxFilterClosedTableauxActionPerformed

    public void makePause() {
        btnPauseResume.setText("Resume");
        btnPauseResume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/play.png")));
        disableControlsButtons();//will be re-enabled inside the pause treatement!!
        mainFrame.getEngine().pauseWork();
    }

    public void makeResume() {
        btnPauseResume.setText("Pause");
        btnPauseResume.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/pause.png")));
        disableControlsButtons();//will be re-enabled inside the resume treatement!!
        mainFrame.getEngine().resumeWork();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JInternalFrame CytoscapeFrame;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDisplay;
    private javax.swing.JButton btnNextStep;
    private javax.swing.JButton btnPauseResume;
    private javax.swing.JButton btnStop;
    private javax.swing.ButtonGroup btngrpActions;
    private javax.swing.ButtonGroup btngrpDisplayMode;
    private javax.swing.JCheckBox ckbxFilterClosedTableaux;
    private javax.swing.JComboBox cmbxFormulaMarkNode;
    private javax.swing.JComboBox cmbxFormulaToMark;
    private javax.swing.JComboBox cmbxNodeAdd;
    public static javax.swing.JComboBox cmbxNodeFilter;
    private javax.swing.JComboBox cmbxNodeFrom;
    private javax.swing.JComboBox cmbxNodeMark;
    private javax.swing.JComboBox cmbxNodeTo;
    private javax.swing.JComboBox cmbxTableauxList;
    private javax.swing.JDialog dlgCyFrame;
    private javax.swing.JDialog dlgFilters;
    private javax.swing.JDialog dlgOtherOldComponents;
    private javax.swing.JDialog dlgPremodelsEditor;
    private javax.swing.JDialog dlgRunInfo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAppliedRules;
    private javax.swing.JLabel lblAtRule;
    private javax.swing.JLabel lblElapsedTime;
    private javax.swing.JLabel lblEngineStatus;
    private javax.swing.JLabel lblLastAppliedRule;
    private javax.swing.JLabel lblOnTableauName;
    private javax.swing.JLabel lblPercentage;
    private javax.swing.JLabel lblTableauxCount;
    private javax.swing.JLabel lblTotalAppliedRules;
    private javax.swing.JList lstTableaux;
    private javax.swing.JPanel pnlControls;
    private javax.swing.JPanel pnlCyBiModal;
    private javax.swing.JPanel pnlGlass;
    private javax.swing.JPanel pnlTabList;
    private javax.swing.JRadioButton rdbtnAdd;
    private javax.swing.JRadioButton rdbtnCreate;
    private javax.swing.JRadioButton rdbtnDisplayAll;
    private javax.swing.JRadioButton rdbtnDisplayMultiple;
    private javax.swing.JRadioButton rdbtnDisplayOne;
    private javax.swing.JRadioButton rdbtnLink;
    private javax.swing.JRadioButton rdbtnMark;
    private javax.swing.JRadioButton rdbtnMarkFormula;
    private javax.swing.JRadioButton rdbtnStop;
    private javax.swing.JRadioButton rdbtnUnMark;
    private javax.swing.JRadioButton rdbtnUnMarkFormula;
    private javax.swing.JSplitPane spltLeft;
    private javax.swing.JSplitPane spltLeftBottom;
    private javax.swing.JSplitPane spltMain;
    private javax.swing.JToggleButton tglbtnCascadeTiles;
    private javax.swing.JToggleButton tglbtnFullScreen;
    private javax.swing.JToggleButton tglbtnShowHideTabEditor;
    private javax.swing.JToggleButton tglbtnShowHideTabList;
    private javax.swing.JToolBar tolbrCytoToolBar;
    private javax.swing.JTextField txfFormulaAdd;
    private javax.swing.JTextField txfFormulaMark;
    private javax.swing.JTextField txfMark;
    private javax.swing.JTextField txfNewNodeId;
    private javax.swing.JTextField txfRelation;
    // End of variables declaration//GEN-END:variables

    public void refreshToggleButtons() {
        this.resource = java.util.ResourceBundle.getBundle("lotrec.resources.TableauxEnginePanel", ResourcesProvider.getCurrentLocale());
        if (this.tglbtnFullScreen.isSelected()) {
            this.tglbtnFullScreen.setText(resource.getString("TableauxPanel.ExitFullScreen"));
        } else {
            this.tglbtnFullScreen.setText(resource.getString("TableauxPanel.FullScreen"));
        }

        if (tglbtnShowHideTabList.isSelected()) {
            tglbtnShowHideTabList.setText(resource.getString("TableauxPanel.HideTabList"));
        } else {
            tglbtnShowHideTabList.setText(resource.getString("TableauxPanel.ShowTabList"));
        }

        if (tglbtnShowHideTabEditor.isSelected()) {
            tglbtnShowHideTabEditor.setText(resource.getString("TableauxPanel.HideTabEditor"));
        } else {
            tglbtnShowHideTabEditor.setText(resource.getString("TableauxPanel.ShowTabEditor"));
        }

    }

    public void refresh() {
        this.resource = java.util.ResourceBundle.getBundle("lotrec.resources.TableauxEnginePanel", ResourcesProvider.getCurrentLocale());
        refreshToggleButtons();
    }

    public javax.swing.JInternalFrame getCytoscapeFrame() {
        return CytoscapeFrame;
    }

    public javax.swing.JList getTableauxList() {
        return lstTableaux;
    }

    public javax.swing.JPanel getControlsPanel() {
        return pnlControls;
    }

    public void displayLastAppliedRule(String ruleName, String onTableauName) {
        lblLastAppliedRule.setText(ruleName);
        lblOnTableauName.setText(onTableauName);
    }

    public void displayPausedAtRule(String ruleName) {
        lblAtRule.setText(ruleName);
    }
}
