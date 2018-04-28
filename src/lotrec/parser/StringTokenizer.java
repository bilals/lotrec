/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.parser;

import lotrec.parser.exceptions.StringTokenizerException;
import lotrec.parser.exceptions.ParseException;
import lotrec.parser.exceptions.ExtrasTokensInStringException;
import de.susebox.jtopas.ReaderSource;
import de.susebox.jtopas.StandardTokenizer;
import de.susebox.jtopas.StandardTokenizerProperties;
import de.susebox.jtopas.Token;
import de.susebox.jtopas.Tokenizer;
import de.susebox.jtopas.TokenizerException;
import de.susebox.jtopas.TokenizerProperties;
import java.io.StringReader;
import lotrec.parser.exceptions.NoTokensInStringException;

/**
 *
 * @author said
 */
public class StringTokenizer {

    private Tokenizer tokenizer;
    private String source;

    public StringTokenizer(String source) {
        this.source = source;
        TokenizerProperties props;
        props = new StandardTokenizerProperties();
        tokenizer = new StandardTokenizer();
        tokenizer.setTokenizerProperties(props);
        tokenizer.setSource(new ReaderSource(new StringReader(source)));
    }

    public String readOneStringToken() throws NoTokensInStringException, ExtrasTokensInStringException, StringTokenizerException {        
        String token = readStringToken();
        verifyStringEnd(token);
        return token;
    }

    private void verifyStringEnd(String readToken) throws ExtrasTokensInStringException, StringTokenizerException {
        Token token;
        while (tokenizer.hasMoreToken()) {
            try {
                token = tokenizer.nextToken();
            } catch (TokenizerException ex) {
                ex.printStackTrace();
                throw new StringTokenizerException(source);
            }
            if (token.getType() == Token.NORMAL) {
                throw new ExtrasTokensInStringException(source, readToken, token.getImage(), token.getStartPosition());
            }
        }
    }

    private String readStringToken() throws NoTokensInStringException, StringTokenizerException {
        Token token;
        if (!tokenizer.hasMoreToken()) {
            throw new NoTokensInStringException(source);
        } else {
            try {
                token = tokenizer.nextToken();
            } catch (TokenizerException ex) {
                ex.printStackTrace();
                throw new StringTokenizerException(source);
            }
            if (token.getType() == Token.NORMAL) {
                //Lotrec.println("Normal token read: " + token.getImage());
                return token.getImage();
            } else {
                //Lotrec.println("Ignored token read: " + token.getImage());
                return readStringToken();
            }
        }
    }
}
