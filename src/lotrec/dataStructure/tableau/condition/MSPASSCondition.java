package lotrec.dataStructure.tableau.condition;

import java.util.*;
import java.util.Enumeration;
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
public class MSPASSCondition extends AbstractCondition {
  private SchemeVariable nodeScheme;
  private Expression expressionScheme;
  private int Time;
  
  /**
  
   */
  public MSPASSCondition(SchemeVariable   nodeScheme, Expression expressionScheme, int Time) {
    this.nodeScheme = nodeScheme;
    this.expressionScheme   = expressionScheme;
    this.Time=Time;
    
 }

  public BasicActivator createActivator() {
    return null ;
  }
  
    public Vector getActivationSchemes() {
    return null;
  }


  public Restriction createRestriction() {
    return new MSPASSConstraint(nodeScheme, expressionScheme,Time);
  }

  public Vector updateSchemes(Vector entry) {
    if(entry.contains(nodeScheme) ) return entry;
    else return null;
  }
 
 
}
