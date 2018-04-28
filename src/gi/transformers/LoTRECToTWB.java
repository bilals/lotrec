/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gi.transformers;

import java.util.ArrayList;

/**
 *
 * @author said
 */
public class LoTRECToTWB extends PrefixToInfix {

    public LoTRECToTWB(ArrayList<Connector> connList) {
        super(connList);
    }

    @Override
    protected ArrayList<String> getOutputFormat(Connector c) {
        this.setOutDecorator(FormulaTransformer.FIRST_LETTER_TO_LOWER_CASE_DECORATOR);
        return super.getOutputFormat(c);
    }
}
