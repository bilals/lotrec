package gi.transformers;

import java.util.ArrayList;


/**
 * This class will provide information about the context free grammar (CFG) 
 * in wich the transformed formulas are defined.
 * @author Bilal Said
 */
public class PrefixToPrefix extends FormulaTransformer {

    public PrefixToPrefix(ArrayList<Connector> connectors) {
        super(connectors);
    }

    public PrefixToPrefix() {
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
        int argsCount = c.getArity(); // should be equal to the number of FormulaTransformer.FORMULA occurences in the c.getOutput()
        ArrayList<String> inputParts = new ArrayList<String>();
        inputParts.add(c.getName());
        for (int i = 0; i < argsCount; i++) {
            inputParts.add(FormulaTransformer.FORMULA);
        }
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
        int argsCount = c.getArity(); // should be equal to the number of FormulaTransformer.FORMULA occurences in the c.getOutput()
        ArrayList<String> outputParts = new ArrayList<String>();
        outputParts.add(c.getName());
        for (int i = 0; i < argsCount; i++) {
            outputParts.add(FormulaTransformer.FORMULA);
        }
        return outputParts;
    }
}
