/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.parser.exceptions;

/**
 *
 * @author said
 */
public class InternalParseException extends ParseException{
    public static String HEADER = "Internal Parse Exception!";
    public static String CONDITION_CLASS_NOT_FOUND = "The following condition class name is not found: ";
    public static String SOLUTION = "You need to contact the LoTREC developping team to solve this problem.";
    
    public InternalParseException(String msg){
        super(msg);
    }
}
