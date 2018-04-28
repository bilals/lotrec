/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gi.transformers;

import gi.*;

/**
 *
 * @author said
 */
public class ManualPriority extends SLR1_Grammar {

    public static final int DEFAULT_DECORATOR = 0;
    public static final int FIRST_LETTER_TO_LOWER_CASE_DECORATOR = 1;
    public static final int FIRST_LETTER_TO_UPPER_CASE_DECORATOR = 2;
    private int outDecorator = PriorityInfixToInfix.DEFAULT_DECORATOR;

    public static void main(String[] a) throws Exception {
        ManualPriority i2i = new ManualPriority();
        i2i.compile();
        ParseTree pTree = (ParseTree) i2i.interpret(new String("( []([] p -> [] q) -> [] []([] p -> [] q) )"));
//        ParseTree pTree = (ParseTree) i2i.interpret(new String("[](H -> C ^ <>W)"));
//        ParseTree pTree = (ParseTree) i2i.interpret(new String("[] p -> [] q ^ <> r"));
        System.out.println(pTree.value);
    }

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

    private void compile() throws Exception {
        put("Constant", expression("[[:alpha:]]?([[:alpha:]]|[[:digit:]])*"));
        put("SPACE", expression("[[:space:]]+"));

        Semantics constant = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                if (outDecorator == PriorityInfixToInfix.FIRST_LETTER_TO_LOWER_CASE_DECORATOR) {
                    t.value = firstLetterToLowerCase(t.child[l - 1].value.toString());
                } else if (outDecorator == PriorityInfixToInfix.FIRST_LETTER_TO_UPPER_CASE_DECORATOR) {
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

        Semantics identity = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = t.child[l - 1].value;
            }
        };
        Semantics box = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = "(" + "[]" + t.child[l - 1].value + ")";
            }
        };
        Semantics diamond = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = "(" + "<>" + t.child[l - 1].value + ")";
            }
        };        
        
        Semantics and = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = "(" + t.child[l - 3].value + " ^ " + t.child[l - 1].value + ")";
            }
        };        

        Semantics imp = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = "(" + t.child[l - 3].value + " -> " + t.child[l - 1].value + ")";
            }
        };        
        put("F", new Object[][]{
            
                    {"F0", identity}
                });
        put("F0", new Object[][]{        
                    
                    {"F0", "->", "F1", imp},
                    {"F1", identity},
                });
        put("F1", new Object[][]{
                    {"F1","^", "F2", and},
                    {"F2", identity},
                });
        put("F2", new Object[][]{
                    {"[]", "F2", box},
                    {"<>", "F2", diamond},
                    {"F3", identity},
                });
//        put("F3", new Object[][]{
//                    {"<>", "F4", diamond},
//                    {"F4", identity},
//                });
        put("F3", new Object[][]{
                    {"Constant", constant},
//                    { "F0", identity},   
                    {"(", "F", ")", parentheses},
//                    {"(", "F", ")", parentheses}
                });
    }
}
