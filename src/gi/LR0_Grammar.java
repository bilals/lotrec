/**
 * Generic Interpreter (gi)
 *
 * Copyright (C) 1999-2004 Craig A. Rich <carich@csupomona.edu>
 */
package gi;

import java.io.*;

/**
 * <p>This class implements an LR(0) parser around a {@link Grammar}. The parser adapts to changes in the underlying <code>Grammar</code>. <code>Semantics</code> in a phrase are evaluated during a bottom-up parse, from left to right after all subtrees rooted in the phrase have been constructed. Attributes throughout the phrase are available during evaluation. LR(0) parsing is not very practical, since it ignores lookahead information and is easily confused, but it forms a basis around which SLR(1) and LR(1) parsers are constructed.</p>
 *
 * @version 1.2
 * @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
 */
public class LR0_Grammar extends Grammar {
    //LR0_Grammar.Context
    /**
     * <p>This class implements an LR(0) {@link LR0_Grammar.Context <code>Context</code>}.</p>
     *
     * @version 1.2
     * @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
     */
    static class Context extends Production {

        /**
         * <p>The lookahead position in this LR(0) <code>Context</code>.</p>
         */
        int lookahead;

        /**
         * <p>Constructs an LR(0) <code>Context</code>.</p>
         *
         * @param p the <code>Production</code> in this <code>Context</code>.
         * @param lookahead the lookahead position in this <code>Context</code>.
         */
        Context(Production p, int lookahead) {
            super(p);
            this.lookahead = lookahead;
        }

        /**
         * <p>Returns the string representation of this LR(0) <code>Context</code>.</p>
         *
         * @return the string representation of this <code>Context</code>.
         */
        public String toString() {
            StringBuffer result = new StringBuffer(132);
            result.append(A);
            result.append(" ->");

            for (int l = 0; l < beta.length; l++) {
                result.append(' ');
                if (l == lookahead) {
                    result.append("<>");
                }
                if (beta[l] instanceof Semantics) {
                    result.append('_');
                } else {
                    result.append(beta[l]);
                }
            }
            if (lookahead == beta.length) {
                result.append(" <>");
            }
            return result.toString();
        }
        //LR0_Grammar.Context
    }
    //LR0_Grammar.Contexts
    /**
     * <p>This class implements a set of LR(0) {@link LR0_Grammar.Contexts <code>Contexts</code>}.</p>
     *
     * @version 1.2
     * @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
     */
    static class Contexts extends Set {

        /**
         * <p>Constructs an empty set of LR(0) contexts.</p>
         */
        Contexts() {
            super(150);
        }

        /**
         * <p>Pushes an LR(0) <code>Context</code> onto this set of LR(0) <code>Contexts</code>. The capacity is expanded by 100 if necessary.</p>
         *
         * @param p the <code>Production</code> in the <code>Context</code> pushed.
         * @param lookahead the lookahead position in the <code>Context</code> pushed.
         */
        private void push(Production p, int lookahead) {

            if (size == get.length) {
                System.arraycopy(get, 0,
                        get = new Object[size + 100], 0, size);
            }
            Context c = (Context) get[size];
            if (c == null) {
                get[size] = c = new Context(p, lookahead);
            } else {
                c.A = p.A;
                c.beta = p.beta;
                c.serial = p.serial;
                c.lookahead = lookahead;
            }
            size++;
        }

        /**
         * <p>Adds an LR(0) <code>Context</code> to this set of LR(0) <code>Contexts</code>. The capacity is expanded if necessary.</p>
         *
         * @param p the <code>Production</code> in the <code>Context</code> added.
         * @param lookahead the lookahead position in the <code>Context</code> added.
         */
        private void add(Production p, int lookahead) {

            for (int j = 0; j < size; j++) {
                Context c = (Context) get[j];

                if (c.serial == p.serial && c.lookahead == lookahead) {
                    return;
                }
            }
            push(p, lookahead);
        }

