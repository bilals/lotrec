/*
 * RulesTabPanel.java
 *
 * Created on 30 octobre 2007, 11:28
 */
package lotrec.gui.logicspane;

import java.awt.Point;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.InputMap;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.TransferHandler;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.StringSchemeVariable;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.ParameterType;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.gui.DialogsFactory;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;
import lotrec.process.AbstractAction;
import lotrec.resources.ResourcesProvider;

/**
 *
 * @author  said
 */
public class RulesTabPanel extends javax.swing.JPanel {

    private LogicDefTab logicDefTab;
    private java.util.ResourceBundle resource;
    private int lastSelected = -1;// changed only inside the valueChanged method
    private int editedRuleNum; //-1 if new rule added    
    private Rule editedRule;
    private int lastSelectedCond = -1;// changed only inside the valueChanged method
    private int editedCondNum; //-1 if new Condition added
    private int lastSelectedAct = -1;// changed only inside the valueChanged method
    private int editedActNum; //-1 if new Action added    
    private String condArgDesc1;
    private String condArgDesc2;
    private String condArgDesc3;
    private String condArgDesc4;
    private String actArgDesc1;
    private String actArgDesc2;
    private String actArgDesc3;
    private boolean wasSourceOfDrag;

    /** Creates new form RulesTabPanel */
    public RulesTabPanel() {
        initComponents();
    }

    public RulesTabPanel(LogicDefTab logicDefTab) {
        this.logicDefTab = logicDefTab;
        initComponents();
        //Display the rules in the JList
        refreshRulesList();

        //to select the first row & fire the first display event
        if (logicDefTab.getLogic().getRules().size() > 0) {
            lstRulesList.setSelectedIndex(0);
        }
        displaySelectedRule();
        setFieldsEditable(false);
        RulesListTransferHandler ruleListTransferHandler = new RulesListTransferHandler(this);
        this.lstRulesList.setTransferHandler(ruleListTransferHandler);
        this.setMappings(lstRulesList);
        lstRulesList.setDropMode(DropMode.INSERT);
    }


    // Used by RulesListTransferHandler
    // To determine if a MOVE action can be performed by Drag and Drop
    public boolean wasSourceOfDrag() {
        return this.wasSourceOfDrag;
    }

    public void setWasSourceOfDrag(boolean b) {
        this.wasSourceOfDrag = b;
    }

