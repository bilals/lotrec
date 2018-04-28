package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;
import java.util.Vector;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>How it completes the modifier when it passes :
<p>If the modifier contains the two instances of the node schemes (child and ancestor nodes), the process will pass if ancestor denotes an object that is the ancestor of the node denoted by the childScheme
<p>If the modifier does not contain the ancestor node, it will be completed as possible with a concrete node, finding it among the child node concrete ancestor.
ADDED 2000 10 09 <p>If the modifier does not contain the child node, it will be completed as possible with a concrete node, finding it among the ancestor node concrete childs.
REMOVED <p>If the modifier does not contain the child node, it is an error.
<p>In all cases of success, the completed modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class AncestorMatch extends Restriction {

    private SchemeVariable childScheme;
    private SchemeVariable ancestorScheme;
    //private Expression relationScheme;

    /**
    Creates an ancestor match, ready to be included in a restriction chain, or to begin a chain.
    @param childScheme the scheme representing the child of the links chain
    @param ancestorScheme the scheme representing the ancestor in the links chain
     */
    public AncestorMatch(SchemeVariable childScheme, SchemeVariable ancestorScheme) {
        this.childScheme = childScheme;
        this.ancestorScheme = ancestorScheme;
//    this.relationScheme=relationScheme;
    }

    private boolean hasAncestor(Vector alreadyTested, TableauNode child, TableauNode ancestor) {//,InstanceSet   instanceSet) {
        if (alreadyTested.contains(child)) {
            return false;
        }
        Vector v = (Vector) alreadyTested.clone();
        v.add(child);
        for (Enumeration enumr = child.getLastEdgesEnum(); enumr.hasMoreElements();) {
            TableauEdge edge = (TableauEdge) enumr.nextElement();
            /*Expression r = edge.getRelation();
            InstanceSet newInstanceSet = relationScheme.matchWith(r, instanceSet);
            if(newInstanceSet!=null)*/
            //I added this in 15/06/2005 because i need tot test
            //if possible is realized in PDL
            if (edge.getBeginNode().equals(ancestor)) {
                return true;
            }
            if (hasAncestor(v, (TableauNode) edge.getBeginNode(), ancestor)) {
                return true;
            }
        }
        return false;
    }

    private void getAllAncestors(Vector already, TableauNode child) {//,InstanceSet   instanceSet) 
        for (Enumeration enumr = child.getLastEdgesEnum(); enumr.hasMoreElements();) {
            TableauEdge edge = (TableauEdge) enumr.nextElement();
            /* Expression r = edge.getRelation();
            InstanceSet newInstanceSet = relationScheme.matchWith(r, instanceSet);
            if(newInstanceSet!=null)
             */
            TableauNode father = (TableauNode) edge.getBeginNode();
            if (!father.equals(child)) {
                if (!already.contains(father)) {
                    already.add(father);
                    getAllAncestors(already, father);
                }
            }
        }
    }

    private void getAllChilds(Vector already, TableauNode ancestor) {//InstanceSet   instanceSet)         
        for (Enumeration enumr = ancestor.getNextEdgesEnum(); enumr.hasMoreElements();) {
            TableauEdge edge = (TableauEdge) enumr.nextElement();
            /*Expression r = edge.getRelation();
            InstanceSet newInstanceSet = relationScheme.matchWith(r, instanceSet);
            if(newInstanceSet!=null){*/
            TableauNode son = (TableauNode) edge.getEndNode();
            if (!son.equals(ancestor)) {
                if (!already.contains(son)) {
                    already.add(son);
                    getAllChilds(already, son);
                }
            }
        }
    }

    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {
        InstanceSet instanceSet = (InstanceSet) modifier;
        TableauNode child = (TableauNode) instanceSet.get(childScheme);
        TableauNode ancestor = (TableauNode) instanceSet.get(ancestorScheme);
        /* REMOVED 2000 10 09
        if(child == null) {
        throw new ProcessException(toString()+" : cannot attempt to apply without instance for the child node scheme");
        }
         */
        /* ADDED 2000 10 09 */
        if ((child == null) && (ancestor == null)) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for the child node scheme nor for the ancestor node scheme");
        }
        if (child == null) {
            Vector v = new Vector();
            getAllChilds(v, ancestor);
            for (Enumeration e = v.elements(); e.hasMoreElements();) {
                TableauNode ch = (TableauNode) e.nextElement();
                continueAttemptToApply(action, instanceSet.plus(childScheme, ch), actionStocking,eventMachine);
                if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                    return;
                }
            }
        } else if (ancestor == null) {
            Vector v = new Vector();
            getAllAncestors(v, child);
            for (Enumeration e = v.elements(); e.hasMoreElements();) {
                TableauNode anc = (TableauNode) e.nextElement();
                continueAttemptToApply(action, instanceSet.plus(ancestorScheme, anc), actionStocking,eventMachine);
                if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                    return;
                }
            }
        } else {
            // i.e. if((ancestor!=null) && (child!=null))
            if (hasAncestor(new Vector(), child, ancestor)) {
                continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
            }
        }
        return;
    }
}