        /**
         * <p>Returns the string representation of this set of LR(0) <code>Contexts</code>.</p>
         *
         * @return the string representation of this set of LR(0) <code>Contexts</code>.
         */
        public String toString() {

            StringBuffer result = new StringBuffer(132);

            for (int j = 0; j < size; j++) {
                result.append("\t\t");
                result.append(get[j]);
                result.append(System.getProperty("line.separator"));
            }
            return result.toString();
        }
        //LR0_Grammar.Contexts
    }
    //LR0_Grammar.transition
    /**
     * <p>Computes a transition from a set of LR(0) <code>Contexts</code> on a symbol.</p>
     *
     * @param C the set of LR(0) <code>Contexts</code> from which the transition is made.
     * @param X the symbol on which the transition is made.
     * @return the set of LR(0) <code>Contexts</code> to which the transition is made.
     */
    Contexts transition(Contexts C, Object X) {

        if (states.size == states.get.length) {
            System.arraycopy(states.get, 0,
                    states.get = new Object[states.size + 100], 0, states.size);
        }
        Contexts R = (Contexts) states.get[states.size];
        if (R == null) {
            states.get[states.size] = R = new Contexts();
        } else {
            R.size = 0;
        }
        for (int j = 0; j < C.size; j++) {
            Context c = (Context) C.get[j];

            if (c.lookahead < c.beta.length && c.beta[c.lookahead].equals(X)) {
                R.push(c, c.lookahead + 1);
            }
        }
        return R;
    }
    //LR0_Grammar.closure
    /**
     * <p>Computes the reflexive transitive closure of a set of LR(0) <code>Contexts</code> under empty transition.</p>
     *
     * @param C the set of LR(0) <code>Contexts</code> whose reflexive transitive closure is computed under empty transition.
     * @return the reflexive transitive closure of <code>from</code> under empty transition.
     */
    Contexts closure(Contexts C) {

        for (int j = 0; j < C.size; j++) {
            Context c = (Context) C.get[j];
            /*\semantics*/
            for (; c.lookahead < c.beta.length; c.lookahead++) {
                if (!(c.beta[c.lookahead] instanceof Semantics)) {
                    break;
                /*\off*/ }
            }
            if (c.lookahead == c.beta.length) {
                continue;
            }
            Object B = c.beta[c.lookahead];
            if (!nonterminal(B)) {
                continue;
            }
            Stack P = (Stack) this.P.get(B);

            for (int k = 0; k < P.size; k++) {
                Production p = (Production) P.get[k];
                C.add(p, 0);
            }
        }
        return C;
    }
    //LR0_Grammar.initial
    /**
     * <p>The augmented start symbol.</p>
     */
    static final Object S$ = new String("S'");
    /**
     * <p>The states through which this LR(0) parser transitions.</p>
     */
    final Stack states = new Stack(200);
    /**
     * <p>The parse trees through which this LR(0) parser transitions.</p>
     */
    final Stack trees = new Stack(200);

    /**
     * <p>Computes the initial state of the LR(0) DFA.</p>
     */
    Contexts initial() {

        if (!nonterminal(S$)) {
            put(S$, new Object[]{S});
            S = S$;
        }
        Contexts I = (Contexts) states.get[0];
        if (I == null) {
            I = new Contexts();
        } else {
            I.size = 0;
        }
        I.push((Production) ((Stack) P.get(S$)).top(), 0);
        return closure(I);
    }
    //LR0_Grammar.parse
    /**
     * <p>Computes the <code>Production</code> to use in a reverse rightmost derivation.</p>
     *
     * @param C the set of LR(0) <code>Contexts</code>.
     * @param a the lookahead terminal.
     * @return the highest priority <code>Production</code> underlying an applicable reduce <code>Context</code> in <code>C</code>; returns <code>null</code> if none.
     */
    Production parse(Contexts C, Object a) {

        Production p = null;

        for (int j = 0; j < C.size; j++) {
            Context c = (Context) C.get[j];

            if (c.lookahead == c.beta.length) {/*debug*/
                if ((debug & CONFLICT) > 0 && p != null) {
                    System.out.println(
                            "??? LR(0) reduce/reduce conflict");/*off*/

                }
                if (p == null || p.serial < c.serial) {
                    p = c;
                }
            }
        }
        return p;
    }

