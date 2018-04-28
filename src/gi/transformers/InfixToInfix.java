package gi.transformers;

import java.util.ArrayList;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will provide information about the context free grammar (CFG) 
 * in wich the transformed formulas are defined.
 * @author Bilal Said
 */
public class InfixToInfix extends FormulaTransformer {

    public InfixToInfix(ArrayList<Connector> connectors) {
        super(connectors);
    }

    public InfixToInfix() {
        super();
    }

    /**
     * Analyzes the connector output c.getOutput() which is its infix form.
     * It replaces each Connector.SPECIAL_CHARACTER occurence by FormulaTransformer.FORMULA
     * For example: for the connector c where c.getOutput().equals("(_->_)"), the returned set of strings is:
     * {"(",FormulaTransformer.FORMULA,"->",FormulaTransformer.FORMULA,")"}
     * @param Connector c the connector that should be analyzed.
     * @return the set of Strings that should be recognized.
     */
    protected ArrayList<String> getInputFormat(Connector c) {
        Pattern p = Pattern.compile(String.valueOf(Connector.SPECIAL_CHARACTER));
        Matcher m = p.matcher(c.getOutput());
        StringBuffer sb = new StringBuffer();
        ArrayList<String> inputParts = new ArrayList<String>();
        while (m.find()) {
            m.appendReplacement(sb, "");
            if (!sb.toString().equals("")) {
                inputParts.add(sb.toString());
            }
            inputParts.add(FormulaTransformer.FORMULA);
            sb.setLength(0);
        }
        m.appendTail(sb);
        if (!sb.toString().equals("")) {
            inputParts.add(sb.toString());
        }
        sb.setLength(0);
        return inputParts;
    }

    /**
     * Should return the desired output format as set of String objects. This set is treated by getSemantics() method as following:
     * 1- FormulaTransformer.FORMULA elements are replaced by the corresponding values from the ParseTree
     * 2- The other String objects are considered as constants, and thus copied as they are.
     * @param c
     * @return
     */
    @Override
    protected ArrayList<String> getOutputFormat(Connector c) {
        return getInputFormat(c);
    }
}
