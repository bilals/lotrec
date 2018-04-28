package lotrec.process;

import java.util.ArrayList;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;

/**
Extends the AllRules
@author Bilal Said
 */
public class Strategy extends AllRules {

    //NOOOOTTTTTEEE: When changing the attribute set of this class,
    // we should change the code in oldiesTokenizer that makes a duplicate like process

//    // added with the xml version and used
//    // to figure out the usability of a strategy
//    // it could be only one of the values :
//    //     "partial" or "complete"
//    private String usability;
    // added with the xml version
    // a comment on the strategy code definition
    private String comment;
    // added with the xml version
    // the code of the strategy
    private String code;

    /**
    Creates a strategy that will give a name to the keep, able to be force stopped.
    @param keep the keep to delegate the work
     */
    public Strategy() {
        super();
    }

    @Override
    public String toString() {
        return super.toString();// + "; usability: " + usability;
    }

    //duplication
    /**
    Creates a strategy with the toDuplicate's keep.
    <p><b>The duplication process will duplicate and translate the keep.</b>
     */
    public Strategy(Strategy toDuplicate) {
        super(toDuplicate);
    // other fields to duplicate..
        this.code = toDuplicate.code;
        this.comment = toDuplicate.comment;
//        this.usability = toDuplicate.usability;
    }

    @Override
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new Strategy(this);
        duplicator.setImage(this, d);
        return d;
    }         
    
//    public String getUsability() {
//        return usability;
//    }
//
//    public void setUsability(String usability) {
//        this.usability = usability;
//    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
