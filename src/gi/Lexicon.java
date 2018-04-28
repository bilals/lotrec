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
* <p>This class implements a {@link Lexicon}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
public class Lexicon {
//Q
/**
* <p>The number of lexical NFA states constructed.</p>
*/
private static int QSize = 0;

/**
* <p>Creates a new state in the lexical NFA.</p>
*
* @return a new state in the lexical NFA.
*/
private static Integer s() {
	return new Integer(QSize++);
}
//Stack
/**
* <p>This class implements a {@link Lexicon.Stack <code>Stack</code>}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
static class Stack {

	/**
	* <p>The number of objects on this <code>Stack</code>.</p>
	*/
	int size = 0;
	/**
	* <p>The objects on this <code>Stack</code>.</p>
	*/
	Object[] get;

	/**
	* <p>Constructs a <code>Stack</code> with an initial capacity.</p>
	*
	* @param capacity the initial capacity.
	*/
	Stack(int capacity) {
		get = new Object[capacity];
	}

	/**
	* <p>Pushes an object onto this <code>Stack</code>. The capacity is expanded by 50% if necessary.</p>
	*
	* @param object the object to push onto this <code>Stack</code>.
	* @return <code>true</code> indicates this <code>Stack</code> is changed.
	*/
	boolean push(Object object) {
		if (size == get.length) System.arraycopy(get, 0,
			get = new Object[3*size/2], 0, size);
		get[size++] = object;
		return true;
	}

	/**
	* <p>Pops and returns the top object of this <code>Stack</code>.</p>
	*
	* @return the top object of this <code>Stack</code>.
	*/
	Object pop() {
		return get[--size];
	}

	/**
	* <p>Returns the top object of this <code>Stack</code>.</p>
	*
	* @return the top object of this <code>Stack</code>.
	*/
	Object top() {
		return get[size-1];
	}

	/**
	* <p>Returns the string representation of this <code>Stack</code>.</p>
	*
	* @return the string representation of this <code>Stack</code>.
	*/
        @Override
	public String toString() {
		StringBuffer result = new StringBuffer(80);
		result.append('{');

		for (int j = 0; j < size; j++) {
			if (j > 0) result.append(' ');
			result.append(get[j]);
		}
		result.append('}');
		return result.toString();
	}
//Stack
}
//delta
/**
* <p>The transition relation of the lexical NFA.</p>
*/
private static final Stack delta = new Stack(2000);

