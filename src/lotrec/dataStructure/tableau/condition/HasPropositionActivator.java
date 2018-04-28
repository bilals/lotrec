package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
This activator for a restriction chain sends a modifier in the chain when it receives a <code>ExpressionEvent.EXPRESSION_ADDED</code> event.
<p>It builds the modifier with the received event and with its initializers (schemes) : it joins up the concrete node and its scheme, and it matches the expression with the concrete expression (received in the event).
@see lotrec.graph.ExpressionEvent#EXPRESSION_ADDED
 */
@Deprecated
/*
 * hasElement + isProposition may replace this hasProposition condition
 */
public class HasPropositionActivator extends BasicActivator {

    private SchemeVariable nodeScheme;
    private Expression expressionScheme;

    /**
    Creates an expression activator. It will receive event of <code>ExpressionEvent.EXPRESSION_ADDED</code> type (builds a <code>BasicActivator</code> with this event).
    @param nodeScheme the scheme representing the node where the expression is added
    @param expressionScheme the scheme representing the added expression
    @see TableauNode
    @see ExpressionEvent
     */
    public HasPropositionActivator(SchemeVariable nodeScheme, Expression expressionScheme) {
        super(ExpressionEvent.EXPRESSION_ADDED);
        this.nodeScheme = nodeScheme;
        this.expressionScheme = expressionScheme;
    }

    @Override
    public Object[] createModifiers(ProcessEvent event) {
        InstanceSet s = new InstanceSet();
        ExpressionEvent ee = (ExpressionEvent) event;
        if (ee.markedExpression.expression instanceof ExpressionWithSubExpressions) {
            return null;
        }
        s.put(nodeScheme, ee.getNode());
        return new Object[]{expressionScheme.matchWith(ee.markedExpression.expression, s)};
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "[" + nodeScheme + ", " + expressionScheme + "]";
    }
}
