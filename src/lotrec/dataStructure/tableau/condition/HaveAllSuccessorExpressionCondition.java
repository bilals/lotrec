package lotrec.dataStructure.tableau.condition;

import java.util.Vector;
import lotrec.dataStructure.expression.*;
import lotrec.process.*;
import lotrec.util.Marked;

/**
Delivers knowledge about the contains constraint construction : how the restriction works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see MarkConstraint
@author David Fauthoux
 */
public class HaveAllSuccessorExpressionCondition extends AbstractCondition {
  private SchemeVariable nodeScheme;
  private Expression expressionScheme;
 private Expression relation;
  /**
  Creates a contains constraint condition, ready to deliver knowledge about the corresponding restriction
  The contains constraint can be represented by "N0 contains N1" or by "N1 C N0" ('C' representing the mathematical inclusion)
  @param bigNodeScheme the scheme representing the node N0
  @param smallNodeScheme the scheme representing the node N1
   */
  public
 
HaveAllSuccessorExpressionCondition(SchemeVariable
   nodeScheme, Expression expressionScheme, Expression  relation ) {
   this.nodeScheme = nodeScheme;
    this.expressionScheme = expressionScheme;
    this.relation = relation;
    }

  /**
  Returns null
  @return null
   */
  public BasicActivator createActivator() {
      //THE ACTIVATOR SHOULD NOT BE NULL!!
      // IN CONTRAST, IT SHOULD BE ONE OF: ExpressionActivator OR LinkActivator!!!
       return null;
  }

  /**
  Returns null
  @return null
   */
  public Vector getActivationSchemes() {
       return null;
  }


  public Restriction createRestriction() {
    return new  HaveAllSuccessorExpressionMatch(nodeScheme,
    expressionScheme, relation);
  }

  public Vector updateSchemes(Vector entry) {
     if(entry.contains(nodeScheme)) return entry;
    else return null;
  }
  }



