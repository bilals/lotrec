package lotrec.dataStructure.tableau;

/* ADDED 2000/12/21 */

import lotrec.process.ProcessEvent;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Marked;


public final class MarkExpressionEvent extends ProcessEvent implements Duplicateable {

    
    public static int MARK_EX = 20;
    public static int UNMARK_EX = 21;
    public TableauNode node;
    public Object mark;
    
    public MarkExpressionEvent(TableauNode node,Marked source, int type, Object mark) {
        super(source, type);
        this.mark = mark;
	this.node = node;
    }

public TableauNode getNode()
{
return node;
}

    //duplication

    public MarkExpressionEvent(MarkExpressionEvent toDuplicate) {
        super(toDuplicate);
         mark = toDuplicate.mark;
	 node = toDuplicate.node;
    }

    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
    }

    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
	if(duplicator.hasImage(mark)) mark = duplicator.getImage(mark);
        if(duplicator.hasImage(node)) node =(TableauNode)duplicator.getImage(node);
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new MarkExpressionEvent(this);
        duplicator.setImage(this, d);
        return d;
    }
}
