package lotrec.process;

import java.util.EventListener;

/**
   A process listener handles process event.
   @see ProcessEvent
   @author David Fauthoux
 */
public interface ProcessListener extends EventListener {
    /**
       Invoked when a process action occured
       @param e the event to handle
     */
    public void process(ProcessEvent e);
}



