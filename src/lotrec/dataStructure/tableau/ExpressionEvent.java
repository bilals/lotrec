package lotrec.dataStructure.tableau;

import lotrec.process.ProcessEvent;
import lotrec.dataStructure.expression.MarkedExpression;
import lotrec.util.Duplicateable;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;

/**
  Event which indicates an expression has been added or removed from a node. This event is sent by a <code>TableauNode</code>. It is sent to ProcessListener objects.
  @see Graph#addProcessListener(ProcessListener listener)
  @author David Fauthoux
 */
public final class ExpressionEvent extends ProcessEvent implements Duplicateable {

  /**
  The event thrown indicates that <code>markedExpression</code> has been added to the node
   */
  public static int EXPRESSION_ADDED = 6;
  /**
  The event thrown indicates that <code>markedExpression</code> has been removed from the node
   */
  public static int EXPRESSION_REMOVED = 7;

  /**
  Specifies the added or removed expression
   */
  public MarkedExpression markedExpression;

  /**
  Class constructor building a specific event
  @param source the source of the event
  @param type the type of the event : <code>ADDED</code> or <code>REMOVED</code>
  @param node the expression added or removed
   */
  public ExpressionEvent(TableauNode source, int type, MarkedExpression markedExpression) {
    super(source, type);
    this.markedExpression = markedExpression;
  }

  /**
  Returns the node where the expression has been added or removed
  @return the node where the expression has been added or removed
   */
  public TableauNode getNode() {
    return (TableauNode)getSource();
  }
  /**
  Returns the expression, added or removed.
  @return the marked expression, added or removed.
   */
  public MarkedExpression getMarkedExpression() {
    return markedExpression;
  }



  //duplication

  /**
  Creates an event with the toDuplicate's fields. In a duplication process, the event must be translated to reference the duplicated node.
  @param toDuplicate event to duplicate
  @see translateDuplication(Duplicator duplicator)
   */
  public ExpressionEvent(ExpressionEvent toDuplicate) {
    super(toDuplicate);
    markedExpression = toDuplicate.markedExpression;
  }

    @Override
  public void completeDuplication(Duplicator duplicator) throws ClassCastException {
    super.completeDuplication(duplicator);
    
  }

    @Override
  public void translateDuplication(Duplicator duplicator) throws DuplicateException {
    super.translateDuplication(duplicator);
    markedExpression = (MarkedExpression)duplicator.getImage(markedExpression);
  }

  public Duplicateable duplicate(Duplicator duplicator) {
    Duplicateable d = new ExpressionEvent(this);
    duplicator.setImage(this, d);
    return d;
  }
}
