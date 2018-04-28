/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.parser.exceptions;

/**
 *
 * @author said
 */
public class NoTokensInStringException  extends StringTokenizerException {
   
    public NoTokensInStringException(String givenString){
        super(givenString);
    }
    
    @Override
    public String getMessage(){
        return "No tokens detected in the following given string:\n\"" + givenString+"\"";
    }
}
