/*
 * StratTabPanel.java
 *
 * Created on 31 octobre 2007, 16:49
 */
package lotrec.gui.logicspane;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import lotrec.dataStructure.tableau.Rule;
import lotrec.gui.DialogsFactory;
import lotrec.parser.OldiesTokenizer;
import lotrec.process.Strategy;
import lotrec.resources.ResourcesProvider;

/**
 *
 * @author  said
 */
public class StratTabPanel extends javax.swing.JPanel {

    private LogicDefTab logicDefTab;
    private java.util.ResourceBundle resource;
    int lastSelected = -1;// changed only inside the valueChanged method
    int editedStratNum; //-1 if new strat added

    /**
     * Creates new form StratTabPanel
     */
    public StratTabPanel() {
        initComponents();
    }

    public StratTabPanel(LogicDefTab logicDefTab) {
        this.logicDefTab = logicDefTab;
        initComponents();
        //Display the rules in the JList
        refreshStrategiesList();
        //to select the first row & fire the first display event
        lstStrategiesList.setSelectedIndex(0);
        setFieldsEditable(false);
    }

    public void displayDefaultStrategy() {
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        String defaultStrategyName = logicDefTab.getLogic().getMainStrategyName();
        for (int i = 0; i < logicDefTab.getLogic().getStrategies().size(); i++) {
            String stratName = ((Strategy) logicDefTab.getLogic().getStrategies().get(i)).getWorkerName();
            comboModel.addElement(stratName);
            if (stratName.equals(defaultStrategyName)) {
                comboModel.setSelectedItem(stratName);
            }
        }
        cmbxDefaultStrategy.setModel(comboModel);
    }

    public int getLastSelectedIndex() {
        return lastSelected;
    }

    public void refreshSelection(int indexOfSelection) {
        lstStrategiesList.setSelectedIndex(indexOfSelection);
    }

    public void displaySelectedStrategy() {
        if (lastSelected != -1) {
            Strategy str = (Strategy) logicDefTab.getLogic().getStrategies().get(lastSelected);
            txfName.setText(str.getWorkerName());
            txaCode.setText(str.getCode());
            txaComments.setText(str.getComment());
            txaComments.setCaretPosition(0);
            if (txaComments.getText().equals("")) {
                hideComments();
            } else {
                showComments();
            }
        }
    }

    private void setDefaultStrategy() {
        logicDefTab.getLogic().addDefaultStrategy();
    }

