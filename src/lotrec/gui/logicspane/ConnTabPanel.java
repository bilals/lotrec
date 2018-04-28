/*
 * ConRulTabsPanel.java
 *
 * Created on 30 octobre 2007, 11:26
 */
package lotrec.gui.logicspane;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.gui.DialogsFactory;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.Verifier;
import lotrec.resources.ResourcesProvider;

/**
 *
 * @author  said
 */
public class ConnTabPanel extends javax.swing.JPanel {

    private LogicDefTab logicDefTab;
    private java.util.ResourceBundle resource;
    private int lastSelected = -1;// changed only inside the valueChanged method
    private int editedConNum; //-1 if new conn added
    //conNum of selected con to be edited
    /** Creates new form ConRulTabsPanel */
    public ConnTabPanel() {
        initComponents();
    }

    public ConnTabPanel(LogicDefTab logicDefTab) {
        this.logicDefTab = logicDefTab;
        initComponents();

        //Display the connectors in the JList
        refreshConnectorsList();
        //to select the first row & fire the first display event
        if (logicDefTab.getLogic().getConnectors().size() > 0) {
            lstConnectorsList.setSelectedIndex(0);
        }
        initializeShowHidePredefList();
        displaySelectedConnector();
        setFieldsEditable(false);
    }

    public void displaySelectedConnector() {
        if (lastSelected == -1) {
            txfName.setText("");
            txfArity.setText("");
            txfOutputFormat.setText("");
            txfPriority.setText("");
            chkbxAssociative.setSelected(false);
            txaComments.setText("");
            hideComments();
        } else {
            Connector conn = (Connector) logicDefTab.getLogic().getConnectors().get(lastSelected);
            txfName.setText(conn.getName());
            txfArity.setText(String.valueOf(conn.getArity()));
            txfOutputFormat.setText(conn.getOutString());
            txfPriority.setText(String.valueOf(conn.getPriority()));
            chkbxAssociative.setSelected(conn.isAssociative());
            txaComments.setText(conn.getComment());
            txaComments.setCaretPosition(0);
            if (txaComments.getText().equals("")) {
                hideComments();
            } else {
                showComments();
            }
        }
    }

    public void refreshConnectorsList() {
        //Display the connectors in the JList
        if (logicDefTab.getLogic().getConnectors() != null) {
            DefaultListModel listModel = new DefaultListModel();
            for (int i = 0; i < logicDefTab.getLogic().getConnectors().size(); i++) {
                listModel.addElement(((Connector) logicDefTab.getLogic().getConnectors().get(i)).getName());
            }
            lstConnectorsList.setModel(listModel);
        }
        if (logicDefTab.getLogic().getConnectors().size() == 0) {
            btnEdit.setEnabled(false);
            btnDelete.setEnabled(false);
        } else {
            btnEdit.setEnabled(true);
            btnDelete.setEnabled(true);
        }
    }

