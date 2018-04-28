package lotrec.dataStructure.tableau.condition;

/* ADDED 00/12/10 */

import java.util.Vector;
import lotrec.dataStructure.expression.*;
import lotrec.dataStructure.tableau.Rule;
import lotrec.process.*;

/**
Delivers knowledge about the bind matching construction : the activator, the restriction and how they works.
This class is used with the <code>Rule</code> class, it is a useful class.
@see Rule
@see BindActivator
@see BindMatch
@author David Fauthoux
 */
public class BindCondition extends AbstractCondition {
  private SchemeVariable scheme;
 private String name;
  private Expression expressionScheme;

  /**
  Creates a bind condition, ready to deliver knowledge about the corresponding activator and restriction
  @param scheme the scheme representing the bound object where the bond is added
  @param name the name for the added bond
  @param expressionScheme the scheme for the added bond
   */
  public BindCondition(SchemeVariable scheme,
  String name, Expression expressionScheme) {
    this.scheme = scheme;
    this.name = name;
    this.expressionScheme = expressionScheme;
  
  }

  public BasicActivator createActivator() {
    return new BindActivator(scheme, name,    expressionScheme);
  }

  public Vector getActivationSchemes() {
    Vector v = new Vector();
    v.add(scheme);
    //expression osf
    return v;
  }


  public Restriction createRestriction() {
    return new BindMatch(scheme, name, expressionScheme);
  }

  public Vector updateSchemes(Vector entry) {
    if(entry.contains(scheme)) return entry;
    else return null;
  }
 
}
