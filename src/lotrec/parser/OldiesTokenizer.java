/*
 * OldiesTokenizer.java
 *
 * Created on 22 mars 2007, 21:24
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec.parser;

import lotrec.parser.exceptions.ParseException;
import de.susebox.jtopas.ReaderSource;
import de.susebox.jtopas.StandardTokenizer;
import de.susebox.jtopas.StandardTokenizerProperties;
import de.susebox.jtopas.Token;
import de.susebox.jtopas.Tokenizer;
import de.susebox.jtopas.TokenizerException;
import de.susebox.jtopas.TokenizerProperties;
import de.susebox.jtopas.TokenizerSource;
import java.io.StringReader;
import lotrec.dataStructure.Logic;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.ParameterType;
import lotrec.dataStructure.tableau.Rule;
import lotrec.dataStructure.tableau.action.*;
import lotrec.dataStructure.tableau.condition.AncestorCondition;
import lotrec.dataStructure.tableau.condition.HasPropositionCondition;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.dataStructure.tableau.condition.ContainsCondition;
import lotrec.dataStructure.tableau.condition.ExpressionCondition;
import lotrec.dataStructure.tableau.condition.HasNotSuccessorCondition;
import lotrec.dataStructure.tableau.condition.IdenticalCondition;
import lotrec.dataStructure.tableau.condition.LinkCondition;
import lotrec.dataStructure.tableau.condition.MarkCondition;
import lotrec.dataStructure.tableau.condition.MarkExpressionCondition;
import lotrec.dataStructure.tableau.condition.NodeCreatedCondition;
import lotrec.dataStructure.tableau.condition.NotExpressionCondition;
import lotrec.dataStructure.tableau.condition.NotIdenticalCondition;
import lotrec.dataStructure.tableau.condition.NotLinkCondition;
import lotrec.dataStructure.tableau.condition.NotMarkCondition;
import lotrec.dataStructure.tableau.condition.NotMarkExpressionCondition;
import lotrec.process.AbstractAction;
import lotrec.process.AllRules;
import lotrec.process.EventMachine;
import lotrec.process.FirstRule;
import lotrec.process.Routine;
import lotrec.process.Repeat;
import lotrec.process.Strategy;

/**
 *
 * @author said
 */
public class OldiesTokenizer {

    private TokenizerSource source;
    private TokenizerProperties props;
    private Tokenizer tokenizer;
    private Token token;
    private String tokenImage;
    private Logic parsedLogic;
//    private boolean testMarkActivationValidity;
    //private StringBuffer        oldiesTokenizerOutput;
    /** Creates a new instance of OldiesTokenizer */
    public OldiesTokenizer(Logic parsedLogic) {
        this.setParsedLogic(parsedLogic);
//        testMarkActivationValidity = true;
    //this.oldiesTokenizerOutput = new StringBuffer();
    }

