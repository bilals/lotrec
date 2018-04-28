/**
* Generic Interpreter (gi)
*
* Copyright (C) 1999-2004 Craig A. Rich <carich@csupomona.edu>
*/
package gi;

import java.io.*;
import java.util.*;
import gnu.getopt.*;

/**
* <p>This class implements a {@link Grammar}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
public class Grammar extends Lexicon {
//Production
/**
* <p>The number of <code>Production</code>s constructed.</p>
*/
private static int PSize = 0;

/**
* <p>This class implements a {@link Grammar.Production <code>Production</code>}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
static class Production {

	/**
	* <p>The nonterminal on the left side of this <code>Production</code>.</p>
	*/
	Object A;
	/**
	* <p>The phrase on the right side of this <code>Production</code>.</p>
	*/
	Object[] beta;
	/**
	* <p>The serial number of this <code>Production</code>. It is the priority of this <code>Production</code> when resolving parse conflicts.</p>
	*/
	int serial;

	/**
	* <p>Constructs a <code>Production</code> with a nonterminal and phrase.</p>
	*
	* @param A the nonterminal on the left side of this <code>Production</code>.
	* @param beta the phrase on the right side of this <code>Production</code>.
	*/
	Production(Object A, Object[] beta) {
		this.A = A;
		this.beta = beta;
		serial = ++PSize;
	}

	Production(Production p) {
		A = p.A;
		beta = p.beta;
		serial = p.serial;
	}

	/**
	* <p>Returns the string representation of this <code>Production</code>.</p>
	*
	* @return the string representation of this <code>Production</code>.
	*/
	public String toString() {
		StringBuffer result = new StringBuffer(80);
		result.append(A);
		result.append(" ->");

		for (int l = 0; l < beta.length; l++) {
			result.append(' ');
			if (beta[l] instanceof Semantics) result.append('_');
			else result.append(beta[l]);
		}
		return result.toString();
	}
//Production
}
//Semantics
/**
* <p>This class implements {@link Grammar.Semantics <code>Semantics</code>} embedded in productions and evaluated when interpreting.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class Semantics {

	/**
	* <p>Constructs <code>Semantics</code>.</p>
	*/
	protected Semantics() {}

	/**
	* <p>Evaluates attributes in a <code>ParseTree</code> when interpreting. The semantic function <code>f</code> is invoked when a production containing this <code>Semantics</code> is applied to the <code>ParseTree</code>. <code>t</code> provides the context in which attribute evaluation occurs. <code>t.root</code> and <code>t.child</code> are the left and right side of the production in which this <code>Semantics</code> is embedded.</p>
	* <p>User-defined semantics are implemented by extending the class <code>Semantics</code> and overriding this method. The <code>f</code> method provided does nothing.</p>
	* <p>During top-down {@link LL1_Grammar LL(1)} parsing, the <code>ParseTree</code> is constructed from the top down by leftmost derivation. This is a depth-first left-to-right traversal, in which embedded <code>Semantics</code> are evaluated when first visited. Evaluation should not depend on attribute values produced by <code>Semantics</code> below or to the right of this <code>Semantics</code>. In other words, LL parsing supports evaluation of L-attributed semantic specifications.</p>
	* <p>During bottom-up {@link LR0_Grammar LR(0)}, {@link SLR1_Grammar SLR(1)} or {@link LR1_Grammar LR(1)} parsing, the <code>ParseTree</code> is constructed from the bottom up by reverse rightmost derivation. Embedded <code>Semantics</code> are evaluated after all subtrees rooted in the phrase to be reduced have been visited.</p>
	*
	* @param t the <code>ParseTree</code> in which attribute evaluation occurs.
	* @param l the index in <code>t.child</code> at which the <code>Semantics</code> being evaluated occurs. Introduced in version 1.1.
	* @since 1.1, renames <code>evaluate</code> in version 1.0.
	*/
	protected void f(ParseTree t, int l) throws Exception {};
}
//P
/**
* <p>The <code>Production</code>s put into this <code>Grammar</code>. It is a mapping from a nonterminal to its <code>Production</code>s.</p>
*/
final Map P;
/**
* <p>The start symbol of this <code>Grammar</code>. It is the nonterminal on the left side of the <code>Production</code> first put into this <code>Grammar</code>.</p>
*/
Object S;

