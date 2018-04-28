/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.parser.exceptions;

/**
 * thrown when using a connector (syntactically correct & acts as a connector)
 * but not already defined!
 * This class should be thrown also when global semantical verification fails,
 * i.e. when deleting a connector used in some formula or rule.. etc
 * @author said
 */
public class SemanticException extends ParseException{
public SemanticException(String msg) {
        super(msg);
    }
}
