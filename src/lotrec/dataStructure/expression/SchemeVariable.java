package lotrec.dataStructure.expression;

/**
Defines a scheme used in the Lotrec project.
A scheme is used to reference any object, in an <code>InstanceSet</code>.
@see InstanceSet
@author David Fauthoux
 */
public interface SchemeVariable extends java.io.Serializable {
  /**
  Tests whether the specified object is a scheme and equals this.
  @param o the object to test
  @return true if the specified object is a scheme and equals this, false otherwise
   */
  public abstract boolean equals(Object o);
  /**
  Value used to find the corresponding object in an <code>InstanceSet</code>
   */
  public abstract int hashCode();
}
