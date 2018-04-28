package lotrec.bind;

/* ADDED 2000/12/10 */

import lotrec.dataStructure.expression.Expression;

import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class BasicBinder implements Bound {

    private Map map;
    
    public BasicBinder() {
        map = Collections.synchronizedMap(new HashMap());
    }

    public void bind(String name, Expression expression) {
        map.put(name, expression);
    }
    
    public Expression getBond(String name) {
        return (Expression)map.get(name);
    }
}