    public void refresh() {
        resource = java.util.ResourceBundle.getBundle("lotrec.resources.LoadedLogicsPanel", ResourcesProvider.getCurrentLocale());
        pnlConnList.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("ConnTab.ConnectorsList")));
        pnlSelectedConnector.setBorder(javax.swing.BorderFactory.createTitledBorder(resource.getString("ConnTab.SelectedConnector")));
        lblName.setText(resource.getString("ConnTab.Titles.Name"));
        lblArity.setText(resource.getString("ConnTab.Titles.Arity"));
        chkbxAssociative.setText(resource.getString("ConnTab.Titles.Associative"));
        lblOutputFormat.setText(resource.getString("ConnTab.Titles.OutputFormat"));
        lblPriority.setText(resource.getString("ConnTab.Titles.Priority"));
        lblComments.setText(resource.getString("ConnTab.Titles.Comments"));
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
        txfArity.setEditable(yes);
        txfOutputFormat.setEditable(yes);
        txfPriority.setEditable(yes);
        chkbxAssociative.setEnabled(yes);
        txaComments.setEditable(yes);
    }

    private void deleteCon() {
        if (lastSelected == -1) {
//            System.out.println("connectors list most likely empty.. no con to be deleted..");
//        } else if (logic.getConnectors().size() == 1) {
        // CAUTION!!
        // last connector
        } else {
            Connector conn = (Connector) logicDefTab.getLogic().getConnectors().get(lastSelected);
            //--> display WARNING
            //if ok
            //  WE MUST ASK THE IMPACT VERIFIER FIRST!!
            //   --> delete from lstConnectorsList
            //   --> delete it really from the logic
            if (logicDefTab.getLogic().isUsedConnector(conn)) {
                DialogsFactory.semanticErrorMessage(pnlSelectedConnector, "The connector " + conn.getName() + " could not be deleted!\n\n" +
                        "The connector you are deleting is already used in some predefined formulas and/or\n" +
                        "in some actions and/or conditions of some of your rules");
                return;
            }
            int choice = DialogsFactory.deleteDialog(pnlSelectedConnector, "the connector " + conn.getName());
            if (choice == 0) {
//                System.out.println("conn " + conn.getName() + " will be deleted");
                logicDefTab.getLogic().removeConnector(conn);
                logicDefTab.setModifiedAndNotSaved(true);
                int selectionIndex;
                if (lastSelected == 0) {
                    //when the first con is deleted
                    //show the one coming to its place                
                    selectionIndex = lastSelected;
                } else {
                    //else, show the one before (always safer! ;))
                    selectionIndex = lastSelected - 1;
                }
                refreshConnectorsList();
                lstConnectorsList.setSelectedIndex(selectionIndex);
            } else {
                return;
            }
        }
    }

    private void addNew() {
        //Verify infos
        //if ok
        //   --> Save info
        //   --> Cancel the Edition Dialog appropriately
        //else
        //   --> display the error
        //   --> remain in edition     
//        String conName = txfNameIn.getText();
        String conName;
        String strArity = txfArityIn.getText();
        int arity;
        String outFormat = txfOutputFormatIn.getText();
        String strPriority = txfPriorityIn.getText();
        int priority;
        boolean associative = chkbxAssociativeIn.isSelected();
        String comment = txaCommentsIn.getText();
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(logicDefTab.getLogic());
        oldiesTokenizer.initializeTokenizerAndProps();
        oldiesTokenizer.setSource(txfNameIn.getText());
        try {
            conName = oldiesTokenizer.readStringToken();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given connector name raised the following parser exception:\n\n" +
                    ex.getMessage());
            return;
        }
        try {
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            JOptionPane.showMessageDialog(dlgEdit,
                    "It seems that the given name has some extra text at the end.\n" +
                    "LoTREC will suppose being given this name: '" + conName + "' and will ignore the rest.\n\n" +
                    "Raised exception:\n" +
                    ex.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (conName.equals("variable")) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not be 'variable'. It's a keyword,\n" +
                    "and using it will make confusion while parsing expressions.");
            return;
        }
        if (conName.equals("constant")) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not be 'constant'. It's a keyword,\n" +
                    "and using it will make confusion while parsing expressions.");
            return;
        }
        if (conName.startsWith("_")) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not start with '_'.\n" +
                    "It's a special character used to designate variables, and using it\n" +
                    "as a connector name will make confusion while parsing expressions.");
            return;
        }
        if (Character.isUpperCase(conName.charAt(0))) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not start with a Capital Letter like: '" + conName.charAt(0) + "'.\n" +
                    "Capital Letter at the begining of a word is a way to designate constants,\n" +
                    "and using it in a connector name will make confusion while parsing expressions.");
            return;
        }
//        if (!Verifier.verifyConnectorName(conName)) {
//            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given connector name: '" + conName +
//                    "\n' doesn't match this syntax pattern: " + Verifier.CONNECTOR_NAME_SYNTAX +
//                    "\n\nA valid connector name starts with a lower case alphabet letter\n" +
//                    "followed by any sequence of alpha-numeric characters");
//            return;
//        } 
        if (logicDefTab.getLogic().getConnector(conName) != null) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The given connector name: '" + conName +
                    "' is already used as a key identifier for another connector.\n" +
                    "Please chose another name.");
            return;
        }
        try {
            arity = Integer.parseInt(strArity);
        } catch (NumberFormatException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The arity value '" + strArity + "' is not a valid number");
            return;
        }
        if (arity < 1) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The arity should be greater than or equal 1");
            return;
        }
        int _Num = Verifier.verifyConnectorOutFormat(outFormat);
        if (_Num != arity) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The Output Format of a connector must respect its arity\n" +
                    "Each '_' occurence will be replace by a parameter of the connector\n" +
                    _Num + " occurence(s) of '_' found while the given arity is " + arity +
                    "\nYou should adjust the output format or the arity to match them correctly.");
            return;
        }
        try {
            priority = Integer.parseInt(strPriority);
        } catch (NumberFormatException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The priority value '" + strPriority + "' is not a valid number");
            return;
        }
        if (priority < 0) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The priority should not be neagtive");
            return;
        }
        Connector conn = new Connector();
        conn.setName(conName);
        conn.setArity(arity);
        conn.setOutString(outFormat);
        conn.setPriority(priority);
        conn.setAssociative(associative);
        conn.setComment(comment);
        logicDefTab.getLogic().addConnector(conn);
        logicDefTab.setModifiedAndNotSaved(true);
