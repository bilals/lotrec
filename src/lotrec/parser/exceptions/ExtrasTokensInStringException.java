/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.parser.exceptions;

/**
 *
 * @author said
 */
public class ExtrasTokensInStringException extends StringTokenizerException {

    private String readToken;
    private String extraToken;
    private int extraTokenPosition;

    public ExtrasTokensInStringException(String givenString, String readToken, 
            String extraToken, int extraTokenPosition) {
        super(givenString);
        this.readToken = readToken;
        this.extraToken = extraToken;
        this.extraTokenPosition = extraTokenPosition;
    }

    @Override
    public String getMessage() {
        return "There must be just one token in the following given string: \"" + givenString + "\"\n"+
                "The string tokenizer recognized this token: \""+ readToken+ "\" at first.\n" +
                "But after, at least, this extra token: \""+extraToken+
                "\" was found at position: " +extraTokenPosition;
    }
}
