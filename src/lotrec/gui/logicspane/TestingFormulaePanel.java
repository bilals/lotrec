/*
 * TestingFormulaePanel.java
 *
 * Created on 1 novembre 2007, 17:10
 */
package lotrec.gui.logicspane;

import javax.swing.DefaultListModel;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.gui.DialogsFactory;
import lotrec.parser.OldiesTokenizer;
import lotrec.resources.ResourcesProvider;

/**
 *
 * @author  said
 */
public class TestingFormulaePanel extends javax.swing.JPanel {

    private LogicDefTab logicDefTab;
    private java.util.ResourceBundle resource;
    private int lastSelected = -1;//should never be changed outside valueChanged
    int editedFormulaNum;

    /** Creates new form TestingFormulaePanel */
    public TestingFormulaePanel() {
        initComponents();
    }

    public TestingFormulaePanel(LogicDefTab logicDefTab) {
        this.logicDefTab = logicDefTab;
        initComponents();
        refreshFormulaeList();

        //to select the first row & fire the first display event
        if (logicDefTab.getLogic().getTestingFormulae().size() > 0) {
            lstFormulaeList.setSelectedIndex(0);
        }
        displaySelectedFormula();
        setFieldsEditable(false);
    }

    //this refresh changes lastSelected index
    //so it's necessary to adjust lastSelected 
    //manually and conviniently after each call
    public void refreshFormulaeList() {
        //Display the rules in the JList
        DefaultListModel listModel = new DefaultListModel();
        int[] similarsCount = new int[logicDefTab.getLogic().getTestingFormulae().size()];
        for (int i = 0; i < logicDefTab.getLogic().getTestingFormulae().size(); i++) {
            String forName = logicDefTab.getLogic().getTestingFormulae().get(i).getDisplayName();
            if (listModel.contains(forName)) {
                listModel.addElement(forName + "(" + ++similarsCount[listModel.indexOf(forName)] + ")");
            } else {
                listModel.addElement(forName);
            }
        }
        getLstFormulaeList().setModel(listModel);
        if (logicDefTab.getLogic().getTestingFormulae().size() == 0) {
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } else {
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    public void displaySelectedFormula() {
        if (getLastSelected() == -1) {
            txfName.setText("");
            txaCode.setText("");
            txaComments.setText("");
            hideComments();
        } else {
            TestingFormula tf = (TestingFormula) logicDefTab.getLogic().getTestingFormulae().get(getLastSelected());
            txfName.setText(tf.getDisplayName());
            txaCode.setText(tf.getCode());
            txaCode.setCaretPosition(0);
            txaComments.setText(tf.getComment());
            txaComments.setCaretPosition(0);
            if (txaComments.getText().equals("")) {
                hideComments();
            } else {
                showComments();
            }
        }
    }

    public void refresh() {
        resource = java.util.ResourceBundle.getBundle("lotrec.resources.LoadedLogicsPanel", ResourcesProvider.getCurrentLocale());
        lblFormulaName.setText(resource.getString("TFTab.Titles.CodeAppearance"));
        lblCode.setText(resource.getString("TFTab.Titles.Code"));
        lblComments.setText(resource.getString("TFTab.Titles.Comments"));
        pnlSelectedFormula.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("TFTab.SelectedFormula")));
        pnlFormulaeList.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("TFTab.FormulaeList")));
    }

    private void deleteFormula() {
        if (getLastSelected() == -1) {
//            System.out.println("Formulas list most likely empty.. no formula to be deleted..");
//        } else if (logic.getTestingFormulae().size() == 1) {
        // CAUTION!!
        // last formula
        } else {
            TestingFormula tf = (TestingFormula) logicDefTab.getLogic().getTestingFormulae().get(getLastSelected());
            //--> display WARNING
            //if ok
            //  WE MUST ASK THE IMPACT VERIFIER FIRST!!
            //   --> delete from lstFormulaeList
            //   --> delete it really from the logic
            int choice = DialogsFactory.deleteDialog(pnlSelectedFormula, "the formula " + tf.getDisplayName());
            if (choice == 0) {
//                System.out.println("formula " + tf.getDisplayName() + " will be deleted");
                logicDefTab.getLogic().removeTestingFormula(tf);
                logicDefTab.setModifiedAndNotSaved(true);
                int selectionIndex;
                if (getLastSelected() == 0) {
                    //when the first formula is deleted
                    //show the one coming to its place                
                    selectionIndex = getLastSelected();
                } else {
                    //else, show the one before (always safer! ;))
                    selectionIndex = getLastSelected() - 1;
                }
                logicDefTab.getMainFrame().getControlsPanel().refreshFormulaeList();
                refreshFormulaeList();
                getLstFormulaeList().setSelectedIndex(selectionIndex);
            } else {
                return;
            }
        }
    }

    private void showComments() {
        //lblShowHideComments.setText("[--]");
        lblShowHideComments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/hide.png")));
        scrlComments.setVisible(true);
    }

    private void hideComments() {
        //lblShowHideComments.setText("[+]");
        lblShowHideComments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/show.png")));
        scrlComments.setVisible(false);
    }

    private void setFieldsEditable(boolean yes) {
        txfName.setEditable(yes);
        txaCode.setEditable(yes);
        txaComments.setEditable(yes);
    }

    private void saveEdition() {
        //Verify infos
        //if ok 
        //   --> Save info
        //else
        //   --> display the error
        //   --> remain in edition 
        TestingFormula tf;
        String code = txaCodeIn.getText();
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(logicDefTab.getLogic());
        oldiesTokenizer.initializeTokenizerAndProps();
        MarkedExpression formula;
        try {
            formula = new MarkedExpression(oldiesTokenizer.parseExpression(code));
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given formula code raised the following parser exception:\n\n" +
                    ex.getMessage());
            return;
        }
        if (editedFormulaNum == -1) {
            tf = new TestingFormula();
            tf.setFormula(formula);
            if (radbtnOutput.isSelected()) {
                tf.setName("");
            } else {
                tf.setName(txfNameIn.getText());
            }
//            if (txfNameIn.getText().equals("")) {
//                tf.setName(formula.toString());
//            } else {
//                tf.setName(txfNameIn.getText() + " " + formula.toString());
//            }
//            tf.setName(getDiffrentName(tf));
            tf.setComment(txaCommentsIn.getText());
//            System.out.println("new formula will be added:" + tf.getDisplayName());
            logicDefTab.getLogic().addTestingFormula(tf);
            logicDefTab.setModifiedAndNotSaved(true);
            cancelEditionDialog(logicDefTab.getLogic().getTestingFormulae().size() - 1);
        } else {
            tf = (TestingFormula) logicDefTab.getLogic().getTestingFormulae().get(editedFormulaNum);
//            System.out.println("Formula " + tf.getDisplayName() + " will be saved as: " + txfNameIn.getText());
            tf.setFormula(formula);
            if (radbtnOutput.isSelected()) {
                tf.setName("");
            } else {
                tf.setName(txfNameIn.getText());
            }
//            if (txfNameIn.getText().equals("")) {
//                tf.setName(formula.toString());
//            } else {
//                tf.setName(txfNameIn.getText());
//            }
//            tf.setName(getDiffrentName(tf));
            tf.setComment(txaCommentsIn.getText());
            logicDefTab.setModifiedAndNotSaved(true);
            cancelEditionDialog(getLastSelected());
        }
    }

    private void cancelEdition() {
        int n = DialogsFactory.cancelDialog(dlgEdit);
        if (n == 0) {
            cancelEditionDialog(getLastSelected());

        } else {
        //nothing to do
        }
    }

    private void cancelEditionDialog(int selectionIndex) {
        dlgEdit.dispose();
        refreshFormulaeList();
        logicDefTab.getMainFrame().getControlsPanel().refreshFormulaeList();
        getLstFormulaeList().setSelectedIndex(selectionIndex);
    }

    private void showEditionDialog() {
        if (editedFormulaNum != -1) {
            dlgEdit.setTitle("Edit Formula");
            TestingFormula tf = (TestingFormula) logicDefTab.getLogic().getTestingFormulae().get(editedFormulaNum);
            if (tf.getName().equals(tf.getDisplayName())) {
                radbtnUserName.setSelected(true);
                txfNameIn.setText(tf.getDisplayName());
                txfNameIn.setEditable(true);
            } else {
                radbtnOutput.setSelected(true);
                txfNameIn.setEditable(false);
                txfNameIn.setText("");
            }
            txaCodeIn.setText(tf.getCode());
            txaCodeIn.setCaretPosition(0);
            txaCommentsIn.setText(tf.getComment());
            txaCommentsIn.setCaretPosition(0);
        } else {
            dlgEdit.setTitle("New Formula");
            radbtnOutput.setSelected(true);
            txfNameIn.setEditable(false);
            txfNameIn.setText("");
            txaCodeIn.setText("");
            txaCommentsIn.setText("");
        }
        dlgEdit.pack();
        dlgEdit.setLocation(pnlSelectedFormula.getLocationOnScreen());
        dlgEdit.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dlgEdit = new javax.swing.JDialog();
        pnlEditedFormula = new javax.swing.JPanel();
        lblCodeIn = new javax.swing.JLabel();
        lblCommentsIn = new javax.swing.JLabel();
        scrlCodeIn = new javax.swing.JScrollPane();
        txaCodeIn = new javax.swing.JTextArea();
        scrlCommentsIn = new javax.swing.JScrollPane();
        txaCommentsIn = new javax.swing.JTextArea();
        txfNameIn = new javax.swing.JTextField();
        pnlSaveCancel = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        radbtnOutput = new javax.swing.JRadioButton();
        radbtnUserName = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        btngrpDisplayName = new javax.swing.ButtonGroup();
        pnlFormulaeList = new javax.swing.JPanel();
        scrlFormulaeList = new javax.swing.JScrollPane();
        lstFormulaeList = new javax.swing.JList();
        pnlAED = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        pnlSelectedFormula = new javax.swing.JPanel();
        lblFormulaName = new javax.swing.JLabel();
        lblCode = new javax.swing.JLabel();
        lblComments = new javax.swing.JLabel();
        scrlCode = new javax.swing.JScrollPane();
        txaCode = new javax.swing.JTextArea();
        scrlComments = new javax.swing.JScrollPane();
        txaComments = new javax.swing.JTextArea();
        lblShowHideComments = new javax.swing.JLabel();
        txfName = new javax.swing.JTextField();

        dlgEdit.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dlgEdit.setAlwaysOnTop(true);
        dlgEdit.setModal(true);
        dlgEdit.setResizable(false);
        dlgEdit.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgEditWindowClosing(evt);
            }
        });

        pnlEditedFormula.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Formula"));
        pnlEditedFormula.setOpaque(false);

        lblCodeIn.setText("Code");

        lblCommentsIn.setText("Comments");

        txaCodeIn.setColumns(20);
        txaCodeIn.setLineWrap(true);
        txaCodeIn.setRows(5);
        txaCodeIn.setText("<Fomula Code>");
        txaCodeIn.setWrapStyleWord(true);
        txaCodeIn.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlCodeIn.setViewportView(txaCodeIn);

        txaCommentsIn.setColumns(20);
        txaCommentsIn.setLineWrap(true);
        txaCommentsIn.setRows(5);
        txaCommentsIn.setText("<Comments>");
        txaCommentsIn.setWrapStyleWord(true);
        txaCommentsIn.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlCommentsIn.setViewportView(txaCommentsIn);

        txfNameIn.setText("<Formula Name>");

        pnlSaveCancel.setOpaque(false);
        pnlSaveCancel.setLayout(new java.awt.GridBagLayout());

        btnSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/check-16.png"))); // NOI18N
        btnSave.setMnemonic('S');
        btnSave.setText("Save");
        btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        pnlSaveCancel.add(btnSave, gridBagConstraints);

        btnCancel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cross-16.png"))); // NOI18N
        btnCancel.setMnemonic('C');
        btnCancel.setText("Cancel");
        btnCancel.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });
        pnlSaveCancel.add(btnCancel, new java.awt.GridBagConstraints());

        btngrpDisplayName.add(radbtnOutput);
        radbtnOutput.setText("Use the Code's Output Format");
        radbtnOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radbtnOutputActionPerformed(evt);
            }
        });

        btngrpDisplayName.add(radbtnUserName);
        radbtnUserName.setText("Use this Name");
        radbtnUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radbtnUserNameActionPerformed(evt);
            }
        });

        jLabel1.setText("Name:");

        org.jdesktop.layout.GroupLayout pnlEditedFormulaLayout = new org.jdesktop.layout.GroupLayout(pnlEditedFormula);
        pnlEditedFormula.setLayout(pnlEditedFormulaLayout);
        pnlEditedFormulaLayout.setHorizontalGroup(
            pnlEditedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedFormulaLayout.createSequentialGroup()
                .add(lblCommentsIn)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlEditedFormulaLayout.createSequentialGroup()
                .add(pnlEditedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pnlEditedFormulaLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlCodeIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlSaveCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, lblCodeIn)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jLabel1)
                    .add(pnlEditedFormulaLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(pnlEditedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, scrlCommentsIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, pnlEditedFormulaLayout.createSequentialGroup()
                                .add(radbtnUserName)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(txfNameIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, radbtnOutput))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                .add(0, 0, 0))
        );
        pnlEditedFormulaLayout.setVerticalGroup(
            pnlEditedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedFormulaLayout.createSequentialGroup()
                .add(lblCodeIn)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlCodeIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(radbtnOutput)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEditedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(radbtnUserName)
                    .add(txfNameIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblCommentsIn)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlCommentsIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlSaveCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout dlgEditLayout = new org.jdesktop.layout.GroupLayout(dlgEdit.getContentPane());
        dlgEdit.getContentPane().setLayout(dlgEditLayout);
        dlgEditLayout.setHorizontalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedFormula, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgEditLayout.setVerticalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedFormula, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setOpaque(false);

        pnlFormulaeList.setBorder(javax.swing.BorderFactory.createTitledBorder("Formulae List"));
        pnlFormulaeList.setOpaque(false);

        lstFormulaeList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "<Testing Formulae List>" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstFormulaeList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstFormulaeListValueChanged(evt);
            }
        });
        lstFormulaeList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstFormulaeListMouseClicked(evt);
            }
        });
        scrlFormulaeList.setViewportView(lstFormulaeList);

        pnlAED.setOpaque(false);
        pnlAED.setLayout(new java.awt.GridBagLayout());

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/add.png"))); // NOI18N
        btnAdd.setMnemonic('A');
        btnAdd.setText("Add");
        btnAdd.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        pnlAED.add(btnAdd, gridBagConstraints);

        btnEdit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/edt.png"))); // NOI18N
        btnEdit.setMnemonic('E');
        btnEdit.setText("Edit");
        btnEdit.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlAED.add(btnEdit, gridBagConstraints);

        btnDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/del.png"))); // NOI18N
        btnDelete.setMnemonic('D');
        btnDelete.setText("Delete");
        btnDelete.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        pnlAED.add(btnDelete, new java.awt.GridBagConstraints());

        org.jdesktop.layout.GroupLayout pnlFormulaeListLayout = new org.jdesktop.layout.GroupLayout(pnlFormulaeList);
        pnlFormulaeList.setLayout(pnlFormulaeListLayout);
        pnlFormulaeListLayout.setHorizontalGroup(
            pnlFormulaeListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(scrlFormulaeList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
        );
        pnlFormulaeListLayout.setVerticalGroup(
            pnlFormulaeListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlFormulaeListLayout.createSequentialGroup()
                .add(scrlFormulaeList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAED, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnlSelectedFormula.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Formula"));
        pnlSelectedFormula.setOpaque(false);

        lblFormulaName.setText("Name");

        lblCode.setText("Code");

        lblComments.setText("Comments");

        txaCode.setColumns(20);
        txaCode.setLineWrap(true);
        txaCode.setRows(5);
        txaCode.setText("<Fomula Code>");
        txaCode.setWrapStyleWord(true);
        txaCode.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlCode.setViewportView(txaCode);

        txaComments.setColumns(20);
        txaComments.setLineWrap(true);
        txaComments.setRows(5);
        txaComments.setText("<Comments>");
        txaComments.setWrapStyleWord(true);
        txaComments.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlComments.setViewportView(txaComments);

        lblShowHideComments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/show.png"))); // NOI18N
        lblShowHideComments.setToolTipText("Show/Hide Comments");
        lblShowHideComments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShowHideCommentsMouseClicked(evt);
            }
        });

        txfName.setText("<Formula Name>");

        org.jdesktop.layout.GroupLayout pnlSelectedFormulaLayout = new org.jdesktop.layout.GroupLayout(pnlSelectedFormula);
        pnlSelectedFormula.setLayout(pnlSelectedFormulaLayout);
        pnlSelectedFormulaLayout.setHorizontalGroup(
            pnlSelectedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedFormulaLayout.createSequentialGroup()
                .add(pnlSelectedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSelectedFormulaLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlCode, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                    .add(pnlSelectedFormulaLayout.createSequentialGroup()
                        .add(lblFormulaName)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(txfName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                    .add(pnlSelectedFormulaLayout.createSequentialGroup()
                        .add(lblComments)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblShowHideComments))
                    .add(lblCode)
                    .add(pnlSelectedFormulaLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlComments, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlSelectedFormulaLayout.setVerticalGroup(
            pnlSelectedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedFormulaLayout.createSequentialGroup()
                .add(pnlSelectedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblFormulaName)
                    .add(txfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblCode)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedFormulaLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblComments)
                    .add(lblShowHideComments))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlComments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 44, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlFormulaeList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedFormula, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlFormulaeList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlSelectedFormula, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    private void lblShowHideCommentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblShowHideCommentsMouseClicked
        if (scrlComments.isVisible()) {
            hideComments();
        } else {
            showComments();
        }
    }//GEN-LAST:event_lblShowHideCommentsMouseClicked

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        editedFormulaNum = -1;
        showEditionDialog();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        //This code must be changed all the way with lstFormulaeList mouse click
        editedFormulaNum = getLastSelected();
        showEditionDialog();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteFormula();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void lstFormulaeListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstFormulaeListValueChanged
        lastSelected = getLstFormulaeList().getSelectedIndex();
        displaySelectedFormula();
    }//GEN-LAST:event_lstFormulaeListValueChanged

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveEdition();
}//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelEdition();
}//GEN-LAST:event_btnCancelActionPerformed

    private void dlgEditWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgEditWindowClosing
        cancelEdition();
    }//GEN-LAST:event_dlgEditWindowClosing

    private void radbtnOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radbtnOutputActionPerformed
        txfNameIn.setEditable(false);
}//GEN-LAST:event_radbtnOutputActionPerformed

    private void radbtnUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radbtnUserNameActionPerformed
        txfNameIn.setEditable(true);
}//GEN-LAST:event_radbtnUserNameActionPerformed

    private void lstFormulaeListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstFormulaeListMouseClicked
        if (evt.getClickCount() == 2) {
            //This code must be changed all the way with btnEdit mouse click
            editedFormulaNum = getLastSelected();
            showEditionDialog();
        }
    }//GEN-LAST:event_lstFormulaeListMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSave;
    private javax.swing.ButtonGroup btngrpDisplayName;
    private javax.swing.JDialog dlgEdit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblCode;
    private javax.swing.JLabel lblCodeIn;
    private javax.swing.JLabel lblComments;
    private javax.swing.JLabel lblCommentsIn;
    private javax.swing.JLabel lblFormulaName;
    private javax.swing.JLabel lblShowHideComments;
    private javax.swing.JList lstFormulaeList;
    private javax.swing.JPanel pnlAED;
    private javax.swing.JPanel pnlEditedFormula;
    private javax.swing.JPanel pnlFormulaeList;
    private javax.swing.JPanel pnlSaveCancel;
    private javax.swing.JPanel pnlSelectedFormula;
    private javax.swing.JRadioButton radbtnOutput;
    private javax.swing.JRadioButton radbtnUserName;
    private javax.swing.JScrollPane scrlCode;
    private javax.swing.JScrollPane scrlCodeIn;
    private javax.swing.JScrollPane scrlComments;
    private javax.swing.JScrollPane scrlCommentsIn;
    private javax.swing.JScrollPane scrlFormulaeList;
    private javax.swing.JTextArea txaCode;
    private javax.swing.JTextArea txaCodeIn;
    private javax.swing.JTextArea txaComments;
    private javax.swing.JTextArea txaCommentsIn;
    private javax.swing.JTextField txfName;
    private javax.swing.JTextField txfNameIn;
    // End of variables declaration//GEN-END:variables
    public javax.swing.JList getLstFormulaeList() {
        return lstFormulaeList;
    }

    public int getLastSelected() {
        return lastSelected;
    }
}
