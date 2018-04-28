package lotrec.dataStructure.tableau.action;

/* ADDED 00/12/10 */

import lotrec.dataStructure.expression.*;
import lotrec.process.AbstractAction;
import lotrec.process.ProcessException;
import lotrec.process.EventMachine;

//this qction creates a new constant and instancies the expression sheme by it.
//So sheme must be a variable
public class CreateNewConstantAction extends AbstractAction {
    private Expression scheme;

    // scheme must be a variable
    public CreateNewConstantAction(Expression scheme) {
        this.scheme = scheme;
    }

    @Override
    public Object apply(EventMachine em, Object modifier) {
        InstanceSet instanceSet = (InstanceSet)modifier;

        instanceSet = scheme.matchWith(new ConstantExpression(), instanceSet);

        if(instanceSet == null) 
throw new ProcessException(this.getClass().getSimpleName() + " in rule " + em.getWorkerName() + ":\n" +
        "cannot match to create a new constant.\n Use a variable to bind the new constant, invalid scheme: " + scheme);

        return instanceSet;
    }
}
