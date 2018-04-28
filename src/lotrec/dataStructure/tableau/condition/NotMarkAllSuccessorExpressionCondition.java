package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;

/**
Delivers knowledge about the contains constraint construction : how the restriction works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see MarkConstraint
@author David Fauthoux
 */
public class NotMarkAllSuccessorExpressionCondition extends AbstractCondition {
 	private SchemeVariable nodeScheme;
    private Expression markedExpression,relation;
    private Object marker;
 
 
  /**
  Creates a contains constraint condition, ready to deliver knowledge about the corresponding restriction
  The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
  @param bigNodeScheme the scheme representing the node N0
  @param smallNodeScheme the scheme representing the node N1
   */
  public  NotMarkAllSuccessorExpressionCondition(SchemeVariable    nodeScheme,Expression markedExpression,Expression relation, Object  marker) 
    {   this.nodeScheme=nodeScheme;
	    
	    this.markedExpression= markedExpression;
	this.relation=relation;
        this.marker = marker;
	   }
  /**
  Returns null
  @return null
   */
  public BasicActivator createActivator() {
       return new NotMarkExpressionActivator(nodeScheme,markedExpression, marker);
  }

  /**
  Returns null
  @return null
   */
  public Vector getActivationSchemes() {
        Vector v = new Vector();
        v.add(nodeScheme);
        return v;
  }


  public Restriction createRestriction() {
    return new NotMarkAllSuccessorExpressionMatch(nodeScheme,markedExpression,relation,marker);
  }

  public Vector updateSchemes(Vector entry) {
     if(entry.contains(nodeScheme)) return entry;
    else return null;
  }
  }



