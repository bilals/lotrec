/*
 * LogicXMLParser.java
 *
 * Created on 7 mars 2007, 15:33
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec.parser;

import lotrec.parser.exceptions.ParseException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;
import lotrec.FileUtils;
import lotrec.Lotrec;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.ParametersTypes;
import lotrec.dataStructure.TestingFormula;
import lotrec.dataStructure.expression.Connector;
import lotrec.dataStructure.expression.Expression;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.dataStructure.expression.StringSchemeVariable;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.ParameterType;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.process.AbstractAction;
import lotrec.process.Strategy;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.parsers.*;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author said
 */
public class LogicXMLParser {

    public static String PREDEFINED_XML_VERSION = "V2-";
    public static String LOGIC_TAG = "logic";
    private static String PARSER_VERSION_TAG = "parser-version";
//    public static String LOGIC_NAME_TAG = "logic-name";
    private static String LOGIC_DESCRIPTION_TAG = "description";
//    private static String LOGIC_AUTHOR = "author";
//    private static String LOGIC_LAST_UPDATE = "last-update";
    private static String LOGIC_MAIN_STRATEGY_TAG = "main-strategy";
//    private static String LOGIC_DEFAULT_STRATEGY_TAG = "default-strategy";
    private static String CONNECTOR_TAG = "connector";
    private static String CONN_NAME_TAG = "connector-name";
    private static String CONN_ARITY_TAG = "arity";
    private static String CONN_ASSOCIATIVE_TAG = "associative";
    private static String CONN_OUTPUT_FORMAT_TAG = "output-format";
    private static String CONN_PRIORITY_TAG = "priority";
    private static String CONN_COMMENT_TAG = "connector-comment";
    private static String RULE_TAG = "rule";
    private static String RULE_NAME_TAG = "rule-name";
//    private static String RULE_COMMUTATIVE_TAG = "commutative";
    private static String CONDITION_TAG = "condition";
    private static String CONDITION_NAME_TAG = "condition-name";
    private static String ACTION_TAG = "action";
    private static String ACTION_NAME_TAG = "action-name";
//    private static String ARGUMENT_TAG = "argument";    
    private static String PARAMETER_TAG = "parameter";
//    private static String ARGUMENT_TYPE_TAG = "type";
    private static String RULE_COMMENT_TAG = "rule-comment";
    private static String STRATEGY_TAG = "strategy";
    private static String STRATEGY_NAME_TAG = "strategy-name";
//    private static String STRATEGY_USABILITY_TAG = "usability";
    private static String STRATEGY_CODE_TAG = "strategy-code";
    private static String STRATEGY_COMMENT_TAG = "strategy-comment";
    private static String TESTING_FORMULA_TAG = "testing-formula";
    private static String TF_NAME_TAG = "formula-name";
    private static String TF_CODE_TAG = "formula-code";
    private static String TF_COMMENT_TAG = "formula-comment";
    private static String CURRENT_PARSER_VERSION = "2.1";
    private OldiesTokenizer oldiesTokenizer;
    private Document doc;

    /**
     * Creates a new instance of LogicXMLParser
     */
    public LogicXMLParser() {
    }