    public void refreshRulesList() {
        //Display the rules in the JList
        DefaultListModel listModel = new DefaultListModel();
        for (int i = 0; i < logicDefTab.getLogic().getRules().size(); i++) {
            listModel.addElement(((Rule) logicDefTab.getLogic().getRules().get(i)).getName());
        }
        lstRulesList.setModel(listModel);
        if (logicDefTab.getLogic().getRules().size() == 0) {
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } else {
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    public void refreshConditionsList(Rule rule) {
        DefaultListModel listModel = new DefaultListModel();
        if (rule != null) {
            for (int i = 0; i < rule.getConditions().size(); i++) {
                listModel.addElement(((AbstractCondition) rule.getConditions().get(i)).getCode());
            }
        }
        lstConditionsList.setModel(listModel);
    }

    public void refreshActionsList(Rule rule) {
        DefaultListModel listModel = new DefaultListModel();
        if (rule != null) {
            for (int i = 0; i < rule.getActions().size(); i++) {
                listModel.addElement(((AbstractAction) rule.getActions().get(i)).getCode());
            }
        }
        lstActionsList.setModel(listModel);
    }

    public void displaySelectedRule() {
        if (lastSelected == -1) {
            txfRuleName.setText("");
//            lblCommutative.setVisible(false);
            txaComments.setText("");
            hideComments();
            refreshConditionsList(null);
            refreshActionsList(null);
        } else {
            Rule rule = (Rule) logicDefTab.getLogic().getRules().get(lastSelected);
            txfRuleName.setText(rule.getName());
//            if (rule.isCommutative()) {
//                lblCommutative.setVisible(true);
//            } else {
//                lblCommutative.setVisible(false);
//            }
//            chkbxCommutative.setSelected(rule.isCommutative());
            txaComments.setText(rule.getComment());
            txaComments.setCaretPosition(0);
            if (txaComments.getText().equals("")) {
                hideComments();
            } else {
                showComments();
            }
            refreshConditionsList(rule);
            refreshActionsList(rule);
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
        txfRuleName.setEditable(yes);
//        chkbxCommutative.setEnabled(yes);
        txaComments.setEditable(yes);
    }

    public void refresh() {
        resource = java.util.ResourceBundle.getBundle("lotrec.resources.LoadedLogicsPanel", ResourcesProvider.getCurrentLocale());
        pnlRulesList.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("RulTab.RulesList")));
        pnlSelectedRule.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("RulTab.SelectedRule")));
        lblName.setText(resource.getString("RulTab.Titles.Name"));
        lblConditions.setText(resource.getString("RulTab.Titles.Conditions"));
        lblActions.setText(resource.getString("RulTab.Titles.Actions"));
//        chkbxCommutative.setText(resource.getString("RulTab.Titles.Commutative"));
        lblComments.setText(resource.getString("RulTab.Titles.Comments"));
    }

    private void deleteRule() {
        if (lastSelected == -1) {
//            System.out.println("Rules list most likely empty.. no Rule to be deleted..");
//        } else if (logic.getRules().size() == 1) {
            // CAUTION!!
            // last rule
        } else {
            Rule rule = (Rule) logicDefTab.getLogic().getRules().get(lastSelected);
            //--> display WARNING
            //if ok
            //  WE MUST ASK THE IMPACT VERIFIER FIRST!!
            //   --> delete from lstRulesList
            //   --> delete it really from the logic
            if (logicDefTab.getLogic().isRuleCalledInStrategies(rule.getName())) {
                DialogsFactory.semanticErrorMessage(pnlSelectedRule, "The rule '" + rule.getName() + "' could not be deleted!\n" +
                        "The rule you are deleting is already called by one or more strategies.");
                return;
            }
            int choice = DialogsFactory.deleteDialog(pnlSelectedRule, "the rule '" + rule.getName() + "'");
            if (choice == 0) {
//                System.out.println("Rule " + rule.getName() + " will be deleted");
                logicDefTab.getLogic().removeRule(rule);
                logicDefTab.setModifiedAndNotSaved(true);
                int selectionIndex;
                if (lastSelected == 0) {
                    //when the first rule is deleted
                    //show the one coming to its place                
                    selectionIndex = lastSelected;
                } else {
                    //else, show the one before (always safer! ;))
                    selectionIndex = lastSelected - 1;
                }
                refreshRulesList();
                lstRulesList.setSelectedIndex(selectionIndex);
            } else {
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
        String newName;
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(logicDefTab.getLogic());
        oldiesTokenizer.initializeTokenizerAndProps();
        oldiesTokenizer.setSource(txfRuleNameIn.getText());
        try {
            newName = oldiesTokenizer.readStringToken();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given rule name raised the following parser exception:\n\n" +
                    ex.getMessage());
            return;
        }
        try {
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            JOptionPane.showMessageDialog(dlgEdit,
                    "It seems that the given name has some extra text at the end.\n" +
                    "LoTREC will suppose being given this name: '" + newName + "' and will ignore the rest.\n\n" +
                    "Raised exception:\n" +
                    ex.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (logicDefTab.getLogic().isRoutineName(newName)) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given rule name is a reserved keyword for routines.\n" +
                    "Please choose a name diffrent than 'allRules', 'firstRule' and 'repeat'.");
            return;
        }
        if (logicDefTab.getLogic().isStrategyName(newName)) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given rule name is already identifying a strategy.\n" +
                    "Please choose a diffrent name.");
            return;
        }


        if (editedRuleNum == -1) {
            if (logicDefTab.getLogic().isRuleName(newName)) {
                DialogsFactory.syntaxErrorMessage(dlgEdit, "The given rule name is already identifying another rule.\n" +
                        "Please choose a diffrent name.");
                return;
            }
            editedRule.setName(newName);
//            editedRule.setCommutative(chkbxCommutativeIn.isSelected());
            editedRule.setComment(txaCommentsIn.getText());
//            System.out.println("new Rule will be added:" + txfRuleNameIn.getText());
            logicDefTab.getLogic().addRule(editedRule);
            logicDefTab.setModifiedAndNotSaved(true);
            cancelEditionDialog(logicDefTab.getLogic().getRules().size() - 1);
        } else {
            if (logicDefTab.getLogic().isOtherRuleName(editedRule.getName(), newName)) {
                DialogsFactory.syntaxErrorMessage(dlgEdit, "The given rule name is already identifying another rule.\n" +
                        "Please choose a diffrent name.");
                return;
            }
            String oldRuleName = editedRule.getName();
            editedRule.setName(newName);
//            editedRule.setCommutative(chkbxCommutativeIn.isSelected());
            editedRule.setComment(txaCommentsIn.getText());
            if (logicDefTab.getLogic().isRuleCalledInStrategies(oldRuleName)) {
//                JOptionPane.showMessageDialog(dlgEdit,
//                        "The edited Rule '" + oldRuleName + "' is called by one or more strategies.\n" +
//                        "LoTREC will take care of replacing it appropriately.",// by '" + editedRule.getName() + "'.\n",
//                        "Warning",
//                        JOptionPane.WARNING_MESSAGE);
                try {
                    logicDefTab.getLogic().replaceRuleCalls(oldRuleName, editedRule);
                } catch (RuntimeException ex) { //that may be caused by parsing the rule during rule.CreateMachine();
                    DialogsFactory.ruleDefinitionErrorMessage(dlgEdit, ex.getMessage());
                    return;
                }
                int indexOfSelection = logicDefTab.getMainFrame().
                        getLoadedLogicsPanel().getSelectedLogicDefTab().
                        getStratTabPanel().getLastSelectedIndex();
                logicDefTab.getMainFrame().getLoadedLogicsPanel().
                        getSelectedLogicDefTab().getStratTabPanel().
                        refreshStrategiesList();
                logicDefTab.getMainFrame().getLoadedLogicsPanel().
                        getSelectedLogicDefTab().getStratTabPanel().
                        refreshSelection(indexOfSelection);
//                logicDefTab.getMainFrame().getControlsPanel().refreshSteps(); // Not sure this is necessary!!
            }
            logicDefTab.setModifiedAndNotSaved(true);
//            System.out.println("Rule " + oldRuleName + " will be saved as: " + editedRule.getName());
//            logic.getRules().setElementAt(editedRule, editedRuleNum);
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

    public void refreshConditionsListIn() {
        DefaultListModel listModel = new DefaultListModel();
        if (editedRule != null) {
            for (int i = 0; i < editedRule.getConditions().size(); i++) {
                listModel.addElement(((AbstractCondition) editedRule.getConditions().get(i)).getCode());
            }
            if (editedRule.getConditions().size() == 0) {
                btnEditCond.setEnabled(false);
                btnDeleteCond.setEnabled(false);
            } else {
                btnEditCond.setEnabled(true);
                btnDeleteCond.setEnabled(true);
            }
        }
        lstConditionsListIn.setModel(listModel);
    }

    public void refreshActionsListIn() {
        DefaultListModel listModel = new DefaultListModel();
        if (editedRule != null) {
            for (int i = 0; i < editedRule.getActions().size(); i++) {
                listModel.addElement(((AbstractAction) editedRule.getActions().get(i)).getCode());
            }
            if (editedRule.getActions().size() == 0) {
                btnEditAct.setEnabled(false);
                btnDeleteAct.setEnabled(false);
            } else {
                btnEditAct.setEnabled(true);
                btnDeleteAct.setEnabled(true);
            }
        }
        lstActionsListIn.setModel(listModel);
    }

    private void displayEditedRule() {
        if (editedRule == null) {
            txfRuleNameIn.setText("");
//            chkbxCommutativeIn.setSelected(false);
            txaCommentsIn.setText("");
        } else {
            txfRuleNameIn.setText(editedRule.getName());
//            chkbxCommutativeIn.setSelected(editedRule.isCommutative());
            txaCommentsIn.setText(editedRule.getComment());
            txaCommentsIn.setCaretPosition(0);
        }
        refreshConditionsListIn();
        refreshActionsListIn();
    }

    private void showEditionDialog() {
        if (editedRuleNum != -1) {
            dlgEdit.setTitle("Edit Rule");
        } else {
            dlgEdit.setTitle("New Rule");
        }
        displayEditedRule();
        //It seems that there is no problem
        //even if there is no condition (action)!
        lstConditionsListIn.setSelectedIndex(0);
        lstActionsListIn.setSelectedIndex(0);
        dlgEdit.pack();
        dlgEdit.setLocation(pnlSelectedRule.getLocationOnScreen());
        dlgEdit.setVisible(true);
    }

    private void cancelEditionDialog(int selectionIndex) {
        dlgEdit.dispose();
        refreshRulesList();
        lstRulesList.setSelectedIndex(selectionIndex);
//        System.out.println("At Cancel: lastSelected equals " + lastSelected);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        dlgDescription = new javax.swing.JDialog();
        lblDesc = new javax.swing.JLabel();
        lblDescTitle = new javax.swing.JLabel();
        pnlDescJunk = new javax.swing.JPanel();
        dlgEdit = new javax.swing.JDialog();
        pnlEditedRule = new javax.swing.JPanel();
        lblName1 = new javax.swing.JLabel();
        lblConditions1 = new javax.swing.JLabel();
        lblActions1 = new javax.swing.JLabel();
        lblComments1 = new javax.swing.JLabel();
        txfRuleNameIn = new javax.swing.JTextField();
        scrlComments1 = new javax.swing.JScrollPane();
        txaCommentsIn = new javax.swing.JTextArea();
        scrlConditionsList1 = new javax.swing.JScrollPane();
        lstConditionsListIn = new javax.swing.JList();
        scrlActionsList1 = new javax.swing.JScrollPane();
        lstActionsListIn = new javax.swing.JList();
        pnlSaveCancel = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlAEDCond = new javax.swing.JPanel();
        btnAddCond = new javax.swing.JButton();
        btnEditCond = new javax.swing.JButton();
        btnDeleteCond = new javax.swing.JButton();
        pnlAEDAct = new javax.swing.JPanel();
        btnAddAct = new javax.swing.JButton();
        btnEditAct = new javax.swing.JButton();
        btnDeleteAct = new javax.swing.JButton();
        dlgEditCond = new javax.swing.JDialog();
        pnlCond = new javax.swing.JPanel();
        pnlCondArgs = new javax.swing.JPanel();
        lblCondArg1 = new javax.swing.JLabel();
        lblCondArg2 = new javax.swing.JLabel();
        txfCondArgVal1 = new javax.swing.JTextField();
        txfCondArgVal2 = new javax.swing.JTextField();
        lblCondArgDesc1 = new javax.swing.JLabel();
        lblCondArgDesc2 = new javax.swing.JLabel();
        lblCondArg3 = new javax.swing.JLabel();
        txfCondArgVal3 = new javax.swing.JTextField();
        lblCondArgDesc3 = new javax.swing.JLabel();
        lblCondArg4 = new javax.swing.JLabel();
        txfCondArgVal4 = new javax.swing.JTextField();
        lblCondArgDesc4 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        pnlCondSaveCancel = new javax.swing.JPanel();
        btnSaveCond = new javax.swing.JButton();
        btnCancelCond = new javax.swing.JButton();
        cmbxConditionNames = new javax.swing.JComboBox();
        dlgEditAct = new javax.swing.JDialog();
        pnlCond1 = new javax.swing.JPanel();
        pnlCondArgs1 = new javax.swing.JPanel();
        lblActArg1 = new javax.swing.JLabel();
        lblActArg2 = new javax.swing.JLabel();
        txfActArgVal1 = new javax.swing.JTextField();
        txfActArgVal2 = new javax.swing.JTextField();
        lblActArgDesc1 = new javax.swing.JLabel();
        lblActArgDesc2 = new javax.swing.JLabel();
        lblActArg3 = new javax.swing.JLabel();
        txfActArgVal3 = new javax.swing.JTextField();
        lblActArgDesc3 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        pnlCondSaveCancel1 = new javax.swing.JPanel();
        btnSaveAct = new javax.swing.JButton();
        btnCancelAct = new javax.swing.JButton();
        cmbxActionNames = new javax.swing.JComboBox();
        pnlSelectedRule = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblConditions = new javax.swing.JLabel();
        lblActions = new javax.swing.JLabel();
        lblComments = new javax.swing.JLabel();
        txfRuleName = new javax.swing.JTextField();
        scrlComments = new javax.swing.JScrollPane();
        txaComments = new javax.swing.JTextArea();
        scrlConditionsList = new javax.swing.JScrollPane();
        lstConditionsList = new javax.swing.JList();
        scrlActionsList = new javax.swing.JScrollPane();
        lstActionsList = new javax.swing.JList();
        lblShowHideComments = new javax.swing.JLabel();
        pnlRulesList = new javax.swing.JPanel();
        pnlAED = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        scrlRulesList = new javax.swing.JScrollPane();
        lstRulesList = new javax.swing.JList();

        dlgDescription.setTitle("Description");
        dlgDescription.setAlwaysOnTop(true);
        dlgDescription.setModal(true);
        dlgDescription.setResizable(false);
        dlgDescription.setUndecorated(true);

        lblDesc.setBackground(new java.awt.Color(255, 255, 255));
        lblDesc.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDesc.setText("<html>This node argument will be used as an identifier of the ansector node. This node argument will be used as an identifier of the ansector node. This node argument will be used as an identifier of the ansector node.</html>:");
        lblDesc.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblDesc.setOpaque(true);
        lblDesc.setPreferredSize(new java.awt.Dimension(280, 60));
        lblDesc.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDescMouseClicked(evt);
            }
        });
        dlgDescription.getContentPane().add(lblDesc, java.awt.BorderLayout.CENTER);

        lblDescTitle.setBackground(new java.awt.Color(255, 255, 255));
        lblDescTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/idea.PNG"))); // NOI18N
        lblDescTitle.setText("<html><h4>Parameter Description:</h4></html>:");
        lblDescTitle.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        lblDescTitle.setOpaque(true);
        lblDescTitle.setPreferredSize(new java.awt.Dimension(280, 30));
        lblDescTitle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblDescTitleMouseClicked(evt);
            }
        });
        dlgDescription.getContentPane().add(lblDescTitle, java.awt.BorderLayout.NORTH);

        pnlDescJunk.setBackground(new java.awt.Color(255, 255, 255));
        pnlDescJunk.setPreferredSize(new java.awt.Dimension(20, 60));
        pnlDescJunk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pnlDescJunkMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlDescJunkLayout = new org.jdesktop.layout.GroupLayout(pnlDescJunk);
        pnlDescJunk.setLayout(pnlDescJunkLayout);
        pnlDescJunkLayout.setHorizontalGroup(
            pnlDescJunkLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 20, Short.MAX_VALUE)
        );
        pnlDescJunkLayout.setVerticalGroup(
            pnlDescJunkLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 61, Short.MAX_VALUE)
        );

        dlgDescription.getContentPane().add(pnlDescJunk, java.awt.BorderLayout.LINE_START);

        dlgEdit.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dlgEdit.setTitle("Edit Rule");
        dlgEdit.setAlwaysOnTop(true);
        dlgEdit.setModal(true);
        dlgEdit.setResizable(false);
        dlgEdit.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgEditWindowClosing(evt);
            }
        });

        pnlEditedRule.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlEditedRule.setOpaque(false);

        lblName1.setText("Name");

        lblConditions1.setText("Conditions");

        lblActions1.setText("Actions");

        lblComments1.setText("Comments");

        txfRuleNameIn.setText("<Name>");

        txaCommentsIn.setColumns(20);
        txaCommentsIn.setLineWrap(true);
        txaCommentsIn.setRows(5);
        txaCommentsIn.setWrapStyleWord(true);
        txaCommentsIn.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlComments1.setViewportView(txaCommentsIn);

        lstConditionsListIn.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstConditionsListIn.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstConditionsListIn.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstConditionsListInValueChanged(evt);
            }
        });
        lstConditionsListIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstConditionsListInMouseClicked(evt);
            }
        });
        scrlConditionsList1.setViewportView(lstConditionsListIn);

        lstActionsListIn.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstActionsListIn.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstActionsListIn.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstActionsListInValueChanged(evt);
            }
        });
        lstActionsListIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstActionsListInMouseClicked(evt);
            }
        });
        scrlActionsList1.setViewportView(lstActionsListIn);

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

        pnlAEDCond.setOpaque(false);
        pnlAEDCond.setLayout(new java.awt.GridBagLayout());

        btnAddCond.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cond-act-add.png"))); // NOI18N
        btnAddCond.setText("Add");
        btnAddCond.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAddCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddCondActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        pnlAEDCond.add(btnAddCond, gridBagConstraints);

        btnEditCond.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cond-act-edit.png"))); // NOI18N
        btnEditCond.setText("Edit");
        btnEditCond.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnEditCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditCondActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlAEDCond.add(btnEditCond, gridBagConstraints);

        btnDeleteCond.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cond-act-remove.png"))); // NOI18N
        btnDeleteCond.setText("Delete");
        btnDeleteCond.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDeleteCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteCondActionPerformed(evt);
            }
        });
        pnlAEDCond.add(btnDeleteCond, new java.awt.GridBagConstraints());

        pnlAEDAct.setOpaque(false);
        pnlAEDAct.setLayout(new java.awt.GridBagLayout());

        btnAddAct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cond-act-add.png"))); // NOI18N
        btnAddAct.setText("Add");
        btnAddAct.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnAddAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        pnlAEDAct.add(btnAddAct, gridBagConstraints);

        btnEditAct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cond-act-edit.png"))); // NOI18N
        btnEditAct.setText("Edit");
        btnEditAct.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnEditAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        pnlAEDAct.add(btnEditAct, gridBagConstraints);

        btnDeleteAct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cond-act-remove.png"))); // NOI18N
        btnDeleteAct.setText("Delete");
        btnDeleteAct.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnDeleteAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActActionPerformed(evt);
            }
        });
        pnlAEDAct.add(btnDeleteAct, new java.awt.GridBagConstraints());

        org.jdesktop.layout.GroupLayout pnlEditedRuleLayout = new org.jdesktop.layout.GroupLayout(pnlEditedRule);
        pnlEditedRule.setLayout(pnlEditedRuleLayout);
        pnlEditedRuleLayout.setHorizontalGroup(
            pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedRuleLayout.createSequentialGroup()
                .add(pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlEditedRuleLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlComments1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE))
                    .add(pnlEditedRuleLayout.createSequentialGroup()
                        .add(pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblConditions1)
                            .add(lblActions1)
                            .add(lblName1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(scrlConditionsList1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, scrlActionsList1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                            .add(pnlEditedRuleLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(pnlAEDCond, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE))
                            .add(pnlEditedRuleLayout.createSequentialGroup()
                                .add(10, 10, 10)
                                .add(pnlAEDAct, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE))
                            .add(txfRuleNameIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)))
                    .add(lblComments1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSaveCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlEditedRuleLayout.setVerticalGroup(
            pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedRuleLayout.createSequentialGroup()
                .add(pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName1)
                    .add(txfRuleNameIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(scrlConditionsList1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblConditions1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAEDCond, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlEditedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(scrlActionsList1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblActions1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAEDAct, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblComments1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlComments1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlSaveCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout dlgEditLayout = new org.jdesktop.layout.GroupLayout(dlgEdit.getContentPane());
        dlgEdit.getContentPane().setLayout(dlgEditLayout);
        dlgEditLayout.setHorizontalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedRule, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgEditLayout.setVerticalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlEditedRule, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dlgEditCond.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dlgEditCond.setTitle("Edit Condition");
        dlgEditCond.setAlwaysOnTop(true);
        dlgEditCond.setModal(true);
        dlgEditCond.setResizable(false);
        dlgEditCond.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgEditCondWindowClosing(evt);
            }
        });

        pnlCond.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlCond.setOpaque(false);

        pnlCondArgs.setOpaque(false);
        pnlCondArgs.setLayout(new java.awt.GridBagLayout());

        lblCondArg1.setText("arg 1");
        lblCondArg1.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 10;
        pnlCondArgs.add(lblCondArg1, gridBagConstraints);

        lblCondArg2.setText("arg 2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 10;
        pnlCondArgs.add(lblCondArg2, gridBagConstraints);

        txfCondArgVal1.setPreferredSize(new java.awt.Dimension(100, 19));
        txfCondArgVal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfCondArgVal1ActionPerformed(evt);
            }
        });
        txfCondArgVal1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txfCondArgVal1FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        pnlCondArgs.add(txfCondArgVal1, gridBagConstraints);

        txfCondArgVal2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfCondArgVal2ActionPerformed(evt);
            }
        });
        txfCondArgVal2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txfCondArgVal2FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        pnlCondArgs.add(txfCondArgVal2, gridBagConstraints);

        lblCondArgDesc1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/info.png"))); // NOI18N
        lblCondArgDesc1.setToolTipText("Parameter Description");
        lblCondArgDesc1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCondArgDesc1MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlCondArgs.add(lblCondArgDesc1, gridBagConstraints);

        lblCondArgDesc2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/info.png"))); // NOI18N
        lblCondArgDesc2.setToolTipText("Parameter Description");
        lblCondArgDesc2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCondArgDesc2MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlCondArgs.add(lblCondArgDesc2, gridBagConstraints);

        lblCondArg3.setText("arg 3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 10;
        pnlCondArgs.add(lblCondArg3, gridBagConstraints);

        txfCondArgVal3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfCondArgVal3ActionPerformed(evt);
            }
        });
        txfCondArgVal3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txfCondArgVal3FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        pnlCondArgs.add(txfCondArgVal3, gridBagConstraints);

        lblCondArgDesc3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/info.png"))); // NOI18N
        lblCondArgDesc3.setToolTipText("Parameter Description");
        lblCondArgDesc3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCondArgDesc3MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlCondArgs.add(lblCondArgDesc3, gridBagConstraints);

        lblCondArg4.setText("arg 4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 10;
        pnlCondArgs.add(lblCondArg4, gridBagConstraints);

        txfCondArgVal4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txfCondArgVal4FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        pnlCondArgs.add(txfCondArgVal4, gridBagConstraints);

        lblCondArgDesc4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/info.png"))); // NOI18N
        lblCondArgDesc4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCondArgDesc4MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlCondArgs.add(lblCondArgDesc4, gridBagConstraints);

        jLabel9.setText("Name");

        jLabel10.setText("Parameters:");

        pnlCondSaveCancel.setLayout(new java.awt.GridBagLayout());

        btnSaveCond.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/check-16.png"))); // NOI18N
        btnSaveCond.setMnemonic('S');
        btnSaveCond.setText("Save");
        btnSaveCond.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSaveCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveCondActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        pnlCondSaveCancel.add(btnSaveCond, gridBagConstraints);

        btnCancelCond.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cross-16.png"))); // NOI18N
        btnCancelCond.setMnemonic('C');
        btnCancelCond.setText("Cancel");
        btnCancelCond.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCancelCond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelCondActionPerformed(evt);
            }
        });
        pnlCondSaveCancel.add(btnCancelCond, new java.awt.GridBagConstraints());

        cmbxConditionNames.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbxConditionNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxConditionNamesActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlCondLayout = new org.jdesktop.layout.GroupLayout(pnlCond);
        pnlCond.setLayout(pnlCondLayout);
        pnlCondLayout.setHorizontalGroup(
            pnlCondLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCondLayout.createSequentialGroup()
                .add(pnlCondLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCondLayout.createSequentialGroup()
                        .add(jLabel9)
                        .add(52, 52, 52)
                        .add(cmbxConditionNames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 178, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel10))
                .addContainerGap(45, Short.MAX_VALUE))
            .add(pnlCondSaveCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
            .add(pnlCondLayout.createSequentialGroup()
                .add(pnlCondArgs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlCondLayout.setVerticalGroup(
            pnlCondLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCondLayout.createSequentialGroup()
                .add(pnlCondLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(cmbxConditionNames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(11, 11, 11)
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCondArgs, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlCondSaveCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout dlgEditCondLayout = new org.jdesktop.layout.GroupLayout(dlgEditCond.getContentPane());
        dlgEditCond.getContentPane().setLayout(dlgEditCondLayout);
        dlgEditCondLayout.setHorizontalGroup(
            dlgEditCondLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCond, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgEditCondLayout.setVerticalGroup(
            dlgEditCondLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCond, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        dlgEditAct.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dlgEditAct.setTitle("Edit Action");
        dlgEditAct.setAlwaysOnTop(true);
        dlgEditAct.setModal(true);
        dlgEditAct.setResizable(false);
        dlgEditAct.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgEditActWindowClosing(evt);
            }
        });

        pnlCond1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlCond1.setOpaque(false);

        pnlCondArgs1.setOpaque(false);
        pnlCondArgs1.setLayout(new java.awt.GridBagLayout());

        lblActArg1.setText("arg 1");
        lblActArg1.setPreferredSize(new java.awt.Dimension(40, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 10;
        pnlCondArgs1.add(lblActArg1, gridBagConstraints);

        lblActArg2.setText("arg 2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 10;
        pnlCondArgs1.add(lblActArg2, gridBagConstraints);

        txfActArgVal1.setPreferredSize(new java.awt.Dimension(100, 19));
        txfActArgVal1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfActArgVal1ActionPerformed(evt);
            }
        });
        txfActArgVal1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txfActArgVal1FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        pnlCondArgs1.add(txfActArgVal1, gridBagConstraints);

        txfActArgVal2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txfActArgVal2FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        pnlCondArgs1.add(txfActArgVal2, gridBagConstraints);

        lblActArgDesc1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/info.png"))); // NOI18N
        lblActArgDesc1.setToolTipText("Parameter Description");
        lblActArgDesc1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblActArgDesc1MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlCondArgs1.add(lblActArgDesc1, gridBagConstraints);

        lblActArgDesc2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/info.png"))); // NOI18N
        lblActArgDesc2.setToolTipText("Parameter Description");
        lblActArgDesc2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblActArgDesc2MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlCondArgs1.add(lblActArgDesc2, gridBagConstraints);

        lblActArg3.setText("arg 3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.ipady = 10;
        pnlCondArgs1.add(lblActArg3, gridBagConstraints);

        txfActArgVal3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfActArgVal3ActionPerformed(evt);
            }
        });
        txfActArgVal3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txfActArgVal3FocusGained(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 80;
        pnlCondArgs1.add(txfActArgVal3, gridBagConstraints);

        lblActArgDesc3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/info.png"))); // NOI18N
        lblActArgDesc3.setToolTipText("Parameter Description");
        lblActArgDesc3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblActArgDesc3MouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 8, 0, 0);
        pnlCondArgs1.add(lblActArgDesc3, gridBagConstraints);

        jLabel11.setText("Name");

        jLabel12.setText("Parameters:");

        pnlCondSaveCancel1.setLayout(new java.awt.GridBagLayout());

        btnSaveAct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/check-16.png"))); // NOI18N
        btnSaveAct.setMnemonic('S');
        btnSaveAct.setText("Save");
        btnSaveAct.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnSaveAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 8);
        pnlCondSaveCancel1.add(btnSaveAct, gridBagConstraints);

        btnCancelAct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/cross-16.png"))); // NOI18N
        btnCancelAct.setMnemonic('C');
        btnCancelAct.setText("Cancel");
        btnCancelAct.setMargin(new java.awt.Insets(2, 2, 2, 2));
        btnCancelAct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActActionPerformed(evt);
            }
        });
        pnlCondSaveCancel1.add(btnCancelAct, new java.awt.GridBagConstraints());

        cmbxActionNames.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbxActionNames.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxActionNamesActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlCond1Layout = new org.jdesktop.layout.GroupLayout(pnlCond1);
        pnlCond1.setLayout(pnlCond1Layout);
        pnlCond1Layout.setHorizontalGroup(
            pnlCond1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCond1Layout.createSequentialGroup()
                .add(pnlCond1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlCond1Layout.createSequentialGroup()
                        .add(jLabel11)
                        .add(55, 55, 55)
                        .add(cmbxActionNames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jLabel12)
                    .add(pnlCondArgs1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(pnlCondSaveCancel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE)
        );
        pnlCond1Layout.setVerticalGroup(
            pnlCond1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlCond1Layout.createSequentialGroup()
                .add(pnlCond1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(cmbxActionNames, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(11, 11, 11)
                .add(jLabel12)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlCondArgs1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlCondSaveCancel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout dlgEditActLayout = new org.jdesktop.layout.GroupLayout(dlgEditAct.getContentPane());
        dlgEditAct.getContentPane().setLayout(dlgEditActLayout);
        dlgEditActLayout.setHorizontalGroup(
            dlgEditActLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 318, Short.MAX_VALUE)
            .add(pnlCond1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        dlgEditActLayout.setVerticalGroup(
            dlgEditActLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 172, Short.MAX_VALUE)
            .add(pnlCond1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setOpaque(false);

        pnlSelectedRule.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Rule"));
        pnlSelectedRule.setOpaque(false);

        lblName.setText("Name");

        lblConditions.setText("Conditions");

        lblActions.setText("Actions");

        lblComments.setText("Comments");

        txfRuleName.setText("<Name>");
        txfRuleName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txfRuleNameActionPerformed(evt);
            }
        });

        txaComments.setColumns(20);
        txaComments.setLineWrap(true);
        txaComments.setRows(5);
        txaComments.setWrapStyleWord(true);
        txaComments.setMargin(new java.awt.Insets(1, 5, 2, 4));
        scrlComments.setViewportView(txaComments);

        lstConditionsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstConditionsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstConditionsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstConditionsListValueChanged(evt);
            }
        });
        scrlConditionsList.setViewportView(lstConditionsList);

        lstActionsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstActionsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstActionsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstActionsListValueChanged(evt);
            }
        });
        scrlActionsList.setViewportView(lstActionsList);

        lblShowHideComments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/show.png"))); // NOI18N
        lblShowHideComments.setToolTipText("Show/Hide Comments");
        lblShowHideComments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShowHideCommentsMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlSelectedRuleLayout = new org.jdesktop.layout.GroupLayout(pnlSelectedRule);
        pnlSelectedRule.setLayout(pnlSelectedRuleLayout);
        pnlSelectedRuleLayout.setHorizontalGroup(
            pnlSelectedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedRuleLayout.createSequentialGroup()
                .addContainerGap()
                .add(pnlSelectedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlSelectedRuleLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(scrlActionsList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                    .add(pnlSelectedRuleLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(scrlConditionsList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSelectedRuleLayout.createSequentialGroup()
                        .add(pnlSelectedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(pnlSelectedRuleLayout.createSequentialGroup()
                                .add(lblName)
                                .add(27, 27, 27))
                            .add(pnlSelectedRuleLayout.createSequentialGroup()
                                .add(lblConditions)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(txfRuleName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE))
                    .add(lblActions)
                    .add(pnlSelectedRuleLayout.createSequentialGroup()
                        .add(lblComments)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lblShowHideComments))
                    .add(scrlComments, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 357, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlSelectedRuleLayout.setVerticalGroup(
            pnlSelectedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedRuleLayout.createSequentialGroup()
                .add(pnlSelectedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txfRuleName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 19, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblConditions)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlConditionsList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblActions)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlActionsList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedRuleLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblComments)
                    .add(lblShowHideComments))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlComments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlRulesList.setBorder(javax.swing.BorderFactory.createTitledBorder("Rules List"));
        pnlRulesList.setOpaque(false);

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

        lstRulesList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstRulesList.setDragEnabled(true);
        lstRulesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstRulesListValueChanged(evt);
            }
        });
        lstRulesList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstRulesListMouseClicked(evt);
            }
        });
        scrlRulesList.setViewportView(lstRulesList);

        org.jdesktop.layout.GroupLayout pnlRulesListLayout = new org.jdesktop.layout.GroupLayout(pnlRulesList);
        pnlRulesList.setLayout(pnlRulesListLayout);
        pnlRulesListLayout.setHorizontalGroup(
            pnlRulesListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(scrlRulesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
        );
        pnlRulesListLayout.setVerticalGroup(
            pnlRulesListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlRulesListLayout.createSequentialGroup()
                .add(scrlRulesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAED, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlRulesList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedRule, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedRule, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnlRulesList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    private void lblShowHideCommentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblShowHideCommentsMouseClicked
        if (scrlComments.isVisible()) {
            hideComments();
        } else {
            showComments();
        }
    }//GEN-LAST:event_lblShowHideCommentsMouseClicked

    private void lblDescTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDescTitleMouseClicked
        dlgDescription.setVisible(false);
}//GEN-LAST:event_lblDescTitleMouseClicked

    private void lblDescMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDescMouseClicked
        dlgDescription.setVisible(false);
}//GEN-LAST:event_lblDescMouseClicked

    private void pnlDescJunkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDescJunkMouseClicked
        dlgDescription.setVisible(false);
}//GEN-LAST:event_pnlDescJunkMouseClicked

    private void txfRuleNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfRuleNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txfRuleNameActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        editedRuleNum = -1;
        editedRule = new Rule();
