package lotrec.process;

/**
Adds a property to an event eater : a single event eater is capable to handle only <b>one</b> type of event, so the eater must specified the type it wants to handle, in order to be correctly tied to a <code>RestrictedDispatcher</code>.
The dispatcher will only send the event of the specified type to the eater.
@see RestrictedDispatcher#addProcessListener
 */
public interface SingleEventEater extends EventEater {
  /**
  Returns the type of the event this eater will handle
  @return the type of the event
   */
  public abstract int getEventId();
  
}
