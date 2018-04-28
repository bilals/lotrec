package lotrec.gui.logicspane;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.ParameterType;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.gui.DialogsFactory;
import lotrec.process.AbstractAction;

public class RulesListTransferHandler extends TransferHandler {

    private RulesTabPanel ruleTabPanel;

    public RulesListTransferHandler(RulesTabPanel rulesTabPanel) {
        this.ruleTabPanel = rulesTabPanel;
    }

    /**
     * Perform the actual data import.
     */
    @Override
    public boolean importData(TransferHandler.TransferSupport info) {
        //If we can't handle the import, bail now.
        if (!canImport(info)) {
            return false;
        }
        //Get the target component
        JList list = (JList) info.getComponent();
        ArrayList<Rule> rulesList = null;
        try {
            rulesList = (ArrayList<Rule>) info.getTransferable().getTransferData(RulesListTransferable.rulesListFlavor);
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("importData: unsupported data flavor" + ufe.getMessage());
            return false;
        } catch (IOException ioe) {
            System.out.println("importData: I/O exception" + ioe.getMessage());
            return false;
        }
        Logic logic = ruleTabPanel.getLogicDefTab().getLogic();
        boolean returnValue = true;
        if (rulesList != null) {
            for (Rule rule : rulesList) {
                boolean exitForRules = false;
                for (AbstractCondition c : rule.getConditions()) {
                    for (Parameter p : c.getParameters()) {
                        if (p.getType().equals(ParameterType.FORMULA) || p.getType().equals(ParameterType.RELATION)) {
                            ArrayList<Connector> usedConnectors = ((Expression) p.getValue()).getUsedConnectors();
                            for (Connector con : usedConnectors) {
                                if (logic.getConnector(con.getName()) == null) {
                                    DialogsFactory.syntaxErrorMessage(list, "Cannot paste this rule.\n" +
                                            "A fomrmula in the condition " + c.getCode() + "\n" +
                                            "is using the undefined connector \"" + con.getName() + "\"\n" +
                                            "Please define this connector first.");
                                    exitForRules = true;
                                    break;
                                } else {
                                    Connector logicCon = logic.getConnector(con.getName());
                                    if (logicCon.getArity() != con.getArity()) {
                                        DialogsFactory.syntaxErrorMessage(list, "Cannot paste this rule.\n" +
                                                "A fomrmula in the condition " + c.getCode() + "\n" +
                                                "is using the connector \"" + con.getName() + "\" with arity " + con.getArity() + "\n" +
                                                "a connector with the same name is already defined in the current logic" + "\n" +
                                                "but with an arity " + logicCon.getArity());
                                        exitForRules = true;
                                        break;
                                    }
                                }
                            }
                            if (exitForRules) {
                                break;
                            }
                        }
                    }
                    if (exitForRules) {
                        break;
                    }
                }
                if (exitForRules) {
                    continue;
                }
                for (AbstractAction a : rule.getActions()) {
                    for (Parameter p : a.getParameters()) {
                        if (p.getType().equals(ParameterType.FORMULA) || p.getType().equals(ParameterType.RELATION)) {
                            ArrayList<Connector> usedConnectors = ((Expression) p.getValue()).getUsedConnectors();
                            for (Connector con : usedConnectors) {
                                if (logic.getConnector(con.getName()) == null) {
                                    DialogsFactory.syntaxErrorMessage(list, "Cannot paste this rule.\n" +
                                            "A fomrmula in the action " + a.getCode() + "\n" +
                                            "is using the undefined connector \"" + con.getName() + "\"\n" +
                                            "Please define this connector first.");
                                    exitForRules = true;
                                    break;
                                } else {
                                    Connector logicCon = logic.getConnector(con.getName());
                                    if (logicCon.getArity() != con.getArity()) {
                                        DialogsFactory.syntaxErrorMessage(list, "Cannot paste this rule.\n" +
                                                "A fomrmula in the action " + a.getCode() + "\n" +
                                                "is using the connector \"" + con.getName() + "\" with arity " + con.getArity() + "\n" +
                                                "a connector with the same name is already defined in the current logic" + "\n" +
                                                "but with an arity " + logicCon.getArity());
                                        exitForRules = true;
                                        break;
                                    }
                                }
                            }

                            if (exitForRules) {
                                break;
                            }
                        }
                    }

                    if (exitForRules) {
                        break;
                    }
                }
                if (exitForRules) {
                    continue;
                }
                String newName = rule.getName();
                int i = 2;
                while (logic.isRoutineName(newName) ||
                        logic.isStrategyName(newName) ||
                        logic.isRuleName(newName)) {
                    newName = rule.getName() + "_" + i;
                    i++;
                }
                // In case you copy/paste Rule_x you will have Rule_x_x
                // it is a little bit ugly, but still rare to have it
                if (logic.isRoutineName(rule.getName())) {
                    DialogsFactory.syntaxWarningMessage(list,
                            "The rule name " + rule.getName() + " is a reserved keyword for routines.\n" +
                            "We are changing its name to \"" + newName + "\"");
                    rule.setName(newName);
                }
                if (logic.isStrategyName(rule.getName())) {
                    DialogsFactory.syntaxWarningMessage(list,
                            "The rule name " + rule.getName() + " is already identifying a strategy in the current logic.\n" +
                            "We are changing its name to \"" + newName + "\"");
                    rule.setName(newName);
                }
                // If you make Drag&Drop of a rule inside the rules list the following will be true:
                // 1- It is a MOVE
                // 2- It is a Drop
                // 3- and the ruleTabPanel was the source of the last Drag action
                //
                // In this case, the exportData() will find that the same rule name already exits
                // but this should be tolerated since the rule is being MOVED
                // and then, it will be deleted later by the exportDone(..) method
                if (logic.isRuleName(rule.getName()) &&
                        !(info.isDrop() &&
                        (info.getDropAction() & TransferHandler.MOVE) == TransferHandler.MOVE &&
                        this.ruleTabPanel.wasSourceOfDrag())) {
                    DialogsFactory.syntaxWarningMessage(list,
                            "The rule name " + rule.getName() + " is already identifying another rule in the current logic.\n" +
                            "We are changing its name to \"" + newName + "\"");
                    rule.setName(newName);
                }
                if (info.isDrop()) { //This is a drop
                    JList.DropLocation dl = (JList.DropLocation) info.getDropLocation();
                    int index = dl.getIndex();
                    if (dl.isInsert()) {
//                System.out.println("The rule " + rule.getName() + " should be added to logic " + logic.getName() + " at position " + index);
                        logic.addRule(index, rule);
                        ruleTabPanel.refreshRulesList();
                        list.setSelectedIndex(index);
                        ruleTabPanel.displaySelectedRule();
                    } else {
//                model.set(index, data);
//                System.out.println("The rule " + rule.getName() + " should replace the rule " + logic.getRules().get(index) + " in the logic " + logic.getName() + " at position " + index);
                        returnValue = false;
                    }
                } else { //This is a paste
                    int index = list.getSelectedIndex();
                    // if there is a valid selection,
                    // insert data after the selection
                    if (index >= 0) {
                        logic.addRule(list.getSelectedIndex() + 1, rule);
                    // else append to the end of the list
                    } else {
                        logic.addRule(rule);
                    }
                    ruleTabPanel.refreshRulesList();
                    list.setSelectedIndex(index + 1);
                    ruleTabPanel.displaySelectedRule();
                }
            }
            return returnValue;
        } else {
            return false;
        }
    }

