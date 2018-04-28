package lotrec.bind;

/* ADDED 2000/12/10 */

import lotrec.process.ProcessEvent;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.dataStructure.expression.Expression;

public final class BindEvent extends ProcessEvent implements Duplicateable {

    public static int BIND = 8;
    //not implemented, useless: public static int UNBIND = 9;

    public String name;
    public Expression expression;

    public BindEvent(Bound source, int type, String name, Expression expression) {
        super(source, type);
        this.name = name;
        this.expression = expression;
    }


    //duplication

    public BindEvent(BindEvent toDuplicate) {
        super(toDuplicate);
        name = toDuplicate.name;
        expression = toDuplicate.expression;
    }

    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
    }

    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
    }

    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new BindEvent(this);
        duplicator.setImage(this, d);
        return d;
    }
}
