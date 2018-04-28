/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lotrec.parser.exceptions;

/**
 * Should be thrown when trying to create objects from
 * syntactically verified strings.
 * ex: more parameters then neccessary for a condition/action
 * i.e. that the structure is invalid, eventhough each given 
 * param is of a good Syntactical type..
 * @author said
 */
public class LexicalException extends ParseException{
    
    
    public LexicalException(String msg){
        super(msg);
    }
    
    public static String TOO_FEW_PARAMS(String readThingType, String readThing, int neededParamsNum, int foundParamsNum){
        return "Too few parameteres found for " + readThingType + " " + readThing +
                "\nOnly "+foundParamsNum+" parameter(s) found while "+ neededParamsNum+" parameter(s) needed.";
    }
    public static String MORE_PARAMS(String readThingType, String readThing, int neededParamsNum, int foundParamsNum){
        return "More parameteres found for " + readThingType + " " + readThing +
                "\nIn fact, "+foundParamsNum+" parameter(s) found while "+ neededParamsNum+" parameter(s) needed.";
    }    
}
