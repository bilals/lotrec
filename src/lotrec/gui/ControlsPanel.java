/*
 * EngineSettingsPanel.java
 *
 * Created on 1 novembre 2007, 15:59
 */
package lotrec.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.engine.Engine;
import lotrec.parser.exceptions.ParseException;
import lotrec.parser.OldiesTokenizer;
import lotrec.process.AbstractWorker;
import lotrec.process.EventMachine;
import lotrec.process.Routine;
import lotrec.process.Strategy;
import lotrec.resources.ResourcesProvider;

/**
 *
 * @author  said
 */
public class ControlsPanel extends javax.swing.JPanel {

    private MainFrame mainFrame;
    private ResourceBundle resource;
    private List<JCheckBox> stepsCheckBoxes;
    private ArrayList rulesBreakPoints;

    /** Creates new form EngineSettingsPanel */
    public ControlsPanel() {
        this(null);
    }

    public ControlsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        txaFormulaCode.setFont(new java.awt.Font("Tahoma", 0, 11));
        refreshFormulaeList();
        stepsCheckBoxes = new ArrayList();
        initSatCheckOptions();
    }

// Should be called when changing the rules (especially the rules called by the default stratgey)
    public void refreshSteps() {
        Logic selectedLogic = null;
        if (mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
            selectedLogic = mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab().getLogic();
        }
        if (selectedLogic == null) {
            treStepsTree = new JTree(new DefaultMutableTreeNode("No Strategy"));
        } else {
//            String strName = (String) cmbxCompleteStrategies.getSelectedItem();
            Strategy str = selectedLogic.getStrategy(selectedLogic.getMainStrategyName());
            MyMutableTreeNode root = new MyMutableTreeNode(str.getWorkerName());
            stepsCheckBoxes = new ArrayList();
            rulesBreakPoints = new ArrayList();
//            getMainFrame().getTableauxEnginePanel().getEngine().getRulesBreakPoints().clear();
            fillWorkerChildrenInItsNode(str, root);
//            for (AbstractWorker w : ((Routine) str).getWorkers()) {
//                fillStepsWith(w, root);
//            }
//            System.out.println("------------stepsCheckBoxes-------------");
            for (int i = 0; i < stepsCheckBoxes.size(); i++) {
//                System.out.println(stepsCheckBoxes.get(i).getText() + " at pos " + i);
            }
//            System.out.println("------------stepsCheckBoxes-------------");
            treStepsTree = new JTree(root);
            int i = 0;
            while (i < treStepsTree.getRowCount()) {
                treStepsTree.expandRow(i);
                i++;
            }
            MyRenderer renderer = new MyRenderer();
            MyCellEditor myEditor = new MyCellEditor();
            treStepsTree.setEditable(true);
            treStepsTree.setCellRenderer(renderer);
            treStepsTree.setCellEditor(myEditor);
        }
        scrlStepsTree.setViewportView(treStepsTree);
    }

    public void displayStepsTree(Strategy str) {
        MyMutableTreeNode root = new MyMutableTreeNode(str.getWorkerName());
        stepsCheckBoxes = new ArrayList();
        rulesBreakPoints = new ArrayList();
//            getMainFrame().getTableauxEnginePanel().getEngine().getRulesBreakPoints().clear();
        fillWorkerChildrenInItsNode(str, root);
//            for (AbstractWorker w : ((Routine) str).getWorkers()) {
//                fillStepsWith(w, root);
//            }
//        System.out.println("------------stepsCheckBoxes-------------");
        for (int i = 0; i < stepsCheckBoxes.size(); i++) {
//            System.out.println(stepsCheckBoxes.get(i).getText() + " at pos " + i);
        }
//        System.out.println("------------stepsCheckBoxes-------------");
        treStepsTree = new JTree(root);
        int i = 0;
        while (i < treStepsTree.getRowCount()) {
            treStepsTree.expandRow(i);
            i++;
        }
        MyRenderer renderer = new MyRenderer();
        MyCellEditor myEditor = new MyCellEditor();
        treStepsTree.setEditable(true);
        treStepsTree.setCellRenderer(renderer);
        treStepsTree.setCellEditor(myEditor);
        scrlStepsTree.setViewportView(treStepsTree);
    }

    private void fillWorkerChildrenInItsNode(AbstractWorker parentWorker, MyMutableTreeNode parentNode) {
        for (AbstractWorker subWorker : ((Routine) parentWorker).getWorkers()) {
            if (subWorker instanceof Routine) {
                MyMutableTreeNode newParentNode = new MyMutableTreeNode(subWorker.getWorkerName());
                parentNode.add(newParentNode);
                fillWorkerChildrenInItsNode(subWorker, newParentNode);
            } else {
                MyMutableTreeNode childNode = new MyMutableTreeNode(subWorker.getWorkerName());
                parentNode.add(childNode);
                ((EventMachine) subWorker).setLevel(stepsCheckBoxes.size());
                stepsCheckBoxes.add(childNode.getCheckBox());
                childNode.getCheckBox().addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        JCheckBox source = ((JCheckBox) e.getSource());
                        if (source.isSelected()) {
                            rulesBreakPoints.add(stepsCheckBoxes.indexOf(source));
//                    rulesBreakPoints.add(source.getText());
                        } else {
                            rulesBreakPoints.remove(rulesBreakPoints.indexOf(stepsCheckBoxes.indexOf(source)));
//                    rulesBreakPoints.remove(source.getText());
                        }
//                        System.out.println(source.getText() + " at pos " + stepsCheckBoxes.indexOf(source) + " selected-value = " + source.isSelected());
//                        System.out.println(rulesBreakPoints);
//                        System.out.println(rulesBreakPoints.indexOf(1));
                    }
                });
            }
        }
    }

