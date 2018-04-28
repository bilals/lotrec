/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lotrec.parser.exceptions;

/**
 *
 * @author said
 */
public class ElementNotFoundException extends ParseException {

    public ElementNotFoundException(String element) {
        super("The following element "+element+" is not found in the XML logic file\n" +
                "The XML file may be corrupted (someone had changed its structure)\n" +
                "or it may be in a diffrent version than the current XML version used by LoTREC");
    }
}