/**
* <p>Puts a transition into the lexical NFA.</p>
*
* @param s the state from which the transition is made.
* @param A the <code>Alphabet</code> on which the transition is made.
* @param r the state to which the transition is made.
*/
private static void put(Integer s, Alphabet A, Integer r) {

	for (int j = delta.size; j <= s.intValue(); j++) delta.push(null);

	Stack pairs = (Stack)delta.get[s.intValue()];
	if (pairs == null) delta.get[s.intValue()] = pairs = new Stack(4);

	pairs.push(A);
	pairs.push(r);
}
//Set
/**
* <p>This class implements a {@link Lexicon.Set <code>Set</code>}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
static class Set extends Stack {

	/**
	* <p>The null exclusion indicator. If <code>true</code>, <code>add</code> methods will not add <code>null</code> to this <code>Set</code>.</p>
	*/
	private final boolean excludeNull;

	/**
	* <p>Constructs a <code>Set</code> with an initial capacity.</p>
	*
	* @param capacity the initial capacity. The magnitude of <code>capacity</code> is the initial capacity. The null exclusion indicator is initialized to <code>true</code> if <code>capacity</code> is negative.
	*/
	Set(int capacity) {
		super(Math.abs(capacity));
		excludeNull = (capacity < 0);
	}

	/**
	* <p>Indicates whether an object occurs in this <code>Set</code>.</p>
	*
	* @param object the object whose membership if requested.
	* @return <code>true</code> if <code>o</code> occurs in this <code>Set</code>; <code>false</code> otherwise.
	*/
	boolean contains(Object object) {
		for (int j = size; --j >= 0;)
			if (get[j] == object) return true;
		return false;
	}

	/**
	* <p>Adds an object to this <code>Set</code>. The object is not added if it occurs in this <code>Set</code> or it is <code>null</code> and the null exclusion indicator is <code>true</code>. The capacity is expanded if necessary.</p>
	*
	* @param object the object to add to this <code>Set</code>.
	* @return <code>true</code> if this <code>Set</code> is changed; <code>false</code> otherwise.
	*/
	boolean add(Object object) {
		if (excludeNull && object == null || contains(object)) return false;
		return push(object);
	}

	/**
	* <p>Adds a <code>Set</code> of objects to this <code>Set</code>. An object is not added if it occurs in this <code>Set</code> or it is <code>null</code> and the null exclusion indicator is <code>true</code>. The capacity is expanded if necessary.</p>
	*
	* @param S the <code>Set</code> to add to this <code>Set</code>.
	* @param start the index in <code>S</code> beyond which objects are added.
	* @return <code>true</code> if this <code>Set</code> is changed; <code>false</code> otherwise.
	*/
	boolean add(Set S, int start) {
		if (S == null) return false;
		boolean push = (size == 0);
		boolean changed = false;

		for (int j = start; j < S.size; j++) {
			Object object = S.get[j];
			if (excludeNull && object == null) continue;
			if (push ? push(object) : add(object)) changed = true;
		}
		return changed;
	}

	/**
	* <p>Adds a <code>Set</code> of objects to this <code>Set</code>. An object is not added if it occurs in this <code>Set</code> or it is <code>null</code> and the null exclusion indicator is <code>true</code>. The capacity is expanded if necessary.</p>
	*
	* @param S the <code>Set</code> to add to this <code>Set</code>.
	* @return <code>true</code> if this <code>Set</code> is changed; <code>false</code> otherwise.
	*/
	boolean add(Set S) {
		return add(S, 0);
	}
}
//I
/**
* <p>The initial states of the lexical NFA. When empty, there is a need to compute the current initial states. It is computed only on demand created by {@link #initial()}.</p>
*/
private final Set I;
//F
/**
* <p>The final states of the lexical NFA. A final state is mapped to the terminal it accepts in this <code>Lexicon</code>. When empty, there is a need to compute current final states. It is computed only on demand created by {@link #initial()}.</p>
*/
private final Map F;
//Lexicon.transition
/**
* <p>Computes a transition using the lexical NFA.</p>
*
* @param S the states from which the transition is made.
* @param a the character on which the transition is made.
* @param R the states to which the transition is made.
* @return the states to which the transition is made.
*/
private static Set transition(Set S, char a, Set R) {
	R.size = 0;

	for (int j = 0; j < S.size; j++) {
		Integer s = (Integer)S.get[j];
		Stack pairs = (Stack)delta.get[s.intValue()];
		if (pairs == null) continue;

		for (int k = 0; k < pairs.size; k += 2) {
			Alphabet A = (Alphabet)pairs.get[k];
			if (A == null) continue;

			Integer r = (Integer)pairs.get[k+1];
			if (A.contains(a)) R.add(r);
		}
	}
	return R;
}
//Lexicon.closure
/**
* <p>Computes a reflexive transitive closure under empty transition using the lexical NFA. The closure is computed in place by a breadth-first search expanding <code>S</code>.</p>
*
* @param S the states whose reflexive transitive closure is computed under empty transition.
* @return the reflexive transitive closure of <code>S</code> under empty transition.
*/
private static Set closure(Set S) {

	for (int j = 0; j < S.size; j++) {
		Integer s = (Integer)S.get[j];
		Stack pairs = (Stack)delta.get[s.intValue()];
		if (pairs == null) continue;

		for (int k = 0; k < pairs.size; k += 2) {
			Alphabet A = (Alphabet)pairs.get[k];
			if (A != null) continue;

			Integer r = (Integer)pairs.get[k+1];
			S.add(r);
		}
	}
	return S;
}
//Expression
/**
* <p>This class implements an {@link Lexicon.Expression <code>Expression</code>} expressing a regular language.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
abstract protected static class Expression {

	/**
	* <p>The initial state of the NFA constructed from this <code>Expression</code>.</p>
	*/
	Integer i;
	/**
	* <p>The final state of the NFA constructed from this <code>Expression</code>.</p>
	*/
	Integer f;

	/**
	* <p>Creates a copy of this <code>Expression</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Expression</code>.
	*/
	abstract Expression copy();
}
//Alphabet
/**
* <p>This class implements an {@link Lexicon.Alphabet <code>Alphabet</code>} of character symbols.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
abstract protected static class Alphabet extends Expression {

	/**
	* <p>Indicates whether a character occurs in this <code>Alphabet</code>.</p>
	*
	* @param a the character whose status is requested.
	* @return <code>true</code> if <code>a</code> occurs in this <code>Alphabet</code>; <code>false</code> otherwise.
	*/
	abstract boolean contains(char a);
}
//Match
/**
* <p>This class implements an {@link Lexicon.Alphabet <code>Alphabet</code>} containing some characters.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class Match extends Alphabet {

	/**
	* <p>The {@link Character} or {@link String} representing this <code>Alphabet</code>.</p>
	*/
	final Object A;

	/**
	* <p>Constructs an <code>Alphabet</code> containing some characters, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param i the initial state of the NFA constructed.
	* @param A the {@link Character} or {@link String} of characters in this <code>Alphabet</code>.
	* @param f the final state of the NFA constructed.
	*/
	private Match(Integer i, Object A, Integer f) {
		this.A = A;
		put(this.i = i, this, this.f = f);
	}

	/**
	* <p>Constructs an <code>Alphabet</code> containing one character, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param i the initial state of the NFA constructed.
	* @param a the character in this <code>Alphabet</code>.
	* @param f the final state of the NFA constructed.
	*/
	private Match(Integer i, char a, Integer f) {
		this(i, new Character(a), f);
	}

	/**
	* <p>Constructs an <code>Alphabet</code> containing one character, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param a the character in this <code>Alphabet</code>.
	*/
	public Match(char a) {
		this(s(), a, s());
	}

	/**
	* <p>Constructs an <code>Alphabet</code> containing some characters, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param A the {@link Character} or {@link String} of characters in this <code>Alphabet</code>.
	*/
	public Match(Object A) {
		this(s(), A, s());
	}

	/**
	* <p>Indicates whether a character occurs in this <code>Alphabet</code>.</p>
	*
	* @param a the character whose status is requested.
	* @return <code>true</code> if <code>a</code> occurs in this <code>Alphabet</code>; <code>false</code> otherwise.
	*/
	boolean contains(char a) {
		if (A instanceof Character)
			return ((Character)A).charValue() == a;

		if (A instanceof String)
			return ((String)A).indexOf(a) != -1;

		if (A instanceof Stack)
			for (int j = 0; j < ((Stack)A).size; j++)
				if (((Alphabet)((Stack)A).get[j]).contains(a)) return true;
		return false;
	}

	/**
	* <p>Creates a copy of this <code>Alphabet</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Alphabet</code>.
	*/
	Expression copy() {
		return new Match(A);
	}
}
//NonMatch
/**
* <p>This class implements an {@link Lexicon.Alphabet <code>Alphabet</code>} containing all except some characters.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class NonMatch extends Match {

	/**
	* <p>Constructs an <code>Alphabet</code> containing all characters except one, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param a the character not in this <code>Alphabet</code>.
	*/
	public NonMatch(char a) {
		super(a);
	}

	/**
	* <p>Constructs an <code>Alphabet</code> containing all characters except some, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param A the {@link Character} or {@link String} of characters not in this <code>Alphabet</code>.
	*/
	public NonMatch(Object A) {
		super(A);
	}

	/**
	* <p>Indicates whether a character occurs in this <code>Alphabet</code>.</p>
	*
	* @param a the character whose status is requested.
	* @return <code>true</code> if <code>a</code> occurs in this <code>Alphabet</code>; <code>false</code> otherwise.
	*/
        @Override
	boolean contains(char a) {
		return a != (char)-1 && !super.contains(a);
	}

	/**
	* <p>Creates a copy of this <code>Alphabet</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Alphabet</code>.
	*/
        @Override
	Expression copy() {
		return new NonMatch(A);
	}
}
//Range
/**
* <p>This class implements an {@link Lexicon.Alphabet <code>Alphabet</code>} containing the characters in a range.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class Range extends Alphabet {

	/**
	* <p>The first character in the range.</p>
	*/
	private final char a1;
	/**
	* <p>The last character in the range.</p>
	*/
	private final char a2;

	/**
	* <p>Constructs an <code>Alphabet</code> containing the characters in a range, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param a1 the first character in the range.
	* @param a2 the last character in the range.
	*/
	public Range(char a1, char a2) {
		this.a1 = a1;
		this.a2 = a2;
		put(i = s(), this, f = s());
	}

	/**
	* <p>Indicates whether a character occurs in this <code>Alphabet</code>.</p>
	*
	* @param a the character whose status is requested.
	* @return <code>true</code> if <code>a</code> occurs in this <code>Alphabet</code>; <code>false</code> otherwise.
	*/
	boolean contains(char a) {
		return a1 <= a && a <= a2;
	}

	/**
	* <p>Creates a copy of this <code>Alphabet</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Alphabet</code>.
	*/
	Expression copy() {
		return new Range(a1, a2);
	}
}

