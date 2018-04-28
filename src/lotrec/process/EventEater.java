package lotrec.process;

/**
Capable to eat event and produce energy with it ! Energy is any actions, put in the bag (<code>ActionStocking</code>) in order to be executed consistently by an external class that will empty the bag (<code>EventMachine</code>).
<p><b>It is serializable because its state must not influence its work.</b>
@author David Fauthoux
 */
public interface EventEater extends java.io.Serializable {
  /**
The received event is handled. Its analyse will produce any <code>Action</code>. The actions are usually put in the actionStocking in order to be execute consistently and at the correct moment.
  @param event the event to handle
  @param actionStocking the bag to put actions, if some actions are created
   */
  public void eat(ProcessEvent event, ActionStocking actionStocking,EventMachine eventMachine);
}
