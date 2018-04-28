package lotrec.process;

import java.util.Enumeration;
import java.util.Vector;
import lotrec.util.Duplicator;
import lotrec.util.DuplicateException;
import lotrec.util.Duplicateable;

/**
Useful class to dispatch events. It receives event and sends them to its listener.
@see addProcessListener(ProcessListener listener)
@author David Fauthoux
 */
public final class Dispatcher implements ProcessListener, Duplicateable {
  private Vector listeners;

  /**
  Creates a dispatcher ready to receive listeners.
   */
  public Dispatcher() {
    listeners = new Vector();
  }

  /**
  Adds a listener to the dispatching process. Any received event will be sent to the listeners.
  @param listener the listener to add
   */
  public synchronized void addProcessListener(ProcessListener listener) {
    if(listener == this) throw new ProcessException("Loop in "+this+". Do not add itself to a dispatcher.");
    listeners.add(listener);
  }


  /**
  Removes a listener from the dispatching process. The listener will no longer receive any event from this dispatcher.
  @param listener the listener to remove
   */
  public synchronized void removeProcessListener(ProcessListener listener) {
    listeners.remove(listener);
  }

  /**
  The received event is sent to all the listeners.
  @param e the event which must be dispatch
   */
  public synchronized void process(ProcessEvent e) {
    for(Enumeration enumr = listeners.elements(); enumr.hasMoreElements();)
    ((ProcessListener)enumr.nextElement()).process(e);
  }

  //duplication

  /**
  Creates a dispatcher with the toDuplicate's listeners.
  <b>A call to the translateDuplication method will translate the listeners to their duplication value if they have images in the <code>Duplicator</code>.</b>
  @param toDuplicate dispatcher to duplicate
   */
  public Dispatcher(Dispatcher toDuplicate) {
    listeners = (Vector)toDuplicate.listeners.clone();
    /*%%%%%%new Vector();

    for(Enumeration enumr = toDuplicate.listeners.elements(); enumr.hasMoreElements();) {
    ProcessListener l = ((ProcessListener)enumr.nextElement());
    if(l instanceof Duplicateable) listeners.add(l);
    }*/
  }

  public synchronized void completeDuplication(Duplicator duplicator) throws ClassCastException {
    /*%%%for(int j = 0; j < listeners.size(); j++) {
    Duplicateable l = (Duplicateable)((Duplicateable)listeners.get(j)).duplicate(duplicator);
    listeners.setElementAt(l, j);
    l.completeDuplication(duplicator);
    }*/
  }

  public synchronized void translateDuplication(Duplicator duplicator) throws DuplicateException {
    for(int j = 0; j < listeners.size(); j++) {
      if(duplicator.hasImage(listeners.get(j))) listeners.set(j, duplicator.getImage(listeners.get(j)));
    }
    /*%%%%
    for(int j = 0; j < listeners.size(); j++)
    ((CompleteDuplicateable)listeners.get(j)).translateDuplication(duplicator);*/
  }

  public Duplicateable duplicate(Duplicator duplicator) {
    Duplicateable d = new Dispatcher(this);
    duplicator.setImage(this, d);
    return d;
  }
}