/**
* <p>This class implements an {@link Lexicon.Alphabet <code>Alphabet</code>} containing the characters in a POSIX character class.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class PosixClass extends Alphabet {

	/**
	* <p>The bit mask representing this <code>PosixClass</code>.</p>
	*/
	private final int posixClass;

	/**
	* <p>Constructs an <code>Alphabet</code> containing the characters in a POSIX character class, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param posixClass the bit mask representing this <code>PosixClass</code>.
	*/
	private PosixClass(int posixClass) {
		this.posixClass = posixClass;
		put(i = s(), this, f = s());
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the uppercase alphabetic characters.</p>
	*/
	public static PosixClass upper() {
		return new PosixClass(0x0001);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the lowercase alphabetic characters.</p>
	*/
	public static PosixClass lower() {
		return new PosixClass(0x0002);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the alphabetic characters.</p>
	*/
	public static PosixClass alpha() {
		return new PosixClass(0x0004);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the decimal digit characters.</p>
	*/
	public static PosixClass digit() {
		return new PosixClass(0x0008);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the hexadecimal digit characters.</p>
	*/
	public static PosixClass xdigit() {
		return new PosixClass(0x0010);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the alphanumeric characters.</p>
	*/
	public static PosixClass alnum() {
		return new PosixClass(0x0020);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the punctuation characters.</p>
	*/
	public static PosixClass punct() {
		return new PosixClass(0x0040);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the graphical characters.</p>
	*/
	public static PosixClass graph() {
		return new PosixClass(0x0080);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the printable characters.</p>
	*/
	public static PosixClass print() {
		return new PosixClass(0x0100);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the blank characters.</p>
	*/
	public static PosixClass blank() {
		return new PosixClass(0x0200);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the space characters.</p>
	*/
	public static PosixClass space() {
		return new PosixClass(0x0400);
	}

	/**
	* <p>Creates an <code>Alphabet</code> containing the control characters.</p>
	*/
	public static PosixClass cntrl() {
		return new PosixClass(0x0800);
	}

	/**
	* <p>Indicates whether a character occurs in this <code>Alphabet</code>.</p>
	*
	* @param a the character whose status is requested.
	* @return <code>true</code> if <code>a</code> occurs in this <code>Alphabet</code>; <code>false</code> otherwise.
	*/
	boolean contains(char a) {
		int UPPER = 0x0001; int LOWER = 0x0002;
		int ALPHA = 0x0004; int DIGIT = 0x0008;
		int XDIGIT = 0x0010; int ALNUM = 0x0020;
		int PUNCT = 0x0040; int GRAPH = 0x0080;
		int PRINT = 0x0100; int BLANK = 0x0200;
		int SPACE = 0x0400; int CNTRL = 0x0800;
		int classes = 0;

		switch (Character.getType(a)) {
			default: break;
			case Character.UPPERCASE_LETTER:
				classes |= UPPER | ALPHA | (('A' <= a && a <= 'F') ? XDIGIT : 0) | ALNUM | GRAPH | PRINT; break;
			case Character.LOWERCASE_LETTER:
				classes |= LOWER | ALPHA | (('a' <= a && a <= 'f') ? XDIGIT : 0) | ALNUM | GRAPH | PRINT; break;
			case Character.TITLECASE_LETTER:
			case Character.MODIFIER_LETTER:
			case Character.OTHER_LETTER:
				classes |= ALPHA | ALNUM | GRAPH | PRINT; break;
			case Character.NON_SPACING_MARK:
			case Character.COMBINING_SPACING_MARK:
			case Character.ENCLOSING_MARK:
				classes |= PUNCT | GRAPH | PRINT; break;
			case Character.DECIMAL_DIGIT_NUMBER:
				classes |= DIGIT | XDIGIT | ALNUM | GRAPH | PRINT; break;
			case Character.LETTER_NUMBER:
			case Character.OTHER_NUMBER:
				classes |= ALNUM | GRAPH | PRINT; break;
			case Character.CONNECTOR_PUNCTUATION:
			case Character.DASH_PUNCTUATION:
			case Character.START_PUNCTUATION:
			case Character.END_PUNCTUATION:
			case Character.INITIAL_QUOTE_PUNCTUATION:
			case Character.FINAL_QUOTE_PUNCTUATION:
			case Character.OTHER_PUNCTUATION:
			case Character.MATH_SYMBOL:
			case Character.CURRENCY_SYMBOL:
			case Character.MODIFIER_SYMBOL:
			case Character.OTHER_SYMBOL:
				classes |= PUNCT | GRAPH | PRINT; break;
			case Character.SPACE_SEPARATOR:
				classes |= PRINT | BLANK | SPACE; break;
			case Character.LINE_SEPARATOR:
			case Character.PARAGRAPH_SEPARATOR:
				break;
			case Character.CONTROL:
				classes |= ((a == '\t') ? BLANK : 0) | ((a == '\t' || a == '\n' || a == '\013' || a == '\f' || a == '\r') ? SPACE : 0) | CNTRL; break;
			case Character.FORMAT:
			case Character.SURROGATE:
			case Character.PRIVATE_USE:
			case Character.UNASSIGNED:
				break;
		}
		return (classes & posixClass) != 0;
	}

	/**
	* <p>Creates a copy of this <code>Alphabet</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Alphabet</code>.
	*/
	Expression copy() {
		return new PosixClass(posixClass);
	}
}
//UnicodeCategory
/**
* <p>This class implements an {@link Lexicon.Alphabet <code>Alphabet</code>} containing the characters in a Unicode general category.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class UnicodeCategory extends Alphabet {

	/**
	* <p>The byte representing the Unicode general category.</p>
	*/
	private final byte category;

	/**
	* <p>Constructs an <code>Alphabet</code> containing the characters in a Unicode general category, and builds the NFA constructed from this <code>Expression</code>. The class {@link Character} defines byte constants representing each of the Unicode general categories.</p>
	*
	* @param category The byte representing the Unicode general category.
	* @see Character
	*/
	public UnicodeCategory(byte category) {
		this.category = category;
		put(i = s(), this, f = s());
	}

	/**
	* <p>Indicates whether a character occurs in this <code>Alphabet</code>.</p>
	*
	* @param a the character whose status is requested.
	* @return <code>true</code> if <code>a</code> occurs in this <code>Alphabet</code>; <code>false</code> otherwise.
	*/
	boolean contains(char a) {
		return Character.getType(a) == category;
	}

	/**
	* <p>Creates a copy of this <code>Alphabet</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Alphabet</code>.
	*/
	Expression copy() {
		return new UnicodeCategory(category);
	}
}
//Repetition
/**
* <p>This class implements an {@link Lexicon.Expression <code>Expression</code>} expressing the repetition of a regular language.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class Repetition extends Expression {

	/**
	* <p>The operand <code>Expression</code>.</p>
	*/
	private final Expression e1;
	/**
	* <p>The minimum number of times <code>e1</code> is repeated.</p>
	*/
	private final int min;
	/**
	* <p>The maximum number of times <code>e1</code> is repeated.</p>
	*/
	private final int max;

	/**
	* <p>Constructs an <code>Expression</code> expressing the repetition of a regular language, and builds the NFA constructed from this <code>Expression</code>. Large finite values for the minimum or maximum cause the NFA constructed from the operand <code>Expression</code> to be copied many times, resulting in a space-inefficient NFA.</p>
	*
	* @param e1 the operand <code>Expression</code>.
	* @param min the minimum number of times <code>e1</code> is repeated. If negative, it is assumed to be zero.
	* @param max the maximum number of times <code>e1</code> is repeated. If negative, it is assumed to be infinity.
	*/
	public Repetition(Expression e1, int min, int max) {
		this.e1 = e1 = e1.copy();
		this.min = min = Math.max(min, 0);
		this.max = max;

		i = (min > 0) ? e1.i : s();
		f = (min > 0) ? e1.f : i;

		if (min == 0 && max < 0) {
			put(i, null, e1.i);
			put(e1.f, null, i);
		}
		else {
			for (int k = 2; k <= min; k++) {
				e1 = e1.copy();
				put(f, null, e1.i);
				f = e1.f;
			}
			if (max > min) {
				Integer tail = f;
				put(tail, null, f = s());

				for (int k = min+1; k <= max; k++) {
					if (k > 1) e1 = e1.copy();
					put(tail, null, e1.i);
					put(tail = e1.f, null, f);
				}
			}
			else if (max < 0) put(f, null, e1.i);
		}
	}

	/**
	* <p>Creates a copy of this <code>Expression</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Expression</code>.
	*/
	Expression copy() {
		return new Repetition(e1, min, max);
	}
}
//Concatenation
/**
* <p>This class implements an {@link Lexicon.Expression <code>Expression</code>} expressing the concatenation of two regular languages.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class Concatenation extends Expression {

	/**
	* <p>The left operand <code>Expression</code>.</p>
	*/
	private final Expression e1;
	/**
	* <p>The right operand <code>Expression</code>.</p>
	*/
	private final Expression e2;

	/**
	* <p>Constructs an <code>Expression</code> expressing the concatenation of two regular languages, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param e1 the left operand <code>Expression</code>.
	* @param e2 the right operand <code>Expression</code>.
	*/
	public Concatenation(Expression e1, Expression e2) {
		this.e1 = e1 = e1.copy();
		this.e2 = e2 = e2.copy();

		i = e1.i;
		f = e2.f;

		put(e1.f, null, e2.i);
	}

	/**
	* <p>Creates a copy of this <code>Expression</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Expression</code>.
	*/
	Expression copy() {
		return new Concatenation(e1, e2);
	}
}
//Singleton
/**
* <p>This class implements an {@link Lexicon.Expression <code>Expression</code>} expressing a singleton language.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class Singleton extends Expression {

	/**
	* <p>The string whose singleton language is expressed.</p>
	*/
	private final String x;

	/**
	* <p>Constructs an <code>Expression</code> expressing a singleton language, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param x the string whose singleton language is expressed.
	*/
	public Singleton(String x) {
		this.x = x;

		f = i = s();

		for (int l = 0; l < x.length(); l++)
			new Match(f, x.charAt(l), f = s());
	}

	/**
	* <p>Creates a copy of this <code>Expression</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Expression</code>.
	*/
	Expression copy() {
		return new Singleton(x);
	}
}
//Union
/**
* <p>This class implements an {@link Lexicon.Expression <code>Expression</code>} expressing the union of two regular languages.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected static class Union extends Expression {

	/**
	* <p>The left operand <code>Expression</code>.</p>
	*/
	private final Expression e1;
	/**
	* <p>The right operand <code>Expression</code>.</p>
	*/
	private final Expression e2;

	/**
	* <p>Constructs an <code>Expression</code> expressing the union of two regular languages, and builds the NFA constructed from this <code>Expression</code>.</p>
	*
	* @param e1 the left operand <code>Expression</code>.
	* @param e2 the right operand <code>Expression</code>.
	*/
	public Union(Expression e1, Expression e2) {
		this.e1 = e1 = e1.copy();
		this.e2 = e2 = e2.copy();

		i = s();
		f = s();

		put(i, null, e1.i); put(e1.f, null, f);
		put(i, null, e2.i); put(e2.f, null, f);
	}

	/**
	* <p>Creates a copy of this <code>Expression</code>, and replicates the NFA constructed from this <code>Expression</code>.</p>
	*
	* @return a copy of this <code>Expression</code>.
	*/
	Expression copy() {
		return new Union(e1, e2);
	}
}
//expression(ere)
/**
* <p>Creates an <code>Expression</code> by interpreting a POSIX extended regular expression (ERE), as used in egrep. The syntax and semantics for EREs is formally specified by the <a href="../../../src/gi/ERE.java">ERE <code>Grammar</code></a>. Provides a convenient method for constructing an <code>Expression</code>, at the cost of an LR(1) parse. Implementations seeking maximum speed should avoid this method and use explicit <code>Expression</code> subclass constructors; for example,</p>
* <blockquote><code>new Union(new NonMatch("0"), new Singleton("foo"))</code></blockquote>
* instead of
* <blockquote><code>expression("[^0]|foo")</code></blockquote>
*
* @param ere the POSIX extended regular expression (ERE) to interpret.
* @return the <code>Expression</code> constructed by interpreting <code>string</code>.
* @throws Lexicon.Exception if an ERE syntax error occurs.
*/
protected static Expression expression(String ere) throws Exception {
	return ERE.expression(ere);
}
//E
/**
* <p>The mapping representing this <code>Lexicon</code>. A terminal is mapped to the initial state of the NFA constructed from the associated <code>Expression</code>.</p>
*/
private final Map E;

/**
* <p>Puts a terminal and associated <code>Expression</code> into this <code>Lexicon</code>. The <code>Expression</code> supersedes any previously associated with the terminal.</p>
*
* @param a the terminal to add to this <code>Lexicon</code>.
* @param e the <code>Expression</code> associated with terminal <code>a</code>. When grabbing, the language expressed by <code>e</code> matches <code>a</code>.
*/
protected void put(Object a, Expression e) {
	E.put(a, e);
	I.size = 0;
	F.clear();
}

/**
* <p>Indicates whether a symbol is a terminal in this <code>Lexicon</code>.</p>
*
* @param a the symbol whose status is requested.
* @return <code>true</code> if <code>symbol</code> is a terminal in this <code>Lexicon</code>; <code>false</code> otherwise.
*/
boolean terminal(Object a) {
	return E.containsKey(a);
}
//Lexicon()
/**
* <p>The terminal matched by the character at the end of a source stream.</p>
* @since 1.1, renames <code>END_OF_SOURCE</code> in version 1.0.
*/
protected static final Object $ = new String("$");

/**
* <p>The <code>Alphabet</code> containing the character at the end of a source stream.</p>
*/
private static final Expression $_EXPRESSION = new Match((char)-1);

/**
* <p>Constructs an empty <code>Lexicon</code>.</p>
*/
protected Lexicon() {
	E = new HashMap(500);
	I = new Set(-200);
	F = new HashMap(500);
	put($, $_EXPRESSION);
}

/**
* <p>Constructs a <code>Lexicon</code> that is a shallow copy of <code>lexicon</code>. The fields of the new <code>Lexicon</code> refer to the same objects as those in <code>lexicon</code>.</p>
*
* @param lexicon the <code>Lexicon</code> to copy.
*/
Lexicon(Lexicon lexicon) {/*debug*/
	debug = lexicon.debug;/*off*/
	E = lexicon.E;
	I = lexicon.I;
	F = lexicon.F;
}
//Lexicon.initial
/**
* <p>Returns the initial states of the lexical NFA.</p>
*
* @return {@link #I}, computing it and {@link #F} if there is a need to compute the current initial states and final states.
*/
private Set initial() {

	if (I.size == 0) {

		for (Iterator j = E.entrySet().iterator(); j.hasNext();) {
			Map.Entry entry = (Map.Entry)j.next();
			Object a = entry.getKey();
			Expression e = (Expression)entry.getValue();

			I.add(e.i);
			F.put(e.f, a);
		}
		closure(I);
	}
	return I;
}
//accept
/**
* <p>Computes the current final state, if any, in the lexical NFA.</p>
*
* @param S the current states.
* @return the maximum final state in <code>S</code>. Returns <code>null</code> if <code>S</code> contains no final states.
*/
private Integer accept(Set S) {

	Integer f = null;

	for (int j = 0; j < S.size; j++) {
		Integer s = (Integer)S.get[j];

		if (F.containsKey(s))
			if (f == null || f.compareTo(s) < 0) f = s;
	}
	return f;
}

/**
* <p>This class implements an {@link Lexicon.Exception <code>Exception</code>}.</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
protected class Exception extends java.lang.Exception {

	/**
	* <p>The extended error message.</p>
	*/
	private StringBuffer message;

	/**
	* <p>Constructs an <code>Exception</code> with a message.</p>
	*
	* @param message the error message.
	*/
	public Exception(String message) {
		super(message);
	}

	/**
	* <p>Returns the error message.</p>
	*/
        @Override
	public String getMessage() {
		return (message == null) ? super.getMessage() : message.toString();
	}

	/**
	* <p>Extends the error message in this <code>Exception</code>. The extended message includes the line number, message and source characters following the error.</p>
	*
	* @param source the source character stream.
	* @return this <code>Exception</code> with an extended message.
	*/
	Exception extend(LineNumberReader source) {
		if (message == null) message = new StringBuffer();
		else message.setLength(0);

		message.append("line ");
		message.append(source.getLineNumber()+1);
		message.append(": ");
		message.append(super.getMessage());
		message.append(System.getProperty("line.separator"));
		message.append("...");
		message.append(word());
		try {
			String rest = source.readLine();
			if (rest != null) message.append(rest);
		}
		catch (IOException exception) {}
		message.append(System.getProperty("line.separator"));
		message.append("   ^");
		return this;
	}
}
//Lexicon.grab
/**
* <p>The states through which the lexical NFA transitions.</p>
*/
private final Set[] R = new Set[]{new Set(-200), new Set(-200)};
/**
* <p>The <code>StringBuffer</code> containing the word most recently grabbed.</p>
*/
private final StringBuffer w = new StringBuffer(4000);

/**
* <p>Grabs a terminal from a source character stream using this <code>Lexicon</code>. The variable returned by {@link #word()} is set to the longest nonempty prefix of the remaining source characters matching an <code>Expression</code> in this <code>Lexicon</code>. If no nonempty prefix matches an <code>Expression</code>, a <code>Lexicon.Exception</code> is thrown. If the longest matching prefix matches more than one <code>Expression</code>, the terminal associated with the <code>Expression</code> most recently constructed is returned. Blocks until a character is available, an I/O error occurs, or the end of the source stream is reached.</p>
*
* @param source the source character stream.
* @return the terminal grabbed from <code>source</code>.
* @throws Lexicon.Exception if an I/O or lexical error occurs.
*/
protected Object grab(LineNumberReader source) throws Exception {
	Set S = initial();
	w.setLength(0);
	int wLength = 0;
	Object b = null;
	try {
		source.mark(w.capacity());
		do {
			int a = source.read();
			S = closure(transition(S, (char)a, R[w.length() % 2]));

			if (S.size == 0) break;
			if (a != -1) w.append((char)a); else w.append($);
			Integer f = accept(S);
			if (f == null) continue;

			wLength = w.length();
			b = F.get(f);
			source.mark(w.capacity());
		} while (b != $);
		w.setLength(wLength);
		source.reset();
	}
	catch (IOException exception) {
		throw new Exception(exception.getMessage());
	}
	if (wLength == 0) throw new Exception("lexical error").extend(source);
	return b;
}

/**
* <p>Returns the word most recently grabbed using this <code>Lexicon</code>.</p>
*
* @return the word most recently grabbed by {@link #grab(java.io.LineNumberReader) <code>grab(source)</code>}.
*/
protected String word() {
	return w.substring(0);
}
//Lexicon.interpret
/**
* <p>Repeatedly invokes {@link #grab(java.io.LineNumberReader) <code>grab(source)</code>} until the end of the source stream reached. Blocks until a character is available, or an I/O error occurs. This method is overridden by <code>Grammar</code> and its parser subclasses, so it is only invoked when this <code>Lexicon</code> has not been extended into a <code>Grammar</code> or parser.</p>
*
* @param source the source character stream.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>. A <code>Lexicon</code> always returns null.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
Object interpret(LineNumberReader source) throws Exception {/*debug*/
	if ((debug & TERMINALS) > 0) System.out.println(
		"----terminals\n\t" + E.keySet().toString().replaceFirst("\\[", "{").replaceAll(", ", " ").replaceFirst("\\]$", "}\n----------"));/*off*/

	for (Object a; (a = grab(source)) != $;)
		/*debug*/if ((debug & LEXICAL) > 0) System.out.println(
			a + (!a.equals(word()) ? " " + word() : ""))/*off*/;
	return null;
}

/**
* <p>Interprets a source character stream using this <code>Lexicon</code>.</p>
*
* @param source the source character stream.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
public Object interpret(Reader source) throws Exception {
	return interpret(new LineNumberReader(source));
}

/**
* <p>Interprets a source string using this <code>Lexicon</code>.</p>
*
* @param source the source string.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
public Object interpret(String source) throws Exception {
	return interpret(new StringReader(source));
}

/**
* <p>Interprets a source byte stream using this <code>Lexicon</code>.</p>
*
* @param source the source byte stream.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
public Object interpret(InputStream source) throws Exception {
	return interpret(new InputStreamReader(source));
}

/**
* <p>Interprets the standard input stream using this <code>Lexicon</code>.</p>
*
* @return the <code>ParseTree</code> constructed by interpreting the standard input stream.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
public Object interpret() throws Exception {
	return interpret(System.in);
}

/**
* <p>Interprets a source file using this <code>Lexicon</code>.</p>
*
* @param source the source file.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
* @throws FileNotFoundException if the source file cannot be found.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
public Object interpret(File source) throws FileNotFoundException, Exception {
	return interpret(new FileReader(source));
}

/**
* <p>Interprets a source pipe using this <code>Lexicon</code>.</p>
*
* @param source the source pipe.
* @return the <code>ParseTree</code> constructed by interpreting <code>source</code>.
* @throws IOException if the source pipe cannot be connected.
* @throws Lexicon.Exception if an I/O, lexical, syntax or semantic error occurs.
*/
public Object interpret(PipedWriter source) throws IOException, Exception {
	return interpret(new PipedReader(source));
}
//Lexicon.interpret(arguments)
/**
* <p>The debug switches, initially zero. The following bits enable debugging to standard output:</p>
* <blockquote><dl>
*	<dt><code>0x01</code> = <code>TERMINALS</code></dt>
*	<dd>Print the set of terminals before lexical analysis</dd>
*	<dt><code>0x02</code> = <code>LEXICAL</code> </dt>
*	<dd>Print terminals and associated words grabbed during lexical analysis</dd>
*	<dt><code>0x04</code> = <code>FIRST_FOLLOW</code></dt>
*	<dd>Print first and follow sets precomputed during syntax analysis</dd>
*	<dt><code>0x08</code> = <code>SYNTAX</code></dt>
*	<dd>Print parsing decisions made during syntax analysis</dd>
*	<dt><code>0x10</code> = <code>CONFLICT</code></dt>
*	<dd>Print parsing conflicts encountered during syntax analysis</dd>
*	<dt><code>0x20</code> = <code>PARSE_TREE</code></dt>
*	<dd>Print each <code>ParseTree</code> produced by syntax analysis</dd>
* </dl></blockquote>
* @since 1.1
*/
protected int debug = 0;

/**
* <p>{@link #debug <code>debug</code>} switch constant enabling printing the set of terminals before lexical analysis.</p>
* @since 1.1
*/
protected static final int TERMINALS = 0x01;
/**
* <p>{@link #debug <code>debug</code>} switch constant enabling printing terminals and associated words grabbed during lexical analysis.</p>
* @since 1.1
*/
protected static final int LEXICAL = 0x02;
/**
* <p>{@link #debug <code>debug</code>} switch constant enabling all debugging.</p>
* @since 1.1
*/
protected static final int VERBOSE = 0xFF;

/**
* <p>Lexical analysis by command-line arguments using this <code>Lexicon</code>. The first I/O or lexical error that occurs during lexical analysis is printed to the standard error stream.</p>
*
* @param arguments the command-line arguments controlling the interpreter.
* <blockquote>
* The following arguments may appear zero or more times, are processed in order, and have the following effects:
* <blockquote><dl>
*	<dt><code>-t</code>, <code>--terminals</code></dt>
*	<dd>Print the set of terminals in this <code>Lexicon</code> before subsequent lexical analyses.</dd>
*	<dt><code>-l</code>, <code>--lexical</code></dt>
*	<dd>Print terminals in this <code>Lexicon</code> grabbed during subsequent lexical analyses.</dd>
*	<dt><code>-v</code>, <code>--verbose</code></dt>
*	<dd>Print maximum debugging. Equivalent to <code>-tl</code>.</dd>
*	<dt><code>-</code></dt>
*	<dd>Lexically analyze the standard input stream using this <code>Lexicon</code>.</dd>
*	<dt><code><var>filename</var></code></dt>
*	<dd>Lexically analyze source file <code><var>filename</var></code> using this <code>Lexicon</code>.</dd>
* </dl></blockquote>
* If no <code><var>filename</var></code> arguments are given, the standard input stream is lexically analyzed.
* </blockquote>
*/
public void interpret(String[] arguments) {

	boolean sourceSpecified = false;

	Getopt options = new Getopt(this.getClass().getName(), arguments,
		"-tlv", new LongOpt[]{
			new LongOpt("terminals", 0, null, 't'),
			new LongOpt("lexical", 0, null, 'l'),
			new LongOpt("verbose", 0, null, 'v'),
		}
	);
	for (int option; (option = options.getopt()) != -1;) {
		switch (option) {
		case 't': debug |= TERMINALS; break;
		case 'l': debug |= LEXICAL; break;
		case 'v': debug |= VERBOSE; break;
		case 1: String argument = options.getOptarg();
			try {
				sourceSpecified = true;
				if (argument.equals("-"))
					interpret();
				else
					interpret(new FileReader(argument));
			}
			catch (java.lang.Exception exception) {
				System.err.println(exception);
			}
		}
	}
	if (!sourceSpecified) try {
		interpret();
	}
	catch (java.lang.Exception exception) {
		System.err.println(exception);
	}
}

}
