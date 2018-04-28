/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.gui.logicspane;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import lotrec.dataStructure.tableau.Rule;

/**
 *
 * @author said
 */
public class RuleTransferable implements Transferable {

    public static DataFlavor ruleFlavor = new DataFlavor(Rule.class, "RuleFlavor");
    private Rule rule;

    public RuleTransferable(Rule rule) {
        this.rule = rule;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{ruleFlavor, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        if (flavor.equals(RuleTransferable.ruleFlavor) || flavor.equals(DataFlavor.stringFlavor)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(RuleTransferable.ruleFlavor)) {
            return this.rule;
        }
        if (flavor.equals(DataFlavor.stringFlavor)) {
            return this.rule.getCode();
        }
        throw new UnsupportedFlavorException(flavor);
    }
}