//    public void fillStepsWith(AbstractWorker aw, DefaultMutableTreeNode parentNode) {
//        if (aw instanceof Routine) {
//            MyMutableTreeNode n = new MyMutableTreeNode(aw.getWorkerName());
//            parentNode.add(n);
//            for (AbstractWorker w : ((Routine) aw).getWorkers()) {
//                fillStepsWith(w, n);
//            }
//        } else {
//            MyMutableTreeNode n = new MyMutableTreeNode(aw.getWorkerName());
//            parentNode.add(n);
//            ((EventMachine) aw).setLevel(stepsCheckBoxes.size());
//            stepsCheckBoxes.add(n.getCheckBox());
//            n.getCheckBox().addItemListener(new ItemListener() {
//
//                public void itemStateChanged(ItemEvent e) {
//                    JCheckBox source = ((JCheckBox) e.getSource());
//                    if (source.isSelected()) {
//                        rulesBreakPoints.add(stepsCheckBoxes.indexOf(source));
////                    rulesBreakPoints.add(source.getText());
//                    } else {
//                        rulesBreakPoints.remove(rulesBreakPoints.indexOf(stepsCheckBoxes.indexOf(source)));
////                    rulesBreakPoints.remove(source.getText());
//                    }
//                    System.out.println(source.getText() + " at pos " + stepsCheckBoxes.indexOf(source) + " selected-value = " + source.isSelected());
//                    System.out.println(rulesBreakPoints);
//                    System.out.println(rulesBreakPoints.indexOf(1));
//                }
//            });
//        }
//    }
    private void invertAllWorkersSelection() {
        MyMutableTreeNode root = (MyMutableTreeNode) treStepsTree.getModel().getRoot();
        invertLeafWorkersSelection(root);
    }

    private void invertLeafWorkersSelection(MyMutableTreeNode parent) {
        for (Enumeration enumr = parent.children(); enumr.hasMoreElements();) {
            MyMutableTreeNode child = (MyMutableTreeNode) enumr.nextElement();
            if (child.isLeaf()) {
                child.getCheckBox().setSelected(!child.getCheckBox().isSelected());
            } else {
                invertLeafWorkersSelection(child);
            }
        }
    }

    private void setAllWorkersSelected() {
        MyMutableTreeNode root = (MyMutableTreeNode) treStepsTree.getModel().getRoot();
        setLeafWorkersSelected(root);
    }

    private void setLeafWorkersSelected(MyMutableTreeNode parent) {
        for (Enumeration enumr = parent.children(); enumr.hasMoreElements();) {
            MyMutableTreeNode child = (MyMutableTreeNode) enumr.nextElement();
            if (child.isLeaf()) {
                child.getCheckBox().setSelected(true);
            } else {
                setLeafWorkersSelected(child);
            }
        }
    }

    private void displayNodesStates() {
        MyMutableTreeNode root = (MyMutableTreeNode) treStepsTree.getModel().getRoot();
        displayLeafChildrenStates(root);
    }

    private void displayLeafChildrenStates(MyMutableTreeNode parent) {
        for (Enumeration enumr = parent.children(); enumr.hasMoreElements();) {
            MyMutableTreeNode child = (MyMutableTreeNode) enumr.nextElement();
            if (child.isLeaf()) {
//                System.out.println("Rule " + child + //.getCheckBox().getText() or .toString()
//                        " is " + child.getCheckBox().isSelected());
            } else {
//                System.out.println("Routine " + child + " is to be treated");//child.toString()
                displayLeafChildrenStates(child);
            }
        }
    }

    // shoud be called when changing:
    // * the logic (DefTab) selection
    // * the connectors
    // * the testing formulae
    // 1- if it is != null it displays the testing formulae of the selected logic
    //     and select the last selected one, if available,
    //     or the first one, otherwise.
    // 2- else, it displqys nothing
    public void refreshFormulaeList() {
        Logic selectedLogic = null;
        if (mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
            selectedLogic = mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab().getLogic();
        }
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        int lastSelectedTFIndex = cmbxTestingFormulae.getSelectedIndex();
        if (selectedLogic == null) {
            cmbxTestingFormulae.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"- you should select a logic first -"}));
            cmbxTestingFormulae.setSelectedIndex(0);
        } else if (selectedLogic.getTestingFormulae().size() > 0) {
            ArrayList<String> list = new ArrayList();
            int[] similarsCount = new int[selectedLogic.getTestingFormulae().size()];
            for (int i = 0; i < selectedLogic.getTestingFormulae().size(); i++) {
                String forName = selectedLogic.getTestingFormulae().get(i).getDisplayName();
                if (list.contains(forName)) {
                    list.add(forName + "(" + ++similarsCount[list.indexOf(forName)] + ")");
                } else {
                    list.add(forName);
                }
            }
            for (int i = 0; i < selectedLogic.getTestingFormulae().size(); i++) {
                comboModel.addElement(list.get(i));
            }
            cmbxTestingFormulae.setModel(comboModel);
            if (lastSelectedTFIndex == -1) {
                cmbxTestingFormulae.setSelectedIndex(0);
            } else if (lastSelectedTFIndex > (comboModel.getSize() - 1)) {
                cmbxTestingFormulae.setSelectedIndex(lastSelectedTFIndex - 1);
            } else {
                cmbxTestingFormulae.setSelectedIndex(lastSelectedTFIndex);
            }
        } else {
            cmbxTestingFormulae.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"- This logic has no testing formula -"}));
            cmbxTestingFormulae.setSelectedIndex(0);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dlgSteps = new javax.swing.JDialog();
        pnlStepControls = new javax.swing.JPanel();
        scrlStepsTree = new javax.swing.JScrollPane();
        treStepsTree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        btnSelectAll = new javax.swing.JButton();
        btnInvertSelection = new javax.swing.JButton();
        btnStartSteps = new javax.swing.JButton();
        dlgSatCheckOptions = new javax.swing.JDialog();
        jLabel1 = new javax.swing.JLabel();
        rdbtnStop = new javax.swing.JRadioButton();
        rdbtnPause = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        btnStartSatCheck = new javax.swing.JButton();
        btngrpSatCheckOptions = new javax.swing.ButtonGroup();
        dlgFormulaTransformer = new javax.swing.JDialog();
        pnlEngineSettings = new javax.swing.JPanel();
        scrlFormulaCode = new javax.swing.JScrollPane();
        txaFormulaCode = new javax.swing.JTextArea();
        lblSelectFormula = new javax.swing.JLabel();
        cmbxTestingFormulae = new javax.swing.JComboBox();
        lblComposeFormula = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        btnBuildTableaux = new javax.swing.JButton();
        btnSteps = new javax.swing.JButton();
        btnSatCheck = new javax.swing.JButton();
        lblInfixEditor = new javax.swing.JLabel();

        dlgSteps.setTitle("Step By Step - Break Points");
        dlgSteps.setAlwaysOnTop(true);
        dlgSteps.setModal(true);
        dlgSteps.setResizable(false);
        dlgSteps.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgStepsWindowClosing(evt);
            }
        });

        pnlStepControls.setBorder(javax.swing.BorderFactory.createTitledBorder("Break Points"));

        scrlStepsTree.setViewportView(treStepsTree);

        org.jdesktop.layout.GroupLayout pnlStepControlsLayout = new org.jdesktop.layout.GroupLayout(pnlStepControls);
        pnlStepControls.setLayout(pnlStepControlsLayout);
        pnlStepControlsLayout.setHorizontalGroup(
            pnlStepControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlStepsTree, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
        );
        pnlStepControlsLayout.setVerticalGroup(
            pnlStepControlsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(scrlStepsTree, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
        );

        jPanel1.setLayout(new java.awt.GridBagLayout());

        btnSelectAll.setText("Select All");
        btnSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSelectAllActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel1.add(btnSelectAll, gridBagConstraints);

        btnInvertSelection.setText("Invert Selection");
        btnInvertSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInvertSelectionActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel1.add(btnInvertSelection, gridBagConstraints);

        btnStartSteps.setText("Start...");
        btnStartSteps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartStepsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        jPanel1.add(btnStartSteps, gridBagConstraints);

        org.jdesktop.layout.GroupLayout dlgStepsLayout = new org.jdesktop.layout.GroupLayout(dlgSteps.getContentPane());
        dlgSteps.getContentPane().setLayout(dlgStepsLayout);
        dlgStepsLayout.setHorizontalGroup(
            dlgStepsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStepControls, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
        );
        dlgStepsLayout.setVerticalGroup(
            dlgStepsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dlgStepsLayout.createSequentialGroup()
                .add(pnlStepControls, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        dlgSatCheckOptions.setTitle("Satisfiability Check Options");
        dlgSatCheckOptions.setAlwaysOnTop(true);
        dlgSatCheckOptions.setModal(true);
        dlgSatCheckOptions.setResizable(false);

        jLabel1.setText("In order to reduce the run time, you may only check for satisfiability and choose to:");

        btngrpSatCheckOptions.add(rdbtnStop);
        rdbtnStop.setText("Stop after finding a first open premodel, or");

        btngrpSatCheckOptions.add(rdbtnPause);
        rdbtnPause.setText("Pause after each found open premodel,");

        jLabel2.setText("so you can resume to lookup for another open one.");

        btnStartSatCheck.setText("Start...");
        btnStartSatCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartSatCheckActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout dlgSatCheckOptionsLayout = new org.jdesktop.layout.GroupLayout(dlgSatCheckOptions.getContentPane());
        dlgSatCheckOptions.getContentPane().setLayout(dlgSatCheckOptionsLayout);
        dlgSatCheckOptionsLayout.setHorizontalGroup(
            dlgSatCheckOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dlgSatCheckOptionsLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .add(dlgSatCheckOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(dlgSatCheckOptionsLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(dlgSatCheckOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(rdbtnPause)
                            .add(rdbtnStop)
                            .add(dlgSatCheckOptionsLayout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(jLabel2))))))
            .add(dlgSatCheckOptionsLayout.createSequentialGroup()
                .add(180, 180, 180)
                .add(btnStartSatCheck)
                .addContainerGap(174, Short.MAX_VALUE))
        );
        dlgSatCheckOptionsLayout.setVerticalGroup(
            dlgSatCheckOptionsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dlgSatCheckOptionsLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(rdbtnStop)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rdbtnPause)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .add(18, 18, 18)
                .add(btnStartSatCheck)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout dlgFormulaTransformerLayout = new org.jdesktop.layout.GroupLayout(dlgFormulaTransformer.getContentPane());
        dlgFormulaTransformer.getContentPane().setLayout(dlgFormulaTransformerLayout);
        dlgFormulaTransformerLayout.setHorizontalGroup(
            dlgFormulaTransformerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        dlgFormulaTransformerLayout.setVerticalGroup(
            dlgFormulaTransformerLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

        pnlEngineSettings.setBorder(javax.swing.BorderFactory.createTitledBorder("Premodels Construction Settings"));

        txaFormulaCode.setColumns(20);
        txaFormulaCode.setLineWrap(true);
        txaFormulaCode.setRows(5);
        txaFormulaCode.setText("<No Formula Selected>");
        txaFormulaCode.setWrapStyleWord(true);
        txaFormulaCode.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlFormulaCode.setViewportView(txaFormulaCode);

        lblSelectFormula.setText("Select a Formula");

        cmbxTestingFormulae.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "- you should select a logic first -" }));
        cmbxTestingFormulae.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbxTestingFormulaeItemStateChanged(evt);
            }
        });
        cmbxTestingFormulae.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxTestingFormulaeActionPerformed(evt);
            }
        });

        lblComposeFormula.setText("Or Compose your own formula");

        jPanel3.setLayout(new java.awt.GridBagLayout());

        btnBuildTableaux.setText("Build Premodels");
        btnBuildTableaux.setToolTipText("Build all tableaux");
        btnBuildTableaux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuildTableauxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(btnBuildTableaux, gridBagConstraints);

        btnSteps.setText("Step By Step...");
        btnSteps.setToolTipText("Allows break points on rules");
        btnSteps.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(btnSteps, gridBagConstraints);

        btnSatCheck.setText("Satisfiability Check...");
        btnSatCheck.setToolTipText("Stops after first open tableau found");
        btnSatCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSatCheckActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        jPanel3.add(btnSatCheck, gridBagConstraints);

        lblInfixEditor.setForeground(new java.awt.Color(0, 70, 213));
        lblInfixEditor.setText("<html><u>infix formula editor..</u></html>");

        org.jdesktop.layout.GroupLayout pnlEngineSettingsLayout = new org.jdesktop.layout.GroupLayout(pnlEngineSettings);
        pnlEngineSettings.setLayout(pnlEngineSettingsLayout);
        pnlEngineSettingsLayout.setHorizontalGroup(
            pnlEngineSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEngineSettingsLayout.createSequentialGroup()
                .add(pnlEngineSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlEngineSettingsLayout.createSequentialGroup()
                        .add(lblSelectFormula)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cmbxTestingFormulae, 0, 347, Short.MAX_VALUE))
                    .add(lblComposeFormula)
                    .add(pnlEngineSettingsLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlFormulaCode, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE))
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlEngineSettingsLayout.createSequentialGroup()
                        .addContainerGap(332, Short.MAX_VALUE)
                        .add(lblInfixEditor)))
                .addContainerGap())
        );
        pnlEngineSettingsLayout.setVerticalGroup(
            pnlEngineSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEngineSettingsLayout.createSequentialGroup()
                .add(pnlEngineSettingsLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblSelectFormula)
                    .add(cmbxTestingFormulae, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblComposeFormula)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlFormulaCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblInfixEditor)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEngineSettings, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEngineSettings, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents
    /*
     * should test if the selected formula is a valid one
     * then should display the selected formula code in txaFormulaCode
     */
    private void cmbxTestingFormulaeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxTestingFormulaeActionPerformed
        Logic selectedLogic = null;
        if (mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
            selectedLogic = mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab().getLogic();
        }
        if (selectedLogic == null) {
            txaFormulaCode.setText((String) cmbxTestingFormulae.getSelectedItem());
        } else if (selectedLogic.getTestingFormulae().size() == 0) {
            txaFormulaCode.setText("");
        } else {
            txaFormulaCode.setText(selectedLogic.getTestingFormulae().get(cmbxTestingFormulae.getSelectedIndex()).getCode());
        }
        txaFormulaCode.setCaretPosition(0);
    }//GEN-LAST:event_cmbxTestingFormulaeActionPerformed

    /*
     * should test the given arguments and settings (it's sufficient to test if the composed formula is parsed well)
     * then ask the engine to build the tbaleau with the specified arguments and settings
     * then ask the TableauxPanel to display the wallet...
     */
    private void btnBuildTableauxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuildTableauxActionPerformed
        Logic selectedLogic = null;
        if (mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
            selectedLogic = mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab().getLogic();
        }
        Engine engine = null;
        if (selectedLogic != null) {
            engine = getEngine(selectedLogic);
        }
        if (engine != null) {
            getMainFrame().setEngine(engine);
            getMainFrame().getEngine().buildTableaux();
            getMainFrame().getEngine().start();
        }
    }//GEN-LAST:event_btnBuildTableauxActionPerformed

    private Engine getEngine(Logic chosenLogic) {
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(chosenLogic);
        oldiesTokenizer.initializeTokenizerAndProps();
        MarkedExpression formula = null;
        if (txaFormulaCode.getText().trim().length() != 0) {
            try {
                formula = new MarkedExpression(oldiesTokenizer.parseExpression(txaFormulaCode.getText()));
                oldiesTokenizer.verifyCodeEnd();
            } catch (ParseException ex) {
                DialogsFactory.syntaxErrorMessage(this.mainFrame, "The given formula code raised the following parser exception:\n\n" +
                        ex.getMessage());
                return null;
            }
        }
        Strategy newStr;
        try {
            newStr = oldiesTokenizer.parseStrategy(chosenLogic.getStrategy(chosenLogic.getMainStrategyName()).getCode());//(String) cmbxCompleteStrategies.getSelectedItem()
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            DialogsFactory.syntaxErrorMessage(mainFrame, "The given strategy raised the following parser exception:\n" +
                    ex.getMessage());
            return null;
        }
        Engine engine = new Engine(chosenLogic, newStr, formula, mainFrame);
        return engine;
    }

    public void enableBuildButtons() {
        btnBuildTableaux.setEnabled(true);
        btnSteps.setEnabled(true);
        btnSatCheck.setEnabled(true);
    }

    public void disableBuildButtons() {
        btnBuildTableaux.setEnabled(false);
        btnSteps.setEnabled(false);
        btnSatCheck.setEnabled(false);
    }

    private void showStepsDialog() {
        refreshSteps();
        setAllWorkersSelected();
        treStepsTree.repaint();
        dlgSteps.pack();
        Point p = mainFrame.getLocation();
        int x = (mainFrame.getWidth() - dlgSteps.getWidth()) / 2;
        int y = (mainFrame.getHeight() - dlgSteps.getHeight()) / 2;
        p.translate(x, y);
        dlgSteps.setLocation(p);
        dlgSteps.setVisible(true);
    }

    private void hideStepsDialog() {
        dlgSteps.dispose();
    }

    private void initSatCheckOptions() {
        rdbtnStop.setSelected(true);
    }

    private void showSatCheckDialog() {
        dlgSatCheckOptions.pack();
        int x = (mainFrame.getWidth() - dlgSatCheckOptions.getWidth()) / 2;
        int y = (mainFrame.getHeight() - dlgSatCheckOptions.getHeight()) / 2;
        Point p = mainFrame.getLocation();
        p.translate(x, y);
        dlgSatCheckOptions.setLocation(p);
        dlgSatCheckOptions.setVisible(true);
    }

    private void hideSatCheckDialog() {
        dlgSatCheckOptions.dispose();
    }

    private void btnStartStepsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartStepsActionPerformed
        getMainFrame().getEngine().setRulesBreakPoints(rulesBreakPoints);
        getMainFrame().getEngine().setRunningBySteps(true);
        getMainFrame().getEngine().buildTableaux();
        getMainFrame().getEngine().start();
        hideStepsDialog();
}//GEN-LAST:event_btnStartStepsActionPerformed

    private void btnStepsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStepsActionPerformed
        Logic selectedLogic = null;
        if (mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
            selectedLogic = mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab().getLogic();
        }
        Engine engine = null;
        if (selectedLogic != null) {
            engine = getEngine(selectedLogic);
        }
        if (engine != null) {
            getMainFrame().setEngine(engine);
            displayStepsTree(engine.getStrategy());
            showStepsDialog();
        }
}//GEN-LAST:event_btnStepsActionPerformed

    private void dlgStepsWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgStepsWindowClosing
        hideStepsDialog();
    }//GEN-LAST:event_dlgStepsWindowClosing

    private void btnSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSelectAllActionPerformed
        setAllWorkersSelected();
        treStepsTree.repaint();
