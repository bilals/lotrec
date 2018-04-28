package lotrec.dataStructure.tableau.condition;

import java.util.*;
import java.util.Vector;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the link construction : the activator, the restriction and how they works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see LinkActivator
@see LinkMatch
@author Sahade Mohamad
 */
public class CompareCondition extends AbstractCondition {
  private SchemeVariable nodeFromScheme;
  private SchemeVariable nodeToScheme;
  
  /**
  
   */
  public CompareCondition(SchemeVariable   nodeFromScheme, SchemeVariable nodeToScheme) {
    this.nodeFromScheme = nodeFromScheme;
    this.nodeToScheme = nodeToScheme;
    
 }

  public BasicActivator createActivator() {
    return null ;
  }
  
    public Vector getActivationSchemes() {
    return null;
  }


  public Restriction createRestriction() {
    return new CompareConstraint(nodeFromScheme, nodeToScheme);
  }

  public Vector updateSchemes(Vector entry) {
    if(entry.contains(nodeFromScheme) && entry.contains(nodeToScheme)) return entry;
    else return null;
  }
 
 
}
