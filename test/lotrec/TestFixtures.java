package lotrec;

import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.expression.Expression;
import lotrec.parser.LogicXMLParser;
import lotrec.parser.OldiesTokenizer;
import lotrec.parser.exceptions.ParseException;

/**
 * Shared test fixtures and utilities for LoTREC test suite.
 * Provides factory methods for creating common test objects.
 */
public final class TestFixtures {

    private TestFixtures() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a minimal Logic with standard modal logic connectors.
     * Includes: not (unary), and, or, imp (binary), nec, pos (unary modal).
     * @return Logic configured for basic modal logic testing
     */
    public static Logic createMinimalLogic() {
        Logic logic = new Logic();
        logic.setName("TestLogic");
        logic.addConnector(new Connector("not", 1, "~_"));
        logic.addConnector(new Connector("and", 2, "_&_"));
        logic.addConnector(new Connector("or", 2, "_|_"));
        logic.addConnector(new Connector("imp", 2, "_->_"));
        logic.addConnector(new Connector("nec", 1, "[]_"));
        logic.addConnector(new Connector("pos", 1, "<>_"));
        return logic;
    }

    /**
     * Creates and initializes an OldiesTokenizer for the given logic.
     * @param logic the Logic containing connector definitions
     * @return initialized OldiesTokenizer ready for parsing
     */
    public static OldiesTokenizer createTokenizer(Logic logic) {
        OldiesTokenizer tokenizer = new OldiesTokenizer(logic);
        tokenizer.initializeTokenizerAndProps();
        return tokenizer;
    }

    /**
     * Returns the path to a predefined logic XML file.
     * @param name the logic name (without .xml extension)
     * @return path to the logic XML file
     */
    public static String logicPath(String name) {
        return "src/lotrec/logics/" + name + ".xml";
    }

    /**
     * Loads and parses a predefined logic by name.
     * @param name the logic name (without .xml extension)
     * @return parsed Logic object
     * @throws ParseException if parsing fails
     */
    public static Logic loadLogic(String name) throws ParseException {
        LogicXMLParser parser = new LogicXMLParser();
        return parser.parseLogic(logicPath(name));
    }

    /**
     * Creates args array for Launcher.treatArgsForBenchmark().
     * Note: treatArgsForBenchmark requires 4 arguments.
     * @param logicName One of 38 predefined logic names (without .xml extension)
     * @param formulaInfix Formula in infix format (use expression.getCodeString())
     * @param stopAtFirstOpen true = SAT mode (stop at first open), false = build all
     * @param negateFormula true to test negation of formula
     * @return String array suitable for Launcher.treatArgsForBenchmark()
     */
    public static String[] benchmarkArgs(String logicName, String formulaInfix,
            boolean stopAtFirstOpen, boolean negateFormula) {
        return new String[] {
            logicName,
            formulaInfix,
            String.valueOf(stopAtFirstOpen),
            String.valueOf(negateFormula)
        };
    }

    /**
     * Converts an Expression to infix format suitable for benchmark.
     * @param expr the Expression to convert
     * @return infix string representation
     */
    public static String toInfixCode(Expression expr) {
        return expr.getCodeString();
    }

    /**
     * All 38 predefined logic names for parameterized tests.
     * These match the XML files in src/lotrec/logics/
     */
    public static final String[] ALL_LOGIC_NAMES = {
        "Classical-Propositional-Logic",
        "Monomodal-K",
        "S4-Explicit-R",
        "S4-with-history",
        "S4Optimal",
        "S4Minimal",
        "S5-explicit-edges",
        "S5-implicit-edges",
        "KD",
        "KD45",
        "KD45Optimal",
        "KT-explicit-edges",
        "KT-implicit-edges",
        "KB-explicit-edges",
        "KB-implicit-edges",
        "K4-explicit-edges",
        "K4-implicit-edges",
        "K4Confluence",
        "K45Optimal",
        "K5",
        "KB5",
        "KBT",
        "KBD",
        "Kalt1",
        "KMinimal",
        "K+Universal",
        "K2-with-Inclusion",
        "Multimodal-Kn",
        "KConfluence",
        "Intuitionistic-Logic-Lj",
        "Hybrid-Logic-H-at",
        "LTL",
        "PDL",
        "Model-Checking-Monomodal",
        "Model-Checking-Multimodal",
        "Multi-S5-PAL",
        "xstit",
        "LJminimal"
    };
}