//        displayNodesStates(); 
    }//GEN-LAST:event_btnSelectAllActionPerformed

    private void btnInvertSelectionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInvertSelectionActionPerformed
        invertAllWorkersSelection();
        treStepsTree.repaint();
    }//GEN-LAST:event_btnInvertSelectionActionPerformed

private void btnSatCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSatCheckActionPerformed
    showSatCheckDialog();
}//GEN-LAST:event_btnSatCheckActionPerformed

private void btnStartSatCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartSatCheckActionPerformed
    Logic selectedLogic = null;
    if (mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab() != null) {
        selectedLogic = mainFrame.getLoadedLogicsPanel().getSelectedLogicDefTab().getLogic();
    }
    Engine engine = null;
    if (selectedLogic != null) {
        engine = getEngine(selectedLogic);
    }
    if (engine != null) {
        if (rdbtnStop.isSelected()) {
            engine.setOpenTableauAction(Engine.STOP_WHEN_HAVING_OPEN_TABLEAU);
        } else {
            engine.setOpenTableauAction(Engine.PAUSE_WHEN_HAVING_OPEN_TABLEAU);
        }
        getMainFrame().setEngine(engine);
        getMainFrame().getEngine().buildTableaux();
        getMainFrame().getEngine().start();
    }
    hideSatCheckDialog();
}//GEN-LAST:event_btnStartSatCheckActionPerformed

