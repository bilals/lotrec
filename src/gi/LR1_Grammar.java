/**
* Generic Interpreter (gi)
*
* Copyright (C) 1999-2004 Craig A. Rich <carich@csupomona.edu>
*/
package gi;

/**
* <p>This class implements an LR(1) parser around a {@link Grammar}. The parser adapts to changes in the underlying <code>Grammar</code>. <code>Semantics</code> in a phrase are evaluated during a bottom-up parse, from left to right after all subtrees rooted in the phrase have been constructed. Attributes throughout the phrase are available during evaluation. LR(1) parsing considers context-specific lookahead terminals to more accurately choose between applicable phrases. LR(1) parsing is the default method used for a <code>Grammar</code> around which no parser has been explicitly constructed, and is the recommended method.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
public class LR1_Grammar extends LR0_Grammar {
//LR1_Grammar.Context
/**
* <p>This class implements an LR(1) {@link LR1_Grammar.Context <code>Context</code>}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
static class Context extends LR0_Grammar.Context {

	/**
	* <p>The follow <code>Set</code> in this LR(1) <code>Context</code>.</p>
	*/
	final Set follow = new Set(-50);
	/**
	* <p>The frontier beyond which reflexive transitive closure has not been pursued.</p>
	*/
	private int frontier;

	/**
	* <p>Constructs an LR(1) <code>Context</code>.</p>
	*
	* @param p the <code>Production</code> in this <code>Context</code>.
	* @param lookahead the lookahead position in this <code>Context</code>.
	*/
	Context(Production p, int lookahead) {
		super(p, lookahead);
	}

	/**
	* <p>Returns the string representation of this LR(1) <code>Context</code>.</p>
	*
	* @return the string representation of this <code>Context</code>.
	*/
	public String toString() {
		StringBuffer result = new StringBuffer(132);
		result.append(super.toString());
		result.append(" \\");
		result.append(follow);
		return result.toString();
	}