//        System.out.println("new conn will be added:" + conName);
        cancelEditionDialog(logicDefTab.getLogic().getConnectors().size() - 1);
    }

    private void saveEdition() {
        //Verify infos
        //if ok
        //   --> Save info
        //   --> Cancel the Edition Dialog appropriately
        //else
        //   --> display the error
        //   --> remain in edition  
        Connector conn = (Connector) logicDefTab.getLogic().getConnectors().get(editedConNum);
//        String conName = txfNameIn.getText();
        String conName;
        String strArity = txfArityIn.getText();
        int arity;
        String outFormat = txfOutputFormatIn.getText();
        String strPriority = txfPriorityIn.getText();
        int priority;
        boolean associative = chkbxAssociativeIn.isSelected();
        String comment = txaCommentsIn.getText();
        OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(logicDefTab.getLogic());
        oldiesTokenizer.initializeTokenizerAndProps();
        oldiesTokenizer.setSource(txfNameIn.getText());
        try {
            conName = oldiesTokenizer.readStringToken();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given connector name raised the following parser exception:\n\n" +
                    ex.getMessage());
            return;
        }
        try {
            oldiesTokenizer.verifyCodeEnd();
        } catch (lotrec.parser.exceptions.ParseException ex) {
            JOptionPane.showMessageDialog(dlgEdit,
                    "It seems that the given name has some extra text at the end.\n" +
                    "LoTREC will suppose being given this name: '" + conName + "' and will ignore the rest.\n\n" +
                    "Raised exception:\n" +
                    ex.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if (conName.equals("variable")) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not be 'variable'. It's a keyword,\n" +
                    "and using it will make confusion while parsing expressions.");
            return;
        }
        if (conName.equals("constant")) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not be 'constant'. It's a keyword,\n" +
                    "and using it will make confusion while parsing expressions.");
            return;
        }
        if (conName.startsWith("_")) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not start with '_'.\n" +
                    "It's a special character used to designate variables, and using it\n" +
                    "as a connector name will make confusion while parsing expressions.");
            return;
        }
        if (Character.isUpperCase(conName.charAt(0))) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The connector name should not start with a Capital Letter like: '" + conName.charAt(0) + "'.\n" +
                    "Capital Letter at the begining of a word is a way to designate constants,\n" +
                    "and using it in a connector name will make confusion while parsing expressions.");
            return;
        }
//        if (!Verifier.verifyConnectorName(conName)) {
//            DialogsFactory.syntaxErrorMessage(dlgEdit, "The given connector name: '" + conName +
//                    "\n' doesn't match this syntax pattern: " + Verifier.CONNECTOR_NAME_SYNTAX +
//                    "\n\nA valid connector name starts with a lower case alphabet letter\n" +
//                    "followed by any sequence of alpha-numeric characters");
//            return;
//        } 
        if ((logicDefTab.getLogic().getConnector(conName) != null) && !conn.getName().equals(conName)) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The given connector name: '" + conName +
                    "' is already used as a key identifier for another connector.\n" +
                    "Please chose another name.");
            return;
        }
        try {
            arity = Integer.parseInt(strArity);
        } catch (NumberFormatException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The arity value '" + strArity + "' is not a valid number");
            return;
        }
        if (arity < 1) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The arity should be greater than or equal 1");
            return;
        }
        if (logicDefTab.getLogic().isUsedConnector(conn) && (conn.getArity() != arity)) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The arity of a used connector could not be changed!\n" +
                    "The arity " + conn.getArity() + " will remain the same\n\n" +
                    "The connector you are editing is already used in some predefined formulas and/or\n" +
                    "in some actions and/or conditions of some of your rules");
            arity = conn.getArity();
            txfArityIn.setText(String.valueOf(arity));
        }
        int _Num = Verifier.verifyConnectorOutFormat(outFormat);
        if (_Num != arity) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The Output Format of a connector must respect its arity\n" +
                    "Each '_' occurence will be replaced by a parameter of the connector\n" +
                    _Num + " occurence(s) of '_' found while the given arity is " + arity +
                    "\nYou should adjust the output format or the arity to match them correctly.");
            return;
        }
        try {
            priority = Integer.parseInt(strPriority);
        } catch (NumberFormatException ex) {
            DialogsFactory.syntaxErrorMessage(dlgEdit, "The priority value '" + strPriority + "' is not a valid number");
            return;
        }
        if (priority < 0) {
            DialogsFactory.semanticErrorMessage(dlgEdit, "The priority should not be neagtive");
            return;
        }
        conn.setName(conName);
        conn.setArity(arity);
        conn.setOutString(outFormat);
        conn.setPriority(priority);
        conn.setAssociative(associative);
        conn.setComment(comment);
        logicDefTab.setModifiedAndNotSaved(true);
