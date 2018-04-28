package lotrec.dataStructure.tableau.condition;

/* ADDED 00/12/10 */

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;
import lotrec.bind.*;

/**
 * This activator for a restriction chain sends a modifier in the chain when it receives a <code>BindEvent.BIND</code> event.
 * <p>It builds the modifier with the received event and with its initializers (schemes) : it joins up the concrete bound object and its scheme, and it matches the expression with the concrete expression (received in the event).
 * @see lotrec.bind.BindEvent#BIND
 */
public class BindActivator extends BasicActivator {
    private SchemeVariable scheme;
    private String name;
    private Expression expressionScheme;
    
    /**
  Creates a bind activator. It will receive event of <code>BindEvent.BIND</code> type (builds a <code>BasicActivator</code> with this event).
  @param scheme the scheme representing the bound object where the bond is added (or updated)
  @param name the name for the added bond
  @param expressionScheme the scheme for the added bond
  @see TableauNode
  @see BindEvent
     */
    public BindActivator(SchemeVariable scheme,
    String name, Expression expressionScheme) {
        super(BindEvent.BIND);
        this.scheme = scheme;
        this.name = name;
        this.expressionScheme = expressionScheme;
    }
    
    public Object[] createModifiers(ProcessEvent event) {
        InstanceSet s = new InstanceSet();
        BindEvent ee = (BindEvent)event;
        if(!ee.name.equals(name)) return null;
        
        s.put(scheme, ee.getSource());
        return new Object[]{expressionScheme.matchWith(ee.expression, s)};
    }
}
