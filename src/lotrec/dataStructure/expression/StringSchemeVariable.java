package lotrec.dataStructure.expression;

/**
Useful class to easily create scheme variables.
 */
public class StringSchemeVariable implements SchemeVariable {
  private String name;

  /**
  Creates a scheme with the specified name
  @param name the name of this scheme, used in the <i>equals</i> method
   */
  public StringSchemeVariable(String name) {
    this.name = name;
  }

  /**
  Tests the equality between two schemes.
  @param o the scheme to test
  @return true if the specified object is a <code>StringSchemeVariable</code> and its name equals this scheme name
   */
    @Override
  public boolean equals(Object o) {
    if(o instanceof StringSchemeVariable)
    return ((StringSchemeVariable)o).name.equals(name);
    else return false;
  }

    @Override
  public String toString() {
    return name;
  }

    @Override
  public int hashCode() {
    return name.hashCode();
  }
}
