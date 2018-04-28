/**
* Generic Interpreter (gi)
*
* Copyright (C) 1999-2004 Craig A. Rich <carich@csupomona.edu>
*/
package gi;

import java.io.*;

/**
* <p>This class implements an LL(1) parser around a {@link Grammar}. The parser adapts to changes in the underlying <code>Grammar</code>. <code>Semantics</code> in a phrase are evaluated during a top-down left-to-right recursive descent parse, when they are first visited. Attributes above or to the left of the <code>Semantics</code> are available during evaluation. A <code>Grammar</code> with left-recursive productions can cause infinite recursion, unless productions that terminate recursion have priority over productions that recurse.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
public class LL1_Grammar extends Grammar {
//LL1_Grammar.parse
/**
* <p>Computes a <code>Production</code> to use in a leftmost derivation.</p>
*
* @param A the expected nonterminal.
* @param a the lookahead terminal.
* @return the highest priority <code>Production</code> with which to replace the expected nonterminal and eventually match the lookahead terminal; returns <code>null</code> if none.
*/
private Production parse(Object A, Object a) {

	Stack P = (Stack)this.P.get(A);
	Production p = null;

	for (int k = 0; k < P.size; k++) {
		Production q = (Production)P.get[k];
		Set first = first(q.beta);

		if (first.contains(a) || first.contains(null) && follow(A).contains(a)) {/*debug*/
			if ((debug & CONFLICT) > 0 && p != null) System.out.println(
				"??? LL(1) conflict");/*off*/
			if (p == null || p.serial < q.serial) p = q;
		}
	}
	return p;
}

/**
* <p>Computes the terminals matching a nonterminal.</p>
*
* @param A the expected nonterminal.
* @return the terminals matching the expected nonterminal.
*/
private Set expected(Object A) {

	Set expected = new Set(-50);

	expected.add(first(A));
	if (first(A).contains(null)) expected.add(follow(A));

	return expected;
}
//LL1_Grammar.interpret
/**
* <p>The lookahead terminal.</p>
*/
private Object a;

/**
* <p>Interprets a source character stream by LL(1) recursive descent.</p>
*
* @param source the source character stream.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
Object interpret(LineNumberReader source) throws Exception {

	a = grab(source);
	ParseTree t = new ParseTree(S, /*\semantics*/null, /*\off*/null);
	descend(source, t);/*debug*/
	if ((debug & PARSE_TREE) > 0) System.out.print(t);/*off*/
	return t;
}

/**
* <p>Completes a seed <code>ParseTree</code> by LL(1) recursive descent.</p>
*
* @param source the source character stream.
* @param t a seed <code>ParseTree</code> to be completed by interpreting <code>source</code>.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
private void descend(LineNumberReader source, ParseTree t) throws Exception {

	Object X = t.root;

	if (terminal(X)) { // match
		if (X != a) throw new Exception(
			"expected " + X).extend(source);/*debug*/
		if ((debug & SYNTAX) > 0) System.out.println(
			"\tmatch " + a);/*off*/

/*\semantics*/		t.value = word();
/*\off*/		a = grab(source);
	}
	else { // produce
		Production p = parse(X, a);
		if (p == null) throw new Exception(
			"expected " + expected(X)).extend(source);/*debug*/
		if ((debug & SYNTAX) > 0) System.out.println(
			"\tproduce " + p);/*off*/

		t.child = new ParseTree[p.beta.length];
		for (int l = 0; l < p.beta.length; l++)
			t.child[l] = new ParseTree(p.beta[l], /*\semantics*/null, /*\off*/null);

		for (int l = 0; l < p.beta.length; l++)
			/*\semantics*/if (p.beta[l] instanceof Semantics) try {
				((Semantics)p.beta[l]).f(t, l);
			}
			catch (Exception exception) {
				throw exception.extend(source);
			}
			else /*\off*/descend(source, t.child[l]);
	}
}

	/**
	* <p>Constructs an LL(1) parser around a new empty <code>Grammar</code>.</p>
	*/
	protected LL1_Grammar() {}

	/**
	* <p>Constructs an LL(1) parser around an existing <code>Grammar</code>.</p>
	*
	* @param G the <code>Grammar</code> around which the parser is constructed.
	*/
	protected LL1_Grammar(Grammar G) {
		super(G);
	}
}
