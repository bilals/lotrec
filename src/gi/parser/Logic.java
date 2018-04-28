/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gi.parser;

/**
 *
 * @author said
 */
import gi.*;
import java.util.ArrayList;

class Logic extends SLR1_Grammar {

    Logic() throws Exception {

        put("Constant", expression("[[:upper:]]?([[:alpha:]]|[[:digit:]])*"));
        // If we want to make a difrence between two diffrent kind of constants
        // we should use a difrent set of terminals to represent them
        put("AtomicAction", expression("[[:lower:]]+"));
        put("SPACE", expression("[[:space:]]+"));

//        Semantics print = new Semantics() {
//
//            @Override
//            public void f(ParseTree t, int l) {
//                System.out.println(t.child[l - 1].value);
//            }
//        };

        Semantics constant = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = (String) t.child[l - 1].value; //Integer.decode((String) t.child[l - 1].value);
            }
        };

        Semantics parentheses = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = t.child[l - 1].value;
            }
        };

        Semantics and = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                String left = (String) t.child[l - 3].value; //((Integer) t.child[l - 3].value).intValue();
                String right = (String) t.child[l - 1].value; //((Integer) t.child[l - 1].value).intValue();
                t.value = "(" + left + " ^ " + right + ")"; //new Integer(left + right);
            }
        };

        Semantics imp = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                String left = (String) t.child[l - 3].value; //((Integer) t.child[l - 3].value).intValue();
                String right = (String) t.child[l - 1].value; //((Integer) t.child[l - 1].value).intValue();
                t.value = "(" + left + " -> " + right + ")"; //new Integer(left + right);
            }
        };

        Semantics box = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                //int left = ((Integer) t.child[l - 3].value).intValue();
                //int right = ((Integer) t.child[l - 1].value).intValue();
                t.value = "([] " + t.child[l - 1].value + ")";//new Integer(left * right);
            }
        };

        Semantics boxI = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                //int left = ((Integer) t.child[l - 3].value).intValue();
                //int right = ((Integer) t.child[l - 1].value).intValue();
                t.value = "([" + t.child[l - 3].value + "] " + t.child[l - 1].value + ")";//new Integer(left * right);
            }
        };

        Semantics diamond = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                //int left = ((Integer) t.child[l - 3].value).intValue();
                //int right = ((Integer) t.child[l - 1].value).intValue();
                t.value = "(<> " + t.child[l - 1].value + ")";//new Integer(left * right);
            }
        };

        Semantics diamondAction = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                //int left = ((Integer) t.child[l - 3].value).intValue();
                //int right = ((Integer) t.child[l - 1].value).intValue();
                t.value = "(<" + t.child[l - 3].value + "> " + t.child[l - 1].value + ")";//new Integer(left * right);
            }
        };

        Semantics seq = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                String left = (String) t.child[l - 3].value; //((Integer) t.child[l - 3].value).intValue();
                String right = (String) t.child[l - 1].value; //((Integer) t.child[l - 1].value).intValue();
                t.value = "(" + left + " ; " + right + ")"; //new Integer(left + right);
            }
        };
        
        Semantics union = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                String left = (String) t.child[l - 3].value; //((Integer) t.child[l - 3].value).intValue();
                String right = (String) t.child[l - 1].value; //((Integer) t.child[l - 1].value).intValue();
                t.value = "(" + left + " v " + right + ")"; //new Integer(left + right);
            }
        };        

        Semantics star = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                //int left = ((Integer) t.child[l - 3].value).intValue();
                //int right = ((Integer) t.child[l - 1].value).intValue();
                t.value = "(" + t.child[l - 1].value + ")*";//new Integer(left * right);
            }
        };        
        
//        put("E", new Object[][]{
//                    {"Formula", print}
//                });

        ArrayList ruleBox = new ArrayList();
        ruleBox.add("[]");
        ruleBox.add("Formula");
        ruleBox.add(box);
        Object[] array = ruleBox.toArray();
        System.out.println(array[0].toString()+" "+array[1].toString()+" "+array[2].toString());
        put("Formula", new Object[][]{
                    {"(", "Formula", /*\semantics*/ parentheses, /*\off*/ ")"},
                    {"Formula", "->", "Formula"/*\semantics*/, imp/*\off*/},
                    {"Formula", "^", "Formula"/*\semantics*/, and/*\off*/},
                    //{"[]", "Formula"/*\semantics*/, box/*\off*/},
                    {array[0],array[1],array[2]},
                    {"[", "Agent", "]", "Formula"/*\semantics*/, boxI/*\off*/},
                    {"<>", "Formula"/*\semantics*/, diamond/*\off*/},
                    {"<", "Action", ">", "Formula"/*\semantics*/, diamondAction/*\off*/},
                    {"Constant"/*\semantics*/, constant/*\off*/}
                });
        put("Agent", new Object[][]{
                    {"Constant", constant}
                });
        put("Action", new Object[][]{
                    //{"(", "Action", /*\semantics*/ parentheses, /*\off*/ ")"},
                    {"Action", ";", "Action"/*\semantics*/, seq/*\off*/},
                    {"Action", "v", "Action"/*\semantics*/, union/*\off*/},
                    {"Action", "*" /*\semantics*/, star/*\off*/},
                    {"AtomicAction", constant}
                });


        debug = PARSE_TREE;
    }

    public static void main(String[] arguments) throws Exception {
        new Logic().interpret(new String("(<i> PTT->P2t) ^ [] (Q ^ [I] S)"));
    }
}
