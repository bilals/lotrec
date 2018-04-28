/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.gui.logicspane;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import lotrec.dataStructure.tableau.Rule;

/**
 *
 * @author said
 */
public class RulesListTransferable implements Transferable {

    public static DataFlavor rulesListFlavor = new DataFlavor(ArrayList.class, "RuleListFlavor");
    private ArrayList<Rule> rulesList;

    public RulesListTransferable(ArrayList<Rule> rulesList) {
        this.rulesList = rulesList;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{rulesListFlavor, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(RulesListTransferable.rulesListFlavor) || flavor.equals(DataFlavor.stringFlavor)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(RulesListTransferable.rulesListFlavor)) {
            return this.rulesList;
        }
        if (flavor.equals(DataFlavor.stringFlavor)) {
            StringBuffer code= new StringBuffer("");
            for(Rule rule: rulesList){
                code.append(rule.getCode()+"\n\n");
            }
            code.deleteCharAt(code.lastIndexOf("\n"));
            code.deleteCharAt(code.lastIndexOf("\n"));
            return code.toString();
        }
        throw new UnsupportedFlavorException(flavor);
    }
}
