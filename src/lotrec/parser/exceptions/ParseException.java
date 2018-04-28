/*
 * LogicParserException.java
 *
 * Created on 21 mars 2007, 16:44
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lotrec.parser.exceptions;

import lotrec.parser.LogicXMLParser;

/**
 *
 * @author said
 */
public class ParseException extends java.lang.Exception {
    
    public static String EXCEPTION_CAUSE = "\nThis exception was caused by this value: ";
    public static String EXCEPTION_HEADER = "Logic parsing error: ";
    public static String NO_LOGIC = EXCEPTION_HEADER + "XML file doesn't contain a logic definition.\n" +
            "No '"+LogicXMLParser.LOGIC_TAG+"' tag element in this file.";
//    public static String NO_LOGIC_NAME_TAG = EXCEPTION_HEADER + "XML file lacks a logic name.\n" +
//            "No '"+LogicXMLParser.LOGIC_NAME_TAG+"' tag element in this file.";
    public static String NO_LOGIC_NAME = EXCEPTION_HEADER + "Logic name should be specified...";
    public static String NO_CONNECTORS = EXCEPTION_HEADER + "No connector defined!!";
    public static String DUPLICATED_CONNECTORS = EXCEPTION_HEADER + "The following connector name is used to identify more than one connector: ";
    public static String NO_CONNECTOR_NAME = EXCEPTION_HEADER + "Connector name field is empty."+
            "\nThe name is mandatory and it is the connector's reference to be used in your logic definition.";
    public static String NO_CONNECTOR_ARITY = EXCEPTION_HEADER + "The connector arity is not defined or not a valid integer.";
    public static String NO_CONNECTOR_ASSOCIATIVITY = EXCEPTION_HEADER + "The connector associativity should be either \"true\" or \"false\".";
    public static String NO_CONNECTOR_OUTPUT = EXCEPTION_HEADER + "The connector output format is not given.";
    public static String NO_CONNECTOR_PRIORITY = EXCEPTION_HEADER +  "The connector arity is not defined or not a valid integer.";
    public static String UNKOWN_EXPRESSION_CONNECTOR = EXCEPTION_HEADER +  "Incorrect connector, or variable, or constant.\n" +
            "A small letter in a formula should designate one of the following:\n" +
            "- \"variable\" or \"_\" to designate a variable expression,\n" +
            "- \"nodeVariable\", or \"n_\" to designate a variable node expression,\n" +
            "- \"constant\" followed by a constant expression,\n" +
            "- or a connector name.\n" +
            "Otherwise, it should be a word starting with capital letter and designating a constant.";
    public static String NO_RULES = EXCEPTION_HEADER + "No rule defined!!";
    public static String DUPLICATED_RULES = EXCEPTION_HEADER + "The following rule name is used to identify more than one rule: ";
    public static String NO_RULE_NAME = EXCEPTION_HEADER + "Rule name field is empty." +
            "\nThe name is mandatory and it is the rule's reference to be used in your logic definition.";
    public static String NO_RULE_COMMUTATIVE = EXCEPTION_HEADER + "The rule commutativity should be either \"true\" or \"false\".";
    public static String NO_RULE_CONDITIONS = EXCEPTION_HEADER + "No conditions defined for the rule ";
    public static String NO_RULE_ACTIONS = EXCEPTION_HEADER + "No actions defined for the rule ";
    public static String BAD_DUPLICATION_ACTION = EXCEPTION_HEADER + "A duplication action is not well defined." + 
            "\nA well defined one must be of the form \"begin <source> <destination> <source> <destination> ... <source> <destination> end\"";
    public static String NO_STRATEGIES = EXCEPTION_HEADER + "There is no strategy defined in this logic!!";
    public static String NO_MAIN_STRATEGY = EXCEPTION_HEADER + "There is no main strategy in the parsed logic file!";
    public static String INCORRECT_MAIN_STRATEGY = EXCEPTION_HEADER + "The given main strategy name is incorrect!";
    public static String BAD_STRATEGY_DEF = EXCEPTION_HEADER + "Unkown sub-strategy identifier.\n" +
            "A strategy code is a set of blocks,\n" +
            "A block could be only one of the following:\n" +
            " - rule name,\n" +
            " - other strategy name\n" +
            " - routine block starting with one of the keywords:\n" +
            "   'allRules', 'firstRule' or 'repeat' and ending with 'end'.\n" +
            "   Note that a routine block is a strategy,\n" +
            "   so it may also contain a set of blocks.";
    public static String EMPTY_STRATEGY_DEF = EXCEPTION_HEADER + "The strategy definition is empty! We cannot recognized a well defined strategy code.";
    public static String DUPLICATED_STRATEGIES = EXCEPTION_HEADER + "The following strategy name is used to identify more than one: ";    
    public static String EMPTY_TESTING_FORMULA = EXCEPTION_HEADER + "An empty code is given in a testing formula definition.";
    public static String BAD_MAX_STEPS_FORMAT = EXCEPTION_HEADER + "The logic strategies max steps number is not defined or not a valid integer.";
    
    //List of exception messages for the oldies Tokenizer
    public static String TOO_FEW_TOKENS = EXCEPTION_HEADER + "Too few parameters or some text is missing.";   
    public static String TOO_MORE_TOKENS = EXCEPTION_HEADER + "Too more parameters or some extra text is added at the end.";    
    public static String TOKENIZER_EXCEPTION = EXCEPTION_HEADER + "The String Tokenizer used in LoTREC had the following exception while processing.";
    public static String UNKOWN_CODITION = EXCEPTION_HEADER + "Unkown condition name. Please consult our complete list of conditions and actions:\n" + 
    "http://www.irit.fr/recherches/LILAC/Lotrec/Lotrec/conditions.pdf";
    public static String UNKOWN_ACTION = EXCEPTION_HEADER + "Unkown action name. Please consult our complete list of conditions and actions:\n" + 
    "http://www.irit.fr/recherches/LILAC/Lotrec/Lotrec/conditions.pdf";
    public static String UNKOWN_ARGUMENT_TYPE = EXCEPTION_HEADER + "Unkown condition/action argument type";
    public static String CONDITION_PARAMETER_PARSING = EXCEPTION_HEADER + "Problem while parsing a condition parameter";
    public static String CONDITION_PARSING = EXCEPTION_HEADER + "Problem while parsing a condition";
    public static String ACTION_PARSING = EXCEPTION_HEADER + "Problem while parsing an action";
    public static String CONDITION_PARAMETERS_COUNT = EXCEPTION_HEADER + "Condition parameters count is not correct given its keyword";
    public static String ACTION_PARAMETERS_COUNT = EXCEPTION_HEADER + "Action parameters count is not correct given its keyword";
    public static String ACTION_PARAMETER_PARSING = EXCEPTION_HEADER + "Problem while parsing an action parameter";
    
    /**
     * Creates a new instance of <code>LogicParserException</code> without detail message.
     */
    public ParseException() {
    }
    
    
    /**
     * Constructs an instance of <code>LogicParserException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ParseException(String msg) {
        super(msg);
    }
}
