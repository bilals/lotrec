/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.parser.exceptions;

/**
 *
 * @author said
 */
public class StringTokenizerException extends ParseException{
    
    protected String givenString;
    
    public StringTokenizerException(String givenString){
       this.givenString = givenString;
    }
    
    @Override
    public String getMessage(){
        return "The String Tokenizer used in LoTREC had an exception " +
                "while processing the following given string:\n\"" + givenString+"\"";
    }
}