//LR1_Grammar.Context
}
//LR1_Grammar.Contexts
/**
* <p>This class implements a set of LR(1) {@link LR1_Grammar.Contexts <code>Contexts</code>}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
static class Contexts extends LR0_Grammar.Contexts {

	/**
	* <p>Pushes an LR(1) <code>Context</code> onto this set of LR(1) <code>Contexts</code>. The capacity is expanded by 100 if necessary.</p>
	*
	* @param p the <code>Production</code> in the <code>Context</code> pushed.
	* @param lookahead the lookahead position in the <code>Context</code> pushed.
	* @param follow the follow <code>Set</code> in the <code>Context</code> pushed.
	*
	* @return the index in this set of LR(1) <code>Contexts</code> at which the pushed <code>Context</code> occurs.
	*/
	private int push(Production p, int lookahead, Set follow) {

		if (size == get.length) System.arraycopy(get, 0,
			get = new Object[size+100], 0, size);

		Context c = (Context)get[size];
		if (c == null) get[size] = c = new Context(p, lookahead);
		else {
			c.A = p.A;
			c.beta = p.beta;
			c.serial = p.serial;
			c.lookahead = lookahead;
			c.frontier = 0;
		}
		c.follow.size = 0;
		c.follow.add(follow);
		return size++;
	}

	/**
	* <p>Adds an LR(1) <code>Context</code> to this set of LR(1) <code>Contexts</code>. The capacity is expanded if necessary.</p>
	*
	* @param p the <code>Production</code> in the <code>Context</code> added.
	* @param lookahead the lookahead position in the <code>Context</code> added.
	* @param follow the follow <code>Set</code> in the <code>Context</code> added.
	*
	* @return the index in this set of LR(1) <code>Contexts</code> at which the <code>Context</code> occurs if this set of LR(1) <code>Contexts</code> changed; otherwise the size of this set of LR(1) <code>Contexts</code>.
	*/
	private int add(Production p, int lookahead, Set follow) {

		for (int j = 0; j < size; j++) {
			Context c = (Context)get[j];

			if (c.serial == p.serial && c.lookahead == lookahead)
				return (c.follow.add(follow)) ? j : size;
		}
		return push(p, lookahead, follow);
	}
}
//LR1_Grammar.transition
/**
* <p>Computes a transition from a set of LR(1) <code>Contexts</code> on a symbol.</p>
*
* @param C the set of LR(1) <code>Contexts</code> from which the transition is made.
* @param X the symbol on which the transition is made.
* @return the set of LR(1) <code>Contexts</code> to which the transition is made.
*/
LR0_Grammar.Contexts transition(LR0_Grammar.Contexts C, Object X) {

	if (states.size == states.get.length) System.arraycopy(states.get, 0,
		states.get = new Object[states.size+100], 0, states.size);
	Contexts R = (Contexts)states.get[states.size];
	if (R == null) states.get[states.size] = R = new Contexts();
	else R.size = 0;

	for (int j = 0; j < C.size; j++) {
		Context c = (Context)C.get[j];

		if (c.lookahead < c.beta.length && c.beta[c.lookahead] == X)
			R.push(c, c.lookahead+1, c.follow);
	}
	return R;
}
//LR1_Grammar.closure
/**
* <p>Computes the reflexive transitive closure of a set of LR(1) <code>Contexts</code> under empty transition.</p>
*
* @param C the set of LR(1) <code>Contexts</code> whose reflexive transitive closure is computed under empty transition.
* @return the reflexive transitive closure of <code>from</code> under empty transition.
*/
LR0_Grammar.Contexts closure(LR0_Grammar.Contexts C) {

	boolean closed;
	do {
		closed = true;

		for (int j = 0; j < C.size; j++) {
			Context c = (Context)C.get[j];
			if (c.frontier == c.follow.size) continue;

			int frontier = c.frontier;
			c.frontier = c.follow.size;

/*\semantics*/			for (; c.lookahead < c.beta.length; c.lookahead++)
				if (!(c.beta[c.lookahead] instanceof Semantics)) break;
/*\off*/			if (c.lookahead == c.beta.length) continue;

			Object B = c.beta[c.lookahead];
			if (!nonterminal(B)) continue;

			Set first = first(c.beta, c.lookahead+1);

			if (first.contains(null)) {
				first.pop();

				if (frontier > 0) first.size = 0;
				first.add(c.follow, frontier);
			}
			Stack P = (Stack)this.P.get(B);

			for (int k = 0; k < P.size; k++) {
				Production p = (Production)P.get[k];

				if (((Contexts)C).add(p, 0, first) <= j) closed = false;
			}
		}
	} while (!closed);

	return C;
}
//LR1_Grammar.initial
/**
* <p>Computes the initial state of the LR(1) DFA.</p>
*/
LR0_Grammar.Contexts initial() {

	if (!nonterminal(S$)) {
		put(S$, new Object[]{S});
		S = S$;
	}
	Contexts I = (Contexts)states.get[0];
	if (I == null) I = new Contexts();
	else I.size = 0;

	Set follow_S$ = new Set(1);
	follow_S$.push($);
	I.push((Production)((Stack)P.get(S$)).top(), 0, follow_S$);
	return closure(I);
}
//LR1_Grammar.parse
/**
* <p>Computes the <code>Production</code> to use in a reverse rightmost derivation.</p>
*
* @param C the set of LR(1) <code>Contexts</code>.
* @param a the lookahead terminal.
* @return the highest priority <code>Production</code> underlying an applicable reduce <code>Context</code> in <code>C</code>; returns <code>null</code> if none.
*/
Production parse(LR0_Grammar.Contexts C, Object a) {

	Production p = null;

	for (int j = 0; j < C.size; j++) {
		Context c = (Context)C.get[j];

		if (c.lookahead == c.beta.length && c.follow.contains(a)) {/*debug*/
			if ((debug & CONFLICT) > 0 && p != null) System.out.println(
				"??? LR(1) reduce/reduce conflict");/*off*/
			if (p == null || p.serial < c.serial) p = c;
		}
	}
	return p;
}

	/**
	* <p>Computes the terminals expected in a set of LR(1) <code>Contexts</code>.</p>
	*
	* @param C the set of LR(1) <code>Contexts</code>.
	* @return the terminals matching a shift or reduce <code>Context</code> in <code>C</code>.
	*/
	Set expected(LR0_Grammar.Contexts C) {

		Set expected = super.expected(C);

		for (int j = 0; j < C.size; j++) {
			Context c = (Context)C.get[j];
			if (c.lookahead == c.beta.length) expected.add(c.follow);
		}
		return expected;
	}

	/**
	* <p>Returns the string representation of this parser, specifically "LR(1)".</p>
	*
	* @return the string representation of this parser, specifically "LR(1)".
	* @since 1.1
	*/
	public String toString() {
		return "LR(1)";
	}

	/**
	* <p>Constructs an LR(1) parser around a new empty <code>Grammar</code>.</p>
	*/
	protected LR1_Grammar() {}

	/**
	* <p>Constructs an LR(1) parser around an existing <code>Grammar</code>.</p>
	*
	* @param G the <code>Grammar</code> around which the parser is constructed.
	*/
	protected LR1_Grammar(Grammar G) {
		super(G);
	}
}