/**
* <p>Puts a production into this <code>Grammar</code>. The start symbol is the first nonterminal put in this <code>Grammar</code>.</p>
*
* @param nonterminal the nonterminal added to this <code>Grammar</code>.
* @param phrase the phrase produced by <code>nonterminal</code>. <code>phrase</code> may contain nonterminals, terminals, and <code>Semantics</code>.
*/
protected void put(Object nonterminal, Object[] phrase) {

	Stack otherP = (Stack)this.P.get(nonterminal);
	if (otherP == null) this.P.put(nonterminal, otherP = new Stack(20));

	otherP.push(new Production(nonterminal, phrase));
	if (S == null) S = nonterminal;

	T.clear();
	first.clear();
	follow.clear();
}

/**
* <p>Puts productions into this <code>Grammar</code>. The productions are successively added using {@link #put(Object, Object[]) <code>put(nonterminal, phrase)</code>}.</p>
*
* @param nonterminal the nonterminal on the left side of the productions.
* @param phrases the phrases produced by <code>nonterminal</code>. Each phrase in <code>phrases</code> may contain nonterminals, terminals, and {@link Grammar.Semantics <code>Semantics</code>}.
*/
protected void put(Object nonterminal, Object[][] phrases) {

	for (int l = 0; l < phrases.length; l++)
		put(nonterminal, phrases[l]);
}
//N
/**
* <p>Indicates whether a symbol is a nonterminal in this <code>Grammar</code>.</p>
*
* @param X the symbol whose status is requested.
* @return <code>true</code> if <code>X</code> is a nonterminal in this <code>Grammar</code>; <code>false</code> otherwise.
*/
boolean nonterminal(Object X) {
	return P.containsKey(X);
}
//T
/**
* <p>The terminals put into this <code>Grammar</code>. When empty, there is a need to discover terminals. It is computed only on demand created by {@link #terminal(Object) <code>terminal(X)</code>}.</p>
*/
private final HashSet T;

/**
* <p>Indicates whether a symbol is a terminal in this <code>Grammar</code>.</p>
*
* @param X the symbol whose status is requested.
* @return <code>true</code> if <code>X</code> is a terminal in this <code>Grammar</code>; <code>false</code> otherwise.
*/
boolean terminal(Object X) {

	if (T.isEmpty()) {

		for (Iterator j = this.P.entrySet().iterator(); j.hasNext();) {
			Map.Entry entry = (Map.Entry)j.next();
			Stack P = (Stack)entry.getValue();

			for (int k = 0; k < P.size; k++) {
				Object[] beta = ((Production)P.get[k]).beta;

				for (int l = 0; l < beta.length; l++) {
					Object a = beta[l];
					if (/*\semantics*/a instanceof Semantics || /*\off*/nonterminal(a)) continue;

					if (a instanceof String && !super.terminal(a))
						put(a, new Singleton((String)a));

					T.add(a);
				}
			}
		}
		T.add($);/*debug*/
		if ((debug & TERMINALS) > 0) System.out.println(
			"----terminals\n\t" + T.toString().replaceFirst("\\[", "{").replaceAll(", ", " ").replaceFirst("\\]$", "}\n----------"));/*off*/
	}
	return T.contains(X);
}
//Grammar.grab
/**
* <p>Grabs a terminal from a source character stream using this <code>Grammar</code>. Invokes {@link Lexicon#grab(LineNumberReader) <code>Lexicon.grab(source)</code>} until it returns a terminal occurring in a phrase of this <code>Grammar</code> or end of source. Blocks until a character is available, an I/O error occurs, or the end of the source stream is reached.</p>
*
* @param source the source character stream.
* @return the first terminal occurring in a phrase of this <code>Grammar</code>.
* @throws Lexicon.Exception if an I/O or lexical error occurs.
*/
    @Override
protected Object grab(LineNumberReader source) throws Exception {

	Object a = null;

	while (!terminal(a))
		a = super.grab(source);/*debug*/
	if ((debug & LEXICAL) > 0) System.out.println(
		a + (!a.equals(word()) ? " " + word() : ""));/*off*/

	return a;
}
//ParseTree
/**
* <p>This class implements a {@link Grammar.ParseTree <code>ParseTree</code>} constructed by interpreting a source stream.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class ParseTree {

	/**
	* <p>The symbol at the root of this <code>ParseTree</code>. <code>root</code> can be a nonterminal, a terminal or <code>Semantics</code>.</p>
	* @since 1.1, renames <code>symbol</code> in version 1.0.
	*/
	public Object root;
/*\semantics*/	/**
	* <p>The attribute value at the root of this <code>ParseTree</code>. If <code>root</code> is a terminal, <code>value</code> is initially the source word <code>root</code> matches; otherwise, <code>value</code> is initially <code>null</code>. <code>value</code> may be modified when interpreting by evaluation of embedded <code>Semantics</code>.</p>
	* @since 1.1, renames <code>attribute</code> in version 1.0.
	*/
	public Object value;