    public void refreshStrategiesList() {
        //Display the rules in the JList
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < logicDefTab.getLogic().getStrategies().size(); i++) {
            listModel.addElement(((Strategy) logicDefTab.getLogic().getStrategies().get(i)).getWorkerName());
        }
        lstStrategiesList.setModel(listModel);
        //display the list of available COMPLETE strategies, and put the default one as the default
        displayDefaultStrategy();
    }

    public void refresh() {
        resource = java.util.ResourceBundle.getBundle("lotrec.resources.LoadedLogicsPanel", ResourcesProvider.getCurrentLocale());
        lblName.setText(resource.getString("StratTab.Titles.Name"));
        lblCode.setText(resource.getString("StratTab.Titles.Code"));
        lblComments.setText(resource.getString("StratTab.Titles.Comments"));
        pnlSelectedStrategy.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("StratTab.SelectedStrategy")));
        pnlDefaultStrategy.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("StratTab.DefaultStrategy")));
        pnlStrategiesList.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("StratTab.StrategiesList")));
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

    private void deleteStrat() {
        if (lastSelected == -1) {
//            System.out.println("Strategies list most likely empty.. no Strategy to be deleted..");
//        } else if (logic.getStrategies().size() == 1) {
        // CAUTION!!
        // last strategy
        } else {
            //--> display WARNING
            //if ok
            //  WE MUST ASK THE IMPACT VERIFIER FIRST!!            
            //   --> delete from lstStrategiesList
            //   --> delete it really from the logic    
            Strategy str = (Strategy) logicDefTab.getLogic().getStrategies().get(lastSelected);
            int choice = DialogsFactory.deleteDialog(pnlSelectedStrategy, "the strategy " + str.getWorkerName());
            if (choice == 0) {
                // test simply if the name of this strateggy figures in the code of other ones
                if (logicDefTab.getLogic().isStrategyCalledInOthers(str)) {
                    DialogsFactory.semanticErrorMessage(pnlSelectedStrategy, "The strategy " + str.getWorkerName() + " is called by some other strategies.\n" +
                            "Thus, it could not be deleted before deleting these calls first.\n");
                    return;
                }
//                System.out.println("strategy " + str.getWorkerName() + " will be deleted");
                logicDefTab.getLogic().removeStrategy(str);
                logicDefTab.setModifiedAndNotSaved(true);
                int selectionIndex;
                if (lastSelected == 0) {
                    //when the first strategy is deleted
                    //show the one coming to its place                
                    selectionIndex = lastSelected;
                } else {
                    //else, show the one before (always safer! ;))
                    selectionIndex = lastSelected - 1;
                }

                //When the deleting a strategy
                // verify that there will be :
                //  - at least one strategy
                //    (in worst case create the default empty strategy)
                //  - and a main strategy for the logic
                //if the deleted strategy is the last one
                if (logicDefTab.getLogic().getStrategies().size() == 0) {
                    //create the default empty one and add it
                    JOptionPane.showMessageDialog(pnlSelectedStrategy,
                            "You are deleting the last strategy.\n" +
                            "LoTREC will add a default (empty) strategy automatically.\n\n" +
                            "This strategy does nothing, but keeps your logic file runnable\n" +
                            "and able to build a tableau for an input formula.\n" +
                            "For more details, see its 'Comments'.",
                            "Warning",
                            JOptionPane.WARNING_MESSAGE);
                    setDefaultStrategy();
                    selectionIndex = logicDefTab.getLogic().getStrategies().size() - 1;
                } else {
                    //Note: I put it in the else part, in order to avoid having two message dialogs
                    //If the deleted strategy was the main one, than choose another one to replace it
                    if (logicDefTab.getLogic().getMainStrategyName().equals(str.getWorkerName())) {
                        JOptionPane.showMessageDialog(pnlSelectedStrategy,
                                "You are deleting the main strategy.\n" +
                                "LoTREC will choose automatically another strategy\n" +
                                "as the new main strategy.\n\n" +
                                "However, you may change it and choose another one later.",
                                "Warning",
                                JOptionPane.WARNING_MESSAGE);
                        logicDefTab.getLogic().setMainStrategyName(
                                logicDefTab.getLogic().getStrategies().firstElement().getWorkerName());
                    }
                }
//                //When the deleting a complete strategy
//                //is performed, verify that there will be :
//                //  - at least one complete strategy
//                //    (in worst case create the empty complete strategy)
//                //  - and a default strategy for the logic
//                if (str.getUsability().equals("complete")) {
//                    //if the deleted complete strategy was the last one
//                    if (logicDefTab.getLogic().getCompleteStrategies().size() == 0) {
//                        //create empty one as a complete one,
//                        //add it, and make it the default one
//                        JOptionPane.showMessageDialog(pnlSelectedStrategy,
//                                "You are deleting the last 'complete' strategy.\n" +
//                                "LoTREC will add an EmptyCompleteStrategy automatically.\n\n" +
//                                "This strategy does nothing, but keeps your logic file runnable\n" +
//                                "and able to build a tableau for an input formula.\n" +
//                                "For more details, see its 'Comments'.",
//                                "Warning",
//                                JOptionPane.WARNING_MESSAGE);
//                        setDefaultStrategy();
//                        selectionIndex = logicDefTab.getLogic().getStrategies().size() - 1;
//                    } else {
//                        //the size is >0 i.e. there are some other complete strategies remaining,
//                        // thus, make the first one of them as the new default strategy
//                        if (logicDefTab.getLogic().getMainStrategyName().equals(str.getWorkerName())) {
//                            JOptionPane.showMessageDialog(pnlSelectedStrategy,
//                                    "You are deleting the 'Default' complete strategy.\n" +
//                                    "LoTREC will choose automatically another complete one\n" +
//                                    "as the new 'Default' strategy.\n\n" +
//                                    "However, you may change it and choose another one later.",
//                                    "Warning",
//                                    JOptionPane.WARNING_MESSAGE);
//                            logicDefTab.getLogic().setMainStrategyName(
//                                    logicDefTab.getLogic().getCompleteStrategies().firstElement().getWorkerName());
//                        }
//                    }
//                }
                refreshStrategiesList();
                lstStrategiesList.setSelectedIndex(selectionIndex);
//                logicDefTab.getMainFrame().getControlsPanel().refreshSteps(); // Not sure this is necessary!!
            } else {
                //delete canceled
                return;
            }
        }
    }

    private void saveEdition() {
        //Verify infos
        //if ok 
        //   --> Save info
        //   --> Cancel the Edition Dialog appropriately
        //else
        //   --> display the error
        //   --> remain in edition
        Strategy newStr;
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(logicDefTab.getLogic());
        oldiesTokenizer.initializeTokenizerAndProps();
        try {
            newStr = oldiesTokenizer.parseStrategy(txaCodeIn.getText());
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given strategy code raised the following parser exception:\n" +
                    ex.getMessage());
            return;
        }
        String newStrName;
        oldiesTokenizer.setSource(txfNameIn.getText());
        try {
            newStrName = oldiesTokenizer.readStringToken();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given strategy name raised the following parser exception:\n\n" +
                    ex.getMessage());
            return;
        }
        try {
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            JOptionPane.showMessageDialog(dlgEdit,
                    "It seems that the given name has some extra text at the end.\n" +
                    "LoTREC will suppose being given this name: '" + newStrName + "' and will ignore the rest.\n\n" +
                    "Raised exception:\n" +
                    ex.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (logicDefTab.getLogic().isRoutineName(newStrName)) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given strategy name is a reserved keyword for routines.\n" +
                    "Please choose a name diffrent than 'allRules', 'firstRule' and 'repeat'.");
            return;
        }
        if (logicDefTab.getLogic().isRuleName(newStrName)) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given strategy name is already identifying a rule.\n" +
                    "Please choose a diffrent name.");
            return;
        }
        newStr.setWorkerName(newStrName);
        newStr.setCode(txaCodeIn.getText());
