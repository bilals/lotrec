/*
 * AbstractCondition.java
 *
 * Created on 31 octobre 2007, 12:11
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package lotrec.dataStructure.tableau.condition;

import java.util.HashMap;
import java.util.Vector;
import lotrec.dataStructure.tableau.Parameter;
import lotrec.dataStructure.tableau.Rule;

/**
 *
 * @author said
 */
public abstract class AbstractCondition implements Condition {

    private Rule itsRule;
    private Vector<Parameter> parameters;
    private String name;
    public static HashMap<String, String> CLASSES_KEYWORDS;
    public static String CONDITIONS_PACKAGE = "lotrec.dataStructure.tableau.condition.";

    static {
        CLASSES_KEYWORDS = new HashMap<String, String>();
        CLASSES_KEYWORDS.put("hasElement", "ExpressionCondition");
        CLASSES_KEYWORDS.put("hasNotElement", "NotExpressionCondition");
        /*
         * hasElement + isProposition can replace hasProposition
         */
        //CLASSES_KEYWORDS.put("hasProposition", "HasPropositionCondition");
        CLASSES_KEYWORDS.put("isAtomic", "IsAtomicCondition");
        CLASSES_KEYWORDS.put("isNotAtomic", "IsNotAtomicCondition");
        CLASSES_KEYWORDS.put("isLinked", "LinkCondition");
        CLASSES_KEYWORDS.put("isNotLinked", "NotLinkCondition");
        CLASSES_KEYWORDS.put("hasNoSuccessor", "HasNotSuccessorCondition");
        CLASSES_KEYWORDS.put("hasNoParents", "HasNoParentsCondition");
        CLASSES_KEYWORDS.put("isAncestor", "AncestorCondition");
        CLASSES_KEYWORDS.put("areIdentical", "IdenticalCondition");
        CLASSES_KEYWORDS.put("areNotIdentical", "NotIdenticalCondition");
        CLASSES_KEYWORDS.put("areNotEqual", "NotEqualCondition");
        CLASSES_KEYWORDS.put("contains", "ContainsCondition");
        CLASSES_KEYWORDS.put("haveSameFormulasSet", "HaveSameFormulasSetCondition");
        CLASSES_KEYWORDS.put("isNewNode", "NodeCreatedCondition");
        CLASSES_KEYWORDS.put("isMarked", "MarkCondition");
        CLASSES_KEYWORDS.put("isNotMarked", "NotMarkCondition");
        CLASSES_KEYWORDS.put("isMarkedExpression", "MarkExpressionCondition");
        CLASSES_KEYWORDS.put("isNotMarkedExpression", "NotMarkExpressionCondition");
        CLASSES_KEYWORDS.put("isMarkedExpressionInAllChildren", "MarkedExpressionInAllChildrenCondition");
    //    Reserved to MSPASS...
    //    else if(s.equals("younger")){
    //    return new CompareDescriptor(new   StringSchemeVariable(readStringToken()), new
    //    StringSchemeVariable(readStringToken()));
    //    } else if(s.equals("MSPASSCall")){
    //    return new MSPASSDescriptor(new   StringSchemeVariable(readStringToken()),
    //    recognizeExpression(), readIntToken());
    //    }
    //    May be activated later whend found in some old predefined files...
    //    else if(s.equals("haveAllSuccessorExpression")){
    //    return new HaveAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //    recognizeExpression(), recognizeExpression());
    //    }else if(s.equals("haveNotAllSuccessorExpression")){
    //    return new HaveNotAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //    recognizeExpression(), recognizeExpression());
    //    }else if(s.equals("isMarkedInAllSuccessor")){
    //    return new MarkAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //    recognizeExpression(), recognizeExpression(),readStringToken(), testMarkActivationValidity);
    //    }else if(s.equals("isNotMarkedInAllSuccessor")){
    //    return new NotMarkAllSuccessorExpressionDescriptor(new   StringSchemeVariable(readStringToken()),
    //    recognizeExpression(), recognizeExpression(),readStringToken(), testMarkActivationValidity);
    //    }
    }
    //Could have other attributes such as the type, 
    //the list of parameters (in an appropriate manner..), 
    //the list of dependecies with the other conditions and/or 
    //actions of a rule to which it belongs
    /** Creates a new instance of AbstractCondition */
    public AbstractCondition() {
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
