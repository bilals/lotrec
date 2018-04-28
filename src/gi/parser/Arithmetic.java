package gi.parser;

//Arithmetic
import gi.*;

class Arithmetic extends SLR1_Grammar {

	Arithmetic() throws Exception {
		put("LITERAL", expression("[[:digit:]]+"));
		put("SPACE", expression("[[:space:]]+"));

		// /*\semantics*/semantic specification/*\off*/
//Arithmetic.S
/*\semantics*/		Semantics print = new Semantics() {
			public void f(ParseTree t, int l) {
				System.out.println(t.child[l-1].value);
			}
		};
/*\off*/		put("S", new Object[][] {
			{"E"/*\semantics*/, print/*\off*/}
		});
//Arithmetic.E
/*\semantics*/		Semantics identity = new Semantics() {
			public void f(ParseTree t, int l) {
				t.value = t.child[l-1].value;
			}
		};
		Semantics add = new Semantics() {
			public void f(ParseTree t, int l) {
				int left = ((Integer)t.child[l-3].value).intValue();
				int right = ((Integer)t.child[l-1].value).intValue();
				t.value = new Integer(left + right);
			}
		};
		Semantics multiply = new Semantics() {
			public void f(ParseTree t, int l) {
				int left = ((Integer)t.child[l-3].value).intValue();
				int right = ((Integer)t.child[l-1].value).intValue();
				t.value = new Integer(left * right);
			}
		};
/*\off*/		put("E", new Object[][] {
			{"T"/*\semantics*/, identity/*\off*/},
			{"E", "+", "T"/*\semantics*/, add/*\off*/},
		});
		put("T", new Object[][] {
			{"F"/*\semantics*/, identity/*\off*/},
			{"T", "*", "F"/*\semantics*/, multiply/*\off*/},
		});
//Arithmetic.F
/*\semantics*/		Semantics decode = new Semantics() {
			public void f(ParseTree t, int l) {
				t.value = Integer.decode((String)t.child[l-1].value);
			}
		};
/*\off*/		put("F", new Object[][] {
			{"LITERAL"/*\semantics*/, decode/*\off*/},
			{"(", "E", /*\semantics*/identity, /*\off*/")"},
		});
//Arithmetic

		debug = PARSE_TREE;
	}

	public static void main(String[] arguments) throws Exception {
		new Arithmetic().interpret(arguments);
	}
}