    /**
     * Bundle up the data for export.
     *
     * The type of the returned transferable
     * should be "Rule". If you want to change it,
     * you should make the convenient changes
     * in the exportDone() method
     */
    @Override
    protected Transferable createTransferable(JComponent c) {
        JList list = (JList) c;
        ArrayList<Rule> rulesList = new ArrayList<Rule>();
        for (int i : list.getSelectedIndices()) {
            rulesList.add(this.ruleTabPanel.getLogicDefTab().getLogic().getRules().get(i));
        }
        this.ruleTabPanel.setWasSourceOfDrag(true);
        return new RulesListTransferable(rulesList);

    }

    /**
     * The list handles both copy and move actions.
     */
    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    //MOVE is important to be able to make drag n' drop gesture to reorder the rules.
//        return TransferHandler.COPY;
    }

    /**
     * When the export is complete, nothing happens
     * I assume that dragNdrop is permitted only to re-order the rules within the same rulesList...
     * //remove the old list entry if the
     * //action was a move.
     */
    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        this.ruleTabPanel.setWasSourceOfDrag(false);
        if (action != TransferHandler.MOVE) {
            return;
        }
        JList list = (JList) c;
        ArrayList<Rule> ruleList = null;
        Logic logic = ruleTabPanel.getLogicDefTab().getLogic();
        RulesListTransferable rulesListTransferable = (RulesListTransferable) data;
        try {
            ruleList = (ArrayList<Rule>) rulesListTransferable.getTransferData(RulesListTransferable.rulesListFlavor);
        } catch (UnsupportedFlavorException ufe) {
            System.out.println("importData: unsupported data flavor" + ufe.getMessage());
        } catch (IOException ioe) {
            System.out.println("importData: I/O exception" + ioe.getMessage());
        }
        if (ruleList != null) {
            int index = list.getSelectedIndex();
//            System.out.println("Rule " + rule.getName() + " should be deleted form logic " + logic.getName() + " at index " + index);
            logic.getRules().removeAll(ruleList);
            ruleTabPanel.refreshRulesList();
            if (list.getModel().getSize() > 0) {
                if (index == 0) {
                    list.setSelectedIndex(index);
                } else {
                    list.setSelectedIndex(index - 1);
                }
                ruleTabPanel.displaySelectedRule();
            }
        }
    }

    /**
     * We support importing Rule
     */
    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        // we only import Rule
        //support.isDataFlavorSupported(DataFlavor.stringFlavor)
        if (support.isDataFlavorSupported(RulesListTransferable.rulesListFlavor)) {
            return true;
        }
        return false;
    }
}
