package lotrec.dataStructure.tableau.condition;

/* ADDED 00/12/10 */

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.bind.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>How it completes the modifier when it passes :
<p>If the name is the same between the scheme bond, and the concrete bond,
<p>the modifier must contain the instance of the bound object scheme. The modifier will be completed within the matching process between the concrete expression (found in the concrete bond) and the expression scheme in the pattern.
@author David Fauthoux
 */
public class BindMatch extends Restriction {
    private SchemeVariable scheme;
    private String name;
    private Expression expressionScheme;

    /**
    Creates a bond match, ready to be included in a restriction chain, or to begin a chain.
    @param scheme the scheme representing the bound object containing the expression
    @param name the name for the bond
    @param expressionScheme the scheme for the contained expression in the bond
     */
    public BindMatch(SchemeVariable scheme, String name, Expression expressionScheme) {
        this.scheme = scheme;
        this.name = name;
        this.expressionScheme = expressionScheme;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {
        InstanceSet instanceSet = (InstanceSet)modifier;

        Object toBind = instanceSet.get(scheme);
        if(!(toBind instanceof Bound)) throw new ProcessException(toString()+" : can only apply on Bound objects, known Bound objects: TableauNode, TableauEdge");
        Bound b = (Bound)toBind;

        if(b == null) throw new ProcessException(toString()+" : cannot attempt to apply without instance for bound object");

        Expression e = b.getBond(name);
        if(e != null) {
            InstanceSet newInstanceSet = expressionScheme.matchWith(e, instanceSet);
            if(newInstanceSet != null)   continueAttemptToApply(action, newInstanceSet, actionStocking,eventMachine);
        }
	return ;
    }
}
