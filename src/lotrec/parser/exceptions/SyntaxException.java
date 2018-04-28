/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.parser.exceptions;

/**
 * Should be actually returned by the recognizeXXX()methods
 * of the Tokenizer which tests for the syntactical structure
 * of a parameter, ex: constant, variable expressions; ...
 * @author said
 */
public class SyntaxException extends ParseException{
    
    private String details;
    public SyntaxException(String msg, String details){
        super(msg);
        this.details = details;
    }
    
    public String getDetails(){
        return details;
    }

}
