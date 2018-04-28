/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lotrec.parser.exceptions.StringTokenizerException;
import lotrec.parser.exceptions.SyntaxException;
import lotrec.process.Strategy;

/**
 *
 * @author said
 */
public class Verifier {
    
    public static String CONNECTOR_NAME_SYNTAX = "[a-z][a-zA-Z0-9]*";
    public static String CONNECTOR_NAME_SYNTAX_DESC = "<html>A connetcor name is a single word identifier," +
            "starting by a lower-case alphabet letter followed by any sequence of alpha-numeric character<br>" +
            "or, imp2, isItSat, posEpis are wellformed<br>" +
            "Or, is It Sat, <>, ! are malformed</html>";

    public static String getVerifiedConnectorName(String connecName) throws SyntaxException {
        String verifiedName = null;
        StringTokenizer tokenizer = new StringTokenizer(connecName);
        try {
            verifiedName = tokenizer.readOneStringToken();
        } catch (StringTokenizerException ex) {
            throw new SyntaxException("Connector name: \"" + connecName + "\" is malformed.", ex.getMessage());
        }
        if (Character.isUpperCase(verifiedName.charAt(0))) {
            throw new SyntaxException("Connector name: \"" + connecName + "\" is malformed.",
                    "The first character in the given name is Upper Case.\n" +
                    "This will make confusion with \"constants\" while parsing a formula's (or a relation's) expression.");
        }
        return verifiedName;
    }

    public static void replaceRuleNameInStrategyCode(String oldRuleName, String newRuleName, Strategy str) {
        String REGEX = oldRuleName;//"\b"+oldRuleName+"\b";
        String INPUT = str.getCode();
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // get a matcher object        
        str.setCode(m.replaceAll(newRuleName));           
    }

    public static boolean verifyConnectorName(String connecName) {
        String REGEX = CONNECTOR_NAME_SYNTAX;
        String INPUT = connecName;
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // get a matcher object        
        return m.matches();
    }
    
    public static boolean verifyStrategyNameExistsInOtherStrategyCode(String strName, String otherStrCode) {
        String REGEX = strName;
        String INPUT = otherStrCode;
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // get a matcher object        
        return m.find();
    }    
    
    public static void replaceStrategyNameInOtherStrategyCode(String oldStrName, String newStrName, Strategy otherStr){
        String REGEX = oldStrName;
        String INPUT = otherStr.getCode();
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // get a matcher object        
        otherStr.setCode(m.replaceAll(newStrName));        
    }
    
    public static int verifyConnectorOutFormat(String outFormat) {
        String REGEX = "_";
        String INPUT = outFormat;
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // get a matcher object  
        int _Num = 0;
        while(m.find()){
            _Num++;
        }        
        return _Num;
    }    
    
    
}
