package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
 * @author Bilal Said
 */
public class HaveSameFormulasSetMatch extends Restriction {

    private SchemeVariable node1;
    private SchemeVariable node2;

    /**
    Creates a contains constraint, ready to be included in a restriction chain.
    The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
    @param bigNodeScheme the scheme representing the node N0
    @param smallNodeScheme the scheme representing the node N1
     */
    public HaveSameFormulasSetMatch(SchemeVariable node1, SchemeVariable node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {

        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode firstNode = (TableauNode) instanceSet.get(node1);
        TableauNode secondNode = (TableauNode) instanceSet.get(node2);

        if ((firstNode == null) || (secondNode == null)) {
            throw new ProcessException(toString() + " : cannot attempt to apply without the two instances (for bigNode and for smallNode where bigNode must contain smallNode)");
        }

        //REMOVED 10 16 2000 : int count = 0;
        for (Enumeration enumr = secondNode.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
            Expression e = ((MarkedExpression) enumr.nextElement()).expression;
            if (!firstNode.contains(e)) {
                return;
            //REMOVED 10 16 2000 : count++;
            }
        }
        
        //REMOVED 10 16 2000 : int count = 0;
        for (Enumeration enumr = firstNode.getMarkedExpressionsEnum(); enumr.hasMoreElements();) {
            Expression e = ((MarkedExpression) enumr.nextElement()).expression;
            if (!secondNode.contains(e)) {
                return;
            //REMOVED 10 16 2000 : count++;
            }
        }        

        //REMOVED 10 16 2000 : if(count > 0) 
        continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
    }
}
