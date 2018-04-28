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

class Logic4 extends SLR1_Grammar {

    Logic4() throws Exception {
        //put("CON", expression("[[:lower:]]+"));        
        put("CONST", expression("[[:upper:]]+"));
        put("SPACE", expression("[[:space:]]+"));

        // /*\semantics*/semantic specification/*\off*/
        //Logic.E
        /*\semantics*/
        Semantics print = new Semantics() {
            @Override
            public void f(ParseTree t, int l) {
                System.out.println(t.child[l - 1].value);
            }
        };
        /*\off*/
        put("E", new Object[][]{
                    {"C"/*\semantics*/, print/*\off*/}
                });
        //Logic.C
        /*\semantics*/
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
                t.value = " ("+left + " ^ " + right+") "; //new Integer(left + right);
            }
        };
        Semantics box = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                //int left = ((Integer) t.child[l - 3].value).intValue();
                //int right = ((Integer) t.child[l - 1].value).intValue();
                t.value = " ([] " + t.child[l - 1].value+") ";//new Integer(left * right);
            }
        };
        /*\off*/
        put("C", new Object[][]{
                    {"T"/*\semantics*/, identity/*\off*/},
                    {"E", "^", "T"/*\semantics*/, and/*\off*/},
                });
        put("T", new Object[][]{
                    {"F"/*\semantics*/, identity/*\off*/},
                    {"[]", "F"/*\semantics*/, box/*\off*/},
                });
        //Logic.F
        /*\semantics*/
        Semantics decode = new Semantics() {

            @Override
            public void f(ParseTree t, int l) {
                t.value = (String) t.child[l - 1].value; //Integer.decode((String) t.child[l - 1].value);
            }
        };
        /*\off*/
        put("F", new Object[][]{
                    {"CONST"/*\semantics*/, decode/*\off*/},
                    {"(", "E", /*\semantics*/ identity, /*\off*/ ")"},
                });

        debug = PARSE_TREE;
    }

    public static void main(String[] arguments) throws Exception {
        new Logic().interpret(arguments);
    }
}
