package lotrec.dataStructure.tableau.condition;

import java.util.Enumeration;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>How it completes the modifier when it passes :
<p>If the modifier contains the two instances of the node schemes (source and destination nodes), the modifier will be completed within the matching process between the concrete relation in the tableau and the relation scheme in the pattern.
<p>If the modifier does not contain one of the two nodes (source or destination), it will be completed as possible with a concrete node, finding it among the known node links. That's because this class does not accept modifier without any information about nodes. The modifier must contain the concrete reference of the source or destination node scheme.
<p>In all cases of success, the completed modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class LinkMatch extends Restriction {

    private SchemeVariable nodeFromScheme;
    private SchemeVariable nodeToScheme;
    private Expression relationScheme;

    /**
    Creates a link match, ready to be included in a restriction chain, or to begin a chain.
    @param nodeFromScheme the scheme representing the source node of the link
    @param nodeToScheme the scheme representing the destination node of the link
    @param relationScheme the scheme representing the relation of the <code>TableauEdge</code>
    @see TableauEdge
     */
    public LinkMatch(SchemeVariable nodeFromScheme, SchemeVariable nodeToScheme, Expression relationScheme) {
        this.nodeFromScheme = nodeFromScheme;
        this.nodeToScheme = nodeToScheme;
        this.relationScheme = relationScheme;
    }

    @Override
    public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException {
        InstanceSet instanceSet = (InstanceSet) modifier;

        TableauNode nFrom = (TableauNode) instanceSet.get(nodeFromScheme);
        TableauNode nTo = (TableauNode) instanceSet.get(nodeToScheme);

        if ((nFrom == null) && (nTo == null)) {
            throw new ProcessException(toString() + " : cannot attempt to apply without instance for nodeFrom nor for nodeTo");
        }

        //tous les schemas sont instancies
        if ((nFrom != null) && (nTo != null)) {
            for (Enumeration enumr = nFrom.getNextEdgesEnum(); enumr.hasMoreElements();) {
                TableauEdge edge = (TableauEdge) enumr.nextElement();
                if (edge.getEndNode().equals(nTo)) {
                    Expression r = edge.getRelation();
                    InstanceSet newInstanceSet = relationScheme.matchWith(r, instanceSet);
                    if (newInstanceSet != null) {
                        continueAttemptToApply(action, newInstanceSet, actionStocking,eventMachine);
                        if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                            return;
                        }
                    }
                }
            }
            /*      for(Enumeration enumr = nFrom.getEdgesWithSuchNextNode(nTo); enumr.hasMoreElements();) {
            Expression r = ((TableauEdge)enumr.nextElement()).getRelation();
            InstanceSet newInstanceSet = relationScheme.matchWith(r, instanceSet);
            if(newInstanceSet != null) continueAttemptToApply(action, newInstanceSet, actionStocking);
            }*/
            return;
        }

        //cas ou le test du lien part de nodeFrom
        if (nFrom != null) {

            for (Enumeration enumr = nFrom.getNextEdgesEnum(); enumr.hasMoreElements();) {
                TableauEdge edge = (TableauEdge) enumr.nextElement();
                //instanceSet.plus(..,..) doesn't change instanceSet, like it does put method..
                //And that's why there's no fear to call it..
                InstanceSet newInstanceSet = relationScheme.matchWith(edge.getRelation(), instanceSet.plus(nodeToScheme, edge.getEndNode()));
                if (newInstanceSet != null) {
                    continueAttemptToApply(action, newInstanceSet, actionStocking,eventMachine);
                    if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                        return;
                    }
                }
            }
            return;
        }

        //cas ou le test du lien part de nodeTo (invers)
        if (nTo != null) {

            for (Enumeration enumr = nTo.getLastEdgesEnum(); enumr.hasMoreElements();) {
                TableauEdge edge = (TableauEdge) enumr.nextElement();
                InstanceSet newInstanceSet = relationScheme.matchWith(edge.getRelation(), instanceSet.plus(nodeFromScheme, edge.getBeginNode()));
                if (newInstanceSet != null) {
                    continueAttemptToApply(action, newInstanceSet, actionStocking,eventMachine);
                    if (eventMachine.isApplyOnOneOccurence() && !actionStocking.isEmpty()) {
                        return;
                    }
                }
            }
            return;
        }
        return;
    }
}
