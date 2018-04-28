/*
 * AbstractAction.java
 *
 * Created on 31 octobre 2007, 14:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec.process;

import java.util.HashMap;
import java.util.Vector;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.Rule;

/**
 *
 * @author said
 */
public abstract class AbstractAction implements Action {

    private Rule itsRule;
    private Vector<Parameter> parameters;
    private String name;
    public static HashMap<String, String> CLASSES_KEYWORDS;
    public static String ACTIONS_PACKAGE = "lotrec.dataStructure.tableau.action.";

    static {
        CLASSES_KEYWORDS = new HashMap<String, String>();
        CLASSES_KEYWORDS.put("add", "AddExpressionAction");
        CLASSES_KEYWORDS.put("createNewNode", "AddNodeAction");
        CLASSES_KEYWORDS.put("link", "LinkAction");
        CLASSES_KEYWORDS.put("stop", "StopStrategyAction");
        CLASSES_KEYWORDS.put("mark", "MarkAction");
        CLASSES_KEYWORDS.put("unmark", "UnmarkAction");
        CLASSES_KEYWORDS.put("markExpressions", "MarkExpressionsAction");
        CLASSES_KEYWORDS.put("unmarkExpressions", "UnmarkExpressionsAction");
        CLASSES_KEYWORDS.put("createOneSuccessor", "AddOneSuccessorAction");
        CLASSES_KEYWORDS.put("createOneParent", "AddOneParentAction");
        CLASSES_KEYWORDS.put("hide", "HideAction");
        CLASSES_KEYWORDS.put("kill", "KillAction");
        CLASSES_KEYWORDS.put("duplicate", "DuplicateAction");
        CLASSES_KEYWORDS.put("unlink", "UnlinkAction");
        CLASSES_KEYWORDS.put("merge", "MergeNodeInNodeAction");
    // Perhaps an un-used list of actions!!
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
    }

    /** Creates a new instance of AbstractAction */
    public AbstractAction() {
        parameters = new Vector<Parameter>();
    }

    public String getCode() {
        String code = name;
        for (Parameter param : parameters) {
            code = code + " " + param.getValueCode();
        }
        return code;
    }
//    Disactivated for the moment..
//    Anywa, it should be calculated automatically    
//    public void setCode(String code) {
//        this.code = code;
//    }
    public Rule getItsRule() {
        return itsRule;
    }

    public void setItsRule(Rule itsRule) {
        this.itsRule = itsRule;
    }

    public void addParameter(Parameter arg) {
        this.parameters.add(arg);
    }

    public Vector<Parameter> getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
