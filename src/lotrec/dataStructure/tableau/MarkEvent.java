package lotrec.dataStructure.tableau;

/* ADDED 2000/12/21 */

import lotrec.process.ProcessEvent;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Marked;

public final class MarkEvent extends ProcessEvent implements Duplicateable {

    public static int MARK = 8;
    public static int UNMARK = 9;
    
    
    public Object mark;

    public MarkEvent(Marked source, int type, Object mark) {
        super(source, type);
        this.mark = mark;
    }


    //duplication

    public MarkEvent(MarkEvent toDuplicate) {
        super(toDuplicate);
        mark = toDuplicate.mark;
    }

    @Override
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
    }

    @Override
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
        if(duplicator.hasImage(mark)) mark = duplicator.getImage(mark);
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new MarkEvent(this);
        duplicator.setImage(this, d);
        return d;
    }
}