    public void saveLogic(Logic logic, String xmlFileName) throws ParseException {
        Element e = null;
        Node n = null;
        // Document (Xerces implementation only).
        Document xmldoc = new DocumentImpl();
        // Root element.
        Element root = xmldoc.createElement(LogicXMLParser.LOGIC_TAG);
        e = xmldoc.createElement(LogicXMLParser.PARSER_VERSION_TAG);
        n = xmldoc.createTextNode("2.1");
        e.appendChild(n);
        root.appendChild(e);

//        e = xmldoc.createElement(LogicXMLParser.LOGIC_NAME_TAG);
//        n = xmldoc.createTextNode(logic.getName());
//        e.appendChild(n);
//        root.appendChild(e);

        e = xmldoc.createElement(LogicXMLParser.LOGIC_DESCRIPTION_TAG);
        n = xmldoc.createTextNode(logic.getDescription());
//        n = xmldoc.createTextNode(logic.getDescription()+"\n"
//                +"Author: "+logic.getAuthor()+"\n"+
//                "Last update: "+logic.getLastUpdate());
        e.appendChild(n);
        root.appendChild(e);

//        e = xmldoc.createElement(LogicXMLParser.LOGIC_AUTHOR);
//        n = xmldoc.createTextNode(logic.getAuthor());
//        e.appendChild(n);
//        root.appendChild(e);
//
//        e = xmldoc.createElement(LogicXMLParser.LOGIC_LAST_UPDATE);
//        n = xmldoc.createTextNode(logic.getLastUpdate());
//        e.appendChild(n);
//        root.appendChild(e);

        Element ce = null;
        for (Connector con : logic.getConnectors()) {
            ce = xmldoc.createElement(LogicXMLParser.CONNECTOR_TAG);

            e = xmldoc.createElement(LogicXMLParser.CONN_NAME_TAG);
            n = xmldoc.createTextNode(con.getName());
            e.appendChild(n);
            ce.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.CONN_ARITY_TAG);
            n = xmldoc.createTextNode(String.valueOf(con.getArity()));
            e.appendChild(n);
            ce.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.CONN_ASSOCIATIVE_TAG);
            n = xmldoc.createTextNode(String.valueOf(con.isAssociative()));
            e.appendChild(n);
            ce.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.CONN_OUTPUT_FORMAT_TAG);
            n = xmldoc.createTextNode(con.getOutString());
            e.appendChild(n);
            ce.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.CONN_PRIORITY_TAG);
            n = xmldoc.createTextNode(String.valueOf(con.getPriority()));
            e.appendChild(n);
            ce.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.CONN_COMMENT_TAG);
            n = xmldoc.createTextNode(con.getComment());
            e.appendChild(n);
            ce.appendChild(e);

            root.appendChild(ce);
        }

        Element re = null;
        for (Rule rule : logic.getRules()) {
            re = xmldoc.createElement(LogicXMLParser.RULE_TAG);

            e = xmldoc.createElement(LogicXMLParser.RULE_NAME_TAG);
            n = xmldoc.createTextNode(rule.getName());
            e.appendChild(n);
            re.appendChild(e);

//            e = xmldoc.createElement(LogicXMLParser.RULE_COMMUTATIVE_TAG);
//            n = xmldoc.createTextNode(String.valueOf(rule.isCommutative()));
//            e.appendChild(n);
//            re.appendChild(e);

            Element rce = null;
            for (AbstractCondition cond : rule.getConditions()) {
                rce = xmldoc.createElement(LogicXMLParser.CONDITION_TAG);

                e = xmldoc.createElement(LogicXMLParser.CONDITION_NAME_TAG);
                n = xmldoc.createTextNode(cond.getName());
                e.appendChild(n);
                rce.appendChild(e);

                Element conarge = null;
                for (Parameter arg : cond.getParameters()) {
                    conarge = xmldoc.createElement(LogicXMLParser.PARAMETER_TAG);
//                    conarge.setAttribute(LogicXMLParser.ARGUMENT_TYPE_TAG, arg.getType().toString());
                    n = xmldoc.createTextNode(arg.getValueCode());
                    conarge.appendChild(n);
                    rce.appendChild(conarge);
                }

                re.appendChild(rce);
            }

            Element rae = null;
            for (AbstractAction act : rule.getActions()) {
                rae = xmldoc.createElement(LogicXMLParser.ACTION_TAG);

                e = xmldoc.createElement(LogicXMLParser.ACTION_NAME_TAG);
                n = xmldoc.createTextNode(act.getName());
                e.appendChild(n);
                rae.appendChild(e);

                Element acarge = null;
                for (Parameter arg : act.getParameters()) {
                    acarge = xmldoc.createElement(LogicXMLParser.PARAMETER_TAG);
//                    acarge.setAttribute(LogicXMLParser.ARGUMENT_TYPE_TAG, arg.getType().toString());
                    n = xmldoc.createTextNode(arg.getValueCode());
                    acarge.appendChild(n);
                    rae.appendChild(acarge);
                }

                re.appendChild(rae);
            }

            e = xmldoc.createElement(LogicXMLParser.RULE_COMMENT_TAG);
            n = xmldoc.createTextNode(rule.getComment());
            e.appendChild(n);
            re.appendChild(e);

            root.appendChild(re);
        }

        Element se = null;
        for (Strategy str : logic.getStrategies()) {
            se = xmldoc.createElement(LogicXMLParser.STRATEGY_TAG);

            e = xmldoc.createElement(LogicXMLParser.STRATEGY_NAME_TAG);
            n = xmldoc.createTextNode(str.getWorkerName());
            e.appendChild(n);
            se.appendChild(e);

//            e = xmldoc.createElement(LogicXMLParser.STRATEGY_USABILITY_TAG);
//            n = xmldoc.createTextNode(str.getUsability());
//            e.appendChild(n);
//            se.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.STRATEGY_CODE_TAG);
            n = xmldoc.createTextNode(str.getCode());
            e.appendChild(n);
            se.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.STRATEGY_COMMENT_TAG);
            n = xmldoc.createTextNode(str.getComment());
            e.appendChild(n);
            se.appendChild(e);

            root.appendChild(se);
        }

        e = xmldoc.createElement(LogicXMLParser.LOGIC_MAIN_STRATEGY_TAG);
        n = xmldoc.createTextNode(logic.getMainStrategyName());
        e.appendChild(n);
        root.appendChild(e);

        Element fe = null;
        for (TestingFormula frm : logic.getTestingFormulae()) {
            fe = xmldoc.createElement(LogicXMLParser.TESTING_FORMULA_TAG);

            e = xmldoc.createElement(LogicXMLParser.TF_NAME_TAG);
            n = xmldoc.createTextNode(frm.getName());
            e.appendChild(n);
            fe.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.TF_CODE_TAG);
            n = xmldoc.createTextNode(frm.getCode());
            e.appendChild(n);
            fe.appendChild(e);

            e = xmldoc.createElement(LogicXMLParser.TF_COMMENT_TAG);
            n = xmldoc.createTextNode(frm.getComment());
            e.appendChild(n);
            fe.appendChild(e);

            root.appendChild(fe);
        }

        xmldoc.appendChild(root);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(xmlFileName);
            // XERCES 1 or 2 additionnal classes.
            OutputFormat of = new OutputFormat("XML", "UTF-8", true);
            of.setIndent(1);
            of.setIndenting(true);
//            of.setDoctype(null, "logic.dtd");
            XMLSerializer serializer = new XMLSerializer(fos, of);
            // As a DOM Serializer
            serializer.asDOMSerializer();
            serializer.serialize(xmldoc.getDocumentElement());
        } catch (FileNotFoundException ex) {
            System.out.println("XML Logic Parser Exception while saving logic " +
                    logic.getName() + " in file " + xmlFileName);
            System.out.println("This exception due to FileNotFoundException. In the following some details:");
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println("XML Logic Parser Exception while saving logic " +
                    logic.getName() + " in file " + xmlFileName);
            System.out.println("This exception due to IOException. In the following some details:");
            System.out.println(ex.getMessage());
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                System.out.println("XML Logic Parser Exception while saving logic " +
                        logic.getName() + " in file " + xmlFileName);
                System.out.println("This exception due to IOException while closing the file. In the following some details:");
                System.out.println(ex.getMessage());
            }
        }
    }

//    public Logic parseJarLogicFile(String jarResourceName) throws ParseException {
//        InputStream is = null;
//        is = getClass().getResourceAsStream(jarResourceName);
//
//        DOMParser parser = new DOMParser();
//        try {
//            parser.parse(new InputSource(is));
//            doc = parser.getDocument();
//        } catch (SAXException ex) {
//            Lotrec.println("XML Logic parsing stoped because of the following SAX Exception:");
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            Lotrec.println("XML Logic parsing stoped because of the following IO Exception:");
//            ex.printStackTrace();
//        }
//        Logic parsedLogic = new Logic();
//        oldiesTokenizer = new OldiesTokenizer(parsedLogic);
//        oldiesTokenizer.initializeTokenizerAndProps();
//        Lotrec.println("XML Logic parsing is processing...");
//        if (getTagTextContent(doc, LogicXMLParser.LOGIC_NAME_TAG).equals("")) {
//            throw new ParseException(ParseException.NO_LOGIC_NAME);
//        } else {
//            parsedLogic.setName(getTagTextContent(doc, LogicXMLParser.LOGIC_NAME_TAG));
//        }
//        parsedLogic.setDescription(getTagTextContent(doc, LogicXMLParser.LOGIC_DESCRIPTION_TAG));
//        parsedLogic.setAuthor(getTagTextContent(doc, LogicXMLParser.LOGIC_AUTHOR));
//        parsedLogic.setLastUpdate(getTagTextContent(doc, LogicXMLParser.LOGIC_LAST_UPDATE));
//        this.parseConnectors(parsedLogic);
//        this.parseRules(parsedLogic);
//        this.parseStrategies(parsedLogic);
//        if (getTagTextContent(doc, LogicXMLParser.LOGIC_DEFAUT_STRATEGY_TAG) == null) {
//            throw new ParseException(ParseException.NO_MAIN_STRATEGY);
//        } else {
//            if (parsedLogic.getStrategy(getTagTextContent(doc, LogicXMLParser.LOGIC_DEFAUT_STRATEGY_TAG)) != null) {
//                parsedLogic.setMainStrategyName(getTagTextContent(doc, LogicXMLParser.LOGIC_DEFAUT_STRATEGY_TAG));
//            } else {
//                throw new ParseException(ParseException.INCORRECT_MAIN_STRATEGY);
//            }
//        }
//        this.parseTestingFormulae(parsedLogic);
//
//        //Lotrec.println("------------The resulting parsed Logic-------------");
//        //Lotrec.println(parsedLogic);
//        //Lotrec.println("-------End of resulting parsed Logic Display-------");
//
//        Lotrec.println("XML Logic parsing completed successfully...");
//        return parsedLogic;
//    }
    public static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
