package lotrec.process;

/**
Implement this interface to define an action usable in a restriction managed process.
<p><b>It is serializable because its state must not influence its work.</b>
@see Restriction
@see RestrictionChain
@see ActionStocking
@author David Fauthoux
 */
public interface Action extends java.io.Serializable {
  /**
  Called when the event processing bag is open (usually by the <code>EventMachine</code>) and all the actions are executed.
  @param modifier the modifier object passed thru the restriction process
  @return the modifier object, because the action can modify it. When an action is composed by many actions (<code>ActionContainer</code>), the next action will use this modified <i>modifier</i>.
   */
  public abstract Object apply(EventMachine em, Object modifier);
    @Override
 public abstract boolean equals(Object modifier);
}
