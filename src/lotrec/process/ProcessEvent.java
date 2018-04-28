package lotrec.process;

import java.util.EventObject;
import lotrec.util.Duplicator;
import lotrec.util.CompleteDuplicateable;
import lotrec.util.DuplicateException;

/**
Indicates a process action. To receive such events, implements a <code>ProcessListener</code> and use the addProcessListeener methods.
User must override this class, in order to specify a type. The duplication process should be completed by adding the <code>Duplicateable</code> interface.
@see Dispatcher
@author David Fauthoux
 */
public abstract class ProcessEvent extends EventObject implements CompleteDuplicateable {

  /**
  Specify the type of the event, grouping a set of specialized event in a specific action. Used in collaboration with a <code>RestrictedDispatcher</code>. <b>Warning:the types of the event classes must be choosen in order to not overlap. An overview of the program is necessary to chose the correct integers.</b>
   */
  public int type;

  /**
  Class constructor building a specific event
  @param source the source of the event
  @param type the type of the event
   */
  public ProcessEvent(Object source, int type) {
    super(source);
    this.type = type;
    //Lotrec.println("A new event created, with source: " + source + ", and type: " + type);
  }

  /**
  Returns the type of the event.
  @return the type of the event.
  @see type
   */
  public int getType() {
    return type;
  }

    @Override
  public String toString() {
    return this.getClass().getSimpleName()+"[source: "+source+ ", typeID: " + type+"]" ;
  }

  //duplication
  /**
  Creates an event with the toDuplicate's fields. In a duplication process, the event must be translated to reference the duplicated source of the event.
  @param toDuplicate event to duplicate
  @see translateDuplication(Duplicator duplicator)
   */
  public ProcessEvent(ProcessEvent toDuplicate) {
    super(toDuplicate.source);
    type = toDuplicate.type;
  }

    @Override
  public void completeDuplication(Duplicator duplicator) throws ClassCastException {
  }

    @Override
  public void translateDuplication(Duplicator duplicator) throws DuplicateException {
//    System.out.println("---------- TRANSLATE ProcessEvent WORKING --------" + this);  
    source = duplicator.getImage(source);    
//    System.out.println("---------- TRANSLATE ProcessEvent DONE --------" + this);  
  }
}