//        System.out.println("conn " + conn.getName() + " will be saved as: " + conName);
        //After changing the name, we must refresh the rules and formulas'display
        logicDefTab.getRulesTabPanel().displaySelectedRule();
        int toSelect = logicDefTab.getTestingFormulaePanel().getLastSelected();
        logicDefTab.getTestingFormulaePanel().refreshFormulaeList();
        logicDefTab.getTestingFormulaePanel().getLstFormulaeList().setSelectedIndex(toSelect);
//        logicDefTab.getTestingFormulaePanel().displaySelectedFormula();
        logicDefTab.getMainFrame().getControlsPanel().refreshFormulaeList();
        cancelEditionDialog(lastSelected);
    }

    private void cancelEdition() {
        int n = DialogsFactory.cancelDialog(dlgEdit);
        if (n == 0) {
            cancelEditionDialog(lastSelected);
        } else {
        // jsut the dlgEdit will be brought again..
        }
    }

    private void fillEditionDialogWith(Connector conn) {
        txfNameIn.setText(conn.getName());
        txfArityIn.setText(String.valueOf(conn.getArity()));
//            txfArityIn.setEditable(false);
        txfOutputFormatIn.setText(conn.getOutString());
        txfPriorityIn.setText(String.valueOf(conn.getPriority()));
        chkbxAssociativeIn.setSelected(conn.isAssociative());
        txaCommentsIn.setText(conn.getComment());
        txaCommentsIn.setCaretPosition(0);
    }

    private void showEditionDialog() {
        if (editedConNum != -1) {
            dlgEdit.setTitle("Edit Connector");
            Connector conn = (Connector) logicDefTab.getLogic().getConnectors().get(editedConNum);
            txfNameIn.setText(conn.getName());
            txfArityIn.setText(String.valueOf(conn.getArity()));
//            txfArityIn.setEditable(false);
            txfOutputFormatIn.setText(conn.getOutString());
            txfPriorityIn.setText(String.valueOf(conn.getPriority()));
            chkbxAssociativeIn.setSelected(conn.isAssociative());
            txaCommentsIn.setText(conn.getComment());
            txaCommentsIn.setCaretPosition(0);
            hideShowHidePredefList();
        } else {
            dlgEdit.setTitle("New Connector");
            txfNameIn.setText("connec-name");
            txfArityIn.setText("1");
//            txfArityIn.setEditable(true);
            txfOutputFormatIn.setText("(_)");
            txfPriorityIn.setText("0");
            chkbxAssociativeIn.setSelected(false);
            txaCommentsIn.setText("");
            showShowHidePredefList();
        }
        dlgEdit.pack();
        dlgEdit.setLocation(pnlSelectedConnector.getLocationOnScreen());
        dlgEdit.setVisible(true);
    }

    private void cancelEditionDialog(int selectionIndex) {
        dlgEdit.dispose();
        refreshConnectorsList();
        lstConnectorsList.setSelectedIndex(selectionIndex);
//        System.out.println("At Cancel: lastSelected equals " + lastSelected);
    }

    private void showDesc(Point location, String label) {
        lblDesc.setText(label);
        location.translate(18, 18);
        dlgDescription.setLocation(location);
        dlgDescription.pack();
        dlgDescription.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jDialog1 = new javax.swing.JDialog();
        jScrollPane2 = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        dlgEdit = new javax.swing.JDialog();
        pnlConEdit = new javax.swing.JPanel();
        lblNameIn = new javax.swing.JLabel();
        lblArityIn = new javax.swing.JLabel();
        txfNameIn = new javax.swing.JTextField();
        txfArityIn = new javax.swing.JTextField();
        chkbxAssociativeIn = new javax.swing.JCheckBox();
        lblOutputFormatIn = new javax.swing.JLabel();
        lblPrioritIn = new javax.swing.JLabel();
        lblCommentsIn = new javax.swing.JLabel();
        txfOutputFormatIn = new javax.swing.JTextField();
        txfPriorityIn = new javax.swing.JTextField();
        scrlCommentsIn = new javax.swing.JScrollPane();
        txaCommentsIn = new javax.swing.JTextArea();
        pnlSaveCancel = new javax.swing.JPanel();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        pnlShowHidePredefList = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tglbtnPredefinedList = new javax.swing.JToggleButton();
        pnlPredefConnList = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstPredefConnList = new javax.swing.JList();
        cmbxLoadedLogics = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        titledBorderIn = javax.swing.BorderFactory.createTitledBorder("");
        dlgDescription = new javax.swing.JDialog();
        lblDesc = new javax.swing.JLabel();
        lblDescTitle = new javax.swing.JLabel();
        pnlDescJunk = new javax.swing.JPanel();
        pnlSelectedConnector = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        lblArity = new javax.swing.JLabel();
        txfName = new javax.swing.JTextField();
        txfArity = new javax.swing.JTextField();
        chkbxAssociative = new javax.swing.JCheckBox();
        lblOutputFormat = new javax.swing.JLabel();
        lblPriority = new javax.swing.JLabel();
        lblComments = new javax.swing.JLabel();
        txfOutputFormat = new javax.swing.JTextField();
        txfPriority = new javax.swing.JTextField();
        lblShowHideComments = new javax.swing.JLabel();
        scrlComments = new javax.swing.JScrollPane();
        txaComments = new javax.swing.JTextArea();
        pnlConnList = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstConnectorsList = new javax.swing.JList();
        pnlAED = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        jEditorPane1.setContentType("text/html");
        jEditorPane1.setText("test");
        jScrollPane2.setViewportView(jEditorPane1);

        org.jdesktop.layout.GroupLayout jDialog1Layout = new org.jdesktop.layout.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );

        dlgEdit.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        dlgEdit.setAlwaysOnTop(true);
        dlgEdit.setModal(true);
        dlgEdit.setResizable(false);
        dlgEdit.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dlgEditWindowClosing(evt);
            }
        });

        pnlConEdit.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        pnlConEdit.setOpaque(false);

        lblNameIn.setText("Name");

        lblArityIn.setText("Arity");

        txfNameIn.setText("<Name>");

        txfArityIn.setText("<Arity>");

        chkbxAssociativeIn.setText("Associative");
        chkbxAssociativeIn.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxAssociativeIn.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkbxAssociativeIn.setOpaque(false);

        lblOutputFormatIn.setText("Display");

        lblPrioritIn.setText("Priority");

        lblCommentsIn.setText("Comments");

        txfOutputFormatIn.setText("<Output Format>");

        txfPriorityIn.setText("<Priority>");

        txaCommentsIn.setColumns(20);
        txaCommentsIn.setLineWrap(true);
        txaCommentsIn.setRows(5);
        txaCommentsIn.setText("<Comments>");
        txaCommentsIn.setWrapStyleWord(true);
        txaCommentsIn.setMargin(new java.awt.Insets(1, 5, 2, 4));
        txaCommentsIn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txaCommentsInMouseClicked(evt);
            }
        });
        scrlCommentsIn.setViewportView(txaCommentsIn);

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

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/idea.PNG"))); // NOI18N
        jLabel1.setText("Fill in with a predefined connector:");

        tglbtnPredefinedList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/arrow-right.png"))); // NOI18N
        tglbtnPredefinedList.setText("Show List");
        tglbtnPredefinedList.setContentAreaFilled(false);
        tglbtnPredefinedList.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        tglbtnPredefinedList.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        tglbtnPredefinedList.setIconTextGap(2);
        tglbtnPredefinedList.setMargin(new java.awt.Insets(0, 0, 0, 0));
        tglbtnPredefinedList.setSelectedIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/arrow-left.png"))); // NOI18N
        tglbtnPredefinedList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tglbtnPredefinedListActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout pnlShowHidePredefListLayout = new org.jdesktop.layout.GroupLayout(pnlShowHidePredefList);
        pnlShowHidePredefList.setLayout(pnlShowHidePredefListLayout);
        pnlShowHidePredefListLayout.setHorizontalGroup(
            pnlShowHidePredefListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlShowHidePredefListLayout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 65, Short.MAX_VALUE)
                .add(tglbtnPredefinedList))
        );
        pnlShowHidePredefListLayout.setVerticalGroup(
            pnlShowHidePredefListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlShowHidePredefListLayout.createSequentialGroup()
                .add(pnlShowHidePredefListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(tglbtnPredefinedList)
                    .add(jLabel1))
                .addContainerGap(3, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pnlConEditLayout = new org.jdesktop.layout.GroupLayout(pnlConEdit);
        pnlConEdit.setLayout(pnlConEditLayout);
        pnlConEditLayout.setHorizontalGroup(
            pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlConEditLayout.createSequentialGroup()
                .add(pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pnlConEditLayout.createSequentialGroup()
                        .add(lblCommentsIn)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 267, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlConEditLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(scrlCommentsIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlConEditLayout.createSequentialGroup()
                        .add(pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(lblOutputFormatIn)
                            .add(lblNameIn))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(pnlConEditLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(chkbxAssociativeIn))
                            .add(txfOutputFormatIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                            .add(txfNameIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblPrioritIn)
                            .add(lblArityIn))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txfArityIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, txfPriorityIn, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)))
                    .add(pnlShowHidePredefList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlSaveCancel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlConEditLayout.setVerticalGroup(
            pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlConEditLayout.createSequentialGroup()
                .add(pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(txfNameIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(txfArityIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblArityIn)
                    .add(lblNameIn))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlConEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOutputFormatIn)
                    .add(txfOutputFormatIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblPrioritIn)
                    .add(txfPriorityIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chkbxAssociativeIn)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblCommentsIn)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlCommentsIn, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(pnlSaveCancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlShowHidePredefList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pnlPredefConnList.setBorder(javax.swing.BorderFactory.createTitledBorder("Predefined Connectors"));

        lstPredefConnList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "list is empty.." };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstPredefConnList.setToolTipText("Double-Click to choose");
        lstPredefConnList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstPredefConnListMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(lstPredefConnList);

        cmbxLoadedLogics.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Loaded logics" }));
        cmbxLoadedLogics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxLoadedLogicsActionPerformed(evt);
            }
        });

        jLabel2.setText("Loaded Logics:");

        org.jdesktop.layout.GroupLayout pnlPredefConnListLayout = new org.jdesktop.layout.GroupLayout(pnlPredefConnList);
        pnlPredefConnList.setLayout(pnlPredefConnListLayout);
        pnlPredefConnListLayout.setHorizontalGroup(
            pnlPredefConnListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPredefConnListLayout.createSequentialGroup()
                .add(jLabel2)
                .addContainerGap())
            .add(cmbxLoadedLogics, 0, 134, Short.MAX_VALUE)
            .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
        );
        pnlPredefConnListLayout.setVerticalGroup(
            pnlPredefConnListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlPredefConnListLayout.createSequentialGroup()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cmbxLoadedLogics, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout dlgEditLayout = new org.jdesktop.layout.GroupLayout(dlgEdit.getContentPane());
        dlgEdit.getContentPane().setLayout(dlgEditLayout);
        dlgEditLayout.setHorizontalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dlgEditLayout.createSequentialGroup()
                .add(pnlConEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlPredefConnList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dlgEditLayout.setVerticalGroup(
            dlgEditLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlConEdit, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(pnlPredefConnList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

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
        lblDescTitle.setText("<html><h4>Syntax Description:</h4></html>:");
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
            .add(0, 60, Short.MAX_VALUE)
        );

        dlgDescription.getContentPane().add(pnlDescJunk, java.awt.BorderLayout.LINE_START);

        setOpaque(false);

        pnlSelectedConnector.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Connector"));
        pnlSelectedConnector.setOpaque(false);

        lblName.setText("Name");

        lblArity.setText("Arity");

        txfName.setText("<Name>");

        txfArity.setText("<Arity>");

        chkbxAssociative.setText("Associative");
        chkbxAssociative.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        chkbxAssociative.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chkbxAssociative.setOpaque(false);

        lblOutputFormat.setText("Display");

        lblPriority.setText("Priority");

        lblComments.setText("Comments");

        txfOutputFormat.setText("<Output Format>");

        txfPriority.setText("<Priority>");

        lblShowHideComments.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/show.png"))); // NOI18N
        lblShowHideComments.setToolTipText("Show/Hide Comments");
        lblShowHideComments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblShowHideCommentsMouseClicked(evt);
            }
        });

        txaComments.setColumns(20);
        txaComments.setLineWrap(true);
        txaComments.setRows(5);
        txaComments.setText("<Comments>");
        txaComments.setWrapStyleWord(true);
        txaComments.setMargin(new java.awt.Insets(1, 5, 2, 4));
        txaComments.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txaCommentsMouseClicked(evt);
            }
        });
        scrlComments.setViewportView(txaComments);

        org.jdesktop.layout.GroupLayout pnlSelectedConnectorLayout = new org.jdesktop.layout.GroupLayout(pnlSelectedConnector);
        pnlSelectedConnector.setLayout(pnlSelectedConnectorLayout);
        pnlSelectedConnectorLayout.setHorizontalGroup(
            pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedConnectorLayout.createSequentialGroup()
                .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(lblName)
                    .add(lblOutputFormat))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(chkbxAssociative)
                    .add(pnlSelectedConnectorLayout.createSequentialGroup()
                        .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txfName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .add(txfOutputFormat, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(lblPriority)
                            .add(lblArity))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(txfArity, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                            .add(txfPriority, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
                        .addContainerGap())))
            .add(pnlSelectedConnectorLayout.createSequentialGroup()
                .add(lblComments)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(lblShowHideComments))
            .add(pnlSelectedConnectorLayout.createSequentialGroup()
                .addContainerGap()
                .add(scrlComments, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlSelectedConnectorLayout.setVerticalGroup(
            pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlSelectedConnectorLayout.createSequentialGroup()
                .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblName)
                    .add(txfName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblArity)
                    .add(txfArity, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblOutputFormat)
                    .add(txfOutputFormat, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(lblPriority)
                    .add(txfPriority, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(chkbxAssociative)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedConnectorLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lblComments)
                    .add(lblShowHideComments))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(scrlComments, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pnlConnList.setBorder(javax.swing.BorderFactory.createTitledBorder("Connectors List"));
        pnlConnList.setToolTipText("Conn List Panel");
        pnlConnList.setOpaque(false);

        lstConnectorsList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstConnectorsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstConnectorsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstConnectorsListValueChanged(evt);
            }
        });
        lstConnectorsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstConnectorsListMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstConnectorsList);

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

        org.jdesktop.layout.GroupLayout pnlConnListLayout = new org.jdesktop.layout.GroupLayout(pnlConnList);
        pnlConnList.setLayout(pnlConnListLayout);
        pnlConnListLayout.setHorizontalGroup(
            pnlConnListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pnlAED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
        );
        pnlConnListLayout.setVerticalGroup(
            pnlConnListLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pnlConnListLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlAED, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(pnlConnList, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pnlSelectedConnector, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlSelectedConnector, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pnlConnList, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void lstConnectorsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstConnectorsListMouseClicked
        if (evt.getClickCount() == 2) {
            //This code must be updated all the way with edit-button click
            editedConNum = lastSelected;
            Connector conn = (Connector) logicDefTab.getLogic().getConnectors().get(editedConNum);
//            System.out.println("conn " + conn.getName() + " will be edited");
            showEditionDialog();
        }
    }//GEN-LAST:event_lstConnectorsListMouseClicked

    private void txaCommentsInMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txaCommentsInMouseClicked
    // TODO add your handling code here:
}//GEN-LAST:event_txaCommentsInMouseClicked

    private void txaCommentsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txaCommentsMouseClicked
        
    }//GEN-LAST:event_txaCommentsMouseClicked

    private void dlgEditWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dlgEditWindowClosing
        cancelEdition();
    }//GEN-LAST:event_dlgEditWindowClosing

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        //This code must be updated all the way with lstConnector mouse click
        editedConNum = lastSelected;
        Connector conn = (Connector) logicDefTab.getLogic().getConnectors().get(editedConNum);