/*\off*/	/**
	* <p>The subtrees of the root of this <code>ParseTree</code>. If <code>root</code> is a nonterminal, <code>child</code> is the array of subtrees produced by <code>root</code>; otherwise, <code>child</code> is <code>null</code>.</p>
	* @since 1.1, renames <code>phrase</code> in version 1.0.
	*/
	public ParseTree[] child;

	/**
	* <p>Constructs a <code>ParseTree</code> with a root and its subtrees.</p>
	*
	* @param root the symbol at the root.
	* @param value the attribute value at the root.
	* @param child the array of subtrees produced by <code>root</code>.
	*/
	ParseTree(Object root, /*\semantics*/Object value, /*\off*/ParseTree[] child) {
		this.root = root;
/*\semantics*/		this.value = value;
/*\off*/		this.child = child;
	}

	/**
	* <p>The last string representation of a <code>ParseTree</code> returned.</p>
	*/
	private static StringBuffer t;

	/**
	* <p>Appends the string representation of this <code>ParseTree</code> to <code>t</code>}.</p>
	*
	* @param depth the depth at which this <code>ParseTree</code> occurs.
	*/
	private void toString(int depth) {
		if (root instanceof Semantics) return;
		for (int k = 0; k < depth; k++) t.append("| ");
		t.append(root);

		if (value != null && !root.equals(value)) {
			t.append(" [");
			if (!(value instanceof Object[]))
				t.append(value);
			else {
				Object[] array = (Object[])value;
				for (int i = 0; i < array.length; i++) {
					if (i > 0) t.append(' ');
					t.append(array[i]);
				}
			}
			t.append(']');
		}
		t.append(System.getProperty("line.separator"));

		if (child != null)
			for (int l = 0; l < child.length; l++)
				child[l].toString(depth+1);
	}

	/**
	* <p>Returns the string representation of this <code>ParseTree</code>. The symbols in the <code>ParseTree</code> are shown in outline form, with children below their parent indented two columns. Each symbol is followed by its attribute value, if it is not <code>null</code> and differs from the symbol.</p>
	*
	* @return the string representation of this <code>ParseTree</code>.
	*/
	public String toString() {
		if (t == null) t = new StringBuffer(4000);
		else t.setLength(0);
		toString(0);
		return t.toString();
	}
//ParseTree
}
//Grammar.interpret
/**
* <p>Interprets a source character stream using an {@link LR1_Grammar LR(1) parser} around this <code>Grammar</code>. This method is overridden by all parsers, so it is only invoked when this <code>Grammar</code> has not been extended by a parser.</p>
*
* @param source the source character stream.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
Object interpret(LineNumberReader source) throws Exception {
	return new LR1_Grammar(this).interpret(source);
}
//Grammar.interpret(arguments)
/**
* <p>{@link #debug <code>debug</code>} switch constant enables printing first and follow sets precomputed during syntax analysis.</p>
* @since 1.1
*/
protected static final int FIRST_FOLLOW = 0x04;
/**
* <p>{@link #debug <code>debug</code>} switch constant enables printing parsing decisions made during syntax analysis.</p>
* @since 1.1
*/
protected static final int SYNTAX = 0x08;
/**
* <p>{@link #debug <code>debug</code>} switch constant enables printing parsing conflicts encountered during syntax analysis.</p>
* @since 1.1
*/
protected static final int CONFLICT = 0x10;
/**
* <p>{@link #debug <code>debug</code>} switch constant enables printing each <code>ParseTree</code> produced by syntax analysis.</p>
* @since 1.1
*/
protected static final int PARSE_TREE = 0x20;

