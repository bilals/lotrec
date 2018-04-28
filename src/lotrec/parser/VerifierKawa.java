/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.parser;

import lotrec.parser.exceptions.ParseException;
import lotrec.parser.exceptions.InternalParseException;
import lotrec.parser.exceptions.LexicalException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import lotrec.dataStructure.ParametersDescriptions;
import lotrec.dataStructure.tableau.condition.AbstractCondition;
import lotrec.process.AbstractAction;
import lotrec.dataStructure.ParametersTypes;

/**
 * Is the gate between the (XML parser - GUI)
 * and the ParseException classes..
 * Interprets the big parts and their smaller parts
 * And verify every little String argument
 * or Object creation, change or deletion..
 * It's up to the end-user interface to interpret
 * the ParseExceptions by warning the user or by sending errors..
 * @author said
 */
public class VerifierKawa {

    public static boolean verifyConditionParams(String keyword, ArrayList<String> params) throws ParseException {
        String condClassName = AbstractCondition.CONDITIONS_PACKAGE +
                AbstractCondition.CLASSES_KEYWORDS.get(keyword);
        try {
            Class condClass = Class.forName(condClassName);
            Constructor[] cs = condClass.getConstructors();
            Constructor c = cs[0];
            Class[] paramClasses = c.getParameterTypes();
//            Annotation paramsTypes = c.getAnnotation(ParametersTypes.class);
//            Annotation paramsDescriptions = c.getAnnotation(ParametersDescriptions.class);
            if (paramClasses.length > params.size()) {
                throw new LexicalException(LexicalException.TOO_FEW_PARAMS(
                        "condition", keyword, paramClasses.length, params.size()));
            }
            if (paramClasses.length < params.size()) {
                throw new LexicalException(LexicalException.MORE_PARAMS(
                        "condition", keyword, paramClasses.length, params.size()));
            }
//            for (int i = 0; i < paramClasses.length; i++) {
//                Class paramClass = paramClasses[i];
//
//            }
        } catch (ClassNotFoundException ex) {
            throw new InternalParseException(InternalParseException.HEADER + "\n" +
                    InternalParseException.CONDITION_CLASS_NOT_FOUND + condClassName + "\n" +
                    InternalParseException.SOLUTION);
        }
        return true;
    }

    public static Class[] getConditionParamsClasses(String keyword) throws ParseException {
        String condClassName = AbstractCondition.CONDITIONS_PACKAGE +
                AbstractCondition.CLASSES_KEYWORDS.get(keyword);
        try {
            Class condClass = Class.forName(condClassName);
            Constructor[] cs = condClass.getConstructors();
            Constructor c = cs[0];
            return c.getParameterTypes();

        } catch (ClassNotFoundException ex) {
            throw new InternalParseException(InternalParseException.HEADER + "\n" +
                    InternalParseException.CONDITION_CLASS_NOT_FOUND + condClassName + "\n" +
                    InternalParseException.SOLUTION);
        }
    }

    public static void displayConditionsClasses() {
        for (String keyword : AbstractCondition.CLASSES_KEYWORDS.keySet()) {
//            System.out.println("Keyword: " + keyword);
            String conditionClassName = AbstractCondition.CLASSES_KEYWORDS.get(keyword);
            try {
                Class condClass = Class.forName(
                        AbstractCondition.CONDITIONS_PACKAGE + conditionClassName);
//                System.out.println("Corresponding class: " + conditionClassName);
                for (Constructor constructor : condClass.getConstructors()) {
                    System.out.println("KEYWORD: " + keyword + ",   CLASS: " + conditionClassName + ",   PARAMS: "+ constructor.getGenericParameterTypes().length);
                    Class[] paramClasses = constructor.getParameterTypes();
                    ParametersTypes paramsTypes = (ParametersTypes)constructor.getAnnotation(ParametersTypes.class);
                    ParametersDescriptions paramsDesc = (ParametersDescriptions) constructor.getAnnotation(ParametersDescriptions.class);
                    for (int i = 0; i < paramClasses.length; i++) {
                        Class paramClass = paramClasses[i];
                        System.out.println("["+(i+1)+"] CLASS: "+paramClass.getSimpleName() + ",   TYPE: " + paramsTypes.types()[i] + 
                                ",   DESC: " + paramsDesc.descriptions()[i]);
                    }
                }
                System.out.println();
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------");
    }

    public static void displayActionsClasses() {
        for (String keyword : AbstractAction.CLASSES_KEYWORDS.keySet()) {
//            System.out.println("Keyword: " + keyword);
            String actionClassName = AbstractAction.CLASSES_KEYWORDS.get(keyword);
            try {
                Class condClass = Class.forName(
                        AbstractAction.ACTIONS_PACKAGE + actionClassName);
//                System.out.println("Corresponding class: " + actionClassName);
                for (Constructor constructor : condClass.getConstructors()) {
                    System.out.println("KEYWORD: " + keyword + ",   CLASS: " + actionClassName + ",   PARAMS: " + constructor.getGenericParameterTypes().length);
                    Class[] paramClasses = constructor.getParameterTypes();
                    ParametersTypes paramsTypes = (ParametersTypes)constructor.getAnnotation(ParametersTypes.class);
                    ParametersDescriptions paramsDesc = (ParametersDescriptions) constructor.getAnnotation(ParametersDescriptions.class);
                    for (int i = 0; i < paramClasses.length; i++) {
                        Class paramClass = paramClasses[i];
                        System.out.println("["+(i+1)+"] CLASS: "+paramClass.getSimpleName() + ",   TYPE: " + paramsTypes.types()[i] + 
                                ",   DESC: " + paramsDesc.descriptions()[i]);
                    }
                }
                System.out.println();
            } catch (ClassNotFoundException ex) {
                System.out.println(ex);
            }
        }
    }
}