//        System.out.println("conn " + conn.getName() + " will be edited");
        showEditionDialog();
}//GEN-LAST:event_btnEditActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        editedConNum = -1;
//        System.out.println("new conn will be added");
        showEditionDialog();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        deleteCon();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        if (editedConNum == -1) {
            addNew();
        } else {
            saveEdition();
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        cancelEdition();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void lstConnectorsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstConnectorsListValueChanged
        lastSelected = lstConnectorsList.getSelectedIndex();
//        System.out.println("lastSelected now equals " + lastSelected);
        displaySelectedConnector(); 
    }//GEN-LAST:event_lstConnectorsListValueChanged

    private void lblDescMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDescMouseClicked
        dlgDescription.setVisible(false);
    }//GEN-LAST:event_lblDescMouseClicked

    private void lblDescTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblDescTitleMouseClicked
        dlgDescription.setVisible(false);
    }//GEN-LAST:event_lblDescTitleMouseClicked

    private void pnlDescJunkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlDescJunkMouseClicked
        dlgDescription.setVisible(false);
    }//GEN-LAST:event_pnlDescJunkMouseClicked

    private void initializeShowHidePredefList() {
        tglbtnPredefinedList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/arrow-right.png"))); // NOI18N
        tglbtnPredefinedList.setText("Show List");
        tglbtnPredefinedList.setSelected(false);
        pnlShowHidePredefList.setVisible(false);
        pnlPredefConnList.setVisible(false);
        dlgEdit.pack();
    }

    private void showShowHidePredefList() {
        pnlShowHidePredefList.setVisible(true);
        if (tglbtnPredefinedList.isSelected()) {
            showPredefConnList();
        }
        dlgEdit.pack();
    }

    private void hideShowHidePredefList() {
        pnlShowHidePredefList.setVisible(false);
        pnlPredefConnList.setVisible(false);
        dlgEdit.pack();
    }

    private void showPredefConnList() {
        fillLogicsList();
        cmbxLoadedLogics.setSelectedItem(logicDefTab.getLogic().getName());
        fillPredefConnList(logicDefTab.getLogic());
        cmbxLoadedLogics.getSelectedIndex();
        pnlPredefConnList.setVisible(true);
        dlgEdit.pack();
    }

    private void hidePredefConnList() {
        pnlPredefConnList.setVisible(false);
        dlgEdit.pack();
    }

    private void tglbtnPredefinedListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tglbtnPredefinedListActionPerformed
        if (tglbtnPredefinedList.isSelected()) {
            tglbtnPredefinedList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/arrow-left.png"))); // NOI18N
            tglbtnPredefinedList.setText("Hide List");
            // No need to fillPredefConnList();
            // It's done auto after each refreshConnList()...
            showPredefConnList();
        } else {
            tglbtnPredefinedList.setIcon(new javax.swing.ImageIcon(getClass().getResource("/lotrec/images/arrow-right.png"))); // NOI18N
            tglbtnPredefinedList.setText("Show List");
            hidePredefConnList();
        }
    }//GEN-LAST:event_tglbtnPredefinedListActionPerformed

    private void fillLogicsList() {
        javax.swing.DefaultComboBoxModel comboModel = new javax.swing.DefaultComboBoxModel();
        ArrayList loadedLogics = logicDefTab.getMainFrame().getLoadedLogicsPanel().getLoadedLogics();
        for (int i = 0; i < loadedLogics.size(); i++) {
            String logicName = ((Logic) loadedLogics.get(i)).getName();
            comboModel.addElement(logicName);
        }
        cmbxLoadedLogics.setModel(comboModel);
    }

    private void fillPredefConnList(Logic logic) {
        DefaultListModel listModel = new DefaultListModel();
        if (logic.getConnectors() != null && logic.getConnectors().size() != 0) {
            for (int i = 0; i < logic.getConnectors().size(); i++) {
                listModel.addElement(((Connector) logic.getConnectors().get(i)).getName());
            }
        } else {
            listModel.addElement("list is empty..");
        }
        lstPredefConnList.setModel(listModel);
    }

    private void lstPredefConnListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstPredefConnListMouseClicked
        if (evt.getClickCount() == 2) {
            Logic chosenLogic = logicDefTab.getMainFrame().getLoadedLogicsPanel().getLoadedLogics().get(cmbxLoadedLogics.getSelectedIndex());
            if (chosenLogic.getConnectors().size() > 0) {
                fillEditionDialogWith(chosenLogic.getConnectors().get(lstPredefConnList.getSelectedIndex()));
            }
        }
}//GEN-LAST:event_lstPredefConnListMouseClicked

    private void cmbxLoadedLogicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxLoadedLogicsActionPerformed
        fillPredefConnList(logicDefTab.getMainFrame().getLoadedLogicsPanel().getLoadedLogics().get(cmbxLoadedLogics.getSelectedIndex()));
    }//GEN-LAST:event_cmbxLoadedLogicsActionPerformed