//        System.out.println("new Rule will be added");
        showEditionDialog();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        //This code must be changed all the way with lsRules mouse click
        editedRuleNum = lastSelected;
        editedRule = (Rule) logicDefTab.getLogic().getRules().get(editedRuleNum);
//        System.out.println("Rule " + editedRule.getName() + " will be edited");
        showEditionDialog();
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteRule();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void deleteCond() {
        if (lastSelectedCond == -1) {
//            System.out.println("Conditions list most likely empty.. no condition to be deleted..");
//        } else if (editedRule.getConditions().size() == 1) {
            // CAUTION!!
            // last condition
        } else {
            AbstractCondition cond = (AbstractCondition) editedRule.getConditions().get(lastSelectedCond);
            //--> display WARNING
            //if ok
            //  WE MUST ASK THE IMPACT VERIFIER FIRST!!
            //   --> delete it really from the editedRule
            //   --> delete from lstConditionsListIn
            int choice = DialogsFactory.deleteDialog(dlgEdit, "the condition '" + cond.getName() + "'");
            if (choice == 0) {
//                System.out.println("Condition " + cond.getName() + " will be deleted");
                editedRule.removeCondition(cond);
                int selectionIndex;
                if (lastSelectedCond == 0) {
                    //when the first condition is deleted
                    //show the one coming to its place                
                    selectionIndex = lastSelectedCond;
                } else {
                    //else, show the one before (always safer! ;))
                    selectionIndex = lastSelectedCond - 1;
                }
                refreshConditionsListIn();
                lstConditionsListIn.setSelectedIndex(selectionIndex);
            } else {
                return;
            }
        }
    }

    private String getCondParamValue(int i) {
        switch (i) {
            case 0:
                return txfCondArgVal1.getText();
            case 1:
                return txfCondArgVal2.getText();
            case 2:
                return txfCondArgVal3.getText();
            case 3:
                return txfCondArgVal4.getText();
            default:
                return null;
        }
    }

    private void saveCondEdition() {
        //Verify infos
        //if ok 
        //   --> Save info
        //   --> Cancel the Edition Dialog appropriately
        //else
        //   --> display the error
        //   --> remain in edition       
        String keyword = (String) cmbxConditionNames.getSelectedItem();
        String conditionClassName = AbstractCondition.CLASSES_KEYWORDS.get(keyword);
        StringSchemeVariable nodeArg = null;
        StringSchemeVariable node2Arg = null;
        Expression formulaArg = null;
        Expression formulaArg2 = null;
        Expression relationArg = null;
        String markArg = null;
        AbstractCondition editedCond = null;
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(logicDefTab.getLogic());
        oldiesTokenizer.initializeTokenizerAndProps();
        try {
            Class condClass = Class.forName(
                    AbstractCondition.CONDITIONS_PACKAGE + conditionClassName);
            Constructor constructor = condClass.getConstructors()[0];
//                Class[] paramClasses = constructor.getParameterTypes();
            ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
            Vector<Parameter> params = new Vector<Parameter>();
            boolean firstNode = true;
            boolean firstFormula = true;
            for (int i = 0; i < paramsTypes.types().length; i++) {
                String paramTypeStr = paramsTypes.types()[i];
                ParameterType paramType = ParameterType.getParameterType(paramTypeStr);
                String paramValStr = getCondParamValue(i);
                switch (paramType) {
                    case NODE:
                        if (firstNode) {
                            nodeArg = new StringSchemeVariable(paramValStr);
                            params.add(new Parameter(paramType, nodeArg));
                            firstNode = false;
                        } else {
                            node2Arg = new StringSchemeVariable(paramValStr);
                            params.add(new Parameter(paramType, node2Arg));
                        }
                        break;
                    case FORMULA:
                        if (firstFormula) {
                            try {
                                formulaArg = oldiesTokenizer.parseExpression(paramValStr);
                                oldiesTokenizer.verifyCodeEnd();
                            } catch (ParseException ex) {
                                DialogsFactory.syntaxErrorMessage(dlgEditCond, "The given formula parameter raised the following parser exception:\n\n" +
                                        ex.getMessage());
                                return;
                            }
                            params.add(new Parameter(paramType, formulaArg));
                            firstFormula = false;

                        } else {
                            try {
                                formulaArg2 = oldiesTokenizer.parseExpression(paramValStr);
                                oldiesTokenizer.verifyCodeEnd();
                            } catch (ParseException ex) {
                                DialogsFactory.syntaxErrorMessage(dlgEditCond, "The given formula parameter raised the following parser exception:\n\n" +
                                        ex.getMessage());
                                return;
                            }
                            params.add(new Parameter(paramType, formulaArg2));
                        }
                        break;
                    case RELATION:
                        try {
                            relationArg = oldiesTokenizer.parseExpression(paramValStr);
                            oldiesTokenizer.verifyCodeEnd();
                        } catch (ParseException ex) {
                            DialogsFactory.syntaxErrorMessage(dlgEditCond, "The given relation parameter raised the following parser exception:\n\n" +
                                    ex.getMessage());
                            return;
                        }
                        params.add(new Parameter(paramType, relationArg));
                        break;
                    case MARK:
                        markArg = paramValStr;
                        params.add(new Parameter(paramType, markArg));
                        break;
                }
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
            }
            int paramsNum = params.size();
            switch (paramsNum) {
                case 1:
                    editedCond = (AbstractCondition) constructor.newInstance(
                            params.get(0).getValue());
                    break;
                case 2:
                    editedCond = (AbstractCondition) constructor.newInstance(
                            params.get(0).getValue(),
                            params.get(1).getValue());
                    break;
                case 3:
                    editedCond = (AbstractCondition) constructor.newInstance(
                            params.get(0).getValue(),
                            params.get(1).getValue(),
                            params.get(2).getValue());
                    break;
                case 4:
                    editedCond = (AbstractCondition) constructor.newInstance(
                            params.get(0).getValue(),
                            params.get(1).getValue(),
                            params.get(2).getValue(),
                            params.get(2).getValue());
                    break;
            }
//            System.out.println("editedCond class name is: " + editedCond.getClass().getSimpleName());
            for (Parameter p : params) {
                editedCond.addParameter(p);
            }
            editedCond.setItsRule(editedRule);
            editedCond.setName(keyword);
//            System.out.println("editedCond code is: " + editedCond.getCode());
        } catch (InstantiationException ex) {
            System.out.println(ex);
        } catch (IllegalAccessException ex) {
            System.out.println(ex);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
        } catch (InvocationTargetException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        if (editedCondNum == -1) {
//            System.out.println("new Condition: '" + editedCond.getCode() + "' will be added");
            editedRule.addCondition(editedCond);
            cancelCondEditionDialog(editedRule.getConditions().size() - 1);
        } else {
//            AbstractCondition cond = (AbstractCondition) editedRule.getConditions().get(editedCondNum);
//            System.out.println("Condition '" + editedCond.getCode() + "' will be saved");
            editedRule.getConditions().setElementAt(editedCond, editedCondNum);
            cancelCondEditionDialog(lastSelectedCond);
        }
    }

    private void cancelCondEdition() {
        int n = DialogsFactory.cancelDialog(dlgEditCond);
        if (n == 0) {
            cancelCondEditionDialog(lastSelectedCond);
        } else {
            // jsut the dlgEditCon will be brought back again..
        }
    }

    private void setVisibleCondArg1(boolean yes) {
        lblCondArg1.setVisible(yes);
        txfCondArgVal1.setVisible(yes);
        lblCondArgDesc1.setVisible(yes);
    }

    private void setVisibleCondArg2(boolean yes) {
        lblCondArg2.setVisible(yes);
        txfCondArgVal2.setVisible(yes);
        lblCondArgDesc2.setVisible(yes);
    }

    private void setVisibleCondArg3(boolean yes) {
        lblCondArg3.setVisible(yes);
        txfCondArgVal3.setVisible(yes);
        lblCondArgDesc3.setVisible(yes);
    }

    private void setVisibleCondArg4(boolean yes) {
        lblCondArg4.setVisible(yes);
        txfCondArgVal4.setVisible(yes);
        lblCondArgDesc4.setVisible(yes);
    }

    private void setVisibleCondArg(int argNum, boolean yes) {
        switch (argNum) {
            case 0:
                setVisibleCondArg1(yes);
                break;
            case 1:
                setVisibleCondArg2(yes);
                break;
            case 2:
                setVisibleCondArg3(yes);
                break;
            case 3:
                setVisibleCondArg4(yes);
                break;
        }
    }

    private void setCondArgLabelValDesc(int argNum, String label, String val, String desc) {
        switch (argNum) {
            case 0:
                lblCondArg1.setText(label);
                txfCondArgVal1.setText(val);
                condArgDesc1 = "<html>" + desc + "</html>";
                break;
            case 1:
                lblCondArg2.setText(label);
                txfCondArgVal2.setText(val);
                condArgDesc2 = "<html>" + desc + "</html>";
                break;
            case 2:
                lblCondArg3.setText(label);
                txfCondArgVal3.setText(val);
                condArgDesc3 = "<html>" + desc + "</html>";
                break;
            case 3:
                lblCondArg4.setText(label);
                txfCondArgVal4.setText(val);
                condArgDesc4 = "<html>" + desc + "</html>";
                break;
        }
    }

    private void fillConditionParameters(String keyword) {
        for (int i = 0; i < 4; i++) {
            setVisibleCondArg(i, false);
        }
        String conditionClassName = AbstractCondition.CLASSES_KEYWORDS.get(keyword);
        try {
            Class condClass = Class.forName(
                    AbstractCondition.CONDITIONS_PACKAGE + conditionClassName);
//            System.out.println("Corresponding class: " + conditionClassName);
            for (Constructor constructor : condClass.getConstructors()) {
//                System.out.println("Found constructor with " + constructor.getGenericParameterTypes().length + " params: ");
                Class[] paramClasses = constructor.getParameterTypes();
                ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
                ParametersDescriptions paramsDesc = (ParametersDescriptions) constructor.getAnnotation(ParametersDescriptions.class);
                for (int i = 0; i < paramClasses.length; i++) {
                    setVisibleCondArg(i, true);
                    setCondArgLabelValDesc(i, paramsTypes.types()[i], paramsTypes.types()[i], paramsDesc.descriptions()[i]);
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
                }
            }
//            System.out.println();
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
    }

    private void fillConditionParametersValues(String keyword, Vector<Parameter> params) {
        for (int i = 0; i < 4; i++) {
            setVisibleCondArg(i, false);
        }
        String conditionClassName = AbstractCondition.CLASSES_KEYWORDS.get(keyword);
        try {
            Class condClass = Class.forName(
                    AbstractCondition.CONDITIONS_PACKAGE + conditionClassName);
//            System.out.println("Corresponding class: " + conditionClassName);
            for (Constructor constructor : condClass.getConstructors()) {
//                System.out.println("Found constructor with " + constructor.getGenericParameterTypes().length + " params: ");
                Class[] paramClasses = constructor.getParameterTypes();
                ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
                ParametersDescriptions paramsDesc = (ParametersDescriptions) constructor.getAnnotation(ParametersDescriptions.class);
                for (int i = 0; i < paramClasses.length; i++) {
                    setVisibleCondArg(i, true);
                    setCondArgLabelValDesc(i, paramsTypes.types()[i], params.get(i).getValueCode(), paramsDesc.descriptions()[i]);
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
                }
            }
            System.out.println();
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
    }

    private void displayEditedCond() {
        AbstractCondition cond = (AbstractCondition) editedRule.getConditions().get(editedCondNum);
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
//        String condName = cond.getName();
//        for (String keyword : AbstractCondition.CLASSES_KEYWORDS.keySet()) {
//            comboModel.addElement(keyword);
//            if (keyword.equals(condName)) {
//                comboModel.setSelectedItem(keyword);
//            }
//        }
        comboModel.addElement(cond.getName());
        cmbxConditionNames.setModel(comboModel);
        cmbxConditionNames.setEnabled(false);
        fillConditionParametersValues(cond.getName(), cond.getParameters());
    }

    private void displayEmptyCondition() {
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        for (String keyword : AbstractCondition.CLASSES_KEYWORDS.keySet()) {
            comboModel.addElement(keyword);
        }
        cmbxConditionNames.setModel(comboModel);
        cmbxConditionNames.setEnabled(true);
        cmbxConditionNames.setSelectedIndex(0);
    }

    private void showCondEditionDialog() {
        if (editedCondNum != -1) {
            dlgEditCond.setTitle("Edit Condition");
            displayEditedCond();
        } else {
            dlgEditCond.setTitle("New Condition");
            displayEmptyCondition();
        }
        dlgEditCond.pack();
        //        dlgEditCond.setLocation(btnAddCond.getLocationOnScreen());       
        dlgEditCond.setLocation(pnlAEDCond.getLocationOnScreen());
        dlgEditCond.setVisible(true);
    }

    private void cancelCondEditionDialog(int selectionIndex) {
        dlgEditCond.dispose();
        refreshConditionsListIn();
        lstConditionsListIn.setSelectedIndex(selectionIndex);
//        System.out.println("At Cancel Cond: lastSelectedCond equals " + lastSelectedCond);
    }

    private void btnAddCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddCondActionPerformed
        editedCondNum = -1;
        showCondEditionDialog();
}//GEN-LAST:event_btnAddCondActionPerformed

    private void btnEditCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditCondActionPerformed
        //This code must be changed all the way with lstConditionsList mouse click
        editedCondNum = lastSelectedCond;
        showCondEditionDialog();
}//GEN-LAST:event_btnEditCondActionPerformed

    private void btnDeleteCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteCondActionPerformed
        deleteCond();
}//GEN-LAST:event_btnDeleteCondActionPerformed

    private void deleteAct() {
        if (lastSelectedAct == -1) {
//            System.out.println("Actions list most likely empty.. no action to be deleted..");
//        } else if (editedRule.getActions().size() == 1) {
            // CAUTION!!
            // last action
        } else {
            AbstractAction act = (AbstractAction) editedRule.getActions().get(lastSelectedAct);
            //--> display WARNING
            //if ok
            //  WE MUST ASK THE IMPACT VERIFIER FIRST!!
            //   --> delete it really from the editedRule
            //   --> delete from lstActionsListIn
            int choice = DialogsFactory.deleteDialog(dlgEdit, "the action '" + act.getName() + "'");
            if (choice == 0) {
//                System.out.println("Action " + act.getName() + " will be deleted");
                editedRule.removeAction(act);
                int selectionIndex;
                if (lastSelectedAct == 0) {
                    //when the first action is deleted
                    //show the one coming to its place                
                    selectionIndex = lastSelectedAct;
                } else {
                    //else, show the one before (always safer! ;))
                    selectionIndex = lastSelectedAct - 1;
                }
                refreshActionsListIn();
                lstActionsListIn.setSelectedIndex(selectionIndex);
            } else {
                return;
            }
        }
    }

    private String getActParamValue(int i) {
        switch (i) {
            case 0:
                return txfActArgVal1.getText();
            case 1:
                return txfActArgVal2.getText();
            case 2:
                return txfActArgVal3.getText();
            default:
                return null;
        }
    }

    private void saveActEdition() {
        //Verify infos
        //if ok 
        //   --> Save info
        //   --> Cancel the Edition Dialog appropriately
        //else
        //   --> display the error
        //   --> remain in edition        
        String keyword = (String) cmbxActionNames.getSelectedItem();
        String actionClassName = AbstractAction.CLASSES_KEYWORDS.get(keyword);
        StringSchemeVariable nodeArg = null;
        StringSchemeVariable node2Arg = null;
        Expression formulaArg = null;
        Expression relationArg = null;
        String markArg = null;
        AbstractAction editedAct = null;
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(logicDefTab.getLogic());
        oldiesTokenizer.initializeTokenizerAndProps();
        try {
            Class actClass = Class.forName(
                    AbstractAction.ACTIONS_PACKAGE + actionClassName);
            Constructor constructor = actClass.getConstructors()[0];
//                Class[] paramClasses = constructor.getParameterTypes();
            ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
            Vector<Parameter> params = new Vector<Parameter>();
            boolean firstNode = true;
            for (int i = 0; i < paramsTypes.types().length; i++) {
                String paramTypeStr = paramsTypes.types()[i];
                ParameterType paramType = ParameterType.getParameterType(paramTypeStr);
                String paramValStr = getActParamValue(i);
                switch (paramType) {
                    case NODE:
                        if (firstNode) {
                            nodeArg = new StringSchemeVariable(paramValStr);
                            params.add(new Parameter(paramType, nodeArg));
                            firstNode = false;
                        } else {
                            node2Arg = new StringSchemeVariable(paramValStr);
                            params.add(new Parameter(paramType, node2Arg));
                        }
                        break;
                    case FORMULA:
                        try {
                            formulaArg = oldiesTokenizer.parseExpression(paramValStr);
                            oldiesTokenizer.verifyCodeEnd();
                        } catch (ParseException ex) {
                            DialogsFactory.syntaxErrorMessage(dlgEditAct, "The given formula parameter raised the following parser exception:\n\n" +
                                    ex.getMessage());
                            return;
                        }
                        params.add(new Parameter(paramType, formulaArg));
                        break;
                    case RELATION:
                        try {
                            relationArg = oldiesTokenizer.parseExpression(paramValStr);
                            oldiesTokenizer.verifyCodeEnd();
                        } catch (ParseException ex) {
                            DialogsFactory.syntaxErrorMessage(dlgEditAct, "The given relation parameter raised the following parser exception:\n\n" +
                                    ex.getMessage());
                            return;
                        }
                        params.add(new Parameter(paramType, relationArg));
                        break;
                    case MARK:
                        markArg = paramValStr;
                        params.add(new Parameter(paramType, markArg));
                        break;
                }
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
            }
            int paramsNum = params.size();
            switch (paramsNum) {
                case 1:
                    editedAct = (AbstractAction) constructor.newInstance(
                            params.get(0).getValue());
                    break;
                case 2:
                    editedAct = (AbstractAction) constructor.newInstance(
                            params.get(0).getValue(),
                            params.get(1).getValue());
                    break;
                case 3:
                    editedAct = (AbstractAction) constructor.newInstance(
                            params.get(0).getValue(),
                            params.get(1).getValue(),
                            params.get(2).getValue());
                    break;
            }
//            System.out.println("editedAct class name is: " + editedAct.getClass().getSimpleName());
            for (Parameter p : params) {
                editedAct.addParameter(p);
            }
            editedAct.setItsRule(editedRule);
            editedAct.setName(keyword);
//            System.out.println("editedAct code is: " + editedAct.getCode());
        } catch (InstantiationException ex) {
            System.out.println(ex);
        } catch (IllegalAccessException ex) {
            System.out.println(ex);
        } catch (IllegalArgumentException ex) {
            System.out.println(ex);
        } catch (InvocationTargetException ex) {
            System.out.println(ex);
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
        if (editedActNum == -1) {
//            System.out.println("new Action: '" + editedAct.getCode() + "' will be added");
            editedRule.addAction(editedAct);
            cancelActEditionDialog(editedRule.getActions().size() - 1);
        } else {
//            AbstractAction act = (AbstractAction) editedRule.getActions().get(editedActNum);
//            System.out.println("Action " + editedAct.getCode() + " will be saved as: ");
            editedRule.getActions().setElementAt(editedAct, editedActNum);
            editedRule.getActionContainer().setElementAt(editedAct, editedActNum);
            cancelActEditionDialog(lastSelectedAct);
        }
    }

    private void cancelActEdition() {
        int n = DialogsFactory.cancelDialog(dlgEditAct);
        if (n == 0) {
            cancelActEditionDialog(lastSelectedAct);
        } else {
            // jsut the dlgEditCon will be brought back again..
        }
    }

    private void setVisibleActArg1(boolean yes) {
        lblActArg1.setVisible(yes);
        txfActArgVal1.setVisible(yes);
        lblActArgDesc1.setVisible(yes);
    }

    private void setVisibleActArg2(boolean yes) {
        lblActArg2.setVisible(yes);
        txfActArgVal2.setVisible(yes);
        lblActArgDesc2.setVisible(yes);
    }

    private void setVisibleActArg3(boolean yes) {
        lblActArg3.setVisible(yes);
        txfActArgVal3.setVisible(yes);
        lblActArgDesc3.setVisible(yes);
    }

    private void setVisibleActArg(int argNum, boolean yes) {
        switch (argNum) {
            case 0:
                setVisibleActArg1(yes);
                break;
            case 1:
                setVisibleActArg2(yes);
                break;
            case 2:
                setVisibleActArg3(yes);
                break;
        }
    }

    private void setActArgLabelValDesc(int argNum, String label, String val, String desc) {
        switch (argNum) {
            case 0:
                lblActArg1.setText(label);
                txfActArgVal1.setText(val);
                actArgDesc1 = "<html>" + desc + "</html>";
                break;
            case 1:
                lblActArg2.setText(label);
                txfActArgVal2.setText(val);
                actArgDesc2 = "<html>" + desc + "</html>";
                break;
            case 2:
                lblActArg3.setText(label);
                txfActArgVal3.setText(val);
                actArgDesc3 = "<html>" + desc + "</html>";
                break;
        }
    }

    private void fillActionParameters(String keyword) {
        for (int i = 0; i < 3; i++) {
            setVisibleActArg(i, false);
        }
        String actionClassName = AbstractAction.CLASSES_KEYWORDS.get(keyword);
        try {
            Class actClass = Class.forName(
                    AbstractAction.ACTIONS_PACKAGE + actionClassName);
//            System.out.println("Corresponding class: " + actionClassName);
            for (Constructor constructor : actClass.getConstructors()) {
//                System.out.println("Found constructor with " + constructor.getGenericParameterTypes().length + " params: ");
                Class[] paramClasses = constructor.getParameterTypes();
                ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
                ParametersDescriptions paramsDesc = (ParametersDescriptions) constructor.getAnnotation(ParametersDescriptions.class);
                for (int i = 0; i < paramClasses.length; i++) {
                    setVisibleActArg(i, true);
                    setActArgLabelValDesc(i, paramsTypes.types()[i], paramsTypes.types()[i], paramsDesc.descriptions()[i]);
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
                }
            }
//            System.out.println();
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
    }

    private void fillActionParametersValues(String keyword, Vector<Parameter> params) {
        for (int i = 0; i < 3; i++) {
            setVisibleActArg(i, false);
        }
        String actionClassName = AbstractAction.CLASSES_KEYWORDS.get(keyword);
        try {
            Class actClass = Class.forName(
                    AbstractAction.ACTIONS_PACKAGE + actionClassName);
//            System.out.println("Corresponding class: " + actionClassName);
            for (Constructor constructor : actClass.getConstructors()) {
//                System.out.println("Found constructor with " + constructor.getGenericParameterTypes().length + " params: ");
                Class[] paramClasses = constructor.getParameterTypes();
                ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
                ParametersDescriptions paramsDesc = (ParametersDescriptions) constructor.getAnnotation(ParametersDescriptions.class);
                for (int i = 0; i < paramClasses.length; i++) {
                    setVisibleActArg(i, true);
                    setActArgLabelValDesc(i, paramsTypes.types()[i], params.get(i).getValueCode(), paramsDesc.descriptions()[i]);
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
                }
            }
//            System.out.println();
        } catch (ClassNotFoundException ex) {
            System.out.println(ex);
        }
    }

    private void displayEditedAct() {
        AbstractAction act = (AbstractAction) editedRule.getActions().get(editedActNum);
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        comboModel.addElement(act.getName());
        cmbxActionNames.setModel(comboModel);
        cmbxActionNames.setEnabled(false);
        fillActionParametersValues(act.getName(), act.getParameters());
    }

    private void displayEmptyAction() {
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        for (String keyword : AbstractAction.CLASSES_KEYWORDS.keySet()) {
            comboModel.addElement(keyword);
        }
        cmbxActionNames.setModel(comboModel);
        cmbxActionNames.setEnabled(true);
        cmbxActionNames.setSelectedIndex(0);
    }

    private void showActEditionDialog() {
        if (editedActNum != -1) {
            dlgEditAct.setTitle("Edit Action");
            displayEditedAct();
        } else {
            dlgEditAct.setTitle("New Action");
            displayEmptyAction();
        }
        dlgEditAct.pack();
//        dlgEditAct.setLocation(btnAddAct.getLocationOnScreen());        
        dlgEditAct.setLocation(pnlAEDAct.getLocationOnScreen());
        dlgEditAct.setVisible(true);
    }

    private void cancelActEditionDialog(int selectionIndex) {
        dlgEditAct.dispose();
        refreshActionsListIn();
        lstActionsListIn.setSelectedIndex(selectionIndex);
//        System.out.println("At Cancel Act: lastSelectedAct equals " + lastSelectedAct);
    }

    private void showParamDesc(Point location, String label) {
        lblDesc.setText(label);
        location.translate(18, 18);
        dlgDescription.setLocation(location);
        dlgDescription.pack();
        dlgDescription.setVisible(true);
    }

    /**
     * Add the cut/copy/paste actions to the action map.
     */
    private void setMappings(JList list) {
        ActionMap map = list.getActionMap();
        map.put(TransferHandler.getCutAction().getValue(Action.NAME),
                TransferHandler.getCutAction());
        map.put(TransferHandler.getCopyAction().getValue(Action.NAME),
                TransferHandler.getCopyAction());
        map.put(TransferHandler.getPasteAction().getValue(Action.NAME),
                TransferHandler.getPasteAction());
        InputMap imap = list.getInputMap();
        imap.put(KeyStroke.getKeyStroke("ctrl X"),
                TransferHandler.getCutAction().getValue(Action.NAME));
        imap.put(KeyStroke.getKeyStroke("ctrl C"),
                TransferHandler.getCopyAction().getValue(Action.NAME));
        imap.put(KeyStroke.getKeyStroke("ctrl V"),
                TransferHandler.getPasteAction().getValue(Action.NAME));

    }

    private void btnAddActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActActionPerformed
        editedActNum = -1;
        showActEditionDialog();
}//GEN-LAST:event_btnAddActActionPerformed

    private void btnEditActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActActionPerformed
        //This code must be changed all the way with lstActionsList mouse click
        editedActNum = lastSelectedAct;
        showActEditionDialog();
}//GEN-LAST:event_btnEditActActionPerformed

    private void btnDeleteActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActActionPerformed
        deleteAct();
}//GEN-LAST:event_btnDeleteActActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        saveEdition();
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelEdition();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txfCondArgVal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfCondArgVal1ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfCondArgVal1ActionPerformed

    private void lblCondArgDesc1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCondArgDesc1MouseClicked
        showParamDesc(lblCondArgDesc1.getLocationOnScreen(), condArgDesc1);
}//GEN-LAST:event_lblCondArgDesc1MouseClicked

    private void txfCondArgVal3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfCondArgVal3ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfCondArgVal3ActionPerformed

    private void btnSaveCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveCondActionPerformed
        saveCondEdition();
}//GEN-LAST:event_btnSaveCondActionPerformed

    private void btnCancelCondActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelCondActionPerformed
        cancelCondEdition();
}//GEN-LAST:event_btnCancelCondActionPerformed

    private void lstRulesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstRulesListValueChanged
        lastSelected = lstRulesList.getSelectedIndex();