    /**
     * <p>Computes the terminals expected in a set of LR(0) <code>Contexts</code>.</p>
     *
     * @param C the set of LR(0) <code>Contexts</code>.
     * @return the terminals matching a shift or reduce <code>Context</code> in <code>C</code>.
     */
    Set expected(Contexts C) {

        Set expected = new Set(-50);

        for (int j = 0; j < C.size; j++) {
            Context c = (Context) C.get[j];
            if (c.lookahead == c.beta.length) {
                continue;
            }
            Object a = c.beta[c.lookahead];
            if (terminal(a)) {
                expected.add(a);
            }
        }
        return expected;
    }
    //LR0_Grammar.interpret
    /**
     * <p>Interprets a source character stream by LR shift-reduce ascent.</p>
     *
     * @param source the source character stream.
     * @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
     * @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
     */
    Object interpret(LineNumberReader source) throws Exception {
        states.size = 0;
        states.push(initial());/*debug*/
        if ((debug & SYNTAX) > 0) {
            System.out.print((Contexts) states.top());/*off*/

        }
        trees.size = 0;
        trees.push(null);
        Object a = grab(source);

        while (true) {
            Contexts C = closure(transition((Contexts) states.top(), a));

            if (C.size != 0) { // shift
			/*debug*/
                if ((debug & CONFLICT) > 0 && parse((Contexts) states.top(), a) != null) {
                    System.out.println(
                            "??? " + this + " shift/reduce conflict");
                }
                if ((debug & SYNTAX) > 0) {
                    System.out.println(
                            "\tshift " + a);
                /*off*/
                }
                states.push(C);/*debug*/
                if ((debug & SYNTAX) > 0) {
                    System.out.print((Contexts) states.top());/*off*/

                }
                trees.push(new ParseTree(a, /*\semantics*/ word(), /*\off*/ null));
                a = grab(source);
            } else { // reduce
                Production p = parse((Contexts) states.top(), a);
                if (p == null) {
                    //System.out.println("Recognized part: "+((ParseTree)trees.top()).value);
                    throw new Exception(
                            "expected " + expected((Contexts) states.top())).extend(source);/*debug*/

                }
                if ((debug & SYNTAX) > 0) {
                    System.out.println(
                            "\treduce " + p.toString().replaceFirst(" <>.*$", ""));/*off*/

                }
                if (p.A == S$) {
                    break;
                }
                ParseTree t = new ParseTree(p.A, /*\semantics*/ null, /*\off*/ new ParseTree[p.beta.length]);

                for (int l = p.beta.length; --l >= 0;) /*\semantics*/ {
                    if (p.beta[l] instanceof Semantics) {
                        t.child[l] = new ParseTree(p.beta[l], null, null);
                    } else /*\off*/ {
                        /*\semantics*/	/*\off*/ states.pop();
                        /*\semantics*/	/*\off*/ t.child[l] = (ParseTree) trees.pop();
                    /*\semantics*/	/*\off*/                    }
                /*\semantics*/ }
                for (int l = 0; l < p.beta.length; l++) {
                    if (p.beta[l] instanceof Semantics) {
                        try {
                            ((Semantics) p.beta[l]).f(t, l);
                        } catch (Exception exception) {
                            throw exception.extend(source);
                        }
                    /*\off*/ }
                }
                states.push(closure(transition((Contexts) states.top(), p.A)));/*debug*/
                if ((debug & SYNTAX) > 0) {
                    System.out.print((Contexts) states.top());/*off*/

                }
                trees.push(t);
            }
        }/*debug*/
        if ((debug & PARSE_TREE) > 0) {
//            System.out.print(trees.top());/*off*/

        }
        return trees.top();
    }

    /**
     * <p>Returns the string representation of this parser, specifically "LR(0)".</p>
     *
     * @return the string representation of this parser, specifically "LR(0)".
     * @since 1.1
     */
    public String toString() {
        return "LR(0)";
    }

    /**
     * <p>Constructs an LR(0) parser around a new empty <code>Grammar</code>.</p>
     */
    protected LR0_Grammar() {
    }

    /**
     * <p>Constructs an LR(0) parser around an existing <code>Grammar</code>.</p>
     *
     * @param G the <code>Grammar</code> around which the parser is constructed.
     */
    protected LR0_Grammar(Grammar G) {
        super(G);
    }
}
