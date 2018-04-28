/**
* Generic Interpreter (gi)
*
* Copyright (C) 1999-2004 Craig A. Rich <carich@csupomona.edu>
*/
package gi;

/**
* <p>This class implements a {@link Grammar} for interpreting POSIX extended regular expressions (EREs).</p>
*
* @version 1.2
* @author &copy; 1999-2004 <a href="http://www.csupomona.edu/~carich/">Craig A. Rich</a> &lt;<a href="mailto:carich@csupomona.edu">carich@csupomona.edu</a>&gt;
*/
class ERE extends LR1_Grammar {

	/**
	* <p>The <code>Grammar</code> for POSIX extended regular expressions (EREs).</p>
	*/
	private static Grammar ere;

	/**
	* <p>Constructs a <code>Grammar</code> for POSIX extended regular expressions (EREs).</p>
	*/
	ERE() {
		put("BASIC_ELEM", new NonMatch("^-].[$()|*+?{\\}"));
		put("DIGIT", new UnicodeCategory(Character.DECIMAL_DIGIT_NUMBER));

		Semantics union = new Semantics() {
			protected void f(ParseTree t, int l) {
				Expression e1 = (Expression)t.child[l-3].value;
				Expression e2 = (Expression)t.child[l-1].value;
				t.value = new Union(e1, e2);
			}
		};
		Semantics concatenation = new Semantics() {
			protected void f(ParseTree t, int l) {
				Expression e1 = (Expression)t.child[l-2].value;
				Expression e2 = (Expression)t.child[l-1].value;
				t.value = new Concatenation(e1, e2);
			}
		};
		Semantics repetition = new Semantics() {
			protected void f(ParseTree t, int l) {
				ParseTree[] c = t.child;
				Expression e1 = (Expression)t.child[0].value;
				int min = 0;
				int max = -1;

				if (l == 2) {
					String power = (String)t.child[l-1].value;
					if (power.equals("+")) min = 1;
					else if (power.equals("?")) max = 1;
				}
				else {
					min = Integer.parseInt((String)t.child[2].value);
					if (l == 3) max = min;
					else if (l == 5) max = Integer.parseInt((String)t.child[l-1].value);
				}
				t.value = new Repetition(e1, min, max);
			}
		};
		Semantics any = new Semantics() {
			protected void f(ParseTree t, int l) {
				t.value = new NonMatch("");
			}
		};

		Semantics match = new Semantics() {
			protected void f(ParseTree t, int l) {
				Object A = t.child[l-1].value;
				t.value = new Match(A);
			}
		};
		Semantics nonmatch = new Semantics() {
			protected void f(ParseTree t, int l) {
				Object A = t.child[l-1].value;
				t.value = new NonMatch(A);
			}
		};
		Semantics range = new Semantics() {
			protected void f(ParseTree t, int l) {
				char a1 = ((String)t.child[l-2].value).charAt(0);
				char a2 = ((String)t.child[l-1].value).charAt(0);
				t.value = new Range(a1, a2);
			}
		};
		Semantics posixclass = new Semantics() {
			protected void f(ParseTree t, int l) throws Exception {
				String name = (String)t.child[l-1].value;
				Expression expression;
				if (name.equalsIgnoreCase("upper")) expression = PosixClass.upper();
				else if (name.equalsIgnoreCase("lower")) expression = PosixClass.lower();
				else if (name.equalsIgnoreCase("alpha")) expression = PosixClass.alpha();
				else if (name.equalsIgnoreCase("digit")) expression = PosixClass.digit();
				else if (name.equalsIgnoreCase("xdigit")) expression = PosixClass.xdigit();
				else if (name.equalsIgnoreCase("alnum")) expression = PosixClass.alnum();
				else if (name.equalsIgnoreCase("punct")) expression = PosixClass.punct();
				else if (name.equalsIgnoreCase("graph")) expression = PosixClass.graph();
				else if (name.equalsIgnoreCase("print")) expression = PosixClass.print();
				else if (name.equalsIgnoreCase("blank")) expression = PosixClass.blank();
				else if (name.equalsIgnoreCase("space")) expression = PosixClass.space();
				else if (name.equalsIgnoreCase("cntrl")) expression = PosixClass.cntrl();
				else throw new Exception("invalid POSIX character class name");
				t.value = expression;
			}
		};
		Semantics push = new Semantics() {
			protected void f(ParseTree t, int l) {
				Stack A = (l == 1) ? new Stack(10) : (Stack)t.child[l-2].value;
				A.push(t.child[l-1].value);
				t.value = A;
			}
		};

		Semantics emptystring = new Semantics() {
			protected void f(ParseTree t, int l) {
				t.value = "";
			}
		};
		Semantics append = new Semantics() {
			protected void f(ParseTree t, int l) {
				ParseTree[] c = t.child;
				t.value = (String)c[l-2].value + (String)c[l-1].value;
			}
		};
		Semantics synth = new Semantics() {
			protected void f(ParseTree t, int l) {
				t.value = t.child[l-1].value;
			}
		};

		/**
		* Syntax Specification
		*
		* IEEE P1003.2 Draft 11.2
		* Copyright (C) 1991 IEEE
		*/
		put("ERE", new Object[][] {
			{"AnchoredERE", synth},
			{"NonAnchoredERE", synth},
			{"ERE", "|", "NonAnchoredERE", union},
			{"ERE", "|", "AnchoredERE", union}
		});
		put("AnchoredERE", new Object[][] {
			{"^", "NonAnchoredERE", synth},
			{"^", "NonAnchoredERE", synth, "$"},
			{"NonAnchoredERE", synth, "$"},
			{"^", emptystring},
			{"$", emptystring},
			{"^", "$", emptystring}
		});
		put("NonAnchoredERE", new Object[][] {
			{"ERExpression", synth},
			{"NonAnchoredERE", "ERExpression", concatenation}
		});
		put("ERExpression", new Object[][] {
			{"OneCharacterERE", synth},
			{"(", "ERE", synth, ")"},
			{"ERExpression", "*", repetition},
			{"ERExpression", "+", repetition},
			{"ERExpression", "?", repetition},
			{"ERExpression", "{", "Digits", repetition, "}"},
			{"ERExpression", "{", "Digits", ",", repetition, "}"},
			{"ERExpression", "{", "Digits", ",", "Digits", repetition, "}"}
		});
		put("Digits", new Object[][] {
			{"DIGIT", synth},
			{"Digits", "DIGIT", append}
		});
		put("OneCharacterERE", new Object[][] {
			{"OrdChar", match},
			{"\\", "AnyChar", match},
			{".", any},
			{"BracketExpression", synth}
		});

		/**
		* Syntax Specification for Bracket Expressions
		*
		* IEEE P1003.2 Draft 11.2
		* Copyright (C) 1991 IEEE
		*/
		put("BracketExpression", new Object[][] {
			{"[", "MatchingList", synth, "]"},
			{"[", "NonMatchingList", synth, "]"}
		});
		put("MatchingList", new Object[][] {
			{"BracketList", match}
		});
		put("NonMatchingList", new Object[][] {
			{"^", "BracketList", nonmatch}
		});
		put("BracketList", new Object[][] {
			{"FollowList", synth},
			{"FollowList", "-", push}
		});
		put("FollowList", new Object[][] {
			{"ExpressionTerm", push},
			{"FollowList", "ExpressionTerm", push}
		});
		put("ExpressionTerm", new Object[][] {
			{"SingleExpression", synth},
			{"RangeExpression", synth}
		});
		put("SingleExpression", new Object[][] {
			{"EndRange", match},
			{"CharacterClass", synth}
		});
		put("RangeExpression", new Object[][] {
			{"StartRange", "EndRange", range},
			{"StartRange", "-", range}
		});
		put("StartRange", new Object[][] {
			{"EndRange", synth, "-"}
		});
		put("EndRange", new Object[][] {
			{"CollElem", synth},
			{"^", synth},
			{"-", synth},
//			{"]", synth},
			{"CollatingSymbol", synth}
		});
		put("CollatingSymbol", new Object[][] {
			{"[", ".", "CollElem", synth, ".", "]"},
			{"[", ".", "^", synth, ".", "]"},
			{"[", ".", "-", synth, ".", "]"},
			{"[", ".", "]", synth, ".", "]"},
			{"\\", "AnyChar", synth}
		});

		put("CharacterClass", new Object[][] {
			{"[", ":", "ClassName", posixclass, ":", "]"}
		});
		put("ClassName", new Object[][] {
			{"BASIC_ELEM", synth},
			{"ClassName", "BASIC_ELEM", append}
		});

		/**
		* pseudo-terminal symbols
		*/
		put("CollElem", new Object[][] { // new NonMatch("^-]")
			{"BASIC_ELEM", synth},
			{"DIGIT", synth},
			{",", synth},
			{":", synth},
			{"SpecCharNoAnchor", synth},
			{"}", synth}
		});
		put("AnyChar", new Object[][] { // new NonMatch("")
			{"CollElem", synth},
			{"^", synth},
			{"-", synth},
			{"]", synth}
		});
		put("SpecCharNoAnchor", new Object[][] { // new Match(".[$()|*+?{\\"))
			{".", synth},
			{"[", synth},
			{"$", synth},
			{"(", synth},
			{")", synth},
			{"|", synth},
			{"*", synth},
			{"+", synth},
			{"?", synth},
			{"{", synth},
			{"\\", synth}
		});
		put("SpecChar", new Object[][] { // new Match("^.[$()|*+?{\\"))
			{"^", synth},
			{"SpecCharNoAnchor", synth}
		});
		put("OrdChar", new Object[][] { // new NonMatch("^.[$()|*+?{\\"))
			{"BASIC_ELEM", synth},
			{"DIGIT", synth},
			{",", synth},
			{":", synth},
			{"-", synth},
			{"]", synth},
			{"}", synth}
		});
	}

	/**
	* <p>Creates an <code>Expression</code> by interpreting a POSIX extended regular expression (ERE), as used in egrep.</p>
	*
	* @param string the POSIX extended regular expression (ERE) to interpret.
	* @return the <code>Expression</code> constructed by interpreting <code>string</code>.
	* @throws Lexicon.Exception if a syntax error occurs.
	*/
	protected static Expression expression(String string) throws Exception {
		if (ere == null) ere = new ERE();
		return (Expression)((ParseTree)ere.interpret(string)).value;
	}
}