//        System.out.println("lastSelected now equals " + lastSelected);
        displaySelectedRule();
    }//GEN-LAST:event_lstRulesListValueChanged

    private void lstConditionsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstConditionsListValueChanged
        lstConditionsList.clearSelection();
    }//GEN-LAST:event_lstConditionsListValueChanged

    private void lstActionsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstActionsListValueChanged
        lstActionsList.clearSelection();
    }//GEN-LAST:event_lstActionsListValueChanged

    private void dlgEditWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgEditWindowClosing
        cancelEdition();
    }//GEN-LAST:event_dlgEditWindowClosing

    private void lstConditionsListInValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstConditionsListInValueChanged
        lastSelectedCond = lstConditionsListIn.getSelectedIndex();

//        System.out.println("lastSelectedCond now equals " + lastSelectedCond);
    }//GEN-LAST:event_lstConditionsListInValueChanged

    private void dlgEditCondWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgEditCondWindowClosing
        cancelCondEdition();
    }//GEN-LAST:event_dlgEditCondWindowClosing

    private void lstActionsListInValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstActionsListInValueChanged
        lastSelectedAct = lstActionsListIn.getSelectedIndex();

//        System.out.println("lastSelectedAct now equals " + lastSelectedAct);
    }//GEN-LAST:event_lstActionsListInValueChanged

    private void dlgEditActWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgEditActWindowClosing
        cancelActEdition();
    }//GEN-LAST:event_dlgEditActWindowClosing

    private void txfActArgVal1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfActArgVal1ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfActArgVal1ActionPerformed

    private void lblActArgDesc1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblActArgDesc1MouseClicked
        showParamDesc(lblActArgDesc1.getLocationOnScreen(), actArgDesc1);
}//GEN-LAST:event_lblActArgDesc1MouseClicked

    private void txfActArgVal3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfActArgVal3ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_txfActArgVal3ActionPerformed

    private void btnSaveActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActActionPerformed
        saveActEdition();
}//GEN-LAST:event_btnSaveActActionPerformed

    private void btnCancelActActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActActionPerformed
        cancelActEdition();
}//GEN-LAST:event_btnCancelActActionPerformed

    private void cmbxConditionNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxConditionNamesActionPerformed
        String keyword = (String) cmbxConditionNames.getSelectedItem();
        fillConditionParameters(keyword);
        dlgEditCond.pack();
    }//GEN-LAST:event_cmbxConditionNamesActionPerformed

    private void lblCondArgDesc2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCondArgDesc2MouseClicked
        showParamDesc(lblCondArgDesc2.getLocationOnScreen(), condArgDesc2);
    }//GEN-LAST:event_lblCondArgDesc2MouseClicked

    private void lblCondArgDesc3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCondArgDesc3MouseClicked
        showParamDesc(lblCondArgDesc3.getLocationOnScreen(), condArgDesc3);
    }//GEN-LAST:event_lblCondArgDesc3MouseClicked

    private void cmbxActionNamesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxActionNamesActionPerformed
        String keyword = (String) cmbxActionNames.getSelectedItem();
        fillActionParameters(keyword);
        dlgEditCond.pack();
    }//GEN-LAST:event_cmbxActionNamesActionPerformed

    private void lblActArgDesc2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblActArgDesc2MouseClicked
        showParamDesc(lblActArgDesc2.getLocationOnScreen(), actArgDesc2);
    }//GEN-LAST:event_lblActArgDesc2MouseClicked

    private void lblActArgDesc3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblActArgDesc3MouseClicked
        showParamDesc(lblActArgDesc3.getLocationOnScreen(), actArgDesc3);
    }//GEN-LAST:event_lblActArgDesc3MouseClicked

    private void lstRulesListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstRulesListMouseClicked
        if (evt.getClickCount() == 2) {
            //This code must be changed all the way with btnEdit click
            editedRuleNum = lastSelected;
            editedRule = (Rule) logicDefTab.getLogic().getRules().get(editedRuleNum);
//            System.out.println("Rule " + editedRule.getName() + " will be edited");
            showEditionDialog();
        }
    }//GEN-LAST:event_lstRulesListMouseClicked

    private void lstConditionsListInMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstConditionsListInMouseClicked
        if (evt.getClickCount() == 2) {
            //This code must be changed all the way with btnEditCond mouse click
            editedCondNum = lastSelectedCond;
            showCondEditionDialog();
        }
    }//GEN-LAST:event_lstConditionsListInMouseClicked

    private void lstActionsListInMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstActionsListInMouseClicked
        if (evt.getClickCount() == 2) {
            //This code must be changed all the way with lstActionsList mouse click
            editedActNum = lastSelectedAct;
            showActEditionDialog();
        }
    }//GEN-LAST:event_lstActionsListInMouseClicked

    private void txfCondArgVal2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txfCondArgVal2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txfCondArgVal2ActionPerformed

    private void txfCondArgVal1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfCondArgVal1FocusGained
        txfCondArgVal1.selectAll();
    }//GEN-LAST:event_txfCondArgVal1FocusGained

    private void txfCondArgVal2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfCondArgVal2FocusGained
        txfCondArgVal2.selectAll();
    }//GEN-LAST:event_txfCondArgVal2FocusGained

    private void txfCondArgVal3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfCondArgVal3FocusGained
        txfCondArgVal3.selectAll();
    }//GEN-LAST:event_txfCondArgVal3FocusGained

    private void txfActArgVal1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfActArgVal1FocusGained
        txfActArgVal1.selectAll();
    }//GEN-LAST:event_txfActArgVal1FocusGained

    private void txfActArgVal2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfActArgVal2FocusGained
        txfActArgVal2.selectAll();
    }//GEN-LAST:event_txfActArgVal2FocusGained

    private void txfActArgVal3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfActArgVal3FocusGained
        txfActArgVal3.selectAll();
    }//GEN-LAST:event_txfActArgVal3FocusGained

    private void txfCondArgVal4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txfCondArgVal4FocusGained
        txfCondArgVal4.selectAll();
    }//GEN-LAST:event_txfCondArgVal4FocusGained

    private void lblCondArgDesc4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCondArgDesc4MouseClicked
        showParamDesc(lblCondArgDesc4.getLocationOnScreen(), condArgDesc4);
    }//GEN-LAST:event_lblCondArgDesc4MouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddAct;
    private javax.swing.JButton btnAddCond;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCancelAct;
    private javax.swing.JButton btnCancelCond;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDeleteAct;
    private javax.swing.JButton btnDeleteCond;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnEditAct;
    private javax.swing.JButton btnEditCond;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSaveAct;
    private javax.swing.JButton btnSaveCond;
    private javax.swing.JComboBox cmbxActionNames;
    private javax.swing.JComboBox cmbxConditionNames;
    private javax.swing.JDialog dlgDescription;
    private javax.swing.JDialog dlgEdit;
    private javax.swing.JDialog dlgEditAct;
    private javax.swing.JDialog dlgEditCond;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel lblActArg1;
    private javax.swing.JLabel lblActArg2;
    private javax.swing.JLabel lblActArg3;
    private javax.swing.JLabel lblActArgDesc1;
    private javax.swing.JLabel lblActArgDesc2;
    private javax.swing.JLabel lblActArgDesc3;
    private javax.swing.JLabel lblActions;
    private javax.swing.JLabel lblActions1;
    private javax.swing.JLabel lblComments;
    private javax.swing.JLabel lblComments1;
    private javax.swing.JLabel lblCondArg1;
    private javax.swing.JLabel lblCondArg2;
    private javax.swing.JLabel lblCondArg3;
    private javax.swing.JLabel lblCondArg4;
    private javax.swing.JLabel lblCondArgDesc1;
    private javax.swing.JLabel lblCondArgDesc2;
    private javax.swing.JLabel lblCondArgDesc3;
    private javax.swing.JLabel lblCondArgDesc4;
    private javax.swing.JLabel lblConditions;
    private javax.swing.JLabel lblConditions1;
    private javax.swing.JLabel lblDesc;
    private javax.swing.JLabel lblDescTitle;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblName1;
    private javax.swing.JLabel lblShowHideComments;
    private javax.swing.JList lstActionsList;
    private javax.swing.JList lstActionsListIn;
    private javax.swing.JList lstConditionsList;
    private javax.swing.JList lstConditionsListIn;
    private javax.swing.JList lstRulesList;
    private javax.swing.JPanel pnlAED;
    private javax.swing.JPanel pnlAEDAct;
    private javax.swing.JPanel pnlAEDCond;
    private javax.swing.JPanel pnlCond;
    private javax.swing.JPanel pnlCond1;
    private javax.swing.JPanel pnlCondArgs;
    private javax.swing.JPanel pnlCondArgs1;
    private javax.swing.JPanel pnlCondSaveCancel;
    private javax.swing.JPanel pnlCondSaveCancel1;
    private javax.swing.JPanel pnlDescJunk;
    private javax.swing.JPanel pnlEditedRule;
    private javax.swing.JPanel pnlRulesList;
    private javax.swing.JPanel pnlSaveCancel;
    private javax.swing.JPanel pnlSelectedRule;
    private javax.swing.JScrollPane scrlActionsList;
    private javax.swing.JScrollPane scrlActionsList1;
    private javax.swing.JScrollPane scrlComments;
    private javax.swing.JScrollPane scrlComments1;
    private javax.swing.JScrollPane scrlConditionsList;
    private javax.swing.JScrollPane scrlConditionsList1;
    private javax.swing.JScrollPane scrlRulesList;
    private javax.swing.JTextArea txaComments;
    private javax.swing.JTextArea txaCommentsIn;
    private javax.swing.JTextField txfActArgVal1;
    private javax.swing.JTextField txfActArgVal2;
    private javax.swing.JTextField txfActArgVal3;
    private javax.swing.JTextField txfCondArgVal1;
    private javax.swing.JTextField txfCondArgVal2;
    private javax.swing.JTextField txfCondArgVal3;
    private javax.swing.JTextField txfCondArgVal4;
    private javax.swing.JTextField txfRuleName;
    private javax.swing.JTextField txfRuleNameIn;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the logicDefTab
     */
    public LogicDefTab getLogicDefTab() {
        return logicDefTab;
    }
}
