/*
 * TokenizerTest.java
 *
 * Created on 22 février 2007, 16:19
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package lotrec;

import de.susebox.jtopas.Flags;
import de.susebox.jtopas.Token;
import de.susebox.jtopas.Tokenizer;
import de.susebox.jtopas.TokenizerProperties;
import de.susebox.jtopas.StandardTokenizer;
import de.susebox.jtopas.StandardTokenizerProperties;
import de.susebox.jtopas.ReaderSource;
import de.susebox.jtopas.TokenizerSource;
import java.io.StringReader;

/**
 *
 * @author said
 */
public class TokenizerTest {
    
    /** Creates a new instance of TokenizerTest */
    public TokenizerTest() {
    }
    
    public static void main(String[] args) throws Exception {
        String              myData = "kawa kawa <mawa> kaw\n kah kah";
        TokenizerSource     source = new ReaderSource(new StringReader(myData));
        TokenizerProperties props     = new StandardTokenizerProperties();
        Tokenizer           tokenizer = new StandardTokenizer();
        Token               token;
        int                 len;
        int                 caseFlags;
        
        // setup the tokenizer
        props.setParseFlags( Flags.F_NO_CASE
                | Flags.F_TOKEN_POS_ONLY
                | Flags.F_RETURN_WHITESPACES);
        caseFlags = props.getParseFlags() & ~Flags.F_NO_CASE;
        
        //caseFlags = props.getParseFlags();
        props.setSeparators(null);
        props.addBlockComment("<", ">");
        props.addBlockComment("<HEAD>", "</HEAD>");
        props.addBlockComment("<!--", "-->");
        props.addSpecialSequence("&lt;", "<");
        props.addSpecialSequence("&gt;", ">");
        props.addSpecialSequence("&auml;", "ä", caseFlags);
        props.addSpecialSequence("&Auml;", "Ä", caseFlags);
        props.addSpecialSequence("&ouml;", "ö", caseFlags);
        props.addSpecialSequence("&Ouml;", "Ö", caseFlags);
        props.addSpecialSequence("&uuml;", "ü", caseFlags);
        props.addSpecialSequence("&Uuml;", "Ü", caseFlags);
        props.addSpecialSequence("<b>", "");
        props.addSpecialSequence("</b>", "");
        props.addSpecialSequence("<i>", "");
        props.addSpecialSequence("</i>", "");
        props.addSpecialSequence("<code>", "");
        props.addSpecialSequence("</code>", "");
        
        tokenizer.setTokenizerProperties(props);
        tokenizer.setSource(source);
        
        // tokenize the file and print basically
        // formatted context to stdout
        len = 0;
        while (tokenizer.hasMoreToken()) {
            token = tokenizer.nextToken();
            switch (token.getType()) {
                case Token.NORMAL:
                    System.out.print(tokenizer.currentImage());
                    len += token.getLength();
                    break;
                case Token.SPECIAL_SEQUENCE:
                    System.out.print((String)token.getCompanion());
                    break;
                case Token.BLOCK_COMMENT:
                    if (len > 0) {
                        System.out.println();
                        len = 0;
                    }
                    break;
                case Token.WHITESPACE:
                    if (len > 75) {
                        System.out.println();
                        len = 0;
                    } else if (len > 0) {
                        System.out.print(' ');
                        len++;
                    }
                    
                    break;
            }
        }
    }
    
    
}
