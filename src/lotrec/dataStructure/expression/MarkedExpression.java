package lotrec.dataStructure.expression;

import java.util.regex.*;
import lotrec.util.Marked;
import lotrec.util.Duplicator;
import lotrec.util.Duplicateable;
import lotrec.util.DuplicateException;

///englobe expression
public final class MarkedExpression extends Marked implements Duplicateable, java.io.Serializable {

    ///
    public Expression expression;

    ///
    public MarkedExpression(Expression expression) {
        this.expression = expression;
    }

    public boolean isUsed(Connector c) {
        return expression.isUsed(c);
    }

    ///
    @Override
    public String toString() {
        if (this.marks.isEmpty()) {
            return expression.toString();
        } else {
            return expression.toString() + " " + super.toString();
        }
    }
    //Bilal change
    public String getXMLString() {
        String REGEX = "&";
        String INPUT = this.toString();
        String REPLACE = "and";
        Pattern p = Pattern.compile(REGEX);
        Matcher m = p.matcher(INPUT); // get a matcher object
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, REPLACE);
        }
        m.appendTail(sb);
        System.out.println(sb.toString());
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MarkedExpression) {
            MarkedExpression m = (MarkedExpression) o;
            if (this.expression.equals(m.expression)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.expression != null ? this.expression.hashCode() : 0);
        return hash;
    }

    public String getCodeString() {
        return expression.getCodeString();
    }
    //end change
    ///////////////////////////////////////////////////
    ///
    public MarkedExpression(MarkedExpression toDuplicate) {
        super(toDuplicate);
        expression = toDuplicate.expression;
    }
    ///
    public void completeDuplication(Duplicator duplicator) throws ClassCastException {
        super.completeDuplication(duplicator);
    //nothing
    }
    ///
    public void translateDuplication(Duplicator duplicator) throws DuplicateException {
        super.translateDuplication(duplicator);
    //nothing
    }
    ///
    public Duplicateable duplicate(Duplicator duplicator) {
        Duplicateable d = new MarkedExpression(this);
        duplicator.setImage(this, d);
        return d;
    }
    /////////////////////////////////////////////////
}
