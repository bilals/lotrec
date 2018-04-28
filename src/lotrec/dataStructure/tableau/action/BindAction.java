package lotrec.dataStructure.tableau.action;

/* ADDED 00/12/10 */

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.bind.Bound;

public class BindAction extends AbstractAction {
    private SchemeVariable scheme;
    private String name;
    private Expression expressionScheme;

    public BindAction(SchemeVariable scheme, String name, Expression expressionScheme) {
        this.scheme = scheme;
        this.name = name;
        this.expressionScheme = expressionScheme;
    }

    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet)modifier;
        Object toBind = instanceSet.get(scheme);
        if(!(toBind instanceof Bound)) throw new ProcessException(this.getClass().getSimpleName()+ " in rule " + em.getWorkerName() + ":\n" +
                "can only apply on Bound objects, known Bound objects: TableauNode, TableauEdge");
        Bound b = (Bound)toBind;

        if(b == null) throw new ProcessException(this.getClass().getSimpleName()+" in rule " + em.getWorkerName() + ":\n" +
                "cannot apply without instance for bound");

        Expression e = expressionScheme.getInstance(instanceSet);
        /* ADDED 00/12/10 */ if(e == null) throw new ProcessException(this.getClass().getSimpleName()+" in rule " + em.getWorkerName() + ":\n" +
                "cannot apply action, cannot instanciate expression: " + expressionScheme);

        b.bind(name, e);

        return instanceSet;
    }
}