/**
* <p>Interprets by command-line arguments using this <code>Grammar</code>. When interpreting, the parser used will be (in order):</p>
* <blockquote><ul>
*	<li> The parser most recently constructed by preceding command-line arguments.
*	<li> The parser originally around this <code>Grammar</code>.
*	<li> An LR(1) parser.
* </ul></blockquote>
* The first I/O, lexical, syntax or semantic error that occurs when interpreting is printed to the standard error stream.</p>
*
* @param arguments the command-line arguments controlling the interpreter.
* <blockquote>
* The following arguments may appear zero or more times, are processed in order, and have the following effects:
* <blockquote><dl>
*	<dt><code>-t</code>, <code>--terminals</code></dt>
*	<dd>Print the set of terminals in this <code>Grammar</code> before subsequent syntax analyses.</dd>
*	<dt><code>-l</code>, <code>--lexical</code></dt>
*	<dd>Print terminals in this <code>Grammar</code> grabbed during subsequent syntax analyses.</dd>
*	<dt><code>-f</code>, <code>--firstfollow</code></dt>
*	<dd>Print first and follow sets precomputed during subsequent syntax analyses.</dd>
*	<dt><code>-s</code>, <code>--syntax</code></dt>
*	<dd>Print parsing decisions made during subsequent syntax analyses.</dd>
*	<dt><code>-c</code>, <code>--conflict</code></dt>
*	<dd>Print parsing conflicts encountered during subsequent syntax analyses.</dd>
*	<dt><code>-p</code>, <code>--parsetree</code>, <code>--tree</code></dt>
*	<dd>Print each <code>ParseTree</code> produced by subsequent syntax analyses.</dd>
*	<dt><code>-v</code>, <code>--verbose</code></dt>
*	<dd>Print maximum debugging. Equivalent to <code>-tlfscp</code>.</dd>
*	<dt><code>--ll1</code></dt>
*	<dd>Construct an {@link LL1_Grammar LL(1) parser} around this <code>Grammar</code>.</dd>
*	<dt><code>--lr0</code></dt>
*	<dd>Construct an {@link LR0_Grammar LR(0) parser} around this <code>Grammar</code>.</dd>
*	<dt><code>--slr1</code></dt>
*	<dd>Construct an {@link SLR1_Grammar SLR(1) parser} around this <code>Grammar</code>.</dd>
*	<dt><code>--lr1</code></dt>
*	<dd>Construct an {@link LR1_Grammar LR(1) parser} around this <code>Grammar</code>.</dd>
*	<dt><code>-</code></dt>
*	<dd>Interpret the standard input stream using this <code>Grammar</code>.</dd>
*	<dt><code><var>filename</var></code></dt>
*	<dd>Interpret source file <code><var>filename</var></code> using this <code>Grammar</code>.</dd>
* </dl></blockquote>
* If no <code><var>filename</var></code> arguments are given, the standard input stream is interpreted.
* </blockquote>
*/
public void interpret(String[] arguments) {

	Grammar G = this;
	boolean sourceSpecified = false;

	Getopt options = new Getopt(this.getClass().getName(), arguments,
		"-tlfscpv", new LongOpt[]{
			new LongOpt("terminals", 0, null, 't'),
			new LongOpt("lexical", 0, null, 'l'),
			new LongOpt("firstfollow", 0, null, 'f'),
			new LongOpt("syntax", 0, null, 's'),
			new LongOpt("conflict", 0, null, 'c'),
			new LongOpt("parsetree", 0, null, 'p'),
			new LongOpt("tree", 0, null, 'p'),
			new LongOpt("verbose", 0, null, 'v'),
			new LongOpt("ll1", 0, null, 2),
			new LongOpt("lr0", 0, null, 3),
			new LongOpt("slr1", 0, null, 4),
			new LongOpt("lr1", 0, null, 5),
		}
	);
	for (int option; (option = options.getopt()) != -1;) {
		switch (option) {
		case 't': G.debug |= TERMINALS; break;
		case 'l': G.debug |= LEXICAL; break;
		case 'f': G.debug |= FIRST_FOLLOW; break;
		case 's': G.debug |= SYNTAX; break;
		case 'c': G.debug |= CONFLICT; break;
		case 'p': G.debug |= PARSE_TREE; break;
		case 'v': G.debug |= VERBOSE; break;
		case 2: G = new LL1_Grammar(G); break;
		case 3: G = new LR0_Grammar(G); break;
		case 4: G = new SLR1_Grammar(G); break;
		case 5: G = new LR1_Grammar(G); break;
		case 1: String argument = options.getOptarg();
			try {
				sourceSpecified = true;
				if (argument.equals("-"))
					G.interpret();
				else
					G.interpret(new FileReader(argument));
			}
			catch (java.lang.Exception exception) {
				System.err.println(exception);
			}
		}
	}
	if (!sourceSpecified) try {
		G.interpret();
	}
	catch (java.lang.Exception exception) {
		System.err.println(exception);
	}
}
//first
/**
* <p>The mapping from a nonterminal to its first set in this <code>Grammar</code>. When empty, there is a need to compute current first sets. It is computed only on demand created by {@link #first(Object) <code>first(nonterminal)</code>}.</p>
*/
private final Map first;