//            return "Exception message:\n" + e.getMessage() + "\nException stack trace:\n" + sw.toString();
            return sw.toString();
        } catch (Exception e2) {
            return "Cannot get the stack trace!\n" +
                    "The exception message is:\n" +
                    e.getMessage();
        }
    }

    public Logic parseLogic(String xmlFileName) throws ParseException {
        String fileName = (String) xmlFileName;
        DOMParser parser = new DOMParser();
        try {
            parser.parse(fileName);
            doc = parser.getDocument();
        } catch (SAXException ex) {
            System.err.print(LogicXMLParser.stack2string(ex));
            throw new ParseException("XML Logic parsing stoped because of the following SAX Exception:\n" +
                    "The exception message is:\n" +
                    ex.getMessage() + "\n" +
                    "For more details read the command prompt (i.e. shell) output...");
        } catch (java.io.FileNotFoundException ex) {
            System.err.print(LogicXMLParser.stack2string(ex));
            throw new ParseException("XML Logic parsing stoped because of FileNotFoundException:\n" +
                    "The exception message is:\n" +
                    ex.getMessage() + "\n" +
                    "For more details read the command prompt (i.e. shell) output...");
        } catch (IOException ex) {
            if (ex instanceof java.net.MalformedURLException) {
                System.err.print(LogicXMLParser.stack2string(ex));
                throw new ParseException("XML Logic parsing stoped because of MalformedURL Exception.\n" +
                        "This exception occurs when the file name contains some special characters, such as ;,:,(,[,...\n" +
                        "The exception message is:\n" +
                        ex.getMessage() + "\n" +
                        "For more details read the command prompt (i.e. shell) output...");
            } else {
                System.err.print(LogicXMLParser.stack2string(ex));
                throw new ParseException("XML Logic parsing stoped because of the following IO Exception:\n" +
                        "The exception message is:\n" +
                        ex.getMessage() + "\n" +
                        "For more details read the command prompt (i.e. shell) output...");
            }
        }
        Logic parsedLogic = new Logic();
        oldiesTokenizer = new OldiesTokenizer(parsedLogic);
        oldiesTokenizer.initializeTokenizerAndProps();
        Lotrec.println("XML Logic parsing is processing...");
        if (doc.getElementsByTagName(LogicXMLParser.LOGIC_TAG).getLength() == 0) {
            throw new ParseException(ParseException.NO_LOGIC);
        }

        String fileParserVersion = getTagTextContent(doc, LogicXMLParser.PARSER_VERSION_TAG);
        if (fileParserVersion != null && !fileParserVersion.equals(LogicXMLParser.CURRENT_PARSER_VERSION)) {
            System.out.println("File parser-version is: " + fileParserVersion +
                    ", while current parser version is: " + LogicXMLParser.CURRENT_PARSER_VERSION);
            System.out.println("The file may not be parsed correctly!");
        }

//        if (doc.getElementsByTagName(LogicXMLParser.LOGIC_NAME_TAG).getLength() == 0) {
//            throw new ParseException(ParseException.NO_LOGIC_NAME_TAG);
//        }
//        if (getTagTextContent(doc, LogicXMLParser.LOGIC_NAME_TAG).equals("")) {
//            throw new ParseException(ParseException.NO_LOGIC_NAME);
//        } else {
//            parsedLogic.setName(getTagTextContent(doc, LogicXMLParser.LOGIC_NAME_TAG));
//        }

//        System.out.println("Logic file name is: "+FileUtils.getFileNameWithoutExtension(fileName));
        parsedLogic.setName(FileUtils.getFileNameWithoutExtension(fileName));

        parsedLogic.setDescription(getTagTextContent(doc, LogicXMLParser.LOGIC_DESCRIPTION_TAG));
//        parsedLogic.setAuthor(getTagTextContent(doc, LogicXMLParser.LOGIC_AUTHOR));
//        parsedLogic.setLastUpdate(getTagTextContent(doc, LogicXMLParser.LOGIC_LAST_UPDATE));
        this.parseConnectors(parsedLogic);
        this.parseRules(parsedLogic);
        this.parseStrategies(parsedLogic);
//        String strMainStrategy = getTagTextContent(doc, LogicXMLParser.LOGIC_DEFAULT_STRATEGY_TAG);
        String strMainStrategy = getTagTextContent(doc, LogicXMLParser.LOGIC_MAIN_STRATEGY_TAG);
        if (strMainStrategy == null) {
            throw new ParseException(ParseException.NO_MAIN_STRATEGY);
        } else {
            if (parsedLogic.getStrategy(strMainStrategy) != null) {
                parsedLogic.setMainStrategyName(strMainStrategy);
            } else {
                throw new ParseException(ParseException.INCORRECT_MAIN_STRATEGY);
            }
        }
        this.parseTestingFormulae(parsedLogic);

        //Lotrec.println("------------The resulting parsed Logic-------------");
        //Lotrec.println(parsedLogic);
        //Lotrec.println("-------End of resulting parsed Logic Display-------");

        Lotrec.println("XML Logic parsing completed successfully...");
        return parsedLogic;
    }

    private void parseConnectors(Logic parsedLogic) throws ParseException {
        int connectorsCount = doc.getElementsByTagName(LogicXMLParser.CONNECTOR_TAG).getLength();
        if (connectorsCount == 0) {
//            throw new ParseException(ParseException.NO_CONNECTORS);
        } else {
            for (int i = 0; i < connectorsCount; i++) {
                Element connElement = (Element) doc.getElementsByTagName(CONNECTOR_TAG).item(i);
                Connector conn = new Connector();
                if (getTagTextContent(connElement, LogicXMLParser.CONN_NAME_TAG).equals("")) {
                    throw new ParseException(ParseException.NO_CONNECTOR_NAME +
                            ParseException.EXCEPTION_CAUSE + getTagTextContent(connElement, LogicXMLParser.CONN_NAME_TAG));
                } else if (parsedLogic.getConnector(getTagTextContent(connElement, LogicXMLParser.CONN_NAME_TAG)) != null) {
                    throw new ParseException(ParseException.DUPLICATED_CONNECTORS +
                            getTagTextContent(connElement, LogicXMLParser.CONN_NAME_TAG));
                } else {
                    conn.setName(getTagTextContent(connElement, LogicXMLParser.CONN_NAME_TAG));
                }
                try {
                    int arity = Integer.parseInt(getTagTextContent(connElement, LogicXMLParser.CONN_ARITY_TAG));
                    conn.setArity(arity);
                } catch (Exception e) {
                    if (e instanceof NumberFormatException) {
                        throw new ParseException(ParseException.NO_CONNECTOR_ARITY +
                                ParseException.EXCEPTION_CAUSE + getTagTextContent(connElement, LogicXMLParser.CONN_ARITY_TAG));
                    }
                }
                if (getTagTextContent(connElement, LogicXMLParser.CONN_ASSOCIATIVE_TAG).equals("true")) {
                    conn.setAssociative(true);
                } else if (getTagTextContent(connElement, LogicXMLParser.CONN_ASSOCIATIVE_TAG).equals("false")) {
                    conn.setAssociative(false);
                } else {
                    throw new ParseException(ParseException.NO_CONNECTOR_ASSOCIATIVITY +
                            ParseException.EXCEPTION_CAUSE + getTagTextContent(connElement, LogicXMLParser.CONN_ASSOCIATIVE_TAG));
                }
                if (getTagTextContent(connElement, LogicXMLParser.CONN_OUTPUT_FORMAT_TAG).equals("")) {
                    throw new ParseException(ParseException.NO_CONNECTOR_OUTPUT +
                            ParseException.EXCEPTION_CAUSE + getTagTextContent(connElement, LogicXMLParser.CONN_OUTPUT_FORMAT_TAG));
                } else {
                    conn.setOutString(getTagTextContent(connElement, LogicXMLParser.CONN_OUTPUT_FORMAT_TAG));
                }
                try {
                    int priority = Integer.parseInt(getTagTextContent(connElement, LogicXMLParser.CONN_PRIORITY_TAG));
                    conn.setPriority(priority);
                } catch (Exception e) {
                    if (e instanceof NumberFormatException) {
                        throw new ParseException(ParseException.NO_CONNECTOR_PRIORITY +
                                ParseException.EXCEPTION_CAUSE + getTagTextContent(connElement, LogicXMLParser.CONN_PRIORITY_TAG));
                    }
                }
                conn.setComment(getTagTextContent(connElement, LogicXMLParser.CONN_COMMENT_TAG));
                parsedLogic.addConnector(conn);
            }
        }
    }

    private void parseRules(Logic parsedLogic) throws ParseException {
        int rulesCount = doc.getElementsByTagName(RULE_TAG).getLength();
        if (rulesCount == 0) {
//            throw new ParseException(ParseException.NO_RULES);
        } else {
            for (int i = 0; i < rulesCount; i++) {
                Element ruleElement = (Element) doc.getElementsByTagName(RULE_TAG).item(i);
                Rule rule = new Rule();
                if (this.getTagTextContent(ruleElement, LogicXMLParser.RULE_NAME_TAG).equals("")) {
                    throw new ParseException(ParseException.NO_RULE_NAME +
                            ParseException.EXCEPTION_CAUSE + getTagTextContent(ruleElement, LogicXMLParser.RULE_NAME_TAG));
                } else if (parsedLogic.getRule(getTagTextContent(ruleElement, LogicXMLParser.RULE_NAME_TAG)) != null) {
                    throw new ParseException(ParseException.DUPLICATED_RULES +
                            getTagTextContent(ruleElement, LogicXMLParser.RULE_NAME_TAG));
                } else {
                    rule.setName(getTagTextContent(ruleElement, LogicXMLParser.RULE_NAME_TAG));
                }
//                if (this.getTagTextContent(ruleElement, LogicXMLParser.RULE_COMMUTATIVE_TAG).equals("true")) {
//                    rule.setCommutative(true);
//                } else if (this.getTagTextContent(ruleElement, LogicXMLParser.RULE_COMMUTATIVE_TAG).equals("false")) {
//                    rule.setCommutative(false);
//                } else {
//                    throw new ParseException(ParseException.NO_RULE_COMMUTATIVE +
//                            ParseException.EXCEPTION_CAUSE + getTagTextContent(ruleElement, LogicXMLParser.RULE_COMMUTATIVE_TAG));
//                }
                parseConditions(rule, ruleElement);
                parseActions(rule, ruleElement);
                rule.setComment(getTagTextContent(ruleElement, LogicXMLParser.RULE_COMMENT_TAG));
                parsedLogic.addRule(rule);
            }
        }
    }

    public void parseConditions(Rule rule, Element ruleElement) throws ParseException {
        int conditionsCount = ruleElement.getElementsByTagName(LogicXMLParser.CONDITION_TAG).getLength();
        if (conditionsCount == 0) {
//            throw new ParseException(ParseException.NO_RULE_CONDITIONS + rule.getName() + ".");
        } else {
            for (int i = 0; i < conditionsCount; i++) {
                Element condElement = (Element) ruleElement.getElementsByTagName(LogicXMLParser.CONDITION_TAG).item(i);
                AbstractCondition cond = null;
                StringSchemeVariable nodeArg = null;
                StringSchemeVariable node2Arg = null;
                Expression formulaArg = null;
                Expression formulaArg2 = null;
                Expression relationArg = null;
                String markArg = null;

                String keyword = getTagTextContent(condElement, LogicXMLParser.CONDITION_NAME_TAG);
                String conditionClassName = AbstractCondition.CLASSES_KEYWORDS.get(keyword);
                if (conditionClassName == null) {
                    throw new ParseException(ParseException.UNKOWN_CODITION +
                            ParseException.EXCEPTION_CAUSE + keyword);
                }
                int paramsCount = condElement.getElementsByTagName(LogicXMLParser.PARAMETER_TAG).getLength();
//                int paramsCount = condElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).getLength();
                ArrayList<String> paramsStrVals = new ArrayList<String>();
                for (int j = 0; j < paramsCount; j++) {
                    Element paramElement = (Element) condElement.getElementsByTagName(LogicXMLParser.PARAMETER_TAG).item(j);
//                    Element paramElement = (Element) condElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).item(j);
                    String paramStrVal = paramElement.getTextContent();
                    paramsStrVals.add(paramStrVal);
                }
                try {
                    Class condClass = Class.forName(
                            AbstractCondition.CONDITIONS_PACKAGE + conditionClassName);
                    Constructor constructor = condClass.getConstructors()[0];
//                Class[] paramClasses = constructor.getParameterTypes();
                    ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
                    Vector<Parameter> params = new Vector<Parameter>();
                    if (paramsTypes.types().length != paramsStrVals.size()) {
                        throw new ParseException(ParseException.CONDITION_PARAMETERS_COUNT +
                                ParseException.EXCEPTION_CAUSE + "Condition '" + keyword + "' must take " +
                                paramsTypes.types().length +
                                " parameter(s) while the found prameter(s) count is " +
                                paramsStrVals.size());
                    }
                    boolean firstNode = true;
                    boolean firstFormula = true;
                    for (int j = 0; j < paramsTypes.types().length; j++) {
                        String paramTypeStr = paramsTypes.types()[j];
                        ParameterType paramType = ParameterType.getParameterType(paramTypeStr);
                        String paramStrVal = paramsStrVals.get(j);
                        switch (paramType) {
                            case NODE:
                                if (firstNode) {
                                    nodeArg = new StringSchemeVariable(paramStrVal);
                                    params.add(new Parameter(paramType, nodeArg));
                                    firstNode = false;
                                } else {
                                    node2Arg = new StringSchemeVariable(paramStrVal);
                                    params.add(new Parameter(paramType, node2Arg));
                                }
                                break;
                            case FORMULA:
                                if (firstFormula) {
                                    try {
                                        formulaArg = oldiesTokenizer.parseExpression(paramStrVal);
                                        oldiesTokenizer.verifyCodeEnd();
                                    } catch (ParseException ex) {
                                        throw new ParseException(ParseException.CONDITION_PARAMETER_PARSING +
                                                ParseException.EXCEPTION_CAUSE + paramStrVal +
                                                "Fomula's expression parsing exception:\n" + ex.getMessage());
                                    }
                                    params.add(new Parameter(paramType, formulaArg));
                                    firstFormula = false;
                                } else {
                                    try {
                                        formulaArg2 = oldiesTokenizer.parseExpression(paramStrVal);
                                        oldiesTokenizer.verifyCodeEnd();
                                    } catch (ParseException ex) {
                                        throw new ParseException(ParseException.CONDITION_PARAMETER_PARSING +
                                                ParseException.EXCEPTION_CAUSE + paramStrVal +
                                                "Fomula's expression parsing exception:\n" + ex.getMessage());
                                    }
                                    params.add(new Parameter(paramType, formulaArg2));
                                }

                                break;
                            case RELATION:
                                try {
                                    relationArg = oldiesTokenizer.parseExpression(paramStrVal);
                                    oldiesTokenizer.verifyCodeEnd();
                                } catch (ParseException ex) {
                                    throw new ParseException(ParseException.CONDITION_PARAMETER_PARSING +
                                            ParseException.EXCEPTION_CAUSE + paramStrVal +
                                            "Relation's expression parsing exception:\n" + ex.getMessage());
                                }
                                params.add(new Parameter(paramType, relationArg));
                                break;
                            case MARK:
                                markArg = paramStrVal;
                                params.add(new Parameter(paramType, markArg));
                                break;
                        }
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
                    }
                    int paramsNum = params.size();
//                    cond = (AbstractCondition) constructor.newInstance(params.toArray());
                    switch (paramsNum) {
                        case 1:
                            cond = (AbstractCondition) constructor.newInstance(
                                    params.get(0).getValue());
                            break;
                        case 2:
                            cond = (AbstractCondition) constructor.newInstance(
                                    params.get(0).getValue(),
                                    params.get(1).getValue());
                            break;
                        case 3:
                            cond = (AbstractCondition) constructor.newInstance(
                                    params.get(0).getValue(),
                                    params.get(1).getValue(),
                                    params.get(2).getValue());
                            break;
                        case 4:
                            cond = (AbstractCondition) constructor.newInstance(
                                    params.get(0).getValue(),
                                    params.get(1).getValue(),
                                    params.get(2).getValue(),
                                    params.get(3).getValue());
                            break;
                    }
                    for (Parameter p : params) {
                        cond.addParameter(p);
                    }
                    cond.setName(keyword);
                    cond.setItsRule(rule);
                    rule.addCondition(cond);
                } catch (InstantiationException ex) {
                    throw new ParseException(ParseException.CONDITION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (IllegalAccessException ex) {
                    throw new ParseException(ParseException.CONDITION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    throw new ParseException(ParseException.CONDITION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (InvocationTargetException ex) {
                    throw new ParseException(ParseException.CONDITION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (ClassNotFoundException ex) {
                    throw new ParseException(ParseException.CONDITION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                }
            }
        }
    }

    /*
     * Version using the <argument type="node"> ... </argument>
     * 
     * 
    public void parseConditions(Rule rule, Element ruleElement) throws ParseException {
    int conditionsCount = ruleElement.getElementsByTagName(LogicXMLParser.CONDITION_TAG).getLength();
    if (conditionsCount == 0) {
    //            throw new ParseException(ParseException.NO_RULE_CONDITIONS + rule.getName() + ".");
    } else {
    for (int i = 0; i < conditionsCount; i++) {
    Element condElement = (Element) ruleElement.getElementsByTagName(LogicXMLParser.CONDITION_TAG).item(i);
    AbstractCondition cond;
    String condName;
    StringSchemeVariable nodeArg = null;
    StringSchemeVariable node2Arg = null;
    Expression formulaArg = null;
    Expression relationArg = null;
    String markArg = null;
    if (this.getTagTextContent(condElement, LogicXMLParser.CONDITION_NAME_TAG).equals("")) {
    throw new ParseException(ParseException.UNKOWN_CODITION +
    ParseException.EXCEPTION_CAUSE + getTagTextContent(condElement, LogicXMLParser.CONDITION_NAME_TAG));
    } else {
    condName = getTagTextContent(condElement, LogicXMLParser.CONDITION_NAME_TAG);
    int argsCount = condElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).getLength();
    boolean firstNode = true;
    ArrayList<String> args = new ArrayList<String>();
    for (int j = 0; j < argsCount; j++) {
    Element argElement = (Element) condElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).item(j);
    String argValueText = argElement.getTextContent();
    args.add(argValueText);
    // getTagTextContent(, LogicXMLParser.ARGUMENT_TAG);
    String argTypeText = argElement.getAttribute(LogicXMLParser.ARGUMENT_TYPE_TAG);
    //getAttribute(argElement, LogicXMLParser.ARGUMENT_TAG, LogicXMLParser.ARGUMENT_TYPE_TAG);
    ParameterType argType = ParameterType.getParameterType(argTypeText);
    switch (argType) {
    case NODE:
    if (firstNode) {
    nodeArg = new StringSchemeVariable(argValueText);
    firstNode = false;
    } else {
    node2Arg = new StringSchemeVariable(argValueText);
    }
    break;
    case FORMULA:
    formulaArg = oldiesTokenizer.parseExpression(argValueText);
    break;
    case RELATION:
    relationArg = oldiesTokenizer.parseExpression(argValueText);
    break;
    case MARK:
    markArg = argValueText;
    break;
    default:
    throw new ParseException(ParseException.UNKOWN_ARGUMENT_TYPE +
    "\nThe concerned condition name is: " + getTagTextContent(condElement, LogicXMLParser.CONDITION_NAME_TAG) +
    "\nArgument type should be one of the following: " + ParameterType.values());
    }
    }
    //                    Verifier.verifyConditionParams(condName, args);                    
    if (condName.equals("hasElement")) {
    cond = new ExpressionCondition(nodeArg, formulaArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    } else if (condName.equals("hasNotElement")) {
    cond = new NotExpressionCondition(nodeArg, formulaArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    } else if (condName.equals("isProposition")) {
    cond = new AtomCondition(nodeArg, formulaArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    } else if (condName.equals("isLinked")) {
    cond = new LinkCondition(nodeArg, node2Arg, relationArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    cond.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    } else if (condName.equals("isNotLinked")) {
    cond = new NotLinkCondition(nodeArg, node2Arg, relationArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    cond.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    } else if (condName.equals("hasNoSuccessor")) {
    cond = new HasNotSuccessorCondition(nodeArg, relationArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    } else if (condName.equals("isAncestor")) {
    cond = new AncestorCondition(nodeArg, node2Arg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    } else if (condName.equals("areIdentical")) {
    cond = new IdenticalCondition(nodeArg, node2Arg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    } else if (condName.equals("areNotIdentical")) {
    cond = new NotIdenticalCondition(nodeArg, node2Arg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    } else if (condName.equals("contains")) {
    cond = new ContainsCondition(nodeArg, node2Arg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    } else if (condName.equals("isNewNode")) {
    cond = new NodeCreatedCondition(nodeArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    } else if (condName.equals("isMarked")) {
    cond = new MarkCondition(nodeArg, markArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else if (condName.equals("isNotMarked")) {
    cond = new NotMarkCondition(nodeArg, markArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else if (condName.equals("isMarkedExpression")) {
    cond = new MarkExpressionCondition(nodeArg, formulaArg, markArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    cond.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else if (condName.equals("isNotMarkedExpression")) {
    cond = new NotMarkExpressionCondition(nodeArg, formulaArg, markArg);
    cond.setName(condName);
    cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    cond.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else {
    throw new ParseException(ParseException.UNKOWN_CODITION +
    ParseException.EXCEPTION_CAUSE + condName);
    }
    //                // Reserved to MSPASS...
    //                else if(s.equals("younger")){
    //                return new CompareDescriptor(new   StringSchemeVariable(readStringToken()), new
    //                StringSchemeVariable(readStringToken()));
    //                } else if(s.equals("MSPASSCall")){
    //                return new MSPASSDescriptor(new   StringSchemeVariable(readStringToken()),
    //                recognizeExpression(), readIntToken());
    //                }
    //
    //                // May be activated later whend found in some old predefined files...
    //                else if(s.equals("haveAllSuccessorExpression")){
    //                return new HaveAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //                recognizeExpression(), recognizeExpression());
    //                }else if(s.equals("haveNotAllSuccessorExpression")){
    //                return new HaveNotAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //                recognizeExpression(), recognizeExpression());
    //                }else if(s.equals("isMarkedInAllSuccessor")){
    //                return new MarkAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //                recognizeExpression(), recognizeExpression(),readStringToken(), testMarkActivationValidity);
    //                }else if(s.equals("isNotMarkedInAllSuccessor")){
    //                return new NotMarkAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //                recognizeExpression(), recognizeExpression(),readStringToken(), testMarkActivationValidity);
    //                }
    }
    cond.setItsRule(rule);
    rule.addCondition(cond);
    }
    }
    }*/
    public void parseActions(Rule rule, Element ruleElement) throws ParseException {
        int actionsCount = ruleElement.getElementsByTagName(LogicXMLParser.ACTION_TAG).getLength();
        if (actionsCount == 0) {
//            throw new ParseException(ParseException.NO_RULE_ACTIONS + rule.getName() + ".");
        } else {
            for (int i = 0; i < actionsCount; i++) {
                Element acElement = (Element) ruleElement.getElementsByTagName(LogicXMLParser.ACTION_TAG).item(i);
                AbstractAction act = null;
                StringSchemeVariable nodeArg = null;
                StringSchemeVariable node2Arg = null;
                Expression formulaArg = null;
                Expression relationArg = null;
                String markArg = null;

                String keyword = getTagTextContent(acElement, LogicXMLParser.ACTION_NAME_TAG);
                String actionClassName = AbstractAction.CLASSES_KEYWORDS.get(keyword);
                if (actionClassName == null) {
                    throw new ParseException(ParseException.UNKOWN_ACTION +
                            ParseException.EXCEPTION_CAUSE + keyword);
                }
                int paramsCount = acElement.getElementsByTagName(LogicXMLParser.PARAMETER_TAG).getLength();
//                int paramsCount = acElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).getLength();
                ArrayList<String> paramsStrVals = new ArrayList<String>();
                for (int j = 0; j < paramsCount; j++) {
                    Element paramElement = (Element) acElement.getElementsByTagName(LogicXMLParser.PARAMETER_TAG).item(j);
//                    Element paramElement = (Element) acElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).item(j);
                    String paramStrVal = paramElement.getTextContent();
                    paramsStrVals.add(paramStrVal);

                }
                try {
                    Class actClass = Class.forName(
                            AbstractAction.ACTIONS_PACKAGE + actionClassName);
                    Constructor constructor = actClass.getConstructors()[0];
//                Class[] paramClasses = constructor.getParameterTypes();
                    ParametersTypes paramsTypes = (ParametersTypes) constructor.getAnnotation(ParametersTypes.class);
                    Vector<Parameter> params = new Vector<Parameter>();
                    if (paramsTypes.types().length != paramsStrVals.size()) {
                        throw new ParseException(ParseException.ACTION_PARAMETERS_COUNT +
                                ParseException.EXCEPTION_CAUSE + "Action '" + keyword + "' must take " +
                                paramsTypes.types().length +
                                " parameter(s) while the found prameter(s) count is " +
                                paramsStrVals.size());
                    }
                    boolean firstNode = true;
                    for (int j = 0; j < paramsTypes.types().length; j++) {
                        String paramTypeStr = paramsTypes.types()[j];
                        ParameterType paramType = ParameterType.getParameterType(paramTypeStr);
                        String paramStrVal = paramsStrVals.get(j);
                        switch (paramType) {
                            case NODE:
                                if (firstNode) {
                                    nodeArg = new StringSchemeVariable(paramStrVal);
                                    params.add(new Parameter(paramType, nodeArg));
                                    firstNode = false;
                                } else {
                                    node2Arg = new StringSchemeVariable(paramStrVal);
                                    params.add(new Parameter(paramType, node2Arg));
                                }
                                break;
                            case FORMULA:
                                try {
                                    formulaArg = oldiesTokenizer.parseExpression(paramStrVal);
                                    oldiesTokenizer.verifyCodeEnd();
                                } catch (ParseException ex) {
                                    throw new ParseException(ParseException.ACTION_PARAMETER_PARSING +
                                            ParseException.EXCEPTION_CAUSE + paramStrVal +
                                            "Fomula's expression parsing exception:\n" + ex.getMessage());
                                }
                                params.add(new Parameter(paramType, formulaArg));
                                break;
                            case RELATION:
                                try {
                                    relationArg = oldiesTokenizer.parseExpression(paramStrVal);
                                    oldiesTokenizer.verifyCodeEnd();
                                } catch (ParseException ex) {
                                    throw new ParseException(ParseException.ACTION_PARAMETER_PARSING +
                                            ParseException.EXCEPTION_CAUSE + paramStrVal +
                                            "Relation's expression parsing exception:\n" + ex.getMessage());
                                }
                                params.add(new Parameter(paramType, relationArg));
                                break;
                            case MARK:
                                markArg = paramStrVal;
                                params.add(new Parameter(paramType, markArg));
                                break;
                        }
//                    Class paramClass = paramClasses[i];
//                    System.out.println("Class to use: " + paramClass.getSimpleName() + ",   type: " + paramsTypes.types()[i] +
//                            ",   description: " + paramsDesc.descriptions()[i]);
                    }
                    int paramsNum = params.size();
//                    act = (AbstractAction) constructor.newInstance(params.toArray());
                    switch (paramsNum) {
                        case 1:
                            act = (AbstractAction) constructor.newInstance(
                                    params.get(0).getValue());
                            break;
                        case 2:
                            act = (AbstractAction) constructor.newInstance(
                                    params.get(0).getValue(),
                                    params.get(1).getValue());
                            break;
                        case 3:
                            act = (AbstractAction) constructor.newInstance(
                                    params.get(0).getValue(),
                                    params.get(1).getValue(),
                                    params.get(2).getValue());
                            break;
                    }
                    for (Parameter p : params) {
                        act.addParameter(p);
                    }
                    act.setName(keyword);
                    act.setItsRule(rule);
                    rule.addAction(act);
                } catch (InstantiationException ex) {
                    throw new ParseException(ParseException.ACTION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (IllegalAccessException ex) {
                    throw new ParseException(ParseException.ACTION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    throw new ParseException(ParseException.ACTION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (InvocationTargetException ex) {
                    throw new ParseException(ParseException.ACTION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                } catch (ClassNotFoundException ex) {
                    throw new ParseException(ParseException.ACTION_PARSING +
                            ParseException.EXCEPTION_CAUSE + ex.getMessage());
                }
            }
        }
    }

    /*
     * Version using the <argument type="node"> ... </argument>
     * 
    public void parseActions(Rule rule, Element ruleElement) throws ParseException {
    int actionsCount = ruleElement.getElementsByTagName(LogicXMLParser.ACTION_TAG).getLength();
    if (actionsCount == 0) {
    //            throw new ParseException(ParseException.NO_RULE_ACTIONS + rule.getName() + ".");
    } else {
    for (int i = 0; i < actionsCount; i++) {
    Element acElement = (Element) ruleElement.getElementsByTagName(LogicXMLParser.ACTION_TAG).item(i);
    AbstractAction ac;
    String acName;
    StringSchemeVariable nodeArg = null;
    StringSchemeVariable node2Arg = null;
    Expression formulaArg = null;
    Expression relationArg = null;
    String markArg = null;
    if (this.getTagTextContent(acElement, LogicXMLParser.ACTION_NAME_TAG).equals("")) {
    throw new ParseException(ParseException.UNKOWN_ACTION +
    ParseException.EXCEPTION_CAUSE + getTagTextContent(acElement, LogicXMLParser.ACTION_NAME_TAG));
    } else {
    acName = getTagTextContent(acElement, LogicXMLParser.ACTION_NAME_TAG);
    int argsCount = acElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).getLength();
    boolean firstNode = true;
    for (int j = 0; j < argsCount; j++) {
    Element argElement = (Element) acElement.getElementsByTagName(LogicXMLParser.ARGUMENT_TAG).item(j);
    String argValueText = argElement.getTextContent();
    // getTagTextContent(, LogicXMLParser.ARGUMENT_TAG);
    String argTypeText = argElement.getAttribute(LogicXMLParser.ARGUMENT_TYPE_TAG);
    //getAttribute(argElement, LogicXMLParser.ARGUMENT_TAG, LogicXMLParser.ARGUMENT_TYPE_TAG);
    ParameterType argType = ParameterType.getParameterType(argTypeText);
    switch (argType) {
    case NODE:
    if (firstNode) {
    nodeArg = new StringSchemeVariable(argValueText);
    firstNode = false;
    } else {
    node2Arg = new StringSchemeVariable(argValueText);
    }
    break;
    case FORMULA:
    formulaArg = oldiesTokenizer.parseExpression(argValueText);
    break;
    case RELATION:
    relationArg = oldiesTokenizer.parseExpression(argValueText);
    break;
    case MARK:
    markArg = argValueText;
    break;
    default:
    throw new ParseException(ParseException.UNKOWN_ARGUMENT_TYPE +
    "\nThe concerned action name is: " + getTagTextContent(acElement, LogicXMLParser.ACTION_NAME_TAG) +
    "\nArgument type should be one of the following: " + ParameterType.values());
    }
    }
    if (acName.equals("add")) {
    ac = new AddExpressionAction(nodeArg, formulaArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    } else if (acName.equals("createNewNode")) {
    ac = new AddNodeAction(nodeArg, node2Arg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    } else if (acName.equals("link")) {
    ac = new LinkAction(nodeArg, node2Arg, relationArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    ac.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    } else if (acName.equals("stop")) {
    ac = new StopStrategyAction(nodeArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    } else if (acName.equals("mark")) {
    ac = new MarkAction(nodeArg, markArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else if (acName.equals("unmark")) {
    ac = new UnmarkAction(nodeArg, markArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else if (acName.equals("markExpressions")) {
    ac = new MarkExpressionsAction(nodeArg, formulaArg, markArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    ac.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else if (acName.equals("unmarkExpressions")) {
    ac = new UnmarkExpressionsAction(nodeArg, formulaArg, markArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    ac.addParameter(new Parameter(ParameterType.MARK, markArg));
    } else if (acName.equals("createOneSuccessor")) {
    ac = new AddOneNodeAction(nodeArg, node2Arg, relationArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    ac.addParameter(new Parameter(ParameterType.RELATION, relationArg));
    } else if (acName.equals("hide")) {
    ac = new HideAction(nodeArg, formulaArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
    } // Actions dealing with the global strategy!!!!
    // To be resolved...
    else if (acName.equals("kill")) {
    ac = new KillAction(nodeArg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    } else if (acName.equals("duplicate")) {
    ac = new DuplicateAction(nodeArg, node2Arg);
    ac.setName(acName);
    ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
    ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
    } // Perhaps an un-used list of actions!!
    // this must be verified
    //                    else if(s.equals("linkIfNotExist")) {
    //                    return new LinkIfNotExistAction(new StringSchemeVariable(readStringToken()), new StringSchemeVariable(readStringToken()), recognizeExpression());
    //                    }else if(s.equals("createNewConstant")) {
    //                    return new CreateNewConstantAction(recognizeExpression());
    //                    }else if(s.equals("callOracle")){
    //                    return new OracleAction(new StringSchemeVariable(readStringToken()), readStringToken());
    //                    }else if(s.equals("run")){
    //                    return new ProcessAction(new StringSchemeVariable(readStringToken()),readStringToken());
    //                    }
    else {
    throw new ParseException(ParseException.UNKOWN_ACTION +
    ParseException.EXCEPTION_CAUSE + acName);
    }
    }
    ac.setItsRule(rule);
    rule.addAction(ac);
    }
    }
    }
     */
    public void parseStrategies(Logic parsedLogic) throws ParseException {
        int strategiesCount = doc.getElementsByTagName(LogicXMLParser.STRATEGY_TAG).getLength();
        if (strategiesCount == 0) {
            throw new ParseException(ParseException.NO_STRATEGIES);
        } else {
            for (int i = 0; i < strategiesCount; i++) {
                Element strategyElement = (Element) doc.getElementsByTagName(LogicXMLParser.STRATEGY_TAG).item(i);
                String strategyName = this.getTagTextContent(strategyElement, LogicXMLParser.STRATEGY_NAME_TAG);
                Strategy strategy = parsedLogic.getStrategy(strategyName);
                if (strategy != null) {
                    throw new ParseException(ParseException.DUPLICATED_STRATEGIES + strategyName);
                }
                strategy = oldiesTokenizer.parseStrategy(getTagTextContent(strategyElement, LogicXMLParser.STRATEGY_CODE_TAG));
                strategy.setWorkerName(strategyName);
                strategy.setCode(getTagTextContent(strategyElement, LogicXMLParser.STRATEGY_CODE_TAG));
                //Usability must be tested for complete or partial!!
//                strategy.setUsability(getTagTextContent(strategyElement, LogicXMLParser.STRATEGY_USABILITY_TAG));
                strategy.setComment(getTagTextContent(strategyElement, LogicXMLParser.STRATEGY_COMMENT_TAG));
                parsedLogic.addStrategy(strategy);
            }

        }
    }

    public void parseTestingFormulae(Logic parsedLogic) throws ParseException {
        int tstFormulaeCount = doc.getElementsByTagName(LogicXMLParser.TESTING_FORMULA_TAG).getLength();
        if (tstFormulaeCount > 0) {
            for (int i = 0; i < tstFormulaeCount; i++) {
                Element tstFormulaElement = (Element) doc.getElementsByTagName(LogicXMLParser.TESTING_FORMULA_TAG).item(i);
                TestingFormula tstFormula = new TestingFormula();
                String tstFormulaCode = getTagTextContent(tstFormulaElement, LogicXMLParser.TF_CODE_TAG);
                if (tstFormulaCode.equals("")) {
                    throw new ParseException(ParseException.EMPTY_TESTING_FORMULA);
                }
                tstFormula.setFormula(new MarkedExpression(oldiesTokenizer.parseExpression(tstFormulaCode)));
                tstFormula.setName(getTagTextContent(tstFormulaElement, LogicXMLParser.TF_NAME_TAG));
                tstFormula.setComment(getTagTextContent(tstFormulaElement, LogicXMLParser.TF_COMMENT_TAG));
                parsedLogic.addTestingFormula(tstFormula);
            }
        }
    }

//    public Connector parseConnector(String connectorCode) {
//        return null;
//    }
//    
//    public ConstantExpression parseConstant(String constantCode) {
//        return null;
//    }
//    
//    public VariableExpression parseVariable(String variableCode) {
//        return null;
//    }
//    
//    public MarkedExpression parseFormula(String formulaCode, Logic logicDef) {
//        return null;
//    }
//    
//    public AbstractCondition parseRuleCondition(String conditionCode, Logic logicDef) {
//        return null;
//    }
//    
//    public Activator parseRuleActivator(String actionCode, Logic logicDef) {
//        return null;
//    }
//    
//    public Rule parseRule(String ruleCode, Logic logicDef) {
//        return null;
//    }
//    
//    public Strategy parseStrategy(String strategyCode, Logic logicDef) {
//        return null;
//    }

//    public String getAttribute(
//            Object e, String elementName, String attributeName) {
//        if (e instanceof Document) {
//            return ((Document) e).getElementsByTagName(elementName).item(0).getAttributes().getNamedItem(attributeName).getNodeValue();
//        } else {
//            return ((Element) e).getElementsByTagName(elementName).item(0).getAttributes().getNamedItem(attributeName).getNodeValue();
//        }
//
//    }

    public String getTagTextContent(
            Object e, String tagName) {
        if (e instanceof Document) {
            return ((Document) e).getElementsByTagName(tagName).item(0).getTextContent();
        } else {
            return ((Element) e).getElementsByTagName(tagName).item(0).getTextContent();
        }

    }

//    public void setTagTextContent(Object e, String tagName, String value) {
//        if (e instanceof Document) {
//            ((Document) e).getElementsByTagName(tagName).item(0).setTextContent(value);
//        } else {
//            ((Element) e).getElementsByTagName(tagName).item(0).setTextContent(value);
//        }
//
//    }

//    public int getElementsCount(Document document, String elementName) {
//        int res = 0;
//        try {
//            res = document.getElementsByTagName(elementName).getLength();
//        //item(0).getAttributes().getNamedItem(attributeName).getNodeValue();
//        } catch (Exception e) {
//        }
//        return res;
//    }
}