private void cmbxTestingFormulaeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbxTestingFormulaeItemStateChanged
}//GEN-LAST:event_cmbxTestingFormulaeItemStateChanged

    public void refresh() {
        this.resource = java.util.ResourceBundle.getBundle("lotrec.resources.TableauxEnginePanel", ResourcesProvider.getCurrentLocale());
        this.setBorder(BorderFactory.createTitledBorder(resource.getString("EngineSettings.Title")));
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuildTableaux;
    private javax.swing.JButton btnInvertSelection;
    private javax.swing.JButton btnSatCheck;
    private javax.swing.JButton btnSelectAll;
    private javax.swing.JButton btnStartSatCheck;
    private javax.swing.JButton btnStartSteps;
    private javax.swing.JButton btnSteps;
    private javax.swing.ButtonGroup btngrpSatCheckOptions;
    private javax.swing.JComboBox cmbxTestingFormulae;
    private javax.swing.JDialog dlgFormulaTransformer;
    private javax.swing.JDialog dlgSatCheckOptions;
    private javax.swing.JDialog dlgSteps;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lblComposeFormula;
    private javax.swing.JLabel lblInfixEditor;
    private javax.swing.JLabel lblSelectFormula;
    private javax.swing.JPanel pnlEngineSettings;
    private javax.swing.JPanel pnlStepControls;
    private javax.swing.JRadioButton rdbtnPause;
    private javax.swing.JRadioButton rdbtnStop;
    private javax.swing.JScrollPane scrlFormulaCode;
    private javax.swing.JScrollPane scrlStepsTree;
    private javax.swing.JTree treStepsTree;
    private javax.swing.JTextArea txaFormulaCode;
    // End of variables declaration//GEN-END:variables

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public void setMainFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // End of variables declaration
    class MyMutableTreeNode extends DefaultMutableTreeNode {

        JCheckBox x;
        Object obj;

        public MyMutableTreeNode(String title) {
            super(title);
            x = new JCheckBox(title);
            x.setBackground(Color.WHITE);
        }

        private MyMutableTreeNode() {
            super();
        }

        public JCheckBox getCheckBox() {
            return x;
        }
    }

    class MyRenderer implements TreeCellRenderer {

        public MyRenderer() {
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree,
                Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            if (leaf) {
                return ((MyMutableTreeNode) value).getCheckBox();
            } else {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
//                System.out.println(node.getUserObject());
                if (node.getUserObject() != null) {
                    return new JLabel(node.getUserObject().toString());
                }
                return new JLabel("Null");
            //node.getUserObject().toString()
            }
        }
    }

    class MyCellEditor implements TreeCellEditor {

        @Override
        public void addCellEditorListener(javax.swing.event.CellEditorListener l) {
        }

        @Override
        public void cancelCellEditing() {
        }

        @Override
        public Object getCellEditorValue() {
            return this;
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value,
                boolean isSelected, boolean expanded, boolean leaf, int row) {
            if (leaf) {
                return ((MyMutableTreeNode) value).getCheckBox();
            } else {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                return new JLabel(node.getUserObject().toString());
            }
        }

        @Override
        public boolean isCellEditable(java.util.EventObject isEditableEvt) {
            if (isEditableEvt instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) isEditableEvt;
                if (me.getClickCount() == 1) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void removeCellEditorListener(javax.swing.event.CellEditorListener l) {
        }

        @Override
        public boolean shouldSelectCell(java.util.EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            return true;
        }
    }

    public int getInternalPanelHeight() {
        return pnlEngineSettings.getWidth();
    }
}
