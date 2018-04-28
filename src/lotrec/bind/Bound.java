package lotrec.bind;

/* ADDED 2000/12/10 */

import lotrec.dataStructure.expression.Expression;

public interface Bound {
    public void bind(String name, Expression expression);
    public Expression getBond(String name);
}