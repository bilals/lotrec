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

class Logic3 extends SLR1_Grammar {

    Logic3() throws Exception {

        put("CONST", expression("[[:upper:]]+"));
        put("SPACE", expression("[[:space:]]+"));

        Semantics print = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                System.out.println(t.child[l - 1].value);
            }
        };

        Semantics decode = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = (String) t.child[l - 1].value; //Integer.decode((String) t.child[l - 1].value);
            }
        };

        Semantics identity = new Semantics() {

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
                t.value = " (" + left + " ^ " + right + ") "; //new Integer(left + right);
            }
        };

        Semantics box = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                //int left = ((Integer) t.child[l - 3].value).intValue();
                //int right = ((Integer) t.child[l - 1].value).intValue();
                t.value = " ([] " + t.child[l - 1].value + ") ";//new Integer(left * right);
            }
        };
        
        put("E", new Object[][]{
                    {"F", print}
                });        

        put("F", new Object[][]{
                    {"(", "F", /*\semantics*/ identity, /*\off*/ ")"},
                    {"[]", "F"/*\semantics*/, box/*\off*/},
                    {"F", "^", "F"/*\semantics*/, and/*\off*/},
                    {"CONST"/*\semantics*/, decode/*\off*/}
                });


        debug = PARSE_TREE;
    }

    public static void main(String[] arguments) throws Exception {
        new Logic().interpret(arguments);
    }
}