    public AbstractCondition parseCondition(String conditionCode) throws ParseException {
        this.setSource(conditionCode);
        String condName = readStringToken();
        StringSchemeVariable nodeArg;
        StringSchemeVariable node2Arg;
        Expression formulaArg;
        Expression relationArg;
        String markArg;
        AbstractCondition cond;
        if (condName.equals("hasElement")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            cond = new ExpressionCondition(nodeArg, formulaArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            return cond;
        } else if (condName.equals("hasNotElement")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            cond = new NotExpressionCondition(nodeArg, formulaArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            return cond;
        } else if (condName.equals("hasProposition")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            cond = new HasPropositionCondition(nodeArg, formulaArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            return cond;
        } else if (condName.equals("isLinked")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            relationArg = recognizeExpression();
            cond = new LinkCondition(nodeArg, node2Arg, relationArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            cond.addParameter(new Parameter(ParameterType.RELATION, relationArg));
            return cond;
        } else if (condName.equals("isNotLinked")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            relationArg = recognizeExpression();
            cond = new NotLinkCondition(nodeArg, node2Arg, relationArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            cond.addParameter(new Parameter(ParameterType.RELATION, relationArg));
            return cond;
        } else if (condName.equals("hasNoSuccessor")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            relationArg = recognizeExpression();
            cond = new HasNotSuccessorCondition(nodeArg, relationArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.RELATION, relationArg));
            return cond;
        } else if (condName.equals("isAncestor")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            cond = new AncestorCondition(nodeArg, node2Arg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            return cond;
        } else if (condName.equals("areIdentical")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            cond = new IdenticalCondition(nodeArg, node2Arg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            return cond;
        } else if (condName.equals("areNotIdentical")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            cond = new NotIdenticalCondition(nodeArg, node2Arg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            return cond;
        } else if (condName.equals("contains")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            cond = new ContainsCondition(nodeArg, node2Arg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            return cond;
        } else if (condName.equals("isNewNode")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            cond = new NodeCreatedCondition(nodeArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            return cond;
        } else if (condName.equals("isMarked")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            markArg = readStringToken();
            cond = new MarkCondition(nodeArg, markArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.MARK, markArg));
            return cond;
        } else if (condName.equals("isNotMarked")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            markArg = readStringToken();
            cond = new NotMarkCondition(nodeArg, markArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.MARK, markArg));
            return cond;
        } else if (condName.equals("isMarkedExpression")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            markArg = readStringToken();
            cond = new MarkExpressionCondition(nodeArg, formulaArg, markArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            cond.addParameter(new Parameter(ParameterType.MARK, markArg));
            return cond;
        } else if (condName.equals("isNotMarkedExpression")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            markArg = readStringToken();
            cond = new NotMarkExpressionCondition(nodeArg, formulaArg, markArg);
            cond.setName(condName);
            cond.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            cond.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            cond.addParameter(new Parameter(ParameterType.MARK, markArg));
            return cond;
        } else {
            throw new ParseException(ParseException.UNKOWN_CODITION +
                    ParseException.EXCEPTION_CAUSE + condName);
        }

    // Reserved to MSPASS...
        /*else if(s.equals("younger")){
    return new CompareDescriptor(new   StringSchemeVariable(readStringToken()), new
    StringSchemeVariable(readStringToken()));
    } else if(s.equals("MSPASSCall")){
    return new MSPASSDescriptor(new   StringSchemeVariable(readStringToken()),
    recognizeExpression(), readIntToken());
    }*/

    // May be activated later whend found in some old predefined files...
        /*else if(s.equals("haveAllSuccessorExpression")){
    return new HaveAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    recognizeExpression(), recognizeExpression());
    }else if(s.equals("haveNotAllSuccessorExpression")){
    return new HaveNotAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    recognizeExpression(), recognizeExpression());
    }else if(s.equals("isMarkedInAllSuccessor")){
    return new MarkAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    recognizeExpression(), recognizeExpression(),readStringToken(), testMarkActivationValidity);
    }else if(s.equals("isNotMarkedInAllSuccessor")){
    return new NotMarkAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    recognizeExpression(), recognizeExpression(),readStringToken(), testMarkActivationValidity);
    }
     */
    }

    public AbstractAction parseAction(String actionCode) throws ParseException {
        this.setSource(actionCode);
        String acName = readStringToken();
        StringSchemeVariable nodeArg;
        StringSchemeVariable node2Arg;
        Expression formulaArg;
        Expression relationArg;
        String markArg;
        AbstractAction ac;
        if (acName.equals("add")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            ac = new AddExpressionAction(nodeArg, formulaArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            return ac;
        } else if (acName.equals("createNewNode")) {
            nodeArg = new StringSchemeVariable(readStringToken());
//            node2Arg = new StringSchemeVariable(readStringToken());
//            ac = new AddNodeAction(nodeArg, node2Arg);
            ac = new AddNodeAction(nodeArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
//            ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            return ac;
        } else if (acName.equals("link")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            relationArg = recognizeExpression();
            ac = new LinkAction(nodeArg, node2Arg, relationArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            ac.addParameter(new Parameter(ParameterType.RELATION, relationArg));
            return ac;
        } else if (acName.equals("stop")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            ac = new StopStrategyAction(nodeArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            return ac;
        } else if (acName.equals("mark")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            markArg = readStringToken();
            ac = new MarkAction(nodeArg, markArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.MARK, markArg));
            return ac;
        } else if (acName.equals("unmark")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            markArg = readStringToken();
            ac = new UnmarkAction(nodeArg, markArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.MARK, markArg));
            return ac;
        } else if (acName.equals("markExpressions")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            markArg = readStringToken();
            ac = new MarkExpressionsAction(nodeArg, formulaArg, markArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            ac.addParameter(new Parameter(ParameterType.MARK, markArg));
            return ac;
        } else if (acName.equals("unmarkExpressions")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            markArg = readStringToken();
            ac = new UnmarkExpressionsAction(nodeArg, formulaArg, markArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            ac.addParameter(new Parameter(ParameterType.MARK, markArg));
            return ac;
        } else if (acName.equals("createOneSuccessor")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            node2Arg = new StringSchemeVariable(readStringToken());
            relationArg = recognizeExpression();
            ac = new AddOneSuccessorAction(nodeArg, node2Arg, relationArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.NODE, node2Arg));
            ac.addParameter(new Parameter(ParameterType.RELATION, relationArg));
            return ac;
        } else if (acName.equals("hide")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            formulaArg = recognizeExpression();
            ac = new HideAction(nodeArg, formulaArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.FORMULA, formulaArg));
            return ac;
        } // Actions dealing with the global strategy!!!!
        // To be resolved...
        else if (acName.equals("kill")) {
            nodeArg = new StringSchemeVariable(readStringToken());
            ac = new KillAction(nodeArg);
            ac.setName(acName);
            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            return ac;
        } else if (acName.equals("duplicate")) {
//            nodeArg = new StringSchemeVariable(readStringToken());//.equals(node0)


            readStringToken(); //.equals("begin")            
            readStringToken(); //.equals(node0)
            //node2Arg = new StringSchemeVariable(readStringToken());//.equals(node1)

            markArg = readStringToken();
//            ac = new DuplicateAction(nodeArg, markArg);
            ac = new DuplicateAction(markArg);
            readStringToken(); //.equals("end")

            ac.setName(acName);
//            ac.addParameter(new Parameter(ParameterType.NODE, nodeArg));
            ac.addParameter(new Parameter(ParameterType.MARK, markArg));
            return ac;
        } // Perhaps an un-used list of actions!!
        // this must be verified
        /*else if(s.equals("linkIfNotExist")) {
        return new LinkIfNotExistAction(new StringSchemeVariable(readStringToken()), new StringSchemeVariable(readStringToken()), recognizeExpression());
        }else if(s.equals("createNewConstant")) {
        return new CreateNewConstantAction(recognizeExpression());
        }else if(s.equals("callOracle")){
        return new OracleAction(new StringSchemeVariable(readStringToken()), readStringToken());
        }else if(s.equals("run")){
        return new ProcessAction(new StringSchemeVariable(readStringToken()),readStringToken());
        }
         */ else {
            throw new ParseException(ParseException.UNKOWN_ACTION +
                    ParseException.EXCEPTION_CAUSE + acName);
        }
    }

    public Strategy recognizeStrategy() throws ParseException {
//        boolean empty = true;
        Strategy result = new Strategy();
        String t = "";
        try {
            t = readStringToken();
        } catch (Exception e) {
//            System.out.println("Empty Strategy!!");
        }
        //Lotrec.println("Begining of Strategy definition");
        if (!t.equals("")) {
            while (true) {
                //Lotrec.println("Strategy definition read part: " + t);
                if (t.equals("allRules") || t.equals("firstRule") || t.equals("repeat")) {
                    result.add(recognizeRoutine(t), null);
//                    empty = false;
                } else {
                    if (t.equals("applyOnce")) {
                        try {
                            t = readStringToken();
                        } catch (Exception e) {
                            throw new ParseException("Rule name is expected after applyOnce routine");
                        }
                        Rule rule = parsedLogic.getRule(t);
                        if (rule == null) {
                            throw new ParseException("Rule name is expected after applyOnce routine\n" +
                                    "The found token is '" + t + "' seems not a valid rule name.");
                        } else {
                            EventMachine machine = rule.createMachine();
                            machine.setApplyOnOneOccurence(true);
                            result.add(machine, null);
//                        empty = false;                            
                        }
                    } else {
                        Rule rule = parsedLogic.getRule(t);
                        if (rule != null) {
                            EventMachine machine = rule.createMachine();
                            result.add(machine, null);
//                        empty = false;
                        } else {
                            Strategy strategy = parsedLogic.getStrategy(t);
                            if (strategy != null) {
//                            result.add(strategy, null);
                                OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(parsedLogic);
                                oldiesTokenizer.initializeTokenizerAndProps();
                                Strategy newStr = oldiesTokenizer.parseStrategy(strategy.getCode());
                                result.add(newStr, null);
//                            empty = false;
                            } else {
                                throw new ParseException(ParseException.EXCEPTION_CAUSE + "'" + t + "'\n\n" +
                                        ParseException.BAD_STRATEGY_DEF);
                            }
                        }
                    }
                }
                // read another block in the strategy
                // or just break;
                try {
                    t = readStringToken();
                } catch (ParseException e) {
                    //Lotrec.println("End of Strategy definition");
                    break;
                }
            }
        } else {
//            throw new ParseException(ParseException.EMPTY_STRATEGY_DEF);
        }
        return result;
    }

    private Routine recognizeRoutine(String s) throws ParseException {
        Routine routine;
        if (s.equals("allRules")) {
            routine = new AllRules();
        } else if (s.equals("firstRule")) {
            routine = new FirstRule();
        } else if (s.equals("repeat")) {
            routine = new Repeat();
        } else {
            throw new ParseException(ParseException.EXCEPTION_CAUSE + "'" + s + "'\n\n" +
                    ParseException.BAD_STRATEGY_DEF);
        }
        for (;;) {
            String t = readStringToken();
            if (t.equals("end")) {
                return routine;
            } else if (t.equals("allRules") || t.equals("firstRule") || t.equals("repeat")) {
                Routine other = recognizeRoutine(t);
                routine.add(other, null);
            } else {
                if (t.equals("applyOnce")) {
                    try {
                        t = readStringToken();
                    } catch (Exception e) {
                        throw new ParseException("Rule name is expected after applyOnce routine");
                    }
                    Rule rule = parsedLogic.getRule(t);
                    if (rule == null) {
                        throw new ParseException("Rule name is expected after applyOnce routine\n" +
                                "The found token is '" + t + "' seems not a valid rule name.");
                    } else {
                        EventMachine machine = rule.createMachine();
                        machine.setApplyOnOneOccurence(true);
                        routine.add(machine, null);                  
                    }
                } else {
                    Rule rule = parsedLogic.getRule(t);
                    if (rule != null) {
                        EventMachine machine = rule.createMachine();
                        routine.add(machine, null);
                    } else {
                        Strategy strategy = parsedLogic.getStrategy(t);
                        if (strategy != null) {
//                        routine.add(strategy, null);                        
                            OldiesTokenizer oldiesTokenizer = new OldiesTokenizer(parsedLogic);
                            oldiesTokenizer.initializeTokenizerAndProps();
                            Strategy newStr = oldiesTokenizer.parseStrategy(strategy.getCode());
                            newStr.setWorkerName(strategy.getWorkerName());
                            newStr.setCode(strategy.getCode());
//                            newStr.setUsability(strategy.getUsability());
                            routine.add(newStr, null);
                        } else {
                            throw new ParseException(ParseException.EXCEPTION_CAUSE + "'" + t + "'\n\n" +
                                    ParseException.BAD_STRATEGY_DEF);
                        }
                    }
                }
            }
        }
    }

    public Strategy parseStrategy(String strategyCode) throws ParseException {
        this.setSource(strategyCode);
        return recognizeStrategy();
    }

    public Expression recognizeExpression() throws ParseException {
        String s = readStringToken();
        if ((s.length() > 0) && (s.charAt(0) != '_') && Character.isUpperCase(s.charAt(0))) {
            return new ConstantExpression(s);
        }
        if (s.startsWith("n_")) {
            String tail = s.substring(2);
            return new VariableNodeExpression(tail);
        }
        if (s.startsWith("_")) {
            String tail = s.substring(1);
            return new VariableExpression(tail);
        }
        if (s.equals("constant")) {
            return new ConstantExpression(readStringToken().toUpperCase());
        }
        if (s.equals("nodeVariable")) {
            return new VariableNodeExpression(readStringToken());
        }
        if (s.equals("variable")) {
            return new VariableExpression(readStringToken());
        }
        Connector connector = parsedLogic.getConnector(s);
        if (connector == null) {
            throw new ParseException(ParseException.EXCEPTION_CAUSE + s+"\n\n" +
                    ParseException.UNKOWN_EXPRESSION_CONNECTOR);
        }
        ExpressionWithSubExpressions expression = new ExpressionWithSubExpressions(connector);
        for (int i = 0; i < connector.getArity(); i++) {
            expression.setExpression(recognizeExpression(), i);
        }
        return expression;
    }

// (we don't need readIntToken()), instead,
// we need readStringToken() for 2 goals:
// 1- tests wethere there's more tokens or not (the end of definition)
// 2- tests if the token is smthg normal (else than comments...)
// 3- UPCOMING tests if the tokens verify some pattern rules...
    public String readStringToken() throws ParseException {
        if (!tokenizer.hasMoreToken()) {
            throw new ParseException(ParseException.TOO_FEW_TOKENS);
        } else {
            try {
                token = tokenizer.nextToken();
            } catch (TokenizerException ex) {
                throw new ParseException(ParseException.TOKENIZER_EXCEPTION + ex.getMessage());
            }
            if (token.getType() == Token.NORMAL) {
                //oldiesTokenizerOutput.append("Read : " + token.getImage() +"\n");
                //Lotrec.println("Normal token read: " + token.getImage());
                return token.getImage();
            } else {
                //Lotrec.println("Ignored token read: " + token.getImage());
                return readStringToken();
            }
        }
    }
//Should be called after each parsing operation
//Otherwise, we will not be sure that the user
// has added some unuseful code at the end...
    @SuppressWarnings("empty-statement")
    public void verifyCodeEnd() throws ParseException {
        while (tokenizer.hasMoreToken()) {
            try {
                token = tokenizer.nextToken();
            } catch (TokenizerException ex) {
                throw new ParseException(ParseException.TOKENIZER_EXCEPTION + ex.getMessage());
            }
            if (token.getType() == Token.NORMAL) {
                throw new ParseException(ParseException.TOO_MORE_TOKENS +
                        ParseException.EXCEPTION_CAUSE + token.getImage());
            } else {
                ;
            }//Lotrec.println("Ignored token at the end: " + token.getImage());
        }
    }

    public Expression parseExpression(String formulaCode) throws ParseException {
        setSource(formulaCode);
        return recognizeExpression();
    }

    public void initializeTokenizerAndProps() {
        setProps(new StandardTokenizerProperties());
        props.addLineComment("//");
        props.addBlockComment(TokenizerProperties.DEFAULT_BLOCK_COMMENT_START,
                TokenizerProperties.DEFAULT_BLOCK_COMMENT_END);
        setTokenizer(new StandardTokenizer());
        getTokenizer().setTokenizerProperties(getProps());
    }

    protected void recognizeDuplication(DuplicateAction duplicateAction) throws ParseException {
        String s = readStringToken();
        if (!s.equals("begin")) {
            throw new ParseException(ParseException.BAD_DUPLICATION_ACTION);
        }
        for (;;) {
            String t = readStringToken();
            if (t.equals("end")) {
                return;
            } else {
                readStringToken();
                //DO NOT WORK ANY MORE
                //duplicateAction.add(new StringSchemeVariable(t), new StringSchemeVariable(readStringToken()));
            }
        }
    }

    public TokenizerSource getSource() {
        return source;
    }

    public void setSource(TokenizerSource source) {
        this.source = source;
    }

    public void setSource(String sourceCode) {
        this.setSource(new ReaderSource(new StringReader(sourceCode)));
        getTokenizer().setSource(this.getSource());
    }

    public TokenizerProperties getProps() {
        return props;
    }

    public void setProps(TokenizerProperties props) {
        this.props = props;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getTokenImage() {
        return tokenImage;
    }

    public void setTokenImage(String tokenImage) {
        this.tokenImage = tokenImage;
    }

    public Logic getParsedLogic() {
        return parsedLogic;
    }

    public void setParsedLogic(Logic parsedLogic) {
        this.parsedLogic = parsedLogic;
    }
    /*public StringBuffer getOldiesTokenizerOutput() {
    return oldiesTokenizerOutput;
    }
    public void setOldiesTokenizerOutput(StringBuffer oldiesTokenizerOutput) {
    this.oldiesTokenizerOutput = oldiesTokenizerOutput;
    }
     */
}
