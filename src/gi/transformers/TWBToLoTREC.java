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
public class TWBToLoTREC extends InfixToPrefix {

    TWBToLoTREC(ArrayList<Connector> connList) {
        super(connList);
    }

    @Override
    protected ArrayList<String> getOutputFormat(Connector c) {
        this.setOutDecorator(FormulaTransformer.FIRST_LETTER_TO_UPPER_CASE_DECORATOR);
        return super.getOutputFormat(c);
    }    
}