//        newStr.setUsability("complete");
        newStr.setComment(txaCommentsIn.getText());
        if (editedStratNum == -1) {
            if (logicDefTab.getLogic().isStrategyName(newStr.getWorkerName())) {
                DialogsFactory.syntaxErrorMessage(dlgEdit, "The given strategy name is already identifying another strategy.\n" +
                        "Please choose a diffrent name.");
                return;
            }
//            System.out.println("new Strategy will be added:" + newStr.getWorkerName());
            logicDefTab.getLogic().addStrategy(newStr);
            logicDefTab.setModifiedAndNotSaved(true);
            cancelEditionDialog(logicDefTab.getLogic().getStrategies().size() - 1);
        } else {
            Strategy editedStr = (Strategy) logicDefTab.getLogic().getStrategies().get(editedStratNum);
            if (logicDefTab.getLogic().isOtherStrategyName(editedStr, newStr.getWorkerName())) {
                DialogsFactory.syntaxErrorMessage(dlgEdit, "The given strategy name is already identifying another strategy.\n" +
                        "Please choose a diffrent name.");
                return;
            }
            if (logicDefTab.getLogic().isStrategyCalledInOthers(editedStr)) {
                JOptionPane.showMessageDialog(dlgEdit,
                        "The edited strategy '" + editedStr.getWorkerName() + "' is called by other ones.\n" +
                        "LoTREC will take care of replacing it appropriately by '" + newStr.getWorkerName() + "'.\n",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                logicDefTab.getLogic().replaceStrategyCalls(editedStr, newStr);
            }
//            if (editedStr.getUsability().equals("complete") &&
//                    newStr.getUsability().equals("partial") &&
//                    logicDefTab.getLogic().getCompleteStrategies().size() == 1) {
//                JOptionPane.showMessageDialog(dlgEdit,
//                        "You are changing the last 'complete' strategy and making it 'partial'.\n" +
//                        "LoTREC will add an EmptyCompleteStrategy automatically.\n\n" +
//                        "This strategy does nothing, but keeps your logic file runnable\n" +
//                        "and able to build a tableau for an input formula.\n" +
//                        "For more details, see its 'Comments'.",
//                        "Warning",
//                        JOptionPane.WARNING_MESSAGE);
//                setDefaultStrategy();
//            }
//            if (logicDefTab.getLogic().getMainStrategyName().equals(editedStr.getWorkerName()) &&
//                    newStr.getUsability().equals("complete")) {
//                logicDefTab.getLogic().setMainStrategyName(newStr.getWorkerName());
//            }
            if (logicDefTab.getLogic().getMainStrategyName().equals(editedStr.getWorkerName())) {
                logicDefTab.getLogic().setMainStrategyName(newStr.getWorkerName());
            }
//            System.out.println("strategy " + editedStr.getWorkerName() + " will be saved as: " + newStr.getWorkerName());
            logicDefTab.getLogic().getStrategies().setElementAt(newStr, editedStratNum);
            logicDefTab.setModifiedAndNotSaved(true);
            cancelEditionDialog(lastSelected);
        }

    }

    private void cancelEdition() {
        int n = DialogsFactory.cancelDialog(dlgEdit);
        if (n == 0) {
            cancelEditionDialog(lastSelected);
        } else {
            // jsut the dlgEdit will be brought again..
        }
    }

    private void showEditionDialog() {
        if (editedStratNum != -1) {
            dlgEdit.setTitle("Edit Strategy");
            Strategy str = (Strategy) logicDefTab.getLogic().getStrategies().get(editedStratNum);
            txfNameIn.setText(str.getWorkerName());
            txaCodeIn.setText(str.getCode());
            txaCommentsIn.setText(str.getComment());
            txaCommentsIn.setCaretPosition(0);
            // display the usability
//            if (str.getUsability().equals("complete")) {
//                cmbxUsabilityIn.setSelectedIndex(1);
//            } else {
//                cmbxUsabilityIn.setSelectedIndex(0);
//            }
            fillPredefStratListExcept(str.getWorkerName());
        } else {
            dlgEdit.setTitle("New Strategy");
            txfNameIn.setText("");
            txaCodeIn.setText("");
            txaCommentsIn.setText("");
            txaCommentsIn.setCaretPosition(0);
            fillPredefStratList();
        }
        fillPredefRulesList();
        dlgEdit.pack();
        dlgEdit.setLocation(pnlSelectedStrategy.getLocationOnScreen());
        dlgEdit.setVisible(true);
    }

    private void cancelEditionDialog(int selectionIndex) {
        dlgEdit.dispose();
        refreshStrategiesList();
        lstStrategiesList.setSelectedIndex(selectionIndex);
//        System.out.println("At Cancel: lastSelected equals " + lastSelected);
//        logicDefTab.getMainFrame().getControlsPanel().refreshSteps(); // Not sure this is necessary!!
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
        pnlSelectedStrategy1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblName1 = new javax.swing.JLabel();
        lblCode1 = new javax.swing.JLabel();
        lblComments1 = new javax.swing.JLabel();
        txfNameIn = new javax.swing.JTextField();
        scrlComments1 = new javax.swing.JScrollPane();
        txaCommentsIn = new javax.swing.JTextArea();
        scrlCode1 = new javax.swing.JScrollPane();
        txaCodeIn = new javax.swing.JTextArea();
        pnlSaveCancel = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlPredefRulesList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstPredefRulesList = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstRoutinesList = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstPredefStrategiesList = new javax.swing.JList();
        pnlStrategiesList = new javax.swing.JPanel();
        scrlStrategiesList = new javax.swing.JScrollPane();
        lstStrategiesList = new javax.swing.JList();
        pnlAED = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        pnlSelectedStrategy = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblCode = new javax.swing.JLabel();
        lblComments = new javax.swing.JLabel();
        txfName = new javax.swing.JTextField();
        scrlComments = new javax.swing.JScrollPane();
        txaComments = new javax.swing.JTextArea();
        scrlCode = new javax.swing.JScrollPane();
        txaCode = new javax.swing.JTextArea();
        lblShowHideComments = new javax.swing.JLabel();
        pnlDefaultStrategy = new javax.swing.JPanel();
        cmbxDefaultStrategy = new javax.swing.JComboBox();

        dlgEdit.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dlgEdit.setAlwaysOnTop(true);
        dlgEdit.setModal(true);
        dlgEdit.setResizable(false);
        dlgEdit.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgEditWindowClosing(evt);
            }
        });

        pnlSelectedStrategy1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlSelectedStrategy1.setOpaque(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/idea.PNG"))); // NOI18N
        jLabel1.setText("Fill Code with Routines,Rules and/or Strategies (Double-Click on the lists)");

        lblName1.setText("Name");

        lblCode1.setText("Code");

        lblComments1.setText("Comments");

        txfNameIn.setText("<Name>");

        txaCommentsIn.setColumns(20);
        txaCommentsIn.setLineWrap(true);
        txaCommentsIn.setRows(5);
        txaCommentsIn.setWrapStyleWord(true);
        txaCommentsIn.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlComments1.setViewportView(txaCommentsIn);

        txaCodeIn.setColumns(20);
        txaCodeIn.setRows(5);
        txaCodeIn.setTabSize(2);
        txaCodeIn.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlCode1.setViewportView(txaCodeIn);

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

        org.jdesktop.layout.GroupLayout pnlSelectedStrategy1Layout = new org.jdesktop.layout.GroupLayout(pnlSelectedStrategy1);
        pnlSelectedStrategy1.setLayout(pnlSelectedStrategy1Layout);
        pnlSelectedStrategy1Layout.setHorizontalGroup(
            pnlSelectedStrategy1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedStrategy1Layout.createSequentialGroup()
                .add(pnlSelectedStrategy1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSelectedStrategy1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlComments1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 361, Short.MAX_VALUE))
                    .add(pnlSelectedStrategy1Layout.createSequentialGroup()
                        .add(pnlSelectedStrategy1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblName1)
                            .add(lblCode1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSelectedStrategy1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txfNameIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .add(scrlCode1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)))
                    .add(lblComments1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSaveCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSelectedStrategy1Layout.setVerticalGroup(
            pnlSelectedStrategy1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedStrategy1Layout.createSequentialGroup()
                .add(pnlSelectedStrategy1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName1)
                    .add(txfNameIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedStrategy1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblCode1)
                    .add(scrlCode1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblComments1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlComments1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlSaveCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel1))
        );

        pnlPredefRulesList.setBorder(javax.swing.BorderFactory.createTitledBorder("Available Rules"));

        lstPredefRulesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "list is empty.." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstPredefRulesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstPredefRulesListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstPredefRulesList);

        org.jdesktop.layout.GroupLayout pnlPredefRulesListLayout = new org.jdesktop.layout.GroupLayout(pnlPredefRulesList);
        pnlPredefRulesList.setLayout(pnlPredefRulesListLayout);
        pnlPredefRulesListLayout.setHorizontalGroup(
            pnlPredefRulesListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
        );
        pnlPredefRulesListLayout.setVerticalGroup(
            pnlPredefRulesListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Routines"));

        lstRoutinesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "repeat", "allRules", "firstRule", "applyOnce" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstRoutinesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstRoutinesListMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lstRoutinesList);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Strategies"));

        lstPredefStrategiesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "list is empty.." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstPredefStrategiesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstPredefStrategiesListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(lstPredefStrategiesList);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout dlgEditLayout = new org.jdesktop.layout.GroupLayout(dlgEdit.getContentPane());
        dlgEdit.getContentPane().setLayout(dlgEditLayout);
        dlgEditLayout.setHorizontalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dlgEditLayout.createSequentialGroup()
                .add(pnlSelectedStrategy1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlPredefRulesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        dlgEditLayout.setVerticalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedStrategy1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(dlgEditLayout.createSequentialGroup()
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPredefRulesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setOpaque(false);

        pnlStrategiesList.setBorder(javax.swing.BorderFactory.createTitledBorder("Strategies List"));
        pnlStrategiesList.setOpaque(false);

        lstStrategiesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstStrategiesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstStrategiesListValueChanged(evt);
            }
        });
        lstStrategiesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstStrategiesListMouseClicked(evt);
            }
        });
        scrlStrategiesList.setViewportView(lstStrategiesList);

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

        org.jdesktop.layout.GroupLayout pnlStrategiesListLayout = new org.jdesktop.layout.GroupLayout(pnlStrategiesList);
        pnlStrategiesList.setLayout(pnlStrategiesListLayout);
        pnlStrategiesListLayout.setHorizontalGroup(
            pnlStrategiesListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(scrlStrategiesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
        );
        pnlStrategiesListLayout.setVerticalGroup(
            pnlStrategiesListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlStrategiesListLayout.createSequentialGroup()
                .add(scrlStrategiesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAED, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnlSelectedStrategy.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Strategy"));
        pnlSelectedStrategy.setOpaque(false);

        lblName.setText("Name");

        lblCode.setText("Code");

        lblComments.setText("Comments");

        txfName.setText("<Name>");

        txaComments.setColumns(20);
        txaComments.setLineWrap(true);
        txaComments.setRows(5);
        txaComments.setWrapStyleWord(true);
        txaComments.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlComments.setViewportView(txaComments);

        txaCode.setColumns(20);
        txaCode.setRows(5);
        txaCode.setTabSize(2);
        txaCode.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlCode.setViewportView(txaCode);

        lblShowHideComments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/show.png"))); // NOI18N
        lblShowHideComments.setToolTipText("Show/Hide Comments");
        lblShowHideComments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShowHideCommentsMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlSelectedStrategyLayout = new org.jdesktop.layout.GroupLayout(pnlSelectedStrategy);
        pnlSelectedStrategy.setLayout(pnlSelectedStrategyLayout);
        pnlSelectedStrategyLayout.setHorizontalGroup(
            pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedStrategyLayout.createSequentialGroup()
                .add(pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSelectedStrategyLayout.createSequentialGroup()
                        .add(pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblName)
                            .add(lblCode))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txfName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
                            .add(scrlCode, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)))
                    .add(pnlSelectedStrategyLayout.createSequentialGroup()
                        .add(lblComments)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblShowHideComments)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 242, Short.MAX_VALUE))
                    .add(pnlSelectedStrategyLayout.createSequentialGroup()
                        .add(20, 20, 20)
                        .add(scrlComments, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE)))
                .addContainerGap())
        );
        pnlSelectedStrategyLayout.setVerticalGroup(
            pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedStrategyLayout.createSequentialGroup()
                .add(pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblCode)
                    .add(scrlCode, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblComments)
                    .add(lblShowHideComments))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlComments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlDefaultStrategy.setBorder(javax.swing.BorderFactory.createTitledBorder("Main Strategy"));
        pnlDefaultStrategy.setOpaque(false);

        cmbxDefaultStrategy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<Complete Strategies>" }));
        cmbxDefaultStrategy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxDefaultStrategyActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlDefaultStrategyLayout = new org.jdesktop.layout.GroupLayout(pnlDefaultStrategy);
        pnlDefaultStrategy.setLayout(pnlDefaultStrategyLayout);
        pnlDefaultStrategyLayout.setHorizontalGroup(
            pnlDefaultStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDefaultStrategyLayout.createSequentialGroup()
                .addContainerGap()
                .add(cmbxDefaultStrategy, 0, 161, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlDefaultStrategyLayout.setVerticalGroup(
            pnlDefaultStrategyLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlDefaultStrategyLayout.createSequentialGroup()
                .addContainerGap()
                .add(cmbxDefaultStrategy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(pnlDefaultStrategy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(pnlStrategiesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedStrategy, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlSelectedStrategy, 0, 343, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(pnlStrategiesList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlDefaultStrategy, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
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
        editedStratNum = -1;
//        System.out.println("new Strategy will be added");
        showEditionDialog();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        //This code must be changed all the way with lstStrategiesList mouse click
        editedStratNum = lastSelected;
        Strategy str = (Strategy) logicDefTab.getLogic().getStrategies().get(editedStratNum);
//        System.out.println("strategy " + str.getWorkerName() + " will be edited");
        showEditionDialog();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteStrat();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveEdition();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelEdition();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void dlgEditWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgEditWindowClosing
        cancelEdition();
    }//GEN-LAST:event_dlgEditWindowClosing

    private void lstStrategiesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstStrategiesListValueChanged
        lastSelected = lstStrategiesList.getSelectedIndex();
//        System.out.println("lastSelected now equals " + lastSelected);
        displaySelectedStrategy();
    }//GEN-LAST:event_lstStrategiesListValueChanged

    private void cmbxDefaultStrategyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxDefaultStrategyActionPerformed
        if (!((String) cmbxDefaultStrategy.getSelectedItem()).equals(logicDefTab.getLogic().getMainStrategyName())) {
            logicDefTab.getLogic().setMainStrategyName((String) cmbxDefaultStrategy.getSelectedItem());
            logicDefTab.setModifiedAndNotSaved(true);
//            logicDefTab.getMainFrame().getControlsPanel().refreshSteps(); // Not sure this is necessary!!
        }
    }//GEN-LAST:event_cmbxDefaultStrategyActionPerformed

    private void lstStrategiesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstStrategiesListMouseClicked
        if (evt.getClickCount() == 2) {
            //This code must be changed all the way with btnEdit click
            editedStratNum = lastSelected;
//            Strategy str = (Strategy) logicDefTab.getLogic().getStrategies().get(editedStratNum);
//            System.out.println("strategy " + str.getWorkerName() + " will be edited");
            showEditionDialog();
        }
    }//GEN-LAST:event_lstStrategiesListMouseClicked

    private void fillPredefStratListExcept(String strName) {
        DefaultListModel listModel = new DefaultListModel();
        if (logicDefTab.getLogic().getStrategies() != null && logicDefTab.getLogic().getStrategies().size() != 0) {
            for (int i = 0; i < logicDefTab.getLogic().getStrategies().size(); i++) {
                String strToAddName = ((Strategy) logicDefTab.getLogic().getStrategies().get(i)).getWorkerName();
                if (!strToAddName.equals(strName)) {
                    listModel.addElement(strToAddName);
                }
            }
        } else {
            listModel.addElement("list is empty..");
        }
        lstPredefStrategiesList.setModel(listModel);
    }

    private void fillPredefStratList() {
        DefaultListModel listModel = new DefaultListModel();
        if (logicDefTab.getLogic().getStrategies() != null && logicDefTab.getLogic().getStrategies().size() != 0) {
            for (int i = 0; i < logicDefTab.getLogic().getStrategies().size(); i++) {
                listModel.addElement(((Strategy) logicDefTab.getLogic().getStrategies().get(i)).getWorkerName());
            }
        } else {
            listModel.addElement("list is empty..");
        }
        lstPredefStrategiesList.setModel(listModel);
    }

    private void fillPredefRulesList() {
        DefaultListModel listModel = new DefaultListModel();
        if (logicDefTab.getLogic().getRules() != null && logicDefTab.getLogic().getRules().size() != 0) {
            for (int i = 0; i < logicDefTab.getLogic().getRules().size(); i++) {
                listModel.addElement(((Rule) logicDefTab.getLogic().getRules().get(i)).getName());
            }
        } else {
            listModel.addElement("list is empty..");
        }
        lstPredefRulesList.setModel(listModel);
    }

    private void addToCode(String text) {
        try {
//            System.out.println("Line number: " + txaCodeIn.getLineOfOffset(txaCodeIn.getCaretPosition()));
//            System.out.println("Line start offset: " + txaCodeIn.getLineStartOffset(txaCodeIn.getLineOfOffset(txaCodeIn.getCaretPosition())));
//            System.out.println("Line end offset: " + txaCodeIn.getLineEndOffset(txaCodeIn.getLineOfOffset(txaCodeIn.getCaretPosition())));
            if (txaCodeIn.getSelectionStart() != txaCodeIn.getSelectionEnd()) {
                txaCodeIn.replaceSelection(text);
            } else if (txaCodeIn.getLineEndOffset(txaCodeIn.getLineOfOffset(txaCodeIn.getCaretPosition())) - txaCodeIn.getLineStartOffset(txaCodeIn.getLineOfOffset(txaCodeIn.getCaretPosition())) == 1) {
                //There's no selection, but the line is empty..
                txaCodeIn.insert(text, txaCodeIn.getCaretPosition());
            } else {
                //There's no selection, but the line is not empty..
                //So, go to the end of the current line, and make a new line, and put in it the new text
                txaCodeIn.insert(text + "\n", txaCodeIn.getLineEndOffset(txaCodeIn.getLineOfOffset(txaCodeIn.getCaretPosition())));
                txaCodeIn.setCaretPosition(txaCodeIn.getLineEndOffset(txaCodeIn.getLineOfOffset(txaCodeIn.getCaretPosition())));
            }
        } catch (BadLocationException blex) {
            System.out.println("Exception while getting the offset:\n" + blex.getMessage());
        }
    }

    private void lstPredefRulesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstPredefRulesListMouseClicked
        if (evt.getClickCount() == 2) {
            String text = (String) lstPredefRulesList.getSelectedValue();
            addToCode(text);
        }
    }//GEN-LAST:event_lstPredefRulesListMouseClicked

    private void lstRoutinesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstRoutinesListMouseClicked
        if (evt.getClickCount() == 2) {
            String text = (String) lstRoutinesList.getSelectedValue();
            addToCode(text);
        }
}//GEN-LAST:event_lstRoutinesListMouseClicked

    private void lstPredefStrategiesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstPredefStrategiesListMouseClicked
        if (evt.getClickCount() == 2) {
            String text = (String) lstPredefStrategiesList.getSelectedValue();
            addToCode(text);
        }
    }//GEN-LAST:event_lstPredefStrategiesListMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cmbxDefaultStrategy;
    private javax.swing.JDialog dlgEdit;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblCode;
    private javax.swing.JLabel lblCode1;
    private javax.swing.JLabel lblComments;
    private javax.swing.JLabel lblComments1;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblName1;
    private javax.swing.JLabel lblShowHideComments;
    private javax.swing.JList lstPredefRulesList;
    private javax.swing.JList lstPredefStrategiesList;
    private javax.swing.JList lstRoutinesList;
    private javax.swing.JList lstStrategiesList;
    private javax.swing.JPanel pnlAED;
    private javax.swing.JPanel pnlDefaultStrategy;
    private javax.swing.JPanel pnlPredefRulesList;
    private javax.swing.JPanel pnlSaveCancel;
    private javax.swing.JPanel pnlSelectedStrategy;
    private javax.swing.JPanel pnlSelectedStrategy1;
    private javax.swing.JPanel pnlStrategiesList;
    private javax.swing.JScrollPane scrlCode;
    private javax.swing.JScrollPane scrlCode1;
    private javax.swing.JScrollPane scrlComments;
    private javax.swing.JScrollPane scrlComments1;
    private javax.swing.JScrollPane scrlStrategiesList;
    private javax.swing.JTextArea txaCode;
    private javax.swing.JTextArea txaCodeIn;
    private javax.swing.JTextArea txaComments;
    private javax.swing.JTextArea txaCommentsIn;
    private javax.swing.JTextField txfName;
    private javax.swing.JTextField txfNameIn;
    // End of variables declaration//GEN-END:variables
}
