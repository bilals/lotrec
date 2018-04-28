package lotrec.dataStructure.tableau.condition;

import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.dataStructure.tableau.*;

/**
Represents a restriction that will test if the modifier defines a recognizable tableau pattern.
<p>The modifier will not be completed. This class stands as a constraint, usually in the end of a restriction chain.
<p>The modifier must contain the instance of the two nodes schemes. The pass succeeds if the first node (represented by the first scheme) contains the second node (represented by the second scheme).
<p>In case of success, the modifier will be passed thru the chain. It is a recursive process.
@author David Fauthoux
 */
public class CompareConstraint extends Restriction {
  private SchemeVariable bigNodeScheme;
  private SchemeVariable smallNodeScheme;

  /**
  Creates a contains constraint, ready to be included in a restriction chain.
  The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
  @param bigNodeScheme the scheme representing the node N0
  @param smallNodeScheme the scheme representing the node N1
   */
  public CompareConstraint(SchemeVariable bigNodeScheme, SchemeVariable smallNodeScheme) {
    this.bigNodeScheme = bigNodeScheme;
    this.smallNodeScheme = smallNodeScheme;
  }

  public void attemptToApply(Action action, Object modifier, ActionStocking actionStocking,EventMachine eventMachine) throws ProcessException
  {    
    InstanceSet instanceSet = (InstanceSet)modifier;
    TableauNode bigNode = (TableauNode)instanceSet.get(bigNodeScheme);
    TableauNode smallNode = (TableauNode)instanceSet.get(smallNodeScheme);

    if((bigNode == null) || (smallNode == null)) {
      throw new ProcessException(toString()+" : cannot attempt to apply without the two instances (for bigNode and for smallNode where bigNode must contain smallNode)");
    }

    if(bigNode.number >= smallNode.number) 
  
    return ;
  


     continueAttemptToApply(action, instanceSet, actionStocking,eventMachine);
  }
}
