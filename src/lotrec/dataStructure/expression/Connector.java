package lotrec.dataStructure.expression;

/**
Defines a connector used to build <code>ExpressionWithSubExpressions</code>
@see ExpressionWithSubExpressions
@author David Fauthoux
 */
public class Connector implements java.io.Serializable {
  /**
  Used to build the output string in <code>ExpressionWithSubExpression.toString()</code>
   */
  public static char DEFAULT_SPECIAL_CHARACTER = '_';

  ///
  public static int DEFAULT_TERNARY_PRIORITY = 0;
  ///
  public static int DEFAULT_BINARY_PRIORITY = 1;
  ///
  public static int DEFAULT_UNARY_PRIORITY = 2;
  ///
  public static int DEFAULT_ZEROARY_PRIORITY = 3;
  ///
  public static int DEFAULT_UNDEFINED_PRIORITY = 4;

  private static int forName = 0;
  private int priority;
  private String outString;
  private char specialCharacter;
  private int arity;
  private String name;
  private boolean associative;
  //Bilo Added
  private String comment;

  /**
  Creates a connector with default arity, default priority, default name and default out string.
   */
  public Connector() {
    name = "undefined connector" + forName;
    forName++;
    outString = "undefined string !";
    specialCharacter = DEFAULT_SPECIAL_CHARACTER;
    arity = 0;
    priority = DEFAULT_ZEROARY_PRIORITY;
    setAssociative(true);
  }

  /**
  Creates a connector (with default special character, and default priority corresponding to arity).
  @param name the name of this connector, used in <i>equals</i> method
  @param arity the arity of this connector
  @param outString the out string of this connector
  */  
  public Connector(String name, int arity, String outString) {
    this.name = name;
    this.arity = arity;
    this.outString = outString;
    specialCharacter = DEFAULT_SPECIAL_CHARACTER;
    switch(arity) {
      case 0: priority = DEFAULT_ZEROARY_PRIORITY;break;
      case 1: priority = DEFAULT_UNARY_PRIORITY;break;
      case 2: priority = DEFAULT_BINARY_PRIORITY;break;
      case 3: priority = DEFAULT_TERNARY_PRIORITY;break;
      default: priority = DEFAULT_UNDEFINED_PRIORITY;break;
    }
    setAssociative(true);
  }

  /**
  Returns the name of this connector, used in the <i>equals</i> method
  @return the name of this connector
   */
  public String getName() {
    return name;
  }

  /**
  Defines the name of this connector, used in the <i>equals</i> method.
  @param name the name of this connector
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
  Tests whether the specified connector name equals this connector name
  @param o the connector to test, must be a <code>Connector</code>
   */
  public boolean equals(Object o) {
    return ((Connector)o).name.equals(name);
  }

  /**
  Returns the priority of this connector, used to build the out string of an <code>ExpressionWithSubExpressions</code>
  @return the priority of this connector
   */
  public int getPriority() {
    return priority;
  }

  /**
  Defines the priority of this connector, used to build the out string of an <code>ExpressionWithSubExpressions</code>
  @param priority the priority of this connector
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
  Returns the special character used to place the sub expressions of an <code>ExpressionWithSubExpressions</code>, in the <i>toString</i> method.
  @return the special character of this connector
   */
  public char getSpecialCharacter() {
    return specialCharacter;
  }

  /**
  Defines the special character used to place the sub expressions of an <code>ExpressionWithSubExpressions</code>, in the <i>toString</i> method.
  @param specialCharacter the special character of this connector
   */
  public void setSpecialCharacter(char specialCharacter) {
    this.specialCharacter = specialCharacter;
  }

  /**
  Returns the out string of this connector, used with the special character, to build the out string in the <code>ExpressionWithSubExpressions.toString</code> method
  @return the out string of this connector
   */
  public String getOutString() {
    return outString;
  }

  /**
  Defines the out string of this connector, used with the special character, to build the out string in the <code>ExpressionWithSubExpressions.toString</code> method
  @param outString the out string of this connector
   */
  public void setOutString(String outString) {
    this.outString = outString;
  }

  /**
  Returns the arity of this connector, usually used by <code>ExpressionWithSubExpressions</code>
  @return the arity of this connector
   */
  public int getArity() {
    return arity;
  }

  /**
  Defines the arity of this connector
  @param arity the arity of this connector
   */
  public void setArity(int arity) {
    this.arity = arity;
  }

  /**
  Says whether this connector is associative or not. This specification is used in the <code>ExpressionWithSubExpressions.toString</code> method, to put or not brackets when identical connectors are found.
  @return true if this connector is associative, false otherwise
   */
  public boolean isAssociative() {
    return associative;
  }

  /**
  This specification is used in the <code>ExpressionWithSubExpressions.toString</code> method, to put or not brackets when identical connectors are found.
  @param associative true to set this connector associative, false otherwise
   */
  public void setAssociative(boolean associative) {
    this.associative = associative;
  }


  /**
  Returns a string representation of this connector
  @return the name of this connector
   */
  public String toString() {
    return "Connector name:" + name + ", arity:" + arity + ", associative:" + isAssociative() + ", output format:" + outString + ", priority:" + priority + ". Comments: " + comment;
  }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