//    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
//        try {
//            jEditorPane1 = new javax.swing.JEditorPane(this.getClass().getResource("/lotrec/images/Untitled-1.html"));
//            jScrollPane2.setViewportView(jEditorPane1);
//            jDialog1.pack();
//            jDialog1.setVisible(true);
//        } catch (IOException ex) {
//            Logger.getLogger(ConnTabPanel.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chkbxAssociative;
    private javax.swing.JCheckBox chkbxAssociativeIn;
    private javax.swing.JComboBox cmbxLoadedLogics;
    private javax.swing.JDialog dlgDescription;
    private javax.swing.JDialog dlgEdit;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblArity;
    private javax.swing.JLabel lblArityIn;
    private javax.swing.JLabel lblComments;
    private javax.swing.JLabel lblCommentsIn;
    private javax.swing.JLabel lblDesc;
    private javax.swing.JLabel lblDescTitle;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblNameIn;
    private javax.swing.JLabel lblOutputFormat;
    private javax.swing.JLabel lblOutputFormatIn;
    private javax.swing.JLabel lblPrioritIn;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblShowHideComments;
    private javax.swing.JList lstConnectorsList;
    private javax.swing.JList lstPredefConnList;
    private javax.swing.JPanel pnlAED;
    private javax.swing.JPanel pnlConEdit;
    private javax.swing.JPanel pnlConnList;
    private javax.swing.JPanel pnlDescJunk;
    private javax.swing.JPanel pnlPredefConnList;
    private javax.swing.JPanel pnlSaveCancel;
    private javax.swing.JPanel pnlSelectedConnector;
    private javax.swing.JPanel pnlShowHidePredefList;
    private javax.swing.JScrollPane scrlComments;
    private javax.swing.JScrollPane scrlCommentsIn;
    private javax.swing.JToggleButton tglbtnPredefinedList;
    private javax.swing.border.TitledBorder titledBorderIn;
    private javax.swing.JTextArea txaComments;
    private javax.swing.JTextArea txaCommentsIn;
    private javax.swing.JTextField txfArity;
    private javax.swing.JTextField txfArityIn;
    private javax.swing.JTextField txfName;
    private javax.swing.JTextField txfNameIn;
    private javax.swing.JTextField txfOutputFormat;
    private javax.swing.JTextField txfOutputFormatIn;
    private javax.swing.JTextField txfPriority;
    private javax.swing.JTextField txfPriorityIn;
    // End of variables declaration//GEN-END:variables
}
