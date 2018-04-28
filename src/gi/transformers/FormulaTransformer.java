/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gi.transformers;

import gi.*;
import java.util.ArrayList;

/**
 *
 * @author said
 */
public abstract class FormulaTransformer extends SLR1_Grammar {

    public static void main(String[] a) throws Exception {
        ArrayList<Connector> connList = new ArrayList<Connector>();
        connList.add(new Connector("not", 1, "~_", 2));
        connList.add(new Connector("nec", 1, "[]_", 2));
        connList.add(new Connector("pos", 1, "<>_", 2));
        connList.add(new Connector("and", 2, "_^_", 1));
        connList.add(new Connector("or", 2, "_v_", 1));
        connList.add(new Connector("imp", 2, "_->_", 0));
        FormulaTransformer i2i = new InfixToInfix(connList);
//        FormulaTransformer i2i = new PrefixToInfix(connList);
        i2i.compile();
        ParseTree pTree = (ParseTree) i2i.interpret(new String("[](H -> (~C) v <>W)"));
//        ParseTree pTree = (ParseTree) i2i.interpret(new String("pos nec imp or W C H"));
        System.out.println(pTree.value);
    }
    public static final String FORMULA = "Formula";
    public static final int DEFAULT_DECORATOR = 0;
    public static final int FIRST_LETTER_TO_LOWER_CASE_DECORATOR = 1;
    public static final int FIRST_LETTER_TO_UPPER_CASE_DECORATOR = 2;
    private ArrayList<Connector> connectors;
    private int outDecorator = FormulaTransformer.DEFAULT_DECORATOR;

    protected FormulaTransformer() {
        this.connectors = new ArrayList<Connector>();
    }

    protected FormulaTransformer(ArrayList<Connector> connectors) {
        this.connectors = connectors;
    }

    public void add(Connector c) {
        connectors.add(c);
    }

    public String transform(String formula) throws Exception {
        ParseTree parseTree = (ParseTree) interpret(formula);
        return parseTree.value.toString();
    }

    /**
     * Analyzes the output format of the connector c and gives back the suitable Semantics object, whose void f(...) method is put in the connector production in order to be called by the interpreter.
     * Actually, the f(...) method of the returned Semantic object assigns to the ParseTree, corresponding to the connector c, a String value calculated as following:
     * 1- A StringBufffer sb is initialized to "("
     * 2- constant Strings from the output format are appended to sb
     * 3- the ith occurence of FormulaTransformer.FORMULA in the output format is replaced by the corresponding ith FormulaTransformer.FORMULA child in the ParseTree, then appended to sb
     * 4- ")" is appended to sb, then sb.toString() is assigned 
     * @param c
     * @return Semantics object, whose void f(...) method is put in the connector production in order to be called by the interpreter
     */
    protected Semantics getSemantics(Connector c) {
        final ArrayList<String> copyOutParts = this.getOutputFormat(c);
        Semantics semantics = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                StringBuffer sb = new StringBuffer();
                sb.append("(");
                int lastUsedChild = -1;
                int partNum = 0;
                for (String outPart : copyOutParts) {
                    if (outPart.startsWith(FormulaTransformer.FORMULA)) {
                        String arg = outPart;
                        for (int i = 0; i < t.child.length; i++) {
                            Object o = t.child[i].root;
                            if (o != null && o instanceof String) {
                                String s = (String) o;
                                if (s.equals(arg)) {
                                    if (i <= lastUsedChild) {
                                        continue;
                                    } else {
                                        lastUsedChild = i;
                                        sb.append(t.child[i].value.toString());
                                        break;
                                    }
                                }
                            }
                        }
                    } else {
                        sb.append(outPart);
                    }
                    if (partNum != (copyOutParts.size() - 1)) {
                        sb.append(" ");
                    }
                    partNum++;
                }
                sb.append(")");
                t.value = sb.toString();
            }
        };
        return semantics;
    }

    /**
     * Analyzes the input format of the connector c and returns a suitable production to the FormulaTransformer.
     * @param c an instance of Connector
     * @return the production representing the input format of c.
     */
    protected Object[] getProduction(Connector c) {
        ArrayList<String> inputFormat = getInputFormat(c);
        Object[] production = new Object[inputFormat.size() + 1];
        for (int j = 0; j < inputFormat.size(); j++) {
            production[j] = inputFormat.get(j);
        }
        production[inputFormat.size()] = getSemantics(c);
        return production;
    }

    protected abstract ArrayList<String> getOutputFormat(Connector c);

    protected abstract ArrayList<String> getInputFormat(Connector c);

    private String firstLetterToUpperCase(String STRING) {
        String sTRING;
        if (STRING.length() == 0) {
            sTRING = STRING;
        } else {
            sTRING = STRING.substring(0, 1).toUpperCase().concat(STRING.substring(1, STRING.length()));
        }
        return sTRING;
    }

    private String firstLetterToLowerCase(String STRING) {
        String sTRING;
        if (STRING.length() == 0) {
            sTRING = STRING;
        } else {
            sTRING = STRING.substring(0, 1).toLowerCase().concat(STRING.substring(1, STRING.length()));
        }
        return sTRING;
    }

    public void compile() throws Exception {

//        put("Constant", expression("[[:upper:]]?([[:alpha:]]|[[:digit:]])*"));
        put("Constant", expression("[[:alpha:]]?([[:alpha:]]|[[:digit:]])*"));
        put("SPACE", expression("[[:space:]]+"));

        Semantics constant = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                if (outDecorator == FormulaTransformer.FIRST_LETTER_TO_LOWER_CASE_DECORATOR) {
                    t.value = firstLetterToLowerCase(t.child[l - 1].value.toString());
                } else if (outDecorator == FormulaTransformer.FIRST_LETTER_TO_UPPER_CASE_DECORATOR) {
                    t.value = firstLetterToUpperCase(t.child[l - 1].value.toString());
                } else {
                    t.value = t.child[l - 1].value.toString();
                }
            }
        };
        Semantics parentheses = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = t.child[l - 2].value;
            }
        };

        Object[][] rules = new Object[connectors.size() + 2][];
        rules[0] = new Object[]{
                    "(", FormulaTransformer.FORMULA, ")", parentheses,
                };

        for (int i = 0; i < connectors.size(); i++) {
            rules[i + 1] = getProduction(connectors.get(i));
        }
        rules[connectors.size() + 1] = new Object[]{
                    "Constant", constant
                };
        put(FormulaTransformer.FORMULA, rules);

        debug = PARSE_TREE;
    }

    public int getOutDecorator() {
        return outDecorator;
    }

    public void setOutDecorator(int outDecorator) {
        this.outDecorator = outDecorator;
    }
}