/**
* <p>Returns the first set of a nonterminal.</p>
*
* @param nonterminal the nonterminal whose first set is requested.
* @return <code>{@link #first}.get(nonterminal)</code>, computing {@link #first} if there is a need to compute current first sets.
*/
Set first(Object nonterminal) {

	if (first.isEmpty()) {

		for (Iterator j = this.P.keySet().iterator(); j.hasNext();)
			first.put(j.next(), new Set(50));

		boolean closed;
		do {
			closed = true;

			for (Iterator j = this.P.entrySet().iterator(); j.hasNext();) {
				Map.Entry entry = (Map.Entry)j.next();
				Object A = entry.getKey();
				Stack P = (Stack)entry.getValue();

				for (int k = 0; k < P.size; k++) {
					Object[] beta = ((Production)P.get[k]).beta;

					if (first(A).add(first(beta))) closed = false;
				}
			}
		} while (!closed);/*debug*/
		if ((debug & FIRST_FOLLOW) > 0) System.out.println(
			"----first\n\t" + first.toString().replaceFirst("^\\{","").replaceAll("\\}, ", "}\n\t").replaceFirst("\\}$", "\n----------"));/*off*/

		first_beta.size = 0;
	}
	return (Set)first.get(nonterminal);
}
//first(beta)
/**
* <p>A first set computed by {@link #first(Object[],int) <code>first(beta, start)</code>}.</p>
*/
private final Set first_beta = new Set(-200);

/**
* <p>Computes the first set of a phrase.</p>
*
* @param beta the phrase whose first set is computed.
* @param start the index at which to start computing the first set.
* @return first(<code>beta[start..beta.length-1]</code>).
*/
Set first(Object[] beta, int start) {
	first_beta.size = 0;

	for (int l = start; l < beta.length; l++) {
		Object X = beta[l];
/*\semantics*/		if (X instanceof Semantics) continue;
/*\off*/
		if (terminal(X)) {
			first_beta.add(X);
			return first_beta;
		}
		first_beta.add(first(X));
		if (!first(X).contains(null)) return first_beta;
	}
	first_beta.push(null);
	return first_beta;
}

/**
* <p>Computes the first set of a phrase.</p>
*
* @param beta the phrase whose first set is computed.
* @return first(<code>beta</code>).
*/
Set first(Object[] beta) {
	return first(beta, 0);
}
//follow
/**
* <p>The mapping from a nonterminal to its follow set in this <code>Grammar</code>. When empty, there is a need to compute current follow sets. It is computed only on demand created by {@link #follow(Object) <code>follow(nonterminal)</code>}.</p>
*/
private final Map follow;

/**
* <p>Returns the follow set of a nonterminal.</p>
*
* @param nonterminal the nonterminal whose follow set is requested.
* @return <code>{@link #follow}.get(nonterminal)</code>, computing {@link #follow} if there is a need to compute current follow sets.
*/
Set follow(Object nonterminal) {

	if (follow.isEmpty()) {

		for (Iterator j = this.P.keySet().iterator(); j.hasNext();)
			follow.put(j.next(), new Set(-50));

		if (S != null) follow(S).add($);

		boolean closed;
		do {
			closed = true;

			for (Iterator j = this.P.entrySet().iterator(); j.hasNext();) {
				Map.Entry entry = (Map.Entry)j.next();
				Object A = entry.getKey();
				Stack P = (Stack)entry.getValue();

				for (int k = 0; k < P.size; k++) {
					Object[] beta = ((Production)P.get[k]).beta;

					for (int l = 0; l < beta.length; l++) {
						Object B = beta[l];
						if (!nonterminal(B)) continue;

						Set delta = first(beta, l+1);

						if (follow(B).add(delta))
							closed = false;
						if (delta.contains(null) && follow(B).add(follow(A)))
							closed = false;
					}
				}
			}
		} while (!closed);/*debug*/
		if ((debug & FIRST_FOLLOW) > 0) System.out.println(
			"----follow\n\t" + follow.toString().replaceFirst("^\\{","").replaceAll("\\}, ", "}\n\t").replaceFirst("\\}$", "\n----------"));/*off*/
	}
	return (Set)follow.get(nonterminal);
}
//Grammar()
/**
* <p>Constructs an empty <code>Grammar</code>.</p>
*/
protected Grammar() {
	P = new HashMap(500);
	T = new HashSet(500);
	first = new HashMap(500);
	follow = new HashMap(500);
}

/**
* <p>Constructs a <code>Grammar</code> that is a shallow copy of <code>G</code>. The fields of the new <code>Grammar</code> refer to the same objects as those in <code>G</code>.</p>
*
* @param G the <code>Grammar</code> copied.
*/
Grammar(Grammar G) {
	super(G);
	S = G.S;
	P = G.P;
	T = G.T;
	first = G.first;
	follow = G.follow;
}

}
