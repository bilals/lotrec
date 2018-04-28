package lotrec.dataStructure.expression;

import java.util.ArrayList;

/**
Defines an expression used in all the Lotrec project.
An expression must be able to compute all the matching and instancing actions.
This actions should not throw any error, but use the instance set.
@author David Fauthoux
 */
public interface Expression extends java.io.Serializable {
  /**
  Computes the matching process, watching for the contained objects of this expression (<code>Connector</code> for example).
  This method uses the specified instance set, and completes it.
  @param e the expression on which the instanciation will run
  @param current the instance set to use and complete
  @return the completed instance set if the matching process succeeds, null if it fails
   */
  public abstract InstanceSet matchWith(Expression e, InstanceSet current);

  /**
  Checks whether the specified expression is equal or not to this.
  @param e the expression to test
  @return true if the specified expression equals this, false otherwise, false if the specified expression if null
   */
    @Override
  public abstract boolean equals(Object e);

  /**
  Applies the instanciation process on this expression.
  @param set the instance set used in the instanciation
  @return the expression which results from the instanciation
   */
  public abstract Expression getInstance(InstanceSet set);

      /**
    Creates a xml string representation of this expression for XML files.    
    @return a string representaion of this expression
     */
   public String getCodeString() ;
  
    /**
    Creates a string representation of this expression for MSPASS.
    
    @return a string representaion of this expression
     */
   public abstract String toMSPASS() ;

   public abstract ArrayList<Expression> getVariableExpressions() ;
   
       public abstract boolean isUsed(Connector c);
       public abstract ArrayList<Connector> getUsedConnectors();
  
  }
