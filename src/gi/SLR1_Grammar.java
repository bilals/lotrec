/**
* Generic Interpreter (gi)
*
* Copyright (C) 1999-2004 Craig A. Rich <carich@csupomona.edu>
*/
package gi;

/**
* <p>This class implements an SLR(1) parser around a {@link Grammar}. The parser adapts to changes in the underlying <code>Grammar</code>. <code>Semantics</code> in a phrase are evaluated during a bottom-up parse, from left to right after all subtrees rooted in the phrase have been constructed. Attributes throughout the phrase are available during evaluation. SLR(1) parsing is more space- and time-efficient than LR(1) parsing; however, SLR(1) parsing is more easily confused than LR(1) parsing, since it considers lookahead terminals generally following a nonterminal (rather than specifically following it in a context) to choose between applicable phrases.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
public class SLR1_Grammar extends LR0_Grammar {
//SLR1_Grammar.parse
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
		Context c = (Context)C.get[j];

		if (c.lookahead == c.beta.length && follow(c.A).contains(a)) {/*debug*/
			if ((debug & CONFLICT) > 0 && p != null) System.out.println(
				"??? SLR(1) reduce/reduce conflict");/*off*/
			if (p == null || p.serial < c.serial) p = c;
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

		Set expected = super.expected(C);

		for (int j = 0; j < C.size; j++) {
			Context c = (Context)C.get[j];
			if (c.lookahead == c.beta.length) expected.add(follow(c.A));
		}
		return expected;
	}

	/**
	* <p>Returns the string representation of this parser, specifically "SLR(1)".</p>
	*
	* @return the string representation of this parser, specifically "SLR(1)".
	* @since 1.1
	*/
	public String toString() {
		return "SLR(1)";
	}

	/**
	* <p>Constructs an SLR(1) parser around a new empty <code>Grammar</code>.</p>
	*/
	protected SLR1_Grammar() {}

	/**
	* <p>Constructs an SLR(1) parser around an existing <code>Grammar</code>.</p>
	*
	* @param G the <code>Grammar</code> around which the parser is constructed.
	*/
	protected SLR1_Grammar(Grammar G) {
		super(G);
	}
}
